package esn.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.LoginFaceBookListener;
import esn.adapters.ListViewFindFriendAdapter;
import esn.classes.Sessions;
import esn.classes.Utils;
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
	private ProgressDialog dialog;
	private Users[] users;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.find_friends);
		new Handler();
		res = getResources();
		context = this;
		session = Sessions.getInstance(context);
		// progress dialog
		dialog = new ProgressDialog(this);

		dialog.setTitle(res.getString(R.string.esn_global_loading));
		dialog.setMessage(res
				.getString(R.string.esn_find_friend_finding));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
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
			dialog.dismiss();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.esn_global_waring);
			builder.setMessage(R.string.esn_global_must_enable_fb_con);
			builder.setNegativeButton(R.string.esn_global_ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Intent intent = new Intent(context,
									SettingsAppActivity.class);
							startActivity(intent);
							finish();
						}

					});
			builder.setPositiveButton(R.string.esn_global_cancel,
					new DialogInterface.OnClickListener() {
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
			/*
			 * select name, current_location, uid, pic_square from user where
			 * uid in (select uid2 from friend where uid1=me()) order by name
			 */
			String query = "select uid, name, pic_square from user where is_app_user="
					+ WelcomeActivity.APP_ID
					+ " and uid in (select uid2 from friend where uid1=me()) order by name";
			params.putString("method", "fql.query");
			params.putString("query", query);
			// params.putString("fields", "name, picture, location");
			mAsync.request(null, params, new FriendsRequestListener());
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
		if (adapter != null) {
			adapter.stopThread();
			adapter.clearCache();
		}
		if (lstFindFriend != null) {
			lstFindFriend.setAdapter(null);

		}

		super.onDestroy();
	}

	public void btnFindClicked(View view) {
	}

	private void dissmisDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private class FriendsRequestListener implements RequestListener {

		protected static final String LOG_TAG = "FriendsRequestListener";

		@Override
		public void onComplete(String response, Object state) {

			try {
				JSONArray jsonArray = new JSONArray(response);
				if (jsonArray != null && jsonArray.length() > 0) {

					UsersManager manager = new UsersManager();
					users = manager.GetFbAccountHasRegistered(jsonArray,
							session.currentUser.AccID);

					if (users != null) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								for (Users user : users) {

									adapter.add(user);
									Log.d(LOG_TAG, user.Name);
								}
								adapter.notifyDataSetChanged();
								dialog.dismiss();

							}
						});
					} else {
						Utils.showToast(FindFriendsActivity.this,
								res.getString(R.string.esn_global_list_empty),
								Toast.LENGTH_SHORT);
						return;
					}
				} else {
					Utils.showToast(FindFriendsActivity.this,
							res.getString(R.string.esn_global_list_empty),
							Toast.LENGTH_SHORT);
					return;
				}

			} catch (JSONException e) {
				Utils.DismitDialog(dialog, FindFriendsActivity.this);
				Utils.showToast(FindFriendsActivity.this,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_SHORT);
				Log.d(TAG_LOG, e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Utils.DismitDialog(dialog,FindFriendsActivity.this);
				Utils.showToast(FindFriendsActivity.this,
						res.getString(R.string.esn_global_ConnectionError),
						Toast.LENGTH_SHORT);
				Log.d(TAG_LOG, e.getMessage());
				e.printStackTrace();
			}

		}

		@Override
		public void onIOException(IOException e, Object state) {
			Utils.DismitDialog(dialog,FindFriendsActivity.this);
			Utils.showToast(FindFriendsActivity.this,
					res.getString(R.string.esn_global_ConnectionError),
					Toast.LENGTH_SHORT);
			Log.d(TAG_LOG, e.getMessage());
			e.printStackTrace();
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			Utils.DismitDialog(dialog,FindFriendsActivity.this);
			Utils.showToast(FindFriendsActivity.this,
					res.getString(R.string.esn_global_ConnectionError),
					Toast.LENGTH_SHORT);
			Log.e(TAG_LOG, e.getMessage());
			e.printStackTrace();
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			Utils.DismitDialog(dialog,FindFriendsActivity.this);
			Utils.showToast(FindFriendsActivity.this,
					res.getString(R.string.esn_global_Error),
					Toast.LENGTH_SHORT);
			Log.e(TAG_LOG, e.getMessage());
			e.printStackTrace();
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			Utils.DismitDialog(dialog,FindFriendsActivity.this);
			Toast.makeText(context, res.getString(R.string.esn_global_Error), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}
}
