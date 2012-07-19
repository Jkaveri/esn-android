package esn.activities;

import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.android.Facebook;

import esn.classes.LoginThread;
import esn.classes.Maps;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.EventType;
import esn.models.EventTypeManager;
import esn.models.UsersManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
	private final String TAG_LOG = "WELCOME_SCREEN";
	WelcomeScreen context;
	private Facebook fb;
	public double centerLong;
	public double centerLat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_screen);

		// getActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().hide();

		context = this;

		session = Sessions.getInstance(context);
		// get resource
		res = getResources();
		// check ket noi mang
		if (Utils.isNetworkAvailable(this)) {
			
			
			
			initConfig();
			loadModelThread = new LoadModelsThread();
			loadModelThread.start();

		} else {
			// ko co ket noi mang thi thong bao cho nguoi ta biet
			ProgressBar progress = (ProgressBar) findViewById(R.id.esn_welcomeScreen_progressBar);
			progress.setVisibility(View.INVISIBLE);
			TextView tvLoading = (TextView) findViewById(R.id.esn_welcomeScreen_tvLoading);
			tvLoading.setVisibility(View.INVISIBLE);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(res
					.getString(R.string.esn_welcomeScreen_lostConnection));
			builder.setMessage(res
					.getString(R.string.esn_welcomeScreen_lostConnectionDescription));
			builder.setIcon(R.drawable.ic_alerts_and_states_error);
			builder.setCancelable(false);
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
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

		boolean firstLauch = session.get("firstLauch", true);

		if (firstLauch) {
			/* TODO: confirm user to detect location */

			// set radius for load event around
			session.put("radiusEventAround", 2);
			session.get("firstLauch", false);
			session.put("filterList", "");
		}

	}

	private void executeLogin() {
		// get email stored
		email = session.get("email", null);
		password = session.get("password", null);
		// instance fb
		fb = new Facebook(WelcomeActivity.APP_ID);

		// login = account esn
		// if email + pass != null
		if (email != null && password != null) {
			loginThread = new LoginThread(this, email, password, null);

			Intent successIntent = new Intent(this, HomeActivity.class);
			Intent failIntent = new Intent(this, WelcomeActivity.class);
			failIntent.putExtra("loginResult", "Login failed");

			loginThread.setSuccessIntent(successIntent);
			loginThread.setFailIntent(failIntent);
			loginThread.start();

		} else if (session.restoreFaceBook(fb)) {
			new Thread() {
				@Override
				public void run() {
					try {
						Looper.prepare();
						session.currentUser = new UsersManager()
								.RetrieveByAccessToken(session.get(
										"fb_access_token", ""));
						if (session.currentUser != null) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// da login bang fb thi cho vao luon
									Intent successIntent = new Intent(
											WelcomeScreen.this,
											HomeActivity.class);
									startActivity(successIntent);
									finish();
								}
							});
						} else {

							session.put("isLogined", false);
							Intent loginIntent = new Intent(WelcomeScreen.this,
									WelcomeActivity.class);
							startActivity(loginIntent);
							finish();
						}
					} catch (Exception e) {
						Log.e(TAG_LOG, e.getMessage());
						e.printStackTrace();
					}
				};
			}.start();

		} else {// chua login
			session.put("isLogined", false);
			Intent loginIntent = new Intent(WelcomeScreen.this,
					WelcomeActivity.class);
			startActivity(loginIntent);
			finish();
		}
	}

	private class LoadModelsThread extends Thread {
		@Override
		public void run() {
			Looper.prepare();
			try {
				// load event types
				EventTypeManager manager = new EventTypeManager();
				// get list event type
				ArrayList<EventType> list = null;

				list = manager.getList();
				// store session list to session
				session.eventTypes = list;
				

				// session
				if (session.logined()) {
					// neu da tung login thi execute login
					executeLogin();
				} else {
					// ko thi vao mang hinh welcome
					Intent loginIntent = new Intent(WelcomeScreen.this,
							WelcomeActivity.class);
					startActivity(loginIntent);
					finish();
				}

			} catch (Exception e) {
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			}

		}
	}

}
