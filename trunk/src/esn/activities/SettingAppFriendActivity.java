package esn.activities;
import esn.activities.R;
import esn.classes.Sessions;
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
		
		session = Sessions.getInstance(SettingAppFriendActivity.this);
		
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
		
		Boolean chb1 = session.get("app.setting.friend.email.comment",false);	
		Boolean chb2 = session.get("app.setting.friend.email.createevent",false);
		Boolean chb3 = session.get("app.setting.friend.email.friendfbjoin",false);
		Boolean chb4 = session.get("app.setting.friend.email.truefalse",false);
		Boolean chb5 = session.get("app.setting.friend.email.request",false);
		Boolean chb6 = session.get("app.setting.friend.email.shareplace",false);
		
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
		
		Boolean chb7 = session.get("app.setting.friend.phone.comment",false);
		Boolean chb8 = session.get("app.setting.friend.phone.createevent",false);
		Boolean chb9 = session.get("app.setting.friend.phone.friendfbjoin",false);
		Boolean chb10 = session.get("app.setting.friend.phone.truefalse",false);
		Boolean chb11= session.get("app.setting.friend.phone.request",false);
		Boolean chb12 = session.get("app.setting.friend.phone.shareplace",false);
		
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
		
		if(chbEmailComment.isChecked())
		{
			session.put("app.setting.friend.email.comment", true);
		}	
		else
		{
			session.put("app.setting.friend.email.comment", false);
		}
		if(chbEmailCreateEvent.isChecked())
		{
			session.put("app.setting.friend.email.createevent", true);
		}
		else
		{
			session.put("app.setting.friend.email.createevent", false);
		}
		if(chbEmailFriendFbJoin.isChecked())
		{
			session.put("app.setting.friend.email.friendfbjoin", true);
		}
		else
		{
			session.put("app.setting.friend.email.friendfbjoin", false);
		}
		if(chbEmailTrueFalse.isChecked())
		{
			session.put("app.setting.friend.email.truefalse", true);
		}
		else
		{
			session.put("app.setting.friend.email.truefalse", false);
		}
		if(chbEmailFriendRequest.isChecked())
		{
			session.put("app.setting.friend.email.request", true);
		}
		else
		{
			session.put("app.setting.friend.email.request", false);
		}
		if(chbEmailSharePlace.isChecked())
		{
			session.put("app.setting.friend.email.shareplace", true);
		}
		else
		{
			session.put("app.setting.friend.email.shareplace", false);
		}		
		
		
		
		
		if(chbPhoneComment.isChecked())
		{
			session.put("app.setting.friend.Phone.comment", true);
		}
		else
		{
			session.put("app.setting.friend.Phone.comment", false);
		}
		if(chbPhoneCreateEvent.isChecked())
		{
			session.put("app.setting.friend.Phone.createevent", true);
		}
		else
		{
			session.put("app.setting.friend.Phone.createevent", false);
		}
		if(chbPhoneFriendFbJoin.isChecked())
		{
			session.put("app.setting.friend.Phone.friendfbjoin", true);
		}
		else
		{
			session.put("app.setting.friend.Phone.friendfbjoin", false);
		}
		if(chbPhoneTrueFalse.isChecked())
		{
			session.put("app.setting.friend.Phone.truefalse", true);
		}
		else
		{
			session.put("app.setting.friend.Phone.truefalse", false);
		}
		if(chbPhoneFriendRequest.isChecked())
		{
			session.put("app.setting.friend.Phone.request", true);
		}
		else
		{
			session.put("app.setting.friend.Phone.request", false);
		}
		if(chbPhoneSharePlace.isChecked())
		{
			session.put("app.setting.friend.Phone.shareplace", true);
		}
		else
		{
			session.put("app.setting.friend.Phone.shareplace", false);
		}
		
		Toast.makeText(context, res.getString(R.string.esn_setting_app_informationsaved), 100).show();
	}
}
