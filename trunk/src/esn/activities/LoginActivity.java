package esn.activities;

import java.io.IOException;

import org.json.JSONException;

import com.actionbarsherlock.app.SherlockActivity;

import esn.classes.Sessions;
import esn.models.Users;
import esn.models.UsersManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends SherlockActivity{

	Intent intent;

	Resources res;

	private Context context;

	public SharedPreferences pref;


	private Sessions session;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);
		
		getActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().hide();
		 
		session = Sessions.getInstance(context);

		intent = this.getIntent();
		context = this;
		
		res = getResources();
		TextView tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassgord);

		tvForgotPassword
				.setText(Html
						.fromHtml("<a href=\"http://www.esn.com/forgotpassword\">Forgot your password?</a> "));

		tvForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void BackClicked(View view) {
		intent = new Intent(context, WelcomeActivity.class);
		startActivity(intent);
		finish();
	}

	@SuppressLint({ "NewApi", "NewApi" })
	public void LoginClicked(View view) {

		EditText txtEmail = (EditText) findViewById(R.id.esn_login_Email);
		
		EditText txtPass = (EditText) findViewById(R.id.esn_login_pass);
		String email = txtEmail.getText().toString();
		
		if(email.isEmpty())
		{
			Toast.makeText(context, res.getString(R.string.esn_login_enteremail), 10).show();
			return;
		}
		String password = txtPass.getText().toString();
		
		if(password.isEmpty())
		{
			Toast.makeText(context, res.getString(R.string.esn_login_enterpassword), 10).show();
			return;
		}
		
	/*	ProgressDialog dialog = new ProgressDialog(this);
		dialog.setTitle(this.getResources().getString(R.string.app_Processing));
		dialog.setMessage("Waiting ....");
		dialog.show();*/
		
		
		LoginThread loginThread = new LoginThread(this, email,password,null);
		Intent successIntent = new Intent(this, HomeActivity.class);
	
		loginThread.setSuccessIntent(successIntent);
		loginThread.start();
	}
	public static LoginThread createLoginThread(Activity activity, String _email, String _password,ProgressDialog dialog){
		return new LoginActivity().new LoginThread(activity, _email, _password, dialog);
	}

	public class LoginThread extends Thread {
		private Activity activity;
		private String email;
		private String password;
		private ProgressDialog dialog;
		private Intent successIntent;
		private Intent failIntent;
		
		public LoginThread(Activity activity, String _email, String _password,ProgressDialog dialog) {
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
			if (id>0) {
				activity.runOnUiThread(new loginSuccess(this.activity,email,password,dialog,successIntent));
			} else {
				activity.runOnUiThread(new loginFail(this.activity,dialog,failIntent));
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
		
	}

	private class loginFail implements Runnable {
		private Context context;
		private Activity activity;
		private ProgressDialog dialog;
		private Intent failIntent;
		public loginFail(Activity activity,ProgressDialog dialog,Intent failIntent){
			this.context = activity.getApplicationContext();
			this.activity = activity;
			this.dialog = dialog;
			this.failIntent = failIntent;
		}
		@Override
		public void run() {
			if(dialog!=null){
				dialog.cancel();
				dialog.dismiss();				
			}
			if(failIntent!=null){
				
				activity.startActivity(failIntent);
				
			}else{
				
				Toast.makeText(context, "Login fail", Toast.LENGTH_SHORT).show();
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
		public loginSuccess(Activity activity,String email, String password,ProgressDialog dialog, Intent successIntent) {
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
			new GetUserInfoThread(this.session).start();
			if(dialog!=null){
				dialog.dismiss();
			}
			if(successIntent!=null){
				activity.startActivity(successIntent);
				activity.finish();
			}
		}
	}
	private class GetUserInfoThread extends Thread{
		private Sessions session;
		public GetUserInfoThread(Sessions session){
			this.session = session;
		}
		@Override
		public void run() {
			Looper.prepare();
			UsersManager manager = new UsersManager();
			
			try {
				Users user = manager.RetrieveByEmail(session.get("email", ""));
				if(user!=null){
					session.currentUser = user;
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
