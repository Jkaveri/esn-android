/**
 * 
 */
package com.facebook.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;

/**
 * @author JK
 *
 */
public class FbUpdateStatusListener implements RequestListener {
	
	private Context context;
	private Handler handler;
	public FbUpdateStatusListener(Context context){
		this.context = context;
		handler = new Handler();
	}
	
	@Override
	public void onComplete(String response, Object state) {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
			}
		});
		
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
