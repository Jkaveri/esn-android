package esn.adapters;

import java.util.ArrayList;

import esn.activities.R;
import esn.classes.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public abstract class CustomListAdapter<T> extends BaseAdapter {
	private Activity activity;
	private ArrayList<T> data;
	private static LayoutInflater inflater = null;
	private int layoutRow;
	private ImageLoader imageLoader;
	private int idIcon;

	public CustomListAdapter(Activity activity, ArrayList<T> list,
			int layoutRow, int idIcon) {
		this.activity = activity;
		this.data = list;
		this.layoutRow = layoutRow;
		list = null;
		this.idIcon = idIcon;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = new ImageLoader(activity.getApplicationContext());
		this.imageLoader.setDefaultEmptyImage(this.idIcon);
	}

	public CustomListAdapter(Activity activity, int layoutRow, int idIcon) {
		this.activity = activity;
		this.data = new ArrayList<T>();
		this.layoutRow = layoutRow;
		this.idIcon = idIcon;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = new ImageLoader(activity.getApplicationContext());
		this.imageLoader.setDefaultEmptyImage(this.idIcon);
	}

	public CustomListAdapter(Activity activity, ArrayList<T> list, int layoutRow) {
		this.activity = activity;
		this.data = list;
		list = null;
		this.idIcon = R.drawable.ic_no_photo;
		this.layoutRow = layoutRow;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void add(ArrayList<T> list) {
		int len = list.size();
		for (int i = 0; i < len; i++) {
			data.add(list.get(i));
		}
		list = null;
		this.notifyDataSetChanged();
	}

	public void add(T item) {
		data.add(item);
		item = null;
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
		T bean = data.get(index);
		final Object holder;
		if (convertView == null) {
			vi = inflater.inflate(layoutRow, null);
			holder = createHolder(vi);
			vi.setTag(holder);
		} else {
			holder = vi.getTag();
		}
		customRowView(bean, holder);
		return vi;
	}

	public void setDefaultEmptyImage(int idIcon) {
		if (imageLoader == null) {
			this.imageLoader = new ImageLoader(activity.getApplicationContext());
		}
		this.idIcon = idIcon;
		this.imageLoader.setDefaultEmptyImage(this.idIcon);
	}

	protected abstract void customRowView(T rowBean, Object rowHolder);

	protected abstract Object createHolder(View convertView);

	public void displayImage(String src, ImageView image) {
		if (imageLoader == null) {
			this.imageLoader = new ImageLoader(activity.getApplicationContext());
		}
		imageLoader.displayImage(src, activity, image);
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void stopThread() {
		if (imageLoader != null)
			this.imageLoader.stopThread();
	}

	public void clearCache() {
		if (imageLoader != null)
			this.imageLoader.clearCache();
	}

	public ArrayList<T> getData() {
		return data;
	}

	public void setData(ArrayList<T> data) {
		this.data = data;
		this.notifyDataSetChanged();
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public void setLayoutRow(int layoutRow) {
		this.layoutRow = layoutRow;
	}

	public int getLayoutRow() {
		return layoutRow;
	}

	public int getDefaultEmptyImage() {
		return idIcon;
	}

}
