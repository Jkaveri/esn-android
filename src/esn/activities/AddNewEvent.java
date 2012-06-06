package esn.activities;

import esn.models.Events;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AddNewEvent extends Activity {
	private Intent homeData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_event);
		homeData = getIntent();

		TextView txtCoordinate = (TextView) findViewById(R.id.esn_addNewEvent_txtCoordinate);
		txtCoordinate.setText(homeData.getIntExtra("latitudeE6", 361) + ", "
				+ homeData.getIntExtra("longtitudeE6", 361));

	}

	public void btnAddClicked(View view) {
		String title = ((TextView) findViewById(R.id.esn_addNewEvent_txtTitle))
				.getText().toString();
		String description = ((TextView) findViewById(R.id.esn_addNewEvent_txtDescription))
				.getText().toString();
		Events event = new Events();
		homeData.putExtra("eventTitle", title);
		homeData.putExtra("eventDescription", description);
		setResult(RESULT_OK, homeData);
		finish();
	}

	public void btnCancelClicked(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
