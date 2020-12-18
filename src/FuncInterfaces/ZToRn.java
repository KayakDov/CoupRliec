package FuncInterfaces;

import RnSpace.points.Point;
import java.util.function.Function;

/**
 *
 * @author Kayak
 */
public interface ZToRn extends Function<Integer, Point>{
    public Point of(int i);

    @Override
    public default Point apply(Integer t) {
        return of(t);
    }

    public default <V> Function<V, Point> of(Function<? super V, ? extends Integer> before){
        return i -> of(before.apply(i));
    }
    
    
    
}
