package esn.classes;

import com.google.android.maps.GeoPoint;
import esn.activities.EsnServices;
import esn.activities.R;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.location.Location;
import android.os.IBinder;
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
	private TextView tvResult;
	private Activity act;
	private Resources res;
	public Intent service;
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(LOG_TAG, "Service Disconnected" + name.getShortClassName());
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(LOG_TAG, "Service connected");
		}
	};

	public VoiceModeHelper(Activity _activity, ImageButton btnRecord,
			TextView tv, Maps _map) {
		act = _activity;
		this.map = _map;
		this.tvResult = tv;
		// init voice manager
		voiceMng = new VoiceManager(act.getResources());
		// init animate icon
		dynIcon = new DynamicIconRecord(btnRecord);
		// Thiet dat goi lai khi thuc thi xong den voiceMng
		voiceMng.setVoiceListener(new VoiceModeListener());
		// get resource
		res = act.getResources();
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
		if (service != null) {
			act.unbindService(mConnection);
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
				if (result.getAction().equals("KICH_HOAT")) {
					Utils.showToast(act, "KICH_HOAT", Toast.LENGTH_SHORT);
					/*if (service == null) {
						service = new Intent(act.getApplicationContext(),
								EsnServices.class);
						act.bindService(service, mConnection,
								Context.BIND_AUTO_CREATE);
					}*/

				} else if (result.getAction().equals("SAP_TOI")) {
					new LookingEventsThread(currLocation.getLatitude(),
							currLocation.getLongitude(), result.getEvent(), 1)
							.start();
				} else if (result.getAction().equals("NULL")) {
					// TODO: thong bao cho nguoi ta co loi bang giong noi
					Utils.showToast(act, "Ko nhan dang duoc",
							Toast.LENGTH_SHORT);
				} else if (result.getAction().equals("ERROR")) {
					// TODO: thong bao cho nguoi ta co loi bang giong noi
					Utils.showToast(act, "Loi tu webservice",
							Toast.LENGTH_SHORT);
					Log.e(LOG_TAG, result.getStrRecog());
				}

				// //////////////////////////////

				// voiceMng.voiceAlertHasEvent(result.getEvent(), "GO_VAP");

				recordState = STATE_STOPED;
			} catch (Exception e) {
				// TODO: thong bao cho nguoi ta co loi bang giong noi
				Utils.showToast(act, res.getString(R.string.esn_global_Error),
						Toast.LENGTH_SHORT);
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

	public void stopService() {
		if (service != null) {
			act.stopService(service);
		}
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
				EventsManager _manager = new EventsManager();
				Events[] events = _manager.lookingAheadEvents(lat, lon, radius,
						eventType);
				Log.d(LOG_TAG,"events: "+events.length);
				if (events != null && events.length > 0) {
					voiceMng.voiceAlertHasEvent(eventType, "GO_VAP");
					act.runOnUiThread(new LoadEventsAroundHandler(events));
				}

			} catch (Exception e) {
				// TODO: thong bao cho nguoi ta co loi bang giong noi
				Utils.showToast(act, res.getString(R.string.esn_global_Error),
						Toast.LENGTH_SHORT);
				Log.e(LOG_TAG, e.getMessage());
				e.printStackTrace();
			}
		}
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
			for (int i = 0; i < events.length; i++) {
				Events event = events[i];

				GeoPoint point = new GeoPoint((int) (event.EventLat * 1E6),
						(int) (event.EventLng * 1E6));
				int icon = EventType.getIconId(event.EventTypeID,
						event.getLevel());
				map.setEventMarker(point, event.Title, event.Description,
						event.EventID, icon);
			}
			map.getMap().invalidate();
			Log.d("esn", "end load events around!");
		}
	}
}
