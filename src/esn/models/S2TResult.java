package esn.models;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class S2TResult implements KvmSerializable{
	private String type;
	private String address;

	@Override
	public Object getProperty(int index) {
		switch (index) {
		case 0:			
			return type;
		case 1:			
			return address;
		default:
			return null;
		}
	}

	@Override
	public int getPropertyCount() {
		return 2;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void getPropertyInfo(int index, Hashtable hasTab, PropertyInfo proInf) {
		switch (index) {
		case 0:
			proInf.type = PropertyInfo.STRING_CLASS;
			proInf.name = "Type";
			break;
		case 1:
			proInf.type = PropertyInfo.STRING_CLASS;
			proInf.name = "Address";
			break;
		}
	}

	@Override
	public void setProperty(int index, Object value) {
		switch (index) {
		case 0:
			type = value.toString();
			break;
		case 1:
			address = value.toString();
			break;
		}
	}

	public String getAddress() {
		return address;
	}
	
	public String getType() {
		return type;
	}
}
