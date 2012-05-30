package esn.adapters;

import java.util.ArrayList;

import esn.activities.R;
import esn.models.EsnListItem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EsnListAdapter extends BaseAdapter {
	ArrayList<EsnListItem> items = new ArrayList<EsnListItem>();

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int index) {
		return items.get(index);
	}

	@Override
	public long getItemId(int index) {
		return items.get(index).getId();
	}

	@Override
	public View getView(int index, View view, ViewGroup parent) {
		if (view == null) {
			EsnListItem item = items.get(index);
			LayoutInflater li = LayoutInflater.from(parent.getContext());
			view = li.inflate(R.layout.list_item, parent, false);
			// set icon
			ImageView icon = (ImageView) view.findViewById(R.id.esn_listIcon);
			if (icon != null)
				icon.setImageDrawable(view.getResources().getDrawable(
						item.getIcon()));
			// set title
			TextView title = (TextView) view.findViewById(R.id.esn_listTitle);
			if (title != null)
				title.setText(item.getTitle());
			// set subtitle
			TextView subtitle = (TextView) view
					.findViewById(R.id.esn_listSubtitle);
			if (subtitle != null)
				subtitle.setText(item.getSubtitle());
		}
		return view;
	}

	public void add(EsnListItem item) {
		items.add(item);
	}
	public void remove(EsnListItem item){
		items.remove(item);
	}
	public void remove(int index){
		items.remove(index);
	}
}
