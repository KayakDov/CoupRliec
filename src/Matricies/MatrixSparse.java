package Matricies;

import listTools.Pair1T;
import static java.lang.Math.*;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.ejml.data.DMatrix;
import org.ejml.data.DMatrix1Row;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparse;
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
    public PointSparse mult(PointSparse p) {

        PointSparse mult = new PointSparse(rows());
        CommonOps_DSCC.mult(ejmlSparse, p.ejmlMatrix(), mult.ejmlMatrix());

        return mult;
    }

    public PointDense mult(PointDense p) {

        PointDense mult = new PointDense(rows());
        CommonOps_DSCC.mult(ejmlSparse, p.ejmlMatrix(), mult.ejmlMatrix());

        return mult;
    }

    public MatrixSparse(int rows, int cols) {
        ejmlSparse = new DMatrixSparseCSC(rows, cols);
    }

    /**
     * Multiplies to the two matrices. matrix A.
     *
     * @param A
     * @return
     */
    public Matrix mult(Matrix A) {
        if (cols() != A.rows()) return null;
        if (isDense()) return A.asDense();
        else return A.asSparse();
    }

    public MatrixDense mult(MatrixDense A) {
        DMatrixRMaj mult = new DMatrixRMaj(rows(), A.cols);
        CommonOps_DSCC.mult(ejmlMatrix(), A.ejmlMatrix(), mult);
        return new MatrixDense(mult);

    }

    public MatrixSparse mult(MatrixSparse A) {

        MatrixSparse mult = new MatrixSparse(rows(), cols());
        CommonOps_DSCC.mult(ejmlSparse, A.ejmlMatrix(), mult.ejmlMatrix());
        return mult;

    }

    /**
     * multiplies the matrix by a constant.
     *
     * @param k the constant
     * @return
     */
    public Matrix mult(double k) {

        MatrixSparse mult = new MatrixSparse(rows(), cols());
        CommonOps_DSCC.add(k, ejmlSparse, 0, null, mult.ejmlSparse, null, null);
        return mult;

    }

    /**
     * The determinant
     *
     * @return
     */
    public double det() {
        return CommonOps_DSCC.det(ejmlSparse);
    }

    public MatrixSparse(int row, int cols, int nonZeroeLength) {
        ejmlSparse = new DMatrixSparseCSC(row, cols, nonZeroeLength);
    }

    /**
     * The sum of two matrices.
     *
     * @param m
     * @return
     */
    public MatrixSparse plus(MatrixSparse m) {

        MatrixSparse plus = new MatrixSparse(rows(), cols(), m.ejmlSparse.getNonZeroLength() + ejmlSparse.getNonZeroLength());
        CommonOps_DSCC.add(1, ejmlSparse, 1, m.ejmlMatrix(), plus.ejmlMatrix(), null, null);
        return plus;

    }

    public MatrixDense plus(MatrixDense m) {
        return new MatrixDense(m.rows, m.cols).setAll((i, j) -> m.get(i, j) + ejmlSparse.get(i, j));
    }

    public Matrix minus(Matrix m) {
        return plus(m.mult(-1));
    }

    @Override
    public String toString() {
        return ejmlSparse.toString();
    }

    /**
     *
     * @param n
     * @return the nth column
     */
    public PointSparse col(int n) {
        PointSparse col = new PointSparse(rows());
        col.ejmlSparse = CommonOps_DSCC.extractColumn(ejmlSparse, n, null);
        return col;

    }

    public MatrixSparse(DMatrixSparseCSC ejmlSparse) {
        this.ejmlSparse = ejmlSparse;
    }

    public PointSparse row(int n) {

        PointSparse row = new PointSparse(cols());
        row.ejmlSparse = new MatrixSparse(CommonOps_DSCC.extractRows(ejmlSparse, n, n + 1, null)).T().ejmlMatrix();
        return row;

    }

    /**
     * M^T, the transpose of this matrix
     *
     * @return a new matrix, the transpose of this matrix
     */
    public MatrixSparse T() {
        MatrixSparse T = new MatrixSparse(cols(), rows(), ejmlSparse.getNonZeroLength());
        CommonOps_DSCC.transpose(ejmlSparse, T.ejmlSparse, null);
        return T;
    }

    public MatrixDense inverse() {
        DMatrixRMaj inverse = new DMatrixRMaj(cols(), rows());
        CommonOps_DSCC.invert(ejmlSparse, inverse);
        return new MatrixDense(inverse);
    }

    /**
     * The sum of the elements on the diagonal.
     *
     * @return
     */
    public double trace() {
        return CommonOps_DSCC.trace(ejmlSparse);
    }

    /**
     * sets all the elements of the matrix, in parallel, to f(i, j)
     *
     * @param f a unction of the row and column
     * @return
     */
    
    @Override
    public MatrixSparse setAll(Z2ToR f) {
        int nonZeroes = (int) Matrix.z2Stream(rows(), cols()).filter(p -> f.apply(p) != 0).count();

        DMatrixSparseTriplet dmst = new DMatrixSparseTriplet(rows(), cols(), nonZeroes);

        Matrix.z2Stream(rows(), cols()).filter(p -> f.apply(p) != 0).forEach(p -> dmst.set(p.l, p.r, f.apply(p)));

        DMatrixSparseCSC dmscsc = new DMatrixSparseCSC(rows(), cols(), nonZeroes);

        ConvertDMatrixStruct.convert(dmst, dmscsc);

        ejmlSparse = dmscsc;

        return this;
    }

    protected MatrixSparse mapToSparse(DoubleFunction<Double> f) {

        return new MatrixSparse(rows(), cols()).setAll((i, j) -> f.apply(get(i, j)));
    }

    protected Matrix mapToDense(DoubleFunction<Double> f) {

        return new MatrixDense(rows(), cols()).setAll((i, j) -> f.apply(get(i, j)));
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
        setAll(filteredF);
        return this;
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
