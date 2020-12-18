package realFunction;

import RnSpace.points.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import listTools.MergeSort;


/**
 * Lagrange's method of interpolation
 * @author dov
 */
public class Polynomial extends RToRFunc{
    private Point2d[] points;


    private void removeDoubleXs(){
        for(int i = 0; i < points.length - 1; i++)
            if(points[i].x() == points[i+1].x()){
                Point2d[] temp = points;
                points = new Point2d[temp.length - 1];
                for(int j = 0; j < i; j++)
                    points[j] = temp[j];
                for(int j = i+1; j < temp.length; j++)
                    points[j-1] = temp[j];
                removeDoubleXs();
                return;
            }
    }
    /**
     * The constructor
     * @param points the polynomial will pass through
     * @param a
     * @param b
     */
    public Polynomial(Point2d[] points, double a, double b) {
        super(a, b);
        Arrays.sort(points, (x1,x2) -> x1.x() - x2.x() < 0? -1:1);
        this.points = points;
        removeDoubleXs();
    }

    /**
     *
     * @return the degree of the polynomial
     */
    public int deg(){
        return points.length;
    }

    private double x(int i){
        return points[i].x();
    }
    private double y(int i){
        return points[i].y();
    }
    private double l(int j, double x){
        double l = 1;
        for(int m = 0; m < points.length; m++){
            if(m != j)
                l *= (x - x(m))/(x(j) - x(m));
        }
        return l;
    }

    /**
     * the constructor
     * @param points an array of points the polynomial passes through
     */
    public Polynomial(Point2d[] points) {
        super(points[0].x(), points[points.length -1].x());
        this.points = points;
    }

    @Override
    public double of(double t) {
        double of = 0;
        for(int j = 0; j < points.length; j++)
            of += y(j)*l(j,t);
        return of;
    }

    public Point2d[] getPoints() {
        return points;
    }



}
