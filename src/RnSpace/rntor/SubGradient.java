
package RnSpace.rntor;

import RnSpace.points.Point;
import Convex.Sphere;
import RnSpcae.RnToRmFunc.VectorField;

/**
 *
 * @author Dov Neimand
 */
public class SubGradient{// extends VectorField{
//    final double dr, end;
//    final RnToRFunc f;
//    
//
//    public SubGradient(RnToRFunc f, double dr, double end) {
//        super(f.getN());
//        this.dr = dr;
//        this.f = f;
//        this.end = end;
//    }
//    
//    
//    @Override
//    public Point of(double[] x) {
//        Point xp = new Point(x);
//        Sphere sphere = new Sphere(xp, dr);
//        Point min = sphere.minSurfacePoint(f.times(-1), end);
//        return min.minus(xp).mult(f.of(min) - f.of(x)).mult(1/(dr*dr));
//    }
//    

}
