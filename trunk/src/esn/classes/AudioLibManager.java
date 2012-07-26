/*
 * By lnbienit@gmail.com 
 */

package esn.classes;

import java.util.Hashtable;

import esn.activities.R;

public class AudioLibManager {
	public final static int FILE_NOT_FOUND = -10;
	private Hashtable<String, Integer> streets;
	private Hashtable<String, Integer> eventTypes;
	public AudioLibManager(){
		setupStreets();
		setupEventTypes();
	}
	private void setupStreets(){
		streets = new Hashtable<String, Integer>();
		
		streets.put("bachdang",R.raw.a_bachdang);
		streets.put("batrieu",R.raw.a_batrieu);
		streets.put("buidinhtuy",R.raw.a_buidinhtuy);
		streets.put("buithixuan",R.raw.a_buithixuan);
		streets.put("buivien",R.raw.a_buivien);
		streets.put("calmette",R.raw.a_calmette);
		streets.put("caotrieuphat",R.raw.a_caotrieuphat);
		streets.put("chauvanliem",R.raw.a_chauvanliem);
		streets.put("chuvanan",R.raw.a_chuvanan);
		streets.put("cachmangthang8",R.raw.a_cmt8);
		streets.put("cmt8",R.raw.a_cmt8);
		streets.put("d2",R.raw.a_d2);
		streets.put("d3",R.raw.a_d3);
		streets.put("d5",R.raw.a_d5);
		streets.put("dangducthuat",R.raw.a_dangducthuat);
		streets.put("daotri",R.raw.a_daotri);
		streets.put("dienbienphu",R.raw.a_dienbienphu);
		streets.put("dinhbolinh",R.raw.a_dinhbolinh);
		streets.put("dinhtienhoang",R.raw.a_dinhtienhoang);
		streets.put("duongtruc",R.raw.a_duongtruc);
		streets.put("hahuytap",R.raw.a_hahuytap);
		streets.put("haibatrung",R.raw.a_haibatrung);
		streets.put("haithuonglanong",R.raw.a_haithuonglanong);
		streets.put("hoangquocviet",R.raw.a_hoangquocviet);
		streets.put("hongbang",R.raw.a_hongbang);
		streets.put("hungvuong",R.raw.a_hungvuong);
		streets.put("huynhtanphat",R.raw.a_huynhtanphat);
		streets.put("leduan",R.raw.a_leduan);
		streets.put("lehongphong",R.raw.a_lehongphong);
		streets.put("lelai",R.raw.a_lelai);
		streets.put("lequangdinh",R.raw.a_lequangdinh);
		streets.put("lequydon",R.raw.a_lequydon);
		streets.put("lethanhtong",R.raw.a_lethanhtong);
		streets.put("levanluong",R.raw.a_levanluong);
		streets.put("luongnhuhoc",R.raw.a_luongnhuhoc);
		streets.put("lylongtuong",R.raw.a_lylongtuong);
		streets.put("lythuongkiet",R.raw.a_lythuongkiet);
		streets.put("lytutrong",R.raw.a_lytutrong);
		streets.put("macthienich",R.raw.a_macthienich);
		streets.put("ngoducke",R.raw.a_ngoducke);
	}
	
	private void setupEventTypes(){
		eventTypes = new Hashtable<String, Integer>();
		//ko biet su kien
		eventTypes.put("0", R.raw.chuy);
		eventTypes.put("10", R.raw.chuy);
		eventTypes.put("23", R.raw.chuy);
		//
		eventTypes.put("1", R.raw.n_coketxe);
		eventTypes.put("KET_XE", R.raw.n_coketxe);
		eventTypes.put("2", R.raw.n_colocot);
		eventTypes.put("LO_COT", R.raw.n_colocot);
		eventTypes.put("3", R.raw.n_cotngt);
		eventTypes.put("TAI_NAN", R.raw.n_cotngt);
		eventTypes.put("4", R.raw.n_colulut);
		eventTypes.put("LU_LUT", R.raw.n_colulut);
		eventTypes.put("5", R.raw.n_colodat);
		eventTypes.put("LO_DAT", R.raw.n_colodat);
		eventTypes.put("6", R.raw.n_coduongxau);
		eventTypes.put("DUONG_XAU", R.raw.n_coduongxau);
		eventTypes.put("7", R.raw.n_cochayno);
		eventTypes.put("CHAY_NO", R.raw.n_cochayno);
		eventTypes.put("8", R.raw.n_coduongchan);
		eventTypes.put("DUONG_CHAN", R.raw.n_coduongchan);
		eventTypes.put("9", R.raw.n_codongdat);
		eventTypes.put("DONG_DAT", R.raw.n_codongdat);
		
		
	}
	
	/**
	 * Get has event type audio
	 * @param eventType KET_XE, LO_COT, TAI_NAN,LU_LUT,DUONG_XAU, CHAY_NO, DUONG_CHAN, DONG_DAT
	 * @return raw value
	 */
	public int getHasEventTypeAudio(String eventType) {
		if(eventTypes.containsKey(eventType)){
			int result = eventTypes.get(eventType);
			return result;
		}
		return FILE_NOT_FOUND;
		
	}

	/**
	 * Get voice for street name
	 * @param street: street name (no white space and lower case)
	 * @return raw value
	 */
	public int getStreetAudio(String street) {
		android.util.Log.d("GetStreetAudio",street);
		int result = streets.get(street);
		if(result<=0){
			return FILE_NOT_FOUND;
		}
		return result;
	}
	public boolean isExistEventTypeAudio(String eventType) {
		return eventTypes.containsKey(eventType);
	}
	public boolean isExistStreetAudio(String street) {
		// TODO Auto-generated method stub
		return streets.containsKey(street);
	}
}
