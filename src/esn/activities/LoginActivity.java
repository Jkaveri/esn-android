package esn.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {

	Intent intent;
	
	private final int REQUEST_CODE_CREATE_LOGIN_HOME =1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login);
		
		intent = this.getIntent();
	}
	
	public void BackClicked(View view)
	{
		setResult(RESULT_CANCELED, intent);
		
		finish();
	}
	
	public void LoginClicked(View view)
	{
		Intent intentLogin = new Intent(this, HomeActivity.class);
		
		startActivityForResult(intentLogin, REQUEST_CODE_CREATE_LOGIN_HOME);
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    }
}
