package esn.models;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import esn.classes.EsnWebServices;

public class EventsManager {
	private EsnWebServices service = new EsnWebServices(
			"http://localhost/",
			"http://10.0.2.2/esn/EventsWS.asmx");

	public EventsManager() {

	}

	public Events[] getAvailableEvents() {
		Events[] events = null;
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("pageNum", 0);
		params.put("pageSize",10);
		// get soap result
		SoapObject response = service.InvokeMethod("GetAvailableEvents",params);
		if (response != null && response.getPropertyCount()>0) {
			// get event Array
			SoapObject eventArray = (SoapObject) response.getProperty(0);
			// get array count
			int eventCount = eventArray.getPropertyCount();
			// init arrays
			events = new Events[eventCount];

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
				events[i] = event;
			}
		}
		return events;
	}
	
	public List<Events> getEventsAround(double lat, double log,int radius){
		List<Events> events = null;
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("lat", 0);
		params.put("lon",10);
		params.put("radius",radius);
		// get soap result
		SoapObject response = service.InvokeMethod("GetListEventsAround",params);
		if (response != null && response.getPropertyCount()>0) {
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
	
	public Events setEntity(Events event) {

		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("accountID", event.AccID);
		params.put("eventTypeID", event.EventTypeID);
		params.put("title", event.Title);
		params.put("description", event.Description);
		params.put("picture", event.Picture);
		params.put("latitude", event.EventLong);
		params.put("longitude", event.EventLat);
		params.put("shareType", event.ShareID);
		SoapObject response = service.InvokeMethod("CreateEvent", params);
		if(response!=null && response.getPropertyCount()>0){
		    SoapObject result = (SoapObject) response.getProperty(0);
			event.EventID = Integer.parseInt(result.getProperty(0).toString());
		}else{
			event = null;
		}
		return event;

	}
}
