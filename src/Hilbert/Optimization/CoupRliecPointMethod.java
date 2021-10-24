package Hilbert.Optimization;

import Convex.ASKeys.ASKey;
import Convex.ASKeys.ASKeyPCo;
import Hilbert.GeneratingPCone;
import Hilbert.HalfSpace;
import Hilbert.PCone;
import Hilbert.StrictlyConvexFunction;
import Matricies.Matrix;
import Matricies.MatrixDense;
import Matricies.Point;
import Matricies.PointD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tools.Combinatorics;

/**
 *
 * @author Dov Neimand
 */
public class CoupRliecPointMethod extends CoupRliec<Point> {

    protected final ArrayList<Map<ASKey, GeneratingPCone>> memoization;

    /**
     * The constructor
     *
     * @param f the function to optimize
     * @param halfSpaces the half spaces optimized over
     */
    public CoupRliecPointMethod(StrictlyConvexFunction<Point> f, List<HalfSpace<Point>> halfSpaces) {
        super(f, halfSpaces);
        memoization = new ArrayList<>(numSeqentialIterations() + 1);
        int n = numSeqentialIterations();
        for (int i = 0; i < n + 1; i++)
            memoization.add(new HashMap<>(Combinatorics.choose(halfSpaces.size(), i)));
        GeneratingPCone allSpace = GeneratingPCone.allSpace(f, memoization, poly);
        allSpace.min();
        memoization.get(0).put(new ASKeyPCo(allSpace), allSpace);
    }

    @Override
    public Point argMin() {
        if (dim() < poly.numHalfSpaces()) {
            List<GeneratingPCone> pCones
                    = Combinatorics.choose(new HashSet<>(poly.getHalfspaces()), dim())
                            .parallelStream()
                            .map(hsSet -> new GeneratingPCone(f, hsSet, memoization, poly))
                            .collect(Collectors.toList());

            pCones = pCones.parallelStream().filter(p -> poly.hasElement(p.point())).collect(Collectors.toList());
            pCones.sort(Comparator.comparingDouble(cone -> f.apply(cone.point())));

            for (GeneratingPCone gpc : pCones) {
                if (gpc.getMeetsSufficient())
                    return gpc.getSavedArgMin().argMin();
            }
        }
        return new CoupRliecOrderedHalfSpaces<>(f, poly.getHalfspaces()).argMin();

    }

}
