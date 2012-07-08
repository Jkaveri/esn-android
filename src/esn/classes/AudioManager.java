package esn.classes;

import android.util.Log;

public class AudioManager {
	public AudioRecoder recorder;
	public AudioPlayer player;
	public WAVFormatConver wavConver;
	public WAVFormatReader wavReader;

	private Thread thSendWs;
	private AudioWebService auWs;

	public AudioManager() {
		recorder = new AudioRecoder();
		player = new AudioPlayer();
		wavConver = new WAVFormatConver();
		wavReader = new WAVFormatReader();
		auWs = new AudioWebService();
		
		wavConver.setBitsPerSample(16); //16 => 16BIT, 8 => 8BIT
		wavConver.setSubchunkSize(16); //16 => PCM
		wavConver.setFormat(1); // 1 FOR PCM
		wavConver.setChannels(1); //1 => MONO, 2 => STEREO
		wavConver.setSampleRate(recorder.SAMPLE_RATE);
	}
	
	public void sendDataToServer(){
		thSendWs = new Thread(new Runnable() {
			
			@Override
			public void run() {
				wavConver.setBuffer(recorder.getBufferRecod());
				wavConver.prepare();
				wavConver.conver();
				
				byte[] buf = wavConver.getWAVData();
				String result = auWs.send(buf);
				
				Log.i("AudioManager", "Data record length: " + buf.length);
				Log.i("AudioManager", "Result: " + result);
			}
		});
		
		thSendWs.start();
	}
}
