package RnSpace.curves;

import RnSpcae.RnToRmFunc.VectorField;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;
import FuncInterfaces.RToR;
import FuncInterfaces.RToRn;
import Convex.Cube;
import realFunction.RToRFunc;
import realFunction.RToRFuncStr;
import static java.lang.Math.*;
import Convex.Interval;

/**
 *
 * @author Dov
 */
public abstract class Curve implements RToRn {

    public String name = "";
    public Interval I;

    public Curve setName(String name) {
        this.name = name;
        return this;
    }

    

    public abstract Point of(double t);

    /**
     * the dimension of the range of the curve
     * @return 
     */
    public int dim() {
        return of(I.start()).dim();
    }

    /**
     * The constructor c:[start,end] -> R^n
     * @param a
     * @param b 
     */
    public Curve(double a, double b) {
        this(new Interval(a, b));
    }

    /**
     * Creates start new curve on the interval
     * @param I 
     */
    public Curve(Interval I) {
        this.I = I;
    }
 
       /**
     * Creates start new curve on the interval
     * @param I 
     */
    public Curve(Cube I) {
        this.I = (Interval)I;
    }
    

    /**
     * gets a set of points that are defining for the curve. If the curve is
     * smooth, best to return a lot of points along the curve.
     *
     * @return
     */
    public Point[] getPoints(int n) {
        return (Point[])I.streamR(n).mapToObj(x -> of(x)).toArray();
    }
    private static int fileNum = 0;

    boolean definedOn(double t) {
        return I.contains(t);
    }

    public Curve plus(Curve c) {
        Curve g = this;
        return new Curve(c.I.intersection(g.I)) {
            @Override
            public Point of(double t) {
                return g.of(t).plus(c.of(t));
            }
        };
    }

    public Curve minus(Curve c) {
        Curve g = this;
        return new Curve(max(c.I.start(), g.I.start()), min(c.I.end(), g.I.end())) {
            @Override
            public Point of(double t) {
                return g.of(t).minus(c.of(t));
            }
        };
    }

    public double arcLength() {
        return arcLength(1e-7);
    }

    public double arcLength(double EPSILON) {
        double arcLength = 0;
        Point p1;
        Point p2 = of(I.start());
        for (double t = I.start(); t < I.end(); t += EPSILON) {
            p1 = p2;
            p2 = of(t + EPSILON);
            arcLength += p1.d(p2);
        }
        return arcLength;
    }

    public double work(VectorField vf) {
        return work(vf, 1e-5);
    }

    public double work(VectorField vf, double EPSILON) {
        double work = 0;
        Point prev;
        Point current = of(I.start()), next = of(I.start());
        Point curveV;
        Point totV, dir;
        for (double t = I.start(); t < I.end(); t += EPSILON) {
            prev = current;
            current = next;
            next = of(t + EPSILON);

            curveV = current.minus(prev).mult(1 / EPSILON);
            totV = curveV.plus(vf.of(of(t)).mult(EPSILON));

            dir = next.minus(current).mult(1 / EPSILON);
            work += dir.minus(totV).magnitude();
        }
        return work;
    }

    public static Curve st(RToRn g) {
        return st(g, new Interval(0, 1));
    }
    
    public static Curve st(RToRn g, double a, double b) {
        return st(g, new Interval(a, b));
    }
    public static Curve st(RToRn g, Interval I) {
        return new Curve(I) {
            @Override
            public Point of(double t) {
                return g.of(t);
            }
        };
    }

    public static Curve st(RToR x, RToR y, double a, double b) {
        return st(new RToRn() {
            @Override
            public Point of(double t) {
                return new Point(x.of(t), y.of(t));
            }
        }, a, b);
    }

    public static Curve st(RToR x, RToR y, RToR z, double a, double b) {
        return st(new RToRn() {
            @Override
            public Point of(double t) {
                return new Point(x.of(t), y.of(t), z.of(t));
            }
        }, a, b);
    }

    public static Curve st(String y, double a, double b) {
        return new Curve(a, b) {
            RToRFuncStr f = new RToRFuncStr(y);

            @Override
            public Point of(double t) {
                return new Point(Point.oneD(f.of(t)));
            }
        };
    }

    public static Curve st(String x, String y, double a, double b) {
        return new Curve(a, b) {
            RToRFuncStr gy = new RToRFuncStr(y);
            RToRFuncStr gx = new RToRFuncStr(x);

            @Override
            public Point of(double t) {
                return new Point(gx.of(t), gy.of(t));
            }
        };
    }

    public static Curve st(String x, String y, String z, double a, double b) {
        return new Curve(a, b) {
            RToRFuncStr gy = new RToRFuncStr(y);
            RToRFuncStr gx = new RToRFuncStr(x);
            RToRFuncStr gz = new RToRFuncStr(z);

            @Override
            public Point of(double t) {
                return new Point(gx.of(t), gy.of(t), gz.of(t));
            }
        };
    }

    public RToRFunc get(int i) {
        Curve c = this;
        return new RToRFunc(I) {
            @Override
            public double of(double t) {
                return c.of(t).get(i);
            }
        };
    }

    public Curve d(final double dt) {
        Curve c = this;

        return st(t -> c.of(t - 2 * dt).minus(c.of(t - dt).mult(8)).
                plus(c.of(t + dt).mult(8)).minus(of(t + 2 * dt)).multMe(1 / 12),
                I);
    }

    public RToRFunc magnitude() {
        Curve g = this;
        return new RToRFunc(I) {
            @Override
            public double of(double t) {
                return g.of(t).magnitude();
            }
        };

    }

    /**
     * Uses the bisection method to find the minimum value on this curve through
 start function.
     * @param f the function the curve runs through.
     * @param dt the differentiation constant, start small number. 
     * @param end the end condition, also start small number.
     * @return the value in the setDomain of this curve where f has the highest 
 value.
     */
    public double bisectionMethod(RnToRFunc f, double dt, double end){
       return f.of(this).bisectionMethodMin(dt, end, I);       
    }

    /**
     * changes the domain of this curve.
     * @param I the new domain.
     * @return  this function.
     */
    public Curve setI(Interval I) {
        this.I = I;
        return this;
    }
    
    /**
     * changes the domain of this curve.
     * @param a the beginning of the interval
     * @param b the end of the interval
     * @return  this function.
     */
    public Curve setI(double a, double b) {
        return setI(new Interval(a, b));
    }
    
    
    /**
     * the start point of the range of the curve
     * @return 
     */
    public Point ofA(){
        return of(I.start());
    }
    
    /**
     * the end point of the range
     * @return 
     */
    public Point ofB(){
        return of(I.end());
    }
    
    /**
     * the beginning of the domain
     * @return 
     */
    public double a(){
        return I.start();
    }
    /**
     * the end of the domain
     * @return 
     */
    public double b(){
        return I.end();
    }
}
