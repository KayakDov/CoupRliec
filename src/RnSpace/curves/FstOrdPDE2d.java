/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RnSpace.curves;

import RnSpace.points.Point;
import RnSpace.rntor.RnToRFuncStr;
import RnSpace.rntor.RnToRFunc;
import realFunction.Point2d;
import realFunction.RToRFunc;


/**
 *
 * @author Dov
 */
public class FstOrdPDE2d extends FstOrdPDE{
    private RnToRFunc dxdt,dydt;

   
    /**
     * 
     * @param dxdt a function of (x, y, t)
     * @param dydt a function of (x, y, t)
     * @param p0
     * @param T how long the curve id [0, T]
     */
    public FstOrdPDE2d(RnToRFunc dxdt, RnToRFunc dydt, Point p0, double T) {
        super(p0, 0, T);
        this.dxdt = dxdt;
        this.dydt = dydt;
        super.setLines();
    }
    
    public FstOrdPDE2d(String dxdt, String dydt, Point p0, double T) {
        this(
                new RnToRFuncStr(dxdt, new String[]{"x", "y", "t"}), 
                new RnToRFuncStr(dydt, new String[]{"x", "y", "t"}),
                p0, T);
    }   
    
    @Override
    public Point f(double t, Point x) {
        
        Point xt = new Point(x.get(0), x.get(1), t);  
        
        return new Point2d(dxdt.of(xt),dydt.of(xt));
    }
    
//    public static void phasePortrait(Curve c, int numLines, double lineLength, String dxdt, String dydt){
//        
//        double h = (c.b - c.a)/numLines;        
//        for(double t = c.a; t <= c.b; t += h){
//            Curve2dAprx line = new Curve2dAprx
//                    (new FstOrdPDE2d(dxdt, dydt, new MyPoint(c.of(t)), lineLength));
//            
//            line.plot();            
//        }
//    }
}
