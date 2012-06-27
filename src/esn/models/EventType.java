package esn.models;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import esn.activities.R;

import android.graphics.drawable.Drawable;

public class EventType implements KvmSerializable {
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
	
	
	@Override
	public Object getProperty(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg, PropertyInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProperty(int index, Object value) {
		// TODO Auto-generated method stub

	}

}