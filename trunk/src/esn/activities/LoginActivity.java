package esn.activities;

import com.facebook.android.Facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends Activity {

	Intent intent;
	
	private final int REQUEST_CODE_CREATE_LOGIN_HOME =1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login);
		
		intent = this.getIntent();
		
		TextView tvForgotPassword = (TextView)findViewById(R.id.tvForgotPassgord);
		
		tvForgotPassword.setText(Html.fromHtml("<a href=\"http://www.esn.com/forgotpassword\">Forgot your password?</a> "));
		
		tvForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());			
	}
	
	public void BackClicked(View view)
	{
		setResult(RESULT_CANCELED, intent);
		
		finish();
	}
	
	public void EmailClicked(View view) {
	
		TextView email = (TextView)findViewById(R.id.txtEmail);
		email.setText("");
	}
	
	public void PasswordClicked(View view) {
		TextView pass = (TextView)findViewById(R.id.txtPass);
		pass.setText("");
	}
	
	public void LoginClicked(View view)
	{
		Intent intentLogin = new Intent(this, HomeActivity.class);
		
		startActivityForResult(intentLogin, REQUEST_CODE_CREATE_LOGIN_HOME);
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	super.onActivityResult(requestCode, resultCode, data);
    }
}
