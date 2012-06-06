package esn.activities;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import esn.adapters.ListViewCustomAdapter;
import esn.models.ItemBean;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
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
	private ListViewCustomAdapter adapter;
    private ArrayList<Object> itemList;
    private ItemBean bean;

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

	 // Add one item into the Array List
	private void addObjectToList(int image, String title, String desc)
    {
        bean = new ItemBean();
        bean.setDescription(desc);
        bean.setImage(image);
        bean.setTitle(title);
        itemList.add(bean);
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
		itemList = new ArrayList<Object>();
        addObjectToList(R.drawable.ic_no_avata, "Lý Ngọc Biên", "Search desc");
        addObjectToList(R.drawable.ic_no_avata, "Đào Minh Hoàng", "Settings desc");
        addObjectToList(R.drawable.ic_no_avata, "Shan Lee Yu", "Status");
        
        /////////////////////////////////////////////////////
        lstFriend = (ListView)findViewById(R.id.lisvFriends);
		adapter = new ListViewCustomAdapter(this, itemList);
		lstFriend.setAdapter(adapter);
		lstFriend.setOnItemClickListener(this);
	}
	
	@Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
        ItemBean bean = (ItemBean)adapter.getItem(position);
        
        final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.diag_frd_slec);
//		dialog.setTitle("Title...");
		TextView dis = (TextView) dialog.findViewById(R.id.txt_Friends_Diaglog_Discript);
		dis.setText(bean.getDescription());		
		TextView fullname = (TextView) dialog.findViewById(R.id.txt_Friends_Diaglog_FullName);
		fullname.setText(bean.getTitle());
		ImageView image = (ImageView) dialog.findViewById(R.id.img_Friends_Diaglog_Avatar);
		image.setImageResource(bean.getImage());
		
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
