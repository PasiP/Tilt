package fi.uta.sis.thesis.tilt;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.webkit.WebView.FindListener;

public class SensorService extends Service implements SensorEventListener {

	  private SensorManager mSensorManager;
	  private Sensor mOrientation;

	  @Override
	  public void onCreate() {
		// super.onCreate(savedInstanceState);
		System.out.println("SERVICE onCreate()");
	    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	    mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
	  }

	  @Override
	  public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    // Do something here if sensor accuracy changes.
	    // You must implement this callback in your code.
	  }
	  
	  protected void onResume() {
	    mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
	  }

	  protected void onPause() {
	    mSensorManager.unregisterListener(this, mOrientation);
	  }

	  @Override
	  public void onSensorChanged(SensorEvent event) {
	    float azimuth_angle = event.values[0];
	    float pitch_angle = event.values[1];
	    float roll_angle = event.values[2];
	    // Do something with these orientation angles.
	    //System.out.println("azimuth: "+ azimuth_angle +", pitch: "+ pitch_angle +", roll: "+ roll_angle);
	    Intent i = new Intent("ORIENTATION_UPDATED");
	    i.putExtra("azimuth", azimuth_angle);
		i.putExtra("pitch", pitch_angle);
		i.putExtra("roll", roll_angle);
	    sendBroadcast(i);
	  }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
