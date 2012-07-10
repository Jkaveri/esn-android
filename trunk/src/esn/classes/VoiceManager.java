/*
 * By lnbienit@gmail.com 
 */

package esn.classes;

import java.io.IOException;
import java.io.InputStream;

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
	private IVoiceCallBack callBack;
	
	public VoiceManager(Resources resource) {
		recorder = new AudioRecorder();
		player = new AudioPlayer();
		wavConver = new WAVFormatConver();
		wavReader = new WAVFormatReader();
		auWs = new AudioWebService();
		
		this.resource = resource;
		
		wavConver.setBitsPerSample(16); //16 => 16BIT, 8 => 8BIT
		wavConver.setSubchunkSize(16); //16 => PCM
		wavConver.setFormat(1); // 1 FOR PCM
		wavConver.setChannels(1); //1 => MONO, 2 => STEREO
		wavConver.setSampleRate(recorder.SAMPLE_RATE);
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
	
	public void setSendDataBack(IVoiceCallBack callBack){
		this.callBack = callBack;
	}
	
	private void runSendWS() throws IOException{
		byte[] recordBuf = recorder.getBufferRecord();
		recorder.clearBuffer();//giai phong bo nho
		Log.i("AudioManager", "Data record length: " + recordBuf.length);
		
		wavConver.setBuffer(recordBuf);
		recordBuf = null;//giai phong bo nho
		wavConver.prepare();
		wavConver.conver();
		
		byte[] buf = wavConver.getWAVData();
		wavConver.clearBuffer();//giai phong bo nho
		//S2TResult result = auWs.send(buf);//khi send tu giai phong bo nho
		//Log.i("AudioManager", "Result: " + result.getType());
		//callBack.returnCall(result);
		boolean ok = loadPlayerBuffer("cos lowr ddaast owr", "hafng xanh");
		if(ok){
			player.play();
		}else{
			Log.e("AudioManager", "Voice phan hoi khong thanh cong");
		}
	}
	
	public void stopRecording(){
		recorder.stopRecording();
	}
	
	public void startRecording(){
		recorder.startRecording();
	}
	
	public void sendDataToServer(){// Gui du lieu ghi am xuong server
		destroy();
		thSendWs = new Thread(runSendWs);
		thSendWs.start();
	}
	
	private boolean loadPlayerBuffer(String type, String address){//Load du lieu vao buffer player theo the loai va dia chi
		player.release();//giai phong bo nho
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
			bufType = null;
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
		player.loadBufferPCM(bufferPCM);
		bufferPCM = null;//giai phong bo nho
		
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
}
