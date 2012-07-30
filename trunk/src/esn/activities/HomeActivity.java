package esn.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.readystatesoftware.maps.OnSingleTapListener;
import esn.adapters.EsnListAdapterNoSub;
import esn.classes.EsnListItem;
import esn.classes.EsnMapView;
import esn.classes.Maps;
import esn.classes.Sessions;
import esn.classes.Utils;

public class HomeActivity extends SherlockMapActivity implements
		OnNavigationListener {
	private EsnListItem[] mNavigationItems;
	private Maps map;
	private Resources res;
	protected Handler handler;
	private EsnMapView mapView;
	public final static int CODE_REQUEST_SET_FILTER = 2;
	public static final int REQUEST_CODE_HOME_EVENT_LIST = 121;
	HomeActivity context;

	Sessions sessions;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		res = getResources();
		handler = new Handler();
		setContentView(R.layout.home);
		setupActionBar();
		setupMap();
		setupListNavigate();

		context = this;

		sessions = Sessions.getInstance(context);
	}

	private void setupListNavigate() {
		mNavigationItems = new EsnListItem[2];
		mNavigationItems[0] = new EsnListItem(1);
		mNavigationItems[0].setTitle(res
				.getString(R.string.app_global_viewasmap));
		mNavigationItems[0].setIcon(R.drawable.ic_view_as_map2);

		mNavigationItems[1] = new EsnListItem(2);
		mNavigationItems[1].setTitle(res
				.getString(R.string.app_global_viewaslist));
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
		mapView = (EsnMapView) findViewById(R.id.gmapView);

		mapView.setActivity(this);
		map = new Maps(this, mapView);
		map.displayCurrentLocationMarker();
		// set zoom level to 15
		map.setZoom(16);

		mapView.setOnSingleTapListener(new OnSingleTapListener() {

			@Override
			public boolean onSingleTap(MotionEvent e) {
				map.hideAllBallon();
				return true;
			}

		});
		mapView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				map.hideAllBallon();
			}
		});
	}

	@Override
	protected void onResume() {
		map.enableMyLocation();
		super.onResume();
	}

	@Override
	protected void onPause() {
		map.disableMyLocation();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		com.actionbarsherlock.view.MenuInflater menuInfalte = getSupportMenuInflater();
		menuInfalte.inflate(R.menu.home_menus, menu);
		MenuItem searchItem = menu.findItem(R.id.esn_home_menuItem_search);
		View collapsed = searchItem.getActionView();
		ImageButton btnSearchGo = (ImageButton) collapsed
				.findViewById(R.id.btnSearchGo);
		// set onclic listener
		btnSearchGo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnSearchGoClicked(v);
			}
		});

		return true;
	}

	public void btnSearchGoClicked(View view) {
		EditText txtSearchQuery = ((EditText) findViewById(R.id.searchLocationQuery));
		if (txtSearchQuery != null) {
			String query = txtSearchQuery.getText().toString();
			txtSearchQuery.setText("");
			try {
				map.search(query);
			} catch (Exception e) {
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("Error");
				builder.setMessage(e.getMessage());
				builder.show();
			}
		}

	}

	public void btnDetectMyLocation(View view) {
		map.displayCurrentLocationMarker();
	}

	@SuppressLint("NewApi")
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
		case R.id.esn_home_menuItem_zoomIn:
			map.zoomIn();
			break;
		case R.id.esn_home_menuItem_zoomOut:
			map.zoomOut();
			break;
		case R.id.esn_home_menus_addNewEvent:
			Location currLocation = map.getCurrentLocation();
			if (currLocation != null) {
				double latitude = currLocation.getLatitude();
				double longtitude = currLocation.getLongitude();
				Intent addNewEventIntent = new Intent(this,
						SelectEventLabel.class);
				addNewEventIntent.putExtra("latitude", latitude);
				addNewEventIntent.putExtra("longtitude", longtitude);
				startActivityForResult(addNewEventIntent,
						EsnMapView.REQUEST_CODE_ADD_NEW_EVENT);
			} else {
				Utils.showToast(
						this,
						res.getString(R.string.esn_global_your_location_not_found),
						Toast.LENGTH_SHORT);
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case EsnMapView.REQUEST_CODE_ADD_NEW_EVENT:

				double latitude = data.getDoubleExtra("latitude",
						Double.MIN_VALUE);
				double longtitude = data.getDoubleExtra("longtitude",
						Double.MIN_VALUE);
				String title = data.getStringExtra("eventTitle");
				String description = data.getStringExtra("eventDescription");
				int eventId = data.getIntExtra("eventId", 0);
				int labelIcon = data.getIntExtra("labelIcon", 0);
			//	int labelId = data.getIntExtra("labelId", 0);
				if (latitude != Integer.MIN_VALUE
						&& longtitude != Integer.MIN_VALUE) {
					GeoPoint point = new GeoPoint((int) (latitude * 1E6),
							(int) (longtitude * 1E6));
					map.setEventMarker(point, title, description, eventId, labelIcon);
					mapView.getController().animateTo(point);
				}
				break;
			case CODE_REQUEST_SET_FILTER:
				mapView.new LoadEventsAroundThread(mapView.calculateRadius())
						.start();
				break;
			case REQUEST_CODE_HOME_EVENT_LIST:
				
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		if (itemPosition == 0) {
			Toast.makeText(this, mNavigationItems[itemPosition].getTitle(),
					Toast.LENGTH_SHORT).show();
		} else if (itemPosition == 1) {
			Intent intent = new Intent(context, HomeEventListActivity.class);

			startActivityForResult(intent, REQUEST_CODE_HOME_EVENT_LIST);
			
		} else {
			Toast.makeText(this, mNavigationItems[itemPosition].getTitle(),
					Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {

		return false;
	}

	public void btnVoidMode(View view) {
		Intent intent = new Intent(context, VoiceModeActivity.class);
		startActivity(intent);
	}
}
