package esn.classes;

import com.google.android.maps.GeoPoint;
import esn.activities.EsnLookingAheadEventsServices;
import esn.activities.R;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceModeHelper {
	public static final int STATE_RECORDING = 1;
	public static final int STATE_LOADING = 2;
	public static final int STATE_STOPED = 3;
	public static final String LOG_TAG = "VoiceModeHelper";

	private int recordState = STATE_STOPED;
	private VoiceManager voiceMng;
	private DynamicIconRecord dynIcon;
	private Maps map;
	private ImageButton btnServices;
	private TextView tvResult;
	private Activity act;
	public Intent service;
	public AudioLibManager audioLibManager;
	private Sessions session;
	private double radius;

	public VoiceModeHelper(Activity _activity, ImageButton btnServices,
			ImageButton btnRecord, TextView tv, Maps _map) {
		act = _activity;
		this.map = _map;
		this.tvResult = tv;
		this.btnServices = btnServices;
		// init voice manager
		voiceMng = new VoiceManager(act.getResources());
		// init animate icon
		dynIcon = new DynamicIconRecord(btnRecord);
		// Thiet dat goi lai khi thuc thi xong den voiceMng
		voiceMng.setVoiceListener(new VoiceModeListener());
		act.getResources();
		//
		session = Sessions.getInstance(_activity.getApplicationContext());
		radius = session.getRadiusForEventAround();
		//
		audioLibManager = new AudioLibManager();
		//

	}

	public void startRecording() {
		voiceMng.startRecording();
		dynIcon.startIconRecord();
		recordState = STATE_RECORDING;
	}

	public void stopRecording() {
		voiceMng.stopRecording();// Goi ham nay se phat sinh su kien
									// onStopedRecord()
	}

	public int getRecordState() {
		return recordState;
	}

	public void destroy() {
		dynIcon.destroy();
		voiceMng.destroy();

		// stopService();
	}

	public void startService() {
		if (service == null) {
			service = new Intent(act.getApplicationContext(),
					EsnLookingAheadEventsServices.class);
		}

		if (!isMyServiceRunning()) {
			dynIcon.handler.post(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(
							act,
							act.getString(R.string.esn_voicemode_services_start),
							Toast.LENGTH_SHORT).show();
					btnServices.setImageResource(R.drawable.ic_event_alert_de);
				}
			});

			act.startService(service);
		} else {
			dynIcon.handler.post(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(
							act,
							act.getString(R.string.esn_voicemode_services_start),
							Toast.LENGTH_SHORT).show();
					btnServices.setImageResource(R.drawable.ic_event_alert_de);
				}
			});
			voiceMng.playInsiteThread(R.raw.hiendangbatchucnang);
		}

		if (!isMyServiceRunning()) {
			voiceMng.playInsiteThread(R.raw.xinloi);
		}
	}

	public void stopService() {
		if (isMyServiceRunning()) {
			if (service == null)
				service = new Intent(act.getApplicationContext(),
						EsnLookingAheadEventsServices.class);
			dynIcon.handler.post(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(
							act,
							act.getString(R.string.esn_voicemode_services_stop),
							Toast.LENGTH_SHORT).show();
					btnServices.setImageResource(R.drawable.ic_event_alert_ac);
				}
			});

			act.stopService(service);
			voiceMng.playInsiteThread(R.raw.datatchucnang);
		}

	}

	private class VoiceModeListener implements VoiceListener {
		// Goi web service nhan dang giong noi xong
		@Override
		public void onS2TPostBack(final S2TParser result) {
			try {
				dynIcon.handler.post(new Runnable() {

					@Override
					public void run() {
						tvResult.setText(result.getStrRecog());
					}
				});

				dynIcon.stopIconRecord();// Ws post back

				Location currLocation = map.getCurrentLocation();
				if (currLocation == null) {
					voiceMng.play(R.raw.kichhoatgps);
					return;
				}
				if (result.getAction().equals("KICH_HOAT")) {
					/* Utils.showToast(act, "KICH_HOAT", Toast.LENGTH_SHORT); */
					startService();

				} else if (result.getAction().equals("SAP_TOI")) {
					String filter = "";
					if (!result.getEvent().equals("KHONG")) {
						filter = "type:" + EventType.getID(result.getEvent());
					}

					new LookingEventsThread(currLocation.getLatitude(),
							currLocation.getLongitude(), filter, radius)
							.start();
				} else if (result.getAction().equals("NULL")) {
					voiceMng.play(R.raw.xinloi);
					/*
					 * Utils.showToast(act, "Ko nhan dang duoc",
					 * Toast.LENGTH_SHORT);
					 */
				} else if (result.getAction().equals("ERROR")) {
					voiceMng.play(R.raw.xinloi);
					/*
					 * Utils.showToast(act, "Loi tu webservice",
					 * Toast.LENGTH_SHORT); Log.e(LOG_TAG,
					 * result.getStrRecog());
					 */
				}

				// //////////////////////////////

				// voiceMng.voiceAlertHasEvent(result.getEvent(), "GO_VAP");

				recordState = STATE_STOPED;
			} catch (Exception e) {
				// TODO: thong bao cho nguoi ta co loi bang giong noi
				/*
				 * Utils.showToast(act,
				 * res.getString(R.string.esn_global_Error),
				 * Toast.LENGTH_SHORT);
				 */
				Log.e(LOG_TAG, e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public void onStopedRecord() {// Khi noi xong tu stop
			dynIcon.startIconLoading();// Loading cho ws post back
			recordState = STATE_LOADING;
		}

	}

	private class LookingEventsThread extends Thread {

		private double lat;
		private double lon;
		private String eventType;
		private double radius;

		public LookingEventsThread(double lat, double lon, String eventType,
				double radius) {

			this.lat = lat;
			this.lon = lon;
			this.eventType = eventType;
			this.radius = radius;
		}

		@Override
		public void run() {
			try {
				EventsManager _manager = new EventsManager();

				Events[] events = _manager.lookingAheadEvents(lat, lon, radius,
						eventType);
				// Log.d(LOG_TAG, "events: " + events.length);
				// kiem tra co event nao ko
				if (events != null && events.length > 0) {
					act.runOnUiThread(new LoadEventsAroundHandler(events));
				} else {
					voiceMng.play(R.raw.khongcosukiennao);
				}

			} catch (Exception e) {
				voiceMng.play(R.raw.kichhoatmang);
				// TODO: thong bao cho nguoi ta co loi bang giong noi
				/*
				 * Utils.showToast(act,
				 * res.getString(R.string.esn_global_Error),
				 * Toast.LENGTH_SHORT);
				 */
				Log.e(LOG_TAG, e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) act
				.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("esn.activities.EsnLookingAheadEventsServices"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Load events around hanlder: run on UI
	 * 
	 * @author JK
	 * 
	 */
	private class LoadEventsAroundHandler implements Runnable {
		private Events[] events;

		public LoadEventsAroundHandler(Events[] events) {
			this.events = events;
		}

		@Override
		public void run() {
			// clear events cu truoc khi add events moi
			map.clearEventMarker();
			// play chu y
			voiceMng.play(R.raw.chuy);

			// duyet tat ca cac event
			for (int i = 0; i < events.length; i++) {
				// lay event
				Events event = events[i];
				// lay dia chi cua event
				Address address = event.getAddress(act.getApplicationContext());
				boolean played = false;
				// street
				String street = address.getThoroughfare();
				if (street != null) {
					street = removeUTF8.execute(street).toLowerCase();

				}
				if (street != null
						&& audioLibManager.isExistStreetAudio(street)) {
					voiceMng.voiceAlertHasEvent(
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
								.replace(" ", "_").toLowerCase();

						if (street != null
								&& audioLibManager.isExistStreetAudio(street)) {
							voiceMng.voiceAlertHasEvent(
									String.valueOf(event.EventID), street);
							// bat co
							played = true;
							break;
						}
					}
				}

				if (!played) {
					voiceMng.play(audioLibManager.getHasEventTypeAudio(String
							.valueOf(event.EventTypeID)));

				}
				// set marker cho event do
				GeoPoint point = new GeoPoint((int) (event.EventLat * 1E6),
						(int) (event.EventLng * 1E6));
				int icon = EventType.getIconId(event.EventTypeID,
						event.getLevel());

				map.setEventMarker(point, event.Title, event.Description,
						event.EventID, icon);
				map.postInvalidate();

			}

			Log.d("esn", "end load events around!");
		}
	}

	public boolean isActivateServices() {
		return isMyServiceRunning();
	}
}
