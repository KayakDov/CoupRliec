package Hilbert;

import Matricies.Point;

/**
 * This is an affine space of finite co-dimension, represented internally as the
 * solution Ax=b with the option of the additional representation of the linear
 * null space A and a point p in the affine space.
 *
 * @author Dov Neimand
 * @param <Vec> the type of element in the Hilbert space
 */
public class AffineSpace<Vec extends Vector<Vec>>{

    /**
     * The linear space underlying the affine space. If the affine space is Ax =
     * b then this linear space is Ax = 0.
     */
    protected final LinearSpace<Vec> linearSpace;

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
        this.linearSpace = ls;
        this.b = b;
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

    /**
     * A copy constructor
     * @param as 
     */
    public AffineSpace(AffineSpace<Vec> as) {
        this(as.linearSpace, as.b);
    }
    
    /**
     * Does this affine space contain the proffered element. A default tolerance 
     * is used.
     * @param x
     * @return 
     */
    public boolean hasElement(Vec x) {
        return hasElement(x, tolerance);
    }
    
    /**
     * Does this affine space have the proffered element.
     * @param x
     * @param tolerance
     * @return 
     */
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

    private AffineSpace() {
        linearSpace = LinearSpace.allSpace();
    }

    /**
     * An affine space that is all of the space.
     *
     * @param <T> the type of vector space.
     * @return The Hilbert space.
     */
    public static <T extends Vector<T>> AffineSpace<T> allSpace() {
        return new AffineSpace<>();
    }

    /**
     * Is this space the Hilbert space?
     * @return true if it is, false otherwise.
     */
    public boolean isAllSpace() {
        return linearSpace == null || linearSpace.isAllSpace();
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
