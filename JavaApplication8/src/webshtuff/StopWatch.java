package webshtuff;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class StopWatch {

    private GregorianCalendar startTime;
    private double elapsedTime; //stored in milliseconds
    private boolean isStopped;

    //constructor
    public StopWatch() {
        startTime = new GregorianCalendar();
        elapsedTime = 0;
        isStopped = true;
    }

    //starts the timer
    public void start() {
        if (isStopped) {
            startTime = (GregorianCalendar) Calendar.getInstance();
        }
        isStopped = false;
    }

    //adds the most recent time segment to the total elapsed time and stops the timer
    public void stop() {
        elapsedTime = calculateRunningTime() + elapsedTime;
        isStopped = true;
    }

    //returns the time elapsed
    public double getTime() {
        return (elapsedTime + calculateRunningTime()) / 1000;
    }

    // resets and stops the timer
    public void reset() {
        isStopped = true;
        elapsedTime = 0;
    }

    //helper Method to Calculate the Running Time of a Segment
    private double calculateRunningTime() {
        if (isStopped) {
            return 0;
        }
        GregorianCalendar rightNow;
        rightNow = (GregorianCalendar) Calendar.getInstance();
        return (double) (rightNow.getTimeInMillis() - startTime.getTimeInMillis());

    }
}
