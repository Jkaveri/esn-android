package esn.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import esn.adapters.ListViewFriendsAdapter;
import esn.classes.Sessions;
import esn.models.Users;
import esn.models.UsersManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class SearchFriendResultActivity extends ListActivity {
	public static final String LOG_TAG = "SearchFriendResultActivity";
	private ListView lstFriend;
	private String query;
	private Sessions sessions;
	private ListViewFriendsAdapter adapter;
	private ProgressDialog progressDialog;
	private Context context;
	private Resources res;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_list);
		sessions = Sessions.getInstance(this);
		getActionBar().setDisplayShowHomeEnabled(true);
		res = getResources();
		context = this;
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.esn_global_loading);
		progressDialog
				.setMessage(res.getString(R.string.esn_global_pleaseWait));
		progressDialog.show();

		Intent data = getIntent();

		query = data.getStringExtra("query");
		setupListView();
		if (query != null && query.length() > 0)

			new SearchFriendThread(sessions.currentUser.AccID, query).start();

	}

	private void setupListView() {
		lstFriend = (ListView) findViewById(android.R.id.list);
		adapter = new ListViewFriendsAdapter(this, new ArrayList<Users>());
		lstFriend.setAdapter(adapter);
		lstFriend
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> adView,
							View view, int index, long id) {
						SearchFriendResultActivity.this.onItemLongClick(adView,
								view, index, id);
						return true;
					}
				});
		lstFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adView, View view,
					int index, long id) {
				Users bean = (Users) adapter.getItem(index);
				Intent it = new Intent(context, FriendsActivity.class);

				if (sessions.currentUser.AccID == bean.AccID) {
					it = new Intent(context, ProfileActivity.class);
				}

				it.putExtra("accountID", bean.AccID);
				startActivity(it);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});
	}

	protected void onItemLongClick(AdapterView<?> adView, View view, int index,
			long id) {

		final Users bean = (Users) adapter.getItem(index);

		final Dialog dialog = new Dialog(this);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.diag_frd_slec);

		// dialog.setTitle("Title...");

		TextView dis = (TextView) dialog
				.findViewById(R.id.txt_Friends_Diaglog_Discript);
		dis.setText("Phone: " + bean.Phone);
		TextView fullname = (TextView) dialog
				.findViewById(R.id.txt_Friends_Diaglog_FullName);
		fullname.setText(bean.Name);
		ImageView image = (ImageView) dialog
				.findViewById(R.id.img_Friends_Diaglog_Avatar);
		adapter.displayImage(bean.Avatar, image);
		Button btnVisit = (Button) dialog
				.findViewById(R.id.btn_Friends_Diaglog_Visit);
		// if button is clicked, close the custom dialog
		btnVisit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

				Intent it = new Intent(context, FriendsActivity.class);

				if (sessions.currentUser.AccID == bean.AccID) {
					it = new Intent(context, ProfileActivity.class);
				}

				it.putExtra("accountID", bean.AccID);
				startActivity(it);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});

		Button btnUnfriend = (Button) dialog
				.findViewById(R.id.btn_Friends_Diaglog_Unfriend);
		// if button is clicked, close the custom dialog
		btnUnfriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				final int fId = bean.AccID;
				final int aId = sessions.currentUser.AccID;
				new Thread() {
					public void run() {

						try {
							UsersManager manager = new UsersManager();
							boolean rs = manager.UnFriend(aId, fId);

							if (rs) {
								runOnUiThread(new Runnable() {
									public void run() {
										dialog.dismiss();
										Toast.makeText(
												context,
												res.getString(R.string.btn_Friends_Lists_Diaglog_unfriendsuccess),
												Toast.LENGTH_SHORT).show();
									}
								});
							} else {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										dialog.dismiss();
										Toast.makeText(
												context,
												res.getString(R.string.btn_Friends_Lists_Diaglog_unfriendnotsuccess),
												Toast.LENGTH_SHORT).show();

									}
								});

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
			}
		});

		Button btnClose = (Button) dialog
				.findViewById(R.id.btn_Friends_Diaglog_Close);
		// if button is clicked, close the custom dialog
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private class SearchFriendThread extends Thread {
		private int accId;
		private String name;

		public SearchFriendThread(int accId, String name) {
			this.accId = accId;
			this.name = name;
		}

		@Override
		public void run() {
			UsersManager manager = new UsersManager();
			try {
				List<Users> users = manager.SearchFriend(accId, name);
				runOnUiThread(new LoadFriendHandler(users));

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// loi ket noi
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class LoadFriendHandler implements Runnable {
		private List<Users> users;

		public LoadFriendHandler(List<Users> users) {
			this.users = users;
		}

		@Override
		public void run() {
			Log.d(LOG_TAG, "load list friend");
			Log.d(LOG_TAG, ": " + users.size());
			if (users != null && users.size() > 0) {
				adapter.clear();
				for (Users user : users) {
					adapter.add(user);
				}
				adapter.notifyDataSetChanged();

			} else {
				Toast.makeText(context, "Ko tim thay", Toast.LENGTH_SHORT).show();
			}
			progressDialog.dismiss();

		}
	}
}
