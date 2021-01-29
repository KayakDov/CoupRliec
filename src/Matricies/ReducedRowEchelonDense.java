package Matricies;

import java.util.HashSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ejml.dense.row.CommonOps_DDRM;



/**
 * TODO:  Remove the need for this code and use ejml instead.
 * the older version of this code
 * @author Dov Neimand
 */
public class ReducedRowEchelonDense extends MatrixDense implements ReducedRowEchelon{

    /**
     * Converts this matrix to reduced row echelon form.Note, if the det() != 0,
     * than this will return the identity matrix.
     *
     * @param m the matrix to be reduced
     * @param epsilon a small number
     * @return
     */
    public ReducedRowEchelonDense(Matrix m) {
        
        super(CommonOps_DDRM.rref(m.asDense(), -1, null));
        
        this.freeVariables = new HashSet<>(numCols);
        
        setFreeVariables();
        
    }
    
    public ReducedRowEchelonDense(MatrixSparse m) {
        this(m.asDense());
    }
    
    private HashSet<Integer> freeVariables;

    @Override
    public HashSet<Integer> freeVariables(){
        return freeVariables;
    }
    

}
