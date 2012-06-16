package esn.models;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import esn.classes.EsnWebServices;

public class EventTypeManager {
	final String NAMESPACE = "http://localhost/";
	final String URL = "http://10.0.2.2:3333/EventsWS.asmx";
	EsnWebServices services = new EsnWebServices(NAMESPACE, URL);

	public EventTypeManager() {
		// TODO Auto-generated constructor stub
	}

	public List<EventType> getList() {
		List<EventType> list = new ArrayList<EventType>();
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		SoapObject response = services.InvokeMethod("GetListEventTypes");
		if(response!=null && response.getPropertyCount()>0){
			SoapObject soapListEventType = (SoapObject) response.getProperty(0);
			//duyet tung event type
			for (int i = 0; i < soapListEventType.getPropertyCount(); i++) {
				SoapObject soapEventType = (SoapObject) soapListEventType.getProperty(i);
				EventType type = new EventType();
				for(int j = 0;i<soapEventType.getPropertyCount();j++){
					type.setProperty(j, soapEventType.getProperty(j));
					
				}
				list.add(type);
			}
		}
		return list;
	}
}
