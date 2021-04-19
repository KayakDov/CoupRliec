package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys.ASKey;
import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Convex.Polytope;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.EmptyPolytopeException;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys.ASKeyAS;
import Matricies.Point;
import Matricies.PointD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import listTools.ChoosePlanes;

/**
 * A class for finding the projection onto a polytope.
 * @author dov
 */
public class ProjPolytope {

    /**
     * All the saved affine space projection functions.
     */
    private final ConcurrentHashMap<ASKey, ASNode> projectionFunctions;
    /**
     * All the planes that are boundaries of half spaces who intersect to form the polytope.
     * The planes intersection is p dot x is less than or equal to b for each plane.
     */
    private final List<Plane> planes;

    /**
     * The constructor
     * @param dim the n for R^n.
     */
    public ProjPolytope(int dim) {
        this.planes = new ArrayList<>(dim);
        this.projectionFunctions = new ConcurrentHashMap<>((int) Math.pow(2, dim));
    }

    /**
     * The constructor
     * @param p A polytope that is going to be projected onto.
     */
    public ProjPolytope(Polytope p) {
        this.planes = p.planes().collect(Collectors.toList());
        this.projectionFunctions = new ConcurrentHashMap<>((int) Math.pow(2, planes.size()));
    }

    private Plane[] concat(Plane[] a, Plane b) {
        Plane[] arrayOfPlanes = new Plane[a.length + 1];
        System.arraycopy(a, 0, arrayOfPlanes, 0, a.length);
        arrayOfPlanes[a.length] = b;
        return arrayOfPlanes;
    }

    private ASFail[] nextLevel(ASFail[] lowerLevel, Point y) {

        return Arrays.stream(lowerLevel).parallel()
                .flatMap(asf -> IntStream.range(asf.asNode.lastIndex() + 1, planes.size())
                        .mapToObj(i -> 
                                new ASFail(concat(asf.asNode.planeArray, planes.get(i)), 
                                    y, 
                                    i, 
                                    projectionFunctions
                                    )
                )).toArray(ASFail[]::new);

    }

    private ASProj projOnLevel(Point preProj, ASFail[] level, ConcurrentHashMap<ASKey, ASFail> ll) {
        return Arrays.stream(level)//.parallel()
                .filter(asf -> asf.mightContainProj(ll, preProj))
                .map(asFail -> new ASNProj(preProj, asFail))
                .filter(p -> p.asn.spaceIsNonEmpty())
                .filter(p -> hasElement(p))
                .findAny()
                .orElse(null);
    }

    public Point proj(Point y) {
        return proj(y, null).proj;
    }

    public ASProj proj(Point preProj, Point y) {
        if (hasElementParallel(preProj))
            return new ASProj(preProj, new ASNode.AllSpace(preProj.dim()));

        ASFail currentLevel[] = new ASFail[planes.size()];
        Arrays.setAll(currentLevel, i -> new ASFail(planes.get(i), i, projectionFunctions));

        ASProj proj = projOnLevel(preProj, currentLevel, null);

        int size = ChoosePlanes.choose(preProj.dim(), preProj.dim() / 2);
        ConcurrentHashMap<ASKey, ASFail> lowerLevel = new ConcurrentHashMap<>(size > 0 ? size : Integer.MAX_VALUE);

        for (int i = 2; i <= Math.min(preProj.dim(), planes.size()); i++) {

            if (proj != null) return proj;

            lowerLevel.clear();

            Arrays.stream(currentLevel).parallel().forEach(asf -> lowerLevel.put(new ASKeyAS(asf), asf));
            currentLevel = nextLevel(currentLevel, y);
                        
            proj = projOnLevel(preProj, currentLevel, lowerLevel);

        }

        if (preProj.dim() == 2 || proj != null) return proj;
        throw new EmptyPolytopeException();
    }

    public boolean hasElement(Point p) {
        for (Plane plane : planes)
            if (!plane.aboveOrContains(p)) return false;
        return true;

    }

    public boolean hasElementParallel(Point p) {
        return planes.parallelStream().allMatch(hs -> hs.aboveOrContains(p));
    }

    public boolean hasElement(Point p, double epsilon) {
        return planes.stream().allMatch(hs -> hs.aboveOrContains(p, epsilon));
    }

    public boolean hasElement(ASNProj p) {
        for (Plane plane : planes)
            if (!p.asn.planeSet().contains(plane) && !plane.aboveOrContains(p.proj))
                return false;
        return true;
    }

    public void removeExcept(ASNode as) {

        if (as.as.isAllSpace()) {
            projectionFunctions.clear();
            planes.clear();
            return;
        }

        Set<Plane> planesToBePreserved = as.planeSet();

//        projectionFunctions.entrySet().removeIf(entry -> !planesToBePreserved.containsAll(entry.getValue().planeSet));
        projectionFunctions.entrySet().parallelStream()
                .filter(entry -> !planesToBePreserved.containsAll(entry.getValue().planeSet()))
                .forEach(needsRemoval -> projectionFunctions.remove(needsRemoval.getKey()));

        planes.removeIf(hs -> !planesToBePreserved.contains(hs));

    }

    public void add(Plane plane) {
        if (planes.contains(plane))
            throw new RuntimeException("This plane has already been added to ProjPolytope and has index " + planes.indexOf(plane) + " out of " + planes.size() + ".");//TODO: remove
        planes.add(plane);
    }

}
