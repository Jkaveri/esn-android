package esn.activities;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapView;
import esn.adapters.ListViewFriendsAdapter;
import esn.adapters.EsnListAdapterNoSub;
import esn.classes.EsnListItem;
import esn.classes.ListNavigationItem;
import esn.classes.Maps;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class FriendEventsActivity extends SherlockMapActivity implements
		OnNavigationListener, OnItemClickListener {
	private EsnListItem[] mNavigationItems;
	private Maps map;
	private ActionMode mMode;
	private Resources res;
	private MapView mapView;

	private ListView lstFdEvents;
	private ListViewFriendsAdapter adapter;
	private ArrayList<Object> itemList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setRes(getResources());

		setContentView(R.layout.friend_events);
		setupActionBar();
		setupMap();
		setupFdEvent();
		setupListNavigate();
		setupListView();
	}

	private void setupListView() {
		itemList = new ArrayList<Object>();
		addObjectToList(R.drawable.ic_search, "Search", "Search desc");
		addObjectToList(R.drawable.ic_settings, "Settings", "Settings desc");

		lstFdEvents = (ListView) findViewById(R.id.lisvFdEvents);
//		adapter = new ListViewFriendsAdapter(this, itemList);
		lstFdEvents.setAdapter(adapter);
		lstFdEvents.setOnItemClickListener(this);
	}

	// Add one item into the Array List
	private void addObjectToList(int image, String title, String desc) {
		// bean = new ItemBean();
		// bean.setDescription(desc);
		// bean.setImage(image);
		// bean.setTitle(title);
		// itemList.add(bean);
	}

	private void setupFdEvent() {

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

	private void setupMap() {

		/** setup map **/
		mapView = (MapView) findViewById(R.id.maps_friend_google);
		map = new Maps(this, mapView);
		// set zoom level to 14
		map.setZoom(14);
		map.setCurrMarkerIcon(R.drawable.ic_current_location);
		map.displayCurrentLocation();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		// TODO Auto-generated method stub
		// ItemBean bean = (ItemBean)adapter.getItem(position);
		// Toast.makeText(this,
		// "Title => "+bean.getTitle()+" n Description => "+bean.getDescription(),
		// Toast.LENGTH_SHORT).show();
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
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		String postTitle = mNavigationItems[itemPosition].getTitle().toString();
		Toast.makeText(this, postTitle, Toast.LENGTH_SHORT).show();

		if (postTitle.equals(getString(R.string.str_Friends_Events_ViewAsMap))) {
			lstFdEvents.setVisibility(View.INVISIBLE);
			mapView.setVisibility(View.VISIBLE);
		} else if (postTitle
				.equals(getString(R.string.str_Friends_Events_ViewAsList))) {
			lstFdEvents.setVisibility(View.VISIBLE);
			mapView.setVisibility(View.INVISIBLE);
		}
		return true;
	}

	public void setmMode(ActionMode mMode) {
		this.mMode = mMode;
	}

	public ActionMode getmMode() {
		return mMode;
	}

	public void setRes(Resources res) {
		this.res = res;
	}

	public Resources getRes() {
		return res;
	}
}
