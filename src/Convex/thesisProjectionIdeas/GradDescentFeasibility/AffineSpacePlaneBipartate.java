package Convex.thesisProjectionIdeas.GradDescentFeasibility;

import Convex.HalfSpace;
import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Convex.Polytope;
import Convex.PolytopeCone;
import Matricies.Point;
import Matricies.PointD;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Can this class be spead up by a better choice of dataSturctures?
 *
 * @author dov
 */
public class AffineSpacePlaneBipartate {

    private final int dim;

    public class PlaneNode {

        public Plane plane;
        public Set<ASNode> affineSpaces;

        /**
         * creates a new plane node and starts it off with its own affines space
         * node.
         *
         * @param plane
         */
        public PlaneNode(Plane plane) {
            this.plane = plane;
            affineSpaces = new HashSet<>(dim * dim);
        }

        public void setAffineSpaces(Set<ASNode> affineSpaces) {
            affineSpaces.forEach(asn -> asn.planes.add(this));
            affineSpaces.addAll(this.affineSpaces);
            this.affineSpaces = affineSpaces;
        }

        public void addAS(ASNode asn) {
            affineSpaces.add(asn);
            asn.planes.add(this);
        }

        /**
         * After calling this you still need to call planeNodes.remove(plane);
         */
        public void prepareForRemoval() {
            for (ASNode asn : affineSpaces) asn.prepareForRemoval(this);

        }
    }

    public class ASNode {

        public AffineSpace affineSpace;
        public List<PlaneNode> planes;

        public ASNode(AffineSpace affineSpace, List<PlaneNode> planes) {
            this.affineSpace = affineSpace;
            setPlanes(planes);
            while (planes.size() >= affSpByNumPlanes.size())
                affSpByNumPlanes.add(new HashSet<>());
            affSpByNumPlanes.get(planes.size()).add(this);
        }

        public ASNode(PlaneNode pn) {
            planes = new ArrayList<>(dim);
            planes.add(pn);
            affineSpace = pn.plane;
            affSpByNumPlanes.get(planes.size()).add(this);
        }

        private void setPlanes(List<PlaneNode> planes) {
            this.planes = planes;
            planes.forEach(pn -> pn.affineSpaces.add(this));
        }

        public void prepareForRemoval() {
            prepareForRemoval(null);
        }

        public void prepareForRemoval(PlaneNode except) {
            if (affineSpaceNodes.remove(affineSpace) == null)
                throw new RuntimeException("Failed to remove affine space.");
            if (!affSpByNumPlanes.get(planes.size()).remove(this))
                throw new RuntimeException("Failed to remove affine space.");
            planes.forEach(planeNode -> {
                if (planeNode != except) planeNode.affineSpaces.remove(this);
            });
        }
    }

    private HashMap<Plane, PlaneNode> planeNodes = null;
    private HashMap<AffineSpace, ASNode> affineSpaceNodes = null;
    private ArrayList<HashSet<ASNode>> affSpByNumPlanes;

    public ArrayList<HashSet<ASNode>> getAffSpByNumPlanes() {
        return affSpByNumPlanes;
    }

    
    
    public AffineSpacePlaneBipartate(int dim) {
        this.affineSpaceNodes = new HashMap<>(dim * dim);
        this.dim = dim;
        planeNodes = new HashMap<>(dim);
        affSpByNumPlanes = new ArrayList<>(dim);
        for (int i = 0; i < dim; i++)
            affSpByNumPlanes.add(new HashSet<>(dim * dim));
    }

    public void addPlane(Plane plane, Point y) {
        PlaneNode planeNode = new PlaneNode(plane);
        List<ASNode> asNodesToBeAdded = new ArrayList<>(affineSpaceNodes.values());

        for (ASNode asn : affineSpaceNodes.values()) {
            if (asn.planes.size() < dim) {
                AffineSpace as = asn.affineSpace.intersection(plane);
                List<PlaneNode> asPlanes = new ArrayList<>(asn.planes);
                asPlanes.add(planeNode);
                asNodesToBeAdded.add(new ASNode(as, asPlanes));
            }
        }
        asNodesToBeAdded.forEach(asntba -> affineSpaceNodes.put(asntba.affineSpace, asntba));

        ASNode planeASNode = new ASNode(planeNode);
        planeNode.affineSpaces.add(planeASNode);
        affineSpaceNodes.put(planeASNode.affineSpace, planeASNode);

        planeNodes.put(plane, planeNode);

        affineSpaceNodes()/*.filter(asn -> !asn.affineSpace.hasAPoint())*/.forEach(asn -> asn.affineSpace.setP(y));

//        System.out.println(affineSpaceNodes.size());
    }

    public void revmovePlane(Plane plane) {
        planeNodes.get(plane).prepareForRemoval();
        planeNodes.remove(plane);
    }

    private Stream<ASNode> affineSpaceNodes() {
        return affineSpaceNodes.values().stream();
    }

    /**
     * The number of planes that intersect to make this affine space.
     *
     * @param numPlanes
     * @return
     */
    public Stream<AffineSpace> affineSpaces(int numPlanes) {

        return affSpByNumPlanes.get(numPlanes).stream()
                .parallel()//;
                .map(asn -> asn.affineSpace);
    }

    public Stream<AffineSpace> affineSpaces() {
        return affineSpaceNodes()
                .parallel()
                .map(asn -> asn.affineSpace);//TODO: this should be parralel
    }

    public int numAffineSpaces() {
        return affineSpaceNodes.size();
    }

    public Stream<PlaneNode> planeNodes() {
        return planeNodes.values().stream();
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        planeNodes.values().stream().forEach(pn -> {
            toString.append("\nPlane:  ").append(pn.plane).append("\ncontributes to affines spaces:");
            pn.affineSpaces.forEach(asn -> {
                toString.append("\n\t").append(asn.affineSpace.toString());
            });
        });
        toString.append("\n");
        return toString.toString();
    }

    public Stream<Plane> planes(AffineSpace as) {
        return affineSpaceNodes.get(as).planes.stream().map(pn -> pn.plane).parallel();
    }

    /**
     * removes all the planes that don't contain this affine space.
     *
     * @param as
     * @param polytope removes fromt the polytope any half spaces whose planes
     * don't contain the give affine space.
     */
    public void removeExcept(AffineSpace as, Polytope polytope) {

        if (as.isAllSpace()) {
            polytope.clearFaces();
            planeNodes.clear();
            affineSpaceNodes.clear();
            affSpByNumPlanes.forEach(set -> set.clear());
            return;
        }

        //TODO: This section needs to be made faster!
        List<Plane> planesToBePreserved = affineSpaceNodes.get(as).planes.stream().map(pn -> pn.plane).collect(Collectors.toList());
        List<Plane> planesToBeRemoved = planeNodes.keySet().stream().filter(plane -> !planesToBePreserved.contains(plane)).collect(Collectors.toList());

        polytope.removeIf(hs -> planesToBeRemoved.stream().anyMatch(plane -> hs.boundary() == plane));

        planesToBeRemoved.forEach(plane -> {
            planeNodes.get(plane).prepareForRemoval();
            planeNodes.remove(plane);
        });

    }

    public void verifySpace(Polytope p) {
        if (true)
            throw new RuntimeException("This method is only meant to be called during test runs.");
        if (p.stream().anyMatch(hs -> !planeNodes.containsKey(hs.boundary())))
            throw new RuntimeException("polytope has a half space that affine space handler does not.");
        if (planeNodes.keySet().stream().anyMatch(plane -> p.stream().allMatch(hs -> hs.boundary() != plane)))
            throw new RuntimeException("The affine space handler has a plane that the polytope does not.");
    }
    
    /**
     * An affine space can only contain a projection 
     * @param as
     * @param preProj
     * @param epsilon
     * @return 
     */
    public boolean projectionRule(AffineSpace as, Point preProj, double epsilon){//TODO: Make this faster.  I should not be creating new half spaces here.

         return planes(as).anyMatch(plane -> !plane.above(preProj, epsilon));
        
//         if(!fastTest) return false;
//         
//        if(as.b.dim() == 1) return as.linearSpace().getNormals()[0].dot(preProj) > as.b.get(0);
//        boolean slowTest =  hsProjections(as, preProj).allMatch(p -> outOfPoly(as, p, epsilon));//I need to think about this and make it better.  Right now it doesn't seem to catch anything the first one doesn't catch, whcih doesn't make sence to me.
//        
//        if(!slowTest) System.out.println("Convex.thesisProjectionIdeas.GradDescentFeasibility.AffineSpacePlaneBipartate.projectionRule()");
//        return slowTest;
                
    }
    
    /**
     * Is the point p in the polytope made from the planes that intersect to form the affine space.
     * @param as
     * @param p
     * @param epsilon
     * @return 
     */
    private boolean outOfPoly(AffineSpace as, Point p, double epsilon){
        return planes(as).anyMatch(plane -> !plane.above(p, epsilon));
    }
    /**
     * The projections onto all the planes that intersect to make the affine space
     * @param as
     * @param preProj
     * @return 
     */
    public Stream<Point> hsProjections(AffineSpace as, Point preProj){
        return planes(as).map(plane -> plane.above(preProj)? preProj: plane.proj(preProj));
    }

}
