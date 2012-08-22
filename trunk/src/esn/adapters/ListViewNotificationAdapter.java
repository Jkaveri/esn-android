package esn.adapters;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.NotificationManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import esn.activities.R;
import esn.models.AppEnums;
import esn.models.FriendsManager;
import esn.models.NotificationDTO;
import esn.models.UsersManager;

public class ListViewNotificationAdapter extends
		CustomListAdapter<NotificationDTO> {
	private Confirm addFriendThread;

	public ListViewNotificationAdapter(Activity activity,
			ArrayList<NotificationDTO> list) {
		super(activity, list, R.layout.notification_row);
	}

	private class ViewHolder {
		public TextView name;
		public TextView disc;
		public TextView date;
		public ImageView image;
		public Button btYes;
		public Button btNo;
	}

	@Override
	protected void customRowView(final NotificationDTO rowBean, Object rowHolder) {
		ViewHolder holder = (ViewHolder) rowHolder;
		// holder.name.setText(rowBean.SendName);
		String mss = rowBean.Description + " " + rowBean.SendName;
		holder.disc.setText(mss);// So ban chung
		holder.date.setText(rowBean.DateCreate.toString());// So ban chung
		displayImage(rowBean.Image, holder.image);

		if (rowBean.TargetTypeName.equals("Relation")) {
			holder.btYes.setVisibility(View.INVISIBLE);
			holder.btNo.setVisibility(View.INVISIBLE);
			holder.btYes.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					new Confirm(rowBean.TargetID).start();
				}
			});

			holder.btNo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new NotConfirm(rowBean.TargetID);
				}
			});
		} else {
			holder.btYes.setVisibility(View.GONE);
			holder.btNo.setVisibility(View.GONE);
		}

	}

	@Override
	protected Object createHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		// holder.name = (TextView)
		// convertView.findViewById(R.id.esn_notification_name);
		holder.disc = (TextView) convertView
				.findViewById(R.id.esn_notification_message);
		holder.date = (TextView) convertView
				.findViewById(R.id.esn_notification_date);
		holder.image = (ImageView) convertView
				.findViewById(R.id.esn_notification_avatar);
		holder.btYes = (Button) convertView
				.findViewById(R.id.esn_notification_btactionyes);
		holder.btNo = (Button) convertView
				.findViewById(R.id.esn_notification_btactionyes);

		return holder;
	}

	private class Confirm extends Thread {
		private int relationID;

		public Confirm(int relationId) {
			this.relationID = relationId;
		}

		@Override
		public void run() {
			UsersManager userManager = new UsersManager();
			try {
				userManager.ConfirmAddFriendRequest(relationID);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class NotConfirm extends Thread {

		private int relationID;

		public NotConfirm(int relationId) {
			this.relationID = relationId;
		}

		@Override
		public void run() {
			UsersManager userManager = new UsersManager();
			try {
				userManager.NotConfirmAddFriendRequest(relationID);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
