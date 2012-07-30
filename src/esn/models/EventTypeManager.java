package esn.models;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import esn.classes.HttpHelper;
import esn.classes.Utils;

public class EventTypeManager {
	final String NAMESPACE = "http://esn.com.vn/";
	final String URL = "http://bangnl.info/ws/EventsWS.asmx";
	HttpHelper helper = new HttpHelper(URL);

	public EventTypeManager() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<EventType> getList() throws ClientProtocolException,
			IOException, JSONException, IllegalArgumentException,
			IllegalAccessException, ParseException {
		ArrayList<EventType> list = new ArrayList<EventType>();

		JSONObject response = helper.invokeWebMethod("GetListEventTypes");
		if (response != null) {
			JSONArray eventTypeArr = response.getJSONArray("d");

			for (int i = 0; i < eventTypeArr.length(); i++) {
				JSONObject jsonEventType = eventTypeArr.getJSONObject(i);
				EventType eventType = new EventType();
				Utils.JsonToObject(jsonEventType, eventType);
				list.add(eventType);				
			}
		}
		return list;
	}
}
