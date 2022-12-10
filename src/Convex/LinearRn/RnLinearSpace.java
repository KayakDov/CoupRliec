package Convex.LinearRn;

import Hilbert.LinearSpace;
import Matricies.Matrix;
import Matricies.ReducedRowEchelon;
import Matricies.Point;
import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

/**
 * An object that describes a linear/vector space
 *
 * @author Dov Neimand
 */
public class RnLinearSpace extends LinearSpace<Point> {

    /**
     * Has a projection function been found for this space.
     *
     * @return
     */
    public boolean hasProjFunction() {
        return projFunc != null;
    }

    public RnLinearSpace(LinearSpace<Point> ls) {
        super(ls.normals());
    }

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
    public boolean equals(RnLinearSpace ls) {
        return Arrays.equals(normals, ls.normals);
    }

    /**
     * Constructor
     *
     * @param normals the rows of Ax = 0
     */
    public RnLinearSpace(Point[] normals) {
        super(normals);
    }


    /**
     * a linear space factory method to create a null space
     *
     * @param m the matrix whos null space we're creating
     * @return a new linear space that is the null space of the provided matrix.
     */
    public static RnLinearSpace nullSpace(Matrix m) {
        return new RnLinearSpace(m.rowsArray());
    }

    /**
     * TODO: is the null space generated from a sparse column space sparse? A
     * linear space factory method to create a column space
     *
     * @param basis the basis of the new linear space being created. Note, the
     * basis vectors are the columns of the matrix provided here.
     * @return the column space of the given matrix
     */
    public static RnLinearSpace colSpace(Matrix basis) {

        Matrix rcef = basis.T().reducedRowEchelon().T();

        int rows = basis.rows() - basis.cols(),
                cols = basis.rows();

        IntFunction<Point> setCol = i -> {
            
            if (i < basis.cols()){
                IntToDoubleFunction setPoint = j -> rcef.get(j + basis.cols(), i);
                return new Point(rows, setPoint);
            }
            else return new Point(rows, i - basis.cols(), -1);

        };
        Matrix nullMatrix = Matrix.fromCols(cols, setCol);
        
        return new RnLinearSpace(nullMatrix.rowsArray());
    }

    /**
     * The orthogonal complement of this linear space
     *
     * @return a new space
     */
    public RnLinearSpace OrhtogonalComplement() {
        return colSpace(matrix().T());
    }

    /**
     * The matrix used for the null space. Changing this matrix will change this
     * linear space.
     *
     * @return
     */
    protected Matrix matrix() {
        return Matrix.fromRows(normals);
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
    public Matrix colSpaceMatrix() {
        return colSpaceMatrix(matrix());
    }

    /**
     * Returns the column space matrix for the given null space matrix.
     *
     * @param nullSpaceMatrix
     * @return
     */
    public static Matrix colSpaceMatrix(Matrix nullSpaceMatrix) {
        ReducedRowEchelon rre = new ReducedRowEchelon(nullSpaceMatrix);

        Matrix IMinus = Matrix.identity(Math.max(rre.numRows, rre.numCols)).minus(rre.square());

        if (rre.noFreeVariable()) return new Point(rre.numRows);

        return Matrix.subMatrixFromCols(rre.freeVariables(), IMinus);
    }

    @Override
    public boolean hasElement(Point x) {
        return hasElement(x, tolerance);
    }

    @Override
    public boolean hasElement(Point x, double tolerance) {
        if (normals.length == 0) return true;
        return Arrays.stream(normals).allMatch(normal -> normal.dot(x) < tolerance);
    }

    /**
     * The function that projects onto this linear space.
     */
    public ProjectPoint projFunc = null;

    /**
     * The function that projects onto this linear space.
     * @return 
     */
    public ProjectPoint getProjFunc() {
        if (projFunc == null)
            projFunc = new ProjectPoint(this, null, tolerance);

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


    /**
     * The linear sum of two vector spaces = {a + b | a in this, b in ls}
     *
     * @param ls the other vector space
     * @return a new vector space
     */
    public RnLinearSpace lineaerSum(RnLinearSpace ls) {
        return RnLinearSpace.colSpace(colSpaceMatrix().rowConcat(ls.colSpaceMatrix()));
    }

    /**
     * Creates a linear space equal to Rn.
     * @param dim
     * @return 
     */
    public static RnLinearSpace allSpace(int dim) {
        return new RnLinearSpace(new Point[0]);
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
    public RnLinearSpace intersection(RnLinearSpace ls) {
        Point[] intersection = new Point[normals.length + ls.normals.length];
        System.arraycopy(normals, 0, intersection, 0, normals.length);
        System.arraycopy(ls.normals, 0, intersection, normals.length, ls.normals.length);
        return new RnLinearSpace(intersection);
    }

}
