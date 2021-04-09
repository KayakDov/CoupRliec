package Convex;

import Convex.Linear.Plane;
import Convex.Linear.AffineSpace;
import Matricies.Matrix;
import Matricies.Point;
import Matricies.PointD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import listTools.ChoosePlanes;

/**
 * This is a convex polyhedron in R^3 By default, instances do not keep track of
 * vertices.
 *
 * to better understand how faces are connected, read about face latices in
 * https://en.wikipedia.org/wiki/Convex_polytope
 *
 * @author Dov Neimand
 */
public class Polytope implements ConvexSet {

    protected ArrayList<HalfSpace> halfSpaces;
    private Set<HalfSpace> halfSpaceSet;

    /**
     * Add a bunch of half spaces to this polytope
     *
     * @param adds
     */
    public void addAll(Collection<HalfSpace> adds) {
        for (HalfSpace add : adds) {
            add(add);
        }
    }

    /**
     * Add a bunch of half spaces to this polytope
     *
     * @param adds
     */
    public void addAll(Polytope adds) {
        for (HalfSpace add : adds.halfSpaces) {
            add(add);
        }
    }

    private void reInit() {
        lastProjectionSource = null;
        lastProjection = null;
    }

    public void add(HalfSpace hs) {
        if (halfSpaceSet.add(hs)) {
            halfSpaces.add(hs);
            reInit();
        }
    }

    public void remove(HalfSpace hs) {
        if (halfSpaceSet.remove(hs)) {
            halfSpaces.remove(hs);
            reInit();
        }

    }

    public void removeIf(Predicate<HalfSpace> pred) {
        halfSpaces.removeIf(pred);
        halfSpaceSet.removeIf(pred);
        reInit();
    }

    public void removeAll(Collection<HalfSpace> colec) {
        halfSpaces.removeAll(colec);
        halfSpaceSet.removeAll(colec);
        reInit();
    }
    /**
     * All the vertices of the polytope. be sure to call with getVertices().
     */
    protected Matrix vertices;

    /**
     * Every Point x in this polytope satisfies the equation Mx less than b
     * where M is the matrix of normals. Each face f satisfies the equation
     * f*x=bi.
     *
     * @return
     */
    public PointD b() {
        return new PointD(size())
                .setAll(i
                        -> halfSpaces.get(i).boundary().b.get(0));
    }

    /**
     * The constructor
     *
     * @param faces of this polytope
     */
    public Polytope(Collection<HalfSpace> faces) {
        this.halfSpaceSet = new HashSet<>(faces);
        this.halfSpaces = new ArrayList<>(halfSpaceSet);
    }

    public Polytope(Stream<HalfSpace> hsStream) {
        this(hsStream.collect(Collectors.toList()));

    }

    public Polytope(Polytope toClone) {
        this(toClone.halfSpaces);
    }

    /**
     * This polytope is defined by the equation normals*x < end
     *
     * @param normals a matrix of the normal vectors of the half spaces
     * @param b the right side of the defning inequality
     */
    public Polytope(Matrix normals, Point b) {
        this();
        halfSpaces.addAll(
                IntStream.range(0, normals.rows()).mapToObj(i -> new HalfSpace(
                normals.row(i), b.get(i)))
                        .collect(Collectors.toList()));
        halfSpaceSet.addAll(halfSpaces);
    }

    public Polytope() {
        this(new ArrayList<HalfSpace>());
    }

    /**
     * removes all the halfSpaces not smallestContainingSubSpace x on their
     * surface
     *
     * @param x
     */
    public void removeFacesNotContaining(Point x) {
        removeIf(face -> !face.onSurface(x, epsilon));
    }

    public void removeFacesNotFacing(Point y) {
        removeIf(f -> f.hasElement(y));
    }

    /**
     * adds a half space intersectionPoint to this polytope.
     *
     * @param p
     */
    public Polytope addFace(HalfSpace p) {
        halfSpaces.add(p);
        lastProjectionSource = null;
        lastProjection = null;
        return this;
    }

    public Polytope addFaces(Polytope p) {
        halfSpaces.addAll(p.halfSpaces);
        lastProjectionSource = null;
        lastProjection = null;
        return this;
    }

    /**
     * The constructor
     *
     * @param faces of this polytope
     */
    public Polytope(HalfSpace[] faces) {
        this(Arrays.asList(faces));
    }

    @Override
    public boolean hasElement(Point p) {//TODO: make this paralel
        return stream().allMatch((HalfSpace hs) -> hs.hasElement(p, epsilon));
    }

    private Point lastProjectionSource = null, lastProjection = null;

    public boolean hasHalfSpaces() {
        return halfSpaces.isEmpty();
    }

    public double epsilon = 1e-9;//1e-9;

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public void clearFaces() {
        reInit();
        halfSpaces.clear();
        halfSpaceSet.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("convex polytope:\n");
        stream().forEach(face -> sb.append(face.toString()).append("\n"));
        sb.append("\n");
        if (vertices != null) {
            vertices.rowStream().forEach(point -> sb.append(point).append("\n"));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * are there any halfspaces that intersect to create this polytope.
     *
     * @return Will return true if this polytope includes all of space, false
     * otherwise.
     */
    public boolean isEmpty() {
        return halfSpaces.isEmpty();
    }

    /**
     * The number of half spaces that intersect to make this polytope
     *
     * @return
     */
    public int size() {
        return halfSpaces.size();
    }

    public int dim() {
        if (halfSpaces.isEmpty()) {
            return 0;
        }
        return halfSpaces.get(0).dim();
    }

    public Stream<HalfSpace> stream() {
        return halfSpaces.stream();
    }

    @Override
    public boolean hasElement(Point x, double epsilon) {
        return stream().allMatch(halfSpace -> halfSpace.hasElement(x, epsilon));
    }

    /**
     * TODO fix this. Use feasabilityPoint
     *
     * Does the affine space given have a non empty intersection with this
     * polytope.
     *
     * @param as the affine space that may or may not intersect this polytope.
     * @return true if there is a nonempty intersection, false otherwise.
     */
    public boolean hasNonEmptyIntersection(AffineSpace as) {

        try {
            return intersect(as.asPolytope()).feasibilityPoint().isReal();
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    /**
     * Checks if two halfspaces are adjacent O(n+dim^3) where n is the number of
     * faces. Ever facet is adjacent to itself.
     *
     * @param a a half space that should be in this polytope
     * @param b a half space that should be in this polytope
     * @return true if a is adjacent to b.
     */
    public boolean adjacent(HalfSpace a, HalfSpace b) {
        try {
            return hasNonEmptyIntersection(a.boundary().intersection(b.boundary()));
        } catch (ArithmeticException ex) {
            return false;
        }
    }

    /**
     * the planes that make up the boundary of this polytope.
     *
     * @return
     */
    public Stream<Plane> planes() {
        return stream().map(half -> half.boundary());
    }

    /**
     * The closest half space in this polytope to a point.
     *
     * @param x the point we're looking for the closest plane to.
     * @return the closest plane.
     */
    public HalfSpace closestTo(Point x) {
        return stream().parallel().max(Comparator.comparing(plane -> plane.d(x))).get();
    }

    /**
     * Some point in this polytope if it's non empty. Otherwise a NaN point.
     * There is likely a faster way to do this.
     *
     * @return
     */
    public Point feasibilityPoint() throws NoSuchElementException {

//        return new GradDescentFeasibility();
        return bruteForceFeasibility();
    }

    /**
     * The intersection of this polytope and another.
     *
     * @param poly
     * @return
     */
    public Polytope intersect(Polytope poly) {
        Polytope intersct = new Polytope();
        intersct.addFaces(this);
        intersct.addFaces(poly);
        return intersct;
    }

    /**
     * This is a brute force projection method. It works by sampling all the
     * affine spaces, and seeing which projections are in this polytope.
     *
     * @param y the point being projected.
     * @return the projection of y onto this convex polytope.
     */
    public Point bruteForceProjection(Point y) {

        if (hasElement(y, epsilon)) {
            return y;
        }

        try {
            return affineSubSpaces().map(as -> as.proj(y))
                    .filter(asProj -> {
//                        System.out.println(stream().mapToDouble(hs -> hs.d(asProj)).sum());
                        return hasElement(asProj, epsilon);
                    })
                    .min(Comparator.comparing(p -> p.d(y))).get();
        } catch (NoSuchElementException ex) {

            String problem = toString()
                    + "\ny = " + y
                    + "\npost filter count = " + affineSubSpaces().map(as -> as.proj(y))
                            .filter(asProj -> hasElement(asProj, epsilon)).count()
                    + "\nprefilter count = " + affineSubSpaces().count()
                    + "\nprojections = \n"
                    + affineSubSpaces().map(as -> as.proj(y)).map(p -> p.toString())
                            .reduce((a, b) -> a + "\n" + b).get();

            throw new NoSuchElementException(problem);

        }
    }

    /**
     * returns some feasible point.
     *
     * @return
     */
    public Point bruteForceFeasibility() {

        return affineSubSpaces().map(as -> as.p()).filter(p -> hasElement(p)).findAny().get();
    }

    /**
     * All the intersections of all the bounding planes.
     *
     * @return
     */
    public Stream<AffineSpace> affineSubSpaces() {

        return affineSubSpaces(dim());
    }

    /**
     * Same as affineSubSpaces but allows to limit the number of halfspaces per
     * intersection.
     *
     * @param maxHSPerIntersection the maximum number of halfspaces per
     * intersection.
     * @return
     */
    public Stream<AffineSpace> affineSubSpaces(int maxHSPerIntersection) {

        return IntStream
                .rangeClosed(1, maxHSPerIntersection)
                .mapToObj(i -> i)
                .flatMap(i -> 
                        new ChoosePlanes(planes().collect(Collectors.toList()),i)
                                .chooseStream())
                        .map(planeArray -> new AffineSpace(planeArray));

    }

    /**
     * Finds all the faces with the given point on their surface.
     *
     * @param x a point on the surface of the polytope.
     * @return all the faces smallestContainingSubSpace x on their surface.
     */
    private Polytope facesContaining(Point x) {
        return new Polytope(stream().filter(hs -> hs.boundary().hasElement(x)));
    }

    public boolean equals(Polytope poly) {
        return new HashSet<>(halfSpaces).equals(new HashSet<>(poly.halfSpaces));
    }

    /**
     * This returns the list of halfspaces, not a copy of it. So adjust with
     * caution.
     *
     * @return
     */
    protected ArrayList<HalfSpace> getHalfSpaces() {
        return halfSpaces;
    }

    public Set<HalfSpace> getHalfSpaceSet() {
        return halfSpaceSet;
    }

    @Override
    public Point proj(Point p) {
        return bruteForceProjection(p);
    }

    public static Polytope randomNonEmpty(int numFaces, double radius, int dim) {

        Polytope poly = new Polytope();
        IntStream.range(0, numFaces).forEach(i -> {
            
            PointD random = PointD.uniformRand(new PointD(dim), radius);
            
            random.multMe(radius / random.magnitude());
                        
            poly.add(new HalfSpace(random, random));
        });

        return poly;
    }

    public static Polytope random(int numFaces, double radius, int dim) {

        Polytope poly = new Polytope();
        IntStream.range(0, numFaces).forEach(i -> {
            PointD random1 = PointD.uniformRand(new PointD(dim), radius);
            PointD random2 = PointD.uniformRand(new PointD(dim), radius);

            poly.add(new HalfSpace(random1.mult(radius), random2.mult(1 / random2.magnitude())));
        });

        return poly;
    }

}
