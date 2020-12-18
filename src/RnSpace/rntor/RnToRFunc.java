package RnSpace.rntor;

import RnSpace.Optimization.Min;
import Convex.Cube;
import RnSpace.points.Point;
import FuncInterfaces.RnToR;

public abstract class RnToRFunc implements RnToR {

    public String name = "";
    private Cube domain = null;

    /**
     * Sets the domain of the function. The domain must be a cuboid.
     *
     * @param c the setDomain
     * @return this function
     */
    public RnToRFunc setDomain(Cube c) {
        if (c == null) {
            domain = null;
            return this;
        }
        if (domain == null) domain = new Cube(c);
        else throw new RuntimeException("This function already has a domain.");
        if (domain.dim() != n)
            throw new RuntimeException("domain has wrong number of dimesnions. Has " + domain.dim() + " but wants " + n);
        return this;
    }

    /**
     * gets a copy of the setDomain of the function
     *
     * @return
     */
    public Cube getDom() {
        if (domain == null)
            throw new NullPointerException("The domain is not yet defined on this function.");
        return new Cube(domain);
    }

    private final int n;

    /**
     * the number of dimensions in the setDomain
     *
     * @return
     */
    public int getN() {
        return n;
    }

    public static RnToRFunc st(RnToR f, int n) {
        return st(f, n, null, "");
    }

    /**
     * Creates an Rn to R function from an interface / lambda notation
     *
     * @param f the interface i.e. x -> x[0]*x[1] for f(x,y) = x*y
     * @param n the number of dimensions in the setDomain
     * @param domain the domaino of the function
     * @param name the name of the function
     * @return a function from rn to r
     */
    public static RnToRFunc st(RnToR f, int n, Cube domain, String name) {
        return new RnToRFunc(n) {
            @Override
            public double of(double[] x) {
                return f.of(x);
            }
        }.setDomain(domain).setName(name);
    }

    public RnToRFunc setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * creates a new function
     *
     * @param f
     * @param domain a cuboid
     * @return
     */
    public static RnToRFunc st(RnToR f, Cube domain) {
        return new RnToRFunc(domain.dim()) {
            @Override
            public double of(double[] x) {
                return f.of(x);
            }
        }.setDomain(domain);
    }

    /**
     * Creates an Rn to R function from an interface / lambda notation
     *
     * @param f the interface i.e. x -> x[0]*x[1] for f(x,y) = x*y
     * @param d the derivitves of the function
     * @param n the number of dimensions in the setDomain
     * @return a function from rn to r
     */
    public static RnToRFunc st(RnToR f, RnToR d, int n) {
        
        class Deriv extends Derrivitive{
            public Deriv(int i){
                super(null, i, 0);
            }

            @Override
            public double of(double[] xa) {
                return d.of(xa);
            }
            
        }
        
        return new RnToRFunc(n) {
            @Override
            public double of(double[] x) {
                return f.of(x);
            }

            ;
            @Override
            public Derrivitive d(int i, double dt) {
                return new Deriv(i);
            }
        };
    }

    /**
     * creates an empty Rn to R function over n dimensional space.
     *
     * @param n
     */
    public RnToRFunc(int n) {
//        this.threads = new ConcurrentHashMap<>();
        this.n = n;
        name = "";
        domain = null;
    }

    public Point zero(Point guess, double acc) {
        return Min.gradientDescentLineSearch(RnToRFunc.st(abs(), domain), fork(guess), acc, acc);
    }
    



    /**
     * the Reiman integral of the function
     *
     * @param dx the size of the tiny steps
     * @return the integral
     */
    public double integral(double dx) {
        if (domain == null) throw new RuntimeException("Domain is undefined.");

        return integral(domain, dx);
    }


}
