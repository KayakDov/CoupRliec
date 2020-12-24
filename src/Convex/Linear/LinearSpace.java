package Convex.Linear;

import Convex.ConvexSet;
import Matricies.Matrix;
import Matricies.ReducedRowEchelon;
import Matricies.PointDense;
import java.util.Arrays;

/**
 * An object that describes a linear/vector space
 *
 * @author Dov Neimand
 */
public class LinearSpace implements ConvexSet {

    
//    private final Matrix nullSpace;

    private PointDense[] normals;

    public LinearSpace(PointDense[] normals) {
        this.normals = normals;
    }
    
    
    /**
     * The constructor
     *
     * @param nullSpace a matrix whose null space describes this linear space
     */
//    protected LinearSpace(Matrix nullSpace) {
//        this.nullSpace = nullSpace;
//    }

    /**
     * A small number used for thresholds
     */
    private static double epsilon = 1e-8;

    /**
     * Sets the epsilon for this set
     *
     * @param epsilon a small number used for thresholds
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * a linear space factory method to create a null space
     *
     * @param m the matrix whos null space we're creating
     * @return a new linear space that is the null space of the provided matrix.
     */
    public static LinearSpace nullSpace(Matrix m) {
        return new LinearSpace(m.rowArray());
    }

    /**
     * The plane that goes through each column of the given matrix and the
     * origin. The matric
     *
     * @param colSpace
     * @return
     */
    public static LinearSpace plane(Matrix colSpace) {
        return new LinearSpace(colSpace.T().crossProduct().T().rowArray());
    }

    /**
     * A linear space factory method to create a column space
     *
     * @param basis the basis of the new linear space being created. Note, the
     * basis vectors are the columns of the matrix provided here.
     * @return the column space of the given matrix
     */
    public static LinearSpace colSpace(Matrix basis) {
        
        Matrix rcef = basis.T().reducedRowEchelon().T();
        Matrix nullMatrix = new Matrix(basis.rows - basis.cols, basis.rows);
                
        nullMatrix.setCols(i -> {
            if(i < basis.cols)
                return new PointDense(nullMatrix.rows).setAll(j -> rcef.get(j + basis.cols, i));
            else return new PointDense(nullMatrix.rows).set(i - basis.cols, -1);
                
        });
        return new LinearSpace(nullMatrix.rowArray());
        
//    An old way of doing it.    
//        Matrix basisRows = basis.independentColumns(epsilon).T();        
//        
//        if(basisRows.rows == basisRows.cols) 
//            return allSpace(basis.rows);
//
//        Matrix outSides = linearIndependentPoints(basisRows);
//                
//        return IntStream.range(0, outSides.rows).parallel()
//                .mapToObj(i -> new Matrix(outSides.rows - 1, outSides.cols)
//                     .setRows(j -> j < i? outSides.row(j): outSides.row(j + 1)))
//                .map(pointsRowMat -> LinearSpace.plane(basisRows.rowConcat(pointsRowMat).T()))
//                .reduce((ls1, ls2) -> ls1.intersection(ls2)).get();
    }
    

    /**
     * The orthogonal complement of this linear space
     *
     * @return a new space
     */
    public LinearSpace OrhtogonalComplement() {
        return colSpace(matrix().T());
    }

    public boolean isAllSpace(){
        return normals.length == 0;
    }
    
    /**
     * The matrix used for the null space.  Changing this matrix will change this linear space.
     * @return 
     */
    protected Matrix matrix(){
        return Matrix.fromRows(normals);
    }
    public Matrix nullSpaceMatrix(){
        return Matrix.fromRows(normals);
    }

    /**
     * A new matrix whose column space defines this linear space
     *
     * @return
     */
    public Matrix colSpaceMatrix() {
 
        ReducedRowEchelon rre = new ReducedRowEchelon(matrix());

        Matrix IMinus = Matrix.identityMatrix(Math.max(rre.rows, rre.cols)).minus(rre.squareMatrixFromAbove());

        if (rre.noFreeVariable()) return new PointDense(rre.rows);

        return Matrix.fromCols(rre.getFreeVariables().map(i -> IMinus.col(i)));

    }

    @Override
    public boolean hasElement(PointDense x) {
        return hasElement(x, epsilon);
    }

    @Override
    public boolean hasElement(PointDense x, double epsilon) {
        if (normals.length == 0) return true;
        return Arrays.stream(normals).allMatch(normal -> normal.dot(x) < epsilon);
    }

    private Matrix projFunc = null;

    @Override
    public PointDense proj(PointDense p) {
        if (isAllSpace()) return p;
        
        if (projFunc == null) {
            Matrix A = colSpaceMatrix();
            if (!A.isZero(epsilon))
                projFunc = A.mult(A.T().mult(A).inverse()).mult(A.T());//TODO might be made faster with p - QRT^-1(Ap) where PQ are the decomposition of A see On the easibility of projection methods for convex feasibility problems with linear inequality constraints
            else return new PointDense(p.dim());
        }
        
        return projFunc.mult(p);
    }

    @Override
    public String toString() {
        return "null space of \n" + Matrix.fromRows(normals);
    }

    /**
     * The linear sum of two vector spaces = {a + b | a in this, b in ls}
     *
     * @param ls the other vector space
     * @return a new vector space
     */
    public LinearSpace lineaerSum(LinearSpace ls) {
        return LinearSpace.colSpace(colSpaceMatrix().rowConcat(ls.colSpaceMatrix()));
    }

    public static LinearSpace allSpace(int dim) {
        return new LinearSpace(new PointDense[0]);
    }

    /**
     * The dimension of the subspace.
     *
     * @return
     */
    public long subSpaceDim() {
//        System.out.println(colSpaceMatrix());

        return colSpaceMatrix().rank();

    }

    /**
     * The intersection of this space and another.
     *
     * @param ls
     * @return
     */
    public LinearSpace intersection(LinearSpace ls) {
        PointDense[] intersection = new PointDense[normals.length + ls.normals.length];
        System.arraycopy(normals, 0, intersection, 0, normals.length);
        System.arraycopy(ls.normals, 0, intersection, normals.length, ls.normals.length);
        return new LinearSpace(intersection);
    }
}
