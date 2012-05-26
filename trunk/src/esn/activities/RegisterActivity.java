package esn.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class RegisterActivity extends Activity {

	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.register);
		
		intent = this.getIntent();
		
		TextView sv = (TextView)findViewById(R.id.lkService);
		
		sv.setText(Html.fromHtml("<a href=\"http://www.esn.com/policy\">Term of Service</a> "));
		
		sv.setMovementMethod(LinkMovementMethod.getInstance());
		
		TextView po = (TextView)findViewById(R.id.lkPolicy);
				
		po.setText(Html.fromHtml("<a href=\"http://www.esn.com/policy\">Privacy and Policy</a> "));
				
		po.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	public void CancelClicked(View view) {
		
		setResult(RESULT_CANCELED, intent);
		
		finish();
	}
}