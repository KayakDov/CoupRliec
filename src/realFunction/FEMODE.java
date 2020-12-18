package realFunction;
///**
// * A class that facilitates using the Finite element method for solving higher
// * order ODEs.
// * This class needs lots fo work on account of changes elsewhere in the code.  
// * @author dov
// */
public abstract class FEMODE{// extends RToRFunc {
//
//    private initCond y0[];
//
//    public class initCond {
//
//        private int der;
//        private double t0;
//        private double y0;
//
//        /**
//         * (d^der/dt^der)y(t0) = y0
//         *
//         * @param der
//         * @param t0
//         * @param y0
//         */
//        public initCond(int der, double t0, double y0) {
//            this.der = der;
//            this.t0 = t0;
//            this.y0 = y0;
//        }
//
//        /**
//         *
//         * @return the darivative of y that gives y0 at t0
//         */
//        public int getNumTags() {
//            return der;
//        }
//
//        public double t0() {
//            return t0;
//        }
//
//        public double getY0() {
//            return y0;
//        }
//
//        /**
//         *
//         * @param f f[0] should be a continuiuse function such that for all i
//         * f[i] = (d^i/dt^i)f[0]
//         * @return the function meets the initial value condition.
//         */
//        public boolean checkFunc(RToRFunc f) {
//            final double EPSILON = 1E-5;
//            return Math.abs(f.ddt(der).of(t0) - y0) < EPSILON;
//        }
//    }
//
//    /**
//     * The equation set to zero
//     *
//     * @param y y, a fucntion of t that is to be solved for
//     * @param t the variable y is a function of
//     * @return
//     */
//    abstract protected RToRFunc R(RToRFunc y, Id t);
//
//    public FEMODE(initCond[] y0, double a, double b) {
//        super(a, b);
//        this.y0 = y0;
//
//    }
//
//    /**
//     *
//     * @param y0 initial condition
//     * @param a
//     * @param b
//     */
//    public FEMODE(initCond y0, double a, double b) {
//        super(a, b);
//        this.y0 = new initCond[1];
//        this.y0[0] = y0;
//    }
//
//    /**
//     *
//     * @param y0 y(0) = y0
//     * @param a
//     * @param b
//     */
//    public FEMODE(double y0, double a, double b) {
//        super(a, b);
//        this.y0 = new initCond[1];
//        this.y0[0] = new initCond(0, 0, 1);
//
//    }
//
//    private final int DEFAULT_NUM_NODES = 100;
//    private int numNodes = DEFAULT_NUM_NODES;
//
//    public void setNumNodes(int numNodes) {
//        this.numNodes = numNodes;
//        prepOf();
//    }
//
//    public double weight(Vertex v) {
//        final int BIG_NUM = 10 * numNodes * numNodes;
//
//        RToRFunc R = R(funcSum(v), new Id());
//
//        double coeficiantWeight = 0;
//        MyPoint2d RPoints[] = R.abs().getDataPoints(numNodes);
//        for (int i = 0; i < numNodes; i++) {
//            coeficiantWeight += RPoints[i].y();//potY.times(basis(i)).simpsonCompIntegral(10*numNodes);//numNodes);
//        }
//        coeficiantWeight /= numNodes;
//
//        for (initCond ic : y0)//accounts for initial conditions
//        {
//            coeficiantWeight += BIG_NUM * Math.abs(funcSum(v).of(ic.t0) - ic.y0);
//        }
//
//        return coeficiantWeight;
//    }
//
//    /**
//     * The basis and all the relative derivatives need to be built into this
//     * function. For example, if the basis is x^i then basis(i, der) =
//     * (i!/der!)x^(i - der) for all i - der => 0. I built a derivative method
//     * into the super class, but dealing with limits, this seemed more accurate.
//     *
//     * @param i the index of the basis function
//     * @return the der derivative of the basis function.
//     */
//    abstract protected RToRFunc basis(int i);
//
//    private RToRFunc funcSum(ArrayList<Double> g) {
//        RToRFunc sum = new Const(0);
//        for (int i = 0; i < g.size(); i++) {
//            sum = (basis(i).times(new Const(g.get(i)))).plus(sum);
//        }
//        return sum;
//    }
//
//    /**
//     * a guess as to a likely coeficiant candidate to make the search go faster.
//     *
//     * @param guess
//     */
//    private FEMODE This = this;
//
//    public void setGuess(Vertex guess) {
//        this.guess = guess;
//    }
//
//    class locallVert extends Vertex {
//
//        @Override
//        public void setWeight() {
//            weight= This.weight(this);
//        }
//
//        @Override
//        public Vertex empty() {
//            return new locallVert();
//        }
//
//        @Override
//        public Double set(int index, Double element) {
//            if(index == size()){
//                add(element);
//                return element;
//            }
//            else return super.set(index, element); 
//        }
//    }
//    
//    private Vertex guess;
//
//    private Simplex prepSearch() {
//        if (guess != null) {
//            return prepSearch(guess);
//        }
//
//        Vertex guess = new locallVert();
//        for (int i = 0; i < numNodes; i++) {
//            guess.add(0.0);
//        }
//        return prepSearch(guess);
//    }
//
//    private Simplex prepSearch(Vertex guess) {
//        int n = numNodes;
//        if (guess.nDim() < n) {
//            locallVert temp = new locallVert();
//            for (int i = 0; i < n; i++) {
//                if (i < guess.size()) {
//                    temp.add(guess.get(i));
//                } else {
//                    temp.add(0.0);
//                }
//            }
//            guess = temp;
//        }
//
//        Simplex s = new Simplex();
//        for (int i = 0; i < n + 1; i++) {
//            locallVert vd = new locallVert();
//            for (int j = 0; j < n; j++) {
//                if (i == j) {
//                    vd.add(guess.get(j));
//                } else {
//                    vd.add(guess.get(j) + n * n);
//                }
//            }
//            s.add(vd);
//        }        
//        
//        return s;
//    }
//    private RToRFunc y;
//
//    private void prepOf() {
//        guess = (new NelderMead(.001)).min(prepSearch());
//        y = new JoinedLines(funcSum(guess).getDataPoints(numNodes));
//    }
//
//    @Override
//    public double of(double t) {
//        if (y == null) {
//            prepOf();
//        }
//        return y.of(t);
//    }
//
}
