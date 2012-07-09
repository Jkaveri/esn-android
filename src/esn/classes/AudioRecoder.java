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

public class AudioRecoder {
	public static final int RECORDSTATE_RECORDING = AudioRecord.RECORDSTATE_RECORDING;
	public static final int RECORDSTATE_PAUSED = 192168;
	public static final int RECORDSTATE_STOPPED = AudioRecord.RECORDSTATE_STOPPED;

	public int SAMPLE_RATE = 8000;
	public int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	public int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	public int REC_BUFFER_SIZE;
	
	private int RECORDSTATE = 0;
	private AudioRecord audioRecord;
	private Thread th;
	private ByteArrayOutputStream bufferStream;
	private DataOutputStream dos;
	private Runnable run;

	public AudioRecoder() {
		REC_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
		audioRecord = new AudioRecord(AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, REC_BUFFER_SIZE);
		
		bufferStream = new ByteArrayOutputStream();
		dos = new DataOutputStream(bufferStream);
		
		run = new Runnable() {

			@Override
			public void run() {
				byte[] buffer = new byte[REC_BUFFER_SIZE]; 
				audioRecord.startRecording();				
				
				while (RECORDSTATE == RECORDSTATE_RECORDING) {
					int count = audioRecord.read(buffer, 0, REC_BUFFER_SIZE);
					if(count > 0){
						try {
							dos.write(buffer, 0, count);
						} catch (IOException e) {
							Log.e("AudioRecoder", "IOException writeShort() at output stream");
						}
					}
				}
			}
		};
	}

	public void startRecording() {
		if (RECORDSTATE != RECORDSTATE_RECORDING) {
			if (RECORDSTATE == RECORDSTATE_STOPPED) {
				bufferStream.reset();
			}
			RECORDSTATE = RECORDSTATE_RECORDING;
			th = new Thread(run);
			th.start();
		}
	}
	
	public void pauseRecording() {
		if(RECORDSTATE == RECORDSTATE_RECORDING){
			RECORDSTATE = RECORDSTATE_PAUSED;
			try {
				dos.flush();
			} catch (IOException e) {
				Log.e("AudioRecoder", "IOException flush stream data");
			}
			audioRecord.stop();
			th.interrupt();
			th = null;
		}
	}

	public void stopRecording() {
		if (RECORDSTATE != RECORDSTATE_STOPPED) {
			RECORDSTATE = RECORDSTATE_STOPPED;
			try {
				dos.flush();
				dos.close();
			} catch (IOException e) {
				Log.e("AudioRecorder", "IOException close output stream");
			}
			audioRecord.stop();
			if(th != null){
				th.interrupt();
				th = null;
			}
		}
	}

	public byte[] getBufferRecod() {
		return bufferStream.toByteArray();
	}
	
	public void resetBufferRecod(){
		if(bufferStream != null)
			bufferStream.reset();
	}

	public int getRecordState() {
		return RECORDSTATE;
	}
}