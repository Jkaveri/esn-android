package esn.activities;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONException;

import com.facebook.android.Util;
import esn.adapters.ListViewEventUserAdapter;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.Events;
import esn.models.EventsManager;
import esn.models.Users;
import esn.models.UsersManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

	Intent intent;

	ProfileActivity context;

	private ProgressDialog dialog;

	public Handler handler;

	EventsManager eventsManager = new EventsManager();

	Sessions session;

	UsersManager usersManager = new UsersManager();

	private ListView listUserEvent;

	private ListViewEventUserAdapter adapter;

	private int page = 1;

	Resources res;

	private int lastScroll = 0;

	Users users = new Users();

	protected ArrayList<Events> itemList;

	public final static int CODE_REQUEST_SET_FILTER = 2;

	private static final int PAGE_SIZE = 8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.profile);

		context = this;

		res = getResources();

		// setupActionBar();

		handler = new Handler();

		session = Sessions.getInstance(context);
		listUserEvent = (ListView) findViewById(R.id.esn_setting_profile_listeventuser);
		adapter = new ListViewEventUserAdapter(this, new ArrayList<Events>());
		listUserEvent.setAdapter(adapter);

		ShowInforUser();

		

		listUserEvent.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int scroll = firstVisibleItem + visibleItemCount;
				boolean acti = scroll == totalItemCount - 1;
				if (acti && scroll > lastScroll) {
					lastScroll = scroll;
					page++;
					new GetListEventThread(page, PAGE_SIZE).start();
				}
			}
		});
		new GetListEventThread(1, PAGE_SIZE).start();
	}

	private void ShowInforUser() {

		dialog = new ProgressDialog(this);
		dialog.setTitle(this.getResources().getString(
				R.string.esn_global_loading));
		dialog.setMessage(res.getString(R.string.esn_global_pleaseWait));
		dialog.show();

		ShowProfileThread showProfileThread = new ShowProfileThread();

		showProfileThread.start();
	}

	public void SettingClicked(View v) {
		intent = new Intent(context, SettingsAppActivity.class);

		startActivity(intent);
	}

	public void MessageClicked(View v) {
		intent = new Intent(context, Notification.class);

		startActivity(intent);
	}

	public void FriendsClicked(View v) {
		intent = new Intent(context, FriendListActivity.class);

		startActivity(intent);
	}

	public void PersonalClicked(View v) {
		intent = new Intent(context, EditProfileActivity.class);

		startActivity(intent);
	}


	@Override
	public void onDestroy() {
		adapter.stopThread();
		adapter.clearCache();
		listUserEvent.setAdapter(null);
		super.onDestroy();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		item.getTitle().toString();
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.esn_home_menuItem_search:
			item.collapseActionView();
			break;
		case R.id.esn_home_menuItem_friends:
			Intent intenFdsList = new Intent(this, FriendListActivity.class);
			startActivity(intenFdsList);
			break;
		case R.id.esn_home_menuItem_navigator:

			break;
		case R.id.esn_home_menuItem_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		case R.id.esn_home_menuItem_labels:
			Intent setFilterIntent = new Intent(this, SetFilterActivity.class);
			startActivityForResult(setFilterIntent, CODE_REQUEST_SET_FILTER);
			break;
		case R.id.esn_home_menus_addNewEvent:
			break;
		default:
			break;
		}
		return true;
	}

	public class ShowProfileThread extends Thread {

		public ShowProfileThread() {
		}

		public void run() {

			if (session.currentUser != null) {

				try {
					users = usersManager
							.RetrieveById(session.currentUser.AccID);
					session.currentUser = users;
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}

				if (users != null) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							int id = users.AccID;

							session.put("AccId", id);

							TextView txtName = (TextView) findViewById(R.id.esn_setting_profile_name);
							TextView txtAddress = (TextView) findViewById(R.id.esn_setting_profile_address);

							final String url = users.Avatar;

							if (url != null) {
								new Thread() {
									public void run() {

										Bitmap bitmap = null;

										try {

											bitmap = Utils
													.getBitmapFromURL(url);

										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										handler.post(new SetAvatar(bitmap));

									};
								}.start();
							}
							txtName.setText(users.Name);
							txtAddress.setText(users.Address + ","
									+ users.Street + "," + users.District + ","
									+ users.City + "," + users.Country);

						}
					});
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();

							Util.showAlert(
									context,
									res.getResourceName(R.string.esn_global_Error),
									res.getResourceEntryName(R.string.esn_global_ConnectionError));
						}
					});
				}
			} else {
				Utils.showToast(ProfileActivity.this,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_SHORT);
			}

		}
	}

	public class SetAvatar implements Runnable {

		private Bitmap bitmap;

		public SetAvatar(Bitmap bm) {
			bitmap = bm;
		}

		@Override
		public void run() {
			ImageView avatar = (ImageView) findViewById(R.id.esn_setting_profile_avataruser);

			avatar.setImageBitmap(bitmap);

			dialog.dismiss();
		}

	}

	private class GetListEventThread extends Thread {
		protected static final String LOG_TAG = "GetListEventThread";
		private int pageNum;
		private int pageSize;
		public GetListEventThread(int pageNum, int pageSize){
			this.pageNum = pageNum;
			this.pageSize = pageSize;
		}
		@Override
		public void run() {
			EventsManager eventsManager = new EventsManager();

			try {
				itemList = eventsManager.getEventUserList(pageNum,pageSize,
						session.currentUser.AccID);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						for (Events event : itemList) {
							Log.d(LOG_TAG, event.Title);
							adapter.add(event);
						}
						adapter.notifyDataSetChanged();
					}
				});
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
