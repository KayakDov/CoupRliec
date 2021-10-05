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

    public List<HalfSpace<Vec>> halfspaces;

    public Polyhedron(List<HalfSpace<Vec>> halfspaces) {
        this.halfspaces = halfspaces;
    }
    
    public Polyhedron(Stream<HalfSpace<Vec>> halfspaces) {
        this(halfspaces.collect(Collectors.toList()));
    }
    
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
     * The half space of index i
     * @param i
     * @return 
     */
    public HalfSpace<Vec> getHS(int i){
        return halfspaces.get(i);
    }

}
