package tools;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The power function and the choose function. Both methods utilize parallel
 * streams.
 *
 * @author Dov Neimand
 */
public class Combinatorics {

    private static<T> ArrayList<T> add(List<T> list, T i) {
        ArrayList<T> add = new ArrayList<>(list.size() + 1);
        add.add(i);
        add.addAll(list);
        return add;
    }

    private static Stream<List<Integer>> nextChoose(Stream<List<Integer>> chooseList, int n, int remaining) {

        return chooseList.flatMap(list -> IntStream
                .range(list.get(0) + 1, n + 1 - remaining).mapToObj(i -> add(list, i))
        );
    }

    /**
     * All the sets of n choose k combinations of integers.
     *
     * @param n the highest ineteger in the set + 1
     * @param k the size of the subsets chosen
     * @return a stream of all the sets of integers of the given size.
     */
    public static Stream<List<Integer>> chooseIntegers(int n, int k) {
        if (k == 0) return Stream.of(new ArrayList<>());

        Stream<List<Integer>> chooseList
                = IntStream.range(0, n + 1 - k).mapToObj(i -> Arrays.asList(i)).parallel();

        for (int i = 1; i < k; i++)
            chooseList = nextChoose(chooseList, n, k - i);

        return chooseList;
    }

    public static <T> Stream<List<T>> choose(List<T> chooseFrom, int k) {
        return chooseIntegers(chooseFrom.size(), k).map(intList
                -> intList.stream()
                        .map(i -> chooseFrom.get(i))
                        .collect(Collectors.toList()));
    }

    
    /**
     * @param <T>
     * @param set
     * @return 
     */
    public static <T> Stream<List<T>> powerSet(List<T> set) {
        return powerSet(set, 0);
    }

    /**
     * The power
     *
     * @param <T>
     * @param list the set you want the power set of
     * @return
     */
    private static <T> Stream<List<T>> powerSet(List<T> list, int i) {
        Stream<List<T>> lists = Stream.of(new ArrayList<>(0));
        for(T t: list)
            lists = lists.flatMap(subList -> Stream.of(subList, add(subList,t))).parallel();
        return lists;
    }


    /**
     * n choose k function
     *
     * @param n
     * @param k
     * @return
     */
    public static int choose(int n, int k) {
        if (k == 0) return 1;
        return choose(n - 1, k - 1) * n / k;
    }

    /**
     * For testing the functions of this class.
     *
     * @param args
     */
    public static void main(String[] args) {
        ArrayList<String> set = new ArrayList<>(4);
        set.add("a");
        set.add("b");
        set.add("c");
        set.add("d");

        int n = 4, k = 2;
        Combinatorics.powerSet(set).forEach(System.out::print);
        System.out.println("");
        System.out.println("wnumber of elements is "
                + powerSet(set).count());
        System.out.println("We should have " + Math.pow(2, set.size()));
    }
}
