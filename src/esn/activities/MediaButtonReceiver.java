package esn.activities;

import esn.classes.Maps;
import esn.classes.S2TParser;
import esn.classes.Sessions;
import esn.classes.VoiceListener;
import esn.classes.VoiceManager;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;

public class MediaButtonReceiver extends BroadcastReceiver {

	private static final String TAG_LOG = "MediaButtonReceiver";
	private static final long DOUBLE_CLICK_TIME_OUT = 500;
	public static final String LOG_TAG = null;
	private VoiceManager voiceManager;
	private Maps map;
	private Context context;
	private Sessions session;
	public MediaButtonReceiver() {
		super();

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		if (voiceManager == null) {
			// instance voice manager
			voiceManager = new VoiceManager(context.getResources());
			
			voiceManager.setVoiceListener(new VoiceModeListener());
		
		}
		if(session == null){
			session = Sessions.getInstance(context);
		}
		//instance map
		map = new Maps(context);
		
		Log.d(TAG_LOG, "Fire");
		if (intent.getAction().equals("android.intent.action.MEDIA_BUTTON")) {
			KeyEvent event = (KeyEvent) intent
					.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

			if (event == null) {
				return;
			}

			//int keycode = event.getKeyCode();

			if (event.getAction() == KeyEvent.ACTION_UP) {
				long now = System.currentTimeMillis();
				long delta = now - session.get("media_button_last_time_up", (long)0);
				Log.d(TAG_LOG, "ACTION_UP_" + delta);
				boolean isRecording = session.get("isRecording", false);
				if (delta < DOUBLE_CLICK_TIME_OUT) {
					session.put("media_button_last_time_up", (long)0);
					Log.d(TAG_LOG, "DOUBLE_CLICK");
					
					if (!isRecording) {
						startRecord();
					}
				} else {
					if (isRecording) {
						stopRecord();
					}
				}

				session.put("media_button_last_time_up", now);
			}
		}
	}

	private void startRecord() {
		voiceManager.startRecording();
		session.put("isRecording",true);
	}

	private void stopRecord() {
		voiceManager.stopRecording();
		session.put("isRecording", false);
	}
	private class VoiceModeListener implements VoiceListener {
		// Goi web service nhan dang giong noi xong
		@Override
		public void onS2TPostBack(final S2TParser result) {
			try {
				
				Location currLocation = map.getCurrentLocation();
				if (currLocation == null) {
					voiceManager.play(R.raw.kichhoatgps);
					return;
				}
				if (result.getAction().equals("KICH_HOAT")) {
					voiceManager.play(R.raw.thongbaokichhoat);
				} else if (result.getAction().equals("SAP_TOI")) {
					String filter = "";
					if (!result.getEvent().equals("KHONG")) {
						filter = "type:" + EventType.getID(result.getEvent());
					}

					new LookingEventsThread(currLocation.getLatitude(),
							currLocation.getLongitude(), filter, session.getRadiusForEventAround())
							.start();
				} else if (result.getAction().equals("NULL")) {
					voiceManager.play(R.raw.xinloi);
					/*
					 * 
					 * Utils.showToast(act, "Ko nhan dang duoc",
					 * Toast.LENGTH_SHORT);
					 */
				} else if (result.getAction().equals("ERROR")) {
					voiceManager.play(R.raw.xinloi);
					/*
					 * Utils.showToast(act, "Loi tu webservice",
					 * Toast.LENGTH_SHORT); Log.e(LOG_TAG,
					 * result.getStrRecog());
					 */
				}

				// //////////////////////////////

				// voiceMng.voiceAlertHasEvent(result.getEvent(), "GO_VAP");

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
			session.put("isRecording", false);
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
		this.context.sendBroadcast(broad);
	}
}
