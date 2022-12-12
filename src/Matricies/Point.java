package Matricies;
import Hilbert.Vector;
import java.util.Arrays;
import java.util.Random;
import java.util.function.DoubleFunction;
import java.util.function.IntToDoubleFunction;

/**
 *
 * @author dov
 */
public class Point extends Matrix implements Vector<Point>{

    /**
     * constructor
     * Note, the array passed is the array used and changes made to it will be
     * made to the array.
     * @param x
     */
    public Point(double... x) {
        super(x.length, 1);
        data = x;
    }

    /**
     * Creates an empty point in n dimensional space. This point needs to be
     * assigned values.
     *
     * @param n
     */
    public Point(int n) {
        super(n, 1);
    }
    
    /**
     * A constructor that sets all values to 0 except at the given index
     * @param dim the dimension of space
     * @param i the index that with a non 0 value.
     * @param val the value at the given index.
     */
    public Point(int dim, int i, double val){
        this(dim);
        data[i] = val;
    }

    /**
     * @see plus
     * @param p
     * @return
     */
    
    public Point minus(Point p) {
        return plus(p.mult(-1));
    }

    /**
     * distance function Lp2
     *
     * @param mp The point distant from this one.
     * @return The distance from this point to the given point.
     */
    
    public double d(Point mp) {
        return Math.sqrt(distSq(mp));
    }

    /**
     * The distance squared from this point to another.
     * @param mp
     * @return 
     */
    public double distSq(Point mp) {
        return minus(mp).dot(minus(mp));
    }

    /**
     * The magnitude of the vector reprisented by this point
     *
     * @return The distance of this point from the origan;
     */
    
    public double magnitude() {
        return d(new Point(dim()));
    }

    /**
     * the direction this vector is pointed in
     *
     * @return
     */
    
    public Point dir() {
        double m = magnitude();
        if (m == 0) return new Point(dim());
        return map(x -> x / m);
    }

    @Override
    public Point map(DoubleFunction<Double> f) {
        return new Point(dim(), i -> f.apply(data[i]));
    }

    /**
     * the dot product between this point and p inner product Will truncate the
     * longer point if they're not equal in length.
     *
     * @param p
     * @return
     */
    
    public double dot(Point p) {
        int dim = dim();
        double sum = 0;
        for (int i = 0; i < dim; i++)
            sum += data[i] * p.get(i);
        return sum;

    }

    /**
     * scalar multiplication
     *
     * @param k
     * @return
     */
    @Override
    public Point mult(double k) {
        return map(x -> x * k);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < dim(); i++) {
            sb.append(get(i));
            if (i < dim() - 1)
                sb.append(", ");
        }
        return sb.append(")").toString();
    }

    /**
     * does this point have the same x,y as p
     *
     * @param p
     * @return
     */
    
    public boolean equals(Point p) {
        if (p == this) return true;
        return equals(p);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Point other = (Point) obj;
        return Arrays.equals(this.data, other.data);
    }

    /**
     * this point exists in n dimensional space
     *
     * @return the number of dimensions the point is defined in.
     */
    
    public int dim() {
        return data.length;
    }

    /**
     * returns p_i which will be 0 if this point doesn't have i dimensions
     *
     * @param i
     * @return
     */
    @Override
    public double get(int i) {
        if (i >= dim() || i < 0)
            return 0;
        return data[i];
    }
    
    public double x() {
        return get(0);
    }

    public double y() {
        return get(1);
    }

    public double z() {
        return get(2);
    }

    /**
     * the sum of this point and another
     *
     * @param p the other point
     * @return the sum of the two points
     */
    
    public Point plus(Point p) {
        return new Point(dim(), i -> get(i) + p.get(i));
    }

    /**
     * creates a one dimensional point
     *
     * @param x the value of the point
     * @return a one dimensional point located at x
     */
    public static Point oneD(double x) {
        return new Point(new double[]{x});
    }

    public static Random rand = new Random(1);

    /**
     * generates a vertex randomly distributed on the surface of a sphere.
     *
     * @param dim the dimension of the point to be created.
     * @param r the radius of the sphere.
     * @return a random vertex on the surface of the sphere.
     */
    public static Point uniformRandSphereSurface(int dim, double r) {
        return new Point(dim, i -> rand.nextGaussian()).dir().mult(r);
    }

    /**
     * concatenates this point and p in a new point
     *
     * @param p the point to concatenate with this point
     * @return a new point
     */
    public Point concat(Point p) {
        Point concat = new Point(dim() + p.dim());
        System.arraycopy(data, 0, concat.data, 0, dim());
        System.arraycopy(p.data, 0, concat.data, dim(), p.dim());
        return concat;
    }

    /**
     * concatenates a single value onto this point.
     *
     * @param d
     * @return
     */
    public Point concat(double d) {
        return concat(Point.oneD(d));
    }

    /**
     * A constructor.  Each value is assigned as a function of its index.
     * @param dim
     * @param f 
     */
    public Point(int dim, IntToDoubleFunction f){
        this(dim);
        Arrays.setAll(data, f);
    }

    @Override
    public Matrix T() {
        return new Matrix(data, 1, dim());
    }

    @Override
    public Point sum(Point v) {
        return plus(v);
    }

    @Override
    public double ip(Point v) {
        return dot(v);
    }
}
