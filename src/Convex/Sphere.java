package Convex;

import Convex.ConvexSet;
import Matricies.Point;
import Matricies.PointD;
import java.util.stream.IntStream;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Dov Neimand
 */
public class Sphere implements ConvexSet {

    final private Point center;
    final private double r;

    /**
     * Constructor for unit sphere
     *
     * @param dim the dimension the sphere is in
     */
    public Sphere(int dim) {
        this(dim, 1);
    }

    /**
     * a sphere centered at the origin
     *
     * @param dim the dimension of the sphere
     * @param r the radius of the sphere
     */
    public Sphere(int dim, double r) {
        this(new PointD(dim), r);
    }

    /**
     * constructor
     *
     * @param center
     * @param r
     */
    public Sphere(Point center, double r) {
        this.center = center;
        this.r = r;
    }

    /**
     * constructor
     *
     * @param center the point at the center of the sphere
     * @param r
     */
    public Sphere(double[] center, double r) {
        this(new PointD(center), r);
    }

    /**
     * The dimension the sphere is in, not the surface manifold dimension
     *
     * @return
     */
    public int dim() {
        return center.dim();
    }

    @Override
    public boolean hasElement(Point p) {
        return p.d(center) <= r;
    }

    @Override
    public Point proj(Point p) {
        if (hasElement(p)) return p;
        Point cToP = p.minus(center);
        cToP = cToP.mult(1 / cToP.magnitude()).mult(r);
        return cToP.plus(center);
    }

    private static ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * Generates a random point on the surface of the sphere.
     */
}
