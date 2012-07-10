package esn.activities;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import esn.adapters.ListViewCommentsAdapter;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.Comments;
import esn.models.CommentsManager;
import esn.models.EventType;
import esn.models.EventsManager;
import esn.models.UsersManager;

public class EventDetailActivity extends SherlockActivity implements OnNavigationListener {

	private Intent data;
	private ProgressDialog dialog;
	private int eventId;
	private int accId;
	
	public Handler handler;
	EventsManager manager = new EventsManager();
	
	CommentsManager commentsManager = new CommentsManager();
	
	Sessions session;
	
	EventDetailActivity context;
		
	UsersManager usersManager = new UsersManager();
		
	private ListView lstCm;
	private ListViewCommentsAdapter adapter;
	private int lastScroll = 0;
	private int page = 1;
	
	Resources res;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_detail);				
		data = getIntent();
		
		context = this;
		session = Sessions.getInstance(context);
		
		handler = new Handler();
		dialog = new ProgressDialog(this);
		dialog.setTitle(getResources().getString(R.string.esn_global_loading));
		dialog.setMessage(getResources().getString(R.string.esn_global_pleaseWait));
			
		res = getResources();
		
		eventId = data.getIntExtra("id", 0);
		
		dialog.show();
		
		new GetEventDetailThread(eventId).start();
		
		GetListComment();
		
		lstCm = (ListView)findViewById(R.id.esn_eventDetails_listComments);
				
		lstCm.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adView, View view, int index, long id) {
				//Comments bean = (Comments) adapter.getItem(index);
				//Intent it = new Intent(context, UserPageActivity.class);
				//it.putExtra("accountID", bean.AccID);
				//startActivity(it);
			}
		});	
		
		int i = lstCm.getChildCount();
		
		
	}
	
	@Override
	public void onDestroy() {
		adapter.stopThread();
		adapter.clearCache();
		lstCm.setAdapter(null);
		super.onDestroy();
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater menuInflater = new MenuInflater(this);
		
		menuInflater.inflate(R.menu.event_detail_menu, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId()== R.id.esn_detail_event_like)
		{
			LikeAction();
			return true;
		}
		else if(item.getItemId() == R.id.esn_detail_event_notice)
		{			
			data = new Intent(context,FeedbackActivity.class);
			data.putExtra("EventId", eventId);
			data.putExtra("AccId", accId);			
			startActivity(data);
			return true;
		}
		else if(item.getItemId() == R.id.esn_detail_event_dislike)
		{
			DislikeAction();
			return true;
		}
		else
		{
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	public void LikeAction()
	{
		IsLikeThread isLikeThread = new IsLikeThread();
		isLikeThread.start();
	}
	private class IsLikeThread extends Thread{
		@Override
		public void run() {
			try {
				
				Boolean checkLike = manager.isLiked(eventId, session.currentUser.AccID);
				boolean checkDislike = manager.isDisliked(eventId,session.currentUser.AccID);
				
				if(checkLike==true)
				{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							Toast.makeText(context, res.getString(R.string.esn_eventDetail_islike), 10).show();
							
						}
					});
				}
				else if(checkDislike == true)
				{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							Toast.makeText(context, res.getString(R.string.esn_eventDetail_isdislike), 10).show();
							
						}
					});
				}
				else
				{
					handler.post(new Runnable() {
						
						@Override
						public void run() {								
							btnLikeCLicked();
						}
					});
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void DislikeAction()
	{
		IsDislikeThread isDislikeThread = new IsDislikeThread();
		isDislikeThread.start();
	}
	
	private class IsDislikeThread extends Thread{
		@Override
			public void run() {
			try {
				
				Boolean checkLike = manager.isLiked(eventId, session.currentUser.AccID);
				boolean checkDislike = manager.isDisliked(eventId,session.currentUser.AccID);
				
				if(checkLike==true)
				{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							Toast.makeText(context, res.getString(R.string.esn_eventDetail_islike), 10).show();
							
						}
					});
				}
				else if(checkDislike == true)
				{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							Toast.makeText(context, res.getString(R.string.esn_eventDetail_isdislike), 10).show();
							
						}
					});
				}
				else
				{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
								
							btnDislikeCLicked();
						}
					});
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void btnLikeCLicked() {
		new LikeEventThread().start();
	}

	private class LikeEventThread extends Thread {
		@Override
		public void run() {
						
			int like = -2;
			
			try {
				
				like = manager.like(eventId, session.currentUser.AccID);			
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (like > 0) {
				
				handler.post(new LikeSuccess(like));
				
			} else {
				
				handler.post(new LikeFail(like));
				
			}
		}
	}

	private class LikeSuccess implements Runnable {
		
		private int like;

		public LikeSuccess(int like) {
			
			this.like = like;			
		}

		@Override
		public void run() {
			
			TextView tvLike = (TextView)findViewById(R.id.esn_eventDetail_like);
			
			String k = String.valueOf(like);
			
			tvLike.setText(k);
			
			GetListComment();
			
			Toast.makeText(context, res.getString(R.string.esn_eventDetail_likesuccess), 10).show();
			
		}
	}

	private class LikeFail implements Runnable {
		private int like;

		public LikeFail(int like) {
			this.like = like;
		}

		@Override
		public void run() {
			/*
			 * TextView tvDislike = (TextView)
			 * findViewById(R.id.esn_eventDetail_tvDislikeCount);
			 * tvDislike.setText(like);
			 */
			
			Toast.makeText(context, res.getString(R.string.esn_eventDetail_likefail), 10).show();
		}
	}

	public void btnDislikeCLicked() {
		
		new DisLikeEventThread().start();
		
	}
	
	private class DisLikeEventThread extends Thread {
		@Override
		public void run() {
						
			int dislike = -2;
			
			try {
				
				dislike = manager.dislike(eventId, session.currentUser.AccID);			
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (dislike > 0) {
				
				handler.post(new DisLikeSuccess(dislike));
				
			} else {
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(context, res.getString(R.string.esn_eventDetail_likefail), 10).show();						
					}
				});
				
			}
		}
	}
	
	private class DisLikeSuccess implements Runnable {
		
		private int disLike;

		public DisLikeSuccess(int disLike) {
			
			this.disLike = disLike;			
		}

		@Override
		public void run() {
			
			TextView tvLike = (TextView)findViewById(R.id.esn_eventDetail_dislike);
			
			String k = String.valueOf(disLike);
			
			tvLike.setText(k);
			
			GetListComment();
			
			Toast.makeText(context, res.getString(R.string.esn_eventDetail_dislikesuccess), 10).show();
			
		}
	}

	private class GetEventDetailThread extends Thread {
		private int eventId;

		public GetEventDetailThread(int id) {
			eventId = id;
		}

		@Override
		public void run() {
			if (eventId > 0) {
				EventsManager manager = new EventsManager();
				
				esn.models.Events event = null;
				
				try {
					
					try {
						event = manager.retrieve(eventId);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (manager.isLiked(eventId,Sessions.getInstance(EventDetailActivity.this).currentUser.AccID)) {
						
						EventDetailActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								/*
								 * Button btLike = (Button)
								 * findViewById(R.id.esn_eventDetail_btLike);
								 * btLike.setVisibility(Button.INVISIBLE);
								 */
							}
						});
					} else if (manager
							.isDisliked(
									eventId,
									Sessions.getInstance(EventDetailActivity.this).currentUser.AccID)) {
						EventDetailActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								/*
								 * Button btDislike = (Button)
								 * findViewById(R.id.esn_eventDetail_btDislike);
								 * btDislike.setVisibility(Button.INVISIBLE);
								 */
							}
						});
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				EventDetailActivity.this.runOnUiThread(new GetDetailSuccess(event));
			}
		}
	}

	private class GetDetailSuccess implements Runnable {
		
		private esn.models.Events event;

		public GetDetailSuccess(esn.models.Events event) {
			this.event = event;
		}

		@Override
		public void run() {
			
			if (event != null) {
				TextView tvTitle = (TextView) findViewById(R.id.esn_eventDetail_title);
				TextView tvDescription = (TextView) findViewById(R.id.esn_eventDetail_description);
				TextView tvDateCreated = (TextView) findViewById(R.id.esn_eventDetail_dateCreated);
				//TextView tvEventType = (TextView)findViewById(R.id.esn_eventDetail_eventType);
				TextView tvDislike = (TextView)findViewById(R.id.esn_eventDetail_dislike);
				TextView tvLike = (TextView)findViewById(R.id.esn_eventDetail_like);
				ImageView icEventType = (ImageView) findViewById(R.id.esn_eventDetail_iconEventType);
				TextView tvUsername = (TextView)findViewById(R.id.esn_eventDetail_name);
				
				final ImageView imgEvent = (ImageView) findViewById(R.id.esn_eventDetail_image);
				imgEvent.setImageResource(R.drawable.ic_no_avata);
								
				tvTitle.setText(event.Title);
				tvDateCreated.setText(Utils.DateToStringByLocale(event.DayCreate,1));
				tvDescription.setText(event.Description);
				
				tvDislike.setText(String.valueOf(event.Dislike));
				tvLike.setText(String.valueOf(event.Like));
				
				tvUsername.setText(event.user.Name);
				
				accId = event.AccID;
				
				icEventType.setImageResource(EventType.getIconId(event.EventTypeID, event.getLevel()));
								
				DisplayMetrics dm = new DisplayMetrics();
		        getWindowManager().getDefaultDisplay().getMetrics(dm);
		        
		        imgEvent.setMaxWidth(dm.widthPixels);
		        imgEvent.setMaxHeight(dm.widthPixels);
		        
				
				if (event.Picture != null && event.Picture.length() > 0) {
					new Thread() {
						public void run() {
							try {
								final Bitmap image = Utils
										.getBitmapFromURL(event.Picture);
								handler.post(new Runnable() {

									@Override
									public void run() {
										imgEvent.setImageBitmap(image);
									}
								});
							} catch (IOException e) {
								Log.e("esn", e.getMessage());
							}

						};
					}.start();
				}
				//dialog.dismiss();
			}
		}
	}

	public void CommentClicked(View view)
	{
		EditText txtComment = (EditText)findViewById(R.id.esn_eventDetail_txtComment);
		
		String content = txtComment.getText().toString();
		
		if(content.isEmpty())
		{
			Toast.makeText(context, res.getString(R.string.esn_eventDetail_entercontent), Toast.LENGTH_SHORT).show();
			return;
		}
		
		new CommentThread(content, session.currentUser.AccID, eventId).start();
	}

	private class CommentThread extends Thread {
		
		private String content;
		private int accId;
		private int eventId;
		
		public CommentThread(String ct,int acc, int event)
		{
			this.content = ct;
			this.accId = acc;
			this.eventId = event;
		}
		
		@Override
		public void run() {
			
			try {
				int rs = commentsManager.CreateComment(eventId, accId, content);
				
				if(rs>0)
				{
					handler.post(new CommentSuccess());
				}
				else
				{
					handler.post(new CommentFail());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private class CommentSuccess implements Runnable{

		@Override
		public void run() {
			//Toast.makeText(context, res.getString(R.string.esn_eventDetail_commensuccess) , 10).show();
			dialog.show();
			GetListComment();
		}
		
	}
	
	private class CommentFail implements Runnable{

		@Override
		public void run() {
			Toast.makeText(context, res.getString(R.string.esn_eventDetail_commenfail), 10).show();
		}		
	}

	public void GetListComment()
	{
		Thread thr = new Thread(new Runnable() {
			
			Handler hd = new Handler();
			@Override
			public void run() {

				CommentsManager commentsManager = new CommentsManager();
				try {
					
					final ArrayList<Comments> itemList = commentsManager.GetListComment(eventId,page,3);
					
					hd.post(new Runnable() {

						@Override
						public void run() {
							adapter = new ListViewCommentsAdapter(EventDetailActivity.this,itemList);
							
							lstCm.setAdapter(adapter);
							
							int k = lstCm.getCount();
							
							ListView lv = (ListView)findViewById(R.id.esn_eventDetails_listComments);
							
							//Button bt = (Button)findViewById(R.id.esn_eventDetails_btShowCommentAll);
							
							if(k==0)
							{
								lv.setLayoutParams(new LinearLayout.LayoutParams(
								          LinearLayout.LayoutParams.FILL_PARENT,10
								      ));
															
								//bt.setVisibility(0);
							}
							else if(k==1)
							{
								lv.setLayoutParams(new LinearLayout.LayoutParams(
								          LinearLayout.LayoutParams.FILL_PARENT,78
								      ));
							}
							else if(k==2)
							{
								lv.setLayoutParams(new LinearLayout.LayoutParams(
								          LinearLayout.LayoutParams.FILL_PARENT,147
								      ));
							}
							else
							{
								lv.setLayoutParams(new LinearLayout.LayoutParams(
								          LinearLayout.LayoutParams.FILL_PARENT,204
								      ));
							}
							
							dialog.dismiss();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thr.start();
	}
		
	private void loadCommentList(final int pageSize, final int pageIndex) {
		Thread thr = new Thread(new Runnable() {
			
			@Override
			public void run() {

				CommentsManager commentsManager = new CommentsManager();
				try {
					final ArrayList<Comments> itemList = commentsManager.GetListComment(eventId,pageIndex,pageSize);
					handler.post(new Runnable() {

						@Override
						public void run() {
							adapter.add(itemList);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thr.start();
	}	

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void ShowAllCommentClicked(View view)
	{
		data = new Intent(context,EventDetailCommentActivity.class);
		
		data.putExtra("EventId", eventId);
		data.putExtra("AccId", accId);
		startActivity(data);
	}
	
}
