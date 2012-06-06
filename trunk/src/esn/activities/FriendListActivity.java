package esn.activities;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import esn.adapters.ListViewFriendsAdapter;
import esn.classes.Utils;
import esn.models.FriendsListsDTO;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
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
    private ArrayList<Object> itemList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setRes(getResources());
		setContentView(R.layout.friends_list);
		setupActionBar();
		setupListNavigate();
		setupFriendList();
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
	
	 // Add one item into the Array List
	private void addObjectToList(int accID, String avtURL, String title, String desc)
    {
		FriendsListsDTO bean = new FriendsListsDTO();
        bean.name = title;
        bean.avatarURL = avtURL;
        bean.accID = accID;
        itemList.add(bean);
    }

	private void setupFriendList() {
		itemList = new ArrayList<Object>();
        addObjectToList(1, "", "LÃ½ Ngá»�c BiÃªn", "Search desc");
        addObjectToList(2, "", "Ä�Ã o Minh HoÃ ng", "Settings desc");
        addObjectToList(3, "", "Shan Lee Yu", "Status");
        
        /////////////////////////////////////////////////////
        lstFriend = (ListView)findViewById(R.id.lisvFriends);
		adapter = new ListViewFriendsAdapter(this, itemList);
		lstFriend.setAdapter(adapter);
		lstFriend.setOnItemClickListener(this);
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
		
		if(bean.avatarURL != null && !bean.avatarURL.equals("")){
			Bitmap bm =  Utils.getBitmapFromURL(bean.avatarURL);
			image.setImageBitmap(bm);
		}else{
			image.setImageResource(R.drawable.ic_no_avata);
		}
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
