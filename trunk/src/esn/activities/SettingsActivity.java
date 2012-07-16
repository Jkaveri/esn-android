package esn.activities;



import esn.adapters.EsnListAdapter;
import esn.classes.EsnListItem;
import esn.classes.Sessions;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
 
public class SettingsActivity extends Activity implements OnItemClickListener {

	private EsnListAdapter adapter;
	private Resources res;

	private Sessions session;
	private Context context;
	
	private ProgressDialog dialog;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		res = getResources();
		
		setContentView(R.layout.settings);
		
		context = this;
		
		session = Sessions.getInstance(context);
		
		
		
		if(session.currentUser==null)
		{
			Intent intent = new Intent(context,WelcomeActivity.class);
			
			startActivity(intent);
		}
		
		ListView settingList = (ListView) findViewById(R.id.setting_list);
		
		adapter = new EsnListAdapter();
		
		// Account setting
				adapter.add(new EsnListItem(res.getString(R.string.esn_settings_account),
						res.getString(R.string.esn_settings_subscript_account), R.drawable.ic_setting_acc_dark));
		// Application setting
		adapter.add(new EsnListItem(res.getString(R.string.esn_settings_app_settings), 
				res.getString(R.string.esn_settings_subscript_app_settings), R.drawable.ic_setting_app_dark));
		// Term
		adapter.add(new EsnListItem(res.getString(R.string.esn_settings_provition),
				res.getString(R.string.esn_settings_subscript_provition), R.drawable.ic_setting_term_dark));
		// Help
		adapter.add(new EsnListItem(res.getString(R.string.esn_settings_help),
				res.getString(R.string.esn_settings_subscript_help), R.drawable.ic_setting_help_dark));
		// Logout
		adapter.add(new EsnListItem(res.getString(R.string.esn_settings_logout), 
				res.getString(R.string.esn_settings_subscript_logout), R.drawable.ic_setting_logout_dark));
		
		settingList.setAdapter(adapter);
		
		settingList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int index, long id) {
		
		Intent intent;
		if (index == 0) {
			intent = new Intent(this, SettingsAccountActivity.class);
			startActivity(intent);
		} else if (index == 1) {
			intent = new Intent(this, SettingsAppActivity.class);
			startActivity(intent);
		} else if (index == 2) {
			intent = new Intent(this, SettingsTermActivity.class);
			startActivity(intent);
		} else if (index == 3) {
			intent = new Intent(this, SettingsHelpActivity.class);
			startActivity(intent);
		} else if (index == 4) {
//			intent = new Intent(this, SettingsLogoutActivity.class);
//			startActivity(intent);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you want to logout?")
						.setCancelable(false)
						.setPositiveButton(res.getString(R.string.app_global_yes), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ExecuteLogout();
							}
						})
						.setNegativeButton(res.getString(R.string.app_global_no), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						});
			AlertDialog alert = builder.create();
			alert.show();
		}
	} // end onItemClick
	
	public void ExecuteLogout()
	{	
		session.clear();
		
		Intent intent = new Intent(context, WelcomeActivity.class);
		startActivity(intent);		
		finish();
	}
}

