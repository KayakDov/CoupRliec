package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.HalfSpace;
import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.EmptyPolytopeException;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Partition;
import Matricies.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

    public ConcurrentHashMap<ASKey, ASNode> asProjs;
    public List<Plane> planes;

    public ProjPolytope(Partition part) {
        int dim = part.getGradient().dim();
        this.planes = new ArrayList<>(dim);
        this.asProjs = new ConcurrentHashMap<>((int) Math.pow(2, dim));
        this.gradInBounds = part.getGradient();
        this.part = part;
        travelThrough = AffineSpace.allSpace(gradInBounds.dim());
    }

    public Point grad() {
        return gradInBounds;
    }

    public Plane[] concat(Plane[] a, Plane b) {
        Plane[] arrayOfPlanes = new Plane[a.length + 1];
        System.arraycopy(a, 0, arrayOfPlanes, 0, a.length);
        arrayOfPlanes[a.length] = b;
        return arrayOfPlanes;
    }

    private List<ASFail> nextLevel(List<ASFail> lowerLevel, Point y) {
        List<ASFail> nextLevel = lowerLevel
                .parallelStream()
                .flatMap(asf -> IntStream
                .range(asf.asNode.lastIndex + 1, planes.size())
                .mapToObj(i -> new ASFail(concat(asf.asNode.planeList, planes.get(i)), y, i, asProjs))
                ).collect(Collectors.toList());
        return nextLevel;
    }

    public ASProj proj(Point preProj, Point y) {
        if (hasElementParallel(preProj))
            return new ASProj(preProj, AffineSpace.allSpace(preProj.dim()));

        List<ASFail> currentLevel = IntStream.range(0, planes.size())
                .parallel()
                .mapToObj(i -> new ASFail(planes.get(i), i, asProjs).setMightContainProj(planes.get(i).below(preProj)))
                .collect(Collectors.toList());

        ASProj proj = currentLevel
                .parallelStream()
                .filter(asf -> asf.mightContProj)
                .map(asFail -> new ASProj(asFail.asNode.as.proj(preProj), asFail.asNode.somePlane()))
                .filter(p -> hasElement(p.proj))
                .findAny()
                .orElse(null);
        int size = ChoosePlanes.choose(y.dim(), y.dim() / 2);
        ConcurrentHashMap<ASKey, ASFail> lowerLevel = new ConcurrentHashMap<>(size > 0 ? size : Integer.MAX_VALUE);

        for (int i = 2; i < y.dim(); i++) {

            if (proj != null) return proj;

            lowerLevel.clear();

            currentLevel.parallelStream().forEach(asf -> lowerLevel.put(new ASKey(asf.asNode), asf));

            currentLevel = nextLevel(currentLevel, y);

            /////////////////////Good way to do it///////////////////////////////
            proj = currentLevel.parallelStream()
                    .filter(asf -> asf.mightContainProj(lowerLevel, preProj))
                    .map(asf -> new ASProj(preProj, asf))
                    .filter(asp -> hasElement(asp.proj))
                    .findAny()//min(Comparator.comparing(p -> p.proj.d(preProj)))
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

//            System.out.println("dim:" + i + " percent pass" + (double) pass / (pass + fail));
//            proj = candidates.parallelStream().min(Comparator.comparing(p -> p.proj.d(preProj))).orElse(null);
            ///////end of slow section to be cut/////////////////////////////////
        }

        if (y.dim() == 2 || proj != null) return proj;
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

    public void removeExcept(AffineSpace as) {

        if (as.isAllSpace()) {
            asProjs.clear();
            planes.clear();
            return;
        }

        HashSet<Plane> planesToBePreserved = as.intersectingPlanesSet();

        planes.parallelStream()
                .filter(plane -> !planesToBePreserved.contains(plane))
                .forEach(plane -> asProjs.entrySet()
                    .removeIf(asnMap -> asnMap.getValue().planeSet.contains(plane)));
        
        planes.removeIf(hs -> !planesToBePreserved.contains(hs));

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
