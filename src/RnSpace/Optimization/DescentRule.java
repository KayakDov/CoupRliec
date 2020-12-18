package RnSpace.Optimization;

import FuncInterfaces.RnToR;
import FuncInterfaces.RnToRn;
import RnSpcae.RnToRmFunc.VectorField;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;

/**
 * A class for building descent rules fro descent methods.
 */
public abstract class DescentRule implements RnToRn{

    private RnToR f;
    public final double end, dt;

    /**
     * The function to find the minimum of
     *
     * @param end for the end condition
     * @param f
     * @param dt
     */
    public DescentRule(RnToR f, double end, double dt) {
        this.f = f;
        this.end = end;
        this.dt = dt;
    }

    public abstract Point of(Point x);

    /**
     * Are we at a minimum point?
     *
     * @param x the point to check if it's a minimum
     * @return true if the point is a minimum
     */
    public abstract boolean end(Point x);

    public void setF(RnToR f) {
        this.f = f;
    }

    public RnToR getF() {
        return f;
    }

}
