
package time;

/**
 *
 * @author Dov Neimand
 */
public class MethodTimer {
    
    public static interface Method{
        public void go();
    }
    
    public static double time(Method method){
        long startTime = System.nanoTime();
        method.go();
        long endTime = System.nanoTime();
        return startTime - endTime;
    }
    
    public static void printTime(Method method){
        System.out.println(time(method));
    }

}
