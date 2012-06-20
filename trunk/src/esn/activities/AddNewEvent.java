package esn.activities;

import esn.models.Events;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AddNewEvent extends Activity {
	private Intent homeData;
	private Resources res;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_event);
		res = getResources();
		homeData = getIntent();

		TextView txtCoordinate = (TextView) findViewById(R.id.esn_addNewEvent_txtCoordinate);
		
		txtCoordinate.setText(homeData.getDoubleExtra("latitude", 0) + ", "
				+ homeData.getDoubleExtra("longtitude", 0));

	}

	public void btnAddClicked(View view) {
		EditText txtTitle = (EditText) findViewById(R.id.esn_addNewEvent_txtTitle);
		String title = txtTitle.getText().toString();
		EditText txtDescription = (EditText) findViewById(R.id.esn_addNewEvent_txtDescription);
		String description = txtDescription
				.getText().toString();
		if(!title.isEmpty()){
			if(!description.isEmpty()){
				Events event = new Events();
				homeData.putExtra("eventTitle", title);
				homeData.putExtra("eventDescription", description);
				setResult(RESULT_OK, homeData);
				finish();
			}else{
				txtDescription.setError("Description is required",res.getDrawable( R.drawable.ic_alerts_and_states_error));
				return;
			}
		}else{
			txtTitle.setError("Title is required",res.getDrawable(R.drawable.ic_alerts_and_states_error));
			return;
		}
	}

	public void btnCancelClicked(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
