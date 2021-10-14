package tools;

/**
 * A class to time methods.
 *
 * @author Dov Neimand
 */
public abstract class Time {
    
    /**
     * 
     * @param timeable Pass in a void parameterless lambda expression
     * @return how long does the function take.
     */
    public static long inMilli(TimeableVoid timeable){
        return timeable.time();
    }

    /**
     * A void parameterless function that can be timed.
     */
    public interface TimeableVoid {

        /**
         * Do something
         */
        public void doSomething();

        /**
         * How long does it take to do something.
         * @return 
         */
        public default long time() {
            final long start = System.currentTimeMillis();
            doSomething();
            return System.currentTimeMillis() - start;
        }
    }
}
