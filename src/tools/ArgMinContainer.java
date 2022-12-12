
package tools;

import Hilbert.Vector;


/**
 * This class is a pair, the argmin and weather or not the
 * sufficient criteria are met.
 *
 * @author Dov Neimand
 * @param <Vec>
 */
public class ArgMinContainer<Vec extends Vector<Vec>> {
    
    public final Vec argMin;
    public final boolean isPolyhedralMin;

    /**
     * The constructor for the pair
     *
     * @param argMin the argmin over this ACone
     * @param isPolyhedralMin weather or not this ACone meets the
     * necessary criteria
     */
    public ArgMinContainer(Vec argMin, boolean isPolyhedralMin) {    
        this.argMin = argMin;
        this.isPolyhedralMin = isPolyhedralMin;
    }

    /**
     * The argmin over this ACone
     *
     * @return
     */
    public Vec argMin() {
        return argMin;
    }


}
