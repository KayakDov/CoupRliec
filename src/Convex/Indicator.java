/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex;

import RnSpace.points.Point;
import java.util.function.Predicate;

/**
 *
 * @author Kayak
 */
public interface Indicator extends Predicate<Point>{
    public boolean isMember(Point x);

    @Override
    public default boolean test(Point t) {
        return isMember(t);
    }
    
}
