package esn.activities;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import com.facebook.android.Util;

import esn.classes.EsnCameras;
import esn.classes.Sessions;
import esn.classes.ShareToFacebookThread;
import esn.classes.Utils;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
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
	private EsnCameras mCamera;

	private static final int SELECT_PICTURE = 1;
	private static final int SELECT_EVENT_TYPE = 12;
	public static final String uploadURL = "http://bangnl.info/ws/498F5926A3E4493E803AADC0125E39B5.aspx";
	int fb = 1;

	String eventTypeName="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);

			setContentView(R.layout.add_new_event);

			res = getResources();

			homeData = getIntent();

			context = this;
			// instance session
			sessions = Sessions.getInstance(context);

			// instance new event
			event = new Events();
			// toa do
			/*TextView txtCoordinate = (TextView) findViewById(R.id.esn_addNewEvent_txtCoordinate); */
			double lat = homeData.getDoubleExtra("latitude", 0);
			double lon = homeData.getDoubleExtra("longtitude", 0);
			event.EventLat = lat;
			event.EventLng = lon;

			// display coordinate
			//txtCoordinate.setText(String.format("{%1$s}, {%2$s}", lat, lon));
			// convert coordinate to address
			/*TextView tvAddress = (TextView) findViewById(R.id.esn_addNewEvent_tvAddress);
			Geocoder geoCoder = new Geocoder(this);*/
			// hien dia chi
			/*List<Address> listAddress = geoCoder.getFromLocation(lat, lon, 1);
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
			} else {
				tvAddress.setText(res
						.getString(R.string.esn_addNewEvent_noAddress));
			}
*/
			TextView tvImageEventStatus = (TextView) findViewById(R.id.esn_addNewEvent_txtImageStatus);
			tvImageEventStatus.setText(String.format(
					res.getString(R.string.esn_addNewEvent_imageeventstatus),
					this));

			Spinner spinner = (Spinner) findViewById(R.id.esn_addNewEvent_sharetype);

			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> adapter, View arg1,
						int i, long l) {
					if (i == 1) {
						TableRow row = (TableRow) findViewById(R.id.ak);
						row.setVisibility(View.GONE);
					} else {
						TableRow row = (TableRow) findViewById(R.id.ak);
						row.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}

			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	
			MenuInflater menuInflater = new MenuInflater(this);

			menuInflater.inflate(R.menu.new_event_menu, menu);

			return true;	

	}

	public void btnAddClicked() {

			EditText txtDescription = (EditText) findViewById(R.id.esn_addNewEvent_txtDescription);
			String description = txtDescription.getText().toString();

			event.AccID = sessions.currentUser.AccID;
			event.Title = eventTypeName;
			event.Description = description;

			event.ShareType = 0;

			Spinner ddlShareType = (Spinner) findViewById(R.id.esn_addNewEvent_sharetype);

			String sharetype = ddlShareType.getSelectedItem().toString();

			if (sharetype.equals(res
					.getString(R.string.esn_addNewEvent_sharetypepublic))) {
				event.ShareType = 1;
			}

			if (event.ShareType == 0) {
				event.EventTypeID = 10;
			}

			if (event.EventTypeID <= 0) {
				Toast.makeText(context, "Ban phai chon loai su kien",
						Toast.LENGTH_SHORT).show();
				return;
			}

			int i = 0;
			// waiting for upload
			// if uploaded or upload failed
			while (!isUploaded && !isUploadFailed && i < 25) {

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					Toast.makeText(context, res.getString(R.string.esn_global_Error), Toast.LENGTH_SHORT).show();
					DismitDialog();
				}
				i++;

			}

			if (isUploadFailed || i >= 25) {
				if (task != null) {
					task.cancel(true);
				}
				dialog.dismiss();
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
				alertBuilder.setTitle(res
						.getString(R.string.esn_addNewEvent_canNotUpload));
				alertBuilder.setMessage(res
						.getString(R.string.esn_addNewEvent_wantToContinue));
				alertBuilder.setPositiveButton("OK", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new CreateEventsThread(event).start();
					}
				});

			} else {
				// show dialog
				dialog = new ProgressDialog(context);
				dialog.setTitle(res.getString(R.string.esn_global_loading));
				dialog.setTitle(res.getString(R.string.esn_global_pleaseWait));
				dialog.show();
				new CreateEventsThread(event).start();
			}
		
	}

	public void btnCancelClicked() {
		Intent it = new Intent(this, HomeActivity.class);
		startActivity(it);

	}

	public void ChangeEventType(View view) {
		Log.d(LOG_TAG, "Chang event Type");
		Intent intent = new Intent(this, SelectEventLabel.class);
		startActivityForResult(intent, SELECT_EVENT_TYPE);
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

		OpenCamera();
	}

	public void OpenCamera() {
		mCamera = new EsnCameras(this);
		mCamera.takePicture();
	}

	public void OpenPhotoGallery() {
		imageSelectDialog.dismiss();
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(intent, SELECT_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == EsnCameras.TAKE_PICTURE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				if (mCamera.fileUri != null) {

					new UploadImageTask().execute(mCamera.fileUri);
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
		} else if (requestCode == SELECT_EVENT_TYPE) {
			if (resultCode == RESULT_OK) {
				int id = data.getIntExtra("labelId", 0);

				eventTypeName = data.getStringExtra("labelName");

				TextView txteventTypeName = (TextView) findViewById(R.id.esn_addNewEvent_txtEventTypeName);

				if (eventTypeName == null) {
					txteventTypeName.setText(res
							.getString(R.string.esn_eventDetail_iconEventType));
				} else {
					txteventTypeName.setText(eventTypeName);
				}

				ImageView imgType = (ImageView) findViewById(R.id.esn_addNewEvent_txtEventTypeImage);

				int icon = data.getIntExtra("labelIcon", 0);
				imgType.setImageResource(icon);

				event.EventTypeID = id;
			}
		}
	}

	public class UploadImageTask extends AsyncTask<Uri, Integer, String> {
		@Override
		protected void onPreExecute() {
			// show dialog
			dialog = new ProgressDialog(context);
			dialog.setTitle(res.getString(R.string.esn_addNewEvent_uploading));
			dialog.setTitle(res
					.getString(R.string.esn_addNewEvent_uploadEventImage));
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

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
				
				String result = Util.openUrl(uploadURL, "POST", p);
				Log.d(LOG_TAG, result);

				// Log.d(LOG_TAG, "up anh xong");
				if (result != null) {

					event.Picture = result;
					// set image into UI
					runOnUiThread(new SetImage(imgBytes));

					isUploaded = true;
					return uploadURL;
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
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				dialog.dismiss();
				Log.d("esn_uploadImage", result);
			} else {
				dialog.dismiss();
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
					if (fb == 2) {

						new ShareToFacebookThread(event, AddNewEvent.this)
								.start();
					}
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							dialog.dismiss();
							homeData.putExtra("eventId", event.EventID);
							homeData.putExtra("labelId", event.EventTypeID);
							homeData.putExtra("labelIcon",
									EventType.getIconId(event.EventTypeID, 1));
							homeData.putExtra("eventTitle", event.Title);
							homeData.putExtra("eventDescription",
									event.Description);
							setResult(RESULT_OK, homeData);
							finish();
						}
					});
				} else {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							dialog.dismiss();
							AlertDialog.Builder builder = new AlertDialog.Builder(
									context);
							builder.setTitle("");
							builder.setMessage("");
							builder.create().show();
						}
					});
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Utils.showToast(AddNewEvent.this,
						res.getString(R.string.esn_global_ConnectionError),
						Toast.LENGTH_SHORT);
				Log.e(LOG_TAG, e.getMessage());
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

	public void ShareFbClick(View v) {
		ImageView im = (ImageView) findViewById(R.id.esn_addNewEvent_sharefb);

		if (fb == 1) {
			im.setImageDrawable(res.getDrawable(R.drawable.ic_newevent_fb_en));
			fb = 2;
		} else {
			im.setImageDrawable(res.getDrawable(R.drawable.ic_newevent_fb_dis));
			fb = 1;
		}

	}
	
	public void DismitDialog() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
	}
}
