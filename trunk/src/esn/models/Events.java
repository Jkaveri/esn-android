package esn.models;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;


public class Events{
	 public int EventID;
     public int AccID;
     public int EventTypeID;
     public String Title;
     public String Description;
     public String Picture;
     public Date DayCreate;
     public double EventLat;
     public double EventLng;
     public int ShareType;
     public int Like;
     public int Dislike;
     public int Status;
     public Users user;

	public int getLevel(){
		if(Like>50) return 3;
		if(Like>25 && Like <=50)return 2;
		return 1;
	}
	public Address getAddress(Context ctx){
		Locale en_US = new Locale("en");

		Geocoder coder = new Geocoder(ctx,en_US);
		try {
			List<Address> addresses = coder.getFromLocation(EventLat, EventLng, 1);
			if(addresses.size()>0){
				Address address = addresses.get(0);
				return address;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public GeoPoint getPoint(){
		return new GeoPoint((int)(EventLat*1E6),(int)(EventLng*1E6));
	}
}
