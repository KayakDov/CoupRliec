
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.Linear.AffineSpace;
import Matricies.Point;

/**
 *
 * @author dov
 */
public class ASProj {

    public Point proj;
    public AffineSpace as;
    public ASNode asn;

//    public ASProj(Point proj, AffineSpace as) {
//        this.proj = proj;
//        this.as = as;
//    }

    public ASProj(Point proj, ASNode asn) {
        this.proj = proj;
        this.as = asn.as;
        this.asn = asn;
    }
    
    



}
