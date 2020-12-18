
package realFunction;

import java.util.ArrayList;

/**
 * f|R -> R
 * @author dov
 */
public class JoinedLines extends  RToRFunc{
    private ArrayList<Line> lines = new ArrayList<Line>();

   /**
     * constructor
     * @param points start set of points to be joined by lines|R -> R
     */
    public JoinedLines(ArrayList<Point2d> points) {
        super(null);
        if (points.size() > 0) {
            setLines(points);
            setDomain(lines.get(0).I.start(), lines.get(lines.size() - 1).I.end());
        }
    }
    protected void setLines(ArrayList<Point2d> points){
        lines.clear();
        for(int i = 0; i < points.size() - 1; i++)
            lines.add(new Line(points.get(i),points.get(i + 1)));
    }
    /**
     *
     * @param points
     */
    public JoinedLines(Point2d[] points) {
        super(null);
        for(int i = 0; i < points.length - 1; i++)
            lines.add(new Line(points[i],points[i+1]));
        setDomain(lines.get(0).getI().start(), lines.get(lines.size()-1).getI().end());
    }

    @Override
    public double of(double t) {
        for(Line line: lines)
            if(line.definedOn(t)) return line.of(t);
        if(t < I.start()) return lines.get(0).of(t);
        if(t > I.end()) return lines.get(lines.size()-1).of(t);

        return Double.NaN;
    }
    /**
     * only works on positive line segments for now
     * @return  the integral value of this function from start to be
     */
    public double integral(){
        double I = 0;
        for(Line line: lines){
            I += line.integral();
        }
        return I;
    }

    /**
     * for now, this only lets you expand the joined lines, not retract
     * @param a
     * @param b
     */
    @Override
    public void setDomain(double a, double b) {
        if(a < lines.get(0).I.start())
            lines.get(0).setDomain(a, lines.get(0).I.end());
        if(b > lines.get(lines.size()-1).I.end())
            lines.get(lines.size()-1).setDomain(lines.get(lines.size()-1).I.start(), b);
        super.setDomain(a, b);
    }

}

