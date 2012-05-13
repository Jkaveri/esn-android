package esn.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends Activity {
    
	private final int REQUEST_CODE_CREATE_LOGIN = 1;
	private final int REQUEST_CODE_CREATE_LOGINFB = 2;
	private final int REQUEST_CODE_CREATE_REGISTER = 3;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
    }
    
    public void LoginClicked(View view)
    {
    	Intent intent = new Intent(this, LoginActivity.class);
    	
    	startActivityForResult(intent, REQUEST_CODE_CREATE_LOGIN);
    }
    
    public void LoginFBClicked(View view)
    {
    	Intent intent = new Intent(this, LoginActivity.class);
    	
    	startActivityForResult(intent, REQUEST_CODE_CREATE_LOGINFB);
    }
    
    public void RegisterClicked(View view)
    {
    	Intent intent = new Intent(this, RegisterActivity.class);
    	
    	startActivityForResult(intent, REQUEST_CODE_CREATE_REGISTER);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    }
}