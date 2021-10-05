package Hilbert;

import Convex.ConvexSet;
import Convex.RnHalfSpace;
import Convex.LinearRn.RnPlane;
import Matricies.Point;

/**
 *
 * @author Dov Neimand
 * @param <Vec> The Hilbert Space
 */
public class HalfSpace<Vec extends Vector<Vec>> implements ConvexSet<Vec>{
    private final Plane<Vec> boundry;
    
    
    /**
     * The constructor
     * @param normal a vector normal to the plane
     * @param b normal dot x \<= b
     */
    public HalfSpace(Vec normal, double b){
        boundry = new Plane(normal, b);
    }
    
    /**
     * The constructor
     * @param boundary 
     */
    public HalfSpace(Plane boundary){
        this.boundry = boundary;
    }
    

    
    @Override
    public boolean hasElement(Vec x){
        return boundry.above(x) || boundry.hasElement(x);
    }
    
    /**
     * Is the given point x in the interior of this half space.
     * @param x
     * @return 
     */
    public boolean interiorHasElement(Vec x){
        return boundry.above(x);
    }
    
    /**
     * Is the given point x in the interior of this half space.
     * @param x
     * @return 
     */
    public boolean interiorHasElement(Vec x, double epsilon){
        return !boundry.hasElement(x, epsilon) && boundry.above(x);
    }
    
    
    @Override
    public boolean hasElement(Vec x, double epsilon){
        return boundry.above(x) || boundry.hasElement(x, epsilon);
    }
    
    
    /**
     * the complement space
     * @return 
     */
    public HalfSpace complement(){
        return new HalfSpace(boundry.flipNormal());
    }
    
   /**
    * a vector normal to the boundary of this half space
    * @return 
    */
    public Vec normal(){
        return boundry.normal();
    }
    
    /**
     * is the point epsilon-near the boundary of this half space
     * @param x
     * @param epsilon
     * @return 
     */
    public boolean onSurface(Vec x, double epsilon){
        return boundry.hasElement(x, epsilon);
    }
        
   
    /**
     * the plane that makes up the boundary of this half space
     * @returna  the plane that makes up the surface of this halfspace.
     * This is the actual plane, so mess with it at your peril.
     */
    public Plane boundary(){
        return boundry;
    }

    @Override
    public String toString() {
        return boundary().toString().replace("=", "<=");
    }    

    @Override
    public Vec proj(Vec x) {
        if(hasElement(x) || boundry.hasElement(x)) return x;
        return boundry.proj(x);
    }
    
    
}
