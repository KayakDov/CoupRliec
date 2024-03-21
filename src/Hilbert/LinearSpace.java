package Hilbert;

import java.util.Arrays;

/**
 * The class represents a linear subspace of a Hilbert Space, where this subspace
 * has a finite codimension.
 * @author Dov Neimand
 * @param <Vec> the elements of the Hilbert Space
 */
public class LinearSpace<Vec extends Vector<Vec>> {
    /**
     * The set of vectors normal to the planes that intersect to form this space.
     */
    protected Vec[] normals;

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final LinearSpace other = (LinearSpace) obj;
        if (!Arrays.deepEquals(this.normals, other.normals))
            return false;
        return true;
    }

    /**
     * A list of vectors that are normal to the linear space.
     * @return 
     */
    public Vec[] normals() {
        return normals;
    }

    /**
     * The constructor
     * @param normalVectors the rows of the matrix for which this is the null space.
     */
    public LinearSpace(Vec[] normalVectors) {
        this.normals = normalVectors;
    }

    /**
     * Is this space the Hilbert space.
     * @return 
     */
    public boolean isAllSpace(){
        return normals == null || normals.length == 0;
    }
    
    /**
     * A tolerance used to avoid numerical errors.
     */
    public double tolerance = 1e-8;
    
    private LinearSpace(){
        
    }
    
    public static <T extends Vector<T>> LinearSpace<T> allSpace(){
        return new LinearSpace<T>();
    }

    @Override
    public String toString() {
        if(normals.length == 0) return "The entire Hilbert Space";
        return Arrays.toString(normals);
    }
    
    
}
