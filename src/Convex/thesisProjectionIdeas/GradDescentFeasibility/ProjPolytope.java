package Convex.thesisProjectionIdeas.GradDescentFeasibility;

import Convex.HalfSpace;
import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Matricies.Matrix;
import Matricies.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import listTools.ChoosePlanes;

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
        int dim = part.getGradient().dim();
        this.planes = new HashSet<>(dim);
        this.asProjs = new ConcurrentHashMap<>((int) Math.pow(2, dim));
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

        public Plane somePlane() {
            return planes.iterator().next();
        }

        private boolean localHasElement(Point x) {
            return planes.stream()
                    .allMatch(plane -> plane.aboveOrContains(x));
        }

        public Point getProj(Point preProj) {
            if (planes.size() == 1) return somePlane().proj(preProj);
            else {
                if (asProjs.containsKey(this))
                    return as.proj(asProjs.get(this), preProj);
                else {
                    asProjs.put(this, as.linearSpace().getProjFunc());
                    return as.proj(preProj);
                }
            }

        }

        @Override
        public String toString() {
            return as.toString(); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class ASFail {

        public ASNode asNode;
        public List<Point> failed;
        public boolean mightContProj;

        public ASFail(ASNode asNode) {
            this.asNode = asNode;
        }

        public ASFail(Plane plane) {
            this(new ASNode(plane));
        }

        public ASFail setMightContainProh(boolean might) {
            mightContProj = might;
            return this;
        }

        public ASFail(HashSet<Plane> hsList, Point y) {
            this(new ASNode(new AffineSpace(hsList).setP(y), hsList));
        }

        public ASFail(Plane[] planes, Point y) {

            this(new ASNode(new AffineSpace(planes).setP(y), Set.of(planes)));
        }

        private Plane somePlane() {
            return asNode.somePlane();
        }

        @Override
        public String toString() {
            return asNode.toString() + "\n" + failed;
        }

        public boolean mightContainProj(Map<AffineSpace, ASFail> lowerLevel, Point preProj) {

            if (asProjs.containsKey(asNode)) return mightContProj = true;

            failed = asNode.as.oneDown()
                    .flatMap(as -> {
                        ASFail oneDownI = lowerLevel.get(as);
                        if (oneDownI.mightContProj)
                            return Stream.of(oneDownI.asNode.getProj(preProj));
                        if (oneDownI.failed == null) return Stream.of();
                        return oneDownI.failed.stream();
                    })
                    .filter(p -> asNode.localHasElement(p))
                    .collect(Collectors.toList());

            return mightContProj = failed.isEmpty();
        }
    }
    //TODO: init size for speeed

    public ConcurrentHashMap<ASNode, Matrix> asProjs;
    public HashSet<Plane> planes;

    public void remove(HalfSpace hs) {
        asProjs.entrySet().removeIf(asn -> asn.getKey().planes.contains(hs.boundary()));
        planes.remove(hs);
    }

    public ASProj proj(Point preProj, Point y) {
        if (hasElementParallel(preProj))
            return new ASProj(preProj, AffineSpace.allSpace(preProj.dim()));

        ConcurrentHashMap<AffineSpace, ASFail> lowerLevel = new ConcurrentHashMap<>((int) ChoosePlanes.choose(y.dim(), y.dim() / 2));
        List<ASFail> currentLevel = planes
                .parallelStream()
                .map(plane -> new ASFail(plane).setMightContainProh(plane.below(preProj)))
                .collect(Collectors.toList());

        ASProj proj = currentLevel
                .parallelStream()
                .map(asFail -> new ASProj(asFail.asNode.as.proj(preProj), asFail.asNode.somePlane()))
                .filter(p -> hasElement(p.proj))
                .findAny()
                .orElse(null);

        for (int i = 2; i < y.dim(); i++) {
            
            if (proj != null) return proj;

            lowerLevel.clear();

            currentLevel.parallelStream().forEach(asf -> lowerLevel.put(asf.asNode.as, asf));
            
            currentLevel = new ChoosePlanes(new ArrayList<>(planes), i)
                    .chooseStream()
                    .map(arrayOfPlanes -> new ASFail(arrayOfPlanes, y))
                    .collect(Collectors.toList());

            /////////////////////Good way to do it///////////////////////////////
            proj = currentLevel.parallelStream()
                    .filter(asf -> asf.mightContainProj(lowerLevel, preProj))
                    .map(asf -> new ASProj(preProj, asf.asNode))
                    .filter(p -> hasElement(p.proj))
                    .min(Comparator.comparing(p -> p.proj.d(preProj)))
                    .orElse(null);
            //////////////End of good way to do it, begin profiler way to do it////////////////
//            List<ASProj> candidates = new ArrayList<>(5);
//
//            int fail = 0, pass = 0;
//            for (ASFail asf : currentLevel) {
//                if (asf.mightContainProj(lowerLevel, preProj)) {
//                    pass++;
//                    ASProj asps = new ASProj(preProj, asf.asNode);
//                    if (hasElement(asps.proj))
//                        candidates.add(asps);
//                } else fail++;
//            }
//
//            System.out.println((double) pass / (pass + fail));
//
//            ASProj proj = candidates.parallelStream().min(Comparator.comparing(p -> p.proj.d(preProj))).orElse(null);
            ///////////end of slow section to be cut/////////////////////////////////

        }
        throw new EmptyPolytopeException();
    }

    public boolean hasElement(Point p) {
        return planes.stream().allMatch(hs -> hs.aboveOrContains(p));
    }

    public boolean hasElementParallel(Point p) {
        return planes.parallelStream().allMatch(hs -> hs.aboveOrContains(p));
    }

    public boolean hasElement(Point p, double epsilon) {
        return planes.stream().allMatch(hs -> hs.aboveOrContains(p, epsilon));
    }

    public class ASProj {

        public Point proj;
        public AffineSpace as;

        public ASProj(Point proj, AffineSpace as) {
            this.proj = proj;
            this.as = as;
        }

        public ASProj(Point preProj, ASNode asn) {
            this.as = asn.as;
            if (asn.planes.size() == 1) proj = asn.somePlane().proj(preProj);
            else {
                if (asProjs.containsKey(asn))
                    proj = asn.as.proj(asProjs.get(asn), preProj);
                else {
                    asProjs.put(asn, asn.as.linearSpace().getProjFunc());
                    proj = asn.as.proj(preProj);
                }
            }
        }

    }

    public void removeExcept(AffineSpace as) {

        if (as.isAllSpace()) {
            asProjs.clear();
            planes.clear();
            return;
        }

        HashSet<Plane> planesToBePreserved = as.intersectingPlanesSet();
        planes.removeIf(hs -> !planesToBePreserved.contains(hs));
        asProjs.keySet().removeIf(asn -> !planesToBePreserved.containsAll(asn.planes));

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

        planes.add(add.boundary());

        ASProj asProj = proj(preProj, y);

        travelThrough = asProj.as;

        if (asProj.proj.equals(y)) {
            throw new EmptyPolytopeException();
        }

        gradInBounds = y.minus(asProj.proj);

    }
}
