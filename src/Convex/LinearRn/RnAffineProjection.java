package Convex.LinearRn;

import Hilbert.AffineSpace;
import Hilbert.StrictlyConvexFunction;
import Matricies.Point;

/**
 *
 * @author Dov Neimand
 */
public class RnAffineProjection implements StrictlyConvexFunction<Point>{

    private RnAffineSpace projectOnto;
    private Point project;

    /**
     * The constructor.  The point being projected is constant.  ArgMinAffine 
     * can be called on any number of spaces.  Apply should not be called.
     * @param project the point being projected.
     */
    public RnAffineProjection(Point project) {
        this.project = project;
    }

    /**
     * The affine space being projected onto is set.  The methods of this
     * function may be called on points.
     * @param projectOnto the affine space being projected onto.
     */
    public RnAffineProjection(RnAffineSpace projectOnto) {
        this.projectOnto = projectOnto;
    }
    
    
    public Point argMinAffine(RnAffineSpace A) {
        try{
        if(project != null) return A.proj(project);
        } catch(ProjectionFunction.NoProjFuncExists ex){
            return null;
        }
        throw new RuntimeException("No point to be projected has been set.");
    }

    /**
     * The distance from the given point to the affine space being projected onto.
     * @param t
     * @return 
     */
    @Override
    public Double apply(Point t) {
        if(projectOnto != null) return projectOnto.d(t);
        throw new RuntimeException("No affine space to project onto has been set.");
    }
    
    /**
     * The projection onto the saved affine space
     * @param y the point to be projected.
     * @return the nearest point in the affine space to the point projected.
     */
    public Point argMinAffine(Point y) {
        if(projectOnto != null) return projectOnto.proj(y);
        throw new RuntimeException("No affine space to project onto has been set.");
    }

    @Override
    public Point argMinAffine(AffineSpace<Point> A) {
        return argMinAffine(new RnAffineSpace(A));
    }

    @Override
    public double min(AffineSpace<Point> A) {
        return project.d(argMinAffine(A));
    }
    
    
}
