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
     * A name for this halfspace
     */
    protected String name;

    /**
     * Sets the name for this halfspace
     * @param name
     * @return 
     */
    public HalfSpace<Vec> setName(String name) {
        this.name = name;
        return this;
    }
    
    
    /**
     * The constructor
     * @param normal a vector normal to the plane
     * @param b normal dot x = b
     */
    public HalfSpace(Vec normal, double b){
        boundry = new Plane(normal, b);
    }
    
    /**
     * The constructor
     * @param boundary 
     */
    public HalfSpace(Plane<Vec> boundary){
        this.boundry = boundary;
    }

    /**
     * The constructor
     * @param onPlane a point on the plane
     * @param normal a vector normal to the plane
     */
    public HalfSpace(Vec normal, Vec onPlane) {
        this.boundry = new Plane<>(normal, onPlane);
    }
    
    public boolean hasElement(Vec x){
        return boundry.above(x) || boundry.hasElement(x);
    }
    
    /**
     * Is the given point x in the interior of this half space.
     * @param x
     * @return 
     */
    public boolean interiorHasElement(Vec x){
        return boundry.above(x);
    }
    
    /**
     * Is the given point x in the interior of this half space.
     * @param x
     * @return 
     */
    public boolean interiorHasElement(Vec x, double epsilon){
        return !boundry.hasElement(x, epsilon) && boundry.above(x);
    }
    
    /**
     * Is the proffered element within an epsilon distance of this half space.
     * @param x
     * @param epsilon
     * @return 
     */
    public boolean hasElement(Vec x, double epsilon){
        return boundry.above(x) || boundry.hasElement(x, epsilon);
    }
    
    
    /**
     * the complement space
     * @return 
     */
    public HalfSpace complement(){
        return new HalfSpace(boundry.flipNormal());
    }
    
   /**
    * a vector normal to the boundary of this half space
    * @return 
    */
    public Vec normal(){
        return boundry.normal();
    }
    
    /**
     * is the point epsilon-near the boundary of this half space
     * @param x
     * @param epsilon
     * @return 
     */
    public boolean onSurface(Vec x, double epsilon){
        return boundry.hasElement(x, epsilon);
    }
        
   
    /**
     * the plane that makes up the boundary of this half space
     * @return a  the plane that makes up the surface of this halfspace.
     * This is the actual plane, so mess with it at your peril.
     */
    public Plane boundary(){
        return boundry;
    }

    @Override
    public String toString() {
        return boundary().toString().replace("=", "<=") + " " + (name != null? name:"");
    }
    
    public Vec proj(Vec x) {
        if(hasElement(x) || boundry.hasElement(x)) return x;
        return boundry.proj(x);
    }
    
        /**
     * A point on the boundary of this halfspace
     * @return 
     */
    public Vec surfacePoint(){
        return boundry.p();
    }
    
    /**
     * The number of dimensions the halfspace lives in.
     * @return 
     */
    public int dim(){
        return normal().dim();
    }
    
    
}
