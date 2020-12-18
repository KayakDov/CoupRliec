package FuncInterfaces;

import java.util.function.Function;

/**
 *
 * @author Kayak
 */
public interface ZToR extends Function<Integer, Double>{
    public double of(int n);

    @Override
    public default Double apply(Integer t) {
        return of(t);
    }

    public default <V> Function<V, Double> of(Function<? super V, ? extends Integer> before){
        return i -> of(before.apply(i));
    }
    
    
    
}
