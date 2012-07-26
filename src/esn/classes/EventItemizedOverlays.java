package esn.classes;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

import esn.activities.EventDetailActivity;

public class EventItemizedOverlays<item extends OverlayItem> extends
		BalloonItemizedOverlay<EventOverlayItem> {

	private ArrayList<EventOverlayItem> items = new ArrayList<EventOverlayItem>();
	private Context c;

	public EventItemizedOverlays(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
		setShowClose(false);
		setShowDisclosure(true);

	}
	
	public void addOverlay(EventOverlayItem item) {
		items.add(item);

		populate();
	}
	public void addOverlay(EventOverlayItem item,Drawable marker) {
		item.setMarker(boundCenter(marker));
		items.add(item);
		
		populate();
	}
	public void removeOverlay(EventOverlayItem item) {

		items.remove(item);
		populate();
	}

	public void removeOverlay(GeoPoint point) {
		OverlayItem item = new OverlayItem(point, "", "");
		items.remove(item);
		populate();
	}

	@Override
	protected EventOverlayItem createItem(int index) {

		return items.get(index);
	}

	@Override
	public int size() {

		return items.size();
	}

	@Override
	protected boolean onBalloonTap(int index, EventOverlayItem item) {
		int id = item.getEventId();
		if (id > 0) {
			// Long mID = Long.parseLong(item.getSnippet());
			String action = Intent.ACTION_PICK;

			Intent intent = new Intent(c, EventDetailActivity.class);
			intent.putExtra("id", id);
			intent.setAction(action);
			c.startActivity(intent);
			hideAllBalloons();
		}
		return true;
	}
	public Drawable setBoundCenter(Drawable drawable){
		return setBoundCenter(drawable);
	}
}
