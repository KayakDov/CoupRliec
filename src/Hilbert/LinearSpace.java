package Hilbert;

import Convex.GradDescentFeasibility.Proj.ASNode;
import Matricies.Point;
import java.util.Arrays;

/**
 * The class represents a linear subspace of a Hilbert Space, where this subspace
 * has a finite codimension.
 * @author Dov Neimand
 * @param <Vec> the elements of the Hilbert Space
 */
public class LinearSpace<Vec extends Vector<Vec>> implements Convex.ConvexSet<Vec>{
    Vec[] nomralVectors;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final LinearSpace other = (LinearSpace) obj;
        if (!Arrays.deepEquals(this.nomralVectors, other.nomralVectors))
            return false;
        return true;
    }

    /**
     * A list of vectors that are normal to the linear space.
     * @return 
     */
    public Vec[] getNomrals() {
        return nomralVectors;
    }

    /**
     * The constructor
     * @param nomralVectors the rows of the matrix for which this is the null space.
     */
    public LinearSpace(Vec[] nomralVectors) {
        this.nomralVectors = nomralVectors;
    }

    public boolean isAllSpace(){
        return nomralVectors.length == 0;
    }
    
    public double tolerance = 1e-8;
    
    @Override
    public boolean hasElement(Vec x) {
        return hasElement(x, tolerance);
    }

    @Override
    public boolean hasElement(Vec x, double tolerance) {
        if (nomralVectors.length == 0) return true;
        return Arrays.stream(nomralVectors).allMatch(normal -> normal.ip(x) < tolerance);
    }

    @Override
    public Vec proj(Vec x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
