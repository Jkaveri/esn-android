package esn.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

import esn.classes.VoiceModeHelper;

public class VoiceModeActivity extends MapActivity{
	private VoiceModeHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.esn_voice_mode);
		this.setTitle(getString(R.string.esn_voicemode_title));
		
		TextView txtStates = (TextView) this.findViewById(R.id.esn_voicemode_txt_state);  
		txtStates.setSelected(true);
		
        ImageButton btnRecord = (ImageButton) findViewById(R.id.esn_voicemode_btn_record);
		helper = new VoiceModeHelper(this.getResources(), btnRecord, txtStates);
	}
	
	public void btnRecordClick(View view) {
		if(helper.isRecording()){
			helper.stopRecording();
		}else{
			helper.startRecording();
		}
	}
	
	@Override
	public void onDestroy() {
		helper.destroy();
		super.onDestroy();
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
