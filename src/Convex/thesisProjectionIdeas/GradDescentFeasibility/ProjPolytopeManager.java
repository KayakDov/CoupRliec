package Convex.thesisProjectionIdeas.GradDescentFeasibility;

import Convex.HalfSpace;
import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASNode;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASProj;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ProjPolytope;
import Matricies.Point;

/**
 *
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

        travelThrough = asProj.asn;

        if (asProj.proj.equals(y)) {
            throw new EmptyPolytopeException();
        }

        gradInBounds = y.minus(asProj.proj);

    }

    public Point grad() {
        return gradInBounds;
    }
    
}
