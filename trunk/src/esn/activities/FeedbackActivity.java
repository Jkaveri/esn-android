package esn.activities;

import java.io.IOException;
import java.util.Date;

import org.json.JSONException;

import esn.adapters.ListViewCommentsAdapter;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.CommentsManager;
import esn.models.EventsManager;
import esn.models.UsersManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class FeedbackActivity extends Activity {

	private Intent data;
	private ProgressDialog dialog;
	private int eventId;
	private int accId;
	
	public Handler handler;
	EventsManager manager = new EventsManager();
	
	CommentsManager commentsManager = new CommentsManager();
	
	Sessions session;
	
	FeedbackActivity context;
		
	UsersManager usersManager = new UsersManager();
		
	private ListView lstCm;
	private ListViewCommentsAdapter adapter;
	private int lastScroll = 0;
	private int page = 1;
	
	Resources res;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.feedback);
		
		context = this;
		
		session = Sessions.getInstance(context);
		
		data=getIntent();
		
		res = getResources();
		
		handler = new Handler();
		dialog = new ProgressDialog(this);			
		
		eventId = data.getIntExtra("EventId", 0);
		accId = data.getIntExtra("AccId", 0);
		
	}
	
	public void feedbackClicked(View view)
	{		
		dialog.setTitle(getResources().getString(R.string.esn_global_loading));
		dialog.setMessage(getResources().getString(R.string.esn_global_pleaseWait));
		dialog.show();
		
		FeedbackThread feedbackThread = new FeedbackThread();
		feedbackThread.start();
	}
	
	private class FeedbackThread extends Thread{
		@Override
		public void run() {
			
			EditText txtTitle = (EditText)findViewById(R.id.esn_feedback_title);
			EditText txtContent = (EditText)findViewById(R.id.esn_feedback_content);
			
			String content = txtContent.getText().toString();
			String title = txtTitle.getText().toString();
			
			try {
				Boolean rs = manager.NewFeedback(eventId, accId, title, content);
				
				if(rs==true)
				{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							dialog.dismiss();
							
							Toast.makeText(context, res.getString(R.string.esn_app_feedback_statussendsuccess), 10).show();
							
						}
					});
				}
				else
				{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							dialog.dismiss();
							
							Toast.makeText(context, res.getString(R.string.esn_app_feedback_statussendfail), 10).show();							
						}
					});
				}
			} catch (JSONException e) {
				Utils.DismitDialog(dialog, context);
				Utils.showToast(context, res.getString(R.string.esn_global_Error), Toast.LENGTH_SHORT);
				e.printStackTrace();
			} catch (IOException e) {
				Utils.DismitDialog(dialog, context);
				Utils.showToast(context, res.getString(R.string.esn_global_ConnectionError), Toast.LENGTH_SHORT);
				e.printStackTrace();
			}		
		}
	}
	
}
