
package Convex;

import Convex.LinearRn.RnPlane;
import Matricies.Point;

/**
 *
 * @author Dov Neimand
 */
public class HalfSpaceRn implements ConvexSet<Point>{

    private final RnPlane boundry;
    /**
     * The constructor for this class
     *
     * @param p a point on the plane
     * @param normal a vector normal to the plane
     */
    public HalfSpaceRn(Point p, Point normal) {
        this(new RnPlane(p, normal));
    }

    
    /**
     * The constructor
     * @param normal a vector normal to the plane
     * @param b normal dot x \<= b
     */
    public HalfSpaceRn(Point normal, double b){
        boundry = new RnPlane(normal, b);
    }
    
    /**
     * The constructor
     * @param border 
     */
    public HalfSpaceRn(RnPlane border){
        this.boundry = border;
    }
    

    @Override
    public Point proj(Point x) {
        if(boundry.below(x))return boundry.proj(x); 
        else return x;
    }
    
    @Override
    public boolean hasElement(Point x){
        return boundry.above(x) || boundry.hasElement(x);
    }
    
    /**
     * Is the given point x in the interior of this half space.
     * @param x
     * @return 
     */
    public boolean interiorHasElement(Point x){
        return boundry.above(x);
    }
    
    /**
     * Is the given point x in the interior of this half space.
     * @param x
     * @return 
     */
    public boolean interiorHasElement(Point x, double epsilon){
        return boundry.above(x, epsilon);
    }
    
    
    @Override
    public boolean hasElement(Point x, double epsilon){
        return boundry.above(x) || boundry.hasElement(x, epsilon);
    }
    
    
    /**
     * the complement space
     * @return 
     */
    public HalfSpaceRn complement(){
        return new HalfSpaceRn(boundry.flipNormal());
    }
    
   /**
    * a vector normal to the boundary of this half space
    * @return 
    */
    public Point normal(){
        return boundry.normal();
    }
    
    /**
     * is the point epsilon-near the boundary of this half space
     * @param x
     * @param epsilon
     * @return 
     */
    public boolean onSurface(Point x, double epsilon){
        return boundry.onPlane(x, epsilon);
    }
    
    /**
     * A point on the boundary of this halfspace
     * @return 
     */
    public Point surfacePoint(){
        return boundry.p();
    }
    
    public int dim(){
        return normal().dim();
    }
    
    public boolean hasBadSurface(){
        return boundry.isBadPlane();
    }
   
    /**
     * the plane that makes up the boundary of this half space
     * @returna  the plane that makes up the surface of this halfspace.
     * This is the actual plane, so mess with it at your peril.
     */
    public RnPlane boundary(){
        return boundry;
    }

    @Override
    public String toString() {
        return boundary().toString().replace("=", "<=");
    }    
    
    /**
     * The distance from the given point to this halfspace.
     * @param x
     * @return 
     */
    public double d(Point x){
        if(hasElement(x)) return 0;
        return boundry.d(x);
    }
    
}
