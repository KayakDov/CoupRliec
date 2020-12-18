package RnSpace.curves;

import RnSpace.curves.Line;
import RnSpace.points.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import Convex.Interval;
import Matricies.Matrix;

/**
 *
 * @author Dov
 */
public class JoinedLines extends Curve {

    protected LinkedList<Line> lines;

    public JoinedLines(Line[] ls) {
        super(ls[0].I.start(), ls[ls.length - 1].I.end());
        this.lines = new LinkedList<>();
        lines.addAll(Arrays.asList(ls));
    }

    public JoinedLines(Point[] points, double a, double b) {
        this(a, b);
        for (int i = 0; i < points.length - 1; i++)
            lines.add(new Line(points[i], points[i + 1], i * (b - a) / points.length, (i + 1) * (b - a) / points.length));
    }

    /**
     * Constructor
     *
     * @param points points to be linked by lines
     */
    public JoinedLines(Point[] points, double a) {
        this(a, a + 1);
        double b = a;
        for (int i = 0; i < points.length - 1; i++)
            b += points[i].d(points[i + 1]);
        setI(a, b);
        lines.add(new Line(points[0], points[1], a, points[0].d(points[1])));
        for (int i = 1; i < points.length - 1; i++)
            lines.add(new Line(points[i], points[i + 1], lines.getLast().b(), lines.getLast().b() + points[i].d(points[i + 1])));
    }

    /**
     * Constructor The domain for this constructor is 0,1. The lines maintain
     * their length ratio, one to each other.
     *
     * @param points points to be linked by lines
     */
    public JoinedLines(Matrix points) {
        this(0, 1);
        double b = 0;
        for (int i = 0; i < points.rows - 1; i++)
            b += points.row(i).d(points.row(i + 1));

        lines.add(new Line(points.row(0), points.row(1), 0, points.row(0).d(points.row(1)) / b));
        for (int i = 1; i < points.rows - 1; i++)
            lines.add(new Line(points.row(i), points.row(i+1), 
                    lines.getLast().b(), 
                    lines.getLast().b() + points.row(i).d(points.row(i+1)) / b));
    }

    public JoinedLines(Curve c, int n) {
        this(c.I);
        Point current;

        Point next = c.of(I.start());
        for (int i = 0; i < n; i++) {
            current = next;
            next = c.of(I.start() + (i + 1) * I.len() / n);
            lines.add(new Line(current, next, I.start() + i * I.len() / n, I.start() + (i + 1) * I.len() / n));
        }
    }

    protected JoinedLines(double a, double b) {
        this(new Interval(a, b));
    }

    protected JoinedLines(Interval I) {
        super(I);
        this.lines = new LinkedList<>();
    }

    @Override
    public Point of(double t) {
        if (t <= I.start()) return lines.get(0).of(t);
        if (t >= I.end()) return lines.get(lines.size() - 1).of(t);
        int n = lines.size();

        for (Iterator<Line> iter = lines.iterator(); iter.hasNext();) {
            Line next = iter.next();
            if (next.definedOn(t)) return next.of(t);
        }
        return null;
    }

    public Point[] getPoints() {
        Point[] points = new Point[lines.size() + 1];

        for (int i = 0; i < lines.size(); i++) points[i] = lines.get(i).getP1();

        points[lines.size()] = lines.get(lines.size() - 1).getP2();

        return points;
    }

    @Override
    public int dim() {
        int maxDim = 0;

        for (Line l : lines)
            if (l.dim() > maxDim) maxDim = l.dim();
        return maxDim;
    }

}
