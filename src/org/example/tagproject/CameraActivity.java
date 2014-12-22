/*
 * Code for implementation of camera functionality taken from
 *  Professor Arno Puder's Camera Tutorial 
*/
package org.example.tagproject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.example.helper.ConstantValues;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

	/**
	 * 
	 * This application demonstrates the use of camera in android. On creation of
	 * the activity, the camera preview starts which allows the user to preview
	 * before capturing an image. The image can be captured using the button that is
	 * provided. On click of the button the callback method
	 * <code> PictureCallback() </code> is called. The callback handles storing the
	 * captured image in jpeg format into the external storage device. A SurfaceView
	 * is created for the camera preview. The Activity implements the
	 * SurfaceHolder.Callback to get notifications about the changes in the surface.
	 * 
	 * The CAMERA permissions need to be declared in the AndroidManifest.xml file in
	 * order to access the camera device. <uses-feature> element is used to declare
	 * the features of the camera that will be used by the application.
	 */

	public class CameraActivity extends Activity implements SurfaceHolder.Callback {

	    protected Camera        camera;
	    protected SurfaceView   surfaceView;
	    protected SurfaceHolder surfaceHolder;
	    protected ImageButton   cameraButton;
	    private File            path;
	    LocationManager locationManager;
	    protected String fileName;
	   // private GoogleApiClient mGoogleApiClient;
	    //private String filePath;
	    private String filepath;

	    @Override
	    public void onCreate(Bundle icicle) {

	        super.onCreate(icicle);

	        /*
	         * Set the camera preview to be full screen without the notification bar
	         */
	        getWindow().setFormat(PixelFormat.TRANSLUCENT);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        setContentView(R.layout.activity_camera);

	        /*
	         * Get the external storage directory path Generally /mnt/sdcard/
	         */
	        path = Environment.getExternalStorageDirectory();
	        
	        /*
	         * Get the SurfaceView which is defined in the resources file using the
	         * resource ID
	         */
	        surfaceView = (SurfaceView) findViewById(R.id.surfaceCamera);

	        /*
	         * Surface holder allows us to control the changes to the surface. A
	         * Callback interface is registered in order to control changes to the
	         * surface.
	         */
	        surfaceHolder = surfaceView.getHolder();
	        surfaceHolder.addCallback(this);
	        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	        /*
	         * A Button is provided, on click which the image is captured An image
	         * button is used here which allows to specify an image for the button
	         * instead of plain text. The button is defined in the resource file is
	         * referenced using the resource ID.
	         */
	        cameraButton = (ImageButton) findViewById(R.id.cameraButton);    
	    }
	    
	    /**
	     * Get the image and save it to Google Drive.
	     */
	 //   private void saveFileToDrive(final String path/*,final File outputFile*/) {
	    	
	    /*	new AsyncTask<String, Void, Void>() {
	    		
	    		@Override
				protected Void doInBackground(String... params) {
	    			
	              	//Log.v("GoogleDRiveActivity","saveFileToDrive1");
	              	//Log.v("TAG", "Creating new contents.value of imahge is::::"+path);
	              	
	              	final Bitmap image =  BitmapFactory.decodeFile(path);
	              	
	             // Start by creating a new contents, and setting a callback.
	              	
	              	Drive.DriveApi.newDriveContents(mGoogleApiClient)
	                .setResultCallback(new ResultCallback<DriveContentsResult>() {

	                	@Override
	                	public void onResult(DriveContentsResult result) {
	                		
	                		/*
	                		 * If the operation was not successful , then we cannot
	                		 *  proceed and hence we return
	                		 */
	                		
	                		//Log.v("GoogleDRiveActivity","saveFileToDrive2");
	                		
	                /*		if (!result.getStatus().isSuccess()) {

	                			return;
	                		}
	                
	                /*
	                 * Otherwise, we can write the data to the new contents.
	                 * Get an output stream for the contents.
	                 */
	             /*   OutputStream outputStream = result.getDriveContents().getOutputStream();
	                // Write the bitmap data from it.
	                ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
	                image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
	                try {
	                	//Log.v("GoogleDRiveActivity","saveFileToDrive3");
	                    outputStream.write(bitmapStream.toByteArray());
	                } catch (IOException e1) {
	                	e1.printStackTrace();
	                    //Log.i(TAG, "Unable to write file contents.");
	                }*/
	                
	                /*
	                 *Create the initial metadata - MIME type and title.
	                 *User will be prompted to change these later. 
	                 * A collection of metadata changes. Any fields with 
	                 * null values will retain their current value. 
	                 */
	                
	                
	          /*      MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
	                        .setMimeType("image/jpeg").setTitle("TagPics.png").build();
	               
	                // Create an intent for the file chooser, and start it.
	               IntentSender intentSender = Drive.DriveApi
	                        .newCreateFileActivityBuilder()
	                        .setInitialMetadata(metadataChangeSet)
	                        .setInitialDriveContents(result.getDriveContents())
	                        .build(mGoogleApiClient);
	                Log.v("GoogleDRiveActivity","saveFileToDrive4");
	               try {
	                    startIntentSenderForResult(
	                            intentSender, ConstantValues.REQUEST_CODE_CREATOR, null, 0, 0, 0);
	                    Log.v("GoogleDRiveActivity","saveFileToDrive5");
	                } catch (SendIntentException e) {
	                	e.printStackTrace();
	                	// Log.i(TAG, "Failed to launch file chooser.");
	                }
	            }
	        });
	       // finish();
			return null;
	    }
	   }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,filePath);
	   camera.startPreview();
	    } 	
	    
	    @Override
	    protected void onResume() {
	    	Log.v("GoogleDRiveActivity","onResume");
	    	super.onResume();
	    	if (mGoogleApiClient == null) {
	    		/*
	    		 * Create the API client and bind it to an instance variable.
	             * We use this instance as the callback for connection and connection
	             * failures. If no account name is passed, the user is prompted to choose.
	    		 * 
	    		 */

	        /*    mGoogleApiClient = new GoogleApiClient.Builder(this)
	                    .addApi(Drive.API)
	                    .addScope(Drive.SCOPE_FILE)
	                    .addConnectionCallbacks(this)
	                    .addOnConnectionFailedListener(this)
	                    .build();
	    	}
	        // Connect the client.
	        mGoogleApiClient.connect();
	       // camera.startPreview();
	    } */

	 /*   @Override
	    protected void onPause() {
	    	//Start the camera preview again
	    	camera.startPreview();
	    	Log.v("GoogleDRiveActivity","onPause");
	        if (mGoogleApiClient != null) {
	            mGoogleApiClient.disconnect();
	            
	        }
	        super.onPause();
	        
	    }

	   @Override
	    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
	    	
	    	Log.v("GoogleDRiveActivity","onActivityResult");
	    	//Start the camera preview again
	    	camera.startPreview();
	    } 
	   	/*
	   	 * If the user has not previously authorized the application, the onConnectionFailed() 
	   	 * callback is invoked. The callback should check for the presence of a pending intent 
	   	 * for resolving the error and start the activity. The user will be prompted to authorize 
	   	 * the application to access their files in Drive.
	   	 */
	/*    @Override
	    public void onConnectionFailed(ConnectionResult result) {
	        // Called whenever the API client fails to connect.
	       // Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
	        if (!result.hasResolution()) {
	            // show the localized error dialog.
	            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
	            return;
	        }
	        
	        /*
	         *If the failure has a resolution then we need to resolve it. 
	         *Called typically when the app is not yet authorized, and an
	         *authorization dialog is displayed to the user.
	         */
	  /*      try {
	        	Log.v("GoogleDRiveActivity","onConnectionFailed");
	            result.startResolutionForResult(this, ConstantValues.REQUEST_CODE_RESOLUTION);
	        } catch (SendIntentException e) {
	            e.printStackTrace();
	        	
	        }
	    }

	    @Override
	    public void onConnected(Bundle connectionHint) {
	    	
	    }

	    @Override
	    public void onConnectionSuspended(int cause) {
	        //Log.i(TAG, "GoogleApiClient connection suspended");
	    }
	    

	    /*
	     * This method is called when the button is clicked. On click of the button,
	     * the takePicture() is called which initiates a series of Callbacks for
	     * image capture. A Shutter Callback occurs close to the moment of image
	     * capture.A raw Callback occurs when raw image data is available. A jpeg
	     * Callback occurs when compressed image is available. A null can be passed
	     * when a callback is not required.
	     */
	    public void onClick(View v) {
	        camera.takePicture(shutterCallback, null, pictureCallback);
	        
	    }

	    /*
	     * This callback can be used to play shutter sound and such since this
	     * callback method is called when the image is captured by the sensor
	     */
	    ShutterCallback        shutterCallback = new ShutterCallback() {
	                                               @Override
	                                               public void onShutter() {
	                                                   Log.v("Camera", "picturecallback");
	                                               }
	                                           };

	    /*
	     * The image data that is available after image capture is supplied using
	     * this callback interface. The image data is available in form of bytes[],
	     * the format of which depends on the context of the callback. This can be
	     * converted to any formats using Bitmap or BitmapFactory classes.
	     */
	    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
	                                               @Override
	                                               public void onPictureTaken(byte[] imageData, Camera c) {

	                                                   /*
	                                                    * This callback method is
	                                                    * called when the data is
	                                                    * available after capture of
	                                                    * the image.
	                                                    */
	                                                   Log.v("Camera", "picturecallback");
	                                                   FileOutputStream outStream = null,newoutStream = null;
	                                                   try {
	                                                       File dir = new File(path.getAbsolutePath() + "/TagPics");
	                                                	   dir.mkdirs(); 
	                                                	   final String fileName = String.format("img%d.jpg", System.currentTimeMillis());
	                                       				   filepath=path.getAbsolutePath() + "/TagPics/"+fileName;
	                                                	   Log.v("camera-back", "going to save");
	                                                	   File outFile = new File(dir, fileName);
	                                                       outStream = new FileOutputStream(outFile);
	                                                       newoutStream = new FileOutputStream(outFile);
	                                                       outStream.write(imageData);
	                                                       outStream.flush();
	                                                       outStream.close();
	                                                       /*
	                                                        * Rotating the image taken in portrait mode to 
	                                                        * original orientation and saving the latitude-longitude 
	                                                        * information
	                                                        */
	                                                       
	                                                       rotateandSaveExif(filepath,newoutStream,outFile);
	                                                      
	                                                     
	                                                   } catch (FileNotFoundException e) {
	                                                       e.printStackTrace();
	                                                   		} catch (IOException e) {
	                                                       e.printStackTrace();
	                                                   		}
	                                                   camera.startPreview();
	                                               }
	                                           };
	                                           
	    /*
	     * This callback is called immediately after the surface is first created.
	     * Obtain an instance of the camera when the surface is created.
	     */
	    @Override
	    public void surfaceCreated(SurfaceHolder holder) {
	        camera = Camera.open();
	    }

	    /*
	     * This callback is called when there are any structural changes to the
	     * surface.
	     */
	    @Override
	    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	        View root = findViewById(R.id.root);
	        camera.stopPreview();

	        /*
	         * Camera parameters can be modified by modifying the object that is
	         * returned by getParameters()
	         */
	        Camera.Parameters p = camera.getParameters();

	        /*
	         * Determine supported preview sizes and and set the preview size which
	         * best matches the device's screen resolution.
	         */
	        List<Size> supportedSizes = p.getSupportedPreviewSizes();
	        Size previewSize = determinePreviewSize(supportedSizes, root.getWidth(), root.getHeight());

	        /*
	         * Switch camera to portrait mode. TODO: Handle upside down orientations
	         * properly
	         */
	        if (root.getWidth() < root.getHeight()) {
	            camera.setDisplayOrientation(90);
	        }
	        p.setPreviewSize(previewSize.width, previewSize.height);

	        p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
	        camera.setParameters(p);

	        try {
	            /*
	             * setPreviewDisplay() must be called before calling startPreview()
	             * This method sets the surface required for the camera preview. A
	             * fully initialized SurfaceHolder must be passed to this method.
	             */
	            camera.setPreviewDisplay(holder);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        /*
	         * Layout the surfaceView correctly. It might not cover the whole
	         * screen, depending in the screen's and camera's aspect ratio.
	         */
	        float previewAspectRatio = (float) (previewSize.width) / previewSize.height;
	        if (root.getWidth() > root.getHeight()) {
	            surfaceView.layout(0, 0, (int) (root.getHeight() * previewAspectRatio),
	                    root.getHeight());
	        } else {
	            surfaceView.layout(0, 0, root.getWidth(), (int) (root.getWidth() * previewAspectRatio));
	        }
	        
	        /*
	         * Starts the live preview required for camera
	         */
	        camera.startPreview();
	    }

	    /*
	     * This callback is called just before the surface is being destroyed. Since
	     * camera is a shared resource it is good practice to release the resource
	     * when not using it.
	     */
	    @Override
	    public void surfaceDestroyed(SurfaceHolder holder) {
	        Log.e("Camera", "surfaceDestroyed");
	        camera.stopPreview();
	        camera.release();
	    }

	    /**
	     * Determine preview size based on the supported preview sizes. The preview
	     * size is the maximum preview size supported which is less or equal to the
	     * device's screen resolution. To test the best matching size both portrait
	     * and landscape screen orientations are tested.
	     * 
	     * @param sizes
	     *            The preview sizes supported by the device's camera.
	     * 
	     * @param width
	     *            The device's screen width.
	     * 
	     * @param height
	     *            The device's screen height.
	     * 
	     * @return The determined preview size.
	     */
	    private Size determinePreviewSize(List<Size> sizes, int width, int height) {
	        if (height > width) {
	            int temp = width;
	            width = height;
	            height = temp;
	        }

	        for (Size s : sizes) {
	            if (s.width <= width && s.height <= height) {
	                return s;
	            }

	            if (s.width <= height && s.height <= width) {
	                return s;
	            }
	        }
	        return null;
	    }
	    /*
	     * Switch to home activity when Home button is clicked.
	     */
	    public void onClickHome(View v) {
	    	Intent intent = new Intent(this, HomeActivity.class);
	    	startActivity(intent);
	    } 
	    /*
	     * Images taken with some devices in portrait mode gets automatically rotated.
	     * This image needs to be rotated back to the original shape. this method also 
	     * takes care of adding values to the exif tags.
	     */
	    public void rotateandSaveExif(String filepath, FileOutputStream outputStream, File outfile){
	    	//saveFileToDrive(filepath/*,outfile*/);
	    	final FileOutputStream newoutStream=outputStream;
	    	final File outputfile= outfile;
	    	
	    	new AsyncTask<String, Void, Void>() {

				@Override
				protected Void doInBackground(String... params) {
					//FileOutputStream newoutStream = null;
					String filepath1 = params[0];
					//newoutStream = params[1];
					Bitmap myBitmap = BitmapFactory.decodeFile(filepath1);
					//Log.v("CameraActivity","before----->value of file path is "+filepath1);
					   Matrix matrix = new Matrix();
					   matrix.postRotate(90);
					   Bitmap rotatedbitmap = Bitmap.createBitmap(myBitmap,0,0,myBitmap.getWidth(),myBitmap.getHeight(),matrix,true);
					   ByteArrayOutputStream stream = new ByteArrayOutputStream();
					   rotatedbitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					   byte[] byteArray = stream.toByteArray();
					   //refreshGallery(outFile);
		      
					   try {
						   Log.v("in try","trying to rotate and save image");
						   newoutStream.write(byteArray);
						   newoutStream.flush();
						   newoutStream.close(); 
					   } catch (IOException e) {
						   // TODO Auto-generated catch block
						   e.printStackTrace();
					   	}
					   /*
					    * Refreshing the gallery for the captured images to appear in gallery
					    */
					   	Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
  		                intent.setData(Uri.fromFile(outputfile));
  		            	sendBroadcast(intent);
  		            	
                        Log.v("CameraActivity","value of file path is "+filepath1);    
                        Log.v("CameraActivity"," after ********value of file path is "+filepath1);
                         //startActivity(intentSaveImage);
                         
  		            /*
  		     	      * Storing the latitude and longitude information in the JPEG image. 
  		     	      * Associated with every image there is a UserComment attribute.
  		     	      * This attribute would take the tag value when user enters tags.                               
  		     	      */
                      try {
                    	  
  						ExifInterface exif = new ExifInterface(filepath1);
  						locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
  						Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
  						double latitude,longitude;
  						if(location!=null){
  						
  						
  						latitude = location.getLatitude();
  						longitude = location.getLongitude();
  						
  						}
  						else{
  								Log.v("camera activity","here");
  								latitude =37.7233;
  			                    longitude =-122.4797;
  			            
  						}
  						exif.setAttribute(ExifInterface.TAG_DATETIME, String.valueOf(System.currentTimeMillis()));
  						
  						//if(location.getLatitude()location.getLongitude())
  						//Log.v("CameraActivity","latitude"+location.getLatitude());
  						exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, decimaltoDegreeMinuteSeconds(latitude));
  						
  						exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, decimaltoDegreeMinuteSeconds(longitude));
  						
  						if(latitude>0){
  							exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
  						}
  						else{
  							exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
  						}
  						if(longitude>0){
  							exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
  						}
  						else{
  							exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
  						}	
  						
  						/*
  						 * We need to convert the latitude and longitude to Exif GPS data format
  						 * The format of Exif TAG_GPS_LATITUDE  is "num1/denom1,num2/denom2,num3/denom3".
  						 */
  						
  						exif.setAttribute("UserComment","na");
  						exif.saveAttributes();
  						
  					} catch (IOException e) {
  						
  						e.printStackTrace();
  					}     
		            
					return null;

				}
	    			
	    	}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, filepath);
	    	
	    	//saveFileToDrive(filepath);
	    }
	    
	   
	    /*
	     * Converting the decimal value of the latitude and longitude obtained from 
	     * GPS provider to the format that can be saved on the Exif tag
	     */
	   public String decimaltoDegreeMinuteSeconds(double coordinate) {  
		   coordinate = coordinate > 0 ? coordinate : -coordinate;  // -105.9876543 -> 105.9876543
	    	  String sOut = Integer.toString((int)coordinate) + "/1,";   // 105/1,
	    	  coordinate = (coordinate % 1) * 60;         // .987654321 * 60 = 59.259258
	    	  sOut = sOut + Integer.toString((int)coordinate) + "/1,";   // 105/1,59/1,
	    	  coordinate = (coordinate % 1) * 60000;             // .259258 * 60000 = 15555
	    	  sOut = sOut + Integer.toString((int)coordinate) + "/1000";   // 105/1,59/1,15555/1000
	    	  return sOut;
	    	}
}
