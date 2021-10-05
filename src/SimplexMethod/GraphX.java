package SimplexMethod;

/**
 *
 * @author Dov Neimand
 */

import Hilbert.Vector;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;
import Hilbert.StrictlyConvexFunction;

/**
 *
 * @author Dov Neimand
 */
/**
 * launches a new thread to compute this function at the given value
 */
abstract class GraphX implements Vector<GraphX> {

    private RecursiveTask<Double> thread = new RecursiveTask<Double>() {
            @Override
            protected Double compute() {
                return f.apply(GraphX.this);
            }

        };

    private final StrictlyConvexFunction<GraphX> f;
    private double y;
    private Vector x;
    private boolean isSet;

    /**
     * computes this function at x.
     *
     * @param f
     * @param x
     */
    public GraphX(StrictlyConvexFunction f, Vector x) {
        this.x = x;
        this.isSet = false;
        this.f = f;

        thread.fork();
    }

    public GraphX(GraphX x) {
        this.x = x;
        this.f = x.f;
        this.y = x.y;
        this.isSet = x.isSet;
    }

    public GraphX recalculate(){
        thread.fork();
        return this;
    };
    

    public StrictlyConvexFunction getF() {
        return f;
    }

    public double y() {
        if (!isSet) {
            isSet = true;
            try {
                y = thread.join();//get()
            } catch (Exception ex) {
                System.err.println("thread mess:" + ex);
                System.err.println("aborted, cause:" + ex.getCause());
                System.err.println("\nx = " + GraphX.this.toString());
                System.err.println("y = " + y);
                throw new RuntimeException(ex);
            }
        }
        return y;
    }

    
}

