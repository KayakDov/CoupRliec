package Convex.LinearRn;

import Matricies.Matrix;
import Matricies.MatrixDense;
import Matricies.Point;
import Matricies.PointD;
import java.util.function.Function;

/**
 * A projection function for an affine space.
 * @author dov
 */
public class ProjectionFunction implements Function<Point, Point> {

    /**
     * The matrix for projecting onto the linear space.
     */
    private Matrix pm;
    /**
     * A point in the affine space.
     */
    private Point p;

    /**
     * A constructor
     * @param ls the linear space underlying the affine space.
     * @param p a point in the affine space.  Should be left null if the affine
     * space is a linear space.
     * @param epsilon a small number
     */
    public ProjectionFunction(RnLinearSpace ls, Point p, double epsilon) {

        Matrix nullSpaceMatrix = ls.matrix();
        Matrix A = RnLinearSpace.colSpaceMatrix(nullSpaceMatrix);

        if (!A.isZero(epsilon)) pm = A.mult(A.pseudoInverse());
        else{
            if(A.asDense().rank() == A.asDense().numCols) {
                if(p == null)p =  new PointD(nullSpaceMatrix.rows());
                pm = null;
            }else throw new NoProjFuncExists(ls);
        }
        
        this.p = p;
    }

    /**
     * A projection function for Rn.
     * @return 
     */
    public static ProjectionFunction ID(){
        class ID extends ProjectionFunction{
            
        public ID() {
            super(null, null, 0);
        }

        @Override
        public PointD apply(Point t) {
            return t.asDense();
        }
            
        }
        return new ID();
    }
    @Override
    public Point apply(Point x) {
        if(pm == null) return p;
        if(p == null) return pm.mult(x);
        return p.plus(pm.mult(x.minus(p)));
    }

    /**
     * An exception to be thrown when no projection function can be found.
     */
    public static class NoProjFuncExists extends RuntimeException {

        /**
         * The constructor.
         * @param ls 
         */
        public NoProjFuncExists(RnLinearSpace ls) {
            super("There is no projection function for this linear space.\nlinear space is " + ls);
        }

    }

}
