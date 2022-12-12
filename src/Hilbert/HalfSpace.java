package Hilbert;

/**
 *
 * @author Dov Neimand
 * @param <Vec> The Hilbert Space
 */
public class HalfSpace<Vec extends Vector<Vec>>{
    /**
     * The surface of the half space
     */
    protected final Plane<Vec> boundry;

    /**
     * The boundary of the half space.
     * @return 
     */
    public Plane<Vec> getBoundry() {
        return boundry;
    }
    
    /**
     * The constructor
     * @param onPlane a point on the plane
     * @param normal a vector normal to the plane
     */
    public HalfSpace(Vec normal, Vec onPlane) {
        this.boundry = new Plane<>(normal, onPlane);
    }
    
    /**
     * Does this half space contain the proffered element.
     * @param x
     * @return 
     */
    public boolean hasElement(Vec x){
        return boundry.aboveOrContains(x);
    }    
    
   /**
    * a vector normal to the boundary of this half space
    * @return 
    */
    public Vec normal(){
        return boundry.normal();
    }
       
    @Override
    public String toString() {
        return boundry.toString().replace("=", "<=");
    }
}
