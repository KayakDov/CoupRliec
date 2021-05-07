package Convex.GradDescentFeasibility.Proj.ASKeys;

import Convex.Linear.AffineSpace;

/**
 *
 * @author dov
 */
public class ASKeyRI extends ASKey{
    
    AffineSpace immidiateSubSpace;
    int removeIndex;
    
    public ASKeyRI(AffineSpace as, int removeIndex){
        super(as.hashCode() - as.hashRow(removeIndex));
        this.removeIndex = removeIndex;
        immidiateSubSpace = as;
    }
    
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
    
    
    
}
