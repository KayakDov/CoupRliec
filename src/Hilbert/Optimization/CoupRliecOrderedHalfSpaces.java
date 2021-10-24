package Hilbert.Optimization;

import Convex.ASKeys.ASKey;
import Convex.ASKeys.ASKeyPCo;
import Hilbert.HalfSpace;
import Hilbert.PCone;
import Hilbert.StrictlyConvexFunction;
import Hilbert.Vector;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import tools.Combinatorics;

/**
 * We accelerate the CoupRleic algorithm by ordering the half spaces and searching up before out.
 *
 * @author Dov Neimand
 * @param <Vec>
 */
public class CoupRliecOrderedHalfSpaces<Vec extends Vector<Vec>> extends CoupRliec<Vec> {

    protected final ArrayList<Map<ASKey, PCone<Vec>>> affSpacesCoDimI;
    

    /**
     * The constructor
     * @param f the function to optimize
     * @param halfSpaces the half spaces optimized over
     */
    public CoupRliecOrderedHalfSpaces(StrictlyConvexFunction<Vec> f, List<HalfSpace<Vec>> halfSpaces) {
        super(f, halfSpaces);
        affSpacesCoDimI = new ArrayList<>(numSeqentialIterations() + 1);
        int n = numSeqentialIterations();
        for (int i = 0; i < n + 1; i++)
            affSpacesCoDimI.add(new HashMap<>(Combinatorics.choose(halfSpaces.size(), i)));
        PCone allSpace = PCone.allSpace(f);
        allSpace.min(affSpacesCoDimI.get(0));
        affSpacesCoDimI.get(0).put(new ASKeyPCo(allSpace), allSpace);
    }

    /**
     * Generates the next outer iteration of PCones.  If the current iteration
     * is all teh PCones of codimension i, then this will return all the 
     * PCones of codimension i+1.
     * @param coDim the codimension of the current level of PCones, not the next level.
     * @param hs The half space most recently added to the list of half spaces.
     * @param superConeAddOns the list of supercones that are subets of the halfspace hs
     * @return The next tear of PCones that have hs in them.
     */
    public Map<ASKey, PCone<Vec>> nextPConeTear(int coDim, HalfSpace<Vec> hs, Map<ASKey, PCone<Vec>> superConeAddOns){
        Map<ASKey, PCone<Vec>> superCones = new ConcurrentHashMap<>(superConeAddOns.size() + affSpacesCoDimI.get(coDim).size());
        superCones.putAll(affSpacesCoDimI.get(coDim));
        superCones.putAll(superConeAddOns);
        
        return affSpacesCoDimI.get(coDim).values().parallelStream().map(pCone -> {
                PCone<Vec> concat = pCone.concat(hs);
                concat.min(superCones);
                return concat;
            }).collect(Collectors.toMap(pCone -> new ASKeyPCo(pCone), pCone -> pCone));
    }
    
    /**
     * Pass the next half space into here to examine the intersections of this
     * half space and all the ones studied so far.
     * @param hs the half space being added
     * @return the optimal point in the polyhedron if it can be found by adding
     * this half space to those already studied.
     */
    private Vec nextHalfSpace(HalfSpace<Vec> hs) {

        int n = numSeqentialIterations();
        Map<ASKey, PCone<Vec>> nextAddOnPcones = nextPConeTear(0, hs, new HashMap<>()), addOnPCones;
        
        for (int i = 1; i <= n; i++) {
            addOnPCones = nextAddOnPcones;
            PCone<Vec> minCone = addOnPCones.values().parallelStream().filter(pCone -> suffCrit(pCone.getSavedArgMin())).findAny().orElse(null);
            if(minCone != null) return minCone.getSavedArgMin().argMin();
            nextAddOnPcones = nextPConeTear(i, hs, addOnPCones);
            affSpacesCoDimI.get(i).putAll(addOnPCones);
        }
        return null;
    }

    /**
     * Sorts the half spaces by -f.min(hs.boundary())
     * @param argMinHilb 
     */
    private void sortHalfSpaces(Vec argMinHilb){
        
        poly.getHalfspaces().sort(Comparator.comparingDouble(hs -> {
            if(hs.interiorHasElement(argMinHilb)) return Double.POSITIVE_INFINITY;
            return -f.min(hs.boundary());
        }));
    }
    
    @Override
    public Vec argMin() {
        Vec argMinHilb = f.ArgMin();
        if(poly.hasElement(argMinHilb)) return argMinHilb;
        sortHalfSpaces(argMinHilb);
        for(HalfSpace<Vec> hs: poly.getHalfspaces()){
            Vec min = nextHalfSpace(hs);
            if(min != null) return min;
        }
        return null;
    }
    
}
