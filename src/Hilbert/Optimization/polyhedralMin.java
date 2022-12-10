package Hilbert.Optimization;


import Hilbert.HalfSpace;
import Hilbert.StrictlyConvexFunction;
import Hilbert.PCone;
import Hilbert.Vector;
import java.util.Collections;
import java.util.List;

/**
 * The algorithm spelled out in Hilbert-Space Convex Optimization Utilizing
 * Parallel Reduction of Linear Inequality to Equality Constraints
 *
 * @author Dov Neimand
 * @param <Vec> the type of Hilbert space this is over
 */
public class polyhedralMin<Vec extends Vector<Vec>> {

    protected final StrictlyConvexFunction<Vec> f;
    protected final List<HalfSpace<Vec>> poly;

    public polyhedralMin(StrictlyConvexFunction<Vec> f, List<HalfSpace<Vec>> halfSpaces) {
        this.f = f;
        poly = halfSpaces;
    }

    /**
     * This gives the total number of affine spaces.
     *
     * @return
     */
    private int totallNumberOfAffineSpaces() {
        int tot = 0;
        for (int i = 0; i <= minRN(); i++)
            tot += choose(poly.size(), i);
        return tot;
    }

    /**
     * The percent of affine spaces the minimum value was computed over.
     *
     * @return
     */
    public double fracAffineSpacesChecked() {
        argMin();
        return (double) numAffineSpacesChecked / totallNumberOfAffineSpaces();
    }

    /**
     * The next set of affine spaces of the polyhedron of codimension i+1
     *
     * @param coDim the set of affine spaces of codimension i
     * @return the set of affine spaces of the polyhedron of codim i + 1
     */
    private List<PCone<Vec>> coDimPlusPlus(List<PCone<Vec>> coDim) {

        return coDim.parallelStream()
                .flatMap(pCone -> pCone.immediateSubCones()).toList();

    }

    /**
     * This is the dimension of the Hilbert space we're working in. A value of
     * Integer.maxvalue is reserved for infinite dimensions. If there are no
     * constraints in this space a value of 1 is returned.
     *
     * @return
     */
    public int dim() {
        if (poly.size() == 0)
            throw new NullPointerException("The polyhedron is empty.");
        return poly.get(0).normal().dim();
    }


    /**
     * Finds the minimum if it exists for all the affine spaces of a given
     * codimension.
     *
     *
     * @param prevLevel all the affine spaces at the codimension - 1.
     * @param level all the affine spaces at the given codimension.
     * @return null if there is no minimum over the polyhedron at this level,
     * the argmin otherwise.
     */
    private Vec posMinOnLevel(List<PCone<Vec>> level, StrictlyConvexFunction<Vec> f) {

        return level.parallelStream()
                .map(pCone -> pCone.aMin(f))
                .filter(aMin -> aMin.isPolyhedralMin)
                .map(container -> container.argMin())
                .findAny()
                .orElse(null);
    }

    /**
     * The maximum of the number of half space or the number of dimensions. This
     * is the minimum number of iterations required.
     *
     * @return
     */
    protected int minRN() {
        return Math.min(poly.size(), dim());
    }

    /**
     * This variable is meant to track the number of affine spaces checked when
     * the algorithm is fun.
     */
    private int numAffineSpacesChecked = 0;

    /**
     * Finds the arg aMin over the polyhedron.
     *
     * @return
     */
    public Vec argMin() {

        PCone<Vec> hilb = new PCone(poly);

        List<PCone<Vec>> pConeCoDimI = Collections.singletonList(hilb);

        for (int i = 0; i <= minRN(); pConeCoDimI = coDimPlusPlus(pConeCoDimI), i++) {
            Vec min = posMinOnLevel(pConeCoDimI, f);
            if (min != null) return min;

        }
        return null;
    }


    
    /**
     * n choose k function
     *
     * @param n
     * @param k
     * @return
     */
    public static int choose(int n, int k) {
        if (k == 0) return 1;
        return choose(n - 1, k - 1) * n / k;
    }

    @Override
    public String toString() {
        return argMin().toString();
    }
    
    
}
