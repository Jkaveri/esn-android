package esn.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import esn.classes.Maps;
import esn.classes.Sessions;
import esn.classes.VoiceModeHelper;
import esn.models.EventType;
import esn.models.Events;

public class VoiceModeActivity extends MapActivity {
	//private static final String LOG_TAG = "VoiceModeActivity";
	public static final String ACTION_EVENT_AUDIO_ALERT = "esn.actions.ACTION_EVENT_AUDIO_ALERT";

	private VoiceModeHelper helper;
	private EventAlertOnMapReceiver receiver;
	private Maps map;

	public boolean firstHeadPhoneConnect = false;
	private boolean servicesAct = false;
	private Sessions session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.esn_voice_mode);
		this.setTitle(getString(R.string.esn_voicemode_title));
		session = Sessions.getInstance(this);
		TextView txtStates = (TextView) this
				.findViewById(R.id.esn_voicemode_txt_state);
		txtStates.setSelected(true);

		ImageButton btnRecord = (ImageButton) findViewById(R.id.esn_voicemode_btn_record);
		ImageButton btnServices = (ImageButton) findViewById(R.id.esn_voicemode_btn_Service);
		MapView mapView = (MapView) findViewById(R.id.esn_google_maps_state);
		map = new Maps(this, mapView);
		map.displayCurrentLocationMarker();

		helper = new VoiceModeHelper(this, btnServices, btnRecord, txtStates, map);
		// register reciever
		IntentFilter filter = new IntentFilter();
		filter.addAction(VoiceModeActivity.ACTION_EVENT_AUDIO_ALERT);
		filter.addAction(Intent.ACTION_HEADSET_PLUG);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		//filter.setPriority(1000);
		receiver = new EventAlertOnMapReceiver();
		registerReceiver(receiver, filter);
	}

	public void btnRecordClick(View view) {
		if (helper.getRecordState() == VoiceModeHelper.STATE_RECORDING) {
			helper.stopRecording();
		} else if (helper.getRecordState() == VoiceModeHelper.STATE_STOPED) {
			helper.startRecording();
		}
	}

	public void btnDetectMyLocation(View view) {
		map.displayCurrentLocationMarker();
	}

	public void btnServiceClicked(View view) {
		if(servicesAct){
			helper.stopService();		
			session.setNotifyEvents(false);//Thong bao cho setting
			servicesAct = false;
		}else{
			servicesAct = true;
			helper.startService();
			session.setNotifyEvents(true);//Thong bao cho setting
		}
	}

	@Override
	public void onDestroy() {
		helper.destroy();
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		firstHeadPhoneConnect = true;

		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public class EventAlertOnMapReceiver extends BroadcastReceiver {

		private static final String LOG_TAG = "EventAlertReciever";

		@Override
		public void onReceive(Context context, Intent data) {

			Log.d(LOG_TAG, "Recieved");
			if (data.getAction().equals(ACTION_EVENT_AUDIO_ALERT)) {
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

			}

		}
	}

	
}
