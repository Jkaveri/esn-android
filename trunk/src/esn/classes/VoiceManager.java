/*
 * By lnbienit@gmail.com 
 */

package esn.classes;

import java.io.IOException;
import java.io.InputStream;

import esn.activities.R;
import esn.models.S2TResult;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.util.Log;

public class VoiceManager {
	private AudioRecorder recorder;
	private AudioPlayer player;
	private WAVFormatConver wavConver;
	private WAVFormatReader wavReader;

	private Thread thSendWs;
	private AudioWebService auWs;
	private Resources resource;
	private Runnable runSendWs;
	private VoiceListener callBack;
	
	public VoiceManager(Resources resource) {
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
	
	private boolean beep(int soundId, boolean isInsiteThread){
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
//		player.release();
//		player.setDefaultConfig();
//		player.prepare();		
//		player.loadBufferPCM(recordBuf);
		Log.i("AudioManager", "Data record length: " + recordBuf.length);
		
//		wavConver.setBuffer(recordBuf);
//		recordBuf = null;//giai phong bo nho
//		wavConver.prepare();
//		wavConver.conver();
//		
//		byte[] buf = wavConver.getWAVData();
//		wavConver.clearBuffer();//giai phong bo nho
		
		S2TResult result = auWs.send(recordBuf);
		Log.i("AudioManager", "Result: " + result.getType());
		callBack.onS2TPostBack(result);
//		boolean ok = loadPlayerSumBuffer("cos lowr ddaast owr", "hafng xanh");
//		if(ok){
			player.play();
//		}else{
//			Log.e("AudioManager", "Voice phan hoi khong thanh cong");
//		}
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
	
	@SuppressWarnings("unused")
	private boolean loadPlayerSumBuffer(String type, String address){//Load du lieu vao buffer player theo the loai va dia chi
		int auTypeId = AudioLibManager.getAudioType(type);
		if(auTypeId == AudioLibManager.TYPE_NOT_FOUND){//khong ton tai file
			Log.e("AudioManager", "Khong tim thay file kieu: \"" + type + "\"");
			return false;
		}
		InputStream typeStream = resource.openRawResource(auTypeId);
		wavReader.setBuffer(typeStream);
		if(!wavReader.read()){
			wavReader.clearBuffer();//giai phong bo nho
			typeStream = null;//giai phong bo nho
			Log.e("AudioManager", "Khong doc duoc file kieu: \"" + type + "\"");
			return false;
		}
		byte[] bufType = wavReader.getData();
		wavReader.clearBuffer();//giai phong bo nho
		typeStream = null;//giai phong bo nho
		
		
		
		int auAddressId = AudioLibManager.getAudioAddress(address);
		if(auAddressId == AudioLibManager.ADDRESS_NOT_FOUND){//khong ton tai file
			Log.e("AudioManager", "Khong tim thay file dia chi: \"" + address + "\"");
			bufType = null;//Giai phong bo nho
			return false;
		}
		InputStream addressStream = resource.openRawResource(auAddressId);
		wavReader.setBuffer(addressStream);
		if(!wavReader.read()){
			wavReader.clearBuffer();//giai phong bo nho
			addressStream = null;//giai phong bo nho
			Log.e("AudioManager", "Khong doc duoc file dia chi: \"" + address + "\"");
			return false;
		}
		byte[] bufAddress = wavReader.getData();
		wavReader.clearBuffer();//giai phong bo nho
		addressStream = null;//giai phong bo nho
		
		
		
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
		
		return true;
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
	
	public void destroy(){
		player.release();
		recorder.release();
		if(thSendWs != null){
			thSendWs.interrupt();
			thSendWs = null;
		}
	}
}
