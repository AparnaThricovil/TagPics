package org.example.helper;
 
//import org.example.helper.R;
 
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
    	//bitmap.recycle();
    	//bitmap = null;
    	System.gc();
        ImageView imgDisplay;
        Button btnClose;
  
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);
  
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
         
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 8;
        bitmap = BitmapFactory.decodeFile(imagePaths.get(position), options);
        imgDisplay.setImageBitmap(bitmap);
         
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {           
            @Override
            public void onClick(View v) {
            	recycleBitmap();
            	//bitmap.recycle();
                //bitmap = null;
                //System.gc();
            	
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