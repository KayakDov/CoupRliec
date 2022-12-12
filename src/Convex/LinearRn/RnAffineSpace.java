package Convex.LinearRn;

import Hilbert.AffineSpace;
import Matricies.Matrix;
import Matricies.ReducedRowEchelon;
import Matricies.Point;

/**
 * This is an affine space of finite co-dimension, represented internally as the
 * solution Ax=b with the option of the additional representation of the linear
 * null space A and a point p in the affine space.
 *
 * @author Dov Neimand
 */
public class RnAffineSpace extends AffineSpace<Point> {

    /**
     * The matrix A with Ax = b.
     *
     * @return
     */
    public Matrix nullMatrix() {
        return Matrix.fromRows(linearSpace.normals());
    }

    /**
     * a constructor
     *
     * @param ls a linear space parallel to this affine space
     * @param onSpace a point in the affine space
     */
    public RnAffineSpace(RnLinearSpace ls, Point onSpace) {
        super(ls, ls.matrix().mult(onSpace));
        this.p = onSpace;
    }

    /**
     * Converts an affine space to an RnAffine space.
     *
     * @param as
     */
    public RnAffineSpace(AffineSpace<Point> as) {
        super(as);
    }

    /**
     * A point in the affine space. If on has not previously been found or set,
     * then it is computed.
     *
     * @return
     */
    @Override
    public Point p() {

        if (p != null) return p;        

        ReducedRowEchelon rre = new ReducedRowEchelon(nullMatrix());

        if (rre.noFreeVariable())
            return p = nullMatrix().solve(b);

        Matrix append = Matrix.fromRows(rre.getFreeVariables()
                .map(i -> new Point(rre.numCols, i, 1))
                .toArray(Point[]::new)
        );

        Point b2 = b.concat(new Point(append.rows()));

        return p = nullMatrix().rowConcat(append).solve(b2);

    }
    
    /**
     * The underlying linear space. If Ax = b the this returns Null(A).
     *
     * @return
     */
    public RnLinearSpace RnLinearSpace() {
        return new RnLinearSpace(linearSpace);
    }


    @Override
    public String toString() {
        return linearSpace + "*x = " + b;//(p != null ? "\nwith point " + p : "\nb = " + b);
    }

    /**
     * the dimension of the space containing this affine space
     *
     * @return
     */
    public int dim() {
        if (nullMatrixRows().length == 0 && p != null) return p.dim();
        return linearSpace.normals()[0].dim();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof RnAffineSpace)) return false;
        final RnAffineSpace other = (RnAffineSpace) obj;
        return other.linearSpace.equals(linearSpace) && other.b.equals(b);
    }

    /**
     * Is this affine space equal to the given affine space.
     *
     * @param obj
     * @return
     */
    public boolean equals(RnAffineSpace obj) {

        return obj.linearSpace.equals(linearSpace) && obj.b.equals(b);

    }

}
