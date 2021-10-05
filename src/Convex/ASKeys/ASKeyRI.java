package Convex.ASKeys;

import Convex.LinearRn.RnAffineSpace;
import Hilbert.AffineSpace;

/**
 * This is for generating an affine space key by the remove index.  That is,
 * from a subspace with a removed constraint.
 * @author Dov Neimand
 */
public class ASKeyRI extends ASKey{
    
    AffineSpace immidiateSubSpace;
    int removeIndex;
    
    /**
     * The constructor
     * @param as a subspace of the affine space that this key is meant to access.
     * @param removeIndex the index of the constraint removed. 
     */
    public ASKeyRI(RnAffineSpace as, int removeIndex){
        super(as.hashCode() - as.hashRow(removeIndex));
        this.removeIndex = removeIndex;
        immidiateSubSpace = as;
    }
    
    /**
     * The index of the constraint removed from the sub space.
     * @return 
     */
    public int removeIndex() {
        return removeIndex;
    }

    @Override
    public boolean equals(ASKeyAS askas) {
        return askas.equals(this);
    }

    @Override
    public boolean equals(ASKeyPlanes askp) {
        return askp.equals(this);
    }

    @Override
    public boolean equals(ASKeyRI askRI) {
        return immidiateSubSpace.equals(askRI.immidiateSubSpace) && removeIndex == askRI.removeIndex;
    }

    @Override
    public boolean equals(ASKeyPCo askaco) {
        return askaco.equals(this);
    }
    
    
    
}
