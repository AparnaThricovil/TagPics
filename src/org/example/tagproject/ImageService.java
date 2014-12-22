//Code for reference(the service structure) taken from Professor Puder's Android Bound Service tutorial
package org.example.tagproject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.example.helper.ConstantValues;
import org.example.helper.ImageItem;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

public class ImageService extends Service {
		
	private File path;
	Bitmap myBitmap;
	/**
	 * ImageService is an example of a bound service. The service will be
	 * automatically created when the first client (i.e., AddImagetoTagsActivity)
	 * connects and also automatically destroyed when the last client disconnects. A
	 * bound service needs to implement onBind() and return a so-called binder that
	 * the client can use to retrieve a reference to the service.
	 */
	

	    private HandlerThread           thread;
	    private ServiceHandler          serviceHandler;
	    private IBinder                 binder = new LocalBinder();
	    private ImageResultListener resultListener;

	    /**
	     * This example uses a local binder. The purpose of the binder is to return
	     * a reference to the service that the client can use to invoke methods on
	     * the service. A local binder can only be used by activities that run in
	     * the same process as the service.
	     */
	    public class LocalBinder extends Binder {

	        public ImageService getService() {
	            return ImageService.this;
	        }
	    }

	    /**
	     * The ServiceHandler performs the actual work 
	     * It is used by a Looper that is associated with a HandlerThread.
	     * The Looper will dispatch messages to this ServiceHandler.
	     */
	    private final class ServiceHandler extends Handler {

	        public ServiceHandler(Looper looper) {
	            super(looper);
	        } 

	        /**
	         * The looper will call handleMessage() when it dispatches a new
	         * message.
	         */
	        @Override
	        public void handleMessage(Message msg) {
	            String tag  = (String) msg.obj;
	            int option = msg.arg1;
	            //Log.v("Image service","tag value is"+tag);   
	            
	            final ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
        		path = Environment.getExternalStorageDirectory();
        		File dir = new File(path.getAbsolutePath() + "/TagPics");
        		
        		if(dir.exists()){
        			
        			String[] projection = {MediaColumns._ID, MediaColumns.DISPLAY_NAME, MediaColumns.DATE_ADDED, MediaColumns.DATA};
        			String folder = ConstantValues.FOLDERNAME;
        			folder = folder + "%";
        			String[] whereArgs = new String[]{folder};
        			/*
        			 * public static final Cursor query (ContentResolver cr, Uri uri, String[] projection, 
        			 * String where, String orderBy). MediaStore.Images.Media.EXTERNAL_CONTENT_URI refers to URI for the "primary" 
        			 * external storage volume. MediaColumns.DATA refers to the data stream for the file 
        			 */
        			Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection,MediaStore.Images.Thumbnails.DATA + " like ? ",whereArgs,MediaColumns.DATA);
        			String imagePath=null;
        			/*
        			 * Create a default Options object, which if left unchanged will give the same result from the decoder 
        			 * as if null were passed. 
        			 */
        			BitmapFactory.Options options=new BitmapFactory.Options();
        			/*
        			 * The sample size is the number of pixels in either dimension that correspond to a single pixel in the 
        			 * decoded bitmap. For example, inSampleSize == 4 returns an image that is 1/4 the width/height of the 
        			 * original, and 1/16 the number of pixels. Any value <= 1 is treated the same as 1. Note: the decoder 
        			 * uses a final value based on powers of 2, any other value will be rounded down to the nearest power of 2. 
        			 */
					options.inSampleSize = 4;
					
        			if (cursor.getCount()>0) {
        				/*
        				 *  getColumnIndexOrThrow(String columnName)
        				 *	Returns the zero-based index for the given column name, or throws IllegalArgumentException 
        				 *	if the column doesn't exist. 
        				 */
        				int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);	
        				
        				while(cursor.moveToNext()){
        					
        					imagePath = cursor.getString(columnIndex);
        					/*
        					 * This is a class for reading and writing Exif tags in a JPEG file. 
        					 */
        					ExifInterface exif;
        					
        					try {
        						
        						exif = new ExifInterface(imagePath);
        						/*
        						 * Returns the value of the specified tag or null if there is no such tag in the JPEG file. 
        						 * "UserComment" is the tag used in my application to enter tag details of images.
        						 */
        						exif.getAttribute("UserComment");
        						String imageTag = exif.getAttribute("UserComment");
        						Log.v("***********","imagetag is"+imageTag);
        						Log.v("***********","tag is"+tag);
        						Log.v("***********","option is"+option);
        						
        						
        						//Case where images mapped under a tag is displayed(displaying images under tags)
        						if((tag.equalsIgnoreCase(imageTag) && option ==1) ||
        								
        								//Show images which are not tagged (Add images screen display images from gallery)
        								(imageTag.equalsIgnoreCase("na") && option ==3) ||
        								
        								//Get images that match the tag to delete the image exif data
        								(tag.equalsIgnoreCase(imageTag) && option ==2) ||
        								
        								//Add exif tag to the image(while actually adding images) 
        								(option==4) || (tag.equalsIgnoreCase(imageTag) && option ==5) || (option ==6)) {
        							
        							myBitmap = BitmapFactory.decodeFile(imagePath, options);
        							imageItems.add(new ImageItem(myBitmap,imagePath));
        						}
        						
     			
        					} catch (IOException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        					}
        					
        				}
        			}
        		}
        		
	            /*
	             * If the resultListener is null it means that the activity that
	             * triggered the getImages has been
	             * destroyed in the meantime. In this case simply discard the
	             * result.
	             */
	            if (resultListener != null) {
	                /*
	                 * Report the result back to the activity that triggered the
	                 * computation. Note that this callback happens in the context
	                 * of a HandlerThread and not the UI thread.
	                 */
	                resultListener.imageAvailable(imageItems,option);
	            }
	        }
	    }

	    @Override
	    public void onCreate() {
	        super.onCreate();
	        //Log.v("ImageService", "in onCreate");
	     
	        /*
	         * Create a new HandlerThread. A HandlerThread is simply a Java Thread
	         * with an associated Looper.
	         */
	        thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
	        thread.start();

	        /*
	         * Create the ServiceHandler and pass to it the Looper of the
	         * HandlerThread.
	         */
	        serviceHandler = new ServiceHandler(thread.getLooper());
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        /*
	         * Calling quit() will cause the Looper to exit at the next possible
	         * moment. This will also terminate the HanderThread.
	         */
	        thread.quit();
	        resultListener = null;
	    }

	    /**
	     * Implementing onBind() marks this service as a bound service that other
	     * clients can bind to.
	     */
	    @Override
	    public IBinder onBind(Intent arg0) {
	        return binder;
	    }

	    /**
	     * getImages() is a public method that will be called by
	     * AddImagtoTagsActivity to trigger obtaining all images. Note
	     * that this method merely queues a new message with the ServiceHandler. The
	     * actual computation happens later when the Looper dispatches the message.
	     */
	    public void getImages(String tagvalue,int option) {
	        /*
	         * Get the new message.
	         */
	    	//Log.v("ImageService", "in getImages"+tagvalue);
	        Message msg = serviceHandler.obtainMessage();
	        /*
	         * Get the tagvalue and option and pass it into the 
	         * message
	         */
	        msg.obj = tagvalue;
	        msg.arg1 = option;
	        /*
	         * Enqueue the message.
	         */
	        serviceHandler.sendMessage(msg);
	    }

	    public void registerResultListener(ImageResultListener listener) {
	        resultListener = listener;
	    }
} 
