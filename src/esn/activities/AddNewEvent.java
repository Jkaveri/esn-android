package esn.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import esn.classes.Base64;
import esn.classes.HttpHelper;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.AppEnums;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddNewEvent extends Activity {
	private Intent homeData;
	private Resources res;

	Sessions sessions;
	private Context context;
	private AlertDialog imageSelectDialog;
	private ImageView image;
	private Events event;
	private ProgressDialog dialog;
	private boolean isUploaded = true;
	private boolean isUploadFialed = false;
	private final String LOG_TAG = "esn.addNewEvent";

	private static final int SELECT_PICTURE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_event);

		res = getResources();

		homeData = getIntent();

		context = this;

		sessions = Sessions.getInstance(context);

		// instance new event
		event = new Events();

		TextView txtCoordinate = (TextView) findViewById(R.id.esn_addNewEvent_txtCoordinate);
		double lat = homeData.getDoubleExtra("latitude", 0);
		double lon = homeData.getDoubleExtra("longtitude", 0);
		event.EventLat = lat;
		event.EventLng = lon;

		// display coordinate
		txtCoordinate.setText(String.format("{%1$s}, {%2$s}", lat, lon));
		// convert coordinate to address
		TextView tvAddress = (TextView) findViewById(R.id.esn_addNewEvent_tvAddress);
		Geocoder geoCoder = new Geocoder(this);
		try {

			List<Address> listAddress = geoCoder.getFromLocation(lat, lon, 1);
			if (listAddress.size() > 0) {
				Address address = listAddress.get(0);
				int count = address.getMaxAddressLineIndex() + 1;
				String add = "";
				for (int i = 0; i < count; i++) {
					if (i > 0)
						add += ", ";
					add += address.getAddressLine(i);
				}
				tvAddress.setText(add);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TextView eventTypeName = (TextView) findViewById(R.id.esn_addNewEvent_txtEventTypeName);
		eventTypeName.setText(homeData.getStringExtra("labelName"));

		ImageView imgType = (ImageView) findViewById(R.id.esn_addNewEvent_txtEventTypeImage);

		int i = homeData.getIntExtra("labelIcon", 0);
		imgType.setImageResource(i);

		TextView tvImageEventStatus = (TextView) findViewById(R.id.esn_addNewEvent_txtImageStatus);
		tvImageEventStatus
				.setText(String.format(res
						.getString(R.string.esn_addNewEvent_imageeventstatus),
						this));

		if (sessions.get("EventTitle", null) != null) {
			EditText txtTitle = (EditText) findViewById(R.id.esn_addNewEvent_txtTitle);

			txtTitle.setText(sessions.get("EventTitle", null).toString());
		}

		if (sessions.get("EventDescription", null) != null) {
			EditText txtDescription = (EditText) findViewById(R.id.esn_addNewEvent_txtDescription);
			txtDescription.setText(sessions.get("EventDescription", null)
					.toString());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater menuInflater = new MenuInflater(this);

		menuInflater.inflate(R.menu.new_event_menu, menu);

		return true;
	}

	public void btnAddClicked() {
		// show dialog
		dialog = new ProgressDialog(context);
		dialog.setTitle(res.getString(R.string.esn_global_loading));
		dialog.setTitle(res.getString(R.string.esn_global_pleaseWait));
		dialog.show();

		EditText txtTitle = (EditText) findViewById(R.id.esn_addNewEvent_txtTitle);
		String title = txtTitle.getText().toString();
		EditText txtDescription = (EditText) findViewById(R.id.esn_addNewEvent_txtDescription);
		String description = txtDescription.getText().toString();
		if ((title != null) && title.length() > 0) {
			if (description != null && description.length() > 0) {
				// put into result
				homeData.putExtra("eventTitle", title);
				homeData.putExtra("eventDescription", description);
				// put into object

				event.AccID = sessions.currentUser.AccID;
				event.Title = title;
				event.Description = description;
				event.EventTypeID = homeData.getIntExtra("labelId", 0);
				event.EventLat = homeData.getDoubleExtra("latitude", 0);
				event.EventLng = homeData.getDoubleExtra("longtitude", 0);
				event.ShareType = AppEnums.ShareTypes.Public;
				int i = 0;
				//waiting for upload
				//if uploaded or upload failed
				while (!isUploaded || isUploadFialed) {
					try {
						Log.d(LOG_TAG, "waiting for upload image");

						Thread.sleep(200);
						i++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (isUploadFialed) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
					alertBuilder.setTitle(res.getString(R.string.esn_addNewEvent_canNotUpload));
					alertBuilder.setMessage(res.getString(R.string.esn_addNewEvent_wantToContinue));
					alertBuilder.setPositiveButton("OK", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					
				}else{
					new CreateEventsThread(event).start();
				}
				
			} else {
				txtDescription.setError("Description is required",
						res.getDrawable(R.drawable.ic_alerts_and_states_error));
				return;
			}
		} else {
			txtTitle.setError("Title is required",
					res.getDrawable(R.drawable.ic_alerts_and_states_error));
			return;
		}
	}

	public void btnCancelClicked() {
		Intent it = new Intent(this, HomeActivity.class);
		startActivity(it);
	}

	public void ChangeEventType(View view) {
		Intent intent = new Intent(this, SelectEventLabel.class);
		startActivity(intent);

		EditText txtTitle = (EditText) findViewById(R.id.esn_addNewEvent_txtTitle);
		String title = txtTitle.getText().toString();
		EditText txtDescription = (EditText) findViewById(R.id.esn_addNewEvent_txtDescription);
		String description = txtDescription.getText().toString();

		sessions.put("EventTitle", title);
		sessions.put("EventDescription", description);
	}

	public void CameraClicked() {

		final CharSequence[] items = { "Photo Gallery", "Camera", "Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose");

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					OpenPhotoGallery();
				} else if (item == 1) {
					OpenCamera();
				} else {
					return;
				}
			}
		});

		imageSelectDialog = builder.create();
		imageSelectDialog.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.new_event_add) {
			btnAddClicked();
			return true;
		} else if (item.getItemId() == R.id.new_event_cancel) {

			btnCancelClicked();
			return true;
		} else if (item.getItemId() == R.id.new_event_capture) {
			CameraClicked();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public void OpenCamera() {
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

	}

	private static final int CAMERA_PIC_REQUEST = 1337;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CAMERA_PIC_REQUEST) {
			if (resultCode == RESULT_OK) {
				isUploaded = false;
				Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				new UploadImageTask().execute(thumbnail);

			}

		} else if (requestCode == SELECT_PICTURE) {
			if (resultCode == RESULT_OK) {
				isUploaded = false;
				Uri selectedImageUri = data.getData();
				Bitmap thumbnail = null;
				try {
					thumbnail = BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(selectedImageUri));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (thumbnail != null) {
					new UploadImageTask().execute(thumbnail);
				}
			}
		}
	}


	public void OpenPhotoGallery() {
		imageSelectDialog.dismiss();
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		intent.setType("image/*");
		startActivityForResult(intent, SELECT_PICTURE);
	}

	public class UploadImageTask extends AsyncTask<Bitmap, Integer, String> {

		@Override
		protected String doInBackground(Bitmap... params) {

			try {
				final Bitmap img = params[0];
				String base64Img = Utils.bitmapToBase64(img);
				HttpHelper helper = new HttpHelper(
						"http://bangnl.info/ws/ApplicationsWS.asmx");
				JSONObject p = new JSONObject();
				p.put("base64Image", base64Img);
				p.put("fileType", "jpg");
				JSONObject response = helper.invokeWebMethod("UploadImage", p);

				if (response != null && response.has("d")) {
					String url = response.getString("d");
					event.Picture = url;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ImageView image = (ImageView) findViewById(R.id.esn_addnewEvent_imgEvent);
							TextView tvImageEventStatus = (TextView) findViewById(R.id.esn_addNewEvent_txtImageStatus);
							image.setImageBitmap(img);
							tvImageEventStatus.setText("");
						}
					});
					isUploaded = true;
					return url;
				} else {
					isUploadFialed = true;
					return "";
				}
			} catch (Exception e) {
				
				isUploadFialed = true;
				Log.e(LOG_TAG, e.getMessage());
				return "";
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Log.d("esn_uploadImage", result);
			} else {
				Log.d("esn_uploadImage", "failed");
			}
		}

	}

	private class CreateEventsThread extends Thread {
		private Events event;

		public CreateEventsThread(Events event) {
			this.event = event;
		}

		@Override
		public void run() {
			Looper.prepare();
			EventsManager manager = new EventsManager();
			try {

				Events event = manager.setEntity(this.event);
				if (event.EventID > 0) {

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							dialog.dismiss();
							setResult(RESULT_OK, homeData);
							finish();
						}
					});
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
