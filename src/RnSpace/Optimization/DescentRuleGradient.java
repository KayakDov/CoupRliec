package RnSpace.Optimization;

/**
 *
 * @author Kayak
 */
import FuncInterfaces.RnToR;
import RnSpace.points.Point;
import RnSpace.rntor.Gradient;
import RnSpace.rntor.Hessian;
import RnSpace.rntor.RnToRFunc;

/**
 * A class for building descent rules fro descent methods.
 */
public abstract class DescentRuleGradient extends DescentRule {

    protected Point gradX;
    protected Gradient grad;
    protected Hessian hessian;

    /**
     * The function to find the minimum of
     *
     * @param f
     * @param end
     * @param dt
     */
    public DescentRuleGradient(RnToR f, double end, double dt) {
        super(f, end, dt);
        grad = new Gradient(f, dt);
        hessian = new Hessian(f, dt);
    }
    
    
    @Override
    public boolean end(Point x) {
        return (gradX = grad.of(x)).magnitude() < end;
    }

    public boolean isGoodChoice(Point x, Point xn) {

        return getF().of(x) - dt > getF().of(xn) && !xn.equals(x, dt);

    }
}
