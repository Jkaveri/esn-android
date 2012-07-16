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
	
	public void onS2TPostback(){
		S2TParser result =  getS2TParserResult();//Result tra ve sau khi phan tich giong noi
		
		
		//Viet cai gi thi viet
	}
}
