package Matricies;

import tools.Pair;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.SingularOps_DDRM;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.interfaces.linsol.LinearSolverDense;

public class Matrix extends DMatrixRMaj {

    /**
     * Solves the system of equations Ax=b where this matrix is A
     * @param b the b in the aforementioned equation
     * @return the x in the aforementioned equation.
     */
    public Point solve(Point b) {
        DMatrixRMaj solve = new DMatrixRMaj(numCols, 1);
        CommonOps_DDRM.solve(this, b, solve);
        return new Point(solve.data);

    }

    /**
     * A stream of rows
     *
     * @return
     */
    public Stream<Point> rowStream() {
        return IntStream.range(0, numRows).mapToObj(i -> row(i));
    }

    /**
     * multiplies the matrix by a vector;
     *
     * @param p the vector
     * @return the new vector, a result of multiplying the matrix by a vector.
     */
    public Point mult(Point p) {
        Point mult = new Point(numRows);
        CommonOps_DDRM.mult(this, p, mult);
        return mult;
    }

    /**
     * Creates a stream of integer points nxm
     *
     * @param n
     * @param m
     * @return
     */
    public static Stream<Pair<Integer>> z2Stream(int n, int m) {
        return IntStream.range(0, n)
                .boxed().flatMap(i
                        -> IntStream.range(0, m)
                        .mapToObj(j -> new Pair<Integer>(i, j)));
    }

    /**
     * Multiplies to the two matrices. Launches a thread for each column in
     * matrix A.
     *
     * @param A
     * @return
     */
    public Matrix mult(Matrix A) {

        DMatrixRMaj mult = new DMatrixRMaj(numRows, A.cols());
        CommonOps_DDRM.mult(this, A, mult);
        return new Matrix(mult);
    }

    /**
     * multiplies the matrix by a constant.
     *
     * @param k the constant
     * @return
     */
    public Matrix mult(double k) {
        return new Matrix(numRows, numCols).setAll(i -> data[i] * k);
    }

    /**
     *
     * @param rows num rows
     * @param cols num columns
     */
    public Matrix(int rows, int cols) {
        super(rows, cols);
    }

    /**
     * A square matrix.
     *
     * @param n
     */
    public Matrix(int n) {
        this(n, n);
    }

    /**
     * This matrix minus another matrix
     * @param m
     * @return 
     */
    public Matrix minus(Matrix m) {
        Matrix mDense = m;
        Matrix minus = new Matrix(numRows, numCols);

        CommonOps_DDRM.subtract(this, mDense, minus);

        return minus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++)
                sb.append(get(i, j)).append(" ");
            sb.append("\n");
        }

        return sb.toString().replaceAll(".0 ", " ");
    }

    /**
     *
     * @param n
     * @return the nth column
     */
    public Point col(int n) {
        return new Point(numRows, i -> get(i, n));
    }

    /**
     * 
     * @param n
     * @return the nth row
     */
    public Point row(int n) {
        return new Point(numCols, i -> get(n, i));
    }

    /**
     * Sets the column at index n to v.
     * @param n
     * @param v
     * @return 
     */
    private Matrix setCol(int n, Point v) {
        IntStream.range(0, numRows).forEach(i -> set(i, n, v.get(i)));
        return this;
    }

    /**
     * Sets the nth row to v
     *
     * @param n
     * @param v
     * @return this
     */
    private Matrix setRow(int n, Point v) {
        return setRow(n, v.data);
    }

    /**
     * Sets the ith row to x
     * @param i
     * @param x
     * @return 
     */
    private Matrix setRow(int i, double[] x) {
        System.arraycopy(x, 0, data, numCols * i, x.length);
        return this;
    }

    /**
     * M^T, the transpose of this matrix
     *
     * @return a new matrix, the transpose of this matrix
     */
    public Matrix T() {
        return new Matrix(numCols, numRows, (i, j) -> get(j, i));
    }

    /**
     * Sets every element in this matrix as a function of the index in the
     * underlying data array.
     * @param f
     * @return 
     */
    protected Matrix setAll(IntToDoubleFunction f) {
        Arrays.setAll(data, i -> f.applyAsDouble(i));
        return this;
    }

    /**
     * maps this matrix to another by mapping each element to one in the same
     * position in the new matrix.
     * @param f a function f : R to R
     * @return 
     */
    protected Matrix map(DoubleFunction<Double> f) {
        return new Matrix(numRows, numCols, (i, j) -> f.apply(get(i, j)));
    }

    /**
     * A function from Z^2 to R
     */
    public interface Z2ToR extends BiFunction<Integer, Integer, Double> {

        public default Double apply(Pair<Integer> pair) {
            return apply(pair.l, pair.r);
        }

    }

    /**
     * sets all the elements of the matrix, in parallel, to f(i, j)
     *
     * @param rows the number of rows in the matrix
     * @param cols the number of columns in the matrix
     * @param f a unction of the row and column
     */
    public Matrix(int rows, int cols, Z2ToR f) {
        this(rows, cols);
        Arrays.parallelSetAll(data, a -> f.apply(rowIndex(a), colIndex(a)));
    }

    /**
     * Constructor
     *
     * @param array a single array representation of a 2d concept. The first col
     * elements go in the first row, the second col elements go in the second
     * row, and so on. Make sure that rows*cols = array.length;
     * @param rows the number of rows in the matrix.
     * @param cols the number of cols in the matrix.
     */
    public Matrix(double[] array, int rows, int cols) {
        super(rows, cols);
        this.data = array;
    }
    
    /**
     * A copy constructor
     * @param m 
     */
    public Matrix(DMatrixRMaj m) {
        this(m.data, m.numRows, m.numCols);
    }
    
    /**
     * the identity matrix
     *
     * @param n an nxn identity matrix
     * @return
     */
    public static Matrix identity(int n) {
        Matrix id = new Matrix(n);
        for (int i = 0; i < n; i++) id.set(i, i, 1);
        return id;
    }
    
    /**
     * creates a new Matrix from a set of rows
     *
     * @param rows
     * @return
     */
    public static Matrix fromRows(Point[] rows) {
        if (rows.length == 0)
            throw new RuntimeException("You're tyring to create a matrix from an empty array.");
        return new Matrix(rows.length, rows[0].dim(), (i, j) -> rows[i].get(j));
    }

    /**
     * Creates a matrix from columns generated by a mapping from the index
     * to the column value.
     * @param numCols the total number of columns
     * @param setCol the mapping.
     * @return 
     */
    public static Matrix fromCols(int numCols, IntFunction<Point> setCol) {
        Point firstCol = setCol.apply(0);
        int colLength = firstCol.dim();
        Matrix fromCols = new Matrix(colLength, numCols);
        fromCols.setCol(0, firstCol);
        IntStream.range(1, numCols).forEach(i -> fromCols.setCol(i, setCol.apply(i)));
        return fromCols;
    }

    /**
     * is this matrix equal to zero.
     *
     * @param epsilon
     * @return
     */
    public boolean isZero(double epsilon) {
        for (int i = 0; i < data.length; i++)
            if (data[i] < -epsilon || data[i] > epsilon) return false;
        return true;
    }

    /**
     * The rank of this matrix.
     * @return 
     */
    public long rank() {
        if (numRows == 0 || numCols == 0) return 0;
        return SingularOps_DDRM.rank(this);
    }

    /**
     * Creates a new matrix whose rows are appended to this one
     *
     * @param rows the rows to be added
     * @return a new matrix with the rows from both of the previous matrices
     */
    public Matrix rowConcat(Matrix rows) {
        
        double[] rowConcatArray = Arrays.copyOf(data, data.length
                + rows.cols() * rows.rows());

        System.arraycopy(rows.data, 0, rowConcatArray, data.length, rows.data.length);

        return new Matrix(rowConcatArray, this.numRows + rows.numRows, numCols);
    }

    /**
     * The reduced row echelon form of this matrix.
     * @return 
     */
    public ReducedRowEchelon reducedRowEchelon() {
        return new ReducedRowEchelon(this);
    }

    /**
     * The number of rows in this matrix.
     * @return 
     */
    public int rows() {
        return numRows;
    }

    /**
     * The number of columns in this matrix.
     * @return 
     */
    public int cols() {
        return numCols;
    }

    public boolean equals(Matrix obj) {
        return Arrays.equals(obj.data, data);
    }

    /**
     * An array of the rows of this matrix.
     * @return 
     */
    public Point[] rowsArray() {
        return rowStream().toArray(Point[]::new);
    }

    /**
     * Creates a square matrix that contains all the same elements of this matrix
     * with the same indices, and 0's in all the other locations.
     * @return 
     */
    public Matrix square() {
        int size = Math.max(numRows, numCols);
        return new Matrix(size, size, (i, j) -> i < numRows && j < numCols ? get(i, j) : 0);
    }

    /**
     * The psuedo inverse of this matrix.
     * @return 
     */
    public Matrix pseudoInverse() {
        LinearSolverDense<DMatrixRMaj> pseudoInverseFinder = LinearSolverFactory_DDRM.pseudoInverse(false);
        pseudoInverseFinder.setA(this);
        DMatrixRMaj pseudoInv = new DMatrixRMaj(numCols, numRows);
        pseudoInverseFinder.invert(pseudoInv);
        return new Matrix(pseudoInv);
    }

    /**
     * Builds a sub matrix of the proffered matrix from the selected columns.
     * @param cols the columns to be included in the submatrix.
     * @return 
     */
    public Matrix subMatrixFromCols(Set<Integer> cols) {

        Matrix sub = new Matrix(numRows, cols.size() + 1);
        Iterator<Integer> fromCol = cols.iterator();

        int toJ = 0;
        Integer fromJ;

        while (fromCol.hasNext()) {
            toJ++;
            fromJ = fromCol.next();

            for (int i = 0; i < sub.numRows; i++)
                sub.set(i, toJ, get(i, fromJ));

        }

        return sub;
    }

    /**
     * maps an index in the underlying 1d array, data, to a row in this matrix.
     *
     * @param k the index in the underlying 1-d array
     * @return the index of the matching row.
     */
    private int rowIndex(int k) {
        return k / cols();
    }

    /**
     * maps an index in the underlying 1d array, data, to a column in this matrix.
     *
     * @param k the index in the underlying 1-d array
     * @return the index of the matching column.
     */
    private int colIndex(int k) {
        return k % cols();
    }
}
