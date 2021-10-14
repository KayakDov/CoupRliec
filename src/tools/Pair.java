
package tools;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Dov Neimand
 */
public class Pair<T1, T2>{
    
    public T1 l;
    public T2 r;

    public Pair(T1 l, T2 r) {
        this.l = l;
        this.r = r;
    }

    @Override
    public String toString() {
        return "<" + l.toString() + ", " + r.toString() + ">";
    }

    public Stream parStream() {
        return IntStream.range(0, 2).parallel().mapToObj(i -> i == 0 ? l : r);
    }

    public <A, B> Pair(A a, B b, Function<A, ? extends T1> mapper1, Function<B, ? extends T2> mapper2) {

        IntStream.range(0, 2).parallel().forEach(i -> {
            if (i == 0) l = mapper1.apply(a);
            else r = mapper2.apply(b);
        });
    }

    public <A, B> Pair(Pair<A, B> p, Function<A, ? extends T1> mapper1, Function<B, ? extends T2> mapper2) {
        this(p.l, p.r, mapper1, mapper2);
    }

    public <A, B> Pair map(Function<T1, A> mapper1, Function<T2, B> mapper2) {
        return new Pair(l, r, mapper1, mapper2);
    }

    
    public interface DoSomething{
        public void appply();
    }
    
    public static void atOnce(DoSomething f1, DoSomething f2) {
        IntStream.range(0, 2).parallel().forEach(i ->{
            switch(i){
                case 1: f1.appply();break;
                case 2: f2.appply();
            }
        });
    }
    
    
}
