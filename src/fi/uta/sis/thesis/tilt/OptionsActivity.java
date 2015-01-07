package fi.uta.sis.thesis.tilt;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class OptionsActivity extends Activity implements NumberPicker.OnValueChangeListener {
	ArrayList<Menu> menus = new ArrayList<Menu>();
	EditText showAngle;
	EditText hideAngle;
	EditText testCount;
	EditText et_userID;
	CheckBox cb_hideable;
	CheckBox cb_reverse;
	int defaultTestCount = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		
		showAngle = (EditText) findViewById(R.id.showangle_edit);
		showAngle.setText("0");
		hideAngle = (EditText) findViewById(R.id.hideangle_edit);
		hideAngle.setText("0");
		cb_hideable = (CheckBox) findViewById(R.id.cb_hideable);
		cb_reverse = (CheckBox) findViewById(R.id.cb_reverse);
		testCount = (EditText) findViewById(R.id.test_count_edit);
		testCount.setText(Integer.toString(defaultTestCount));
		et_userID = (EditText) findViewById(R.id.userID_edit);
		et_userID.setText(MenuActivity.getUserID());
		
		menus.add(new Menu("Perinteinen valikko",0,0,false,false));
		menus.add(new Menu("Tilt Menu 10 10",10,10,true,false));
		menus.add(new Menu("Tilt Menu 15 10",15,10,true,false));
		menus.add(new Menu("Tilt Menu Reverse 15 10",15,10,true,true));
		
		String[] values = {
				"1. "+ menus.get(0).getName(),
				"2. "+ menus.get(1).getName(),
				"3. "+ menus.get(2).getName(),
				"4. "+ menus.get(3).getName(),
		};
		
		NumberPicker np = (NumberPicker) findViewById(R.id.np);
		np.setMaxValue(values.length-1);
		np.setMinValue(0);
		np.setDisplayedValues(values);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np.setOnValueChangedListener(this);
	}

	@Override
    protected void onStart() {
        super.onStart();
        System.out.println("OptionsActivity .onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("OptionsActivity .onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("OptionsActivity .onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("OptionsActivity .onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("OptionsActivity .onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    public void quitProgram(View view) {
    	System.out.println("SULJE clicked");
    	// opens menuActivity and closes the program
    	Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	intent.putExtra("EXIT", true);
    	startActivity(intent);
    }
    
    public void openForm(View view) {
    	System.out.println("LOMAKE clicked");
    	Intent intent = new Intent(getApplicationContext(), FormActivity.class);
		startActivity(intent);
    }
    
    public void ok(View view) {
    	System.out.println("KÄYTÄ clicked");
    	Intent intent = new Intent(getApplicationContext(), Menu.class);
    	/* String name;
		int showAngle;
		int hideAngle;
		Boolean hideable; */
    	NumberPicker np = (NumberPicker) findViewById(R.id.np);
    	int chosen_idx = np.getValue();
    	String name = menus.get(chosen_idx).getName();
    	intent.putExtra("NAME", name);
    	int show_angle = Integer.parseInt(showAngle.getText().toString());
    	intent.putExtra("SHOW_ANGLE", show_angle);
    	int hide_angle = Integer.parseInt(hideAngle.getText().toString());
    	intent.putExtra("HIDE_ANGLE", hide_angle);
    	Boolean hideable = cb_hideable.isChecked();
    	intent.putExtra("HIDEABLE", hideable);
    	Boolean reverse = cb_reverse.isChecked();
    	intent.putExtra("REVERSE", reverse);
    	int testCountNum = Integer.parseInt(testCount.getText().toString());
    	intent.putExtra("TEST_COUNT", testCountNum);
    	setResult(RESULT_OK, intent);
    	String user_id = et_userID.getText().toString();
    	intent.putExtra("USER_ID", user_id);
    	super.finish();
    }
    
    /*public void cb_hideableClicked(View view) {
    	boolean checked = ((CheckBox) view).isChecked();
    	
    	if(checked) {
    		System.out.println("CB checked");
    	} else {
    		System.out.println("CB unchecked");
    	}
    	
    } */

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		/*EditText showAngle = (EditText) findViewById(R.id.showangle_edit);
		EditText hideAngle = (EditText) findViewById(R.id.hideangle_edit);
		CheckBox cb_hideable = (CheckBox) findViewById(R.id.cb_hideable);*/
		
		int show = menus.get(newVal).getShowAngle();
		showAngle.setText(Integer.toString(show));
		
		int hide = menus.get(newVal).getHideAngle();
		hideAngle.setText(Integer.toString(hide));
		
		Boolean hideable = menus.get(newVal).isHideable();
		cb_hideable.setChecked(hideable);
		
		Boolean reverse = menus.get(newVal).isReverse();
		cb_reverse.setChecked(reverse);
	}
	
	private class Menu {
		String name;
		int showAngle;
		int hideAngle;
		Boolean hideable;
		Boolean reverse;
		
		Menu(String n, int sa, int ha, Boolean h, Boolean r) {
			this.name = n;
			this.showAngle = sa;
			this.hideAngle = ha;
			this.hideable = h;
			this.reverse = r;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getShowAngle() {
			return showAngle;
		}

		public void setShowAngle(int showAngle) {
			this.showAngle = showAngle;
		}

		public int getHideAngle() {
			return hideAngle;
		}

		public void setHideAngle(int hideAngle) {
			this.hideAngle = hideAngle;
		}

		public Boolean isHideable() {
			return hideable;
		}

		public void setHideable(Boolean hideable) {
			this.hideable = hideable;
		}
		
		public Boolean isReverse() {
			return reverse;
		}

		public void setReverse(Boolean reverse) {
			this.reverse = reverse;
		}
	}
}
