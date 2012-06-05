package esn.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class EditProfileActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_profile);
		
	}
	
	public void CancelClicked(View button) {
		setContentView(R.layout.option);
	}
}
