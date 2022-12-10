package Hilbert;

import java.util.Arrays;
import java.util.stream.Stream;

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

    public boolean isAllSpace(){
        return normals == null || normals.length == 0;
    }
    
    public double tolerance = 1e-8;
    
    public boolean hasElement(Vec x) {
        return hasElement(x, tolerance);
    }
    
    public boolean hasElement(Vec x, double tolerance) {
        if (normals.length == 0) return true;
        return Arrays.stream(normals).allMatch(normal -> normal.ip(x) < tolerance);
    }
    
    public Stream<Vec> stream(){
        return Arrays.stream(normals);
    }
    
    /**
     * returns a linear space that is the intersection of this space and another.
     * @param ls
     * @return 
     */
    public LinearSpace<Vec> intersection(LinearSpace<Vec> ls){
        Vec[] intersection = Arrays.copyOf(normals, normals.length + ls.normals.length);
        System.arraycopy(ls.normals, 0, intersection, normals.length, ls.normals.length);
        return new LinearSpace<>(intersection);
    }
    
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
