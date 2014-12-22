/**
 * 
 * @author Aparna 
 * 
 */
/*
 * TagActivity is used to create tags
 */
package org.example.tagproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.helper.DatabaseAccess;
import org.example.helper.Tag;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TagActivity extends ListActivity {
	
	private EditText tag;
	private DatabaseAccess db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag);
		tag = (EditText) findViewById(R.id.editText1);
		hideKeypad();
		displayTags();
	}

	public void onClick(View v){
		//Call the DB class to save the tag
		db = new DatabaseAccess(this);
		
		//Calling the constructor of Tag class 
		
		Tag newTag = new Tag(0,tag.getText().toString());
		
		//Checking if the tag value is not null
		if(tag.getText().toString().matches("")){
			
			//Have a toast asking user to enter a tag
			Toast.makeText(this, "Please enter a tag", Toast.LENGTH_SHORT).show();
			hideKeypad();
		}
		//Creating a new tag in the Tag table in the database
		else{
			
			db.createTag(newTag);
			//Getting all tags
			List<Tag> allTags = db.getAllTags();
			ArrayList<String> createdTags = new ArrayList<String>();	
				for(Tag tags:allTags){
					createdTags.add(tags.getTagName());
				}
			
			displayTags();
			hideKeypad();
			db.closeDB();	
			tag.setText("");
		}
	}
	
	public void displayTags(){
		db = new DatabaseAccess(this);
		//Getting all tags to display as a list view
		List<Tag> allTags = db.getAllTags();
		
		ArrayList<String> createdTags = new ArrayList<String>();
		for(Tag tags:allTags){
			createdTags.add(tags.getTagName());
		}
		/*To reverse the tags. After a tag has been created we want the 
		 * tag to be displayed on the top similar to a stack. To achieve this 
		 * we reverse the list of Tags.
		 */
		Collections.reverse(createdTags);
		
		ArrayAdapter<String> listAdapter= new ArrayAdapter(this,android.R.layout.simple_selectable_list_item,createdTags);
		setListAdapter(listAdapter);
		
		final ListView listView = getListView();
		/*
		 * Setting listener for the list item click action which is actually 
		 * selecting a tag. When a tag is selected the user is taken to a screen 
		 * where they get one of the two views: 1. All images under that particular 
		 * tag. 2. If the user has not added any image for that tag, then the user 
		 * sees an no photo available image view.
		 */
		listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        
               String tagValue    = (String) listView.getItemAtPosition(position);
               callAddImagetoTagActivity(tagValue);
            }

        });
		
	}
	/*
	 * callAddImagetoTagActivity is called from the Item click 
	 * listener event and this would start an activity that actually
	 *  handles the display of images with other functions via an Intent. 
	 */
	public void callAddImagetoTagActivity(String tag){
		
		Intent intent = new Intent(this,AddImagetoTagsActivity.class);
		intent.putExtra("tagSelected", tag);
   		startActivity(intent);
	}
	//To hide the keypad after user enters a new tag and clicks on save.
	public void hideKeypad(){
		InputMethodManager inputMM = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMM.hideSoftInputFromWindow(tag.getWindowToken(), 0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.tag, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
		case R.id.home:
			Intent intentList = new Intent(this, HomeActivity.class);
	    	startActivity(intentList);
			return true;
			
		case R.id.camera:
			Intent intentCamera = new Intent(this, CameraActivity.class);
	    	startActivity(intentCamera);
			return true;
			
		case R.id.gallery:
			Intent intentMap = new Intent(this, GalleryActivity.class);
	    	startActivity(intentMap);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}
}