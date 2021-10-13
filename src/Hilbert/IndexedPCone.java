package Hilbert;

import java.util.ArrayList;
import java.util.List;

/**
 * A PCone with an index
 * @author Dov Neimand
 * @param <Vec> The type of Hilbert Space
 */
public class IndexedPCone<Vec extends Vector<Vec>> extends PCone<Vec>{
    
    private final int indexOfLastHS;
    /**
     * 
     * @param f the function that will be optimized over this PCone.
     * @param hs a list of the halfspaces that intersect to make this
     * @param i The index of the last half space in the list of all the half
     * spaces. This is used to generate subspaces without creating redundancies
     * with the subspaces of other ACones.
     */
    public IndexedPCone(StrictlyConvexFunction<Vec> f, List<HalfSpace<Vec>> hs, int i) {
        super(f, hs);
        indexOfLastHS = i;
    }

    
    
    /**
     * The index of the last halfspace in an external list.
     *
     * @return
     */
    @Override
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
    public static <Vec extends Vector<Vec>> IndexedPCone<Vec> allSpace(StrictlyConvexFunction<Vec> f) {
        return new IndexedPCone<>(f, new ArrayList<>(0), -1);
    }

    
    
    
}
