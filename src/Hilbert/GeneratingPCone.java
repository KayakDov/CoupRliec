package Hilbert;

import Convex.ASKeys.ASKey;
import Convex.ASKeys.ASKeyPConeRI;
import Matricies.MatrixDense;
import Matricies.Point;
import Matricies.PointD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import tools.ArgMinContainer;

/**
 * This method recursively checks the supercones of the given cone.
 *
 * @author Dov Neimand
 */
public class GeneratingPCone extends PCone<Point> {

    private final ArrayList<ConcurrentHashMap<ASKey, GeneratingPCone>> memoization;
    private final Polyhedron<Point> poly;
    private boolean meetsSufficient = false;

    public GeneratingPCone(StrictlyConvexFunction<Point> f, List<HalfSpace<Point>> hs, ArrayList<ConcurrentHashMap<ASKey, GeneratingPCone>> memoization, Polyhedron<Point> poly) {
        super(f, hs);
        this.memoization = memoization;
        this.poly = poly;
    }
    
    public GeneratingPCone(StrictlyConvexFunction<Point> f, Set<HalfSpace<Point>> hs, ArrayList<ConcurrentHashMap<ASKey, GeneratingPCone>> memoization, Polyhedron<Point> poly) {
        this(f, new ArrayList<>(hs), memoization, poly);
    }

    /**
     * creates a list of half spaces without the half space of index i
     *
     * @param i
     * @return
     */
    private ArrayList<HalfSpace<Point>> withoutHS(int i) {
        return new ArrayList<>(
                intStream()
                        .filter(j -> j != i)
                        .mapToObj(j -> getHS(j))
                        .collect(Collectors.toList())
        );
    }

    /**
     * If this PCone has already been solved, return the solved one. Otherwise
     * return this one and save it.
     *
     * @param check has check been solved? If so return the solution, otherwise
     * return check.
     * @return
     */
    private GeneratingPCone getPCone(int i) {
        ASKeyPConeRI key = new ASKeyPConeRI(this, i);
        GeneratingPCone pCone = memoization.get(key.coDim()).get(key);
        if (pCone != null) return pCone;
        GeneratingPCone generated = new GeneratingPCone(f, withoutHS(i), memoization, poly);
        memoization.get(key.coDim()).put(key, generated);
        return generated;
    }

    protected ArgMinContainer<Point> meetsNecesary(GeneratingPCone superCone, int i) {
        
        if(superCone.getMeetsSufficient()){
            meetsSufficient = true;
            return superCone.getSavedArgMin();
        }
        
        return super.meetsNecesary(superCone, i); 
    }

    
    
    /**
     * This function uses recursion and memomization to find the optimal point
     * over this PCone
     *
     * @return
     */
    public ArgMinContainer<Point> min() {
        if (isAllSpace())
            savedArgMin = allSpaceArgMin();

        savedArgMin = findAnyOrAffine(
                intStream()
                        .mapToObj(i -> meetsNecesary(getPCone(i), i))
        );

        if (!meetsSufficient && poly.hasElement(savedArgMin.argMin())) meetsSufficient = true;
        
        return savedArgMin;
    }

    @Override
    public ArgMinContainer<Point> getSavedArgMin() {
        if (savedArgMin != null) return savedArgMin;
        return min();
    }
    
    public boolean getMeetsSufficient(){
        if(savedArgMin == null) min();
        return meetsSufficient;
    }

    /**
     * A polhedral cone with no half spaces, this is the entire hilbert space.
     *
     * @param f
     * @param memoization
     * @param poly
     * @return
     */
    public static GeneratingPCone allSpace(StrictlyConvexFunction<Point> f, ArrayList<ConcurrentHashMap<ASKey, GeneratingPCone>> memoization, Polyhedron<Point> poly) {
        return new GeneratingPCone(f, new ArrayList<>(0), memoization, poly);
    }

    private Point point;

    /**
     * Is the apex of this cone a single point?  if so set it, get it, and save it.
     * @return the single point at the apex of this cone.
     */
    public Point point() {
        if(hasPoint()) return point;
        PointD[] rows = new PointD[getHS(0).dim()];
        Arrays.setAll(rows, i -> getHS(i).normal());
        MatrixDense A = MatrixDense.fromRows(rows);
        
        PointD b = new PointD(rows.length).setAll(i -> getHS(i).boundary().b());
        return point = A.solve(b);
    }
    public boolean hasPoint(){
        return point != null;
    }

    
}
