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

	private ProgressDialog dialog;

	private Handler handler;

	private ListView listLabels;

	private Resources res;

	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_labels);
		res = getResources();
		handler = new Handler();
		listLabels = (ListView) findViewById(R.id.event_labels);
		
		progress = new ProgressDialog(this);
		progress.setTitle(res.getString(R.string.esn_global_loading));
		progress.setMessage(res.getString(R.string.esn_global_pleaseWait));
		progress.setCanceledOnTouchOutside(false);
		progress.show();

		listLabels.setOnItemClickListener(new EventTypeItemClickListener());
		addEventData = getIntent();

		new LoadEventTypeThread().start();

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

	private class LoadEventTypeThread extends Thread {
		@Override
		public void run() {

			try {
				// tao moi adapter
				adapter = new EsnListAdapter();
				// manager
				EventTypeManager manager = new EventTypeManager();
				// get list event type
				List<EventType> list = manager.getList();
				for (EventType type : list) {
					// add item vao adapter
					EsnListItem item = new EsnListItem(type.EventTypeName, "",
							EventType.getIconId(type.EventTypeID, 3));
					item.setId(type.EventTypeID);					
					adapter.add(item);
				}
				// generate list
				handler.post(new LoadEventTypeSuccessHandler());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private class LoadEventTypeSuccessHandler implements Runnable {
		@Override
		public void run() {
			listLabels.setAdapter(adapter);
			progress.dismiss();
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
