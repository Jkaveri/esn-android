package esn.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import esn.activities.R;
import esn.models.FriendsManager;
import esn.models.NotificationDTO;

public class ListViewNotificationAdapter extends CustomListAdapter<NotificationDTO>{
	
	public ListViewNotificationAdapter(Activity activity,ArrayList<NotificationDTO> list) {
		super(activity, list, R.layout.notification_row);
	}

	private class ViewHolder 
	{
		public TextView name;
		public TextView disc;
		public TextView date;
		public ImageView image;
		public Button btYes;
		public Button btNo;
	}
	
	@Override
	protected void customRowView(NotificationDTO rowBean, Object rowHolder) {
		ViewHolder holder = (ViewHolder) rowHolder;
		//holder.name.setText(rowBean.SendName);		
		String mss = rowBean.Description + " " + rowBean.SendName;
		holder.disc.setText(mss);//So ban chung
		holder.date.setText(rowBean.DateCreate.toString());//So ban chung
		displayImage(rowBean.Image, holder.image);
		
		if(rowBean.TargetType==1)
		{
			holder.btYes.setVisibility(View.INVISIBLE);
			holder.btNo.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected Object createHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		//holder.name = (TextView) convertView.findViewById(R.id.esn_notification_name);
		holder.disc = (TextView) convertView.findViewById(R.id.esn_notification_message);
		holder.date = (TextView) convertView.findViewById(R.id.esn_notification_date);
		holder.image = (ImageView) convertView.findViewById(R.id.esn_notification_avatar);
		holder.btYes = (Button)convertView.findViewById(R.id.esn_notification_btactionyes);
		holder.btNo = (Button)convertView.findViewById(R.id.esn_notification_btactionyes);
		holder.btYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread() {
				
				}.start();
			}
		});
		
		holder.btNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread() {
				
				}.start();
			}
		});
		return holder;
	}
}
