package esn.activities;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import org.json.JSONException;
import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.android.Util;

import esn.classes.Sessions;
import esn.models.Users;
import esn.models.UsersManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
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

	private String fbID;

	private String fbAccessToken;

	private long fbAccessExpires;
	
	private static boolean checkEmail=false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false); 
		getSupportActionBar().hide();
		
		intent = this.getIntent();
		context = this;
		handler = new Handler();

		sessions = Sessions.getInstance(context);
		
		res = getResources();
		
		boolean isFbSignup = intent.getBooleanExtra("facebookSignup", false);
		
		fbID = "";
		fbAccessToken = "";
		fbAccessExpires = 0;
		
		if (isFbSignup) {
			String first_name = intent.getStringExtra("first_name");
			String last_name = intent.getStringExtra("last_name");
			String birthday = intent.getStringExtra("birthday");
			String gender = intent.getStringExtra("gender");
			String email = intent.getStringExtra("email");
			fbID = intent.getStringExtra("fb_id");
			fbAccessToken = intent.getStringExtra("fb_access_token");
			fbAccessExpires = intent.getLongExtra("fb_access_expires",0);
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
				
				int pos = arr.getPosition(res.getString(R.string.esn_register_rdbFemale));
						
				sp.setSelection(pos);			
			}
			else
			{
				Spinner sp = (Spinner)findViewById(R.id.esn_register_ddlGender);
				
				ArrayAdapter arr = (ArrayAdapter)sp.getAdapter();
				
				int pos = arr.getPosition(res.getString(R.string.esn_register_rdbFemale));
						
				sp.setSelection(pos);	
			}

		}
				
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
		
		//mDateDisplay.setHint(res.getString(R.string.esn_register_txtBirthday));
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        	    	
	    	intent = new Intent(context,WelcomeActivity.class);
	    	startActivity(intent);
	    	finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	public void RegisterClicked(View view) {
		
		Boolean valid = ValidateEmail();
		
		int require = ValidateRequire();
		
		final EditText email = (EditText)findViewById(R.id.esn_register_txtEmail);
		
		if(valid==false)
		{		
			Toast.makeText(context, res.getString(R.string.esn_register_ValidateEmail),Toast.LENGTH_SHORT).show();			
			return;
		}
		else if(require==1)
		{	
			Toast.makeText(context, res.getString(R.string.esn_register_RequireFistName),Toast.LENGTH_SHORT).show();			
			return;
		}
		else if(require == 2)
		{
			Toast.makeText(context, res.getString(R.string.esn_register_RequirePassword),Toast.LENGTH_SHORT).show();			
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
									Toast.makeText(context, res.getString(R.string.esn_register_EmailExists), Toast.LENGTH_SHORT).show();
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
									dialog.setMessage(res.getString(R.string.esn_global_pleaseWait));
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
			
			user.fbID=fbID;
			
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
			
			if(gender.equals(res.getString(R.string.esn_register_rdbMale)))
			{
				user.Gender=true;
			}
			else
			{
				user.Gender = false;
			}			
			user.AccessToken=fbAccessToken;
			
			user.fbID = fbID;
			
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
			
			builder.setMessage(res.getString(R.string.esn_register_activeyouraccount))
			       .setCancelable(false)
			       .setPositiveButton(res.getString(R.string.esn_register_activeyes), new DialogInterface.OnClickListener() {
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
			       .setNegativeButton(res.getString(R.string.esn_register_activeno), new DialogInterface.OnClickListener() {
			           
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
			
			Util.showAlert(context, res.getString(R.string.esn_global_Error), res.getString(R.string.esn_global_tryagain));			
		}
	}
	
	private class EmailExists implements Runnable{
		@Override
		public void run() {
			checkEmail = true;			
		}
	}
	
	public void PrivatePolicyClicked(View v)
	{
		intent = new Intent(context,PolicyActivity.class);
		startActivity(intent);
	}
}