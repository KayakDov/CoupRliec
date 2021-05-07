package Convex.GradDescentFeasibility.Proj.ASKeys;

import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Convex.GradDescentFeasibility.Proj.ASFail;
import Convex.GradDescentFeasibility.Proj.ASNode;

/**
 *
 * @author dov
 */
public class ASKeyAS extends ASKey{

    private AffineSpace as;
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
            if(!askp.planes[i].normal().equals(as.linearSpace().normals[i]) ||
                    askp.planes[i].b.get(0) != as.b.get(i) ) return false;
        return true;
    }

    @Override
    public boolean equals(ASKeyRI askp) {
        if(askp.immidiateSubSpace.b.dim() != as.b.dim() + 1) return false;
        for(int i = 0, j = 0; i < as.b.dim(); i++, j++){
            if(j == askp.removeIndex) j++;
            if(!askp.immidiateSubSpace.linearSpace().normals[j].equals(as.linearSpace().normals[i]) ||
                    askp.immidiateSubSpace.b.get(j) != as.b.get(i) ) return false;
        }
        return true;
    }
}
