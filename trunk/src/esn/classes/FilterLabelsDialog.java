package esn.classes;

import java.util.ArrayList;
import java.util.List;

import esn.activities.R;
import esn.adapters.EsnListAdapterNoSub;
import esn.adapters.ListMultiChoiceAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class FilterLabelsDialog implements OnClickListener, OnCancelListener,
		OnDismissListener {
	private Context context;
	private ListMultiChoiceAdapter adapter;
	private ItemMenuOnClickListener clickHandler;
	private View view;
	private ListView listView;

	public FilterLabelsDialog(Context ctx) {
		context = ctx;
		adapter = new ListMultiChoiceAdapter();
		LayoutInflater inflate = LayoutInflater.from(context);
		 view = inflate.inflate(R.layout.filter_dialog, null);
		 listView = (ListView) view.findViewById(R.id.filterListView);
	}

	public Dialog createMenu(CharSequence title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(title);
		
		listView.setAdapter(adapter);
		String[] list = new String[10];
		boolean[] b = new boolean[10];
		builder.setMultiChoiceItems(list, b, new DialogInterface.OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// TODO Auto-generated method stub
				
			}
		});
		
		builder.setView(view);

		builder.setInverseBackgroundForced(true);
	
		AlertDialog dialog = builder.create();
		dialog.setOnCancelListener(this);
		dialog.setOnDismissListener(this);
		return dialog;
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int i) {
		EsnListItem item =(EsnListItem) adapter.getItem(i);
		if (clickHandler != null) {
			clickHandler.onClick(item.getId());
		}
	}

	public void addItem(CharSequence title, int icon, int id) {
		EsnListItem item = new EsnListItem();
		item.setTitle(title);
		item.setIcon(icon);
		item.setId(id);
		adapter.add(item);
	}

	@Override
	public void onCancel(DialogInterface dialogInterface) {

	}

	@Override
	public void onDismiss(DialogInterface dialogInterface) {
		// TODO Auto-generated method stub

	}

	/**
	 * Set menu onclick listener
	 * 
	 * @param itemMenuOnClickListener
	 */
	public void setOnClickListener(
			ItemMenuOnClickListener itemMenuOnClickListener) {
		clickHandler = itemMenuOnClickListener;

	}

	/**
	 * IconContextMenu On Click Listener interface
	 */
	public interface ItemMenuOnClickListener {
		public abstract void onClick(int menuId);
	}

}
