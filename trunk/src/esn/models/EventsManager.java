package esn.models;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import esn.classes.EsnWebServices;

public class EventsManager {
	private EsnWebServices service = new EsnWebServices(
			"http://esnservice.somee.com/",
			"http://esnservice.somee.com/eventservice.asmx");

	public EventsManager() {

	}

	public Events[] getAll() {
		Events[] events = null;
		// get soap result
		SoapObject response = service.InvokeMethod("GetAllEvent");
		if (response != null) {
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

	public Events setEntity(Events event) {

		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("event", event);
		service.addMaping("Events", Events.class);
		SoapObject response = service.InvokeMethod("InserEvent", params);

		return event;

	}
}
