package Convex.thesisProjectionIdeas.GradDescentFeasibility;

import Convex.Linear.AffineSpace;
import Convex.HalfSpace;
import Convex.Linear.Plane;
import Convex.Polytope;
import Matricies.PointDense;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 *
 * @author Dov Neimand
 */
public class LocalPolyhedralCone extends Polytope {

    private PointDense gradInBounds;
    private Partition part;
    private AffineSpace travelThrough;

    private AffineSpacePlaneBipartate aspb;

    /**
     * Creates a polyhedral cone with y as its focal point.
     *
     * @param part the current partition for the grad descent feasibility algo.
     */
    public LocalPolyhedralCone(Partition part) {
        this.part = part;
        gradInBounds = part.getGradient();
        aspb = new AffineSpacePlaneBipartate(part.getGradient().dim());
        travelThrough = AffineSpace.allSpace(part.getGradient().dim());
    }
    

    /**
     * The gradient that y will take inside this polytope
     *
     * @return
     */
    public PointDense grad() {
        return gradInBounds;
    }

    public void add(HalfSpace hs, PointDense y) {
        aspb.addPlane(hs.boundary(), y);
        super.add(hs); 
    }

    
    
    /**
     * Adds a newly encountered half space to this polytope, updates the
     * gradient, and removes and half spaces left behind.
     *
     * @param add the half space being added
     * @param y the value for y.
     */
    public void addHalfSpace(HalfSpace add, PointDense y) {

        aspb.removeExcept(travelThrough, this);
                
        PointDense preProj = y.minus(part.getGradient());

        add(add, y);
        
        travelThrough = hasProj(preProj, 1);
              
        PointDense proj = travelThrough.proj(preProj);
        if (proj.equals(y)) {
            throw new EmptyPolytopeException();
        }

        gradInBounds = y.minus(proj);
        
    }

    /**
     * This can be made faster by using the information from the previouse run.
     * There's only a small change the halfspaces for each successive run, so
     * for most of the half space the affine spaces, and projections onto them
     * have already been computed.
     *
     * preProj toProj
     * @param y
     * @param newAddition
     * @param epsilonMult
     * @return
     */
    private AffineSpace hasProj(PointDense preProj, int epsilonMult) {
        
        if(hasElement(preProj)) return AffineSpace.allSpace(dim());

        
        try {
            return aspb.affineSpaces()
                    .min(Comparator.comparing(as -> {
                        
                        PointDense proj = as.proj(preProj);  //TODO:  I should be able to compute the projection for an n lined matrix if I've already computed the projection for an n-1 line matrix
                        
                        
                        if (!hasElement(proj, epsilon))
                            return Double.POSITIVE_INFINITY;
                        return proj.d(preProj);
                    })).get();

        } catch (NoSuchElementException ex) {
            System.err.println("projection error.  Setting epsilon to:" + epsilon * epsilonMult * 10);
            return hasProj(preProj, epsilonMult * 10);
        }
    }

    public AffineSpace getTravelThrouge() {
        return travelThrough;
    }


    
}
