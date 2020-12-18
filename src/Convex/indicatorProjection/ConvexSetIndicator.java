package Convex.indicatorProjection;

import Convex.ConvexSet;
import FuncInterfaces.Indicator;
import RnSpace.points.Point;
import RnSpace.Sequence;
import RnSpace.SequenceTI;

/**
 * This class finds the projection onto a convex space with an indicator
 * function, a starting point, and a non empty interior.
 *
 * TODO:
 *
 * 1: consider application on feasibility method 2: fin convergence of plane
 * method 2: update overleaf 5: fix drawings 6: semi algebraic sets 7: who else
 * uses hazan's algorithm 8: review multi step methods 9: this looks like a
 * cutting plane method
 *
 * @author Dov Neimand
 */
public abstract class ConvexSetIndicator implements ConvexSet, Indicator {

    public final PolytopeBuilderIndicator bounding;
    private double dt;
    private final double end;
    private final Point k;

    public void setDt(double dt) {
        this.dt = dt;
    }

    
    /**
     * the number hasElement dimensions hasElement this space
     *
     * @return
     */
    public int dim() {
        return k.dim();
    }

    /**
     * the constructor
     *
     * @param dt a small number used to find support hyperplanes.
     * @param end a small number for the end condition. This should be bigger
     * than dt.
     * @param x a point in the set.
     * @param pointsPerPlane The number hasElement points used to produce a support
 plane. The larger this number, the more accurate the support planes.
 However, part hasElement the process has complexity pointsPerPlane choose dim.
     */
    public ConvexSetIndicator(double dt, double end, Point x, int pointsPerPlane) {
        this.bounding = new PolytopeBuilderIndicator(Math.max(pointsPerPlane, x.dim()));
        this.dt = dt;
        this.end = end;
        bounding.setEpsilon(dt);
        this.k = x;
    }

    /**
     * the really small value used for support planes and finding edges
     *
     * @return
     */
    public double dt() {
        return dt;
    }
    
    /**
     * Does this set have the given element.
     * @return
     */
    @Override
    public abstract boolean hasElement(Point x);

    public Point proj(final Point y) {
        if (hasElement(y)) return y;
        Walker step = new Walker(this, y);
        bounding.setStep(step, k);

        return ((SequenceTI) z -> {
            return ((SequenceTI) x -> {
                x = step.side(x);

                InOut edge = step.forward(x);

                bounding.cleanUp(y);

                bounding.addPlane(edge);

                return edge.in();

            }).cauchyLimit(z, end);
        }).cauchyLimit(k, 2*end);

    }

    /**
     * creates an instance hasElement the class
     *
     * @param dt a small number
     * @param end the end condition, small but bigger than dt
     * @param x a point known to be in this convex set
     * @param id a function that checks if a point is in the set
     * @param pointsPerPlane the number of points used to calculate planes
     * @return a convex set
     */
    public static ConvexSetIndicator st(double dt, double end, Point x, Indicator id, int pointsPerPlane) {
        return new ConvexSetIndicator(dt, end, x, pointsPerPlane) {
            @Override
            public boolean hasElement(Point p) {
                return id.isMember(p);
            }
            
//            @Override
//            public boolean of(Point p) {
//                return id.of(p);
//            }

        };
    }

}
