package esn.activities;

import java.util.Hashtable;

import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import esn.adapters.ViewTypesListAdapter;
import esn.classes.EsnWebServices;
import esn.classes.ListNavigationItem;
import esn.classes.Maps;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;

public class HomeActivity extends SherlockMapActivity implements
		OnNavigationListener {
	private ListNavigationItem[] mNavigationItems;
	private Maps map;
	private Resources res;
	public static final int REQUEST_CODE_ADD_NEW_EVENT = 1;
	private boolean isPotentialLongPress;
	protected Handler handler;

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
		map.setZoom(5);
		map.setCurrMarkerIcon(R.drawable.ic_current_location);
		// map.displayCurrentLocation();

		new Thread() {
			@Override
			public void run() {
				EventsManager manager = new EventsManager();
				final Events[] events = manager.getAll();
				handler.post(new Runnable() {

					@Override
					public void run() {
						for (Events event : events) {
							GeoPoint point = new GeoPoint(
									(int) (event.EventLat * 1E6),
									(int) (event.EventLong * 1E6));
							map.setMarker(point, event.Title,event.Description,EventType.getDrawable(event.EventTypeID));
						}
					}
				});
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("New Event").setIcon(R.drawable.ic_add)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		MenuItem item;
		item = menu.add("Friends");
		item.setIcon(R.drawable.ic_friends).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		// search
		item = menu.add("Search").setIcon(R.drawable.ic_search);
		item.setActionView(R.layout.collapsible_edittext);// set collapsible
															// action view
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
				| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);// set how to
																// show action
		View collapsed = item.getActionView();
		ImageButton btnSearchGo = (ImageButton) collapsed
				.findViewById(R.id.btnSearchGo);
		// set onclic listener
		btnSearchGo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnSearchGoClicked(v);
			}
		});
		// labels
		menu.add("Labels").setIcon(R.drawable.ic_labels)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		// settings
		menu.add("Settings").setIcon(R.drawable.ic_settings)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add("Navigate").setIcon(R.drawable.ic_search)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
		map.displayCurrentLocation();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		longestTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	private void longestTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			final GeoPoint p = map.getMap().getProjection()
					.fromPixels((int) event.getX(), (int) event.getY());// get
																		// current
																		// point

			new Thread(new Runnable() {
				public void run() {
					Looper.prepare();
					if (isLongPressDetected()) {
						int latitudeE6 = p.getLatitudeE6();
						int longtitudeE6 = p.getLongitudeE6();
						Intent addNewEventIntent = new Intent(map.getContext(),
								SelectEventLabel.class);
						addNewEventIntent.putExtra("latitudeE6", latitudeE6);
						addNewEventIntent
								.putExtra("longtitudeE6", longtitudeE6);
						startActivityForResult(addNewEventIntent,
								REQUEST_CODE_ADD_NEW_EVENT);
					}
				}
			}).start();

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			/*
			 * Only MotionEvent.ACTION_MOVE could potentially be regarded as
			 * part of a longpress, as this event is trigged by the finger
			 * moving slightly on the device screen. Any other events causes us
			 * to cancel this events status as a potential longpress.
			 */
			if (event.getHistorySize() < 1)
				return; // First call, no history

			// Get difference in position since previous move event
			float diffX = event.getX()
					- event.getHistoricalX(event.getHistorySize() - 1);
			float diffY = event.getY()
					- event.getHistoricalY(event.getHistorySize() - 1);

			/*
			 * If position has moved substatially, this is not a long press but
			 * probably a drag action
			 */
			if (Math.abs(diffX) > 0.5f || Math.abs(diffY) > 0.5f) {
				isPotentialLongPress = false;
			}
		} else {
			// This motion is something else, and thus not part of a longpress
			isPotentialLongPress = false;
		}
	}

	protected boolean isLongPressDetected() {
		isPotentialLongPress = true;
		try {
			for (int i = 0; i < 100; i++) {
				Thread.sleep(10);
				System.out.print(i * 10);
				if (!isPotentialLongPress) {
					return false;
				}
			}
			return true;
		} catch (InterruptedException e) {
			return false;
		} finally {
			isPotentialLongPress = false;
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		String itemTitle = item.getTitle().toString();
		if (itemTitle.equals("Search")) {
			item.collapseActionView();

			return true;
		}
		
		if (itemTitle.equals("Friends")) {
			Intent intenFdsList = new Intent(this, FriendListActivity.class);
			startActivity(intenFdsList);
			return true;
		}
		
		if (itemTitle.equals("New Event")) {
			Intent intent = new Intent(this, AddNewEvent.class);
			startActivityForResult(intent, REQUEST_CODE_ADD_NEW_EVENT);
			return true;
		} else {
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_ADD_NEW_EVENT
				&& resultCode == RESULT_OK) {
			int latitude = data.getIntExtra("latitudeE6", 361);
			int longtitude = data.getIntExtra("longtitudeE6", 361);
			String title = data.getStringExtra("eventTitle");
			String description = data.getStringExtra("eventDescription");
			int pointerDrawable = data.getIntExtra("labelIcon", 0);
			if (latitude != 361 && longtitude != 361) {
				GeoPoint newEventPoint = new GeoPoint(latitude, longtitude);
				map.setMarker(newEventPoint, title, description,
						pointerDrawable);
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected boolean isRouteDisplayed() {

		return false;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Toast.makeText(this, mNavigationItems[itemPosition].getText(),
				Toast.LENGTH_SHORT).show();
		return true;
	}
}
