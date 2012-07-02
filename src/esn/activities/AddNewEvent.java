package esn.activities;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import esn.classes.Base64;
import esn.classes.HttpHelper;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.EventType;
import esn.models.Events;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class AddNewEvent extends Activity {
	private Intent homeData;
	private Resources res;

	Sessions sessions;
	private Context context;
	private AlertDialog imageSelectDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_event);
		res = getResources();

		homeData = getIntent();

		context = this;

		sessions = Sessions.getInstance(context);

		TextView txtCoordinate = (TextView) findViewById(R.id.esn_addNewEvent_txtCoordinate);
		double lat = homeData.getDoubleExtra("latitude", 0);
		double lon = homeData.getDoubleExtra("longtitude", 0);
		txtCoordinate.setText(String.format("{%1$s}, {%2$s}", lat, lon));

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
		EditText txtTitle = (EditText) findViewById(R.id.esn_addNewEvent_txtTitle);
		String title = txtTitle.getText().toString();
		EditText txtDescription = (EditText) findViewById(R.id.esn_addNewEvent_txtDescription);
		String description = txtDescription.getText().toString();
		TextView tvEventTypeName = (TextView) findViewById(R.id.esn_addNewEvent_txtEventTypeName);
		String eventTypeName = tvEventTypeName.getText().toString();
		if (!title.isEmpty()) {
			if (!description.isEmpty()) {
				homeData.putExtra("eventTitle", title);
				homeData.putExtra("eventDescription", description);
				homeData.putExtra("eventTypeName", eventTypeName);
				setResult(RESULT_OK, homeData);
				finish();
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

		ImageView image = (ImageView) findViewById(R.id.esn_addnewEvent_imgEvent);

		if (requestCode == CAMERA_PIC_REQUEST) {
			if (resultCode == RESULT_OK) {
				Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				new UploadImageTask().execute(thumbnail);
				image.setImageBitmap(thumbnail);

				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);

				image.setMaxWidth(dm.widthPixels);
				image.setMaxHeight(dm.widthPixels);

				TextView tvImageEventStatus = (TextView) findViewById(R.id.esn_addNewEvent_txtImageStatus);
				tvImageEventStatus.setText("");
			}
		} else if (requestCode == SELECT_PICTURE) {
			if (resultCode == RESULT_OK) {
				Uri selectedImageUri = data.getData();
				selectedImagePath = getPath(selectedImageUri);
				image.setImageURI(selectedImageUri);
			}
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private static final int SELECT_PICTURE = 1;

	private String selectedImagePath;
	private Bitmap img;

	public void OpenPhotoGallery() {
		imageSelectDialog.dismiss();
		Intent intent = new Intent();

		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				SELECT_PICTURE);
	}

	public class UploadImageTask extends AsyncTask<Bitmap, Integer, String> {

		@Override
		protected String doInBackground(Bitmap... params) {

			try {
				Bitmap img = params[0];
				String base64Img = Utils.bitmapToBase64(img);
				HttpHelper helper = new HttpHelper(
						"http://10.0.2.2/esn/ApplicationsWS.asmx");
				JSONObject p = new JSONObject();
				p.put("base64Image", base64Img);
				p.put("fileType", "jpg");
				JSONObject response = helper.invokeWebMethod("UploadImage", p);
				if(response!=null && response.has("d")){
					String url = response.getString("d");
					homeData.putExtra("picture", url);
					return url;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			if(result!=null){
				Log.d("esn_uploadImage", result);
			}else{
				Log.d("esn_uploadImage","failed");
			}
		}

	}
}
