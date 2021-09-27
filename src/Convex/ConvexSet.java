
package Convex;

import Hilbert.Vector;
import Matricies.Point;
import Matricies.Point;

/**
 *
 * @author Dov Neimand
 */
public interface ConvexSet <T extends Vector >{
    
    /**
     * Is the point in the set.  Numerical errors from the projection method
     * may cause this method to err.  Safer to pass in an epsilon.  It's a good
     * idea to overwrite this method.
     * @param x the element being checked.
     * @return 
     */
    public boolean hasElement(T x);
    
    /**
     * Is the point epsilon-near this set.
     * @param x
     * @param epsilon
     * @return 
     */
    public boolean hasElement(T x, double epsilon);
    
    public T proj(T x);
}
