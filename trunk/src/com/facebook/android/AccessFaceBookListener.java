package com.facebook.android;


import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import esn.classes.Sessions;
import esn.models.Users;
import esn.models.UsersManager;

public class AccessFaceBookListener implements DialogListener {
	private Activity act;
	private Context context;
	private Facebook mFacebook;
	private final String LOG_TAG = "LoginFaceBookListner";
	private Sessions session;
	private Users user;
	private final Object obj = new Object();
	public AccessFaceBookListener(Activity activity, Facebook fb) {
		act = activity;
		context = act.getApplicationContext();
		mFacebook = fb;
	}

	@Override
	public void onComplete(Bundle values) {		
		try {			
			session = Sessions.getInstance(context);			
			
			final String access_token = mFacebook.getAccessToken();
			
			session.setAccessToken(access_token);
			
			session.put("fb_access_token_expires", mFacebook.getAccessExpires());
			
			AsyncFacebookRunner asyncFacebookRunner = new AsyncFacebookRunner(mFacebook);
			
			
			asyncFacebookRunner.request("me", new RequestGraphMeAccId(act));
			
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onFacebookError(FacebookError e) {
		Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		Log.e(LOG_TAG, e.getMessage());
		e.printStackTrace();
	}

	@Override
	public void onError(DialogError e) {
		Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		Log.e(LOG_TAG, e.getMessage());
		e.printStackTrace();
	}

	@Override
	public void onCancel() {
	}
}
