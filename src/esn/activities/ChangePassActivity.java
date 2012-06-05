package esn.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

public class ChangePassActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.change_pass);
		
	}
	
	public void changePassCancelClicked(DialogInterface dialog, int which) {
		
		
	}
}
