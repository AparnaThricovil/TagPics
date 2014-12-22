package org.example.helper;

public class Tag {
	
	public Tag(int tagId, String tagName) {
		super();
		this.tagId = tagId;
		this.tagName = tagName;
	}
	int tagId;
	String tagName;
	
	public Tag() {
		super();
		//this.tagId = tagId;
		//this.tagName = tagName;
	}
	
	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
