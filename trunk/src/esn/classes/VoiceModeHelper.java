package esn.classes;

import java.util.Timer;
import java.util.TimerTask;

import android.content.res.Resources;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;

import esn.activities.R;
import esn.models.S2TResult;

public class VoiceModeHelper{
	private Timer tmrDynIcon;
	private boolean recording;
	private ImageButton btnRecord;
	private Handler handler;
	private Runnable runPost_Lig;
	private Runnable runPost_Red;
	private VoiceManager voiceManager;
	private Runnable runPost_Mic;
	
	//Thay doi icon lam cho button nhap nhay
	private class IconTask extends TimerTask{
		private boolean type = true;
		
		@Override
		public void run() {
			if (type) {
				handler.post(runPost_Lig);
				type = false;
			} else {
				handler.post(runPost_Red);
				type = true;
			}
		}
		
	}
	
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
		voiceManager.setVoiceHandler(new VoiceHandler() {
			
			@Override
			public void onS2TPostBack(final S2TResult result) {//Goi web service nhan dang giong noi  xong
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						states.setText(result.getType());
					}
				});
			}

			@Override
			public void onStopingRecord() {//Khi noi xong tu stop
				recording = false;
				tmrDynIcon.cancel();
				handler.post(runPost_Mic);
				voiceManager.sendDataToServer();
			}
		});
		
		//*//
		
		
		//Chuyen button thanh icon chiec micro
		runPost_Mic = new Runnable() {
			
			@Override
			public void run() {
				btnRecord.setImageResource(R.drawable.ic_mic_record);
			}
		};
	}

	public void startRecording(){
		recording = true;
		voiceManager.startRecording();
		tmrDynIcon = new Timer();
		tmrDynIcon.scheduleAtFixedRate(new IconTask(), 0, 1000);
	}
	
	public void stopRecording() {
		recording = false;
		voiceManager.stopRecording();
		tmrDynIcon.cancel();
		btnRecord.setImageResource(R.drawable.ic_mic_record);
		voiceManager.sendDataToServer();
	}
	
	public boolean isRecording(){
		return recording;
	}
	
	public void destroy(){
		tmrDynIcon.cancel();
		tmrDynIcon = null;
		voiceManager.destroy();
	}
}
