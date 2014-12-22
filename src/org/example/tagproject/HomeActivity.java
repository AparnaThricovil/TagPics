/**
 * 
 * @author Aparna 
 * 
 */
/* Displays the last image taken. If no mage found then displays 
 * an image from the res/drawable folder
 * 
 */
package org.example.tagproject;

import java.io.File;

import org.example.helper.ConstantValues;
import org.example.tagproject.CameraActivity;

import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class HomeActivity extends FragmentActivity {
	
	private File path;
	ImageView imgView; 
	String tag;
	String imagePath=null;
	//private FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		//Obtain the path of directory.
		path = Environment.getExternalStorageDirectory();
		File dir = new File(path.getAbsolutePath() + "/TagPics");
		if(dir.exists()){
			
			String[] projection = {MediaColumns._ID, MediaColumns.DISPLAY_NAME, MediaColumns.DATE_ADDED, MediaColumns.DATA};
			String folder = ConstantValues.FOLDERNAME;
			folder = folder + "%";
			String[] whereArgs = new String[]{folder};
			Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection,MediaStore.Images.Media.DATA + " like ? ",whereArgs,MediaColumns.DATA);
			
			imgView = (ImageView) findViewById(R.id.imageView1);
			
			if (cursor.getCount()>0) {
				
				//Log.v("Home", "cursor not null");
				int columnIndex1 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				//Log.v("Home", "after data");
				/*
				 * Obtain the last image that was captured.
				 * Get the imagePath and find the bitmap from the imagePath. 
				 * Display the image in an imageview
				 */
				cursor.moveToLast();
				imagePath = cursor.getString(columnIndex1);
				Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
				ExifInterface exif;
				
				try {
					
					exif = new ExifInterface(imagePath);
					/*
					 * Returns the value of the specified tag or null if there is no such tag in the JPEG file. 
					 * "UserComment" is the tag used in my application to enter tag details of images.
					 */
					tag = exif.getAttribute("UserComment");
				}
				catch(Exception e){
					
				}
				imgView.setImageBitmap(myBitmap);
	    	}
			else {
				
				//imgView = (ImageView) findViewById(R.id.imageView1);
				imgView.setImageResource(R.drawable.noimage);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
		case R.id.listview:
			Intent intentList = new Intent(this, TagActivity.class);
	    	startActivity(intentList);
			return true;
			
		case R.id.camera:
			Intent intentCamera = new Intent(this, CameraActivity.class);
	    	startActivity(intentCamera);
			return true;
			
		case R.id.mapview:
			Intent intentMap = new Intent(this, MapActivity.class);
			intentMap.putExtra("tagSelected", tag);
			intentMap.putExtra("imagefile", imagePath);
	    	startActivity(intentMap);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}
		
}
