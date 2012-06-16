package esn.activities;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import esn.adapters.ListViewFriendsAdapter;
import esn.classes.ImageLoader;
import esn.models.FriendsListsDTO;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;

public class FriendListActivity extends SherlockActivity implements
		OnNavigationListener, OnItemClickListener {
	private ActionMode mMode;
	private Resources res;
	private ListView lstFriend;
	private ListViewFriendsAdapter adapter;
	private FriendListActivity context;
	private Handler handler;
	private ArrayList<FriendsListsDTO> itemList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		
		handler = new Handler();
		context = this;
		setRes(getResources());
		setContentView(R.layout.friends_list);
		setupActionBar();
		setupListNavigate();
		setupFriendList();
	}
	
	@Override
	public void onDestroy() {
		adapter.imageLoader.stopThread();
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
//		Thread thr = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				
//				FriendsManager frdMng = new FriendsManager();
//				itemList = frdMng.getFriendsList(1, 2);
//				
//				handler.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						lstFriend = (ListView)findViewById(R.id.lisvFriends);
//						adapter = new ListViewFriendsAdapter(context, itemList);
//						lstFriend.setAdapter(adapter);
//						lstFriend.setOnItemClickListener(context);
//					}
//				});
//			}
//		});
//		
//		thr.start();
		
		
		////////////////////
		itemList = new ArrayList<FriendsListsDTO>();		
		itemList.add(new FriendsListsDTO(1, "Huyền Vũ", "http://a3.twimg.com/profile_images/740897825/AndroidCast-350_normal.png"));
		itemList.add(new FriendsListsDTO(1, "Thiện Trương Quang", "http://a3.twimg.com/profile_images/670625317/aam-logo-v3-twitter.png"));
		lstFriend = (ListView)findViewById(R.id.lisvFriends);
		adapter = new ListViewFriendsAdapter(context, itemList);
		lstFriend.setAdapter(adapter);
		lstFriend.setOnItemClickListener(context);
	}
	
	@Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		FriendsListsDTO bean = (FriendsListsDTO)adapter.getItem(position);
        final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.diag_frd_slec);
//		dialog.setTitle("Title...");
		TextView dis = (TextView) dialog.findViewById(R.id.txt_Friends_Diaglog_Discript);
		dis.setText("Discription");
		TextView fullname = (TextView) dialog.findViewById(R.id.txt_Friends_Diaglog_FullName);
		fullname.setText(bean.name);
		ImageView image = (ImageView) dialog.findViewById(R.id.img_Friends_Diaglog_Avatar);
		adapter.imageLoader.displayImage(bean.avatarURL, this, image);
		Button btnVisit = (Button) dialog.findViewById(R.id.btn_Friends_Diaglog_Visit);
		// if button is clicked, close the custom dialog
		btnVisit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
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
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		String itemTitle = item.getTitle().toString();
		if (itemTitle.equals("Search")) {
			item.collapseActionView();
			return true;
		}else if(itemTitle.equals("FriendEvent")){
			finish();
			Intent intenFdsEvent = new Intent(this, FriendEventsActivity.class);
			startActivity(intenFdsEvent);
			return true;
		}else {
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Toast.makeText(this, getString(R.string.str_Friends_Lists_Title),
				Toast.LENGTH_SHORT).show();
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
