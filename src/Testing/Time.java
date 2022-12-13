package Testing;

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
        final long start = System.currentTimeMillis();
        timeable.doSomething();
        return System.currentTimeMillis() - start;
    }

    /**
     * A void parameterless function that can be timed.
     */
    public interface TimeableVoid {

        /**
         * Do something
         */
        public void doSomething();

    }
}
