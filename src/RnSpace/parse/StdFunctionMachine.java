package RnSpace.parse;

import FuncInterfaces.RToR;


/**
 * A tool to deal with standard functions, like trigonometric, in strings.
 * @author dov
 */
public class StdFunctionMachine {
    private Func func;
    private String initString;
    private int oldStart, oldEnd;
    private boolean startsWithFunction;
    private String insideString;

    /**
     * the functions recognized
     */
    public static enum Func{sin,cos,tan,asin,acos,atan, log, ln}

    private void setUp(String st, int index){
        ParenthMachine pm = new ParenthMachine(st.substring(index));
        for(int i = 0; i < Func.values().length; i++) {
            if(initString.startsWith(Func.values()[i].toString())){
                func = Func.values()[i];
                oldStart = index;
                insideString = pm.getReplaced();
                oldEnd = st.lastIndexOf(pm.getRight());
                startsWithFunction = true;
                return;
            }
        }
    }

    /**
     *
     * @return the string the standard function is acting on
     */
    public String getInsideString() {
        return insideString;
    }



    /**
     * constructor
     * @param st a string that may contain standard functions.
     */
    public StdFunctionMachine(String st) {
        startsWithFunction = false;
        initString = st;
        oldEnd = 0;
        oldStart = 0;
        setUp(st, 0);
    }

    /**
     *
     * @return the string starts with a standard functions
     */
    public boolean startsWithFunction(){
        return startsWithFunction;
    }
    /**
     *
     * @param s
     * @return is the string s a standard function
     */
    public static boolean isStFunc(String s){
        for(int j = 0; j < Func.values().length; j++)
            if(s.equals(Func.values()[j].toString())) return true;
        return false;
    }

    /**
     *
     * @return the function preformed on the value with it in the string
     */
    public RToR op(){
        switch(func){
            case acos: return t -> Math.acos(t);
            case asin: return t -> Math.asin(t);
            case atan: return t -> Math.atan(t);
            case cos: return t -> Math.cos(t);
            case sin: return t -> Math.sin(t);
            case tan: return t -> Math.tan(t);
            case log: return t -> Math.log10(t);
            case ln: return t -> Math.log(t);
            default: return t -> t;
        }
    }

    /**
     *
     * @return the string being replaced by the result of the operation
     */
    public String getReplaced(){
        return initString.substring(oldStart, oldEnd);
    }
}
