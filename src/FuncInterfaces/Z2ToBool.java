package FuncInterfaces;

import listTools.Pair1T;
import java.util.function.Predicate;

/**
 *
 * @author Kayak
 */
public interface Z2ToBool extends Predicate<Pair1T<Integer>>{
    public boolean of(int i, int j);

    @Override
    public default boolean test(Pair1T<Integer> t) {
        return of(t.l, t.r);
    }
}
