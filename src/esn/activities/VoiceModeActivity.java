package esn.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import esn.classes.AudioLibManager;
import esn.classes.Maps;
import esn.classes.VoiceManager;
import esn.classes.VoiceModeHelper;
import esn.classes.removeUTF8;
import esn.models.EventType;
import esn.models.Events;

public class VoiceModeActivity extends MapActivity {
	private VoiceModeHelper helper;
	private EventAlertReciever receiver;
	private Maps map;
	private AudioLibManager audioLibManager;
	private VoiceManager voiceMng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.esn_voice_mode);
		this.setTitle(getString(R.string.esn_voicemode_title));

		TextView txtStates = (TextView) this
				.findViewById(R.id.esn_voicemode_txt_state);
		txtStates.setSelected(true);

		ImageButton btnRecord = (ImageButton) findViewById(R.id.esn_voicemode_btn_record);
		MapView mapView = (MapView) findViewById(R.id.esn_google_maps_state);
		map = new Maps(this, mapView);
		map.displayCurrentLocationMarker();

		helper = new VoiceModeHelper(this, btnRecord, txtStates, map);
		// instance for voice
		audioLibManager = new AudioLibManager();
		voiceMng = new VoiceManager(getResources());
		// register reciever
		IntentFilter filter = new IntentFilter();
		filter.addAction(EventAlertReciever.ACTION_RESP);
		filter.addAction(EventAlertReciever.ACTION_RESP_CURR);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new EventAlertReciever();
		registerReceiver(receiver, filter);
	}

	public void btnRecordClick(View view) {
		if (helper.getRecordState() == VoiceModeHelper.STATE_RECORDING) {
			helper.stopRecording();
		} else if (helper.getRecordState() == VoiceModeHelper.STATE_STOPED) {
			helper.startRecording();
		}
	}

	@Override
	public void onDestroy() {
		helper.destroy();
		unregisterReceiver(receiver);
		
		super.onDestroy();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public class EventAlertReciever extends BroadcastReceiver {
		public static final String ACTION_RESP = "esn.activities.EVENT_ALERT";
		public static final String ACTION_RESP_CURR = "esn.activities.CURRENT_LOCATION_CHANGED";
		private static final String LOG_TAG = "EventAlertReciever";

		@Override
		public void onReceive(Context context, Intent data) {
			Log.d("id", "Recieved");
			if (data.getAction().equals(ACTION_RESP)) {
				// data: lat, long, title, description, id
				Log.d(LOG_TAG, String.valueOf(data.getIntExtra("eventId", 0)));
				Events event = new Events();
				// get data
				event.EventLat = data.getDoubleExtra("latitude", 0);
				event.EventLng = data.getDoubleExtra("longtitude", 0);
				event.Title = data.getStringExtra("eventTitle");
				event.Description = data.getStringExtra("eventDescription");
				event.EventID = data.getIntExtra("eventId", 0);
				event.EventTypeID = data.getIntExtra("eventTypeId", 0);

				int level = data.getIntExtra("level", 0);
				// set marker
				GeoPoint point = event.getPoint();
				int drawable = EventType.getIconId(event.EventTypeID, level);

				map.setEventMarker(point, event.Title, event.Description,
						event.EventID, drawable);

				map.postInvalidate();
				// play audio
				// lay dia chi cua event
				Address address = event.getAddress(context);
				// flag, da phat
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
						street = removeUTF8.execute(address.getAddressLine(j)).toLowerCase();

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

			} else if (data.getAction().equals(ACTION_RESP_CURR)) {
				double lat = data.getDoubleExtra("lat", 0);
				double lon = data.getDoubleExtra("long", 0);
				GeoPoint currPoint = new GeoPoint((int) (lat * 1E6),
						(int) (lon * 1E6));
				map.setMarker(currPoint, "vi tri cua ban", "",
						R.drawable.ic_current_location);
				Log.d("current location changed",
						String.valueOf(data.getDoubleExtra("lat", 0)));
			}

		}
	}
}
