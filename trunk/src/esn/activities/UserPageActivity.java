package esn.activities;



import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import esn.classes.ImageLoader;
import esn.classes.Utils;
import esn.models.FriendsManager;
import esn.models.Users;
import esn.models.UsersManager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserPageActivity extends SherlockActivity implements OnNavigationListener{
	private Handler handler;
	private ProgressDialog dialog;
	public ImageLoader imageLoader;
	private Activity activity;
	private DateFormat formatter;
	Resources res;
	UserPageActivity context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userpage);
		
		context = this;
		
		res = getResources();
		
		this.imageLoader = new ImageLoader(this.getApplicationContext());
		this.imageLoader.setDefaultEmptyImage(R.drawable.ic_no_avata);
		handler = new Handler();
		
		formatter = new SimpleDateFormat(getString(R.string.esn_global_dateFormat));
		
		setupActionBar();
		setupListNavigate();
		loadData();
	}
	
	@Override
	public void onDestroy() {
		imageLoader.stopThread();
		imageLoader.clearCache();
		super.onDestroy();
	}

	private void loadData() {
		final int accID = this.getIntent().getIntExtra("accountID", 0);
		dialog = new ProgressDialog(this);
		dialog.setTitle(getString(R.string.esn_global_loading));
		dialog.setMessage(getString(R.string.esn_global_pleaseWait));
		dialog.show();
		
		Thread thr = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					final Users user = new UsersManager().RetrieveById(accID);
					
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							TextView txtfullName = (TextView) findViewById(R.id.txt_esn_userpage_fullname);
							TextView txtstatus = (TextView) findViewById(R.id.txt_esn_userpage_status);
							TextView txtadress = (TextView) findViewById(R.id.txt_esn_userpage_adress);
							TextView txtcountry = (TextView) findViewById(R.id.txt_esn_userpage_country);
							TextView txtbirthday = (TextView) findViewById(R.id.txt_esn_userpage_birthday);
							TextView txtfavorite = (TextView) findViewById(R.id.txt_esn_userpage_favorite);
							TextView txtgender = (TextView) findViewById(R.id.txt_esn_userpage_gender);
							TextView txtphone = (TextView) findViewById(R.id.txt_esn_userpage_phone);
							ImageView avatar = (ImageView) findViewById(R.id.img_esn_userpage_avatar);
							
							String name = user.Name;
							txtfullName.setText(user.Name);						
							
							Boolean isonl = user.IsOnline;
							
							if(isonl==true)
							{
								txtstatus.setText(res.getString(R.string.esn_userpage_online));
							}
							else
							{
								txtstatus.setText(res.getString(R.string.esn_userpage_ofline));
							}
							
							String city = user.Address +" "+ user.Street +", "+ user.District +", "+ user.City ;						
							txtadress.setText(city);
								
							String country = user.Country;
							txtcountry.setText(country);
									
							txtbirthday.setText(Utils.DateToStringByLocale(user.Birthday,1));
							
							Boolean gender = user.Gender;
							
							if(gender==true)
							{
								txtgender.setText(res.getString(R.string.esn_userpage_Male));
							}
							else
							{
								txtgender.setText(res.getString(R.string.esn_userpage_Female));
							}
							
							txtfavorite.setText(user.Favorite);
							
							txtphone.setText(user.Phone);							
							
							imageLoader.displayImage(user.Avatar, activity, avatar);
							
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

	@SuppressLint("NewApi")
	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		String itemTitle = item.getTitle().toString();
		if (itemTitle.equals(res.getString(R.string.app_global_search))) {
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
