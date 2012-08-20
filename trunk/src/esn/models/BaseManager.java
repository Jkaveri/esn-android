package esn.models;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import esn.classes.EsnRequestListener;
import esn.classes.HttpHelper;

public abstract class BaseManager<T> {
	protected String url;
	protected EsnRequestListener listener;
	private Thread requestThread;
	private Object lockObject = new Object();
	protected JSONObject response;
	protected BaseManager(String url, EsnRequestListener listener) {
		this.url = url;
		this.listener = listener;
	}

	public abstract void Create(T t);

	public abstract void get(int id);

	public void request(final String methodName, final JSONObject params) {
		
		requestThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpHelper helper = new HttpHelper(url);

				try {
					
					response = helper.invokeWebMethod(methodName, params);
					listener.onComplete(methodName, response, null);
					synchronized (lockObject) {
						lockObject.notify();
					}
				} catch (ClientProtocolException e) {
					listener.onConnectError(methodName, e.getMessage(), e);
					e.printStackTrace();
				} catch (IOException e) {
					listener.onConnectError(methodName, e.getMessage(), e);
					e.printStackTrace();
				} catch (JSONException e) {
					listener.onMethodError(methodName, e.getMessage(), e);
					e.printStackTrace();
				}
			}
		});
		requestThread.start();
		synchronized (lockObject) {
			try {
				lockObject.wait();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		listener.onAfterInvolke(methodName, response, null);
	
	}
	public void clean(){
		if(requestThread!=null){
			requestThread.interrupt();
		}
	}

}
