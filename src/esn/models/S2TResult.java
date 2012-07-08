package esn.models;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class S2TResult implements KvmSerializable{
	private String result;

	@Override
	public Object getProperty(int index) {
		switch (index) {
		case 0:			
			return result;
		default:
			return null;
		}
	}

	@Override
	public int getPropertyCount() {
		return 1;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void getPropertyInfo(int index, Hashtable hasTab, PropertyInfo proInf) {
		switch (index) {
		case 0:
			proInf.type = PropertyInfo.STRING_CLASS;
			proInf.name = "WriteWAVFileResult";
			break;
		}
	}

	@Override
	public void setProperty(int index, Object value) {
		switch (index) {
		case 0:
			result = value.toString();
			break;
		}
	}

	public String getResult() {
		return result;
	}
}
