package esn.activities;

import esn.adapters.EsnListAdapter;
import esn.classes.EsnListItem;
import esn.classes.Sessions;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SettingsAppActivity extends Activity implements OnItemClickListener{

	private EsnListAdapter adapter;
	private Resources res;

	private Sessions session;
	private Context context;
	
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_app);
		
		res = getResources();
				
		context = this;
		
		session = Sessions.getInstance(context);
		
		ListView settingList = (ListView) findViewById(R.id.setting_app_list);
		
		adapter = new EsnListAdapter();
		adapter = new EsnListAdapter();
		
		
		adapter.add(new EsnListItem(res.getString(R.string.esn_setting_app_enableEventNotification),
						res.getString(R.string.esn_setting_app_enableEventNotification_small), R.drawable.ic_setting_enablenotification));
		
		adapter.add(new EsnListItem(res.getString(R.string.esn_setting_app_enableFriendNotification), 
				res.getString(R.string.esn_setting_app_enableFriendNotification_small), R.drawable.ic_setting_eventnotice));
		
		adapter.add(new EsnListItem(res.getString(R.string.esn_setting_app_enableConnectFacebook), 
				res.getString(R.string.esn_setting_app_enableConnectFacebook_small), R.drawable.ic_setting_app_connection));
		
		settingList.setAdapter(adapter);
		
		settingList.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int index, long id) {
		
		Intent intent;
		if (index == 0) {
			intent = new Intent(this, SettingAppEventActivity.class);
			startActivity(intent);
		} else if (index == 1) {
			intent = new Intent(this, SettingAppFriendActivity.class);
			startActivity(intent);
		}
		else if (index == 2) {
			intent = new Intent(this, SettingAppFacebookActivity.class);
			startActivity(intent);
		}
		else
		{
			return;
		}
	}
}
