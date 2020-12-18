package RnSpace.parse;

import FuncInterfaces.RnToR;


/**
 * A tool that works with a set of recognized binary operators that are part of
 * a string.
 * @author dov
 */
public class BinaryMachine {
    public RnToR a, b;
    public char op;
    public boolean isGood = true;
    private String initString;
    private int oldStart, oldEnd;

    /**
     * the constructor
     * @param a the first value the operation is to be preformed on.
     * @param op the operation to be preformed.  Accepts * ^ + - /
     * @param b the 2nd value the operation is to be preformed on
     */
    public BinaryMachine(double a, char op, double b) {
        this.a = RnToR.Const(a);
        this.b = RnToR.Const(b);
        this.op = op;
    }
    public BinaryMachine(RnToR a, char op, double b) {
        this.a = a;
        this.b = RnToR.Const(b);
        this.op = op;
    }
    public BinaryMachine(RnToR a, char op, RnToR b) {
        this.a = a;
        this.b = b;
        this.op = op;
    }

    /**
     *
     * @return the operation preformed on a and b
     */
    public RnToR op(){
        RnToR sol = a;
        if(op == '*') sol = a.times(b);
        else if(op == '+') sol = a.plus(b);
        else if(op == '^') sol = a.pow(b);
        else if(op == '-') sol = a.minus(b);
        else if(op == '/') sol = a.over(b);
        return sol;
    }

    /**
     *
     * @param c
     * @return is c a recognized operation
     */
    public static boolean isOperator(char c){
        return c == '+' || c == '-' || c == '*' || c == '/'
                || c == '^';
    }

    /**
     *  constructor
     */
    public BinaryMachine() {
    }

    /**
     *
     * @return the string replaced by the current operation
     */
    public String getReplaced(){
        try{
            return initString.substring(oldStart, oldEnd);
        }catch(Exception e){
            return "";
        }
    }

    private boolean isStringBad(String st, char op){
        return !isOperator(op) ||
           !st.contains(""+op) ||
           st.lastIndexOf(op) == st.length() - 1;
    }

    /**
     *
     * @param st a string containing the operation, a and b
     * @param c the operation to be preformed
     */
    public void setOperation(String st, char c, String[] vars){
        if(isOperator(c)) op = c;

        int cent = st.lastIndexOf(op);

        if(isStringBad(st, op)){
            isGood = false;
            return;
        }

        initString = st;
        if(cent == 0 && (op == '+' || op == '-')){
            a = RnToR.Const(0);
            oldStart = 0;
        }
        else {
            NumberMachine nm = new NumberMachine(st, cent - 1, vars);
            oldStart = nm.getOldStart();
            a = nm.getNum();
        }
        NumberMachine nm = new NumberMachine(st, cent + 1, vars);
        b = nm.getNum();
        oldEnd = nm.getOldEnd();
    }

    public boolean beginsWithOp(String st){
        return isOperator(st.charAt(0));
    }

}
