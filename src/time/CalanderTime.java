package time;

/**
 *
 * @author Dov
 */
public class CalanderTime {
    private TimeOfDay time;
    private int date, month, year;

    public CalanderTime(TimeOfDay time) {
        this.time = time;
        
    }
    
    
    
    private Time JanuaryFirst2013;
    
    private boolean isLeapYear(int year){
        return 4%year == 0;
    }
    private int daysIn4Y = 4*365+1, jan = 31, feb = 28, febl = 29, march= 31, apr = 30, may = 31,
            june = 30, july = 31, aug = 31, sept = 30, oct =  31,nov = 30, dec = 31;
    
}
