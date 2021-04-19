package Convex;

import Matricies.Point;
import java.util.function.Predicate;

/**
 *
 * @author Kayak
 */
public interface Indicator extends Predicate<Point>{
    public boolean isMember(Point x);

    @Override
    public default boolean test(Point t) {
        return isMember(t);
    }
    
}
