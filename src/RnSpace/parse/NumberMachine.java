package RnSpace.parse;

import FuncInterfaces.RnToR;

/**
 * pulls numbers out of strings
 * @author dov
 */
public class NumberMachine {
    private String st;
    private int index, oldStart, oldEnd;
    private FunctionParse fp;
    private RnToR num;

    
    /**
     *
     * @param c
     * @return is c a digit, 0 - 9
     */
    public static boolean isDigit(char c){
        return 58 > (int) c && (int) c > 47;
    }

    /**
     * a class designed to assist with the identification of variables
     */
    private class VarData {

        public boolean isVar;
        public int varD;
        public String var;
        public int startAt, endAt;

        public VarData(String s, int index, String[] variables) {
            String[] vars = FunctionParse.LongToShort(variables);
            for (String var : vars)
                for (int j = 0; j < var.length(); j++)
                    if (s.startsWith(var, index - j)) {
                        isVar = true;
                        this.var = var;
                        startAt = index - j;
                        endAt = startAt + var.length();
                        for (int k = 0; k < variables.length; k++)
                            if (var.equals(variables[k]))
                                varD = k;
                    }
            isVar = false;
        }
    }
     
    
    /**
     * constructor
     * @param st A string the double is to be pulled out of
     * @param index a location in the string that is part of the double
     */
    public NumberMachine(String st, int index, String[] vars) {
        this.st = st;
        this.index = index;
        VarData isVar = new VarData(st, index, vars);
        if(isVar.isVar){//st.charAt(index) == 't'){
            num = RnToR.Id(isVar.varD);
            oldStart = isVar.startAt;
            oldEnd = isVar.endAt;
        }
        else{
        String pred = getPred(), aft = getAft();
        num = RnToR.Const(Double.parseDouble(pred + aft));
        oldStart = index - pred.length();
        oldEnd = index + aft.length();
        }

    }

    /**
     *
     * @return the location where the double being pulled out started.
     */
    public int getOldStart() {
        return oldStart;
    }

    /**
     *
     * @return the location where the double being pulled out ended.
     */
    public int getOldEnd() {
        return oldEnd;
    }

    /**
     * sets the string to be worked on.
     * @param initString the string the number is to be extracted from.
     * @return this machine
     */
    public NumberMachine setString(String initString) {
        this.st = initString;
        return this;
    }
    /**
     *
     * @param c
     * @return c is c a lower case letter
     */
    public boolean isLCaseLetter(char c){
        return 123 > (int)c && (int)c > 96;
    }

    private boolean isPartOfNum(int i){
        char c = st.charAt(i);
        if(c == '-' && i < st.length() - 1 && st.charAt(i+1) == '-')
            return false;
        boolean isPartofNum =
                 (isDigit(c) ||
                 c == 'E'||
                 c == '.' ||
                 (c == '-' &&
                   (i == 0 ||
                    i > 0  && (
                      BinaryMachine.isOperator(st.charAt(i - 1)) ||
                      st.charAt(i - 1) == '(' ||
                      st.charAt(i - 1) == 'E' ))
                ||(i == index && i + 1 < st.length() && c == '-' && isDigit(st.charAt(i + 1))))
                );
        return isPartofNum;
    }

    private String getAft(){
        String aft = "";
        for(int i = index; i < st.length() && isPartOfNum(i); i++)
            aft += ""+st.charAt(i);
        return aft;
    }
    private String getPred(){
        int i = index;
        String pred = "";
        if(!isPartOfNum(i)) return pred;
        for(; i > 0 && isPartOfNum(i - 1); i--)
            pred = pred.replaceFirst("", ""+st.charAt(i - 1));
        return pred;
    }
    /**
     *
     * @return the number this machine is set to find
     */
    public RnToR getNum(){
        return num;
    }
}
