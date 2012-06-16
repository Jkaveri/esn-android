package esn.models;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class FriendsListsDTO implements KvmSerializable{
	public int accID;
	public String name;
	public String avatarURL;
	
	public FriendsListsDTO(){
	}
	
	
	public FriendsListsDTO(int accID, String name, String avtURL){
		this.accID = accID;
		this.name = name;
		this.avatarURL = avtURL;
	}
	
	@Override
	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return accID;
		case 1:
			return name;
		case 2:
			return avatarURL;
		default:
			return null;
		}
	}

	@Override
	public int getPropertyCount() {
		return 3;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable hasTab, PropertyInfo inf) {
		switch (index) {
		case 0:
			inf.type = PropertyInfo.INTEGER_CLASS;
			inf.name = "AccID";
			break;
		case 1:
			inf.type = PropertyInfo.STRING_CLASS;
			inf.name = "Name";
			break;
		case 2:
			inf.type = PropertyInfo.STRING_CLASS;
			inf.name = "Avatar";
			break;
		}
	}

	@Override
	public void setProperty(int index, Object obj) {
		switch (index) {
		case 0:
			accID = Integer.parseInt(obj.toString());
			break;
		case 1:
			name = obj.toString();
			break;
		case 2:
			avatarURL = obj.toString();
			break;
		default:
			break;
		}
	}
}
