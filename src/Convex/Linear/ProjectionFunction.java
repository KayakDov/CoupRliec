package Convex.Linear;

import Matricies.Matrix;
import Matricies.MatrixDense;
import Matricies.Point;
import Matricies.PointD;
import java.util.function.Function;

/**
 *
 * @author dov
 */
public class ProjectionFunction implements Function<Point, Point> {

    private Matrix pm;
    private Point p;

    public ProjectionFunction(LinearSpace ls, Point p, double epsilon) {

        Matrix nullSpaceMatrix = ls.matrix();
        Matrix A = LinearSpace.colSpaceMatric(nullSpaceMatrix);

        if (!A.isZero(epsilon)) pm = A.mult(A.pseudoInverse());
        else{
            if(A.asDense().rank() == A.asDense().numCols) {
                if(p == null)p =  new PointD(nullSpaceMatrix.rows());
                pm = null;
            }else throw new NoProjFuncExists(ls);
        }
        
        this.p = p;
    }

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

    public static class NoProjFuncExists extends RuntimeException {

        public NoProjFuncExists(LinearSpace ls) {
            super("There is no projection function for this linear space.\nlinear space is " + ls);
        }

    }

}
