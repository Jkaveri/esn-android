package esn.classes;

import java.util.Hashtable;

import org.ksoap2.serialization.SoapObject;

import esn.models.S2TResult;
import android.util.Base64;

public class AudioWebService {
	public static final String NAMSPACE = "http://localhost/S2TWebService.asmx";
	public static final String URL = "http://10.0.2.2:49609/S2TWebService/S2TWebService.asmx";
	private EsnWebServices service;
	
	public AudioWebService() {
		service = new EsnWebServices(NAMSPACE, URL);
	}
	
	public String send(byte[] byteWavData){
		S2TResult s2tResult = new S2TResult();		
		String byteString = Base64.encodeToString(byteWavData, Base64.DEFAULT);
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("ByteArrWav", byteString);
		
		SoapObject response = service.InvokeMethod("WriteWAVFile", params);
		if (response != null) {
			Object value = response.getProperty(0);
			s2tResult.setProperty(0, value);
		}
		return s2tResult.getResult();
	}
}
