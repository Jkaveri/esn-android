package esn.adapters;

import java.util.ArrayList;

import esn.activities.R;
import esn.classes.EsnListItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


public class ListMultiChoiceAdapter extends android.widget.BaseAdapter {
	ArrayList<EsnListItem> items = new ArrayList<EsnListItem>();
	private ListMultiChoiceAdapter.onItemCheckedListener checkedHandler;
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
    static class ViewHolder{
    	public TextView text;
    	public CheckBox checkbox;
    }
	@Override
	public View getView(int index, View view, ViewGroup parent) {
		ViewHolder viewHolder = null;
		EsnListItem item = items.get(index);
		if (view == null) {
			
			LayoutInflater li = LayoutInflater.from(parent.getContext());
			view = li.inflate(R.layout.list_item_checkbox, parent, false);
			//setup view holder
			viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.item_checkbox_text);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.item_checkbox_checkbox);
			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(checkedHandler!=null){
						checkedHandler.onCheckedChanged(buttonView,isChecked);
					}
					
				}
			});
			view.setTag(viewHolder);
			view.setTag(R.id.item_checkbox_text, viewHolder.text);
			view.setTag(R.id.item_checkbox_checkbox,viewHolder.checkbox);
			
		}else{
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.checkbox.setTag(index);
		
		
		viewHolder.text.setText(item.getTitle());
		viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(), 0, 0, 0);
		viewHolder.text.setCompoundDrawablePadding(8);
		
		viewHolder.checkbox.setChecked(item.isChecked());
		
		
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

	public void setOnItemCheckedListener(ListMultiChoiceAdapter.onItemCheckedListener listener){
		checkedHandler = listener;
	}
	
	public interface onItemCheckedListener{
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
	}
}
