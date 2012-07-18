/*
 * By lnbienit@gmail.com 
 */

package esn.classes;

import com.google.android.maps.MapView;

import android.content.res.Resources;
import android.util.Log;

public class VoiceProcesser extends VoiceManager{

	public VoiceProcesser(Resources resource, MapView maps) {
		super(resource);
	}
	
	public void onS2TPostback(){
		S2TParser result =  getS2TParserResult();//Result tra ve sau khi phan tich giong noi
//		voiceAlertAction("KET_XE");
		if(result.getAction() == "KICH_HOAT"){
			voiceAlertActivate(result.getEvent());
		}else if(result.getAction() == "SAP_TOI"){
			//Gia su co su kien tai hang xanh
			voiceAlertHasEvent(result.getEvent(), "HANG_XANH");
			Log.e("aaaaaaaaa", result.toString());
		}
		//Viet cai gi thi viet
	}
}
