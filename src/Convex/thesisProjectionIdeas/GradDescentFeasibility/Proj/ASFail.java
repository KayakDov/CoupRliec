/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Matricies.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author dov
 */
public class ASFail {

    public final ASNode asNode;
    private List<Point> failed;
    public boolean mightContProj;

    public ASFail(ASNode asNode) {
        this.asNode = asNode;
        failed = new ArrayList<>(asNode.planeList.length * 2);

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
        return asNode.toString() + "\n" + failed;
    }

    public boolean mightContainProj(Map<ASKey, ASFail> lowerLevel, Point preProj) {

        if (asNode.asProjs.containsKey(asNode)) return mightContProj = true;

        ASKey[] oneDown = asNode.as.oneDownKeys();

        boolean allFacesContainPreProj = true;

        for (ASKey oneDownAS : oneDown) {

            ASFail oneDownI = lowerLevel.get(oneDownAS);

            if (oneDownI.mightContProj) {
                allFacesContainPreProj = false;
                Point proj = oneDownI.asNode.getProj(preProj);
                if (asNode.localHasElement(proj)) failed.add(proj);
            } else if (!oneDownI.failed.isEmpty()) {
                allFacesContainPreProj = false;
                for (Point fail : oneDownI.failed)
                    if (asNode.localHasElement(fail)) failed.add(fail);

            }
        }

        return mightContProj = (failed.isEmpty() && !allFacesContainPreProj);
    }

    public void clearFailures() {
        failed.clear();
    }
}
