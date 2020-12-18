package RnSpcae.RnToRmFunc;

import RnSpace.curves.Curve;
import RnSpace.curves.Curve2dAprx;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFuncStr;
import RnSpace.rntor.RnToRFunc;
import FuncInterfaces.RToR;
import FuncInterfaces.RnToR;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import realFunction.Point2d;

/**
 * start 2 dimensional vector field
 *
 * @author Dov
 */
public class VectorField2d extends VectorField {

    private RnToRFunc x, y;

    public VectorField2d(String xS, String yS) {
        super(2);
        x = new RnToRFuncStr(xS, new String[]{"x", "y"});
        y = new RnToRFuncStr(yS, new String[]{"x", "y"});
    }
    
    public VectorField2d(RnToR x, RnToR y){
        super(2);
        this.x = RnToRFunc.st(x, 2);
        this.y = RnToRFunc.st(y, 2);
    }

    @Override
    public Point of(double[] p) {
        return new Point2d(x.of(p), y.of(p));
    }

    /**
     * start curve through the vector field that starts at t = p0 and ends with t =
 l
     *
     * @param p0
     * @param l
     * @return
     */
    public Curve2dAprx getCurve(Point2d p0, double l) {
        return new Curve2dAprx(super.getCurve(p0, l));
    }

    /**
     *
     * @param p0 the curves begin space evenly along p0
     * @param numCurves the number of curves to be returned
     * @param l the length of the curves to be returned
     * @return start set of curves beginning along p0 from 0 < t < l
     */
    public Curve2dAprx[] getCurves(Curve p0, int numCurves, double l) {

        Curve2dAprx[] curves = new Curve2dAprx[numCurves];
        double h = p0.I.len() / numCurves;

        for (int i = 0; i < numCurves; i++)
            curves[i] = getCurve(new Point2d(p0.of(p0.I.start() + i * h)), l);

        return curves;
    }

//    public void plotCruves(Curve p0, int numCurves, double l) {
//
//        double h = (p0.end - p0.start) / numCurves;
//
//        for (int i = 0; i < numCurves; i++)
//            getCurve(new MyPoint2d(p0.of(p0.start + i * h)), l).plot();
//    }
    
}
