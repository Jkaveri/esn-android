package esn.classes;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

public class EsnWebServices {
	public String namespace;
	public String url;
	public int soapEnvelopeVer;
	public boolean dotNet;
	
	public SoapSerializationEnvelope envelope;
	
	//private ArrayList<SoapMaping> mapings = new ArrayList<SoapMaping>();
	
	public EsnWebServices(String NAMESPACE, String URL) {
		
		this.namespace = NAMESPACE;
		this.url = URL;
		this.dotNet = true;
		this.soapEnvelopeVer = SoapEnvelope.VER11;
	}
	/*public void addMaping(String name,Class<?> type){
		mapings.add(new SoapMaping(this.namespace, name, type));
	}*/
	/*private void addMaping(){
		if(mapings.size()>0 && this.envelope!=null){
			for(SoapMaping maping:mapings){
				this.envelope.addMapping(maping.getNamespace(), maping.getName(), maping.getClazz());
			}
		}
	}*/
	/**
	 * Invoke a Web Service Method
	 * 
	 * @param url
	 *            - URL of service
	 * @param namespace
	 *            - NAMESPACE of service
	 * @param methodName
	 *            - Method name to call
	 * @return Response - call's result
	 */
	public SoapObject InvokeMethod(String url, String namespace,
			String methodName) {

		SoapObject request = GetSoapObject(methodName);
		this.envelope = GetEnvelope(request, true);
		return MakeCall(url, envelope, namespace, methodName);
	}

	/**
	 * Invoke WebService method with params
	 * 
	 * @param url
	 *            - URL of service
	 * @param namespace
	 *            - NAMESPACE of service
	 * @param methodName
	 *            - Method name to call
	 * @param params
	 *            - parameters of Web method
	 * @return Response - call's result
	 */
	public SoapObject InvokeMethod(String url, String namespace,
			String methodName, Hashtable<String, Object> params) {
		// inititalize request
		SoapObject request = GetSoapObject(methodName);

		// get all keys
		Enumeration<String> keys = params.keys();
		while (keys.hasMoreElements()) {
			// get key
			String key = keys.nextElement();
			// get value
			Object value = params.get(key);
			// initial property
			PropertyInfo pi = new PropertyInfo();
			// set property name
			pi.setName(key);
			// set property value
			pi.setValue(value);
			// set property type
			pi.setType(value.getClass());
			// add property to request
			request.addProperty(pi);
		}
		// get envelope by request and use .net
		this.envelope = GetEnvelope(request);
		return MakeCall(url, this.envelope, namespace, methodName);
	}

	public SoapObject InvokeMethod(String methodName) {
		// inititalize request
		SoapObject request = GetSoapObject(methodName);
		// get envelope by request and use .net
		this.envelope = GetEnvelope(request);
		return MakeCall(this.url, this.envelope, this.namespace, methodName);
	}

	public SoapObject InvokeMethod(String methodName,
			Hashtable<String, Object> params) {
		// inititalize request
		SoapObject request = GetSoapObject(methodName);
		
		// get all keys
		Enumeration<String> keys = params.keys();
		while (keys.hasMoreElements()) {
			// get key
			String key = keys.nextElement();
			// get value
			Object value = params.get(key);
			// initial property
			PropertyInfo pi = new PropertyInfo();
			// set property name
			pi.setName(key);
			// set property value
			pi.setValue(value);
			// set property type
			pi.setType(value.getClass());
			// add property to request
			request.addProperty(pi);
		}
		// get envelope by request and use .net
		this.envelope = GetEnvelope(request);
		
		return MakeCall(this.url, this.envelope, this.namespace, methodName);
	}

	/**
	 * 
	 * @param url
	 *            - URL of service
	 * @param envelope
	 *            - The envelope to be passed
	 * @param namespace
	 *            - The NAMESPACE of WebService
	 * @param methodName
	 *            - Method name to make call
	 * @return Response - call's result
	 */
	private SoapObject MakeCall(String url, SoapSerializationEnvelope envelope,
			String namespace, String methodName) {
		AndroidHttpTransport transport = new AndroidHttpTransport(url);
		try {
			String soapAction = namespace;
			if (!namespace.endsWith("/")) {
				soapAction += "/" + methodName;
			} else {
				soapAction += methodName;
			}
			transport.call(soapAction, envelope);
			return (SoapObject) envelope.bodyIn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * create envelope object
	 * 
	 * @param soap
	 *            - SoapObject
	 * @return envelope object
	 */
	private SoapSerializationEnvelope GetEnvelope(SoapObject soap) {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				this.soapEnvelopeVer);
		envelope.dotNet = this.dotNet;
		envelope.setOutputSoapObject(soap);
		return envelope;
	}

	/**
	 * create envelope object for .net service
	 * 
	 * @param soap
	 * @param dotNet
	 * @return envelope object
	 */
	private SoapSerializationEnvelope GetEnvelope(SoapObject soap,
			boolean dotNet) {
		envelope = new SoapSerializationEnvelope(
				this.soapEnvelopeVer);
		envelope.setOutputSoapObject(soap);
		envelope.dotNet = dotNet;
		//addMaping();
		return envelope;
	}

	/**
	 * Get SoapObject
	 * 
	 * @param methodName
	 * @return SoapObject
	 */
	private SoapObject GetSoapObject(String methodName) {
		return new SoapObject(this.namespace, methodName);
	}
}
