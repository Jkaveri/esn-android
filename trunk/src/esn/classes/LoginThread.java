package esn.classes;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

import esn.models.Users;
import esn.models.UsersManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

public class LoginThread extends Thread {
	private Activity activity;
	private String email;
	private String password;
	private ProgressDialog dialog;
	private Intent successIntent;
	private Intent failIntent;

	public LoginThread(Activity activity, String _email, String _password,
			ProgressDialog dialog) {
		this.activity = activity;
		email = _email;
		password = _password;
		this.dialog = dialog;
	}

	@Override
	public void run() {
		Looper.prepare();
		UsersManager usermManager = new UsersManager();
		int id = 0;
		try {
			id = usermManager.Login(email, password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id > 0) {
			activity.runOnUiThread(new loginSuccess(this.activity, email,
					password, dialog, successIntent));
		} else {
			activity.runOnUiThread(new loginFail(this.activity, dialog,
					failIntent));
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

			} else {

				Toast.makeText(context, "Login fail", Toast.LENGTH_SHORT)
						.show();
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
			new GetUserInfoThread(this.session).start();
			if (dialog != null) {
				dialog.dismiss();
			}
			if (successIntent != null) {
				activity.startActivity(successIntent);
				activity.finish();
			}
		}
	}

	private class GetUserInfoThread extends Thread {
		private Sessions session;

		public GetUserInfoThread(Sessions session) {
			this.session = session;
		}

		@Override
		public void run() {
			Looper.prepare();
			UsersManager manager = new UsersManager();

			try {
				String email = session.get("email", "");
				
				Users user = manager.RetrieveByEmail(email);
				
				if (user != null) {
					session.currentUser = user;
					if(user.AccessToken!=null && user.AccessToken.length()>0){
						session.setAccessToken(user.AccessToken);
						//set access expire
						Calendar now = Calendar.getInstance();
						now.add(Calendar.DATE, 2);
						session.setAccessExpires(now.getTimeInMillis());
					}
				}else{
					
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
