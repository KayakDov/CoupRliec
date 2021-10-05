package Hilbert;

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
    
    
    
}
