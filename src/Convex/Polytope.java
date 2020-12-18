package Convex;

import Convex.Linear.Plane;
import Convex.Linear.AffineSpace;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.GradDescentFeasibility;
import DiscreteMath.Choose;
import Matricies.Matrix;
import RnSpace.Sequence;
import RnSpace.points.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    
    public void removeAll(Collection<HalfSpace> colec){
        halfSpaces.removeAll(colec);
        halfSpaceSet.removeAll(colec);
        reInit();
    }
    /**
     * All the vertices of the polytope. be sure to call with getVertices().
     */
    protected Matrix vertices;
    /**
     * The vertices of each halfspace.
     */
    protected HashMap<HalfSpace, Matrix> faceVerticies;

    /**
     * A matrix of all the normal vectors
     *
     * @return
     */
    public Matrix normalMatix() {
        return Matrix.fromRows(stream().map(face -> face.normal()));
    }

    /**
     * Every Point x in this polytope satisfies the equation Mx less than b
     * where M is the matrix of normals. Each face f satisfies the equation
     * f*x=bi.
     *
     * @return
     */
    public Point b() {
        return new Point(size()).setAll(i -> halfSpaces.get(i).normal().dot(halfSpaces.get(i).surfacePoint()));
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
                IntStream.range(0, normals.rows).mapToObj(i -> new HalfSpace(
                normals.row(i), b.get(i)))
                        .collect(Collectors.toList()));
        halfSpaceSet.addAll(halfSpaces);
    }

    public Polytope() {
        this(new ArrayList<HalfSpace>());
    }

    protected Polytope setVertices() {
        vertices = verticies();
        faceVerticies = new HashMap<>(size());
        stream().forEach(face -> faceVerticies.put(face, Matrix.fromRows(
                vertices.rowStream().filter(point
                        -> face.onSurface(point, epsilon)))));
        return this;
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
        this.faceVerticies = new HashMap<>();
    }

    @Override
    public boolean hasElement(Point p) {//TODO: make this paralel
        return stream().allMatch((HalfSpace hs) -> hs.hasElement(p, epsilon));
    }

    private Point lastProjectionSource = null, lastProjection = null;

    public boolean hasHalfSpaces() {
        return halfSpaces.isEmpty();
    }

    /**
     * dykstras proj algorithm onto polytopes see
     * https://drive.google.com/drive/u/0/folders/1kqfDQvhd1FT-xaq89WlmYQn8MNV55BWs
     *
     * @AUTHOR="Deutsch, F. and Hundal, H.", @TITLE="The rate of convergence of
     * Dykstra's cyclic projections algorithm: The polyhedral case.", @JOURNAL=
     * "Numerical Functional Analysis and Optimization", @VOLUME="15", @number =
     * "5-6", @PAGES="537-565", @YEAR="1994"
     *
     * @param y
     * @return
     */
    @Override
    public Point proj(Point y) {

        if (!hasHalfSpaces()) {
            return y;
        }
        if (lastProjectionSource != null
                && lastProjectionSource.equals(y)) {
            return new Point(lastProjection);
        }

        return new DykstraSeq().cauchyLimit(y, epsilon);

    }

    /**
     * Dykstra's algorithm as represented by the sequence
     */
    class DykstraSeq implements Sequence {

        private LinkedList<Point> e = new LinkedList<>();

        private LinkedList<Point> prevX = new LinkedList<>();

        DykstraSeq() {
            for (int i = 0; i < size(); i++) {
                e.add(new Point(dim()));
            }

            for (int i = 0; i < 2 * size(); i++) {
                prevX.add(new Point(dim()));
            }
        }

        private boolean end(Point x, double epsilon) {
            return prevX.stream().allMatch(prev -> prev.d(x) < epsilon);
        }

        @Override
        public Point cauchyLimit(Point start, double end) {
            Point x = iteration(start, 1);

            for (int i = 2; !end(x, epsilon); i++) {
                x = iteration(x, i);
            }

            return x;
        }

        void e(Point prevX, Point x) {
            e.add(prevX.plus(e.removeFirst()).minus(x));
        }

        @Override
        public Point iteration(Point prevX, int n) {
            HalfSpace faceN = halfSpaces.get(n % size());
            Point x = faceN.proj(prevX.plus(e.getFirst()));
            e(prevX, x);
            this.prevX.add(x);
            this.prevX.remove();
            return x;
        }
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
        if (faceVerticies != null) {
            faceVerticies.forEach((K, V) -> sb.append(K).append("\n").append(V).append("\n"));
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

    /**
     * This should create a list of the verticies of this polytope. Not sure
     * what will happen if two planes have parallel normal lines. Regardless,
     * this needs to be tested. TODO::Test this function.
     *
     *
     * @return
     */
    private Matrix verticies() {
        return Matrix.fromRows(vertexStream());
    }

    private Stream<Point> vertexStream() {
        return new Choose<>(halfSpaces, dim()).chooseStream()
                .filter(hsSet -> new Polytope(hsSet).normalMatix().det() != 0)
                .map(hsSet -> AffineSpace.intersection(hsSet.stream().map(hs -> hs.boundary())).p())
                .filter(p -> isMember(p));
    }

    public Matrix getVertices() {
        if (vertices == null) {
            vertices = verticies();
        }
        return vertices;
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

        return IntStream.rangeClosed(1, maxHSPerIntersection).parallel().mapToObj(i -> i)
                .flatMap(i -> new Choose<>(
                halfSpaces.stream().map(hs -> hs.boundary()).collect(Collectors.toList()),
                i
        ).chooseStream()).map(hsList -> {
            try {
                return AffineSpace.intersection(hsList.stream());
            } catch (ArithmeticException ae) {
                return null;
            }
        }).filter(as -> as != null);

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
     * A random halfspace
     *
     * @param numFaces the number of faces
     * @param radius some point on the surface of the half space will be less
     * than or equal to radius distance from the origin.
     * @param dim the number of dimensions the half space is in
     * @return a random half space.
     */
    public static Polytope random(int numFaces, double radius, int dim) {
        return new Polytope(
                IntStream.range(0, numFaces)
                        .mapToObj(i -> HalfSpace.random(radius, dim)));
    }

    /**
     * @param numFaces The number of faces of the polytope
     * @param radius the nearest point on the polytope to the origan, with a
     * normal facing out.
     * @param dim the number of dimensions of the polytope
     * @return a random polytope containing a ball centered at the origan.
     */
    public static Polytope randomNonEmpty(int numFaces, double radius, int dim) {
        return new Polytope(
                IntStream.range(0, numFaces).mapToObj(i -> {
                    Point normal = new Sphere(dim, radius).randomSurfacePoint();
                    return new HalfSpace(normal, normal);
                })
        );
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

    
}
