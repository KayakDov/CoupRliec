package Hilbert.Optimization;

import Hilbert.AffineSpace;
import Hilbert.HalfSpace;
import Hilbert.StrictlyConvexFunction;
import Hilbert.Vector;
import Matricies.Point;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import tools.ArgMinContainer;

/**
 *
 * @author Dov Neimand
 */
public class PCone<Vec extends Vector<Vec>> {

    /**
     * These are the half spaces of the polyhedron P. These half spaces make up
     * the alphabet of the trie.
     */
    protected final List<HalfSpace<Vec>> allHalfSpaces;
    /**
     * The parent node to this one in the trie.
     */
    public final PCone trieSuperCone;

    /**
     * The children nodes of this one in the trie and all the immediate subcones
     * of this one that maintain the strict monotone rising order of half space
     * indices.
     */
    private PCone<Vec>[] immediateSubcones;

    /**
     * This cone is the Hilbert space cone. It should have a childe for each
     * half space in the polyhedron P. This should be the root cone of the trie.
     */
    private final PCone<Vec> hilbertCone;

    /**
     * The indices of each of the half spaces that intersect to make this
     * P-cone. They should be in ascending order. This information is also
     * available from the trie, but in the trie it must be accessed via iterator
     * from last to first instead of allowing for random access as arrays do.
     *
     */
    private final int[] hsIndicies;

    /**
     * All the immediate supercones of this cone.
     */
    private PCone<Vec>[] superCones;

    /**
     * The arg min of this cone, which is not available by default and needs to
     * be calculated.
     */
    protected ArgMinContainer<Vec> savedArgMin;

    /**
     * The constructor for a p-cone that is the Hilbert Space
     *
     * @param halfSpaceList
     */
    public PCone(List<HalfSpace<Vec>> halfSpaceList) {
        this(-1, halfSpaceList, null, null);
    }

    /**
     * The number of halfspaces that intersect to make this cone.
     *
     * @return
     */
    public int codim() {
        return hsIndicies.length;
    }

    /**
     * The constructor.
     *
     * @param halfSpaceindex the index of the most recently appended half space.
     * @param halfSpaceList the list of all the half spaces of the polyhedron.
     * @param trieParentCone the PCone in the trie that generated this one. The
     * index of its last halfspce should be less than halfSpaceIndex.
     * @param hilbertCone The root cone of the trie.
     */
    public PCone(int halfSpaceindex, List<HalfSpace<Vec>> halfSpaceList, PCone<Vec> trieParentCone, PCone<Vec> hilbertCone) {
        this.allHalfSpaces = halfSpaceList;
        this.trieSuperCone = trieParentCone;
        this.hilbertCone = hilbertCone;

        hsIndicies = trieParentCone != null
                ? new int[trieParentCone.hsIndicies.length + 1]
                : new int[0];

        if (hsIndicies.length > 1)
            System.arraycopy(trieParentCone.hsIndicies, 0,
                    hsIndicies, 0,
                    hsIndicies.length - 1);
        if (hsIndicies.length > 0)
            hsIndicies[hsIndicies.length - 1] = halfSpaceindex;
    }

    /**
     * gets the intersection of this cone and the halfspace with the proffered
     * index.
     *
     * @param halfSpaceIndex the index of the halfspace that is to intersect
     * this cone.
     * @return A subcone of this cone.
     */
    public PCone<Vec> subCone(int halfSpaceIndex) {
        if (immediateSubcones == null) setSubCones();
        int lastIndex = (codim() == 0 ? 0 : hsIndicies[codim() - 1] + 1);
        return subCone0Ind(halfSpaceIndex - lastIndex);
    }

    /**
     * returns the subcone that is stored at the given index in the array of
     * subcones. If there is no cone at that index, one is created and saved.
     *
     * @param i
     * @return
     */
    public PCone<Vec> subCone0Ind(int i) {
        if (immediateSubcones[i] != null) return immediateSubcones[i];
        else return immediateSubcones[i] = new PCone<>(
                    i + (codim() > 0 ? hsIndicies[codim() - 1] + 1 : 0),
                    allHalfSpaces,
                    this,
                    hilbertCone == null ? this : hilbertCone);
    }

    /**
     * Creates the array of subcones, initially empty.
     */
    private void setSubCones() {
        int halfSpaceIndex = hsIndicies.length > 0 ? hsIndicies[hsIndicies.length
                - 1] : -1;
        immediateSubcones = new PCone[allHalfSpaces.size() - halfSpaceIndex - 1];
    }

    /**
     * The immediate subcones of this cone.
     *
     * @return
     */
    public Stream<PCone<Vec>> immediateSubCones() {

        if (immediateSubcones == null) setSubCones();
        return IntStream.range(0, immediateSubcones.length).mapToObj(i -> subCone0Ind(i));
    }

    /**
     * The superspace generated by removing the half space with the proffered
     * index from the set of half spaces that intersect to make this cone.
     *
     * @param i
     * @return
     */
    public PCone<Vec> immidiateSuper(int i) {

        PCone<Vec> superCone = hilbertCone;
        for (int j = 0; j < codim() && superCone != null; j++)
            if (i != j)
                superCone = superCone.subCone(hsIndicies[j]);

        return superCone;
    }
    
    /**
     * Has the optimal point been computed for this cones corresponding affine
     * space.
     */
    private boolean affineSpaceComputed = false;

    /**
     * Has the optimal point been computed for this cones corresponding affine
     * space.
     */
    public boolean isAffineSpaceComputed() {
        return affineSpaceComputed;
    }

    /**
     * The affine space from which this cone emanates.
     *
     * @return
     */
    public AffineSpace<Vec> affineSpace() {
        if (hsIndicies.length == 0) return AffineSpace.<Vec>allSpace();
        Vec[] normals = (Vec[]) (Array.newInstance(getHS(0).normal().getClass(), codim()));
        Point b = new Point(codim());

        for (int i = 0; i < codim(); i++) {
            HalfSpace<Vec> hs = getHS(i);
            normals[i] = hs.normal();
            b.data[i] = hs.getBoundry().b();
        }
        return new AffineSpace(normals, b);
    }

    /**
     * The argmin of this pcone.
     *
     * @return
     */
    public ArgMinContainer<Vec> aMin(StrictlyConvexFunction<Vec> f) {

        if (savedArgMin != null) return savedArgMin;

        for (int i = 0; i < codim(); i++) {
            ArgMinContainer<Vec> argMinSuper = immidiateSuper(i).aMin(f);
            if (argMinSuper.isPolyhedralMin) return argMinSuper;

            if (getHS(i).hasElement(argMinSuper.argMin()))
                return savedArgMin = new ArgMinContainer(argMinSuper.argMin(), false);
        }

        Vec aMin = f.argMin(affineSpace());
        affineSpaceComputed = true;

        return savedArgMin = new ArgMinContainer(aMin, allHalfSpaces.parallelStream().allMatch(hs -> hs.hasElement(aMin)));

    }

    /**
     * gets the half space with the given index in the cones list of halfspaces.
     *
     * @param i the index in the list of halfspaces that intersect to make this
     * cone.
     * @return
     */
    public HalfSpace<Vec> getHS(int i) {
        return allHalfSpaces.get(hsIndicies[i]);
    }

    @Override
    public String toString() {
        return IntStream.range(0, codim()).mapToObj(i -> getHS(i)).toList().toString();
    }

    /**
     * Does this cone have a saved argMin
     *
     * @return
     */
    public boolean hasArgMin() {
        return savedArgMin != null;
    }
    
    /**
     * The number of affine spaces computed in the subtree rooted here.
     * @return 
     */
    public long numAffineSpacesComputed(){
        return (affineSpaceComputed?1:0) + (immediateSubcones ==null?0:
                Arrays.stream(immediateSubcones)
                        .filter(subCone -> subCone != null)
                        .mapToLong(subCone -> subCone.numAffineSpacesComputed())
                        .sum());
    }

}
