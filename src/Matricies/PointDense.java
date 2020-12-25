package Matricies;

import Convex.ConvexSet;
import Convex.Linear.Plane;
import Matricies.Matrix;
import static java.lang.Math.abs;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparse;
import org.ejml.data.DMatrixSparseTriplet;

/**
 *
 * @author dov
 */
public class PointDense extends MatrixDense implements Point {//implements Comparable {

    /**
     * constructor
     *
     * @param x
     * @param y
     */
    public PointDense(double[] x) {
        super(x.length, 1);
        System.arraycopy(x, 0, array, 0, x.length);
    }

    /**
     * Creates an empty point in n dimensional space. This point needs to be
     * assigned values.
     *
     * @param n
     */
    public PointDense(int n) {
        super(n, 1);
    }

    public static PointDense sparse(int dim, int numNonZeroes) {
        PointDense m = new PointDense(dim);
        m.array = null;
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
    public PointDense(PointDense p) {
        this(p.array.length);
        System.arraycopy(p.array, 0, array, 0, p.array.length);
    }

    /**
     * constructor
     *
     * @param p another point
     * @param mag start scalar the other point gets multiplied by to create this
     * point.
     */
    public PointDense(PointDense p, double mag) {
        this(p.dim());
        setAll(i -> p.get(i) * mag);
    }

    /**
     * @param dim the number of dimensions of the origin.
     * @return (0,0)
     */
    public static PointDense Origin(int dim) {
        return new PointDense(dim).setAll(i -> 0);
    }

    @Override
    public PointDense addToMe(Point p) {
        return setAll(i -> get(i) + p.get(i));
    }

    /**
     * @see plus
     * @param p
     * @return
     */
    @Override
    public PointDense minus(Point p) {
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
        return d(Origin(dim()));
    }

    /**
     * the direction this vector is pointed in
     *
     * @return
     */
    @Override
    public PointDense dir() {
        double m = magnitude();
        if (m == 0) return Origin(dim());
        return map(x -> x / m);
    }

    @Override
    public PointDense map(DoubleFunction<Double> f) {
        return new PointDense(dim()).setAll(i -> f.apply(array[i]));
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
        return IntStream.range(0, Math.min(dim(), p.dim())).
                mapToDouble(i -> p.get(i) * get(i)).sum();
    }

    /**
     * the dot product between this point and p
     *
     * @param p
     * @return
     */
    public double dot(double[] p) {
        return dot(new PointDense(p));
    }

    @Override
    public MatrixDense outerProduct(Point p) {
        return new MatrixDense(dim(), p.dim()).setAll((i, j) -> get(i) * p.get(j));
    }
    
    public MatrixSparse outerProduct(PointSparse p) {
        DMatrixSparseTriplet trip = new DMatrixSparseTriplet(dim(), p.dim(), p.ejmlSparse.getNonZeroLength()*dim());
        Iterator<DMatrixSparse.CoordinateRealValue> iter = p.ejmlSparse.createCoordinateIterator();
        while(iter.hasNext()){
            DMatrixSparse.CoordinateRealValue crv = iter.next();
            IntStream.range(0, dim()).forEach(i -> trip.set(i, crv.row, crv.value));
        }
        return new MatrixSparse(trip);
    }

    /**
     * scalar multiplication
     *
     * @param k
     * @return
     */
    @Override
    public PointDense mult(double k) {
        return map(x -> x * k);
    }

    @Override
    public Matrix mult(Matrix matrix) {
        return new Matrix(this).T().mult(matrix);
    }

    @Override
    public PointDense multMe(double k) {
        return setAll(i -> get(i) * k);
    }

    /**
     * The projection of this vector onto start unit vector
     *
     * @param p the unit vector this one is projected onto
     * @return the result of shadowing this vector onto start unit vector
     */
    @Override
    public PointDense inDir(PointDense p) {
        return p.dir().mult(dot(p.dir()));
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
    public PointDense(String fromString) {
        this((int) (fromString.chars().filter(c -> c == (int) (',')).count() + 1));

        fromString = fromString.replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll(" ", "");

        array = Arrays.stream(fromString.split(",")).mapToDouble(Double::parseDouble).toArray();
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
    public boolean equals(PointDense p) {
        return Arrays.equals(array, p.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final PointDense other = (PointDense) obj;
        return Arrays.equals(this.array, other.array);
    }

    /**
     * is this point really near p?
     *
     * @param p the point near by
     * @param acc the distance aloud to p
     * @return
     */
    @Override
    public boolean equals(PointDense p, double acc) {
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
        return array.length;
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
        return array[i];
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
    public PointDense set(int i, double y) {
        array[i] = y;
        return this;
    }

    /**
     *
     * @return the point as an array
     */
    @Override
    public double[] array() {
        return array;
    }

    /**
     *
     * @return the sum of the absolute values of the elements in the point
     */
    public double sumAbs() {
        double s = 0;
        for (int i = 0; i < dim(); i++)
            s += Math.abs(get(i));
        return s;
    }

    public PointDense(double x, double y) {
        this(2);
        set(0, x);
        set(1, y);
    }

    public PointDense(double x, double y, double z) {
        this(3);
        set(0, x);
        set(1, y);
        set(2, z);
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
     * the midpoint of this point and another
     *
     * @param b the other point
     * @return the point halfway between the other point and this point.
     */
    public PointDense mid(PointDense b) {
        return plus(b).mult(.5);
    }

    /**
     * the sum of this point and another
     *
     * @param p the other point
     * @return the sum of the two points
     */
    @Override
    public PointDense plus(PointDense p) {
        return new PointDense(dim()).setAll(i -> get(i) + p.get(i));
    }

    @Override
    public PointDense dot(Matrix m) {
        if (m.rows != dim())
            throw new ArithmeticException("wrong number of rows in matrix to multiply by this point");

        return new PointDense(m.cols).setAll(i -> m.row(i).dot(this));
    }

    /**
     * creates start new point shifted in one dimension the given distance
     *
     * @param dim the dimension the new point is shifted in from this one.
     * @param dist the distance the new point is shifted.
     * @return start new point as described above.
     */
    public PointDense shift(int dim, double dist) {
        PointDense shift = new PointDense(this);
        shift.set(dim, get(dim) + dist);
        return shift;
    }

    /**
     * creates start one dimensional point
     *
     * @param x the value of the point
     * @return start one dimensional point located at x
     */
    public static PointDense oneD(double x) {
        return new PointDense(new double[]{x});
    }

    /**
     * sets the values of this point to a sub array.
     *
     * @param x the array
     * @param srcStartPos the starting index of the MyPoint in the array
     * @return start this point
     */
    @Override
    public PointDense setFromSubArray(double[] x, int srcStartPos) {
        System.arraycopy(x, srcStartPos, array, 0, Math.min(dim(), x.length));
        return this;
    }

    /**
     * sets the values of this point to a sub array.
     *
     * @param x the array
     * @param start the starting index of the x VALUES in this point
     * @return start this point
     */
    public PointDense setFromSubArray(PointDense x, int start) {
        return setFromSubArray(x.array, start);
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
    public static PointDense uniformRand(PointDense center, double r) {
        return center.map(t -> t + r * (2 * rand.nextDouble() - 1));
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
    public static PointDense gaussianRand(int dim, PointDense mean, PointDense standardDeviation, Random rand) {
        return new PointDense(dim).setAll(i -> rand.nextGaussian()
                * standardDeviation.get(i) + mean.get(i));
    }

    /**
     * reflects this point through start center point
     *
     * @param center
     * @param scale
     * @return the new point
     */
    public PointDense reflectThrough(PointDense center, double scale) {

        return new PointDense(center.dim()).setAll(i -> center.get(i) + scale
                * (center.get(i) - get(i)));

    }

    public void swapValues(int i, int j) {
        double temp = get(i);
        set(i, get(j));
        set(j, temp);
    }

    /**
     * the absolute value at the given index
     *
     * @param i the index you want to get the absolute value at
     * @return
     */
    public double getAbs(int i) {
        return abs(get(i));
    }

    @Override
    public DoubleStream stream() {
        return Arrays.stream(array);
    }

    /**
     * The average value in the point
     *
     * @return the average value in the point
     */
    @Override
    public double avg() {
        return stream().parallel().sum() / dim();
    }

    /**
     * The variance in the elements of this point
     *
     * @return
     */
    public double variance() {
        return covariance(this);
    }

    /**
     * The standard deviation in this point
     *
     * @return
     */
    public double standardDeviation() {
        return Math.sqrt(variance());
    }

    /**
     * the covariance between this point and another point
     *
     * @param p
     */
    public double covariance(PointDense p) {
        double avg = avg(), pAvg = p.avg();
        return IntStream.range(0, dim()).parallel().mapToDouble(i -> (avg
                - get(i)) * (pAvg - p.get(i))).sum() / dim();
    }

    /**
     * the covariance matrix of this point and another, each is treated as
     * though it's start discrete random variable.
     *
     * @param p
     * @return
     */
    public Matrix covarianceMatrix(PointDense p) {
        return covarianceMatrix(new PointDense[]{this, p});
    }

    /**
     * A covariance matrix for an array of data points
     *
     * @param p an array of data points, each point is start discrete random
     * variable
     * @return the covariance Matrix of the data points
     */
    public static Matrix covarianceMatrix(PointDense[] p) {
        return new Matrix(p.length).setAll((i, j) -> p[i].covariance(p[j]));
    }

    /**
     * The cross product of this vector and another. Make sure both are 3
     * dimensional vectors.
     *
     * @param p the other point
     * @return the cross product of the two points
     */
    @Override
    public PointDense cross(PointDense p) {
        return Matrix.fromRows(new PointDense[]{this, p}).crossProduct();
    }

    /**
     * The cross product of n + 1 points where each point has dimension n.
     * https://math.stackexchange.com/questions/2371022/cross-product-in-higher-dimensions
     *
     * @param p an array of points
     * @return the cross product of the points
     */
    public static PointDense cross(PointDense[] p) {
        return Matrix.fromRows(p).crossProduct();
    }

    /**
     * Is one of these vectors start multiple of the other.
     *
     * @param p
     * @param epsilon
     * @return
     */
    @Override
    public boolean sameDirection(PointDense p, double epsilon) {
        return dir().distSq(p.dir()) < epsilon;
    }

    /**
     * Sets the values of this point to those in the array.
     *
     * @param x an array of scalars
     * @return this point
     */
    @Override
    public PointDense set(double[] x) {
        System.arraycopy(x, 0, array, 0, x.length);
        return this;
    }

    /**
     * sets this point equal to the given point
     *
     * @param x
     * @return
     */
    @Override
    public PointDense set(PointDense x) {
        return set(x.array);
    }

    @Override
    public <T> List mapToList(Function<Double, T> f) {
        return stream().parallel().mapToObj(t -> f.apply(t)).collect(Collectors.toList());
    }

    /**
     * are all the elmenets in this point less than all the elements in the
     * given point.
     *
     * @param p
     * @return
     */
    public boolean lessThan(PointDense p) {
        return IntStream.range(0, dim()).allMatch(i -> get(i) < p.get(i));
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

    public boolean isMember(ConvexSet cs, double epsilon) {
        return cs.d(this) < epsilon;
    }

    /**
     * The projection of this point onto a convex set.
     *
     * @param cs the convex set
     * @return the projection of this point on the convex set
     */
    @Override
    public PointDense proj(ConvexSet cs) {
        return cs.proj(this);
    }

    /**
     * Is this point above the plane
     *
     * @param plane
     * @return
     */
    @Override
    public boolean above(Plane plane) {
        return plane.below(this);
    }

    /**
     * is this point below the plane
     *
     * @param plane
     * @return
     */
    @Override
    public boolean below(Plane plane) {
        return plane.above(this);
    }

    /**
     * concatenates this point and p in a new point
     *
     * @param p the point to concatenate with this point
     * @return a new point
     */
    @Override
    public PointDense concat(PointDense p) {
        PointDense concat = new PointDense(dim() + p.dim());
        System.arraycopy(array, 0, concat.array, 0, dim());
        System.arraycopy(p.array, 0, concat.array, dim(), p.dim());
        return concat;
    }

    /**
     * concatenates a single value onto this point.
     *
     * @param d
     * @return
     */
    @Override
    public PointDense concat(double d) {
        return concat(PointDense.oneD(d));
    }

    @Override
    public PointDense setAll(IntToDoubleFunction f) {
        Arrays.setAll(array, f);
        return this;
    }

    /**
     * The constructor, a 4f point
     *
     * @param x
     * @param y
     * @param z
     * @param t
     */
    public PointDense(double x, double y, double z, double t) {
        this(new double[]{x, y, z, t});
    }

    /**
     * cut the rows identified in the list
     *
     * @param cut a list of rows to be cut
     * @return a new point without the rows cut.
     */
    public PointDense removeRows(List<Integer> cut) {
        PointDense removeRows = new PointDense(dim() - cut.size());
        for (int to = 0, from = 0; from < dim(); from++)
            if (!cut.contains(from)) removeRows.set(to++, get(from));
        return removeRows;
    }

    @Override
    public MatrixDense T() {
        return new MatrixDense(array, 1, dim());
    }

    
    
    

}
