package esn.classes;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class EsnItemizedOverlays extends ItemizedOverlay<EsnOverlayItem> {
	ArrayList<EsnOverlayItem> items = new ArrayList<EsnOverlayItem>();

	public EsnItemizedOverlays(Drawable marker) {
		super(boundCenter(marker));
	}

	public void addOverlay(EsnOverlayItem item) {
		items.add(item);

		populate();
	}

	public void addOverlay(EsnOverlayItem item, Drawable marker) {
		item.setMarker(boundCenter(marker));
		items.add(item);

		populate();
	}

	public void removeOverlay(EsnOverlayItem item) {

		items.remove(item);
		populate();
	}

	public void removeOverlay(GeoPoint point) {
		OverlayItem item = new OverlayItem(point, "", "");
		items.remove(item);
		populate();
	}

	@Override
	protected EsnOverlayItem createItem(int index) {

		return items.get(index);

	}

	@Override
	public int size() {
		return items.size();

	}

	
}
