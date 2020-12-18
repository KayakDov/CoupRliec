package Convex.indicatorProjection;

import Convex.Linear.Plane;
import Convex.Sphere;
import listTools.Pair1T;
import RnSpace.points.Point;
import RnSpace.points.Point;

/**
 *
 * @author Dov Neimand
 */
/**
 * This class keeps track where a point has come from and where it would be
 * going if it didn't hit a surface.
 */
public class InOut extends Pair1T<Point> {

    /**
     * the constructor
     *
     * @param in a point definitely inside the convex set
     */
    public InOut(Point in) {
        super(in, null);
    }
    
    /**
     * A copy constructor
     * @param io the io being copied.
     */
    public InOut(InOut io){
        this(io.in(), io.out());
    }

    /**
     * the constructor
     *
     * @param in a point definitely inside the convex set
     * @param out a point outside of the convex set
     */
    public InOut(Point in, Point out) {
        super(in, out);
    }

    /**
     * the in location of x, as it's been last updated
     *
     * @return
     */
    public Point in() {
        return new Point(l);
    }

    /**
     * A point outside the convex hull near x
     *
     * @return
     */
    public Point out() {
        if (r == null)
            throw new NullPointerException("no out point has been declared.");
        return new Point(r);
    }

    /**
     * A check if a point outside the convex hull near x is known.
     *
     * @return
     */
    public boolean hasOutPoint() {
        return r != null;
    }

    public int dim(){
        return l.dim();
    }
    
    /**
     * the midpoint between the in point and the out point
     * @return 
     */
    public Point mid(){
        return l.mid(r);
    }
    
    /**
     * the smallest sphere that contains the two points
     * @return 
     */
    public Sphere smallestContaining(){
        return new Sphere(mid(), mid().d(l));
    }
    
    public Plane seperating(){
        return new Plane(mid(), out().minus(l));
    }
    
    /**
     * creates an in out point with the out part twice as far away
     * @return 
     */
    public InOut bigger(){
        return new InOut(l, l.plus(r.minus(l).mult(2)));
    }
}
