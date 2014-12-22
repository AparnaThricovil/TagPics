package org.example.tagproject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.example.helper.GridImages;
import org.example.helper.GridViewAdapter;
import org.example.helper.ImageItem;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ImagesTagActivity extends Activity implements ImageResultListener {

	private File path;
	String tag;
	ArrayList<Bitmap> allImagePaths = new ArrayList<Bitmap>();
	Bitmap myBitmap;
	private ImageView imgView;
	private GridView gridView;
	private GridViewAdapter customGridAdapter;
	private ImageService imageService;
    private boolean          bound;
    private boolean          computationPending = false;
    ArrayList<GridImages> myObjects;
    ArrayList<String> imageNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_images_tag);
		gridView = (GridView) findViewById(R.id.gridView);
		Intent intent = getIntent();
		tag = intent.getStringExtra("tagSelected");
		//Set title for the activity programmatically with tag
		this.setTitle("Add images to"+" "+tag+" "+"tag");
		//Log.v("Addimage", "tag is"+tag);
		myObjects = new ArrayList<GridImages>();
		imageNames = new ArrayList<String>();
        //for (String s : numbers) {
          //  myObjects.add(new GridObject(s, 0));
        //}
		//tag="na";
		bindToImageService();
		callImageService();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.images_tag, menu);
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
			
		case R.id.saveImage:
			imageService.getImages(tag,4);
			//Intent intent = new Intent(this, TagActivity.class);
	    	//startActivity(intentList);			
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
             * Retrieve the FibonacciService instance and save it. The reference
             * to this instance can be used to invoke methods on the service.
             */
            Log.v("Addimage", "in onServiceConnected");
            imageService = binder.getService();
            bound = true;
            /*
             * Register the current activity as a callback that the service will
             * use to report back results when they are ready.
             */
            imageService.registerResultListener(ImagesTagActivity.this);
            if (computationPending) {
                /*
                 * If computationPending is true it means that the user clicked
                 * on the button to trigger the computation of a Fibonacci
                 * number before we bound to the FibonacciService. Since we are
                 * bound now, start the postponed computation now.
                 */
                computationPending = false;
                imageService.getImages(tag,3);
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
	
	public void callImageService() {
		Log.v("IT", "in callImageService");
        try {
            
            if (bound) {
            	Log.v("IT", "in callImage service-> in if of bound");
                /*
                 * We are already bound to the service. Simply call the public
                 * method startComputation() to trigger the computation of the
                 * Fibonacci value.
                 */
            	//tag="na";
                imageService.getImages(tag,3);
            } else {
                /*
                 * We are currently not bound to the service. Set the flag
                 * computationPending to true to ensure that the computation
                 * will be triggered once we are bound to the service. Then bind
                 * to the service.
                 */
                computationPending = true;
                bindToImageService();
            }
        } catch (Exception exc) {
            //lblResult.setText("Invalid input ...");
        }
    }

    private void bindToImageService() {
    	Log.v("IT", "in bindToImageService");
        if (!bound) {
            /*
             * Binding to a service happens via an Intent. If the service isn't
             * already running, it will be created. Binding to a service happens
             * asynchronously and the 'connection' object is used to notify when
             * the binding has succeeded.
             */
        	Log.v("IT", "in bindToImageService inside if");
            Intent intent1 = new Intent(this, ImageService.class);            
            bindService(intent1, connection, Context.BIND_AUTO_CREATE);
        }
        Log.v("IT", "outside if bindToImageService");
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
    }
	@Override
	public void imageAvailable(final ArrayList<ImageItem> images, int option){
		if(option==4){
			//gridView.post(new Runnable() {
	            //@Override
	            //public void run() {
	        
	            	for (ImageItem s : images) {
	            		for (String allImages : imageNames){
	            		if(s.getFileName().equalsIgnoreCase(allImages)){
	            			ExifInterface exif;	
	            			try {
	            				
	            				exif = new ExifInterface(s.getFileName());
	            				exif.setAttribute("UserComment",tag);
	            				exif.saveAttributes();
	            		
	            			} catch (IOException e) {
	            			// TODO Auto-generated catch block
	            			e.printStackTrace();
	            			}
	            		}
	            		}
	                    
	                }
	            	Intent intentImage = new Intent(this, AddImagetoTagsActivity.class);
	    			intentImage.putExtra("tagSelected", tag);
	    	    	startActivity(intentImage);
	           // }
			//});
		}
		else{
		 gridView.post(new Runnable() {
	            @Override
	            public void run() {
	            	//If no images are returned.
	            	if(images.isEmpty())
	                {
	            		setContentView(R.layout.no_image);
	                	imgView=(ImageView) findViewById(R.id.imageView1);
	                	imgView.setImageResource(R.drawable.noimage);
	                }
	            	for (ImageItem s : images) {
	                    myObjects.add(new GridImages(s.getFileName(), 0));
	                }
	        		customGridAdapter = new GridViewAdapter(ImagesTagActivity.this, R.layout.row_grid, images);
	        		gridView.setAdapter(customGridAdapter);
	        		gridView.setOnItemClickListener(new OnItemClickListener() {
	        			
	                    @Override
	                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	                    	
	                    	String myimagePath = myObjects.get(position).getName();
	                    	if(myObjects.get(position).getState()==1){
	                    		myObjects.get(position).setState(0);
	                    		removeImagePath(myimagePath);
	                    	}
	                    	else{
	                        myObjects.get(position).setState(1);
	                        //Log.v("ImagesTagActivity","val is"+myObjects.get(position).getName());
	                        
	                        addImagePath(myimagePath);
	                        //selectImagePath = myObjects.get(position).getName();
	                    	}
	                        customGridAdapter.notifyDataSetChanged();
	                    }
	                    
	                });
	        		//Log.v("imagestagactivity","grid-view-before-choice");
	            }
	        });
		} 
	}
	public void addImagePath(String imagePath){
		
		imageNames.add(imagePath);
		return;
	}
	public void removeImagePath(String imagePath){
		imageNames.remove(imagePath);
	}
	//public void onClick(View v){
		//add tag to images selected
		//imageService.getImages(tag,4);
		
	//}
	
	static class ViewHolder {
		ImageView image;
	}

	public class GridViewAdapter extends ArrayAdapter<ImageItem> {
		private Context context;
		private int layoutResourceId;
		private ArrayList<ImageItem> data = new ArrayList<ImageItem>();
		//private static int[] saved;
		//private static int i=0;
		//CheckableLayout l;
		public GridViewAdapter(Context context, int layoutResourceId,
				ArrayList<ImageItem> data) {
			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}

		@Override
		public View getView(int position, final View convertView, ViewGroup parent) {
			GridImages object = myObjects.get(position);
			//object.setState(1);
			View row = convertView;
			ViewHolder holder = null;
			final int pos = position;
			if (row == null) {
				LayoutInflater inflater = ((Activity) context).getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) row.findViewById(R.id.image);
				row.setTag(holder);
			} else {
				holder = (ViewHolder) row.getTag();
			}
			
			ImageItem item = (ImageItem) data.get(position);
			holder.image.setImageBitmap(item.getImage());
			if (object.getState() == 1) {
                holder.image.setBackgroundColor(Color.BLUE);
                //object.setState(0);
            } else {
                holder.image.setBackgroundColor(Color.TRANSPARENT);            	
            }
			final int rowId = row.getId();
			return row;
		}

	}
	
	//on click of done button call the image service passing tag and option value-"add"
	//the exif attribute for tag of the image gets over written by the new tag
	//return no need for images just a toast with image tagged and redirect to addimagetotags activity
	
	
}
