package esn.activities;

import java.io.IOException;
import java.util.Currency;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.view.MenuInflater;
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
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.android.Util;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;

import esn.adapters.EsnListAdapterNoSub;
import esn.classes.EsnListItem;
import esn.classes.EsnMapView;
import esn.classes.EsnWebServices;
import esn.classes.EventOverlayItem;
import esn.classes.FilterLabelsDialog;
import esn.classes.ListNavigationItem;
import esn.classes.Maps;
import esn.classes.Sessions;
import esn.models.AppEnums;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;

public class HomeActivity extends SherlockMapActivity implements
		OnNavigationListener {
	private EsnListItem[] mNavigationItems;
	private Maps map;
	private Resources res;
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
	}

	private void setupListNavigate() {
		mNavigationItems = new EsnListItem[2];
		mNavigationItems[0] = new EsnListItem(1);
		mNavigationItems[0].setTitle("View as Map");
		mNavigationItems[0].setIcon(R.drawable.ic_view_as_map2);

		mNavigationItems[1] = new EsnListItem(2);
		mNavigationItems[1].setTitle("View as List");
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
		EsnMapView mapView = (EsnMapView) findViewById(R.id.gmapView);
		mapView.setActivity(this);
		map = new Maps(this, mapView);
		// set zoom level to 14
		map.setZoom(15);
		map.setCurrMarkerIcon(R.drawable.ic_current_location);
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
		map.displayCurrentLocation();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		String itemTitle = item.getTitle().toString();
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
			FilterLabelsDialog builder = new FilterLabelsDialog(this);
			builder.addItem(res, "Ket xe", R.drawable.ic_event_label_1, 1);
			builder.addItem(res, "Ket xe", R.drawable.ic_event_label_1, 2);
			builder.addItem(res, "Ket xe", R.drawable.ic_event_label_1, 3);
			builder.addItem(res, "Ket xe", R.drawable.ic_event_label_1, 4);
			builder.addItem(res, "Ket xe", R.drawable.ic_event_label_1, 4);
			builder.addItem(res, "Ket xe", R.drawable.ic_event_label_1, 4);
			builder.addItem(res, "Ket xe", R.drawable.ic_event_label_1, 4);
			builder.addItem(res, "Ket xe", R.drawable.ic_event_label_1, 4);
			builder.addItem(res, "Ket xe", R.drawable.ic_event_label_1, 4);
			builder.addItem(res, "Ket xe", R.drawable.ic_event_label_1, 4);
			builder.addItem(res, "Ket xe", R.drawable.ic_event_label_1, 4);
			builder.setOnClickListener(new FilterLabelsDialog.ItemMenuOnClickListener() {

				@Override
				public void onClick(int menuId) {
					switch (menuId) {
					case 1:
						Toast.makeText(HomeActivity.this, "Ket xe",
								Toast.LENGTH_LONG).show();
						break;

					default:
						break;
					}
				}
			});
			Dialog filterLabelDialog = builder.createMenu("Labels");
			filterLabelDialog.show();
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
			}
			break;
		default:
			break;
		}
		return true;
	}

	private void labelsClicked() {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EsnMapView.REQUEST_CODE_ADD_NEW_EVENT
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
			String picture = data.getStringExtra("picture");
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
				event.Picture = (picture == null) ? "" : picture;
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
		Toast.makeText(this, mNavigationItems[itemPosition].getTitle(),
				Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {

		return false;
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
