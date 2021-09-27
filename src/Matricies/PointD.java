package Matricies;
import Convex.ConvexSet;
import Convex.LinearRn.RnPlane;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.function.DoubleFunction;
import java.util.function.IntToDoubleFunction;

/**
 *
 * @author dov
 */
public class PointD extends MatrixDense implements Point {//implements Comparable {

    /**
     * constructor
     *
     * @param x
     * @param y
     */
    public PointD(double[] x) {
        super(x.length, 1);
        System.arraycopy(x, 0, data, 0, x.length);
        hash = Arrays.hashCode(x);
    }

    public PointD(Point x) {
        this(x.asDense());
    }

    /**
     * Creates an empty point in n dimensional space. This point needs to be
     * assigned values.
     *
     * @param n
     */
    public PointD(int n) {
        super(n, 1);
    }

    public static PointD sparse(int dim, int numNonZeroes) {
        PointD m = new PointD(dim);
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
    public PointD(PointD p) {
        this(p.data.length);
        System.arraycopy(p.data, 0, data, 0, p.data.length);
        this.hash = p.hash;
    }

    private int hash;

    public PointD setHash() {
        hash = Arrays.hashCode(super.data);
        return this;
    }

    /**
     * @param p the point to be added to this one
     * @return this point, with p added to it.
     */
    @Override
    public PointD addToMe(Point p) {
        return setAll(i -> get(i) + p.get(i));
    }

    /**
     * @see plus
     * @param p
     * @return
     */
    @Override
    public PointD minus(Point p) {
        return plus(p.mult(-1));
    }

    /**
     * distance function Lp2
     *
     * @param mp The point distant from this one.
     * @return The distance from this point to the given point.
     */
    @Override
    public double d(Point mp) {
        return Math.sqrt(distSq(mp));
    }

    @Override
    public double distSq(Point mp) {
        return minus(mp).dot(minus(mp));
    }

    /**
     * The magnitude of the vector reprisented by this point
     *
     * @return The distance of this point from the origan;
     */
    @Override
    public double magnitude() {
        return d(new PointD(dim()));
    }

    /**
     * the direction this vector is pointed in
     *
     * @return
     */
    @Override
    public PointD dir() {
        double m = magnitude();
        if (m == 0) return new PointD(dim());
        return mapToDense(x -> x / m);
    }

    @Override
    public PointD mapToDense(DoubleFunction<Double> f) {
        return new PointD(dim()).setAll(i -> f.apply(data[i]));
    }

    @Override
    public PointSparse mapToSparse(DoubleFunction<Double> f) {
        return mapToDense(f).asSparse();
    }

    /**
     * the dot product between this point and p inner product Will truncate the
     * longer point if they're not equal in length.
     *
     * @param p
     * @return
     */
    @Override
    public double dot(Point p) {
        int dim = dim();
        double sum = 0;
        for (int i = 0; i < dim; i++)
            sum += data[i] * p.get(i);
        return sum;

    }

    public double dot(PointD p) {
        int dim = dim();
        double sum = 0;
        for (int i = 0; i < dim; i++)
            sum += data[i] * p.data[i];
        return sum;
    }

    /**
     * the dot product between this point and p
     *
     * @param p
     * @return
     */
    public double dot(double[] p) {
        return dot(new PointD(p));
    }

    /**
     * scalar multiplication
     *
     * @param k
     * @return
     */
    @Override
    public PointD mult(double k) {
        return mapToDense(x -> x * k);
    }

    @Override
    public MatrixDense mult(Matrix matrix) {
        return new MatrixDense(this).T().mult(matrix);
    }

    @Override
    public PointD multMe(double k) {
        return setAll(i -> get(i) * k);
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
    public PointD(String fromString) {
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
    @Override
    public boolean isReal() {
        return stream().allMatch(y -> (!Double.isNaN(y) && Double.isFinite(y)));
    }

    /**
     * does this point have the same x,y as p
     *
     * @param p
     * @return
     */
    @Override
    public boolean equals(Point p) {
        if (p == this) return true;
        if (p.isDense()) return equals(p.asDense());
        else return equals(p.asSparse());
    }

    public boolean equals(PointD pd) {
        if (this == pd) return true;
        return Arrays.equals(data, pd.data);
    }

    public boolean equals(PointSparse ps) {
        return IntStream.range(0, dim()).allMatch(i -> Math.abs(ps.get(i) - get(i)) <= tolerance);
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
        final PointD other = (PointD) obj;
        return Arrays.equals(this.data, other.data);
    }

    /**
     * is this point really near p?
     *
     * @param p the point near by
     * @param acc the distance aloud to p
     * @return
     */
    @Override
    public boolean equals(Point p, double acc) {
        if (p == null) return false;
        return d(p) < acc;
    }

    /**
     * this point exists in n dimensional space
     *
     * @return the number of dimensions the point is defined in.
     */
    @Override
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

    /**
     * Sets the value of the point
     *
     * @param i the index of the value
     * @param y the new value at that index
     * @return this point
     */
    @Override
    public double set(int i, double y) {
        data[i] = y;
        return y;
    }

    @Override
    public PointD asDense() {
        return this;
    }

    /**
     *
     * @return the point as an array
     */
    @Override
    public double[] array() {
        return data;
    }


    public PointD(double x, double y) {
        this(2);
        set(0, x);
        set(1, y);
        setHash();
    }

    public PointD(double x, double y, double z) {
        this(3);
        set(0, x);
        set(1, y);
        set(2, z);
        setHash();
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
    @Override
    public PointD plus(Point p) {
        return new PointD(dim()).setAll(i -> get(i) + p.get(i));
    }

    @Override
    public PointD dot(Matrix m) {
        if (m.rows() != dim())
            throw new ArithmeticException("wrong number of rows in matrix to multiply by this point");

        return new PointD(m.cols()).setAll(i -> m.row(i).dot(this));
    }


    /**
     * creates start one dimensional point
     *
     * @param x the value of the point
     * @return start one dimensional point located at x
     */
    public static PointD oneD(double x) {
        return new PointD(new double[]{x});
    }

    public static Random rand = new Random(1);

    /**
     * generates start vertex randomly distributed in start sphere of radius r
     * around start central point.
     *
     * @param dim the space the point is in
     * @param center the center of the sphere the vertes is randomly generated
     * in.
     * @param r the radius of the sphere.
     * @param rand the random number generator.
     * @return start random vertex in start sphere.
     */
    public static PointD uniformRand(PointD center, double r) {
        return center.mapToDense(t -> t + r * (2 * rand.nextDouble() - 1));
    }

    /**
     * creates start new point with start gaussian distribution near the
     * provided mean.
     *
     * @param dim the dimensions of the new point
     * @param mean the center of the distribution
     * @param standardDeviation the standard deviation of the distribution.
     * @param r
     * @param rand the random number generator
     * @return start new randomly distributed point.
     */
    public static PointD gaussianRand(int dim, PointD mean, PointD standardDeviation, Random rand) {
        return new PointD(dim).setAll(i -> rand.nextGaussian()
                * standardDeviation.get(i) + mean.get(i));
    }



    @Override
    public DoubleStream stream() {
        return Arrays.stream(data);
    }

    /**
     * Sets the values of this point to those in the array.
     *
     * @param x an array of scalars
     * @return this point
     */
    @Override
    public PointD set(double[] x) {
        System.arraycopy(x, 0, data, 0, x.length);
        return setHash();
    }

    /**
     * sets this point equal to the given point
     *
     * @param x
     * @return
     */
    @Override
    public PointD set(Point x) {
        return set(x.asDense().data);
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
    @Override
    public boolean above(RnPlane plane) {
        return plane.below(this);
    }

    /**
     * is this point below the plane
     *
     * @param plane
     * @return
     */
    @Override
    public boolean below(RnPlane plane) {
        return plane.above(this);
    }

    /**
     * concatenates this point and p in a new point
     *
     * @param p the point to concatenate with this point
     * @return a new point
     */
    @Override
    public PointD concat(Point p) {
        PointD concat = new PointD(dim() + p.dim());
        System.arraycopy(data, 0, concat.data, 0, dim());

        if (p.isDense())
            System.arraycopy(p.asDense().data, 0, concat.data, dim(), p.dim());
        else {
            p.asSparse().nonZeroes().forEach(coord -> concat.set(dim() + coord.row, coord.value));
        }

        return concat.setHash();
    }

    /**
     * concatenates a single value onto this point.
     *
     * @param d
     * @return
     */
    @Override
    public PointD concat(double d) {
        return concat(PointD.oneD(d));
    }

    @Override
    public PointD setAll(IntToDoubleFunction f) {
        Arrays.setAll(data, f);
        return setHash();
    }

    /**
     * The constructor, a 4f point
     *
     * @param x
     * @param y
     * @param z
     * @param t
     */
    public PointD(double x, double y, double z, double t) {
        this(new double[]{x, y, z, t});
    }


    @Override
    public MatrixDense T() {
        return new MatrixDense(data, 1, dim());
    }

    @Override
    public PointSparse asSparse() {
        PointSparse ps = new PointSparse(numRows);
        ps.setAll((i, j) -> get(i));
        return ps;
    }

}
