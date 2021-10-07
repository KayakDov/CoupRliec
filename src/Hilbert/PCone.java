package Hilbert;

import Convex.ASKeys.ASKey;
import Convex.ASKeys.ASKeyPConeRI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import listTools.ArgMinContainer;

/**
 * The ACone of an affine space is defined
 *
 * @author Dov Neimand
 * @param <Vec>
 */
public class PCone<Vec extends Vector<Vec>> extends Polyhedron<Vec> {

    private final int indexOfLastHS;
    private ArgMinContainer<Vec> savedArgMin;
    private final StrictlyConvexFunction<Vec> f;

    /**
     *
     * @param f the function we seek to find the minimum of
     * @param hs a list of halfspaces
     * @param i The index of the last half space in the list of all the half
     * spaces. This is used to generate subspaces without creating redundancies
     * with the subspaces of other ACones.
     */
    public PCone(StrictlyConvexFunction<Vec> f, List<HalfSpace<Vec>> hs, int i) {
        super(hs);
        indexOfLastHS = i;
        this.f = f;
    }

    /**
     * Creates a new polyhedron with the additional inequality constraint.
     *
     * @param addOn the inequality constraint being added
     * @param lastIndex the index of addOn
     * @return A new PCone at the intersection of these half spaces and the
     * added one.
     */
    public PCone<Vec> concat(HalfSpace<Vec> addOn, int lastIndex) {
        ArrayList<HalfSpace<Vec>> concatHSList
                = new ArrayList(halfspaces.size() + 1);
        concatHSList.addAll(halfspaces);
        concatHSList.add(addOn);
        return new PCone<>(f, concatHSList, lastIndex);
    }

    /**
     * Checks to see if the optimal point over the immediate superspace PCone
     * achieved by removing the i indexed halfspace is in this PCone.  If it is
     * then we do not meet the necessary criteria and and that optimal point is
     * the optimal point over this PCone.
     * @param i the halfspace to be removed.
     * @param superCones an inventory of PCones that contains all the immediate superspaces.
     * @return null if the optimal point over the immediate supercone is outside 
     * this cone, and the optimal point over the immediate supercone if it's 
     * inside this cone.
     */
    private ArgMinContainer<Vec> superCone(int i, Map<ASKey, PCone<Vec>> superCones) {

        ASKey asKey = new ASKeyPConeRI(this, i);
        
        PCone<Vec> superCone = superCones.get(asKey);
        System.out.println(superCone);
        Vec superAConeArgMin = superCone.savedArgMin.argMin();
        
        if (getHS(i).hasElement(superAConeArgMin))
            return new ArgMinContainer<>(superAConeArgMin, false);
        return null;
    }

    /**
     * Returns the minimum over this polyhedral cone and weather or not this
     * cone meets the necessary criteria.
     *
     * @param superCones a set containing all the possible super cones of this
     * one whose halfspaces are halfspaces of the greater polyhedron.
     * @return the minimum over this cone.
     */
    public ArgMinContainer<Vec> min(Map<ASKey, PCone<Vec>> superCones) {
        if (isAllSpace())
            savedArgMin = new ArgMinContainer(f.argMinAffine(AffineSpace.<Vec>allSpace()), true);

        else savedArgMin = IntStream.range(0, halfspaces.size()).parallel()
                    .mapToObj(i -> superCone(i, superCones))
                    .filter(obj -> obj != null)
                    .findAny()
                    .orElse(new ArgMinContainer<>(f.argMinAffine(affineSpace()), true));

        return savedArgMin;

    }

    /**
     * The affine space that is contained in the surfaces of all the halfspaces.
     *
     * @return
     */
    private AffineSpace<Vec> affineSpace() {
        return new AffineSpace<>(stream().map(hs -> hs.boundary()).toArray(Plane[]::new));
    }

    /**
     * The index of the last halfspace in an external list.
     *
     * @return
     */
    public int getIndexOfLastHS() {
        return indexOfLastHS;
    }

    /**
     * A polhedral cone with no half spaces, this is the entire hilbert space.
     *
     * @param <Vec>
     * @param f
     * @return
     */
    public static <Vec extends Vector<Vec>> PCone<Vec> allSpace(StrictlyConvexFunction<Vec> f) {
        return new PCone<>(f, new ArrayList<>(0), -1);
    }
}
