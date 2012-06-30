package esn.adapters;

import java.util.ArrayList;

import esn.activities.R;
import esn.classes.ImageLoader;
import esn.models.FriendsListsDTO;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewFriendsAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<FriendsListsDTO> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	
	private class ViewHolder {
		public TextView title;
		public TextView disc;
		public ImageView image;
	}

	public ListViewFriendsAdapter(Activity a, ArrayList<FriendsListsDTO> listFrd) {
		this.activity = a;
		this.data = listFrd;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = new ImageLoader(activity.getApplicationContext());
		this.imageLoader.setDefaultEmptyImage(R.drawable.ic_no_avata);
	}
	
	public void add(ArrayList<FriendsListsDTO> listFrd){
		for (FriendsListsDTO frd : listFrd) {
			data.add(frd);
		}
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int index) {
		return data.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		View vi = convertView;
		final ViewHolder holder;
		if (convertView == null){
			vi = inflater.inflate(R.layout.listitem_row, null);
			holder = new ViewHolder();
			holder.title = (TextView) vi.findViewById(R.id.txtViewTitle);
			holder.disc = (TextView) vi.findViewById(R.id.txtViewDescription);
			holder.image = (ImageView) vi.findViewById(R.id.imgViewLogo);
			vi.setTag(holder);
		}else{
			holder = (ViewHolder) vi.getTag();
		}

		FriendsListsDTO bean = data.get(index);
		holder.title.setText(bean.Name);
		holder.disc.setText("Phone: " + bean.Phone);//So ban chung
		imageLoader.displayImage(bean.Avatar, activity, holder.image);
		return vi;
	}
}