/**
 * 
 * Followed tutorial for the Navigation Drawer Fragment 
 * 
 */
package org.example.tagproject;

import org.example.helper.*;

import java.io.IOException;
import java.util.ArrayList;

import org.example.helper.GridViewAdapter;

import android.support.v4.widget.DrawerLayout;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

public class AddImagetoTagsActivity extends Activity implements ImageResultListener,
			NavigationDrawerFragment.NavigationDrawerCallbacks,NavigationDrawerFragment.OnDeleteTagSelectedListener{
	
	static String tag;
	private DatabaseAccess db;
	public ArrayList<Bitmap> allImagePaths = new ArrayList<Bitmap>();
	public Bitmap myBitmap;
	private ImageView imgView;
	private GridView gridView;
	private GridViewAdapter customGridAdapter;
	private ImageService imageService;
    private boolean          bound;
    private boolean          computationPending = false;
    /**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment navigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_imageto_tags);
		Intent intent = getIntent();
		tag = intent.getStringExtra("tagSelected");
		//Setting the title for the activity with the tag name 	
		this.setTitle(tag+" "+"images");
		gridView = (GridView) findViewById(R.id.gridView);
		
		navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		title = getTitle();

		// Set up the drawer.
		navigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		bindToImageService();
		callImageService();
	}
	
	public String getTag(){
		return tag;
	}
	 final private class ImageServiceConnection implements ServiceConnection {

	        /**
	         * onServiceConnected will be called whenever the connection to a
	         * service has been established. The parameters are the class name of
	         * the service and a reference to the IBinder interface. The latter can
	         * be used to retrieve the ImageService instance.
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
	            imageService.registerResultListener(AddImagetoTagsActivity.this);
	            if (computationPending) {
	                /*
	                 * If computationPending is true it means that the user called 
	                 * callImageService for obtaining the image. Since we are
	                 * bound now, start getting the images by calling the getImages of the ImageService.
	                 */
	                computationPending = false;
	                imageService.getImages(tag,1);
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
		case R.id.addimages:
			Intent intentImage = new Intent(this, ImagesTagActivity.class);
			intentImage.putExtra("tagSelected", tag);
	    	startActivity(intentImage);
			return true;
			
		case R.id.deletetag:
			db = new DatabaseAccess(this);
			//showAlert();
			return true;
			
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
			intentMap.putExtra("imagefile", "na");
	    	startActivity(intentMap);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}
	//Show Alert when a tag is deleted.
	public void onDeleteSelectedShowAlert(){
		db = new DatabaseAccess(this);
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
				imageService.getImages(tag,2);
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
                imageService.getImages(tag,1);
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
             * ImageService, the service will be destroyed as well.
             */
            unbindService(connection);
            bound = false;
        }
    }
	@Override
	public void imageAvailable(final ArrayList<ImageItem> images,int option){
		/*if the selected option was to delete tag, then we need to return to 
		 * the listing of tags view. The exif data of the image is set to "na"
		 */
		if(option==2){
			
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
		}
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
	            	
	        		customGridAdapter = new GridViewAdapter(AddImagetoTagsActivity.this, R.layout.row_grid, images);
	        		gridView.setAdapter(customGridAdapter);
	            }
	        });
	}
	
	

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			title = getString(R.string.title_section1);
			break;
		case 2:
			title = getString(R.string.title_section2);
			break;
		case 3:
			title = getString(R.string.title_section3);
			break;
		case 4:
			title = getString(R.string.title_section4);
			break;
		case 5:
			title = getString(R.string.title_section5);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
		Log.v("AITT","RESTORE ACTION BAR");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			Log.v("AITT","newInstance");
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			args.putString("tagImages", tag);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Log.v("AITT","onCreateView");
			View rootView = inflater.inflate(R.layout.fragment_list, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			Log.v("AITT","onAttach");
			//((AddImagetoTagsActivity) activity).onSectionAttached(getArguments().getInt(
					//ARG_SECTION_NUMBER));
		}
	}	
}
