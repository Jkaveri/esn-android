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
	
	FriendNotification friendNotification;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.esn_setting_app_friend);
		
		context = this;
		
		session = Sessions.getInstance(context);
		
		handler = new Handler();
		
		res = getResources();
		
		friendNotification = new FriendNotification(session);
		
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
		
		Boolean chb1 = friendNotification.getEmailComment();		
		Boolean chb2 = friendNotification.getEmailCreateEvent();
		
		Boolean chb3 = friendNotification.getEmailFriendFbJoin();
		Boolean chb4 = friendNotification.getEmailConfirmEvent();
		Boolean chb5 = friendNotification.getEmailFriendRequest();
		Boolean chb6 = friendNotification.getEmailSharePlace();
		Boolean chb7 = friendNotification.getPhoneComment();
		Boolean chb8 = friendNotification.getPhoneCreateEvent();
		Boolean chb9 = friendNotification.getPhoneConfirmEvent();
		Boolean chb10 = friendNotification.getPhoneConfirmEvent();
		Boolean chb11 = friendNotification.getPhoneFriendRequest();
		Boolean chb12 = friendNotification.getPhoneSharePlace();
		
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
		
		
		if(chbEmailComment.isChecked())
		{
			friendNotification.setEmailComment(true);
		}	
		else
		{
			friendNotification.setEmailComment(false);
		}
		if(chbEmailCreateEvent.isChecked())
		{
			friendNotification.setEmailCreateEvent(true);
		}
		else
		{
			friendNotification.setEmailCreateEvent(false);
		}
		if(chbEmailFriendFbJoin.isChecked())
		{
			friendNotification.setEmailFriendFbJoin(true);
		}
		else
		{
			friendNotification.setEmailFriendFbJoin(false);
		}
		if(chbEmailTrueFalse.isChecked())
		{
			friendNotification.setEmailConfirmEvent(true);
		}
		else
		{
			friendNotification.setEmailConfirmEvent(false);
		}
		if(chbEmailFriendRequest.isChecked())
		{
			friendNotification.setEmailFriendRequest(true);
		}
		else
		{
			friendNotification.setEmailFriendRequest(false);
		}
		if(chbEmailSharePlace.isChecked())
		{
			friendNotification.setEmailSharePlace(true);
		}
		else
		{
			friendNotification.setEmailSharePlace(false);
		}		
		
		
		
		
		if(chbPhoneComment.isChecked())
		{
			friendNotification.setPhoneComment(true);
		}
		else
		{
			friendNotification.setPhoneComment(false);
		}
		if(chbPhoneCreateEvent.isChecked())
		{
			friendNotification.setPhoneCreateEvent(true);
		}
		else
		{
			friendNotification.setPhoneCreateEvent(false);
		}
		if(chbPhoneFriendFbJoin.isChecked())
		{
			friendNotification.setPhoneFriendFbJoin(true);
		}
		else
		{
			friendNotification.setPhoneFriendFbJoin(false);
		}
		if(chbPhoneTrueFalse.isChecked())
		{
			friendNotification.setPhoneConfirmEvent(true);
		}
		else
		{
			friendNotification.setPhoneConfirmEvent(false);
		}
		if(chbPhoneFriendRequest.isChecked())
		{
			friendNotification.setPhoneFriendRequest(true);
		}
		else
		{
			friendNotification.setPhoneFriendRequest(false);
		}
		if(chbPhoneSharePlace.isChecked())
		{
			friendNotification.setPhoneSharePlace(true);
		}
		else
		{
			friendNotification.setPhoneSharePlace(false);
		}		
		
		Toast.makeText(context, res.getString(R.string.esn_setting_app_informationsaved), Toast.LENGTH_SHORT).show();
	}
}
