package time;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * An instance of this class will activate an alarm on the next occasion of a
 * specific time of day
 * 
 * @author Dov
 */
public class Clock implements Runnable, TimeUp{

    
    private long stopWatchStart;
    
    
    /**
     * starts a stop watch
     */
    public void startStopWatch(){
        stopWatchStart = System.currentTimeMillis();
    }
    
    /**
     * the amount of time since the stop watch was started
     * @return 
     */
    public Time stopWatchTime(){
        return Time.milli(System.currentTimeMillis() - stopWatchStart);
    }
    
    /**
     * 
     * @param alarmTimeOfDay the time of day the alarm is to go off
     */
    public void setAlarm(TimeOfDay alarmTOD){
        if(currentTOD().before(alarmTOD)) 
            waitTime = (alarmTOD.minus(currentTOD()));
        else
            waitTime = (Time.hours(24).minus((currentTOD().minus(alarmTOD))));
               
        (new Thread(this)).start();
    }

    private Clock(Time waitTime, TimeUp timeUp) {
         this.waitTime = waitTime;
         this.timeUp = timeUp;
    }

    public Clock() {
    }
    
    
    
    private Time waitTime;
    
    public void timer(Time timeFromNow){
        timer(timeFromNow, this);
    }
    public void timer(Time timeFromNow, TimeUp tu){
        waitTime = currentTOD().plus(timeFromNow);
        (new Thread(new Clock(timeFromNow, tu))).start();
    }
    public void silentTimer(Time timeFromNow){
        waitTime = currentTOD().plus(timeFromNow);
    }
    
    public Time timeRemaining(){
        return waitTime.minus(currentTOD());
    }

    private TimeUp timeUp;
    @Override
    public void run() {
        wait(waitTime);
        timeUp.beep();
    }
    
    protected void alarmRing(){
        try {
            String runMovie = "cmd /c start "
                    + "wmplayer "
                    + "\"C:\\Users\\Dov\\Videos\\Coraline.avi\"";
            Runtime.getRuntime().exec(runMovie);
        } catch (IOException ex) {
            Logger.getLogger(Clock.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("ALARM!!\n" + currentTOD());
    }
    
    /**
     *
     * @return the current time of day, I'm not sure how this works in other
     * time zones
     */
    public static TimeOfDay currentTOD() {
        return TimeOfDay.get(Time.milli((System.currentTimeMillis() - 6 * 3600 * 1000) % (24 * 3600 * 1000)));
    }
    
    
    /**
     * causes this thread to sleep for the appointed time t
     *
     * @param t the amount of time this thread does nothing for
     */
    public static void wait(Time t) {
        try {
            Thread.sleep(t.totMilli());
        } catch (InterruptedException ex) {
            Logger.getLogger(Time.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void beep() {
        alarmRing();
    }
}
