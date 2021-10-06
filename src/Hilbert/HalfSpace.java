package Hilbert;

import Convex.ConvexSet;
import Matricies.Point;

/**
 *
 * @author Dov Neimand
 * @param <Vec> The Hilbert Space
 */
public class HalfSpace<Vec extends Vector<Vec>> implements ConvexSet<Vec>{
    protected final Plane<Vec> boundry;
    
    
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
    public HalfSpace(Plane<Vec> boundary){
        this.boundry = boundary;
    }

    public HalfSpace(Vec onPlane, Vec normal) {
        this.boundry = new Plane<Vec>(normal, onPlane);
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
    
        /**
     * A point on the boundary of this halfspace
     * @return 
     */
    public Vec surfacePoint(){
        return boundry.p();
    }
    
    /**
     * The number of dimensions the halfspace lives in.
     * @return 
     */
    public int dim(){
        return normal().dim();
    }
    
}
