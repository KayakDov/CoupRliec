package RnSpace.Optimization;

import FuncInterfaces.RToR;
import FuncInterfaces.RnToR;
import RnSpace.points.Point;
import RnSpace.points.Simplex;
import RnSpace.rntor.GraphX;
import RnSpace.rntor.RnToRFunc;
import java.util.Arrays;
import java.util.stream.IntStream;
import realFunction.RToRFunc;

/**
 * TODO: change graphX points back to regular points, or reduce f's as
 * parameters
 *
 * @author Kayak
 */
public class Min extends Point {

    public Min(Point p) {
        super(p);
    }

    public boolean isMin(RnToR f, double dt) {

        return IntStream.range(0, dim()).allMatch(i
                -> f.of(shift(i, dt)) >= f.of(this)
                && f.of(shift(i, -dt)) >= f.of(this));
    }

    public boolean isMax(RnToRFunc f, double dt) {
        return !IntStream.range(0, dim()).parallel().anyMatch(i
                -> f.of(shift(i, dt).array()) > f.of(this)
                || f.of(shift(i, -dt).array()) > f.of(this));
    }

    private final static double SMALL_STEP = .5, BIG_STEP = 2;

    /**
     * Uses Nelder Mead algorithm to find the minimum.
     *
     * @param f the function to find a minimum of
     * @param x a point near which the simplex will start
     * @param initSearchArea effects the size of the starting simplex
     * @param end the end condition, should be a small number
     * @return the minimum
     */
    public static Min nelderMead(RnToR f, Point x, double initSearchArea, double end) {

        return new Min(nelderMead(f, Simplex.Random(x, initSearchArea, f), end).get(0));
    }

    /**
     * Uses the Nelder Mead algorithm to find the minimum
     *
     * @param f the function to find the minimum of
     * @param s the starting simplex
     * @param end an end condition, a small number.
     * @return the final small simplex
     */
    public static Simplex nelderMead(RnToR f, Simplex s, double end) {

        GraphX r, c, e, centerOfMass;

        double bestWeight;

        s.orderVertices(f);

        while (!s.isSmall(end)) {

            centerOfMass = s.centerOfMass();
            r = new GraphX(f, s.getLast().reflectThrough(centerOfMass, 1));
            c = new GraphX(f, s.getLast().reflectThrough(centerOfMass, SMALL_STEP));
            e = new GraphX(f, s.getLast().reflectThrough(centerOfMass, BIG_STEP));

            bestWeight = s.get(0).y();

            if (r.y() < s.last2nd().y() && r.y() >= bestWeight)
                s.insertAndPop(f, r);
            else if (r.y() < bestWeight)
                s.insertAndPop(f, e.y() < r.y() ? e : r);
            else if (c.y() < s.getLast().y())
                s.insertAndPop(f, c);
            else {
                s.shrink();
                s.orderVertices(f);
            }
        }

        return s;
    }

    /**
     * A generic descent method
     *
     * @param descentRule a rule for how algorithm descends
     * @param x the starting point
     * @return the minimum of the function if it exists
     */
    public static Min descentMethod(DescentRule descentRule, Point x) {
        
        while (!descentRule.end(x)){
            System.out.println(x);
            x = descentRule.of(x);
        }

        return new Min(x);

    }

    /**
     * Uses coordinate descent to find a local min.
     *
     * @param f the function to find the minimum of
     * @param x a guess.
     * @param end a small number that determines when the final solution is
     * found.
     * @param dt increment of differentiation
     * @return a local minimum
     */
    public static Min coordinateDescent(RnToRFunc f, Point x, double end, double dt) {
        return descentMethod(new CoordinateDescentRule(f, end, dt), x);
    }

    /**
     * This uses the gradient descent algorithm without a line search.If it's
     * now working, try making the end condition bigger or the dt value smaller.
     * alternatively, overwrite the d(i, dt) function to improve accuracy.
     *
     * @param f The function to find the minimum of.
     * @param start starting guess
     * @param end a small number representing the end condition
     * @param dt a small number to calculate derivatives
     * @return the minimum of the function.
     */
    public static Min gradDescentNoLineSearch(RnToR f, Point start, double end, double dt) {
        return descentMethod(new GradDescentNoLineSearch(f, end, dt), start);
    }

    /**
     * Uses gradient descent to find a local min.Don't forget to overwrite d(i,
     * dt) for much greater efficiency.
     *
     * @param f the function to find the minimum of
     * @param start a guess.
     * @param end a small number that determines when the final solution is
     * found.
     * @param dt a very small number to find derrivitive.
     * @return a local minimum
     */
    public static Min gradientDescentLineSearch(RnToRFunc f, Point start, double end, double dt) {
        return descentMethod(new GradDescentLineSearch(f, end, dt), start);
    }

    /**
     * This class is used to calculate penalty function
     */
    private static class Phi implements RnToR {

        public boolean checkConstraints(Point x) {
            return !Arrays.stream(g).parallel().anyMatch(g -> g.of(x) > 0);
        }

        private double r;
        private RnToR f, g[];
        private final RToR penalty;

        public Phi(double r, RnToR f, RnToR[] g, RToR penalty) {
            this.r = r;
            this.f = f;
            this.g = g;
            this.penalty = penalty;
        }

        public void setR(double r) {
            this.r = r;
        }

        public double getR() {
            return r;
        }

        @Override
        public double of(double[] x) {

            Point px = f.fork(x);
            return Arrays.stream(g).parallel()
                    .mapToDouble(g -> penalty.of(g.of(x)) * r).sum() + px.y();
        }

    }

    /**
     *
     * @param f the function to maximize
     * @param g constraints
     * @param x an initial guess, should meet the constraints
     * @param dt differentiation increment
     * @param end the end condition
     * @param usesSubGrad should the method use subgradient or gradient
     * @return the minimum
     */
    public static Min interiorPenaltyFunctionMethod(RnToRFunc f, RnToRFunc g, Point x, double dt, double end, boolean usesSubGrad) {
        return penaltyFunctionMethod(new RnToRFunc[]{g}, new GradDescentNoLineSearch(f, end, dt), RToRFunc.st(t -> -1 / t), x, 10, .5, dt);
    }

    /**
     *
     * @param f the function to maximize
     * @param g constraints
     * @param x an initial guess, should meet the constraints
     * @param dt differentiation increment
     * @param end the end condition
     * @return the minimum
     */
    public static Min interiorPenaltyFunctionMethod(RnToRFunc f, RnToRFunc[] g, Point x, double dt, double end) {
        return penaltyFunctionMethod(g, new NewtonDescent(f, end, dt), RToRFunc.st(t -> -1 / t), x, 10, .5, dt);
    }

    /**
     *
     * @param f the function to maximize
     * @param g constraints
     * @param x an initial guess, should not meet the constraints
     * @param dt differentiation increment
     * @param end the end condition
     * @param usesSubGrad should the method use subGradient or gradient
     * @return the minimum
     */
    public static Min exteriorPenaltyFunctionMethod(RnToRFunc f, RnToRFunc[] g, Point x, double dt, double end) {
        return penaltyFunctionMethod(g, new GradDescentNoLineSearch(f, end, dt), RToRFunc.st(t -> Math.max(0, t) * t), x, 1, 2, dt);
    }

    /**
     *
     * @param g constraints
     * @param dr the policy for choosing the next iteration
     * @param penalty the penalty of being outside the constraints, will take in
     * each of the constraints functions, be multiplied by r, and then added
     * together
     * @param x the initial guess
     * @param r a constant to multiply the penalty by
     * @param changeR how much should r be multiplied by with each new iteration
     * @param dt the differentiation increment
     * @param end the end condition
     * @return a minimum
     */
    public static Min penaltyFunctionMethod(RnToR[] g, DescentRule dr, RToR penalty, Point x, double r, double changeR, double dt) {
        RnToR f = dr.getF();
        Phi phi = new Phi(r, f, g, penalty);
        Min nextX = new Min(x);

        do {
            dr.setF(phi);
            x = nextX;
            nextX = Min.descentMethod(dr, x);
//            nextX = Min.gradDescentNoLineSearch(phi, x, end, dt);

            phi.setR(phi.getR() * changeR);

        } while (!nextX.isMin(f, dt) && x.distSq(nextX) > dr.end * dr.end);

        //check if it satisifies constraints
        return nextX;

    }

    ////////Methods from Nonlinear Programing, Dimitiri Bertsekas/////////////
    public static Min newtonsDescentMethod(RnToRFunc f, Point x, double end, double dt) {
        return descentMethod(new NewtonDescent(f, end, dt), x);
    }

}
