package esn.activities;



import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.actionbarsherlock.app.SherlockActivity;


import esn.activities.LoginActivity.LoginThread;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.EventType;
import esn.models.EventTypeManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WelcomeScreen extends SherlockActivity {
	private Sessions session;
	private String password;
	private String email;
	private LoadModelsThread loadModelThread;
	private LoginThread loginThread;
	private Resources res;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_screen);
		
		//getActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().hide();
		
		res = getResources();
		if(Utils.isNetworkAvailable(this)){
			initConfig();
			loadModelThread = new LoadModelsThread();
			loadModelThread.start();
		}else{
			ProgressBar progress = (ProgressBar) findViewById(R.id.esn_welcomeScreen_progressBar);
			progress.setVisibility(View.INVISIBLE);
			TextView tvLoading = (TextView) findViewById(R.id.esn_welcomeScreen_tvLoading);
			tvLoading.setVisibility(View.INVISIBLE);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(res.getString(R.string.esn_welcomeScreen_lostConnection));
			builder.setMessage(res.getString(R.string.esn_welcomeScreen_lostConnectionDescription));
			builder.setIcon(R.drawable.ic_alerts_and_states_error);
			builder.setCancelable(false);
			builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			builder.create().show();
		}
		
	}

	private void initConfig() {
		// application session
		session = Sessions.getInstance(this);
	
		// first lauched
		boolean firstLauch = session.get("firstLauch", true);
		
		if (firstLauch) {
			// set radius for load event around
			session.put("radiusEventAround", 2);
			session.get("firstLauch", false);
		}
		
	}

	private void executeLogin() {
		email = session.get("email", null);
		password = session.get("password", null);
		
		loginThread = LoginActivity.createLoginThread(this, email, password, null);
		
		Intent successIntent = new Intent(this, HomeActivity.class);
		Intent failIntent = new Intent(this, LoginActivity.class);
		loginThread.setSuccessIntent(successIntent);
		loginThread.setFailIntent(failIntent);
		loginThread.start();
	}
	private class LoadModelsThread extends Thread {
		@Override
		public void run() {
			Looper.prepare();
			try {
				//load event types
				EventTypeManager manager = new EventTypeManager();
				// get list event type
				ArrayList<EventType> list = manager.getList();
				session.eventTypes = list;
				if(session.logined()){
					executeLogin();	
				}else{
					Intent loginIntent = new Intent(WelcomeScreen.this,WelcomeActivity.class);
					startActivity(loginIntent);
					finish();
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
