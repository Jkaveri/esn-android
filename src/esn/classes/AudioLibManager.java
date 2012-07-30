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
		
		streets.put("bach_dang",R.raw.a_bachdang);
		streets.put("ba_trieu",R.raw.a_batrieu);
		streets.put("bui_dinh_tuy",R.raw.a_buidinhtuy);
		streets.put("bui_thi_xuan",R.raw.a_buithixuan);
		streets.put("bui_vien",R.raw.a_buivien);
		streets.put("calmette",R.raw.a_calmette);
		streets.put("cao_trieu_phat",R.raw.a_caotrieuphat);
		streets.put("chau_van_liem",R.raw.a_chauvanliem);
		streets.put("chu_van_an",R.raw.a_chuvanan);
		streets.put("cach_mang_thang8",R.raw.a_cmt8);
		streets.put("cmt8",R.raw.a_cmt8);
		streets.put("duong_d2",R.raw.a_d2);
		streets.put("duong_d3",R.raw.a_d3);
		streets.put("duong_d5",R.raw.a_d5);
		streets.put("dang_duc_thuat",R.raw.a_dangducthuat);
		streets.put("dao_tri",R.raw.a_daotri);
		streets.put("dien_bien_phu",R.raw.a_dienbienphu);
		streets.put("dinh_bo_linh",R.raw.a_dinhbolinh);
		streets.put("dinh_tien_hoang",R.raw.a_dinhtienhoang);
		streets.put("duong_truc",R.raw.a_duongtruc);
		streets.put("ha_huy_tap",R.raw.a_hahuytap);
		streets.put("hai_ba_trung",R.raw.a_haibatrung);
		streets.put("hai_thuong_lan_ong",R.raw.a_haithuonglanong);
		streets.put("hoang_quoc_viet",R.raw.a_hoangquocviet);
		streets.put("hong_bang",R.raw.a_hongbang);
		streets.put("hung_vuong",R.raw.a_hungvuong);
		streets.put("huynh_tan_phat",R.raw.a_huynhtanphat);
		streets.put("le_duan",R.raw.a_leduan);
		streets.put("le_hong_phong",R.raw.a_lehongphong);
		streets.put("le_lai",R.raw.a_lelai);
		streets.put("le_quang_dinh",R.raw.a_lequangdinh);
		streets.put("le_quy_don",R.raw.a_lequydon);
		streets.put("le_thanh_tong",R.raw.a_lethanhtong);
		streets.put("le_van_luong",R.raw.a_levanluong);
		streets.put("luong_nhu_hoc",R.raw.a_luongnhuhoc);
		streets.put("ly_long_tuong",R.raw.a_lylongtuong);
		streets.put("ly_thuong_kiet",R.raw.a_lythuongkiet);
		streets.put("ly_tu_trong",R.raw.a_lytutrong);
		streets.put("mac_thien_ich",R.raw.a_macthienich);
		streets.put("ngo_duc_ke",R.raw.a_ngoducke);
		streets.put("ngo_gia_tu", R.raw.a_ngogiatu);
		streets.put("ngo_quyen", R.raw.a_ngoquyen);
		streets.put("nguyen_cao_bac", R.raw.a_nguyencaobac);
		streets.put("nguyen_cao_nam", R.raw.a_nguyencaonam);
		streets.put("nguyen_du", R.raw.a_nguyendu);
		streets.put("nguyen_duc_canh", R.raw.a_nguyenduccanh);
		streets.put("nguyen_huu_tho", R.raw.a_nguyenhuutho);
		streets.put("nguyen_kim", R.raw.a_nguyenkim);
		streets.put("nguyen_luong_bang", R.raw.a_nguyenluongbang);
		streets.put("nguyen_thi_thap", R.raw.a_nguyenthithap);
		streets.put("nguyen_trai", R.raw.a_nguyentrai);
		streets.put("nguyen_van_cu", R.raw.a_nguyenvancu);
		streets.put("nguyen_van_dau", R.raw.a_nguyenvandau);
		streets.put("nguyen_van_linh", R.raw.a_nguyenvanlinh);
		streets.put("nguyen_xi", R.raw.a_nguyenxi);
		streets.put("no_trang_long", R.raw.a_notranglong);
		streets.put("pasteur", R.raw.a_pasteur);
		streets.put("pham_hong_thai", R.raw.a_phamhongthai);
		streets.put("pham_huu_chi", R.raw.a_phamhuuchi);
		streets.put("pham_huu_lau", R.raw.a_phamhuulau);
		streets.put("pham_ngu_lao", R.raw.a_phamngulao);
		streets.put("pham_thai_buong", R.raw.a_phamthaibuong);
		streets.put("phan_dang_luu", R.raw.a_phandangluu);
		streets.put("phan_khiem_ich", R.raw.a_phankhiemich);
		streets.put("phan_van_tri", R.raw.a_phanvantri);
		streets.put("phuoc_hung", R.raw.a_phuochung);
		streets.put("su_van_hang", R.raw.a_suvanhanh);
		streets.put("tan_da", R.raw.a_tanda);
		streets.put("tang_bat_ho", R.raw.a_tangbatho);
		streets.put("tan_phu", R.raw.a_tanphu);
		streets.put("ton_duc_thang", R.raw.a_tonducthang);
		streets.put("tran_hung_dao", R.raw.a_tranhungdao);
		streets.put("tran_ke_xuong", R.raw.a_trankexuong);
		streets.put("tran_phu", R.raw.a_tranphu);
		streets.put("tran_quy_cap", R.raw.a_tranquycap);
		streets.put("tran_van_tra", R.raw.a_tranvantra);
		streets.put("truong_dinh", R.raw.a_truongdinh);
		streets.put("ung_van_khiem", R.raw.a_ungvankhiem);
		streets.put("van_kiep", R.raw.a_vankiep);
		streets.put("vo_van_kiet", R.raw.a_vovankiet);
		streets.put("vo_van_tan", R.raw.a_vovantan);
		streets.put("xa_lo_ha_noi", R.raw.a_xalohanoi);
		streets.put("xvnt", R.raw.a_xvnt);
		streets.put("xo_viet_nghe_tinh", R.raw.a_xvnt);
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
