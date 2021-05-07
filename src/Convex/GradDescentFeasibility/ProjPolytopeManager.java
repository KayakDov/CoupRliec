package Convex.GradDescentFeasibility;

import Convex.HalfSpace;
import Convex.GradDescentFeasibility.Proj.ASNode;
import Convex.GradDescentFeasibility.Proj.ASProj;
import Convex.GradDescentFeasibility.Proj.ProjPolytope;
import Matricies.Point;

/**
 * Manages the projection polytope for use by the feasibility algorithm.
 * @author dov
 */
public class ProjPolytopeManager extends ProjPolytope {

    private Partition part;
    /**
     * The closest feasible direction to the gradient.
     */
    private Point gradInBounds;
    /**
     * The partition in the feasibility algorithm. This may be marked as null if
     * this function
     */

    public ASNode travelThrough;

    /**
     * The constructor
     * @param part a partition of the polytope
     */
    public ProjPolytopeManager(Partition part) {
        super(part.getGradient().dim());
        this.part = part;
        this.gradInBounds = part.getGradient();
        travelThrough = new ASNode.AllSpace(gradInBounds.dim());
    }

    /**
     * Adds a newly encountered half space to this polytope, updates the
     * gradient, and removes and half spaces left behind.
     *
     * @param arrivalHS the half space being added
     * @param y the value for y.
     */
    public void travelToNewLocalPolytope(HalfSpace arrivalHS, Point y) {

        removeExcept(travelThrough);

        Point preProj = y.minus(part.getGradient());

        add(arrivalHS.boundary());

        ASProj asProj = proj(preProj, y);

        travelThrough = asProj.asn();

        if (asProj.proj().equals(y)) {
            throw new EmptyPolytopeException();
        }

        gradInBounds = y.minus(asProj.proj());

    }

    /**
     * The gradient without leaving the local containing polytope.
     * @return 
     */
    public Point grad() {
        return gradInBounds;
    }
    
}
