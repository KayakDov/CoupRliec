
package Convex.GradDescentFeasibility.Proj;

import Convex.Linear.AffineSpace;
import Matricies.Point;

/**
 * the projection of a point onto an affine space.
 * @author dov
 */
public class ASProj {

    protected Point proj;
    protected AffineSpace as;
    protected ASNode asn;

    /**
     * The constructor
     * @param proj the projection
     * @param asn the affine space node which is projected onto.
     */
    public ASProj(Point proj, ASNode asn) {
        this.proj = proj;
        this.as = asn.as;
        this.asn = asn;
    }

    /**
     * The projection
     * @return 
     */
    public Point proj() {
        return proj;
    }

    /**
     * The affine space
     * @return 
     */
    public AffineSpace as() {
        return as;
    }

    /**
     * The affine space node.
     * @return 
     */
    public ASNode asn() {
        return asn;
    }
    
    
    



}
