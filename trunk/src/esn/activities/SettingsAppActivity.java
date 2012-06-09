package esn.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class SettingsAppActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_app);
	}
	
}
