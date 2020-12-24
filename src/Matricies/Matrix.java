/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Matricies;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import listTools.Pair1T;
import org.ejml.data.DMatrix;

/**
 *
 * @author Kayak
 */
public interface Matrix {

    public int rows();

    public int cols();

    public MatrixDense asDense();

    public MatrixSparse asSparse();

    /**
     * the PQ decomposition of this matrix. This may also be called QR?
     *
     * @return
     */
    Pair1T<Matrix> QRDecomposition();

    /**
     * M^T, the transpose of this matrix
     *
     * @return a new matrix, the transpose of this matrix
     */
    Matrix T();

    /**
     *
     * @param n
     * @return the nth column
     */
    PointDense col(int n);

    /**
     * A new matrix that is concatenation of the columns in this matrix and the
     * new columns
     *
     * @param cols the columns to be concatenated
     * @return a new matrix, the concatenation of this one and the new columns.
     */
    Matrix colConcat(Matrix cols);

    List<PointDense> colList();

    /**
     * A stream of the columns of this matrix.
     *
     * @return
     */
    Stream<PointDense> colStream();

    /**
     * The determinant
     *
     * @return
     */
    double det();

    /**
     *
     * @param i row
     * @param j column
     * @return the value at M_i,j
     */
    double get(int i, int j);

    Matrix inverse();

    /**
     * Is this a square matrix
     *
     * @return
     */
    boolean isSquare();

    /**
     * is this matrix equal to zero.
     *
     * @param epsilon
     * @return
     */
    boolean isZero(double epsilon);

    /**
     * multiplies the matrix by a vector;
     *
     * @param p the vector
     * @return the new vector, a result of multiplying the matrix by a vector.
     */
    PointDense mult(PointDense p);

    /**
     * Multiplies to the two matrices. Launches a thread for each column in
     * matrix A.
     *
     * @param A
     * @return
     */
    Matrix mult(Matrix A);

    /**
     * multiplies the matrix by a constant.
     *
     * @param k the constant
     * @return
     */
    Matrix mult(double k);

    /**
     * The sum of two matrices.
     *
     * @param m
     * @return
     */
    Matrix plus(Matrix m);

    ReducedRowEchelon reducedRowEchelon();

    PointDense row(int n);

    /**
     * Creates a new matrix whose rows are appended to this one
     *
     * @param rows the rows to be added
     * @return a new matrix with the rows from both of the previous matrices
     */
    Matrix rowConcat(Matrix rows);

    /**
     * Creates a new matrix whose rows are appended to this one
     *
     * @param row
     * @return a new matrix with the rows from both of the previous matrices
     */
    Matrix rowConcat(PointDense row);

    /**
     * a list of the rows of this array
     *
     * @return
     */
    List<PointDense> rowList();

    /**
     * A stream of the rows of this matrix.
     *
     * @return
     */
    Stream<PointDense> rowStream();

    /**
     * sets A_i,j = d
     *
     * @param i the row of the new value
     * @param j the column for the new value
     * @param d the value to be placed at (i, j)
     * @return this
     */
    Matrix set(int i, int j, double d);

    public interface Z2ToR extends BiFunction<Integer, Integer, Double> {}

    /**
     * sets all the elements of the matrix, in parallel, to f(i, j)
     *
     * @param f a unction of the row and column
     * @return
     */
    Matrix setAll(Z2ToR f);

    PointDense solve(PointDense b);

    /**
     * gets the row index for an index in the underlying 1-d array.
     *
     * @param k the index in the underlying 1-d array
     * @return the index of the matching row.
     */
    public default int rowIndex(int k) {
        return k / cols();
    }

    /**
     * gets the column index for an index in the underlying 1-d array.
     *
     * @param k the index in the underlying 1-d array
     * @return the index of the matching column.
     */
    public default int colIndex(int k) {
        return k % cols();
    }
    
    public DMatrix ejmlMatrix();
    public boolean isSparse();
    public default boolean isDense(){
        return !isSparse();
    }

}
