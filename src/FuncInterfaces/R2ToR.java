package FuncInterfaces;

/**
 *
 * @author Kayak
 */
public interface R2ToR extends RnToR{

    public double of(double x, double y);

    @Override
    public default double of(double[] x){
        return of(x[0], x[1]);
    }   

}
