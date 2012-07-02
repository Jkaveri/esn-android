package esn.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.kobjects.isodate.IsoDate;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.SoapObject;

import esn.adapters.EsnListAdapter;
import esn.classes.EsnListItem;
import esn.classes.EsnMapView;
import esn.classes.EsnWebServices;
import esn.classes.Sessions;
import esn.models.EventType;
import esn.models.EventTypeManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SelectEventLabel extends Activity {
	private Intent addEventData;

	private EsnListAdapter adapter;

	private ListView listLabels;

	private Resources res;

	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_labels);
		res = getResources();
		Sessions session = Sessions.getInstance(this);
		listLabels = (ListView) findViewById(R.id.event_labels);
		if(session.eventTypes!=null){
			adapter = new EsnListAdapter();
			//add item to adapter
			for (EventType type : session.eventTypes) {
				// add item vao adapter
				EsnListItem item = new EsnListItem(type.EventTypeName,"",
						EventType.getIconId(type.EventTypeID, 3));
				item.setId(type.EventTypeID);
				adapter.add(item);
			}
			listLabels.setOnItemClickListener(new EventTypeItemClickListener());
			listLabels.setAdapter(adapter);
			addEventData = getIntent();
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EsnMapView.REQUEST_CODE_ADD_NEW_EVENT) {
			setResult(resultCode, data);
			finish();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private class EventTypeItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapters, View view, int index,
				long id) {
			EsnListItem item = (EsnListItem) adapter.getItem(index);
			Context context = view.getContext();
			Intent intent = new Intent(context, AddNewEvent.class);
			intent.putExtra("latitude",
					addEventData.getDoubleExtra("latitude", Double.MIN_VALUE));
			intent.putExtra("longtitude",
					addEventData.getDoubleExtra("longtitude", Double.MIN_VALUE));
			intent.putExtra("labelName", item.getTitle());
			intent.putExtra("labelDescription", item.getSubtitle());
			intent.putExtra("labelIcon", item.getIcon());
			intent.putExtra("labelId", item.getId());

			startActivityForResult(intent,
					EsnMapView.REQUEST_CODE_ADD_NEW_EVENT);

		}
	}

}
