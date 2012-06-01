package esn.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class RegisterActivity extends Activity {

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register);

		intent = this.getIntent();
		boolean isFbSignup = intent.getBooleanExtra("facebookSignup", false);
		if (isFbSignup) {
			String first_name = intent.getStringExtra("first_name");
			String last_name = intent.getStringExtra("last_name");
			String birthday = intent.getStringExtra("birthday");
			String gender = intent.getStringExtra("gender");
			String email = intent.getStringExtra("email");
			((TextView) findViewById(R.id.esn_register_txtFirstName))
					.setText(first_name);
			((TextView) findViewById(R.id.esn_register_txtLastname))
					.setText(last_name);
			((TextView) findViewById(R.id.esn_register_txtEmail))
					.setText(email);
			((TextView) findViewById(R.id.esn_register_txtBirthday))
					.setText(birthday);
			if (gender.equals("male"))
				((RadioButton) findViewById(R.id.esn_register_rbMale))
						.setChecked(true);
			else
				((RadioButton) findViewById(R.id.esn_register_rbFemale))
						.setChecked(true);

		}
		TextView sv = (TextView) findViewById(R.id.lkService);

		sv.setText(Html
				.fromHtml("<a href=\"http://www.esn.com/policy\">Term of Service</a> "));

		sv.setMovementMethod(LinkMovementMethod.getInstance());

		TextView po = (TextView) findViewById(R.id.lkPolicy);

		po.setText(Html
				.fromHtml("<a href=\"http://www.esn.com/policy\">Privacy and Policy</a> "));

		po.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void CancelClicked(View view) {

		setResult(RESULT_CANCELED, intent);

		finish();
	}
}