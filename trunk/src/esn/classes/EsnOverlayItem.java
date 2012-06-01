package esn.classes;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class EsnOverlayItem extends OverlayItem {

	private String title;
	private GeoPoint point;
	private String subtitle;

	public EsnOverlayItem(GeoPoint point, String title, String subtitle) {
		super(point, title, subtitle);
		this.point = point;
		this.title = title;
		this.subtitle = subtitle;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null && EsnOverlayItem.class != o.getClass())
			return false;
		EsnOverlayItem other = (EsnOverlayItem)o;
		return (other.getPoint().equals(point));
	}

}
