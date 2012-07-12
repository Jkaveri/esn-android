/*
 * By lnbienit@gmail.com
 */

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
	public static final int PLAYSTATE_PLAYING = 0;
	public static final int PLAYSTATE_STOPPED = 1;
	
	public int SAMPLE_RATE;
	public int CHANNEL_CONFIG;
	public int AUDIO_FORMAT;
	
	private int MODE = AudioTrack.MODE_STREAM;
	private int STREAM_TYPE = AudioManager.STREAM_MUSIC;
	private static int TR_BUFFER_SIZE;
	private int PLAYSTATE = PLAYSTATE_STOPPED;
	
	private Runnable run;
	private Thread th;
	private InputStream ips;
	private ByteArrayInputStream bis;

	public AudioPlayer() {
		run = new Runnable() {

			@Override
			public void run() {
				playOutsiteTask();
			}
		};
	}

	public void playOutsiteTask() {
		AudioTrack track = new AudioTrack(STREAM_TYPE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, TR_BUFFER_SIZE, MODE);
		byte[] buffer = new byte[TR_BUFFER_SIZE];
		DataInputStream is;
		
		if(bis != null)
			is = new DataInputStream(bis);
		else
			is = new DataInputStream(ips);
		
		track.play();
		PLAYSTATE = PLAYSTATE_PLAYING;
		try {
			while (PLAYSTATE != PLAYSTATE_STOPPED) {
				int count = is.read(buffer, 0, TR_BUFFER_SIZE);
				track.write(buffer, 0, count);
				if(count == -1){
					PLAYSTATE = PLAYSTATE_STOPPED;//Stop
				}
			}
		} catch (IOException e) {
			Log.e("AudioPlayer", "IOException read buffer");
		} finally{
			try {
				is.close();
			} catch (IOException e) {
				Log.e("AudioPlayer", "IOException close input tream");
			}
		}
		track.stop();
		track.release();
		track = null;
	}

	public void loadBufferPCM(byte[] bufferPCM) {
		release();
		bis = new ByteArrayInputStream(bufferPCM);
	}
	
	public void loadBufferStream(InputStream ips) {
		release();
		this.ips = ips;
	}
	
	public void setDefaultConfig(){
		SAMPLE_RATE = AudioRecorder.SAMPLE_RATE;
		CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
		AUDIO_FORMAT = AudioRecorder.AUDIO_FORMAT;
	}
	
	public void prepare(){
		TR_BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
	}

	public void play() {
		if(PLAYSTATE != PLAYSTATE_PLAYING){
			if (th != null) {
				th.interrupt();
				th = null;
			}
			th = new Thread(run);
			th.start();
		}
	}

	public void stop() {
		if(PLAYSTATE != PLAYSTATE_STOPPED){
			PLAYSTATE = PLAYSTATE_STOPPED;
		}
	}
	
	
	/*
	 * Close all stream and destroy thread
	 */
	public void release(){
		PLAYSTATE = PLAYSTATE_STOPPED;
		try{
			if(bis != null){
				bis.close();
				bis	= null;
			}
			
			if(ips != null){
				ips.close();
				ips = null;
			}
			
		} catch (IOException e) {
			Log.e("AudioPlayer", "IOException read buffer");
		}
		if (th != null) {
			th.interrupt();
			th = null;
		}
	}

	public int getPlayState() {
		return PLAYSTATE;
	}
}
