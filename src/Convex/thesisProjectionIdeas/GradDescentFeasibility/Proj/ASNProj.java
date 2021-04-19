
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.Linear.AffineSpace;
import Matricies.Point;

/**
 * This is an affine space projection that additionally stores the affine space node
 * @author dov
 */
public class ASNProj extends ASProj {
    public ASNode asn; 

    /**
     * The constructor
     * @param preProj the point being projected
     * @param asf an affine space failure node that contains the desired affine space.
     */
    public ASNProj(Point preProj, ASFail asf) {
        super(asf.asNode.getProj(preProj), asf.asNode);
        asn = asf.asNode;
        if (asf.mightContProj) asf.failed = proj;
    }
}
