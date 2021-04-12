package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys;

import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys.ASKey;
import Convex.Linear.Plane;
import java.util.Arrays;

/**
 *
 * @author dov
 */
public class ASKeyPlanes extends ASKey{
    Plane[] planes;
    
    public ASKeyPlanes(Plane[] planes) {
        super(Arrays.stream(planes).mapToInt(Plane::hashCode).sum());
        this.planes = planes;
    }

    @Override
    public boolean equals(ASKeyAS askas) {
        return askas.equals(this);
    }

    @Override
    public boolean equals(ASKeyPlanes askp) {
        return Arrays.equals(planes, askp.planes);
    }

    @Override
    public boolean equals(ASKeyRI askRI) {
       if(askRI.immidiateSubSpace.b.dim() != planes.length + 1) return false;
        for(int i = 0, j = 0; i < planes.length; i++, j++)
            if(!planes[i].normal().equals(askRI.immidiateSubSpace.linearSpace().normals[j])
                    || planes[i].b.get(0) !=  askRI.immidiateSubSpace.b.get(j)) 
                return false;
        return true;
    }
    
    
}
