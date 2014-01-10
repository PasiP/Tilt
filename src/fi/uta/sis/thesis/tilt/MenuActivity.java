package fi.uta.sis.thesis.tilt;

import java.util.Date;
import java.util.List;
import java.util.Random;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MenuActivity extends Activity {
	public enum Alignment { LEFT, RIGHT };
	private Intent intent;
	private TimerThread th;
	private String[] items = {"A", "B", "C", "D", "E", "1", "2", "3", "4", "5"};
	private String previous = "";
	private TextView debug;
	private TextView characterView;
	private TextView timer;
	private TextView times;
	private TextView sensor;
	private LinearLayout container;
	private SwipeMenu firstMenuView;
	private SwipeMenu secondMenuView;
	private int points = 0;
	private int games = 0;
	private static StringBuilder task_times = new StringBuilder();
	private static StringBuilder task_answers = new StringBuilder();
	private static StringBuilder task_correct = new StringBuilder();
	private static Time time = new Time(Time.getCurrentTimezone());
	private BroadcastReceiver updateOrientation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		container = (LinearLayout) findViewById(R.id.container);
		buildMenus();		
		timer = (TextView) findViewById(R.id.timer);
		times = (TextView) findViewById(R.id.times);
		debug = (TextView) findViewById(R.id.debug);
		debug.setText("Pisteet 0/0");
		sensor = (TextView) findViewById(R.id.sensor);
		characterView = (TextView) findViewById(R.id.characterView);
		
		intent = new Intent(getBaseContext(), SensorService.class);
		getBaseContext().startService(intent);
		updateOrientation = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		    	String s = "Sensori: " +/*"azimuth: "+ intent.getExtras().getFloat("azimuth") + */
		    				"pitch: "+ intent.getExtras().getFloat("pitch") +" "+
		    				"roll: "+ intent.getExtras().getFloat("roll");
		    	sensor.setText(s);
		    }
		};
		registerReceiver(updateOrientation, new IntentFilter("ORIENTATION_UPDATED"));
		
		th = new TimerThread(timer);
		time.setToNow();
		lotto();
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        System.out.println("MenuActivity .onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("MenuActivity .onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        th.resume();
        System.out.println("MenuActivity .onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("MenuActivity .onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("MenuActivity .onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
	
	private void buildMenus() {
		firstMenuView = new SwipeMenu(Alignment.LEFT, this);
		secondMenuView = new SwipeMenu(Alignment.RIGHT, this);
		
		setStyle(firstMenuView);
		setStyle(secondMenuView);
		
		container.addView(firstMenuView, 0);
		container.addView(secondMenuView, container.getChildCount());
		
		container.invalidate();
		
		for(int i = 0; i < items.length; i++) {
			Button b = new Button(this);
			b.setText(items[i]);
			b.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
			b.setTextSize(22f);
			if(i < 5) {
				firstMenuView.addView(b);
			} else {
				secondMenuView.addView(b);
			}
			
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if( ((SwipeMenu)v.getParent()).isVisible() ) {
						String answer = ((Button)v).getText().toString();
						task_answers.append(answer +",");
						boolean correct = checkup(answer);
						games++;
						if(correct) {
							points++;
						}
						debug.setText("Pisteet "+ points +"/"+ games);
						String old = times.getText().toString();
						String time = timer.getText().toString();
						times.setText( time +", "+ old);
						task_times.append("\""+ time +"\",");
						th.resetTimer();
						lotto();
					}
				}
			});
		}
	}
	
	private boolean checkup(String answer) {
		String correct = characterView.getText().toString();
		task_correct.append(correct +",");
		return correct.equals(answer);
	}
	
	private String lotto() {
		int rand = new Random().nextInt(10);
		String newCharacter = items[rand];
	
		if(newCharacter.equals(previous)) {
			lotto();
		} else {
			characterView.setText(newCharacter);
			previous = newCharacter;
		}
		return newCharacter;
	}
	
	private void setStyle(LinearLayout l) {
		l.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		lp.weight = 1;
		l.setBackgroundColor(Color.parseColor("#bb000000"));
		l.setLayoutParams(lp);
	}
	
	public void openForm(View view) {
		System.out.println("LOMAKE clicked");
		getBaseContext().stopService(intent); // stops SensorService NOT
		Intent intent = new Intent(getApplicationContext(), FormActivity.class);
		th.pause();
		startActivity(intent);
	}
	
	public void showMenu(View view) {
		System.out.println("NÄYTÄ clicked");
		firstMenuView.show();
		secondMenuView.show();
	}
	
	public void hideMenu(View view) {
		System.out.println("PIILOTA clicked");
		firstMenuView.hide();
		secondMenuView.hide();
	}
	
	public static Time getTime() {
		return time;
	}
	
	public static String getTaskTimes() {
		return task_times.toString();
	}
	
	public static String getTaskAnswers() {
		return task_answers.toString();
	}
	
	public static String getTaskCorrect() {
		return task_correct.toString();
	}
}
