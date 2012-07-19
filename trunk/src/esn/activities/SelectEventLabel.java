package esn.activities;

import esn.adapters.EsnListAdapter;
import esn.classes.EsnListItem;
import esn.classes.Sessions;
import esn.models.EventType;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SelectEventLabel extends Activity {
	private Intent addEventData;

	private EsnListAdapter adapter;

	private ListView listLabels;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_labels);
		Sessions session = Sessions.getInstance(this);
		listLabels = (ListView) findViewById(R.id.event_labels);

		if (session.eventTypes != null) {
			adapter = new EsnListAdapter();
			// add item to adapter
			for (EventType type : session.eventTypes) {
				// add item vao adapter
				EsnListItem item = new EsnListItem(type.EventTypeName, "",
						EventType.getIconId(type.EventTypeID, 3));
				item.setId(type.EventTypeID);
				adapter.add(item);
			}
			listLabels.setOnItemClickListener(new EventTypeItemClickListener());
			listLabels.setAdapter(adapter);
			addEventData = getIntent();
		}

	}


	private class EventTypeItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapters, View view, int index,
				long id) {
			if (addEventData != null) {
				EsnListItem item = (EsnListItem) adapter.getItem(index);

				addEventData.putExtra("labelName", item.getTitle());
				addEventData.putExtra("labelDescription", item.getSubtitle());
				addEventData.putExtra("labelIcon", item.getIcon());
				addEventData.putExtra("labelId", item.getId());

				setResult(RESULT_OK, addEventData);
				finish();
			} else {
				Toast.makeText(SelectEventLabel.this, "Error",
						Toast.LENGTH_LONG).show();
			}

		}
	}

}
