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
	public static final int STATE_RECORDING = 1;
	public static final int STATE_LOADING = 2;
	public static final int STATE_STOPED = 3;
	
	private Timer tmrDynIcon;
	private int recordState = STATE_STOPED;
	private ImageButton btnRecord;
	private Handler handler;
	private VoiceManager voiceMng;
	
	//Thay doi icon lam cho button nhap nhay
	private class IconTask extends TimerTask{
		private boolean type = true;
		
		@Override
		public void run() {
			if (type) {
				postIcon(R.drawable.ic_mic_stop_lig);
				type = false;
			} else {
				postIcon(R.drawable.ic_mic_stop_red);
				type = true;
			}
		}
		
	}
	
	private class IconTaskLoad extends TimerTask{
		private int key = 1;
		
		@Override
		public void run() {
			switch (key) {
			case 1:
				postIcon(R.drawable.rec_load_1);
				break;
			case 2:
				postIcon(R.drawable.rec_load_2);
				break;
			case 3:
				postIcon(R.drawable.rec_load_3);
				break;
			case 4:
				postIcon(R.drawable.rec_load_4);
				break;
			case 5:
				postIcon(R.drawable.rec_load_5);
				break;
			case 6:
				postIcon(R.drawable.rec_load_6);
				break;
			case 7:
				postIcon(R.drawable.rec_load_7);
				break;
			case 8:
				postIcon(R.drawable.rec_load_8);
				break;
			case 9:
				postIcon(R.drawable.rec_load_9);
				break;
			case 10:
				postIcon(R.drawable.rec_load_10);
				break;
			case 11:
				postIcon(R.drawable.rec_load_11);
				break;
			case 12:
				postIcon(R.drawable.rec_load_12);
				break;
			}
			
			if(key == 12)
				key = 1;
			else
				key++;
		}		
	}
	
	private void postIcon(final int idIcon){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				btnRecord.setImageResource(idIcon);
			}
		});
	}
	
	public VoiceModeHelper(Resources resources, ImageButton record, final TextView states, MapView maps){
		this.btnRecord = record;
		voiceMng = new VoiceManager(resources);
		handler = new Handler();
		
		//////////////////////////////////////////////////////
		//Thiet dat goi lai khi thuc thi xong den voiceMng
		voiceMng.setVoiceListener(new VoiceListener() {
			
			@Override
			public void onS2TPostBack(final S2TParser result) {//Goi web service nhan dang giong noi  xong
				tmrDynIcon.cancel();
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						states.setText(result.getStrRecog());
						btnRecord.setImageResource(R.drawable.ic_mic_record);
					}
				});				
				recordState = STATE_STOPED;
			}

			@Override
			public void onStopedRecord() {//Khi noi xong tu stop
				startIconLoading();				
				recordState = STATE_LOADING;
			}
		});
		//*//
	}
	
	private void startIconLoading(){
		
		if(tmrDynIcon != null){
			tmrDynIcon.cancel();
		}
		tmrDynIcon = new Timer();
		tmrDynIcon.scheduleAtFixedRate(new IconTaskLoad(), 0, 100);
	}

	public void startRecording(){
		voiceMng.startRecording();
		if(tmrDynIcon != null){
			tmrDynIcon.cancel();
		}
		tmrDynIcon = new Timer();
		tmrDynIcon.scheduleAtFixedRate(new IconTask(), 0, 1000);
		recordState = STATE_RECORDING;
	}
	
	public void stopRecording() {
		voiceMng.stopRecording();//Goi ham nay se phat sinh su kien onStopedRecord()
	}
	
	public int getRecordState(){
		return recordState;
	}
	
	public void destroy(){
		if(tmrDynIcon != null){
			tmrDynIcon.cancel();
			tmrDynIcon = null;
		}
		voiceMng.destroy();
	}
}
