package Matricies;

import tools.Pair1T;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
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

public class MatrixDense extends DMatrixRMaj implements Matrix {

    protected double tolerance = 1e-9;

    public void setEpsilon(double epsilon) {
        this.tolerance = epsilon;
    }

    /**
     * copy the 2d vector into this matrix.
     *
     * @param matrix
     */
    public MatrixDense(double[][] matrix) {
        this(matrix.length, matrix[0].length, (i, j) -> matrix[i][j]);
    }

    /**
     * Is this a square matrix
     *
     * @return
     */
    @Override
    public boolean isSquare() {
        return numRows == numCols;
    }

    /**
     * Copy constructor
     *
     * @param m
     */
    public MatrixDense(MatrixDense m) {
        this(m.numRows, m.numCols);
        System.arraycopy(m.data, 0, data, 0, data.length);
    }

    public MatrixDense(MatrixSparse sm) {
        this(sm.rows(), sm.cols());
        sm.nonZeroes().forEach(coord -> set(coord.row, coord.col, coord.value));
    }

    @Override
    public PointD solve(Point b) {
        DMatrixRMaj solve = new DMatrixRMaj(numCols, 1);
        CommonOps_DDRM.solve(this, b.asDense(), solve);
        return new PointD(solve.data);

    }

    /**
     * A stream of rows
     *
     * @return
     */
    @Override
    public Stream<PointD> rowStream() {
        return IntStream.range(0, numRows).mapToObj(i -> row(i));
    }

    /**
     * A stream of columns
     *
     * @return
     */
    @Override
    public Stream<PointD> colStream() {
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
    @Override
    public Point mult(Point p) {
        if (p.isDense()) return mult(p.asDense());
        else return mult(p.asSparse());
    }

    public PointD mult(PointD p) {
        PointD mult = new PointD(numRows);
        CommonOps_DDRM.mult(this, p, mult);
        return mult;
    }

    public PointSparse mult(PointSparse p) {
        throw new UnsupportedOperationException("Dov, you still have to write this funtion.");
    }

    /**
     * Multiplies to the two matrices. Launches a thread for each column in
     * matrix A.
     *
     * @param A
     * @return
     */
    @Override
    public MatrixDense mult(Matrix A) {

        DMatrixRMaj mult = new DMatrixRMaj(numRows, A.cols());
        CommonOps_DDRM.mult(this, A.asDense(), mult);
        return new MatrixDense(mult);
    }

    /**
     * multiplies the matrix by a constant.
     *
     * @param k the constant
     * @return
     */
    @Override
    public MatrixDense mult(double k) {
        return new MatrixDense(numRows, numCols).setAll(i -> data[i] * k);
    }

    /**
     * The determinant
     *
     * @return
     */
    @Override
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
    @Override
    public MatrixDense plus(Matrix m) {
        MatrixDense mDense = m.asDense();
        MatrixDense plus = new MatrixDense(numRows, numCols);
        CommonOps_DDRM.add(this, mDense, plus);
        return plus;

    }

    /**
     *
     * @param rows num rows
     * @param cols num columns
     */
    public MatrixDense(int rows, int cols) {
        super(rows, cols);
    }

    /**
     * A square matrix.
     *
     * @param n
     */
    public MatrixDense(int n) {
        this(n, n);
    }

    public MatrixDense minus(Matrix m) {
        MatrixDense mDense = m.asDense();
        MatrixDense minus = new MatrixDense(numRows, numCols);

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
    @Override
    public PointD col(int n) {
        return new PointD(numRows, i -> get(i, n));
    }

    @Override
    public PointD row(int n) {
        return new PointD(numCols, i -> get(n, i));
    }

    /**
     * sets the nth column to v
     *
     * @param n
     * @param v
     * @return this
     */
    private Matrix setCol(int n, double[] v) {
        return setCol(n, new PointD(v));
    }

    private Matrix setCol(int n, PointD v) {
        IntStream.range(0, numRows).forEach(i -> set(i, n, v.get(i)));
        return this;
    }

    private Matrix setCol(int n, PointSparse v) {
        v.nonZeroes().forEach(coord -> set(coord.row, n, coord.value));
        return this;
    }

    /**
     * Sets the nth column to v
     *
     * @param n
     * @param v
     * @return this
     */
    private MatrixDense setRow(int n, PointD v) {
        return setRow(n, v.data);
    }

    private MatrixDense setRow(int n, PointSparse v) {
        v.nonZeroes().forEach(coord -> set(n, coord.row, coord.value));
        return this;
    }

    private MatrixDense setRow(int i, double[] x) {
        System.arraycopy(x, 0, data, numCols * i, x.length);
        return this;
    }

    /**
     * M^T, the transpose of this matrix
     *
     * @return a new matrix, the transpose of this matrix
     */
    @Override
    public MatrixDense T() {
        return new MatrixDense(numCols, numRows, (i, j) -> get(j, i));
    }

    /**
     * sets all the elements of the matrix to a scalar.
     *
     * @param x the value to set the elements of the matrix equal to.
     * @return this matrix.
     */
    public MatrixDense setAll(double x) {
        return setAll(i -> x);
    }

    protected MatrixDense setAll(IntToDoubleFunction f) {
        Arrays.setAll(data, i -> f.applyAsDouble(i));
        return this;
    }

    protected Matrix mapToDense(DoubleFunction<Double> f) {
        return new MatrixDense(numRows, numCols, (i, j) -> f.apply(get(i, j)));
    }

    /**
     * sets all the elements of the matrix, in parallel, to f(i, j)
     *
     * @param rows the number of rows in the matrix
     * @param cols the number of columns in the matrix
     * @param f a unction of the row and column
     * @return
     */
    public MatrixDense(int rows, int cols, Z2ToR f) {
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
    public MatrixDense(double[] array, int rows, int cols) {
        super(rows, cols);
        this.data = array;
    }

    /**
     * the identity matrix
     *
     * @param n an nxn identity matrix
     * @return
     */
    public static MatrixDense identity(int n) {
        MatrixDense id = new MatrixDense(n);
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
        PointD temp = row(rowA);
        setRow(rowA, row(rowB));
        setRow(rowB, temp);
        return this;
    }

    public MatrixDense(DMatrixRMaj m) {
        this(m.data, m.numRows, m.numCols);
    }

    /**
     * the PQ decomposition of this matrix. This may also be called QR?
     *
     * @return
     */
    @Override
    public Pair1T<Matrix> QRDecomposition() {
        org.ejml.interfaces.decomposition.QRDecomposition<DMatrixRMaj> qrd
                = DecompositionFactory_DDRM.qr();

        qrd.decompose(this);

        return new Pair1T<>(new MatrixDense(qrd.getQ(null, true)), new MatrixDense(qrd.getR(null, true)));
    }

    /**
     * creates a new Matrix from a set of rows
     *
     * @param rows
     * @return
     */
    public static MatrixDense fromRows(Point[] rows) {
        if (rows.length == 0)
            throw new RuntimeException("You're tyring to create a matrix from an empty array.");
        if (rows[0].isDense())
            return new MatrixDense(rows.length, rows[0].dim(), (i, j) -> rows[i].get(j));
        else {
            MatrixDense m = new MatrixDense(rows.length, rows[0].dim());
            IntStream.range(0, m.numRows).forEach(i -> m.setRow(i, rows[i].asSparse()));
            return m;
        }
    }

    /**
     * Returns a matrix with rows set by the passed function.
     *
     * @param numRows
     * @param setRow;
     * @return a matrix created from rows.
     */
    public static MatrixDense fromRows(int numRows, IntFunction<PointD> setRow) {
        PointD firstRow = setRow.apply(0);
        int rowLength = firstRow.dim();
        MatrixDense fromRows = new MatrixDense(numRows, rowLength);
        fromRows.setRow(0, firstRow);
        for (int i = 1; i < numRows; i++)
            fromRows.setRow(i, setRow.apply(i));
        return fromRows;
    }

    public static MatrixDense fromCols(int numCols, IntFunction<Point> setCol) {
        PointD firstCol = setCol.apply(0).asDense();
        int colLength = firstCol.dim();
        MatrixDense fromCols = new MatrixDense(colLength, numCols);
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
    public static MatrixDense fromCols(Point[] cols) {
        return new MatrixDense(cols[0].dim(), cols.length, (i, j) -> cols[j].get(i));
    }

    public PointD[] rowArray() {
        PointD[] points = new PointD[numRows];
        Arrays.parallelSetAll(points, i -> row(i));
        return points;
    }

    /**
     * a list of the rows of this array
     *
     * @return
     */
    @Override
    public List<PointD> rowList() {
        return Arrays.asList(rowArray());
    }

    /**
     * is this matrix equal to zero.
     *
     * @param epsilon
     * @return
     */
    @Override
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
    @Override
    public MatrixDense rowConcat(Matrix rows) {

        MatrixDense rowsDense = rows.asDense();
        double[] rowConcatArray = Arrays.copyOf(data, data.length
                + rows.cols() * rows.rows());

        System.arraycopy(rowsDense.data, 0, rowConcatArray, data.length, rowsDense.data.length);

        return new MatrixDense(rowConcatArray, this.numRows + rowsDense.numRows, numCols);
    }

    /**
     * Creates a new matrix whose rows are appended to this one
     *
     * @param row
     * @return a new matrix with the rows from both of the previous matrices
     */
    @Override
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
    @Override
    public MatrixDense colConcat(Matrix cols) {
        MatrixDense colsDense = cols.asDense();
        return MatrixDense.fromCols(cols.cols() + cols(), i -> i < this.numCols
                ? col(i)
                : colsDense.col(i - this.numCols));
    }

    @Override
    public ReducedRowEchelonDense reducedRowEchelon() {
        return new ReducedRowEchelonDense(this);
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
    public static MatrixDense randomColPoints(int numPoints, PointD center, double r) {

        return MatrixDense.fromCols(numPoints, i -> PointD.uniformBoundedRand(center, r));
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
    public static MatrixDense randomRowPoints(int numPoints, PointD center, double r) {
        return MatrixDense.fromRows(numPoints, i -> PointD.uniformBoundedRand(center, r));
    }

    public boolean hasFullRank() {
        return rank() == numRows;
    }

    @Override
    public int rows() {
        return numRows;
    }

    @Override
    public int cols() {
        return numCols;
    }

    @Override
    public MatrixDense asDense() {
        return this;
    }

    @Override
    public MatrixSparse asSparse() {
        long size = Arrays.stream(data).filter(x -> x != 0).count();
        DMatrixSparseTriplet dmst = new DMatrixSparseTriplet(numRows, numCols, (int) size);
        IntStream.range(0, numCols).forEach(col -> IntStream.range(0, numRows).forEach(row -> dmst.set(row, col, get(row, col))));
        return new MatrixSparse(dmst);
    }

    @Override
    public boolean isSparse() {
        return false;
    }

    @Override
    public boolean equals(Matrix obj) {
        if (obj.isSparse())
            return obj.asSparse().nonZeroes()
                    .allMatch(coord -> Math.abs(coord.value - get(coord.row))
                    < tolerance);
        return Arrays.equals(obj.asDense().data, data);
    }

    @Override
    public int numNonZeroes() {
        return (int) Matrix.z2Stream(numRows, numCols).filter(p -> get(p.l, p.r)
                != 0).count();

    }

    @Override
    public PointD[] rowsArray() {
        return rowStream().toArray(PointD[]::new);
    }

    @Override
    public MatrixDense sameType() {
        return new MatrixDense(numRows, numCols);
    }

    @Override
    public MatrixDense square() {
        int size = Math.max(numRows, numCols);
        return new MatrixDense(size, size, (i, j) -> i<numRows && j < numCols? get(i, j): 0);
    }

    @Override
    public MatrixDense pseudoInverse() {
        LinearSolverDense<DMatrixRMaj> pseudoInverseFinder = LinearSolverFactory_DDRM.pseudoInverse(false);
        pseudoInverseFinder.setA(this);
        DMatrixRMaj pseudoInv = new DMatrixRMaj(numCols, numRows);
        pseudoInverseFinder.invert(pseudoInv);
        return new MatrixDense(pseudoInv);
    }

    public static MatrixDense subMatrixFromCols(Set<Integer> cols, MatrixDense greater) {

        MatrixDense sub = new MatrixDense(greater.numRows, cols.size() + 1);
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
}
