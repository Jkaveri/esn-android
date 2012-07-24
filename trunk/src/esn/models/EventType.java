package esn.models;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import esn.activities.R;

import android.graphics.drawable.Drawable;

public class EventType{
	public int EventTypeID;
	public String EventTypeName;
	public String LabelImage;
	public String Slug;
	public int Time;
	public int Status;
	
	public static int getIconId(int eventTypeID,int level) {
		int default_icon = R.drawable.ic_event_label_1;
		try {
			Field field = esn.activities.R.drawable.class.getField("ic_event_type_"
					+ eventTypeID+"_"+level);

			if (field != null) {
				String value = field.get(esn.activities.R.class).toString();
				return Integer.parseInt(value);
			}
		} catch (NoSuchFieldException e) {
			return default_icon;
		} catch (IllegalArgumentException e) {
			return default_icon;
		} catch (IllegalAccessException e) {
			return default_icon;
		}
		return default_icon;
	}
	
	public static int getID(String eventType){
		if(eventType.equals("KET_XE")){
			return 1;
		}else if(eventType.equals("LO_COT")){
			return 2;
		}else if(eventType.equals("TAI_NAN")){
			return 3;
		}else if(eventType.equals("LU_LUT")){
			return 4;
		}else if(eventType.equals("LO_DAT")){
			return 5;
		}else if(eventType.equals("DUONG XAU")){
			return 6;
		}else if(eventType.equals("CHAY_NO")){
			return 7;
		}else if(eventType.equals("DUONG_CHAN")){
			return 8;
		}else if(eventType.equals("DONG_DAT")){
			return 9;
		}
		return 0;
	}
	

}
