package esn.classes;

import java.io.IOException;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import esn.activities.R;
import esn.models.EventType;

public class Maps {
	private MapView mapView;
	private MapController mapController;
	private Context context;
	private EventItemizedOverlays<?> marker;
	private Location currLocation;
	private ProgressDialog dialog;
	protected Geocoder geoCoder;

	private final int WIFI_ENABLED = 1;
	private final int GPS_ENABLED = 2;
	private LocationManager locationManager;
	private String provider;
	private EventItemizedOverlays<EventOverlayItem> eventMarkers;
	private MyLocationOverlay myLocationOverlay;
	private Resources res;

	public Maps(Context context, MapView map) {
		this.mapView = map;
		this.context = context;
		this.mapController = map.getController();
		this.res = context.getResources();

		eventMarkers = new EventItemizedOverlays<EventOverlayItem>(context
				.getResources().getDrawable(EventType.getIconId(0, 3)), map);
		myLocationOverlay = new MyLocationOverlay(context, map);
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public void setOnTouchEvent(OnTouchListener l) {
		this.mapView.setOnTouchListener(l);

	}

	public void hideAllBallon() {
		if (marker != null) {
			marker.hideAllBalloons();
		}
	}

	public void setEventMarker(GeoPoint point, String title,
			String description, int eventID, int drawable) {
		// create overlay item
		EventOverlayItem item = new EventOverlayItem(point, title, description,
				eventID);
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);

		// add itemOVerlay to itemizedOverlay
		eventMarkers.addOverlay(item, markerIcon);
		// set marker
		List<Overlay> mOverlays = mapView.getOverlays();
		mOverlays.remove(eventMarkers);
		mOverlays.add(eventMarkers);
		mOverlays = null;
	}

	public void setMarker(EventOverlayItem item, int drawable) {
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// instance HelloItemizedOverlay with image
		marker = new EventItemizedOverlays<EventOverlayItem>(markerIcon,
				mapView);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		List<Overlay> mOverlays = mapView.getOverlays();
		// set marker
		mOverlays.add(marker);
	}

	public void setMarker(GeoPoint point, String title, String subtitle,
			int drawable) {
		// create overlay item
		EventOverlayItem item = new EventOverlayItem(point, title, subtitle);
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// instance HelloItemizedOverlay with image
		marker = new EventItemizedOverlays<EventOverlayItem>(markerIcon,
				mapView);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		List<Overlay> mOverlays = mapView.getOverlays();
		// set marker
		mOverlays.add(marker);
		mOverlays = null;
	}

	public void displayCurrentLocationMarker() {
		if (!myLocationOverlay.isMyLocationEnabled()) {
			if (!enableMyLocation()) {
				Toast.makeText(context,
						res.getString(R.string.esn_global_must_enable_gps),
						Toast.LENGTH_SHORT).show();
				return;
			}
		}
		List<Overlay> mOverlays = mapView.getOverlays();
		mOverlays.remove(myLocationOverlay);
		mOverlays.add(myLocationOverlay);

		GeoPoint cPoint = myLocationOverlay.getMyLocation();

		animateTo(cPoint);
		mOverlays = null;
	}

	public boolean enableMyLocation() {
		return myLocationOverlay.enableMyLocation();
	}

	public void disableMyLocation() {
		myLocationOverlay.disableMyLocation();
	}

	public void search(final String query) {
		// show dialog
		dialog = ProgressDialog.show(context, "Search...",
				"Searching your request");
		dialog.show();
		// get geo coder
		geoCoder = new Geocoder(context);
		boolean isFirst = true;
		List<Address> listAddress = null;
		try {
			listAddress = geoCoder.getFromLocationName(query, 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GeoPoint point = null;
		// with every address
		for (Address address : listAddress) {

			// get point
			point = new GeoPoint((int) (address.getLatitude() * 1E6),
					(int) (address.getLongitude() * 1E6));
			// initialize itemOverlay with current point
			EventOverlayItem itemOverlay = new EventOverlayItem(point,
					address.getCountryName(), address.getLocality(), 0);
			setMarker(itemOverlay, R.drawable.pointer);
			if (isFirst) {
				mapController.setCenter(point);
				isFirst = false;
			}

		}
		dialog.hide();
		mapController.animateTo(mapView.getMapCenter());
	}

	public void setMap(MapView map) {
		this.mapView = map;
	}

	public MapView getMap() {
		return mapView;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return this.context;
	}

	public void setZoom(int level) {
		mapController.setZoom(level);
	}

	public void setCenter(GeoPoint point) {
		mapController.setCenter(point);
	}

	public void animateTo(GeoPoint point) {
		if (point != null) {
			mapController.animateTo(point);
		}

	}

	public GeoPoint getCenter() {
		return mapView.getMapCenter();
	}

	public int getZoomLevel() {
		return mapView.getZoomLevel();
	}

	public boolean zoomIn() {
		return mapController.zoomIn();
	}

	public boolean zoomOut() {
		return mapController.zoomOut();
	}
	public void postInvalidate(){
		mapView.postInvalidate();
	}
	public int getSupportProvider() {

		boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean wifiEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		return (wifiEnabled) ? WIFI_ENABLED : (gpsEnabled) ? GPS_ENABLED : 0;

	}

	public boolean isProviderSupported(String provider) {
		return locationManager.isProviderEnabled(provider);
	}

	public Location getCurrentLocation() {

		currLocation = null;

		// get support provider
		int providerEnabled = getSupportProvider();
		// set event listener for current location is change
		// neu wifi support
		if (providerEnabled == WIFI_ENABLED) { // provider
			provider = LocationManager.NETWORK_PROVIDER;
			currLocation = locationManager.getLastKnownLocation(provider);

		} else // neu gps support
		if (providerEnabled == GPS_ENABLED) {
			// get provider
			provider = LocationManager.GPS_PROVIDER;
			currLocation = locationManager.getLastKnownLocation(provider);
		} else {
			Toast.makeText(context,
					res.getString(R.string.esn_global_must_enable_gps),
					Toast.LENGTH_LONG).show();
		}
		return currLocation;

	}

	public void EnableOnLocationChangedListener() {
		// set listioner
	}
}
