package RnSpace.rntor.polynomial;

import DiscreteMath.ZToRFunc;
import RnSpace.rntor.Derrivitive;
import RnSpace.rntor.RnToRFunc;
import RnSpace.rntor.myDerriv;
import java.util.Arrays;

/**
 * A polynomial term over Rn
 *
 * @author Dov Neimand
 */
public class Term extends RnToRFunc {

    private final int[] varPows;
    private double coeficient;

    /**
     * Constructor The space over which the term exists. The default value is
     * 1x^0y^0... Powers of variables an the coeficient can be set separately.
     *
     * @param n the dimension of the space
     */
    public Term(int n) {
        super(n);
        varPows = new int[n];
        coeficient = 1;
    }

    /**
     * Constructor
     *
     * @param varPows the powers of each variable. Order matters.
     * @param coeficient the coeficient/scalar in front of the term
     */
    public Term(int[] varPows, double coeficient) {
        super(varPows.length);
        this.varPows = varPows;
        this.coeficient = coeficient;
    }

    /**
     * The constructor You'll still need to set the coeficient. It's default
     * value is 1.
     *
     * @param varPows the powers of the variables
     * @param n R^n
     */
    public Term(int[] varPows) {
        super(varPows.length);
        this.varPows = varPows;
        this.coeficient = 1;
    }

    @Override
    public double of(double[] x) {
        return ZToRFunc.st(0, x.length, i -> Math.pow(x[i], varPows[i])).product() * coeficient;
    }

    @Override
    public String toString() {
        if (coeficient == 0) return "0";
        StringBuilder sb = new StringBuilder("" + (coeficient == 1.0 ? "" : coeficient));

        for (int i = 0; i < varPows.length; i++) {
            if (varPows[i] != 0) {
                sb.append("x_").append(i);
                if (varPows[i] != 1)
                    sb.append("^").append(varPows[i]).append(" ");
                else sb.append(" ");
            }
        }
        if("".equals(sb.toString())) return ""+coeficient;
        return sb.toString();
    }

    /**
     * Sets the coeficient.
     *
     * @param coeficient
     */
    public void setCoeficient(double coeficient) {
        this.coeficient = coeficient;
    }

    /**
     * adds one to the power of a specific variable and returns a new term
     *
     * @param varIndex the index of the variable
     * @param pow how much to change the value at index by
     * @return returns a new term 
     */
    public Term multX(int varIndex, int pow) {
        Term atr = new Term(this);
        atr.varPows[varIndex] += pow;
        return atr;
    }
    

    /**
     * Sets the power of a specific variable
     *
     * @param varIndex the index of the variable
     * @param pow the power to be set to
     */
    public void setPow(int varIndex, int pow) {
        varPows[varIndex] = pow;
    }

    /**
     * gets the coeficient
     *
     * @return
     */
    public double coeficient() {
        return coeficient;
    }

    /**
     * the degree of the term
     *
     * @return
     */
    public int degree() {
        return (int) ZToRFunc.st(0, varPows.length, i -> varPows[i]).sum();
    }

    /**
     * Determines if the other term and this one are like terms.
     *
     * @param term
     * @return
     */
    public boolean likeTerms(Term term) {
        return Arrays.equals(varPows, term.varPows);
    }

    /**
     * Checks if the terms are equal.
     *
     * @param term
     * @return
     */
    public boolean equals(Term term) {
        return likeTerms(term) && coeficient == term.coeficient;
    }

    public Term(Term term) {
        super(term.getN());
        varPows = Arrays.copyOf(term.varPows, term.varPows.length);
        coeficient = term.coeficient;
    }

    /**
     * Adds to the coeficient
     *
     * @param c
     */
    public void addToCoef(double c) {
        coeficient += c;
    }

    @Override
    public Derrivitive d(int i, double dti) {
        Term d = new Term(this);
        int pow = d.varPows[i];
        if(pow == 0) return myDerriv.deriv(this, x -> 0, i);
        d.coeficient*= pow;
        d.varPows[i] -= 1;
        return myDerriv.deriv(this, x -> d.of(x), i);
    }
    
    
    public boolean hasNegExp(){
        return Arrays.stream(varPows).anyMatch(integer -> integer < 0);
    }
    
    
}
