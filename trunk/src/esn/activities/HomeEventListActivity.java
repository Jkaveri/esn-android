package esn.activities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import esn.adapters.EsnListAdapterNoSub;
import esn.adapters.ListViewEventHomeAdapter;
import esn.classes.EsnListItem;
import esn.classes.EsnMapView;
import esn.classes.Sessions;
import esn.models.Events;
import esn.models.EventsManager;
import esn.models.UsersManager;

public class HomeEventListActivity extends SherlockActivity implements OnNavigationListener {

	private ProgressDialog dialog;

	public Handler handler;
	
	EventsManager manager = new EventsManager();
	
	Sessions session;
	
	HomeEventListActivity context;
	
	UsersManager usersManager = new UsersManager();
	
	
	private ListView lstEvent;
	
	private ListViewEventHomeAdapter adapter;
	private int lastScroll = 0;
	private int page = 1;
	
	Resources res;
	
	EventsManager eventsManager = new EventsManager();
	
	private EsnListItem[] mNavigationItems;
	
	public final static  int CODE_REQUEST_SET_FILTER = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.home_list);
		
		setupActionBar();
		setupListNavigate();
		
		context = this;
		
		session = Sessions.getInstance(context);
		
		res = getResources();
		
		handler = new Handler();
		
		dialog = new ProgressDialog(this);
		dialog.setTitle(getResources().getString(R.string.esn_global_loading));
		dialog.setMessage(getResources().getString(R.string.esn_global_pleaseWait));
		dialog.show();
		
		GetListComment();
		
		lstEvent = (ListView)findViewById(R.id.esn_home_listevent);
		
		lstEvent.setOnScrollListener(new OnScrollListener() {
			
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
					page++;
					context.loadEventList(10, page);
					//Toast.makeText(context, "Load data", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		lstEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adView, View view, int index, long id) {
				Events bean = (Events) adapter.getItem(index);
				Intent it = new Intent(context, EventDetailActivity.class);
				it.putExtra("id", bean.EventID);
				startActivity(it);
			}
		});

	}
	private void setupListNavigate() {
		
		mNavigationItems = new EsnListItem[2];
		mNavigationItems[0] = new EsnListItem(1);
		mNavigationItems[0].setTitle("View as List");
		mNavigationItems[0].setIcon(R.drawable.ic_view_as_list);
		
		mNavigationItems[1] = new EsnListItem(2);
		mNavigationItems[1].setTitle("View as Map");
		mNavigationItems[1].setIcon(R.drawable.ic_view_as_map2);
		
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
				
			} catch (Exception e) {
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("Error");
				builder.setMessage(e.getMessage());
				builder.show();
			}
		}

	}
	
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		
		if(itemPosition==0)
		{
			Toast.makeText(this, mNavigationItems[itemPosition].getTitle(),
					Toast.LENGTH_SHORT).show();
		}
		else if(itemPosition==1)
		{
			Intent intent = new Intent(context,HomeActivity.class);
			
			startActivity(intent);
		}
		else
		{
			Toast.makeText(this, mNavigationItems[itemPosition].getTitle(),
					Toast.LENGTH_SHORT).show();
		}
		return true;
	}
	
	@SuppressLint("NewApi")
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
			Intent setFilterIntent = new Intent(this, SetFilterActivity.class);
			startActivityForResult(setFilterIntent,CODE_REQUEST_SET_FILTER);
			break;
		case R.id.esn_home_menus_addNewEvent:
			break;
		default:
			break;
		}
		return true;
	}
	
	public void GetListComment()
	{
		Thread thr = new Thread(new Runnable() {
			
			Handler hd = new Handler();
			@Override
			public void run() {

				try {
					
					final ArrayList<Events> itemList = eventsManager.getAvailableEventsList(page,10);
					
					hd.post(new Runnable() {

						@Override
						public void run() {
							
							adapter = new ListViewEventHomeAdapter(HomeEventListActivity.this,itemList);
							
							lstEvent.setAdapter(adapter);
							
							dialog.dismiss();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thr.start();
	}
	
	@Override
	public void onDestroy() {
		adapter.stopThread();
		adapter.clearCache();
		lstEvent.setAdapter(null);
		super.onDestroy();
	}	
	
	
	private void loadEventList(final int pageSize, final int pageIndex) {
		
		Thread thr = new Thread(new Runnable() {
			
			@Override
			public void run() {

				try {
					
					final ArrayList<Events> itemList = eventsManager.getAvailableEventsList(pageIndex,pageSize);
					
					handler.post(new Runnable() {

						@Override
						public void run() {
							adapter.add(itemList);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thr.start();
	}
}
