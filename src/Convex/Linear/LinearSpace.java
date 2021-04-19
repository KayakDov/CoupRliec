package Convex.Linear;

import Convex.ConvexSet;
import Matricies.Matrix;
import Matricies.MatrixDense;
import Matricies.MatrixSparse;
import Matricies.ReducedRowEchelonDense;
import Matricies.Point;
import Matricies.PointD;
import Matricies.PointSparse;
import java.util.Arrays;

/**
 * An object that describes a linear/vector space
 *
 * @author Dov Neimand
 */
public class LinearSpace implements ConvexSet {

    /**
     * Has a projection function been found for this space.
     *
     * @return
     */
    public boolean hasProjFunction() {
        return projFunc != null;
    }

    /**
     * The rows of the the matrix Ax = 0.
     */
    public Point[] normals;

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (int i = 0; i < normals.length; i++)
            hashCode += normals[i].hashCode();
        return hashCode;
    }

    /**
     * Are these two affine spaces equal? Note, the normals of two affine spaces
     * must be in the same order to be equals.
     *
     * @param ls
     * @return
     */
    public boolean equals(LinearSpace ls) {
        return Arrays.equals(normals, ls.normals);
    }

    /**
     * Constructor
     *
     * @param normals the rows of Ax = 0
     */
    public LinearSpace(Point[] normals) {
        this.normals = normals;
    }

    /**
     * The rows of Ax = 0.
     *
     * @return
     */
    public Point[] getNormals() {
        return normals;
    }

    /**
     * A small number used for thresholds
     */
    private static double epsilon = 1e-8;

    /**
     * Sets the epsilon for this set
     *
     * @param epsilon a small number used for thresholds
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * a linear space factory method to create a null space
     *
     * @param m the matrix whos null space we're creating
     * @return a new linear space that is the null space of the provided matrix.
     */
    public static LinearSpace nullSpace(Matrix m) {
        return new LinearSpace(m.rowsArray());
    }

    /**
     * TODO: is the null space generated from a sparse column space sparse? A
     * linear space factory method to create a column space
     *
     * @param basis the basis of the new linear space being created. Note, the
     * basis vectors are the columns of the matrix provided here.
     * @return the column space of the given matrix
     */
    public static LinearSpace colSpace(Matrix basis) {

        Matrix rcef = basis.T().reducedRowEchelon().T();

        boolean isDense = basis.isDense();
        int rows = basis.rows() - basis.cols(),
                cols = basis.rows();

        Matrix nullMatrix = isDense ? new MatrixDense(rows, cols) : new MatrixSparse(rows, cols);

        nullMatrix.setCols(i -> {
            if (i < basis.cols())
                return (isDense ? new PointD(rows) : new PointSparse(rows)).setAll(j -> rcef.get(j + basis.cols(), i));
            else
                return (isDense ? new PointD(rows) : new PointSparse(rows)).setInit(i - basis.cols(), -1);

        });
        return new LinearSpace(nullMatrix.rowsArray());
    }

    /**
     * The orthogonal complement of this linear space
     *
     * @return a new space
     */
    public LinearSpace OrhtogonalComplement() {
        return colSpace(matrix().T());
    }

    /**
     * Is this space equal to Rn
     *
     * @return
     */
    public boolean isAllSpace() {
        return normals.length == 0;
    }

    /**
     * The matrix used for the null space. Changing this matrix will change this
     * linear space.
     *
     * @return
     */
    protected Matrix matrix() {
        if (normals[0].isDense()) return MatrixDense.fromRows(normals);
        else return MatrixSparse.fromRows(normals);
    }

    /**
     * The matrix with Ax = 0.
     * @return 
     */
    public Matrix nullSpaceMatrix() {
        return Matrix.fromRows(normals);
    }

    /**
     * The matrix who's span is this linea space.
     * TODO: Can this be sparse, I don't think so. A new matrix whose column
     * space defines this linear space
     *
     * @return
     */
    public MatrixDense colSpaceMatrix() {
        return colSpaceMatric(matrix());
    }

    /**
     * Returns the column space matrix for the given null space matrix.
     *
     * @param nullSpaceMatrix
     * @return
     */
    public static MatrixDense colSpaceMatric(Matrix nullSpaceMatrix) {
        ReducedRowEchelonDense rre = new ReducedRowEchelonDense(nullSpaceMatrix);

        MatrixDense IMinus = MatrixDense.identity(Math.max(rre.numRows, rre.numCols)).minus(rre.square());

        if (rre.noFreeVariable()) return new PointD(rre.numRows);

        return MatrixDense.subMatrixFromCols(rre.freeVariables(), IMinus);
    }

    @Override
    public boolean hasElement(Point x) {
        return hasElement(x, epsilon);
    }

    @Override
    public boolean hasElement(Point x, double epsilon) {
        if (normals.length == 0) return true;
        return Arrays.stream(normals).allMatch(normal -> normal.dot(x) < epsilon);
    }

    /**
     * The function that projects onto this linear space.
     */
    public ProjectionFunction projFunc = null;

    /**
     * The function that projects onto this linear space.
     * @return 
     */
    public ProjectionFunction getProjFunc() {
        if (projFunc == null)
            projFunc = new ProjectionFunction(this, null, epsilon);

        return projFunc;
    }

    /**
     * An exception to be thrown if this linear space can't be projected onto with
     * the current method.
     */
    public class NoProjFuncExists extends RuntimeException {

        public NoProjFuncExists() {
            super("There is no projection function for this linear space.");
        }

    }

    @Override
    public Point proj(Point p) {
        if (isAllSpace()) return p;

        return getProjFunc().apply(p);

    }

    /**
     * Throws out the projection function.
     */
    public void clearProjFunc() {
        projFunc = null;
    }

    @Override
    public String toString() {
        return "null space of \n" + Matrix.fromRows(normals);
    }

    /**
     * The linear sum of two vector spaces = {a + b | a in this, b in ls}
     *
     * @param ls the other vector space
     * @return a new vector space
     */
    public LinearSpace lineaerSum(LinearSpace ls) {
        return LinearSpace.colSpace(colSpaceMatrix().rowConcat(ls.colSpaceMatrix()));
    }

    /**
     * Creates a linear space equal to Rn.
     * @param dim
     * @return 
     */
    public static LinearSpace allSpace(int dim) {
        return new LinearSpace(new Point[0]);
    }

    /**
     * The dimension of the space.
     *
     * @return
     */
    public long subSpaceDim() {
        return colSpaceMatrix().rank();

    }

    /**
     * The intersection of this space and another.
     *
     * @param ls
     * @return
     */
    public LinearSpace intersection(LinearSpace ls) {
        Point[] intersection = new Point[normals.length + ls.normals.length];
        System.arraycopy(normals, 0, intersection, 0, normals.length);
        System.arraycopy(ls.normals, 0, intersection, normals.length, ls.normals.length);
        return new LinearSpace(intersection);
    }

}
