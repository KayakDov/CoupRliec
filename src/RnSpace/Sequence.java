package RnSpace;

import FuncInterfaces.RnToRn;
import FuncInterfaces.ZToR;
import RnSpace.points.Point;

/**
 *
 * @author Dov Neimand
 */
public interface Sequence {

    public abstract Point iteration(Point x, int i);

    public default Point cauchyLimit(Point start, double end) {

        Point x = iteration(start, 1),
                prevX = start;

        for (int i = 2; x.d(prevX) > end; i++) {
            prevX = x;
            x = iteration(x, i);
        }
        return x;
    }

    public default Point of(Point start, int n) {
        Point x = new Point(start);
        for (int i = 1; i < n; i++) x = iteration(x, i);
        return x;
    }

}
