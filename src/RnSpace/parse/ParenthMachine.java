package RnSpace.parse;

/**
 * A tool for handling parenthesis in Strings
 * @author dov
 */
public class ParenthMachine {
    public enum type {LACKS, CONTAINS, INVALID}

    private String st;
    private int oldStart, oldEnd;

    /**
     * the constructor
     * @param initSt the string to handle
     */
    public ParenthMachine(String st) {
        this.st = st;
        oldStart = st.indexOf('(');
        if(oldStart == -1) return;
        int numP = 0;
        for(int i = oldStart; i < st.length(); i++){
            if(st.charAt(i) == '(') numP++;
            if(st.charAt(i) == ')'){
                if(--numP == 0){
                    oldEnd = i;
                    return;
                }
            }
        }

    }

    /**
     *
     * @param initSt string to be examined
     * @return Does the s/tring contain or lack parenthasis.
     * Is there something wrong with them?
     */
    public type status() {
        int open = 0, close = 0;
        for (int i = 0; i < st.length(); i++){
            if(st.charAt(i) == '(') open++;
            if(st.charAt(i) == ')') close++;
        }
        if(open != close) return type.INVALID;
        if(open == 0) return type.LACKS;
        return type.CONTAINS;
    }
    /**
     *
     * @param s a string
     * @return if the string contains parenthesis the
     * bounded substring without the parenthesis.
     */
    public String getSSL(){
        return st.substring(oldStart+1, oldEnd);
    }

    /**
     *
     * @return the sting surounded by parenthasis
     */
    public String getReplaced(){
        return st.substring(oldStart+1, oldEnd);
    }

    /**
     *
     * @return the string to the left of the inner most parenthasis.
     */
    public String getLeft(){
        return st.substring(0, oldStart);
    }
    /**
     *
     * @return the string to the right of the inner most parenthasis.
     */
    public String getRight(){
        return st.substring(oldEnd + 1);
    }

}
