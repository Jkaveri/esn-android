package esn.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.LoginFaceBookListener;
import com.facebook.android.Util;

import esn.adapters.ListViewFindFriendAdapter;
import esn.classes.Sessions;
import esn.models.FriendsManager;
import esn.models.Users;
import esn.models.UsersManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class FindFriendsActivity extends Activity {
	private static final int PAGE_SIZE = 10;
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsync;
	Sessions session;

	private final String TAG_LOG = "FindFriends";

	private ListViewFindFriendAdapter adapter;
	private ListView lstFindFriend;
	Resources res;

	Context context;

	FriendsManager friendsManager = new FriendsManager();
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.find_friends);
		new Handler();
		res = getResources();
		context = this;
		session = Sessions.getInstance(context);
		// progress dialog
		progressDialog = new ProgressDialog(this);

		progressDialog.setTitle(res.getString(R.string.esn_global_loading));
		progressDialog.setMessage(res
				.getString(R.string.esn_find_friend_finding));
		progressDialog.show();
		// setup list view
		adapter = new ListViewFindFriendAdapter(this, new ArrayList<Users>(),
				R.layout.find_friend_facebook_row);
		lstFindFriend = (ListView) findViewById(R.id.esn_findFriend_list);

		lstFindFriend.setAdapter(adapter);
		lstFindFriend
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int index, long id) {

					}
				});
		// get facebook friends
		if (session.getSettingFacebook()) {

			GetFriendFacebook(1, PAGE_SIZE);
		} else {
			progressDialog.dismiss();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.esn_global_waring);
			builder.setMessage(R.string.esn_global_must_enable_fb_con);
			builder.setNegativeButton(R.string.esn_global_ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Intent intent = new Intent(context,SettingsAppActivity.class);
							startActivity(intent);
							finish();
						}

					});
			builder.setPositiveButton(R.string.esn_global_cancel,new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.dismiss();
					finish();
				}
			});
			builder.create().show();
		}

	}

	private void GetFriendFacebook(int pageNum, int pageSize) {
		session = Sessions.getInstance(this);

		mFacebook = new Facebook(WelcomeActivity.APP_ID);
		mAsync = new AsyncFacebookRunner(mFacebook);

		if (session.restoreFaceBook(mFacebook)) {

			Bundle params = new Bundle();
			params.putString("fields", "name, picture, location");
			// params.putInt("offset", pageNum);
			// params.putInt("limit", pageSize);
			mAsync.request("me/friends", params, new FriendsRequestListener());
		} else {
			mFacebook.authorize(this, WelcomeActivity.FB_PERMISSIONS,
					new LoginFaceBookListener(this, mFacebook));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mFacebook.authorizeCallback(requestCode, resultCode, data);

	}

	@Override
	public void onDestroy() {
		adapter.stopThread();
		adapter.clearCache();
		lstFindFriend.setAdapter(null);
		super.onDestroy();
	}

	public void btnFindClicked(View view) {
	}

	private class FriendsRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {

			try {
				JSONObject responseObject = new JSONObject(response);
				if (responseObject.has("data")) {
					JSONArray jsonArray = responseObject.getJSONArray("data");

					Users[] users = (new UsersManager())
							.GetFbAccountHasRegistered(jsonArray,
									session.currentUser.AccID);

					for (int i = 0; i < users.length; i++) {

						adapter.add(users[i]);
					}
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							adapter.notifyDataSetChanged();
							progressDialog.dismiss();
						}
					});
				} else {

				}

			} catch (JSONException e) {
				Toast.makeText(FindFriendsActivity.this,
						R.string.esn_global_ConnectionError, Toast.LENGTH_SHORT)
						.show();
				Log.d(TAG_LOG, e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(FindFriendsActivity.this,
						R.string.esn_global_ConnectionError, Toast.LENGTH_SHORT)
						.show();
				Log.d(TAG_LOG, e.getMessage());
				e.printStackTrace();
			}

		}

		@Override
		public void onIOException(IOException e, Object state) {
			Toast.makeText(FindFriendsActivity.this,
					R.string.esn_global_ConnectionError, Toast.LENGTH_SHORT)
					.show();
			Log.d(TAG_LOG, e.getMessage());
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
