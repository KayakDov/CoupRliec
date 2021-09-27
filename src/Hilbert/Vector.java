package Hilbert;

/**
 * A vector in a Hilbert space
 *
 * @author Dov Neimand
 * @param <T> this should be the same type of vector that's being created.
 */
public interface Vector<T extends Vector> {

    /**
     * The sum of this vector and another
     *
     * @param v the vector being added to this one
     * @return the resulting vector
     */
    public T sum(T v);

    /**
     * The product of this vector and a scalar
     *
     * @param s the scalar
     * @return the resulting product
     */
    public T mult(double s);
    
    /**
     * The inner product of this vector and another.
     *
     * @param v the other vector
     * @return the resulting inner product of the two vectors.
     */
    public double ip(T v);

    /**
     * the difference between this vector and another.  This vector minus the other.
     * @param v
     * @return 
     */
    public default T dif(T v){
        return sum((T)v.mult(-1));
    }
    
    /**
     * reflects this point through start center point
     *
     * @param center
     * @param scale
     * @return the new point
     */
    public default T reflectThrough(T center, double scale) {
        return (T)center.sum((T)((T)center.dif(this)).mult(scale));
    }
    
    /**
     * The norm of this vector squared
     * @return 
     */
    public default double normSquared(){
        return ip((T)this);
    }
    
    /**
     * The norm of this vector.
     * @return 
     */
    public default double norm(){
        return Math.sqrt(normSquared());
    }
    
    /**
     * The distance from this vector to another squared.
     * @param v
     * @return 
     */
    public default double distSquared(T v){
        return dif(v).normSquared();
    }
    
    /**
     * The distance from this vector to another.
     * @param v
     * @return 
     */
    public default double dist(T v){
        return dif(v).norm();
    }
}
