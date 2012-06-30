package esn.classes;

import java.io.IOException;

import esn.activities.R;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

public class MyImageLoader {
	
	private int imgDefault = R.drawable.stub;
	
	public MyImageLoader(){
	}
	
	public MyImageLoader(int noImg) {
		imgDefault = noImg;
	}
	
	public void displayImage(final ImageView image, final String src){
		final Handler handler = new Handler();
		Thread thr = new Thread(new Runnable() {
					
			@Override
			public void run() {

				try {
					final Bitmap bm = Utils.getBitmapFromURL(src);
					handler.post(new Runnable() {

						@Override
						public void run() {
							image.setImageBitmap(bm);
						}
					});
				} catch (IOException e) {
					image.setImageResource(imgDefault);
					e.printStackTrace();
				}
			}
		});

		thr.start();
	}
}
