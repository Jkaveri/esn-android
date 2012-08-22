package esn.adapters;

import java.util.ArrayList;

import esn.activities.R;
import esn.classes.EsnListItem;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EsnListAdapterNoSub extends ArrayAdapter<EsnListItem> {

	private ArrayList<EsnListItem> items = new ArrayList<EsnListItem>();
	private Context context;

	public EsnListAdapterNoSub(Context context, int textViewResourceId,
			EsnListItem[] items) {
		super(context, textViewResourceId, items);
		this.context = context;
		for (int i = 0; i < items.length; i++) {
			this.items.add(items[i]);
		}
	}
	public EsnListAdapterNoSub(Context context, int textViewResourceId, ArrayList<EsnListItem> items){
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
	}

	@Override
	public EsnListItem getItem(int index) {
		// TODO Auto-generated method stub
		return items.get(index);
	}

	@Override
	public int getPosition(EsnListItem item) {
		int i = 0;
		for (EsnListItem it : items) {
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
		return items.size();
	}

	@Override
	public View getDropDownView(int index, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater li = LayoutInflater.from(parent.getContext());
			view = li.inflate(R.layout.spinner_dropdown_item, null);
		}
		EsnListItem item = items.get(index);

		if (item != null) {
			TextView tv;
			if (view.getClass() == TextView.class) {
				tv = (TextView) view;
			} else {
				tv = (TextView) view.findViewById(android.R.id.text1);
			}
			tv.setText(item.getTitle());

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
			view = li.inflate(R.layout.spinner_item, null);
		}
		EsnListItem item = items.get(position);

		if (item != null) {
			TextView tv;
			if (view.getClass() == TextView.class) {
				tv = (TextView) view;
			} else {
				tv = (TextView) view.findViewById(android.R.id.text1);
			}
			tv.setText(item.getTitle());
			Drawable icon = context.getResources().getDrawable(item.getIcon());
			
			tv.setCompoundDrawablePadding(5);
			tv.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		}

		return view;
	}
}
