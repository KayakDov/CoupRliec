package RnSpace.curves;

import RnSpace.points.Point;
import Convex.Interval;

/**
 *
 * @author dov
 */
public class ArcLengthParAprox extends Curve {

    private Point[] points;
    private SegFinder sf;

    private class SegFinder {

        double[] segStarts = new double[points.length];
        double minSeg;
        int[] sendTo;
        double cellSize;

        public SegFinder() {
            minSeg = Double.MAX_VALUE;
            for (int i = 1; i < points.length - 1; i++) {
                double d = points[i - 1].d(points[i]);
                segStarts[i] = segStarts[i - 1] + d;
                if (d < minSeg) {
                    minSeg = d;
                }
            }
            segStarts[points.length - 1] = I.end();
            int target = 0;
            cellSize = minSeg / 4;
            sendTo = new int[(int) (I.end() / cellSize)];
            for (int i = 0; i < sendTo.length; i++) {
                sendTo[i] = target;
                if (i < sendTo.length - 2 && cellSize * (i + 1) > segStarts[target + 1]) {
                    sendTo[i] = -1;
                    target++;
                }
            }

        }


        int getSeg(double d) {
            int cell = (int) (d / cellSize);
            if(cell >= sendTo.length){
//                System.out.println("d = " + d);
//                System.out.println("end = " + d);
//                System.out.println("cell = " + cell);
//                System.out.println("sendTo.lenght = " + sendTo.length);
                cell--;
            }
            if(cell < 0){
//                System.out.println("d = " + d);
//                System.out.println("end = " + d);
//                System.out.println("cell = " + cell);
//                System.out.println("sendTo.lenght = " + sendTo.length);
                cell++;
            }
            if (sendTo[cell] != -1) {
                return sendTo[cell];
            } else if (d < segStarts[sendTo[cell + 1]]) {
                return sendTo[cell - 1];
            }
            return sendTo[cell + 1];
        }
        
        double segStart(int i){
            return segStarts[i];
        }

    }

    
    /**this needs to be phased out with processing**/
    public ArcLengthParAprox(Curve c, int n) {
        super(0, 0);
        double a = 0, b = 0;
        points = new Point[n];
        points[0] = c.of(c.I.start());
        
        for (int i = 1; i < n; i++) {
            points[i] = c.of(c.I.start() + i * c.I.len() / (n - 1));
            b += points[i - 1].d(points[i]);
        }
        I = new Interval(a, b);
        sf = new SegFinder();
    }

    @Override
    public Point of(double t) {
        final double EPSILON = 1E-5;
        if (t >= super.I.end() - EPSILON)  return points[points.length - 1];

        int seg = sf.getSeg(t);
        Point a = points[seg];
        Point b = points[seg + 1];
        return ((b.minus(a)).mult((t-sf.segStart(seg))/(b.d(a)))).plus(a);
        
    }

}
