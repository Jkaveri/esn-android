package esn.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RegisterActivity extends Activity {

	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.register);
		
		intent = this.getIntent();
	}
	
	public void CancelClicked(View view) {
		
		setResult(RESULT_CANCELED, intent);
		
		finish();
	}
}