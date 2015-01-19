package fi.uta.sis.thesis.tilt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Environment;
import android.text.format.Time;

public class FileWriter {
	private static final String app_root = Environment
			.getExternalStorageDirectory().getPath() + "/Tilt/";
	private static String filename;
	private static final String encoding = "utf8";
	private static final int BUFFER_SIZE = 8192;
	private static StringBuilder sb;
	
	public static boolean writeTextToFile(boolean append) {
		sb = new StringBuilder();
		boolean success = false;
		
		final ArrayList<String> task_times = MenuActivity.getTaskTimes();
		final ArrayList<String> task_answers = MenuActivity.getTaskAnswers();
		final ArrayList<String> task_correct = MenuActivity.getTaskCorrect();
		final ArrayList<String> task_selections = MenuActivity.getTaskSelections();
		final ArrayList<String> task_news = MenuActivity.getTaskNews();
		
		
		// Add date and time to log string
    	Time time = MenuActivity.getTime();
    	int d = time.monthDay;				// Month day (1-31)
    	int kk = time.month + 1;            // Month (0-11)
    	int y = time.year;                  // Year
    	String t = time.format("%k:%M:%S"); // Current time
		
    	filename = MenuActivity.getUserID() +"_"+d+kk+y+"_"+t+".csv";
    	String filePath = app_root + filename;
    	
    	sb.append("\"Aika:\","+d+"."+kk+"."+y+" "+t+"\n");
    	sb.append("\"Testihenkilö ID:\","+ MenuActivity.getUserID() +"\n");
    	sb.append("\"Valikko:\","+ MenuActivity.getMenuName()  +"\n\n");
    	sb.append("\"Testi\",\"Merkki\",\"Vastaus\",\"Aika\",\"N->V\",\"V->U\"\n");
    	
    	for(int i = 0; i < task_times.size(); i++) {
    		int num = i+1;
    		if(task_selections.isEmpty() || task_news.isEmpty() ) { // when menu is hideable
    			sb.append("\""+ num +"\",\""+ task_correct.get(i) +"\",\""+ task_answers.get(i) +"\",\""+ task_times.get(i) +"\"\n" );
    		} else {
    			sb.append("\""+ num +"\",\""+ task_correct.get(i) +"\",\""+ task_answers.get(i) +"\",\""+ task_times.get(i) +"\",\""+
    				task_selections.get(i) +"\",\""+ task_news.get(i) +"\"\n" );
    		}
    	}
    	
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
					bw.write(sb.toString());
					bw.close();
					success = true;
				}			
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}
	
	
	/* public static void listFiles() {
		List<String> fileList = new ArrayList<String>();
		File f = new File(app_root);
		File[] files = f.listFiles();
	     fileList.clear();
	     for (File file : files){
	    	 fileList.add(file.getPath());
	    	 if (file.isDirectory()) {
	    		 System.out.println("DIRECTORY: "+ file.getPath());
	    	 }
	     }
	      
	     Iterator<String> iterator = fileList.iterator();
	     while(iterator.hasNext()) {
	    	 System.out.println(iterator.next());
	     }
	    
	} */
}
