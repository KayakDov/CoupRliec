package Convex.GradDescentFeasibility;

import java.util.NoSuchElementException;

/**
 * An exception to be thrown when the system upstream needs to be notified that
 * a polytope has been found to be empty.
 * @author dov
 */
public class EmptyPolytopeException extends NoSuchElementException {

    public EmptyPolytopeException() {
        super("This polytope is empty.\n");
    }

}
