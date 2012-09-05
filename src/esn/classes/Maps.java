package esn.classes;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
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
	private EsnItemizedOverlays marker;
	private Location currLocation;
	private ProgressDialog dialog;
	protected Geocoder geoCoder;

	private final int WIFI_ENABLED = 1;
	private final int GPS_ENABLED = 2;
	private LocationManager locationManager;
	private EventItemizedOverlays<EventOverlayItem> eventMarkers;
	private MyLocationOverlay myLocationOverlay;
	private Resources res;
	private Sessions session;

	public Maps(Context context, MapView map) {
		this.mapView = map;
		this.context = context;
		this.mapController = map.getController();
		this.res = context.getResources();
		this.session = Sessions.getInstance(context);
		eventMarkers = new EventItemizedOverlays<EventOverlayItem>(
				res.getDrawable(EventType.getIconId(0, 3)), map);

		// instance HelloItemizedOverlay with image
		marker = new EsnItemizedOverlays(res.getDrawable(R.drawable.pointer));
		myLocationOverlay = new MyLocationOverlay(context, map);
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public Maps(Context context) {
		this.context = context;
		this.res = context.getResources();
		this.session = Sessions.getInstance(context);
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}
	public void destroy(){
		if(mapView!=null){
			mapView.destroyDrawingCache();
		}
		eventMarkers = null;
		marker = null;
		locationManager = null;
		
	}
	public void setOnTouchEvent(OnTouchListener l) {
		if (mapView != null)
			this.mapView.setOnTouchListener(l);

	}

	public void hideAllBallon() {
		if(eventMarkers!=null && eventMarkers.size() >0){
			eventMarkers.hideAllBalloons();
		}
		if(marker!=null && marker.size()>0){
			
		}
	}

	public void setEventMarker(GeoPoint point, String title,
			String description, int eventID, int drawable) {
		if (mapView == null)
			return;
		List<Overlay> mOverlays = mapView.getOverlays();
		// create overlay item
		EventOverlayItem item = new EventOverlayItem(point, title, description,
				eventID);
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// remove before add
		eventMarkers.removeOverlay(item);
		mOverlays.remove(eventMarkers);
		// add itemOVerlay to itemizedOverlay
		eventMarkers.addOverlay(item, markerIcon);
		// set marker

		mOverlays.add(eventMarkers);
		mOverlays = null;
	}

	public void clearEventMarker() {
		if(eventMarkers!=null)
		eventMarkers.clearOverlay();
	}

	public void setMarker(GeoPoint point, String title, String subtitle,
			int drawable) {
		if (mapView == null)
			return;
		// create overlay item
		EsnOverlayItem item = new EsnOverlayItem(point, title, subtitle);
		// remove befor add
		marker.removeOverlay(item);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		List<Overlay> mOverlays = mapView.getOverlays();
		// set marker
		mOverlays.remove(marker);
		mOverlays.add(marker);
		mOverlays = null;
	}

	public void displayCurrentLocationMarker() {
		if (mapView == null)
			return;
		if (session.getSettingLocation()) {
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
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(R.string.esn_welcomeScreen_GPSLocationService);
			builder.setMessage(R.string.esn_welcomeScreen_GPSLocationService_Confirm);

			builder.setCancelable(false);

			String ok = res.getString(R.string.esn_global_ok);
			String cancel = res.getString(R.string.esn_global_cancel);

			builder.setPositiveButton(ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							session.setSettingLocation(true);
							dialog.dismiss();
						}
					});
			builder.setNegativeButton(cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							session.setSettingLocation(false);
							dialog.dismiss();
						}
					});
			builder.create().show();
		}

	}

	public boolean enableMyLocation() {
		if (mapView == null)
			return false;
		return myLocationOverlay.enableMyLocation();
	}

	public void disableMyLocation() {
		if (mapView == null)
			return;
		myLocationOverlay.disableMyLocation();
	}

	public void search(final String query) {
		if (mapView == null)
			return;
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
			setMarker(point, address.toString(), address.getLocality(),
					R.drawable.pointer);
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
		if (mapView == null)
			return;
		mapController.setZoom(level);
	}

	public void setCenter(GeoPoint point) {
		if (mapView == null)
			return;
		mapController.setCenter(point);
	}

	public void animateTo(GeoPoint point) {
		if (mapView == null)
			return;
		if (point != null) {
			mapController.animateTo(point);
		}

	}

	public GeoPoint getCenter() {
		if (mapView == null)
			return null;
		return mapView.getMapCenter();
	}

	public int getZoomLevel() {
		if (mapView == null)
			return -1;
		return mapView.getZoomLevel();
	}

	public boolean zoomIn() {
		if (mapView == null)
			return false;
		return mapController.zoomIn();
	}

	public boolean zoomOut() {
		if (mapView == null)
			return false;
		return mapController.zoomOut();
	}

	public void postInvalidate() {
		if (mapView == null)
			return;
		mapView.postInvalidate();
	}

	public int getSupportProvider() {

		boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean wifiEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		return (wifiEnabled) ? WIFI_ENABLED : (gpsEnabled) ? GPS_ENABLED : 0;

	}

	public String getBestProvider() {
		Criteria criteria = new Criteria();
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setCostAllowed(true);

		return locationManager.getBestProvider(criteria, true);

	}

	public boolean isProviderSupported(String provider) {
		return locationManager.isProviderEnabled(provider);
	}

	public Location getCurrentLocation() {
		currLocation = null;
		if (session.getSettingLocation()) {

			// get support provider
			String provider = getBestProvider();
			if (provider != null && provider.length() > 0) {
				currLocation = locationManager.getLastKnownLocation(provider);
			} else {
				Toast.makeText(context,
						res.getString(R.string.esn_global_must_enable_gps),
						Toast.LENGTH_LONG).show();
			}

		} else {
			Toast.makeText(context, res
					.getString(R.string.esn_global_must_allow_access_location),
					Toast.LENGTH_LONG).show();
		}

		return currLocation;

	}

	public void EnableOnLocationChangedListener() {
		// set listioner
	}
}
