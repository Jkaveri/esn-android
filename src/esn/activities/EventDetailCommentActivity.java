package esn.activities;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;

import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;


import esn.adapters.ListViewCommentsAdapter;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.Comments;
import esn.models.CommentsManager;
import esn.models.EventsManager;
import esn.models.UsersManager;

public class EventDetailCommentActivity extends Activity implements
		OnNavigationListener {

	private Intent data;
	private ProgressDialog dialog;
	private int eventId;
	private int accId;

	public Handler handler;
	EventsManager manager = new EventsManager();

	CommentsManager commentsManager = new CommentsManager();

	Sessions session;

	EventDetailCommentActivity context;

	UsersManager usersManager = new UsersManager();

	private ListView lstCm;

	private ListViewCommentsAdapter adapter;
	private int lastScroll = 0;
	private int page = 1;

	Resources res;

	Date lastTime = null;
	long timeout = 30000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.event_detail_comment);

		context = this;

		session = Sessions.getInstance(context);

		data = getIntent();

		res = getResources();

		handler = new Handler();
		dialog = new ProgressDialog(this);
		dialog.setTitle(getResources().getString(R.string.esn_global_loading));
		dialog.setMessage(getResources().getString(R.string.esn_global_pleaseWait));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		eventId = data.getIntExtra("EventId", 0);
		accId = data.getIntExtra("AccId", 0);

		dialog.show();

		GetListComment();

		lstCm = (ListView) findViewById(R.id.esn_comments_listComments);

		lstCm.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int scroll = firstVisibleItem + visibleItemCount;
				boolean acti = scroll == totalItemCount - 1;
				if (acti && scroll > lastScroll) {
					lastScroll = scroll;
					page++;
					context.loadCommentList(8, page);
					// Toast.makeText(context, "Load data",
					// Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	public void GetListComment() {
		
		Thread thr = new Thread(new Runnable() {

			Handler hd = new Handler();

			@Override
			public void run() {

				CommentsManager commentsManager = new CommentsManager();
				
					final ArrayList<Comments> itemList;
					
					try {
						
						itemList = commentsManager.GetListComment(eventId, page, 8);
						
						adapter = new ListViewCommentsAdapter(EventDetailCommentActivity.this, itemList);

						lstCm.setAdapter(adapter);

						dialog.dismiss();
						
					} catch (IllegalArgumentException e) {
						Utils.DismitDialog(dialog,context);
						Utils.showToast(context, res.getString(R.string.esn_global_Error), Toast.LENGTH_SHORT);
						e.printStackTrace();
					} catch (JSONException e) {
						Utils.DismitDialog(dialog,context);
						Utils.showToast(context, res.getString(R.string.esn_global_Error), Toast.LENGTH_SHORT);
						e.printStackTrace();
					} catch (IOException e) {
						Utils.DismitDialog(dialog,context);
						Utils.showToast(context, res.getString(R.string.esn_global_ConnectionError), Toast.LENGTH_SHORT);
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						Utils.DismitDialog(dialog,context);
						Utils.showToast(context, res.getString(R.string.esn_global_Error), Toast.LENGTH_SHORT);
						e.printStackTrace();
					} catch (ParseException e) {
						Utils.DismitDialog(dialog,context);
						Utils.showToast(context, res.getString(R.string.esn_global_Error), Toast.LENGTH_SHORT);
					}

					hd.post(new Runnable() {

						@Override
						public void run() {
							
						}
					});
				
			}
		});

		thr.start();
	}

	@Override
	public void onDestroy() {
		if (adapter != null) {
			adapter.stopThread();
			adapter.clearCache();
		}

		lstCm.setAdapter(null);
		super.onDestroy();
	}

	private void loadCommentList(final int pageSize, final int pageIndex) {

		Thread thr = new Thread(new Runnable() {

			@Override
			public void run() {

				CommentsManager commentsManager = new CommentsManager();
				try {

					final ArrayList<Comments> itemList = commentsManager
							.GetListComment(eventId, pageIndex, pageSize);

					handler.post(new Runnable() {

						@Override
						public void run() {
							adapter.add(itemList);
						}
					});
				} catch (Exception e) {
					Utils.DismitDialog(dialog,context);
					Utils.showToast(context, res.getString(R.string.esn_global_Error), Toast.LENGTH_SHORT);
				}
			}
		});

		thr.start();
	}

	public void CommentClicked(View view) {
		Date now = new Date();

		if (lastTime != null) {
			long count = Utils.calculateTime(lastTime, now);

			if (count > timeout) {
				EditText txtComment = (EditText) findViewById(R.id.esn_comments_txtComment);

				String content = txtComment.getText().toString();

				if (content.isEmpty()) {
					Toast.makeText(
							context,
							res.getString(R.string.esn_eventDetail_entercontent),
							Toast.LENGTH_SHORT).show();
					return;
				}

				lastTime = now;

				new CommentThread(content, session.currentUser.AccID, eventId)
						.start();

			} else {
				Toast.makeText(context,
						res.getString(R.string.esn_eventDetail_commentwaiting),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			EditText txtComment = (EditText) findViewById(R.id.esn_comments_txtComment);

			String content = txtComment.getText().toString();

			if (content.isEmpty()) {
				Toast.makeText(context,
						res.getString(R.string.esn_eventDetail_entercontent),
						Toast.LENGTH_SHORT).show();
				return;
			}

			lastTime = now;

			new CommentThread(content, session.currentUser.AccID, eventId).start();
		}
	}

	private class CommentThread extends Thread {

		private String content;
		private int accId;
		private int eventId;

		public CommentThread(String ct, int acc, int event) {
			this.content = ct;
			this.accId = acc;
			this.eventId = event;
		}

		@Override
		public void run() {

			try {
				int rs = commentsManager.CreateComment(eventId, accId, content);

				if (rs > 0) {
					handler.post(new CommentSuccess());
				} else {
					handler.post(new CommentFail());
				}
			} catch (JSONException e) {
				Utils.DismitDialog(dialog, EventDetailCommentActivity.this);
				Utils.showToast(context, res.getString(R.string.esn_global_Error), Toast.LENGTH_SHORT);
				e.printStackTrace();
			} catch (IOException e) {
				Utils.DismitDialog(dialog, EventDetailCommentActivity.this);
				Utils.showToast(context, res.getString(R.string.esn_global_ConnectionError), Toast.LENGTH_SHORT);
				e.printStackTrace();
			}

		}
	}

	private class CommentSuccess implements Runnable {

		@Override
		public void run() {
			Toast.makeText(context,
					res.getString(R.string.esn_eventDetail_commensuccess),
					Toast.LENGTH_SHORT).show();

			EditText txtComment = (EditText) findViewById(R.id.esn_comments_txtComment);
			txtComment.setText(null);

			dialog.show();
			lastScroll = 0;
			dialog.show();
			GetListComment();
		}

	}

	private class CommentFail implements Runnable {

		@Override
		public void run() {
			Toast.makeText(context,res.getString(R.string.esn_eventDetail_commenfail), 10).show();
		}
	}
}
