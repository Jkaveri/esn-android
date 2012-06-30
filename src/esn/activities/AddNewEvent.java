package esn.activities;

import org.json.JSONObject;

import esn.classes.Base64;
import esn.classes.HttpHelper;
import esn.classes.Sessions;
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
import android.net.Uri;
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
		
		txtCoordinate.setText(homeData.getDoubleExtra("latitude", 0) + ", "
				+ homeData.getDoubleExtra("longtitude", 0));
		
		TextView eventTypeName = (TextView)findViewById(R.id.esn_addNewEvent_txtEventTypeName);
		eventTypeName.setText(homeData.getStringExtra("labelName"));
		
		ImageView imgType = (ImageView)findViewById(R.id.esn_addNewEvent_txtEventTypeImage);
		
		int i = homeData.getIntExtra("labelIcon", 0);
		imgType.setImageResource(i);
		
		TextView tvImageEventStatus = (TextView)findViewById(R.id.esn_addNewEvent_txtImageStatus);
		tvImageEventStatus.setText(String.format(res.getString(R.string.esn_addNewEvent_imageeventstatus), this));
		
		if(sessions.get("EventTitle", null)!=null)
		{
			EditText txtTitle = (EditText) findViewById(R.id.esn_addNewEvent_txtTitle);
			
			txtTitle.setText(sessions.get("EventTitle", null).toString());
		}
		
		if(sessions.get("EventDescription", null)!=null)
		{
			EditText txtDescription = (EditText) findViewById(R.id.esn_addNewEvent_txtDescription);
			txtDescription.setText(sessions.get("EventDescription", null).toString());
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
		TextView tvEventTypeName = (TextView)findViewById(R.id.esn_addNewEvent_txtEventTypeName);
		String eventTypeName = tvEventTypeName.getText().toString();
		if(!title.isEmpty()){
			if(!description.isEmpty()){
				homeData.putExtra("eventTitle", title);
				homeData.putExtra("eventDescription", description);
				homeData.putExtra("eventTypeName", eventTypeName);
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

	public void btnCancelClicked() {
		Intent it = new Intent(this,HomeActivity.class);
		startActivity(it);
	}
	
	public void ChangeEventType(View view)
	{
		Intent intent = new Intent(this,SelectEventLabel.class);
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

		builder.show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.new_event_add)
		{
			btnAddClicked();
			return true;
		}
		else if(item.getItemId()==R.id.new_event_cancel)
		{
			btnCancelClicked();
			return true;
		}
		else if(item.getItemId()==R.id.new_event_capture)
		{
			CameraClicked();
			return true;
		}
		else
		{
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

				image.setImageBitmap(thumbnail);
				
				DisplayMetrics dm = new DisplayMetrics();
		        getWindowManager().getDefaultDisplay().getMetrics(dm);
		        
		        image.setMaxWidth(dm.widthPixels);
		        image.setMaxHeight(dm.widthPixels);
		        
		        TextView tvImageEventStatus = (TextView)findViewById(R.id.esn_addNewEvent_txtImageStatus);
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
		Intent intent = new Intent();

		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				SELECT_PICTURE);
	}

	public void Uploader(String imagePath) {
		new Thread() {
			public void run() {
				Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),1);

				java.io.ByteArrayOutputStream bao = new java.io.ByteArrayOutputStream();

				bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 90, bao);

				byte[] ba = bao.toByteArray();

				String ba1 = Base64.encodeBytes(ba);

				try {

					HttpHelper helper = new HttpHelper("");
					JSONObject params = new JSONObject();
					params.put("image", ba1);
					helper.invokeWebMethod("UploadImage", params);
				} catch (Exception e) {

					Log.e("log_tag", "Error in http connection " + e.toString());

				}
			}
		}.start();
	}
}
