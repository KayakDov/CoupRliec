package realFunction;

/**
 *A line l|R -> R defined by two points
 * @author dov
 * nts
 * @author dov
 */
public class Line extends RToRFunc{

    private Point2d p1, p2;

    /**
     * the constructor
     * @param p1 a point the line passes through
     * @param p2 another point the line passes through
     * 
     */
    
    public Line(Point2d p1, Point2d p2) {
        super(null);
        if (p2.x() < p1.x()){
            this.p1 = p2;
            this.p2 = p1;
        }
        else {
            this.p1 = p1;
            this.p2 = p2;
        }
        super.setDomain(p1.x(), p2.x());
    }

    public void setP1(Point2d p1) {
        this.p1 = p1;
    }

    public void setP2(Point2d p2) {
        this.p2 = p2;
    }



    public Line(double x1, double y1, double x2, double y2) {
        this(new Point2d(x1, y1), new Point2d(x2, y2));
    }

    @Override
    public double of(double t) {
        return (p2.y() - p1.y()) * (t - p1.x()) / (p2.x() - p1.x()) + p1.y();
    }

    /**
     * is t between a and and b
     * @param t
     * @return true if t is in [a,b]
     */
    public boolean accepts(double t){
        return p1.x() <= t && t < p2.x();
    }

    /**
     * currently written only for entirely positive line segments
     * @return
     */
    public double integral(){
        return (p2.x() - p1.x())*Math.min(p1.y(), p2.y()) +
                .5*Math.abs(p2.y() - p1.y())*(p2.x() - p1.x());
    }

    @Override
    public void setDomain(double a, double b) {
        p1 = new Point2d(a, of(a));
        p2 = new Point2d(b, of(b));
        super.setDomain(a, b);
    }
    
}
