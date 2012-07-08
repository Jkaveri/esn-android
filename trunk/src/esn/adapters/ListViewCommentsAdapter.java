package esn.adapters;

import java.util.ArrayList;

import esn.activities.R;
import esn.classes.Utils;
import esn.models.Comments;
import esn.models.FriendsListsDTO;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewCommentsAdapter extends CustomListAdapter<Comments>{

	private class ViewHolder {
		
		public TextView name;
		public TextView comment;
		public TextView date;
		public ImageView image;
	}
	
	public ListViewCommentsAdapter(Activity activity, ArrayList<Comments> listCm) {
		
		super(activity, listCm, R.layout.comment_layout_row, R.drawable.ic_no_avata);
		
	}	
	
	@Override
	protected void customRowView(Comments rowBean, Object rowHolder) {
		
		ViewHolder holder = (ViewHolder) rowHolder;
		holder.name.setText(rowBean.ProfileName);
		holder.comment.setText(rowBean.Content);		
		String day = Utils.DateToStringByLocale(rowBean.DayCreate, 1);		
		holder.date.setText("("+day+")");
		displayImage(rowBean.ProfileAvatar, holder.image);
	}

	@Override
	protected Object createHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) convertView.findViewById(R.id.esn_comment_listName);
		holder.comment = (TextView) convertView.findViewById(R.id.esn_comment_listComment);
		holder.date = (TextView) convertView.findViewById(R.id.esn_comment_listDate);
		holder.image = (ImageView) convertView.findViewById(R.id.esn_comment_listAvatar);
		return holder;
	}
}
