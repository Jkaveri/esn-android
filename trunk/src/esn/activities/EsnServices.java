package esn.activities;

import esn.classes.VoiceManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class EsnServices extends Service {

	public static final String LOG_TAG = null;
	public VoiceManager voiceMng;
	public boolean serviceStated = false;
	


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

		voiceMng = new VoiceManager(getResources());
		
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		/*
		 * // register headphone pluged receiver headPhoneReceiver = new
		 * HeadPhonePlugedReceiver(); IntentFilter filter = new IntentFilter();
		 * filter.addAction(Intent.ACTION_HEADSET_PLUG);
		 * filter.addCategory(Intent.CATEGORY_DEFAULT);
		 * registerReceiver(headPhoneReceiver, filter);
		 */

		
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}

	private class HeadPhonePlugedReceiver extends BroadcastReceiver {

		private Intent lookingAheadEventService;

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
				int state = intent.getIntExtra("state", 0);
				Log.d(LOG_TAG,
						"Headset pluged ----------"
								+ intent.getIntExtra("state", 0));
				// connected
				if (state == 1 && !serviceStated) {
					if (lookingAheadEventService == null) {
						lookingAheadEventService = new Intent(
								getApplicationContext(),
								EsnLookingAheadEventsServices.class);

					}
					if (!isMyServiceRunning())
						startService(lookingAheadEventService);
					if (isMyServiceRunning()) {
						serviceStated = true;
						voiceMng.voiceAlertActivate();
					} else {

						voiceMng.play(R.raw.xinloi);
					}

				}
				// disconnected
				else if (state == 0 && serviceStated) {
					if (isMyServiceRunning()) {
						if (lookingAheadEventService == null)
							lookingAheadEventService = new Intent(
									getApplicationContext(),
									EsnLookingAheadEventsServices.class);
						stopService(lookingAheadEventService);
					}
				}

			}
		}

	}

	

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("esn.activities.EsnLookingAheadEventsServices"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
