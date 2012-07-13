package esn.classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
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
import android.view.DragEvent;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import esn.activities.HomeActivity;
import esn.activities.R;
import esn.models.Events;

public class Maps implements LocationListener {
	private MapView map;
	private MapController mapController;
	private Context context;
	private EsnItemizedOverlay marker;
	private Location currLocation;
	private GeoPoint currPoint;
	private boolean displayCurrentLocation = false;
	private int currMarkerIcon;
	private ProgressDialog dialog;
	private Handler handler;
	protected Geocoder geoCoder;
	private Resources res;
	private List<Overlay> mapOverlays;
	private ArrayList<Events> events;
	public Maps(Context context, MapView map) {
		this.map = map;
		this.context = context;
		this.res = context.getResources();
		this.mapController = map.getController();
		mapOverlays = map.getOverlays();
		handler = new Handler();
		events = new ArrayList<Events>();
		
	}
	public void addEvent(Events event){
		events.add(event);
	}
	public void removeEvent(Events event){
		events.remove(event);
	}
	public Events getEvent(int index){
		return events.get(index);
	}
	
	public void setOnTouchEvent(OnTouchListener l){
		this.map.setOnTouchListener(l);
	
	}
	@SuppressLint("NewApi")
	public void setOnDragListener(OnDragListener l){
		this.map.setOnDragListener(l);
	}
	public void hideAllBallon(){
		if(marker!=null){
			marker.hideAllBalloons();
		}
	}
	
	public void setMarker(EventOverlayItem item, int drawable) {
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// instance HelloItemizedOverlay with image
		marker = new EsnItemizedOverlay(markerIcon, map);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		// set marker
		mapOverlays.add(marker);
	}

	public void setMarker(EventOverlayItem item, Drawable drawable) {
		// instance HelloItemizedOverlay with image
		marker = new EsnItemizedOverlay(drawable, map);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		// set marker
		mapOverlays.add(marker);
	}

	public void setMarker(GeoPoint point, String title, String subtitle,int eventId,
			int drawable) {
		// create overlay item
		EventOverlayItem item = new EventOverlayItem(point, title, subtitle,eventId);
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// instance HelloItemizedOverlay with image
		marker = new EsnItemizedOverlay(markerIcon, map);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		// set marker
		mapOverlays.add(marker);
	}
	public void setMarker(GeoPoint point, String title, String subtitle,
			int drawable) {
		// create overlay item
		EventOverlayItem item = new EventOverlayItem(point, title, subtitle);
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// instance HelloItemizedOverlay with image
		marker = new EsnItemizedOverlay(markerIcon, map);
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
		Thread th = new Thread() {
			public void run() {
				try {
					Thread.sleep(500);
					// get geo coder
					geoCoder = new Geocoder(context);
					// search by address

					handler.post(new Runnable() {

						@Override
						public void run() {
							boolean isFirst = true;
							List<Address> listAddress = null;
							try {
								listAddress = geoCoder.getFromLocationName(
										query, 5);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							GeoPoint point = null;
							// with every address
							for (Address address : listAddress) {

								// get point
								point = new GeoPoint((int) (address
										.getLatitude() * 1E6), (int) (address
										.getLongitude() * 1E6));
								// initialize itemOverlay with current point
								EventOverlayItem itemOverlay = new EventOverlayItem(
										point, address.getCountryName(),
										address.getLocality(),0);
								setMarker(itemOverlay, R.drawable.pointer);
								if (isFirst) {
									mapController.setCenter(point);
									isFirst = false;
								}

							}
							dialog.hide();
							mapController.animateTo(map.getMapCenter());
						}

					});

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		th.start();
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
	public Context getContext(){
		return this.context;
	}
	public void setZoom(int level) {
		mapController.setZoom(level);
	}

	public void setCenter(GeoPoint point) {
		mapController.setCenter(point);
	}
	public GeoPoint getCenter(){
		return map.getMapCenter();
	}
	public int getZoomLevel() {
		// TODO Auto-generated method stub
		return map.getZoomLevel();
	}
	public boolean zoomIn(){
		return mapController.zoomIn();
	}
	public boolean zoomOut(){
		return mapController.zoomOut();
	}
	public Location getCurrentLocation() {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		boolean enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// set event listener for current location is change
		
		if (enabled) {
			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, false);
			currLocation = locationManager.getLastKnownLocation(provider);
			handler.post(new LocationUpudate(locationManager));
			
		} else {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.esn_global_must_enable_gps),
					Toast.LENGTH_LONG);
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
					Thread.sleep(500);
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
								Toast.makeText(
										context,
										res.getString(R.string.esn_global_your_location_not_found),
										Toast.LENGTH_SHORT).show();
							}

						}
					});
				} catch (Exception e) {
					e.printStackTrace();
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
			//mapController.animateTo(currPoint);

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

	public int getCurrMarkerIcon() {
		return currMarkerIcon;
	}

	public void setCurrMarkerIcon(int currMarkerIcon) {
		this.currMarkerIcon = currMarkerIcon;
	}
	private class LocationUpudate implements Runnable{
		
		private LocationManager locationManager;
		public LocationUpudate(LocationManager manager) {
			locationManager = manager;
		}
		@Override
		public void run() {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, Maps.this);
		}
	}
	
	
}
