package RnSpace.curves;

import RnSpace.points.Point;

/**
 *
 * @author Dov
 */
public abstract class FstOrdPDE extends JoinedLines{

    protected Point p0;


    public FstOrdPDE(Point p0, double a, double b, double maxStepErr, double maxStep){ 
        super(a, b);
        this.p0 = p0; this.MAX_ERR = maxStepErr;
        MAX_STEP = maxStep;
        try{
            setLines();
        }catch(NullPointerException e){}
    }
    public FstOrdPDE(Point p0, double a, double b){
        this(p0, a, b, 1E-10, (b - a)*1E-4);
    }


    private Point nextPoint(Point x, double t, double d, int numSteps){
        Point p = new Point(x);
        double h = d / numSteps;
        for(int i = 0; i < numSteps; i++)
            p = nextPoint(p, t + i * h, h);
        return p;
    }
    private final double MAX_ERR;
    private final double MAX_STEP;
    final protected void setLines(){
        
        double h = MAX_STEP;
        double err, h1;

        Point p = new Point(p0);
        //System.out.println("FstOrdeODE::setLines " + p0);

        for(double t = I.start(); t < I.end(); t = t + h1){

            Point p2;
            
            int count = 0;
                        
            do{
                p2 = nextPoint(p, t, h, 2);

                err = nextPoint(p, t, h).d(p2);
                //System.out.println("err = " + err);

                h1 = h;
                h = Math.min(h * Math.pow(MAX_ERR / err, .2), MAX_STEP);
                
                count++;
                if(count > 15){
                    //System.err.println("Cant get error small enough");
                    h = h/2;
                    count = 10;
                }
                if(h < 1E-16){
                    //System.out.println("FstOrdePDE::setLines h is to small");
                    //b = t;  I took this line out.  Maybe it needs to be put back.
                    return;
                }
            }while(err > MAX_ERR) ;

            //System.out.println("h = " + h);
            if(!p2.isReal()) break;
            lines.add( new Line(p, p2, t, t+h1));
            
            
            
            if(!p2.isReal()) break;

            p = p2;
        }

    }


    /**
     * x'(t) = f(t,x)
     * @param t
     * @param x
     * @return
     */
    public abstract Point f(double t, Point x);


   private Point nextPoint(final Point x, final double t, final double h){
       
        Point[] k = new Point[4];
        k[0] = f(t, x).mult(h);
        k[1] = f(t + h/2, x.plus(k[0].mult(1.0/2))).mult(h);
        k[2] = f(t + h/2, x.plus(k[1].mult(1.0/2))).mult(h);
        k[3] = f(t + h, x.plus(k[2])).mult(h);
        
        return x .plus
          ((k[0] .plus ((k[1] .plus (k[2])).mult(2)) .plus (k[3])).mult(1.0/6));
        }
   


}


