package com.facebook.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner.RequestListener;

import esn.activities.RegisterActivity;

public class RequestGraphMe implements RequestListener {
	private Activity activity;
	private Context context;
	private final String TAG_LOG = "RequestGraphMe";
	public RequestGraphMe(Activity act){
		this.activity = act;
		context = act.getApplicationContext();
	}
	@Override
	public void onComplete(String response, Object state) {
		try {
			JSONObject accountInfo = Util
					.parseJson(response);
			
			Intent intent = new Intent(context,RegisterActivity.class);
			
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
			
			activity.startActivity(intent);
			activity.finish();
			
		} catch (Exception e) {
			Log.e(TAG_LOG,e.getMessage());
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
