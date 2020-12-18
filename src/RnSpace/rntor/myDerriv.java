
package RnSpace.rntor;

import FuncInterfaces.RnToR;

/**
 *
 * @author Dov Neimand
 */
public abstract class myDerriv extends Derrivitive{
    public myDerriv(RnToRFunc f, int i){
        super(f, i, 0);
    }

    @Override
    public abstract double of(double[] xa);
    
    public static myDerriv deriv(RnToRFunc f, RnToR d, int i){
        return new myDerriv(f, i) {
            @Override
            public double of(double[] xa) {
                return d.of(xa);
            }
        };
    }
    
}
