/*
 * By lnbienit@gmail.com 
 */

package esn.classes;

import esn.activities.R;

public class AudioLibManager {
	public static final int FILE_NOT_FOUND = -10;
	
	////////////////////////////////////
	//        TYPE EVENT DEFINE       //
	////////////////////////////////////
	
	private static final String[] TYPE_EVENT = {
		"LO_DAT",
		"CHAY_NO",
		"KET_XE"
	};	
	private static final int[] AUDIO_ALERT_HAS_EVENT = {
		R.raw.t_lo_dat,
		R.raw.t_chay_no,
		R.raw.t_ket_xe
	};
    ////////////////////////////////////
	//           EVENT NAME DEFINE          //
	////////////////////////////////////
	
	private static final int[] AUDIO_EVENT_NAME = {
		R.raw.n_lo_dat,
		R.raw.n_chay_no,
		R.raw.n_ket_xe
	};
	
	
	
	
	
	////////////////////////////////////
	//         ADDRESS DEFINE         //
	////////////////////////////////////
	
	private static final String[] ADDRESS = {
		"GO_VAP",
		"HANG_XANH",
		"THU_DUC"
	};
	private static final int[] AUDIO_ADDRESS = {
		R.raw.a_go_vap,
		R.raw.a_hang_xanh,
		R.raw.a_thu_duc
	};
	
	
	
	
	
	
	////////////////////////////////////
	//          METHOD DEFINE         //
	////////////////////////////////////
	
	public static int getAudioTypeEvent(String eventName){
		int audioId = FILE_NOT_FOUND;
		int len = TYPE_EVENT.length;
		eventName = eventName.trim();
		for(int i = 0; i < len; i++){
			if(eventName.equals(TYPE_EVENT[i])){
				audioId = AUDIO_ALERT_HAS_EVENT[i];
			}
		}
		return audioId;
	}
	
	public static int getAudioAddress(String address){
		int audioId = FILE_NOT_FOUND;
		int len = ADDRESS.length;
		address = address.trim();
		for(int i = 0; i < len; i++){
			if(address.equals(ADDRESS[i])){
				audioId = AUDIO_ADDRESS[i];
			}
		}
		return audioId;
	}
	
	public static int getAudioEventName(String eventName) {
		int audioId = FILE_NOT_FOUND;
		int len = TYPE_EVENT.length;
		eventName = eventName.trim();
		for(int i = 0; i < len; i++){
			if(eventName.equals(TYPE_EVENT[i])){
				audioId = AUDIO_EVENT_NAME[i];
			}
		}
		return audioId;
	}
	
	public static int getAudioActive(String status){
		return R.raw.a_activate;
	}
	
}
