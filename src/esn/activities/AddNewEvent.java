package esn.activities;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import com.facebook.android.Util;

import esn.classes.EsnCameras;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.AppEnums;
import esn.models.Events;
import esn.models.EventsManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
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
	private Events event;
	private ProgressDialog dialog;
	private boolean isUploaded = true;
	private boolean isUploadFailed = false;
	private final String LOG_TAG = "esn.addNewEvent";
	private UploadImageTask task;
	private Uri fileUri;

	private static final int SELECT_PICTURE = 1;

	@SuppressLint("NewApi")
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

		
		String eventTypeName = homeData.getStringExtra("labelName");
		
		TextView txteventTypeName = (TextView) findViewById(R.id.esn_addNewEvent_txtEventTypeName);
		
		if(eventTypeName == null)
		{
			txteventTypeName.setText(res.getString(R.string.esn_eventDetail_iconEventType));
		}		
		else
		{
			txteventTypeName.setText(eventTypeName);
		}
		
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
		if (sessions.get("EventAddress", null) != null) {
			TextView txtDescription = (TextView) findViewById(R.id.esn_addNewEvent_tvAddress);
			txtDescription.setText(sessions.get("EventAddress", null)
					.toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater menuInflater = new MenuInflater(this);

		menuInflater.inflate(R.menu.new_event_menu, menu);

		return true;
	}

	@TargetApi(9)
	public void btnAddClicked() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// show dialog
				dialog = new ProgressDialog(context);
				dialog.setTitle(res.getString(R.string.esn_global_loading));
				dialog.setTitle(res.getString(R.string.esn_global_pleaseWait));
				dialog.show();
			}
		});

		EditText txtTitle = (EditText) findViewById(R.id.esn_addNewEvent_txtTitle);
		String title = txtTitle.getText().toString();

		if (title.isEmpty()) {
			Toast.makeText(context,
					res.getString(R.string.esn_eventDetail_entereventtitle),
					Toast.LENGTH_SHORT).show();
			return;
		}

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
				// waiting for upload
				// if uploaded or upload failed
				while (!isUploaded && !isUploadFailed && i < 25) {
					try {
						Log.d(LOG_TAG, "waiting for upload image");

						Thread.sleep(200);
						i++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Log.d(LOG_TAG, "isUploadFailed:" + isUploadFailed);
				Log.d(LOG_TAG, "i: " + i);
				if (isUploadFailed || i >= 25) {
					if (task != null) {
						task.cancel(true);
					}
					dialog.dismiss();
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
							this);
					alertBuilder.setTitle(res
							.getString(R.string.esn_addNewEvent_canNotUpload));
					alertBuilder
							.setMessage(res
									.getString(R.string.esn_addNewEvent_wantToContinue));
					alertBuilder.setPositiveButton("OK", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							new CreateEventsThread(event).start();
							sessions.put("EventTitle", null);
							sessions.put("EventDescription", null);
							sessions.put("EventAddress", null);
						}
					});

				} else {
					new CreateEventsThread(event).start();
					sessions.put("EventTitle", null);
					sessions.put("EventDescription", null);
					sessions.put("EventAddress", null);
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
		sessions.put("EventTitle", null);
		sessions.put("EventDescription", null);
		sessions.put("EventAddress", null);
		
		Intent it = new Intent(this, HomeActivity.class);
		startActivity(it);
		
	}

	public void ChangeEventType(View view) {
		
		EditText txtTitle = (EditText) findViewById(R.id.esn_addNewEvent_txtTitle);
		String title = txtTitle.getText().toString();
		EditText txtDescription = (EditText) findViewById(R.id.esn_addNewEvent_txtDescription);
		String description = txtDescription.getText().toString();
		
		TextView txtAddress = (TextView) findViewById(R.id.esn_addNewEvent_tvAddress);
		String address = txtAddress.getText().toString();

		sessions.put("EventTitle", title);
		sessions.put("EventDescription", description);
		sessions.put("EventAddress", address);
		
		Intent intent = new Intent(this, SelectEventLabel.class);
		startActivity(intent);

		
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

	public void CameraClicked() {

	//	final CharSequence[] items = {res.getString(R.string.app_global_gallery), res.getString(R.string.app_global_camera), res.getString(R.string.app_global_cancel) };
		OpenCamera();

		/*
		 * final CharSequence[] items = { "Photo Gallery", "Camera", "Cancel" };
		 * 
		 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 * builder.setTitle("Choose");
		 * 
		 * builder.setItems(items, new DialogInterface.OnClickListener() {
		 * public void onClick(DialogInterface dialog, int item) { if (item ==
		 * 0) { OpenPhotoGallery(); } else if (item == 1) { OpenCamera(); } else
		 * { return; } } });
		 * 
		 * imageSelectDialog = builder.create(); imageSelectDialog.show();
		 */
	}

	public void OpenCamera() {
		EsnCameras mCamera = new EsnCameras(this);
		mCamera.takePicture();
	}

	public void OpenPhotoGallery() {
		imageSelectDialog.dismiss();
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(intent, SELECT_PICTURE);
	}

	@TargetApi(13)
	@SuppressLint("ParserError")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == EsnCameras.TAKE_PICTURE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				if (fileUri != null) {
					
					new UploadImageTask().execute(fileUri);
				} else {
					Toast.makeText(
							this,
							res.getString(R.string.esn_addNewEvent_canNotUpload),
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this,
						res.getString(R.string.esn_addNewEvent_noPicture),
						Toast.LENGTH_LONG).show();
			}

		} else if (requestCode == SELECT_PICTURE) {
			if (resultCode == RESULT_OK) {
				isUploaded = false;
				if (data != null) {
					final Uri selectedImageUri = data.getData();
					new UploadImageTask().execute(selectedImageUri);
				} else {
					Toast.makeText(
							this,
							res.getString(R.string.esn_addNewEvent_canNotUpload),
							Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(this,
						res.getString(R.string.esn_addNewEvent_noPicture),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public class UploadImageTask extends AsyncTask<Uri, Integer, String> {

		@Override
		protected String doInBackground(Uri... params) {

			try {
				isUploaded = false;
				isUploadFailed = false;
				Uri photoUri = params[0];
				byte[] imgBytes = Utils.scaleImage(AddNewEvent.this, photoUri);
				Bundle p = new Bundle();
				p.putByteArray("photo", imgBytes);
				p.putString("ext", "jpg");
				String url = "http://bangnl.info/ws/Upload.aspx";
				String result = Util.openUrl(url, "POST", p);
				Log.d(LOG_TAG, result);

				// Log.d(LOG_TAG, "up anh xong");
				if (result != null) {

					event.Picture = result;
					// set image into UI
					runOnUiThread(new SetImage(imgBytes));

					isUploaded = true;
					return url;
				} else {
					isUploadFailed = true;
					return "";
				}
			} catch (Exception e) {

				isUploadFailed = true;
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

				final Events event = manager.setEntity(this.event);
				if (event.EventID > 0) {

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							dialog.dismiss();
							homeData.putExtra("eventId", event.EventID);
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

	private class SetImage implements Runnable {
		private byte[] imgBytes;

		public SetImage(byte[] bytes) {
			imgBytes = bytes;
		}

		@Override
		public void run() {
			Bitmap img = BitmapFactory.decodeByteArray(imgBytes, 0,
					imgBytes.length);

			ImageView image = (ImageView) AddNewEvent.this
					.findViewById(R.id.esn_addnewEvent_imgEvent);
			TextView tvImageEventStatus = (TextView) AddNewEvent.this
					.findViewById(R.id.esn_addNewEvent_txtImageStatus);
			image.setImageBitmap(img);
			// img.recycle();
			tvImageEventStatus.setText("");
			
			imgBytes = null;
		}
	}
}
