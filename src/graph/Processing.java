package graph;

import Convex.Interval;
import RnSpace.curves.Curve;
import RnSpace.curves.Curve2dAprx;
import RnSpace.points.Point;
import java.util.ArrayList;
import processing.core.PApplet;
import realFunction.Point2d;
import realFunction.RToRFunc;

/**
 *
 * @author dov
 */
public class Processing extends PApplet {

    public static ArrayList<Curve2dAprx> curves = new ArrayList<>();
    private static int width = 600, height = 600;
    private static double minX = -1, maxX = 1, minY = -1, maxY = 1;

    public static void setDim(double size) {
        setDim(-size/2, size/2, -size/2, size/2);
    }

    public static void setDim(double Xmin, double Xmax, double Ymin, double Ymax) {
        minX = Xmin;
        maxX = Xmax;
        minY = Ymin;
        maxY = Ymax;
    }
    
    public static void drawAxes(double length){
        Curve2dAprx xAxis = new Curve2dAprx(x -> x, y -> 0, new Interval(-length/2, length/2), 1);
        Curve2dAprx yAxis = new Curve2dAprx(x -> 0, y -> y, new Interval(-length/2, length/2), 1);
        addCurve(xAxis);
        addCurve(yAxis);
    }

    public static void addCurve(Curve2dAprx curve) {
        curves.add(curve);
    }

    public static Curve circleAt(Point p, double r) {
        Curve c = Curve.st((t) -> new Point(r * cos((float) t) + p.x(), r * sin((float) t)
                + p.y()), 0, 2 * PI);
        addCurve(c, 20);
        return c;
    }
    
    public static void addCurve(Curve c, int n) {
        addCurve(new Curve2dAprx(c, n));
    }
    
    public static void addCurve(RToRFunc f, int n) {
        addCurve(new Curve2dAprx(Curve.st(t -> t, t -> f.of(t), f.I.start(), f.I.end()), n));
    }

    public static void addCurve(Curve2dAprx[] curveList) {
        for (Curve2dAprx curve : curveList)
            curves.add(curve);
    }

    public Processing() {
    }

    private float plotX(double x) {
        return (float) (width / (maxX - minX) * (x - minX));
    }

    private float plotY(double y) {
        return (float) (-height / (maxY - minY) * (y - minY)) + height;
    }

    @Override
    public void settings() {
        size(width, height);
    }

    @Override
    public void draw() {
        background(255);

        for (int j = 0; j < curves.size(); j++) {
            Curve2dAprx curve = curves.get(j);
            Point2d[] points = curve.getPoints();
            for (int i = 0; i < points.length - 1; i++)
                line(
                        plotX(points[i].x()), plotY(points[i].y()),
                        plotX(points[i + 1].x()), plotY(points[i + 1].y()));
        }
    }

    public static void openWindow() {

        Processing.main("graph.Processing");
    }
}
