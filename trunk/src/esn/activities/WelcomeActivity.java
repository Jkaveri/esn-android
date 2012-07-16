package esn.activities;

import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.android.Facebook;
import com.facebook.android.LoginFaceBookListener;
import esn.classes.Sessions;
import esn.models.Users;
import esn.models.UsersManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class WelcomeActivity extends SherlockActivity {

	
	private final String[] FB_PERMISSIONS = { "email","read_friendlists","publish_actions"," publish_stream","user_birthday" };
	// Login by fB
	
	public static final String APP_ID = "257584821008998";
	private Facebook mFacebook;
	protected Sessions session;
	protected Context context;
	
	public Handler handler;
	public Users user;

	UsersManager usersManager = new UsersManager();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		context = this;
		handler = new Handler();
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().hide();
		session = Sessions.getInstance(this); 
		// init facebook
		mFacebook = new Facebook(APP_ID);
		// init facebook runner
		String fbAccessToken = session.get("fb_access_token", null);
		// cho long do
		long fbAccessExpires = session.get("fb_access_token_expires", Long.MIN_VALUE);
		
		if (fbAccessToken != null) {
			mFacebook.setAccessToken(fbAccessToken);
		}
		if (fbAccessExpires != 0) {
			mFacebook.setAccessExpires(fbAccessExpires);
		}
		
		Intent intent = getIntent();
		if(intent!=null){
			String loginResult = intent.getStringExtra("loginResult");
			if(loginResult!=null && loginResult.length() > 0){
				Toast.makeText(this, loginResult, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void btnLoginfbClicked(View view) {
		if (!mFacebook.isSessionValid()) {
			// if access token is expired			
			
			mFacebook.authorize(this, FB_PERMISSIONS, new LoginFaceBookListener(this,mFacebook));
			
		}else{
			
			session.put("isLogined", true);
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
			finish();
		}
	}

	public void LoginClicked(View view) {
		
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
		
	}

	public void RegisterClicked(View view) {
		
		
		if (mFacebook.isSessionValid()) {
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);			
		} else {
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
		}
		finish();
		
	}

	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}
}