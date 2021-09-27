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
        return linearSpace.nomralVectors[0];
    }
    
    public double b(){
        return super.b.get(0);
    }
    
}
