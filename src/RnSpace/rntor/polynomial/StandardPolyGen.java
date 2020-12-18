package RnSpace.rntor.polynomial;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 *
 * @author Dov Neimand
 */
public class StandardPolyGen {

    private final ConcurrentHashMap<Integer, StandardPolyDimGen> polys;
    private final boolean hasNegExponenets;

    public StandardPolyGen(boolean hasNegExponenets) {
        this.polys = new ConcurrentHashMap<>(5);
        this.hasNegExponenets = hasNegExponenets;
    }

    public Laurent get(int deg, int dim) {
        if (polys.containsKey(dim)) return polys.get(dim).getStandard(deg);
        polys.put(dim, new StandardPolyDimGen(dim, hasNegExponenets));
        return get(deg, dim);
    }

    public class StandardPolyDimGen {

        public final int n;
        public final boolean hasNegExp;

        /**
         * @param n the number of dimensions
         * @param hasNegExp are all the exponents positive?
         */
        public StandardPolyDimGen(int n, boolean hasNegExp) {
            this.standardPols = new ArrayList<>();
            this.n = n;
            this.hasNegExp = hasNegExp;
        }

        private final ArrayList<Laurent> standardPols;

        /**
         * initializes a Laurent polynomial of degree 0 and adds it to the list.
         *
         * @param n
         * @return
         */
        private Laurent deg0() {
            Laurent deg0 = new Laurent(n);
            deg0.terms.add(new Term(new int[n], 1));
            standardPols.add(deg0);
            return deg0;
        }

        /**
         * creates a new standard polynomial when one is not in storage.
         *
         * @param deg the degree of the polynomial
         * @return
         */
        private Laurent LaurentNewStandard(int deg) {
            Laurent p = new Laurent(n);
            getStandard(deg - 1).terms.forEach(
                    term -> IntStream.range(0, n).forEach(i -> {
                        p.terms.add(term.multX(i, 1));
                        if (hasNegExp) p.terms.add(term.multX(i, -1));
                    }));
            p.terms.addAll(getStandard(deg - 1).terms);
            p.removeRedundantTerms();
            standardPols.add(p);
            return p;
        }

        protected Laurent getStandard(int deg) {

            if (standardPols.size() > deg)
                return new Laurent(standardPols.get(deg));

            if (deg == 0) return deg0();

            return LaurentNewStandard(deg);
        }

        public int getN() {
            return n;
        }

        public int numCoeficients(int deg) {
            return getStandard(deg).terms.size();
        }

    }

}
