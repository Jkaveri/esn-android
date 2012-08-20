package esn.models;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import esn.classes.HttpHelper;
import esn.classes.Utils;

public class FriendsManager {
	public static String URL = "http://bangnl.info/ws/AccountsWS.asmx";
	private HttpHelper helper;

	public FriendsManager() {
		helper = new HttpHelper(URL);
	}

	

	
	
	public boolean addfriend(int accID, int friendID) throws JSONException, IOException {
		
		JSONObject params = new JSONObject();
		params.put("accID", accID);
		params.put("friendID", friendID);
		JSONObject result = helper.invokeWebMethod("AddFriend",params);
				
		return result.getBoolean("d");
	}
	

	public Users RetrieveByAccID(int accID) throws JSONException, IOException, IllegalArgumentException, IllegalAccessException, ParseException{
		
		Users frd = new Users();
		JSONObject params = new JSONObject();
		params.put("id", accID);
		JSONObject result = helper.invokeWebMethod("RetrieveJSON",params);
		if (result != null) {
			if (result.has("d")) {
				JSONObject jsonCall = result.getJSONObject("d");
				Utils.JsonToObject(jsonCall, frd);
			}
		}
		return frd;
	}

	public Boolean AcceptFriend(int accId, int friendId) {
				
		return true;
	}
}
