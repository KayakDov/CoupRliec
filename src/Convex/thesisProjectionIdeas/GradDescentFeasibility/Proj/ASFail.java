/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys.ASKey;
import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys.ASKeyRI;
import Matricies.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author dov
 */
public class ASFail {

    public final ASNode asNode;
    public Point failed;
    public boolean mightContProj;
    HashSet<Point> checked;

    public ASFail(ASNode asNode) {
        this.asNode = asNode;
        failed = null;
        checked = new HashSet<>(1);

    }

    public ASFail(Plane plane, int planeIndex, ConcurrentHashMap<ASKey, ASNode> map) {
        this(ASNode.factory(plane, planeIndex, map));
    }

    public ASFail setMightContainProj(boolean might) {
        mightContProj = might;
        return this;
    }

    /**
     * A constructor
     *
     * @param planes the set of planes that intersect to make the affine space
     * @param y the tip of the cone
     * @param index the index of the last plane in the list of planes in the
     * cone
     * @param map a map that given a key returns the node for that key.
     */
    public ASFail(Plane[] planes, Point y, int index, ConcurrentHashMap<ASKey, ASNode> map) {
        this(ASNode.factory(new AffineSpace(planes).setP(y), planes, index, map));
    }

    private Plane somePlane() {
        return asNode.somePlane();
    }

    @Override
    public String toString() {
        return asNode.toString();
    }

    private boolean asHasFailElement(int outPlane, Point lowerFail) {
        return asNode.planeList[outPlane].above(lowerFail);
    }

    boolean fail(Point fail) {
        checked.clear();
        failed = fail;
        return mightContProj = false;
    }

    public boolean mightContainProj(Map<ASKey, ASFail> lowerLevel, Point preProj) {
        if (lowerLevel == null)
            return mightContProj = asNode.planeList[0].below(preProj);

        ASKeyRI[] oneDown = asNode.as.oneDownKeys();

        boolean allFacesContainPreProj = true;

        for (ASKeyRI oneDownAS : oneDown) {

            ASFail oneDownI = lowerLevel.get(oneDownAS);

            if(oneDownI == null){//TODO:remove this
                System.out.println("\n\n\n null \n\n\n");
                System.out.println("" + asNode.as.toString() + "\n" +
                        lowerLevel.toString());
            }
            
            if (oneDownI.mightContProj) {
                if (allFacesContainPreProj) allFacesContainPreProj = false;
                Point proj = oneDownI.failed;
                if (asHasFailElement(oneDownAS.removeIndex(), proj))
                    return fail(proj);

            } else if (oneDownI.failed != null) {
                if (allFacesContainPreProj) allFacesContainPreProj = false;
                if (!checked.add(oneDownI.failed) && asHasFailElement(oneDownAS.removeIndex(), oneDownI.failed))
                    return fail(oneDownI.failed);
            }
        }
        checked.clear();
        return mightContProj = !allFacesContainPreProj;
    }

    public void clearFailures() {
        failed = null;
    }
}
