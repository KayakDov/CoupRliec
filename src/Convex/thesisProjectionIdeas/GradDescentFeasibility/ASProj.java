/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex.thesisProjectionIdeas.GradDescentFeasibility;

import Convex.Linear.AffineSpace;
import Matricies.Point;
import listTools.Pair;

/**
 *
 * @author dov
 */
class ASProj extends Pair<AffineSpace, Point> {

    public ASProj(AffineSpace as, Point preProj) {
        super(as, as.proj(preProj));
    }

    public AffineSpace as() {
        return l;
    }

    public Point proj() {
        return r;
    }

    @Override
    public String toString() {
        return "AffinesSpace:\n" + l +"Proj:\n" + r; 
    }
    
    
}
