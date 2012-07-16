package esn.classes;

import java.util.Timer;
import java.util.TimerTask;

import com.google.android.maps.MapView;

import android.content.res.Resources;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import esn.activities.R;

public class VoiceModeHelper{
	private Timer tmrDynIcon;
	private boolean recording;
	private ImageButton btnRecord;
	private Handler handler;
	private Runnable runPost_Lig;
	private Runnable runPost_Red;
	private VoiceProcesser voiceProcesser;
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
	
	public VoiceModeHelper(Resources resources, ImageButton record, final TextView states, MapView maps){
		this.btnRecord = record;
		voiceProcesser = new VoiceProcesser(resources, maps);
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
		
		//Chuyen button thanh icon chiec micro
		runPost_Mic = new Runnable() {
			
			@Override
			public void run() {
				btnRecord.setImageResource(R.drawable.ic_mic_record);
			}
		};
		//*//
		
		//////////////////////////////////////////////////////
		//Thiet dat goi lai khi thuc thi xong den voiceProcesser
		voiceProcesser.setVoiceListener(new VoiceListener() {
			
			@Override
			public void onS2TPostBack(final S2TParser result) {//Goi web service nhan dang giong noi  xong	
				voiceProcesser.onS2TPostback();
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						states.setText(result.getStrRecog());
					}
				});
			}

			@Override
			public void onStopedRecord() {//Khi noi xong tu stop
				tmrDynIcon.cancel();//Ngung che do icon dong
				handler.post(runPost_Mic);//Set icon ve hinh chiec mic
				recording = false;
			}
		});
		//*//
	}

	public void startRecording(){
		voiceProcesser.startRecording();
		tmrDynIcon = new Timer();
		tmrDynIcon.scheduleAtFixedRate(new IconTask(), 0, 1000);
		recording = true;
	}
	
	public void stopRecording() {
		voiceProcesser.stopRecording();//Goi ham nay se phat sinh su kien onStopedRecord()
	}
	
	public boolean isRecording(){
		return recording;
	}
	
	public void destroy(){
		tmrDynIcon.cancel();
		tmrDynIcon = null;
		voiceProcesser.destroy();
	}
}
