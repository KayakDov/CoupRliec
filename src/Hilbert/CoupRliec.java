package Hilbert;

import Convex.ASKeys.ASKey;
import Convex.ASKeys.ASKeyPCo;
import Convex.GradDescentFeasibility.Proj.ASFail;
import Matricies.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The algorithm spelled out in Hilbert-Space Convex Optimization Utilizing 
 * Parallel Reduction of Linear Inequality to Equality Constraints
 * @author Dov Neimand
 */
public class CoupRliec<Vec extends Vector<Vec>> {
    private StrictlyConvexFunction<Vec> f;
    private HalfSpace<Vec>[] halfSpaces;

    public CoupRliec(StrictlyConvexFunction<Vec> f, HalfSpace<Vec>[] halfSpaces) {
        this.f = f;
        this.halfSpaces = halfSpaces;
    }
    
    public CoupRliec(StrictlyConvexFunction<Vec> f, Polyhedron<Vec> poly) {
        this.f = f;
        this.halfSpaces = poly.halfspaces.toArray(HalfSpace[]::new);
    }
    
    /**
     * The next set of affine spaces of the polyhedron of codimension i+1
     * @param prevCoDim the set of affine spaces of codimension i
     * @return the set of affine spaces of the polyhedron of codim i
     */
    private Map<ASKey, PCone<Vec>> nextCoDim(Collection<PCone<Vec>> prevCoDim){
        return prevCoDim.parallelStream()
                .flatMap(pCone -> 
                        IntStream.range(pCone.getIndexOfLastHS() + 1, halfSpaces.length)
                                .mapToObj(i-> pCone.concat(halfSpaces[i], i))
                                
                ).collect(Collectors.toMap(
                            pCone -> new ASKeyPCo(pCone), 
                            pCone -> pCone
                        )
                );
    }
    
    
    
    public Vec argMin(){
        for()
        
    }
    
}
