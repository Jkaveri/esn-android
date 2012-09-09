package esn.activities;

import esn.adapters.EsnListAdapter;
import esn.classes.EsnListItem;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SettingsAccountActivity extends Activity implements OnItemClickListener {

	private EsnListAdapter adapter;
	private Resources res;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
	
		res = getResources();
		setContentView(R.layout.settings_context_accout);
		ListView settingList = (ListView) findViewById(R.id.setting_acc_list);
		
		adapter = new EsnListAdapter();
		
		// Change password
		adapter.add(new EsnListItem(res.getString(R.string.esn_settings_account_changepass),res.getString(R.string.esn_settings_account_sub_changepass), R.drawable.ic_setting_app_dark));
		// Edit profile
		adapter.add(new EsnListItem(res.getString(R.string.esn_settings_account_editprofile),
				res.getString(R.string.esn_settings_account_sub_editprofile), R.drawable.ic_setting_acc_dark));
		
		settingList.setAdapter(adapter);
		
		
		settingList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int index, long id) {
		// TODO Auto-generated method stub
		Intent intent;
		if (index == 0) {
			intent = new Intent(this, ChangePassActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		} else if (index == 1) {
			intent = new Intent(this, EditProfileActivity.class);
			startActivity(intent);		
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		}
	} // end onItemClick
}
