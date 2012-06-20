package esn.classes;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class EventOverlayItem extends OverlayItem {

	private int eventId;
	private String imageUrl;
	public EventOverlayItem(GeoPoint point, String title, String description,int eventId) {
		super(point, title, description);
		this.eventId = eventId;
	}
	public EventOverlayItem(GeoPoint point, String title, String description){
		super(point, title, description);
		this.eventId = 0;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
