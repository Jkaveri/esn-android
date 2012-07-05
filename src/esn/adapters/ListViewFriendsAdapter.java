package esn.adapters;

import java.util.ArrayList;
import esn.activities.R;
import esn.models.FriendsListsDTO;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewFriendsAdapter extends CustomListAdapter<FriendsListsDTO> {
	private class ViewHolder {
		public TextView title;
		public TextView disc;
		public ImageView image;
	}
	
	public ListViewFriendsAdapter(Activity activity, ArrayList<FriendsListsDTO> listFrd) {
		super(activity, listFrd, R.layout.listitem_row, R.drawable.ic_no_avata);
	}

	@Override
	protected void customRowView(FriendsListsDTO rowBean, Object rowHolder) {
		ViewHolder holder = (ViewHolder) rowHolder;
		holder.title.setText(rowBean.Name);
		holder.disc.setText("Phone: " + rowBean.Phone);//So ban chung
		displayImage(rowBean.Avatar, holder.image);
	}

	@Override
	protected Object createHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(R.id.txtViewTitle);
		holder.disc = (TextView) convertView.findViewById(R.id.txtViewDescription);
		holder.image = (ImageView) convertView.findViewById(R.id.imgViewLogo);
		return holder;
	}
}