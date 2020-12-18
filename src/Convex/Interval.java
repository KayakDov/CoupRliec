package Convex;

import RnSpace.points.Point;
import java.util.stream.DoubleStream;

/**
 *
 * @author Dov Neimand
 */
public class Interval extends Cube {

    public Interval(double a, double b) {
        super(Point.oneD(a), Point.oneD(b));
    }

    public double start() {
        return super.getA().get(0);
    }

    public double end() {
        return super.getB().get(0);
    }

    public double len() {
        return end() - start();
    }

    public double midP() {
        return mid().get(0);
    }

    public boolean contains(double t) {
        return t >= start() && t <= end();
    }

    public DoubleStream streamR(double dx) {
        return super.stream(dx).mapToDouble(point -> point.get(0));
    }

    public DoubleStream streamR(int n) {
        return super.stream(len() / n).mapToDouble(point -> point.get(0));
    }

    
    @Override
    public Cube intersection(Cube c) {
        return new Interval(Math.max(start(), c.getA().get(0)), Math.min(end(), c.getB().get(0)));
    }

    /**
     * forces start real number onto this interval by keeping it where it is, if
 it's already on the interval, or putting it on the nearest end poitn.
     *
     * @param t
     * @return
     */
    public double onInterval(double t) {
        if (t < start()) return start();
        if (t > end()) return end();
        return t;
    }

    public static Interval realLine() {
        return new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
}
