package listTools;

import java.util.ArrayList;
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
public class Choose<T> {

    private final List<T> list;
    private final int choose;

    /**
     * runs the function f on all the subsets of size choose in list
     *
     * @param list
     * @param choose
     */
    public Choose(List<T> list, int choose) {
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
     * @param parallel should the stream be parallel.
     * @return
     */
    public Stream<ArrayList<T>> chooseStream(boolean parallel) {
        return parallel
                ? chooseStreams(new ArrayList<>()).stream().parallel().map(al -> intsToT(al))
                : chooseStreams(new ArrayList<>()).stream().map(al -> intsToT(al));
    }

    /**
     * returns a stream of all the subsets of size choose.
     *
     * @return
     */
    public Stream<ArrayList<T>> chooseStream() {
        return chooseStream(true);
    }

    /**
     * returns an array list of all the arrays of the given choose size
     *
     * @return
     */
    public ArrayList<T>[] chooseListsArray() {
        ArrayList<ArrayList<Integer>> intListSet = chooseStreams(new ArrayList<>());
        ArrayList<T>[] chooseList = new ArrayList[intListSet.size()];
        Arrays.setAll(chooseList, i -> intsToT(intListSet.get(i)));
        return chooseList;
    }

    /**
     * converts a list of integers into a list of T's
     *
     * @param ints the list of integers
     * @return a list of T's
     */
    private ArrayList<T> intsToT(ArrayList<Integer> ints) {
        ArrayList<T> ts = new ArrayList<>();
        ints.forEach(i -> ts.add(list.get(i)));
        return ts;
    }

    /**
     * The last element in the list
     *
     * @param list
     * @return
     */
    private int last(ArrayList<Integer> list) {
        if (list.isEmpty()) return -1;
        return list.get(list.size() - 1);
    }

    /**
     *
     * @param soFar This should be an empty set when this method is called from
     * anywhere other than inside the method.
     * @return All the combinations of integers of the given size
     */
    private ArrayList<ArrayList<Integer>> chooseStreams(ArrayList<Integer> soFar) {
        ArrayList<ArrayList<Integer>> listOfLists = new ArrayList<>();

        if (soFar.size() == choose) {
            listOfLists.add(soFar);
            return listOfLists;
        }

        IntStream.range(last(soFar) + 1, list.size()).forEach(i -> {
            ArrayList<Integer> next = new ArrayList<>(soFar);
            next.add(i);
            listOfLists.addAll(chooseStreams(next));
        });

        return listOfLists;

    }

    /**
     * runs the above choose function on all the integers between start and
     * finish
     *
     * @param start the beginning of the consecutive numbers being chosen from
     * @param finish the end of the consecutive numbers being chosen from
     * @param choose the quantity being chosen
     * @return all the subsets of this size from the integers being chosen
     */
    public static Choose<Integer> range(int start, int finish, int choose) {
        int[] nums = new int[finish - start];
        Arrays.setAll(nums, i -> i + start);
        return new Choose(new ArrayList<>((List) Arrays.asList(nums)), choose);
    }

}
