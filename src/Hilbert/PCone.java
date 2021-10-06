package Hilbert;

import Convex.ASKeys.ASKey;
import Convex.ASKeys.ASKeyPConeRI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import listTools.Pair;

/**
 * The ACone of an affine space is defined 
 * @author Dov Neimand
 * @param <Vec>
 */
public class PCone<Vec extends Vector<Vec>> extends Polyhedron<Vec>{
    
    
    
    private final int indexOfLastHS;
    private SavedArgMin<Vec> sam;
    private final StrictlyConvexFunction<Vec> f;
    
    /**
     * 
     * @param f the function we seek to find the minimum of
     * @param hs a list of halfspaces 
     * @param i The index of the last half space in the list of all the half spaces.
     * This is used to generate subspaces without creating redundancies with 
     * the subspaces of other ACones.
     */
    public PCone(StrictlyConvexFunction<Vec> f, List<HalfSpace<Vec>> hs, int i) {
        super(hs);
        indexOfLastHS = i;
        this.f = f;
    }
    
    /**
     * Creates a new polyhedron with the additional inequality constraint.
     * @param addOn the inequality constraint being added
     * @param lastIndex the index of addOn
     * @return A new PCone at the intersection of these half spaces and the added one.
     */
    public PCone<Vec> concat(HalfSpace<Vec> addOn, int lastIndex){
        ArrayList<HalfSpace<Vec>> concatHSList = new ArrayList(halfspaces.size()+1);
        concatHSList.addAll(halfspaces);
        concatHSList.add(addOn);
        return new PCone<>(f, halfspaces, lastIndex);
    }
        

    

    /**
     * Returns the minimum over this polyhedral cone and weather or not this cone
     * meets the necessary criteria. 
     * @param superCones a set containing all the possible super cones of this one whose halfspaces are halfspaces of the greater polyhedron.
     * @return the minimum over this cone.
     */
    public SavedArgMin<Vec> min(Map<ASKey, PCone<Vec>> superCones) {
        if(isAllSpace()) return new SavedArgMin(f.argMinAffine(AffineSpace.<Vec>allSpace()), true);
                
        return sam = IntStream.range(0, halfspaces.size())
                .mapToObj(i -> {
                        Vec superAConeArgMin = superCones.get(new ASKeyPConeRI(this, i)).sam.argMin();
                        return (getHS(i).hasElement(superAConeArgMin)) ? 
                                new SavedArgMin<Vec>(superAConeArgMin, false):
                                null;
                }
                ).filter(obj -> obj != null)                 
                .findAny()                
                .orElse(new SavedArgMin<>(f.argMinAffine(affineSpace()), true));
        
        
    }

    @Override
    public int hashCode() {
        return stream().mapToInt(hs-> hs.boundary().hashCode()).sum();
    }
    
    /**
     * The affine space that is contained in the surfaces of all the halfspaces.
     * @return 
     */
    private AffineSpace<Vec> affineSpace(){
        return new AffineSpace<>(stream().map(hs -> hs.boundary()).toArray(Plane[]::new));
    }

    /**
     * The index of the last halfspace in an external list.
     * @return 
     */
    public int getIndexOfLastHS() {
        return indexOfLastHS;
    }
    
    /**
     * A polhedral cone with no half spaces, this is the entire hilbert space.
     * @param <Vec>
     * @param f
     * @return 
     */
    public static<Vec extends Vector<Vec>> PCone<Vec> allSpace(StrictlyConvexFunction<Vec> f){
        return new PCone<>(f, new ArrayList<>(0), -1);
    }
}

/**
 * This class is nothing more than a pair, the argmin and weather or not the 
 * necessary criteria are met.
 * @author Dov Neimand
 * @param <Vec> 
 */
class SavedArgMin<Vec extends Vector<Vec>> extends Pair<Vec, Boolean>{

        /**
         * The constructor for the pair
         * @param argMin the argmin over this ACone
         * @param meetsNecesaryCriteria weather or not this ACone meets the necessary criteria
         */
        public SavedArgMin(Vec argMin, boolean meetsNecesaryCriteria) {
            super(argMin, meetsNecesaryCriteria);
        }
        /**
         * The argmin over this ACone
         * @return 
         */
        public Vec argMin(){
            return l;
        }
        /**
         * Does this ACone meet the necessary criteria
         * @return 
         */
        public boolean meetsNecCrti(){
            return r;
        }
                
    }