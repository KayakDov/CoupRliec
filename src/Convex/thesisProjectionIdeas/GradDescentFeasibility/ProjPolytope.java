
package Convex.thesisProjectionIdeas.GradDescentFeasibility;

import Convex.HalfSpace;
import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Matricies.Point;
import Matricies.PointD;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 *What does this polytope need to do:
 * 1:  Save any projection functions that might be used again.
 * 2:  A failed point for a polyhedral cone is a projection onto one of the affine spaces of that cone that is contained in that cone.
 * failed points need to be past up to higher level intersections.
 * @author dov
 */
public class ProjPolytope {
    
    private class ASNode{
        public AffineSpace as;
        public HashSet<Plane> planes;

        public ASNode(AffineSpace as, HashSet<Plane> planes) {
            this.as = as;
            this.planes = planes;
        }
        public ASNode(Plane plane) {
            this.as = plane;
            this.planes = new HashSet<Plane>(1);
            planes.add(plane);
        }
        
    }
    private class ASFail{
        public ASNode asNode;
        public ArrayList<Point> failed;
        public boolean fail;

        public ASFail(ASNode asNode) {
            this.asNode = asNode;
            this.failed = new ArrayList<>(1);
        }
        //this class needs to be fleshed out so that the failure state and fail points are set given a preproj
        
    }
    
    public ArrayList<ASNode> affineSpaces = new ArrayList<>(); //TODO: init size for speeed
    public HashSet<HalfSpace> halfSpaces = new HashSet<>();

    public ProjPolytope() {
    }
    
    
    
    public void remove(HalfSpace hs){
        affineSpaces.removeIf(asn -> asn.planes.contains(hs.boundary()));
        halfSpaces.remove(hs);
    }
    
    public PointD proj(Point preProj){
        
        List<ASFail> current = halfSpaces.stream().map(hs -> new ASFail(plane)).collect(Collectors.toList());
        current.forEach(action);
        
        
        List<ASFail> next;
        for(int i = 0; i < preProj.dim(); i++){
            
        }
            
    }
    
}
