package esn.classes;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

import esn.models.Events;

public class EsnItemizedOverlay<item extends OverlayItem> extends BalloonItemizedOverlay<EventOverlayItem> {

	private ArrayList<EventOverlayItem> items = new ArrayList<EventOverlayItem>();
	private Context c;

	public EsnItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
		setShowClose(false);
		setShowDisclosure(true);
		
	}
    
	public void addOverlay(EventOverlayItem item) {
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
		Toast.makeText(c, "EventID: "+item.getEventId(), Toast.LENGTH_SHORT).show();
		return true;
	}
	
}
