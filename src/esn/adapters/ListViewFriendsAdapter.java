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

	public ListViewFriendsAdapter(Activity a, ArrayList<FriendsListsDTO> listFrd) {
		this.activity = a;
		this.data = listFrd;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.listitem_row, null);

		TextView title = (TextView) vi.findViewById(R.id.txtViewTitle);
		ImageView image = (ImageView) vi.findViewById(R.id.imgViewLogo);
		TextView discrip = (TextView) vi.findViewById(R.id.txtViewDescription);
		title.setText(data.get(position).name);
		discrip.setText("Descript " + data.get(position).accID);//So ban chung
		imageLoader.displayImage(data.get(position).avatarURL, activity, image);
		return vi;
	}
}