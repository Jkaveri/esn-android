/*
 * By lnbienit@gmail.com
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 */


package esn.classes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class AudioRecorder {
	public static final int RECORDSTATE_STOPPED = 0;
	public static final int RECORDSTATE_RECORDING = 1;
	public static final int RECORDSTATE_PAUSED = 2;

	public int SAMPLE_RATE = 16000;
	public int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	public int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	public int REC_BUFFER_SIZE;
	
	private int RECORDSTATE = RECORDSTATE_STOPPED;
	private Thread th;
	private ByteArrayOutputStream bufferStream;
	private Runnable run;

	public AudioRecorder() {
		REC_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
		bufferStream = new ByteArrayOutputStream();
		
		run = new Runnable() {

			@Override
			public void run() {
				byte[] buffer = new byte[REC_BUFFER_SIZE];
				DataOutputStream dos = new DataOutputStream(bufferStream);
				AudioRecord record = new AudioRecord(AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, REC_BUFFER_SIZE);
				record.startRecording();
				try {
					while (RECORDSTATE == RECORDSTATE_RECORDING) {
						int count = record.read(buffer, 0, REC_BUFFER_SIZE);
						if(count > 0){
							dos.write(buffer, 0, count);
						}
					}
				} catch (IOException e) {
					Log.e("Audiorecorder", "IOException write() at output stream");
				}finally{
					try {
						dos.flush();
						dos.close();
					} catch (IOException e){
						Log.e("Audiorecorder", "IOException close at output stream");
					}
				}
				record.stop();
				record.release();
			}
		};
	}

	public void startRecording(){
		if (RECORDSTATE != RECORDSTATE_RECORDING) {
			if (RECORDSTATE == RECORDSTATE_STOPPED) {
				bufferStream.reset();
			}
			if(th != null){
				th.interrupt();
				th = null;
			}
			RECORDSTATE = RECORDSTATE_RECORDING;
			th = new Thread(run);
			th.start();
		}
	}
	
	public void pauseRecording(){
		if(RECORDSTATE == RECORDSTATE_RECORDING){
			RECORDSTATE = RECORDSTATE_PAUSED;
		}
	}

	public void stopRecording(){
		if (RECORDSTATE != RECORDSTATE_STOPPED) {
			RECORDSTATE = RECORDSTATE_STOPPED;
		}
	}

	public byte[] getBufferRecord(){
		try {
			bufferStream.flush();
		} catch (IOException e) {
			return null;
		}
		return bufferStream.toByteArray();
	}
	
	public void clearBuffer(){
		if(bufferStream != null){
			try {
				bufferStream.close();
			} catch (IOException e) {
				Log.e("Audiorecorder", "IOException close at buffer stream");
			}
		}
		bufferStream = new ByteArrayOutputStream();
	}
	
	public void release(){
		RECORDSTATE = RECORDSTATE_STOPPED;
		if(bufferStream != null){
			try {
				bufferStream.close();
			} catch (IOException e) {
				Log.e("Audiorecorder", "IOException close at buffer stream");
			}
			bufferStream = null;
		}
		
		if(th != null){
			th.interrupt();
			th = null;
		}
	}

	public int getRecordState() {
		return RECORDSTATE;
	}
}