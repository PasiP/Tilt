package fi.uta.sis.thesis.tilt;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity {
	public enum Alignment {
		LEFT, RIGHT
	};

	private Intent intent;
	private TimerThread th;
	private String[] items = { "A", "B", "C", "D", "E", "1", "2", "3", "4", "5" };
	private String previous = "";
	private TextView tv_debug;
	private static String menuName;
	private TextView characterView;
	private TextView timer;
	private TextView times;
	private TextView selections;
	private TextView shows;
	private TextView sensor;
	private TextView counter;
	private TextView header;
	private LinearLayout container;
	private LinearLayout ll_debug;
	private SwipeMenu firstMenuView;
	private SwipeMenu secondMenuView;
	private int points = 0;
	private int games = 0;
	private int total = 0;
	private static ArrayList<String> task_times = new ArrayList<String>();
	private static ArrayList<String> task_answers = new ArrayList<String>();
	private static ArrayList<String> task_correct = new ArrayList<String>();
	private static ArrayList<String> task_selections = new ArrayList<String>();
	private static ArrayList<String> task_news = new ArrayList<String>();
	private static Time time = new Time(Time.getCurrentTimezone());
	private BroadcastReceiver updateOrientation;
	private int show_threshold = 15;
	private int hide_threshold = 10;
	private int reset_threshold = 4;
	private static String userID = "0";
	static final int PICK_MENU_REQUEST = 123;
	private Boolean hideable = true;
	private Boolean reverse = false;
	private Boolean reset = false;
	private Boolean debug = false;
	private Boolean firstTime = true;
	private Date showTime; // when menu is shown
	private Date selectTime; // when user selects button
	private Date newTime; // when new task is shown
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		container = (LinearLayout) findViewById(R.id.container);
		ll_debug = (LinearLayout) findViewById(R.id.ll_debug);
		buildMenus();
		timer = (TextView) findViewById(R.id.timer);
		times = (TextView) findViewById(R.id.times);
		selections = (TextView) findViewById(R.id.selections);
		shows = (TextView) findViewById(R.id.shows);
		tv_debug = (TextView) findViewById(R.id.debug);
		tv_debug.setText("Pisteet 0/0");
		sensor = (TextView) findViewById(R.id.sensor);
		characterView = (TextView) findViewById(R.id.characterView);
		counter = (TextView) findViewById(R.id.counterView);
		header = (TextView) findViewById(R.id.headerView);
		
		// closes the program if EXIT flag is set
		if (getIntent().getBooleanExtra("EXIT", false)) {
			quitProgram();
		}
		
		hideSystemUI();
		
		// Receiver for orientation sensor
		intent = new Intent(getBaseContext(), SensorService.class);
		getBaseContext().startService(intent);
		updateOrientation = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				float pitch = intent.getExtras().getFloat("pitch");
				float roll = intent.getExtras().getFloat("roll");
				String s = "Sensori: " + /*
										 * "azimuth: "+
										 * intent.getExtras().getFloat
										 * ("azimuth") +
										 */
				"pitch: " + pitch + "roll: " + roll;
				sensor.setText(s);
				
				// to reset the test for new round
				if(pitch >= -reset_threshold && pitch <= reset_threshold && reset == true ) {
					if(hideable) {
						newTime = new Date();
						long diff = newTime.getTime() - selectTime.getTime();
						shows.append(Long.toString(diff) +", ");
						task_news.add(Long.toString(diff));
					}
					characterView.setTextColor(Color.BLACK);
					th.resetTimer();
					lotto();
					reset = false;
					
					// test is done
					if(games >= total) {
						openTestDoneDialog();
						// FileWriter.listFiles();
						FileWriter.writeTextToFile(false);
					}
				}
				
				if(!reverse) {
					if (pitch <= -show_threshold && !secondMenuView.isVisible()) {
						showTime = new Date();
						secondMenuView.show();
					} else if (pitch > -hide_threshold && secondMenuView.isVisible()) {
						secondMenuView.hide();
					}
	
					if (pitch >= show_threshold && !firstMenuView.isVisible()) {
						showTime = new Date();
						firstMenuView.show();
					} else if (pitch < hide_threshold && firstMenuView.isVisible()) {
						firstMenuView.hide();
					}
				} else {
					if (pitch <= -show_threshold && !firstMenuView.isVisible()) {
						showTime = new Date();
						firstMenuView.show();
					} else if (pitch > -hide_threshold && firstMenuView.isVisible()) {
						firstMenuView.hide();
					}
	
					if (pitch >= show_threshold && !secondMenuView.isVisible()) {
						showTime = new Date();
						secondMenuView.show();
					} else if (pitch < hide_threshold && secondMenuView.isVisible()) {
						secondMenuView.hide();
					}
				}
			}
		};
		registerReceiver(updateOrientation, new IntentFilter(
				"ORIENTATION_UPDATED"));

		// longClickListener for options menu
		Button optionsButton = (Button)findViewById(R.id.b_options);
		optionsButton.setOnLongClickListener(new OnLongClickListener(){
			public boolean onLongClick(View v) {
				openOptions(v);
				return true;
			}
		});
			
		th = new TimerThread(timer);
		time.setToNow();
		lotto();
		
		/* if(firstTime) {
			firstTime = false;
			firstMenuView.hide();
			secondMenuView.hide();
			openOptions(null);
		} */
	}

	public int getShow_threshold() {
		return show_threshold;
	}

	public void setShow_threshold(int show_threshold) {
		this.show_threshold = show_threshold;
	}

	public int getHide_threshold() {
		return hide_threshold;
	}

	public void setHide_threshold(int hide_threshold) {
		this.hide_threshold = hide_threshold;
	}

	public static String getUserID() {
		return userID;
	}
	
	public void setUserID(String id) {
		userID = id;
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

		for (int i = 0; i < items.length; i++) {
			Button b = new Button(this);
			b.setText(items[i]);
			b.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
			b.setTextSize(22f);
			if (i < 5) {
				firstMenuView.addView(b);
			} else {
				secondMenuView.addView(b);
			}

			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((SwipeMenu) v.getParent()).isVisible() && reset == false) {
						selectTime = new Date();
						if(hideable) {
							long diff = selectTime.getTime() - showTime.getTime();
							selections.append(Long.toString(diff) +", ");
							task_selections.add(Long.toString(diff));
						}
						String answer = ((Button) v).getText().toString();
						task_answers.add(answer);
						boolean correct = checkup(answer);
						games++;
						if (correct) {
							points++;
						}
						tv_debug.setText("Pisteet " + points + "/" + games);
						counter.setText(games + " / " + total);
						String old = times.getText().toString();
						String time = timer.getText().toString();
						times.setText(time + ", " + old);
						task_times.add(time);
						// sets boolean that is used to reset the timer and show new character
						reset = true;
						characterView.setTextColor(Color.WHITE);
					}
				}
			});
		}
	}

	private boolean checkup(String answer) {
		String correct = characterView.getText().toString();
		task_correct.add(correct);
		return correct.equals(answer);
	}

	private String lotto() {
		int rand = new Random().nextInt(10);
		String newCharacter = items[rand];

		if (newCharacter.equals(previous)) {
			lotto();
		} else {
			characterView.setText(newCharacter);
			previous = newCharacter;
		}
		return newCharacter;
	}

	private void setStyle(LinearLayout l) {
		l.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.weight = 1;
		l.setBackgroundColor(Color.parseColor("#bb000000"));
		l.setLayoutParams(lp);
	}

	public void openForm(View view) {
		System.out.println("LOMAKE clicked");
		Intent intent = new Intent(getApplicationContext(), FormActivity.class);
		th.pause();
		startActivity(intent);
	}

	public void openOptions(View view) {
		Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
		th.pause();
		startActivityForResult(intent, PICK_MENU_REQUEST);
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

	public void quitProgram() {
		//unregisterReceiver(updateOrientation);
		finish();
		System.exit(0);
	}
	
	public static Time getTime() {
		time.setToNow();
		return time;
	}

	public static ArrayList<String> getTaskTimes() {
		return task_times;
	}

	public static ArrayList<String> getTaskAnswers() {
		return task_answers;
	}

	public static ArrayList<String> getTaskCorrect() {
		return task_correct;
	}

	public static ArrayList<String> getTaskSelections() {
		return task_selections;
	}
	
	public static ArrayList<String> getTaskNews() {
		return task_news;
	}
	
	public static String getMenuName() {
		return menuName;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == PICK_MENU_REQUEST) {
			if (data.hasExtra("NAME")) {
				header.setText(data.getExtras().getString("NAME"));
				menuName = data.getExtras().getString("NAME");
			}
			if (data.hasExtra("HIDE_ANGLE")) {
				hide_threshold = data.getExtras().getInt("HIDE_ANGLE");
			}
			if (data.hasExtra("SHOW_ANGLE")) {
				show_threshold = data.getExtras().getInt("SHOW_ANGLE");
			}
			if (data.hasExtra("HIDEABLE")) {
				hideable = data.getExtras().getBoolean("HIDEABLE");
				firstMenuView.setHideable(hideable);
				secondMenuView.setHideable(hideable);
				if(hideable) {
					firstMenuView.hide();
					secondMenuView.hide();
				} else {
					firstMenuView.show();
					secondMenuView.show();
				}
			}
			if (data.hasExtra("REVERSE")) {
				reverse = data.getExtras().getBoolean("REVERSE");
			}
			if (data.hasExtra("DEBUG")) {
				debug = data.getExtras().getBoolean("DEBUG");

				if(debug) {
					ll_debug.setVisibility(View.VISIBLE);
					header.setVisibility(View.VISIBLE);
					counter.setVisibility(View.VISIBLE);
				} else {
					ll_debug.setVisibility(View.GONE);
					header.setVisibility(View.GONE);
					counter.setVisibility(View.GONE);
				}
			}
			if (data.hasExtra("TEST_COUNT")) {
				total = data.getExtras().getInt("TEST_COUNT");
				counter.setText("0 / "+ Integer.toString(total));
			}
			if (data.hasExtra("USER_ID")) {
				userID = data.getExtras().getString("USER_ID");
			}
			
			reset();
			th.resetTimer();
	  }
	} 
	
	private void reset() {
		games = 0;
		points = 0;
		tv_debug.setText("Pisteet 0/0");
		times.setText("");
		selections.setText("");
		shows.setText("");
		
		task_times.clear();
		task_answers.clear();
		task_correct.clear();
		task_selections.clear();
		task_news.clear();
	}
	
	private void openTestDoneDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Testi on valmis.")
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.dismiss();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	// This snippet hides the system bars.
	private void hideSystemUI() {
		View mDecorView = getWindow().getDecorView();
		mDecorView.setSystemUiVisibility(8);
	}
}
