package esn.activities;

import java.util.Date;

import org.kobjects.isodate.IsoDate;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.SoapObject;

import esn.adapters.EsnListAdapter;
import esn.classes.EsnListItem;
import esn.classes.EsnWebServices;
import esn.classes.EventLabels;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_labels);
		handler = new Handler();
		/*
		 * dialog = new ProgressDialog(this); dialog.setTitle("Loading...");
		 * dialog.setMessage("Waiting..."); dialog.show();
		 */
		Thread th = new Thread() {
			@Override
			public void run() {

				loadLabels();
				// dialog.hide();
			}
		};
		th.start();
		addEventData = getIntent();

	}

	private void loadLabels() {
		final ListView listLabels = (ListView) findViewById(R.id.event_labels);
		adapter = new EsnListAdapter();

		/*EsnWebServices service = new EsnWebServices("http://esn.somee.com",
				"http://esnservice.somee.com/EventService.asmx");
		SoapObject response = service.InvokeMethod("LoadEventType");
		EventLabels[] labels = new EventLabels[response.getPropertyCount()];
		for (int i = 0; i < labels.length; i++) {
			SoapObject pii = (SoapObject) response.getProperty(i);
			EventLabels label = new EventLabels();
			label.setEventTypeID(Integer
					.parseInt(pii.getProperty(0).toString()));
			label.setEventTypeName(pii.getProperty(1).toString());
			label.setLabelImage(pii.getProperty(2).toString());
			label.setTime(IsoDate.stringToDate(pii.getProperty(3).toString(),
					IsoDate.DATE_TIME));
			label.setStatus(Boolean.parseBoolean(pii.getProperty(1).toString()));
			labels[i] = label;
		}*/

		adapter.add(new EsnListItem("titlte", "sub title",
				R.drawable.ic_launcher));
		this.handler.post(new Runnable() {

			@Override
			public void run() {
				listLabels.setAdapter(adapter);
				listLabels.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapters, View view,
							int index, long id) {
						EsnListItem item = (EsnListItem) adapter.getItem(index);
						Context context = view.getContext();
						Intent intent = new Intent(context, AddNewEvent.class);
						intent.putExtra("latitudeE6",
								addEventData.getIntExtra("latitudeE6", 361));
						intent.putExtra("longtitudeE6",
								addEventData.getIntExtra("longtitudeE6", 361));
						intent.putExtra("labelName", item.getTitle());
						intent.putExtra("labelDescription", item.getSubtitle());
						intent.putExtra("labelIcon", item.getIcon());
						intent.putExtra("labelId", item.getId());

						startActivityForResult(intent,
								HomeActivity.REQUEST_CODE_ADD_NEW_EVENT);

					}
				});
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == HomeActivity.REQUEST_CODE_ADD_NEW_EVENT) {
			setResult(resultCode, data);
			finish();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

}
