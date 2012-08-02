package com.facebook.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner.RequestListener;

import esn.activities.RegisterActivity;
import esn.classes.Sessions;
import esn.models.UsersManager;

public class RequestGraphMeAccId implements RequestListener {
	private Activity activity;
	private Context context;
	private final String TAG_LOG = "RequestGraphMe";
	
	private Sessions session;
	
	private final Object obj = new Object();
	private JSONObject accountInfo;
	
	Resources res;
	public RequestGraphMeAccId(Activity act){
		this.activity = act;
		context = act.getApplicationContext();
	}
	@Override
	public void onComplete(String response, Object state) {
		try {
			 accountInfo = Util
					.parseJson(response);
			
			 
			session = Sessions.getInstance(context);
			
			res = context.getResources();
			new  Thread()
			{
				public void run() {
					
					UsersManager manager = new UsersManager();
					
					try {
						String fbId=accountInfo.getString("id");
						
						Boolean rs = manager.UpdateAccessToken(session.currentUser.AccID,session.getAccessToken(),fbId);
												
						if(rs==false)
						{
							Toast.makeText(context, res.getString(R.string.app_global_UpdateUnsuccess), Toast.LENGTH_SHORT).show();
						}
						synchronized (obj) {
							obj.notify();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				};
			}.start();
			synchronized (obj) {
				obj.wait();
			}
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
