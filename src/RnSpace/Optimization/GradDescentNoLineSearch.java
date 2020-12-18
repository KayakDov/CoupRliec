package RnSpace.Optimization;

import FuncInterfaces.RnToR;
import Matricies.Matrix;
import RnSpace.curves.Line;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;

/**
 *
 * @author Kayak
 */
public class GradDescentNoLineSearch extends DescentRuleGradient {

    public GradDescentNoLineSearch(RnToR f, double end, double dt) {
        super(f, end, dt);
    }


    @Override
    public Point of(double[] x) {
        return of(new Point(x));
    }

    public Point of(Point x, Matrix hessX) {
        double alpha = gradX.dot(gradX) / gradX.dot(hessX.mult(gradX));
        Point xn = x.minus(gradX.mult(alpha));
        if (isGoodChoice(x, xn)) return xn;
        return new Line(x,  x.minus(gradX.mult(dt))).min(getF(), dt);
    }

    @Override
    public Point of(Point x) {
        return of(x, hessian.of(x));
    }
}
