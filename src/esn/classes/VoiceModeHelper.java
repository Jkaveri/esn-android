package esn.classes;


import com.google.android.maps.MapView;
import android.content.res.Resources;
import android.widget.ImageButton;
import android.widget.TextView;

public class VoiceModeHelper{
	public static final int STATE_RECORDING = 1;
	public static final int STATE_LOADING = 2;
	public static final int STATE_STOPED = 3;
	
	private int recordState = STATE_STOPED;
	private VoiceManager voiceMng;
	private DynamicIconRecord dynIcon;
	
	public VoiceModeHelper(Resources resources, ImageButton btnRecord, final TextView states, MapView maps){
		voiceMng = new VoiceManager(resources);
		dynIcon = new DynamicIconRecord(btnRecord);
		
		//////////////////////////////////////////////////////
		//Thiet dat goi lai khi thuc thi xong den voiceMng
		voiceMng.setVoiceListener(new VoiceListener() {
			
			@Override
			public void onS2TPostBack(final S2TParser result) {//Goi web service nhan dang giong noi  xong
				dynIcon.handler.post(new Runnable() {
					
					@Override
					public void run() {
						states.setText(result.getStrRecog());
					}
				});
				
				dynIcon.stopIconRecord();//Ws post back
				////////////////////////////////
				
				
				//Example
//				if(result.getAction().equals("KICH_HOAT")){
//					voiceMng.voiceAlertActivate(result.getEvent());
//				}else if(result.getAction().equals("SAP_TOI")){
//					//Gia su co su kien tai Go Vap
//					voiceMng.voiceAlertHasEvent(result.getEvent(), "GO_VAP");
//					voiceMng.voiceAlertHasEvent("LO_DAT", "GO_VAP");
//					voiceMng.voiceAlertHasEvent("KET_XE", "HANG_XANH");
//				}
				
				
				
				/////////////////////////				
				recordState = STATE_STOPED;
			}

			@Override
			public void onStopedRecord() {//Khi noi xong tu stop
				dynIcon.startIconLoading();//Loading cho ws post back
				recordState = STATE_LOADING;
			}
		});
		//*//
	}
	
	public void startRecording(){
		voiceMng.startRecording();
		dynIcon.startIconRecord();
		recordState = STATE_RECORDING;
	}
	
	public void stopRecording() {
		voiceMng.stopRecording();//Goi ham nay se phat sinh su kien onStopedRecord()
	}
	
	public int getRecordState(){
		return recordState;
	}
	
	public void destroy(){
		dynIcon.destroy();
		voiceMng.destroy();
	}
}
