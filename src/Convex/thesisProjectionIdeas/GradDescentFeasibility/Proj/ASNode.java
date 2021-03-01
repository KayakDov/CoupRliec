/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Matricies.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author dov
 */
public class ASNode {

    public AffineSpace as;
    public Set<Plane> planeSet;
    public Plane[] planeList;
    public int lastIndex;
    public ConcurrentHashMap<ASNKey, ASNode> asProjs;

    public ASNode(int index, ConcurrentHashMap<ASNKey, ASNode> map) {
        this.lastIndex = index;
        asProjs = map;
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
        return planeSet.stream()
                .allMatch(plane -> plane.aboveOrContains(x));
    }

    public Point getProj(Point preProj) {
        if (planeSet.size() == 1) return somePlane().proj(preProj);
        else {
            if (!as.hasProjFunc()) asProjs.put(new ASNKey(planeList), this);
            return as.proj(preProj);
        }

    }

    @Override
    public String toString() {
        return as.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public ASNode setIndex(int index){
        this.lastIndex = index;
        return this;
    }

    public static ASNode factory(AffineSpace as, Plane[] planeList, int index, ConcurrentHashMap<ASNKey, ASNode> map) {

        ASNKey key = new ASNKey(planeList);
        if (map.containsKey(key)) return map.get(key).setIndex(index);
        
        ASNode asn = new ASNode(index, map);
        asn.as = as;
        asn.planeSet = Set.of(planeList);
        asn.planeList = planeList;

        return asn;
    }

    public static ASNode factory(Plane plane, int index, ConcurrentHashMap<ASNKey, ASNode> map) {
        ASNode asn = new ASNode(index, map);
        asn.as = plane;
        asn.planeSet = new HashSet<Plane>(1);
        asn.planeSet.add(plane);
        asn.planeList = new Plane[]{plane};
        return asn;
    }

}
