package esn.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.kobjects.isodate.IsoDate;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.PropertyInfo;

import android.text.format.DateFormat;

import esn.classes.MarshalDouble;

public class Events implements KvmSerializable {
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

	@Override
	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return EventID;
		case 1:
			return AccID;
		case 2:
			return EventTypeID;
		case 3:
			return Title;
		case 4:
			return Description;
		case 5:
			return Picture;
		case 6:
			return DayCreate;
		case 7:
			return EventLat;
		case 8:
			return EventLng;
		case 9:
			return ShareType;
		case 10:
			return Like;
		case 11:
			return Dislike;
		case 12:
			return Status;
		case 13 :
			return user;
		default:
			return null;
		}

	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 12;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "EventID";
			info.type = PropertyInfo.INTEGER_CLASS;
			break;
		case 1:
			info.name = "AccID";
			info.type = PropertyInfo.INTEGER_CLASS;
			break;
		case 2:
			info.name = "EventTypeID";
			info.type = PropertyInfo.INTEGER_CLASS;
			break;
		case 3:
			info.name = "Title";
			info.type = PropertyInfo.STRING_CLASS;
			break;
		case 4:
			info.name = "Description";
			info.type = PropertyInfo.STRING_CLASS;
			break;
		case 5:
			info.name = "Picture";
			info.type = PropertyInfo.STRING_CLASS;
			break;
		case 6:
			info.name = "DayCreate";
			info.type = MarshalDate.DATE_CLASS;
			break;
		case 7:
			info.name = "EventLat";
			info.type = MarshalDouble.DOUBLE_CLASS;
			break;
		case 8:
			info.name = "EventLng";
			info.type = MarshalDouble.DOUBLE_CLASS;
			break;
		case 9:
			info.name = "ShareType";
			info.type = PropertyInfo.INTEGER_CLASS;
			break;
		case 10:
			info.name = "Like";
			info.type = PropertyInfo.INTEGER_CLASS;
			break;
		case 11:
			info.name = "Dislike";
			info.type = PropertyInfo.INTEGER_CLASS;
			break;
		case 12:
			info.name = "Status";
			info.type = PropertyInfo.BOOLEAN_CLASS;
			break;
		default:

			break;
		}
	}

	@Override
	public void setProperty(int index, Object value) {
		switch (index) {
		case 0:
			EventID = Integer.parseInt(value.toString());
			break;
		case 1:
			AccID = Integer.parseInt(value.toString());
			break;
		case 2:
			EventTypeID = Integer.parseInt(value.toString());
			break;
		case 3:
			Title = value.toString();
			break;
		case 4:
			Description = value.toString();
			break;
		case 5:
			Picture = value.toString();
			break;
		case 6:
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
			try {
				DayCreate = formatter.parse(value.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 7:
			EventLat = Double.parseDouble(value.toString());
			break;
		case 8:
			EventLng = Double.parseDouble(value.toString());
			break;
		case 9:
			ShareType = Integer.parseInt(value.toString());
			break;
		case 10:
			Like = Integer.parseInt(value.toString());
			break;
		case 11:
			Dislike = Integer.parseInt(value.toString());
			break;
		case 12:
			Status = Integer.parseInt(value.toString());
			break;
		default:
			break;
		}

	}

	public int getLevel(){
		if(Like>50) return 3;
		if(Like>25 && Like <=50)return 2;
		return 1;
	}
}
