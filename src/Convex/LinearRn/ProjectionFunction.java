package Convex.LinearRn;

import Hilbert.LinearSpace;
import Matricies.Matrix;
import Matricies.Matrix;
import Matricies.Point;
import Matricies.Point;
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
    public ProjectionFunction(LinearSpace ls, Point p, double epsilon) {
        
        Matrix nullSpaceMatrix = new RnLinearSpace(ls).matrix();
        Matrix A = RnLinearSpace.colSpaceMatrix(nullSpaceMatrix);

        if (!A.isZero(epsilon) && A.rows() == p.dim()) pm = A.mult(A.pseudoInverse());
        else{
            if(A.asDense().rank() == A.asDense().numCols) {
                if(p == null)p =  new Point(nullSpaceMatrix.rows());
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
        public Point apply(Point t) {
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
        public NoProjFuncExists(LinearSpace ls) {
            super("There is no projection function for this linear space.\nlinear space is " + ls);
        }

    }

}
