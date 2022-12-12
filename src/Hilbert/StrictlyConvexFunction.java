package Hilbert;

import java.util.function.Function;

/**
 * A strictly convex function from a Hilbert space to R.
 * @author Dov Neimand
 * @param <Vec> The domain of the function
 */
public interface StrictlyConvexFunction<Vec extends Vector<Vec>> extends Function<Vec, Double>{
    
    /**
     * The arg min of this function over the given affine space.
     * @param A the affine space to find the arg min of this function over.
     * @return the minimum value of the function on the affine space.
     */
    public Vec argMin(AffineSpace<Vec> A);
    
    /**
     * The arg min over the entire Hilbert space.
     * @return 
     */
    public default Vec argMin(){
        return argMin(AffineSpace.<Vec>allSpace());
    }
    
    public default double min(AffineSpace<Vec> A){
        return apply(argMin(A));
    }
    
    /**
     * The minimum of a half space
     * @param hs
     * @return 
     */
    public default Vec argMin(HalfSpace<Vec> hs){
        Vec hilbAMin = argMin();
        if(hs.hasElement(hilbAMin)) return hilbAMin;
        else return argMin(hs.boundry);
    }
}
