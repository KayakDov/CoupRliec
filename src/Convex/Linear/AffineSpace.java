package Convex.Linear;

import Convex.ConvexSet;
import Convex.Polytope;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys.ASKeyRI;
import Matricies.Matrix;
import Matricies.ReducedRowEchelonDense;
import Matricies.Point;
import Matricies.PointD;
import Matricies.PointSparse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This is an affine space, represented internally as the solution Ax=b with the
 * option of the additional representation of the linear null space A and a
 * point p in the affine space.
 *
 * @author Dov Neimand
 */
public class AffineSpace implements ConvexSet {

    /**
     * The linear space underlying the affine space. If the affine space is Ax =
     * b then this linear space is Ax = 0.
     */
    protected LinearSpace linearSpace;

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
     * A small number
     */
    private double epsilon = 1e-8;

    /**
     * sets a zero threshold number
     *
     * @param epsilon
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * This affine space is the solution set to matrix*x == b. After b is added,
     * it's changed to a row instead of a column.
     *
     * @param normals
     * @param b
     */
    public AffineSpace(Point[] normals, Point b) {
        linearSpace = new LinearSpace(normals);
        this.b = b;
        setHashCode();
    }

    /**
     * The matrix A with Ax = b.
     *
     * @return
     */
    public Matrix nullMatrix() {
        return linearSpace.matrix();
    }

    /**
     * a constructor
     *
     * @param ls a linear space parallel to this affine space
     * @param onSpace a point in the affine space
     */
    public AffineSpace(LinearSpace ls, Point onSpace) {
        linearSpace = ls;
        if (!ls.isAllSpace()) {
            b = new PointD(ls.getNormals().length).setAll(i -> ls.getNormals()[i].dot(onSpace));
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
    public AffineSpace(Set<Plane> intersectingPlanes) {

        Iterator<Plane> iter = intersectingPlanes.iterator();
        Point[] normals = new Point[intersectingPlanes.size()];
        b = new PointD(intersectingPlanes.size());

        for (int i = 0; i < intersectingPlanes.size(); i++) {
            Plane p = iter.next();
            normals[i] = p.normal();
            b.set(i, p.b.get(0));
        }

        linearSpace = new LinearSpace(normals);
        setHashCode();
    }

    /**
     * The constructor.
     *
     * @param planes The planes that intersect to form this affine space.
     */
    public AffineSpace(Plane[] planes) {
        Point[] normals = new Point[planes.length];
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

        Point[] normals = new PointD[intersectingPlanes.size()];
        b = new PointD(intersectingPlanes.size());

        for (int i = 0; i < intersectingPlanes.size(); i++) {
            b.set(i, intersectingPlanes.get(i).b.get(0));
            normals[i] = intersectingPlanes.get(i).normal();
        }

        linearSpace = new LinearSpace(normals);
        setHashCode();
    }

    @Override
    public boolean hasElement(Point x) {
        return nullMatrix().mult(x).equals(b);
    }

    @Override
    public boolean hasElement(Point x, double epsilon) {

        return nullMatrix().mult(x).equals(b, epsilon);
    }

    /**
     * A point in this affine space.
     */
    protected Point p = null;

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
    public AffineSpace setP(Point p) {
        this.p = p;
        return this;
    }

    /**
     * A point in the affine space. If on has not previously been found or set,
     * then it is computed.
     *
     * @return
     */
    public Point p() {

        if (p != null) return p;

        ReducedRowEchelonDense rre = new ReducedRowEchelonDense(nullMatrix());

        if (nullMatrix().isSquare() && rre.hasFullRank())
            return p = nullMatrix().solve(b);

        Matrix append = Matrix.fromRows(
                rre.getFreeVariables().map(i -> {
                    Point row = new PointD(rre.numCols);
                    row.set(i, 1);
                    return row;
                }).toArray(PointD[]::new)
        );

        Point b2 = b.concat(new PointSparse(append.rows()));
        try {
            return p = nullMatrix().rowConcat(append).solve(b2);

        } catch (Exception ex) {
            System.out.println("Convex.Linear.AffineSpace.p()");
            System.out.println(nullMatrix().rowConcat(append));
            System.out.println(this);
            System.out.println("rre = \n" + rre);
            throw ex;
//            throw new NoSuchElementException("This affine space is empty." + this);
        }
    }

    /**
     * The projection onto this affine space. After this function is called the
     * first time, the projection function generated is saved for future use.
     *
     * @param x the point being projected
     * @return the projection onto this space.
     */
    @Override
    public Point proj(Point x) {
        if (isAllSpace()) return x;
        try {
            if (!hasProjFunc())
                projFunc = new ProjectionFunction(linearSpace(), p(), epsilon);
        } catch (NoSuchElementException nse) {
            throw new ProjectionFunction.NoProjFuncExists(linearSpace);
        }
        return projFunc.apply(x);
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

        return new AffineSpace(linearSpace.intersection(as.linearSpace).getNormals(), b.concat(as.b));

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

    /**
     * The projection function used to find the projection onto this space.
     */
    private ProjectionFunction projFunc = null;

    /**
     * Has a projection function been found in the past?
     *
     * @return
     */
    public boolean hasProjFunc() {
        return projFunc != null;
    }

    @Override
    public String toString() {
        return linearSpace().toString() + "\nb = " + b;//(p != null ? "\nwith point " + p : "\nb = " + b);
    }

    private long subSpaceDim = -2;

    /**
     * The dimension of the linear space.
     *
     * @return
     */
    public long subSpaceDim() {
        if (subSpaceDim != -2) return subSpaceDim;
        if (nullMatrix().rows() == 0) return subSpaceDim = dim();
        return subSpaceDim = linearSpace().subSpaceDim();
    }

    /**
     * Gets the affine orthoganal complement of the affine space at a given
     * point.
     *
     * @param x a point on the orthognalal complement space.
     * @return an affine space orthoganal to this one that goes through the
     * given point.
     */
    public AffineSpace orthogonalComplement(Point x) {
        return new AffineSpace(linearSpace().OrhtogonalComplement(), x);
    }

    /**
     * An affine space that is all of the space.
     *
     * @param dim
     * @return
     */
    public static AffineSpace allSpace(int dim) {
        return new AffineSpace(LinearSpace.allSpace(0), new PointSparse(dim));
    }

    /**
     * This affine space as an intersection of half spaces.
     *
     * @return
     */
    public Polytope asPolytope() {

        return new Polytope(
                nullMatrix().rowStream()
                        .flatMap(row -> new Plane(p(), row)
                        .asPolytope().stream()));
    }

    /**
     * the dimension of the space containing this affine space
     *
     * @return
     */
    public int dim() {
        if (nullMatrix().rows() == 0 && p != null) return p.dim();
        return nullMatrix().cols();
    }

    /**
     * is this space a subset of the given space
     *
     * @param containing the space that contains this space
     * @return true if the space contains this space
     */
    public boolean subsetOf(AffineSpace containing) {
        return containing.hasElement(p())
                && linearSpace().colSpaceMatrix().colStream()
                        .allMatch(col -> containing.hasElement(col.plus(p())));
    }

    /**
     * Is the given space a subset of this space
     *
     * @param subset the possible subset
     * @return true if it's a subset, false otherwise
     */
    public boolean containsAsSubset(AffineSpace subset) {
        return subset.subsetOf(this);
    }

    /**
     * The line going through these two points
     *
     * @param a
     * @param b
     * @return a line going through the two given points
     */
    public static AffineSpace twoPointsLine(Point a, Point b) {
        return PointSlopeLine(a, b.minus(a));
    }

    /**
     * A line going through the given point with the given slope
     *
     * @param a the given point
     * @param grad the given slope
     * @return a line
     */
    public static AffineSpace PointSlopeLine(Point a, Point grad) {

        return new AffineSpace(LinearSpace.colSpace(grad), a);
    }

    /**
     * Are all points in space in this affine space?
     *
     * @return
     */
    public boolean isAllSpace() {
        return linearSpace.isAllSpace();
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

    /**
     * If this affine space is defined by the set of solutions to Ax=b, then
     * this function returns the set of A'x = b, where A' is A with one row
     * removed.
     *
     * @return the keys for the afformentioned affine spaces
     */
    public ASKeyRI[] oneDownKeys() {
        int numRows = linearSpace.getNormals().length;

        if (numRows == 1)
            throw new RuntimeException("oneDown may not be called on planes.");

        ASKeyRI[] oneDownArray = new ASKeyRI[numRows];
        Arrays.setAll(oneDownArray, i -> new ASKeyRI(this, i));
        return oneDownArray;
    }

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

    /**
     * The planes that intersect to make this affine space
     *
     * @return
     */
    public Stream<Plane> intersectingPlanesStream() {
        return IntStream.range(0, b.dim()).mapToObj(i -> new Plane(linearSpace.normals[i], b.get(i)));
    }
}
