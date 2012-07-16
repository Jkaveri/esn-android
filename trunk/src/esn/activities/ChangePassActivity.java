package esn.activities;

import java.io.IOException;
import java.util.Currency;

import org.json.JSONException;

import com.facebook.android.Util;

import esn.classes.Sessions;
import esn.models.UsersManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassActivity extends Activity {
	Context context;
	private ProgressDialog dialog;
	Intent intent;
	private Handler handler;
	
	protected Sessions session;	
	public String password;
	public String email;
	private Resources res;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pass);
		
		intent = this.getIntent();
		context = this;
		handler = new Handler();
		
		res = getResources();
		
		session = Sessions.getInstance(context);
		
		email = session.currentUser.Email;
		password = session.get("password", null);
		
		EditText txtEmail = (EditText)findViewById(R.id.esn_changepass_email);		
		txtEmail.setText(email);
	}
	
	public void changePassCancelClicked(View button) {
		
		this.finish();
	}
	
	public void ChangePasswordClicked(View view)
	{
		int chk = ValidateRequire();
		
		if(chk == 1)
		{
			EditText txtCurrentPassword = (EditText)findViewById(R.id.esn_changepass_currentpassword);
			txtCurrentPassword.setError(res.getString(R.string.esn_changepassword_requirecurrentpassword), res.getDrawable(R.drawable.ic_alerts_and_states_error));			
			
		}
		else if(chk==2)
		{
			EditText txtNewPassword = (EditText)findViewById(R.id.esn_changepass_newpassword);
			txtNewPassword.setError(res.getString(R.string.esn_changepassword_requirepassword), res.getDrawable(R.drawable.ic_alerts_and_states_error));			
			
			Toast.makeText(this,R.string.esn_changepassword_passwordnotmatch, Toast.LENGTH_SHORT).show();
			
		}
		else if(chk==3)
		{
			EditText txtRepeatNewPassword = (EditText)findViewById(R.id.esn_changepass_repeatnewpassword);
			txtRepeatNewPassword.setError(res.getString(R.string.esn_changepassword_requirerepeatpassword), res.getDrawable(R.drawable.ic_alerts_and_states_error));
			return;
		}
		else if(chk==4)
		{
			EditText txtRepeatNewPassword = (EditText)findViewById(R.id.esn_changepass_repeatnewpassword);
			txtRepeatNewPassword.setError(res.getString(R.string.esn_changepassword_passwordnotmatch), res.getDrawable(R.drawable.ic_alerts_and_states_error));
			return;
		}
		else if(chk==5)
		{
			Util.showAlert(context, res.getResourceName(R.string.esn_global_Error), res.getResourceName(R.string.esn_changepassword_currentpasswordinccorect));
		}
		else
		{
			dialog = new ProgressDialog(this);
			dialog.setTitle(this.getResources().getString(R.string.esn_global_loading));
			dialog.setMessage(res.getString(R.string.esn_global_pleaseWait));
			dialog.show();
			ChangePasswordThread changePassword = new ChangePasswordThread();
			changePassword.start();
		}
	}
	
	private int ValidateRequire()
	{		
		int i=0;
		
		EditText txtCurrentPassword = (EditText)findViewById(R.id.esn_changepass_currentpassword);
		
		EditText txtNewPassword = (EditText)findViewById(R.id.esn_changepass_newpassword);
		
		EditText txtRepeatNewPassword = (EditText)findViewById(R.id.esn_changepass_repeatnewpassword);
		
		if(txtCurrentPassword.getText().toString().isEmpty())
		{
			i=1;
		}
		else if(txtNewPassword.getText().toString().isEmpty())
		{
			i=2;
		}
		else if(txtRepeatNewPassword.getText().toString().isEmpty())
		{
			i=3;
		}	
		else if(!txtNewPassword.getText().toString().equals(txtRepeatNewPassword.getText().toString()))
		{
			i=4;
		}
		else if (txtCurrentPassword.equals(password)) 
		{
			i=5;
		}		
		return i;
	}
	
	public class ChangePasswordThread extends Thread{
		
		public ChangePasswordThread() {
			
		}
		@Override
		public void run() {
			EditText txtEmail = (EditText)findViewById(R.id.esn_changepass_email);
			EditText txtCurrentPassword = (EditText)findViewById(R.id.esn_changepass_currentpassword);
			EditText txtNewPassword = (EditText)findViewById(R.id.esn_changepass_newpassword);
			EditText txtRepeatNewPassword = (EditText)findViewById(R.id.esn_changepass_repeatnewpassword);
			
			UsersManager usersManager = new UsersManager();
			
			Boolean rs = null;
			try {
				rs = usersManager.ChangePassword(txtEmail.getText().toString(), txtCurrentPassword.getText().toString(), txtNewPassword.getText().toString());
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(rs==true)
			{
				handler.post(new ChangePasswordSuccesful());
			}
			else
			{
				handler.post(new ChangePasswordFail());
			}
		}		
	}
	
	private class ChangePasswordSuccesful implements Runnable{
		@Override
		public void run() {
			
			dialog.dismiss();
			
			Toast.makeText(context, res.getString(R.string.app_global_UpdateSuccess), Toast.LENGTH_SHORT).show();		
		}
	}
	
	private class ChangePasswordFail implements Runnable{
		@Override
		public void run() {
			dialog.dismiss();
			
			Toast.makeText(context, res.getString(R.string.app_global_UpdateUnsuccess), Toast.LENGTH_SHORT).show();			
		}
	}
}
