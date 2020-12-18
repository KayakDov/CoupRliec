package RnSpace.parse;

import FuncInterfaces.RnToR;
import RnSpace.rntor.RnToRFunc;
import java.util.ArrayList;
import listTools.MergeSort;

/**
 * this class describes a real function defined on [-inf, inf]^n the function is
 * described by a string.
 *
 * @author dov
 */
public class FunctionParse {

    private String tString;
    private String[] vars;

    /**
     * Constructor
     *
     * @param st a string describing a real valued function. Some trig and
     * logarithmic functions are recognized. The function may be constant or
     * include one variable, "t".
     */
    public FunctionParse(String st, String[] variables) {
        vars = variables;
        tString = st;
        setUp();
    }

    private boolean unacountedValues() {
        for (int i = 0; i < tString.length(); i++) {
            char c = tString.charAt(i);
            if (!NumberMachine.isDigit(c) && !BinaryMachine.isOperator(c)
                    && c != '(' && c != ')' && c != '.' && !isInReservedWord(i)
                    && c != 't')
                return true;
        }
        return false;
    }

    private void setUp() {
        setReservedWords();
        variableAdj();
        insertVal();
        addMult();
    }
    private ArrayList<String> reservedWords;

    private void setReservedWords() {
        reservedWords = new ArrayList<String>(30);
        reservedWords.add("pi");
        reservedWords.add("e");

        for (StdFunctionMachine.Func func : StdFunctionMachine.Func.values())
            reservedWords.add(func.toString());
    }

    
    /**
     * is index i in tString part of a reserved word
     *
     * @param iun
     * @return
     */
    public boolean isInReservedWord(int i) {
        for (String word : reservedWords)
            for (int k = 0; k < word.length(); k++)
                if (tString.startsWith(word, i - k))
                    return true;
        return false;
    }

    /**
     * used for ordering strings according to their length.
     */
    private static class StringSort implements Comparable<StringSort> {
        public String s;

        public StringSort(String s) {
            this.s = s;
        }
        @Override
        public int compareTo(StringSort t) {
            return s.length() - t.s.length();
        }

        public String getS() {
            return s;
        }
    }
    /**
     * 
     * @param s
     * @return a new array of strings with the contents of s in order according 
     * to length.
     */
    public static String[] LongToShort(final String[] s){
        StringSort[] ss = new StringSort[s.length];
        for(int i = 0; i < s.length; i++)
            ss[i] = new StringSort(s[i]);
        MergeSort.MergeSort(ss);
        String lts[] = new String[s.length];
        for(int i = 0; i < s.length; i++)
            lts[i] = ss[i].getS();
        return lts;
    
    }
    private void variableAdj() {
        String[] ordVars = LongToShort(vars);
        for (int i = 0; i < tString.length(); i++)
            for (String var : ordVars)
                if (tString.startsWith(var, i) && !isInReservedWord(i)) {
                    String preVar = tString.substring(0, i++);
                    tString = tString.replace(preVar + var, preVar + "(" + var + ")");
                }
    }

    private static boolean isLetter(char c) {
        return !NumberMachine.isDigit(c)
                && !BinaryMachine.isOperator(c)
                && c != '.' && c != '(' && c != ')';
    }

    private void addMult() {
        for (int i = 0; i < tString.length(); i++) {
            if (i < tString.length() - 1 && tString.charAt(i) == ')' && (tString.charAt(i + 1) == '(' || isLetter(tString.charAt(i + 1))))
                tString = tString.replace(")" + tString.charAt(i + 1), ")*" + tString.charAt(i + 1));
            if (NumberMachine.isDigit(tString.charAt(i)) || tString.charAt(i) == '.') {
                if (i > 0) {
                    char prev = tString.charAt(i - 1);
                    if (prev == ')' || isLetter(prev))
                        tString = tString.replace("" + prev + tString.charAt(i), prev + "*" + tString.charAt(i));
                }
                if (i < tString.length() - 1) {
                    char next = tString.charAt(i + 1);
                    if (next == '(' || isLetter(next))
                        tString = tString.replace("" + tString.charAt(i) + next, "" + tString.charAt(i) + "*" + next);
                }
            }

        }
    }

    private String insert(String target, int index, String bullet) {
        return target.substring(0, index) + bullet + target.substring(index);
    }

    private void insertVal() {

        tString = tString.replace("pi", "" + Math.PI);
        tString = tString.replace("e", "" + Math.E);
        tString = tString.replace("-(", "-1*(");
        tString = tString.replace(" ", "");

        for (int i = 0; i < tString.length() - 1; i++) {
            String c = tString.substring(i, i + 1),
                    cn = tString.substring(i + 1, i + 2);
            if (c.equals("-") && (isLetter(cn.charAt(0)) || cn.equals(".")))
                tString = tString.replaceAll(c + cn, "-1*" + cn);
        }
        if (tString.startsWith("-"))
            tString = "0" + tString;
        tString = tString.replace("(-", "(0-");
        tString = tString.replace("+-", "-");

    }

    private RnToR resolveBinaryOps(String ls) {
        RnToR plus = binaryOp(ls, '+');
        if (plus != null)
            return plus;

        RnToR minus = binaryOp(ls, '-');
        if (minus != null)
            return minus;

        RnToR times = binaryOp(ls, '*');
        if (times != null)
            return times;

        RnToR divide = binaryOp(ls, '/');
        if (divide != null)
            return divide;

        RnToR pow = binaryOp(ls, '^');
        if (pow != null)
            return pow;

        return null;

    }

    /**
     * helps with the "^-" case
     *
     * @param st
     * @param op
     * @param index
     * @return
     */
    private boolean negExCond(String st, char op, int index) {
        if (index > 0 && st.charAt(index - 1) == '^' && op == '-')
            return false;
        return true;
    }

    private RnToR binaryOp(String ls, char op) {
        for (int i = 0, par = 0; i < ls.length(); i++) {
            if (ls.charAt(i) == '(')
                par++;
            if (ls.charAt(i) == ')')
                par--;
            if (par == 0 && ls.charAt(i) == op && negExCond(ls, op, i)) {
                RnToR left = getFunction(ls.substring(0, i)),
                        right = getFunction(ls.substring(i + 1));
                return (new BinaryMachine(left, op, right)).op();
            }
        }
        return null;
    }

    private String removeOusideParenth(String ls) {
        ParenthMachine pm = new ParenthMachine(ls);

        while (pm.status() == ParenthMachine.type.CONTAINS
                && pm.getLeft().equals("") && pm.getRight().equals("")) {

            ls = pm.getReplaced();
            pm = new ParenthMachine(ls);
        }

        return ls;
    }

    /**
     *
     * @return a function described by the string.
     */
    public RnToR get() {
        return getFunction(tString);
    }

    private RnToR getFunction(String ls) {
        //System.out.println(ls);

        ls = removeOusideParenth(ls);
        
        for(int i = 0; i < vars.length; i++)
        if (ls.equals(vars[i]))
            return RnToR.Id(i, vars.length);
        try {
            return RnToR.Const(Double.valueOf(ls));
        } catch (NumberFormatException nfe) {
        }

        RnToR bo = resolveBinaryOps(ls);
        if (bo != null)
            return bo;

        StdFunctionMachine sfm = new StdFunctionMachine(ls);
        if (sfm.startsWithFunction())
            return sfm.op().of(getFunction(sfm.getInsideString()));
        return null;
    }
    
        
    public int isVariable(String s){
        for(int i = 0; i < vars.length; i++)
            if(vars[i].equals(s)) return i;
        return -1;
    }

    public String[] getVars() {
        return vars;
    }

    
}
