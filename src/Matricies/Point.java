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

    Point addToMe(Point p);

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
    Point concat(Point p);

    /**
     * concatenates a single value onto this point.
     *
     * @param d
     * @return
     */
    Point concat(double d);

    /**
     * The cross product of this vector and another. Make sure both are 3
     * dimensional vectors.
     *
     * @param p the other point
     * @return the cross product of the two points
     */
    Point cross(Point p);

    /**
     * distance function Lp2
     *
     * @param mp The point distant from this one.
     * @return The distance from this point to the given point.
     */
    double d(Point mp);

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
    Point dir();

    double distSq(Point mp);

    /**
     * the dot product between this point and p inner product Will truncate the
     * longer point if they're not equal in length.
     *
     * @param p
     * @return
     */
    double dot(Point p);

    Point dot(Matrix m);

    /**
     * does this point have the same x,y as p
     *
     * @param p
     * @return
     */
    boolean equals(Point p);

    boolean equals(Object obj);

    /**
     * is this point really near p?
     *
     * @param p the point near by
     * @param acc the distance aloud to p
     * @return
     */
    boolean equals(Point p, double acc);

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
    Point inDir(Point p);

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

    Point map(DoubleFunction<Double> f);

    <T> List mapToList(Function<Double, T> f);

    /**
     * @see plus
     * @param p
     * @return
     */
    Point minus(Point p);

    /**
     * scalar multiplication
     *
     * @param k
     * @return
     */
    Point mult(double k);

    Matrix mult(Matrix matrix);

    Point multMe(double k);

    Matrix outerProduct(Point p);

    /**
     * the sum of this point and another
     *
     * @param p the other point
     * @return the sum of the two points
     */
    Point plus(Point p);

    /**
     * The projection of this point onto a convex set.
     *
     * @param cs the convex set
     * @return the projection of this point on the convex set
     */
    Point proj(ConvexSet cs);

    /**
     * Is one of these vectors start multiple of the other.
     *
     * @param p
     * @param epsilon
     * @return
     */
    boolean sameDirection(Point p, double epsilon);

    /**
     * Sets the value of the point
     *
     * @param i the index of the value
     * @param y the new value at that index
     * @return this point
     */
    Point set(int i, double y);

    /**
     * Sets the values of this point to those in the array.
     *
     * @param x an array of scalars
     * @return this point
     */
    Point set(double[] x);

    /**
     * sets this point equal to the given point
     *
     * @param x
     * @return
     */
    Point set(Point x);

    Point setAll(IntToDoubleFunction f);

    /**
     * sets the values of this point to a sub array.
     *
     * @param x the array
     * @param srcStartPos the starting index of the MyPoint in the array
     * @return start this point
     */
    Point setFromSubArray(double[] x, int srcStartPos);

    DoubleStream stream();
    
}
