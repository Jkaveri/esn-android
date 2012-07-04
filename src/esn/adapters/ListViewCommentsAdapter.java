package esn.adapters;

import java.util.ArrayList;

import esn.activities.R;
import esn.models.Comments;
import esn.models.FriendsListsDTO;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewCommentsAdapter extends BaseAdapter{

	private Activity activity;
	
	private ArrayList<Comments> data;
	
	private static LayoutInflater inflater = null;
		
	private class ViewHolder {
		public TextView nameUser;
		public TextView comment;
		public TextView date;
	}
	
	public ListViewCommentsAdapter(Activity a, ArrayList<Comments> listComment) {
		this.activity = a;
		this.data = listComment;
		listComment = null;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
	}	
	
	public void add(ArrayList<Comments> listComment){
		for (Comments frd : listComment) {
			data.add(frd);
		}
		listComment = null;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return data.get(index);
	}

	@Override
	public long getItemId(int index) {
		// TODO Auto-generated method stub
		return index;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		
		View vi = convertView;
		
		final ViewHolder holder;
		
		if (convertView == null){
			vi = inflater.inflate(R.layout.listitem_row, null);
			holder = new ViewHolder();
			holder.nameUser = (TextView) vi.findViewById(R.id.txtViewTitle);
			holder.comment = (TextView) vi.findViewById(R.id.txtViewDescription);
			holder.date = (TextView) vi.findViewById(R.id.imgViewLogo);
			vi.setTag(holder);
		}else{
			holder = (ViewHolder) vi.getTag();
		}

		Comments bean = data.get(index);
		holder.nameUser.setText(bean.Name);
		holder.comment.setText(bean.Content);
		holder.date.setText(bean.DayCreate.toString());
		
		return vi;
	}
}
