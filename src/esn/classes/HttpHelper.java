package esn.classes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

public class HttpHelper {
	private String url;

	public HttpHelper(String URL) {
		this.url = (URL.endsWith("/")) ? URL : URL + "/";
	}

	private HttpResponse post(String url, Bundle headers,
			JSONObject params) throws ClientProtocolException,
			IOException {
		// instance client
		HttpClient client = new DefaultHttpClient();
		// instance HttpPost object
		HttpPost httpPost = new HttpPost(url);
		// add header
		if (headers != null) {
			for (String key : headers.keySet()) {
				httpPost.addHeader(key, headers.getString(key));
			}
		}
		if (params != null) {
			
			httpPost.setEntity(new StringEntity(params.toString(), "UTF-8"));
		}
		// execute request
		return client.execute(httpPost);
	}

	private String read(InputStream in) throws IOException {
		// instance buffered input stream
		BufferedInputStream bfs = new BufferedInputStream(in);
		// Byte array buffer
		ByteArrayBuffer baf = new ByteArrayBuffer(20);

		int current = 0;
		while ((current = bfs.read()) != -1) {
			baf.append((byte) current);
		}

		return new String(baf.toByteArray());
	}

	public JSONObject invokeWebMethod(String method)
			throws ClientProtocolException, IOException, JSONException {

		return invokeWebMethod(method, null);
	}

	/**
	 * ham goi WebMethod Cua .net tra ve JSONObject
	 * 
	 * @param method
	 *            Method in Asp.net page
	 * @param params
	 *            parameter for this method
	 * @return JSONObject result
	 * @throws ClientProtocolException 
	 * @throws JSONException
	 * @throws IOException
	 */
	public JSONObject invokeWebMethod(String method, JSONObject params) throws ClientProtocolException, IOException, JSONException {
		// init result
		JSONObject result = null;

		Bundle headers = new Bundle();
		
		headers.putString("Content-Type", "application/json");
		
		headers.putString("Content-Encoding", "utf-8");
		// set params
		String url_method = this.url + method;
		// execute post request
		HttpResponse response = post(url_method, headers, params);
		// get input stream
		InputStream in = response.getEntity().getContent();
		String jsonString = read(in);
		
		// get result
		if(jsonString!= null && jsonString.length() > 0){
			result = new JSONObject(jsonString);
			
			return result;
		}
		return null;
	}

	public JSONObject get(String method) throws ClientProtocolException,
			IOException, JSONException {
		JSONObject result = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(this.url);
		HttpResponse response = client.execute(httpPost);

		InputStream intputStream = response.getEntity().getContent();

		BufferedInputStream bfs = new BufferedInputStream(intputStream);
		ByteArrayBuffer baf = new ByteArrayBuffer(20);

		int current = 0;
		while ((current = bfs.read()) != -1) {
			baf.append((byte) current);
		}

		String jsonString = new String(baf.toByteArray());
		result = new JSONObject(jsonString);

		return result;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
