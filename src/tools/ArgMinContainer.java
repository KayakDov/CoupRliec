
package tools;

import Hilbert.Vector;


/**
 * This class is nothing more than a pair, the argmin and weather or not the
 * necessary criteria are met.
 *
 * @author Dov Neimand
 * @param <Vec>
 */
public class ArgMinContainer<Vec extends Vector<Vec>> extends Pair<Vec, Boolean> {

    /**
     * The constructor for the pair
     *
     * @param argMin the argmin over this ACone
     * @param meetsNecesaryCriteria weather or not this ACone meets the
     * necessary criteria
     */
    public ArgMinContainer(Vec argMin, boolean meetsNecesaryCriteria) {
        super(argMin, meetsNecesaryCriteria);
    }

    /**
     * The argmin over this ACone
     *
     * @return
     */
    public Vec argMin() {
        return l;
    }

    /**
     * Does this ACone meet the necessary criteria
     *
     * @return
     */
    public boolean meetsNecCrti() {
        return r;
    }

}
