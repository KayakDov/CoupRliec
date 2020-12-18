package time;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is designed to make the use and arrithmatic of units of time more
 * intuative. Each instance represents a unit of time.
 *
 * @author Dov
 */
public class Time {

    protected long day;
    protected long hour;
    protected long min;
    protected long sec;
    protected long mili;

    protected Time(long day, long hour, long min, long sec, long mili) {
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.sec = sec;
        this.mili = mili;
    }

    protected Time(long milli) {
        this(milli / miliInDay, (milli / miliInHour) % 24, (milli / miliInMin) % 60, (milli / miliInSec) % 60, milli % 1000);
    }

    /**
     * Creates a Time instance from a number of milliseconds (1000ms = 1s)
     *
     * @param mili
     * @return A unit of Time
     */
    public static Time milli(double milli) {

        return new Time((long) milli);
    }

    

    /**
     * hour:min:sec or if more than a day "day - hour:min:sec"
     *
     * @return
     */
    @Override
    public String toString() {
        if(hour ==0 && day == 0) return "" + min + ":" + sec;
        if (day == 0) return "" + hour + ":" + min + ":" + sec;        
        return "" + day + " - " + hour + ":" + min + ":" + sec;
    }

    /**
     *
     * @return the amount of days in the day column
     */
    public long getDay() {
        return day;
    }

    /**
     *
     * @return the amount of hours in the hour column
     */
    public long getHour() {
        return hour;
    }

    /**
     * the number of minutes in the minute column
     *
     * @return
     */
    public long getMin() {
        return min;
    }

    /**
     * the number of seconds in the second column
     *
     * @return
     */
    public long getSec() {
        return sec;
    }

    /**
     *
     * @param h
     * @return A time unit of h hours
     */
    public static Time hours(double h) {
        return milli(h * miliInHour);
    }

    /**
     *
     * @param m
     * @return a time unit of m minutes
     */
    public static Time min(double m) {
        return milli(m * miliInMin);
    }

    /**
     *
     * @param s
     * @return a time unit of s seconds
     */
    public static Time sec(double s) {
        return milli(s * miliInSec);
    }
    private final static int miliInSec = 1000, miliInMin = miliInSec * 60, miliInHour = miliInMin * 60,
            miliInDay = miliInHour * 24;

    /**
     *
     * @return the total number of milliseconds in this time
     */
    public long totMilli() {
        return mili + sec * miliInSec + min * miliInMin + hour * miliInHour + day * miliInDay;
    }

    /**
     *
     * @return the total number of seconds
     */
    public double totSeconds() {
        return (double) totMilli() / miliInSec;
    }

    /**
     *
     * @return the total number of minutes
     */
    public double totMinutes() {
        return (double) totMilli() / miliInMin;
    }

    /**
     *
     * @return the total number of hours
     */
    public double totHours() {
        return (double) totMilli() / miliInHour;
    }

    /**
     * the total number of days
     *
     * @return
     */
    public double totDays() {
        return (double) totMilli() / miliInDay;
    }

    /**
     *
     * @param t
     * @return
     */
    public Time minus(Time t) {
        return Time.milli(totMilli() - t.totMilli());
    }

    /**
     *
     * @param t
     * @return
     */
    public Time plus(Time t) {
        return Time.milli(totMilli() + t.totMilli());
    }

    public boolean equals(Time t, double acc) {
        return Math.abs(totMilli() - t.totMilli()) < acc;
    }


    /**
     *
     * @param t
     * @return is this instance less than t
     */
    public boolean before(Time t) {
        return totMilli() < t.totMilli();
    }
    
    
}
