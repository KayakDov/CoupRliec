package SimplexMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;
import Matricies.PointD;
import java.util.Vector;
import java.util.function.Function;
import Hilbert.StrictlyConvexFunction;

/**
 *
 * @author dov
 */
public class Simplex extends ArrayList<GraphX> {

    public Simplex() {
    }

    public int getDim() {
        return size() - 1;
    }

    public GraphX getLast() {
        return get(size() - 1);
    }

    public GraphX last2nd() {
        return get(size() - 2);
    }

    /**
     *
     * @return the center of mass for all the points except the last one.
     */
    public GraphX centerOfMass() {

        GraphX last = get(size() - 1);
        remove(size() - 1);

        GraphX center = parallelStream().reduce((a, b) -> a.sum(b)).get();

        double totWeight = parallelStream().
                mapToDouble(GraphX::y).sum();

        add(last);

        return center.mult(1 / totWeight);
    }

    /*
     * I need to rewrite a faster sorting method.
     */
    public void orderVertices(StrictlyConvexFunction f) {

        Comparator<GraphX> comp = (p1, p2) -> (int) Math.signum(p1.y() - p2.y());
        sort(comp);

    }
    public final double EPSILON = 1e-7;

    private void changeAll(Function<GraphX, GraphX> change) {
        IntStream.range(0, size()).parallel().forEach(i -> set(i, change.apply(get(i))));
    }

    public void shrink() {
        changeAll(v -> v.reflectThrough(get(0), -.5).recalculate());
    }

    public final double DEFAULT_GROW_RATE = 1.5;

    public void grow() {
        grow(DEFAULT_GROW_RATE);
    }

    public void grow(double growRate) {
        changeAll(v -> v.reflectThrough(get(0), growRate).recalculate());
    }

    public void insertAndPop(StrictlyConvexFunction f, GraphX v) {
        int i = size() - 2;
        while (i >= 0 && v.y() < get(i).y()) {
            set(i + 1, get(i));
            i--;
        }
        set(i + 1, v);
    }

    private void swapPoints(int i, int j) {
        GraphX temp = get(i);
        set(i, get(j));
        set(j, temp);
    }

    public boolean isSmall(double d) {

        return stream().allMatch(x -> x.distSquared(get(0)) < d);
    }

    @Override
    public String toString() {
        String toString = "";
        for (int i = 0; i < size(); i++)
            toString += "(" + get(i).toString() + "), ";
        return toString;
    }

    private final static double SMALL_STEP = .5, BIG_STEP = 2;

    /**
     * Uses the Nelder Mead algorithm to find the minimum
     *
     * @param f the function to find the minimum of
     * @param s the starting simplex
     * @param end an end condition, a small number.
     * @return the final small simplex
     */
    public static Simplex nelderMead(StrictlyConvexFunction f, Simplex s, double end) {

        GraphX r, c, e, centerOfMass;

        double bestWeight;

        s.orderVertices(f);

        while (!s.isSmall(end)) {

            centerOfMass = s.centerOfMass();
            r = s.getLast().reflectThrough(centerOfMass, 1);
            c = s.getLast().reflectThrough(centerOfMass, SMALL_STEP);
            e = s.getLast().reflectThrough(centerOfMass, BIG_STEP);

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

}
