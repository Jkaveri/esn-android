package esn.activities;

import java.io.IOException;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.SyncStateContract.Helpers;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
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
import com.facebook.android.Util;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;

import esn.adapters.ViewTypesListAdapter;
import esn.classes.EsnWebServices;
import esn.classes.EventOverlayItem;
import esn.classes.ListNavigationItem;
import esn.classes.Maps;
import esn.classes.Sessions;
import esn.models.AppEnums;
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
	private ProgressDialog progressDialog;
	private ProgressDialog dialog;

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
		LoadEvent();

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
		TapControlledMapView mapView = (TapControlledMapView) findViewById(R.id.gmapView);
		map = new Maps(this, mapView);
		// set zoom level to 14
		map.setZoom(10);
		map.setCurrMarkerIcon(R.drawable.ic_current_location);
		mapView.setOnSingleTapListener(new OnSingleTapListener() {

			@Override
			public boolean onSingleTap(MotionEvent e) {
				map.hideAllBallon();
				return true;
			}
		});
	}

	private void LoadEvent() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(res.getString(R.string.esn_home_loadingEvent));
		progressDialog.setMessage(res.getString(R.string.esn_global_loading));
		progressDialog.show();
		LoadEventsThread loadEvent = new LoadEventsThread();
		loadEvent.start();
		Log.d("esn", "Load event start");
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
		menu.add("Zoom in").setIcon(R.drawable.ic_search)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("Zoom out").setIcon(R.drawable.ic_search)
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
			// get current point
			final GeoPoint p = map.getMap().getProjection()
					.fromPixels((int) event.getX(), (int) event.getY());

			new Thread(new Runnable() {
				public void run() {
					Looper.prepare();
					if (isLongPressDetected()) {
						int latitudeE6 = p.getLatitudeE6();
						int longtitudeE6 = p.getLongitudeE6();
						Intent addNewEventIntent = new Intent(map.getContext(),
								SelectEventLabel.class);
						addNewEventIntent
								.putExtra("latitude", latitudeE6 / 1E6);
						addNewEventIntent.putExtra("longtitude",
								longtitudeE6 / 1E6);
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
			for (int i = 0; i < 75; i++) {
				Thread.sleep(10);
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
		}

		if (itemTitle.equals("Settings")) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		if (itemTitle.equals("Zoom in")) {
			map.zoomIn();
			return true;
		}
		if (itemTitle.equals("Zoom out")) {
			map.zoomOut();
			return true;
		} else {
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_ADD_NEW_EVENT
				&& resultCode == RESULT_OK) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle(res.getString(R.string.esn_global_loading));
			progressDialog.setMessage(res
					.getString(R.string.esn_global_pleaseWait));
			progressDialog.show();
			double latitude = data.getDoubleExtra("latitude", Double.MIN_VALUE);
			double longtitude = data.getDoubleExtra("longtitude",
					Double.MIN_VALUE);
			String title = data.getStringExtra("eventTitle");
			String description = data.getStringExtra("eventDescription");
			int pointerDrawable = data.getIntExtra("labelIcon", 0);
			int labelId = data.getIntExtra("labelId", 0);
			if (latitude != Integer.MIN_VALUE
					&& longtitude != Integer.MIN_VALUE) {
				Events event = new Events();
				Sessions session = Sessions.getInstance(this);
				event.AccID = session.currentUser.AccID;
				event.EventTypeID = labelId;
				event.Title = title;
				event.Description = description;
				// @todo: nang chup hinh khi tao event
				event.Picture = "";
				event.EventLat = latitude;
				event.EventLng = longtitude;
				event.ShareType = AppEnums.ShareTypes.Public;
				new CreateEventsThread(event).start();
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Toast.makeText(this, mNavigationItems[itemPosition].getText(),
				Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {

		return false;
	}

	private class LoadEventsThread extends Thread {
		@Override
		public void run() {
			Log.d("esn", "Load event thread bat dau");
			Location current = map.getCurrentLocation();
			if (current != null) {
				EventsManager manager = new EventsManager();
				Events[] events;
				try {
					events = manager.getAvailableEvents();
					Log.d("esn", "Da load xong events");
					handler.post(new LoadEventSuggess(events));

				} catch (IllegalArgumentException e) {
					Log.d("esn", e.getMessage());
					e.printStackTrace();
				} catch (JSONException e) {
					Log.d("esn", e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					Log.d("esn", e.getMessage());
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					Log.d("esn", e.getMessage());
					e.printStackTrace();
				}

			} else {
				handler.post(new Runnable() {

					@Override
					public void run() {
						progressDialog.dismiss();
					}
				});
			}
		}
	}

	private class CreateEventsThread extends Thread {
		private Events event;

		public CreateEventsThread(Events event) {
			this.event = event;
		}

		@Override
		public void run() {
			EventsManager manager = new EventsManager();
			try {
				Events event = manager.setEntity(this.event);
				if (event.EventID > 0) {
					handler.post(new AddEventToMapHandler(event));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class LoadEventSuggess implements Runnable {
		private Events[] events;

		public LoadEventSuggess(Events[] events) {
			this.events = events;
		}

		@Override
		public void run() {
			Log.d("esn", "Success load event!");
			if (events.length > 0) {
				for (Events event : events) {

					GeoPoint point = new GeoPoint((int) (event.EventLat * 1E6),
							(int) (event.EventLng * 1E6));
					EventOverlayItem item = new EventOverlayItem(point,
							event.Title, event.Description, event.EventID);

					map.setMarker(
							item,
							EventType.getIconId(event.EventTypeID,
									event.getLevel()));
					Log.d("homeEvents", event.EventLat + "|" + event.EventLng);
				}
				map.getMap().invalidate();
			}
			progressDialog.dismiss();
		}

	}

	private class AddEventToMapHandler implements Runnable {
		private Events event;

		public AddEventToMapHandler(Events event) {
			this.event = event;
		}

		@Override
		public void run() {
			GeoPoint point = new GeoPoint((int) (event.EventLat * 1E6),
					(int) (event.EventLng * 1E6));
			EventOverlayItem item = new EventOverlayItem(point, event.Title,
					event.Description, event.EventID);

			map.setMarker(item,
					EventType.getIconId(event.EventTypeID, event.getLevel()));
			map.getMap().invalidate();
			progressDialog.dismiss();
			Log.d("create event in: ", event.EventLat + "|" + event.EventLng);

		}
	}
}
