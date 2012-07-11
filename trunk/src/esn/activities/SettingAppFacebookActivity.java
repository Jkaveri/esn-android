package esn.activities;

import com.facebook.android.Util;

import esn.classes.ImageLoader;
import esn.classes.Sessions;
import esn.models.CommentsManager;
import esn.models.EventsManager;
import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class SettingAppFacebookActivity extends Activity {

	public Handler handler;	
	
	Sessions session;
	
	Context context;
	
	Resources res;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.esn_setting_app_connectfacebook);
		
		context = this;
		
		session = Sessions.getInstance(SettingAppFacebookActivity.this);
		
		handler = new Handler();
		
		res = getResources();
		
		ShowInfo();
	}
	
	public void ShowInfo()
	{
		Boolean check = session.get("app.setting.facebook.enable",false);
		
		Switch sw = (Switch)findViewById(R.id.esn_setting_app_facebook_enable);
		EditText email = (EditText)findViewById(R.id.esn_setting_app_facebook_email);
		EditText password = (EditText)findViewById(R.id.esn_setting_app_facebook_password);
		
		if(check==true)
		{		
			sw.setChecked(true);
			
			email.setText(session.get("app.setting.facebook.email", ""));
			
			String pass = session.get("app.setting.facebook.password", "");
			password.setText(pass);
		}
		else
		{
			sw.setChecked(false);
			
			email.setText("");
			password.setText("");
		}
	}
	
	public void SaveFacebookAccount(View view)
	{
		Switch sw = (Switch)findViewById(R.id.esn_setting_app_facebook_enable);
		
		if(sw.isChecked())
		{
			EditText txtemail = (EditText)findViewById(R.id.esn_setting_app_facebook_email);
			EditText txtpassword = (EditText)findViewById(R.id.esn_setting_app_facebook_password);
			
			String email = txtemail.getText().toString();
			
			if(email.isEmpty())
			{
				Toast.makeText(context, res.getString(R.string.esn_setting_app_enterfacebookemail), Toast.LENGTH_SHORT).show();
				return;
			}
			
			String password = txtpassword.getText().toString();
			
			if(password.isEmpty())
			{
				Toast.makeText(context, res.getString(R.string.esn_setting_app_enterfacebookpassword), Toast.LENGTH_SHORT).show();
				return;
			}
			
			session.put("app.setting.facebook.enable",true);
			session.put("app.setting.facebook.email", email);
			session.put("app.setting.facebook.password", password);
			
			Toast.makeText(context, res.getString(R.string.esn_setting_app_informationsaved),100).show();
		}
		else
		{
			session.put("app.setting.facebook.enable",false);
			
			Toast.makeText(context, res.getString(R.string.esn_setting_app_informationenablebefore),10).show();
			return;
		}
	}	
}