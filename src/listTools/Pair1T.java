package listTools;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Dov Neimand
 * @param <T>
 */
public class Pair1T<T> extends Pair<T, T>{

    public Pair1T(T l, T r) {
        super(l, r);
    }


    public Stream<T> parStream() {
        return IntStream.range(0, 2).parallel().mapToObj(i -> i == 0 ? l : r);
    }

    public <A> Pair1T(A a, A b, Function<A, ? extends T> mapper) {

        super(a, b, mapper, mapper);
    }

    public <A> Pair1T(Pair1T<A> p, Function<A, ? extends T> mapper) {
        this(p.l, p.r, mapper);
    }

    public <A> Pair1T map(Function<T, A> mapper) {
        return new Pair1T(l, r, mapper);
    }

    public T max(Comparator<T> comp) {
        return comp.compare(l, r) > 0 ? l : r;
    }

    
}
