package esn.classes;

import java.util.ArrayList;
import java.util.List;

import esn.activities.R;
import esn.adapters.EsnListAdapterNoSub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;

public class FilterLabelsDialog implements OnClickListener, OnCancelListener,
		OnDismissListener {
	private Context context;
	private EsnListAdapterNoSub adapter;
	private ItemMenuOnClickListener clickHandler;

	public FilterLabelsDialog(Context ctx) {
		context = ctx;
		adapter = new EsnListAdapterNoSub(ctx, R.layout.sherlock_spinner_item,
				new ArrayList<EsnListItem>());
	}

	public Dialog createMenu(CharSequence title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(title);
		builder.setAdapter(adapter, this);
		
		builder.setInverseBackgroundForced(true);
		AlertDialog dialog = builder.create();
		dialog.setOnCancelListener(this);
		dialog.setOnDismissListener(this);
		return dialog;
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int i) {
		EsnListItem item = adapter.getItem(i);
		if (clickHandler != null) {
			clickHandler.onClick(item.getId());
		}
	}

	public void addItem(Resources res, CharSequence title, int icon, int id) {
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
