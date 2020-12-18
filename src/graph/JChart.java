package graph;

import RnSpace.curves.Curve;
import Convex.Interval;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import realFunction.RToRFunc;

/**
 *
 * @author Dov Neimand
 */
public class JChart {

    private final XYSeriesCollection series = new XYSeriesCollection();
    private final ApplicationFrame frame = new ApplicationFrame("");
    private final Interval range;

    public JChart(Interval range, boolean hasLegend){
        JFreeChart chart = ChartFactory.createXYLineChart("", "x", "f(x)", series);
        this.range = range;
        ChartPanel panel = new ChartPanel(chart);
        frame.setContentPane(panel);
        frame.setSize(400, 400);
        panel.getChart().getLegend().setVisible(hasLegend);
        frame.setVisible(true);
    }
    
    public JChart(Interval range){
        this(range, true);
    }
    
    
    public JChart(Interval range, RnToRFunc f, double dx){
        this(range, true);
        addFunction(f, dx);
    }
    
    public JChart(){
        this(Interval.realLine(), true);
    }

    public JChart addFunction(RnToRFunc f, double dx) {
        return addFunction(
                RToRFunc.st(x -> f.of(Point.oneD(x)), f.name, 
                f.getDom().getA().get(0), 
                f.getDom().getB().get(0)), 
                dx);
    }

    public JChart addFunction(RToRFunc f, double dx) {
        
        addFunction(Curve.st(t -> new Point(t, f.of(t)), f.I).setName(f.getName()), dx);
        return this;
    }
    public void addFunction(Curve c, double dx){
        if (c.dim() == 2) {
            XYSeries xy = new XYSeries(c.name);
            c.I.streamR(dx).forEach(x -> {
                Point p = c.of(x);
                xy.add(p.x(), range.onInterval(p.y()));
//                System.out.println(p.x() + "," + range.onInterval(p.y()));
//                System.out.println(p.x() + "," + p.y());
            });
            series.addSeries(xy);
        } else
            throw new RuntimeException("This function's domain has too many dimensions to graph.");
    }


}
