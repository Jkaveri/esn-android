package esn.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import esn.activities.R;
import esn.models.FindFriendDTO;

public class ListViewFindFriendAdapter extends CustomListAdapter<FindFriendDTO>{

	private class ViewHolder {
		public TextView name;
		public TextView email;
		public ImageView avatar;
		public TextView uid;
	}
	
	public ListViewFindFriendAdapter(Activity activity,
			ArrayList<FindFriendDTO> list, int layoutRow) {
		super(activity, list, layoutRow, R.drawable.ic_no_avata);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void customRowView(FindFriendDTO rowBean, Object rowHolder) {
		ViewHolder holder = (ViewHolder) rowHolder;
		holder.name.setText(rowBean.Name);
		holder.email.setText(rowBean.Email);
		holder.uid.setText(rowBean.uid);
		displayImage(rowBean.Avatar, holder.avatar);
		
	}
	@Override
	protected Object createHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) convertView.findViewById(R.id.esn_findfriend_name);
		holder.email = (TextView) convertView.findViewById(R.id.esn_findfriend_email);
		holder.avatar = (ImageView) convertView.findViewById(R.id.esn_findfriend_avatar);
		return holder;
	}	
}
