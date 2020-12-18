package FuncInterfaces;

import listTools.Pair1T;
import java.util.function.Function;

/**
 *
 * @author Kayak
 */
public interface Z2ToR extends Function<Pair1T<Integer>, Double> {

    public double of(int n, int m);
    public default double of(Pair1T<Integer> p){
        return of(p.l, p.r);
    }

    @Override
    public default Double apply(Pair1T<Integer> t) {
        return of(t.l, t.r);
    }

    public default <V> Function<V, Double> of(Function<? super V, ? extends Pair1T<Integer>> before){
        return p -> of(before.apply(p));
    }
    
    

}
