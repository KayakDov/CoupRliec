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

    /**
     * duplicates a list and adds an item to it.
     * @param <T> the type of the list
     * @param list the list being added to
     * @param i the item to be added to the list
     * @return a new list with the given item added to it.
     */
    private static <T> ArrayList<T> add(List<T> list, T i) {
        ArrayList<T> add = new ArrayList<>(list.size() + 1);
        add.addAll(list);
        add.add(i);
        return add;
    }

    /**
     * takes in a an n choose k -1 list and returns and n choose k list
     * @param chooseList a list of all the elements in n choose k - 1
     * @param n the total number of integers being chosen from
     * @param remaining how many more times will this method be called recursively
     * @return a new list of all the n choose k combinatons.
     */
    private static Stream<List<Integer>> nextChoose(Stream<List<Integer>> chooseList, int n, int remaining) {

        return chooseList.flatMap(list -> IntStream
                .range(list.get(list.size() - 1) + 1, n + 1 - remaining).mapToObj(i -> add(list, i))
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

    /**
     * All the subsets of the given set of size k
     * @param <T> the type of element in the lists
     * @param chooseFrom the list that the sublists will be chosen from.
     * @param k the size of the sublists
     * @return all the sublists of the given list of size k.
     */
    public static <T> Stream<List<T>> choose(List<T> chooseFrom, int k) {
        return chooseIntegers(chooseFrom.size(), k).map(intList
                -> intList.stream()
                        .map(i -> chooseFrom.get(i))
                        .collect(Collectors.toList()));
    }

    
    /**
     * The power set of the given list
     * @param <T> the type of elements in the set
     * @param list 
     * @return a stream of all the subsets of the given set.
     */
    public static <T> Stream<List<T>> powerSet(List<T> list) {
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

        int k = 1;
        Combinatorics.choose(set, k).forEach(System.out::print);
        System.out.println("");
        System.out.println("number of elements is "
                + choose(set, k).count());
        System.out.println("We should have " + choose(set.size(), k));
    }
}
