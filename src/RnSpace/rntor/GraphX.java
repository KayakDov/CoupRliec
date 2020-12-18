package RnSpace.rntor;

import FuncInterfaces.RToR;
import FuncInterfaces.RnToR;
import FuncInterfaces.ZToR;
import RnSpace.points.Point;
import java.util.concurrent.RecursiveTask;

/**
 *
 * @author Dov Neimand
 */
/**
 * launches a new thread to compute this function at the given value
 */
public class GraphX extends Point {

    private RecursiveTask<Double> thread = new RecursiveTask<Double>() {
            @Override
            protected Double compute() {
                return f.of(array);
            }

        };

    private final RnToR f;
    private double y;
    private boolean isSet;

    /**
     * computes this function at x.
     *
     * @param f
     * @param x
     */
    public GraphX(RnToR f, Point x) {
        super(x);

        this.isSet = false;
        this.f = f;

        thread.fork();
    }

    public GraphX(GraphX x) {
        super(x);
        this.f = x.f;
        this.y = x.y;
        this.isSet = x.isSet;
    }

    

    public RnToR getF() {
        return f;
    }

    @Override
    public GraphX set(int i, double y) {
        isSet = false;
        super.set(i, y);
        thread.cancel(true);
        thread.fork();
        return this;
    }

    @Override
    public GraphX setAll(ZToR f) {
        isSet = false;
        super.setAll(f);
        thread.cancel(true);
        thread.fork();
        return this;
    }

    public double y() {
        if (!isSet) {
            isSet = true;
            try {
                y = thread.get();//join();
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
