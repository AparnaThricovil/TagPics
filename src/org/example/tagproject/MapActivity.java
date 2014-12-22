package org.example.tagproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.example.helper.DatabaseAccess;
//import org.example.helper.GridViewAdapter;
import org.example.helper.ImageItem;
//import org.example.helper.MapActivityHelper;
import org.example.helper.MapMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
//import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
//import android.annotation.SuppressLint;
import android.app.Activity;
//import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
//import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MapActivity extends Activity implements ImageResultListener{
  
   private GoogleMap googleMap;
   String tag,imagePath=null;
  // String tag = exif.getAttribute("UserComment");
	DatabaseAccess db;
	ArrayList<Bitmap> allImagePaths = new ArrayList<Bitmap>();
	Bitmap myBitmap;
	private RelativeLayout mymapview; //= (RelativeLayout)findViewById(R.id.mapview);
   private HashMap<Marker, MapMarker> mMarkersHashMap;
   private ImageService imageService;
   private boolean          bound;
   private boolean          computationPending = false;
   //private View view;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_map);
      mymapview = (RelativeLayout)findViewById(R.id.mapview);
      mMarkersHashMap = new HashMap<Marker, MapMarker>();
      Intent intent = getIntent();
      tag = intent.getStringExtra("tagSelected");
      imagePath = intent.getStringExtra("imagefile");
      try { 
            if (googleMap == null) {
               googleMap = ((MapFragment) getFragmentManager().
               findFragmentById(R.id.map)).getMap();
            }
         googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
         
         //MarkerOptions marker = new MarkerOptions().position(TutorialsPoint);
         //googleMap.addMarker(marker);
         bindToImageService();
 		 callImageService();

      } catch (Exception e) {
         e.printStackTrace();
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
           imageService.registerResultListener(MapActivity.this);
           if (computationPending) {
               /*
                * If computationPending is true it means that the user called 
                * callImageService for obtaining the image. Since we are
                * bound now, start getting the images by calling the getImages of the ImageService.
                */
               computationPending = false;
               imageService.getImages(tag,5);
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
public boolean onCreateOptionsMenu(Menu menu) {
   MenuInflater inflater = getMenuInflater();
   inflater.inflate(R.menu.map, menu);
   return super.onCreateOptionsMenu(menu);
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
		
	case R.id.home:
		Intent intentHome = new Intent(this, HomeActivity.class);
   	startActivity(intentHome);
		return true;

	default:
		return super.onOptionsItemSelected(item);
	}

}

public void callImageService() {
	//Log.v("Addimage", "in callImageService");
   try {

       if (bound) {
           imageService.getImages(tag,5);
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
}
@Override
public void imageAvailable(final ArrayList<ImageItem> images,int option){
	/*if the selected option was to delete tag, then we need to return to 
	 * the listing of tags view. The exif data of the image is set to "na"
	 */
	if(!(option==5)){
		
		
	}
	/*
	 * When the service returns update the mapview with the markers.
	 * Clicking on the markers would show the image in actual size
	 */
	else{

		mymapview.post(new Runnable() {

           @Override
           public void run() {
        	   /*when MapView is called from HomeActivity, we need to show just 
        	    * one image on the Map
        	    */
        	   if(!imagePath.equalsIgnoreCase("na")){
        		   LatLng imgCoord = getCoordinates(imagePath);
        		   MarkerOptions marker = new MarkerOptions().position(imgCoord);
   				marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
   				//marker.icon(BitmapDescriptorFactory.fromBitmap(item.getImage()));
   		        //googleMap.addMarker(marker);
   				Marker currentMarker = googleMap.addMarker(marker);
   				MapMarker mapItem = new MapMarker(imagePath,imgCoord.latitude,imgCoord.longitude);
		        Log.v("Map activity","this val is: mapItem -->"+mapItem);
		        if(currentMarker!=null && mapItem!= null){
		        mMarkersHashMap.put(currentMarker,mapItem);
		        }
		        googleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        	}
        	   //when Mapview is called from activities other than homeactivity.
        	   else{
        		   for (ImageItem item : images){
        			
        			LatLng imgCoord = getCoordinates(item.getFileName());			
        			MarkerOptions marker = new MarkerOptions().position(imgCoord);
        			marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
        				//marker.icon(BitmapDescriptorFactory.fromBitmap(item.getImage()));
        		       // googleMap.addMarker(marker);
        			Log.v("Map actiovity","didn't reach here");
        		    Marker currentMarker = googleMap.addMarker(marker);
        		    Log.v("Map actiovity","this val is  currentMarker :"+currentMarker);
        		        
        		    MapMarker mapItem = new MapMarker(item.getFileName(),imgCoord.latitude,imgCoord.longitude);
        		    Log.v("Map activity","this val is: mapItem -->"+mapItem);
        		    if(currentMarker!=null && mapItem!= null){
                        mMarkersHashMap.put(currentMarker,mapItem);
        		    }
                    googleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        		        
        		}
        	  }
        			
           }
           
		});
		
	}
}

public LatLng getCoordinates(String filePath){
	ExifInterface exif;
	 LatLng imageCoordinates = new LatLng(0,0);
	String sLat = "", sLatR = "", sLon = "", sLonR = "";
	try {
		
		exif = new ExifInterface(filePath);
		
		sLat  = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
	    sLon  = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
	    sLatR = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
	    sLonR = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
	   
	    double lat = degreeMinuteSecondtoDeouble(sLat);
	    if (lat > 180.0) {
	    	//return null; 
	    	 imageCoordinates = null;
	    }
	    double lon = degreeMinuteSecondtoDeouble(sLon);
	    
	    if (lon > 180.0){
	    	 imageCoordinates = null;//return null; 
	    }
	    else{
	    lat = sLatR.contains("S") ? -lat : lat;
	    lon = sLonR.contains("W") ? -lon : lon;
	    
		 imageCoordinates = new LatLng(lat, lon);
	    }
		
	}
	catch(IOException e){
		e.printStackTrace();
	}
	return imageCoordinates;
}

public double degreeMinuteSecondtoDeouble(String sDMS){
	  double dRV = 999.0;
	  try {
	    String[] DMSs = sDMS.split(",", 3);
	    String s[] = DMSs[0].split("/", 2);
	    dRV = (new Double(s[0])/new Double(s[1]));
	    s = DMSs[1].split("/", 2);
	    dRV += ((new Double(s[0])/new Double(s[1]))/60);
	    s = DMSs[2].split("/", 2);
	    dRV += ((new Double(s[0])/new Double(s[1]))/3600);
	  } catch (Exception e) {}
	  return dRV;
	}
/*
 * GoogleMap.InfoWindowAdapter : Provides views for customized rendering 
 * of info windows.Methods on this provider are called when it is time to 
 * show an info window for a marker, regardless of the cause 
 * (either a user gesture or a programmatic call to showInfoWindow(). 
 * Since there is only one info window shown at any one time, this 
 * provider may choose to reuse views, or it may choose to create
 *  new views on each method invocation. 
 */

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{
    public MarkerInfoWindowAdapter()
    {
    }
    /*
     * getInfoWindow(Marker marker):Provides a custom info window for a 
     * marker. If this method returns a view, it is used for the entire 
     * info window. If you change this view after this method is called, 
     * those changes will not necessarily be reflected in the rendered 
     * info window. If this method returns null , the default info 
     * window frame will be used, with contents provided by getInfoContents(Marker).
     */
    @Override
    public View getInfoWindow(Marker marker)
    {
        return null;
    }
    /*
     * getInfoContents: Provides custom contents for the default info window frame of a marker. 
     * This method is only called if getInfoWindow(Marker) first returns null. 
     * If this method returns a view, it will be placed inside the default info 
     * window frame. If you change this view after this method is called, those
     * changes will not necessarily be reflected in the rendered info window.
     * If this method returns null, the default rendering will be used instead.
     */
    public View getInfoContents(Marker marker)
    {
        View v  = getLayoutInflater().inflate(R.layout.image_on_map, null);

        MapMarker myMarker = mMarkersHashMap.get(marker);
        ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);
        
        /*
         * Just show a scaled image. No need to show the image by full 
         * size since it would cover the full screen.
         */
        
        Bitmap scaledBitmap = Bitmap.createScaledBitmap( BitmapFactory.decodeFile(myMarker.getImage()), 400, 400, false );
        
        markerIcon.setImageBitmap(scaledBitmap);
        
        return v;
    }
}
}