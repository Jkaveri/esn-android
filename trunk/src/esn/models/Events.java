package esn.models;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Events implements KvmSerializable {
	private int ID;
	private String Title;

	@Override
	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return ID;
		case 1:
			return Title;
		default:
			return null;
		}

	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
		switch (index) {
		case 0:
			info.type = PropertyInfo.INTEGER_CLASS;
			info.name = "ID";
			break;
		case 1:
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "Title";
		default:

			break;
		}
	}

	@Override
	public void setProperty(int index, Object value) {
		switch (index) {
		case 0:
			this.ID = Integer.parseInt(value.toString());

			break;
		case 1:
			this.Title = value.toString();
			break;
		default:
			break;
		}

	}

	public int getId() {
		return ID;
	}

	public void setId(int id) {
		this.ID = id;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		this.Title = title;
	}

}
