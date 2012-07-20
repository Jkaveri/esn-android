/*
 * By lnbienit@gmail.com 
 */
package esn.classes;

import java.io.IOException;
import java.io.InputStream;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.util.Log;
import esn.activities.R;
import esn.models.S2TResult;

public class VoiceManager {
	private S2TParser s2tParser;
	private WAVFormatReader wavReader;
	private AudioRecorder recorder;
	private AudioPlayer player;
	private WAVFormatConver wavConver;

	private Thread thSendWs;
	private AudioWebService auWs;
	private Runnable runSendWs;
	private VoiceListener callBack;
	
	protected Resources resource;
	
	public VoiceManager(Resources resource) {
		s2tParser = new S2TParser();
		recorder = new AudioRecorder(new RecordListener() {

			@Override
			public void onSpeaking() {
				//Log.i("VoiceManager", "On Speaking");
			}

			@Override
			public void onSilenting() {
				recorder.stopRecording();//Goi ham nay se phat sinh su kien onStopingRecord()
				//Log.i("VoiceManager", "On Silenting");
			}

			@Override
			public void onStartingRecord() {
				beep(R.raw.record_start, true);//beep trong thread record
			}

			@Override
			public void onStopedRecord() {
				sendDataToServer();//Gui du lieu den server dau tien, ham nay tu chay rieng 1 thread nen se da
				beep(R.raw.record_stop, false);//Beep ket thuc chay mot thread moi
				callBack.onStopedRecord();//Goi su kien stop handler
			}
		});
		
		player = new AudioPlayer();
		wavConver = new WAVFormatConver();
		wavReader = new WAVFormatReader();
		auWs = new AudioWebService();
		
		this.resource = resource;
		
		wavConver.setDefaultWAVFormat();
		
		runSendWs = new Runnable() {
			
			@Override
			public void run() {
				try {
					runSendWS();
				} catch (IOException e) {
					Log.e("AudioManager", "IOException, call runSendWS()", e);
				}
			}
		};
		
	}
	
	public void setVoiceListener(VoiceListener listener){
		this.callBack = listener;
	}
	
	protected boolean beep(int soundId, boolean isInsiteThread){
		boolean ok = true;
		InputStream typeStream = resource.openRawResource(soundId);
		wavReader.setBuffer(typeStream);
		if(wavReader.read()){
			byte[] bufType = wavReader.getData();
			wavReader.clearBuffer();//giai phong bo nho
			setPlayerConfig();
			player.loadBufferPCM(bufType);
			bufType = null;
			if(isInsiteThread){
				player.playOutsiteTask();//Play thong thuong, neu goi ham nay nen bo trong thread
			}else{
				player.play();//Play trong mot thread moi
			}
		}else{
			ok = false;
		}
		try {
			typeStream.close();
		} catch (IOException e) {
			Log.e("AudioManager", "Beep close tream fail");
		}
		typeStream = null;//giai phong bo nho
		return ok;
	}
	
	private void runSendWS() throws IOException{
		byte[] recordBuf = recorder.getBufferRecord();
		recorder.clearBuffer();//giai phong bo nho
		Log.i("AudioManager", "Data record length: " + recordBuf.length);
		
//		wavConver.setBuffer(recordBuf);
//		recordBuf = null;//giai phong bo nho
//		wavConver.prepare();
//		wavConver.conver();
//		   
//		byte[] buf = wavConver.getWAVData();
//		wavConver.clearBuffer();//giai phong bo nho
		
		S2TResult result = auWs.send(recordBuf);
		//Log.i("AudioManager", "Result: " + result.getResult());
		s2tParser.parse(result.getResult());//Gui du lieu len class cha
		callBack.onS2TPostBack(s2tParser);
	}
	
	public void stopRecording(){
		recorder.stopRecording();
	}
	
	public void startRecording(){
		recorder.startRecording();
	}
	
	private void sendDataToServer(){// Gui du lieu ghi am xuong server
		if(thSendWs != null){
			thSendWs.interrupt();
			thSendWs = null;
		}
		thSendWs = new Thread(runSendWs);
		thSendWs.start();
	}
		
	private void setPlayerConfig(){
		if(wavReader.getChannels() == 1){
			player.CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
		}
		else{
			player.CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO;
		}
		
		player.SAMPLE_RATE = (int) wavReader.getSampleRate();
		
		if(wavReader.getBitsPerSample() == 16){
			player.AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
		}
		else if(wavReader.getBitsPerSample() == 8){
			player.AUDIO_FORMAT = AudioFormat.ENCODING_PCM_8BIT;
		}
		
		player.prepare();
	}
	
	private byte[] getBufferSoundLib(int fileID){
		InputStream ipStream = resource.openRawResource(fileID);
		wavReader.setBuffer(ipStream);
		if(!wavReader.read()){
			wavReader.clearBuffer();//giai phong bo nho
			try {
				ipStream.close();
			} catch (IOException e) {
				Log.e("AudioManager", "Khong close File Stream trong Sound Lib");
			}
			ipStream = null;//giai phong bo nho
			Log.e("AudioManager", "Khong doc duoc file trong Sound Lib");
			return null;
		}
		byte[] buffer = wavReader.getData();
		wavReader.clearBuffer();//giai phong bo nho
		try {
			ipStream.close();
		} catch (IOException e) {
			Log.e("AudioManager", "Khong close File Stream trong Sound Lib");
		}
		ipStream = null;//giai phong bo nho
		return buffer;
	}
	
	private boolean voiceJoinPlay(int auIdA, int auIDB){//Load du lieu vao buffer player theo the loai va dia chi
		if(auIdA == AudioLibManager.FILE_NOT_FOUND){//khong ton tai file
			Log.e("AudioManager", "Khong tim thay file auIdA trong Sound Lid");
			return false;
		}
		if(auIDB == AudioLibManager.FILE_NOT_FOUND){//khong ton tai file
			Log.e("AudioManager", "Khong tim thay file auIDB trong Sound Lid");
			return false;
		}
		
		byte[] bufType = getBufferSoundLib(auIdA);
		byte[] bufAddress = getBufferSoundLib(auIDB);		
		
		int sizeTypBuf = bufType.length;
		int sizeAssBuf = bufAddress.length;
		
		int bufferSize = sizeTypBuf + sizeAssBuf;
		byte[] bufferPCM = new byte[bufferSize];
		
		int i;
		for(i = 0; i < sizeTypBuf; i++){
			bufferPCM[i] = bufType[i];
		}
		bufType = null;//giai phong bo nho
		
		int j = 0;
		for(i = sizeTypBuf; i < bufferSize; i++){
			bufferPCM[i] = bufAddress[j];
			j++;
		}
		bufAddress = null;//giai phong bo nho		
		
		setPlayerConfig();
		player.loadBufferPCM(bufferPCM);
		bufferPCM = null;//giai phong bo nho
		player.play();
		return true;
	}
	
	public void destroy(){
		player.release();
		recorder.release();
		if(thSendWs != null){
			thSendWs.interrupt();
			thSendWs = null;
		}
	}
	
	public boolean voiceAlertHasEvent(String event, String address){
		int evAuID = AudioLibManager.getAudioTypeEvent(event);
		int addAuID = AudioLibManager.getAudioAddress(address);
		return voiceJoinPlay(evAuID, addAuID);
	}
	
	public boolean voiceAlertActivate(String event){
		int active = AudioLibManager.getAudioActive("DA_KICH_HOAT");
		int evAuID = AudioLibManager.getAudioEventName(event);
		return voiceJoinPlay(active, evAuID);
	}
}