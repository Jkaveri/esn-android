package esn.activities;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.LoginButton;
import com.facebook.android.R;
import com.facebook.android.SessionEvents;
import com.facebook.android.Util;
import com.facebook.android.LoginButton.SessionListener;
import com.facebook.android.SessionEvents.AuthListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class WelcomeActivity extends Activity {
    
	private final int REQUEST_CODE_CREATE_LOGIN = 1;
	private final int REQUEST_CODE_CREATE_LOGIN_FB = 2;
	private final int REQUEST_CODE_CREATE_REGISTER = 3;
	
	// Login by fB
	public static final String APP_ID = "152764771505402";
	private LoginButton mLoginButton;
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;	

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        
        mLoginButton = (LoginButton) findViewById(R.id.btloginfb);
        
        mFacebook = new Facebook(APP_ID);
       	
        mAsyncRunner = new AsyncFacebookRunner(mFacebook);
       	
       	if(mFacebook.isSessionValid())
       	{
       		LoginFBSuccessLater();
       	}       	
       	
       	SessionEvents.addAuthListener(new SampleAuthListener());
       	
       	mLoginButton.init(this, mFacebook);
    }
    
    public class SampleAuthListener implements AuthListener {

        public void onAuthSucceed() {
        	LoginFBSuccess();        	
        }
        public void onAuthFail(String error) {
            
        }
    }
    
    public void LoginClicked(View view)
    {
    	if(!mFacebook.isSessionValid())
    	{
    		Intent intent = new Intent(this, LoginActivity.class);
    		
    		startActivityForResult(intent, REQUEST_CODE_CREATE_LOGIN);
    	}
    	else
    	{
    		Util.showAlert(this, "Error", "Plase Logout First !");
    	}    	
    }
         
    
    
    
    public void RegisterClicked(View view)
    {
    	if(!mFacebook.isSessionValid())
    	{
    		Intent intent = new Intent(this, RegisterActivity.class);
    	   	
    		startActivityForResult(intent, REQUEST_CODE_CREATE_REGISTER);
    	}
    	else
    	{
    		Util.showAlert(this, "Error", "Plase Logout First !");
    	}
    }
    
    private void LoginFBSuccess()
    {
    	
    	Intent intent = new Intent(this, ProfileActivity.class);
    	
    	startActivityForResult(intent, REQUEST_CODE_CREATE_LOGIN_FB);
    }
    
    private void LoginFBSuccessLater()
    {    	
    	Intent intent = new Intent(this, HomeActivity.class);
    	
    	startActivityForResult(intent, REQUEST_CODE_CREATE_LOGIN_FB);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	mFacebook.authorizeCallback(requestCode, resultCode, data);    	
    }  
}