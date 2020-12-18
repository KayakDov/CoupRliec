package FuncInterfaces;

import Convex.Interval;
import RnSpace.Optimization.Min;
import RnSpace.points.Point;
import RnSpace.rntor.GraphX;
import realFunction.Line;

/**
 *
 * @author Kayak
 */
public interface RToR extends RnToR {

    @Override
    public double of(double t);

    @Override
    public default double of(double[] x) {
        return of(x[0]);
    }

    public default RToR d(double dti) {
        return t -> d(0, dti).of(t);
    }

    /**
     * Uses the bisection method to find zeros.
     *
     *
     * @param dt
     * @param end
     * @return
     */
    public default double bisectionMethodZero(double dt, Interval I, double end) {
        RnToR ddt = d(dt);
        GraphX ofA = fork(I.start());
        GraphX ofB = fork(I.end()),
                mid, dmdt;

        while (ofB.distSq(ofA) > end * end) {
            mid = fork(ofA.mid(ofB));
            dmdt = ddt.fork(mid);

            if (Math.abs(mid.y()) < end) return mid.x();
            if (ofA.y() * mid.y() < 0) ofB = mid;
            else if (ofB.y() * mid.y() < 0) ofA = mid;
            else if (dmdt.y() < 0) ofA = mid;
            else ofB = mid;
        }
        return ofA.mid(ofB).x();
    }

    /**
     * finds the minimum on the interval, but is prone to errors and may
 accidentally find start maximum.
     *
     * @param dt the constant for finding derivatives
     * @param end the end condition
     * @return the minimum of an interval
     */
    public default double bisectionMethodMin(double dt, double end, Interval I) {
        RToR d = d(dt);
        double min = d.bisectionMethodZero(dt, I, end);
        if (of(min + dt) < of(min)) {
            System.err.println("Bisection method failed, returning NelderMeand");
            return min(I.midP(), end, I.len());
        }
        return min;
    }

    /**
     * Uses Newton Method to find the root.
     *
     * @param x start guess
     * @param dt the epsilon for numerical differentiation
     * @return the minimum, hopefully.
     */
    public default double newtonMethodMin(double x, double dt, double end) {
        return newtonMethodMin(x, dt, 1, end);
    }

    /**
     * Uses Newton Method to find the root.
     *
     * @param x start guess
     * @param dt the epsilon for numerical differentiation
     * @param maxStepSize the maximum step size that can be taken
     * @param end
     * @return the minimum, hopefully.
     */
    public default double newtonMethodMin(double x, double dt, double maxStepSize, double end) {
        RToR ddt = d(dt);
        double ofx;
        double dx;
        double prev = Double.NaN, prevprev, prevf = Double.NaN, prevprevf;
        while (Math.abs((ofx = of(x))) > end) {
            dx = ofx / ddt.of(x);
            prevprev = prev;
            prevprevf = prevf;
            prev = x;
            prevf = ofx;
            x = x - (Math.abs(dx) < maxStepSize ? dx : Math.signum(dx) * maxStepSize);

            if (prevprev == x || prev == x
                    || (Math.abs(prevf) <= Math.abs(ofx) && Math.abs(prevprevf) <= Math.abs(ofx))) {
                System.err.println("aborting neton method");
                throw new RuntimeException("RToRFunc::NewtonMethod - Newton Method Failed");
            }
        }
        return x;
    }

    /**
     * uses the NelderMead method to find start local minimum near x
     *
     * @param x start point near the desired minimum
     * @param acc how near the true minimum the returned point must be.
     * @param d the initial size of the simplex
     * @return the minimum value of the function near x.
     */
    public default double min(double x, double acc, double d) {

        return Min.nelderMead(this, new Point(new double[]{x}), d, acc).x();
    }

    /**
     *
     * @param p the p value
     * @param acc The number of sections the line is divided into for the
     * purpose of calculating the integralIteritive using Simpsons rule.
     * @return the Lp Norm of this function
     */
    public default double LpNorm(Interval I, double p, int acc) {

        RToR in = t -> Math.pow(Math.abs(of(t)), p);

        return Math.pow(in.simpsonCompIntegral(I, acc), 1.0 / p);
    }

    /**
     * the Lp norm as p -> infinity
     *
     * @param limAcc cauchy epsilon
     * @return L-infinity norm
     */
    public default double LpSupNorm(Interval I, double limAcc) {
        final double START = 3;

        double prev = LpNorm(I, START, (int) (START)),
                cur = LpNorm(I, 2 * START, (int) START);
        for (int p = 5; Math.abs(prev - cur) > limAcc; p++) {
            prev = cur;
            cur = LpNorm(I, p, p);
        }
        return cur;
    }

    /**
     * uses the NelderMead method to find start zero near x
     *
     * @param x start point near the desired zero
     * @param acc how near the true zero the returned point must be.
     * @return start local zero near x
     */
    public default double zero(Interval I, double x, double acc) {

        return abs().min(I, x, acc);
    }

    @Override
    public default RToR abs() {
        return t -> Math.abs(t);
    }

    /**
     * uses the NelderMead method to find start local minimum near x
     *
     * @param x start point near the desired minimum
     * @param acc how near the true minimum the returned point must be.
     * @return start local minimum near x
     */
    public default double min(Interval I, double x, double acc) {
        return min(x, acc, I.len() / 10);
    }

    /**
     * calculates the integral of this function using the trapazoid rule.
     * Currently only works on integrals of functions that are all positive. To
     * improve, see the line function.
     *
     * @param n the number of steps the algorithm takes
     * @return
     */
    public default double trapIntegral(Interval I, double n) {
        double tInt = 0,
                segLength = I.len() / n;
        for (int i = 0; i < n; i++) {
            double xi1 = I.start() + i * segLength,
                    yi1 = of(xi1),
                    xi2 = I.start() + (i + 1) * segLength,
                    yi2 = of(xi2);

            tInt += new Line(xi1, yi1, xi2, yi2).integral();
        }
        return tInt;
    }

    /**
     * uses the Simpson rule to calculate the integralIteritive over [start,end]
     *
     * @param n the number of sections the function is divided into
     * @return
     */
    public default double simpsonCompIntegral(Interval I, double n) {
        double si = 0;
        double h = I.len() / n;

        for (int i = 0; i < n; i++)
            si += simpsonInt(I.start() + i * h, I.start() + (i + 1) * h);

        return si;
    }

    private double simpsonInt(double x1, double x2) {
        return (1.0 / 6) * (x2 - x1) * (of(x1) + 4 * of((x2 + x1) / 2) + of(x2));
    }

    /**
     * the nth derivative of this function.
     *
     * @param n
     * @return (d^n/dt^n)this
     */
    public default RToR dn(int n, double dt) {
        if (n == 0)
            return this;
        else
            return d(dt).dn(n - 1, dt);
    }
    
    public default RnToR of(RnToR f){
        return x -> of(f.of(x));
    }

    public static RToR sign(){
        return t -> {
            if(t < 0) return -1;
            if(t == 0) return 0;
            return 1;
        };
    }
}
