package fi.uta.sis.thesis.tilt;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

public class TimerThread implements Runnable {
	private Handler handler = new Handler();
	private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private TextView timer;

    public TimerThread(TextView timer) {
    	this.timer = timer;
    	startTime = SystemClock.uptimeMillis();
    	handler.postDelayed(this, 0);
    } 
    
    @Override
	public void run() {
		timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
        updatedTime = timeSwapBuff + timeInMilliseconds;
        int secs = (int) (updatedTime / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        int milliseconds = (int) (updatedTime % 1000);
        timer.setText(mins + ":"
                + String.format("%02d", secs) + ","
                + String.format("%03d", milliseconds) );
        handler.postDelayed(this, 0);
    }
    
    public void pause() {
    	timeSwapBuff += timeInMilliseconds;
        handler.removeCallbacks(this);
    }
    
    public void resume() {
    	startTime = SystemClock.uptimeMillis();
        handler.postDelayed(this, 0);
    }
    
	public long getTime() {
		return timeInMilliseconds;
	}
	
    public void resetTimer() {
    	startTime = SystemClock.uptimeMillis();
    	timeSwapBuff = 0L;
    }
		
}
