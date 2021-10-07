package Convex.ASKeys;

import Hilbert.PCone;
import Hilbert.HalfSpace;
import Hilbert.Plane;
import java.util.List;

/**
 * Affine space keys for ACones
 * @author Dov Neimand
 */
public class ASKeyPCo extends ASKey{

    private List<HalfSpace> halfspaces;
    int removeIndex;

    public ASKeyPCo(PCone aCone) {
        this(aCone, aCone.numHalfSpaces() + 1);
    }
    
     public ASKeyPCo(PCone aCone, int removeIndex) {
        super(aCone.hashCode());
        this.halfspaces = aCone.getHalfspaces();
        this.removeIndex = removeIndex;
    }
    
    /**
     * The boundary of the halfspace of index i
     * @param i the index of the desired plane
     * @return 
     */
    public Plane get(int i){
        return halfspaces.get(i).boundary();
    }
    
    @Override
    public boolean equals(ASKeyPCo askaco) {
        for(int i = 0; i < halfspaces.size(); i++)
            if(!askaco.get(i).equals(get(i)))
                return false;
        return true;
    }

    
}
