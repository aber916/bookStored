package com.example.bookstored;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditNote extends Activity {
	Button mButton;
	EditText mTitle;
	EditText mAuthor;
	EditText mPageNumber;
	EditText mAdditional;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		mButton = (Button)findViewById(R.id.submit_button);
	    mTitle   = (EditText)findViewById(R.id.EditTitleName);
	    mAuthor   = (EditText)findViewById(R.id.EditAuthorName);
	    mPageNumber   = (EditText)findViewById(R.id.EditPageNumber);
	    mAdditional  = (EditText)findViewById(R.id.AdditionalNotes);
	    String name = getIntent().getStringExtra("title");
	    Log.i("title Hi Abe", name);
	    
	    
		
	    mButton.setOnClickListener(
	            new View.OnClickListener()
	            {
	                public void onClick(View view)
	                {
	                    String title = mTitle.getText().toString();
	                    String author = mAuthor.getText().toString();
	                    String pageNumber = mPageNumber.getText().toString();
	                    String aNotes = mAdditional.getText().toString();
	                    writeSubmittedValues(title, author, pageNumber, aNotes);

	                }              
	            });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_note, menu);
		return true;
	}
	
	
	public void writeSubmittedValues(String title, String author, String pageNumber, String aNotes){ 	//add submitted values
		
		
		String name = getIntent().getStringExtra("title");
		File sdcard = new File(Environment.getExternalStorageDirectory(), "Notes");
		File file = new File(sdcard,name);
		try{
			FileWriter fw = new FileWriter(file,true); //the true will append the new data
		    //appends the string to the fileoutput.append("Title: "+title+'\n');
			
			fw.write("\n'Title': "+title+'\n');
			fw.write("'Author': "+author+'\n');
			fw.write("'Page Number': "+ pageNumber+'\n');
			fw.write("\n Additional Notes: \n" + aNotes + '\n');
		    fw.close();
			
			Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, ListNotesView.class);
			startActivity(intent);
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
		
		
	}

}
