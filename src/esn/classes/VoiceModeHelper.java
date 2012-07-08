package esn.classes;

import android.os.Handler;
import android.util.Log;
import com.actionbarsherlock.view.MenuItem;

import esn.activities.R;

public class VoiceModeHelper {
	private Thread thDynamicIcon;
	private boolean iconChange;
	private Runnable run;
	private MenuItem menuRecord;
	private Handler handler;
	private Runnable runPost_Lig;
	private Runnable runPost_Red;
	private AudioManager audioMng;
	
	public VoiceModeHelper(){
		audioMng = new AudioManager();
		handler = new Handler();
		
		runPost_Lig = new Runnable() {
			
			@Override
			public void run() {
				menuRecord.setIcon(R.drawable.ic_mic_stop);
			}
		};
		
		runPost_Red = new Runnable() {
			
			@Override
			public void run() {
				menuRecord.setIcon(R.drawable.ic_mic_stop_red);
			}
		};
		
		run = new Runnable() {

			@Override
			public void run() {
				boolean type = true;
				iconChange = true;
				while (iconChange) {
					if (type) {
						handler.post(runPost_Lig);
						type = false;
					} else {
						handler.post(runPost_Red);
						type = true;
					}

					try {
						Thread.sleep(1100);
					} catch (InterruptedException e) {
						Log.i("Dynamic Icon", "Thread on destroy!");
					}
				}
			}
		};
	}

	public void stopRecording() {
		iconChange = false;
		audioMng.recorder.stopRecording();
		if (thDynamicIcon != null) {
			thDynamicIcon.interrupt();
			thDynamicIcon = null;
			menuRecord.setIcon(R.drawable.ic_mic_start);
		}
		audioMng.sendDataToServer();
	}

	public void setMenuItemRecord(MenuItem startRecord) {
		menuRecord = startRecord;
	}
	
	
	public void startRecording(){
		audioMng.recorder.startRecording();
		thDynamicIcon = new Thread(run);
		thDynamicIcon.start();
	}
}
