package esn.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import esn.classes.Sessions;
import esn.models.UsersManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

public class WelcomeActivity extends Activity {

	private final int REQUEST_CODE_CREATE_LOGIN = 1;
	private final int REQUEST_CODE_CREATE_REGISTER = 3;
	private final String[] FB_PERMISSIONS = { "email", "user_events",
			"user_birthday" };
	// Login by fB
	public static final String APP_ID = "175185989209026";
	private Facebook mFacebook;
	private SharedPreferences prefEdit;
	private AsyncFacebookRunner mAsyncRunner;
	protected Sessions session;
	protected Context context;
	private ProgressDialog dialog;
	public String password;
	public String email;
	public Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		context = this;
		handler = new Handler();
		// application session
		session = Sessions.getInstance(context);
		// init facebook
		mFacebook = new Facebook(APP_ID);
		// init facebook runner
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		// get fb acess token was stored
		String fbAccessToken = session.get("fb_access_token", null);

		long fbAccessExpires = session.get("fb_access_token_expires", 0);
		if (fbAccessToken != null) {
			mFacebook.setAccessToken(fbAccessToken);
		}
		if (fbAccessExpires != 0) {
			mFacebook.setAccessExpires(fbAccessExpires);
		}
		if (session.logined()) {
			executeLogin();
		}

	}

	public void btnLoginfbClicked(View view) {
		if (!mFacebook.isSessionValid()) {// if access token is expired
			mFacebook.authorize(this, FB_PERMISSIONS, new DialogListener() {

				@Override
				public void onComplete(Bundle values) {
					session = Sessions.getInstance(context);
					String access_token = mFacebook.getAccessToken();
					session.put("fb_access_token", access_token);
					session.put("fb_access_token_expires",
							mFacebook.getAccessExpires());
					mAsyncRunner.request("me", new RequestListener() {

						@Override
						public void onComplete(String response, Object state) {
							Intent intent = new Intent(getBaseContext(),
									RegisterActivity.class);

							try {
								JSONObject accountInfo = Util
										.parseJson(response);
								intent.putExtra("facebookSignup", true);
								intent.putExtra("fb_id",
										accountInfo.getString("id"));
								intent.putExtra("name",
										accountInfo.getString("name"));
								intent.putExtra("first_name",
										accountInfo.getString("first_name"));
								intent.putExtra("last_name",
										accountInfo.getString("last_name"));
								intent.putExtra("email",
										accountInfo.getString("email"));
								intent.putExtra("gender",
										accountInfo.getString("gender"));
								intent.putExtra("birthday",
										accountInfo.getString("birthday"));
								startActivity(intent);
								finish();
							} catch (FacebookError e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onIOException(IOException e, Object state) {

						}

						@Override
						public void onFileNotFoundException(
								FileNotFoundException e, Object state) {

						}

						@Override
						public void onMalformedURLException(
								MalformedURLException e, Object state) {
						}

						@Override
						public void onFacebookError(FacebookError e,
								Object state) {
						}

					});
				}

				@Override
				public void onFacebookError(FacebookError e) {

				}

				@Override
				public void onError(DialogError e) {

				}

				@Override
				public void onCancel() {

				}

			});
		}
	}

	public void LoginClicked(View view) {
		if (!mFacebook.isSessionValid()) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
		finish();
	}

	public void RegisterClicked(View view) {
		if (mFacebook.isSessionValid()) {
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
		}
		finish();
	}

	private void executeLogin() {
		dialog = new ProgressDialog(this);
		dialog.setTitle(this.getResources().getString(R.string.app_login));
		dialog.setTitle(getResources().getString(R.string.app_register));
		dialog.show();
		email = session.get("email", null);
		password = session.get("password", null);
		LoginThread loginThread = new LoginThread();
		loginThread.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	public class LoginThread extends Thread {
		public LoginThread() {
		}

		@Override
		public void run() {
			UsersManager usermManager = new UsersManager();
			if (usermManager.Login(email, password)) {

				handler.post(new loginSuccess());
			} else {

				handler.post(new loginFail());
			}
		}

	}

	private class loginFail implements Runnable {
		@Override
		public void run() {
			dialog.dismiss();
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Login failed!");
			alert.setMessage("Username or password is wrong, please reLogin!");
			alert.show();
		}
	}

	private class loginSuccess implements Runnable {
		@Override
		public void run() {
			session.put("email", email);
			session.put("password", password);
			dialog.dismiss();
			Intent intent = new Intent(context, HomeActivity.class);
			startActivity(intent);
			finish();
		}
	}
}