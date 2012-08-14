package esn.adapters;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import esn.activities.R;
import esn.classes.Sessions;
import esn.models.Users;
import esn.models.UsersManager;

public class ListViewFindFriendAdapter extends CustomListAdapter<Users> {

	private class ViewHolder {
		public TextView name;
		public TextView email;
		public ImageView avatar;
		public TextView uid;
		public Button btnAdd;
	}

	public ListViewFindFriendAdapter(Activity activity, ArrayList<Users> list,
			int layoutRow) {
		super(activity, list, layoutRow, R.drawable.ic_no_avata);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void customRowView(final Users rowBean, Object rowHolder) {
		final ViewHolder holder = (ViewHolder) rowHolder;
		holder.name.setText(rowBean.Name);
		holder.email.setText(rowBean.Email);
		holder.btnAdd.setOnClickListener(new View.OnClickListener() {

			private ProgressDialog dialog;

			@Override
			public void onClick(View v) {
				final Activity act = ListViewFindFriendAdapter.this
						.getActivity();
				final Resources res = act.getResources();

				dialog = new ProgressDialog(act);
				dialog.setTitle(res.getString(R.string.esn_global_loading));
				dialog.setMessage(res.getString(R.string.esn_global_pleaseWait));
				dialog.show();
				new Thread() {
					private boolean result;

					@Override
					public void run() {
						UsersManager manager = new UsersManager();
						try {
							result = manager.AddFriend(
									Sessions.getInstance(act).currentUser.AccID,
									rowBean.AccID);
							// succes
							
							act.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									dialog.dismiss();
									holder.btnAdd.setVisibility(View.GONE);
									if(result){
										Toast.makeText(act,res.getString(R.string.esn_global_success), Toast.LENGTH_SHORT).show();
									}else{
										Toast.makeText(act,res.getString(R.string.esn_global_success), Toast.LENGTH_SHORT).show();
									}
								}
							});
							super.run();
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}.start();
			}
		});
		// holder.uid.setText(rowBean.uid);
		displayImage(rowBean.Avatar, holder.avatar);

	}

	@Override
	protected Object createHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) convertView
				.findViewById(R.id.esn_findfriend_name);
		holder.email = (TextView) convertView
				.findViewById(R.id.esn_findfriend_email);
		holder.avatar = (ImageView) convertView
				.findViewById(R.id.esn_findfriend_avatar);
		holder.btnAdd = (Button) convertView
				.findViewById(R.id.esn_findfriend_btadd);
		return holder;
	}
}
