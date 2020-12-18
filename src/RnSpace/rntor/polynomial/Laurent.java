package RnSpace.rntor.polynomial;

import DiscreteMath.ZToRFunc;
import Convex.Interval;
import RnSpace.points.Point;
import RnSpace.rntor.Derrivitive;
import RnSpace.rntor.RnToRFunc;
import graph.JChart;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 *
 * @author Dov Neimand
 */
public class Laurent extends RnToRFunc {

    protected ArrayList<Term> terms;
    /**
     * the terms in the polynomial
     */
    protected static final StandardPolyGen SPG = new StandardPolyGen(true);

    /**
     * A copy constructor
     *
     * @param p
     */
    public Laurent(Laurent p) {
        super(p.getN());
        this.terms = new ArrayList<>(p.terms.size());
        p.terms.forEach(term -> terms.add(new Term(term)));
    }

    /**
     * The constructor The total number of coeficients should be deg^n
     *
     * @param coeficients
     * @param deg the degree of the polynomial
     * @param n the number of dimensions
     */
    public Laurent(Point coeficients, int deg, int n) {
        this(coeficients.array(), deg, n);
    }

    /**
     * constructor
     *
     * @param coeficients
     * @param deg the absolute value of the degree of the polynomial
     */
    public Laurent(double[] coeficients, int deg, int n) {
        this(SPG.get(deg, n));

        IntStream.range(0, Math.min(coeficients.length, terms.size()))
                .forEach(i -> terms.get(i).setCoeficient(coeficients[i]));

//        System.out.println("RnSpace.rntor.polynomial.Laurent.<init>()\n" + toString());
    }

    /**
     * Constructor A polynomial with all coeficients 1
     *
     * @param deg the degree of the polynomial
     * @param n R^n
     */
    public Laurent(int deg, int n) {
        this(SPG.get(deg, n));
    }

    @Override
    public double of(double[] x) {
        return ZToRFunc.st(0, terms.size(), i -> terms.get(i).of(x)).sum();
    }

    /**
     * This method removes like terms from the polynomial. Note, it does not add
     * them, it just deletes the second instance.
     */
    public void removeRedundantTerms() {
        for (int i = 0; i < terms.size(); i++)
            for (int j = i + 1; j < terms.size(); j++)
                if (terms.get(i).likeTerms(terms.get(j)))
                    terms.remove(j);

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        terms.stream().forEach(term -> {
            if (term.coeficient() != 0) sb.append(" + ").append(term);
        });
        return sb.toString();
    }

    @Override
    public Derrivitive d(int i, double dti) {
        class polyDeriv extends Derrivitive {

            public polyDeriv() {
                super(Laurent.this, i, 0);
            }

            @Override
            public double of(double[] xa) {
                return terms.stream().mapToDouble(term -> term.d(i, dti).of(xa)).sum();
            }

        }
        return new polyDeriv();
    }

    public static int numCoeficients(int deg, int n) {
        return SPG.get(deg, n).terms.size();
    }

    protected Laurent(int n) {
        super(n);
        this.terms = new ArrayList<>();
    }
}
