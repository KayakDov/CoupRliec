
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.Linear.AffineSpace;
import Matricies.Point;

/**
 *
 * @author dov
 */
public class ASNProj extends ASProj {
    public ASNode asn; 

    public ASNProj(Point preProj, ASFail asf) {
        super(asf.asNode.getProj(preProj), asf.asNode);
        asn = asf.asNode;
        if (asf.mightContProj) asf.failed = proj;
    }
}
