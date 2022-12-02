package Matricies;

import tools.Pair1T;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.Predicate;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparseTriplet;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.SingularOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.interfaces.linsol.LinearSolverDense;

public class Matrix extends DMatrixRMaj {

    protected double tolerance = 1e-9;

    public void setEpsilon(double epsilon) {
        this.tolerance = epsilon;
    }

    /**
     * copy the 2d vector into this matrix.
     *
     * @param matrix
     */
    public Matrix(double[][] matrix) {
        this(matrix.length, matrix[0].length, (i, j) -> matrix[i][j]);
    }

    /**
     * Is this a square matrix
     *
     * @return
     */
    
    public boolean isSquare() {
        return numRows == numCols;
    }

    /**
     * Copy constructor
     *
     * @param m
     */
    public Matrix(Matrix m) {
        this(m.numRows, m.numCols);
        System.arraycopy(m.data, 0, data, 0, data.length);
    }

    
    public Point solve(Point b) {
        DMatrixRMaj solve = new DMatrixRMaj(numCols, 1);
        CommonOps_DDRM.solve(this, b.asDense(), solve);
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
     * A stream of columns
     *
     * @return
     */
    
    public Stream<Point> colStream() {
        return IntStream.range(0, numCols).mapToObj(i -> col(i));
    }

    /**
     *
     * @param i row
     * @param j column
     * @return the value at M_i,j
     */
    @Override
    public double get(int i, int j) {
        return data[numCols * i + j];
    }

    public double get(int i) {
        return data[i];
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
     * @param n
     * @param m
     * @return 
     */
    public static Stream<Pair1T<Integer>> z2Stream(int n, int m){
        return IntStream.range(0, n)
                .boxed().flatMap( i ->
                        IntStream.range(0, m)
                                .mapToObj(j -> new Pair1T<Integer>(i, j)));
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
        CommonOps_DDRM.mult(this, A.asDense(), mult);
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
     * The determinant
     *
     * @return
     */
    
    public double det() {
        if (numCols == 1) return data[0];
        return CommonOps_DDRM.det(this);
    }

    /**
     * The sum of two matrices.
     *
     * @param m
     * @return
     */
    
    public Matrix plus(Matrix m) {
        Matrix mDense = m.asDense();
        Matrix plus = new Matrix(numRows, numCols);
        CommonOps_DDRM.add(this, mDense, plus);
        return plus;

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

    public Matrix minus(Matrix m) {
        Matrix mDense = m.asDense();
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

    
    public Point row(int n) {
        return new Point(numCols, i -> get(n, i));
    }

    /**
     * sets the nth column to v
     *
     * @param n
     * @param v
     * @return this
     */
    private Matrix setCol(int n, double[] v) {
        return setCol(n, new Point(v));
    }

    private Matrix setCol(int n, Point v) {
        IntStream.range(0, numRows).forEach(i -> set(i, n, v.get(i)));
        return this;
    }

    /**
     * Sets the nth column to v
     *
     * @param n
     * @param v
     * @return this
     */
    private Matrix setRow(int n, Point v) {
        return setRow(n, v.data);
    }


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
     * sets all the elements of the matrix to a scalar.
     *
     * @param x the value to set the elements of the matrix equal to.
     * @return this matrix.
     */
    public Matrix setAll(double x) {
        return setAll(i -> x);
    }

    protected Matrix setAll(IntToDoubleFunction f) {
        Arrays.setAll(data, i -> f.applyAsDouble(i));
        return this;
    }

    protected Matrix mapToDense(DoubleFunction<Double> f) {
        return new Matrix(numRows, numCols, (i, j) -> f.apply(get(i, j)));
    }
    public interface Z2ToR extends BiFunction<Integer, Integer, Double> {

        
        public default Double apply(Pair1T<Integer> pair) {
            return apply(pair.l, pair.r);
        }
        
    }
    public interface Z2Predicate extends BiPredicate<Integer, Integer>{
        public default boolean test(Pair1T<Integer> pair){
            return test(pair.l, pair.r);
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
     * returns a new row identical to this one, but with two rows swapped
     *
     * @param rowA the first row to swap
     * @param rowB the second row to swap
     * @return a new matrix with two rows swapped
     */
    public Matrix swapRows(int rowA, int rowB) {
        Point temp = row(rowA);
        setRow(rowA, row(rowB));
        setRow(rowB, temp);
        return this;
    }

    public Matrix(DMatrixRMaj m) {
        this(m.data, m.numRows, m.numCols);
    }

    /**
     * the PQ decomposition of this matrix. This may also be called QR?
     *
     * @return
     */
    
    public Pair1T<Matrix> QRDecomposition() {
        org.ejml.interfaces.decomposition.QRDecomposition<DMatrixRMaj> qrd
                = DecompositionFactory_DDRM.qr();

        qrd.decompose(this);

        return new Pair1T<>(new Matrix(qrd.getQ(null, true)), new Matrix(qrd.getR(null, true)));
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
     * Returns a matrix with rows set by the passed function.
     *
     * @param numRows
     * @param setRow;
     * @return a matrix created from rows.
     */
    public static Matrix fromRows(int numRows, IntFunction<Point> setRow) {
        Point firstRow = setRow.apply(0);
        int rowLength = firstRow.dim();
        Matrix fromRows = new Matrix(numRows, rowLength);
        fromRows.setRow(0, firstRow);
        for (int i = 1; i < numRows; i++)
            fromRows.setRow(i, setRow.apply(i));
        return fromRows;
    }

    public static Matrix fromCols(int numCols, IntFunction<Point> setCol) {
        Point firstCol = setCol.apply(0).asDense();
        int colLength = firstCol.dim();
        Matrix fromCols = new Matrix(colLength, numCols);
        fromCols.setCol(0, firstCol);
        IntStream.range(1, numCols).forEach(i -> fromCols.setCol(i, setCol.apply(i).asDense()));
        return fromCols;
    }

    /**
     * creates a new Matrix from a set of cols
     *
     * @param cols an array of columns
     * @return
     */
    public static Matrix fromCols(Point[] cols) {
        return new Matrix(cols[0].dim(), cols.length, (i, j) -> cols[j].get(i));
    }

    public Point[] rowArray() {
        Point[] points = new Point[numRows];
        Arrays.parallelSetAll(points, i -> row(i));
        return points;
    }

    /**
     * a list of the rows of this array
     *
     * @return
     */
    
    public List<Point> rowList() {
        return Arrays.asList(rowArray());
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

        Matrix rowsDense = rows.asDense();
        double[] rowConcatArray = Arrays.copyOf(data, data.length
                + rows.cols() * rows.rows());

        System.arraycopy(rowsDense.data, 0, rowConcatArray, data.length, rowsDense.data.length);

        return new Matrix(rowConcatArray, this.numRows + rowsDense.numRows, numCols);
    }

    /**
     * Creates a new matrix whose rows are appended to this one
     *
     * @param row
     * @return a new matrix with the rows from both of the previous matrices
     */
    
    public Matrix rowConcat(Point row) {
        return rowConcat(row.T());
    }

    /**
     * A new matrix that is concatenation of the columns in this matrix and the
     * new columns
     *
     * @param cols the columns to be concatenated
     * @return a new matrix, the concatenation of this one and the new columns.
     */
    
    public Matrix colConcat(Matrix cols) {
        Matrix colsDense = cols.asDense();
        return Matrix.fromCols(cols.cols() + cols(), i -> i < this.numCols
                ? col(i)
                : colsDense.col(i - this.numCols));
    }

    
    public ReducedRowEchelon reducedRowEchelon() {
        return new ReducedRowEchelon(this);
    }

    /**
     * A matrix where each column is a random point.
     *
     * @param numPoints the number of columns
     * @param center the center around which the uniformly random points are
     * created
     * @param r the radius of the points
     * @return a new matrix
     */
    public static Matrix randomColPoints(int numPoints, Point center, double r) {

        return Matrix.fromCols(numPoints, i -> Point.uniformBoundedRand(center, r));
    }

    /**
     * A matrix where each row is a random point.
     *
     * @param numPoints the number of columns
     * @param center the center around which the uniformly random points are
     * created
     * @param r the radius of the points
     * @return a new matrix
     */
    public static Matrix randomRowPoints(int numPoints, Point center, double r) {
        return Matrix.fromRows(numPoints, i -> Point.uniformBoundedRand(center, r));
    }

    public boolean hasFullRank() {
        return rank() == numRows;
    }

    
    public int rows() {
        return numRows;
    }

    
    public int cols() {
        return numCols;
    }

    
    public Matrix asDense() {
        return this;
    }

    

    
    public boolean equals(Matrix obj) {
        return Arrays.equals(obj.asDense().data, data);
    }

    
    public int numNonZeroes() {
        return (int) Matrix.z2Stream(numRows, numCols).filter(p -> get(p.l, p.r)
                != 0).count();

    }

    
    public Point[] rowsArray() {
        return rowStream().toArray(Point[]::new);
    }

    
    public Matrix sameType() {
        return new Matrix(numRows, numCols);
    }

    
    public Matrix square() {
        int size = Math.max(numRows, numCols);
        return new Matrix(size, size, (i, j) -> i<numRows && j < numCols? get(i, j): 0);
    }

    
    public Matrix pseudoInverse() {
        LinearSolverDense<DMatrixRMaj> pseudoInverseFinder = LinearSolverFactory_DDRM.pseudoInverse(false);
        pseudoInverseFinder.setA(this);
        DMatrixRMaj pseudoInv = new DMatrixRMaj(numCols, numRows);
        pseudoInverseFinder.invert(pseudoInv);
        return new Matrix(pseudoInv);
    }

    public static Matrix subMatrixFromCols(Set<Integer> cols, Matrix greater) {

        Matrix sub = new Matrix(greater.numRows, cols.size() + 1);
        Iterator<Integer> fromCol = cols.iterator();

        int toJ = 0;
        Integer fromJ;

        while (fromCol.hasNext()) {
            toJ++;
            fromJ = fromCol.next();

            for (int i = 0; i < sub.numRows; i++)
                sub.set(i, toJ, greater.get(i, fromJ));

        }

        return sub;
    }
    
    
    /**
     * gets the row index for an index in the underlying 1-d array.
     *
     * @param k the index in the underlying 1-d array
     * @return the index of the matching row.
     */
    public int rowIndex(int k) {
        return k / cols();
    }
    
    
    /**
     * gets the column index for an index in the underlying 1-d array.
     *
     * @param k the index in the underlying 1-d array
     * @return the index of the matching column.
     */
    public int colIndex(int k) {
        return k % cols();
    }
    
    /**
     * Is the given index in this matrix?
     * @param row 
     * @param col 
     * @return 
     */
    public boolean has(int row, int col){
        return row >= 0 && col >= 0 && row <= rows() && col <= rows();
    }
}
