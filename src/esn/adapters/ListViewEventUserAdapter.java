package esn.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import esn.activities.R;
import esn.classes.Utils;
import esn.models.EventType;
import esn.models.Events;

public class ListViewEventUserAdapter extends CustomListAdapter<Events>{

	private class ViewHolder {
		
		public TextView eventName;
		public TextView DateCreate;
		public TextView Like;
		public TextView Dislike;
		public ImageView image;
	}
	
	public ListViewEventUserAdapter(Activity activity, ArrayList<Events> list) {
		super(activity, list,R.layout.event_user_row);
	}
	
	@Override
	protected void customRowView(Events rowBean, Object rowHolder) {
		
		ViewHolder holder = (ViewHolder) rowHolder;
		
		holder.eventName.setText(EventType.GetName(rowBean.EventTypeID, holder.eventName.getResources()));
		
		String day = Utils.DateToStringByLocale(rowBean.DayCreate, 1);
		
		holder.DateCreate.setText(day);
		
		holder.Like.setText(String.valueOf(rowBean.Like));
		
		holder.Dislike.setText(String.valueOf(rowBean.Dislike));
		
		//holder.Comment.setText(rowBean.Comment);
		
		displayImage(rowBean.Picture, holder.image);
	}
	
	@Override
	protected Object createHolder(View convertView) {
		
		ViewHolder holder = new ViewHolder();
		
		holder.eventName = (TextView) convertView.findViewById(R.id.esn_setting_profile_list_eventname);
		
		holder.DateCreate = (TextView) convertView.findViewById(R.id.esn_setting_profile_list_eventdate);
		
		holder.Like = (TextView) convertView.findViewById(R.id.esn_setting_profile_list_eventlike);
		
		holder.Dislike = (TextView) convertView.findViewById(R.id.esn_setting_profile_list_eventdislike);
		
		//holder.Comment = (TextView) convertView.findViewById(R.id.esn_setting_profile_comment);
		
		holder.image = (ImageView) convertView.findViewById(R.id.esn_setting_profile_list_eventimage);
		
		return holder;
	}	
}