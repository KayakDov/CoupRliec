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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author dov
 */
public class ASNode {

    public static boolean memoryAvailable = true;

    public AffineSpace as;
    public Set<Plane> planeSet;
    public Plane[] planeList;
    private int lastIndex;
    public ConcurrentHashMap<ASKey, ASNode> projectionFunctions;

    public ASNode(int index, ConcurrentHashMap<ASKey, ASNode> map) {
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

    public Plane somePlane() {
        return planeSet.iterator().next();
    }

    public boolean localHasElement(Point x) {
        for (int i = 0; i < planeList.length; i++)
            if (planeList[i].below(x)) return false;
        return true;
    }

    public Point getProj(Point preProj) {
        if (planeSet.size() == 1) return somePlane().proj(preProj);

        if (memoryAvailable && !as.hasProjFunc())
            projectionFunctions.put(new ASKeyPlanes(planeList), this);
        try {
            return as.proj(preProj);
        } catch (ProjectionFunction.NoProjFuncExists ex) {
//            System.out.println("catching " + ex);
            return null;
        }

    }

    @Override
    public String toString() {
        return as.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    public ASNode setIndex(int index) {
        this.lastIndex = index;
        return this;
    }

    public int lastIndex() {
        return lastIndex;
    }

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

        asn.planeList = planeList;

        return asn;
    }

    public static ASNode factory(Plane plane, int index, ConcurrentHashMap<ASKey, ASNode> map) {
        ASNode asn = new ASNode(index, map);
        asn.as = plane;
        asn.planeSet = new HashSet<Plane>(1);
        asn.planeSet.add(plane);
        asn.planeList = new Plane[]{plane};
        return asn;
    }

    public static class AllSpace extends ASNode {

        public AllSpace(int dim) {
            super(0, null);
            as = AffineSpace.allSpace(dim);
        }

    }
}
