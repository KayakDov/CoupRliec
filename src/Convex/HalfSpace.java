
package Convex;

import Convex.Linear.Plane;
import Matricies.Matrix;
import Matricies.PointDense;

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
    public HalfSpace(PointDense p, PointDense normal) {
        this(new Plane(p, normal));
    }

    
    /**
     * creates a plane from a set of points.
     *
     * @param rowMatrix each row is a point on the boundary of this plane
     */
    public HalfSpace(Matrix rowMatrix) {
        this(new Plane(rowMatrix));
    }
    /**
     * creates a plane from a set of points.
     *
     * @param rowMatrix each row is a point on the boundary of this plane
     */
    public HalfSpace(Matrix rowMatrix, PointDense x) {
        this(new Plane(rowMatrix, x));
    }
    
    public HalfSpace(PointDense normal, double b){
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
    public PointDense proj(PointDense x) {
        if(border.below(x))return border.proj(x); 
        else return new PointDense(x);
    }
    
    @Override
    public boolean hasElement(PointDense x){
        return border.above(x) || border.hasElement(x);
    }
    
    /**
     * Is the given point x in the interior of this half space.
     * @param x
     * @return 
     */
    public boolean interiorHasElement(PointDense x){
        return border.above(x);
    }
    
    /**
     * Is the given point x in the interior of this half space.
     * @param x
     * @return 
     */
    public boolean interiorHasElement(PointDense x, double epsilon){
        return border.above(x, epsilon);
    }
    
    
    @Override
    public boolean hasElement(PointDense x, double epsilon){
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
    public PointDense normal(){
        return border.normal();
    }
    
    /**
     * is the point epsilon-near the boundary of this half space
     * @param x
     * @param epsilon
     * @return 
     */
    public boolean onSurface(PointDense x, double epsilon){
        return border.onPlane(x, epsilon);
    }
    
    /**
     * A point on the boundary of this halfspace
     * @return 
     */
    public PointDense surfacePoint(){
        return new PointDense(border.p());
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
    public double d(PointDense x) {
        if(border.above(x)) return 0;
        return border.d(x);
    }
    
    
}
