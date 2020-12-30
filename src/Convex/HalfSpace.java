
package Convex;

import Convex.Linear.Plane;
import Matricies.Point;

/**
 *
 * @author Dov Neimand
 */
public class HalfSpace implements ConvexSet{

    private final Plane border;
    /**
     * The constructor for this class
     *
     * @param p a point on the plane
     * @param normal a vector normal to the plane
     */
    public HalfSpace(Point p, Point normal) {
        this(new Plane(p, normal));
    }

    
    
    public HalfSpace(Point normal, double b){
        border = new Plane(normal, b);
    }
    
    /**
     * The constructor
     * @param border 
     */
    public HalfSpace(Plane border){
        this.border = border;
    }
    

    @Override
    public Point proj(Point x) {
        if(border.below(x))return border.proj(x); 
        else return x;
    }
    
    @Override
    public boolean hasElement(Point x){
        return border.above(x) || border.hasElement(x);
    }
    
    /**
     * Is the given point x in the interior of this half space.
     * @param x
     * @return 
     */
    public boolean interiorHasElement(Point x){
        return border.above(x);
    }
    
    /**
     * Is the given point x in the interior of this half space.
     * @param x
     * @return 
     */
    public boolean interiorHasElement(Point x, double epsilon){
        return border.above(x, epsilon);
    }
    
    
    @Override
    public boolean hasElement(Point x, double epsilon){
        return border.above(x) || border.hasElement(x, epsilon);
    }
    
    
    /**
     * the complement space
     * @return 
     */
    public HalfSpace complement(){
        return new HalfSpace(border.flipNormal());
    }
    
   /**
    * a vector normal to the boundary of this half space
    * @return 
    */
    public Point normal(){
        return border.normal();
    }
    
    /**
     * is the point epsilon-near the boundary of this half space
     * @param x
     * @param epsilon
     * @return 
     */
    public boolean onSurface(Point x, double epsilon){
        return border.onPlane(x, epsilon);
    }
    
    /**
     * A point on the boundary of this halfspace
     * @return 
     */
    public Point surfacePoint(){
        return border.p();
    }
    
    public int dim(){
        return normal().dim();
    }
    
    public boolean hasBadSurface(){
        return border.isBadPlane();
    }
   
    /**
     * the plane that makes up the boundary of this half space
     * @returna  the plane that makes up the surface of this halfspace.
     * This is the actual plane, so mess with it at your peril.
     */
    public Plane boundary(){
        return border;
    }

    @Override
    public String toString() {
        return boundary().toString().replace("=", "<=");
    }

    @Override
    public double d(Point x) {
        if(border.above(x)) return 0;
        return border.d(x);
    }
    
    
}
