package realFunction;

import java.util.Random;
import RnSpace.points.Point;


/**
 *
 * @author dov
 */
public class Point2d extends Point{
    
    /**
     * constructor
     * @param x
     * @param y
     */
    public Point2d(double x, double y) {
        super(x, y);
    }


    /**
     * constructor
     */
    public Point2d() {
        this(0,0);
    }
    /**
     * constructor
     * @param x a vector with 2 values
     */
    public Point2d(double[] x){
        this(x[0], x[1]);
    }
    
    public Point2d(Point x){
        this(x.x(), x.y());
    }
    /**
     * @return (0,0)
     */
    public static RnSpace.points.Point Origon(){
        return Origin(2);
    }

    private static Random rand = new Random();
    /**
     * creates a random Point on the disk centered at the origon with a
     * radius of mag
     * @param mag the radius of the disk containing the point.
     * @return a random point
     */
    public static Point2d Random(double mag){
        double theta = rand.nextDouble() * 2 * Math.PI,
               r = rand.nextDouble() * mag;
        return new Point2d(r*Math.cos(theta), r*Math.sin(theta));
    }
    public double y(){
        return get(1);
    }
    
    public double x(){
        return get(0);
    }
    
    public void setX(double x){
        set(0, x);
    }
    public void setY(double y){
        set(1, y);
    }
 
}
