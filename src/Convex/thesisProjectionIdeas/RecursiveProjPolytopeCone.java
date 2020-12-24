package Convex.thesisProjectionIdeas;

import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Convex.PolytopeCone;
import Matricies.PointDense;
import java.util.Comparator;

/**
 *  This does not work!  But maybe it can be made to work.  If the result is outside the interior of the halfspace 
 * at the tip of the cone with a normal from the tip to y, then try again recursively looking at each of the n choose n-1 subsets of halfspaces,
 * until I find the solution.  Worst case scenario is this degenerates to going thoruhg every option with is the brute force method.
 * I still haven't found a good way to find one of the closes half spaces.
 * @author Dov Neimand
 */
public class RecursiveProjPolytopeCone extends PolytopeCone {

    public RecursiveProjPolytopeCone(PolytopeCone pc) {
        super(pc.getTip());
        addAll(pc);
    }

    public RecursiveProjPolytopeCone(PointDense tip) {
        super(tip);
    }

    @Override
    public PointDense proj(PointDense y) {
        return proj(y, AffineSpace.allSpace(y.dim()), y.dim());
    }

    /**
     * projects y onto the intersection of an affine space and the polytope.
     *
     * @param y a point in R^n
     * @param as an affine space that is the intersection of some hyperplanes of
     * the polytope
     * @return the projection of y onto the intersection of as and this
     * polytope.
     */
    private PointDense proj(PointDense y, AffineSpace as, int dim) {

        y = as.proj(y);

        if (hasElement(y)) return y;

        AffineSpace containingProj = containingProj(y, as);

        if (dim == 0)
            return as.proj(y);

        return proj(y, containingProj, dim - 1);

    }

    /**
     * returns the closest face of the polytope to a point in the affine space
     * TODO: For a polyhedral cone, perhaps I can rewrite this so that it finds
     * the face who's normal vector is closest to y - p, where p is the tip of
     * the cone.
     *
     * @param y the point we're looking for a face close to
     * @param as the affine space that the point and cut of this polytope are in
     * @return the face, intersected with the affine space, that is closest to
     * y.
     */
    public AffineSpace containingProj(PointDense y, AffineSpace as) {

        
        PointDense toY = y.minus(getTip());

        Plane nearestPlane = stream().map(hs -> hs.boundary())
                .max(Comparator.comparing(
                        plane -> plane.normal().dot(toY))
                ).get();


        return as.intersection(nearestPlane);
//
//        Polytope ASOn = new Polytope(stream().parallel().filter(hs
//                -> !hs.hasElement(y)
//                && hasNonEmptyIntersection(hs.boundary().intersection(as))));
//
//        Matrix projections = Matrix.fromRows(ASOn.stream().map(hs -> (hs.boundary().intersection(as)).proj(y)));
//
//        ASOn.removeIf(hs -> projections.rowStream().anyMatch(p -> !hs.complement().hasElement(p)));
//
//        Point projAS = ASOn.bruteForceProjection(y);
//
//        return ASOn.stream().filter(hs -> hs.boundary().hasElement(projAS))
//                .findAny().get().boundary().intersection(as);

    }

}
