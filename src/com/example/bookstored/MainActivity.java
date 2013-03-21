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

	protected int fileCounter=1;
	protected Button _button;
	protected ImageView _image;
	protected TextView _field;
	protected String _path;
	protected boolean _taken;
	protected static final String PHOTO_TAKEN	= "photo_taken";
	private static final String TAG = "MainActivity.java";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/bookStored/";
	String FILENAME = "bkNote";

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
				out.close();

				Log.v(TAG, "Copied " + "eng" + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + "eng" + " traineddata " + e.toString());
			}
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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

	public void viewNoteList(View v){

		Intent intent = new Intent(this, ListNotesView.class);
		startActivity(intent);

	}

	public void generateNoteOnSD(String sFileName, String sBody){ //create text file in "Notes" folder of the app
		try
		{
			File root = new File(Environment.getExternalStorageDirectory(), "Notes");
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, sFileName);
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(sBody+"\n");
			writer.flush();
			writer.close();
			Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}  



	public String generateFilename(){ //create unique file name to prevent overwritting

		File mfile=new File(Environment.getExternalStorageDirectory(), "Notes");

		int hack = mfile.listFiles().length+1;
		String outFile = FILENAME + hack + ".txt";
		return outFile;

	}
	protected void startCameraActivity() //initiate local camera
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

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options); //decode image from given path and turn into Bitmap for further conversion

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
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false); //adjust orientation of image if characters are incorrectly oriented
			}

			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		Log.v(TAG, "Before baseApi");

		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, "eng"); //convert image to text using OCR
		baseApi.setImage(bitmap);

		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();


		Log.v(TAG, "OCRED TEXT: " + recognizedText);		
		recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
		recognizedText = recognizedText.trim();
		String fName = generateFilename();

		System.out.println("fName at onpicture taken= "+fName);
		generateNoteOnSD(fName, recognizedText); //attach created file with content
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