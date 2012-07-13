package com.facebook.android;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.util.Log;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import esn.activities.HomeActivity;
import esn.activities.WelcomeActivity;
import esn.activities.WelcomeScreen;
import esn.classes.Sessions;
import esn.models.Users;
import esn.models.UsersManager;

public class LoginFaceBookListener implements DialogListener {
	private Activity act;
	private Context ctx;
	private Facebook mFacebook;
	private final String LOG_TAG = "LoginFaceBookListner";
	private Sessions session;

	public LoginFaceBookListener(Activity activity, Facebook fb) {
		act = activity;
		ctx = act.getApplicationContext();
		mFacebook = fb;
	}

	@Override
	public void onComplete(Bundle values) {

		// check access token da ton tai chua
		try {
			session = Sessions.getInstance(ctx);
			final String access_token = mFacebook.getAccessToken();
			session.put("fb_access_token", access_token);
			session.put("fb_access_token_expires", mFacebook.getAccessExpires());
			
			new Thread(){
				@Override
				public void run() {
					try {
						Users user = new UsersManager().RetrieveByAccessToken(access_token);
						if(user!=null){
							session.put("isLogined", true);
							session.currentUser = user;
							Intent welcomeScreen = new Intent(ctx, WelcomeScreen.class);
							welcomeScreen.putExtra("reAuthor", true);
							act.startActivity(welcomeScreen);
							act.finish();
						}else{
							
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			
			

		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onFacebookError(FacebookError e) {
		Log.e(LOG_TAG, e.getMessage());
		e.printStackTrace();
	}

	@Override
	public void onError(DialogError e) {
		Log.e(LOG_TAG, e.getMessage());
		e.printStackTrace();
	}

	@Override
	public void onCancel() {

	}

}
