package Convex.thesisProjectionIdeas.GradDescentFeasibility;

import Convex.Linear.AffineSpace;
import Convex.Linear.LinearSpace;
import Matricies.Point;
import listTools.Pair;

/**
 *
 * @author dov
 */
class ASProj extends Pair<AffineSpace, Point> {

    public ASProj(AffineSpace as, Point preProj) {
        super(as, null);
//        try {
            r = as.proj(preProj);
//        } catch (LinearSpace.NoProjFuncExists ex) {
//
//        }
    }

    public AffineSpace as() {
        return l;
    }

    public Point proj() {
        return r;
    }

    @Override
    public String toString() {
        return "AffinesSpace:\n" + l + "Proj:\n" + r;
    }

}
