package Hilbert;

import Convex.ASKeys.ASKey;
import Convex.ConvexSet;
import Matricies.Point;
import Matricies.Point;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    public Point b;

    /**
     * The vector that has Ax = b.
     *
     * @return
     */
    public Point getB() {
        return b;
    }

    /**
     * The constructor
     *
     * @param ls solution to Ax = b
     * @param b
     */
    public AffineSpace(LinearSpace<Vec> ls, Point b) {
        this(ls);
        this.b = b;
    }

    /**
     * The constructor. This should be used if a point p is available and called
     * together with setP(), if not then this will be a linear space.
     *
     * @param ls
     */
    public AffineSpace(LinearSpace<Vec> ls) {
        this.linearSpace = ls;
    }

    /**
     * This method sets a point on the affine space.If b has a value, then it is
     * incumbent on the caller to make sure this point is in the affine space.
     * If b does not have a value, then this method sets b.
     *
     * @param onSpace a point in the affine space.
     * @return returns this
     */
    public AffineSpace<Vec> setP(Vec onSpace) {
        if (!linearSpace.isAllSpace() && b == null) {
            b = new Point(linearSpace.normals().length, i -> linearSpace.normals()[i].ip(onSpace));
            setHashCode();
        } else hashCode = 0;
        p = onSpace;
        return this;
    }

    /**
     * A small number
     */
    public double tolerance = 1e-8;

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
    public AffineSpace(Vec[] normals, Point b) {
        this(new LinearSpace(normals), b);
    }

    /**
     * The matrix A with Ax = b.
     *
     * @return
     */
    public Vec[] nullMatrixRows() {
        return linearSpace.normals;
    }

    public AffineSpace(AffineSpace<Vec> as) {
        this(as.linearSpace, as.b);
    }

    /**
     * The constructor.
     *
     * @param planes The planes that intersect to form this affine space.
     */
    public AffineSpace(Plane<Vec>[] planes) {
        if (planes.length != 0) {

            Vec[] normals = (Vec[]) (Array.newInstance(
                    planes[0].normal().getClass(),
                    planes.length
            ));
            Arrays.setAll(normals, i -> planes[i].normal());
            b = new Point(planes.length, i -> planes[i].b.get(0));
            linearSpace = new LinearSpace(normals);
        }
        setHashCode();
    }

    /**
     * A constructor
     *
     * @param intersectingPlanes planes that intersect to form this affine
     * space.
     */
    public AffineSpace(List<? extends Plane<Vec>> intersectingPlanes) {
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
     * The intersection of this affine space and the given affine space
     *
     * @param as another affine space
     * @return the intersection of the two spaces.
     */
    public AffineSpace<Vec> intersection(AffineSpace<Vec> as) {
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
    public static <Vec extends Vector<Vec>> AffineSpace<Vec> intersection(AffineSpace<Vec>[] space) {
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
    public static <Vec extends Vector<Vec>> AffineSpace<Vec> intersection(Stream<? extends AffineSpace<Vec>> space) {
        return space.map(as -> (AffineSpace) as).reduce((a, b) -> a.intersection(b)).get();
    }

    /**
     * The underlying linear space. If Ax = b the this returns Null(A).
     *
     * @return
     */
    public LinearSpace<Vec> linearSpace() {
        return linearSpace;
    }

    @Override
    public String toString() {
        return linearSpace().toString() + "*x = " + b;//(p != null ? "\nwith point " + p : "\nb = " + b);
    }

    private long subSpaceDim = -2;

    private AffineSpace() {

    }

    /**
     * An affine space that is all of the space.
     *
     * @param <T> the type of vector space.
     * @return
     */
    public static <T extends Vector<T>> AffineSpace<T> allSpace() {
        return new AffineSpace<T>();
    }

    public boolean isAllSpace() {
        return linearSpace == null || linearSpace.isAllSpace();
    }

    /**
     * A stream of a list of planes that intersect to form this affine space.
     *
     * @return
     */
    public Stream<Plane<Vec>> planeStream() {
        return IntStream.range(0, b.dim())
                .mapToObj(i -> new Plane<Vec>(linearSpace.normals()[i], b.get(i)));
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
        return new Plane(linearSpace.normals()[i], b.get(i));
    }

    @Override
    public int hashCode() {
        if (!hashCodeIsSet) setHashCode();
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
     * The hash value for this function.
     */
    private int hashCode;

    /**
     * Is the hashcode set?
     */
    public boolean hashCodeIsSet = false;

    /**
     * Sets the hashcode.
     */
    protected void setHashCode() {
        if(isAllSpace()) hashCode = 0;
        else hashCode = ASKey.hashCodeGenerator(linearSpace.normals, b);
        hashCodeIsSet = true;
        
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
    public HashSet<Plane<Vec>> intersectingPlanesSet() {
        HashSet<Plane<Vec>> planes = new HashSet<>(b.dim());
        for (int i = 0; i < b.dim(); i++)
            planes.add(new Plane(linearSpace.normals[i], b.get(i)));
        return planes;
    }

    @Override
    public Vec proj(Vec x) {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Is the given row index of this affine spaces constraints equal to the
     * given hyperplane.
     *
     * @param i the index of one of the constraints that make this affine space.
     * @param p the plane that might be equal to the constraint.
     * @return true if the constraint at the given index is equal to the
     * hyperplane, false otherwise.
     */
    public boolean rowEquals(int i, Plane<Vec> p) {
        return this.nullMatrixRows()[i].equals(p.normal()) && b.get(i) == p.b();
    }

    /**
     * are the given constraints equal
     *
     * @param i the i constraint in this affine space
     * @param j the j constraint in the given affine space
     * @param p the given affine space
     * @return true if the i constraint in this affine space is equal to the j
     * constraint in the given affine space.
     */
    public boolean rowEquals(int i, int j, AffineSpace<Vec> p) {
        return this.nullMatrixRows()[i].equals(p.nullMatrixRows()[j])
                && b.get(i) == p.b.get(j);
    }

    /**
     * A point in the affine space.
     *
     * @return
     */
    public Vec p() {
        if (p == null)
            throw new NullPointerException("The value of p has not been set and can't currently be calculated for an arbitrary affines space.");
        return p;
    }
}
