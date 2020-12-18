package realFunction;

import FuncInterfaces.RToR;
import java.util.ArrayList;


public abstract class FirstOrdODE extends JoinedLines{

    /**
     * y' = f(y,t)
     * @param y
     * @param t
     * @return
     */
    public abstract double f(double y, double t);
    private double y0;

    /**
     * y0 = y(start)
 y'=f(y, t)   start < t < end
     *
     * @param y0
     * @param a
     * @param b
     */
    public FirstOrdODE(double y0, double a, double b) {
        super(new Point2d[0]);
        setDomain(a, b);
        this.y0 = y0;
    }

    /**
     * different numerical methods that can be used to solve the ODE
     */
    public enum CompMethod{ForewardEular, BackwardEular, RungeKutta}
    private CompMethod cm = CompMethod.RungeKutta;

    /**
     *  sets the method to solve
     * @param cm the medthod
     * @param stepSize the step size the method should take
     */
    public void setCompMethod(CompMethod cm, double stepSize) {
        this.cm = cm;
        this.points = null;
        this.stepSize = stepSize;
        setLines();
    }

    private double stepSize = .0001;
    protected ArrayList<Point2d> points;

    /**
     * Generates the points that make up this line function using the
     * selected method.
     */
    private void setLines(){

        points = new ArrayList<>((int)(I.len()/stepSize) + 1);

        points.add(new Point2d(I.start(), y0));

        for(double t = I.start() + stepSize; t < I.end() + stepSize; t += stepSize){
            double lastY = points.get(points.size() - 1).y();
            switch(cm){
                case BackwardEular:
                    points.add(nextBackwardPoint(lastY, t)); break;
                case ForewardEular:
                    points.add(new Point2d(t, lastY + stepSize*f(lastY,t))); break;
                case RungeKutta:
                    points.add(nextRungeKutta(lastY, t));
            }

        }
        setLines(points);

        /*for(MyPoint output: points)
            System.out.println(output.toString());*/
    }

    private Point2d nextBackwardPoint(final double y, double t){
        final double nextT = t + stepSize;

        RToR f = nextY -> Math.abs(nextY - (y + stepSize*f(nextY, nextT)));
            
         return new Point2d(nextT, f.min(y, stepSize/10000, .1));
    }

    @Override
    public double of(double t) {
        if (points == null)
            setLines();

        return super.of(t);
    }

    public double y0() {
        return y0;
    }

    private Point2d nextRungeKutta(double y, double t){
        double h = stepSize;
        double  k1 = h * f(y, t),
                k2 = h * f(y+ k1/2, t+h/2.0),
                k3 = h*f(y + k2/2, t + h/2),
                k4 = h * f(y + k3, t + h);
        return new Point2d(t+ h, y + (k1+ 2*(k2+k3)+k4)/6);
    }

    @Override
    public double integral() {
        if(points == null) setLines();
        return super.integral();
    }



}

