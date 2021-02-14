package listTools;

import Convex.Linear.Plane;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Preforms the provided operation on all the subsets of a given size on the
 * set.
 *
 * @author Dov Neimand
 */
public class ChoosePlanes {

    private final List<Plane> list;
    private final int choose;

    /**
     * runs the function f on all the subsets of size choose in list
     *
     * @param list
     * @param choose
     */
    public ChoosePlanes(List<Plane> list, int choose) {
        this.list = list;
        this.choose = choose;
    }

    /**
     * n choose k function
     *
     * @param n
     * @param k
     * @return
     */
    public static long choose(long n, long k) {
        if (k == 0) return 1;
        return choose(n - 1, k - 1) * n / k;
    }

    
    /**
     * returns a stream of all the subsets of size choose.
     *
     * @return
     */
    public Stream<Plane[]> chooseStream() {
        return streamOfListsOfInts().parallel().map(al -> intsToList(al));
    }
    
    


    /**
     * converts a list of integers into a list of T's
     *
     * @param ints the list of integers
     * @return a list of T's
     */
    private Plane[] intsToList(int[] ints) {
        
        Plane[] ts = new Plane[ints.length];
        
        Arrays.setAll(ts, i -> this.list.get(ints[i]));
        return ts;
    }
    
    

    private Stream<int[]> streamOfListsOfInts() {
        return streamOfListsOfInts(new int[0], choose, list.size());
    }
    
    private static Stream<int[]> streamOfListsOfInts(int choose, int from) {
        return streamOfListsOfInts(new int[0], choose, from);
    }
    
    
    /**
     *
     * @param soFar This should be an empty set when this method is called from
     * anywhere other than inside the method.
     * @return All the combinations of integers of the given size
     */
    public static Stream<int[]> streamOfListsOfInts(int[] soFar, int choose, int from) {

        if (soFar.length == choose)
            return Stream.of(soFar);
        
        int start = soFar.length - 1 >= 0 ? soFar[ soFar.length - 1] + 1 : 0;
        
        
        return IntStream
                .range(start, from)
                .mapToObj(i -> i)
                .flatMap(i -> {
                    int[] next = new int[soFar.length + 1];
                    System.arraycopy(soFar, 0, next, 0, soFar.length);
                    next[next.length - 1] = i;
                    return streamOfListsOfInts(next, choose, from);
                });
    }
    

}
