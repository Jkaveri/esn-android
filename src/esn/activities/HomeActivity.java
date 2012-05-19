package esn.activities;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapView;

import esn.adapters.ViewTypesListAdapter;
import esn.models.ListNavigationItem;
import esn.models.Maps;
import esn.models.SearchActionMode;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Window;
import android.widget.Toast;

public class HomeActivity extends SherlockMapActivity implements
		OnNavigationListener {
	private ListNavigationItem[] mNavigationItems;
	private Maps map;
	private ActionMode mMode;
	private Resources res;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		res = getResources();

		setContentView(R.layout.home);
		setupActionBar();
		setupMap();
		setupListNavigate();
	}

	private void setupListNavigate() {
		mNavigationItems = new ListNavigationItem[2];
		mNavigationItems[0] = new ListNavigationItem();
		mNavigationItems[0].setText("View as Map");
		mNavigationItems[0].setIcon(R.drawable.ic_view_as_map2);

		mNavigationItems[1] = new ListNavigationItem();
		mNavigationItems[1].setText("View as List");
		mNavigationItems[1].setIcon(R.drawable.ic_view_as_list);

		Context context = getSupportActionBar().getThemedContext();

		ViewTypesListAdapter list = new ViewTypesListAdapter(context,
				R.layout.sherlock_spinner_item, mNavigationItems);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
	}

	private void setupActionBar() {
		/** setup action bar **/
		/* getSupportActionBar().hide(); */
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		// setup background for top action bar
		// getSupportActionBar().setBackgroundDrawable(
		// getResources().getDrawable(R.drawable.main_transparent));
		// // setup for split items
		// getSupportActionBar().setSplitBackgroundDrawable(
		// getResources().getDrawable(R.drawable.black_transparent));
	}

	private void setupMap() {

		/** setup map **/
		MapView mapView = (MapView) findViewById(R.id.gmapView);
		map = new Maps(this, mapView);
		// set zoom level to 14
		map.setZoom(14);
		map.setCurrMarkerIcon(R.drawable.ic_current_location);
		map.displayCurrentLocation();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item;
		item = menu.add("Friends");
		item.setIcon(R.drawable.ic_friends).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add("Search")
				.setIcon(R.drawable.ic_search)
				.setActionView(R.layout.collapsible_edittext)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		menu.add("Labels")
				.setIcon(R.drawable.ic_labels)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("Settings")
				.setIcon(R.drawable.ic_settings)
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
		Toast.makeText(this, mNavigationItems[itemPosition].getText(),
				Toast.LENGTH_SHORT).show();
		return true;
	}
}
