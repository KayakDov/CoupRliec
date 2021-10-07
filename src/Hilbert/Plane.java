package Hilbert;

import Matricies.Point;
import Matricies.PointD;
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
     * @param b Every point on this plane, x, is a solution to \<normal, x\> = b
     */
    public Plane(Vec normal, double b) {
        super(
                (Vec[]) (Array.newInstance(normal.getClass(),1)), 
                PointD.oneD(b)
        );
        linearSpace.normals[0] = normal;
    }
    
    /**
     * The constructor for this class
     *
     * @param onPlane a vector on the plane
     * @param normal a vector normal to the plane
     */
    public Plane(Vec onPlane, Vec normal) {
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
     * Is this halfspace above the given vector.  i.e. \<n, v\> \< b
     * @param v
     * @return 
     */
    public boolean above(Vec v){
        return normal().ip(v) < b();
    }
    
    
    /**
     * the plane is above the point by a distance of more than epsilon
     *
     * @param p
     * @param epsilon
     * @return
     */
    public boolean above(Vec p, double epsilon) {
        return normal().ip(p) < b() - epsilon;
    }
    
    /**
     * Is this plane above or does it contain the given point.
     * @param x
     * @return 
     */
    public boolean aboveOrContains(Vec x) {
        return normal().ip(x) <= b() + tolerance;
    }
    
    /**
     * Is this plane below the given point, i.e. \<n, v\> \> b.
     * @param v
     * @return 
     */
    public boolean below(Vec v){
        return normal().ip(v) > b();
    }
    
    public Plane<Vec> flipNormal(){
        return new Plane<>(normal().mult(-1), -b());
    }

    @Override
    public Vec proj(Vec x) {
        return x.dif(normal().mult((x.dif(p())).ip(normal())));
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
