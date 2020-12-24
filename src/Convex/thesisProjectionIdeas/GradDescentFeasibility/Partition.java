package Convex.thesisProjectionIdeas.GradDescentFeasibility;

import Convex.HalfSpace;
import Matricies.PointDense;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 * Variouse partitions of the polytope.  Halfspaces visited vs not visited, half spaces containing the point vs those not containing the point, etc...
 * @author Dov Neimand
 */
public class Partition {

    private GradDescentFeasibility poly;
    private ArrayList<HalfSpace> containing;
    private Set<HalfSpace> excluding;
    private HashSet<HalfSpace> visited;

    private PointDense gradient;

    public Partition(PointDense y, GradDescentFeasibility poly) {
        this.poly = poly;
        containing = new ArrayList<>(poly.size());
        excluding = new HashSet<>(poly.size());
        gradient = new PointDense(poly.dim());
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
     * To be callsed if the iteration point arrives at this spot
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

    public Stream<HalfSpace> containing() {
        return containing.parallelStream();
    }

    public ArrayList<HalfSpace> containingSet() {
        return containing;
    }
    
    

    public Set<HalfSpace> excludingSet() {
        return excluding;
    }

    public Stream<HalfSpace> excluding() {
        return excluding.parallelStream();
    }

    boolean pointIsFeasible() {
        return excluding.size() == 0;
    }

    public Stream<HalfSpace> downhillContaining(PointDense grad, double epsilon) {
        return containing().filter(hs -> /*!visited.contains(hs) &&*/ hs.normal().dot(grad) < -epsilon);
    }

    /**
     * Everything that's downhill, not yet visited, and excludes the current
     * point
     *
     * @param grad
     * @param epsilon
     * @return
     */
    public Stream<HalfSpace> downhillExcluding(PointDense grad, double epsilon) {
        return excluding().filter(hs -> hs.normal().dot(grad) > epsilon);
    }

    public Stream<HalfSpace> downHill(PointDense grad, double epsilon) {
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
    public HalfSpace nearestDownhillFaceContaining(PointDense y, PointDense grad, double epsilon) {

        if(containing.isEmpty()) return null;
        return downhillContaining(grad, epsilon)
                        .min(Comparator.comparing(hs -> hs.boundary().lineIntersection(grad, y).d(y)))
                        .orElse(null);

        
    }

    public Stream<HalfSpace> excludingSpacesBetweenHereAndThere(PointDense grad, PointDense goTo, double epsilon) {
        return downhillExcluding(grad, epsilon).filter(hs -> hs.hasElement(goTo, epsilon));
    }

    
    public PointDense getGradient() {
        return gradient;
    }
    
    

}
