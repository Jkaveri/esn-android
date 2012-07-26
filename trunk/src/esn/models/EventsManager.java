package esn.models;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.maps.GeoPoint;

import esn.classes.EsnWebServices;
import esn.classes.HttpHelper;
import esn.classes.Sessions;
import esn.classes.Utils;

public class EventsManager {
	private static final String URL = "http://bangnl.info/ws/EventsWS.asmx";
	private static final String NAMESPACE = "http://esn.com.vn/";
	HttpHelper helper = new HttpHelper(URL);
	EsnWebServices service = new EsnWebServices(NAMESPACE, URL);

	UsersManager usersManager = new UsersManager();

	public EventsManager() {

	}

	public Events[] getAvailableEvents() throws JSONException, IOException,
			IllegalArgumentException, IllegalAccessException, ParseException {
		Events[] events = null;
		JSONObject params = new JSONObject();
		params.put("pageNum", 1);
		params.put("pageSize", 10);
		JSONObject result = helper
				.invokeWebMethod("GetAvailableEvents", params);
		if (result != null) {
			if (result.has("d")) {
				JSONArray jsonEvents = result.getJSONArray("d");
				events = new Events[jsonEvents.length()];
				for (int i = 0; i < jsonEvents.length(); i++) {
					JSONObject jsonEvent = jsonEvents.getJSONObject(i);
					Events event = new Events();
					Utils.JsonToObject(jsonEvent, event);
					events[i] = event;
				}
			}
		}
		return events;
	}

	public ArrayList<Events> getAvailableEventsList(int pageIndex, int pageSize)
			throws JSONException, IOException, IllegalArgumentException,
			IllegalAccessException, ParseException {
		ArrayList<Events> listEvent = new ArrayList<Events>();

		JSONObject params = new JSONObject();

		params.put("pageNum", pageIndex);
		params.put("pageSize", pageSize);

		JSONObject result = helper
				.invokeWebMethod("GetAvailableEvents", params);

		if (result != null) {
			if (result.has("d")) {
				JSONArray jsonCall = result.getJSONArray("d");

				for (int i = 0; i < jsonCall.length(); i++) {
					JSONObject jsonEvent = jsonCall.getJSONObject(i);
					Events event = new Events();
					Utils.JsonToObject(jsonEvent, event);
					listEvent.add(event);
				}
			}
		}

		return listEvent;
	}

	public Events[] getEventsAround(double lat, double log, double radius,
			String filter) throws JSONException, IOException,
			IllegalArgumentException, IllegalAccessException, ParseException {
		Events[] events = null;
		JSONObject params = new JSONObject();
		params.put("lat", lat);
		params.put("lon", log);
		params.put("radius", radius);
		params.put("filter", filter);
		// get soap result
		JSONObject response = helper.invokeWebMethod("GetListEventsAround",
				params);
		// Log.d("esn", response.toString());
		if (response != null) {
			// get event Array
			JSONArray eventArray = response.getJSONArray("d");
			// get array count
			int eventCount = eventArray.length();
			// init arrays
			events = new Events[eventCount];

			for (int i = 0; i < eventCount; i++) {
				JSONObject eventJSON = eventArray.getJSONObject(i);
				Events event = new Events();
				Utils.JsonToObject(eventJSON, event);
				events[i] = event;
			}
		}
		return events;
	}

	public Events[] lookingAheadEvents(double lat, double log, double radius,
			String filter) throws JSONException, IOException, IllegalArgumentException, IllegalAccessException {
		
		Events[] events = null;
		JSONObject params = new JSONObject();
		params.put("lat", lat);
		
		params.put("lon", log);
		params.put("radius", radius);
		params.put("filter", filter);
		// get soap result
		JSONObject response = helper.invokeWebMethod("GetListEventsAround",
				params);
		// Log.d("esn", response.toString());
		if (response != null) {
			// get event Array
			JSONArray eventArray = response.getJSONArray("d");
			// get array count
			int eventCount = eventArray.length();
			// init arrays
			events = new Events[eventCount];

			for (int i = 0; i < eventCount; i++) {
				JSONObject eventJSON = eventArray.getJSONObject(i);
				Events event = new Events();
				Utils.JsonToObject(eventJSON, event);
				events[i] = event;
			}
		}
		return events;
	}

	public Events setEntity(Events event) throws JSONException, IOException {

		JSONObject params = new JSONObject();
		params.put("accountID", event.AccID);
		params.put("eventTypeID", event.EventTypeID);
		params.put("title", event.Title);
		params.put("description", event.Description);
		params.put("picture", (event.Picture == null) ? "" : event.Picture);
		params.put("latitude", event.EventLat);
		params.put("longitude", event.EventLng);
		params.put("shareType", event.ShareType);
		JSONObject response = helper.invokeWebMethod("CreateEvent", params);
		if (response != null) {
			int id = response.getInt("d");
			event.EventID = id;
		} else {
			event = null;
		}
		return event;

	}

	public Events retrieve(int eventId) throws JSONException, IOException,
			IllegalArgumentException, IllegalAccessException, ParseException {
		Events event = new Events();

		JSONObject params = new JSONObject();

		params.put("id", eventId);

		JSONObject response = helper.invokeWebMethod("RetrieveJSON", params);
		if (response != null) {
			JSONObject eventJson = response.getJSONObject("d");
			Utils.JsonToObject(eventJson, event);

			event.user = usersManager.RetrieveById(event.AccID);

		} else {
			event = null;
		}
		return event;
	}

	public int like(int eventId, int accID) throws JSONException, IOException {
		JSONObject params = new JSONObject();
		params.put("eventId", eventId);
		params.put("accountId", accID);

		JSONObject response = helper.invokeWebMethod("Like", params);
		if (response != null) {
			int like = response.getInt("d");
			return like;
		}
		return -1;
	}

	public int dislike(int eventId, int accID) throws JSONException,
			IOException {
		JSONObject params = new JSONObject();
		params.put("eventId", eventId);
		params.put("accountId", accID);

		JSONObject response = helper.invokeWebMethod("Dislike", params);
		if (response != null) {
			int like = response.getInt("d");
			return like;
		}
		return -1;
	}

	public boolean isLiked(int eventId, int accID) throws JSONException,
			IOException {

		JSONObject params = new JSONObject();
		params.put("eventId", eventId);
		params.put("accountId", accID);

		JSONObject response = helper.invokeWebMethod("IsLiked", params);
		if (response != null) {

			return response.getBoolean("d");
		}
		return false;
	}

	public boolean isDisliked(int eventId, int accID) throws JSONException,
			IOException {

		JSONObject params = new JSONObject();
		params.put("eventId", eventId);
		params.put("accountId", accID);
		JSONObject response = helper.invokeWebMethod("IsDislike", params);

		if (response != null) {

			return response.getBoolean("d");
		}
		return false;
	}

	public Events[] getEventsAround(GeoPoint lastMapCenter) {

		return null;
	}

	public String getFilterString(Sessions session) {
		return session.get("filterString", "");
	}

	public Boolean NewFeedback(int eventId, int accId, String title,
			String content) throws JSONException, IOException {
		JSONObject params = new JSONObject();
		params.put("eventId", eventId);
		params.put("accountId", accId);
		params.put("title", title);
		params.put("content", content);

		JSONObject response = helper.invokeWebMethod("NewFeedback", params);
		if (response != null) {

			return response.getBoolean("d");
		}
		return true;
	}

	public ArrayList<Events> getListEventsFriends(int accID, int pageIndex,
			int pAGE_SIZE) {
		// TODO Auto-generated method stub
		return null;
	}
}
