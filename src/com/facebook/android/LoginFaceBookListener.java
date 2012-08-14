package com.facebook.android;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import esn.activities.WelcomeScreen;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.Users;
import esn.models.UsersManager;

public class LoginFaceBookListener implements DialogListener {
	private Activity act;
	private Context ctx;
	private Facebook mFacebook;
	private final String LOG_TAG = "LoginFaceBookListner";
	private Sessions session;
	private Users user;
	private final Object obj = new Object();

	public LoginFaceBookListener(Activity activity, Facebook fb) {
		act = activity;
		ctx = act.getApplicationContext();
		mFacebook = fb;
	}

	@Override
	public void onComplete(Bundle values) {
		synchronized (mFacebook) {

			try {
				AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(
						mFacebook);
				mAsyncRunner.request("me", new RequestGraphMe(act, mFacebook));
				mFacebook.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onFacebookError(FacebookError e) {
		Utils.showToast(act, "Login facebook failed", Toast.LENGTH_LONG);
		Log.e(LOG_TAG, e.getMessage());
		e.printStackTrace();
	}

	@Override
	public void onError(DialogError e) {
		Utils.showToast(act, "Login facebook failed", Toast.LENGTH_LONG);
		Log.e(LOG_TAG, e.getMessage());
		e.printStackTrace();
	}

	@Override
	public void onCancel() {
	}
}
