package esn.models;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import esn.classes.EsnRequestListener;
import esn.classes.HttpHelper;

public abstract class BaseManager<T> {
	protected String url;
	protected EsnRequestListener listener;
	private Thread requestThread;
	private Object lockObject = new Object();
	protected JSONObject response;
	private boolean async = true;

	protected BaseManager(String url, EsnRequestListener listener) {
		this.url = url;
		this.listener = listener;
	}

	public void setAsyncRequest(Boolean t) {
		async = t;
	}

	public void request(final String methodName, final JSONObject params) {
		if (listener != null) {
			requestThread = new Thread(new Runnable() {

				@Override
				public void run() {
					listener.onBeforeInvolke(methodName);
					HttpHelper helper = new HttpHelper(url);

					try {
						response = helper.invokeWebMethod(methodName, params);
						listener.onComplete(methodName, response, null);
						if (!async) {
							synchronized (lockObject) {
								lockObject.notify();
							}
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
			if (!async) {
				synchronized (lockObject) {
					try {
						lockObject.wait();

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			listener.onAfterInvolke(methodName, response, null);
		}

	}

	public void clean() {
		if (requestThread != null) {
			requestThread.interrupt();
		}
	}

}
