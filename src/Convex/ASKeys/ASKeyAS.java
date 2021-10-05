package Convex.ASKeys;

import Convex.LinearRn.RnAffineSpace;
import Convex.LinearRn.RnPlane;
import Convex.GradDescentFeasibility.Proj.ASFail;
import Convex.GradDescentFeasibility.Proj.ASNode;
import Hilbert.AffineSpace;

/**
 *
 * @author dov
 */
public class ASKeyAS extends ASKey{

    AffineSpace as;
    
    public ASKeyAS(AffineSpace as) {
        super(as.hashCode());
        this.as = as;
    }
     
    public ASKeyAS(ASNode as){
        this(as.as());
    }
    public ASKeyAS(ASFail asf){
        this(asf.asNode);
    }

    @Override
    public boolean equals(ASKeyAS askas) {
        return askas.as.equals(as);
    }

    @Override
    public boolean equals(ASKeyPlanes askp) {
        if(askp.planes.length != as.b.dim()) return false;
        for(int i = 0; i < as.b.dim(); i++)
            if(!as.rowEquals(i, askp.planes[i])) return false;
        return true;
    }

    @Override
    public boolean equals(ASKeyRI askp) {
        if(askp.immidiateSubSpace.b.dim() != as.b.dim() + 1) return false;
        for(int i = 0, j = 0; i < as.b.dim(); i++, j++){
            if(j == askp.removeIndex) j++;
            if(!askp.immidiateSubSpace.rowEquals(j, i, as)) return false;
        }
        return true;
    }

    @Override
    public boolean equals(ASKeyPCo askaco) {
        return askaco.equals(this);
    }
}
