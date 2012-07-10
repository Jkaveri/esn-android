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
	private boolean recording;
	private Runnable run;
	private ImageButton btnRecord;
	private Handler handler;
	private Runnable runPost_Lig;
	private Runnable runPost_Red;
	private Runnable runPost_Mic;
	private VoiceManager voiceManager;
	
	public VoiceModeHelper(Resources resources, ImageButton record, final TextView states){
		this.btnRecord = record;
		voiceManager = new VoiceManager(resources);
		handler = new Handler();
		
		
		//Thay doi icon lam cho nut nhap nhay
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
		//*//
		
		
		//Thiet dat goi lai khi thuc thi xong den VoiceManager
		voiceManager.setPostBack(new IVoiceCallBack() {
			
			@Override
			public void s2tHviteCall(final S2TResult result) {//Goi web service nhan dang giong noi  xong
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						states.setText(result.getType());
					}
				});
			}

			@Override
			public void autoStopRecording() {//Khi noi xong tu stop
				recording = false;
				if (thDynamicIcon != null) {
					thDynamicIcon.interrupt();
					thDynamicIcon = null;
					handler.post(runPost_Mic);
				}
				voiceManager.sendDataToServer();
			}
		});
		
		//*//
		
		
		//Thay doi icon lam cho button nhap nhay
		run = new Runnable() {

			@Override
			public void run() {
				boolean type = true;
				while (recording) {
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
		
		
		//Chuyen button thanh icon chiec micro
		runPost_Mic = new Runnable() {
			
			@Override
			public void run() {
				btnRecord.setImageResource(R.drawable.ic_mic_record);
			}
		};
	}

	public void stopRecording() {
		recording = false;
		voiceManager.stopRecording();
		if (thDynamicIcon != null) {
			thDynamicIcon.interrupt();
			thDynamicIcon = null;
			btnRecord.setImageResource(R.drawable.ic_mic_record);
		}
		voiceManager.sendDataToServer();
	}

	public void startRecording(){
		recording = true;
		voiceManager.startRecording();
		thDynamicIcon = new Thread(run);
		thDynamicIcon.start();
	}
	
	public void destroy(){
		voiceManager.destroy();
	}
	
	public boolean isRecording(){
		return recording;
	}
}
