/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RnSpace.Optimization;

import RnSpace.curves.Line;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;

/**
 *
 * @author Kayak
 */
public class CoordinateDescentRule extends DescentRule {

    public CoordinateDescentRule(RnToRFunc f, double end, double dt) {
        super(f, end, dt);
    }

    private Point lastX;

    @Override
    public Point of(Point x) {
        for(int i = 0; i < x.dim(); i++)
                x = new Line(x, x.shift(i, end)).min(getF(), end);
        return x;
    }
    
    

    @Override
    public boolean end(Point x) {
        return lastX.d(x) < end;
    }

    @Override
    public Point of(double[] x) {
        return of(new Point(x));
    }

}
