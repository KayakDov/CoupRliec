
package DiscreteMath;

import java.util.stream.IntStream;
import FuncInterfaces.ZToR;

/**
 * This class is meant to hold some helpful tools for the use elsewhere.
 * @author Dov Neimand
 */
public abstract class ZToRFunc {
  private final int a, b;

    public ZToRFunc(int a, int b) {
        this.a = a;
        this.b = b;
    }
    
    public abstract double of(int n);
  
    public static ZToRFunc st(int a, int b, ZToR f){
        return new ZToRFunc(a, b) {
            @Override
            public double of(int n) {
                return f.of(n);
            }
        };
    }
    
    /**
     * Note, this sum is inclusive for indices a and b.
     * @return The Sigma sum.
     */
    public double sum(){
        return IntStream.range(a, b).mapToDouble(i -> of(i)).sum();
    }
    
    public double product(){
        double prod = 1;
        for(int i = a; i < b; i++ ) prod *= of(i);
        return prod;
    }
    
}
