package Convex.ASKeys;

import Hilbert.GeneratingPCone;
import Hilbert.HalfSpace;
import Hilbert.PCone;
import Hilbert.Plane;
import java.util.ArrayList;

/**
 *
 * @author Kayak
 */
public class ASKeyPConeRI extends ASKeyPCo{
    
    int removeIndex;
    
    public ASKeyPConeRI(PCone aCone, int removeIndex) {
        super(aCone, removeIndex);
        this.removeIndex = removeIndex;
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
        for (int i = 0; i < halfspaces.numHalfSpaces() - 1; i++)
            if (!askaco.get(i).equals(get(i)))
                return false;
        return true;
    }

    @Override
    public int coDim() {
        return super.coDim() - 1; 
    }
}