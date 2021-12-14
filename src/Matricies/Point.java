/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Matricies;

import Convex.LinearRn.RnPlane;
import Hilbert.Vector;
import java.util.function.DoubleFunction;
import java.util.function.IntToDoubleFunction;
import java.util.stream.DoubleStream;

/**
 *
 * @author dov
 */
public interface Point extends Matrix, Vector<Point>{

    public Matrix T();

    /**
     * Is this point above the plane
     *
     * @param plane
     * @return
     */
    public boolean above(RnPlane plane);

    /**
     *
     * @return the point as an array
     */
    public double[] array();


    /**
     * is this point below the plane
     *
     * @param plane
     * @return
     */
    public boolean below(RnPlane plane);

    /**
     * concatenates this point and p in a new point
     *
     * @param p the point to concatenate with this point
     * @return a new point
     */
    public Point concat(Point p);

    /**
     * concatenates a single value onto this point.
     *
     * @param d
     * @return
     */
    public Point concat(double d);


    /**
     * distance function Lp2
     *
     * @param mp The point distant from this one.
     * @return The distance from this point to the given point.
     */
    public double d(Point mp);

    /**
     * this point exists in n dimensional space
     *
     * @return the number of dimensions the point is defined in.
     */
    public int dim();

    /**
     * the direction this vector is pointed in
     *
     * @return
     */
    public Point dir();

    public double distSq(Point mp);

    /**
     * the dot product between this point and p inner product Will truncate the
     * longer point if they're not equal in length.
     *
     * @param p
     * @return
     */
    public double dot(Point p);

    /**
     * The inner product of the two vectors defaults to dot product.  
     * @param v
     * @return 
     */
    @Override
    public default double ip(Point v) {
        return dot(v);
    }
    
    

    public Point dot(Matrix m);

    /**
     * does this point have the same x,y as p
     *
     * @param p
     * @return
     */
    public boolean equals(Point p);

    public boolean equals(Object obj);

    /**
     * is this point really near p?
     *
     * @param p the point near by
     * @param acc the distance aloud to p
     * @return
     */
    public boolean equals(Point p, double acc);

    /**
     * returns p_i which will be 0 if this point doesn't have i dimensions
     *
     * @param i
     * @return
     */
    public double get(int i);

    public int hashCode();

    
    /**
     *
     * @return this point is defined and has real values
     */
    public boolean isReal();

    /**
     * The magnitude of the vector reprisented by this point
     *
     * @return The distance of this point from the origan;
     */
    public double magnitude();

    public PointSparse mapToSparse(DoubleFunction<Double> f);
    public PointD mapToDense(DoubleFunction<Double> f);

    /**
     * @see plus
     * @param p
     * @return
     */
    public Point minus(Point p);

    
    public Matrix mult(Matrix matrix);

    /**
     * the sum of this point and another
     *
     * @param p the other point
     * @return the sum of the two points
     */
    public Point plus(Point p);

    @Override
    public default Point sum(Point v) {
        return plus(v);
    }
    
    /**
     * sets the values of this point to a sub array.
     *
     * @param x the array
     * @param srcStartPos the starting index of the MyPoint in the array
     * @return start this point
     */
    
    public DoubleStream stream();
    
    public PointD asDense();
    public PointSparse asSparse();
    
    public default double x(){
        return get(0);
    }
    public default double y(){
        return get(1);
    }
    public default double z(){
        return get(2);
    }
    
}
