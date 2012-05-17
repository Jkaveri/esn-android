package esn.models;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import esn.activities.R;

public class Maps implements LocationListener {
	private MapView map;
	private MapController mapController;
	private Context context;
	private Markers marker;
	private Location currLocation;
	private GeoPoint currPoint;
	private boolean displayCurrentLocation = false;
	private int currMarkerIcon;
	private ProgressDialog dialog;
	private Handler handler;

	public Maps(Context context, MapView map) {
		this.map = map;
		this.context = context;
		this.mapController = map.getController();
		handler = new Handler();
	}

	public void setMarker(EsnOverlayItem item, int drawable) {
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// instance HelloItemizedOverlay with image
		marker = new Markers(context, markerIcon);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		// set marker
		map.getOverlays().add(marker);
	}

	public void setMarker(EsnOverlayItem item, Drawable drawable) {
		// instance HelloItemizedOverlay with image
		marker = new Markers(context, drawable);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		// set marker
		map.getOverlays().add(marker);
	}

	public void setMarker(GeoPoint point, String title, String subtitle,
			int drawable) {
		// create overlay item
		EsnOverlayItem item = new EsnOverlayItem(point, title, subtitle);
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// instance HelloItemizedOverlay with image
		marker = new Markers(context, markerIcon);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		// set marker
		map.getOverlays().add(marker);
	}

	public void setMarker(GeoPoint point, String title, String subtitle,
			Drawable drawable) {
		// create overlay item
		EsnOverlayItem item = new EsnOverlayItem(point, title, subtitle);
		// instance HelloItemizedOverlay with image
		marker = new Markers(context, drawable);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		// set marker
		map.getOverlays().add(marker);
	}

	public void search(String query) {

	}

	public void setMap(MapView map) {
		this.map = map;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setZoom(int level) {
		mapController.setZoom(level);
	}

	public void setCenter(GeoPoint point) {
		mapController.setCenter(point);
	}

	public Location getCurrentLocation() {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		boolean enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// set event listener for current location is change
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);
		if (enabled) {
			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, false);
			currLocation = locationManager.getLastKnownLocation(provider);

		}
		return currLocation;

	}

	public void displayCurrentLocation() {
		dialog = new ProgressDialog(context);
		dialog.setTitle(R.string.finding_current_location);
		dialog.show();
		Thread th = new Thread() {
			@Override
			public void run() {

				try {

					handler.post(new Runnable() {

						@Override
						public void run() {
							getCurrentLocation();
							if (currLocation != null) {
								displayCurrentLocation = true;
								currPoint = new GeoPoint(
										(int) (currLocation.getLatitude() * 1E6),
										(int) (currLocation.getLongitude() * 1E6));
								setMarker(
										currPoint,
										context.getString(R.string.map_current_location_title),
										context.getString(R.string.map_current_location_subtitle),
										currMarkerIcon);
								mapController.animateTo(currPoint);
								dialog.hide();
							} else {
								dialog.hide();
							}

						}
					});
				} catch (Exception e) {
					System.out
							.println("-----------------------ERROR-----------------------------");
					System.out.println(e.getMessage());
					System.out.println(e.getStackTrace());
				}
			}
		};
		th.start();

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
			mapController.animateTo(currPoint);

		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Toast.makeText(context, "GPS Provider is disabled", Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public int getCurrMarkerIcon() {
		return currMarkerIcon;
	}

	public void setCurrMarkerIcon(int currMarkerIcon) {
		this.currMarkerIcon = currMarkerIcon;
	}

}
