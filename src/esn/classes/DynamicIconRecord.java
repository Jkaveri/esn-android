package esn.classes;

import java.util.Timer;
import java.util.TimerTask;

import esn.activities.R;
import android.os.Handler;
import android.widget.ImageButton;

public class DynamicIconRecord {
	public ImageButton btnRecord;
	public Handler handler;
	private Timer tmrDynIcon;
	
	public DynamicIconRecord(ImageButton btnRecord){
		this.btnRecord = btnRecord;
		handler = new Handler();
	}
	
	//Thay doi icon lam cho button nhap nhay
	private class IconTaskRec extends TimerTask{
		private boolean type = true;
		
		@Override
		public void run() {
			if (type) {
				postIcon(R.drawable.ic_mic_stop_lig);
				type = false;
			} else {
				postIcon(R.drawable.ic_mic_stop_red);
				type = true;
			}
		}
		
	}
	
	private class IconTaskLoad extends TimerTask{
		private int key = 1;
		
		@Override
		public void run() {
			switch (key) {
			case 1:
				postIcon(R.drawable.rec_load_1);
				break;
			case 2:
				postIcon(R.drawable.rec_load_2);
				break;
			case 3:
				postIcon(R.drawable.rec_load_3);
				break;
			case 4:
				postIcon(R.drawable.rec_load_4);
				break;
			case 5:
				postIcon(R.drawable.rec_load_5);
				break;
			case 6:
				postIcon(R.drawable.rec_load_6);
				break;
			case 7:
				postIcon(R.drawable.rec_load_7);
				break;
			case 8:
				postIcon(R.drawable.rec_load_8);
				break;
			case 9:
				postIcon(R.drawable.rec_load_9);
				break;
			case 10:
				postIcon(R.drawable.rec_load_10);
				break;
			case 11:
				postIcon(R.drawable.rec_load_11);
				break;
			case 12:
				postIcon(R.drawable.rec_load_12);
				break;
			}
			
			if(key == 12)
				key = 1;
			else
				key++;
		}		
	}
	
	private void postIcon(final int idIcon){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				btnRecord.setImageResource(idIcon);
			}
		});
	}	
	
	public void startIconLoading(){		
		if(tmrDynIcon != null){
			tmrDynIcon.cancel();
		}
		tmrDynIcon = new Timer();
		tmrDynIcon.scheduleAtFixedRate(new IconTaskLoad(), 0, 100);
	}
	
	public void stopIconRecord(){
		if(tmrDynIcon != null){
			tmrDynIcon.cancel();
		}
		tmrDynIcon = null;
		postIcon(R.drawable.ic_mic_record);
	}
	
	public void startIconRecord(){
		if(tmrDynIcon != null){
			tmrDynIcon.cancel();
		}
		tmrDynIcon = new Timer();
		tmrDynIcon.scheduleAtFixedRate(new IconTaskRec(), 0, 1000);
	}
	
	public void destroy(){
		if(tmrDynIcon != null){
			tmrDynIcon.cancel();
			tmrDynIcon = null;
		}
		btnRecord = null;
		handler = null;
	}
}
