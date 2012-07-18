/*
 * By lnbienit@gmail.com 
 */

package esn.classes;

import com.google.android.maps.MapView;
import android.content.res.Resources;

public class VoiceProcesser extends VoiceManager{

	public VoiceProcesser(Resources resource, MapView maps) {
		super(resource);
	}
	
	public void onS2TPostback(S2TParser result){
		if(result.getAction().equals("KICH_HOAT")){
			voiceAlertActivate(result.getEvent());
		}else if(result.getAction().equals("SAP_TOI")){
			//Gia su co su kien tai hang xanh
			voiceAlertHasEvent(result.getEvent(), "HANG_XANH");
		}
		//Viet cai gi thi viet
	}
}
