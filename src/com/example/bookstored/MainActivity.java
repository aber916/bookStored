package com.example.bookstored;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity {

	protected Button _button;
	protected ImageView _image;
	protected TextView _field;
	protected String _path;
	protected boolean _taken;
	protected static final String PHOTO_TAKEN	= "photo_taken";
	private static final String TAG = "MainActivity.java";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/bookStored/";
	String FILENAME = "note.txt";

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}



		if (!(new File(DATA_PATH + "tessdata/" + "eng" + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/eng.traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/eng.traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				//while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				//gin.close();
				out.close();

				Log.v(TAG, "Copied " + "eng" + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + "eng" + " traineddata " + e.toString());
			}
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		_image = ( ImageView ) findViewById( R.id.image );
//		_field = ( TextView ) findViewById( R.id.field );
		_button = ( Button ) findViewById( R.id.button );
		_button.setOnClickListener( new ButtonClickHandler() );

		_path = DATA_PATH + "ocr.jpg";
	}

	public class ButtonClickHandler implements View.OnClickListener 
	{
		public void onClick( View view ){
			Log.i("MakeMachine", "ButtonClickHandler.onClick()" );
			startCameraActivity();
		}
	}
	public void viewNotes(View view){
	
		Intent intent = new Intent(this, NoteView.class);
		startActivity(intent);
		
	}
	
	public void editNotes(View view){
		
		Intent intent = new Intent(this, EditNote.class);
		startActivity(intent);
		
	}
	
	
	public void generateNoteOnSD(String sFileName, String sBody){
	    try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
	        if (!root.exists()) {
	            root.mkdirs();
	        }
	        File gpxfile = new File(root, sFileName);
	        FileWriter writer = new FileWriter(gpxfile);
	        writer.append(sBody);
	        writer.flush();
	        writer.close();
	        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
	    }
	    catch(IOException e)
	    {
	         e.printStackTrace();
//	         importError = e.getMessage();
//	         iError();
	    }
	   }  

	protected void startCameraActivity()
	{
		Log.i("MakeMachine", "startCameraActivity()" );
		File file = new File( _path );
		Uri outputFileUri = Uri.fromFile( file );
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
		intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );

		startActivityForResult(intent, 0 );
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{	
		Log.i( "MakeMachine", "resultCode: " + resultCode );
		switch( resultCode )
		{
		case 0:
			Log.i( "MakeMachine", "User cancelled" );
			break;

		case -1:
			onPhotoTaken();
			break;
		}
	}

	protected void onPhotoTaken()
	{
		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

		try {
			ExifInterface exif = new ExifInterface(_path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;

			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}

			Log.v(TAG, "Rotation: " + rotate);

			if (rotate != 0) {

				// Getting width & height of the given image.
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}

			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		// _image.setImageBitmap( bitmap );

		Log.v(TAG, "Before baseApi");

		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, "eng");
		baseApi.setImage(bitmap);

		String recognizedText = baseApi.getUTF8Text();

		baseApi.end();

		// You now have the text in recognizedText var, you can do anything with it.
		// We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
		// so that garbage doesn't make it to the display.
		Toast.makeText(getApplicationContext(), recognizedText, Toast.LENGTH_SHORT).show();
		
		Log.v(TAG, "OCRED TEXT: " + recognizedText);		
		recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
		recognizedText = recognizedText.trim();
		generateNoteOnSD(FILENAME, recognizedText);
		
//		if ( recognizedText.length() != 0 ) {
//			_field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
//			//_field.setSelection(_field.getText().toString().length());
//		}
	}

	@Override 
	protected void onRestoreInstanceState( Bundle savedInstanceState){
		Log.i( "MakeMachine", "onRestoreInstanceState()");
		if( savedInstanceState.getBoolean( MainActivity.PHOTO_TAKEN ) ) {
			onPhotoTaken();
		}
	}

	@Override
	protected void onSaveInstanceState( Bundle outState ) {
		outState.putBoolean( MainActivity.PHOTO_TAKEN, _taken );
	}
}