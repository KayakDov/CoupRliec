package RnSpace.Optimization;

import RnSpace.curves.Line;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;
import RnSpace.rntor.SubGradient;

/**
 *
 * @author Kayak
 */
public class GradDescentLineSearch extends DescentRuleGradient {

    public GradDescentLineSearch(RnToRFunc f, double end, double dt) {
        super(f, end, dt);
    }

    @Override
    public Point of(Point x) {
        return new Line(x,  x.minus(gradX.mult(dt))).minUnsafe(getF(), .3);
    }

    @Override
    public Point of(double[] x) {
        return of(new Point(x));
    }
}
