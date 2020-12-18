package realFunction;

import FuncInterfaces.RToR;
import FuncInterfaces.RnToR;
import RnSpace.parse.FunctionParse;

/**
 * A function class that is defined by a string.
 * note, for now, Sep 26 2012, the expression "^-t" doesn't work.
 * @author dov
 */
public class RToRFuncStr implements RToR{
    RnToR f;

 

    /**
     * The variable in the string must be t
     * @param st
     */
    public RToRFuncStr(String st) {
        f = (new FunctionParse(st.replaceAll("x", "t"), new String[]{"t"})).get();
    }



    @Override
    public double of(double t) {
        return f.of(new double[]{t});
    }

}
