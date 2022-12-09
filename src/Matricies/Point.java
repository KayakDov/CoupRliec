package Matricies;
import Convex.ConvexSet;
import Convex.LinearRn.RnPlane;
import Hilbert.Vector;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.function.DoubleFunction;
import java.util.function.IntToDoubleFunction;

/**
 *
 * @author dov
 */
public class Point extends Matrix implements Vector<Point>{

    /**
     * constructor
     *
     * @param x
     */
    public Point(double... x) {
        super(x.length, 1);
        System.arraycopy(x, 0, data, 0, x.length);
        hash = Arrays.hashCode(x);
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

    public static Point sparse(int dim, int numNonZeroes) {
        Point m = new Point(dim);
        m.data = null;
        return m;
    }

    /**
     * Creates an unsafe point quickly.
     *
     * @param x, the values of the point. Changing the array after it's passed
     * will change the point.
     * @return a point linked to the array
     */
//    public static Point fastPoint(double[] x) {
//        Point p = new Point();
//        p.x = x;
//        return p;
//    }
    /**
     * change the dimensions of the space this point exists in.
     *
     * @param d
     */
//    public void setDimensions(int d)w
    /**
     * copy constructor
     *
     * @param p
     */
    public Point(Point p) {
        this(p.data.length);
        System.arraycopy(p.data, 0, data, 0, p.data.length);
        this.hash = p.hash;
    }

    private int hash;

    public Point setHash() {
        hash = Arrays.hashCode(super.data);
        return this;
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
        return mapToDense(x -> x / m);
    }

    @Override
    public Point mapToDense(DoubleFunction<Double> f) {
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
     * the dot product between this point and p
     *
     * @param p
     * @return
     */
    public double dot(double[] p) {
        return dot(new Point(p));
    }

    /**
     * scalar multiplication
     *
     * @param k
     * @return
     */
    @Override
    public Point mult(double k) {
        return mapToDense(x -> x * k);
    }

    @Override
    public Matrix mult(Matrix matrix) {
        return new Matrix(this).T().mult(matrix);
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
     * creates a point from a string formatted as follows: (1.0, 2.0, 3.0)
     *
     * @param fromString
     */
    public Point(String fromString) {
        this((int) (fromString.chars().filter(c -> c == (int) (',')).count() + 1));

        fromString = fromString.replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll(" ", "");

        data = Arrays.stream(fromString.split(",")).mapToDouble(Double::parseDouble).toArray();
        setHash();
    }

    /**
     *
     * @return this point is defined and has real values
     */
    
    public boolean isReal() {
        return stream().allMatch(y -> (!Double.isNaN(y) && Double.isFinite(y)));
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
//        return Arrays.hashCode(data);
        return hash;
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
     * is this point really near p?
     *
     * @param p the point near by
     * @param acc the distance aloud to p
     * @return
     */
    
    public boolean equals(Point p, double acc) {
        if (p == null) return false;
        return d(p) < acc;
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

    /**
     * gets the value in the last dimension
     *
     * @return
     */
    public double getLast() {
        return get(dim() - 1);
    }

    

    @Override
    public Point asDense() {
        return this;
    }

    /**
     *
     * @return the point as an array
     */
    
    public double[] array() {
        return data;
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

    
    public Point dot(Matrix m) {
        if (m.rows() != dim())
            throw new ArithmeticException("wrong number of rows in matrix to multiply by this point");

        return new Point(m.cols(), i -> m.row(i).dot(this));
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
     * generates a vertex randomly distributed in a sphere of radius r
     * around a central point.
     *
     * @param center the center of the sphere the vertes is randomly generated
     * in.
     * @param r the radius of the sphere.
     * @return a random vertex in a sphere.
     */
    public static Point uniformBoundedRand(Point center, double r) {
        return center.mapToDense(t -> t + r * (2 * rand.nextDouble() - 1));
    }

    /**
     * creates a new point with a gaussian distribution near the
     * provided mean.
     *
     * @param dim the dimensions of the new point
     * @param mean the center of the distribution
     * @param standardDeviation the standard deviation of the distribution.
     * @param rand the random number generator
     * @return a new randomly distributed point.
     */
    public static Point gaussianRand(int dim, Point mean, Point standardDeviation, Random rand) {
        return new Point(dim, i -> rand.nextGaussian()
                * standardDeviation.get(i) + mean.get(i));
    }



    
    public DoubleStream stream() {
        return Arrays.stream(data);
    }

    /**
     * Is this point in a convex set
     *
     * @param cs the convex set
     * @return true if the point is in the set. False otherwise.
     */
    public boolean isMember(ConvexSet cs) {
        return cs.hasElement(this);
    }

    public boolean isMember(ConvexSet cs, double tolerance) {
        return cs.hasElement(this, tolerance);
    }


    /**
     * Is this point above the plane
     *
     * @param plane
     * @return
     */
    
    public boolean above(RnPlane plane) {
        return plane.below(this);
    }

    /**
     * is this point below the plane
     *
     * @param plane
     * @return
     */
    
    public boolean below(RnPlane plane) {
        return plane.above(this);
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
        System.arraycopy(p.asDense().data, 0, concat.data, dim(), p.dim());

        return concat.setHash();
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

    public Point(int dim, IntToDoubleFunction f){
        this(dim);
        Arrays.setAll(data, f);
    }
//    @Override
//    public Point setAll(IntToDoubleFunction f) {
//        
//        return setHash();
//    }

    /**
     * The constructor, a 4f point
     *
     * @param x
     * @param y
     * @param z
     * @param t
     */
    public Point(double x, double y, double z, double t) {
        this(new double[]{x, y, z, t});
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
