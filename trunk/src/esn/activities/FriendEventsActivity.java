package esn.activities;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

import esn.adapters.EsnListAdapterNoSub;
import esn.classes.EsnListItem;
import esn.classes.EsnMapView;
import esn.classes.Maps;
import esn.classes.Sessions;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class FriendEventsActivity extends MapActivity implements
		OnNavigationListener, OnItemClickListener {

	private EsnListItem[] mNavigationItems;
	private Maps map;
	private EventsManager eventsMng;
	private Thread th;
	private Activity context;
	private Sessions sessions;
	private EsnMapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_events);

		context = this;

		eventsMng = new EventsManager();
		sessions = Sessions.getInstance(context);
		mapView = (EsnMapView) findViewById(R.id.gmapView);
		mapView.setCreateEventByLongPress(false);
		mapView.setLoadEventAround(false);
		map = new Maps(this, mapView);
		// set zoom level to 14
		map.setZoom(10);
		loadEvent();
		setupActionBar();
		setupListNavigate();
	}

	@Override
	public void onDestroy() {
		map.destroy();
		super.onDestroy();
	}

	private void loadEvent() {
		if (th != null) {
			th.interrupt();
			th = null;
		}

		th = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					final Events[] events = eventsMng.getListFriendEvents(
							sessions.currentUser.AccID, 0, 10);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							map.clearEventMarker();
							for (int i = 0; i < events.length; i++) {
								Events event = events[i];
								GeoPoint point = event.getPoint();
								int marker = EventType.getIconId(
										event.EventTypeID, event.getLevel());
								map.setEventMarker(point, event.Title,
										event.Description, event.EventID,
										marker);
							}
							map.postInvalidate();
						}
					});
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

		Context context = getActionBar().getThemedContext();

		EsnListAdapterNoSub list = new EsnListAdapterNoSub(context,
				R.layout.spinner_item, mNavigationItems);
		list.setDropDownViewResource(R.layout.spinner_dropdown_item);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setListNavigationCallbacks(list, this);
	}

	private void setupActionBar() {
		/** setup action bar **/
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
	}

	@Override
	public void onItemClick(AdapterView<?> adView, View view, int position,
			long id) {

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
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String itemTitle = item.getTitle().toString();
		if (itemTitle.equals("Search")) {
			item.collapseActionView();
			return true;
		} else if (itemTitle.equals("FriendList")) {
			Intent intenFdsLists = new Intent(this, FriendListActivity.class);
			startActivity(intenFdsLists);
			overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
			finish();
			return true;
		} else if (itemTitle.equals("FindMoreFriend")) {
			/*Intent intenFdsEvent = new Intent(this, FindFriendsActivity.class);
			startActivity(intenFdsEvent);
			overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);*/
			Toast.makeText(this, R.string.esn_global_function_developing, Toast.LENGTH_LONG).show();
			return true;
		}else {
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
		if (postTitle.equals(getString(R.string.str_Friends_Events_ViewAsMap))) {
			Toast.makeText(this, postTitle, Toast.LENGTH_SHORT).show();
		} else if (postTitle
				.equals(getString(R.string.str_Friends_Events_ViewAsList))) {
			Intent intent = new Intent(this, FriendEventsListActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
			finish();
		}
		return true;
	}

}
