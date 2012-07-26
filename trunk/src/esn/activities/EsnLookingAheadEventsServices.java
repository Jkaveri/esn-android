package esn.activities;

import java.util.Date;
import com.google.android.maps.GeoPoint;

import esn.activities.VoiceModeActivity.EventAlertReciever;
import esn.classes.Utils;
import esn.models.Events;
import esn.models.EventsManager;
import android.app.IntentService;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class EsnLookingAheadEventsServices extends IntentService implements
		LocationListener {

	public EsnLookingAheadEventsServices() {
		super("EsnLookingAheadEventsServices");

	}

	public static final String LOG_TAG = null;
	private LocationManager locationManager;
	private Date lastTime = null;
	private static final long TIME_OUT = 180000; // 3min.
	private static final double DICTANCE = 5;// 0.5km
	private GeoPoint lastPoint = null;
	private LookingEventsThread lookingEventsThread;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		String provider = getBestProvider();

		if (locationManager.isProviderEnabled(provider)) {
			locationManager.requestLocationUpdates(provider, 0, 0, this);
		} else {

		}
		return START_NOT_STICKY;
	}

	private String getBestProvider() {
		// get best provider
		Criteria criteria = new Criteria();
		// yeu cau do chinh xac la tuong doi
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// bat buoc provider cung cap ve do cao
		criteria.setAltitudeRequired(false);
		// bat buoc provider cung cap thong tin ve mang
		criteria.setBearingRequired(false);
		// cho phep provider duoc cap 1 luong chi phi'
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

		return locationManager.getBestProvider(criteria, true);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(LOG_TAG, "Location Changed");
		Date now = new Date();
		double lat = location.getLatitude();
		double lng = location.getLongitude();

		if (lastTime != null && lastPoint != null) {
			// Dictance
			GeoPoint currPoint = new GeoPoint((int) (lat * 1E6),
					(int) (lng * 1E6));

			double dictance = Utils.distanceOfTwoPoint(lastPoint, currPoint);
			Log.d(LOG_TAG, String.valueOf(dictance));
			// time
			long time = Utils.calculateTime(lastTime, now);
			Log.d(LOG_TAG, String.valueOf(time));
			// neu khoang cach lon hon khoang cach nhat dinh
			// hoac sau 1 khoang thoi gian thi looking for events
			if (dictance > DICTANCE || time > TIME_OUT) {
				// clean up thread before run new thread
				if (lookingEventsThread != null) {
					lookingEventsThread.interrupt();
					lookingEventsThread = null;
				}
				lookingEventsThread = new LookingEventsThread(lat, lng, "",
						DICTANCE);
				lookingEventsThread.start();
			}

		} else {
			// clean up thread before run new thread
			if (lookingEventsThread != null) {
				lookingEventsThread.interrupt();
				lookingEventsThread = null;
			}
			lookingEventsThread = new LookingEventsThread(lat, lng, "",
					DICTANCE);
			lookingEventsThread.start();

			
		}
		lastPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		lastTime = now;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void sendDataToVoiceMode(Events event) {
		// send broad cast
		Intent broad = new Intent();
		broad.setAction(EventAlertReciever.ACTION_RESP);
		broad.addCategory(Intent.CATEGORY_DEFAULT);
		broad.putExtra("eventId", event.EventID);
		broad.putExtra("eventTitle", event.Title);
		broad.putExtra("eventDescription", event.Title);
		broad.putExtra("level", event.getLevel());
		broad.putExtra("latitude", event.EventLat);
		broad.putExtra("longtitude", event.EventLng);
		broad.putExtra("eventTypeId", event.EventTypeID);
		sendBroadcast(broad);
	}

	public void sendDataToVoiceMode(Location location) {
		// send broad cast
		Intent broad = new Intent();
		broad.setAction(EventAlertReciever.ACTION_RESP_CURR);
		broad.addCategory(Intent.CATEGORY_DEFAULT);
		broad.putExtra("lat", location.getLatitude());
		broad.putExtra("long", location.getLongitude());
		sendBroadcast(broad);
	}

	private class LookingEventsThread extends Thread {

		private double lat;
		private double lon;
		private String eventType;
		private double radius;

		public LookingEventsThread(double lat, double lon, String eventType,
				double radius) {
			super();
			this.lat = lat;
			this.lon = lon;
			this.eventType = eventType;
			this.radius = radius;
		}

		@Override
		public void run() {
			try {
				Looper.prepare();
				EventsManager _manager = new EventsManager();
				Events[] events = _manager.lookingAheadEvents(lat, lon, radius,
						eventType);
				
				if (events != null && events.length > 0) {
					// send broad cast

					for (int i = 0; i < events.length; i++) {
						Events event = events[i];

						sendDataToVoiceMode(event);
						Thread.sleep(500);
					}

					// TODO: can bien lam

					// voiceMng.voiceAlertHasEvent(eventType, "GO_VAP");

				}

			} catch (Exception e) {
				// TODO: thong bao cho nguoi ta co loi bang giong noi
				Log.e(LOG_TAG, e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

	}

}
