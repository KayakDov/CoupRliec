package Convex.LinearRn;

import Convex.HalfSpaceRn;
import Convex.PolyhedronRn;
import Hilbert.Vector;
import Matricies.Point;
import Matricies.PointD;
import java.util.NoSuchElementException;

/**
 * A hyperplane.
 * @author Dov Neimand
 */
public class RnPlane extends RnAffineSpace {

    /**
     * a vector normal to this plane. Not: this vector may not have length 1.
     *
     * @return
     */
    public Point normal() {
        return linearSpace.normals[0];
    }

    /**
     * The constructor for this class
     *
     * @param p a point on the plane
     * @param normal a vector normal to the plane
     */
    public RnPlane(Point p, Point normal) {
        super(new RnLinearSpace(new Point[]{normal.dir()}), p);

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
     * Is the plane below the given point.
     *
     * @param x Is this point above the plane.
     * @return true if the point is above the plane.
     */
    public boolean below(Point x) {
        return normal().ip(x) > b.get(0);
    }

    /**
     * the plane is above the point by a distance of more than epsilon
     *
     * @param p
     * @param epsilon
     * @return
     */
    public boolean above(Point p, double epsilon) {
        return normal().dot(p) < b.get(0) - epsilon;
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
     * is the given point on this plane.
     *
     * @param x
     * @param epsilon margin of error for double
     * @return true if the point is on the plane.
     */
    public boolean onPlane(Point x, double epsilon) {
        return Math.abs(x.dot(normal()) - b.get(0)) <= epsilon;
    }

    /**
     * Constructor
     * @param normal a point normal to the plane.  It should have magnitude 1.
     * @param b the inner product of a point on the plane, and the normal vector
     */
    public RnPlane(Point normal, double b) {
        super(new Point[]{normal}, PointD.oneD(b));
        if(Math.abs(normal.magnitude() - 1) > epsilon){
            double mag = normal.magnitude();
            this.b.set(0, b/mag);
            linearSpace.normals[0] = normal.mult(1/mag);
        }
    }

    /**
     * is this plane above the point
     *
     * @param x the point that may be below the plane.
     * @return true if the point is below the plane.
     */
    public boolean above(Point x) {
        return normal().dot(x) < b.get(0);
    }

    /**
     * Is this plane above or does it contain the given point.
     * @param x
     * @return 
     */
    public boolean aboveOrContains(Point x) {
        return normal().dot(x) <= b.get(0) + epsilon;
    }
    
    /**
     * Is this below above or does it contain the given point.
     * @param x
     * @return 
     */
    public boolean belowOrContains(Point x) {
        return normal().dot(x) >= b.get(0) - epsilon;
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
        return onPlane(plane.p(), epsilon)
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
     * returns a new plane identical to this one, but with a flipped normal
     * vector.
     *
     * @return
     */
    public RnPlane flipNormal() {
        return new RnPlane(p(), normal().mult(-1));
    }

    /**
     * Is there something very wrong with this plane, i.e. normal == 0
     *
     * @return
     */
    public boolean isBadPlane() {
        return normal().magnitude() == 0 || !normal().isReal();
    }

    private double epsilon = 1e-10;

    @Override
    public boolean hasElement(Point p) {
        return hasElement(p, epsilon);
    }

    @Override
    public boolean hasElement(Point p, double epsilon) {
        return Math.abs(normal().dot(p) - b.get(0)) <= dim() * epsilon;
    }

    /**
     * This plane represented by the intersection of two halfspaces.
     *
     * @return a new polytope equal to this plane.
     */
    public PolyhedronRn asPolytope() {
        PolyhedronRn p = new PolyhedronRn();
        p.addFace(new HalfSpaceRn(this));
        p.addFace(new HalfSpaceRn(flipNormal()));
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
            return (nullMatrix().rowConcat(line.nullMatrix())).solve(b.concat(line.b));
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
