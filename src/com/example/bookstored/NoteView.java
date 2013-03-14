package com.example.bookstored;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
  
public class NoteView extends Activity {  
	
  /** Called when the activity is first created. */  
  @Override  
  public void onCreate(Bundle savedInstanceState) {  
    super.onCreate(savedInstanceState);  
    setContentView(R.layout.activity_note_view); 
    int position = getIntent().getIntExtra("position",200);
    Toast.makeText(getApplicationContext(), "position is " + position, Toast.LENGTH_SHORT).show();
    
    
  //Find the directory for the SD Card using the API
  //*Don't* hardcode "/sdcard"
  File sdcard = new File(Environment.getExternalStorageDirectory(), "Notes");

  //Get the text file
  File file = new File(sdcard,"bknote"+position+".txt");
  

  //Read text from file
  StringBuilder text = new StringBuilder();

  try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;

      while ((line = br.readLine()) != null) {
          text.append(line);
          text.append('\n');
      }
      br.close();
  }
  catch (IOException e) {
      //You'll need to add proper error handling here
  }

  //Find the view by its id
  TextView tv = (TextView)findViewById(R.id.text_view);

  //Set the text
  tv.setText(text);
      
     
  }
  
  public void editNotes(View view){
		Intent intent = new Intent(this, EditNote.class);
		startActivity(intent);
	}
  
  
  
  
  
  
} 
