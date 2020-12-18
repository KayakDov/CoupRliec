package Convex;

import Convex.Linear.AffineSpace;
import RnSpace.points.Point;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Care needs to be taken when instances are built that they are actually
 * polyhedral cones since no checks are in place.
 *
 * @author Dov Neimand
 */
public class PolytopeCone extends Polytope {

    private Point tip;

    public PolytopeCone(Point tip) {
        this.tip = tip;
    }

    public Point getTip() {
        return tip;
    }

    public void setTip(Point tip) {
        this.tip = tip;
    }

    public void addPlaneWithNormal(Point normal) {
        add(new HalfSpace(tip, normal));
    }

    public void addPlanesWithNormals(Stream<Point> normal) {
        addAll(normal.map(n -> new HalfSpace(tip, n)).collect(Collectors.toList()));
    }

    public static PolytopeCone samplePolytopeCone() {
        PolytopeCone sample = new PolytopeCone(new Point(3));
        sample.addPlaneWithNormal(new Point(-1, 0, 0));
        sample.addPlaneWithNormal(new Point(0, -1, 0));
        sample.addPlaneWithNormal(new Point(0, 0, -1));
        return sample;
    }

    public static PolytopeCone randomPolytopeCone(int numFaces, int numDim) {
        Sphere unitSphere = new Sphere(numDim);
        PolytopeCone sample = new PolytopeCone(new Point(numDim));
        sample.addPlanesWithNormals(IntStream.range(0, numFaces).mapToObj(i -> unitSphere.randomSurfacePoint()));
        return sample;
    }

///////////////////////////possible projection algortihm////////////////////////
    private HalfSpace almostNearest(Point y) {
        return stream().max(Comparator.comparing(hs -> hs.d(y))).get();
    }

    /**
     * this can be made
     *
     * @param list
     * @return
     */
    private boolean isLoop(HashSet<HalfSpace> set, HalfSpace candidate) {
        return set.contains(candidate);
    }

    @Override
    public Point proj(Point y) {
        HashSet<HalfSpace> projPath = new HashSet<>(size());

        HalfSpace candidate = almostNearest(y);

        while (!isLoop(projPath, candidate)) {
            projPath.add(candidate);
            candidate = almostNearest(candidate.proj(y));
        }

        AffineSpace intersection = candidate.boundary();
        HalfSpace inAffineSpace = almostNearest(candidate.proj(y));

        while (inAffineSpace != candidate) {
            intersection = intersection.intersection(inAffineSpace.boundary());
            inAffineSpace = almostNearest(inAffineSpace.proj(y));
        }

        return intersection.proj(y);

    }

}
