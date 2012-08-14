package esn.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class PolicyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.policy);
		
		WebView webView = (WebView) findViewById(R.id.webViewPolicy);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://myesn.vn/termofprivacy.html");
	}
}
