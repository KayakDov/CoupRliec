package Convex;

import Convex.ConvexSet;
import DiscreteMath.ZToRFunc;
import RnSpace.Optimization.Min;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;
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
        this(new Point(dim), r);
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
        this(new Point(center), r);
    }

    /**
     * The dimension the sphere is in, not the surface manifold dimension
     *
     * @return
     */
    public int dim() {
        return center.dim();
    }

    public Point surface(double[] theta) {
        return surface(new Point(theta));
    }

    /**
     * Takes in an angle, or set of angles theta in R^{n-1} and returns the
     * corresponding point on the surface of the sphere
     *
     * @param theta a point in R^{n-1}
     * @return a point on the surface of the sphere in R^n
     */
    public Point surface(Point theta) {

        if (theta.dim() == dim() - 1) {

            Point surface = new Point(dim()).setAll(i -> r * ZToRFunc.st(0, i, j -> sin(theta.get(j))).product());
            IntStream.range(0, dim() - 1).forEach(i -> surface.set(i, surface.get(i) * cos(theta.get(i))));

            return surface.plus(center);
        }
        if (theta.dim() == dim()) {
            return theta.minus(center).dir().mult(r).plus(center);
        }
        throw new RuntimeException("Sphere::surface theta.dim = " + theta.dim() + " but should = " + (dim() - 1));

    }

    /**
     * the minimum point on the surface of the sphere, uses nelder mead
     *
     * @param f
     * @param end
     * @return
     */
    public Point minSurfacePoint(RnToRFunc f, double end) {
        RnToRFunc fS = RnToRFunc.st(theta -> f.of(surface(theta)), f.getN() - 1);
        return surface(Min.nelderMead(fS, new Point(fS.getN()), 2 * PI, end));
    }

    @Override
    public boolean hasElement(Point p) {
        return p.d(center) <= r;
    }

    @Override
    public Point proj(Point p) {
        if (hasElement(p)) return new Point(p);
        Point cToP = p.minus(center);
        cToP = cToP.mult(1 / cToP.magnitude()).mult(r);
        return cToP.plus(center);
    }

    private static ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * Generates a random point on the surface of the sphere.
     */
    public Point randomSurfacePoint() {
        Point angle = new Point(dim() - 1).setAll(i -> rand.nextDouble()*PI);
        angle.set(angle.dim() - 1, angle.get(angle.dim() - 1)*2);
        return surface(angle);
    }
}
