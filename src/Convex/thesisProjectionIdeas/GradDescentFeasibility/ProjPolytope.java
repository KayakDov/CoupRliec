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

        public ASNode(Plane plane) {
            this.as = plane;
            this.planes = new HashSet<Plane>(1);
            planes.add(plane);
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

        public ASFail(ArrayList<HalfSpace> hsList) {
            this(new ASNode(
                    AffineSpace.intersection(hsList.stream().map(hs -> hs.boundary())), 
                    hsList.stream().map(hs -> hs.boundary()).collect(Collectors.toSet())
            ));
        }
        

        private Plane somePlane() {
            return asNode.planes.iterator().next();
        }

        //this class needs to be fleshed out so that the failure state and fail points are set given a preproj
        public boolean mightContainProj(HashMap<AffineSpace, ASFail> lowerLevel, Point preProj) {

            if (asNode.as.hasProjFunc()) {
                failed.add(asNode.as.proj(preProj));
                return true;
            }

            if (lowerLevel.isEmpty()) {
                if (somePlane().above(preProj)) {
                    failed.add(preProj);
                    return false;
                } else {
                    failed.add(somePlane().proj(preProj));
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

    public HashMap<ASNode, Matrix> affineSpacesProjections = new HashMap<>(); //TODO: init size for speeed
    public HashSet<HalfSpace> halfSpaces = new HashSet<>();

    public ProjPolytope() {
    }

    public void remove(HalfSpace hs) {
        affineSpacesProjections.entrySet().removeIf(asn -> asn.getKey().planes.contains(hs.boundary()));
        halfSpaces.remove(hs);
    }

    public ASProjSave proj(Point preProj) {

        HashMap<AffineSpace, ASFail> lowerLevel = new HashMap<>();
        List<ASFail> currentLevel;

        for (int i = 0; i < preProj.dim(); i++) {
            
            currentLevel = new Choose<HalfSpace>(new ArrayList<HalfSpace>(halfSpaces), i)
                    .chooseStream()
                    .map(subsetOfHalfSpaces -> new ASFail(subsetOfHalfSpaces))
                    .collect(Collectors.toList());

            ASProjSave proj = currentLevel.stream()
                    .filter(asf -> asf.mightContainProj(lowerLevel, preProj))
                    .map(asf -> new ASProjSave(preProj, asf.asNode))
                    .filter(p -> hasElement(p.proj))
                    .min(Comparator.comparing(p -> p.proj.d(preProj)))
                    .orElse(null);

            if (proj != null) return proj;
            
            lowerLevel.clear();
            currentLevel.forEach(asf -> lowerLevel.put(asf.asNode.as, asf));
        }
        throw new EmptyPolytopeException();
    }

    public boolean hasElement(Point p) {
        return halfSpaces.stream().allMatch(hs -> hs.hasElement(p));
    }

    public class ASProjSave {

        public Point proj;
        public AffineSpace as;

        public ASProjSave(Point preProj, ASNode asn) {
            this.proj = proj;

            if (!affineSpacesProjections.containsKey(asn)) {
                affineSpacesProjections.put(asn, asn.as.linearSpace().getProjFunc());
                proj = asn.as.proj(proj);
            } else
                proj = asn.as.proj(affineSpacesProjections.get(asn.as), preProj);

        }

    }

    
    ///////////////////////////////////////////////////////////////////////////
    public void removeExcept(AffineSpace as) {

        if (as.isAllSpace()) {
            affineSpacesProjections.clear();
            halfSpaces.clear();
            return;
        }

        HashSet<Plane> planesToBePreserved = as.intersectingPlanesSet();
        halfSpaces.removeIf(hs -> !planesToBePreserved.contains(hs.boundary()));
        affineSpacesProjections
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

        ASProjSave asProj = proj(preProj);

        travelThrough = asProj.as;
        
        if (asProj.proj.equals(y)) {
            throw new EmptyPolytopeException();
        }

        gradInBounds = y.minus(asProj.proj);

    }
}
