package Convex.GradDescentFeasibility.Proj;

import Convex.ASKeys.ASKey;
import Convex.LinearRn.RnAffineSpace;
import Convex.LinearRn.RnPlane;
import Convex.ASKeys.ASKeyRI;
import Matricies.Point;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Checks if the underlying affine space meets the necessary criteria.
 *
 * @author dov
 */
public class ASFail {

    /**
     * Links to the affine space that this ASFail checks for necessary criteria
     */
    public final ASNode asNode;

    /**
     * It the projection onto the personal polytope of the underlying affine
     * space is not in that space, then it is stored here.
     */
    public Point projOntoPersoanlPoly;

    /**
     * The constructor mightContProj
     *
     * @param asNode The affine node to be checked to meet the necessary
     * conditions
     */
    public ASFail(ASNode asNode) {
        this.asNode = asNode;
        projOntoPersoanlPoly = null;
    }

    /**
     * The constructor
     *
     * @param plane A single plane that is the affine space to be checked for
     * necessary conditions.
     * @param planeIndex the index of the affine space in the list in
     * ProjPolytope
     * @param map A set of all the affine space nodes that have been saved.
     */
    public ASFail(RnPlane plane, int planeIndex, ConcurrentHashMap<ASKey, ASNode> map) {
        this(ASNode.factory(plane, planeIndex, map));
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
    public ASFail(RnPlane[] planes, Point y, int index, ConcurrentHashMap<ASKey, ASNode> map) {
        this(ASNode.factory(new RnAffineSpace(planes).setP(y), planes, index, map));
    }

    @Override
    public String toString() {
        return asNode.toString();
    }

    /**
     * Is the projection onto the personal polytope of an immidiate supersapce
     * inside this affine space's personal polytope?
     *
     * @param outPlane the index of the plane the immediate superspace is not
     * contained in.
     * @param lowerFail the projection onto the personal polytope of the
     * immediate superspace.
     * @return true if the projection is inside this affine space's personal
     * polytope, false otherwise.
     */
    private boolean personalPolyContainesSuperProj(int outPlane, Point lowerFail) {
        return asNode.planeArray[outPlane].above(lowerFail);
    }

    /**
     * If the personal polytope is a halfspace.
     *
     * @param preProj
     * @return true of the preprojection is outside of the halfspace, false
     * otherwise.
     */
    private boolean singleHalfSpaceProj(Point preProj) {
        boolean below = asNode.plane().below(preProj);
        projOntoPersoanlPoly = below ? asNode.getProj(preProj) : preProj;
        return below;
    }

    /**
     * If a projected point is not in the personal polytope then it is
     * in the affine space.
     * @param preProj
     * @return 
     */
    private boolean meetsCriteria(Point preProj) {
        projOntoPersoanlPoly = asNode.getProj(preProj);
        return true;
    }

    /**
     * Determines if the underlying affine space is a candidate.
     *
     * @param containsImmidiateSuperSpaces A set of all the affine spaces that are the
     * intersection of one fewer planes than this affine space.
     * @param preProj the point that is being projected.
     * @return true if the underlying affine space is a candidate, and false
     * otherwise.
     */
    public boolean meetsNecesaryCriteria(Map<ASKey, ASFail> containsImmidiateSuperSpaces, Point preProj) {
        if (containsImmidiateSuperSpaces == null) return singleHalfSpaceProj(preProj);
        if (asNode.as().hasProjFunc()) return meetsCriteria(preProj);

        ASKeyRI[] immidiateSuperKeys = asNode.as().immidiateSuperKeys();

        for (ASKeyRI immidiateSuperKey : immidiateSuperKeys) {

            Point projSuperPP = containsImmidiateSuperSpaces.get(immidiateSuperKey).projOntoPersoanlPoly;

            if (personalPolyContainesSuperProj(immidiateSuperKey.removeIndex(), projSuperPP)) {
                projOntoPersoanlPoly = projSuperPP;
                return false;
            }

        }
        return meetsCriteria(preProj);
    }

    /**
     * This functions removes the saved projection point. It should be callsed
     * before every new projection.
     */
    public void clearFailure() {
        projOntoPersoanlPoly = null;
    }

}
