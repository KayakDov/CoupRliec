package FuncInterfaces;

import Matricies.Matrix;
import RnSpace.curves.Curve;
import RnSpace.points.Point;
import RnSpace.rntor.Derrivitive;
import RnSpace.rntor.GraphX;
import java.util.function.Function;
import Convex.Cube;

/**
 *
 * @author dov
 */
public interface RnToR extends Function<Point, Double> {

    public double of(double[] x);

    public default double of(double x) {
        return of(new double[]{x});
    }

    public default double of(Point p) {
        return of(p.array());
    }

    public default Point of(Matrix rows) {
        return new Point(rows.rows).setAll(i -> of(rows.row(i)));
    }

    public default double of(Double[] x) {

        return of(new Point(x.length).setAll(i -> x[i]));
    }

    public default <V> Function<V, Double> of(Function<? super V, ? extends Point> before) {
        return p -> of(before.apply(p));
    }
    
    public default RToR of(Curve g){
        return t -> of(g.of(t));
    }

    @Override
    public default Double apply(Point t) {
        return of(t);
    }

    public default RnToR abs() {
        return x -> Math.abs(of(x));
    }

    public default RnToR minus(RnToR f) {
        return x -> of(x) - f.of(x);
    }

    public default RnToR plus(RnToR f) {
        return x -> of(x) + f.of(x);
    }

    public default RnToR times(RnToR f) {
        return x -> of(x) * f.of(x);
    }

    public default RnToR times(double d) {
        return x -> of(x) * d;
    }

    public default RnToR over(RnToR f) {
        return x -> of(x) / f.of(x);
    }

    public default RnToR pow(RnToR f) {
        return x -> Math.pow(of(x), f.of(x));
    }

    public static RnToR Const(double c) {
        return t -> c;
    }

    public static RnToR Id(int d, int n) {
        return t -> t[d];
    }

    public static RnToR Id(int d) {
        return Id(d, d + 1);
    }

    /**
     * The derivative function of this one.
     *
     * @param i the dimension to find the derivative of. df/dx_i
     * @param dti the differentiation constant
     * @return the derrivitive function
     */
    public default Derrivitive d(int i, double dti) {
        return new Derrivitive(this, i, dti);
    }

    /**
     * forks off a new thread computing the value of the function at the given
     * point.The value can be recalled with join(x).
     *
     * @param x will compute the value of the function at x.
     * @return the point which the function is forked for, x.
     */
    public default GraphX fork(Point x) {
        return new GraphX(this, x);
    }

    /**
     * forks off a new thread computing the value of the function at the given
     * point.The value can be recalled with join(x).
     *
     * @param x will compute the value of the function at x.
     * @return the point which the function is forked for, x.
     */
    public default GraphX fork(double x) {
        return fork(Point.oneD(x));
    }
    
    /**
     * see fork
     *
     * @param x
     * @return
     */
    public default GraphX fork(double[] x) {
        return fork(new Point(x));
    }

    
    public default double of(GraphX x){
        if(x.getF() == this) return x.y();
        else return of(x);
    }
    
    public default double integral(Cube cube, double dx){
        return cube.stream(dx).mapToDouble(x -> of(x)*Math.pow(dx, cube.dim())).sum();
    }
  
}
