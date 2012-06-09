package esn.activities;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionStore;
import com.facebook.android.Util;

import esn.adapters.FacebookConnect;
import esn.adapters.InteractiveArrayAdapter;
import esn.classes.SettingListViewModel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.ArrayAdapter;


public class ProfileActivity extends SherlockActivity implements ActionBar.TabListener  {

	public static final String APP_ID = "152764771505402";
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.profile);	
		
		ShowActionBarMenu();   
		
		mFacebook = new Facebook(APP_ID);
		
		SessionStore.restore(mFacebook,this);
		
		if(mFacebook.isSessionValid())
		{
			Util.showAlert(this, "A", "Co sess");
		}	
		
	}
	private void ShowActionBarMenu() {
		
		getSupportActionBar().setDisplayShowHomeEnabled(false);
        
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        for(int i=1;i<=2;i++)
        {
        	ActionBar.Tab tab = getSupportActionBar().newTab();
        	
        	if(i==1)
        	{
        		tab.setText("Profile");
        	}        	
        	else
        	{
        		tab.setText("Setting");
        	}
        	
        	tab.setTabListener(this);
        	
        	getSupportActionBar().addTab(tab);
        } 		
	}
	
	

	private SettingListViewModel get(String s) {
		return new SettingListViewModel(s);
	}	
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if(tab.getText().equals("Profile"))
		{			
			LinearLayout l1 = (LinearLayout)findViewById(R.id.lProfile);
			l1.setVisibility(View.VISIBLE);
			
			LinearLayout l2 = (LinearLayout)findViewById(R.id.lSetting);
			l2.setVisibility(View.INVISIBLE);		
			
		}
		else
		{
			LinearLayout l1 = (LinearLayout)findViewById(R.id.lProfile);
			
			l1.setVisibility(View.INVISIBLE);
			
			LinearLayout l2 = (LinearLayout)findViewById(R.id.lSetting);
			l2.setVisibility(View.VISIBLE);		
			
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	public void LogoutClicked(View view) {
		
		SessionStore.clear(getApplicationContext());
		
		Intent intent = new Intent(this,WelcomeActivity.class);
		startActivityForResult(intent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
}
