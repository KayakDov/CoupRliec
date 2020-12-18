package DiscreteMath;

import FuncInterfaces.ZToR;

/**
 *
 * @author Dov Neimand
 */
public class DoubleFactorial{

    private final static long[] doubleFactorialsEven = new long[]{
        1,
        1,
        2,
        8,
        48,
        384,
        3840,
        46080,
        645120,
        10321920,
        185794560,
        3715891200L,
        81749606400L,
        1961990553600L,
        51011754393600L,
        1428329123020800L,
        42849873690624000L,
        1371195958099968000L};
    private final static long[] doubleFactorialOdd = new long[]{
        1,
        3,
        15,
        105,
        945,
        10395,
        135135,
        2027025,
        34459425,
        654729075,
        13749310575L,
        316234143225L,
        7905853580625L,
        213458046676875L,
        6190283353629375L,
        191898783962510625L,
        6332659870762850625L};

   
    public static double of(int n) {
        return n%2==0?
                doubleFactorialsEven[n/2]:
                doubleFactorialOdd[(n - 1)/2];
    }
}
