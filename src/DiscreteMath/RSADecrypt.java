package DiscreteMath;

public class RSADecrypt {

    private final long p, q, e, d;

    public RSADecrypt(long p, long q, long e) {
        this.p = p;
        this.q = q;
        this.e = e;
        d = inverse(e, (p - 1) * (q - 1));
    }

    /**
     * returns an cypher capable of encrypting messages for this to decrypt.
     *
     * @return
     */
    public RSAEncrypt encrypter() {
        return new RSAEncrypt(p * q, e);
    }

    /**
     * A class that describes two integers and their integer coefficients. m*a +
     * n*b
     */
    public static class Coefficients {

        public long a, b, ca, cb;

        /**
         * Describes two integers and their integer coefficients. ca*a + cb*b,
         * and uses them to find Bezout coefficients for integers with and gcd
         * of one.
         *
         * @param ca
         * @param a
         * @param cb
         * @param b
         */
        public Coefficients(long ca, long a, long cb, long b) {
            this.a = a;
            this.b = b;
            this.ca = ca;
            this.cb = cb;
        }

        /**
         * This function replaces either a or b with another coefficients
         * expression. Note, the remaining inserted expression must include, as
         * one of its non coefficient values, the value not being replaced in
         * this expression.  For example:
         * 
         * 2*(5*7+-4*3) + 6*7 -> -8*3 + 16*7
         *
         * @param target the a or b to be replaced. This number must be equal to
         * a or b.
         * @param replacement the expression to replace the target with. The
         * @with expression must include, for either it's a or b value, the
         * value not being replaced in this Coefficients expresion.
         */
        public void replace(long target, Coefficients replacement) {
            if (target == a)
                if (replacement.a == b) {
                    a = replacement.b;
                    ca *= replacement.cb;
                    cb = ca * replacement.ca + cb;
                } else if (replacement.b == b) {
                    replacement.swapAB();
                    replace(target, replacement);
                } else
                    throw new RuntimeException("Bad replacement value passed.  Must contain at least one common integer.");
            else if (target == b) {
                swapAB();
                replace(target, replacement);
            } else
                throw new RuntimeException("replace called with bad target.");
        }

        /**
         * Gives the remaining coefficient in Z_mod
         *
         * @param mod
         * @return the coefficient for a or b, which ever one does not equal
         * mod.
         */
        public long getModCoef(long mod) {
            return a == mod ? cb : ca;
        }

        /**
         * swaps the a and b values.
         */
        private void swapAB() {
            long temp = a;
            long ctemp = ca;
            a = b;
            ca = cb;
            b = temp;
            cb = ctemp;
        }

        @Override
        public String toString() {
            return "" + ca + "*" + a + " + " + cb + "*" + b;
        }

    }

    /**
     * This function will find the Bezout coefficients for two numbers with a
     * gcd of one.
     * This function uses the extended euclidean algorythem.
     * @param a
     * @param b
     * @return two integers m and n such that m*a+n*b=1
     */
    public static Coefficients BezoutCoefForGCDOne(long a, long b) {
        if (a < b) return BezoutCoefForGCDOne(b, a);
        long k = a / b, r = a % b; //a = k*b+r
        if (r == 1) return new Coefficients(1, a, -k, b);
        Coefficients br = BezoutCoefForGCDOne(b, r);
        br.replace(r, new Coefficients(1, a, -k, b));
        return br;
    }

    /**
     * finds the inverse of a number in Z_mod such that n*inverse = 1 (mod mod)
     *
     * @param n the number you want to find the inverse of
     * @param mod the mod, (the number of numbers on a clock)
     * @return the inverse of n
     */
    public static long inverse(long n, long mod) {
        if (n > mod) return inverse(n % mod, mod);
        if (n == 1 || n == mod - 1) return n;
        long inverse = BezoutCoefForGCDOne(n, mod).getModCoef(mod);
        return inverse < 0 ? mod + inverse : inverse;

    }

    /**
     * Turns a number into it's matching letter. i.e. 0 -> a, 1 -> b, etc...
     *
     * @param number
     * @return
     */
    public String numToLetter(long number) {
        StringBuilder s = new StringBuilder();
        for (; number > 0; number /= 100)
            s.insert(0, (char) (number % 100 + (int) 'A'));
        int wordSize = encrypter().wordSize();
        while(s.length() < wordSize/2) s.insert(0, 'A');
        return s.toString();
    }

    public String numToLetter(long[] number) {
        StringBuilder s = new StringBuilder();
        for(long l: number) 
            s.append(numToLetter(l));
        return s.toString();
    }

    
    /**
     * takes in a numerical code and returns the RSA decryption of it.
     * @param code
     * @return 
     */
    public String decrypt(long code) {
        return numToLetter(RSAEncrypt.pow(code, d, p * q));
    }
    
    /**
     * decrypts the code to it's numerical, pre-text, form.
     * @param code
     * @return a new set of decrypted numbers that await translation into strings
     */
    public long[] decryptToInts(long[] code){
        long[] decrypted = new long[code.length];
        long n = p*q;
        for(int i = 0; i < code.length; i++)
            decrypted[i] = RSAEncrypt.pow(code[i], d, n);
        return decrypted;
    }
    
    /**
     * takes in a sequence of numerical codes and returns the RSA decryption.
     * @param code
     * @return 
     */
    public String decrypt(long[] code) {
        StringBuilder decrypt = new StringBuilder();
        code = decryptToInts(code);
        for(long l: code) decrypt.append(numToLetter(l));
        return decrypt.toString();
    }

    /**
     * Just some test code - an example of identity verification.
     * @param Args 
     */
    public static void main(String[] Args) {
        
        RSADecrypt decrypter = new RSADecrypt(43, 59, 13);
        RSAEncrypt encrypter = decrypter.encrypter();
        
        String message = "MEETATNOON";
        
        long[] numOfMessage = encrypter.letterToNum(message);
        
        System.out.println(message + "- direct to numbers:");
        for(long l: numOfMessage) System.out.println(l);
        
        long[] decryptedText = decrypter.decryptToInts(numOfMessage);
        
        System.out.println("Decrypted numbers:\n");
        for(long l: decryptedText) System.out.println(l);
        
        long[] encryptedDecrypted = encrypter.encrypt(decryptedText);
    
        System.out.println("EncryptedDecrypted:\n");
        for(long l: encryptedDecrypted) System.out.println(l);
        
        System.out.println(decrypter.numToLetter(encryptedDecrypted));
    }
}
