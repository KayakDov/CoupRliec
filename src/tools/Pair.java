package tools;

import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Dov Neimand
 * @param <T>
 */
public class Pair<T> {

    public final T l, r;

    public Pair(T l, T r) {
        this.l = l;
        this.r = r;
    }
    
    public Stream<T> parStream() {
        return IntStream.range(0, 2).parallel().mapToObj(i -> i == 0 ? l : r);
    }

    public T max(Comparator<T> comp) {
        return comp.compare(l, r) > 0 ? l : r;
    }

    
}
