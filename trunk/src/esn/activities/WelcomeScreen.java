package esn.activities;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.mime.MinimalField;
import org.json.JSONException;

import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.RequestGraphMe;

import esn.classes.LoginThread;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.EventType;
import esn.models.EventTypeManager;
import esn.models.Users;
import esn.models.UsersManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
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
	private Object accessToken;
	private Facebook fb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_screen);

		// getActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().hide();

		context = this;

		session = Sessions.getInstance(context);
		res = getResources();
		Intent data = getIntent();
		// neu co ket noi mang
		if (Utils.isNetworkAvailable(this)) {
			// neu tu trang login or loginfacebook chuyen ra :)
			boolean reLogin = false;
			if (data != null) {
				reLogin = data.getBooleanExtra("reAuthor", false);
			}

			if (reLogin) {
				// login lai
				executeLogin();
			} else {
				initConfig();
				loadModelThread = new LoadModelsThread();
				loadModelThread.start();
			}

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
			// set radius for load event around
			session.put("radiusEventAround", 2);
			session.get("firstLauch", false);
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
						session.currentUser = new UsersManager()
								.RetrieveByAccessToken(session.get(
										"fb_access_token", ""));
						if (session.currentUser != null) {
							new Handler().post(new Runnable() {

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
							AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(
									fb);
							mAsyncRunner.request("me", new RequestGraphMe(
									WelcomeScreen.this));                                 
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
