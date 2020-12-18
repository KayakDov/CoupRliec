
package FuncInterfaces;

import RnSpace.Optimization.Min;
import RnSpace.curves.FstOrdPDE;
import RnSpace.curves.JoinedLines;
import RnSpace.points.Point;
import RnSpace.rntor.GraphX;
import RnSpcae.RnToRmFunc.VectorField;

/**
 *
 * @author Dov Neimand
 */
public interface RnToRn extends RnToRm{

    /**
     *
     * @param guess a place to start looking for the fixed point with NelderMead
     * @param acc
     * @return a fixed point
     */
    public default Point fixedPoint(GraphX guess, double acc) {

        RnToR fix = x -> of(x).dot(of(x));
            
        return Min.gradDescentNoLineSearch(fix, guess, 1, acc);
    }
    
    /**
     *
     * @param p0 the curve starts at p0 at t = 0 and ends at t = l
     * @param l
     * @return a curve in this vector field that starts at p0
     */
    public default JoinedLines getCurve(Point p0, double l) {
        
        return new FstOrdPDE(p0, 0, l) {

            @Override
            public Point f(double t, Point x) {
                return RnToRn.this.of(x);
            }
        };
    }
}
