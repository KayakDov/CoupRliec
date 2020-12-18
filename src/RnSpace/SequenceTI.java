
package RnSpace;

import RnSpace.points.Point;

/**
 * Time invariant sequence
 * @author Kayak
 */
public interface SequenceTI  extends Sequence{

    public Point iteration(Point x);
    
    @Override
    public default Point iteration(Point x, int i) {
        return iteration(x);
    }
    
    
    
}
