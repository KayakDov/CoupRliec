package Convex.thesisProjectionIdeas.GradDescentFeasibility;

import Convex.HalfSpace;
import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Matricies.Matrix;
import Matricies.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import listTools.Choose;

/**
 * What does this polytope need to do: 1: Save any projection functions that
 * might be used again. 2: A failed point for a polyhedral cone is a projection
 * onto one of the affine spaces of that cone that is contained in that cone.
 * failed points need to be past up to higher level intersections.
 *
 * @author dov
 */
public class ProjPolytope {

    private Point gradInBounds;
    private Partition part;
    public AffineSpace travelThrough;

    public ProjPolytope(Partition part) {
        this.gradInBounds = part.getGradient();
        this.part = part;
        travelThrough = AffineSpace.allSpace(gradInBounds.dim());
    }

    public Point grad() {
        return gradInBounds;
    }

    private class ASNode {

        public AffineSpace as;
        public Set<Plane> planes;

        public ASNode(AffineSpace as, Set<Plane> planes) {
            this.as = as;
            this.planes = planes;
        }

        @Override
        public int hashCode() {
            return as.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return as.equals(((ASNode) obj).as);
        }

        public ASNode(Plane plane) {
            this.as = plane;
            this.planes = new HashSet<Plane>(1);
            planes.add(plane);
        }

        @Override
        public String toString() {
            return as.toString(); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class ASFail {

        public ASNode asNode;
        public ArrayList<Point> failed;
        public boolean mightContProj;

        public ASFail(ASNode asNode) {
            this.asNode = asNode;
            this.failed = new ArrayList<>(1);
        }

        public ASFail(Plane plane) {
            this(new ASNode(plane));
        }

        public ASFail(ArrayList<HalfSpace> hsList, Point y) {
            this(new ASNode(
                    AffineSpace.intersection(hsList.stream().map(hs -> hs.boundary())).setP(y),
                    hsList.stream().map(hs -> hs.boundary()).collect(Collectors.toSet())
            ));
        }

        private Plane somePlane() {
            return asNode.planes.iterator().next();
        }

        //this class needs to be fleshed out so that the failure state and fail points are set given a preproj
        public boolean mightContainProj(HashMap<AffineSpace, ASFail> lowerLevel, Point preProj) {

            if (asProjs.containsKey(asNode)) {
                failed.add(asNode.as.proj(asProjs.get(asNode), preProj));
                
                return true;
            }
            
            if(asNode.as.hasProjFunc()) throw new RuntimeException("A projection function was not added to asProjs, or something else is wrong.");

            if (lowerLevel.isEmpty()) {
                if (somePlane().above(preProj)) {
                    failed.add(preProj);
                    return false;
                } else {
                    failed.add(new ASProjSave(preProj, asNode).proj);
                    return true;
                }
            }

            asNode.as.oneDown()
                    .flatMap(as -> lowerLevel.get(as).failed.stream())
                    .filter(fp -> asNode.planes.stream()
                    .allMatch(plane -> plane.aboveOrContains(fp)))
                    .collect(Collectors.toCollection(() -> failed));
            return mightContProj = failed.isEmpty();
        }
    }

    public HashMap<ASNode, Matrix> asProjs = new HashMap<>(); //TODO: init size for speeed
    public HashSet<HalfSpace> halfSpaces = new HashSet<>();

    public ProjPolytope() {
    }

    public void remove(HalfSpace hs) {
        asProjs.entrySet().removeIf(asn -> asn.getKey().planes.contains(hs.boundary()));
        halfSpaces.remove(hs);
    }

    public ASProjSave proj(Point preProj, Point y) {

        HashMap<AffineSpace, ASFail> lowerLevel = new HashMap<>();
        List<ASFail> currentLevel;

        for (int i = 1; i < y.dim(); i++) {

            currentLevel = new Choose<>(new ArrayList<>(halfSpaces), i)
                    .chooseStream()
                    .map(subsetOfHalfSpaces -> new ASFail(subsetOfHalfSpaces, y))
                    .collect(Collectors.toList());

            /////////////////////Good way to do it///////////////////////////////
            ASProjSave proj = currentLevel.stream()//.parallel()//TODO: making this parralell causes varius bugs.  This should be fixed.
                    .filter(asf -> asf.mightContainProj(lowerLevel, preProj))
                    .map(asf -> new ASProjSave(preProj, asf.asNode))
                    .filter(p -> hasElement(p.proj))
                    .min(Comparator.comparing(p -> p.proj.d(preProj)))
                    .orElse(null);
            //////////////End of good way to do it, begin profiler way to do it////////////////
//            List<ASProjSave> candidates = new ArrayList<>(5);
//
//            for (ASFail asf : currentLevel)
//                if (asf.mightContainProj(lowerLevel, preProj)) {
//                    ASProjSave asps = new ASProjSave(preProj, asf.asNode);
//                    if (hasElement(asps.proj))
//                        candidates.add(asps);
//                }
//            ASProjSave proj = candidates.stream().min(Comparator.comparing(p -> p.proj.d(preProj))).orElse(null);
            ///////////end of slow section to be cut/////////////////////////////////

            if (proj != null) return proj;

            lowerLevel.clear();
            currentLevel.forEach(asf -> lowerLevel.put(asf.asNode.as, asf));
        }
        throw new EmptyPolytopeException();
    }

    public boolean hasElement(Point p) {
        return halfSpaces.stream().allMatch(hs -> hs.hasElement(p));
    }

    public boolean hasElement(Point p, double epsilon) {
        return halfSpaces.stream().allMatch(hs -> hs.hasElement(p, epsilon));
    }

    public class ASProjSave {

        public Point proj;
        public AffineSpace as;

        public ASProjSave(Point preProj, ASNode asn) {
            this.as = asn.as;

            if (!asProjs.containsKey(asn)) {

                asProjs.put(asn, asn.as.linearSpace().getProjFunc());
                proj = asn.as.proj(preProj);

            } else {
                proj = asn.as.proj(asProjs.get(asn), preProj);
            }

        }

    }

    public void removeExcept(AffineSpace as) {

        if (as.isAllSpace()) {
            asProjs.clear();
            halfSpaces.clear();
            return;
        }

        HashSet<Plane> planesToBePreserved = as.intersectingPlanesSet();
        halfSpaces.removeIf(hs -> !planesToBePreserved.contains(hs.boundary()));
        asProjs
                .keySet().removeIf(asn -> !planesToBePreserved.containsAll(asn.planes));

    }

    /**
     * Adds a newly encountered half space to this polytope, updates the
     * gradient, and removes and half spaces left behind.
     *
     * @param add the half space being added
     * @param y the value for y.
     */
    public void addHalfSpace(HalfSpace add, Point y) {

        removeExcept(travelThrough);

        Point preProj = y.minus(part.getGradient());

        halfSpaces.add(add);

        ASProjSave asProj = proj(preProj, y);

        travelThrough = asProj.as;

        if (asProj.proj.equals(y)) {
            throw new EmptyPolytopeException();
        }

        gradInBounds = y.minus(asProj.proj);

    }
}
