/*
 *  Displaying images using the layout manager view pager so that 
 *  users can flip through images from left to right or right to 
 *  left. Used to display images in the Gallery view and the tagged 
 *  images view(where images under a particular tag is displayed 
 *  to the user)
 */
package org.example.tagproject;
import java.util.ArrayList;

import org.example.helper.FullScreenImageAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class FullScreenViewActivity extends Activity{

	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_view);

		viewPager = (ViewPager) findViewById(R.id.pager);

		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		//Passing the image paths of all the images under a particular tag
		ArrayList<String> imagePaths = i.getStringArrayListExtra("data");
		adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,imagePaths);

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);
	}
}
