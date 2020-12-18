package RnSpace.rntor;

import FuncInterfaces.RnToR;
import Matricies.Matrix;
import RnSpace.points.Point;
import Matricies.SymmetricMatrix;
import java.util.stream.IntStream;
import FuncInterfaces.RnToRpxq;

/**
 *
 * @author Kayak
 */
public class Hessian implements RnToRpxq {


    private double dt;
    private RnToR f;

    /**
     * 
     * @param f the function for which we want the hessian function
     * @param dt a small number
     */
    public Hessian(RnToR f, double dt) {
        this.dt = dt;
        this.f = f;
    }
    
    @Override
    public SymmetricMatrix of(double[] x) {
           return new SymmetricMatrix(x.length).setAll((i, j) -> f.d(i, dt).d(j, dt).of(x));
    }

}
