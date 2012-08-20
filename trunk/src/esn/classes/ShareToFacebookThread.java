package esn.classes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.facebook.android.Facebook;

import esn.activities.R;
import esn.activities.WelcomeActivity;
import esn.models.Events;

public class ShareToFacebookThread extends Thread {

	private Events event;
	private Activity context;
	private Sessions session;
	private Resources res;
	
	public ShareToFacebookThread(Events event, Activity context) {
		super();
		this.event = event;
		this.context = context;
		this.res = context.getResources();
		session = Sessions.getInstance(context);
		
	}

	@Override
	public void run() {
		Looper.prepare();
		if (event != null) {
			Facebook mFaceBook = new Facebook(WelcomeActivity.APP_ID);
			if (session.restoreFaceBook(mFaceBook)) {
				try {
					Bundle params = new Bundle();

					params.putString("message", event.Title);

					params.putString("name", event.Title);
					params.putString("caption", "Mạng xã hội sự kiện MyESN");
					params.putString("link", "http://myesn.vn/comment.aspx?ID="+event.EventID);
					params.putString("description", event.Description);
					if (event.Picture != null && event.Picture.length() > 0) {
						params.putString("picture", event.Picture);

					} else {
						params.putString("picture",
								"http://myesn.vn/Images/interface/logo%282%29.gif");
					}
					// mFaceBook.request("me");
					String response = mFaceBook.request("me/feed", params,
							"POST");

					if (response != null && response.length() > 0) {
						Utils.showToast(context, res.getString(R.string.esn_global_share_facebook_success),
								Toast.LENGTH_SHORT);
					} else {
						Utils.showToast(context, res.getString(R.string.esn_global_share_facebook_error),
								Toast.LENGTH_SHORT);
					}
				} catch (FileNotFoundException e) {
					Utils.showToast(context, res.getString(R.string.esn_global_share_facebook_error),
							Toast.LENGTH_SHORT);
					e.printStackTrace();
				} catch (MalformedURLException e) {
					Utils.showToast(context, res.getString(R.string.esn_global_connection_error),
							Toast.LENGTH_SHORT);
					// Toast.makeText(this, "accessing an invalid endpoint",
					// Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (IOException e) {
					Utils.showToast(context, res.getString(R.string.esn_global_connection_error), Toast.LENGTH_SHORT);
					e.printStackTrace();
				}
			}

		} else {

		}
	}
}
