package esn.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.LoginFaceBookListener;
import com.facebook.android.Util;

import esn.adapters.CustomListAdapter;
import esn.adapters.ListViewFindFriendAdapter;
import esn.adapters.ListViewFriendsAdapter;
import esn.classes.EsnListItem;
import esn.classes.Sessions;
import esn.models.FindFriendDTO;
import esn.models.FriendsListsDTO;
import esn.models.FriendsManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FindFriendsActivity extends Activity {
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsync;
	Sessions session;
	
	private final String TAG_LOG = "FindFriends";
	
	private ListViewFindFriendAdapter adapter;
	private ListView lstFindFriend;
	private Handler handler;
	private int lastScroll = 0;
	private ProgressDialog dialog;
	private int page = 1;
	private int accounID = 0;
	Resources res;
	
	Context context;
	
	FriendsManager friendsManager = new FriendsManager();
	private final String[] FB_PERMISSIONS = { "email","read_friendlists","publish_actions"," publish_stream","user_birthday" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.find_friends);
		ListView list = (ListView) findViewById(R.id.esn_findFriend_list);
		
		handler = new Handler();
		
		context = this;
		
		session = Sessions.getInstance(context);
		
		lstFindFriend = (ListView)findViewById(R.id.esn_findFriend_list);
		
		lstFindFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,long id) {
				
			}
		});
		
		
		GetFriendFacebook();
	}

	private void GetFriendFacebook() {
		session = Sessions.getInstance(this);
		
		res = getResources();
		
		mFacebook = new Facebook(WelcomeActivity.APP_ID);
		mAsync = new AsyncFacebookRunner(mFacebook);

		if (session.restoreFaceBook(mFacebook)) {

			Bundle params = new Bundle();
			String query = "select name, email, current_location, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) order by name";
			params.putString("method", "fql.query");
			params.putString("query", query);
			mAsync.request(null, params, new FriendsRequestListener());
		}
		else
		{
			mFacebook.authorize(this, FB_PERMISSIONS, new LoginFaceBookListener(this,mFacebook));			
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
				JSONArray jsonArray = new JSONArray(response);
				int count = jsonArray.length();
				for (int i = 0; i < count; i++) {
					FindFriendDTO friend = new FindFriendDTO();
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					friend.uid = jsonObj.getString("uid");
					friend.Name = jsonObj.getString("name");
					friend.Email = jsonObj.getString("email");
					friend.Avatar = jsonObj.getString("pic_square");
					adapter.add(friend);
				}
				adapter.notifyDataSetChanged();
				
				lstFindFriend.setAdapter(adapter);
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
