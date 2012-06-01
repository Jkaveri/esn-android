package esn.activities;

import java.util.ArrayList;
import java.util.List;

import esn.adapters.InteractiveArrayAdapter;
import esn.classes.SettingListViewModel;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class MyList extends ListActivity {
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.profile);
		
		ArrayAdapter<SettingListViewModel> adapter = new InteractiveArrayAdapter(this,
				getModel());
		setListAdapter(adapter);
	}

	private List<SettingListViewModel> getModel() {
		List<SettingListViewModel> list = new ArrayList<SettingListViewModel>();
		list.add(get("Linux"));
		list.add(get("Windows7"));
		list.add(get("Suse"));
		list.add(get("Eclipse"));
		list.add(get("Ubuntu"));
		list.add(get("Solaris"));
		list.add(get("Android"));
		list.add(get("iPhone"));
		// Initially select one of the items
		list.get(1).setSelected(true);
		return list;
	}

	private SettingListViewModel get(String s) {
		return new SettingListViewModel(s);
	}
}
