package Hilbert;

import Convex.ASKeys.ASKey;
import Convex.ASKeys.ASKeyPConeRI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import tools.ArgMinContainer;

/**
 * The ACone of an affine space is defined
 *
 * @author Dov Neimand
 * @param <Vec>
 */
public class PCone<Vec extends Vector<Vec>> extends Polyhedron<Vec> {

    protected ArgMinContainer<Vec> savedArgMin;
    protected final StrictlyConvexFunction<Vec> f;
    
    
    /**
     * Copy constructor
     * @param pCone
     */
    public PCone(PCone<Vec> pCone) {
        super(pCone.halfspaces);
        this.f = pCone.f;
    }

    /**
     *
     * @param f the function we seek to find the minimum of
     * @param hs a list of halfspaces
     *
     */
    public PCone(StrictlyConvexFunction<Vec> f, List<HalfSpace<Vec>> hs) {
        super(hs);
        this.f = f;
    }

    /**
     *
     * @param f the function we seek to find the minimum of
     * @param hs a halfspace
     */
    public PCone(StrictlyConvexFunction<Vec> f, HalfSpace<Vec> hs) {
        super(hs);
        this.f = f;
    }

    /**
     * Creates a new half space from this polyhedron's half space list list with
     * the addon concatinated to it.
     *
     * @param addOn the half space to be concatinated.
     * @return
     */
    protected List<HalfSpace<Vec>> concatHSToList(HalfSpace<Vec> addOn) {
        ArrayList<HalfSpace<Vec>> concatHSList
                = new ArrayList(halfspaces.size() + 1);
        concatHSList.addAll(halfspaces);
        concatHSList.add(addOn);
        return concatHSList;
    }

    /**
     * Creates a new polyhedron with the additional inequality constraint.
     *
     * @param addOn the inequality constraint being added
     * @return A new PCone at the intersection of these half spaces and the
     * added one.
     */
    public PCone<Vec> concat(HalfSpace<Vec> addOn) {
        return new PCone<>(f, concatHSToList(addOn));
    }

    /**
     * Creates a new polyhedron with the additional inequality constraint.
     *
     * @param addOn the inequality constraint being added
     * @return A new PCone at the intersection of these half spaces and the
     * added one.
     */
    public IndexedPCone<Vec> concat(HalfSpace<Vec> addOn, int indexOfLastPCone) {
        return new IndexedPCone<>(f, concatHSToList(addOn), indexOfLastPCone);
    }

    /**
     * Checks to see if the optimal point over the immediate superspace PCone
     * achieved by removing the i indexed halfspace is in this PCone.If it is
     * then we do not meet the necessary criteria and and that optimal point is
     * the optimal point over this PCone.
     *
     * @param superCone an immediate supercone of this one.
     * @param i the halfspace to be removed.
     * @return null if the optimal point over the immediate supercone is outside
     * this cone, and the optimal point over the immediate supercone if it's
     * inside this cone.
     */
    protected ArgMinContainer<Vec> meetsNecesary(PCone<Vec> superCone, int i) {

        Vec superAConeArgMin = superCone.getSavedArgMin().argMin();
        if (getHS(i).hasElement(superAConeArgMin))
            return new ArgMinContainer<>(superAConeArgMin, false);
        return null;

    }
    
    /**
     * Retrieves the supercone from the map of cones with one less codim than the current;
     * @param i the index of the half space that is in this cone but not the desired supercone
     * @param superCones all of the cones with codim one less than this one, to include the desired supercone.
     * @return a supercone like this cone, but without constraint i.
     */
    private PCone<Vec> superCone(int i, Map<ASKey, PCone<Vec>> superCones){
        return superCones.get(new ASKeyPConeRI(this, i));
    }

    protected ArgMinContainer<Vec> allSpaceArgMin(){
        return new ArgMinContainer(f.argMinAffine(AffineSpace.<Vec>allSpace()), true);
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
            return savedArgMin = allSpaceArgMin();

        return savedArgMin = findAnyOrAffine(
                intStream()
                    .mapToObj(i -> meetsNecesary(superCone(i, superCones), i))
        );

    }

    /**
     * The affine space that is contained in the surfaces of all the halfspaces.
     *
     * @return
     */
    protected AffineSpace<Vec> affineSpace() {
        return new AffineSpace<>(stream().map(hs -> hs.boundary()).toArray(Plane[]::new));
    }

    /**
     * A polhedral cone with no half spaces, this is the entire hilbert space.
     *
     * @param <Vec>
     * @param f
     * @return
     */
    public static <Vec extends Vector<Vec>> PCone<Vec> allSpace(StrictlyConvexFunction<Vec> f) {
        return new PCone<>(f, new ArrayList<>(0));
    }

    /**
     * returns a default value for index of last halfspace.
     *
     * @return
     */
    public int getIndexOfLastHS() {
        return -1;
    }

    /**
     * The saved Arg Min if it exists.
     *
     * @return
     */
    public ArgMinContainer<Vec> getSavedArgMin() {
        return savedArgMin;
    }

    /**
     * 
     * @return the function to be optimized over this cones
     */
    public StrictlyConvexFunction<Vec> getF() {
        return f;
    }
    
    /**
     * This methods checks to see if any of the argmin containers disqualify this
     * PCone.If none do, then it calculates the optimal point over this affine 
     * space.
     * @param stream a stream of argMin containers
     * @return 
     */
    protected ArgMinContainer findAnyOrAffine(Stream<ArgMinContainer> stream){
        try{
        return stream.filter(obj -> obj != null)
                    .findAny()
                    .orElse(new ArgMinContainer<>(f.argMinAffine(affineSpace()), true));
        }catch(ClassCastException cce){
            throw cce;
        }
    }
    
    /**
     * an integer for each half space
     * @return a parallel stream of integers, one for each half space.
     */
    protected IntStream intStream(){
        return IntStream.range(0, halfspaces.size()).parallel();
    }
    
}
