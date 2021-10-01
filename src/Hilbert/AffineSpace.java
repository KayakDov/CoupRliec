package Hilbert;

import Convex.ConvexSet;
import Matricies.PointD;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This is an affine space of finite co-dimension, represented internally as the
 * solution Ax=b with the option of the additional representation of the linear
 * null space A and a point p in the affine space.
 *
 * @author Dov Neimand
 * @param <Vec> the type of element in the Hilbert space
 */
public class AffineSpace<Vec extends Vector<Vec>> implements ConvexSet<Vec> {

    /**
     * The linear space underlying the affine space. If the affine space is Ax =
     * b then this linear space is Ax = 0.
     */
    protected LinearSpace<Vec> linearSpace;

    /**
     * The vector that has Ax=b.
     */
    public PointD b;

    /**
     * The vector that has Ax = b.
     *
     * @return
     */
    public PointD getB() {
        return b;
    }

    /**
     * A small number
     */
    private double tolerance = 1e-8;

    /**
     * sets a zero threshold number
     *
     * @param tolerance
     */
    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * This affine space is the solution set to matrix*x == b. After b is added,
     * it's changed to a row instead of a column.
     *
     * @param normals
     * @param b
     */
    public AffineSpace(Vec[] normals, PointD b) {
        linearSpace = new LinearSpace(normals);
        this.b = b;
        setHashCode();
    }

    /**
     * The matrix A with Ax = b.
     *
     * @return
     */
    public Vec[] nullMatrix() {
        return linearSpace.normals;
    }

    /**
     * a constructor
     *
     * @param ls a linear space parallel to this affine space
     * @param onSpace a point in the affine space
     */
    public AffineSpace(LinearSpace<Vec> ls, Vec onSpace) {
        linearSpace = ls;
        if (!ls.isAllSpace()) {
            b = new PointD(ls.getNormals().length).setAll(i -> ls.getNormals()[i].ip(onSpace));
            setHashCode();
        } else hashCode = 0;
        p = onSpace;
    }

    /**
     * A constructor
     *
     * @param intersectingPlanes the planes that intersect to form this affine
     * space.
     */
    public AffineSpace(Set<Plane<Vec>> intersectingPlanes) {

        this((Plane[]) (Array.newInstance(
                intersectingPlanes.iterator().next().getClass(),
                intersectingPlanes.size()
        )));

    }

    /**
     * The constructor.
     *
     * @param planes The planes that intersect to form this affine space.
     */
    public AffineSpace(Plane<Vec>[] planes) {
        Vec[] normals = (Vec[]) (Array.newInstance(
                planes[0].normal().getClass(),
                planes.length
        ));
        Arrays.setAll(normals, i -> planes[i].normal());
        b = new PointD(planes.length).setAll(i -> planes[i].b.get(0));
        linearSpace = new LinearSpace(normals);
        setHashCode();
    }

    /**
     * A constructor
     *
     * @param intersectingPlanes planes that intersect to form this affine
     * space.
     */
    public AffineSpace(List<Plane> intersectingPlanes) {
        this((Plane[]) (Array.newInstance(
                intersectingPlanes.get(0).getClass(),
                intersectingPlanes.size()
        )));

    }

    @Override
    public boolean hasElement(Vec x) {
        return hasElement(x, tolerance);
    }

    @Override
    public boolean hasElement(Vec x, double tolerance) {

        for (int i = 0; i < linearSpace.normals.length; i++)
            if (Math.abs(linearSpace.normals[i].ip(x) - b.get(i))
                    > tolerance) return false;
        return true;
    }

    /**
     * A point in this affine space.
     */
    protected Vec p = null;

    /**
     * Has a point in this affine space been found and saved.
     *
     * @return
     */
    public boolean hasAPoint() {
        return p != null;
    }

    /**
     * This method is unprotected.It is on the caller to make sure that the
     * given point is in the affine space.
     *
     * @param p
     * @return
     */
    public AffineSpace setP(Vec p) {
        this.p = p;
        return this;
    }

    /**
     * The intersection of this affine space and the given affine space
     *
     * @param as another affine space
     * @return the intersection of the two spaces.
     */
    public AffineSpace intersection(AffineSpace as) {
        if (isAllSpace()) return as;
        if (as.isAllSpace()) return this;

        return new AffineSpace(linearSpace.intersection(as.linearSpace).normals, b.concat(as.b));

    }

    /**
     * The intersections of the given affine spaces
     *
     * @param space
     * @return a new affine space
     */
    public static AffineSpace intersection(AffineSpace[] space) {
        if (space.length == 0)
            throw new RuntimeException("Empty intersection?");
        return intersection(Arrays.stream(space));
    }

    /**
     * The intersections of the given affine spaces
     *
     * @param space
     * @return a new affine space
     */
    public static AffineSpace intersection(Stream<? extends AffineSpace> space) {
        return space.map(as -> (AffineSpace) as).reduce((a, b) -> a.intersection(b)).get();
    }

    /**
     * The underlying linear space. If Ax = b the this returns Null(A).
     *
     * @return
     */
    public LinearSpace linearSpace() {
        return linearSpace;
    }

    @Override
    public String toString() {
        return linearSpace().toString() + "\nb = " + b;//(p != null ? "\nwith point " + p : "\nb = " + b);
    }

    private long subSpaceDim = -2;

    private AffineSpace() {

    }

    /**
     * An affine space that is all of the space.
     *
     * @param dim
     * @return
     */
    public static AffineSpace allSpace(int dim) {
        return new AffineSpace();
    }

    public boolean isAllSpace() {
        return linearSpace == null || linearSpace.isAllSpace();
    }

    /**
     * A stream of a list of planes that intersect to form this affine space.
     *
     * @return
     */
    public Stream<Plane> planeStream() {
        return IntStream.range(0, b.dim())
                .mapToObj(i -> new Plane<Vec>(linearSpace.getNormals()[i], b.get(i)));
    }

    /**
     * This affine space as an intersection of half spaces.
     *
     * @return
     */
    public Polyhedron polyhedralCone() {

        return new Polyhedron(
                planeStream().map(p -> new HalfSpace<Vec>(p))
        );
    }


    /**
     * Will return a plane for which this affine space is a subset. If this
     * affine space is the solution to Ax=b then this will give the solutions to
     * row(A,i) dot x = b_i
     *
     * @param i
     * @return
     */
    public Plane subsetOfPlane(int i) {
        return new Plane(linearSpace.getNormals()[i], b.get(i));
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof AffineSpace)) return false;
        final AffineSpace other = (AffineSpace) obj;
        return other.linearSpace.equals(linearSpace) && other.b.equals(b);
    }

    /**
     * Is this affine space equal to the given affine space.
     *
     * @param obj
     * @return
     */
    public boolean equals(AffineSpace obj) {

        return obj.linearSpace.equals(linearSpace) && obj.b.equals(b);

    }

    /**
     * The has value for just one row.
     *
     * @param row the row
     * @return the hash value of the hyperplane the row represents.
     */
    public int hashRow(int row) {
        return linearSpace.normals[row].hashCode() * Double.hashCode(b.get(row));//When this is plus there is no null pointer bug
    }
    /**
     * The hash value for this function.
     */
    private int hashCode;

    /**
     * Sets and saves the hashcode.
     */
    private void setHashCode() {
        hashCode = 0;
        for (int i = 0; i < b.dim(); i++) hashCode += hashRow(i);
    }

//    /**
//     * If this affine space is defined by the set of solutions to Ax=b, then
//     * this function returns the set of A'x = b', where A' is A with one row
//     * removed.
//     *
//     * @return the keys for the afformentioned affine spaces
//     */
//    public ASKeyRI[] immidiateSuperKeys() {
//        int numRows = linearSpace.getNormals().length;
//
//        if (numRows == 1)
//            throw new RuntimeException("oneDown may not be called on planes.");
//
//        ASKeyRI[] oneDownArray = new ASKeyRI[numRows];
//        Arrays.setAll(oneDownArray, i -> new ASKeyRI(this, i));
//        return oneDownArray;
//    }

    /**
     * The planes that intersect to make this affine space
     *
     * @return
     */
    public HashSet<Plane> intersectingPlanesSet() {
        HashSet<Plane> planes = new HashSet<>(b.dim());
        for (int i = 0; i < b.dim(); i++)
            planes.add(new Plane(linearSpace.normals[i], b.get(i)));
        return planes;
    }

    @Override
    public Vec proj(Vec x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
