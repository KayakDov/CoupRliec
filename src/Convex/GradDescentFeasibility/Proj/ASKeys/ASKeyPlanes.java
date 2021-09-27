package Convex.GradDescentFeasibility.Proj.ASKeys;

import Convex.LinearRn.RnPlane;
import java.util.Arrays;

/**
 *This 
 * @author dov
 */
public class ASKeyPlanes extends ASKey{
    RnPlane[] planes;
    
    public ASKeyPlanes(RnPlane[] planes) {
        super(Arrays.stream(planes).mapToInt(RnPlane::hashCode).sum());
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
