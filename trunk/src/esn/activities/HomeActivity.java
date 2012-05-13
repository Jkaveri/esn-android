package esn.activities;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Path.FillType;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TableLayout.LayoutParams;

public class HomeActivity extends SherlockMapActivity implements OnNavigationListener {
	private MapView mapView;
	private MapController mapController;
	private Resources res;
	private String[] mViewTypes;
	public static int THEME = R.style.Theme_Sherlock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(THEME);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		res = getResources();

		setContentView(R.layout.home);
		setupActionBar();
		setupMap();
		setupListNavigate();
	}

	private void setupListNavigate() {
		mViewTypes = res.getStringArray(R.array.view_types);
		Context context = getSupportActionBar().getThemedContext();

		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(
				context, R.array.view_types, R.layout.sherlock_spinner_item);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
	}

	private void setupActionBar() {
		/** setup action bar **/
		/* getSupportActionBar().hide(); */
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		// setup background for top action bar
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.main_transparent));
		// setup for split items
		getSupportActionBar().setSplitBackgroundDrawable(
				getResources().getDrawable(R.drawable.black_transparent));
	}

	private void setupMap() {
		/** setup map **/
		mapView = (MapView) findViewById(R.id.gmapView);
		// mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(12);
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
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
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
		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
	   	Toast.makeText(this, mViewTypes[itemPosition], Toast.LENGTH_LONG).show();
		return true;
	}
}
