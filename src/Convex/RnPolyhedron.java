
package Convex;

import Convex.LinearRn.RnAffineProjection;
import Hilbert.HalfSpace;
import Hilbert.Polyhedron;
import java.util.List;
import Convex.LinearRn.RnPlane;
import Convex.LinearRn.RnAffineSpace;
import Hilbert.CoupRliec;
import Hilbert.Plane;
import Matricies.Matrix;
import Matricies.Point;
import Matricies.PointD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import listTools.ChoosePlanes;

/**
 *
 * @author Dov Neimand
 */
public class RnPolyhedron extends Polyhedron<Point>{
    
    public RnPolyhedron(List<HalfSpace<Point>> halfspaces) {
        super(halfspaces);
    }
    
    public Stream<RnPlane> planes(){
        return halfspaces.stream().map(hs -> new RnPlane(hs.boundary()));
    }
    
    
        
    /**
     * Add a bunch of half spaces to this polytope
     *
     * @param adds
     */
    public void addAll(Collection<HalfSpace<Point>> adds) {
        for (HalfSpace<Point> add : adds) {
            add(add);
        }
    }

    /**
     * Add a bunch of half spaces to this polytope
     *
     * @param adds
     */
    public void addAll(RnPolyhedron adds) {
        for (HalfSpace<Point> add : adds.halfspaces) {
            add(add);
        }
    }

    /**
     * sets this polytope to the intersection of this polytope and the given
     * half space.
     *
     * @param hs
     */
    public void add(HalfSpace<Point> hs) {
        halfspaces.add(hs);
    }

    /**
     * Removes the given half space from the list of those half spaces
     * intersecting to form this polytope.
     *
     * @param hs
     */
    public void remove(HalfSpace<Point> hs) {
        halfspaces.remove(hs);
    }

    /**
     * removes all the halfspaces from the intersection list that meet the given
     * requirement.
     *
     * @param pred
     */
    public void removeIf(Predicate<HalfSpace<Point>> pred) {
        halfspaces.removeIf(pred);
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
                        -> halfspaces.get(i).boundary().b.get(0));
    }

    /**
     * The constructor
     *
     * @param faces of this polytope
     */
    public RnPolyhedron(Collection<HalfSpace<Point>> faces) {
        super(new ArrayList<>(faces));
    }

    /**
     * Constructor
     *
     * @param hsStream a stream of half spaces to intersect to make this
     * polytope.
     */
    public RnPolyhedron(Stream<HalfSpace<Point>> hsStream) {
        this(hsStream.collect(Collectors.toList()));

    }

    /**
     * Constructor
     *
     * @param toClone a polytope to clone
     */
    public RnPolyhedron(RnPolyhedron toClone) {
        this(toClone.halfspaces);
    }

    /**
     * This polytope is defined by the equation normals*x < end
     *
     * @param normals a matrix of the normal vectors of the half spaces
     * @param b the right side of the defning inequality
     */
    public RnPolyhedron(Matrix normals, Point b) {
        this();
        halfspaces.addAll(
                IntStream.range(0, normals.rows()).mapToObj(i -> new HalfSpace<Point>(
                normals.row(i), b.get(i)))
                        .collect(Collectors.toList()));
    }

    /**
     * A constructor
     */
    public RnPolyhedron() {
        this(new ArrayList<HalfSpace<Point>>());
    }

    /**
     * removes all the halfspaces not smallestContainingSubSpace x on their
     * surface
     *
     * @param x
     */
    public void removeFacesNotContaining(Point x) {
        removeIf(face -> !face.onSurface(x, epsilon));
    }

    /**
     * Sets this polytope equal to the smallest polytope containing the given
     * point from the planes that intersect to make this polytope.
     *
     * @param y
     */
    public void removeFacesNotFacing(Point y) {
        removeIf(f -> f.hasElement(y));
    }

    /**
     * adds a half space intersectionPoint to this polytope.
     *
     * @param p
     * @return 
     */
    public RnPolyhedron addFace(HalfSpace<Point> p) {
        
        halfspaces.add(p);
        return this;
    }

    public RnPolyhedron addFaces(RnPolyhedron p) {
        halfspaces.addAll(p.halfspaces);
        return this;
    }

    /**
     * The constructor
     *
     * @param faces of this polytope
     */
    public RnPolyhedron(HalfSpace<Point>[] faces) {
        this(Arrays.asList(faces));
    }

    @Override
    public boolean hasElement(Point p) {
        return stream().parallel().allMatch((HalfSpace<Point> hs) -> hs.hasElement(p, epsilon));
    }

    /**
     * Is this polytope Rn
     *
     * @return
     */
    public boolean isAllSpace() {
        return halfspaces.isEmpty();
    }

    /**
     * A small number used to check if two floating point values are equal
     */
    public double epsilon = 1e-9;

    /**
     * Sets espilon, a small number used to check if floating point values are
     * equal.
     *
     * @param epsilon
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Sets this polytope equal to Rn
     */
    public void clearFaces() {
        halfspaces.clear();
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
        return halfspaces.isEmpty();
    }

    /**
     * The number of half spaces that intersect to make this polytope
     *
     * @return
     */
    public int size() {
        return halfspaces.size();
    }

    /**
     * The n in Rn.
     * @return 
     */
    public int dim() {
        if (halfspaces.isEmpty()) {
            return 0;
        }
        return halfspaces.get(0).dim();
    }

    /**
     * A stream of the half spaces that intersect to form this polytope.
     * @return 
     */
    public Stream<HalfSpace<Point>> stream() {
        return halfspaces.stream();
    }

    @Override
    public boolean hasElement(Point x, double epsilon) {
        return stream().allMatch(halfSpace -> halfSpace.hasElement(x, epsilon));
    }


    /**
     * The closest half space in this polytope to a point.
     *
     * @param x the point we're looking for the closest plane to.
     * @return the closest plane.
     */
    public HalfSpace<Point> closestTo(Point x) {
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
    public RnPolyhedron intersect(RnPolyhedron poly) {
        RnPolyhedron intersct = new RnPolyhedron();
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
    public Stream<RnAffineSpace> affineSubSpaces() {

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
    public Stream<RnAffineSpace> affineSubSpaces(int maxHSPerIntersection) {

        return IntStream
                .rangeClosed(1, maxHSPerIntersection)
                .mapToObj(i -> i)
                .flatMap(i
                        -> new ChoosePlanes(planes().collect(Collectors.toList()), i)
                        .chooseStream())
                .map(planeArray -> new RnAffineSpace(planeArray));

    }


    /**
     * Are the two polytopes equal.
     * @param poly
     * @return 
     */
    public boolean equals(RnPolyhedron poly) {
        return new HashSet<>(halfspaces).equals(new HashSet<>(poly.halfspaces));
    }

    /**
     * This returns the list of halfspaces, not a copy of it. So adjust with
     * caution.
     *
     * @return
     */
    protected List<HalfSpace<Point>> getHalfSpaces() {
        return halfspaces;
    }


    @Override
    public Point proj(Point p) {
        return new CoupRliec<>(new RnAffineProjection(p), halfspaces).argMin();
    }

    /**
     * A random non empty polytope that contains the given sphere centered at the origin
     * @param numFaces
     * @param radius
     * @param dim
     * @return 
     */
    public static RnPolyhedron randomNonEmpty(int numFaces, double radius, int dim) {
        Random rand = new Random();

        RnPolyhedron poly = new RnPolyhedron();
        IntStream.range(0, numFaces).forEach(i -> {

            PointD random = PointD.uniformRand(new PointD(dim), radius);

            random.multMe(radius * (rand.nextDouble() + 1) / random.magnitude());

            poly.add(new HalfSpace<Point>(random, random));
        });

        return poly;
    }

    /**
     * A random possibly empty polytope
     * @param numFaces
     * @param radius
     * @param dim
     * @return 
     */
    public static RnPolyhedron random(int numFaces, double radius, int dim) {

        RnPolyhedron poly = new RnPolyhedron();
        IntStream.range(0, numFaces).forEach(i -> {
            PointD random1 = PointD.uniformRand(new PointD(dim), radius);
            PointD random2 = PointD.uniformRand(new PointD(dim), radius);

            poly.add(new HalfSpace<Point>(random1.mult(radius), random2.mult(1 / random2.magnitude())));
        });

        return poly;
    }

}

