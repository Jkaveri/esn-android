package com.facebook.android;


import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

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
			session.put("fb_access_token", access_token);
			session.put("fb_access_token_expires", mFacebook.getAccessExpires());
			
			new  Thread()
			{
				public void run() {
					
					UsersManager manager = new UsersManager();
					
					try {
						
						manager.UpdateAccessToken(session.currentUser.AccID,session.get("fb_access_token", "_"));
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				};
			}.start();
			act.finish();			
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
