package esn.activities;

import esn.classes.Sessions;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class SettingAppEventActivity extends Activity {

	public Handler handler;

	Sessions session;

	Context context;

	Resources res;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.esn_setting_app_event);

		context = this;

		session = Sessions.getInstance(SettingAppEventActivity.this);

		handler = new Handler();

		res = getResources();

		ShowInfo();
	}

	@SuppressLint({ "NewApi", "NewApi" })
	public void ShowInfo() {
		Boolean check = session.get("app.setting.event.enable", false);

		Switch sw = (Switch) findViewById(R.id.esn_setting_app_event_enable);

		EditText radius = (EditText) findViewById(R.id.esn_setting_app_event_radius);

		if (check == true) {
			sw.setChecked(true);

			radius.setText(String.valueOf(session.get("app.setting.event.radius", (float) 0.0)));
		} else {
			sw.setChecked(false);

			radius.setText("");
		}
	}

	@SuppressLint({ "NewApi", "NewApi" })
	public void EventSettingSaved(View view) {
		Switch sw = (Switch) findViewById(R.id.esn_setting_app_event_enable);

		if (sw.isChecked()) {
			EditText txtRadius = (EditText) findViewById(R.id.esn_setting_app_event_radius);

			if (!(txtRadius.getText().toString()).isEmpty()) {
				float radius = Float.parseFloat(txtRadius.getText().toString());
				session.put("app.setting.event.enable", true);
				session.put("app.setting.event.radius", radius);

				Toast.makeText(
						context,
						res.getString(R.string.esn_setting_app_informationsaved),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(
						context,
						res.getString(R.string.esn_setting_app_event_enterradius),
						Toast.LENGTH_SHORT).show();
			}

		} else {
			session.put("app.setting.event.enable", false);

			Toast.makeText(
					context,
					res.getString(R.string.esn_setting_app_eventnotifyenablebefore),
					Toast.LENGTH_SHORT).show();

			return;
		}
	}
}
