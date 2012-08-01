package esn.activities;
import esn.activities.R;
import esn.classes.Sessions;
import esn.models.FriendNotification;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;


public class SettingAppFriendActivity extends Activity {

	public Handler handler;	
	
	Sessions session;
	
	Context context;
	
	Resources res;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.esn_setting_app_friend);
		
		context = this;
		
		session = Sessions.getInstance(context);
		
		handler = new Handler();
		
		res = getResources();
		
		ShowInfo();
	}
	
	public void ShowInfo()
	{
		CheckBox chbEmailComment = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendcomment);
		CheckBox chbEmailCreateEvent = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendcreateevent);
		CheckBox chbEmailFriendFbJoin = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendfacebookjoin);
		CheckBox chbEmailTrueFalse = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendtruefalse);
		CheckBox chbEmailFriendRequest = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendrequest);
		CheckBox chbEmailSharePlace = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendshareplace);
		
		CheckBox chbPhoneComment = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendcomment);
		CheckBox chbPhoneCreateEvent = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendcreateevent);
		CheckBox chbPhoneFriendFbJoin = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendfacebookjoin);
		CheckBox chbPhoneTrueFalse = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendtruefalse);
		CheckBox chbPhoneFriendRequest = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendrequest);
		CheckBox chbPhoneSharePlace = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendshareplace);
		
		Boolean chb1 = session.friendNotification.emailComment;	
		Boolean chb2 = session.friendNotification.emailCreateEvent;
		Boolean chb3 = session.friendNotification.emailFriendFbJoin;
		Boolean chb4 = session.friendNotification.emailConfirmEvent;
		Boolean chb5 = session.friendNotification.emailFriendRequest;
		Boolean chb6 = session.friendNotification.emailSharePlace;
		Boolean chb7 = session.friendNotification.phoneComment;
		Boolean chb8 = session.friendNotification.phoneCreateEvent;
		Boolean chb9 = session.friendNotification.phoneFriendFbJoin;
		Boolean chb10 = session.friendNotification.phoneConfirmEvent;
		Boolean chb11 = session.friendNotification.phoneFriendRequest;
		Boolean chb12 = session.friendNotification.phoneSharePlace;
		
		if(chb1==true)
		{
			chbEmailComment.setChecked(true);
		}
		
		if(chb2==true)
		{
			chbEmailCreateEvent.setChecked(true);
		}
		
		if(chb3==true)
		{
			chbEmailFriendFbJoin.setChecked(true);
		}		
		
		if(chb4==true)
		{
			chbEmailTrueFalse.setChecked(true);
		}
		
		if(chb5==true)
		{
			chbEmailFriendRequest.setChecked(true);
		}		
		
		if(chb6==true)
		{
			chbEmailSharePlace.setChecked(true);
		}
		
		if(chb7==true)
		{
			chbPhoneComment.setChecked(true);
		}
		
		if(chb8==true)
		{
			chbPhoneCreateEvent.setChecked(true);
		}
		
		if(chb9==true)
		{
			chbPhoneFriendFbJoin.setChecked(true);
		}		
		
		if(chb10==true)
		{
			chbPhoneTrueFalse.setChecked(true);
		}
		
		if(chb11==true)
		{
			chbPhoneFriendRequest.setChecked(true);
		}		
		
		if(chb12==true)
		{
			chbPhoneSharePlace.setChecked(true);
		}
		
	}
	
	public void FriendSettingSaved(View view)
	{
		CheckBox chbEmailCreateEvent = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendcreateevent);
		CheckBox chbEmailFriendRequest = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendrequest);
		CheckBox chbEmailFriendFbJoin = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendfacebookjoin);
		CheckBox chbEmailComment = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendcomment);
		CheckBox chbEmailTrueFalse = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendtruefalse);
		CheckBox chbEmailSharePlace = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_email_friendshareplace);
		
		CheckBox chbPhoneCreateEvent = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendcreateevent);
		CheckBox chbPhoneFriendFbJoin = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendfacebookjoin);
		CheckBox chbPhoneFriendRequest = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendrequest);
		CheckBox chbPhoneComment = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendcomment);
		CheckBox chbPhoneTrueFalse = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendtruefalse);
		CheckBox chbPhoneSharePlace = (CheckBox)findViewById(R.id.esn_setting_app_friend_chb_phone_friendshareplace);
		
		FriendNotification friendNotification = new FriendNotification();
		
		if(chbEmailComment.isChecked())
		{
			friendNotification.emailComment = true;
		}	
		else
		{
			friendNotification.emailComment = false;
		}
		if(chbEmailCreateEvent.isChecked())
		{
			friendNotification.emailCreateEvent = true;
		}
		else
		{
			friendNotification.emailCreateEvent = false;
		}
		if(chbEmailFriendFbJoin.isChecked())
		{
			friendNotification.emailFriendFbJoin = true;
		}
		else
		{
			friendNotification.emailFriendFbJoin = false;
		}
		if(chbEmailTrueFalse.isChecked())
		{
			friendNotification.emailConfirmEvent = true;
		}
		else
		{
			friendNotification.emailConfirmEvent = false;
		}
		if(chbEmailFriendRequest.isChecked())
		{
			friendNotification.emailFriendRequest = true;
		}
		else
		{
			friendNotification.emailFriendRequest = false;
		}
		if(chbEmailSharePlace.isChecked())
		{
			friendNotification.emailSharePlace = true;
		}
		else
		{
			friendNotification.emailSharePlace = false;
		}		
		
		
		
		
		if(chbPhoneComment.isChecked())
		{
			friendNotification.phoneComment = true;
		}
		else
		{
			friendNotification.phoneComment = false;
		}
		if(chbPhoneCreateEvent.isChecked())
		{
			friendNotification.phoneCreateEvent = true;
		}
		else
		{
			friendNotification.phoneCreateEvent =false;
		}
		if(chbPhoneFriendFbJoin.isChecked())
		{
			friendNotification.phoneFriendFbJoin = true;
		}
		else
		{
			friendNotification.phoneFriendFbJoin = false;
		}
		if(chbPhoneTrueFalse.isChecked())
		{
			friendNotification.phoneConfirmEvent = true;
		}
		else
		{
			friendNotification.phoneConfirmEvent = false;
		}
		if(chbPhoneFriendRequest.isChecked())
		{
			friendNotification.phoneFriendRequest = true;
		}
		else
		{
			friendNotification.phoneFriendRequest = false;
		}
		if(chbPhoneSharePlace.isChecked())
		{
			friendNotification.phoneSharePlace = true;
		}
		else
		{
			friendNotification.phoneSharePlace = false;
		}
		
		session.friendNotification = friendNotification;
		
		Toast.makeText(context, res.getString(R.string.esn_setting_app_informationsaved), Toast.LENGTH_SHORT).show();
	}
}
