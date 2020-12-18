package RnSpace.rntor.polynomial;

import RnSpace.points.Point;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 *
 * @author Dov Neimand
 */
public class Polynomial extends Laurent {

    public static StandardPolyGen SPG = new StandardPolyGen(false);

    /**
     * A copy constructor
     *
     * @param p
     */
    public Polynomial(Laurent p) {
        super(p);
//        if (p.terms.stream().anyMatch(Term::hasNegExp))
//            throw new RuntimeException("Illeagl polynomial generations.  Negitive exponenets are forbidden.");

    }

    /**
     * The constructor The total number of coeficients should be deg^n
     *
     * @param coeficients
     * @param deg the degree of the polynomial
     * @param n the number of dimensions
     */
    public Polynomial(double[] coeficients, int deg, int n) {
        this(SPG.get(deg, n));
        try {
            IntStream.range(0, coeficients.length).forEach(i -> terms.get(i).setCoeficient(coeficients[i]));
        } catch (IndexOutOfBoundsException ex) {

//            throw new RuntimeException("wrong number of coefficients passed. " + coeficients.length + " passed, " + terms.size() + " required.");
//            SPG.get(deg, n).terms.stream().forEach(term -> terms.add(new Term(term)));
//            IntStream.range(0, coeficients.length).forEach(i -> terms.get(i).setCoeficient(coeficients[i]));
        }
    }

    /**
     * A constructor
     *
     * @param coeficients
     * @param deg
     * @param n
     */
    public Polynomial(Point coeficients, int deg, int n) {
        this(coeficients.array(), deg, n);
    }

    /**
     * A standard polynomial
     *
     * @param deg the degree of the polynomial
     * @param n dimensions
     */
    public Polynomial(int deg, int n) {
        this(SPG.get(deg, n));

    }

    public static int numCoefficients(int deg, int n) {
        return SPG.get(deg, n).terms.size();
    }
}
