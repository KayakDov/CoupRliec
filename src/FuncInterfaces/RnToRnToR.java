package FuncInterfaces;

import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;
import java.util.function.Function;

/**
 *
 * @author Kayak
 */
public interface RnToRnToR extends Function<Point, RnToR> {

    public RnToRFunc of(double[] x);

    public default RnToRFunc of(Point x) {
        return of(x.array());
    }

    @Override
    public default RnToR apply(Point t) {
        return of(t);
    }

    public default <V> Function<V, RnToR> of(Function<? super V, ? extends Point> before){
        return x -> of(before.apply(x));
    }
    

}
