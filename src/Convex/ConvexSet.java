
package Convex;

import Matricies.PointDense;

/**
 *
 * @author Dov Neimand
 */
public interface ConvexSet extends Indicator{
    
    /**
     * Is the point in the set.  Numerical errors from the projection method
     * may cause this method to err.  Safer to pass in an epsilon.  It's a good
     * idea to overwrite this method.
     * @param x the element being checked.
     * @return 
     */
    public default boolean hasElement(PointDense x){
        return proj(x).equals(x);
    }
    
    /**
     * Is the point epsilon-near this set.
     * @param x
     * @param epsilon
     * @return 
     */
    public default boolean hasElement(PointDense x, double epsilon){
        if(hasElement(x)) return true;
        return proj(x).equals(x, epsilon);
    }
    
    /**
     * the projection of the point onto this set.
     * @param p
     * @return 
     */
    public PointDense proj(PointDense p);
    
    /**
     * The shortest distance from the point to this set.
     * @param x
     * @return 
     */
    public default double d(PointDense x) {
        if(hasElement(x)) return 0;
        return x.d(proj(x));
    }
    
    /**
     * The distance squared
     * @param x
     * @return 
     */
    public default double distSq(PointDense x){
        if(hasElement(x)) return 0;
        return x.distSq(proj(x));
    }

    @Override
    public default boolean isMember(PointDense x) {
        return hasElement(x);
    }
    
    
}
