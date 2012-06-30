package esn.activities;



import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import esn.models.FriendsManager;
import esn.models.User;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class UserPageActivity extends SherlockActivity implements OnNavigationListener{
	private Handler handler;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userpage);
		
		handler = new Handler();
		
		setupActionBar();
		setupListNavigate();
		loadData();
	}

	private void loadData() {
		final int accID = this.getIntent().getIntExtra("accountID", 0);
//		Toast.makeText(this, String.valueOf(accID), Toast.LENGTH_SHORT).show();
		dialog = new ProgressDialog(this);
		dialog.setTitle(getString(R.string.app_Processing));
		dialog.setMessage(getString(R.string.message_diaglog_process_wait));
		dialog.show();
		
		Thread thr = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					final User user = new FriendsManager().RetrieveByAccID(accID);
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							TextView fullName = (TextView) findViewById(R.id.txt_esn_userpage_fullname);
							fullName.setText(user.Name);
							
							TextView status = (TextView) findViewById(R.id.txt_esn_userpage_status);
							status.setText((user.IsOnline)? getString(R.string.txt_esn_userpage_online): getString(R.string.txt_esn_userpage_offline));
							
							TextView adress = (TextView) findViewById(R.id.txt_esn_userpage_adress);
							adress.setText(user.City);
							
							TextView country = (TextView) findViewById(R.id.txt_esn_userpage_country);
							country.setText(user.Country);
							
							TextView birthday = (TextView) findViewById(R.id.txt_esn_userpage_birthday);
							birthday.setText(user.Birthday.toString());
							
							TextView gender = (TextView) findViewById(R.id.txt_esn_userpage_gender);
							gender.setText((user.Gender)? getString(R.string.txt_esn_userpage_male): getString(R.string.txt_esn_userpage_female));
							
							TextView favorite = (TextView) findViewById(R.id.txt_esn_userpage_favorite);
							favorite.setText(user.Favorite);
							
							dialog.dismiss();
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		thr.start();
	}

	private void setupListNavigate() {
		getSupportActionBar().setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
		getSupportActionBar().setTitle(getString(R.string.str_Friends_UserPage_Title));
	}

	private void setupActionBar() {
		/** setup action bar **/
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
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
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		String itemTitle = item.getTitle().toString();
		if (itemTitle.equals("Search")) {
			item.collapseActionView();
			return true;
		}else if(itemTitle.equals("FriendList")){
			finish();
			return true;
		}else {
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Toast.makeText(this, getString(R.string.str_Friends_UserPage_Title),
				Toast.LENGTH_SHORT).show();
		return true;
	}
}
