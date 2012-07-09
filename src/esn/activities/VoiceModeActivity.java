package esn.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

public class VoiceModeActivity extends MapActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.esn_voice_mode);
		this.setTitle(getString(R.string.esn_voicemode_title));
		TextView tv = (TextView) this.findViewById(R.id.esn_voicemode_txt_state);  
        tv.setSelected(true);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
