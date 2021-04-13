package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys.ASKey;
import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.EmptyPolytopeException;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys.ASKeyAS;
import Matricies.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

    private ConcurrentHashMap<ASKey, ASNode> projectionFunctions;
    private List<Plane> planes;

    public ProjPolytope(int dim) {
        this.planes = new ArrayList<>(dim);
        this.projectionFunctions = new ConcurrentHashMap<>((int) Math.pow(2, dim));
    }

    public Plane[] concat(Plane[] a, Plane b) {
        Plane[] arrayOfPlanes = new Plane[a.length + 1];
        System.arraycopy(a, 0, arrayOfPlanes, 0, a.length);
        arrayOfPlanes[a.length] = b;
        return arrayOfPlanes;
    }

    private ASFail[] nextLevel(ASFail[] lowerLevel, Point y) {

//        int choose = lowerLevel[0].asNode.planeList.length + 1;
//        ASFail[] nextLevel = new ChoosePlanes(planes, choose).chooseStream()
//                .map(planeArray -> new ASFail(planeArray, y, 0, projectionFunctions))
//                .toArray(ASFail[]::new);
//        
//        
/////////////////////////////////////////////////////////////////////////////
//        ASFail nextLevel[] = new ASFail[ChoosePlanes.choose(planes.size(), lowerLevel[0].asNode.planeList.length + 1)];
//
//        int to = 0;
//        for (ASFail asf : lowerLevel)
//            for (int pl = asf.asNode.lastIndex() + 1; pl < planes.size(); pl++, to++) {
//                if(new ASFail(concat(asf.asNode.planeList, planes.get(pl)), y, pl, projectionFunctions) == null) throw new RuntimeException("oh no!");
//                nextLevel[to] = new ASFail(concat(asf.asNode.planeList, planes.get(pl)), y, pl, projectionFunctions);
//            }
        ////////////////////////////////////////////////////////////
        ASFail nextLevel[] = Arrays.stream(lowerLevel)//.parallel()
                .flatMap(asf -> {
//                    System.out.println(asf.asNode.lastIndex());

                    return IntStream.range(asf.asNode.lastIndex() + 1, planes.size()).mapToObj(i -> {
                        ASFail asftemp = new ASFail(concat(asf.asNode.planeList, planes.get(i)), y, i, projectionFunctions);
//                        System.out.println("\t" + asftemp.asNode.lastIndex());
                        return asftemp;
                    });
                }
                ).toArray(ASFail[]::new);

//        Arrays.stream(nextLevel).forEach(asf -> System.out.print(asf.asNode.lastIndex() + " "));

//        System.out.println("lower level length = " + lowerLevel.length);
//        System.out.println(planes.size() + " choose " + (lowerLevel[0].asNode.planeList.length + 1) + " = " + (ChoosePlanes.choose(planes.size(), lowerLevel[0].asNode.planeList.length + 1) + "==" + nextLevel.length));

        if (Arrays.stream(nextLevel).anyMatch(asf -> asf == null)) {
            Arrays.stream(nextLevel).forEach(asft -> System.out.println(asft));
            throw new RuntimeException("null asf found");
        }
        if ((ChoosePlanes.choose(planes.size(), lowerLevel[0].asNode.planeList.length + 1) != nextLevel.length))
            throw new RuntimeException("Bad choose set");
//        
//        Arrays.stream(nextLevel).forEach(asf -> System.out.print(asf.asNode.lastIndex() + " "));
//        System.out.println();
//      

//        System.out.println("");

        return nextLevel;

    }

    private ASProj projOnLevel(Point preProj, ASFail[] level, ConcurrentHashMap<ASKey, ASFail> ll) {
        return Arrays.stream(level).parallel()
                .filter(asf -> asf.mightContainProj(ll, preProj))
                .map(asFail -> new ASNProj(preProj, asFail))
                .filter(p -> hasElement(p))
                .findAny()
                .orElse(null);
    }

    public ASProj proj(Point preProj, Point y) {
        if (hasElementParallel(preProj))
            return new ASProj(preProj, new ASNode.AllSpace(preProj.dim()));

        ASFail currentLevel[] = new ASFail[planes.size()];
        Arrays.setAll(currentLevel, i -> new ASFail(planes.get(i), i, projectionFunctions));

        ASProj proj = projOnLevel(preProj, currentLevel, null);

        int size = ChoosePlanes.choose(y.dim(), y.dim() / 2);
        ConcurrentHashMap<ASKey, ASFail> lowerLevel = new ConcurrentHashMap<>(size > 0 ? size : Integer.MAX_VALUE);

        for (int i = 2; i < Math.max(y.dim(), planes.size()); i++) {

            if (proj != null) return proj;

            lowerLevel.clear();

            Arrays.stream(currentLevel).parallel().forEach(asf -> lowerLevel.put(new ASKeyAS(asf), asf));
            currentLevel = nextLevel(currentLevel, y);

            proj = projOnLevel(preProj, currentLevel, lowerLevel);

        }

        if (y.dim() == 2 || proj != null) return proj;
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
            if (!p.asn.planeSet.contains(plane) && !plane.aboveOrContains(p.proj))
                return false;
        return true;
    }

    public void removeExcept(ASNode as) {

        
        if (as.as.isAllSpace()) {
            projectionFunctions.clear();
            planes.clear();
            return;
        }
        
        Set<Plane> planesToBePreserved = as.planeSet;

//        planes.parallelStream()
//                .filter(plane -> !planesToBePreserved.contains(plane))
//                .forEach(plane -> projectionFunctions.entrySet()
//                    .removeIf(asnMap -> asnMap.getValue().planeSet.contains(plane)));

        projectionFunctions.entrySet().removeIf(entry -> !planesToBePreserved.containsAll(entry.getValue().planeSet));
        
        planes.removeIf(hs -> !planesToBePreserved.contains(hs));
        
    }
    
    
    public void add(Plane plane){
        if(planes.contains(plane)) throw new RuntimeException("This plane has already been added to ProjPolytope and has index " + planes.indexOf(plane) + " out of " + planes.size() + ".");//TODO: remove
        planes.add(plane);
    }

}
