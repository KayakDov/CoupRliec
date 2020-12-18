package RnSpace.rntor;

import FuncInterfaces.RnToR;
import RnSpace.parse.FunctionParse;

/**
 * this class lets a function be defined by parsing a string.
 * @author Dov
 */
public class RnToRFuncStr extends RnToRFunc{
    
    private RnToR core;
    
    public RnToRFuncStr(String f, String[] vars) {
        super(vars.length);
        core = (new FunctionParse(f, vars)).get();
        
    }

    @Override
    public double of(double[] x) {
        return core.of(x);
    }
    
    
    
}