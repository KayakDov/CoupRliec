package Convex.GradDescentFeasibility.Proj;

import Convex.GradDescentFeasibility.Proj.ASKeys.ASKey;
import Convex.Linear.Plane;
import Convex.Polytope;
import Convex.GradDescentFeasibility.EmptyPolytopeException;
import Convex.GradDescentFeasibility.Proj.ASKeys.ASKeyAS;
import Matricies.Point;
import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * Adds the plane b onto the array of planes a
     * @param a
     * @param b
     * @return 
     */
    private Plane[] concat(Plane[] a, Plane b) {
        Plane[] arrayOfPlanes = new Plane[a.length + 1];
        System.arraycopy(a, 0, arrayOfPlanes, 0, a.length);
        arrayOfPlanes[a.length] = b;
        return arrayOfPlanes;
    }

    /**
     * Generates the nexe level of affine spaces from the current level.  A level
     * of affine spaces is all the affine spaces that are the intersection of
     * level planes.
     * @param lowerLevel the immediate superspaces of the spaces to be generated.
     * @param y a point common to all the affine spaces.  If there is no such
     * point, then pass null.
     * @return 
     */
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

    /**
     * The projection onto this level of affine spaces, if it exists.  If it does not, then null is returned.
     * @param preProj The point being projected
     * @param level the level-affine spaces to be checked.  
     * These spaces are the intersections of level number of hyperplanes.
     * @param ll the level - 1 affine spaces that have already been checked.
     * @return the projection if it exists, null otherwise.
     */
    private ASProj projOnLevel(Point preProj, ASFail[] level, ConcurrentHashMap<ASKey, ASFail> ll) {
        return Arrays.stream(level).parallel()
                .filter(asf -> asf.meetsNecesaryCriteria(ll, preProj))  
                .filter(asf -> hasElement(asf))
                .findAny()
                .map(asFail -> new ASNProj(preProj, asFail))             
                .orElse(null);
    }

    /**
     * The projection point of y onto this polytope
     * @param y
     * @return 
     */
    public Point proj(Point y) {
        return proj(y, null).proj;
    }
    
    /**
     * All the planes represented as ASFails.
     * @return 
     */
    private ASFail[] oneAffineSpace(){
        ASFail currentLevel[] = new ASFail[planes.size()];
        Arrays.setAll(currentLevel, i -> new ASFail(planes.get(i), i, projectionFunctions));
        return currentLevel;
    }
    
    /** 
     * An empty vestibule for holding the lower level of affine spaces.
     * @return 
     */
    private ConcurrentHashMap<ASKey, ASFail> lowerLevel(Point preProj){
        int size = ChoosePlanes.choose(preProj.dim(), preProj.dim() / 2);
        return new ConcurrentHashMap<>(size > 0 ? size : Integer.MAX_VALUE);
    }

    /**
     * the projection of y onto this polytope.
     * @param preProj the point being projected.
     * @param y a point common to all the affine spaces of this polytope.  
     * This should be null if no such point exists.
     * @return the projection of preproj onto this polytope
     */
    public ASProj proj(Point preProj, Point y) {
        if (hasElementParallel(preProj))
            return new ASProj(preProj, new ASNode.AllSpace(preProj.dim()));

        ASFail currentLevel[] = oneAffineSpace();

        ASProj proj = projOnLevel(preProj, currentLevel, null);

        ConcurrentHashMap<ASKey, ASFail> lowerLevel = lowerLevel(preProj);

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

    /**
     * Does this polytope have the element p
     * @param p
     * @return 
     */
    public boolean hasElement(Point p) {
        for (Plane plane : planes)
            if (!plane.aboveOrContains(p)) return false;
        return true;

    }

    /**
     * Does this polytope have the element p.  The search will take place in
     * parallel.
     * @param p
     * @return 
     */
    public boolean hasElementParallel(Point p) {
        return planes.parallelStream().allMatch(hs -> hs.aboveOrContains(p));
    }

    /**
     * Checks to see if the point is within an epsilon distance of this polytope.
     * @param p
     * @param epsilon
     * @return 
     */
    public boolean hasElement(Point p, double epsilon) {
        return planes.stream().allMatch(hs -> hs.aboveOrContains(p, epsilon));
    }

    /**
     * Does this polytope contain the latest projection onto the given affine space.
     * @param p
     * @return 
     */
    public boolean hasElement(ASFail p) {
        for (Plane plane : planes)
            if (!p.asNode.planeSet().contains(plane) && !plane.aboveOrContains(p.projOntoPersoanlPoly))
                return false;
        return true;
    }

    /**
     * removes all of the hyperplanes that don't intersect to make the given affine space.
     * @param as 
     */
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

    /**
     * This polytope will now be the intersection of what this polytope was, and 
     * the given plane.
     * @param plane 
     */
    public void add(Plane plane) {
        if (planes.contains(plane))
            throw new RuntimeException("This plane has already been added to ProjPolytope and has index " + planes.indexOf(plane) + " out of " + planes.size() + ".");//TODO: remove
        planes.add(plane);
    }

}
