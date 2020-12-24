package Convex.thesisProjectionIdeas;

import Convex.Linear.AffineSpace;
import Convex.HalfSpace;
import Convex.Polytope;
import Matricies.Matrix;
import Matricies.PointDense;

/**
 *
 * @author Dov Neimand
 */
public class AdjacentFacetPolytopProjection extends Polytope {

    /**
     * 
     * @param y the point being projected
     * @return a polytope including the facet nearest the projection, and all of its neighbors.
     */
    private Polytope facetsNearProj(PointDense y) {
        HalfSpace nearest = closestTo(y);
        return new Polytope(stream().filter(hs -> adjacent(hs, nearest)));
    }

    @Override
    public PointDense proj(final PointDense y) {
        if(isMember(y)) return y;

        Polytope nearProj = facetsNearProj(y);
      
        PointDense proj;
        AffineSpace as = AffineSpace.allSpace(0);
        
        while (!hasElement(proj = as.proj(y))) {
            HalfSpace closest = nearProj.closestTo(proj);
            nearProj.remove(closest);
            as = as.intersection(closest.boundary());
        }

        return proj;
    }

    @Override
    public Polytope addFaces(Polytope p) {
        super.addFaces(p);
        return this;
    }
}
