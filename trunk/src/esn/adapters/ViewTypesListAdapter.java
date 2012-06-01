package esn.adapters;

import esn.activities.R;
import esn.classes.ListNavigationItem;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ViewTypesListAdapter extends ArrayAdapter<ListNavigationItem> {

	private ListNavigationItem[] items;
	private Context context;

	public ViewTypesListAdapter(Context context, int textViewResourceId,
			ListNavigationItem[] items) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
	}

	@Override
	public ListNavigationItem getItem(int position) {
		// TODO Auto-generated method stub
		return items[position];
	}

	@Override
	public int getPosition(ListNavigationItem item) {
		int i = 0;
		for (ListNavigationItem it : items) {
			if (it.equals(item)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.length;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater li = LayoutInflater.from(parent.getContext());
			view = li.inflate(R.layout.sherlock_spinner_dropdown_item, null);
		}
		ListNavigationItem item = items[position];

		if (item != null) {
			TextView tv;
			if (view.getClass() == TextView.class) {
				tv = (TextView) view;
			} else {
				tv = (TextView) view.findViewById(android.R.id.text1);
			}
			tv.setText(item.getText());

			Drawable icon = context.getResources().getDrawable(item.getIcon());
			
			tv.setCompoundDrawablePadding(8);
			tv.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

		}
		return view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater li = LayoutInflater.from(parent.getContext());
			view = li.inflate(R.layout.sherlock_spinner_item, null);
		}
		ListNavigationItem item = items[position];

		if (item != null) {
			TextView tv;
			if (view.getClass() == TextView.class) {
				tv = (TextView) view;
			} else {
				tv = (TextView) view.findViewById(android.R.id.text1);
			}
			tv.setText(item.getText());
			Drawable icon = context.getResources().getDrawable(item.getIcon());
			
			tv.setCompoundDrawablePadding(5);
			tv.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		}

		return view;
	}
}
