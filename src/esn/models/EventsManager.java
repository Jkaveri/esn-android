package esn.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import esn.classes.EsnWebServices;
import esn.classes.HttpHelper;
import esn.classes.Utils;

public class EventsManager {
	private static final String URL = "http://10.0.2.2/esn/EventsWS.asmx";
	private static final String NAMESPACE = "http://esn.com.vn/";
	HttpHelper helper = new HttpHelper(URL);
	EsnWebServices service = new EsnWebServices(NAMESPACE,URL);
	public EventsManager() {

	}

	public Events[] getAvailableEvents() throws JSONException, IOException, IllegalArgumentException, IllegalAccessException {
		Events[] events = null;
		JSONObject params = new JSONObject();
		params.put("pageNum", 1);
		params.put("pageSize", 10);
		JSONObject result = helper.invokeWebMethod("GetAvailableEvents",params);
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

	public List<Events> getEventsAround(double lat, double log, int radius) {
		List<Events> events = null;
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("lat", 0);
		params.put("lon", 10);
		params.put("radius", radius);
		// get soap result
		SoapObject response = service.InvokeMethod("GetListEventsAround",
				params);
		if (response != null && response.getPropertyCount() > 0) {
			// get event Array
			SoapObject eventArray = (SoapObject) response.getProperty(0);
			// get array count
			int eventCount = eventArray.getPropertyCount();
			// init arrays
			events = new ArrayList<Events>();

			for (int i = 0; i < eventCount; i++) {
				// get event
				SoapObject eventSoap = (SoapObject) eventArray.getProperty(i);
				// initialize event
				Events event = new Events();
				// get property count
				int propCount = eventSoap.getPropertyCount();
				for (int j = 0; j < propCount; j++) {
					Object value = eventSoap.getProperty(j);
					if (value != null) {
						event.setProperty(j, value);
					}
				}
				events.add(event);
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
		params.put("picture", event.Picture);
		params.put("latitude", event.EventLat);
		params.put("longitude", event.EventLng);
		params.put("shareType", event.ShareType);
		JSONObject response = helper.invokeWebMethod("CreateEvent",params);
		if (response != null) {
			int id = response.getInt("d");
			event.EventID = id;
		} else {
			event = null;
		}
		return event;

	}
}
