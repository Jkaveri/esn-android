package esn.activities;

import java.io.IOException;

import org.json.JSONException;

import com.facebook.android.AccessFaceBookListener;
import com.facebook.android.Facebook;
import esn.classes.Sessions;
import esn.models.Users;
import esn.models.UsersManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsAppActivity extends Activity {

	private Resources res;

	private Sessions session;
	private Context context;

	private Intent intent;

	UsersManager usersManager;

	private Facebook mFacebook;

	String accessToken = null;

	String email;

	Users users;

	Handler handler;

	protected Object lockObj = new Object();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_app);

		res = getResources();

		context = this;

		session = Sessions.getInstance(context);

		handler = new Handler();

		mFacebook = new Facebook(WelcomeActivity.APP_ID);

		users = new Users();
		usersManager = new UsersManager();

		ShowInfoSettingFb();
		ShowInfoSettingLocation();
		ShowInfoSettingHeadPhone();
	}

	public void SettingFriendClicked(View v) {
		/*intent = new Intent(this, SettingAppFriendActivity.class);
		startActivity(intent);*/
		Toast.makeText(this, R.string.esn_global_function_developing, Toast.LENGTH_LONG).show();
	}

	public void SettingEventCliked(View v) {
		intent = new Intent(this, SettingAppEventActivity.class);
		startActivity(intent);
	}

	public void LogoutClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you want to logout?")
				.setCancelable(false)
				.setPositiveButton(res.getString(R.string.app_global_yes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								session.clear();

								Intent intent = new Intent(context,
										WelcomeActivity.class);
								
								startActivity(intent);
								overridePendingTransition(R.anim.push_left_in,
										R.anim.push_left_out);
								finish();
								Intent logoutIntent = new Intent();
								logoutIntent.addCategory(Intent.CATEGORY_DEFAULT);
								logoutIntent.setAction(HomeActivity.LOGOUT_ACTION);
								sendBroadcast(logoutIntent);
							}
						})
				.setNegativeButton(res.getString(R.string.app_global_no),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void SwitchFbClicked(View v) {
		Switch sw = (Switch) findViewById(R.id.esn_setting_app_facebook_enable);

		if (sw.isChecked()) {

			new Thread() {
				public void run() {

					try {
						synchronized (lockObj) {
							users = usersManager
									.RetrieveById(session.currentUser.AccID);
							lockObj.notify();
						}

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
			}.start();
			synchronized (lockObj) {
				try {
					lockObj.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (users != null) {
				accessToken = users.AccessToken;

				if (accessToken == null || accessToken.isEmpty()
						|| accessToken.equals("_")) {
					ConnecToFacebook();
				} else {
					session.setSettingFacebook(true);
					Toast.makeText(context,
							res.getString(R.string.esn_setting_app_informationsaved),
							Toast.LENGTH_SHORT).show();
				}
			}
		} else {
			session.setSettingFacebook(false);
			Toast.makeText(context,
					res.getString(R.string.esn_setting_app_informationsaved),
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

	public void CheckLocationClicked(View v) {
		CheckBox chk = (CheckBox) findViewById(R.id.esn_setting_app_location_check);

		if (chk.isChecked()) {
			session.setSettingLocation(true);
			Toast.makeText(context,
					res.getString(R.string.esn_setting_app_informationsaved),
					Toast.LENGTH_SHORT).show();
		} else {
			session.setSettingLocation(false);
			Toast.makeText(context,
					res.getString(R.string.esn_setting_app_informationsaved),
					Toast.LENGTH_SHORT).show();
		}
	}

	public void CheckHeadPhoneClicked(View v) {
		CheckBox chk = (CheckBox) findViewById(R.id.esn_setting_app_headphone_check);

		if (chk.isChecked()) {
			session.setAccessHeadPhone(true);
			Toast.makeText(context,
					res.getString(R.string.esn_setting_app_informationsaved),
					Toast.LENGTH_SHORT).show();
		} else {
			session.setAccessHeadPhone(false);
			Toast.makeText(context,
					res.getString(R.string.esn_setting_app_informationsaved),
					Toast.LENGTH_SHORT).show();
		}
	}

	public void ShowInfoSettingFb() {
		Boolean check = session.getSettingFacebook();

		Switch sw = (Switch) findViewById(R.id.esn_setting_app_facebook_enable);

		if (check == true) {
			sw.setChecked(true);
		} else {
			sw.setChecked(false);
		}
	}

	public void ShowInfoSettingLocation() {
		CheckBox chkbox = (CheckBox) findViewById(R.id.esn_setting_app_location_check);

		boolean chk = session.getSettingLocation();

		if (chk == true) {
			chkbox.setChecked(true);
		}
	}

	public void ShowInfoSettingHeadPhone() {
		CheckBox chkbox = (CheckBox) findViewById(R.id.esn_setting_app_headphone_check);

		boolean chk = session.getAccessHeadPhone();

		if (chk == true) {
			chkbox.setChecked(true);
		}
	}

	public void ConnecToFacebook() {
		synchronized (mFacebook) {
			mFacebook.authorize(this, WelcomeActivity.FB_PERMISSIONS,
					new AccessFaceBookListener(SettingsAppActivity.this,
							mFacebook));

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		mFacebook.authorizeCallback(requestCode, resultCode, data);
		/*synchronized (mFacebook) {
			try {
				mFacebook.wait();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/

	}
}
