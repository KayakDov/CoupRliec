package RnSpace.points;

import FuncInterfaces.RnToR;
import RnSpace.rntor.RnToRFunc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;
import FuncInterfaces.ZToRn;
import RnSpace.rntor.GraphX;
import java.util.Vector;
import listTools.InsertionSort;
import listTools.MergeSort;

/**
 *
 * @author dov
 */
public class Simplex extends Vector<GraphX> {

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
        Point center = new Point(get(0).dim());
        GraphX last = remove(size() - 1);

        parallelStream().forEach(x -> center.addToMe(x.mult(x.y())));

        double totWeight = parallelStream().
                mapToDouble(GraphX::y).sum();

        add(last);
        center.multMe(1 / totWeight);

        return new GraphX(get(0).getF(), center);
    }

    /*
     * I need to rewrite a faster sorting method.
     */
    public void orderVertices(RnToR f) {

        Comparator<GraphX> comp = (p1, p2) -> (int) Math.signum(p1.y() - p2.y());
        sort(comp);

    }

    public Simplex setAll(ZToRn set, RnToR f) {
        GraphX[] array = new GraphX[size()];
        Arrays.parallelSetAll(array, i -> new GraphX(f, set.of(i)));
        clear();
        Collections.addAll(this, array);
        return this;
    }

    public final double EPSILON = 1e-7;

    public void shrink() {
        setAll(i -> get(i).reflectThrough(get(0), -.5), get(0).getF());
    }

    public final double DEFAULT_GROW_RATE = 1.5;

    public void grow() {
        grow(DEFAULT_GROW_RATE);
    }

    public void grow(double growRate) {
        setAll(i -> get(i).reflectThrough(get(0), growRate), get(0).getF());
    }

    public void insertAndPop(RnToR f, GraphX v) {
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

        return stream().allMatch(x -> x.d(get(0)) < d);
    }

    @Override
    public String toString() {
        String toString = "";
        for (int i = 0; i < size(); i++)
            toString += "(" + get(i).toString() + "), ";
        return toString;
    }

    /**
     *
     * You'll need code like this t make it work.Simplex s = new Simplex(); for
     * (int i = 0; i < getN() + 1; i++) s.add(new LocallVert());
     *
     *
     * @param center
     * @param r
     * @param f
     * @return
     */
    public static Simplex Random(Point center, double r, RnToR f) {
        Simplex randomSimp = new Simplex();
        IntStream.range(0, center.dim() + 1).forEach(i ->randomSimp.add
            (new GraphX(f, Point.uniformRand(center, r))));
        return randomSimp;
    }

}
