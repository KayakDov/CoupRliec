package Convex.thesisProjectionIdeas.GradDescentFeasibility;

import java.util.NoSuchElementException;

public class EmptyPolytopeException extends NoSuchElementException {

    public EmptyPolytopeException() {
        super("This polytope is empty.\n");
    }

}
