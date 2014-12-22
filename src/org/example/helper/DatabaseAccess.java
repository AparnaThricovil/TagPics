//Database helper
package org.example.helper;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAccess extends SQLiteOpenHelper {
	
	public DatabaseAccess(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "TagImageDB";

	//Tag table name
	private static final String TABLE_TAGS="tags";
 
    // Tag column names
    private static final String TAG_ID = "tagId";
    private static final String TAG_NAME = "tagName";
 
    // Tag table create statement
    private static final String CREATE_TABLE_TAG = "CREATE TABLE " + TABLE_TAGS
            + "(" + TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + TAG_NAME + " TEXT "+ ")";
    
    //Checking if TAG table exists
    //private static final String TABLE_TAG_IF_EXISTS = "SELECT * FROM SQLITE_MASTER WHERE TYPE='TABLE' AND NAME='TABLE_TAGS'; ";
    
    public DatabaseAccess(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
 
        // creating required table
        db.execSQL(CREATE_TABLE_TAG);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
    	}
    
    /*
     * Creating a new tag
     */
    
    public void createTag(Tag tag) {
    	
        SQLiteDatabase db = this.getWritableDatabase();	
        ContentValues values = new ContentValues(); 
        values.put(TAG_NAME, tag.getTagName()); 
        db.insert(TABLE_TAGS, null, values);
        return;
    }
    
    /*
     * To obtain all the tags existing in the table "TABLE_TAGS"
     */
    
    public ArrayList<Tag> getAllTags() {
    	
        ArrayList<Tag> tags = new ArrayList<Tag>();
        String selectQuery = "SELECT  * FROM " + TABLE_TAGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Tag t = new Tag();
                t.setTagId(c.getInt((c.getColumnIndex(TAG_ID))));
                t.setTagName(c.getString(c.getColumnIndex(TAG_NAME)));
     
                // adding to tags list
                tags.add(t);
            } while (c.moveToNext());
        }
        c.close();
        return tags;
    }
    /*
     * This is invoked when delete tag is called
     */
    public void deleteTag(String tag){
    	ArrayList<Tag> allTags = new ArrayList<Tag>();
    	allTags= getAllTags();
    	for(Tag newTag : allTags){
    		if(newTag.getTagName().equalsIgnoreCase(tag)){
    			Log.v("in delete tag","whats the pblm");
    	    	//String deleteQuery = "DELETE from "+ TABLE_TAGS +"WHERE TAG_NAME="+tag;
    	    	SQLiteDatabase db = this.getWritableDatabase();
    	        db.delete(TABLE_TAGS,TAG_ID + "=" + newTag.getTagId(), null);
    	        closeDB();
    	        //DATABASE_TABLE, KEY_NAME + "=" + name, null
    		}
    	}
    }
    
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
