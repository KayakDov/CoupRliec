package Convex.LinearRn;

import Hilbert.AffineSpace;
import Hilbert.StrictlyConvexFunction;
import Matricies.Point;

/**
 * A projection function that can either be constructed as a function of
 * affine spaces or a function of points.
 * @author Dov Neimand
 */
public class ProjectOntoAffine implements StrictlyConvexFunction<Point>{

    private ProjectPoint proj;
    /**
     * The point being projected.  If this is set, the function can be called
     * on different affine spaces.
     */
    private Point p;

    /**
     * The constructor.  The point being projected is constant.  ArgMinAffine 
     * can be called on any number of spaces.  Apply should not be called.
     * @param project the point being projected.
     */
    public ProjectOntoAffine(Point project) {
        this.p = project;
    }
    
    /**
     * The argMin over the given affine space.  This will return the point
     * in the affine space closes the the p.
     * @param affineSpace
     * @return the minimal argument over the given affine space.
     */
    public Point argMin(RnAffineSpace affineSpace) {
        if(affineSpace.isAllSpace()) return p;
        return new ProjectPoint(affineSpace.linearSpace(), affineSpace.p(), tolerance)
                    .apply(p);
    }

    /**
     * The distance from the given point to the affine space being projected onto.
     * @param t
     * @return 
     */
    @Override
    public Double apply(Point t) {
        if(this.proj != null) return p.d(t);
        return 0.0;
    }
    
    /**
     * A small number
     */
    public double tolerance = 1e-8;

    
    @Override
    public Point argMin(AffineSpace<Point> A) {
        return argMin(new RnAffineSpace(A));
    }

    @Override
    public double min(AffineSpace<Point> A) {
        return p.d(argMin(A));
    }
}
