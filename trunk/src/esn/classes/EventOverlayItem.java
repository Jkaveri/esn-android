package esn.classes;

import org.kobjects.io.BoundInputStream;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class EventOverlayItem extends OverlayItem {

	private int eventId;
	private String imageUrl;
	private boolean canRemove;
	public EventOverlayItem(GeoPoint point, String title, String description,
			int eventId) {
		super(point, title, description);
		this.eventId = eventId;
	}

	public EventOverlayItem(GeoPoint point, String title, String description) {
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

	public boolean isCanRemove() {
		return canRemove;
	}

	public void setCanRemove(boolean canRemove) {
		this.canRemove = canRemove;
		
	}

	@Override
	public boolean equals(Object o) {
		//android.util.Log.d("equals_method",o.getClass().getName());
		if(o instanceof EventOverlayItem){
		//	android.util.Log.d("equals_method","sosanh");
			EventOverlayItem other = (EventOverlayItem) o;
			GeoPoint oPoint = other.getPoint();
			GeoPoint point = getPoint();
			boolean a = oPoint.getLatitudeE6() == point.getLatitudeE6();
			boolean b = oPoint.getLongitudeE6() == point.getLongitudeE6();
			return a && b;
		}
		return true;
	}
}
