package Hilbert;

import Convex.LinearRn.RnAffineSpace;
import Matricies.Point;
import Hilbert.Vector;
import java.util.function.Function;

/**
 * A strictly convex function from a Hilbert space to R.
 * @author Dov Neimand
 */
public interface StrictlyConvexFunction extends Function<Vector, Double>{
    
    /**
     * The arg min of this function over the given affine space.
     * @param A the affine space to find the arg min of this function over.
     * @return the minimum value of the function on the affine space.
     */
    public Point argMinAffine(RnAffineSpace A);
}
