package esn.classes;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioPlayer {
	public static final int IS_PLAYING = 1;
	public static final int IS_PAUSED = 2;
	public static final int IS_STOP = 0;
	
	private static final int FREQUENCY = 8000;
	private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
	private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final int MODE = AudioTrack.MODE_STREAM;
	private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
	private static int TR_BUFFER_SIZE;
	
	private AudioTrack track;
	private Runnable run;
	private Thread th;
	private DataInputStream is;
	private ByteArrayInputStream bs;
	private int status = 0;

	public AudioPlayer() {
		TR_BUFFER_SIZE = AudioTrack.getMinBufferSize(FREQUENCY, CHANNEL_CONFIG, AUDIO_ENCODING);
		track = new AudioTrack(STREAM_TYPE, FREQUENCY, CHANNEL_CONFIG, AUDIO_ENCODING, TR_BUFFER_SIZE, MODE);

		run = new Runnable() {

			@Override
			public void run() {
				byte[] buffer = new byte[TR_BUFFER_SIZE];
				track.play();
				try {
					while (status != IS_STOP) {
						if(status == IS_PLAYING){
							int count = is.read(buffer, 0, TR_BUFFER_SIZE);
							track.write(buffer, 0, count);
							if(count == -1){
								stop();
								break;
							}
						}
						else{
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								Log.i("AudioPlayer", "Thread on destroy");
							}
						}
					}
				} catch (IOException e) {
					Log.e("AudioPlayer", "IOException read buffer");
				}
			}
		};
		th = null;
	}

	public void loadBufferPCM(byte[] bufferPCM) {
		if(status != IS_STOP)
			stop();
		bs = new ByteArrayInputStream(bufferPCM);
		is = new DataInputStream(bs);
	}
	
	public void loadBufferStream(InputStream ips) {
		if(status != IS_STOP){
			stop();
			if(bs != null)
				bs = null;
		}
		is = new DataInputStream(ips);
	}

	public void play() {
		if(status != IS_PLAYING){
			status = IS_PLAYING;
			if(th == null){
				th = new Thread(run);
				th.start();
			}
		}
	}

	public void pause() {
		if(status == IS_PLAYING){
			status = IS_PAUSED;
			track.pause();
		}
	}

	public void stop() {
		status = IS_STOP;
		if (th != null) {
			th.interrupt();
			th = null;
		}
		
		try{
			if(bs != null)
				bs.close();
			is.close();
		} catch (IOException e) {
			Log.e("AudioPlayer", "IOException read buffer");
		}
		
		track.stop();
	}

	public int getStatus() {
		return status;
	}
}
