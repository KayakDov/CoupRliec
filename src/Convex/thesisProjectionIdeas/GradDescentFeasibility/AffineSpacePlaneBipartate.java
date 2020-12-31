package Convex.thesisProjectionIdeas.GradDescentFeasibility;

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
import java.util.stream.Stream;

/**
 * Can this class be spead up by a better choice of dataSturctures?
 *
 * @author dov
 */
public class AffineSpacePlaneBipartate {

    private final int dim;

    private class PlaneNode {

        private Plane plane;
        private Set<ASNode> affineSpaces;

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
            for (ASNode asn : affineSpaces) {
                asn.prepareForRemoval(this);
                affineSpaceNodes.remove(asn.affineSpace);
            }
        }
    }

    private class ASNode {

        public AffineSpace affineSpace;
        public List<PlaneNode> planes;

        public ASNode(AffineSpace affineSpace, List<PlaneNode> planes) {
            this.affineSpace = affineSpace;
            setPlanes(planes);
        }

        public ASNode(PlaneNode pn) {
            planes = new ArrayList<>(dim);
            planes.add(pn);
            affineSpace = pn.plane;
        }

        private void setPlanes(List<PlaneNode> planes) {
            this.planes = planes;
            planes.forEach(pn -> pn.affineSpaces.add(this));
        }

        public void prepareForRemoval() {
            prepareForRemoval(null);
        }

        public void prepareForRemoval(PlaneNode except) {
            affineSpaceNodes.remove(this);
            planes.forEach(planeNode -> {
                if (planeNode != except) planeNode.affineSpaces.remove(this);
            });
        }
    }

    private HashMap<Plane, PlaneNode> planeNodes = null;
    private HashMap<AffineSpace, ASNode> affineSpaceNodes = null;

    public AffineSpacePlaneBipartate(int dim) {
        this.affineSpaceNodes = new HashMap<>(dim * dim);
        this.dim = dim;
        planeNodes = new HashMap<>(dim);
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
     * @param numPlanes 
     * @return 
     */
    public Stream<AffineSpace> affineSpaces(int numPlanes) {
        return affineSpaceNodes()
                .parallel()
                .map(asn -> asn.affineSpace)
                .filter(as -> as.linearSpace().getNormals().length == numPlanes);//TODO: store the data so it doesn't need to be filtered.
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


    /**
     * removes all the planes that don't contain this affine space.
     *
     * @param as
     * @param polytope removes fromt the polytope any half spaces whose planes
     * don't contain the give affine space.
     */
    public void removeExcept(AffineSpace as, Polytope polytope) {

//        System.out.println("Convex.thesisProjectionIdeas.GradDescentFeasibility.AffineSpaceHandler.removeExcept()");
        if (as.isAllSpace()) {
            polytope.clearFaces();
            planeNodes.clear();
            affineSpaceNodes.clear();
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
}
