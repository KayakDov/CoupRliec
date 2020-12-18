package RnSpcae.RnToRmFunc;

import RnSpace.points.Point;
import java.util.Iterator;

/**
 *
 * @author Dov
 * 
 * DOES NOT WORK i don't yet know why.
 */
public abstract class RungeKutta45 implements Iterator<Point>, Iterable<Point> {

    private Point p;
    private double t, h;
    final double a, b, maxErr, maxStep;
    final private Point p0;

    public double getT() {
        return t;
    }

    public double getB() {
        return b;
    }
    

    public RungeKutta45(Point p0, double a, double b, double maxErr, double maxStep) {
        this.p0 = p0;
        this.a = a;
        this.p = p0;
        this.t = a;
        this.b = b;
        this.h = maxStep/2;
        this.maxErr = maxErr;
        this.maxStep = maxStep;
    }
    
    public RungeKutta45(Point p0, double a, double b) {
        this(p0, a, b, 1E-10, (b - a)*1E-4);
    }

    
    @Override
    public boolean hasNext() {
        return t < b &&  p != null;
    }

    @Override
    public void remove() {
        next();
    }

    
    
    public abstract Point f(double t, Point x);

    /**
     * there method may return a null if there is no next.
     * @return 
     */
    @Override
    public Point next() {
        
        //System.out.println("RungeKutta45::next");
        
        Point p2;
        
        double err, h1;

        int count = 0;

        do {
            p2 = nextPoint(p, t, h, 2);
            
           //  System.out.println("ode45::next boo p2 = " + p2);

            err = nextPoint(p, t, h).d(p2);
          //  System.out.println("err = " + err);
            
            h1 = h;            
            h = Math.min(h * Math.pow(maxErr / err, .2), maxStep);

            count++;
            if (count > 15) {
                h = h / 2;
                count = 10;
            }
           // if(count > 1)System.out.println("ode45::next boo ------------------------------------------");


        } while (err > maxErr && h > 1E-16);
             
        //System.out.println("\nh = " + h);
        if (!p2.isReal()  || h < 1E-16){
            p = null;
            return null;
        }
        
        h = h1;
        t += h1;
        
        //System.out.println(p);
        
        p = new Point(p2);
        
        return new Point(p2);
    }

    private Point nextPoint(final Point x, final double t, final double h) {

        Point[] k = new Point[4];

        k[0] = f(t, x).mult(h);
        k[1] = f(t + h / 2, x.plus(k[0].mult(1.0 / 2))).mult(h);
        k[2] = f(t + h / 2, x.plus(k[1].mult(1.0 / 2))).mult(h);
        k[3] = f(t + h, x.plus(k[2])).mult(h);

        Point nextPoint = x.plus((k[0].plus((k[1].plus(k[2])).mult(2)).plus(k[3])).mult(1.0 / 6));

        return nextPoint;
    }
    private Point nextPoint(Point x, double t, double h, int numSteps){
        Point p = new Point(x);
        h = h / numSteps;
        for(int i = 0; i < numSteps; i++)
            p = nextPoint(p, t + i * h, h);
        return p;
    }
    
    public Point current(){
        return p;
    }

    /**
     * resets the iterator.
     * @return 
     */
    @Override
    public Iterator<Point> iterator() {
        t = a;
        p = p0;
        return this;
    }
    
    
}
