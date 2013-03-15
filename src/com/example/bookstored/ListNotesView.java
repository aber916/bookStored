package com.example.bookstored;

import java.io.File;
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
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

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
		//intent.putExtra("name", R.id.)
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
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private ArrayList<simpleNote> getNoteTitles(){
		int i;
		ArrayList<simpleNote> notes = new ArrayList<simpleNote>();
		File mfile=new File(Environment.getExternalStorageDirectory(), "Notes");
		File[] list=mfile.listFiles();

		System.out.println("list"+mfile.listFiles().length);
		for(i=0;i<mfile.listFiles().length;i++){
			simpleNote cNote = new simpleNote();
//			System.out.println("hidden path files.."+list[i].getAbsolutePath());
			System.out.println(list[i].getName());

			cNote.title = list[i].getName().replaceAll(".txt", "");
//			cNote.bookTitle = "bookTitle " +x;
//			cNote.author= "author "+x;
//			cNote.pgNum = "pageNumber "+x;
			notes.add(cNote);
		}
		return notes;
	}


	public class simpleNote{
		String title;
		String author;
		String dateCreated;
		String bookTitle;
		String pgNum;
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
			TextView fTitle = (TextView) v.findViewById(R.id.fileTitle);
			TextView btitle = (TextView) v.findViewById(R.id.bookTitle);
			TextView fAuthor = (TextView) v.findViewById(R.id.fileAuthor);
			TextView pgNum = (TextView) v.findViewById(R.id.pageNumber);

			fTitle.setText(currentNote.title);
			btitle.setText(currentNote.bookTitle);
			fAuthor.setText(currentNote.author);
			pgNum.setText(currentNote.pgNum);

			return v;
		}
	}
}


