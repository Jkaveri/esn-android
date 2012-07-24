package esn.classes;

import java.io.IOException;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import esn.activities.R;
import esn.models.EventType;

public class Maps implements LocationListener {
	private MapView map;
	private MapController mapController;
	private Context context;
	private EsnItemizedOverlay<?> marker;
	private Location currLocation;
	private GeoPoint currPoint;
	private boolean displayCurrentLocation = false;
	private int currMarkerIcon;
	private ProgressDialog dialog;
	protected Geocoder geoCoder;
	private Resources res;
	private List<Overlay> mapOverlays;

	private final int WIFI_ENABLED = 1;
	private final int GPS_ENABLED = 2;
	private LocationManager locationManager;
	private String provider;
	private EsnItemizedOverlay<?> eventMarkers;

	public Maps(Context context, MapView map) {
		this.map = map;
		this.context = context;
		this.res = context.getResources();
		this.mapController = map.getController();
		mapOverlays = map.getOverlays();

		eventMarkers = new EsnItemizedOverlay<EventOverlayItem>(context
				.getResources().getDrawable(EventType.getIconId(0, 3)), map);
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public void setOnTouchEvent(OnTouchListener l) {
		this.map.setOnTouchListener(l);

	}

	public void hideAllBallon() {
		if (marker != null) {
			marker.hideAllBalloons();
		}
	}

	public void setMarker(EventOverlayItem item, int drawable) {
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// instance HelloItemizedOverlay with image
		marker = new EsnItemizedOverlay<EventOverlayItem>(markerIcon, map);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		// set marker
		mapOverlays.add(marker);
	}

	public void setEventMarker(GeoPoint point, String title,
			String description, int eventID, int drawable) {
		// create overlay item
		EventOverlayItem item = new EventOverlayItem(point, title, description,
				eventID);
		// for clear markers
		item.setCanRemove(true);
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// remove before add
		if (item.isCanRemove())
			eventMarkers.removeOverlay(item);
		// add itemOVerlay to itemizedOverlay
		eventMarkers.addOverlay(item, markerIcon);
		// set marker
		List<Overlay> mOverlays = map.getOverlays();
		mOverlays.remove(eventMarkers);
		mOverlays.add(eventMarkers);
		mOverlays = null;
	}

	public void setMarker(GeoPoint point, String title, String subtitle,
			int drawable) {
		// create overlay item
		EventOverlayItem item = new EventOverlayItem(point, title, subtitle);
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// instance HelloItemizedOverlay with image
		marker = new EsnItemizedOverlay<EventOverlayItem>(markerIcon, map);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		// set marker
		mapOverlays.add(marker);
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
		mapController.animateTo(map.getMapCenter());
	}

	public void setMap(MapView map) {
		this.map = map;
	}

	public MapView getMap() {
		return map;
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
		mapController.animateTo(point);

	}

	public GeoPoint getCenter() {
		return map.getMapCenter();
	}

	public int getZoomLevel() {
		// TODO Auto-generated method stub
		return map.getZoomLevel();
	}

	public boolean zoomIn() {
		return mapController.zoomIn();
	}

	public boolean zoomOut() {
		return mapController.zoomOut();
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

		/*
		 * // get best provider Criteria criteria = new Criteria(); // yeu cau
		 * do chinh xac la tuong doi
		 * criteria.setAccuracy(Criteria.ACCURACY_COARSE); // bat buoc provider
		 * cung cap ve do cao criteria.setAltitudeRequired(false); // bat buoc
		 * provider cung cap thong tin ve mang
		 * criteria.setBearingRequired(false); // cho phep provider duoc cap 1
		 * luong chi phi' criteria.setCostAllowed(true);
		 * criteria.setPowerRequirement(Criteria.NO_REQUIREMENT); String
		 * provider = locationManager.getBestProvider(criteria, true);
		 * 
		 * if (isProviderSupported(provider)) { currLocation =
		 * locationManager.getLastKnownLocation(provider); return currLocation;
		 * } else { Toast.makeText( context, context.getResources().getString(
		 * R.string.esn_global_must_enable_gps), Toast.LENGTH_LONG).show(); }
		 * return null;
		 */

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
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.esn_global_must_enable_gps),
					Toast.LENGTH_LONG).show();
		}
		return currLocation;

	}

	public void displayCurrentLocation() {
		currLocation = getCurrentLocation();
		if (currLocation != null) {
			displayCurrentLocation = true;
			currPoint = new GeoPoint((int) (currLocation.getLatitude() * 1E6),
					(int) (currLocation.getLongitude() * 1E6));
			setMarker(currPoint,
					context.getString(R.string.map_current_location_title),
					context.getString(R.string.map_current_location_subtitle),
					currMarkerIcon);
			mapController.animateTo(currPoint);

		} else {
			Toast.makeText(context,
					res.getString(R.string.esn_global_your_location_not_found),
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		if (displayCurrentLocation && currLocation != null) {

			marker.removeOverlay(currPoint);

			currPoint = new GeoPoint((int) (location.getLatitude() * 1E6),
					(int) (location.getLongitude() * 1E6));
			setMarker(currPoint,
					context.getString(R.string.map_current_location_title),
					context.getString(R.string.map_current_location_subtitle),
					currMarkerIcon);
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Toast.makeText(context, "GPS Provider is disabled", Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	public void onProviderEnabled(String stri) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void EnableOnLocationChangedListener() {
		locationManager.removeUpdates(this);
		// set listioner
		locationManager.requestLocationUpdates(provider, 0, 0, this);
	}

	public int getCurrMarkerIcon() {
		return currMarkerIcon;
	}

	public void setCurrMarkerIcon(int currMarkerIcon) {
		this.currMarkerIcon = currMarkerIcon;
	}

}
