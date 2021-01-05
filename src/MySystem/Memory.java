/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MySystem;

/**
 *
 * @author dov
 */
public class Memory {
    
    public final static long totalMemory = remaining();
    
    public static long remaining(){
        long allocatedMemory      = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());

        long presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;

        return presumableFreeMemory;
    }
    
    public static float remainingPercent(){
        return (float)remaining()/totalMemory;
    }
    
    
    public static final float LOW_MEM = .1f;
    public static boolean lowOnMemory(){
        return remainingPercent() < LOW_MEM;
    }
}
