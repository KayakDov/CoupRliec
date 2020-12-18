package RnSpace.rntor;

import FuncInterfaces.RnToR;
import FuncInterfaces.RnToRn;
import RnSpace.points.Point;

/**
 *
 * @author Kayak
 */
public class Gradient implements RnToRn{

    private double dt;
    private RnToR f;

    public Gradient( RnToR f,double dt) {
        this.dt = dt;
        this.f = f;
    }

    @Override
    public Point of(double[] x) {
        return new Point(x.length).setAll(i->f.d(i, dt).of(x));
    }
    
}
