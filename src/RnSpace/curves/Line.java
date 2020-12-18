package RnSpace.curves;

import Convex.Linear.AffineSpace;
import Convex.Linear.LinearSpace;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;
import FuncInterfaces.RToR;
import FuncInterfaces.RnToR;
import RnSpace.Optimization.Min;
import RnSpace.points.Simplex;
import RnSpace.rntor.GraphX;
import realFunction.RToRFunc;

/**
 *
 * @author dov
 */
public class Line extends Curve {

    private final Point p1, p2;

    public Line(final Point p1, final Point p2, double a, double b) {
        super(a, b);
        this.p1 = p1;
        this.p2 = p2;
    }

    public Line(Point p1, Point p2) {
        this(p1, p2, 0, 1);
    }

    public int dim() {
        return Math.max(p1.dim(), p2.dim());
    }

    @Override
    public Point of(double t) {
        return (p1.mult((I.end() - t) / I.len())).plus(p2.mult((I.start() - t) / -I.len()));
    }

    public Point[] getPoints() {
        return new Point[]{p1, p2};
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    /**
     * Gives the point on the line that has the lowest value in start function
     *
     * @param f the function that is applied to every value on this line.
     * @param end the end condition
     * @return the point in the line's domain that has the lowest value in f.
     */
    public Point min(RnToR f, double end) {
        Point min; boolean improvement;
        double maxAcceptedValue = f.of(p1);
        do {
            
            min = minUnsafe(f, end);
            improvement = f.of(min) < maxAcceptedValue; 
            if(!improvement) System.err.println("line::min not working");
            end /= 2;
//            System.out.println("distance from old x to new is " + min.d(p1));
        } while (!improvement);
        return min;

    }

    public Point minUnsafe(RnToR f, double end) {
        RToRFunc rFunc = RToRFunc.st((RToR) t -> f.of(of(t)), I);
        Simplex simp = new Simplex();
        simp.add(new GraphX(rFunc, I.getA()));
        simp.add(new GraphX(rFunc, I.getB()));
        double t = Min.nelderMead(rFunc, simp, end).get(0).x();
        return of(t);
    }
    
    /**
     * The affine space that describes this line.
     * @return 
     */
    public AffineSpace affineSpace(){
        return new AffineSpace(LinearSpace.colSpace((p2.minus(p1))), p1);
    }
}
