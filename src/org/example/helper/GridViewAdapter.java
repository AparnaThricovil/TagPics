/**
 * 
 * Code for reference  from : javatechig {@link http://javatechig.com}
 * 
 */
package org.example.helper;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.view.View.OnClickListener;

import org.example.tagproject.FullScreenViewActivity;
import org.example.tagproject.R;

public class GridViewAdapter extends ArrayAdapter<ImageItem> {
	private Context context;
	private int layoutResourceId;
	private ArrayList<ImageItem> data = new ArrayList<ImageItem>();
	private ArrayList<String> path = new ArrayList<String>();
	public GridViewAdapter(Context context, int layoutResourceId,
			ArrayList<ImageItem> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}
	/*
	 * Getting all the images marked by a particular tag
	 */
	public ArrayList<String> getallFilePaths(ArrayList<ImageItem> data){
		
		for(ImageItem images : data){
			path.add(images.getFileName());
		}
		return path;
		
	}

	@Override
	public View getView(int position, final View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;
		final int pos = position;
		if (row == null) {
			/*
			 * Instantiates a layout XML file into its corresponding View objects
			 *  getLayoutInflater() to retrieve a standard LayoutInflater instance
			 *  that is already hooked up to the current context and correctly configured for 
			 *  the device you are running on
			 */
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
		row.setOnClickListener(new OnClickListener() {
			/*
			 * The on click of the image calls FullScreenViewActivity 
			 * class via an Intent and passes all the images so as 
			 * to be displayed for the full screen view which can 
			 * be swiped from right to left and left to right
			 */
	        public void onClick(View v) {
	            Intent i = new Intent(context,FullScreenViewActivity.class);
	            i.putStringArrayListExtra("data", getallFilePaths(data));
	            i.putExtra("position", pos);
	            context.startActivity(i);
	        }
		});
		
		return row;
	}

	static class ViewHolder {
		ImageView image;
	}
    
}