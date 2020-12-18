package listTools;

import java.util.concurrent.RecursiveTask;

/**
 *
 * @author dov
 */
public class MergeSort {
    private static Comparable[][] half(Comparable[] s){
        int mid2 = s.length/2;
        int mid1;
        if(s.length % 2 == 0) mid1 = mid2;
        else mid1 = mid2 + 1;

        Comparable[][] halfs = new Comparable[2][];
        halfs[0] = new Comparable[mid1];
        halfs[1] = new Comparable[mid2];

        for(int i = 0; i < s.length; i++)
            if(i < mid1) halfs[0][i] = s[i] ;
            else halfs[1][i - mid1] = s[i];

        return halfs;
    }
    private static Comparable[] merge(Comparable[] a, Comparable[] b){
        Comparable[] m = new Comparable[a.length + b.length];
        for(int ai = 0, bi = 0; ai + bi < m.length;){
            if(ai >= a.length){
                m[ai + bi] = b[bi]; bi++;
            }
            else if(bi >= b.length){
                m[ai + bi] = a[ai]; ai++;
            }
            else if(a[ai].compareTo(b[bi]) > 0){
                m[ai + bi] = a[ai]; ai++;
            }
            else{
                m[ai + bi] = b[bi]; bi++;
            }
        }

        return m;
    }
    
    
    private static Comparable[] getSort(Comparable[] s){
        if(s.length == 1) return s;
        Comparable[][] half = half(s);
        
        RecursiveTask getSortThread = new RecursiveTask<Comparable[]>() {
            @Override
            protected Comparable[] compute() {
                return getSort(half[1]);
            }
        };
        getSortThread.fork();
        
        return merge(getSort(half[0]), (Comparable[])getSortThread.join());
    }

    /**
     * sorts an array of sortables from low to high
     * @param s the array to be sorted.
     */
    public static void MergeSort(Comparable[] s){
        Comparable[] sorted = getSort(s);
        for(int i = 0; i < s.length; i++)
            s[i] = sorted[i];
    }
    /**
     * is this array already sorted?
     * @param s
     * @return
     */
    public static boolean isSorted(Comparable[] s){
        for(int i = 1; i < s.length; i++)
            if(s[i - 1].compareTo(s[i]) > 0) return false;
        return true;
    }
    
    /**
     * 
     * @param s
     * @return the element with the highest sort by value.
     */
    public static Comparable getMax(Comparable[] s){
        Comparable maxI = s[0];
        for(int i = 1; i < s.length; i++)
            if(s[i].compareTo(maxI) > 0) maxI = s[i];
        return maxI;
    }

}
