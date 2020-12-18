package Convex.indicatorProjection;

import Convex.Sphere;
import Matricies.Matrix;
import RnSpace.curves.Curve;
import RnSpace.curves.JoinedLines;
import RnSpace.curves.Line;
import RnSpace.points.Point;

/**
 *
 * @author Dov Neimand
 */
public class Walker {

    private ConvexSetIndicator set;
    private Point y;

    /**
     * The number of dimensions to be walked through
     *
     * @return
     */
    public int dim() {
        return y.dim();
    }

    /**
     * The constructor
     *
     * @param set the set we want to walk through.
     * @param y the point projected onto the set
     */
    public Walker(ConvexSetIndicator set, Point y) {
        this.set = set;
        this.y = y;
    }

    /**
     * a step sideways, along a sphere centered at the point being projected,
 toward the proj onto the bounding convex polytope.
     *
     * @param from the location of the point moving sideways
     * @return the new location of the point
     */
    public Point side(Point from) {
        if(set.bounding.isEmpty()) return from;
        
        
        Sphere sphere = new Sphere(y, from.d(y));
        Point to = sphere.surface(set.bounding.proj(y));
        Line straight = new Line(from, to);

        return curve(Curve.st(t -> sphere.surface(straight.of(t)))).in();
    }

    /**
     * A point moves along the curve until the curve leaves the convex set. This
     * method should really only be called by the other curve step methods. The
     * a and be parameters here may be smaller than the interval endpoints of
     * the domain of g, to impose on it a smaller domain.
     *
     * @param g the curve the point moves along
     * @param a the beginning of the domain of the point
     * @param b the end of the domain of the point
     * @return the new location of the point
     */
    private InOut curve(Curve g, double a, double b) {

        if (b - a < set.dt())
            return new InOut(g.of(a), g.of(b));

        double mid = (a + b) / 2;
        return set.hasElement(g.of(mid))
                ? curve(g, mid, b)
                : curve(g, a, mid);
    }

    /**
     * A point moves along a curve until it leaves the set. This method, besides
     * finding the final end point of the curve, will also keep track of a point
     * in the direction the final point came from, and a point outside the
     * surface when the curve intersects the edge of the surface.
     *
     * @param g the curve
     * @param x the point being moved.
     * @return The final point, a point outside the curve where relevant, and a
     * point before the final point.
     */
    private InOut curve(Curve g) {

        if (set.hasElement(g.ofB()))
            return new InOut(g.ofB());
        if (!set.hasElement(g.of(g.a() + set.dt())))
            return new InOut(g.ofA(), g.of(g.a() + set.dt()));
        InOut curve = curve(g, g.a(), g.b());

        return curve;

    }

    /**
     * A step toward the proj of y onto the bounding polytope.If the
 function is found during this step to not be convex, or there were errors
 in the location of the support plane, and the point completes its step
 forward not on the surface of the convex set, then the bounding support
 planes will be cleared and the algorithm will start from scratch.
     *
     * @param x the location of x
     * @return the final place of the point.
     */
    public InOut forward(Point x) {
        Point proj = set.bounding.proj(y);

        if (set.hasElement(proj)) {
            set.bounding.clearFaces();//TODO:  see if there's a way to clear just one face instead of all of them?
            return forward(proj);
        }

        return curve(new Line(x, proj));
    }

    /**
     * A curve step along the surface of the smallest spehere from x.in to x.out.
     * @param onTheWay a point halfway between x.in and x.out projected onto the sphere
     * that the path will pass through.
     * @param x a point in, and out, of the convex set.
     * @return where the geodesic curve intersects the convex set.
     */
    public InOut geodesic(Point onTheWay, InOut x) {
        
        Sphere sphere = x.smallestContaining();
        onTheWay = sphere.surface(x.seperating().proj(onTheWay));
        JoinedLines jl = new JoinedLines(Matrix.fromRows(new Point[]{x.in(), onTheWay, x.out()}));
       
        return curve(Curve.st(t -> sphere.surface(jl.of(t))));
    }

    /**
     * A random shortest-path curve along the surface of the minimal sphere from 
     * x.in to x.out
     * @param x a point in, and out of the convex shape.
     * @return the point of intersectionPoint of the path.
     */
    public InOut randomGeodesic(InOut x) {
        Point random = x.smallestContaining().randomSurfacePoint();
        return geodesic(random, x);
    }
    
    /**
     * 
     * @param dt the new value
     * @return 
     */
    public void setDt(double dt){
        set.setDt(dt);
    }
    public double getDt(){
        return set.dt();
    }
    

}
