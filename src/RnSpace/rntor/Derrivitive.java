package RnSpace.rntor;

import FuncInterfaces.RnToR;
import Matricies.Matrix;
import RnSpace.points.Point;

/**
 *
 * @author Kayak
 */
public class Derrivitive implements RnToR {

    private int i;
    private double dxi;
    private RnToR f;

    /**
     * The derivative of the function: df/dx_i.Uses multi threading so that the
     * of(x) function is called four times in parallel.
     *
     * @param f the function to take the derrivitive of.
     * @param i the index of the variable that's being differentiated.
     * @param dxi the step size.
     */
    public Derrivitive(RnToR f, int i, double dxi) {
        this.i = i;
        this.dxi = dxi;
        this.f = f;
    }

    @Override
    public double of(double[] xa) {
        return D(xa)/dxi;
    }
    
    /**
     * The change in rise in each direction.
     * @param xa
     * @return 
     */
    public double D(double xa[]){
        Point x = new Point(xa);

        Point y = f.of(new Matrix(4, x.dim())
                .setRow(0, x.shift(i, -2 * dxi))
                .setRow(1, x.shift(i, -dxi))
                .setRow(2, x.shift(i, dxi))
                .setRow(3, x.shift(i, 2 * dxi)));
        
        return (y.get(0) - 8 * y.get(1) + 8 * y.get(2) - y.get(3)) / 12;

    }

    public int getDimIndex() {
        return i;
    }
    
    

}
