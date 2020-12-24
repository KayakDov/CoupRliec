/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Matricies;

import org.ejml.data.DMatrixSparseCSC;

/**
 *
 * @author dov
 */
public class PointSparse extends MatrixSparse implements Point{

    public PointSparse(int dim) {
        super(1, dim);
    }

    @Override
    public DMatrixSparseCSC ejmlMatrix() {
        return super.ejmlMatrix(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
