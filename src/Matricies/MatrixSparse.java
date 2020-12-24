package Matricies;

import listTools.Pair1T;
import static java.lang.Math.*;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.ejml.data.DMatrix;
import org.ejml.data.DMatrix1Row;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparseCSC;
import org.ejml.data.DMatrixSparseTriplet;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.SingularOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.QRDecomposition;
import org.ejml.ops.ConvertDMatrixStruct;
import org.ejml.sparse.FillReducing;
import org.ejml.sparse.csc.CommonOps_DSCC;
import org.ejml.sparse.csc.factory.DecompositionFactory_DSCC;

public class MatrixSparse implements Matrix {

    protected DMatrixSparseCSC ejmlSparse;

    public boolean isSparse() {
        return true;
    }

    public double[][] twoDArray() {
        double twoDArray[][] = new double[rows()][cols()];
        IntStream.range(0, rows()).forEach(i -> Arrays.setAll(twoDArray[i], j -> get(i, j)));
        return twoDArray;
    }

    /**
     * copy the 2d vector into this matrix.
     *
     * @param matrix
     */
    public MatrixSparse(double[][] matrix) {
        if (matrix.length == 0) ejmlSparse = new DMatrixSparseCSC(0, 0, 0);
        else {
            int numNonZero = Arrays.stream(matrix)
                    .mapToInt(row
                            -> (int) Arrays.stream(row).filter(x -> x != 0).count()
                    ).sum();
            int rows = matrix.length;
            int cols = matrix[0].length;
            DMatrixSparseTriplet dmst = new DMatrixSparseTriplet(rows, cols, numNonZero);
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    dmst.set(i, j, matrix[i][j]);

            ejmlSparse = new DMatrixSparseCSC(rows, cols, numNonZero);
            ConvertDMatrixStruct.convert(dmst, ejmlSparse);
        }
    }

    @Override
    public boolean isSquare() {
        return rows() == cols();
    }

    /**
     * Copy constructor
     *
     * @param m
     */
    public MatrixSparse(MatrixSparse m) {
        ejmlSparse = m.ejmlSparse.copy();
    }

    public PointDense solve(Point b) {

        PointDense x = new SparsePoint(rows());
        CommonOps_DSCC.solve(ejmlSparse.copy(), b.asSparse().ejmlMatrix(), x.asSparse().ejmlMatrix());
        return x;

    }

    @Override
    public DMatrixSparseCSC ejmlMatrix() {
        return ejmlSparse;
    }

    /**
     * A stream of rows
     *
     * @return
     */
    public int rows() {
        return ejmlSparse.numRows;
    }

    /**
     * A stream of columns
     *
     * @return
     */
    public int cols() {
        return ejmlSparse.numCols;
    }

    /**
     *
     * @param i row
     * @param j column
     * @return the value at M_i,j
     */
    public double get(int i, int j) {
        return ejmlSparse.get(i, j);
    }

    /**
     * sets A_i,j = d
     *
     * @param row the row of the new value
     * @param col the column for the new value
     * @param d the value to be placed at (i, j)
     * @return this
     */
    public MatrixSparse set(int row, int col, double d) {
        ejmlSparse.set(row, col, d);
        return this;
    }

    /**
     * multiplies the matrix by a vector;
     *
     * @param p the vector
     * @return the new vector, a result of multiplying the matrix by a vector.
     */
    public PointSparse mult(Point p) {

        Point mult;
        if (p.isSparse()){
            mult = new PointDense(rows());
            CommonOps_DSCC.mult(ejmlSparse, p.asDense().ejmlMatrix(), mult.asDense().ejmlMatrix());
        }
        else{
            mult = new PointSparse(rows());
            CommonOps_DSCC.mult(ejmlSparse, p.asSparse().ejmlMatrix(), mult.asSparse().ejmlMatrix());
        }

        return mult;
    }

    /**
     * Multiplies to the two matrices. Launches a thread for each column in
     * matrix A.
     *
     * @param A
     * @return
     */
    public Matrix mult(Matrix A) {
        if (cols != A.rows) return null;
        if (isDense()) {
            DMatrixRMaj mult = new DMatrixRMaj(rows, A.cols);
            CommonOps_DDRM.mult(ejmlDenseMatrix(), A.ejmlDenseMatrix(), mult);
            return new Matrix(mult);
        } else {
            Matrix mult = Matrix.sparse(rows, cols, 0);
            CommonOps_DSCC.mult(ejmlSparse, A.sparseMatrix, mult.sparseMatrix);
            return mult;
        }
    }

    /**
     * multiplies the matrix by a constant.
     *
     * @param k the constant
     * @return
     */
    public Matrix mult(double k) {
        if (isDense()) return new Matrix(rows, cols).setAll(i -> get(i) * k);
        else {
            Matrix mult = sparse(rows, cols, 0);
            CommonOps_DSCC.add(k, ejmlSparse, 0, null, mult.sparseMatrix, null, null);
            return mult;
        }

    }

    public DMatrixRMaj ejmlDenseMatrix() {
        return new DMatrixRMaj(rows, cols, true, array);
    }

    /**
     * The determinant
     *
     * @return
     */
    public double det() {
        if (isDense()) {
            if (cols == 1) return array[0];
            return CommonOps_DDRM.det(ejmlDenseMatrix());
        } else {
            return CommonOps_DSCC.det(ejmlSparse);
        }
    }

    /**
     * The sum of two matrices.
     *
     * @param m
     * @return
     */
    public Matrix plus(Matrix m) {
        if (isDense()) return new Matrix(rows, cols).setAll(i -> get(i)
                + m.get(i));
        else {
            Matrix plus = sparse(rows, cols, 0);
            CommonOps_DSCC.add(1, ejmlSparse, 1, m.sparseMatrix, plus.sparseMatrix, null, null);
            return plus;
        }
    }

    /**
     *
     * @param n num rows
     * @param m num columns
     */
    public Matrix(int n, int m) {
        rows = n;
        cols = m;
        array = new double[n * m];
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
        return plus(m.mult(-1));
    }

    @Override
    public String toString() {
        if (isDense()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++)
                    sb.append(get(i, j)).append(" ");
                sb.append("\n");
            }

            return sb.toString().replaceAll(".0 ", " ");
        } else return ejmlSparse.toString();
    }

    /**
     *
     * @return the largest element in the matrix
     */
    public double max() {
        return Arrays.stream(array).summaryStatistics().getMax();
    }

    /**
     *
     * @param n
     * @return the nth column
     */
    public PointDense col(int n) {
        if (isDense()) return new PointDense(rows).setAll(i -> get(i, n));
        else {
            PointDense col = PointDense.sparse(rows, 0);
            col.sparseMatrix = CommonOps_DSCC.extractColumn(ejmlSparse, n, null);
            return col;
        }
    }

    public PointDense row(int n) {
        if (isDense()) return new PointDense(cols).setAll(i -> get(n, i));
        else {
            PointDense row = PointDense.sparse(cols, 0);
            row.sparseMatrix = CommonOps_DSCC.extractRows(ejmlSparse, n, n + 1, null);
            return row;
        }
    }

    /**
     * Sets the nth column to v
     *
     * @param n
     * @param v
     * @return this
     */
    public Matrix setRow(int n, PointDense v) {
        return setRow(n, v.array);
    }

    public Matrix setRow(int i, double[] x) {
        System.arraycopy(x, 0, array, cols * i, x.length);
        return this;
    }

    private double alpha(int k, Matrix A) {
        double alpha = 0;

        DoubleFunction<Double> sign = t -> t >= 0 ? t : -t;

        for (int j = k + 1; j < rows; j++)
            alpha += A.get(j, k) * A.get(j, k);
        alpha = Math.sqrt(alpha);
        alpha = -1 * sign.apply(A.get(k + 1, k)) * alpha;
        return alpha;
    }

    public Matrix houseHolderTrans() {
        final int START = 0;
        return houseHolderTrans(this, START);
    }

    private Matrix houseHolderTrans(Matrix A, int k) {

        if (k == rows - 1) return A;

        double alpha = alpha(k, A);

        double r = Math.sqrt((alpha * alpha - A.get(k + 1, k) * alpha) / 2.0);

        PointDense v = PointDense.Origin(rows);
        v.set(k + 1, (A.get(k + 1, k) - alpha) / (2.0 * r));

        for (int j = k + 2; j < rows; j++)
            v.set(j, A.get(j, k) / (2.0 * r));

        Matrix p = identityMatrix(rows).minus(v.outerProduct(v).mult(2));

        A = p.mult(A).mult(p);

        return houseHolderTrans(A, k + 1);
    }

    private Matrix transpose = null;

    /**
     * M^T, the transpose of this matrix
     *
     * @return a new matrix, the transpose of this matrix
     */
    public Matrix T() {
        if (transpose == null)
            return transpose = new Matrix(cols, rows).setAll((i, j) -> get(j, i));
        return transpose;
    }

    public Matrix inverse() {
        DMatrixRMaj inverse = new DMatrixRMaj(rows, cols, true, Arrays.copyOf(array, array.length));
        CommonOps_DDRM.invert(inverse);
        return new Matrix(inverse.data, rows, cols);
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
     * gets the row index for an index in the underlying 1-d array.
     *
     * @param k the index in the underlying 1-d array
     * @return the index of the matching row.
     */
    protected int rowIndex(int k) {
        return k / cols;
    }

    /**
     * gets the column index for an index in the underlying 1-d array.
     *
     * @param k the index in the underlying 1-d array
     * @return the index of the matching column.
     */
    protected int colIndex(int k) {
        return k % cols;
    }

    /**
     * sets all the elements of the matrix, in parallel, to f(i, j)
     *
     * @param f a unction of the row and column
     * @return
     */
    public Matrix setAll(Z2ToR f) {
        mySetAll(f);
        return this;
    }

    protected Matrix setAll(IntToDoubleFunction f) {
        Arrays.parallelSetAll(array, i -> f.applyAsDouble(i));
        return this;
    }

    protected Matrix map(DoubleFunction<Double> f) {
        return new Matrix(rows, cols).setAll((i, j) -> f.apply(get(i, j)));
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

    public abstract interface Z2ToR extends BiFunction<Integer, Integer, Double> {
    }

    /**
     * Sets some of the elements of this matrix and leaves the others alone
     *
     * @param filter chooses which elements to be set
     * @param f the function that sets the element
     * @return this matrix
     */
    public Matrix setSome(BiFunction<Integer, Integer, Boolean> filter, Z2ToR f) {
        Z2ToR filteredF = (i, j) -> filter.apply(i, j) ? f.apply(i, j) : get(i, j);
        mySetAll(filteredF);
        return this;
    }

    public Matrix topLeftSubMatrix(int n) {
        if (n > Math.min(rows, cols))
            throw new ArithmeticException("Submatrix is bigger than parrent matrix.");
        return new Matrix(n).setAll((i, j) -> get(i, j));
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
        this.array = array;
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * if this is a symmetric matrix, this will create the instance to reflect
     * that. Do not call this on a matrix that is not symmetric.
     *
     * @return the same matrix, but wrapped in a symmetric instance..
     */
    public SymmetricMatrix symetric() {
        int n = Math.min(rows, cols);
        return new SymmetricMatrix(array, n);
    }

    /**
     * the identity matrix
     *
     * @param n an nxn identity matrix
     * @return
     */
    public static Matrix identityMatrix(int n) {
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
        PointDense temp = row(rowA);
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
        if (isDense()) {
            QRDecomposition<DMatrixRMaj> qrd
                    = DecompositionFactory_DDRM.qr();

            qrd.decompose(ejmlDenseMatrix());

            return new Pair1T<>(new Matrix(qrd.getQ(null, true)), new Matrix(qrd.getR(null, true)));

        } else {
            QRDecomposition<DMatrixSparseCSC> qrd
                    = DecompositionFactory_DSCC.qr(FillReducing.NONE);

            qrd.decompose(ejmlSparse);

            return new Pair1T<>(new Matrix(qrd.getQ(null, true)), new Matrix(qrd.getR(null, true)));

        }
    }

    public Matrix(DMatrixSparseCSC sparseMatrix) {
        this.rows = sparseMatrix.numRows;
        this.cols = sparseMatrix.numCols;
        this.ejmlSparse = sparseMatrix;
    }

    /**
     * creates a new Matrix from a set of rows
     *
     * @param rows
     * @return
     */
    public static Matrix fromRows(PointDense[] rows) {
        if (rows.length == 0)
            throw new RuntimeException("You're tyring to create a matrix from an empty array.");
        if (rows[0].isDense())
            return new Matrix(rows.length, rows[0].dim()).setAll((i, j) -> rows[i].get(j));
        else {
            return fromSparsePoints(rows, false);
        }
    }

    private static Matrix fromSparsePoints(PointDense[] points, boolean cols) {

        return new Matrix(
                Arrays.stream(points).map(point -> (cols ? point : point.T()).sparseMatrix).
                        reduce((colA, colB) -> CommonOps_DSCC.concatColumns(colA, colB, null))
                        .get()
        );

    }

    /**
     * creates a new Matrix from a set of rows
     *
     * @param pointStream
     * @return
     */
    public static Matrix fromRows(Stream<PointDense> pointStream) {
        return Matrix.fromRows(pointStream.toArray(PointDense[]::new));
    }

    /**
     * creates a new Matrix from a set of rows
     *
     * @param pointStream
     * @return
     */
    public static Matrix fromRows(List<PointDense> pointStream) {
        return Matrix.fromRows(pointStream.toArray(PointDense[]::new));
    }

    public static Matrix fromCols(Stream<PointDense> pointStream) {
        return Matrix.fromCols(pointStream.toArray(PointDense[]::new));
    }

    /**
     * creates a new Matrix from a set of cols
     *
     * @param cols an array of columns
     * @return
     */
    public static Matrix fromCols(PointDense[] cols) {
        if (cols[0].isDense())
            return new Matrix(cols[0].dim(), cols.length).setAll((i, j) -> cols[j].get(i));
        else {
            return fromSparsePoints(cols, true);
        }

    }

    /**
     * A stream of the rows of this matrix.
     *
     * @return
     */
    public Stream<PointDense> rowStream() {
        return IntStream.range(0, rows).mapToObj(i -> row(i));
    }

    /**
     * A stream of the columns of this matrix.
     *
     * @return
     */
    public Stream<PointDense> colStream() {
        return IntStream.range(0, cols).mapToObj(i -> col(i));
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
    public Matrix setRows(IntFunction<PointDense> f) {
        IntStream.range(0, rows).parallel().forEach(i -> setRow(i, f.apply(i)));
        return this;
    }

    /**
     * Sets the columns of the matrix
     *
     * @param f
     * @return
     */
    public Matrix setCols(IntFunction<PointDense> f) {
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
     * Sorts the rows of this array
     *
     * @param comp
     */
    public void sortRows(Comparator<PointDense> comp) {
        List<PointDense> list = new ArrayList(Arrays.asList(rowStream().toArray(PointDense[]::new)));
        list.sort(comp);
        setRows(i -> list.get(i));
    }

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
    public boolean isZero(double epsilon) {
        return Arrays.stream(array).allMatch(a -> Math.abs(a) <= epsilon);
    }

    /**
     * Copies another matrix into part of this matrix.
     *
     * @param top the row the other matrix is to be copied into starting at
     * TODO: this method can be made faster with system copy
     * @param left the column the copy is to start at
     * @param smallerMatrix
     * @return
     */
    public Matrix copySubMatrix(int top, int left, Matrix smallerMatrix) {
        for (int row = 0; row < smallerMatrix.rows; row++)
            for (int col = 0; col < smallerMatrix.cols; col++)
                set(top + row, left + col, smallerMatrix.get(row, col));
        return this;
    }

    /**
     * The smallest square matrix containing this.
     *
     * @return
     */
    public Matrix squareMatrixFromAbove() {
        return new Matrix(Math.max(rows, cols)).copySubMatrix(0, 0, this);
    }

    /**
     * The largest square matrix contained in this.
     *
     * @return
     */
    public Matrix squareMatrixFromBelow() {
        return new Matrix(Math.min(rows, cols)).setAll((i, j) -> get(i, j));
    }

    public long rank() {
        if (rows == 0 || cols == 0) return 0;
        return SingularOps_DDRM.rank(ejmlDenseMatrix());
    }

    /**
     * Creates a new matrix whose rows are appended to this one
     *
     * @param rows the rows to be added
     * @return a new matrix with the rows from both of the previous matrices
     */
    public Matrix rowConcat(Matrix rows) {

        double[] rowConcatArray = Arrays.copyOf(array, array.length
                + rows.array.length);

        System.arraycopy(rows.array, 0, rowConcatArray, array.length, rows.array.length);

        return new Matrix(rowConcatArray, this.rows + rows.rows, cols);
    }

    /**
     * Creates a new matrix whose rows are appended to this one
     *
     * @param row
     * @return a new matrix with the rows from both of the previous matrices
     */
    public Matrix rowConcat(PointDense row) {
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
        Matrix concat = new Matrix(Math.max(this.rows, cols.rows), this.cols
                + cols.cols);
        return concat.setCols(i -> i < this.cols ? col(i) : cols.col(i
                - this.cols));
    }

    /**
     * creates a new matrix created from columns in this matrix that are
     * linearly independent.
     *
     * @param epsilon a small number used in building row echelon form
     * @return a new matrix consisting of the linearly independent columns in
     * this matrix
     */
    public Matrix independentColumns() {
        Matrix ind = new Matrix(rows, cols
                - reducedRowEchelon().numFreeVariables());
        for (int to = 0, from = 0; from < cols; from++)
            if (!reducedRowEchelon().isFreeVariabls(from))
                ind.setCol(to++, col(from));
        return ind;
    }

    /**
     * creates a new matrix created from rows in this matrix that are linearly
     * independent.
     *
     * @param epsilon a small number used in building row echelon form
     * @return a new matrix consisting of the linearly independent rows in this
     * matrix
     */
    public Matrix independentRows(double epsilon) {
        return T().independentColumns().T();
    }

    /**
     * A new matrix without some of the rows
     *
     * @return
     */
    public Matrix filterRows(Predicate<PointDense> filter) {
        return Matrix.fromRows(rowStream().filter(p -> filter.test(p)));
    }

    private ReducedRowEchelon rre = null;

    public ReducedRowEchelon reducedRowEchelon() {
        if (rre == null) return rre = new ReducedRowEchelon(this);
        else return rre;
    }

    public List<PointDense> colList() {
        return colStream().collect(Collectors.toList());
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
    public static Matrix randomColPoints(int numPoints, PointDense center, double r) {

        return new Matrix(center.dim(), numPoints).setCols(i -> PointDense.uniformRand(center, r));
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
    public static Matrix randomRowPoints(int numPoints, PointDense center, double r) {
        return randomColPoints(numPoints, center, r).T();
    }

    public boolean hasFullRank() {
        return rank() == rows;
    }

}
