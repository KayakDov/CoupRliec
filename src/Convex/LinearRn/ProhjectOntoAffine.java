package Convex.LinearRn;

import Hilbert.AffineSpace;
import Hilbert.StrictlyConvexFunction;
import Matricies.Point;

/**
 * A projection function that can either be constructed as a function of
 * affine spaces or a function of points.
 * @author Dov Neimand
 */
public class ProhjectOntoAffine implements StrictlyConvexFunction<Point>{

    private ProjectPoint proj;
    /**
     * The point being projected.  If this is set, the function can be called
     * on different affine spaces.
     */
    private Point project;

    /**
     * The constructor.  The point being projected is constant.  ArgMinAffine 
     * can be called on any number of spaces.  Apply should not be called.
     * @param project the point being projected.
     */
    public ProhjectOntoAffine(Point project) {
        this.project = project;
    }

    /**
     * The affine space being projected onto is set.  The methods of this
     * function may be called on points.
     * @param projectOnto the affine space being projected onto.
     */
    public ProhjectOntoAffine(RnAffineSpace projectOnto) {
        proj = new ProjectPoint(projectOnto.RnLinearSpace(), projectOnto.p(), tolerance);
    }
    
    
    public Point argMinAffine(RnAffineSpace affineSpace) {
        if(affineSpace.isAllSpace()) return project;
        return new ProjectPoint(affineSpace.linearSpace(), affineSpace.p(), tolerance)
                    .apply(project);
    }

    /**
     * The distance from the given point to the affine space being projected onto.
     * @param t
     * @return 
     */
    @Override
    public Double apply(Point t) {
        if(this.proj != null) return proj.apply(t).d(t);
        return Double.valueOf(0);
    }
    
    /**
     * A small number
     */
    public double tolerance = 1e-8;

    
    /**
     * The projection onto the saved affine space
     * @param y the point to be projected.
     * @return the nearest point in the affine space to the point projected.
     */
    public Point argMinAffine(Point y) {
        if(proj != null) {
            return proj.apply(y);
            
        }
        throw new RuntimeException("No affine space to project onto has been set.");
    }

    @Override
    public Point argMin(AffineSpace<Point> A) {
        return argMinAffine(new RnAffineSpace(A));
    }

    @Override
    public double min(AffineSpace<Point> A) {
        return project.d(argMin(A));
    }
    
    
}
