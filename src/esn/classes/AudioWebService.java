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
	
	public S2TResult send(byte[] byteWavData){
		S2TResult s2tResult = new S2TResult();		
		String byteString = Base64.encodeToString(byteWavData, Base64.DEFAULT);
		byteWavData = null;//giai phong bo nho
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("ByteArrWav", byteString);
		
		SoapObject response = service.InvokeMethod("WriteWAVFile", params);
		if (response != null) {
			SoapObject result = (SoapObject)response.getProperty(0);
			int propCount = result.getPropertyCount();
			for (int j = 0; j < propCount; j++) {
				Object value = result.getProperty(j);
				if (value != null) {
					s2tResult.setProperty(j, value);
				}
			}
		}
		return s2tResult;
	}
}
