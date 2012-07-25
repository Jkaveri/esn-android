package esn.activities;

import esn.classes.Sessions;
import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class SettingAppLocationActivity extends Activity {

	private Context context;
	private Sessions session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.esn_setting_app_location);
		
		context = this;
		
		session = Sessions.getInstance(context);
		
		CheckBox chkbox = (CheckBox)findViewById(R.id.esn_setting_app_location_check);
		
		boolean chk = session.get("app.setting.location.location", false);
		
		if(chk==true)
		{
			chkbox.setChecked(true);
		}
	}
		
	public void Checked(View view)
	{
		CheckBox chk = (CheckBox)findViewById(R.id.esn_setting_app_location_check);
		
		if(chk.isChecked())
		{
			session.put("app.setting.location.location", true);
		}
		else
		{
			session.put("app.setting.location.location", false);
		}
	}
}
