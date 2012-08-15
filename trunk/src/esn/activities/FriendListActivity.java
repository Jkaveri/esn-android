package esn.activities;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import esn.adapters.ListViewFriendsAdapter;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.Users;
import esn.models.UsersManager;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class FriendListActivity extends ListActivity implements
		OnNavigationListener {
	private static final int PAGE_SIZE = 8;
	private ListView lstFriend;
	private ListViewFriendsAdapter adapter;
	private int lastScroll = 0;
	private ProgressDialog progressDialog;
	private int page = 1;
	Sessions sessions;
	Resources res;

	public final static int CODE_REQUEST_FRIEND_INFO = 3;
	public static final String LOG_TAG = "FriendListActivity";


	private int friendId = 0;
	private FriendListActivity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_list);

		res = getResources();
		context = this;
		friendId = this.getIntent().getIntExtra("accountID", 0);
		lstFriend = (ListView) findViewById(android.R.id.list);

		adapter = new ListViewFriendsAdapter(this, new ArrayList<Users>());

		sessions = Sessions.getInstance(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(this.getResources().getString(
				R.string.esn_global_loading));
		progressDialog.setMessage("Waiting ....");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		if (friendId == 0) {
			friendId = sessions.currentUser.AccID;
		}

		setupActionBar();
		setupListNavigate();

		lstFriend
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> adView,
							View view, int index, long id) {
						FriendListActivity.this.onItemLongClick(adView, view,
								index, id);
						return true;
					}
				});

		lstFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adView, View view,
					int index, long id) {
				Users bean = (Users) adapter.getItem(index);
				Intent it = new Intent(FriendListActivity.this,
						FriendsActivity.class);

				if (sessions.currentUser.AccID == bean.AccID) {
					it = new Intent(FriendListActivity.this,
							ProfileActivity.class);
				}

				it.putExtra("accountID", bean.AccID);
				startActivityForResult(it, CODE_REQUEST_FRIEND_INFO);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});

		lstFriend.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (visibleItemCount > 0) {
					int scroll = firstVisibleItem + visibleItemCount;
					boolean acti = scroll == totalItemCount;
					if (acti && scroll > lastScroll) {
						lastScroll = scroll;
						page++;
						progressDialog.show();
						new LoadListFriendThread(page, PAGE_SIZE).start();
					}
				}

			}
		});

		lstFriend.setAdapter(adapter);
		new LoadListFriendThread(1, PAGE_SIZE).start();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	public void onDestroy() {
		adapter.stopThread();
		adapter.clearCache();
		lstFriend.setAdapter(null);
		super.onDestroy();
	}

	private void setupListNavigate() {
		getActionBar().setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
		getActionBar().setTitle(getString(R.string.str_Friends_Lists_Title));
	}

	private void setupActionBar() {
		/** setup action bar **/
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);
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

				Intent it = new Intent(FriendListActivity.this,
						FriendsActivity.class);

				if (sessions.currentUser.AccID == bean.AccID) {
					it = new Intent(FriendListActivity.this,
							ProfileActivity.class);
				}

				it.putExtra("accountID", bean.AccID);
				startActivityForResult(it, CODE_REQUEST_FRIEND_INFO);
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
					private String TAG_LOG;

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
										new LoadListFriendThread(1, PAGE_SIZE)
												.start();
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
							Utils.showToast(context,
									res.getString(R.string.esn_global_Error),
									Toast.LENGTH_LONG);
							Log.e(TAG_LOG, e.getMessage());
							e.printStackTrace();
						} catch (IOException e) {
							Utils.showToast(context,
									res.getString(R.string.esn_global_Error),
									Toast.LENGTH_LONG);
							Log.e(TAG_LOG, e.getMessage());
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("FriendEvent")
				.setIcon(R.drawable.ic_friend_event)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		MenuItem searchItem = menu.add("Search").setIcon(R.drawable.ic_search)
				.setActionView(R.layout.collapsible_edittext);
		searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
				| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		View view = searchItem.getActionView();
		ImageButton btnGO = (ImageButton) view.findViewById(R.id.btnSearchGo);
		btnGO.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchFriend(v);
			}
		});

		menu.add("FindMoreFriend")
				.setIcon(R.drawable.ic_friends_add)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Toast.makeText(this, getString(R.string.str_Friends_Lists_Title),
				Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String itemTitle = item.getTitle().toString();
		Log.i("FriendListActivity", itemTitle);
		if (itemTitle.equals("Search")) {
			item.collapseActionView();
			return true;
		} else if (itemTitle.equals("FriendEvent")) {
			Intent intenFdsEvent = new Intent(this, FriendEventsActivity.class);
			startActivity(intenFdsEvent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			finish();
			return true;
		} else if (itemTitle.equals("FindMoreFriend")) {
			Toast.makeText(this, R.string.esn_global_function_developing,
					Toast.LENGTH_LONG).show();
			/*
			 * Intent intenFdsEvent = new Intent(this,
			 * FindFriendsActivity.class); startActivity(intenFdsEvent);
			 * overridePendingTransition(R.anim.push_left_out,
			 * R.anim.push_left_in); finish();
			 */
			return true;
		} else {
			return super.onMenuItemSelected(featureId, item);
		}
	}

	public void searchFriend(View view) {
		if (view != null) {
			EditText editText = (EditText) findViewById(R.id.searchLocationQuery);
			String searchQuery = editText.getText().toString();
			Intent searchIntent = new Intent(this,
					SearchFriendResultActivity.class);
			searchIntent.putExtra("query", searchQuery);
			startActivity(searchIntent);
		}

	}

	private class LoadListFriendThread extends Thread {
		private int pageSize;
		private int pageNum;

		public LoadListFriendThread(int pageNum, int pageSize) {
			this.pageSize = pageSize;
			this.pageNum = pageNum;
		}

		@Override
		public void run() {
			Looper.prepare();
			UsersManager manager = new UsersManager();

			List<Users> itemList;
			try {
				itemList = manager.getFriendsList(pageNum, pageSize, friendId);
				runOnUiThread(new LoadFriendHandler(itemList));
			} catch (IllegalArgumentException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(LOG_TAG, e.getMessage());
				e.printStackTrace();
			} catch (JSONException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(LOG_TAG, e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_connection_error),
						Toast.LENGTH_LONG);
				Log.e(LOG_TAG, e.getMessage());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(LOG_TAG, e.getMessage());
				e.printStackTrace();
			} catch (ParseException e) {
				Utils.showToast(context,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_LONG);
				Log.e(LOG_TAG, e.getMessage());
				e.printStackTrace();
			}

		}
	}

	private class LoadFriendHandler implements Runnable {
		private List<Users> users;
		private boolean doClear;

		public LoadFriendHandler(List<Users> users) {
			this.users = users;
			doClear = false;
		}

		@Override
		public void run() {
			Log.d(LOG_TAG, "load list friend");
			if (users != null) {
				Log.d(LOG_TAG, ": " + users.size());
				if (doClear)
					adapter.clear();
				for (Users user : users) {
					adapter.add(user);
				}
				adapter.notifyDataSetChanged();
			}
			progressDialog.dismiss();
		}
	}
}
