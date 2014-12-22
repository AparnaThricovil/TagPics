package org.example.helper;

public class MapMarker {
	
	//private String mLabel;
    private String image;
    private Double latitude;
    private Double longitude;

    public MapMarker(String image, Double latitude, Double longitude)
    {
        //this.mLabel = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

    
}
