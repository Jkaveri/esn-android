package esn.activities;

import java.util.Date;
import com.google.android.maps.GeoPoint;

import esn.classes.AudioLibManager;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.classes.VoiceManager;
import esn.classes.removeUTF8;
import esn.models.Events;
import esn.models.EventsManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class EsnLookingAheadEventsServices extends Service implements
		LocationListener {

	public EsnLookingAheadEventsServices() {

	}

	public static final String LOG_TAG = "EsnLookingAheadEventsServices";
	private LocationManager locationManager;
	private Date lastTime = null;
	private static final long TIME_OUT = 300000; // 5min.
	private double radius = 1;// 1km
	private GeoPoint lastPoint = null;
	private LookingEventsThread lookingEventsThread;
	private VoiceManager voiceManager;
	private Sessions sessions;
	private AudioLibManager audioLibManager;
	private EventAlertByAudioReceiver receiver;
	public AudioManager auManager;
	private ComponentName eventReceiver;

	@Override
	public void onCreate() {
		voiceManager = new VoiceManager(getResources());
		sessions = Sessions.getInstance(this.getApplicationContext());
		radius = sessions.getRadiusForEventAround();
		audioLibManager = new AudioLibManager();
		// play thong bao
		voiceManager.playInsiteThread(R.raw.thongbaokichhoat);
		// media button
		if (sessions.getAccessHeadPhone()) {
			auManager = (AudioManager) getSystemService(AUDIO_SERVICE);

			eventReceiver = new ComponentName(getApplicationContext(),
					MediaButtonReceiver.class);
			auManager.registerMediaButtonEventReceiver(eventReceiver);
		}

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		String provider = getBestProvider();

		if (locationManager.isProviderEnabled(provider)) {
			locationManager.requestLocationUpdates(provider, 0, 0, this);
		} else {
			voiceManager.play(R.raw.kichhoatgps);
		}
		// register reciever
		IntentFilter filter = new IntentFilter();
		filter.addAction(VoiceModeActivity.ACTION_EVENT_AUDIO_ALERT);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		filter.setPriority(1000);
		receiver = new EventAlertByAudioReceiver();

		registerReceiver(receiver, filter);

		return START_STICKY;
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
	public void onDestroy() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
		if (voiceManager != null) {
			voiceManager.destroy();
		}
		unregisterReceiver(receiver);
		/*if (sessions.getAccessHeadPhone()) {
			auManager.unregisterMediaButtonEventReceiver(eventReceiver);
			eventReceiver = null;
		}*/
		super.onDestroy();
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
			if (dictance > radius || time > TIME_OUT) {
				// clean up thread before run new thread
				if (lookingEventsThread != null) {
					lookingEventsThread.interrupt();
					lookingEventsThread = null;
				}
				lookingEventsThread = new LookingEventsThread(lat, lng, "",
						radius);
				lookingEventsThread.start();
				lastPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
				lastTime = now;
			}

		} else {

			lastPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
			lastTime = now;

		}

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
		broad.setAction(VoiceModeActivity.ACTION_EVENT_AUDIO_ALERT);
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

					voiceManager.play(R.raw.chuy);
					for (int i = 0; i < events.length; i++) {
						Events event = events[i];
						sendDataToVoiceMode(event);
						Thread.sleep(1000);
					}

					// TODO: can bien lam

					// voiceMng.voiceAlertHasEvent(eventType, "GO_VAP");

				} else {
					voiceManager.play(R.raw.khongcosukiennao);
				}

			} catch (Exception e) {
				voiceManager.play(R.raw.kichhoatmang);
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

	public class EventAlertByAudioReceiver extends BroadcastReceiver {

		private Events event;
		private boolean played;
		private String street;

		@Override
		public void onReceive(Context context, Intent intent) {
			event = new Events();
			// get data
			event.EventLat = intent.getDoubleExtra("latitude", 0);
			event.EventLng = intent.getDoubleExtra("longtitude", 0);
			event.Title = intent.getStringExtra("eventTitle");
			event.Description = intent.getStringExtra("eventDescription");
			event.EventID = intent.getIntExtra("eventId", 0);
			event.EventTypeID = intent.getIntExtra("eventTypeId", 0);

			// play audio
			// lay dia chi cua event
			Address address = event.getAddress(context);
			// flag, da phat
			played = false;
			// street
			street = address.getThoroughfare();
			if (street != null) {
				street = removeUTF8.execute(street).toLowerCase();
			}
			if (street != null && audioLibManager.isExistStreetAudio(street)) {
				voiceManager.voiceAlertHasEvent(
						String.valueOf(event.EventTypeID), street);
				// bat co
				played = true;
			} else {

				// lay so dong dia chi
				int addressLineCount = address.getMaxAddressLineIndex();
				// duyet cac dong dia chi
				// neu phat hien 1 dong nao la duong (co tong tai trong
				// thu vien audio thi play)

				for (int j = 0; j < addressLineCount; j++) {
					// lay ten duong
					street = removeUTF8.execute(address.getAddressLine(j))
							.toLowerCase();
					if (street != null
							&& audioLibManager.isExistStreetAudio(street)) {
						voiceManager.voiceAlertHasEvent(
								String.valueOf(event.EventTypeID), street);
						// bat co
						played = true;
						break;
					}
				}
			}

			if (!played) {
				voiceManager.play(audioLibManager.getHasEventTypeAudio(String
						.valueOf(event.EventTypeID)));

			}
		}
	}

}
