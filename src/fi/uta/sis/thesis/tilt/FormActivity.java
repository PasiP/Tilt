package fi.uta.sis.thesis.tilt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.app.Activity;

public class FormActivity extends Activity {
	public static final String app_root = Environment
			.getExternalStorageDirectory().getPath() + "/Tilt/";
	public static final String filename = "data.csv";
	public static final String encoding = "utf8";
	private static final int BUFFER_SIZE = 8192;
	StringBuilder sb;
	int errorCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);
		//hideSystemUI();
		System.out.println("FormActivity .onCreate");
	}

	@Override
    protected void onStart() {
        super.onStart();
        System.out.println("FormActivity .onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("FormActivity .onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("FormActivity .onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("FormActivity .onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("FormActivity .onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean writeTextToFile(String filePath, String text, boolean append) {
		boolean success = false;
		if(filePath!=null && filePath.length()>0 && !filePath.equals("null")) {
			File f = new File(filePath);
			try{
				boolean exist = f.exists();
				if(!exist) {
					File dir = f.getParentFile();
					if(dir!=null && !dir.exists()){
						dir.mkdirs();
					}
					exist = f.createNewFile();
				}
				if(exist) {
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), encoding),BUFFER_SIZE);
					bw.write(text);
					bw.close();
					success = true;
				}			
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}
    
    public void saveForm(View view) {
    	errorCount = 0;
    	sb = new StringBuilder();
    	//EditText et_age = (EditText) findViewById(R.id.age);
    	
    	System.out.println("TALLENNA CLICKED!");
    	
    	// Add date and time to log string
    	Time time = MenuActivity.getTime();
    	int d = time.monthDay;				// Month day (1-31)
    	int kk = time.month + 1;            // Month (0-11)
    	int y = time.year;                  // Year
    	String t = time.format("%k:%M:%S"); // Current time
    	
    	System.out.println(d+"."+kk+"."+y+" "+t);
    	sb.append("\"Aika\","+d+"."+kk+"."+y+" "+t+"\n");
    	
    	// adds tasktimes to file
    	sb.append("Oikeat vastaukset:,");
    	sb.append(MenuActivity.getTaskCorrect() +"\n");
    	sb.append("Käyttäjän vastaukset:,");
    	sb.append(MenuActivity.getTaskAnswers() +"\n");
    	sb.append("Ajat:,");
    	sb.append(MenuActivity.getTaskTimes() +"\n\n");
    	
    	// Validates form.
    	ViewGroup form = (ViewGroup) findViewById(R.id.form_container);
    	Pair data = loopForm(form);
    	if(data.getFirst() == 0) {
    		System.out.println("TALLENNETAAN TIEDOSTOON!");
    		writeTextToFile(app_root + filename, data.getSecond(), false);
    	} else {
    		System.out.println("LOMAKE EI OLE VALIDI! Virheitä "+ data.getFirst() +" kpl.");
    		writeTextToFile(app_root + filename, data.getSecond(), false);
    	}
    }
    
    private Pair loopForm(ViewGroup parent) {
    	System.out.println("PARENT "+ parent.getClass().toString() );
    	
        for(int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            
            if(child instanceof ViewGroup) {
            	if(child instanceof RadioGroup ) {
	            	RadioGroup rg = (RadioGroup)child;
	                int checked = rg.getCheckedRadioButtonId();
	            	if(checked < 0) {
	    	    		((RadioButton) rg.getChildAt(rg.getChildCount()-1)).setError(getString(R.string.default_radiobutton_error));
	    	    		errorCount++;
	    	    	} else {
	    	    		((RadioButton) rg.getChildAt(rg.getChildCount()-1)).setError(null);
	    	    		View checkedButton = rg.findViewById(checked);
	    	    		int idx = rg.indexOfChild(checkedButton) + 1;
	    	    		sb.append("\""+ idx + "\"\n");
	    	    		System.out.println("CHECKED "+ idx);
	    	    	}
            	} else {
            		//Recursively goes through children
                    ViewGroup group = (ViewGroup)child;
                    loopForm(group);
            	}
            } else if(child instanceof RadioButton) {
            	// do nothing
            } else if(child instanceof EditText) {
            	TextView et = (EditText)child;
            	sb.append("\""+ et.getText().toString() +"\"\n");
            } else if(child instanceof TextView) {
                TextView tv = (TextView)child;
                System.out.println("TEXT: "+ tv.getText().toString());
                String tag = (String) tv.getTag(); 
                if(tag == null) { // likert headers/help texts/ save button have a tag
                	sb.append("\""+ tv.getText().toString() +"\",");
                }
            }
            else {
                //Support for other controls
            }

            
        }
        
        Pair pair = new Pair(errorCount, sb.toString());
        return pair;
    }
    
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_man:
                if (checked)
                    System.out.println("Men are the best!");
                break;
            case R.id.radio_woman:
                if (checked)
                    System.out.println("Women are the best!");
                break;
        }
    }
 	
    final class Pair {
        private final int first; // errors count
        private final String second; // string data

        public Pair(int first, String second) {
            this.first = first;
            this.second = second;
        }

        public int getFirst() {
            return first;
        }

        public String getSecond() {
            return second;
        }
    }
}
