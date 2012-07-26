package esn.classes;

import com.google.android.maps.GeoPoint;
import esn.activities.EsnLookingAheadEventsServices;
import esn.activities.R;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
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
	private TextView tvResult;
	private Activity act;
	private Resources res;
	public Intent service;
	public AudioLibManager audioLibManager;

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
		//
		audioLibManager = new AudioLibManager();
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
		stopService();
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
					if (service == null) {
						service = new Intent(act.getApplicationContext(),
								EsnLookingAheadEventsServices.class);
						act.startService(service);
					}
					voiceMng.voiceAlertActivate();

				} else if (result.getAction().equals("SAP_TOI")) {
					String filter = "type:"+EventType.getID(result.getEvent());
					new LookingEventsThread(currLocation.getLatitude(),
							currLocation.getLongitude(), filter, 1)
							.start();
				} else if (result.getAction().equals("NULL")) {
					voiceMng.play(R.raw.xinloi);
					Utils.showToast(act, "Ko nhan dang duoc",
							Toast.LENGTH_SHORT);
				} else if (result.getAction().equals("ERROR")) {
					voiceMng.play(R.raw.xinloi);
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
				Log.d(LOG_TAG, "events: " + events.length);
				// kiem tra co event nao ko
				if (events != null && events.length > 0) {
					act.runOnUiThread(new LoadEventsAroundHandler(events));
				}else{
					
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
			// play chu y
			voiceMng.play(R.raw.chuy);
			// duyet tat ca cac event
			for (int i = 0; i < events.length; i++) {
				// lay event
				Events event = events[i];
				// lay dia chi cua event
				Address address = event.getAddress(act
						.getApplicationContext());
				// flag, da phat
				boolean played = false;
				// duyet cac dong dia chi
				// neu phat hien 1 dong nao la duong (co tong tai trong
				// thu vien audio thi play)

				// play audio
				// lay dia chi cua event
				// flag, da phat
				// street
				String street = address.getThoroughfare();
				if (street != null) {
					street = street.replace("Đường", "").replace(" ", "")
							.toLowerCase();
				}
				if (street != null && audioLibManager.isExistStreetAudio(street)) {
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
						street = address.getAddressLine(j).replace(" ", "")
								.toLowerCase();

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
				//set marker cho event do
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
}
