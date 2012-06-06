package esn.adapters;

import java.util.ArrayList;

import esn.activities.R;
import esn.classes.Utils;
import esn.models.FriendsListsDTO;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewFriendsAdapter extends BaseAdapter {

	public ArrayList<Object> itemList;
	public Activity context;
	public LayoutInflater inflater;

	public ListViewFriendsAdapter(Activity context, ArrayList<Object> itemList) {
		super();

		this.context = context;
		this.itemList = itemList;

		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static class ViewHolder {
		ImageView imgViewLogo;
		TextView txtViewTitle;
		TextView txtViewDescription;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.listitem_row, null);

			holder.imgViewLogo = (ImageView) convertView
					.findViewById(R.id.imgViewLogo);
			holder.txtViewTitle = (TextView) convertView
					.findViewById(R.id.txtViewTitle);
			holder.txtViewDescription = (TextView) convertView
					.findViewById(R.id.txtViewDescription);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		FriendsListsDTO bean = (FriendsListsDTO) itemList.get(position);
		if(bean.avatarURL != null && !bean.avatarURL.equals("")){
			Bitmap bm =  Utils.getBitmapFromURL(bean.avatarURL);
			holder.imgViewLogo.setImageBitmap(bm);
		}else{
			holder.imgViewLogo.setImageResource(R.drawable.ic_no_avata);
		}
		
		holder.txtViewTitle.setText(bean.name);
		holder.txtViewDescription.setText("Chua co");

		return convertView;
	}

}