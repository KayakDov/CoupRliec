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

    private final List<Plane> fromList;
    private final int choose;

    /**
     * runs the function f on all the subsets of size choose in list
     *
     * @param list
     * @param choose
     */
    public ChoosePlanes(List<Plane> list, int choose) {
        this.fromList = list;
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

        Plane[] planeArray = new Plane[ints.length];

        Arrays.setAll(planeArray, i -> fromList.get(ints[i]));
        
        return planeArray;
    }

    private Stream<int[]> streamOfListsOfInts() {
        return streamOfListsOfInts(new int[choose], choose, fromList.size(), 0);
    }

    public static Stream<int[]> streamOfListsOfInts(int choose, int from) {
        return streamOfListsOfInts(new int[choose], choose, from, 0);
    }

    /**
     *
     * @param soFar This should be an empty array of size choose when this
     * method is called from anywhere other than inside the method.
     * @return All the combinations of integers of the given size
     */
    public static Stream<int[]> streamOfListsOfInts(int[] soFar, int choose, int from, int depth) {

        if (depth == choose)
            return Stream.of(soFar);

        int start = depth - 1 >= 0 ? soFar[depth - 1] + 1 : 0;
        if (start == from) return Stream.of();

        soFar[depth] = start;

        return Stream.concat(
                streamOfListsOfInts(soFar, choose, from, depth + 1),
                IntStream.range(start + 1, from)
                        .mapToObj(i -> i)
                        .flatMap(i -> {
                            int[] next = new int[choose];
                            System.arraycopy(soFar, 0, next, 0, depth);
                            next[depth] = i;
                            return streamOfListsOfInts(next, choose, from, depth + 1);
                        }));

    }

}
