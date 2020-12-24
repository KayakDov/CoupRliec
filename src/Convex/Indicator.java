/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex;

import Matricies.PointDense;
import java.util.function.Predicate;

/**
 *
 * @author Kayak
 */
public interface Indicator extends Predicate<PointDense>{
    public boolean isMember(PointDense x);

    @Override
    public default boolean test(PointDense t) {
        return isMember(t);
    }
    
}
