package Hilbert;

import Convex.ASKeys.ASKey;
import Convex.ASKeys.ASKeyPCo;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import listTools.ArgMinContainer;

/**
 * The algorithm spelled out in Hilbert-Space Convex Optimization Utilizing
 * Parallel Reduction of Linear Inequality to Equality Constraints
 *
 * @author Dov Neimand
 * @param <Vec> the type of Hilbert space this is over
 */
public class CoupRliec<Vec extends Vector<Vec>> {

    private final StrictlyConvexFunction<Vec> f;
    private final Polyhedron<Vec> poly;

    public CoupRliec(StrictlyConvexFunction<Vec> f, List<HalfSpace<Vec>> halfSpaces) {
        this.f = f;
        poly = new Polyhedron<>(halfSpaces);
    }

    public CoupRliec(StrictlyConvexFunction<Vec> f, Polyhedron<Vec> poly) {
        this.f = f;
        this.poly = poly;
    }

    /**
     * The next set of affine spaces of the polyhedron of codimension i+1
     *
     * @param prevCoDim the set of affine spaces of codimension i
     * @return the set of affine spaces of the polyhedron of codim i
     */
    private Map<ASKey, PCone<Vec>> coDimPlusPlus(Collection<PCone<Vec>> prevCoDim) {
        return prevCoDim.parallelStream()
                .flatMap(pCone
                        -> IntStream.range(pCone.getIndexOfLastHS() + 1, poly.numHalfSpaces())
                        .mapToObj(i -> pCone.concat(poly.getHS(i), i))
                ).collect(Collectors.toMap(
                        pCone -> new ASKeyPCo(pCone),
                        pCone -> pCone
                )
                );
    }

    /**
     * This is the dimension of the Hilbert space we're working in. A value of
     * Integer.maxvalue is reserved for infinite dimensions. If there are no
     * constraints in this space a value of 1 is returned.
     *
     * @return
     */
    public int dim() {
        if (poly.numHalfSpaces() == 0) return 1;
        return poly.getHS(0).normal().dim();
    }
    
    /**
     * Does this possible minimum meet the sufficient criteria.
     * @param posMin
     * @return 
     */
    private boolean suffCrit(ArgMinContainer<Vec> posMin){
        return posMin.meetsNecCrti() && poly.hasElement(posMin.argMin());
    }
    
    /**
     * Finds the minimum if it exists for all the affine spaces of a given codimension.
     * @param prevLevel all the affine spaces at the codimension - 1.
     * @param level all the affine spaces at the given codimension.
     * @return null if there is no minimum over the polyhedron at this level, the argmin otherwise.
     */
    private ArgMinContainer<Vec> posMinOnLevel(Map<ASKey, PCone<Vec>> prevLevel, Map<ASKey, PCone<Vec>> level){

        return level.values().parallelStream()
                            .map(pCone -> pCone.min(prevLevel))
                            .filter(aMin -> suffCrit(aMin))
                            .findAny()
                            .orElse(null);
    }

    /**
     * Finds the arg min over the polyhedron.
     * @return 
     */
    public Vec argMin() {
        
        PCone pConeHilb = PCone.allSpace(f);

        Map<ASKey, PCone<Vec>> pConeCoDimI = new HashMap<>(), pConeCoDimeIPlusOne;
        pConeCoDimI.put(new ASKeyPCo(pConeHilb), pConeHilb);

        ArgMinContainer<Vec> min = pConeHilb.min(null);
        if(suffCrit(min)) return min.argMin();
        
        int n = Math.min(poly.numHalfSpaces(), dim());

        for (int i = 0; i < n; pConeCoDimI = pConeCoDimeIPlusOne, i++) {
            pConeCoDimeIPlusOne = coDimPlusPlus(pConeCoDimI.values());
            min = posMinOnLevel(pConeCoDimI, pConeCoDimeIPlusOne);
            if(min != null) return min.argMin();            
        }
        return null;

    }

}
