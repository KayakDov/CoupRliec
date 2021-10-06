package Convex.ASKeys;

import Hilbert.PCone;
import Hilbert.HalfSpace;
import Hilbert.Plane;
import java.util.List;

/**
 * Affine spcae keys for ACones
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
    public boolean equals(ASKeyAS askas) {
        int size = halfspaces.size();
        if(askas.as.nullMatrixRows().length != size) return false;
        for(int i = 0; i < size; i++)
            if(askas.as.rowEquals(i, get(i))) return false;
        return true;
    }

    @Override
    public boolean equals(ASKeyPlanes askp) {
        if(askp.planes.length != halfspaces.size()) return false;
        for(int i = 0; i < askp.planes.length; i++)
            if(!askp.planes[i].equals(get(i)))
                    return false;
        return true;
    }

    @Override
    public boolean equals(ASKeyRI askri) {
        
        if(askri.immidiateSubSpace.b.dim()-1 != halfspaces.size()) return false;
        
        for(int i = 0, j = 0; i < halfspaces.size(); i++, j++){
            if(j == askri.removeIndex) j++;
            if(!askri.immidiateSubSpace.rowEquals(j, get(i))) return false;
        }
        return true;
    }

    @Override
    public boolean equals(ASKeyPCo askaco) {
        for(int i = 0; i < halfspaces.size(); i++)
            if(!askaco.get(i).equals(get(i)))
                return false;
        return true;
    }

    
}
