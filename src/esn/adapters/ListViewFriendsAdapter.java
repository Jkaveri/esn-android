package esn.adapters;

import java.util.ArrayList;
import esn.activities.R;
import esn.models.Users;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewFriendsAdapter extends CustomListAdapter<Users> {
	private class ViewHolder {
		public TextView title;
		public ImageView image;
	}
	
	public ListViewFriendsAdapter(Activity activity, ArrayList<Users> listFrd) {
		super(activity, listFrd, R.layout.listitem_row, R.drawable.ic_no_avata);
	}
	
	@Override
	protected void customRowView(Users rowBean, Object rowHolder) {
		ViewHolder holder = (ViewHolder) rowHolder;
		holder.title.setText(rowBean.Name);
		displayImage(rowBean.Avatar, holder.image);
	}

	@Override
	protected Object createHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(R.id.txtViewTitle);
		holder.image = (ImageView) convertView.findViewById(R.id.imgViewLogo);
		return holder;
	}
	
}