package Matricies;

import listTools.Pair1T;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparse;
import org.ejml.data.DMatrixSparseCSC;
import org.ejml.data.DMatrixSparseTriplet;
import org.ejml.interfaces.decomposition.QRDecomposition;
import org.ejml.sparse.FillReducing;
import org.ejml.sparse.csc.CommonOps_DSCC;
import org.ejml.sparse.csc.factory.DecompositionFactory_DSCC;

/**
 * This is a sparse Matrix.  The class is not sufficiently tested.
 * @author Kayak
 */
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

            setFromTrip(dmst);
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

    public Point solve(Point b) {

        PointSparse x = new PointSparse(rows());
        CommonOps_DSCC.solve(ejmlSparse.copy(), b.asSparse().ejmlSparse, x.asSparse().ejmlSparse);
        return x;

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
    public void set(int row, int col, double d) {
        ejmlSparse.set(row, col, d);
    }

    /**
     * multiplies the matrix by a vector;
     *
     * @param p the vector
     * @return the new vector, a result of multiplying the matrix by a vector.
     */
    public PointSparse mult(PointSparse p) {

        PointSparse mult = new PointSparse(rows());
        CommonOps_DSCC.mult(ejmlSparse, p.ejmlSparse, mult.ejmlSparse);

        return mult;
    }

    public PointD mult(PointD p) {

        PointD mult = new PointD(rows());
        CommonOps_DSCC.mult(ejmlSparse, p, mult);

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
    public MatrixSparse mult(Matrix A) {
        if (cols() != A.rows()) return null;
        if (isDense()) return mult(A.asDense());
        else return mult(A.asSparse());
    }

    public MatrixSparse mult(MatrixDense A) {
        MatrixSparse mult = new MatrixSparse(rows(), cols());
        CommonOps_DSCC.mult(ejmlSparse, A.asSparse().ejmlSparse, mult.ejmlSparse);
        return mult;
    }

    public MatrixSparse mult(MatrixSparse A) {

        MatrixSparse mult = new MatrixSparse(rows(), cols());
        CommonOps_DSCC.mult(ejmlSparse, A.ejmlSparse, mult.ejmlSparse);
        return mult;

    }

    /**
     * multiplies the matrix by a constant.
     *
     * @param k the constant
     * @return
     */
    public MatrixSparse mult(double k) {

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
        CommonOps_DSCC.add(1, ejmlSparse, 1, m.ejmlSparse, plus.ejmlSparse, null, null);
        return plus;

    }

    public MatrixDense plus(MatrixDense m) {
        return new MatrixDense(m.numRows, m.numCols).setAll((i, j) -> m.get(i, j) + ejmlSparse.get(i, j));
    }

    public Matrix minus(Matrix m) {
        return plus(m.mult(-1));
    }

    public MatrixDense minus(MatrixDense m) {
        return plus(m.mult(-1));
    }

    public MatrixSparse minus(MatrixSparse m) {
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
        row.ejmlSparse = new MatrixSparse(CommonOps_DSCC.extractRows(ejmlSparse, n, n + 1, null)).T().ejmlSparse;
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

        return setFromTrip(dmst);
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
     * Sets this matrix from a DMatrixSparseTriplet.
     *
     * @param trip
     * @return
     */
    protected MatrixSparse setFromTrip(DMatrixSparseTriplet trip) {
        ejmlSparse = new DMatrixSparseCSC(trip.numRows, trip.numCols, trip.getNonZeroLength());
//        ConvertDMatrixStruct.convert(trip, ejmlSparse);TODO:This function needs to be fixed
        return this;
    }

    /**
     * the identity matrix
     *
     * @param n an nxn identity matrix
     * @return
     */
    public static MatrixSparse identity(int n) {
        DMatrixSparseTriplet dmst = new DMatrixSparseTriplet(n, n, n);
        for (int i = 0; i < n; i++) dmst.set(i, i, 1);
        return new MatrixSparse(n, n).setFromTrip(dmst);
    }

    /**
     * the PQ decomposition of this matrix. This may also be called QR?
     *
     * @return
     */
    public Pair1T<Matrix> QRDecomposition() {

        QRDecomposition<DMatrixSparseCSC> qrd
                = DecompositionFactory_DSCC.qr(FillReducing.NONE);

        qrd.decompose(ejmlSparse);

        return new Pair1T<>(new MatrixSparse(qrd.getQ(null, true)), new MatrixSparse(qrd.getR(null, true)));

    }

    /**
     * creates a new Matrix from a set of rows
     *
     * @param rows
     * @return
     */
    public static MatrixSparse fromRows(Point[] rows) {

        int nonZeroe = (int)Arrays.stream(rows).mapToInt(row -> row.numNonZeroes()).sum();

        DMatrixSparseTriplet trip = new DMatrixSparseTriplet(rows.length, rows[0].dim(), nonZeroe);

        IntStream.range(0, rows.length)
                .forEach(i -> rows[i].asSparse().nonZeroes()
                        .forEach(coord -> trip.set(i, coord.row, coord.value)));
        
        return new MatrixSparse(rows.length, rows[0].dim(), nonZeroe).setFromTrip(trip);
    }

    /**
     * creates a new Matrix from a set of rows
     *
     * @param pointStream
     * @return
     */
    public static MatrixSparse fromRows(Stream<PointSparse> pointStream) {
        return MatrixSparse.fromRows(pointStream.toArray(PointSparse[]::new));
    }

    /**
     * creates a new Matrix from a set of rows
     *
     * @param pointStream
     * @return
     */
    public static MatrixSparse fromRows(List<PointSparse> pointStream) {
        return MatrixSparse.fromRows(pointStream.toArray(PointSparse[]::new));
    }

    
    public static MatrixSparse fromCols(Point[] cols){
        
        int numNonZeroes = Arrays.stream(cols).mapToInt(p -> p.numNonZeroes()).sum();
        
        DMatrixSparseTriplet trip = new DMatrixSparseTriplet(cols[0].dim(), cols.length, numNonZeroes);
        
        IntStream.range(0, cols.length)
                .forEach(colInd -> 
                        IntStream.range(0, cols[colInd].dim())
                                .forEach(rowInd ->
                                    trip.set(rowInd, colInd, cols[colInd].get(rowInd))
                                )
                );
        return new MatrixSparse(trip);
    }

    /**
     * creates a new Matrix from a set of cols
     *
     * @param cols an array of columns
     * @return
     */
    public static Matrix fromCols(PointSparse[] cols) {
        return fromRows(cols).T();
    }

    /**
     * A stream of the rows of this matrix.
     *
     * @return
     */
    public Stream<PointSparse> rowStream() {
        return IntStream.range(0, rows()).mapToObj(i -> row(i));
    }

    /**
     * A stream of the columns of this matrix.
     *
     * @return
     */
    @Override
    public Stream<PointSparse> colStream() {
        return IntStream.range(0, cols()).mapToObj(i -> col(i));
    }

    public boolean isZero(double epsilon) {
        Iterator<DMatrixSparse.CoordinateRealValue> iter = ejmlSparse.createCoordinateIterator();

        while (iter.hasNext())
            if (Math.abs(iter.next().value) > epsilon)
                return false;

        return true;
    }

    /**
     * This implementation creates the reduced row echelon form to find the
     * rank.
     *
     * @return
     */
    public long rank() {
        return new ReducedRowEchelonDense(this).getBasicVariables().count();

    }

    public MatrixSparse rowConcat(Matrix rows) {
        MatrixSparse rowsSparse = rows.asSparse();

        DMatrixSparseCSC rowConcat = new DMatrixSparseCSC(rows() + rows.rows(), cols(), ejmlSparse.getNonZeroLength() + rowsSparse.ejmlSparse.getNonZeroLength());
        CommonOps_DSCC.concatRows(ejmlSparse, rowsSparse.ejmlSparse, rowConcat);
        return new MatrixSparse(rowConcat);
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
        MatrixSparse colsSparse = cols.asSparse();

        DMatrixSparseCSC colConcat = new DMatrixSparseCSC(rows(), cols() + cols.cols(), ejmlSparse.getNonZeroLength() + colsSparse.ejmlSparse.getNonZeroLength());
        CommonOps_DSCC.concatColumns(ejmlSparse, colsSparse.ejmlSparse, colConcat);
        return new MatrixSparse(colConcat);
    }

    @Override
    public ReducedRowEchelonDense reducedRowEchelon() {
        return new ReducedRowEchelonDense(this);
    }

    public MatrixSparse(DMatrixSparseTriplet trip) {
        setFromTrip(trip);
    }

    public boolean hasFullRank() {
        return rank() == rows();
    }

    @Override
    public MatrixDense asDense() {
        return new MatrixDense(rows(), cols()).setAll((i, j) -> get(i, j));
    }

    @Override
    public MatrixSparse asSparse() {
        return this;
    }

    @Override
    public Matrix plus(Matrix m) {
        if (m.isDense()) return plus(m.asDense());
        else return plus(m.asSparse());
    }

    @Override
    public List<Point> rowList() {
        return rowStream().collect(Collectors.toList());
    }

    /**
     * A stream of the non zeroe elements and their coordinates
     *
     * @return
     */
    public Stream<DMatrixSparse.CoordinateRealValue> nonZeroes() {
        Iterator<DMatrixSparse.CoordinateRealValue> iter = ejmlSparse.createCoordinateIterator();

        Iterable<DMatrixSparse.CoordinateRealValue> iterable = () -> iter;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    @Override
    public PointSparse mult(Point p) {

        PointSparse ps = new PointSparse(rows());
        CommonOps_DSCC.mult(ejmlSparse, p.asSparse().ejmlSparse, ps.ejmlSparse);
        return ps;
    }

    public double epsilon = 1e-9;

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public boolean equals(Matrix obj) {
        return nonZeroes().allMatch(coord -> Math.abs(coord.value - obj.get(coord.row, coord.col)) < epsilon);
    }

    @Override
    public int numNonZeroes() {
        return ejmlSparse.getNonZeroLength();
    }

    @Override
    public PointSparse[] rowsArray() {
        return rowStream().toArray(PointSparse[]::new);
    }

    @Override
    public MatrixSparse sameType() {
        return new MatrixSparse(rows(), cols());
    }

    @Override
    public MatrixSparse setCols(IntFunction<Point> f) {

        PointSparse[] cols = new PointSparse[cols()];
        Arrays.setAll(cols, f);

        int numZeroes = Arrays.stream(cols).mapToInt(col -> col.numNonZeroes()).sum();

        DMatrixSparseTriplet trip = new DMatrixSparseTriplet(rows(), cols(), numZeroes);

        IntStream.range(0, cols()).forEach(i -> {
            IntStream.range(0, rows()).forEach(j -> trip.set(i, j, cols[i].get(j)));
        });
        return setFromTrip(trip);
    }

    @Override
    public Matrix square() {
        int size = Math.max(rows(), cols());
        DMatrixSparseTriplet trip = new DMatrixSparseTriplet(size, size, ejmlSparse.getNonZeroLength());
        nonZeroes().forEach(coord -> trip.set(coord.row, coord.col, coord.value));
        return new MatrixSparse(trip);
    }

    
    
    @Override
    public Matrix setIf(Z2Predicate filter, Z2ToR f) {
        DMatrixSparseTriplet trip = new DMatrixSparseTriplet(rows(), cols(), 0);
        Matrix.z2Stream(rows(), cols()).forEach(p ->  trip.set(p.l, p.r, filter.test(p)?f.apply(p):get(p.l, p.r)));
        setFromTrip(trip);
        return this;
    }

    @Override
    public Matrix setIf(Predicate<Double> filter, Z2ToR f) {
        DMatrixSparseTriplet trip = new DMatrixSparseTriplet(rows(), cols(), 0);
        Matrix.z2Stream(rows(), cols()).forEach(p ->  trip.set(p.l, p.r, filter.test(get(p.l, p.r))?f.apply(p):get(p.l, p.r)));
        setFromTrip(trip);
        return this;
    }

    @Override
    public Matrix pseudoInverse() {
         return asDense().pseudoInverse();  //TODO: there might be a better way to do this.
        
    }
}