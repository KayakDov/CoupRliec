package DiscreteMath;

/**
 * See page 299 of Discrete Mathematics and its applications. This is an overly
 * simplified rsa encryption. It does not work for sufficiently big numbers to
 * actually encrypt anything.
 *
 * @author Dov Neimand
 */
public class RSAEncrypt {

    private final long n, e;

    /**
     * The constructor
     *
     * @param n
     * @param e must be relatively prime to (p-1)(q-1) where pq=n.
     */
    public RSAEncrypt(long n, long e) {
        this.n = n;
        this.e = e;
    }

    /**
     * calculates the size of the words to be encrypted. It's the largest number
     * of digits in 2525...25 that does not exceed n.
     *
     * @return
     */
    public int wordSize() {
        int N;
        int twoFive = 0;
        for (N = 0; twoFive <= n; N++)
            twoFive = twoFive * 100 + 25;
        return (N - 1) * 2;

    }

    /**
     * the number of words that will be in the final encrypt message
     *
     * @param words the string to be encrypt
     * @param wordSize the size of the words in the encrypt message
     * @return
     */
    private int numWords(String words, int wordSize) {
        return (words.length() * 2 + wordSize - 1) / wordSize;
    }

    /**
     * Is this letter upper case
     *
     * @param a
     * @return true if the letter is upper case, false otherwise.
     */
    private static boolean isUpperCase(char a) {
        return (int) 'A' <= a && a <= (int) 'Z';
    }

    /**
     * creates an array of sets of numbers, where each number is a direct
     * translation of a substring of the provided string of letters. a->0, b->1,
     * etc... The size of each wordblock that the string is broken into is
     * determine is determined by the word size algorithm.
     *
     * @param stringOfLetters a string, only letters a-z and A-Z. No spaces.
     * @return
     */
    public long[] letterToNum(String stringOfLetters) {

        int encryptedWordSize = wordSize();

        long[] M = new long[numWords(stringOfLetters, encryptedWordSize)];

        for (int i = 0; i < M.length; i++)
            for (int j = encryptedWordSize * i / 2; j < encryptedWordSize * (i
                    + 1) / 2 && j < stringOfLetters.length(); j++) {
                M[i] *= 100;
                M[i] += isUpperCase(stringOfLetters.charAt(j))
                        ? (int) stringOfLetters.charAt(j) - (int) 'A' : (int) stringOfLetters.charAt(j)
                        - (int) 'a';
            }
        return M;
    }

    /**
     * the final set of encrypt words
     *
     * @param stringOfLetters the words you want encrypt. No spaces.
     * @return
     */
    public long[] encrypt(String stringOfLetters) {
        long[] encrypted = letterToNum(stringOfLetters);

        return RSAEncrypt.this.encrypt(encrypted);
    }

    public long[] encrypt(long[] blocks) {
        long[] encrypted = new long[blocks.length];

        for (int i = 0; i < encrypted.length; i++)
            encrypted[i] = pow(blocks[i], e, n);

        return encrypted;
    }

    /**
     * Euclid's algorithm
     *
     * @param a
     * @param b
     * @return the greatest common divisor of a and b
     */
    public static long gcd(long a, long b) {
        if (a < b) return gcd(b, a);
        if (a % b == 0) return b;
        return gcd(b, a % b);
    }

    /**
     * This will only work if the base is coprime to the mod.
     *
     * @param base
     * @param exp
     * @param mod
     * @return
     */
    public static long pow(long base, long exp, long mod) {
        if (exp == 1) return base % mod;
        while (exp > mod - 1) exp -= mod - 1;
        if (exp % 2 == 0) return pow(base * base % mod, exp / 2, mod);
        return (base * pow(base, exp - 1, mod)) % mod;
    }

    public static void main(String[] Args) {
        RSAEncrypt rsaEn = new RSAEncrypt(2537, 13);
        long[] encrypted = rsaEn.encrypt("DONOTPASSGO");//1819 1415.
        for (int i = 0; i < encrypted.length; i++)
            System.out.println(encrypted[i]);

        //"MEETMEATNOON" 1204 0419 0019 1314 1413
    }
}
