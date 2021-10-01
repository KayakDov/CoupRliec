package Hilbert;

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
     * @param normals the vector normal to this plane
     * @param b Every point on this plane, x, is a solution to \<normal, x\> = b
     */
    public Plane(Vec normals, double b) {
        super(
                (Vec[]) (Array.newInstance(normals.getClass(),1)), 
                PointD.oneD(b)
        );
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
    public Vec p(){
        return normal().mult(b()/normal().normSq());
    }
}
