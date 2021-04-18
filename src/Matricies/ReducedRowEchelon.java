/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Matricies;

import java.util.HashSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author dov
 */
public interface ReducedRowEchelon extends Matrix {

    
    public HashSet<Integer> freeVariables();

    
    default void setFreeVariables(){
        for(int i = 0, j = 0; j < cols(); j++)
            if(i < rows() && get(i, j) != 0) i++;
            else freeVariables().add(j);
    }
    
    public default boolean noFreeVariable() {
        return freeVariables().isEmpty();
    }

    public default boolean isFreeVariabls(int i) {
        return freeVariables().contains(i);
    }

    public default Stream<Integer> getFreeVariables() {
        return freeVariables().stream();
    }

    public default Stream<Integer> getBasicVariables() {
        return IntStream.range(0, cols()).filter(i -> !freeVariables().contains(i)).mapToObj(i -> i);
    }

    public default int numFreeVariables() {
        return freeVariables().size();
    }

    public default long rank() {
        return cols() - freeVariables().size();
    }
    
    public default boolean hasFullRank(){
        return rows() - numFreeVariables() == cols();
    }

}
