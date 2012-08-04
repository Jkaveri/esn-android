package esn.activities;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import esn.adapters.ListViewFriendsAdapter;
import esn.adapters.ListViewNotificationAdapter;
import esn.classes.Sessions;
import esn.models.FriendsListsDTO;
import esn.models.FriendsManager;
import esn.models.NotificationDTO;
import esn.models.UsersManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class Notification extends SherlockActivity implements OnNavigationListener {

	private ListView lstNofication;
	private ListViewNotificationAdapter adapter;
	private Notification context;
	private Handler handler;
	private int lastScroll = 0;
	private ProgressDialog dialog;
	private int page = 1;
	private int accounID = 0;
	Sessions sessions;
	Resources res;
	UsersManager usersManager = new UsersManager();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.notification);
		
		handler = new Handler();
		context = this;
		
		sessions = Sessions.getInstance(context);
		
		setupActionBar();
		setupListNavigate();
		
		setupNotificationList();

		res = getResources();
		
		lstNofication = (ListView) findViewById(R.id.esn_notification_listnotification);
				
		lstNofication.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adView, View view, int index, long id) {
				FriendsListsDTO bean = (FriendsListsDTO) adapter.getItem(index);
				Intent it = new Intent(context, UserPageActivity.class);
				it.putExtra("accountID", bean.AccID);
				startActivity(it);
			}
		});

		lstNofication.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int scroll = firstVisibleItem + visibleItemCount;
				boolean acti = scroll == totalItemCount-1;
				if(acti && scroll > lastScroll){
					lastScroll = scroll;
					page++;
					context.loadNotificationList(8, page);
					Toast.makeText(context, "Load data", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	}
	
	private void setupNotificationList() {
		dialog = new ProgressDialog(this);
		dialog.setTitle(this.getResources().getString(R.string.esn_global_loading));
		dialog.setMessage("Waiting ....");
		dialog.show();
		
		Thread thr = new Thread(new Runnable() {
			
			@Override
			public void run() {

				FriendsManager frdMng = new FriendsManager();
				try {
					final ArrayList<NotificationDTO> itemList = usersManager.getNotification(sessions.currentUser.AccID);
					handler.post(new Runnable() {

						@Override
						public void run() {
							adapter = new ListViewNotificationAdapter(context, itemList);
							dialog.dismiss();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thr.start();
	}

	private void loadNotificationList(final int pageSize, final int pageIndex) {
		
		Thread thr = new Thread(new Runnable() {			
			@Override
			public void run() {

				FriendsManager frdMng = new FriendsManager();
				try {
					final ArrayList<NotificationDTO> itemList = usersManager.getNotification(accounID);
					handler.post(new Runnable() {

						@Override
						public void run() {
							adapter.add(itemList);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thr.start();
	}
	
	@Override
	public void onDestroy() {
		adapter.stopThread();
		adapter.clearCache();
		lstNofication.setAdapter(null);
		super.onDestroy();
	}

	private void setupListNavigate() {
		getSupportActionBar().setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
		getSupportActionBar().setTitle(getString(R.string.str_Friends_Lists_Title));
	}

	private void setupActionBar() {
		/** setup action bar **/
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("FriendEvent")
				.setIcon(R.drawable.ic_friend_event)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("Search")
				.setIcon(R.drawable.ic_search)
				.setActionView(R.layout.collapsible_edittext)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		
		menu.add("FindMoreFriend")
				.setIcon(R.drawable.ic_friend_event)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String itemTitle = item.getTitle().toString();
		if (itemTitle.equals(res.getString(R.string.app_global_search))) {
			item.collapseActionView();
			return true;
		} else if (itemTitle.equals(res.getString(R.string.app_global_friendevent))) {
			finish();
			Intent intenFdsEvent = new Intent(this, FriendEventsActivity.class);
			startActivity(intenFdsEvent);
			return true;
		} else if (itemTitle.equals("FindMoreFriend")) {
			finish();
			Intent intenFdsEvent = new Intent(this, FindFriendsActivity.class);
			startActivity(intenFdsEvent);
			return true;			
		} else {
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Toast.makeText(this, getString(R.string.str_Friends_Lists_Title), Toast.LENGTH_SHORT).show();
		return true;
	}
	
}
