package esn.adapters;

import java.util.ArrayList;

import esn.activities.R;
import esn.classes.EsnListItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


public class ListMultiChoiceAdapter extends android.widget.BaseAdapter {
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
			view = li.inflate(R.layout.list_item_checkbox, parent, false);
			// set title
			TextView title = (TextView) view.findViewById(R.id.item_checkbox_text);
			if (title != null){
				title.setText(item.getTitle());
				title.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(), 0, 0, 0);
				title.setCompoundDrawablePadding(8);
			}
			CheckBox check = (CheckBox) view.findViewById(R.id.item_checkbox_checkbox);
			check.setChecked(item.isChecked());
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
