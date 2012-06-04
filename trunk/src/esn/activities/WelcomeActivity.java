package esn.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends Activity {

	private final int REQUEST_CODE_CREATE_LOGIN = 1;
	private final int REQUEST_CODE_CREATE_LOGIN_FB = 2;
	private final int REQUEST_CODE_CREATE_REGISTER = 3;
	private final String[] FB_PERMISSIONS = { "email", "user_events",
			"user_birthday" };
	// Login by fB
	public static final String APP_ID = "175185989209026";
	private Facebook mFacebook;
	private SharedPreferences prefEdit;
	private AsyncFacebookRunner mAsyncRunner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		mFacebook = new Facebook(APP_ID);// init facebook
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);//init async runner 
		prefEdit = getPreferences(MODE_PRIVATE); // get preferences (stored)
		String fbAccessToken = prefEdit.getString("fb_access_token", null);// get
																			// fb
																			// acess
																			// token
																			// was
																			// stored
		long fbAccessExpires = prefEdit.getLong("fb_access_token_expires", 0);// get
																				// fb
																				// access
																				// expires
																				// was
																				// stored
		if (fbAccessToken != null) {
			mFacebook.setAccessToken(fbAccessToken);
		}
		if (fbAccessExpires != 0) {
			mFacebook.setAccessExpires(fbAccessExpires);
		}

	}

	public void btnLoginfbClicked(View view) {
		if (!mFacebook.isSessionValid()) {// if access token is expired
			mFacebook.authorize(this, FB_PERMISSIONS, new DialogListener() {

				@Override
				public void onComplete(Bundle values) {
					SharedPreferences.Editor pref = prefEdit.edit();
					String access_token = mFacebook.getAccessToken();
					pref.putString("fb_access_token",
							access_token);
					pref.putLong("fb_access_token_expires",
							mFacebook.getAccessExpires());
					mAsyncRunner.request("me", new RequestListener() {

						@Override
						public void onComplete(String response, Object state) {
							Intent intent = new Intent(getBaseContext(),RegisterActivity.class);
							
							try {
							JSONObject accountInfo =	Util.parseJson(response);
							intent.putExtra("facebookSignup", true);
							intent.putExtra("fb_id", accountInfo.getString("id"));
							intent.putExtra("name", accountInfo.getString("name"));
							intent.putExtra("first_name", accountInfo.getString("first_name"));
							intent.putExtra("last_name", accountInfo.getString("last_name"));
							intent.putExtra("email", accountInfo.getString("email"));
							intent.putExtra("gender", accountInfo.getString("gender"));
							intent.putExtra("birthday", accountInfo.getString("birthday"));
							startActivity(intent);
							} catch (FacebookError e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}

						@Override
						public void onIOException(IOException e, Object state) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onFileNotFoundException(
								FileNotFoundException e, Object state) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onMalformedURLException(
								MalformedURLException e, Object state) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onFacebookError(FacebookError e,
								Object state) {
							// TODO Auto-generated method stub
							
						}
						
						
					});
				}

				@Override
				public void onFacebookError(FacebookError e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onError(DialogError e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onCancel() {
					// TODO Auto-generated method stub

				}

			});
		}
	}

	public void LoginClicked(View view) {
		if (!mFacebook.isSessionValid()) {
			Intent intent = new Intent(this, LoginActivity.class);

			startActivityForResult(intent, REQUEST_CODE_CREATE_LOGIN);
		} else {
			Util.showAlert(this, "Error", "Plase Logout First !");
		}
	}

	public void RegisterClicked(View view) {
		if (!mFacebook.isSessionValid()) {
			Intent intent = new Intent(this, RegisterActivity.class);

			startActivityForResult(intent, REQUEST_CODE_CREATE_REGISTER);
		} else {
			Util.showAlert(this, "Error", "Plase Logout First !");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}
}