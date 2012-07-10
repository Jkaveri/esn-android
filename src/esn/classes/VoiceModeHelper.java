package esn.classes;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;

import esn.activities.R;
import esn.activities.VoiceModeActivity;

public class VoiceModeHelper {
	private Thread thDynamicIcon;
	private boolean iconChange;
	private Runnable run;
	private ImageButton btnRecord;
	private Handler handler;
	private Runnable runPost_Lig;
	private Runnable runPost_Red;
	private VoiceManager audioMng;
	
	public VoiceModeHelper(VoiceModeActivity activity){
		audioMng = new VoiceManager(activity);
		handler = new Handler();
		
		runPost_Lig = new Runnable() {
			
			@Override
			public void run() {
				btnRecord.setImageResource(R.drawable.ic_mic_stop_lig);
			}
		};
		
		runPost_Red = new Runnable() {
			
			@Override
			public void run() {
				btnRecord.setImageResource(R.drawable.ic_mic_stop_red);
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
			btnRecord.setImageResource(R.drawable.ic_mic_record);
		}
		audioMng.sendDataToServer();
	}

	public void setBtnRecord(ImageButton btnRecord) {
		this.btnRecord = btnRecord;
	}
	
	
	public void startRecording(){
		audioMng.recorder.startRecording();
		thDynamicIcon = new Thread(run);
		thDynamicIcon.start();
	}
	
	public void destroy(){
		audioMng.destroy();
	}
}
