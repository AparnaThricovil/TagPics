package org.example.tagproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;

import org.example.helper.DatabaseAccess;
import org.example.helper.GridViewAdapter;
import org.example.helper.ImageItem;
import org.example.helper.ImageResultListener;
import org.example.helper.ImageService;
//import org.example.helper.MyRecyclerAdapter;
//import org.example.tagproject.AddImagetoTagsActivity.ImageServiceConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.example.helper.*;

public class GalleryActivity extends Activity implements ImageResultListener {
	
		DatabaseAccess db;
		ArrayList<Bitmap> allImagePaths = new ArrayList<Bitmap>();
		Bitmap myBitmap;
		private ImageView imgView;
		private GridView gridView;
		private GridViewAdapter customGridAdapter;
		//private MyRecyclerAdapter customRecycleAdapter;
		private ImageService imageService;
	    private boolean          bound;
	    private boolean          computationPending = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* Allow activity to show indeterminate progress-bar */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        Log.v("GalleryActivity","in onCreate");
        //setContentView(R.layout.activity_gallery);
        setContentView(R.layout.activity_gallery);
        gridView = (GridView) findViewById(R.id.gridView);
        bindToImageService();
		callImageService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
		case R.id.home:
			Intent intentList = new Intent(this, HomeActivity.class);
	    	startActivity(intentList);
			return true;
			
		case R.id.camera:
			Intent intentCamera = new Intent(this, CameraActivity.class);
	    	startActivity(intentCamera);
			return true;
			
		case R.id.taglist:
			Intent intentMap = new Intent(this, TagActivity.class);
	    	startActivity(intentMap);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}
	

	 final private class ImageServiceConnection implements ServiceConnection {

	        /**
	         * onServiceConnected will be called whenever the connection to a
	         * service has been established. The parameters are the class name of
	         * the service and a reference to the IBinder interface. The latter can
	         * be used to retrieve the FibonacciService instance.
	         */
	        @Override
	        public void onServiceConnected(ComponentName className, IBinder service) {
	            ImageService.LocalBinder binder = (ImageService.LocalBinder) service;
	            /*
	             * Retrieve the ImageService instance and save it. The reference
	             * to this instance can be used to invoke methods on the service.
	             */
	            imageService = binder.getService();
	            bound = true;
	            /*
	             * Register the current activity as a callback that the service will
	             * use to report back results when they are ready.
	             */
	            imageService.registerResultListener(GalleryActivity.this);
	            if (computationPending) {
	                /*
	                 * If computationPending is true it means that the user called 
	                 * callImageService for obtaining the image. Since we are
	                 * bound now, start getting the images by calling the getImages of the ImageService.
	                 */
	                computationPending = false;
	                imageService.getImages(ConstantValues.GALLERY_ACTIVITY_TAG,6);
	            }
	        }

	        /**
	         * Callback that notifies us that the connection to the service was
	         * disconnected. This can happen for example when memory is low and
	         * Android decides to kill the service.
	         */
	        @Override
	        public void onServiceDisconnected(ComponentName arg0) {
	        	imageService = null;
	            bound = false;
	        }
	    };

	    private ServiceConnection connection = new ImageServiceConnection();

	
	
	@Override
   public void onResume() {
       super.onResume();
       callImageService();
   }
	
	//Show Alert when a tag is deleted.
	public void showAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Delete Tag")
               .setMessage("Do you want to delete the tag?")
               .setIcon(android.R.drawable.ic_dialog_alert);
       AlertDialog dlg = builder.create();
       dlg.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
    	   /*
       	 * If yes is selected then the tag is deleted. The tag associated with the image is set to "na"
       	 * The image is available for tagging from other tags.
       	 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//delete tag
				imageService.getImages(ConstantValues.GALLERY_ACTIVITY_TAG,6);
			}

           
       });
       /*
        * If "No" is selected, then cancel the alert dialog, since the user does not want to 
        * delete the tag.
        */
       //do not delete the tag
       dlg.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
               new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });
       
       dlg.show();
	}
	

       public void callImageService() {
   		//Log.v("Addimage", "in callImageService");
           try {
     
               if (bound) {
               	//Log.v("Addimage", "in callImage service-> in if of bound");
                   /*
                    * We are already bound to the service. Simply call the public
                    * method getImages() to trigger the images
                    */
                   imageService.getImages(ConstantValues.GALLERY_ACTIVITY_TAG,6);
               } else {
                   /*
                    * We are currently not bound to the service. Set the flag
                    * computationPending to true to ensure that the getting images
                    * will be triggered once we are bound to the service. Then bind
                    * to the service.
                    */
                   computationPending = true;
                   bindToImageService();
               }
           } catch (Exception exc) {
           	exc.printStackTrace();
           }
       }

       private void bindToImageService() {
       	//Log.v("Addimage", "in bindToImageService");
           if (!bound) {
               /*
                * Binding to a service happens via an Intent. If the service isn't
                * already running, it will be created. Binding to a service happens
                * asynchronously and the 'connection' object is used to notify when
                * the binding has succeeded.
                */
           	//Log.v("Addimage", "in bindToImageService inside if");
               Intent intent1 = new Intent(this, ImageService.class);            
               bindService(intent1, connection, Context.BIND_AUTO_CREATE);
           }
           //Log.v("Addimage", "outside if bindToImageService");
       }
       @Override
       protected void onDestroy() {
           super.onDestroy();

           if (bound) {
               /*
                * When the activity gets destroyed, we also unbind from the
                * service. Since this activity is the only client to bind to
                * FibonacciService, the service will be destroyed as well.
                */
               unbindService(connection);
               bound = false;
           }
           //gridView.
           //bitmap.recycle();
       }
   	@Override
   	public void imageAvailable(final ArrayList<ImageItem> images,int option){
   		/*if the selected option was to delete tag, then we need to return to 
   		 * the listing of tags view. The exif data of the image is set to "na"
   		 */
   		/*if(option==2){
   			
   			for (ImageItem item : images){
   			ExifInterface exif;
   			
   			try {
   				
   				exif = new ExifInterface(item.getFileName());
   				exif.setAttribute("UserComment","na");
   				exif.saveAttributes();
   		
   			} catch (IOException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   			}
   			}
   			db.deleteTag(tag);
   			Intent intentList = new Intent(this, TagActivity.class);
   	    	startActivity(intentList);
   		}*/
   		/*
   		 * When the service returns update the gridview.
   		 */
   		gridView.post(new Runnable() {

   	            @Override
   	            public void run() {
   	            	/*
   	            	 * If no images were returned then display "no photo available" image
   	            	 */
   	            	if(images.isEmpty())
   	                {
   	                	imgView=(ImageView) findViewById(R.id.imageView1);
   	                	imgView.setImageResource(R.drawable.noimage);
   	                }
   	            	Log.v("GalleryActivity","in display function");
   	            	customGridAdapter = new GridViewAdapter(GalleryActivity.this, R.layout.row_grid, images);
	        		gridView.setAdapter(customGridAdapter);
   	            }
   	        });
   	}
   	
   	//public void onDestroy()
}
