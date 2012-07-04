package esn.models;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import esn.classes.HttpHelper;
import esn.classes.Utils;

public class FriendsManager {
	private static String URL = "http://bangnl.info/ws/AccountsWS.asmx";
	HttpHelper helper;

	public FriendsManager() {
		helper = new HttpHelper(URL);
	}

	public ArrayList<FriendsListsDTO> getFriendsList(int pageSize, int pageIndex, int accID) throws JSONException, IOException, IllegalArgumentException, IllegalAccessException {
		ArrayList<FriendsListsDTO> frds = new ArrayList<FriendsListsDTO>();
		JSONObject params = new JSONObject();
		params.put("accountID", accID);
		params.put("pageNum", pageIndex);
		params.put("pageSize", pageSize);
		JSONObject result = helper.invokeWebMethod("GetListFriendsJSON",params);
		if (result != null) {
			if (result.has("d")) {
				JSONArray jsonCall = result.getJSONArray("d");
				for (int i = 0; i < jsonCall.length(); i++) {
					JSONObject json = jsonCall.getJSONObject(i);
					FriendsListsDTO frd = new FriendsListsDTO();
					Utils.JsonToObject(json, frd);
					frds.add(frd);
				}
			}
		}
		return frds;
	}

	public boolean unfriend(int accID, int friendID) {
		return true;
	}
	

	public Users RetrieveByAccID(int accID) throws JSONException, IOException, IllegalArgumentException, IllegalAccessException{
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
}
