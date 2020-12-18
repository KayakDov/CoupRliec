/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FuncInterfaces;

import RnSpace.points.Point;
import java.util.function.Function;

/**
 *
 * @author dov
 */
public interface RToRn extends Function<Double, Point>{
    
    public Point of(double t);

    @Override
    public default Point apply(Double t) {
        return of(t);
    }

    public default <V> Function<V, Point> of(Function<? super V, ? extends Double> before){
        return t -> of(before.apply(t));
    }
    
    
    

}
