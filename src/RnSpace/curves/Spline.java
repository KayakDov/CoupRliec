package RnSpace.curves;

import Matricies.Matrix;
import RnSpace.points.Point;

/**
 * http://mathworld.wolfram.com/CubicSpline.html
 *
 * @author dov
 */
public class Spline extends Curve {

    private Matrix D, Y;
    final private Point[] y;

    void pivotDown(Matrix M, int i) {
        if (i == y.length - 1)
            throw new ArithmeticException("nothing to do on the bottom row");

        double mult = 1 / M.get(i + 1, i + 1);
        M.set(i, i + 1, 0);
        
        M.set(i, M.get(i) - mult *M.get(i+1));
        Y.setRow(i, Y.row(i).minus(Y.row(i + 1).mult(mult)));
    }

    void pivotUp(Matrix M, int i) {

        if (i == 0)
            throw new ArithmeticException("nothing to do on the bottom row");

        double mult = 1 / M.get(i - 1, i - 1);
        M.set(i, i - 1, 0);
        M.set(i, i, M.get(i, i) - mult * M.get(i - 1, i));
        Y.setRow(i, Y.row(i).minus(Y.row(i - 1).mult(mult)));

    }

    public Spline(Point[] points) {
        super(0, 1);
        this.y = points;
        buildY();
        Matrix M = buildM();
        for (int i = 1; i < points.length; i++) pivotUp(M, i);
        for (int i = 0; i < points.length - 1; i++) pivotDown(M, i);
//        D = M.solve(Y);
        D = new Matrix(points.length, points[0].dim());
        for (int i = 0; i < points.length; i++)
            D.setRow(i, Y.row(i).mult(1 / M.get(i, i)));

    }

    public Spline(Curve c, int n) {
        this(c.getPoints(n));
    }

    private final void buildY() {
        int n = y.length;
        Y = new Matrix(n, y[0].dim());

        Point Y0 = (y[1].minus(y[0]).mult(3));
        for (int i = 0; i < Y.cols; i++)
            Y.set(0, i, Y0.get(i));

        for (int row = 1; row < n - 1; row++) {
            Point Yi = (y[row + 1].minus(y[row - 1]).mult(3));
            for (int col = 0; col < Y.cols; col++)
                Y.set(row, col, Yi.get(col));
        }

        Point Yn = (y[n - 1].minus(y[n - 2]).mult(3));
        for (int i = 0; i < Y.cols; i++)
            Y.set(n - 1, i, Yn.get(i));
    }

    private final Matrix buildM() {
        int n = y.length;
        Matrix M = new Matrix(n);
        M.set(0, 0, 2);
        M.set(0, 1, 1);
        for (int i = 1; i < n - 1; i++) {
            M.set(i, i, 4);
            M.set(i, i - 1, 1);
            M.set(i, i + 1, 1);
        }
        M.set(n - 1, n - 2, 1);
        M.set(n - 1, n - 1, 2);
        return M;
    }

    private Point of(double t, int i) {
        Point a = y[i];
        Point b = D.row(i),
                c = (y[i + 1].minus(y[i]).mult(3)).minus(b.mult(2)).minus(D.row(i + 1)),
                d = (y[i].minus(y[i + 1]).mult(2)).plus(b).plus(D.row(i + 1));
        return a.plus((b.plus((c.plus(d.mult(t))).mult(t))).mult(t));
    }

    @Override
    public Point of(double t) {
        if (t == 1) return y[y.length - 1];
        int seg = (int) (t * (y.length - 1));
        double t2 = (t * (y.length - 1)) - seg;

        return of(t2, seg);

    }

}
