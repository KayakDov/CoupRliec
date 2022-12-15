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

    public RnLinearSpace(LinearSpace<Point> ls) {
        super(ls.normals());
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
            else return Point.nonZeroIndexAt(rows, i - basis.cols(), -1);

        };
        Matrix nullMatrix = Matrix.fromCols(cols, setCol);
        
        return new RnLinearSpace(nullMatrix.rowsArray());
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

        return IMinus.subMatrixFromCols(rre.freeVariables());
    }

}
