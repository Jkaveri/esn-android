package esn.classes;

import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import esn.activities.R;
import esn.models.S2TResult;

public class VoiceModeHelper{
	private Thread thDynamicIcon;
	private boolean iconChange;
	private Runnable run;
	private ImageButton btnRecord;
	private Handler handler;
	private Runnable runPost_Lig;
	private Runnable runPost_Red;
	private VoiceManager audioMng;
	
	public VoiceModeHelper(Resources resources, ImageButton record, final TextView states){
		this.btnRecord = record;
		audioMng = new VoiceManager(resources);
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
		
		audioMng.setSendDataBack(new IVoiceCallBack() {
			
			@Override
			public void returnCall(final S2TResult result) {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						states.setText(result.getType());
					}
				});
			}
		});
		
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
		audioMng.stopRecording();
		if (thDynamicIcon != null) {
			thDynamicIcon.interrupt();
			thDynamicIcon = null;
			btnRecord.setImageResource(R.drawable.ic_mic_record);
		}
		audioMng.sendDataToServer();
	}

	public void startRecording(){
		audioMng.startRecording();
		thDynamicIcon = new Thread(run);
		thDynamicIcon.start();
	}
	
	public void destroy(){
		audioMng.destroy();
	}
}
