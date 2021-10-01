
package Convex;

import Hilbert.Vector;
import Matricies.Point;
import Matricies.Point;

/**
 *
 * @author Dov Neimand
 */
public interface ConvexSet <Vec extends Vector<Vec> >{
    
    /**
     * Is the point in the set.  Numerical errors from the projection method
     * may cause this method to err.  Safer to pass in an epsilon.  It's a good
     * idea to overwrite this method.
     * @param x the element being checked.
     * @return 
     */
    public boolean hasElement(Vec x);
    
    /**
     * Is the point epsilon-near this set.
     * @param x
     * @param epsilon
     * @return 
     */
    public boolean hasElement(Vec x, double epsilon);
    
    public Vec proj(Vec x);
    
    /**
     * The distance from a point to this convex set. This method uses the projection function.
     * @param x
     * @return 
     */
    default public double d(Vec x){
        Vector dif = proj(x).dif(x);
                
        return Math.sqrt(dif.ip(dif));
    }
}
