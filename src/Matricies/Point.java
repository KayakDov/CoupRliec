/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Matricies;

import Convex.ConvexSet;
import Convex.Linear.Plane;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import java.util.stream.DoubleStream;
import org.ejml.data.DMatrix;
import org.ejml.data.DMatrix1Row;

/**
 *
 * @author dov
 */
public interface Point extends Matrix{

    Matrix T();

    /**
     * Is this point above the plane
     *
     * @param plane
     * @return
     */
    boolean above(Plane plane);

    PointDense addToMe(PointDense p);

    /**
     *
     * @return the point as an array
     */
    double[] array();

    /**
     * The average value in the point
     *
     * @return the average value in the point
     */
    double avg();

    /**
     * is this point below the plane
     *
     * @param plane
     * @return
     */
    boolean below(Plane plane);

    /**
     * concatenates this point and p in a new point
     *
     * @param p the point to concatenate with this point
     * @return a new point
     */
    PointDense concat(PointDense p);

    /**
     * concatenates a single value onto this point.
     *
     * @param d
     * @return
     */
    PointDense concat(double d);

    /**
     * The cross product of this vector and another. Make sure both are 3
     * dimensional vectors.
     *
     * @param p the other point
     * @return the cross product of the two points
     */
    PointDense cross(PointDense p);

    /**
     * distance function Lp2
     *
     * @param mp The point distant from this one.
     * @return The distance from this point to the given point.
     */
    double d(PointDense mp);

    /**
     * this point exists in n dimensional space
     *
     * @return the number of dimensions the point is defined in.
     */
    int dim();

    /**
     * the direction this vector is pointed in
     *
     * @return
     */
    PointDense dir();

    double distSq(PointDense mp);

    /**
     * the dot product between this point and p inner product Will truncate the
     * longer point if they're not equal in length.
     *
     * @param p
     * @return
     */
    double dot(PointDense p);

    /**
     * the dot product between this point and p
     *
     * @param p
     * @return
     */
    double dot(double[] p);

    PointDense dot(Matrix m);

    /**
     * does this point have the same x,y as p
     *
     * @param p
     * @return
     */
    boolean equals(PointDense p);

    boolean equals(Object obj);

    /**
     * is this point really near p?
     *
     * @param p the point near by
     * @param acc the distance aloud to p
     * @return
     */
    boolean equals(PointDense p, double acc);

    /**
     * returns p_i which will be 0 if this point doesn't have i dimensions
     *
     * @param i
     * @return
     */
    double get(int i);

    int hashCode();

    /**
     * The projection of this vector onto start unit vector
     *
     * @param p the unit vector this one is projected onto
     * @return the result of shadowing this vector onto start unit vector
     */
    PointDense inDir(PointDense p);

    /**
     *
     * @return this point is defined and has real values
     */
    boolean isReal();

    /**
     * The magnitude of the vector reprisented by this point
     *
     * @return The distance of this point from the origan;
     */
    double magnitude();

    PointDense map(DoubleFunction<Double> f);

    <T> List mapToList(Function<Double, T> f);

    /**
     * @see plus
     * @param p
     * @return
     */
    PointDense minus(PointDense p);

    /**
     * scalar multiplication
     *
     * @param k
     * @return
     */
    PointDense mult(double k);

    Matrix mult(Matrix matrix);

    PointDense multMe(double k);

    Matrix outerProduct(PointDense p);

    /**
     * the sum of this point and another
     *
     * @param p the other point
     * @return the sum of the two points
     */
    PointDense plus(PointDense p);

    /**
     * The projection of this point onto a convex set.
     *
     * @param cs the convex set
     * @return the projection of this point on the convex set
     */
    PointDense proj(ConvexSet cs);

    /**
     * Is one of these vectors start multiple of the other.
     *
     * @param p
     * @param epsilon
     * @return
     */
    boolean sameDirection(PointDense p, double epsilon);

    /**
     * Sets the value of the point
     *
     * @param i the index of the value
     * @param y the new value at that index
     * @return this point
     */
    PointDense set(int i, double y);

    /**
     * Sets the values of this point to those in the array.
     *
     * @param x an array of scalars
     * @return this point
     */
    PointDense set(double[] x);

    /**
     * sets this point equal to the given point
     *
     * @param x
     * @return
     */
    PointDense set(PointDense x);

    PointDense setAll(IntToDoubleFunction f);

    /**
     * sets the values of this point to a sub array.
     *
     * @param x the array
     * @param srcStartPos the starting index of the MyPoint in the array
     * @return start this point
     */
    PointDense setFromSubArray(double[] x, int srcStartPos);

    DoubleStream stream();
    
}
