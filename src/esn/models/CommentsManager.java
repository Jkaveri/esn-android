package esn.models;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import esn.classes.HttpHelper;
import esn.classes.Utils;

public class CommentsManager {

	private static String URL = "http://bangnl.info/ws/EventsWS.asmx";
	HttpHelper helper;

	public CommentsManager() {
		helper = new HttpHelper(URL);
	}
	
	public int CreateComment(int eventId,int accId,String content) throws JSONException, IOException
	{
		
		JSONObject params = new JSONObject();
		
		params.put("eventId", eventId);
		params.put("accId", accId);
		params.put("content", content);
		
		JSONObject response = helper.invokeWebMethod("CreateComment",params);
		
		int rs = response.getInt("d");
		
		
		return rs;
	}
	
	public ArrayList<Comments> GetListComment(int eventId,int pageNum,int pageSize) throws JSONException, IOException, IllegalArgumentException, IllegalAccessException
	{
		ArrayList<Comments> listComment = new ArrayList<Comments>();
		
		JSONObject params = new JSONObject();
		
		params.put("eventId", eventId);
		params.put("pageNum", pageNum);
		params.put("pageSize", pageSize);
		
		JSONObject result = helper.invokeWebMethod("GetListComments", params);
		
		if (result != null) {
			
			if (result.has("d")) {
				
				JSONArray jsonCall = result.getJSONArray("d");
				
				for (int i = 0; i < jsonCall.length(); i++) {
					JSONObject json = jsonCall.getJSONObject(i);
					Comments comments = new Comments();
					Utils.JsonToObject(json, comments);
					listComment.add(comments);					
				}
			}
		}
		return listComment;
	}
}
