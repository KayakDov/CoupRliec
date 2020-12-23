/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Matricies;

import RnSpace.points.Point;
import java.util.List;
import java.util.stream.Stream;
import listTools.Pair1T;

/**
 *
 * @author Kayak
 */
public interface Matrix {

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
    Point col(int n);

    /**
     * A new matrix that is concatenation of the columns in this matrix and the
     * new columns
     *
     * @param cols the columns to be concatenated
     * @return a new matrix, the concatenation of this one and the new columns.
     */
    Matrix colConcat(Matrix cols);

    List<Point> colList();

    /**
     * A stream of the columns of this matrix.
     *
     * @return
     */
    Stream<Point> colStream();

    /**
     * A stream of columns
     *
     * @return
     */
    Stream<Point> cols();

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
     * Swaps row i with the row that has the highest max value in column i, that
     * is after row i.
     *
     * @param row the row to be swapped
     * @param col the column on which to search for a maximum absolute value
     */
    //    public void pivotRow(int row, int col) {
    //        RnToRm abs = p -> new Point(p).map(x -> Math.abs(x));
    //
    //        Point subArray = (new Point(rows - row).setFromSubArray(abs.of(col(col)), row));
    //
    //        int swapTo = row + subArray.argMax();
    //
    //        if (swapTo == row) return;
    //
    //        swapRows(row, swapTo);
    //    }
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
    Point mult(Point p);

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

    Point row(int n);

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
    Matrix rowConcat(Point row);

    /**
     * a list of the rows of this array
     *
     * @return
     */
    List<Point> rowList();

    /**
     * A stream of the rows of this matrix.
     *
     * @return
     */
    Stream<Point> rowStream();

    /**
     * A stream of rows
     *
     * @return
     */
    Stream<Point> rows();

    /**
     * sets A_i,j = d
     *
     * @param i the row of the new value
     * @param j the column for the new value
     * @param d the value to be placed at (i, j)
     * @return this
     */
    Matrix set(int i, int j, double d);

    /**
     * sets all the elements of the matrix, in parallel, to f(i, j)
     *
     * @param f a unction of the row and column
     * @return
     */
    Matrix setAll(Z2ToR f);

    Point solve(Point b);
    
}
