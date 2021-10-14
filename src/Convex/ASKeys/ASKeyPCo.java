package Convex.ASKeys;

import Hilbert.PCone;
import Hilbert.HalfSpace;
import Hilbert.Plane;
import java.util.List;

/**
 * Affine space keys for ACones
 *
 * @author Dov Neimand
 */
public class ASKeyPCo extends ASKey {

    protected final List<HalfSpace> halfspaces;

    public ASKeyPCo(PCone pCone) {
        super(pCone.hashCode());
        this.halfspaces = pCone.getHalfspaces();
    }
    
    protected ASKeyPCo(PCone pCone, int removeIndex) {
        super(0);
        this.halfspaces = pCone.getHalfspaces();
        for(int i = 0; i < pCone.numHalfSpaces(); i++) 
            if(i != removeIndex)hashCode += pCone.getHS(i).hashCode();
        
    }


    /**
     * The boundary of the halfspace of index i
     *
     * @param i the index of the desired plane
     * @return
     */
    public Plane get(int i) {
        return halfspaces.get(i).boundary();
    }

    @Override
    public boolean equals(ASKeyPCo askaco) {
        for (int i = 0; i < halfspaces.size(); i++)
            if (!askaco.get(i).equals(get(i)))
                return false;
        return true;
    }

}
