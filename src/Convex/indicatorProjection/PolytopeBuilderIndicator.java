package Convex.indicatorProjection;

import Convex.Polytope;
import Convex.HalfSpace;
import Convex.Linear.Plane;
import Matricies.Matrix;
import RnSpace.points.Point;

/**
 * Creates a builder for the bounding polytope of a comvex set defined by an
 * indicator function
 *
 * @author Kayak
 */
public class PolytopeBuilderIndicator extends Polytope {

    private Walker step;
    private int pointsPerPlane;
    private Point wellIn;

    /**
     * the constructor
     *
     * @param step a walker to find the edge
     * @param wellIn a point inside the convex set being bounded, preferably
 away from the boundary
     */
    public void setStep(Walker step, Point wellIn) {
        this.step = step;
        this.wellIn = wellIn;
    }

    /**
     * Finds a set of point close, hopefully, to being on a support plane of the
     * convex set near x.
     *
     * @param x
     * @param step
     */
    private int stackOverflowGuard = 0;

    public void addPlane(InOut x) {

        try {
            Matrix surfacePoints = new Matrix(pointsPerPlane, x.dim()).
                    setRows(i -> step.randomGeodesic(new InOut(x)).out());
            HalfSpace support = new HalfSpace(surfacePoints);

            if (support.hasBadSurface())
                throw new RuntimeException("bad plane");
            if (!support.hasElement(x.in())) support = support.complement();

            addFace(support);

        } catch (Exception ex) {
            stackOverflowGuard++;
            System.err.println("Warning, you may have set dt too low " + stackOverflowGuard);
            if (stackOverflowGuard > 5 && stackOverflowGuard < 10) {
                System.err.println("doubling dt");
                step.setDt(step.getDt() * 2);//TODO: this should be done with a passed variable to keep things thread safe
                addPlane(x.bigger());
                step.setDt(step.getDt() / 2);
            } else if (stackOverflowGuard >= 10) {
                System.err.println("moving x back");
                double dtSq = step.getDt() * step.getDt();
                InOut backedUpX = new InOut((x.in().mult(1 - dtSq)).plus(wellIn.mult(dtSq)), x.out());
                addPlane(backedUpX);
                step.setDt(step.getDt() / 2);
            } else addPlane(x.bigger());
            stackOverflowGuard--;
        }

    }

    public PolytopeBuilderIndicator(int pointsPerPlane) {
        this.pointsPerPlane = pointsPerPlane;
    }

    public void cleanUp(Point y) {
//        System.out.println("num faces before cleaning = " + bounding.faces.size());
        removeFacesNotContaining(proj(y));
        removeFacesNotFacing(y);
//        System.out.println("num faces after cleaning = " + bounding.faces.size());

    }
}
