package esn.models;

import java.util.Date;


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
}
