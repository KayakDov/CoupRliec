/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
        super(asf.asNode.getProj(preProj), asf.asNode.as);
        asn = asf.asNode;
        if (asf.mightContProj) asf.failed = proj;
    }
}
