package esn.activities;

import com.actionbarsherlock.app.SherlockMapActivity;

import android.app.Activity;
import android.os.Bundle;

public class VoiceModeActivity extends SherlockMapActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.esn_voice_mode);		
		
		getSupportActionBar().hide();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
