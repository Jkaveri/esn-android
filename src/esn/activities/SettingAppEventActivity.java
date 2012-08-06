package esn.activities;

import esn.classes.Sessions;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class SettingAppEventActivity extends Activity {

	public Handler handler;

	Sessions session;

	Context context;

	Resources res;

	private EditText txtRadius;

	private Switch sw;

	protected Intent service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.esn_setting_app_event);

		context = this;

		session = Sessions.getInstance(SettingAppEventActivity.this);

		handler = new Handler();

		res = getResources();

		

		sw = (Switch) findViewById(R.id.esn_setting_app_event_enable);
		txtRadius = (EditText) findViewById(R.id.esn_setting_app_event_radius);

		service = new Intent(this, EsnLookingAheadEventsServices.class);

		sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				txtRadius.setEnabled(isChecked);
				session.setNotifyEvents(isChecked);
				if (isChecked) {
					if (!isMyServiceRunning())
						startService(service);
				} else {
					if (isMyServiceRunning())
						stopService(service);
				}
			}
		});
		//show info
		ShowInfo();
	}

	@SuppressLint({ "NewApi", "NewApi" })
	public void ShowInfo() {
		Boolean check = session.getNotifyEvents();
		sw.setChecked(check);
		txtRadius.setText(String.valueOf(session.getRadiusForEventAround()));
	}

	@SuppressLint({ "NewApi", "NewApi" })
	public void EventSettingSaved(View view) {
		if (!(txtRadius.getText().toString()).isEmpty()) {
			float radius = Float.parseFloat(txtRadius.getText().toString());
			session.setRadiusForEventAround(radius);

			Toast.makeText(context,
					res.getString(R.string.esn_setting_app_informationsaved),
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context,
					res.getString(R.string.esn_setting_app_event_enterradius),
					Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("esn.activities.EsnLookingAheadEventsServices"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
