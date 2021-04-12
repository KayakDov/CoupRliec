package Convex.Linear;

import Convex.ConvexSet;
import Convex.Polytope;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys.ASKey;
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
 *
 * @author Dov Neimand
 */
public class AffineSpace implements ConvexSet {

    protected LinearSpace linearSpace;
    public Point b;

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
        if (!ls.isAllSpace()){
            b = new PointD(ls.getNormals().length).setAll(i -> ls.getNormals()[i].dot(onSpace));
            setHashCode();
        }
        else hashCode = 0;
        p = onSpace;
    }

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

    public AffineSpace(Plane[] planes) {
        Point[] normals = new Point[planes.length];
        Arrays.setAll(normals, i -> planes[i].normal());
        b = new PointD(planes.length).setAll(i -> planes[i].b.get(0));
        linearSpace = new LinearSpace(normals);
        setHashCode();
    }

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

    protected Point p = null;

    public boolean hasAPoint() {
        return p != null;
    }

    /**
     * This method is unprotected. It is on the caller to make sure that the
     * given point is in the affine space.
     *
     * @param p
     */
    public AffineSpace setP(Point p) {
        this.p = p;
        return this;
    }

    /**
     * A point in the affine space
     *
     * @return
     */
    public Point p() {

        if (p != null) return p;

        ReducedRowEchelonDense rre = new ReducedRowEchelonDense(nullMatrix());

        if (nullMatrix().isSquare() && rre.hasFullRank())
            return p = nullMatrix().solve(b);

        boolean isSparse = linearSpace.getNormals()[0].isSparse();
        Matrix append = Matrix.fromRows(
                rre.getFreeVariables().map(i -> new PointD(rre.numCols).set(i, 1)).toArray(PointD[]::new)
        );

        Point b2 = b.concat(new PointSparse(append.rows()));

        try {

            return p = nullMatrix().rowConcat(append).solve(b2);

        } catch (Exception ex) {

            throw new NoSuchElementException("This affine space is empty." + this);
        }
    }

    /**
     * p must be set before this function is called.
     *
     * @param x
     * @return
     */
    @Override
    public Point proj(Point x) {
        if (isAllSpace()) return x;
        if(!hasProjFunc()) projFunc = new ProjectionFunction(linearSpace(), p, epsilon);
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

    public static AffineSpace intersection(AffineSpace[] space) {
        if (space.length == 0)
            throw new RuntimeException("Empty intersection?");
        return intersection(Arrays.stream(space));
    }

    public static AffineSpace intersection(Stream<? extends AffineSpace> space) {
        return space.map(as -> (AffineSpace) as).reduce((a, b) -> a.intersection(b)).get();
    }

    public LinearSpace linearSpace() {
        return linearSpace;
    }

    private ProjectionFunction projFunc = null;
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

    public boolean equals(AffineSpace obj) {

        return obj.linearSpace.equals(linearSpace) && obj.b.equals(b);

    }

    

    public int hashRow(int row){
        return linearSpace.normals[row].hashCode() * Double.hashCode(b.get(row));//When this is plus there is no null pointer bug
    }
    private int hashCode;

    private void setHashCode() {
        hashCode = 0;
        for(int i = 0; i < b.dim(); i++) hashCode += hashRow(i);
    }
    
    
    
    /**
     * If this affine space is defined by the set of solutions to Ax=b, then
     * this function returns the set of A'x = b, where A' is A with one row
     * removed.
     *
     * @return the keys for the afformentioned affine spaces
     */
    
    public ASKeyRI[] oneDownKeys(){
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
