package RnSpace.Optimization;

import RnSpace.points.Point;
import Matricies.SymmetricMatrix;
import RnSpace.rntor.GraphX;
import RnSpace.rntor.RnToRFunc;

public class NewtonDescent extends GradDescentNoLineSearch {

    public NewtonDescent(RnToRFunc f, double end, double dt) {
        super(f, end, dt);
    }

    @Override
    public Point of(Point x) {
        SymmetricMatrix hessX = hessian.of(x).symetric();
        if (hessX.positiveDefinite()) {
            GraphX xn = new GraphX(getF(), x.minus(hessX.inverse().mult(gradX)));
            if (isGoodChoice(x, xn)) return xn;
        }
        return super.of(x, hessX);

    }
}
