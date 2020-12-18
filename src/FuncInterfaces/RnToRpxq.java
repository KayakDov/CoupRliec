package FuncInterfaces;

import Matricies.Matrix;
import RnSpace.points.Point;

/**
 *
 * @author Kayak
 */
public interface RnToRpxq {

    public default Matrix of(Point x) {
        return of(x.array());
    }

    public Matrix of(double[] x);
}
