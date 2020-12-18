package time;

/**
 *
 * @author Dov
 */
public class TimeOfDay extends Time{

    public TimeOfDay(long milli) {
        super(milli);
        super.day = 0;
    }
    
    public static TimeOfDay get(Time t){
        return new TimeOfDay(t.totMilli());
    }
    
    public static TimeOfDay fromNow(Time t){
        return TimeOfDay.get(t.plus(Clock.currentTOD()));
    }
    
}
