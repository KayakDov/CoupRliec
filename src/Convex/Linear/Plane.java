package Convex.Linear;

import Convex.HalfSpace;
import Convex.Linear.AffineSpace;
import Convex.Linear.LinearSpace;
import Convex.Polytope;
import Convex.Sphere;
import Matricies.Matrix;
import RnSpace.points.Point;
import java.util.Arrays;
import java.util.NoSuchElementException;
import listTools.Choose;

/**
 *
 * @author Dov Neimand
 */
public class Plane extends AffineSpace {

    /**
     * a vector normal to this plane. Not: this vector may not have length 1.
     *
     * @return
     */
    public Point normal() {
        return nullMatrix().row(0);
    }

    /**
     * The constructor for this class
     *
     * @param p a point on the plane
     * @param normal a vector normal to the plane
     */
    public Plane(Point p, Point normal) {
        super(new LinearSpace(new Point[]{normal}), p);

    }

    /**
     * A copy constructor
     *
     * @param p
     */
    public Plane(Plane p) {
        this(p.p(), p.normal());
    }

    /**
     * creates a plane from a set of points.
     *
     * @param x
     */
    public Plane(Point[] x) {
        this(Matrix.fromRows(x));
    }

    /**
     * creates a plane from a set of points.
     *
     * @param rowMatrix each row is a point on the surface of this plane
     * @param x a point on the plane
     */
    public Plane(Matrix rowMatrix, Point x) {
        this(null, null);
        p = x;

        if (rowMatrix.rows < rowMatrix.cols)
            throw new RuntimeException("Not enough points to form a " + (rowMatrix.cols - 1) + " dimensional hyperpalne.");
        if (rowMatrix.isSquare()) {
            linearSpace = new LinearSpace((rowMatrix).T().rowArray());
        } else {
            linearSpace = new LinearSpace(new Choose<>(rowMatrix.rowList(), rowMatrix.cols).chooseStream()
                    .map(pointSet -> normal(new Matrix(rowMatrix.cols)
                    .setRows(i -> pointSet.get(i))))
                    .reduce((p1, p2) -> p1.plus(p2)).get().T().rowArray());
        }
        b = Point.oneD(normal().dot(x));
    }

    /**
     * The constructor
     *
     * @param rowMatrix a list of points, at least as many as there are
     * dimensions.
     */
    public Plane(Matrix rowMatrix) {
        this(rowMatrix, rowMatrix.row(0));
    }

    /**
     * Gives the normal vector to the points in the matrix. This vector will not
     * have until length 1. the number of rows in the matrix should equal the
     * number of columns
     *
     * @param rowMatrix nullMatrix of points in a plane
     * @return the vector normal to the plane
     */
    public static Point normal(Matrix rowMatrix) {
        Matrix xRelative = new Matrix(rowMatrix.rows - 1, rowMatrix.cols);
        xRelative.setRows(i -> rowMatrix.row(i + 1).minus(rowMatrix.row(0)));
        return xRelative.crossProduct();
    }

    /**
     * returns a point on the plane.
     *
     * @return
     */
    public Point getP() {
        return new Point(p);
    }

    /**
     * Is the plane below the given point.
     *
     * @param x Is this point above the plane.
     * @return true if the point is above the plane.
     */
    public boolean below(Point x) {
        return normal().dot(x.minus(p)) > 0;
    }

    /**
     * the plane is above the point by a distance of more than epsilon
     *
     * @param p
     * @param epsilon
     * @return
     */
    public boolean above(Point p, double epsilon) {
        return !Plane.this.below(p) && !onPlane(p, epsilon);
    }

    /**
     * The plane is below the point by a distance of more than epsilon
     *
     * @param p
     * @param epsilon
     * @return
     */
    public boolean below(Point p, double epsilon) {
        return !Plane.this.above(p) && !onPlane(p, epsilon);
    }

    /**
     * is the given point on this plane.
     *
     * @param x
     * @param epsilon margin of error for double
     * @return true if the point is on the plane.
     */
    public boolean onPlane(Point x, double epsilon) {
        return Math.abs(x.minus(p).dot(normal().dir())) <= epsilon;
    }

    /**
     * Constructor
     *
     * @param normal a point normal to the plane
     * @param b the inner product of a point on the plane, and the normal vector
     */
    public Plane(Point normal, double b) {
        super(new Point[]{normal}, Point.oneD(b));
    }

    /**
     * is this plane above the point
     *
     * @param x the point that may be below the plane.
     * @return true if the point is below the plane.
     */
    public boolean above(Point x) {
        return nullMatrix().mult(x).get(0) <= b.get(0);
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
    public boolean equals(Plane plane, double epsilon) {
        return onPlane(plane.p, epsilon) && normal().sameDirection(plane.normal(), epsilon);
    }

    /**
     * the proj of a point onto this plane
     *
     * @param x the point to be projected
     * @return a new point on this plane
     */
    @Override
    public Point proj(Point x) {
        return x.minus(normal().dir().mult((x.minus(p)).dot(normal().dir())));
    }

    @Override
    public String toString() {
        if (dim() == 2)
            return normal().x() + "*(x-" + p().x() + ") + " + normal().y() + "*(y-" + p().y() + ") = 0";
        if (dim() == 3)
            return normal().x() + "*(x-" + p().x() + ") + " + normal().y() + "*(y-" + p().y() + ") + " + normal().z() + "*(z-" + p().z() + ")= 0";
        return "point " + p + " with normal " + normal();

    }

    /**
     * distance from a point to this plane. Note, the distance will be negative
     * if the point is below the plain.
     *
     * @param x the point in question
     * @return the distance to the point
     */
    @Override
    public double d(Point x) {
        return Math.abs((x.minus(p)).dot(normal().dir()));
    }

    /**
     * returns a new plane identical to this one, but with a flipped normal
     * vector.
     *
     * @return
     */
    public Plane flipNormal() {
        return new Plane(p, normal().mult(-1));
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
        return Math.abs(nullMatrix().mult(p).get(0) - b.get(0)) <= dim()*epsilon;
    }

    /**
     * This plane represented by the intersection of two halfspaces.
     *
     * @return a new polytope equal to this plane.
     */
    public Polytope asPolytope() {
        Polytope p = new Polytope();
        p.addFace(new HalfSpace(this));
        p.addFace(new HalfSpace(flipNormal()));
        return p;
    }



    /**
     * returns the intersection of this plane and a line.  If no such intersextion 
     * exists returns a point that is not a number.
     * This is much slower than the other line intersection method.
     * @param line a line
     * @return the point of intersection of this plane and a line, 
     * or an unreal point if there is no intersection. 
     */
    public Point lineIntersection(AffineSpace line) {
        try {
            return (nullMatrix().rowConcat(line.nullMatrix())).solve(b.concat(line.b));
        } catch (NoSuchElementException | ArithmeticException nsee) {
            return new Point(new double[]{Double.NaN});
        }
    }
    
        /**
     * returns the intersection of this plane and a line.If no such intersextion 
 exists returns a point that is not a number.
     * @param grad the gradient for the line
     * @param onLine some point on the line
     * @return the point of intersection of this plane and a line, 
     * or an unreal point if there is no intersection. 
     */
    public Point lineIntersection(Point grad, Point onLine) {
        double t = (b.get(0) - normal().dot(onLine))/(normal().dot(grad));
        return grad.mult(t).plus(onLine);
    }
    
    
}
