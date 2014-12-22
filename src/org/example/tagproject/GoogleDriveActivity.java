
/**
	 * Copyright 2013 Google Inc. All Rights Reserved.
	 *
	 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
	 * in compliance with the License. You may obtain a copy of the License at
	 *
	 *  http://www.apache.org/licenses/LICENSE-2.0
	 *
	 *  Unless required by applicable law or agreed to in writing, software distributed under the
	 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
	 * express or implied. See the License for the specific language governing permissions and
	 * limitations under the License.
	 */

	
	/**
	 * Android Drive Quickstart activity. This activity saves a photo
	 * to Google Drive. The user is prompted with a pre-made dialog which allows
	 * them to choose the file location.
	 */
package org.example.tagproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;


public class GoogleDriveActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

	    private static final String TAG = "android-drive-quickstart";
	    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
	    private static final int REQUEST_CODE_CREATOR = 2;
	    private static final int REQUEST_CODE_RESOLUTION = 3;

	    private GoogleApiClient mGoogleApiClient;
	    private Bitmap mBitmapToSave;
	    private String filePath;
	    private Bitmap image;
	    private View v;
	    
	    /**
	     * Create a new file and save it to Drive.
	     */
	    private void saveFileToDrive() {
	    	
	    	new AsyncTask<String, Void, Void>() {
	    		
	    		@Override
				protected Void doInBackground(String... params) {
	        // Start by creating a new contents, and setting a callback.
	    	Log.v("GoogleDRiveActivity","saveFileToDrive1");
	        Log.i(TAG, "Creating new contents.");
	        final Bitmap image = mBitmapToSave;
	        Drive.DriveApi.newDriveContents(mGoogleApiClient)
	                .setResultCallback(new ResultCallback<DriveContentsResult>() {

	            @Override
	            public void onResult(DriveContentsResult result) {
	                // If the operation was not successful, we cannot do anything
	                // and must
	                // fail.
	            	Log.v("GoogleDRiveActivity","saveFileToDrive2");
	                if (!result.getStatus().isSuccess()) {
	                    Log.i(TAG, "Failed to create new contents.");
	                    return;
	                }
	                // Otherwise, we can write our data to the new contents.
	                Log.i(TAG, "New contents created.");
	                // Get an output stream for the contents.
	                OutputStream outputStream = result.getDriveContents().getOutputStream();
	                // Write the bitmap data from it.
	                ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
	                image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
	                try {
	                	Log.v("GoogleDRiveActivity","saveFileToDrive3");
	                    outputStream.write(bitmapStream.toByteArray());
	                } catch (IOException e1) {
	                    Log.i(TAG, "Unable to write file contents.");
	                }
	                // Create the initial metadata - MIME type and title.
	                // Note that the user will be able to change the title later.
	                /*
	                 * A collection of metadata changes. Any fields with 
	                 * null values will retain their current value. 
	                 */
	               // DriveFolder folder = Drive.DriveApi
	                 //       .getFolder(mGoogleApiClient, result.getDriveContents().getDriveId());
	                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
	                        .setMimeType("image/jpeg").setTitle("Android.png").build();
	               // Metadata metada = image.get
	                		//.setMimeType("image/jpeg").setTitle("Android.png").build();
	                // Create an intent for the file chooser, and start it.
	               IntentSender intentSender = Drive.DriveApi
	                        .newCreateFileActivityBuilder()
	                      //  .setMimeType(new String[]{ "png"})
	                        .setInitialMetadata(metadataChangeSet)
	                        .setInitialDriveContents(result.getDriveContents())
	                        .build(mGoogleApiClient);
	                Log.v("GoogleDRiveActivity","saveFileToDrive4");
	               try {
	                    startIntentSenderForResult(
	                            intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
	                    Log.v("GoogleDRiveActivity","saveFileToDrive5");
	                } catch (SendIntentException e) {
	                   Log.i(TAG, "Failed to launch file chooser.");
	                }
	            }
	        });
	       // finish();
			return null;
	    }
	   }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,filePath);
	    } 	

	    @Override
	    protected void onResume() {
	    	Log.v("GoogleDRiveActivity","onResume");
	    	super.onResume();
	    	if (mGoogleApiClient == null) {
	    		// Create the API client and bind it to an instance variable.
	            // We use this instance as the callback for connection and connection
	            // failures.
	            // Since no account name is passed, the user is prompted to choose.
	            mGoogleApiClient = new GoogleApiClient.Builder(this)
	                    .addApi(Drive.API)
	                    .addScope(Drive.SCOPE_FILE)
	                    .addConnectionCallbacks(this)
	                    .addOnConnectionFailedListener(this)
	                    .build();
	    	}
	        // Connect the client. Once connected, the camera is launched.
	        mGoogleApiClient.connect();
	    }

	    @Override
	    protected void onPause() {
	    	finish();
	    	Log.v("GoogleDRiveActivity","onPause");
	        if (mGoogleApiClient != null) {
	            mGoogleApiClient.disconnect();
	            
	        }
	        super.onPause();
	    }

	    @Override
	    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
	    	Intent intent = getIntent();
	    	Log.v("GoogleDRiveActivity","onActivityResult");
	    	filePath= intent.getStringExtra("image");
	    	mBitmapToSave =  BitmapFactory.decodeFile(filePath);
	    	saveFileToDrive();
	    } 

	    @Override
	    public void onConnectionFailed(ConnectionResult result) {
	        // Called whenever the API client fails to connect.
	        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
	        if (!result.hasResolution()) {
	            // show the localized error dialog.
	            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
	            return;
	        }
	        // The failure has a resolution. Resolve it.
	        // Called typically when the app is not yet authorized, and an
	        // authorization
	        // dialog is displayed to the user.
	        try {
	        	Log.v("GoogleDRiveActivity","onConnectionFailed");
	            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
	        } catch (SendIntentException e) {
	            Log.e(TAG, "Exception while starting resolution activity", e);
	        }
	    }

	    @Override
	    public void onConnected(Bundle connectionHint) {
	    	
	        Log.i(TAG, "API client connected.");
	        Intent intent = getIntent();
	    	
	    	filePath= intent.getStringExtra("image");
	    	mBitmapToSave =  BitmapFactory.decodeFile(filePath);
	        Log.v("GoogleDRiveActivity","onConnected");
	        if (mBitmapToSave == null) {
	        	// This activity has no UI of its own. Just start the camera.
	        //    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
	          //          REQUEST_CODE_CAPTURE_IMAGE);
	            //return;
	        	finish();
	        }
	        saveFileToDrive();
	    }

	    @Override
	    public void onConnectionSuspended(int cause) {
	        Log.i(TAG, "GoogleApiClient connection suspended");
	    }
	}