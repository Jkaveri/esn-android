package esn.activities;

import com.facebook.android.Facebook;
import com.facebook.android.Util;

import esn.adapters.Md5Encript;
import esn.models.Users;
import esn.models.UsersManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	Intent intent;

	private final int REQUEST_CODE_CREATE_LOGIN_HOME = 1;

	private ProgressDialog dialog;
	
	private Handler handler;
	
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		intent = this.getIntent();
		handler = new Handler();
		context = this;
		TextView tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassgord);

		tvForgotPassword
				.setText(Html
						.fromHtml("<a href=\"http://www.esn.com/forgotpassword\">Forgot your password?</a> "));

		tvForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void BackClicked(View view) {
		setResult(RESULT_CANCELED, intent);

		finish();
	}

	public void LoginClicked(View view) {

			
		/*
		 * dialog = new ProgressDialog(this);
		 * dialog.setTitle(this.getResources().getString(R.string.app_login));
		 * dialog.setTitle(getResources().getString(R.string.app_register));
		 * dialog.show();
		 */
		new Thread() {
			public void run() {

				UsersManager usermManager = new UsersManager();
				Users user = new Users();

				EditText txtEmail = (EditText) findViewById(R.id.esn_login_Email);
				EditText txtPass = (EditText) findViewById(R.id.esn_login_pass);
				user.Email = txtEmail.getText().toString();

				String passEncript = Md5Encript.md5(txtPass.getText()
						.toString());

				user.Password = passEncript;
				user = usermManager.Login(user);
				// dialog.hide();

				if (user != null) {
					Intent intentLogin = new Intent(getApplicationContext(),
							HomeActivity.class);
					startActivityForResult(intentLogin,
							REQUEST_CODE_CREATE_LOGIN_HOME);
				} else {
					handler.post(new Runnable() {

						@Override
						public void run() {

							Util.showAlert(context, "Warning",
									"Incorrect information !");
						}
					});
				}
			};
		}.start();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
	}
}
