package esn.activities;

import esn.activities.OptionArrayAdapter;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;
 
public class OptionActivity extends ListActivity {
 
	static final String[] OPTION_LIST = 
               new String[] { "Position", "Edit Profile", "Change Password", "Help", "About", "Provition", "Button Logout"};
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		setListAdapter(new OptionArrayAdapter(this, OPTION_LIST));
 
	}
 
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
 
		//get selected items
		String selectedValue = (String) getListAdapter().getItem(position);
		Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
 
	}
 
}