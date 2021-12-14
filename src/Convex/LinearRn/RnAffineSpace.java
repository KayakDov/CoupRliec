package Convex.LinearRn;

import Convex.ConvexSet;
import Convex.RnPolyhedron;
import Hilbert.AffineSpace;
import Hilbert.HalfSpace;
import Hilbert.LinearSpace;
import Matricies.Matrix;
import Matricies.ReducedRowEchelonDense;
import Matricies.Point;
import Matricies.PointD;
import Matricies.PointSparse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This is an affine space of finite co-dimension, represented internally as 
 * the solution Ax=b with the option of the additional representation of the 
 * linear null space A and a point p in the affine space.
 *
 * @author Dov Neimand
 */
public class RnAffineSpace extends AffineSpace<Point> implements ConvexSet<Point> {

    /**
     * A constructor
     * @param ls a linear space
     * @param b every point in this affine space will be a solution to the 
     * linear space times x equals b.
     */
    public RnAffineSpace(LinearSpace<Point> ls, Point b) {
        super(ls, b);
    }


    
    /**
     * This affine space is the solution set to matrix*x == b. After b is added,
     * it's changed to a row instead of a column.
     *
     * @param normals
     * @param b
     */
    public RnAffineSpace(Point[] normals, Point b) {
        super(normals, b);
    }

    /**
     * The matrix A with Ax = b.
     *
     * @return
     */
    public Matrix nullMatrix() {
        return Matrix.fromRows(linearSpace.normals());
    }

    /**
     * a constructor
     *
     * @param ls a linear space parallel to this affine space
     * @param onSpace a point in the affine space
     */
    public RnAffineSpace(RnLinearSpace ls, Point onSpace) {
        super(ls);
        setP(onSpace);
    }

    /**
     * The constructor.
     *
     * @param planes The planes that intersect to form this affine space.
     */
    public RnAffineSpace(RnPlane[] planes) {
        super(planes);
    }
    
    
    /**
     * The constructor.
     *
     * @param planes The planes that intersect to form this affine space.
     */
    public RnAffineSpace(Set<RnPlane> planes) {
        this(planes.toArray(RnPlane[]::new));
    }

    /**
     * A constructor
     *
     * @param intersectingPlanes planes that intersect to form this affine
     * space.
     */
    public RnAffineSpace(List<RnPlane> intersectingPlanes) {
        super(intersectingPlanes);
    }


    /**
     * This method is unprotected.It is on the caller to make sure that the
     * given point is in the affine space.
     *
     * @param p
     * @return
     */
    @Override
    public RnAffineSpace setP(Point p) {
        this.p = p;
        return this;
    }
    
    public RnAffineSpace(AffineSpace<Point> as){
        super(as);
    }

    /**
     * A point in the affine space. If on has not previously been found or set,
     * then it is computed.
     *
     * @return
     */
    @Override
    public Point p() {

        if (p != null) return p;

        ReducedRowEchelonDense rre = new ReducedRowEchelonDense(nullMatrix());

        if (rre.noFreeVariable())
            return p = nullMatrix().solve(b);

        Matrix append = Matrix.fromRows(
                rre.getFreeVariables()
                        .map(i ->  new PointD(rre.numCols, i, 1))
                        .toArray(PointD[]::new)
        );

        Point b2 = b.concat(new PointD(append.rows()));
        try {
            return p = nullMatrix().rowConcat(append).solve(b2);

        } catch (Exception ex) {
            System.out.println("Convex.Linear.AffineSpace.p()");
            System.out.println(nullMatrix().rowConcat(append));
            System.out.println("b2 = " + b2 +"\n");
            System.out.println("this = " + this + "\n");
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
                projFunc = new ProjectionFunction(linearSpace, p(), tolerance);
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
    public RnAffineSpace intersection(RnAffineSpace as) {
        if (isAllSpace()) return as;
        if (as.isAllSpace()) return this;

        return new RnAffineSpace(linearSpace.intersection(as.linearSpace).normals(), b.concat(as.b));

    }

    /**
     * The intersections of the given affine spaces
     *
     * @param space
     * @return a new affine space
     */
    public static RnAffineSpace intersection(RnAffineSpace[] space) {
        if (space.length == 0)
            throw new RuntimeException("Empty intersection?");
        return RnIntersection(Arrays.stream(space));
    }

    /**
     * The intersections of the given affine spaces
     *
     * @param space
     * @return a new affine space
     */
    public static RnAffineSpace RnIntersection(Stream<? extends RnAffineSpace> space) {
        
        return space.map(as -> (RnAffineSpace) as).reduce((a, b) -> a.intersection(b)).get();
    }

    /**
     * The underlying linear space. If Ax = b the this returns Null(A).
     *
     * @return
     */
    @Override
    public LinearSpace<Point> linearSpace() {
        return linearSpace;
    }
    
    /**
     * The underlying linear space. If Ax = b the this returns Null(A).
     *
     * @return
     */
    public RnLinearSpace RnlinearSpace() {
        return new RnLinearSpace(linearSpace);
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
        return linearSpace + "*x = " + b;//(p != null ? "\nwith point " + p : "\nb = " + b);
    }
    

    private long subSpaceDim = -2;

    /**
     * The dimension of the linear space.
     *
     * @return
     */
    public long subSpaceDim() {
        if (subSpaceDim != -2) return subSpaceDim;
        if (nullMatrixRows().length == 0) return subSpaceDim = dim();
        return subSpaceDim = RnlinearSpace().subSpaceDim();
    }

    /**
     * Gets the affine orthoganal complement of the affine space at a given
     * point.
     *
     * @param x a point on the orthognalal complement space.
     * @return an affine space orthoganal to this one that goes through the
     * given point.
     */
    public RnAffineSpace orthogonalComplement(Point x) {
        return new RnAffineSpace(RnlinearSpace().OrhtogonalComplement(), x);
    }

    /**
     * An affine space that is all of the space.
     *
     * @param dim
     * @return
     */
    public static RnAffineSpace allSpace(int dim) {
        return new RnAffineSpace(RnLinearSpace.allSpace(0), new PointSparse(dim));
    }

    /**
     * This affine space as an intersection of half spaces.
     *
     * @return
     */
    @Override
    public RnPolyhedron polyhedralCone() {
        return new RnPolyhedron(planeStream().map(pl -> new HalfSpace<Point>(pl)));
    }

    /**
     * the dimension of the space containing this affine space
     *
     * @return
     */
    public int dim() {
        if (nullMatrixRows().length == 0 && p != null) return p.dim();
        return linearSpace.normals()[0].dim();
    }

    /**
     * is this space a subset of the given space
     *
     * @param containing the space that contains this space
     * @return true if the space contains this space
     */
    public boolean subsetOf(RnAffineSpace containing) {
        return containing.hasElement(p())
                && RnlinearSpace().colSpaceMatrix().colStream()
                        .allMatch(col -> containing.hasElement(col.plus(p())));
    }

    /**
     * Is the given space a subset of this space
     *
     * @param subset the possible subset
     * @return true if it's a subset, false otherwise
     */
    public boolean containsAsSubset(RnAffineSpace subset) {
        return subset.subsetOf(this);
    }

    /**
     * The line going through these two points
     *
     * @param a
     * @param b
     * @return a line going through the two given points
     */
    public static RnAffineSpace twoPointsLine(Point a, Point b) {
        return PointSlopeLine(a, b.minus(a));
    }

    /**
     * A line going through the given point with the given slope
     *
     * @param a the given point
     * @param grad the given slope
     * @return a line
     */
    public static RnAffineSpace PointSlopeLine(Point a, Point grad) {

        return new RnAffineSpace(RnLinearSpace.colSpace(grad), a);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof RnAffineSpace)) return false;
        final RnAffineSpace other = (RnAffineSpace) obj;
        return other.linearSpace.equals(linearSpace) && other.b.equals(b);
    }

    /**
     * Is this affine space equal to the given affine space.
     *
     * @param obj
     * @return
     */
    public boolean equals(RnAffineSpace obj) {

        return obj.linearSpace.equals(linearSpace) && obj.b.equals(b);

    }

    /**
     * The planes that intersect to make this affine space
     *
     * @return
     */
    public HashSet<RnPlane> intersectingRnPlanesSet() {
        HashSet<RnPlane> planes = new HashSet<>(b.dim());
        for (int i = 0; i < b.dim(); i++)
            planes.add(new RnPlane(linearSpace.normals()[i], b.get(i)));
        return planes;
    }

    /**
     * The planes that intersect to make this affine space
     *
     * @return
     */
    public Stream<RnPlane> intersectingPlanesStream() {
        return IntStream.range(0, b.dim()).mapToObj(i -> new RnPlane(linearSpace.normals()[i], b.get(i)));
    }

    public RnAffineSpace(RnPlane p) {
        super(new RnPlane[]{p});
    }
    
    
}
