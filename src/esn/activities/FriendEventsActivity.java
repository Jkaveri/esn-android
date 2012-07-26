package esn.activities;

import java.util.ArrayList;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapView;
import esn.adapters.ListViewEventHomeAdapter;
import esn.adapters.EsnListAdapterNoSub;
import esn.classes.EsnListItem;
import esn.classes.Maps;
import esn.classes.Sessions;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

public class FriendEventsActivity extends SherlockMapActivity implements
		OnNavigationListener, OnItemClickListener {
	
	private int pageIndex = 0;
	private static int PAGE_SIZE = 10;
	private int lastScroll = 0;
	
	private EsnListItem[] mNavigationItems;
	private Maps map;
	private ListView lstFdEvents;
	private ListViewEventHomeAdapter adapter;
	private ArrayList<Events> itemList;
	private EventsManager eventsMng;
	private Thread th;
	private Handler handler;
	private Activity context;
	private Sessions sessions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_events);
		
		context = this;
		
		eventsMng = new EventsManager();
		handler = new Handler();		
		sessions = Sessions.getInstance(context);		
		
		map = new Maps(this, (MapView) findViewById(R.id.maps_friend_google));
		// set zoom level to 14
		map.setZoom(16);
		
		//Setup list
		lstFdEvents = (ListView) findViewById(R.id.lisvFdEvents);
		lstFdEvents.setOnItemClickListener(this);
		adapter = new ListViewEventHomeAdapter(context, new ArrayList<Events>());
		lstFdEvents.setAdapter(adapter);
		
		lstFdEvents.setOnScrollListener(new OnScrollListener() {
			
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
					loadEvent();
					Toast.makeText(context, "Load data", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		loadEvent();
		
		setupActionBar();
		setupListNavigate();		
	}
	

	@Override
	public void onDestroy() {
		adapter.stopThread();
		adapter.clearCache();
		lstFdEvents.setAdapter(null);
		super.onDestroy();
	}

	private void loadEvent() {
		if(th != null){
			th.interrupt();
			th = null;
		}
		
		th = new Thread(new Runnable() {			

			@Override
			public void run() {
				pageIndex++;
				
				try {
					itemList = eventsMng.getListEventsFriends(sessions.currentUser.AccID, pageIndex, PAGE_SIZE);
				} catch (Exception e) {
					itemList = null;
					pageIndex --;
				}
				
				if(itemList != null && itemList.size() > 0){
					handler.post(new Runnable() {
	
						@Override
						public void run() {
							adapter.add(itemList);
						}
					});
					
					for (Events ev : itemList) {
						map.setMarker(ev.getPoint(), ev.Title, ev.Description, EventType.getIconId(ev.EventTypeID, ev.getLevel()));
					}
				}
			}
		});
		
		th.start();
	}
	
	private void setupListNavigate() {
		mNavigationItems = new EsnListItem[2];
		mNavigationItems[0] = new EsnListItem();
		mNavigationItems[0]
				.setTitle(getString(R.string.str_Friends_Events_ViewAsMap));
		mNavigationItems[0].setIcon(R.drawable.ic_view_as_map2);

		mNavigationItems[1] = new EsnListItem();
		mNavigationItems[1]
				.setTitle(getString(R.string.str_Friends_Events_ViewAsList));
		mNavigationItems[1].setIcon(R.drawable.ic_view_as_list);

		Context context = getSupportActionBar().getThemedContext();

		EsnListAdapterNoSub list = new EsnListAdapterNoSub(context,
				R.layout.sherlock_spinner_item, mNavigationItems);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
	}

	private void setupActionBar() {
		/** setup action bar **/
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
	}


	@Override
	public void onItemClick(AdapterView<?> adView, View view, int position, long id) {
		
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
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String itemTitle = item.getTitle().toString();
		if (itemTitle.equals("Search")) {
			item.collapseActionView();
			return true;
		} else if (itemTitle.equals("FriendList")) {
			finish();
			Intent intenFdsLists = new Intent(this, FriendListActivity.class);
			startActivity(intenFdsLists);
			return true;
		} else {
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		String postTitle = mNavigationItems[itemPosition].getTitle().toString();
		Toast.makeText(this, postTitle, Toast.LENGTH_SHORT).show();

		if (postTitle.equals(getString(R.string.str_Friends_Events_ViewAsMap))) {
			lstFdEvents.setVisibility(View.INVISIBLE);
			map.getMap().setVisibility(View.VISIBLE);
		} else if (postTitle.equals(getString(R.string.str_Friends_Events_ViewAsList))) {
			lstFdEvents.setVisibility(View.VISIBLE);
			map.getMap().setVisibility(View.INVISIBLE);
		}
		return true;
	}
}
