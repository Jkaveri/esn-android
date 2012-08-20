package esn.classes;

import org.json.JSONObject;

import android.os.Bundle;

public interface EsnRequestListener {
	public void onComplete(String methodName, JSONObject response,Bundle datas);
	public void onConnectError(String methodName, String message, Exception error);
	public void onMethodError(String methodName, String message, Exception e);
	public void onBeforeInvolke(String metthodName, String messae, Exception e);
	public void onAfterInvolke(String methodName, JSONObject response,
			Object object);
}
