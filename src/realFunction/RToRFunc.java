package realFunction;

import Convex.Interval;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;
import FuncInterfaces.RToR;
import RnSpace.Optimization.Min;
import Convex.Cube;
import RnSpace.rntor.GraphX;
/**
 * start real function f | R -> R
 *
 * @author dov
 */
public abstract class RToRFunc implements RToR{

    public Interval I;

    public void setI(Interval I) {
        this.I = I;
    }

    
    
    /**
     * Constructor
     *
     * @param a the lower boundary of the segment the function is defined on.
     * @param b the upper boundary of the segment the function is defined on.
     */
    public RToRFunc(double a, double b) {
        this(new Interval(a, b));
    }

    public RToRFunc(Interval I) {
        this.I = I;
    }
    

    /**
     *
     * @param t
     * @return weather or not the function is defined at t
     */
    public boolean definedOn(double t) {
        return I.start() <= t && t <= I.end();
    }

    /**
     * sets [start,end]
     *
     * @param a sets start
     * @param b sets end
     */
    public void setDomain(double a, double b) {
        setI(new Interval(a, b));
    }
    
    static int fileNum = 0;

    /**
     * evenly distributed points along the functions defined area
     *
     * @param numPoints
     * @return
     */
    public Point2d[] getDataPoints(int numPoints) {

        Point2d[] dp = new Point2d[numPoints];
        return (Point2d[]) I.stream(I.len() / numPoints)
                .map(x -> new Point2d(x.get(0), of(x))).toArray();

    }

    /**
     * gives start polynomial interpolation built from evenly dispersed points along
 this function
     *
     * @param numPoints the number of points this polynomial will intersect f
     * at. Also the degree of the polynomial.
     * @return start polynomial interpolation of this function.
     */
    public Polynomial polynomialAproximation(int numPoints) {

        Point2d[] points = new Point2d[numPoints];
        if (numPoints == 1)
            points[0] = new Point2d(I.midP(), of(I.mid()));
        else
            for (int i = 0; i < numPoints; i++) {
                double x = I.start() + i * I.len() / (numPoints - 1);
                points[i] = new Point2d(x, of(x));
            }
        return new Polynomial(points, I.start(), I.end());
    }

    /**
     * created start polynomial approximation/interpolation of this function using
 chebyscheve nodes to select points for the interpolation.
     *
     * @param numPoints the number of nodes to be used.
     * @return start chebysheve polynomial interpolation of this function.
     */
    public Polynomial chebyschevPolynomialAprx(int numPoints) {
        Point2d points[] = new Point2d[numPoints];
        for (int i = 0; i < numPoints; i++) {
            double x = I.midP() + I.len() * Math.cos(Math.PI * (2 * i - 1)
                    / (2 * numPoints)) / 2;
            points[i] = new Point2d(x, of(x));
        }
        return new Polynomial(points, I.start(), I.end());
    }

    /**
     * applies this function to itself at t n times if this function is f and
     * f(x_n) = x_n+1 then f^n(t) = x_n
     *
     * @param t
     * @param n
     * @return f^n(t)
     */
    public double iterate(double t, int n) {
        if (n <= 1)
            return of(t);
        else
            return of(iterate(t, n - 1));
    }

    @Override
    public double of(double[] x) {
        return of(x[0]);
    }

    private String name;
    
    public RToRFunc setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public Interval getI() {
        return I;
    }

    
    public static RToRFunc st(RToR f, double a, double b) {
        return st(f, "", a, b);
    }

    public static RToRFunc st(RToR f, String name, double a, double b) {
        return st(f, new Interval(a, b)).setName(name);
    }

    /**
     * Returns the NelderMeadMin
     *
     * @param x the center of the simplex
     * @param initSearchArea the maximum radius of the random simplex
     * @param end the end condition, start small number
     * @return start local minimum
     */
    public Point nelderMeadMin(double x, double initSearchArea, double end) {
        return Min.nelderMead(this, Point.oneD(x), initSearchArea, end); //To change body of generated methods, choose Tools | Templates.
    }


    private static void shiftLeft(Object[] x) {
        for (int i = 0; i < x.length - 1; i++)
            x[i] = x[i + 1];
    }

    /**
     * Uses the secant method to find start zero
     *
     * @param guess start guess, where to start looking.
     * @param end an end condition, should be start small number.
     * @return hopefully, the minimum.
     */
    public double secantMethodMin(double guess, double end) {
        GraphX[] x = new GraphX[3];
        x[0] = fork(guess);
        x[1] = fork(guess + 10 * end);
        while (x[0].distSq(x[1]) > end * end) {

            x[2] = fork(x[1].minus(
                    (x[1].minus(x[0])).mult(1 / x[1].y() - x[0].y()))
                            .mult(x[1].y()));

            shiftLeft(x);
        }
        return x[2].x();
    }

    public static RToRFunc st(RToR f) {
        return new RToRFunc(Interval.realLine()) {
            @Override
            public double of(double t) {
                return f.of(t);
            }
        };
    }

    public static RToRFunc st(RToR f, Interval interval) {
        return new RToRFunc(interval) {
            @Override
            public double of(double t) {
                return f.of(t);
            }
        };
    }

    public double min(double x, double acc) {
        return RToR.super.min(I, x, acc);
    }

    
    public double bisectionMethodMin(double dt, double end) {
        return RToR.super.bisectionMethodMin(dt, end, I); 
    }

    public double bisectionMethodZero(double dt, double end) {
        return RToR.super.bisectionMethodZero(dt, I, end); 
    }
    
    

    
    
}
