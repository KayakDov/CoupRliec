package Convex.ASKeys;

import Hilbert.PCone;
import Hilbert.Plane;

/**
 * Affine space keys for ACones
 *
 * @author Dov Neimand
 */
public class ASKeyPCo extends ASKey {

    protected final PCone halfspaces;

    public ASKeyPCo(PCone pCone) {
        super(pCone.hashCode());
        this.halfspaces = pCone;
    }
    
    protected ASKeyPCo(PCone pCone, int removeIndex) {
        super(0);
        this.halfspaces = pCone;
        for(int i = 0; i < pCone.numHalfSpaces(); i++) 
            if(i != removeIndex)hashCode += pCone.getHS(i).hashCode();
        
    }
    
    /**
     * how many half spaces intersect to make this PCone.
     * @return 
     */
    public int coDim(){
        return halfspaces.numHalfSpaces();
    }

    /**
     * The boundary of the halfspace of index i
     *
     * @param i the index of the desired plane
     * @return
     */
    public Plane get(int i) {
        return halfspaces.getHS(i).boundary();
    }

    @Override
    public boolean equals(ASKeyPCo askaco) {
        for (int i = 0; i < halfspaces.numHalfSpaces(); i++)
            if (!askaco.get(i).equals(get(i)))
                return false;
        return true;
    }

    public PCone generatePCone(){
        return new PCone(halfspaces);
    }
}
