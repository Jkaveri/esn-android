package esn.activities;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.actionbarsherlock.app.SherlockActivity;
import esn.classes.LoginThread;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.EventType;
import esn.models.EventTypeManager;
import esn.models.Users;
import esn.models.UsersManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeScreen extends SherlockActivity {
	private Sessions session;
	private String password;
	private String email;
	private LoadModelsThread loadModelThread;
	private LoginThread loginThread;
	private Resources res;
	private final String TAG_LOG = "WELCOME_SCREEN";
	WelcomeScreen context;
	public double centerLong;
	public double centerLat;
	public Object lockObj = new Object();
	public boolean confirmDialogShowing = false;
	public Users user = new Users();
	private LoadUserThread loadUserThread;

	private class LoadUserThread extends Thread {
		private String email;
		private int Id;

		public LoadUserThread(String email) {
			this.email = email;
		}

		public LoadUserThread(int id) {
			this.Id = id;
		}

		@Override
		public void run() {
			Looper.prepare();
			try {
				synchronized (lockObj) {
					if (this.Id > 0) {
						user = (new UsersManager()).RetrieveById(this.Id);

					} else {
						user = (new UsersManager()).RetrieveByEmail(this.email);
					}
					lockObj.notify();
				}

			} catch (IllegalArgumentException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			} catch (JSONException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_connection_error),
						Toast.LENGTH_LONG);
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			}
		};
	};

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

	@Override
	protected void onDestroy() {
		if (loadModelThread != null) {
			loadModelThread.interrupt();
		}
		if (loadUserThread != null) {
			loadUserThread.interrupt();
		}
		super.onDestroy();
	}

	private void initConfig() {
		// application session
		session = Sessions.getInstance(this);

		boolean firstLauch = session.get("firstLauch", true);

		if (!session.getSettingLocation()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(R.string.esn_welcomeScreen_GPSLocationService);
			builder.setMessage(R.string.esn_welcomeScreen_GPSLocationService_Confirm);

			builder.setCancelable(false);

			String ok = res.getString(R.string.esn_global_ok);
			String cancel = res.getString(R.string.esn_global_cancel);

			builder.setPositiveButton(ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							session.setSettingLocation(true);
							confirmDialogShowing = false;
							synchronized (lockObj) {
								lockObj.notify();
							}
							dialog.dismiss();
						}
					});
			builder.setNegativeButton(cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							session.setSettingLocation(false);
							confirmDialogShowing = false;
							synchronized (lockObj) {
								lockObj.notify();
							}
							dialog.dismiss();
						}
					});
			builder.create().show();
			confirmDialogShowing = true;
		}

		if (firstLauch) {
			// set radius for load event around
			session.setRadiusForEventAround(2.0);
			session.put("firstLauch", false);
			session.put("filterList", "");
		}

	}

	private void executeLogin() {
		// get email stored
		email = session.get("email", null);

		password = session.get("password", null);
		// login = account esn
		// if email + pass != null
		if (email != null && password != null) {
			boolean fbLoginSuccess = session.get("loginFacebookSuccess", false);
			Intent successIntent = new Intent(this, HomeActivity.class);
			final Intent failIntent = new Intent(this, WelcomeActivity.class);
			if (fbLoginSuccess) {
				if (session.currentUser == null) {
					loadUserThread = new LoadUserThread(email);
					loadUserThread.start();
					synchronized (lockObj) {
						try {
							lockObj.wait();
						} catch (InterruptedException e) {
							Utils.showToast(context,
									res.getString(R.string.esn_global_Error),
									Toast.LENGTH_LONG);
							Log.e(TAG_LOG, e.getMessage());
							e.printStackTrace();
						}
					}
					if (user != null) {
						session.currentUser = user;
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								this);
						builder.setTitle(res
								.getString(R.string.esn_global_waring));
						builder.setMessage(res
								.getString(R.string.esn_global_error_please_login_again));
						builder.setIcon(R.drawable.ic_alerts_and_states_error);
						builder.setCancelable(false);
						builder.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										startActivity(failIntent);
										overridePendingTransition(
												R.anim.push_up_in,
												R.anim.push_left_out);
										finish();
									}
								});
						builder.create().show();
					}

				} else {
					startActivity(successIntent);
					overridePendingTransition(R.anim.push_up_in,
							R.anim.push_left_out);
					finish();
				}

			} else {
				loginThread = new LoginThread(this, email, password, null);

				failIntent.putExtra("loginResult", "Login failed");

				loginThread.setSuccessIntent(successIntent);
				loginThread.setFailIntent(failIntent);
				loginThread.start();
			}

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
				synchronized (lockObj) {
					if (confirmDialogShowing) {
						lockObj.wait();
					}
				}
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
			} catch (ClientProtocolException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_connection_error),
						Toast.LENGTH_LONG);
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_connection_error),
						Toast.LENGTH_LONG);
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			} catch (JSONException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			} catch (ParseException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			} catch (InterruptedException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			}

		}
	}

}
