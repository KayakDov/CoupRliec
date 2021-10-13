package Hilbert;

import Convex.ConvexSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A polyhedron in a Hilbert Space
 * @author Dov Neimand
 * @param <Vec> The Hilbert Space
 */
public class Polyhedron<Vec extends Vector<Vec>> implements ConvexSet<Vec>{

    /**
     * The half spaces that intersect to make this polyhedron.
     */
    protected List<HalfSpace<Vec>> halfspaces;

    /**
     * The constructor
     * @param halfspaces the set of halfspaces that intersect to form this polyhedron. 
     */
    public Polyhedron(List<HalfSpace<Vec>> halfspaces) {
        this.halfspaces = halfspaces;
    }
    
    /**
     * A constructor
     * @param halfspace a half space that is to be the only halfspace in the
     * polyhedron unless more are added.
     */
    public Polyhedron(HalfSpace<Vec> halfspace) {
        this(new ArrayList<>(1));
        halfspaces.add(halfspace);
    }
    
    /**
     * A constructor
     * @param halfspaces a stream of halfspaces
     */
    public Polyhedron(Stream<HalfSpace<Vec>> halfspaces) {
        this(halfspaces.collect(Collectors.toList()));
    }
    
    /**
     * A stream of the halfspaces that intersect to form this polyhedron
     * @return 
     */
    public Stream<HalfSpace<Vec>> stream(){
        return halfspaces.stream();
    }
    
    @Override
    public boolean hasElement(Vec x) {
        return stream().allMatch(hs -> hs.hasElement(x));
    }

    @Override
    public boolean hasElement(Vec x, double tolerance) {
        return stream().allMatch(hs -> hs.hasElement(x, tolerance));
    }

    @Override
    public Vec proj(Vec x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Is this polyhedron the entire Hilbert space?
     * @return true if this is the Hilbert space, false if it is a strict subset
     * of the Hilbert space.
     */
    public boolean isAllSpace(){
        return halfspaces.isEmpty();
    }

    /**
     * The number of halfspaces that intersect to form this polyhedron.
     * @return 
     */
    public int numHalfSpaces(){
        return halfspaces.size();
    }
    
    /**
     * The halfspace with index i
     * @param i the index of the desired halfsapce
     * @return 
     */
    public HalfSpace<Vec> getHS(int i){
        return halfspaces.get(i);
    }

    /**
     * The halfspaces that intersect to make the polyhedron.
     * @return 
     */
    public List<HalfSpace<Vec>> getHalfspaces() {
        return halfspaces;
    }
    
    /**
     * The haschode of this polyhedron.  It is computed the same way as it is
     * for affine spaces.
     */
    private int hashcode;
    /**
     * Is the hashcode set?
     */
    private boolean hashcodeIsSet = false;
    /**
     * Sets the hashcode the same way it is for an affine space.
     */
    private void setHashCode(){
        hashcode = stream().mapToInt(hs -> hs.boundary().hashCode()).sum();
        hashcodeIsSet = true;
    }
    @Override
    public int hashCode() {
        if(!hashcodeIsSet) setHashCode();
        return hashcode;
    }

    @Override
    public String toString() {
        return halfspaces.toString();
    }
    
    
}
