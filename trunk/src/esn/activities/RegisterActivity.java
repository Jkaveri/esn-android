package esn.activities;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.android.Util;

import esn.activities.LoginActivity.LoginThread;
import esn.classes.Base64;
import esn.classes.HttpHelper;
import esn.classes.Sessions;
import esn.models.Users;
import esn.models.UsersManager;
import android.R.bool;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends SherlockActivity {

	Intent intent;

	private final int REQUEST_CODE_CREATE_LOGIN_LOGIN = 1;

	private EditText mDateDisplay;
	private int mYear;
	private int mMonth;
	private int mDay;
	private static final int DATE_DIALOG_ID = 3;

	Context context;
	
	private Handler handler;
	
	Sessions sessions;
	
	private ProgressDialog dialog;

	public SharedPreferences pref;
	
	UsersManager usersManager = new UsersManager();

	private Resources res;
	
	private static boolean checkEmail=false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register);

		getActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().hide();
		
		intent = this.getIntent();
		context = this;
		handler = new Handler();

		sessions = Sessions.getInstance(context);
		
		res = getResources();
		
		boolean isFbSignup = intent.getBooleanExtra("facebookSignup", false);
		if (isFbSignup) {
			String first_name = intent.getStringExtra("first_name");
			String last_name = intent.getStringExtra("last_name");
			String birthday = intent.getStringExtra("birthday");
			String gender = intent.getStringExtra("gender");
			String email = intent.getStringExtra("email");
			((TextView) findViewById(R.id.esn_register_txtFullName))
					.setText(first_name + " " + last_name );
			((TextView) findViewById(R.id.esn_register_txtEmail))
					.setText(email);
			((TextView) findViewById(R.id.esn_register_txtBirthday))
					.setText(birthday);
			if (gender.equals("male"))
			{
				Spinner sp = (Spinner)findViewById(R.id.esn_register_ddlGender);
				
				ArrayAdapter arr = (ArrayAdapter)sp.getAdapter();
				
				int pos = arr.getPosition("Male");
						
				sp.setSelection(pos);			
			}
			else
			{
				Spinner sp = (Spinner)findViewById(R.id.esn_register_ddlGender);
				
				ArrayAdapter arr = (ArrayAdapter)sp.getAdapter();
				
				int pos = arr.getPosition("Female");
						
				sp.setSelection(pos);	
			}

		}
		TextView sv = (TextView) findViewById(R.id.lkService);

		sv.setText(Html
				.fromHtml("<a href=\"http://www.esn.com/policy\">Term of Service</a> "));

		sv.setMovementMethod(LinkMovementMethod.getInstance());

		TextView po = (TextView) findViewById(R.id.lkPolicy);

		po.setText(Html
				.fromHtml("<a href=\"http://www.esn.com/policy\">Privacy and Policy</a> "));

		po.setMovementMethod(LinkMovementMethod.getInstance());

		// Date time dialog view
		mDateDisplay = (EditText) findViewById(R.id.esn_register_txtBirthday);

		mDateDisplay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		
		mDateDisplay.setHint("Birthday");
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

	public void CancelClicked(View view) {

		intent = new Intent(context, WelcomeActivity.class);
		startActivity(intent);
		finish();
	}

	public void RegisterClicked(View view) {
		
		Boolean valid = ValidateEmail();
		
		int require = ValidateRequire();
		
		final EditText email = (EditText)findViewById(R.id.esn_register_txtEmail);
		
		if(valid==false)
		{
						
			email.setError(res.getString(R.string.esn_register_ValidateEmail), res.getDrawable(R.drawable.ic_alerts_and_states_error));
			return;
		}
		else if(require==1)
		{
			EditText name = (EditText)findViewById(R.id.esn_register_txtFullName);
			name.setError(res.getString(R.string.esn_register_RequireFistName), res.getDrawable(R.drawable.ic_alerts_and_states_error));
			return;
		}
		else if(require == 2)
		{
			EditText pass = (EditText)findViewById(R.id.esn_register_Password);
			pass.setError(res.getString(R.string.esn_register_RequirePassword), res.getDrawable(R.drawable.ic_alerts_and_states_error));
			return;
		}
		else
		{
			new Thread(){
				public void run() {
					
					try {
						if(usersManager.CheckEmailExists(email.getText().toString()))
						{
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									Util.showAlert(context, "Error", "Email is exists !");
								}
							});
							
						}
						else
						{
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									dialog = new ProgressDialog(RegisterActivity.this);
									dialog.setTitle(getResources().getString(R.string.esn_global_loading));
									dialog.setMessage("Waiting ....");
									dialog.show();	
									
									RegistertThread registerThread = new RegistertThread();
									registerThread.start();							
								}
							});
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				};
			}.start();
		}		
	}

	private Boolean ValidateEmail()
	{
		final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
	              "[a-zA-Z0-9+._%-+]{1,256}" +
	              "@" +
	              "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
	              "(" +
	              "." +
	              "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
	              ")+"
	          );
		
		EditText txtEmail = (EditText)findViewById(R.id.esn_register_txtEmail);
		String email = txtEmail.getText().toString();
		
		if(EMAIL_ADDRESS_PATTERN.matcher(email).matches())            
		{
			return true;		
		}
		
		return false;
	}	
	
	private int ValidateRequire()
	{
		EditText txtfName = (EditText)findViewById(R.id.esn_register_txtFullName);
		
		EditText txtPassword = (EditText)findViewById(R.id.esn_register_Password);
		
		if(txtfName.getText().toString().isEmpty())
		{
			return 1;
		}		
		else if(txtPassword.getText().toString().isEmpty())
		{
			return 2;
		}
		else
		{
			return 0;
		}
	}
	
	
	private void CheckEmailExists()
	{
		CheckExistsThread email = new CheckExistsThread();
		
		email.start();
		
	}
	
	public class CheckExistsThread extends Thread{
			
			public CheckExistsThread() {
				
			}
			@Override
			public void run() {
				EditText txtEmail = (EditText)findViewById(R.id.esn_register_txtEmail);
				
				String email = txtEmail.getText().toString();
				
				boolean checkEmail = false;
				try {
					checkEmail = usersManager.CheckEmailExists(email);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(checkEmail==true)
				{
					handler.post(new EmailExists());					
				}
				else
				{
					RegistertThread registerThread = new RegistertThread();
					registerThread.start();
				}
			}
	}
	public class RegistertThread extends Thread{
		
		public RegistertThread() {
			
		}
		@Override
		public void run() {			
			
			Users user = new Users();
			
			EditText txtEmail = (EditText)findViewById(R.id.esn_register_txtEmail);
						
			EditText txtfName = (EditText)findViewById(R.id.esn_register_txtFullName);
			
			user.Name = txtfName.getText().toString();			
			
			user.Email = txtEmail.getText().toString();
			
			EditText txtPassword = (EditText)findViewById(R.id.esn_register_Password);
			user.Password = txtPassword.getText().toString();
				
			EditText txtBirthday = (EditText)findViewById(R.id.esn_register_txtBirthday);
				
			String bd = txtBirthday.getText().toString();
			
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			
			try {
				user.Birthday = format.parse(bd);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
			
			EditText phone = (EditText)findViewById(R.id.esn_register_txtPhone);
				
			user.Phone = phone.getText().toString();
				
			Spinner ddlGender = (Spinner)findViewById(R.id.esn_register_ddlGender);
			
			String gender = ddlGender.getSelectedItem().toString();
			
			if(gender.equals("Male"))
			{
				user.Gender=true;
			}
			else
			{
				user.Gender = false;
			}			
			user.AccessToken="_";
			
			int rs = 0;
			try {
				rs = usersManager.Register(user);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			if(rs != 0)
			{
				handler.post(new registerSuccesful());
			}
			else
			{
				handler.post(new registerFail());
			}			
		}
	}
	
	private class registerSuccesful implements Runnable{
		@Override
		public void run() {
			dialog.dismiss();			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			
			builder.setMessage("Kích hoạt tài khoản ?")
			       .setCancelable(false)
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   
			        	   EditText txtemail = (EditText)findViewById(R.id.esn_register_txtEmail);
			        	   
			        	   String email = txtemail.getText().toString();
			        	   
			        	   String[] arr = email.split("@");
			        	   
			        	   String url = arr[1].toString();
			        	   
			        	   url="http://"+url;
			        	   
			        	   Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			        	   
			        	   startActivity(browse);
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   Intent intent = new Intent(context, LoginActivity.class);
			   				startActivity(intent);
			   				finish();
			           }
			});
			
			builder.show();
		}
	}
	private class registerFail implements Runnable{
		@Override
		public void run() {
			dialog.dismiss();
			
			Util.showAlert(context, "Error", "Register Fail. Try Again.");			
		}
	}
	
	private class EmailExists implements Runnable{
		@Override
		public void run() {
			checkEmail = true;			
		}
	}
}