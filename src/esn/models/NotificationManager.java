package esn.models;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import esn.classes.HttpHelper;
import esn.classes.Utils;

public class NotificationManager {

	String NAMESPACE = "http://esn.com.vn/";
	String URL = "http://bangnl.info/ws/ApplicationsWS.asmx";
	
	HttpHelper helper = new HttpHelper(URL);
	
	public NotificationManager() {
	
	}
	
	public ArrayList<NotificationDTO> getNotification(int accID) throws JSONException, IOException, IllegalArgumentException, IllegalAccessException {
		
		ArrayList<NotificationDTO> listNotification = new ArrayList<NotificationDTO>();
		
		JSONObject params = new JSONObject();
		
		params.put("accountID", accID);
		
		JSONObject result = helper.invokeWebMethod("GetUnReadNotifications", params);
		
		if (result != null) {
			
			if (result.has("d")) {
				
				JSONArray jsonCall = result.getJSONArray("d");
				
				for (int i = 0; i < jsonCall.length(); i++) {
					JSONObject json = jsonCall.getJSONObject(i);
					NotificationDTO notificationDTO = new NotificationDTO();
					Utils.JsonToObject(json, notificationDTO);
					listNotification.add(notificationDTO);					
				}
			}
		}
		return listNotification;		
	}
}
