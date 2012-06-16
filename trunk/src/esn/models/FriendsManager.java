package esn.models;

import java.util.ArrayList;
import java.util.Hashtable;
import org.ksoap2.serialization.SoapObject;
import esn.classes.EsnWebServices;

public class FriendsManager {
	private static String NAMSPACE = "http://esnservice.somee.com/";
	private static String URL = "http://esnservice.somee.com/friendservice.asmx";
	private EsnWebServices service;

	public FriendsManager() {
		service = new EsnWebServices(NAMSPACE, URL);
	}

	public ArrayList<FriendsListsDTO> getFriendsList(int page, int accID) {
		ArrayList<FriendsListsDTO> frdList = new ArrayList<FriendsListsDTO>();
		//Create soap paramater
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("page", page);
		params.put("accID", accID);
		// get soap result
		SoapObject response = service.InvokeMethod("GetFriendList", params);
		if (response != null) {
			// get element Array
			SoapObject userArray = (SoapObject) response.getProperty(0);
			// get array count
			int count = userArray.getPropertyCount();
			// init arrays
			for (int i = 0; i < count; i++) {
				// get an item in userArray
				SoapObject userSoap = (SoapObject) userArray.getProperty(i);
				// initialize user
				FriendsListsDTO frd = new FriendsListsDTO();
				// get property count
				int propCount = userSoap.getPropertyCount();
				for (int j = 0; j < propCount; j++) {
					Object value = userSoap.getProperty(j);
					if (value != null) {
						frd.setProperty(j, value);
					}
				}
				frdList.add(frd);
			}
		}
		return frdList;
	}

	public boolean unfriend(int accID, int friendID) {
		return true;
	}
}
