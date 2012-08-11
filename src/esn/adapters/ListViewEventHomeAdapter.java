package esn.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import esn.activities.R;
import esn.classes.Utils;
import esn.models.Events;

public class ListViewEventHomeAdapter extends CustomListAdapter<Events>{

private class ViewHolder {
		
		public TextView eventName;
		public TextView description;
		public TextView date;
		public ImageView image;
	}
	
	public ListViewEventHomeAdapter(Activity activity, ArrayList<Events> listEventHome) {
		
		super(activity, listEventHome, R.layout.home_event_layout_row, R.drawable.ic_event_default);		
	}	
	
	@Override
	protected Object createHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.eventName = (TextView) convertView.findViewById(R.id.esn_home_event_title);
		holder.description = (TextView) convertView.findViewById(R.id.esn_home_event_description);
		holder.date = (TextView) convertView.findViewById(R.id.esn_home_event_date);
		holder.image = (ImageView) convertView.findViewById(R.id.esn_home_event_listEventImage);
		return holder;
	}

	@Override
	protected void customRowView(Events rowBean, Object rowHolder) {
		ViewHolder holder = (ViewHolder) rowHolder;
		holder.eventName.setText(rowBean.Title);
		holder.description.setText(rowBean.Description);
		
		holder.date.setText(Utils.DateToStringByLocale(rowBean.DayCreate, 1));
		displayImage(rowBean.Picture, holder.image);
	}
}
