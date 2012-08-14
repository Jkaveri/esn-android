package com.facebook.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner.RequestListener;

import esn.activities.RegisterActivity;
import esn.activities.WelcomeScreen;
import esn.classes.Sessions;
import esn.models.Users;
import esn.models.UsersManager;

public class RequestGraphMe implements RequestListener {
	private static final String LOG_TAG = null;
	private Activity activity;
	private Context context;
	private final String TAG_LOG = "RequestGraphMe";
	private Facebook mFacebook;
	private Sessions session;
	private Users user;
	private Object lockObj = new Object();

	public RequestGraphMe(Activity act, Facebook fb) {
		this.activity = act;
		context = act.getApplicationContext();
		mFacebook = fb;

	}

	@Override
	public void onComplete(String response, Object state) {
		try {
			JSONObject accountInfo = Util.parseJson(response);
			final String fbId = accountInfo.getString("id");
			session = Sessions.getInstance(context);
			final String access_token = mFacebook.getAccessToken();

			user = new Users();

			synchronized (lockObj) {

				new Thread() {
					@Override
					public void run() {
						try {
							synchronized (lockObj) {
								user = new UsersManager().RetrieveByFbID(fbId);
								lockObj.notify();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
				lockObj.wait();
				if (user != null && user.AccID > 0) {
					// setting
					session.setSettingFacebook(true);
					session.setAccessToken(access_token);
					session.setAccessExpires(mFacebook.getAccessExpires());
					session.put("email", user.Email);
					session.put("password", user.Password);
					session.put("isLogined", true);
					session.put("loginFacebookSuccess", true);
					//
					session.currentUser = user;
					Intent welcomeScreen = new Intent(context,
							WelcomeScreen.class);
					welcomeScreen.putExtra("reAuthor", true);
					activity.startActivity(welcomeScreen);
					activity.finish();
					synchronized (mFacebook) {
						mFacebook.notify();
					}
					new Thread() {
						@Override
						public void run() {
							try {
								new UsersManager().UpdateAccessToken(
										user.AccID, access_token, fbId);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						};
					}.start();

				} else {
					session.put("loginFacebookSuccess", false);
					Intent intent = new Intent(context, RegisterActivity.class);
					intent.putExtra("fb_access_token", access_token);
					intent.putExtra("fb_access_expires",
							mFacebook.getAccessExpires());
					intent.putExtra("facebookSignup", true);
					intent.putExtra("fb_id", accountInfo.getString("id"));
					intent.putExtra("name", accountInfo.getString("name"));
					intent.putExtra("first_name",
							accountInfo.getString("first_name"));
					intent.putExtra("last_name",
							accountInfo.getString("last_name"));
					intent.putExtra("email", accountInfo.getString("email"));
					intent.putExtra("gender", accountInfo.getString("gender"));
					intent.putExtra("birthday",
							accountInfo.getString("birthday"));
					activity.startActivity(intent);

					activity.finish();
					synchronized (mFacebook) {
						mFacebook.notify();
					}
				}
			}

		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void onIOException(IOException e, Object state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileNotFoundException(FileNotFoundException e, Object state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMalformedURLException(MalformedURLException e, Object state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFacebookError(FacebookError e, Object state) {
		// TODO Auto-generated method stub

	}

}
