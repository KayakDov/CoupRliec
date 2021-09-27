package Optimization;

import Convex.HalfSpaceRn;
import Hilbert.*;

/**
 * This class implements the algorithm in the paper Hilbert-Space Convex 
 * Optimization Utilizing Parallel Reduction of Linear Inequality tom Equality 
 * Constraints (Coup Rliec) for a strictly convex function f.
 * @author Dov Neimand
 */
public class CoupRliec {
    
    /**
     * The inequality constraints.
     */
    private HalfSpaceRn[] halfspaces;
    
    /**
     * A strictly convex function from the Hilbert space to R that we seek to find the optimal point over.
     */
    private StrictlyConvexFunction f;

    /**
     * The constructor
     * @param halfspaces a set of inequality constraints.
     * @param f a striclty convex function that we seek to find the minimum for.
     */
    public CoupRliec(HalfSpaceRn[] halfspaces, StrictlyConvexFunction f) {
        this.halfspaces = halfspaces;
        this.f = f;
    }

    /**
     * changes the function we seek to optimize
     * @param f 
     */
    public void setF(StrictlyConvexFunction f) {
        this.f = f;
    }

    /**
     * Resets the inequality constraints.
     * @param halfspaces 
     */
    public void setHalfspaces(HalfSpaceRn[] halfspaces) {
        this.halfspaces = halfspaces;
    }
    
    
    
    public Vector argMin(){
        
    }
    
}
