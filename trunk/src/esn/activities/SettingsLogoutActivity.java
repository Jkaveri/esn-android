package esn.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Window;

public class SettingsLogoutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);

		//setContentView(R.layout.edit_profile);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.create().show();
	}
}
