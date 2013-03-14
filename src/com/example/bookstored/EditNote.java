package com.example.bookstored;

import java.io.BufferedWriter; 
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditNote extends Activity {
	Button mButton;
	EditText mEdit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		mButton = (Button)findViewById(R.id.submit_button);
	    mEdit   = (EditText)findViewById(R.id.EditTitleName);
	    
	    mButton.setOnClickListener(
	            new View.OnClickListener()
	            {
	                public void onClick(View view)
	                {
	                    Log.v("EditText", mEdit.getText().toString());
	            		try{

	            		String title = mEdit.toString();
	            		String formattedTitle = "AUTHOR: " + title;
	            		File sdcard = new File(Environment.getExternalStorageDirectory(), "Notes");
	            		File file = new File(sdcard,"note.txt");            		
	            		FileWriter fileWritter = new FileWriter(file.getName(),true);
	                    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	                    bufferWritter.write(formattedTitle);
	                    bufferWritter.close();
	            		}
	            		catch(IOException e){
	                		e.printStackTrace();
	                	}
	            		
	            		
	                    
	                }
	                
	            });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_note, menu);
		return true;
	}
	
//	public void submitAnnotation(View view){
//		
//		try{
//		EditText titleField = (EditText) findViewById(R.id.TextViewTitle);  
//		String title = titleField.getText().toString();
//		String formattedTitle = "AUTHOR: " + title;
//		File sdcard = new File(Environment.getExternalStorageDirectory(), "Notes");
//		File file = new File(sdcard,"note.txt");
//		
//		FileWriter fileWritter = new FileWriter(file.getName(),true);
//        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
//        bufferWritter.write(formattedTitle);
//        bufferWritter.close();
//		}
//		catch(IOException e){
//    		e.printStackTrace();
//    	}
		
//		Intent intent = new Intent(this, NoteView.class);
//		startActivity(intent);
//	}

}
