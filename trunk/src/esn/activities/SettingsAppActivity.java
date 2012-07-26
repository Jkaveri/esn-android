package esn.activities;

import java.io.IOException;

import org.json.JSONException;

import com.facebook.android.AccessFaceBookListener;
import com.facebook.android.Facebook;
import com.facebook.android.LoginFaceBookListener;

import esn.adapters.EsnListAdapter;
import esn.classes.Sessions;
import esn.models.Users;
import esn.models.UsersManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsAppActivity extends Activity{

	private EsnListAdapter adapter;
	private Resources res;

	private Sessions session;
	private Context context;
	
	private ProgressDialog dialog;
	private Intent intent;
	
	UsersManager usersManager;
	
	private final String[] FB_PERMISSIONS = { "email","read_friendlists","publish_actions"," publish_stream","user_birthday" };
	
	public static final String APP_ID = "257584821008998";
	
	private Facebook mFacebook;	
	
	String accessToken=null;
	
	String email;
	
	Users users;
	
	Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_app);
		
		res = getResources();
				
		context = this;
		
		session = Sessions.getInstance(context);
		
		handler = new Handler();
		
		mFacebook = new Facebook(APP_ID);
		
		users = new Users();
		usersManager = new UsersManager();
		
		ShowInfoSettingFb();
		ShowInfoSettingLocation();
	}
	
	public void SettingFriendClicked(View v)
	{
		intent = new Intent(this, SettingAppEventActivity.class);
		startActivity(intent);
	}
	
	public void SettingEventCliked(View v)
	{
		intent = new Intent(this, SettingAppEventActivity.class);
		startActivity(intent);
	}
	
	public void SwitchFbClicked(View v)
	{
		Switch sw = (Switch)findViewById(R.id.esn_setting_app_facebook_enable);
		
		if(sw.isChecked())
		{	
			email = session.currentUser.Email;		
			
				new Thread(){
					public void run() {						
						
						try {
							users = usersManager.RetrieveByEmail(email);
							
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									
									accessToken = users.AccessToken;
									
									if(accessToken.isEmpty() || accessToken.equals("_"))
									{
										ConnecToFacebook();										
									}
								}
							});
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
		}
		else
		{
			session.put("app.setting.facebook.enable",false);			
			Toast.makeText(context, res.getString(R.string.esn_setting_app_informationsaved),Toast.LENGTH_SHORT).show();
			return;
		}
	}	
	
	public void CheckLocationClicked(View v)
	{
		CheckBox chk = (CheckBox)findViewById(R.id.esn_setting_app_location_check);
		
		if(chk.isChecked())
		{
			session.put("app.setting.location.location", true);
			Toast.makeText(context, res.getString(R.string.esn_setting_app_informationsaved),Toast.LENGTH_SHORT).show();
		}
		else
		{
			session.put("app.setting.location.location", false);
			Toast.makeText(context, res.getString(R.string.esn_setting_app_informationsaved),Toast.LENGTH_SHORT).show();
		}
	}
		
	public void ShowInfoSettingFb()
	{
		Boolean check = session.get("app.setting.facebook.enable",false);
		
		Switch sw = (Switch)findViewById(R.id.esn_setting_app_facebook_enable);
				
		if(check==true)
		{		
			sw.setChecked(true);		
		}
		else
		{
			sw.setChecked(false);			
		}
	}
	
	public void ShowInfoSettingLocation()
	{
		CheckBox chkbox = (CheckBox)findViewById(R.id.esn_setting_app_location_check);
		
		boolean chk = session.get("app.setting.location.location", false);
		
		if(chk==true)
		{
			chkbox.setChecked(true);
		}
	}
	
	public void ConnecToFacebook()
	{	
		mFacebook.authorize(this, FB_PERMISSIONS, new AccessFaceBookListener(SettingsAppActivity.this,mFacebook));
		
		session.put("app.setting.facebook.enable",true);			
		Toast.makeText(context, res.getString(R.string.esn_setting_app_informationsaved),Toast.LENGTH_SHORT).show();
						
	}
}
