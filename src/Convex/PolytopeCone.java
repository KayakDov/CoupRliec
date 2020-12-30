package Convex;

import Matricies.PointDense;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Care needs to be taken when instances are built that they are actually
 * polyhedral cones since no checks are in place.
 *
 * @author Dov Neimand
 */
public class PolytopeCone extends Polytope {

    private PointDense tip;

    public PolytopeCone(PointDense tip) {
        this.tip = tip;
    }

    public PointDense getTip() {
        return tip;
    }

    public void setTip(PointDense tip) {
        this.tip = tip;
    }

    public void addPlaneWithNormal(PointDense normal) {
        add(new HalfSpace(tip, normal));
    }

    public void addPlanesWithNormals(Stream<PointDense> normal) {
        addAll(normal.map(n -> new HalfSpace(tip, n)).collect(Collectors.toList()));
    }

    public static PolytopeCone samplePolytopeCone() {
        PolytopeCone sample = new PolytopeCone(new PointDense(3));
        sample.addPlaneWithNormal(new PointDense(-1, 0, 0));
        sample.addPlaneWithNormal(new PointDense(0, -1, 0));
        sample.addPlaneWithNormal(new PointDense(0, 0, -1));
        return sample;
    }



}
