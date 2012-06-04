package esn.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.kobjects.isodate.IsoDate;

import com.facebook.android.Util;

import esn.adapters.Md5Encript;
import esn.models.Users;
import esn.models.UsersManager;
import android.R.bool;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class RegisterActivity extends Activity {

	Intent intent;
	
	private final int REQUEST_CODE_CREATE_LOGIN_LOGIN = 1;
	
	private EditText mDateDisplay;  
	private int mYear;  
    private int mMonth;  
    private int mDay;  
	private static final int DATE_DIALOG_ID = 3;

	Context context;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register);

		intent = this.getIntent();
		context = this;
		handler = new Handler();
		
		boolean isFbSignup = intent.getBooleanExtra("facebookSignup", false);
		if (isFbSignup) {
			String first_name = intent.getStringExtra("first_name");
			String last_name = intent.getStringExtra("last_name");
			String birthday = intent.getStringExtra("birthday");
			String gender = intent.getStringExtra("gender");
			String email = intent.getStringExtra("email");
			((TextView) findViewById(R.id.esn_register_txtFirstName))
					.setText(first_name);
			((TextView) findViewById(R.id.esn_register_txtLastname))
					.setText(last_name);
			((TextView) findViewById(R.id.esn_register_txtEmail))
					.setText(email);
			((TextView) findViewById(R.id.esn_register_txtBirthday))
					.setText(birthday);
			if (gender.equals("male"))
				((RadioButton) findViewById(R.id.esn_register_rbMale))
						.setChecked(true);
			else
				((RadioButton) findViewById(R.id.esn_register_rbFemale))
						.setChecked(true);

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
        updateDisplay();
	}
	
	private void updateDisplay() {  
        mDateDisplay.setText(mMonth + "/"+ mDay + "/" + mYear);  
    }
	
	private DatePickerDialog.OnDateSetListener mDateSetListener =  
            new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {					
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

		setResult(RESULT_CANCELED, intent);

		finish();
	}
	
	public void RegisterClicked(View view)
	{
		new Thread(){
			public void run() {
				Users user = new Users();
				
				EditText email = (EditText)findViewById(R.id.esn_register_txtEmail);
				
				user.Email = email.getText().toString().trim();
				
				EditText password = (EditText)findViewById(R.id.esn_register_Password);
				
				String pass = password.getText().toString().trim();
				
				pass = Md5Encript.md5(pass);
				
				user.Password = pass;
				
				EditText fName = (EditText)findViewById(R.id.esn_register_txtFirstName);
				EditText lName = (EditText)findViewById(R.id.esn_register_txtLastname);
				
				user.Name = fName.getText().toString().trim() + lName.getText().toString().trim();
								
				EditText birthday = (EditText)findViewById(R.id.esn_register_txtBirthday);
				/*
				SimpleDateFormat curFormater = new SimpleDateFormat("MM/dd/yyyy"); 
				
				Date dateObj = null;
				try {
					dateObj = curFormater.parse(birthday.getText().toString().trim());
				} catch (ParseException e) {
					
					e.printStackTrace();
				} 						    
				
				user.DateOfBirth = dateObj; */
				
				user.DateOfBirth = birthday.getText().toString();
				
				EditText phone = (EditText)findViewById(R.id.esn_register_txtPhone);
				
				user.Phone = phone.getText().toString().trim();
				
				RadioButton gender = (RadioButton)findViewById(R.id.esn_register_rbMale);
				
				if(gender.isChecked())
				{
					user.Gender = true;
				}
				else
				{
					user.Gender = false;
				}
				
				UsersManager usersManager = new UsersManager();
				
				Boolean rs = usersManager.Register(user);
				
				if(rs==true)
				{
					Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
					startActivityForResult(intentLogin, REQUEST_CODE_CREATE_LOGIN_LOGIN);
				}
				else
				{
					handler.post(new Runnable() {

						@Override
						public void run() {

							Util.showAlert(context, "Warning",
									"Incorrect information !");
						}
					});
				}				
			};
		}.start();
	}
}