package esn.activities;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;


import esn.adapters.EsnListAdapterNoSub;
import esn.adapters.ListViewEventHomeAdapter;
import esn.classes.EsnListItem;
import esn.classes.Sessions;
import esn.models.Events;
import esn.models.EventsManager;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class FriendEventsListActivity extends Activity implements
		OnNavigationListener {
	protected static final int MAX_PROGRESS = 1000;
	protected static final String LOG_TAG = "FriendEventsListActivity";
	private Sessions sessions;
	private ProgressDialog dialog;

	private ListViewEventHomeAdapter adapter;
	private int pageNum = 1;
	private int pageSize = 10;
	protected int lastScroll;
	private EsnListItem[] mNavigationItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_list);
		sessions = Sessions.getInstance(this);
		setupListNavigate();
		// setup argument

		// set up progress dialog
		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// set update list event
		ListView listEvent = (ListView) findViewById(R.id.esn_home_listevent);
		adapter = new ListViewEventHomeAdapter(this, new ArrayList<Events>());
		listEvent.setAdapter(adapter);
		listEvent.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int scroll = firstVisibleItem + visibleItemCount;
				boolean acti = scroll == totalItemCount - 1;
				if (acti && scroll > lastScroll) {
					lastScroll = scroll;
					pageNum++;
					Log.d(LOG_TAG, "list scrolled: " + pageNum);
					dialog.show();
					new LoadEventListAround(pageNum, pageSize).start();
				}

			}
		});
		listEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adView, View view,
					int index, long id) {
				Events bean = (Events) adapter.getItem(index);
				Intent it = new Intent(FriendEventsListActivity.this,
						EventDetailActivity.class);

				it.putExtra("id", bean.EventID);
				startActivity(it);
			}

		});

		new LoadEventListAround(pageNum, pageSize).start();

	}

	private void setupListNavigate() {
		mNavigationItems = new EsnListItem[2];
		mNavigationItems[0] = new EsnListItem();
		mNavigationItems[0]
				.setTitle(getString(R.string.str_Friends_Events_ViewAsList));
		mNavigationItems[0].setIcon(R.drawable.ic_view_as_list);

		mNavigationItems[1] = new EsnListItem();
		mNavigationItems[1]
				.setTitle(getString(R.string.str_Friends_Events_ViewAsMap));
		mNavigationItems[1].setIcon(R.drawable.ic_view_as_map2);

		Context context = getActionBar().getThemedContext();

		EsnListAdapterNoSub list = new EsnListAdapterNoSub(context,
				R.layout.spinner_item, mNavigationItems);
		list.setDropDownViewResource(R.layout.spinner_dropdown_item);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setListNavigationCallbacks(list, this);
	}

	@Override
	protected void onDestroy() {
		adapter.stopThread();
		adapter.clearCache();
		super.onDestroy();
	}

	private class LoadEventListAround extends Thread {
		private int pageNum;
		private int pageSize;

		public LoadEventListAround(int pageNum, int pageSize) {
			this.pageNum = pageNum;
			this.pageSize = pageSize;
		}

		@Override
		public void run() {
			try {
				EventsManager eventsManager = new EventsManager();
				Events[] events = eventsManager.getListFriendEvents(
						sessions.currentUser.AccID, pageNum, pageSize);
				runOnUiThread(new LoadEventListAroundHandler(events));
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
			}
		}
	}

	private class LoadEventListAroundHandler implements Runnable {
		private Events[] _events;

		public LoadEventListAroundHandler(Events[] events) {
			_events = events;
		}

		@Override
		public void run() {
			int sum = _events.length;
			for (int i = 0; i < sum; i++) {
				adapter.add(_events[i]);
			}

			adapter.notifyDataSetChanged();

			dialog.dismiss();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("FriendList")
				.setIcon(R.drawable.ic_friend_list)
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
		.setIcon(R.drawable.ic_friends_add)
		.setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		String itemTitle = item.getTitle().toString();
		if (itemTitle.equals("Search")) {
			item.collapseActionView();
			return true;
		} else if (itemTitle.equals("FriendList")) {
			Intent intenFdsLists = new Intent(this, FriendListActivity.class);
			startActivity(intenFdsLists);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			finish();
			return true;
		} else if (itemTitle.equals("FindMoreFriend")) {
			/*Intent intenFdsEvent = new Intent(this, FindFriendsActivity.class);
			startActivity(intenFdsEvent);
			overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);*/
			Toast.makeText(this, R.string.esn_global_function_developing, Toast.LENGTH_LONG).show();
			return true;
		} else {
			return super.onMenuItemSelected(featureId, item);
		}
	}
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long id) {
		String postTitle = mNavigationItems[itemPosition].getTitle().toString();
		if (postTitle.equals(getString(R.string.str_Friends_Events_ViewAsMap))) {
			Intent intent = new Intent(this, FriendEventsActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			finish();
		} else if (postTitle
				.equals(getString(R.string.str_Friends_Events_ViewAsList))) {

			Toast.makeText(this, postTitle, Toast.LENGTH_SHORT).show();
		}
		return true;
	}
}
