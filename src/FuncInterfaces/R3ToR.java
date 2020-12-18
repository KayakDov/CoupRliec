
package FuncInterfaces;

/**
 *
 * @author Dov Neimand
 */
public interface R3ToR extends RnToR{

    public double of(double x, double y, double z);

    @Override
    public default double of(double[] x){
        return of(x[0], x[1], x[2]);
    }   

}
