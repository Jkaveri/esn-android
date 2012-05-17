package esn.models;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class Markers extends ItemizedOverlay<EsnOverlayItem> {

	private Context context;
	private ArrayList<EsnOverlayItem> mOverlayItems = new ArrayList<EsnOverlayItem>();

	public Markers(Context context, Drawable drawable) {
		super(boundCenterBottom(drawable));
		this.context = context;
	}

	@Override
	protected EsnOverlayItem createItem(int index) {
		return mOverlayItems.get(index);
	}

	public void addOverlay(EsnOverlayItem item) {
		mOverlayItems.add(item);
		populate();

	}

	public void removeOverlay(EsnOverlayItem item) {
		mOverlayItems.remove(item);
		populate();
	}

	public void removeOverlay(GeoPoint point) {
		EsnOverlayItem item = new EsnOverlayItem(point,"","");
		mOverlayItems.remove(item);
		populate();
	}

	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		// TODO Auto-generated method stub
		return super.onTap(arg0, arg1);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlayItems.size();
	}

	

}
