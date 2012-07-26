package esn.classes;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class EsnOverlayItem extends OverlayItem {


	private String imageUrl;
	private boolean canRemove;

	public EsnOverlayItem(GeoPoint point, String title, String description) {
		super(point, title, description);
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
		if(o instanceof EsnOverlayItem){
		//	android.util.Log.d("equals_method","sosanh");
			EsnOverlayItem other = (EsnOverlayItem) o;
			GeoPoint oPoint = other.getPoint();
			GeoPoint point = getPoint();
			boolean a = oPoint.getLatitudeE6() == point.getLatitudeE6();
			boolean b = oPoint.getLongitudeE6() == point.getLongitudeE6();
			return a && b;
		}
		return true;
	}
}
