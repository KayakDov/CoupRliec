package Matricies;

import java.util.HashSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ejml.dense.row.CommonOps_DDRM;



/**
 * the older version of this code
 * @author Dov Neimand
 */
public class ReducedRowEchelon extends Matrix {

    /**
     * Converts this matrix to reduced row echelon form.Note, if the det() != 0,
     * than this will return the identity matrix.
     *
     * @param m the matrix to be reduced
     */
    public ReducedRowEchelon(Matrix m) {
        
        super(CommonOps_DDRM.rref(m, -1, null));
        
        this.freeVariables = new HashSet<>(numCols);
        
        setFreeVariables();
        
    }
        
    /**
     * The free variables of this reduced row echelon form.
     */
    private final HashSet<Integer> freeVariables;

    /**
     * Gets the column indices of the free variables.
     * @return 
     */
    public HashSet<Integer> freeVariables(){
        return freeVariables;
    }
    
    /**
     * Sets the free variables.
     */
    private void setFreeVariables(){
        for(int i = 0, j = 0; j < cols(); j++)
            if(i < rows() && get(i, j) != 0) i++;
            else freeVariables().add(j);
    }
    
    /**
     * 
     * @return true if there are nor free variables, false otherwise.
     */
    public  boolean noFreeVariable() {
        return freeVariables().isEmpty();
    }

    /**
     * The free variable indices.
     * @return 
     */
    public  Stream<Integer> getFreeVariables() {
        return freeVariables().stream();
    }

    /**
     * The basic variable indices.
     * @return 
     */
    public  Stream<Integer> getBasicVariables() {
        return IntStream.range(0, cols()).filter(i -> !freeVariables().contains(i)).mapToObj(i -> i);
    }

    /**
     * The number of free variables.
     * @return 
     */
    public  int numFreeVariables() {
        return freeVariables().size();
    }

    @Override
    public long rank() {
        return cols() - freeVariables().size();
    }
}
