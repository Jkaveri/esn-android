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
	private DataOutputStream dos;
	private Runnable run;

	public AudioRecoder() {
		REC_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
		try {
			clearBuffer();
		} catch (IOException e) {
			Log.e("AudioRecoder", "IOException clearBuffer() at output stream");
		}
		run = new Runnable() {

			@Override
			public void run() {
				byte[] buffer = new byte[REC_BUFFER_SIZE];
				AudioRecord audioRecord = new AudioRecord(AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, REC_BUFFER_SIZE);
				audioRecord.startRecording();				
				while (RECORDSTATE == RECORDSTATE_RECORDING) {
					int count = audioRecord.read(buffer, 0, REC_BUFFER_SIZE);
					if(count > 0){
						try {
							dos.write(buffer, 0, count);
						} catch (IOException e) {
							Log.e("AudioRecoder", "IOException write() at output stream");
						}
					}
				}				
				audioRecord.stop();
				audioRecord.release();
			}
		};
	}

	public void startRecording() throws IOException {
		if (RECORDSTATE != RECORDSTATE_RECORDING) {
			if (RECORDSTATE == RECORDSTATE_STOPPED) {
				bufferStream.reset();
			}
			RECORDSTATE = RECORDSTATE_RECORDING;
			th = new Thread(run);
			th.start();
		}
	}
	
	public void pauseRecording() throws IOException {
		if(RECORDSTATE == RECORDSTATE_RECORDING){
			RECORDSTATE = RECORDSTATE_PAUSED;
			dos.flush();
			if(th != null){
				th.interrupt();
				th = null;
			}
		}
	}

	public void stopRecording() throws IOException {
		if (RECORDSTATE != RECORDSTATE_STOPPED) {
			RECORDSTATE = RECORDSTATE_STOPPED;
			dos.flush();
			dos.close();
			if(th != null){
				th.interrupt();
				th = null;
			}
		}
	}

	public byte[] getBufferRecod() throws IOException {
		bufferStream.flush();
		return bufferStream.toByteArray();
	}
	
	public void clearBuffer() throws IOException{
		if(bufferStream != null){
			bufferStream.close();
			dos.close();
		}
		bufferStream = new ByteArrayOutputStream();
		dos = new DataOutputStream(bufferStream);
	}

	public int getRecordState() {
		return RECORDSTATE;
	}
}