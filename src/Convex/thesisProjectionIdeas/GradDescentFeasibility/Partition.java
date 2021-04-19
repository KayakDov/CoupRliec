package Convex.thesisProjectionIdeas.GradDescentFeasibility;

import Convex.HalfSpace;
import Matricies.Point;
import Matricies.PointD;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 * Various partitions of the polytope.  Halfspaces visited vs not visited, 
 * half spaces containing the point vs those not containing the point, etc...
 * @author Dov Neimand
 */
public class Partition {

    /**
     * The underlying polytope
     */
    private FeasibilityGradDescent poly;
    /**
     * all the halfspaces containing the point
     */
    private ArrayList<HalfSpace> containing;
    /**
     * All the half spaces excluding the point
     */
    private Set<HalfSpace> excluding;
    /**
     * all the half spaces the point has visited.
     */
    private HashSet<HalfSpace> visited;

    /**
     * The gradient for the point pointing toward the polytope.
     */
    private PointD gradient;

    /**
     * The constructor
     * @param y the point all the partitions are around
     * @param poly the polytope whose half spaces are to be partitioned.
     */
    public Partition(Point y, FeasibilityGradDescent poly) {
        this.poly = poly;
        containing = new ArrayList<>(poly.size());
        excluding = new HashSet<>(poly.size());
        gradient = new PointD(poly.dim());
        visited = new HashSet<>(poly.size());

        Consumer<HalfSpace> sort = hs -> {
            if (hs.hasElement(y)) containing.add(hs);
            else {
                excluding.add(hs);
                gradient.addToMe(hs.normal().dir());
            }
        };
        poly.stream().forEach(sort);
        

    }

    /**
     * To be called if the iteration point arrives at this spot
     *
     * @param hs
     */
    public void enterSpace(HalfSpace hs) {
        passThroughSpace(hs);
        visited.add(hs);
    }

    /**
     * to be called if the iteration point passes through this spot without
     * stopping.
     *
     * @param hs
     */
    public void passThroughSpace(HalfSpace hs) {
        if (excluding.contains(hs)) {
            excluding.remove(hs);
            containing.add(hs);
            gradient.addToMe(hs.normal().dir().mult(-1));
        }
    }
    /**
     * The point passes through the list of halfspaces, changing this partition.
     * @param moveThrough 
     */
    public void passThroughSpaces(List<HalfSpace> moveThrough){
        if(moveThrough.isEmpty()) return;
        
        excludingSet().removeAll(moveThrough);
        containingSet().addAll(moveThrough);
        getGradient().addToMe(
                moveThrough.stream()
                        .map(hs -> hs.normal().dir())
                        .reduce((p1, p2) -> p1.plus(p2))
                        .get()
                        .mult(-1));
    }

    /**
     * All the halfspaces containing the point.
     * @return 
     */
    public Stream<HalfSpace> containing() {
        return containing.parallelStream();
    }

    /**
     * All the half spaces containing the point.
     * @return 
     */
    public ArrayList<HalfSpace> containingSet() {
        return containing;
    }
    
    /**
     * ALl the half spaces excluding the point.
     * @return 
     */
    public Set<HalfSpace> excludingSet() {
        return excluding;
    }

    /**
     * All the half spaces excluding the point.
     * @return 
     */
    public Stream<HalfSpace> excluding() {
        return excluding.parallelStream();
    }

    /**
     * Is the point inside the polytope.
     * @return 
     */
    boolean pointIsFeasible() {
        return excluding.isEmpty();
    }

    /**
     * All the half spaces downhill, in the direction of the gradient, that contain the point y.
     * @param grad
     * @param epsilon
     * @return 
     */
    public Stream<HalfSpace> downhillContaining(Point grad, double epsilon) {
        return containing().filter(hs -> /*!visited.contains(hs) &&*/ hs.normal().dot(grad) < -epsilon);
    }

    /**
     * Everything that's downhill, not yet visited, and excludes the current
     * point
     *
     * @param grad the uphill direction from the point
     * @param epsilon
     * @return
     */
    public Stream<HalfSpace> downhillExcluding(Point grad, double epsilon) {
        return excluding().filter(hs -> hs.normal().dot(grad) > epsilon);
    }

    /**
     * All the half spaced downhill of the point
     * @param grad the uphill direction fromthe point.
     * @param epsilon
     * @return 
     */
    public Stream<HalfSpace> downHill(PointD grad, double epsilon) {
        return Stream.concat(downhillContaining(grad, epsilon),
                downhillExcluding(grad, epsilon));
    }

    /**
     * This method will find the nearest half space that contains y along the
     * line in the downhill direction. This will have trouble if there are two
     * near halfspaces of equal distance from y.
     *
     * @param y
     * @param grad
     * @param epsilon
     * @return
     */
    public HalfSpace nearestDownhillFaceContaining(Point y, Point grad, double epsilon) {

        if(containing.isEmpty()) return null;
        return downhillContaining(grad, epsilon)
                        .min(Comparator.comparing(hs -> hs.boundary().lineIntersection(grad, y).d(y)))
                        .orElse(null);

        
    }

    /**
     * All of the downhill spaces between the point and goTo that exclude the point.
     * @param grad the uphill direction
     * @param goTo the other end of the line.
     * @param epsilon
     * @return 
     */
    public Stream<HalfSpace> excludingSpacesBetweenHereAndThere(Point grad, Point goTo, double epsilon) {
        return downhillExcluding(grad, epsilon).filter(hs -> hs.hasElement(goTo, epsilon));
    }

    /**
     * The gradient.
     * @return 
     */
    public PointD getGradient() {
        return gradient;
    }
    
}
