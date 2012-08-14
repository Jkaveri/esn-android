package com.facebook.android;


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

public class AccessFaceBookListener implements DialogListener {
	private Activity act;
	private Context context;
	private Facebook mFacebook;
	private final String LOG_TAG = "LoginFaceBookListner";
	private final Object obj = new Object();
	private Sessions session;
	public AccessFaceBookListener(Activity activity, Facebook fb) {
		act = activity;
		context = act.getApplicationContext();
		mFacebook = fb;
	}

	@Override
	public void onComplete(Bundle values) {		
		try {						
			AsyncFacebookRunner asyncFacebookRunner = new AsyncFacebookRunner(mFacebook);
			asyncFacebookRunner.request("me", new RequestGraphMeAccId(act, mFacebook));
			session = Sessions.getInstance(context);
			session.setAccessToken(mFacebook.getAccessToken());
			session.setAccessExpires(mFacebook.getAccessExpires());
			
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
