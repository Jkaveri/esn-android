package esn.activities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.Util;

import esn.activities.AddNewEvent.UploadImageTask;
import esn.activities.ChangePassActivity.ChangePasswordThread;
import esn.classes.Base64;
import esn.classes.Base64.InputStream;
import esn.classes.HttpHelper;
import esn.classes.ImageLoader;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.Users;
import esn.models.UsersManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class EditProfileActivity extends Activity {
	
	private EditText mDateDisplay;
	private int mYear;
	private int mMonth;
	private int mDay;
	private static final int DATE_DIALOG_ID = 3;
	
	Sessions sessions;
	
	private Context context;
	private ProgressDialog dialog;
	Intent intent;
	private Handler handler;
	private Resources res;
	
	UsersManager usersManager = new UsersManager();
	Users user  = new Users();
	String urlAvatar = null;
	
	ImageLoader imgl ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);
		
		intent = this.getIntent();
		context = this;
		handler = new Handler();		
		res = getResources();
				
		Users user = new Users();
		
		imgl = new ImageLoader(this.getApplicationContext());
		
		sessions = Sessions.getInstance(context);
		
		String email = sessions.currentUser.Email;
				
		if(sessions==null)
		{
			intent = new Intent(this,WelcomeActivity.class);
			startActivity(intent);
		}
		mDateDisplay = (EditText) findViewById(R.id.esn_changeprofile_birthday);

		mDateDisplay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		
		ShowProfileInfo();
	}
	
	private void ShowProfileInfo() {
		
		dialog = new ProgressDialog(this);
		dialog.setTitle(this.getResources().getString(R.string.esn_global_loading));
		dialog.setMessage("Waiting ....");
		dialog.show();
		
		ShowProfileThread  showProfileThread = new ShowProfileThread();
		
		showProfileThread.start();		
	}
	
	private void updateDisplay() {
		mDateDisplay.setText(mMonth + "/" + mDay + "/" + mYear);
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}
	
	public void CancelClicked(View button) {
		this.finish();
	}
	
	
	public void AvatarClicked(View view) {

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

	private static final int CAMERA_PIC_REQUEST = 1337;

	public void OpenCamera() {
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ImageView image = (ImageView) findViewById(R.id.esn_changeprogile_avatar);

		if (requestCode == CAMERA_PIC_REQUEST) {
			if (resultCode == RESULT_OK) {
				Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				new UploadImageTask().execute(thumbnail);
				image.setImageBitmap(thumbnail);

				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);

				image.setMaxWidth(dm.widthPixels);
				image.setMaxHeight(dm.widthPixels);
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
		dialog.dismiss();
		
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
	
	public class ShowProfileThread extends Thread{
			
			public ShowProfileThread() {
				
			}						
			public void run() {
				
				String email = sessions.get("email", null);
				
				try {
					user = usersManager.RetrieveByEmail(email);
					
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				
				if(user!=null)
				{
					handler.post(new Runnable() {						
						
						@Override
						public void run() {
							
							dialog.dismiss();
							
							int id = user.AccID;
							
							sessions.put("AccId", id);
							
							EditText txtName = (EditText)findViewById(R.id.esn_changeprofile_fullname);
							EditText txtPhone = (EditText)findViewById(R.id.esn_changeprofile_phone);
							EditText txtaddress = (EditText)findViewById(R.id.esn_changeprofile_address);
							EditText txtStreet = (EditText)findViewById(R.id.esn_changeprofile_street);
							EditText txtDistrict = (EditText)findViewById(R.id.esn_changeprofile_district);
							EditText txtCity = (EditText)findViewById(R.id.esn_changeprofile_city);
							EditText txtCountry = (EditText)findViewById(R.id.esn_changeprofile_country);
							EditText txtFavorite = (EditText)findViewById(R.id.esn_changeprofile_favorite);
							
							final String url = user.Avatar;
							
							if(url!=null)
							{	
								new Thread()
								{
									public void run() {
										
										Bitmap bitmap = null;
										try {
											bitmap = Utils.getBitmapFromURL(url);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										handler.post(new SetAvatar(bitmap));
										
									};
								}.start();								
							}	
							
							EditText txtBirthday = (EditText)findViewById(R.id.esn_changeprofile_birthday);
							
							txtBirthday.setText(Utils.DateToStringByLocale(user.Birthday,1));
							
							Boolean gender = user.Gender;					
							
							if(gender==true)
							{
								Spinner sp = (Spinner)findViewById(R.id.esn_changeprofile_gender);
								
								ArrayAdapter arr = (ArrayAdapter)sp.getAdapter();
								
								int pos = arr.getPosition("Male");
										
								sp.setSelection(pos);
							}
							else
							{
								Spinner sp = (Spinner)findViewById(R.id.esn_changeprofile_gender);
								
								ArrayAdapter arr = (ArrayAdapter)sp.getAdapter();
								
								int pos = arr.getPosition("Female");
										
								sp.setSelection(pos);
							}
							
							txtName.setText(user.Name);
							txtPhone.setText(user.Phone);
							txtaddress.setText(user.Address);
							txtStreet.setText(user.Street);
							txtDistrict.setText(user.District);
							txtCity.setText(user.City);
							txtCountry.setText(user.Country);
							txtFavorite.setText(user.Favorite);
						}
					});
				}
				else
				{
					handler.post(new Runnable() {						
						@Override
						public void run() {							
							dialog.dismiss();
							
							Util.showAlert(context, res.getResourceName(R.string.esn_global_Error), res.getResourceEntryName(R.string.esn_global_ConnectionError));							
						}
					});
				}
			}
	}
	
	public class SetAvatar implements Runnable{

		private Bitmap bitmap;

		public SetAvatar(Bitmap bm) {
			bitmap=bm;
		}
		@Override
		public void run() {
			ImageView avatar = (ImageView)findViewById(R.id.esn_changeprogile_avatar);										
			
			avatar.setImageBitmap(bitmap);
		}
		
	}
	
	public void UpdateProfileClicked(View view)
	{
		dialog = new ProgressDialog(this);
		dialog.setTitle(res.getString(R.string.esn_global_loading));
		dialog.setMessage(res.getString(R.string.esn_global_pleaseWait));
		
		dialog.show();
		
		UpdateProfileThread updateProfileThread = new UpdateProfileThread();
		
		updateProfileThread.start();
	}
	
	public class UpdateProfileThread extends Thread{
		
		public UpdateProfileThread() {
			
		}						
		public void run() {
			
				synchronized (user) {
					
					EditText txtName = (EditText)findViewById(R.id.esn_changeprofile_fullname);
					EditText txtPhone = (EditText)findViewById(R.id.esn_changeprofile_phone);
					EditText txtaddress = (EditText)findViewById(R.id.esn_changeprofile_address);
					EditText txtStreet = (EditText)findViewById(R.id.esn_changeprofile_street);
					EditText txtDistrict = (EditText)findViewById(R.id.esn_changeprofile_district);
					EditText txtCity = (EditText)findViewById(R.id.esn_changeprofile_city);
					EditText txtCountry = (EditText)findViewById(R.id.esn_changeprofile_country);
					EditText txtFavorite = (EditText)findViewById(R.id.esn_changeprofile_favorite);
					EditText txtBirthday = (EditText)findViewById(R.id.esn_changeprofile_birthday);
					
					
					user.AccID = sessions.get("AccId", 0);
					
					user.Name = txtName.getText().toString();
					user.Phone = txtPhone.getText().toString();
					user.Address = txtaddress.getText().toString();
					user.Street = txtStreet.getText().toString();
					user.District = txtDistrict.getText().toString();
					user.City = txtCity.getText().toString();
					user.Country = txtCountry.getText().toString();
					user.Favorite = txtFavorite.getText().toString();
					
					String bd = txtBirthday.getText().toString();
					
					SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd");					
					try {
						user.Birthday = format.parse(bd);
					} catch (ParseException e1) {
						
						e1.printStackTrace();
					}
					
					Spinner ddlGender = (Spinner)findViewById(R.id.esn_changeprofile_gender);			
					String gender = ddlGender.getSelectedItem().toString();			
					if(gender.equals("Male"))
					{
						user.Gender=true;
					}
					else
					{
						user.Gender = false;
					}
					
									
					try {
						Boolean rs = usersManager.UpdateProfile(user);
						
						if(rs==true)
						{
							handler.post(new UpdateProfileSuccesful());
						}
						else
						{
							handler.post(new UpdateProfileFail());
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}					
				}			
		}		
	}

	private class UpdateProfileSuccesful implements Runnable{
		@Override
		public void run() {
			dialog.dismiss();
			
			Util.showAlert(context, "Configuration", "Update successfully !");			
		}
	}
	
	private class UpdateProfileFail implements Runnable{
		@Override
		public void run() {
			dialog.dismiss();
			
			Util.showAlert(context, "Error", "Update Fail. Try Again.");			
		}
	}
	
	public class UploadImageTask extends AsyncTask<Bitmap, Integer, String> {

		@Override
		protected String doInBackground(Bitmap... params) {

			try {
				Bitmap img = params[0];
				String base64Img = Utils.bitmapToBase64(img);
				HttpHelper helper = new HttpHelper(
						"http://bangnl.info/ws/ApplicationsWS.asmx");
				JSONObject p = new JSONObject();
				p.put("base64Image", base64Img);
				p.put("fileType", "jpg");
				JSONObject response = helper.invokeWebMethod("UploadImage", p);
				if(response!=null && response.has("d")){
					String url = response.getString("d");
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
				synchronized (user) {
					user.Avatar = result;
				}
				
				Log.d("esn_uploadImage", result);
			}else{
				Log.d("esn_uploadImage","failed");
			}
		}

	}
}
