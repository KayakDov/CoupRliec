package Convex.LinearRn;

import Convex.RnPolyhedron;
import Hilbert.AffineSpace;
import Hilbert.HalfSpace;
import Hilbert.Plane;
import Matricies.Matrix;
import Matricies.Point;
import Matricies.PointD;
import java.util.NoSuchElementException;

/**
 * A hyperplane.
 * @author Dov Neimand
 */
public class RnPlane extends Plane<Point> {


    /**
     * The constructor for this class
     *
     * @param p a point on the plane
     * @param normal a vector normal to the plane
     */
    public RnPlane(Point p, Point normal) {
        super(normal, normal);

    }

    public RnPlane(Plane<Point> plane){
        super(plane);
    }
    /**
     * A copy constructor
     *
     * @param p
     */
    public RnPlane(RnPlane p) {
        this(p.p(), p.normal());
    }

    /**
     * returns a point on the plane.
     *
     * @return
     */
    public Point getP() {
        return p();
    }

    /**
     * The plane is below the point by a distance of more than epsilon
     *
     * @param p
     * @param epsilon
     * @return
     */
    public boolean below(Point p, double epsilon) {
        return normal().dot(p) > b.get(0) + epsilon;
    }


    /**
     * Constructor
     * @param normal a point normal to the plane.  It should have magnitude 1.
     * @param b the inner product of a point on the plane, and the normal vector
     */
    public RnPlane(Point normal, double b) {
        super(normal, b);
    }

    
    /**
     * Is this below above or does it contain the given point.
     * @param x
     * @return 
     */
    public boolean belowOrContains(Point x) {
        return normal().dot(x) >= b.get(0) - tolerance;
    }
    
    /**
     * Is this plane above or does it contain the the given point.
     * @param x
     * @param epsilon
     * @return 
     */
    public boolean aboveOrContains(Point x, double epsilon) {
        return normal().dot(x) <= b.get(0) + epsilon;
    }

    /**
     * the number of dimensions of the points in this plane
     *
     * @return
     */
    public int dim() {
        return p().dim();
    }

    /**
     * Are these two planes equal
     *
     * @param plane the other plane this one is being compared to
     * @param epsilon margin of error
     * @return true if the two planes are equal.
     */
    public boolean equals(RnPlane plane, double epsilon) {
        return hasElement(plane.p(), epsilon)
                && normal().equals(plane.normal());
    }

    /**
     * the proj of a point onto this plane
     *
     * @param x the point to be projected
     * @return a new point on this plane
     */
    @Override
    public Point proj(Point x) {
        return x.minus(normal().mult((x.minus(p())).dot(normal())));
    }

    public static boolean printEasyRead = true;

    @Override
    public String toString() {
        if (printEasyRead) {
            if (dim() == 2)
                return normal().x() + "*(x-" + p().x() + ") + " + normal().y() + "*(y-" + p().y() + ") = 0";
            if (dim() == 3)
                return normal().x() + "*(x-" + p().x() + ") + " + normal().y() + "*(y-" + p().y() + ") + " + normal().z() + "*(z-" + p().z() + ")= 0";
        }
        return toStringMultiDim();

    }

    public String toStringMultiDim() {
        return "point " + p + " with normal " + normal();
    }

    /**
     * distance from a point to this plane. Note, the distance will be negative
     * if the point is below the plain.
     *
     * @param x the point in question
     * @return the distance to the point
     */
    public double d(Point x) {
        return x.d(proj(x));
    }


    /**
     * Is there something very wrong with this plane, i.e. normal == 0
     *
     * @return
     */
    public boolean isBadPlane() {
        return normal().magnitude() == 0 || !normal().isReal();
    }



    @Override
    public boolean hasElement(Point p, double epsilon) {
        return Math.abs(normal().dot(p) - b.get(0)) <= dim() * epsilon;
    }

    /**
     * This plane represented by the intersection of two halfspaces.
     *
     * @param as a plane that intersects with this one.
     * @return a new polytope equal to this plane.
     */
    public RnAffineSpace intersection(RnPlane as) {
        return new RnAffineSpace(new RnPlane[]{this, as});
    }

    /**
     * The Polytope that is the halfspace defined by this plane.
     * @return 
     */
    public RnPolyhedron asPolytope() {
        RnPolyhedron p = new RnPolyhedron();
        p.addFace(new HalfSpace<Point>(this));
        p.addFace(new HalfSpace<Point>(flipNormal()));
        return p;
    }

    /**
     * returns the intersection of this plane and a line. If no such
     * intersextion exists returns a point that is not a number. This is much
     * slower than the other line intersection method.
     *
     * @param line a line
     * @return the point of intersection of this plane and a line, or an unreal
     * point if there is no intersection.
     */
    public Point lineIntersection(RnAffineSpace line) {
        try {
            Point[] rows = new Point[nullMatrixRows().length + line.nullMatrixRows().length];
            System.arraycopy(nullMatrixRows(), 0, rows, 0, nullMatrixRows().length);
            System.arraycopy(line.nullMatrixRows(), 0, rows, nullMatrixRows().length, line.nullMatrixRows().length);
            
            
            return (Matrix.fromRows(rows)).solve(b.concat(line.b));
        } catch (NoSuchElementException | ArithmeticException nsee) {
            return new PointD(new double[]{Double.NaN});
        }
    }

    /**
     * returns the intersection of this plane and a line.If no such intersextion
     * exists returns a point that is not a number.
     *
     * @param grad the gradient for the line
     * @param onLine some point on the line
     * @return the point of intersection of this plane and a line, or an unreal
     * point if there is no intersection.
     */
    public Point lineIntersection(Point grad, Point onLine) {
        double t = (b.get(0) - normal().dot(onLine)) / (normal().dot(grad));
        return grad.mult(t).plus(onLine);
    }

}
