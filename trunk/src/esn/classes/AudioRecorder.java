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

	public int SAMPLE_RATE = 16000;
	public int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	public int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	public int REC_BUFFER_SIZE;
	public int SILENCE_THRESHOLD = 600;
	
	private int RECORDSTATE = RECORDSTATE_STOPPED;
	private Thread th;
	private Runnable run;
	private ByteArrayOutputStream bufferStream;
	
	private Thread thAutoStop;
	private Runnable runDetect;
	private int timeOut = 0;
	private boolean isSilence = false;
	private IRecordCallBack callBack;
	
	private void silenceCall(){		
		isSilence = true;
	}
	
	private void speakingCall(){
		isSilence = false;
		timeOut = 0;
	}

	public AudioRecorder(IRecordCallBack autoStop) {
		this.callBack = autoStop;
		
		REC_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
		bufferStream = new ByteArrayOutputStream();
		
		runDetect = new Runnable() {
			
			@Override
			public void run() {
				while(RECORDSTATE == RECORDSTATE_RECORDING){
					if(isSilence){
						timeOut++;
						if(timeOut >= 20){
							Log.i("AudioRecorder", "Auto stop recording");
							timeOut = 0;
							stopRecording();
							callBack.autoStopRecording();
						}else{
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								Log.i("AudioRecorder", "Thread Detect on destroy");
							}
						}
					}
				}
			}
		};
		
		run = new Runnable() {

			@Override
			public void run() {
				byte[] buffer = new byte[REC_BUFFER_SIZE];
				DataOutputStream dos = new DataOutputStream(bufferStream);
				AudioRecord record = new AudioRecord(AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, REC_BUFFER_SIZE);
				
				boolean recording = false;
				float tempFloatBuffer[] = new float[3];
				int tempIndex = 0;
				int totalReadBytes = 0;
				byte totalByteBuffer[] = new byte[60 * 44100 * 2];
				
				record.startRecording();
				try {
					while (RECORDSTATE == RECORDSTATE_RECORDING) {
						float totalAbsValue = 0.0f;
						short sample = 0;
						
						int count = record.read(buffer, 0, REC_BUFFER_SIZE);
						if(count > 0){
							dos.write(buffer, 0, count);
						}
						
						///////////////////////PHAN TICH DANG XEM DANG NOI HAY NGUNG//////////////////
						// Analyze Sound.
						for (int i = 0; i < REC_BUFFER_SIZE; i += 2) {
							sample = (short) ((buffer[i]) | buffer[i + 1] << 8);
							totalAbsValue += Math.abs(sample) / (count / 2);
						}

						// Analyze temp buffer.
						tempFloatBuffer[tempIndex % 3] = totalAbsValue;
						float temp = 0.0f;
						for (int i = 0; i < 3; ++i)
							temp += tempFloatBuffer[i];

						if ((temp >= 0 && temp <= SILENCE_THRESHOLD) && recording == false) {
							//Log.i("AudioRecorder", "[1] Chua noi");//Chua noi
							tempIndex++;
						}

						if (temp >SILENCE_THRESHOLD && recording == false) {
							//Log.i("AudioRecorder", "[2] Bat dau noi");//Bat dau noi
							recording = true;
							continue;
						}

						if ((temp >= 0 && temp <=  SILENCE_THRESHOLD) && recording == true) {
							silenceCall();//Dang ngung noi
							//Log.i("AudioRecorder", "Dang im lang");
						}else{
							speakingCall();//Dang noi
							//Log.i("AudioRecorder", "Dang noi");
						}

						// -> Recording sound here.
						for (int i = 0; i < count; i++){
							totalByteBuffer[totalReadBytes + i] = buffer[i];
						}
						
						totalReadBytes += count;
						tempIndex++;
						//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//
						
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
				
				//Giai phong bo nho
				tempFloatBuffer = null;
				totalByteBuffer = null;
				dos = null;
				
				//Reset nhan dang co dang noi hay khong
				timeOut = 0;//Reset nhan dang co dang noi hay khong
				isSilence = false;
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
				thAutoStop.interrupt();
				thAutoStop = null;
			}
			RECORDSTATE = RECORDSTATE_RECORDING;
			th = new Thread(run);
			th.start();
			thAutoStop = new Thread(runDetect);
			thAutoStop.start();
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
			thAutoStop.interrupt();
			thAutoStop = null;
		}
	}

	public int getRecordState() {
		return RECORDSTATE;
	}
}