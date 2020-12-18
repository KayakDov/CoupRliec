package Matricies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;



/**
 * TODO:  Remove the need for this code and use ejml instead.
 * the older version of this code
 * @author Dov Neimand
 */
public class ReducedRowEchelon extends Matrix {

    /**
     * Converts this matrix to reduced row echelon form.Note, if the det() != 0,
     * than this will return the identity matrix.
     *
     * @param m the matrix to be reduced
     * @param epsilon a small number
     * @return
     */
    public ReducedRowEchelon(Matrix m) {
        
        super(CommonOps_DDRM.rref(m.ejmlDenseMatrix(), -1, null));
        
        this.freeVariables = new HashSet<>(cols);
        
        for(int i = 0, j = 0; j < cols; j++)
            if(i < rows && get(i, j) == 1) i++;
            else freeVariables.add(j);
        
        
        
        
//        freeVariables = new HashSet<>(cols);
//        int col = 0, row = 0;
//        for (; col < cols && row < rows; col++, row++) {
//
//            pivotRow(row, col);
//            while (col < cols && Math.abs(get(row, col)) <= epsilon) {
//                freeVariables.add(col);
//                col++;
//                if (col < cols) pivotRow(row, col);
//            }
//            if (col < cols)
//                for (int i = 0; i < rows; i++)
//                    if (i != row)
//                        setRow(i, row(i).minus(row(row).mult(get(i, col) / get(row, col))));
//                    else setRow(i, row(i).mult(1 / get(row, col)));
//        }
//        if (row >= rows && col < cols)
//            IntStream.range(col, cols).forEach(i -> freeVariables.add(i));

    }
    private HashSet<Integer> freeVariables;

    public boolean noFreeVariable() {
        return freeVariables.isEmpty();
    }

    public boolean isFreeVariabls(int i) {
        return freeVariables.contains(i);
    }

    public Stream<Integer> getFreeVariables() {
        return freeVariables.stream();
    }

    public Stream<Integer> getBasicVariables() {
        return IntStream.range(0, cols).filter(i -> !freeVariables.contains(i)).mapToObj(i -> i);
    }

    public int numFreeVariables() {
        return freeVariables.size();
    }

    public long rank() {
        return cols - freeVariables.size();
    }

}
