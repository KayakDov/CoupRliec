package Hilbert;

import Matricies.Point;
import java.lang.reflect.Array;

/**
 * A hyperplane in a Hilbert Space
 * @author Dov Neimand
 * @param <Vec>
 */
public class Plane<Vec extends Vector<Vec>> extends AffineSpace<Vec>{
    
    /**
     * The constructor
     * @param normal the vector normal to this plane
     * @param b Every point on this plane, x, is a solution to normal dot x = b
     */
    public Plane(Vec normal, double b) {
        super((Vec[]) (Array.newInstance(normal.getClass(),1)), 
                Point.oneD(b)
        );
        linearSpace.normals[0] = normal;
    }
    
    /**
     * The constructor for this class
     *
     * @param onPlane a vector on the plane
     * @param normal a vector normal to the plane
     */
    public Plane(Vec normal, Vec onPlane) {
        this(normal, normal.ip(onPlane));
        this.p = onPlane;

    }
    
    public Plane(Plane<Vec> plane){
        super(plane.linearSpace, plane.b);
        if(plane.hasAPoint()) p = plane.p;
    }
    
    public Vec normal(){
        return linearSpace.normals[0];
    }
    
    public double b(){
        return super.b.get(0);
    }
    
    /**
     * Is this halfspace above the given vector.  i.e. n dot v less than b
     * @param v
     * @return 
     */
    public boolean above(Vec v){
        return dist(v) > 0;
    }
    
    /**
     * The signed distance of a vector from this plane.  The distance will be
     * negative if v is below the plane, and positive otherwise.
     * @param v a vector in H
     * @return the signed distance of v from this plane.
     */
    public double dist(Vec v){
        return normal().ip(v) - b();
    }
      
    /**
     * Is this plane above or does it contain the given point.
     * @param v
     * @return 
     */
    public boolean aboveOrContains(Vec v) {
        return dist(v) <= tolerance;
    }
    
    /**
     * A point on this hyperplane.
     * @return 
     */
    @Override
    public Vec p(){
        if(p != null) return p;
        return normal().mult(b()/normal().normSq());
    }    
}
