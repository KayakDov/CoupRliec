package Hilbert.Optimization;

import Convex.ASKeys.ASKey;
import Convex.ASKeys.ASKeyPCo;
import Hilbert.GeneratingPCone;
import Hilbert.HalfSpace;
import Hilbert.StrictlyConvexFunction;
import Matricies.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import tools.Combinatorics;

/**
 *
 * @author Dov Neimand
 */
public class CoupRliecPointMethod extends CoupRliec<Point> {

    protected final ArrayList<ConcurrentHashMap<ASKey, GeneratingPCone>> memoization;

    /**
     * The constructor
     *
     * @param f the function to optimize
     * @param halfSpaces the half spaces optimized over
     */
    public CoupRliecPointMethod(StrictlyConvexFunction<Point> f, List<HalfSpace<Point>> halfSpaces) {
        super(f, halfSpaces);
        int n = numSeqentialIterations();
        memoization = new ArrayList<>(n + 1);

        for (int i = 0; i < n + 1; i++)
            memoization.add(new ConcurrentHashMap<>(Combinatorics.choose(halfSpaces.size(), i)));
        
        GeneratingPCone allSpace = GeneratingPCone.allSpace(f, memoization, poly);
        allSpace.min();
        memoization.get(0).put(new ASKeyPCo(allSpace), allSpace);
    }

    @Override
    public Point argMin() {

        if (dim() < poly.numHalfSpaces()) {
            GeneratingPCone[] pCones
                    = Combinatorics.choose(poly.getHalfspaces(), dim())
                            .map(hsList -> new GeneratingPCone(f, hsList, memoization, poly))
                            .filter(p -> poly.hasElement(p.point()))
                            .sorted(Comparator.comparingDouble(cone -> f.apply(cone.point())))
                            .toArray(GeneratingPCone[]::new);

            for (GeneratingPCone gpc : pCones) {
                if (gpc.getMeetsSufficient())
                    return gpc.getSavedArgMin().argMin();
            }

            System.out.println("Hilbert.Optimization.CoupRliecPointMethod.argMin()");
            System.out.println("Something went wrong.");
        }

        return new CoupRliecOrderedHalfSpaces<>(f, poly.getHalfspaces()).argMin();

    }

}
