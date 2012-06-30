package esn.classes;

import java.io.IOException;
import java.util.ArrayList;

import esn.activities.R;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

public class MyImageLoader {
	private int imgDefault = R.drawable.stub;
	private ArrayList<ImageView> images;
	private ArrayList<String> srcs;
	private boolean loading = false;
	private int index = 0;
	
	public MyImageLoader(){
		images = new ArrayList<ImageView>();
		srcs = new ArrayList<String>();
	}
	
	public MyImageLoader(int noImg) {
		imgDefault = noImg;
		images = new ArrayList<ImageView>();
		srcs = new ArrayList<String>();
	}
	
	public void displayImage(ImageView image, String src){
		images.add(image);
		srcs.add(src);
		loadToImage();
	}
	
	protected void loadToImage(){
		if(!loading){
			if(index < images.size()){
				loading = true;
				final Handler handler = new Handler();
				Thread thr = new Thread(new Runnable() {
							
					@Override
					public void run() {
		
						try {
							
							final Bitmap bm = Utils.getBitmapFromURL(srcs.get(index));
							handler.post(new Runnable() {
		
								@Override
								public void run() {
									images.get(index).setImageBitmap(bm);
									index++;
									loading = false;
									loadToImage();
								}
							});
						} catch (IOException e) {
							images.get(index).setImageResource(imgDefault);
							e.printStackTrace();
						}
					}
				});
		
				thr.start();
			}
		}else{
			index = 0;
			images.clear();
			srcs.clear();
		}
	}
}
