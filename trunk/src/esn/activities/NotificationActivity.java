package esn.activities;

import java.util.ArrayList;


import esn.adapters.ListViewFriendsAdapter;
import esn.adapters.ListViewNotificationAdapter;
import esn.classes.Sessions;
import esn.models.FriendsListsDTO;
import esn.models.FriendsManager;
import esn.models.NotificationDTO;
import esn.models.UsersManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class NotificationActivity extends Activity {

	private ListView lstNofication;
	private ListViewNotificationAdapter adapter;
	private NotificationActivity context;
	private Handler handler;
	private int lastScroll = 0;
	private ProgressDialog dialog;
	private int page = 1;
	private int accounID = 0;
	Sessions sessions;
	Resources res;

	esn.models.NotificationManager notificationManager = new esn.models.NotificationManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.notification);

		handler = new Handler();
		context = this;

		sessions = Sessions.getInstance(context);
		adapter = new ListViewNotificationAdapter(NotificationActivity.this,
				new ArrayList<NotificationDTO>());

		setupNotificationList();
		res = getResources();
		lstNofication = (ListView) findViewById(R.id.esn_notification_listnotification);
		lstNofication.setAdapter(adapter);
		lstNofication.setOnScrollListener(new OnScrollListener() {

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
					context.loadNotificationList(8, page);
					Toast.makeText(context, "Load data", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

	}

	private void setupNotificationList() {
		dialog = new ProgressDialog(this);
		dialog.setTitle(this.getResources().getString(
				R.string.esn_global_loading));
		dialog.setMessage("Waiting ....");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		Thread thr = new Thread(new Runnable() {

			@Override
			public void run() {

				FriendsManager frdMng = new FriendsManager();
				try {
					final ArrayList<NotificationDTO> itemList = notificationManager
							.getNotification(sessions.currentUser.AccID);
					handler.post(new Runnable() {

						@Override
						public void run() {
							adapter.add(itemList);
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

	private void loadNotificationList(final int pageSize, final int pageIndex) {

		Thread thr = new Thread(new Runnable() {
			@Override
			public void run() {

				FriendsManager frdMng = new FriendsManager();
				
			}
		});

		thr.start();
	}

	
	
	@Override
	public void onDestroy() {
		if (adapter != null) {
			adapter.stopThread();
			adapter.clearCache();
		} else {
			lstNofication.setAdapter(null);
		}
		super.onDestroy();
	}
	private class LoadNotificationThread extends Thread{
		private int pageNum;
		private int pageSize;
		public LoadNotificationThread(int pageNum, int pageSize){
			this.pageNum = pageNum;
			this.pageSize = pageSize;
		}
		@Override
		public void run() {
			try {
				final ArrayList<NotificationDTO> itemList = notificationManager
						.getNotification(accounID);
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
	}
}
