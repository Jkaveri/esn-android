package esn.activities;

import java.io.IOException;

import org.json.JSONException;

import com.actionbarsherlock.view.MenuItem;

import esn.classes.ImageLoader;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetail extends Activity {
	private Intent data;
	private ProgressDialog dialog;
	private int eventId;
	public Handler handler;
	private ImageLoader loader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_detail);
		loader = new ImageLoader(this.getApplicationContext());
		data = getIntent();
		handler = new Handler();
		dialog = new ProgressDialog(this);
		dialog.setTitle(getResources().getString(R.string.esn_global_loading));
		dialog.setMessage(getResources().getString(
				R.string.esn_global_pleaseWait));
		dialog.show();
		eventId = data.getIntExtra("id", 0);
		new GetEventDetailThread(eventId).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Like").setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("Dislike").setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	public void btnLikeCLicked(View view) {
		new LikeEventThread().start();
	}

	private class LikeEventThread extends Thread {
		@Override
		public void run() {
			EventsManager manager = new EventsManager();
			Sessions session = Sessions.getInstance(EventDetail.this);
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
			/*
			 * TextView tvLike = (TextView)
			 * findViewById(R.id.esn_eventDetail_tvLikeCount);
			 * tvLike.setText(like);
			 */
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
				Events event = null;
				try {
					event = manager.retrieve(eventId);
					if (manager
							.isLiked(
									eventId,
									Sessions.getInstance(EventDetail.this).currentUser.AccID)) {
						EventDetail.this.runOnUiThread(new Runnable() {

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
									Sessions.getInstance(EventDetail.this).currentUser.AccID)) {
						EventDetail.this.runOnUiThread(new Runnable() {

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
				EventDetail.this.runOnUiThread(new GetDetailSuccess(event));
			}
		}
	}

	private class GetDetailSuccess implements Runnable {
		private Events event;

		public GetDetailSuccess(Events event) {
			this.event = event;
		}

		@Override
		public void run() {
			if (event != null) {
				TextView tvTitle = (TextView) findViewById(R.id.esn_eventDetail_title);
				TextView tvDescription = (TextView) findViewById(R.id.esn_eventDetail_description);
				TextView tvDateCreated = (TextView) findViewById(R.id.esn_eventDetail_dateCreated);
				ImageView icEventType = (ImageView) findViewById(R.id.esn_eventDetail_iconEventType);

				final ImageView imgEvent = (ImageView) findViewById(R.id.esn_eventDetail_image);
				imgEvent.setImageResource(R.drawable.ic_no_avata);

				tvTitle.setText(event.Title);
				tvDateCreated.setText(Utils.DateToStringByLocale(event.DayCreate,1));
				tvDescription.setText(event.Description);
				icEventType.setImageResource(EventType.getIconId(
						event.EventTypeID, event.getLevel()));
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
				dialog.dismiss();
			}
		}
	}
}