package esn.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import esn.adapters.CustomListAdapter;
import esn.classes.EsnListItem;
import esn.classes.Sessions;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class FindFriends extends Activity {
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsync;
	private Sessions session;
	private final String TAG_LOG = "FindFriends";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.find_friends);

		session = Sessions.getInstance(this);
	}

	public void btnFindClicked() {
		mFacebook = new Facebook(WelcomeActivity.APP_ID);
		mAsync = new AsyncFacebookRunner(mFacebook);

		if (session.restoreFaceBook(mFacebook)) {

			Bundle params = new Bundle();
			String query = "select name, email, current_location, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) order by name";
			params.putString("method", "fql.query");
			params.putString("query", query);
			mAsync.request(null, params, new FriendsRequestListener());

		}

	}

	private class FriendsRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			try {
				JSONArray jsonArray = new JSONArray(response);
				int count = jsonArray.length();
				for (int i = 0; i < count; i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					String id = jsonObj.getString("uid");
					String name = jsonObj.getString("name");
					String pic = jsonObj.getString("pic_square");
				
				}
			} catch (Exception e) {
				Log.e(TAG_LOG, e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {
			Log.e(TAG_LOG, e.getMessage());
			e.printStackTrace();
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			Log.e(TAG_LOG, e.getMessage());
			e.printStackTrace();
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			Log.e(TAG_LOG, e.getMessage());
			e.printStackTrace();
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			Log.e(TAG_LOG, e.getMessage());
			e.printStackTrace();
		}

	}
}
