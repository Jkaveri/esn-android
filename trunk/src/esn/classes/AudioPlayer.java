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
	public static final int PLAYSTATE_PLAYING = AudioTrack.PLAYSTATE_PLAYING;
	public static final int PLAYSTATE_STOPPED = AudioTrack.PLAYSTATE_STOPPED;
	
	public int SAMPLE_RATE;
	public int CHANNEL_CONFIG;
	public int AUDIO_FORMAT;
	
	private int MODE = AudioTrack.MODE_STREAM;
	private int STREAM_TYPE = AudioManager.STREAM_MUSIC;
	private static int TR_BUFFER_SIZE;
	
	private AudioTrack track;
	private Runnable run;
	private Thread th;
	private DataInputStream is;
	private ByteArrayInputStream bs;

	public AudioPlayer() {
		run = new Runnable() {

			@Override
			public void run() {
				byte[] buffer = new byte[TR_BUFFER_SIZE];
				try {
					while (track.getPlayState() != PLAYSTATE_STOPPED) {
						int count = is.read(buffer, 0, TR_BUFFER_SIZE);
						track.write(buffer, 0, count);
						if(count == -1){
							stop();
							break;
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
		stop();
		bs = new ByteArrayInputStream(bufferPCM);
		is = new DataInputStream(bs);
	}
	
	public void loadBufferStream(InputStream ips) {
		stop();
		if(bs != null)
			bs = null;
		is = new DataInputStream(ips);
	}
	
	public void setDefaultConfig(){
		SAMPLE_RATE = 8000;
		CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
		AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	}
	
	public void prepare(){
		if(track != null){
			track.release();
			track = null;
		}
		TR_BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
		track = new AudioTrack(STREAM_TYPE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, TR_BUFFER_SIZE, MODE);
	}

	public void play() {
		if(track.getPlayState() != PLAYSTATE_PLAYING){
			track.play();
			if(th == null){
				th = new Thread(run);
				th.start();
			}
		}
	}

	public void stop() {
		if(track.getPlayState() != PLAYSTATE_STOPPED){
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
	}
	
	public void clearBuffer(){
		if(track != null){
			if(track.getPlayState() != PLAYSTATE_STOPPED){
				track.stop();
			}
			track.release();
			track = null;
		}
		
		if (th != null) {
			th.interrupt();
			th = null;
		}
		
		try{
			if(bs != null){
				bs.close();
			}
			
			if(is != null){
				is.close();
			}
			
			bs	= null;
			is = null;
		} catch (IOException e) {
			Log.e("AudioPlayer", "IOException read buffer");
		}
	}

	public int getPlayState() {
		return track.getPlayState();
	}
}
