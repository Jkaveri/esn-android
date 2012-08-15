package esn.activities;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.facebook.android.Util;

import esn.adapters.ListViewEventUserAdapter;
import esn.classes.Sessions;
import esn.classes.Utils;
import esn.models.Events;
import esn.models.EventsManager;
import esn.models.Users;
import esn.models.UsersManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class FriendsActivity extends Activity {

	FriendsActivity context;
	private static final String LOG_TAG = "FriendsActivity";
	private ProgressDialog dialog;

	public Handler handler;

	EventsManager eventsManager = new EventsManager();

	Sessions session;

	UsersManager usersManager = new UsersManager();

	private ListView listUserEvent;

	private ListViewEventUserAdapter adapter;

	private int page = 1;

	Resources res;

	private int lastScroll = 0;

	Users users = new Users();

	protected ArrayList<Events> itemList;

	public final static int CODE_REQUEST_SET_FILTER = 2;

	public final static int CODE_REQUEST_FRIEND_INFO = 3;

	private int friendId = 0;

	Intent data;
	private static final int PAGE_SIZE = 8;

	private int isFriend = 0;
	private UsersManager manager = new UsersManager();
	private boolean result = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.friends);

		context = this;

		res = getResources();

		data = this.getIntent();

		friendId = this.getIntent().getIntExtra("accountID", 0);

		handler = new Handler();

		session = Sessions.getInstance(context);
		adapter = new ListViewEventUserAdapter(FriendsActivity.this,
				new ArrayList<Events>());

		listUserEvent = (ListView) findViewById(R.id.esn_setting_profile_listeventuser);

		ShowInforFriend();

		listUserEvent.setAdapter(adapter);

		listUserEvent.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int scroll = firstVisibleItem + visibleItemCount;
				boolean acti = scroll == totalItemCount - 1;
				if (acti && scroll > lastScroll) {
					lastScroll = scroll;
					page++;
					new GetListEventThread(page, PAGE_SIZE).start();
				}
			}
		});
	}

	public void DevelopClicked(View view) {
		Toast.makeText(context,
				res.getString(R.string.esn_global_function_developing),
				Toast.LENGTH_SHORT).show();
	}

	public void FriendActionClicked(View v) {
		
			new Thread() {
				public void run() {

					try {
						Boolean rs = false;
						
						if (isFriend==1) {
							rs = manager.UnFriend(session.currentUser.AccID,friendId);
						} else if(isFriend==0) {
							rs = manager.AddFriend(session.currentUser.AccID,friendId);
						}
						else
						{
							return;
						}
						
						result = rs;
						
						if (rs == true) {
							
							handler.post(new Runnable() {
								
								public void run() {
									
									Button bt = (Button) findViewById(R.id.esn_setting_isfriend);

									if (isFriend==1) {
										
										Toast.makeText(context,
												res.getString(R.string.btn_Friends_Lists_Diaglog_unfriendsuccess),Toast.LENGTH_SHORT).show();
										bt.setText(res.getString(R.string.esn_friend_addfriend));
										isFriend=0;
									} else if(isFriend==2){
										
										Toast.makeText(
												context,
												res.getString(R.string.btn_Friends_Lists_Diaglog_addfriendsuccess),
												Toast.LENGTH_SHORT).show();
										bt.setText(res.getString(R.string.esn_friend_unfriend));
										isFriend=2;
									}
									else
									{
										Toast.makeText(
												context,
												res.getString(R.string.btn_Friends_Lists_Diaglog_addfriendsuccess),
												Toast.LENGTH_SHORT).show();
										bt.setText(res.getString(R.string.esn_friend_waiting));
										isFriend=2;
									}
								}
							});
						} else {
							handler.post(new Runnable() {

								@Override
								public void run() {

									Toast.makeText(
											context,
											res.getString(R.string.btn_Friends_Lists_Diaglog_unfriendnotsuccess),
											Toast.LENGTH_SHORT).show();

								}
							});

						}
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				};

			}.start();
		
	}

	public void MessageClicked(View v) {
		/*
		 * data = new Intent(context, Notification.class); startActivity(data);
		 * overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		 */
		return;
	}

	public void FriendsClicked(View v) {

		if (isFriend == 1) {
			data = new Intent(context, FriendListActivity.class);
			data.putExtra("accountID", friendId);
			startActivity(data);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		} else {
			return;
		}

	}

	public void PersonalClicked(View v) {
		data = new Intent(context, EditProfileActivity.class);
		startActivity(data);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void ShowInforFriend() {

		dialog = new ProgressDialog(this);
		dialog.setTitle(this.getResources().getString(
				R.string.esn_global_loading));
		dialog.setMessage(res.getString(R.string.esn_global_pleaseWait));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		ShowProfileThread showProfileThread = new ShowProfileThread();
		showProfileThread.start();

	}

	public class ShowProfileThread extends Thread {

		public ShowProfileThread() {

		}

		public void run() {

			if (friendId != 0) {

				try {

					users = usersManager.RetrieveById(friendId);

				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}

				try {

					isFriend = usersManager.GetRelationStatus(session.currentUser.AccID, friendId);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				handler.post(new Runnable() {

					@Override
					public void run() {

						Button bt = (Button) findViewById(R.id.esn_setting_isfriend);

						if (isFriend==1) {
							bt.setText(res
									.getString(R.string.esn_friend_unfriend));
						} else if(isFriend==2) {
							bt.setText(res.getString(R.string.esn_friend_waiting));
						}
						else
						{
							bt.setText(res.getString(R.string.esn_friend_addfriend));							
						}
					}
				});

				if (users != null) {
					handler.post(new Runnable() {

						@Override
						public void run() {

							TextView txtName = (TextView) findViewById(R.id.esn_setting_profile_name);
							TextView txtGender = (TextView) findViewById(R.id.esn_setting_profile_gender);
							TextView txtAddress = (TextView) findViewById(R.id.esn_setting_profile_address);

							final String url = users.Avatar;

							if (url != null) {
								new Thread() {
									public void run() {

										Bitmap bitmap = null;

										try {

											bitmap = Utils
													.getBitmapFromURL(url);

										} catch (IOException e) {

											Log.d(LOG_TAG, e.getMessage());
											e.printStackTrace();
										}

										handler.post(new SetAvatar(bitmap));

									};
								}.start();
							}

							txtName.setText(users.Name);

							if (users.Gender == true)
								txtGender.setText(res
										.getString(R.string.esn_register_rdbMale));
							else
								txtGender.setText(res
										.getString(R.string.esn_register_rdbFemale));

							String ad = users.Address;

							if (users.Street != null
									&& users.Street.length() > 0)
								ad = ad + ", " + users.Street;

							if (users.District != null
									&& users.District.length() > 0)
								ad = ad + ", " + users.District;

							if (users.City != null && users.City.length() > 0)
								ad = ad + ", " + users.City;

							if (users.Country != null
									&& users.Country.length() > 0)
								ad = ad + ", " + users.Country;

							txtAddress.setText(ad);

							new GetListEventThread(1, PAGE_SIZE).start();
							
							if (dialog != null)
								dialog.dismiss();
						}
					});

				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();

							Util.showAlert(
									context,
									res.getResourceName(R.string.esn_global_Error),
									res.getResourceEntryName(R.string.esn_global_ConnectionError));
						}
					});
				}
			} else {
				Utils.showToast(FriendsActivity.this,
						res.getString(R.string.esn_global_Error),
						Toast.LENGTH_SHORT);
			}

		}
	}

	public class SetAvatar implements Runnable {

		private Bitmap bitmap;

		public SetAvatar(Bitmap bm) {
			bitmap = bm;
		}

		@Override
		public void run() {
			ImageView avatar = (ImageView) findViewById(R.id.esn_setting_profile_avataruser);

			avatar.setImageBitmap(bitmap);
		}

	}

	private class GetListEventThread extends Thread {
		protected static final String LOG_TAG = "GetListEventThread";
		private int pageNum;
		private int pageSize;

		public GetListEventThread(int pageNum, int pageSize) {
			this.pageNum = pageNum;
			this.pageSize = pageSize;
		}

		@Override
		public void run() {

			EventsManager eventsManager = new EventsManager();

			try {

				itemList = eventsManager.getEventUserList(pageNum, pageSize,
						friendId);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						for (Events event : itemList) {
							Log.d(LOG_TAG, event.Title);
							adapter.add(event);
						}
						adapter.notifyDataSetChanged();
					}
				});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
	}
}
