package org.example.helper;

import android.media.ExifInterface;

public class MapActivityHelper {
	
	private boolean valid = false;
	Float latitude, longitude;
	public MapActivityHelper(ExifInterface exif){
	 String exifLatitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
	 String exiflatitude_Ref = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
	 String exifLongitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
	 String exifLongitude_Ref = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

	 if((exifLatitude !=null)
	   && (exiflatitude_Ref !=null)
	   && (exifLongitude != null)
	   && (exifLongitude_Ref !=null))
	 {
	  valid = true;
	 
	  if(exiflatitude_Ref.equals("N")){
	   latitude = convertToDegree(exifLatitude);
	  }
	  else{
	   latitude = 0 - convertToDegree(exifLatitude);
	  }
	 
	  if(exifLongitude_Ref.equals("E")){
	   longitude = convertToDegree(exifLongitude);
	  }
	  else{
	   longitude = 0 - convertToDegree(exifLongitude);
	  }
	 
	 }
	};

	private Float convertToDegree(String degreeMinuteSecond){
	 Float result = null;
	 String[] DMS = degreeMinuteSecond.split(",", 3);

	 String[] stringD = DMS[0].split("/", 2);
	    Double D0 = new Double(stringD[0]);
	    Double D1 = new Double(stringD[1]);
	    Double FloatD = D0/D1;

	 String[] stringM = DMS[1].split("/", 2);
	 Double M0 = new Double(stringM[0]);
	 Double M1 = new Double(stringM[1]);
	 Double FloatM = M0/M1;
	  
	 String[] stringS = DMS[2].split("/", 2);
	 Double S0 = new Double(stringS[0]);
	 Double S1 = new Double(stringS[1]);
	 Double FloatS = S0/S1;
	  
	    result = new Float(FloatD + (FloatM/60) + (FloatS/3600));
	  
	 return result;


	};

	public boolean isValid()
	{
	 return valid;
	}

	@Override
	public String toString() {
	 // TODO Auto-generated method stub
	 return (String.valueOf(latitude)
	   + ", "
	   + String.valueOf(longitude));
	}

	public int getLatitude(){
		System.out.println("Latitude is:"+(int)(latitude*1000000));
	 return (int)(latitude*1000000);
	}

	public int getLongitude(){
		System.out.println("longitude is:"+(int)(longitude*1000000));
	 return (int)(longitude*1000000);
	}
	
}
