package RnSpace.curves;

import Convex.Interval;
import RnSpace.points.Point;
import FuncInterfaces.RToR;
import realFunction.*;
import static java.lang.Math.*;

/**
 *
 * @author Dov
 */
public class Curve2dAprx extends JoinedLines {

    
    public Curve2dAprx(RToR x, RToR y, Interval I, int n) {
        this(Curve.st((t) -> new Point(x.of(t), y.of(t)), I), n);

    }

    public Curve2dAprx(final Curve c, final int n) {
        super(c, n);
    }

    public Curve2dAprx(final RnSpace.curves.JoinedLines c) {
        super(c.I);
        lines.addAll(c.lines);
    }

    @Override
    public Point2d[] getPoints() {
        Point[] points = super.getPoints();
        Point2d[] p2d = new Point2d[points.length];

        for (int i = 0; i < points.length; i++) {
            p2d[i] = points[i].onPlane();
        }

        return p2d;
    }

}
