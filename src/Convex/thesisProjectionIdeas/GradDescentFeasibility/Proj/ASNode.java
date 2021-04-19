/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys.ASKey;
import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Convex.Linear.ProjectionFunction;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys.ASKeyPlanes;
import Matricies.Point;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This encapsulates an affine space and directly links it to the planes 
 * that intersect to form it.
 * @author dov
 */
public class ASNode {
    
    /**
     * This should be set to false if you might run out of memory.  The code will run faster if this is true, given sufficient memory.
     */
    public static boolean memoryAvailable = true;

    /**
     * the affine space contained
     */
    protected AffineSpace as;
    /**
     * The planes that intersect to make the affine space.
     */
    private Set<Plane> planeSet;
    /**
     * The planes that intersect to make the affine space.
     */
    protected Plane[] planeArray;
    /**
     * The index of the last plane in the plane array from the ProjPolytope list of planes.
     * This plays a roll in generating the next level of i-affine spaces.
     */
    protected int lastIndex;
    /**
     * All the projection functions that are beign saved.
     */
    protected ConcurrentHashMap<ASKey, ASNode> projectionFunctions;

    /**
     * The affine space
     * @return 
     */
    public AffineSpace as() {
        return as;
    }

    /**
     * The set of planes that intersect to form the affine space.
     * @return 
     */
    public Set<Plane> planeSet() {
        return planeSet;
    }

    /**
     * The planes that intersect to form the affine space.
     * @return 
     */
    public Plane[] planeArray() {
        return planeArray;
    }

    /**
     * The index of the last plane in the plane array from the ProjPolytope list of planes.
     * This plays a roll in generating the next level of i-affine spaces.
     */
    public int lastIndex() {
        return lastIndex;
    }

    /**
     * All the projection functions that are beign saved.
     */
    public ConcurrentHashMap<ASKey, ASNode> projectionFunctions() {
        return projectionFunctions;
    }
    
    /**
     * THe constructor
     * @param index the PorjFunction plane list index of the last plane in the array.
     * @param map The saved projection functions.
     */
    private ASNode(int index, ConcurrentHashMap<ASKey, ASNode> map) {
        this.lastIndex = index;
        projectionFunctions = map;
    }

    @Override
    public int hashCode() {
        return as.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASNode)
            return as.equals(((ASNode) obj).as);
        return false;
    }

    /**
     * A plane that contains the affine space.  In most cases when this 
     * function is called, this plane will be the affine space.
     * @return 
     */
    public Plane plane() {
        return planeArray[0];
    }

    /**
     * Does the personal polytope of this affine space contain the element.
     * @param x
     * @return 
     */
    public boolean localHasElement(Point x) {
        for (int i = 0; i < planeArray.length; i++)
            if (planeArray[i].below(x)) return false;
        return true;
    }
    
    /**
     * Is this an empty affine space.
     */
    private boolean spaceIsEmpty = false;
    
    /**
     * It has not yet been observed that this affine space is empty.
     * @return true if we don't know, and false if we know that it's empty.
     */
    public boolean spaceIsNonEmpty(){
        return !spaceIsEmpty;
    }
    /**
     * This is an empty affine space.
     * @return false if we don't know.
     */
    public boolean spaceIsEmpty(){
        return spaceIsEmpty;
    }
    
    /**
     * This should be called if it's been observed that this affine space is empty.
     */
    public void setSpaceAsEmpty(){
        spaceIsEmpty = true;
    }
    
    /**
     * Gets the projection onto this affine space.  If the projection function has
     * been found in the past, then the prior projection function will be used.
     * If not, a new on will be made and saved for future use.
     * @param preProj the point to be projected.
     * @return the projection onto this affine space.
     */
    public Point getProj(Point preProj) {
        if (planeArray.length == 1) return plane().proj(preProj);

        if (memoryAvailable && !as.hasProjFunc())
            projectionFunctions.put(new ASKeyPlanes(planeArray), this);
        try {
            return as.proj(preProj);
        } catch (ProjectionFunction.NoProjFuncExists ex) {
//            System.out.println("catching " + ex);
            setSpaceAsEmpty();
            return null;
        }

    }

    @Override
    public String toString() {
        return as.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Sets the index of the last plane in the array of planes.
     * @param index the index for the last plane from the list in ProjPolytope.
     * @return this node.
     */
    public ASNode setIndex(int index) {
        this.lastIndex = index;
        return this;
    }


    /**
     * This method should be called to create a new affine space node.
     * @param as the affine space in the node.  This affine space will not be 
     * used if a similar node was created in the past and built it's own projection
     * function.
     * @param planeList the list of planes that intersect to make this affine space.
     * @param index the index of the last plane in the list from the big list in ProjPolytope.
     * @param map the projection functions that have been saved.
     * @return an affine space node containing the given affine space or one equal to it.
     */
    public static ASNode factory(AffineSpace as, Plane[] planeList, int index, ConcurrentHashMap<ASKey, ASNode> map) {

        ASKeyPlanes key = new ASKeyPlanes(planeList);

        ASNode asn;//TODO: attach this lower down
        if (map.containsKey(key)) {
            if (!map.get(key).as.equals(as))
                throw new RuntimeException("bad retrival");
            return map.get(key).setIndex(index);
        }

        asn = new ASNode(index, map);
        asn.as = as;

        asn.planeSet = Set.of(planeList);

        asn.planeArray = planeList;

        return asn;
    }

    /**
     * Creates an affine space node
     * @param plane the affine space
     * @param index the index of the given plane in the Projolytope list.
     * @param map the saved projection functions.
     * @return an affine space node for the given plane.
     */
    public static ASNode factory(Plane plane, int index, ConcurrentHashMap<ASKey, ASNode> map) {
        ASNode asn = new ASNode(index, map);
        asn.as = plane;
        asn.planeSet = new HashSet<Plane>(1);
        asn.planeSet.add(plane);
        asn.planeArray = new Plane[]{plane};
        return asn;
    }

    /**
     * An affine space node when that space is R^n.
     */
    public static class AllSpace extends ASNode {

        /**
         * The constructor.
         * @param dim 
         */
        public AllSpace(int dim) {
            super(0, null);
            as = AffineSpace.allSpace(dim);
        }

    }
}
