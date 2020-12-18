package FuncInterfaces;

import RnSpace.points.Point;
import java.util.function.Function;
public interface RnToRm extends Function<Point, Point>{

    public Point of(double[] x);
    
    public default Point of(Point x){
        return of(x.array());
    }

    @Override
    public default Point apply(Point t) {
        return of(t);
    }

    public default <V> Function<V, Point> of(Function<? super V, ? extends Point> before){
        return x -> of(before.apply(x));
    }
     public interface RnToRmPoint extends RnToRm{

        @Override
        public Point of(Point x);
        public default Point of(double[] x){
            return of(new Point(x));
        }
         
     }    
}
