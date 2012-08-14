package esn.classes;

import java.io.IOException;
import java.util.Calendar;
import org.json.JSONException;

import esn.activities.R;
import esn.models.UsersManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class LoginThread extends Thread {
	public static final String LOG_TAG = null;
	private Activity activity;
	private String email;
	private String password;
	private ProgressDialog dialog;
	private Intent successIntent;
	private Intent failIntent;
	private Resources res;
	private Sessions session;

	public LoginThread(Activity activity, String _email, String _password,
			ProgressDialog dialog) {
		this.activity = activity;
		email = _email;
		password = _password;
		res = activity.getResources();

		this.dialog = dialog;
		session = Sessions.getInstance(activity);
		
	}

	@Override
	public void run() {
		Looper.prepare();
		UsersManager usermManager = new UsersManager();
		session.put("loginFacebookSuccess", false);
		int id = 0;
		try {
			// thuc thi login
			id = usermManager.Login(email, password);
			// neu login dung
			if (id > 0) {
				// lay thong tin user
				session.currentUser = usermManager.RetrieveById(id);
				// neu lay duoc thi login thanh cong va nguoc lai
				if (session.currentUser == null) {

					// chay login fail
					activity.runOnUiThread(new loginFail(this.activity, dialog,
							failIntent));
				} else {
					// kiem tra access token de reset lai access token
					if (session.currentUser.AccessToken != null
							&& session.currentUser.AccessToken.length() > 0) {
						session.setAccessToken(session.currentUser.AccessToken);
						// set access expire
						Calendar now = Calendar.getInstance();
						now.add(Calendar.DATE, 2);
						session.setAccessExpires(now.getTimeInMillis());
					}
					activity.runOnUiThread(new loginSuccess(this.activity,
							email, password, dialog, successIntent));
				}

			} else {
				// login that bai
				activity.runOnUiThread(new loginFail(this.activity, dialog,
						failIntent));
			}
		} catch (JSONException e) {
			Utils.showToast(activity, res.getString(R.string.esn_global_Error),
					Toast.LENGTH_LONG);
			Log.d(LOG_TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Utils.showToast(activity,
					res.getString(R.string.esn_global_connection_error),
					Toast.LENGTH_LONG);
			Log.d(LOG_TAG, e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			Utils.showToast(activity, res.getString(R.string.esn_global_Error),
					Toast.LENGTH_LONG);
			Log.d(LOG_TAG, e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Utils.showToast(activity, res.getString(R.string.esn_global_Error),
					Toast.LENGTH_LONG);
			Log.d(LOG_TAG, e.getMessage());
			e.printStackTrace();
		}

	}

	public Intent getSuccessIntent() {
		return successIntent;
	}

	public void setSuccessIntent(Intent successIntent) {
		this.successIntent = successIntent;
	}

	public Intent getFailIntent() {
		return failIntent;
	}

	public void setFailIntent(Intent failIntent) {
		this.failIntent = failIntent;
	}

	private class loginFail implements Runnable {
		private Context context;
		private Activity activity;
		private ProgressDialog dialog;
		private Intent failIntent;

		public loginFail(Activity activity, ProgressDialog dialog,
				Intent failIntent) {
			this.context = activity.getApplicationContext();
			this.activity = activity;
			this.dialog = dialog;
			this.failIntent = failIntent;
		}

		@Override
		public void run() {
			if (dialog != null) {
				dialog.cancel();
				dialog.dismiss();
			}
			if (failIntent != null) {
				Sessions session = Sessions.getInstance(context);
				session.clear();

				activity.startActivity(failIntent);
				activity.overridePendingTransition(R.anim.push_up_in,
						R.anim.push_left_out);
				activity.finish();

			} else {

				Toast.makeText(context,
						res.getString(R.string.esn_login_failed),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class loginSuccess implements Runnable {
		private Context context;
		private String password;
		private String email;
		private ProgressDialog dialog;
		private Activity activity;
		private Sessions session;
		private Intent successIntent;

		public loginSuccess(Activity activity, String email, String password,
				ProgressDialog dialog, Intent successIntent) {
			this.context = activity.getApplicationContext();
			this.activity = activity;
			this.email = email;
			this.password = password;
			this.dialog = dialog;
			this.successIntent = successIntent;
			session = Sessions.getInstance(context);
		}

		@Override
		public void run() {

			session.put("email", email);
			session.put("password", password);
			session.put("isLogined", true);
			if (dialog != null) {
				dialog.dismiss();
			}
			if (successIntent != null) {
				activity.startActivity(successIntent);
				activity.overridePendingTransition(R.anim.push_up_in,
						R.anim.push_left_out);
				activity.finish();
			}
		}
	}
}
