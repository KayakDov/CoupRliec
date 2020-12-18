package RnSpcae.RnToRmFunc;

import RnSpace.points.Point;
import FuncInterfaces.RnToRn;

/**
 *
 * @author Dov
 */
public abstract class VectorField implements RnToRn{

    

    /**
     * R^dim -> R^dim
     */
    private final int dim;

    /**
     *
     * @param dim the number of dimensions of the field
     */
    public VectorField(int dim) {
        this.dim = dim;
    }
    
    /**
     * returns the dimension of the setDomain/range
     * @return 
     */
    public int dim() {
        return dim;
    }
    
    public static VectorField st(RnToRn f, int n){
        return new VectorField(n) {
            @Override
            public Point of(double[] x) {
                return f.of(x);
            }
        };
    }
    
}
