package Matricies;

import Matricies.Matrix;
import java.util.Arrays;
import java.util.stream.IntStream;
import FuncInterfaces.ZToR;
import FuncInterfaces.Z2ToR;
import FuncInterfaces.Z2ToBool;

/**
 *
 * @author Kayak
 */
public class SymmetricMatrix extends Matrix {

    public SymmetricMatrix(int n) {
        super(n, n);
    }

    /**
     * It is on the user to check that the matrix passed here is symmetric.
     *
     * @param matrix
     */
    private SymmetricMatrix(Matrix matrix) {
        super(matrix);
    }

    /**
     * is the matrix passed in symmetric?
     *
     * @param matrix the matrix to be checked
     * @return true if it's symmetric, false otherwise.
     */
    public static boolean isSymmetric(Matrix matrix) {
        return IntStream.range(0, matrix.rows).parallel().allMatch(i
                -> IntStream.range(0, i).allMatch(j
                        -> matrix.get(i, j) == matrix.get(j, i)
                )
        );
    }

    @Override
    public SymmetricMatrix set(int i, int j, double d) {
        super.set(j, i, d);
        if (i != j) super.set(i, j, d);
        return this;
    }

    @Override
    public SymmetricMatrix setAll(Z2ToR f) {
        
        setSome((i, j) -> i <= j, (i, j) -> f.of(i, j));
        setSome((i, j) -> i > j, (i, j) -> get(j, i));
        return this;
    }

    @Override
    protected SymmetricMatrix setAll(ZToR f) {
        return setAll((i, j) -> f.of(cols * i + j));
    }

    /**
     * Is this matrix positive definite.
     *
     * @return true if positive definite, false otherwise.
     */
    public boolean positiveDefinite() {
        return IntStream.rangeClosed(1, rows).parallel().allMatch(i
                -> topLeftSubMatrix(i).det() > 0
        );
    }

    @Override
    public SymmetricMatrix inverse() {
        return new SymmetricMatrix(super.inverse());
    }

    public SymmetricMatrix(double[] array, int n) {
        super(array, n, n);
    }

}
