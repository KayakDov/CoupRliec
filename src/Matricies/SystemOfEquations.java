//This code is obsolete

//package Matricies;
//
//import Convex.AffineSpace;
//import Convex.Plane;
//import listTools.Pair1T;
//import Matricies.Matrix;
//import RnSpace.points.Point;
//import java.util.Collections;
//import java.util.LinkedList;
//import java.util.NoSuchElementException;
//import java.util.concurrent.RecursiveTask;
//
///**
// *
// * @author Dov Neimand
// */
//public class SystemOfEquations extends RecursiveTask<Point> {
//
//    private Matrix matrix;
//    private Point b;
//
//    private double epsilon =1e-7;
//
//    public void setEpsilon(double epsilon) {
//        this.epsilon = epsilon;
//    }
//    
//        
//    /**
//     * The constructor Solves the system Ax=b
//     *
//     * @param A a the matrix
//     * @param b
//     */
//    public SystemOfEquations(Matrix A, Point b) {
//        this.matrix = new Matrix(A);
//        if (A.rows < A.cols)
//            throw new RuntimeException("\n" + A + " \nis not a square matrix. Consider working with an affine space.");
//        this.b = new Point(b);
//    }
//    
//    /**
//     * The constructor
//     * @param singlePoint an affine space that contains a single point.
//     */
//    public SystemOfEquations(AffineSpace singlePoint){
//        this(singlePoint.nullMatrix, singlePoint.b);
//    }
//    
//    /**
//     * Finds the intersection point of a line and a plane, if they intersect.
//     * @param plane a hyperplane
//     * @param line a line
//     */
//    public SystemOfEquations(Plane plane, AffineSpace line){
//        this(plane.nullMatrix.rowConcat(line.nullMatrix),plane.b.concat(line.b));
//    }
//
//    class SwapRowCol {
//
//        public int a, b;
//        public boolean isCol;
//        public static final boolean COL = true, ROW = false;
//
//        public SwapRowCol(int a, int b, boolean isCol) {
//            this.a = a;
//            this.b = b;
//            this.isCol = isCol;
//
//            if (isCol) matrix.swapCols(a, b);
//            else {
//                matrix.swapRows(a, b);
//                SystemOfEquations.this.b.swapRows(a, b);
//            }
//        }
//
//        public void unswap(Point sol) {
//            if(isCol) sol.swapValues(a, b);
//        }
//
//    }
//    private LinkedList<SwapRowCol> swaps = new LinkedList<>();
//
//    /**
//     * swaps the first row of the matrix with the row that has a maximal
//     * absolute value in the given column.
//     *
//     * @param col the column to search for a maximal element for swapping
//     */
//    private void pivotColumn(int col) {
//
//        int colSwap = matrix.row(col).map(i -> Math.abs(i)).argMax();
//        if(Math.abs(matrix.row(col).get(colSwap)) < epsilon) colSwap = col;
//        swaps.addFirst(new SwapRowCol(col, colSwap, SwapRowCol.COL));
//    }
//
//    /**
//     * swaps the first row of the matrix with the row that has a maximal
//     * absolute value in the given column.
//     *
//     * @param col the column to search for a maximal element for swapping
//     */
//    private void pivotRow(int rowIndex) {
//        Point row = matrix.col(rowIndex);
//        int rowSwap = row.setAll(i -> i < rowIndex? 0: row.get(i)).map(d -> Math.abs(d)).argMax();
//        
//        if(Math.abs(matrix.col(rowIndex).get(rowSwap)) < epsilon) rowSwap = rowIndex;
//        
//        swaps.addFirst(new SwapRowCol(rowIndex, rowSwap, SwapRowCol.ROW));
//    }
//
//    /**
//     * reduces the given row, by subtracting the proper multiple of the first
//     * row to zero out the index to the left of the diagonal.
//     *
//     * @param rowIndex the row to have one of its values zeroed out
//     * @param topRowIndex a multiple of this row will be subtracted to zero out
//     * the topRowIndex - 1 of the rowIndex row.
//     */
//    private void reduceRow(int rowIndex, int topRowIndex) {
//        
//        Point topRow = matrix.row(topRowIndex),
//                row = matrix.row(rowIndex);
//
//        double mult = row.get(topRowIndex) / topRow.get(topRowIndex);
//
//        row = row.minus(topRow.mult(mult));
//        b.set(rowIndex, b.get(rowIndex) - b.get(topRowIndex) * mult);
//
//        matrix.setRow(rowIndex, row);
//    }
//
//    /**
//     * reduces the rows with partial pivoting to create a triangular matrix.
//     * TODO: reverse pivots on final solution
//     */
//    private void rowRductionWithPivoting() {
//        for (int i = 0; i < matrix.cols; i++) {
//
////            System.out.println("pivoting col\n" + toString());
//            pivotColumn(i);
////            System.out.println("to\n" + toString() + "\npivoting row");
//            pivotRow(i);
////            System.out.println("to\n" + toString());
//
//            for (int j = i + 1; j < matrix.rows; j++) {
////            System.out.println("row rduction\n" + toString());
//                reduceRow(j, i);
////            System.out.println("to\n" + toString());
//            }
//        }
////        System.exit(0);
//    }
//
//    /**
//     * Solves a row of a triangular matrix
//     *
//     * @param rowIndex the index to be solved
//     * @param solSoFar the solution for all the rows beneath this one with zero
//     * at this row and above.
//     * @return the solution for the element on the diagonal of this row.
//     */
//    private double solveRow(int rowIndex, Point solSoFar) {
//        return (b.get(rowIndex) - solSoFar.dot(matrix.row(rowIndex)))
//                / matrix.get(rowIndex, rowIndex);
//    }
//
//    /**
//     * Solves the system of equations.
//     *
//     * @return
//     */
//    @Override
//    public Point compute() {
//
//        rowRductionWithPivoting();
//        
//        for(int i = matrix.cols; i < matrix.rows; i++)
//            if(Math.abs(b.get(i)) > epsilon) {
//                throw new NoSuchElementException("There is no solution to this system of equations. \n matrix = \n" + matrix + " b = " + b);
//
//            }
//
//        Point solve = new Point(matrix.cols);
//        for (int row = matrix.cols - 1; row >= 0; row--)
//            solve.set(row, solveRow(row, solve));
//
//        swaps.forEach(swap -> swap.unswap(solve));
//
//        return solve;
//    }
//
//    @Override
//    public String toString() {
//        return matrix.toString() + b.toString() + "\n";
//    }
//
//}
