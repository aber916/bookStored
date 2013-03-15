package com.example.bookstored;

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle; 
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

@SuppressLint("CutPasteId")
public class ListNotesView extends Activity implements OnItemClickListener{
	ListView listview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_notes_view);
		// Show the Up button in the action bar.
		setupActionBar();

		listview = (ListView) findViewById(R.id.noteList);
		viewNoteAdapter mainAdapt = new viewNoteAdapter(this, R.layout.listheader, getNoteTitles());
		listview.setAdapter(mainAdapt);
		listview.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
		ArrayList<simpleNote> currentList = getNoteTitles();
		simpleNote currentNote = currentList.get(position);
		Intent intent = new Intent();
		intent.setClass(this, NoteView.class);
		intent.putExtra("position", position);
		intent.putExtra("title", currentNote.title+".txt");
		startActivity(intent);
	}



	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	public void viewNote(View view){
		Intent intent = new Intent(this, NoteView.class);
		startActivity(intent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_notes_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private String getFileInfo(String label, File file){ //finds the name of desired label
		String labelName="space!";
		String labelValue= new String();
		String line="";
		StringBuilder text = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
				if(label.equals("title")){
					if(line.contains("'Title'")){
						labelValue=line.replace("'Title': ", "");				
						br.close();
						return labelValue;
					}
				}

				if(label.equals("author")){
					if(line.contains("'Author'")){
						labelValue=line.replace("'Author': ", "");				
						br.close();
						return labelValue;
					}
				}
				if(label.equals("pgnum")){
					if(line.contains("'Page Number'")){
						labelValue=line.replace("'Page Number': ", "");			
						br.close();
						return labelValue;
					}
				}
			}
			br.close();
		}
		catch (IOException e) {
		}
		return labelName;
	}

	private ArrayList<simpleNote> getNoteTitles(){
		int i;
		ArrayList<simpleNote> notes = new ArrayList<simpleNote>();
		File mfile=new File(Environment.getExternalStorageDirectory(), "Notes"); //open folder 
		File[] list=mfile.listFiles();   //put list of files in File[]
		System.out.println("list"+mfile.listFiles().length);
		for(i=0;i<mfile.listFiles().length;i++){   		  //gets names of files and puts them in ListView

			simpleNote cNote = new simpleNote();
			//			System.out.println("hidden path files.."+list[i].getAbsolutePath());
			System.out.println(list[i].getName());
			cNote.title = list[i].getName().replaceAll(".txt", "");
			cNote.noteTitle = getFileInfo("title", list[i]);
			cNote.author= getFileInfo("author",list[i]);
			cNote.pgNum = getFileInfo("pgnum", list[i]);
			notes.add(cNote);
		}
		return notes;
	}

	public class simpleNote{
		String title=" ";
		String author=" ";
		String dateCreated=" ";
		String noteTitle=" ";
		String pgNum=" ";
	}

	public class viewNoteAdapter extends ArrayAdapter<simpleNote> {
		public ArrayList<simpleNote> noteList;
		public viewNoteAdapter(Context context, int textViewResourceId, ArrayList<simpleNote> notes){
			super(context, textViewResourceId, notes);
			this.noteList = notes;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) { //take Twitter feed and display on twitterfeed.xml
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService                        
						(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.listheader, null);
			}
			simpleNote currentNote = noteList.get(position);
			if(!currentNote.title.contentEquals(" ")){
				TextView fTitle = (TextView) v.findViewById(R.id.fileTitle);
				fTitle.setText(currentNote.title);
				fTitle.setTextSize(20);
				fTitle.setVisibility(View.VISIBLE);}

			if(!currentNote.noteTitle.equals("space!")){
				TextView btitle = (TextView) v.findViewById(R.id.fileTitle);
				btitle.setText(currentNote.noteTitle);
				btitle.setTextSize(20);
				btitle.setVisibility(View.VISIBLE);}

			if(!currentNote.author.equals("space!")){
				TextView fAuthor = (TextView) v.findViewById(R.id.fileAuthor);
				fAuthor.setText("Author: "+currentNote.author);
				fAuthor.setTextSize(12);
				fAuthor.setVisibility(View.VISIBLE);}

			if(!currentNote.pgNum.equals("space!")){
				TextView pgNum = (TextView) v.findViewById(R.id.pageNumber);
				pgNum.setText("Page Number: "+currentNote.pgNum);
				pgNum.setTextSize(12);
				pgNum.setVisibility(View.VISIBLE);}

			return v;
		} 
	}
}


