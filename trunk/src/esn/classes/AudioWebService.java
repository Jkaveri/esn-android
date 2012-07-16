package esn.classes;

import java.util.Hashtable;

import org.ksoap2.serialization.SoapObject;

import esn.models.S2TResult;
import android.annotation.SuppressLint;
import android.util.Base64;

@SuppressLint("NewApi")
public class AudioWebService {
	public static final String NAMSPACE = "http://www.aprotrain.com/";
	public static final String URL = "http://aahcmc.aprotrain.com/ESNSpeechRecognition/ESN2012.asmx";//"http://aahcmc.aprotrain.com/ESNSpeechRecognition/ESN2012.asmx";
	private EsnWebServices service;
	
	public AudioWebService() {
		service = new EsnWebServices(NAMSPACE, URL);
	}
	
	public S2TResult send(byte[] byteWavData){
		S2TResult s2tResult = new S2TResult();		
		String byteString = Base64.encodeToString(byteWavData, Base64.DEFAULT);
		byteWavData = null;//giai phong bo nho
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("arrBytes", byteString);
		
		SoapObject response = service.InvokeMethod("ESNSpeechRecognition_iLBC", params);
		if (response != null) {
			Object result = response.getProperty(0);
			s2tResult.setProperty(0, result);
//			int propCount = result.getPropertyCount();
//			for (int j = 0; j < propCount; j++) {
//				Object value = result.getProperty(j);
//				if (value != null) {
//					s2tResult.setProperty(j, value);
//				}
//			}
		}
		return s2tResult;
	}
}
