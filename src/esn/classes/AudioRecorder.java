/*
 * By lnbienit@gmail.com
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 */


package esn.classes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class AudioRecorder {
	public static final int RECORDSTATE_STOPPED = 0;
	public static final int RECORDSTATE_RECORDING = 1;

	public static int SAMPLE_RATE = 8000;
	public static int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	public static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	public int SILENCE_THRESHOLD = 600;
	public int LONG_SILENCE = 20;
	
	public int REC_BUFFER_SIZE;
	
	private int RECORDSTATE = RECORDSTATE_STOPPED;
	private Thread th;
	private Runnable run;
	private ByteArrayOutputStream bufferStream;
	public RecordListener callBack;
	
	private boolean isSilence = false;
	private int timeOut = 0;
	private boolean isCallBackOnStarting = false;
	private String TAG = "AudioRecorder";
	private Codec codec;
	
	private class DetectTask extends TimerTask {
		@Override
	    public void run() {
	    	if(isSilence){
				timeOut++;
				if(timeOut >= LONG_SILENCE){
					callBack.onSilenting();
					reset();
				}
			}
	    }
	}
	
	public void silenceCall(){		
		isSilence = true;
	}
	
	public void speakingCall(){
		isSilence = false;
		timeOut = 0;
		if(!isCallBackOnStarting){
			isCallBackOnStarting = true;
			callBack.onSpeaking();
		}
	}
	
	public void reset(){
		timeOut = 0;
		isCallBackOnStarting = false;
		isSilence = false;
	}
	
	public AudioRecorder(RecordListener recHandler) {
		callBack = recHandler;
		REC_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
		codec = Codec.instance(30);
		int truncated;
        
        if (REC_BUFFER_SIZE == AudioRecord.ERROR_BAD_VALUE) {
        	 Log.i(TAG, "GET REC_BUFFER_SIZE IS AudioRecord.ERROR_BAD_VALUE");
        }
        
        // 480 bytes for 30ms(y mode)
        truncated = REC_BUFFER_SIZE % 480;
        if (truncated != 0) {
        	REC_BUFFER_SIZE += 480 - truncated;
            Log.i(TAG, "Extend buffer to " + REC_BUFFER_SIZE);
        }
        
		bufferStream = new ByteArrayOutputStream();		
		
		run = new Runnable() {

			@Override
			public void run() {
				byte[] buffer = new byte[REC_BUFFER_SIZE];
				byte[] bufferEncode = new byte[REC_BUFFER_SIZE];
				DataOutputStream dos = new DataOutputStream(bufferStream);
				AudioRecord record = new AudioRecord(AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, REC_BUFFER_SIZE);
				Timer timerDetect = new Timer();
				boolean recording = false;
				float tempFloatBuffer[] = new float[3];
				int tempIndex = 0;
				int totalReadBytes = 0;
				byte totalByteBuffer[] = new byte[60 * 44100 * 2];
				
				callBack.onStartingRecord();//Goi handler khi chuan bi ghi am
				
				//Kich hoat lang nghe noi hoac khong noi				
				timerDetect.scheduleAtFixedRate(new DetectTask(), 1, 50);
				
				//Bat dau ghi am
				record.startRecording();
				try {
					while (RECORDSTATE == RECORDSTATE_RECORDING) {
						float totalAbsValue = 0.0f;
						short sample = 0;
						
						int count = record.read(buffer, 0, REC_BUFFER_SIZE);
						
						if (count == AudioRecord.ERROR_INVALID_OPERATION) {
							Log.i(TAG, "READ BUFFER_SIZE IS AudioRecord.ERROR_INVALID_OPERATION");
                        } else if (count == AudioRecord.ERROR_BAD_VALUE) {
                        	Log.i(TAG, "READ BUFFER_SIZE IS AudioRecord.ERROR_BAD_VALUE");
                        } else if (count == AudioRecord.ERROR) {
                        	Log.i(TAG, "READ BUFFER_SIZE IS AudioRecord.ERROR");
                        }
						
						//iLBC encode block 30msec
						int encount = codec.encode(buffer, 0, count, bufferEncode, 0);
						dos.write(bufferEncode, 0, encount);
						
						//dos.write(buffer, 0, count);

						
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
							//Log.i(TAG, "[1] Chua noi");//Chua noi
							tempIndex++;
						}

						if (temp >SILENCE_THRESHOLD && recording == false) {
							//Log.i(TAG, "[2] Bat dau noi");//Bat dau noi
							recording = true;
							continue;
						}

						if ((temp >= 0 && temp <=  SILENCE_THRESHOLD) && recording == true) {
							silenceCall();//Dang ngung noi
							//Log.i(TAG, "Dang im lang");
						}else{
							speakingCall();//Dang noi
							//Log.i(TAG, "Dang noi");
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
					Log.e(TAG, "IOException write() at output stream");
				}finally{
					try {
						dos.flush();
						dos.close();
					} catch (IOException e){
						Log.e(TAG, "IOException close at output stream");
					}
				}
				
				//Destroy
				record.stop();
				record.release();
				record = null;
				timerDetect.cancel();
				timerDetect = null;
				
				//Giai phong bo nho
				tempFloatBuffer = null;
				totalByteBuffer = null;
				buffer = null;
				bufferEncode = null;
				dos = null;
				
				//Reset nhan dang co dang noi hay khong
				reset();
				
				//Phat sinh su kien trong thread truoc khi ket thuc
				callBack.onStopedRecord();
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
				Log.e(TAG, "IOException close at buffer stream");
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
				Log.e(TAG , "IOException close at buffer stream");
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