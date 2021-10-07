package Convex.ASKeys;

import Hilbert.PCone;
import Hilbert.Plane;

/**
 *
 * @author Kayak
 */
public class ASKeyPConeRI extends ASKeyPCo{
    
    int removeIndex;
    
    public ASKeyPConeRI(PCone aCone, int removeIndex) {
        super(aCone);
        this.removeIndex = removeIndex;
        hashCode -= aCone.getHS(removeIndex).hashCode();
        
    }
    
    @Override
    public Plane get(int i){
        if(i < removeIndex) return super.get(i);
        return super.get(i + 1);
    }

    public int getRemoveIndex() {
        return removeIndex;
    }

    @Override
    public boolean equals(ASKeyPCo askaco) {
        for (int i = 0; i < halfspaces.size() - 1; i++)
            if (!askaco.get(i).equals(get(i)))
                return false;
        return true;
    }
    
    
}
