package org.example.helper;
  
import java.util.ArrayList;
 

import org.example.tagproject.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
 
public class FullScreenImageAdapter extends PagerAdapter {
 
    private Activity activity;
    private ArrayList<String> imagePaths;
    private LayoutInflater inflater;
    private Bitmap bitmap;
    // constructor
    public FullScreenImageAdapter(Activity activity,
            ArrayList<String> imagePaths) {
        this.activity = activity;
        this.imagePaths = imagePaths;
    }
    /*
     * Number of images to be loaded
     */
    @Override
    public int getCount() {
        return this.imagePaths.size();
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
     
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    	System.gc();
        ImageView imgDisplay;
        Button btnClose;
  
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);
  
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        /*
         * Attached to every image is a close button to close the 
         * preview of the image and to return back to the activity
         */
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
         /*
          * The inSampleSize is used to load large number of images by 
          * scaling it down.It tells the decoder to subsample the 
          * image, loading a smaller version into memory, by setting 
          * inSampleSize to a value in the BitmapFactory.Options object
          */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 8;
        bitmap = BitmapFactory.decodeFile(imagePaths.get(position), options);
        imgDisplay.setImageBitmap(bitmap);
         /*
          * When the "X" button is clicked the image view is terminated 
          * and all the bitmaps are recycled to save memory.
          */
        btnClose.setOnClickListener(new View.OnClickListener() {           
            @Override
            public void onClick(View v) {
            	recycleBitmap();
            	/*
            	 * The activity is finished and hence the user would 
            	 * be taken back to the list of images screen
            	 */
                activity.finish();
            }
        });
  
        ((ViewPager) container).addView(viewLayout);
  
        return viewLayout;
    }
     
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
  
    }
    /*
     * Recycling bitmap to avoid the out of memory error.
     */
    public void recycleBitmap(){
    	bitmap.recycle();
    	bitmap = null;
    	System.gc();
    }
}