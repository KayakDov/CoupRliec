package Matricies;

import listTools.Pair1T;
import static java.lang.Math.*;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparseTriplet;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.SingularOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;

public class MatrixDense implements Matrix {

    protected double[] array;
    public final int rows, cols;

    protected double epsilon = 1e-9;

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
    
    public double[][] twoDArray() {
        double twoDArray[][] = new double[rows][cols];
        IntStream.range(0, rows).forEach(i -> Arrays.setAll(twoDArray[i], j -> get(i, j)));
        return twoDArray;
    }

    /**
     * copy the 2d vector into this matrix.
     *
     * @param matrix
     */
    public MatrixDense(double[][] matrix) {
        this(matrix.length, matrix[0].length);
        setAll((i, j) -> matrix[i][j]);
    }

    /**
     * Is this a square matrix
     *
     * @return
     */
    @Override
    public boolean isSquare() {
        return rows == cols;
    }

    /**
     * Copy constructor
     *
     * @param m
     */
    public MatrixDense(MatrixDense m) {
        this(m.rows, m.cols);
        System.arraycopy(m.array, 0, array, 0, array.length);
    }

    public MatrixDense(MatrixSparse sm) {
        this(sm.rows(), sm.cols());
        array = new double[sm.ejmlSparse.getNumElements()];
        Arrays.setAll(array, i -> sm.ejmlSparse.get(rowIndex(i), colIndex(i)));
    }

    @Override
    public PointDense solve(Point b) {
        DMatrixRMaj solve = new DMatrixRMaj(cols, 1);
        CommonOps_DDRM.solve(ejmlMatrix(), b.asDense().ejmlMatrix(), solve);
        return new PointDense(solve.data);

    }

    /**
     * A stream of rows
     *
     * @return
     */
    @Override
    public Stream<PointDense> rowStream() {
        return IntStream.range(0, rows).mapToObj(i -> row(i));
    }

    /**
     * A stream of columns
     *
     * @return
     */
    @Override
    public Stream<PointDense> colStream() {
        return IntStream.range(0, cols).mapToObj(i -> col(i));
    }

    /**
     *
     * @param i row
     * @param j column
     * @return the value at M_i,j
     */
    @Override
    public double get(int i, int j) {
        return array[cols * i + j];
    }

    public double get(int i) {
        return array[i];
    }

    /**
     * sets A_i,j = d
     *
     * @param i the row of the new value
     * @param j the column for the new value
     * @param d the value to be placed at (i, j)
     * @return this
     */
    @Override
    public Matrix set(int i, int j, double d) {
        array[cols * i + j] = d;
        return this;
    }

    public Matrix set(int i, double d) {
        array[i] = d;
        return this;
    }

    /**
     * multiplies the matrix by a vector;
     *
     * @param p the vector
     * @return the new vector, a result of multiplying the matrix by a vector.
     */
    @Override
    public PointDense mult(PointDense p) {
        return new PointDense(rows).setAll(i -> row(i).dot(p));
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
        if (A.isDense()) return mult(A.asDense());
        else return mult(A.asSparse());
    }

    public MatrixDense mult(MatrixDense A) {
        DMatrixRMaj mult = new DMatrixRMaj(rows, A.cols());
        CommonOps_DDRM.mult(ejmlMatrix(), A.ejmlMatrix(), mult);
        return new MatrixDense(mult);
    }

    public MatrixDense mult(MatrixSparse A) {
        return A.mult(this);
    }

    /**
     * multiplies the matrix by a constant.
     *
     * @param k the constant
     * @return
     */
    @Override
    public MatrixDense mult(double k) {
        return new MatrixDense(rows, cols).setAll(i -> array[i] * k);
    }

    public DMatrixRMaj ejmlMatrix() {
        return new DMatrixRMaj(rows, cols, true, array);
    }

    /**
     * The determinant
     *
     * @return
     */
    @Override
    public double det() {
        if (cols == 1) return array[0];
        return CommonOps_DDRM.det(ejmlMatrix());
    }

    /**
     * The minor matrix, that is, a replica of this matrix but without the nth
     * column and mth row.
     *
     * @param n the row to be removed
     * @param m the col to be removed
     * @return the minor matrix
     */
    public MatrixDense minor(int n, int m) {
        return new MatrixDense(n <= rows ? rows - 1 : rows, m <= cols ? cols - 1 : cols).
                setAll((i, j) -> get(i < n ? i : i + 1, j < m ? j : j + 1));
    }

    /**
     * returns a new matrix like this one but with a column removed
     *
     * @param col the column to be removed
     * @return a new matrix without the row
     */
    public Matrix removeCol(int col) {
        return minor(rows + 1, col);
    }

    /**
     * a new matrix without a row
     *
     * @param row the row to be removed
     * @return a new matrix without a column
     */
    public Matrix removeRow(int row) {
        return minor(row, cols + 1);
    }

    /**
     * The nth power of this matrix
     *
     * @param n the power
     * @return the new matrix to the nth power.
     */
    public Matrix pow(int n) {
        if (n == 0) return identityMatrix(rows);
        if (n % 2 == 0) return (mult(this)).pow(n / 2);
        return mult(pow(n - 1));
    }

    /**
     * The sum of two matrices.
     *
     * @param m
     * @return
     */
    @Override
    public MatrixDense plus(Matrix m) {
        return new MatrixDense(rows, cols).setAll(i -> array[i] + m.asDense().array[i]);
    }

    /**
     * adds the point to each row
     *
     * @param p
     * @return
     */
    public MatrixDense plus(PointDense p) {
        return new MatrixDense(rows, cols).setRows(i -> row(i).plus(p));
    }

    /**
     *
     * @param rows num rows
     * @param cols num columns
     */
    public MatrixDense(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        array = new double[rows * cols];
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
        return plus(m.mult(-1));
    }

    /**
     * subtracts the point from each row
     *
     * @param p
     * @return
     */
    public MatrixDense minus(PointDense p) {
        return new MatrixDense(rows, cols).setRows(i -> row(i).minus(p));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
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
    public PointDense col(int n) {
        return new PointDense(rows).setAll(i -> get(i, n));
    }

    @Override
    public PointDense row(int n) {
        return new PointDense(cols).setAll(i -> get(n, i));
    }

    /**
     * sets the nth column to v
     *
     * @param n
     * @param v
     * @return this
     */
    public Matrix setCol(int n, double[] v) {
        return setCol(n, new PointDense(v));
    }

    public Matrix setCol(int n, PointDense v) {
        IntStream.range(0, rows).forEach(i -> set(i, n, v.get(i)));
        return this;
    }

    /**
     * Sets the nth column to v
     *
     * @param n
     * @param v
     * @return this
     */
    public MatrixDense setRow(int n, PointDense v) {
        return setRow(n, v.array);
    }

    public MatrixDense setRow(int i, double[] x) {
        System.arraycopy(x, 0, array, cols * i, x.length);
        return this;
    }

    /**
     * M^T, the transpose of this matrix
     *
     * @return a new matrix, the transpose of this matrix
     */
    @Override
    public MatrixDense T() {
        return new MatrixDense(cols, rows).setAll((i, j) -> get(j, i));
    }

    @Override
    public MatrixDense inverse() {
        DMatrixRMaj inverse = new DMatrixRMaj(rows, cols);
        CommonOps_DDRM.invert(ejmlMatrix(), inverse);
        return new MatrixDense(inverse);
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

    /**
     * The sum of the elements on the diagonal.
     *
     * @return
     */
    public double trace() {
        return IntStream.range(0, min(cols, rows)).
                mapToDouble(i -> get(i, i)).sum();
    }

    /**
     * sets all the elements of the matrix, in parallel, to f(i, j)
     *
     * @param f a unction of the row and column
     * @return
     */
    @Override
    public MatrixDense setAll(Z2ToR f) {
        mySetAll(f);
        return this;
    }

    protected MatrixDense setAll(IntToDoubleFunction f) {
        Arrays.parallelSetAll(array, i -> f.applyAsDouble(i));
        return this;
    }

    protected Matrix mapToDense(DoubleFunction<Double> f) {
        return new MatrixDense(rows, cols).setAll((i, j) -> f.apply(get(i, j)));
    }

    /**
     * this is a version of set all that can't be overridden by children
     * classes.
     *
     * @param f
     */
    private final void mySetAll(Z2ToR f) {
        Arrays.parallelSetAll(array, a -> f.apply(rowIndex(a), colIndex(a)));
    }

    /**
     * Sets some of the elements of this matrix and leaves the others alone
     *
     * @param filter chooses which elements to be set
     * @param f the function that sets the element
     * @return this matrix
     */
    public MatrixDense setSome(BiFunction<Integer, Integer, Boolean> filter, Z2ToR f) {
        Z2ToR filteredF = (i, j) -> filter.apply(i, j) ? f.apply(i, j) : get(i, j);
        mySetAll(filteredF);
        return this;
    }

    public MatrixDense topLeftSubMatrix(int n) {
        if (n > Math.min(rows, cols))
            throw new ArithmeticException("Submatrix is bigger than parrent matrix.");
        return new MatrixDense(n).setAll((i, j) -> get(i, j));
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
        this.array = array;
        this.rows = rows;
        this.cols = cols;
    }


    /**
     * the identity matrix
     *
     * @param n an nxn identity matrix
     * @return
     */
    public static MatrixDense identityMatrix(int n) {
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
        PointDense temp = row(rowA);
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

        qrd.decompose(ejmlMatrix());

        return new Pair1T<>(new MatrixDense(qrd.getQ(null, true)), new MatrixDense(qrd.getR(null, true)));
    }

    /**
     * creates a new Matrix from a set of rows
     *
     * @param rows
     * @return
     */
    public static MatrixDense fromRows(PointDense[] rows) {
        if (rows.length == 0)
            throw new RuntimeException("You're tyring to create a matrix from an empty array.");
        return new MatrixDense(rows.length, rows[0].dim()).setAll((i, j) -> rows[i].get(j));
    }

    /**
     * creates a new Matrix from a set of rows
     *
     * @param pointStream
     * @return
     */
    public static MatrixDense fromRows(Stream<PointDense> pointStream) {
        return MatrixDense.fromRows(pointStream.toArray(PointDense[]::new));
    }

    /**
     * creates a new Matrix from a set of rows
     *
     * @param pointStream
     * @return
     */
    public static MatrixDense fromRows(List<PointDense> pointStream) {
        return MatrixDense.fromRows(pointStream.toArray(PointDense[]::new));
    }

    public static Matrix fromCols(Stream<PointDense> pointStream) {
        return MatrixDense.fromCols(pointStream.toArray(PointDense[]::new));
    }

    /**
     * creates a new Matrix from a set of cols
     *
     * @param cols an array of columns
     * @return
     */
    public static MatrixDense fromCols(PointDense[] cols) {
        return new MatrixDense(cols[0].dim(), cols.length).setAll((i, j) -> cols[j].get(i));
    }

    /**
     * the cross product of the rows of this matrix
     *
     * @return
     */
    public PointDense crossProduct() {
        if (cols != 1 + rows)
            throw new RuntimeException("this matrix is the wrong size for cross "
                    + "product. You must have cols == rows + 1 but cols = "
                    + cols
                    + " and rows = " + rows);
        return new PointDense(cols).setAll(i -> Math.pow(-1, i) * removeCol(i).det());
    }

    /**
     * Sets the rows of the matrix
     *
     * TODO: this should probably be parallel.
     *
     * @param f
     * @return
     */
    public MatrixDense setRows(IntFunction<PointDense> f) {
        IntStream.range(0, rows).parallel().forEach(i -> setRow(i, f.apply(i)));
        return this;
    }

    /**
     * Sets the columns of the matrix
     *
     * @param f
     * @return
     */
    public MatrixDense setCols(IntFunction<PointDense> f) {
        IntStream.range(0, cols).forEach(i -> setCol(i, f.apply(i)));//TODO::paralel 
        return this;
    }

    /**
     * returns the point that is the weighted average of the rows of this matrix
     *
     * @param weights how much weight to give each point
     * @return the weighted average of the rows of this matrix
     */
    public PointDense weightedAvgRows(PointDense weights) {
        return weights.dot(this).mult(1.0 / weights.stream().sum());
    }

    public PointDense[] rowArray() {
        PointDense[] points = new PointDense[rows];
        Arrays.parallelSetAll(points, i -> row(i));
        return points;
    }

    /**
     * a list of the rows of this array
     *
     * @return
     */
    @Override
    public List<PointDense> rowList() {
        return Arrays.asList(rowArray());
    }

    /**
     * The sum of the rows
     *
     * @return
     */
    public PointDense rowSum() {
        return new PointDense(cols).setAll(i -> col(i).stream().sum());
    }

    /**
     * The average of the rows.
     *
     * @return
     */
    public PointDense rowAvg() {
        return new PointDense(cols).setAll(i -> col(i).avg());
    }

    /**
     * is this matrix equal to zero.
     *
     * @param epsilon
     * @return
     */
    @Override
    public boolean isZero(double epsilon) {
        return Arrays.stream(array).allMatch(a -> Math.abs(a) <= epsilon);
    }

    public long rank() {
        if (rows == 0 || cols == 0) return 0;
        return SingularOps_DDRM.rank(ejmlMatrix());
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
        double[] rowConcatArray = Arrays.copyOf(array, array.length
                + rows.cols() * rows.rows());

        System.arraycopy(rowsDense.array, 0, rowConcatArray, array.length, rowsDense.array.length);

        return new MatrixDense(rowConcatArray, this.rows + rowsDense.rows, cols);
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
        MatrixDense concat = new MatrixDense(Math.max(this.rows, cols.rows()), this.cols
                + cols.cols());
        
        MatrixDense colsDense = cols.asDense();
        
        return concat.setCols(i -> i < this.cols ? 
                col(i) : 
                colsDense.col(i - this.cols));
    }

    /**
     * creates a new matrix created from columns in this matrix that are
     * linearly independent.
     *
     * @param epsilon a small number used in building row echelon form
     * @return a new matrix consisting of the linearly independent columns in
     * this matrix
     */
    public MatrixDense independentColumns() {
        MatrixDense ind = new MatrixDense(rows, cols
                - reducedRowEchelon().numFreeVariables());
        for (int to = 0, from = 0; from < cols; from++)
            if (!reducedRowEchelon().isFreeVariabls(from))
                ind.setCol(to++, col(from));
        return ind;
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
    public static MatrixDense randomColPoints(int numPoints, PointDense center, double r) {

        return new MatrixDense(center.dim(), numPoints).setCols(i -> PointDense.uniformRand(center, r));
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
    public static MatrixDense randomRowPoints(int numPoints, PointDense center, double r) {
        return randomColPoints(numPoints, center, r).T();
    }

    public boolean hasFullRank() {
        return rank() == rows;
    }

    @Override
    public int rows() {
        return rows;
    }

    @Override
    public int cols() {
        return cols;
    }

    @Override
    public MatrixDense asDense() {
        return this;
    }

    @Override
    public MatrixSparse asSparse() {
        long size = Arrays.stream(array).filter(x -> x != 0).count();
        DMatrixSparseTriplet dmst = new DMatrixSparseTriplet(rows, cols, (int) size);
        IntStream.range(0, cols).forEach(col -> IntStream.range(0, rows).forEach(row -> dmst.set(row, col, get(row, col))));
        return new MatrixSparse(dmst);
    }

    @Override
    public boolean isSparse() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
