/*
 * By lnbienit@gmail.com 
 */

package esn.classes;

import esn.activities.R;

public class AudioLibManager {
	public static final int TYPE_NOT_FOUND = -10;
	public static final int ADDRESS_NOT_FOUND = -11;
	
	////////////////////////////////////
	//           TYPE DEFINE          //
	////////////////////////////////////
	
	private static final String[] TYPE = {
		"cos lowr ddaast owr",
		"cos chasy noor owr",
		"cos kejt xe owr"
	};	
	private static final int[] AUDIO_TYPE = {
		R.raw.t_lo_dat,
		R.raw.t_chay_no,
		R.raw.t_ket_xe
	};
	
	
	
	
	
	////////////////////////////////////
	//         ADDRESS DEFINE         //
	////////////////////////////////////
	
	private static final String[] ADDRESS = {
		"gof vaasp",
		"hafng xanh",
		"thur dduwsc"
	};
	private static final int[] AUDIO_ADDRESS = {
		R.raw.a_go_vap,
		R.raw.a_hang_xanh,
		R.raw.a_thu_duc
	};
	
	
	
	
	
	
	////////////////////////////////////
	//          METHOD DEFINE         //
	////////////////////////////////////
	
	public static int getAudioType(String type){
		int audioId = TYPE_NOT_FOUND;
		int len = TYPE.length;
		type = type.trim().toLowerCase();
		for(int i = 0; i < len; i++){
			if(type.equals(TYPE[i])){
				audioId = AUDIO_TYPE[i];
			}
		}
		return audioId;
	}
	
	public static int getAudioAddress(String address){
		int audioId = ADDRESS_NOT_FOUND;
		int len = ADDRESS.length;
		address = address.trim().toLowerCase();
		for(int i = 0; i < len; i++){
			if(address.equals(ADDRESS[i])){
				audioId = AUDIO_ADDRESS[i];
			}
		}
		return audioId;
	}
}
