package Convex;

import Matricies.PointD;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Care needs to be taken when instances are built that they are actually
 * polyhedral cones since no checks are in place.
 *
 * @author Dov Neimand
 */
public class PolytopeCone extends Polytope {

    private PointD tip;

    public PolytopeCone(PointD tip) {
        this.tip = tip;
    }

    public PointD getTip() {
        return tip;
    }

    public void setTip(PointD tip) {
        this.tip = tip;
    }

    public void addPlaneWithNormal(PointD normal) {
        add(new HalfSpace(tip, normal));
    }

    public void addPlanesWithNormals(Stream<PointD> normal) {
        addAll(normal.map(n -> new HalfSpace(tip, n)).collect(Collectors.toList()));
    }

    public static PolytopeCone samplePolytopeCone() {
        PolytopeCone sample = new PolytopeCone(new PointD(3));
        sample.addPlaneWithNormal(new PointD(-1, 0, 0));
        sample.addPlaneWithNormal(new PointD(0, -1, 0));
        sample.addPlaneWithNormal(new PointD(0, 0, -1));
        return sample;
    }



}
