package Hilbert;

/**
 * A vector in a Hilbert space
 *
 * @author Dov Neimand
 * @param <Vec> this should be the same type of vector that's being created.
 */
public interface Vector<Vec extends Vector> {

    /**
     * The sum of this vector and another
     *
     * @param v the vector being added to this one
     * @return the resulting vector
     */
    public Vec sum(Vec v);

    /**s
     * The product of this vector and a scalar
     *
     * @param s the scalar
     * @return the resulting product
     */
    public Vec mult(double s);
    
    /**
     * The inner product of this vector and another.
     *
     * @param v the other vector
     * @return the resulting inner product of the two vectors.
     */
    public double ip(Vec v);

    /**
     * the difference between this vector and another.  This vector minus the other.
     * @param v
     * @return 
     */
    public default Vec dif(Vec v){
        return sum((Vec)v.mult(-1));
    }
    
    /**
     * reflects this point through start center point
     *
     * @param center
     * @param scale
     * @return the new point
     */
    public default Vec reflectThrough(Vec center, double scale) {
        return (Vec)center.sum((Vec)((Vec)center.dif(this)).mult(scale));
    }
    
    /**
     * The norm of this vector squared
     * @return 
     */
    public default double normSq(){
        return ip((Vec)this);
    }
    
    /**
     * The norm of this vector.
     * @return 
     */
    public default double norm(){
        return Math.sqrt(normSq());
    }
    
    /**
     * The distance from this vector to another squared.
     * @param v
     * @return 
     */
    public default double distSquared(Vec v){
        return dif(v).normSq();
    }
    
    /**
     * The distance from this vector to another.
     * @param v
     * @return 
     */
    public default double dist(Vec v){
        return dif(v).norm();
    }
    
    /**
     * The dimension of this vector.  A return value of Integer.maxValue is
     * reserved for infinite dimensions.
     * @return 
     */
    public int dim();
}
