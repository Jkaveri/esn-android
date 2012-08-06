package esn.activities;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

import esn.adapters.ListMultiChoiceAdapter;
import esn.adapters.ListMultiChoiceAdapter.onItemCheckedListener;
import esn.classes.EsnListItem;
import esn.classes.Sessions;
import esn.models.EventType;

public class SetFilterActivity extends SherlockActivity implements
		onItemCheckedListener {
	private static final String LOG_TAG = "SetFilterActivity";
	private JSONObject filterListJson=null;
	private Sessions session;
	private ListMultiChoiceAdapter adapter;
	private ListView listView;
	private boolean allIsChecked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter_dialog);
		setupJsonList();
		setupListView();
	}

	public void setupJsonList() {

		session = Sessions.getInstance(this);
		String filterList = session.get("filterList", "");
		if (!filterList.equals("")) {
			try {
				filterListJson = new JSONObject(filterList);
				Log.d("filter_list", filterListJson.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void setupListView() {
		try {
			listView = (ListView) findViewById(R.id.filterListView);

			adapter = new ListMultiChoiceAdapter();
			adapter.setOnItemCheckedListener(this);
			if (filterListJson != null) {
				// all
				EsnListItem showAllItem = new EsnListItem();
				showAllItem.setTitle("All");
				showAllItem.setTagName("all");
				showAllItem.setIcon(R.drawable.ic_select_all);
				boolean allItemChecked = false;
				if (filterListJson != null && filterListJson.has("all")) {
					allItemChecked = filterListJson.getBoolean("all");
				}
				showAllItem.setChecked(allItemChecked);
				adapter.add(showAllItem);
				// friend
				EsnListItem friendItem = new EsnListItem();
				friendItem.setTitle("Friend");
				friendItem.setIcon(R.drawable.ic_friends_dark);
				friendItem.setTagName("friend");
				if (filterListJson != null && !allItemChecked
						&& filterListJson.has("friend")) {
					friendItem.setChecked(filterListJson.getBoolean("friend"));
				} else {
					friendItem.setChecked(false);
				}

				adapter.add(friendItem);
				//hot
				EsnListItem hotItem = new EsnListItem();
				hotItem.setTitle("Hot");
				hotItem.setIcon(R.drawable.ic_hot_new);
				hotItem.setTagName("hot");
				if (filterListJson != null && !allItemChecked
						&& filterListJson.has("hot")) {
					hotItem.setChecked(filterListJson.getBoolean("hot"));
				} else {
					hotItem.setChecked(false);
				}

				adapter.add(hotItem);
				//new
				EsnListItem newItem = new EsnListItem();
				newItem.setTitle("New");
				newItem.setIcon(R.drawable.ic_new);
				newItem.setTagName("new");
				if (filterListJson != null && !allItemChecked
						&& filterListJson.has("new")) {
					newItem.setChecked(filterListJson.getBoolean("new"));
				} else {
					newItem.setChecked(false);
				}

				adapter.add(newItem);
				// eventtype
				List<EventType> eventTypes = session.eventTypes;
				for (int i = 0; i < eventTypes.size(); i++) {
					EventType type = eventTypes.get(i);
					EsnListItem item = new EsnListItem();
					Log.d(LOG_TAG, "put-tag");
					item.setTitle(type.EventTypeName);
					item.setIcon(EventType.getIconId(type.EventTypeID, 3));
					item.setId(type.EventTypeID);
					item.setTagName(type.EventTypeID + "");
					if (filterListJson != null
							&& filterListJson.has(type.EventTypeID + "")) {
						boolean checked = filterListJson.getBoolean(String
								.valueOf(type.EventTypeID));
						item.setChecked(checked);
					} else {
						item.setChecked(false);
					}
					adapter.add(item);
				}

			}else{
				// all
				EsnListItem showAllItem = new EsnListItem();
				showAllItem.setTitle("All");
				showAllItem.setTagName("all");
				showAllItem.setIcon(R.drawable.ic_select_all);
				
				showAllItem.setChecked(true);
				adapter.add(showAllItem);
				// friend
				EsnListItem friendItem = new EsnListItem();
				friendItem.setTitle("Friend");
				friendItem.setIcon(R.drawable.ic_friends_dark);
				friendItem.setTagName("friend");
				friendItem.setChecked(false);
				adapter.add(friendItem);
				//hot
				EsnListItem hotItem = new EsnListItem();
				hotItem.setTitle("Hot");
				hotItem.setIcon(R.drawable.ic_hot_new);
				hotItem.setTagName("hot");
				hotItem.setChecked(false);
				adapter.add(hotItem);
				//new
				EsnListItem newItem = new EsnListItem();
				newItem.setTitle("New");
				newItem.setIcon(R.drawable.ic_new);
				newItem.setTagName("new");
				newItem.setChecked(false);
				adapter.add(newItem);
				
				// eventtype
				List<EventType> eventTypes = session.eventTypes;
				for (int i = 0; i < eventTypes.size(); i++) {
					EventType type = eventTypes.get(i);
					EsnListItem item = new EsnListItem();
					Log.d(LOG_TAG, "put-tag");
					item.setTitle(type.EventTypeName);
					item.setIcon(EventType.getIconId(type.EventTypeID, 3));
					item.setId(type.EventTypeID);
					item.setTagName(type.EventTypeID + "");
					item.setChecked(true);
					adapter.add(item);
				}
			}
			listView.setAdapter(adapter);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.set_filter_menus, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.set_filter_menuItem_ok:
			try {
				JSONObject json2Save = new JSONObject();
				boolean flag = false;
				for (int i = 0; i < adapter.getCount(); i++) {
					EsnListItem listItem = (EsnListItem) adapter.getItem(i);
					Log.d(LOG_TAG, "put_type");
					if (listItem.isChecked()) {
						flag = true;
					}
					json2Save.put(listItem.getTagName(), listItem.isChecked());

				}
				if (!flag) {
					json2Save.put("all", true);
				}
				// Log.d("json2save",json2Save.toString());
				String filterString = getFilterString(json2Save);
				session.put("filterList", json2Save.toString());
				session.put("filterString", filterString);
				Intent data = new Intent();
				data.putExtra("filterString", filterString);
				setResult(RESULT_OK, data);
				finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.set_filter_menuItem_cancel:
			finish();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int position = (Integer) buttonView.getTag();
		EsnListItem item = (EsnListItem) adapter.getItem(position);
		String name = item.getTagName();
		item.setChecked(buttonView.isChecked());

		if (name.equals("all")) {
			if (buttonView.isChecked()) {
				int count = adapter.getCount();
				for (int i = 0; i < count; i++) {
					EsnListItem listItem = (EsnListItem) adapter.getItem(i);
					listItem.setChecked(listItem.getTagName().equals("all"));
				}
				adapter.notifyDataSetChanged();
				allIsChecked = true;
			} else {
				allIsChecked = false;
			}
		} else {
			if (allIsChecked && buttonView.isChecked()) {
				int count = adapter.getCount();
				for (int i = 0; i < count; i++) {
					EsnListItem listItem = (EsnListItem) adapter.getItem(i);
					if (listItem.getTagName().equals("all")) {
						listItem.setChecked(false);
						allIsChecked = false;
						break;
					}
				}
				adapter.notifyDataSetChanged();
			}
		}

	}

	private String getFilterString(JSONObject filterList) {
		try {
			String friendFilter = "";
			String eventTypeFilter = "";

			boolean flag = false;
			Iterator<String> keys = filterList.keys();

			while (keys.hasNext()) {
				String key = keys.next();
				boolean checked = filterList.getBoolean(key);
				// neu da check all thi ko can xet nua
				if (key.equals("all") && checked)
					return "";
				if (key.equals("friend") && checked) {
					friendFilter = "friend:" + session.currentUser.AccID;
				}
				if(key.equals("hot") && checked){
					friendFilter += "level:2,3";
				
				}else if(key.equals("new") && checked){
					friendFilter += "level:1";
				}
				else {
					if (checked) {
						if (!flag){
							eventTypeFilter += "type:";
							flag =true;
						}
						if (!eventTypeFilter.equals("type:"))
							eventTypeFilter += ",";
						eventTypeFilter += key;
					}
				}
			}
			String result = "";
			if (!friendFilter.equals("") && eventTypeFilter.equals(""))
				result = eventTypeFilter + "|" + friendFilter;
			else
				result = eventTypeFilter + friendFilter;
			return result.replace("|", "");
			
		} catch (Exception e) {
			return "";
		}
	}
}
