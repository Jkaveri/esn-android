package esn.activities;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import esn.adapters.ListViewFriendsAdapter;
import esn.models.FriendsListsDTO;
import esn.models.FriendsManager;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class FriendListActivity extends SherlockActivity implements OnNavigationListener{
	private ListView lstFriend;
	private ListViewFriendsAdapter adapter;
	private FriendListActivity context;
	private Handler handler;
	private int lastScroll = 0;
	private ProgressDialog dialog;
	private int page = 1;
	private int accounID = 9;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);

		handler = new Handler();
		context = this;
		setContentView(R.layout.friends_list);
		setupActionBar();
		setupListNavigate();
		
		setupFriendList();

		lstFriend = (ListView) findViewById(R.id.lisvFriends);
		
		lstFriend.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adView, View view, int index, long id) {
				context.onItemLongClick(adView, view, index, id);
				return true;
			}
		});
		
		lstFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adView, View view, int index, long id) {
				FriendsListsDTO bean = (FriendsListsDTO) adapter.getItem(index);
				Intent it = new Intent(context, UserPageActivity.class);
				it.putExtra("accountID", bean.AccID);
				startActivity(it);
			}
		});

		lstFriend.setOnScrollListener(new OnScrollListener() {
			
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
					context.loadFriendList(8, page);
					Toast.makeText(context, "Load data", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		adapter.stopThread();
		adapter.clearCache();
		lstFriend.setAdapter(null);
		super.onDestroy();
	}

	private void setupListNavigate() {
		getSupportActionBar().setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
		getSupportActionBar().setTitle(getString(R.string.str_Friends_Lists_Title));
	}

	private void setupActionBar() {
		/** setup action bar **/
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}

	private void setupFriendList() {
		dialog = new ProgressDialog(this);
		dialog.setTitle(this.getResources().getString(R.string.esn_global_loading));
		dialog.setMessage("Waiting ....");
		dialog.show();
		
		Thread thr = new Thread(new Runnable() {
			
			@Override
			public void run() {

				FriendsManager frdMng = new FriendsManager();
				try {
					final ArrayList<FriendsListsDTO> itemList = frdMng.getFriendsList(8, page, accounID);
					handler.post(new Runnable() {

						@Override
						public void run() {
							adapter = new ListViewFriendsAdapter(context, itemList);
							lstFriend.setAdapter(adapter);
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
	
	private void loadFriendList(final int pageSize, final int pageIndex) {
		Thread thr = new Thread(new Runnable() {
			
			@Override
			public void run() {

				FriendsManager frdMng = new FriendsManager();
				try {
					final ArrayList<FriendsListsDTO> itemList = frdMng.getFriendsList(pageSize, pageIndex, accounID);
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
	

	protected void onItemLongClick(AdapterView<?> adView, View view, int index, long id) {
		final FriendsListsDTO bean = (FriendsListsDTO) adapter.getItem(index);
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.diag_frd_slec);
		// dialog.setTitle("Title...");
		TextView dis = (TextView) dialog.findViewById(R.id.txt_Friends_Diaglog_Discript);
		dis.setText("Phone: " + bean.Phone);
		TextView fullname = (TextView) dialog.findViewById(R.id.txt_Friends_Diaglog_FullName);
		fullname.setText(bean.Name);
		ImageView image = (ImageView) dialog.findViewById(R.id.img_Friends_Diaglog_Avatar);
		adapter.displayImage(bean.Avatar, image);
		Button btnVisit = (Button) dialog.findViewById(R.id.btn_Friends_Diaglog_Visit);
		// if button is clicked, close the custom dialog
		btnVisit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent it = new Intent(context, UserPageActivity.class);
				it.putExtra("accountID", bean.AccID);
				startActivity(it);
			}
		});

		Button btnUnfriend = (Button) dialog.findViewById(R.id.btn_Friends_Diaglog_Unfriend);
		// if button is clicked, close the custom dialog
		btnUnfriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Button btnClose = (Button) dialog.findViewById(R.id.btn_Friends_Diaglog_Close);
		// if button is clicked, close the custom dialog
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("FriendEvent")
				.setIcon(R.drawable.ic_friend_event)
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
		} else if (itemTitle.equals("FriendEvent")) {
			finish();
			Intent intenFdsEvent = new Intent(this, FriendEventsActivity.class);
			startActivity(intenFdsEvent);
			return true;
		} else {
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Toast.makeText(this, getString(R.string.str_Friends_Lists_Title), Toast.LENGTH_SHORT).show();
		return true;
	}
}
