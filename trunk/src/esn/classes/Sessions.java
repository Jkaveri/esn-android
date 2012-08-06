package esn.classes;

import java.util.ArrayList;

import com.facebook.android.Facebook;

import esn.models.EventType;
import esn.models.FriendNotification;
import esn.models.Users;
import android.content.Context;
import android.content.SharedPreferences;

public class Sessions {
	
	private static Sessions instance;
	private Context context;
	private SharedPreferences pref;
	public Users currentUser;
	public ArrayList<EventType> eventTypes;
	
	public FriendNotification friendNotification;
	
	
	public Sessions(Context context) {
		this.context = context;
		pref = context.getSharedPreferences("ESN", Context.MODE_PRIVATE);
		friendNotification = new FriendNotification();
	}

	public static Sessions getInstance(Context context) {
		if (instance == null) {
			instance = new Sessions(context);			
		}
		return instance;
	}
	public void clear(){
		if(pref!=null)
		{
			pref.edit().clear().commit();
		}		
	}
	public void put(String key, int value) {
		pref.edit().putInt(key, value).commit();
	}

	public void put(String key, String value) {
		pref.edit().putString(key, value).commit();
	}

	public void put(String key, float value) {
		pref.edit().putFloat(key, value).commit();
	}

	public void put(String key, long value) {
		pref.edit().putLong(key, value).commit();
	}

	public void put(String key, boolean value) {
		pref.edit().putBoolean(key, value).commit();
	}

	public boolean get(String key, boolean defValue) {
		return pref.getBoolean(key, defValue);
	}

	public int get(String key, int defValue) {
		return pref.getInt(key, defValue);
	}

	public float get(String key, float defValue) {
		return pref.getFloat(key, defValue);
	}

	public String get(String key, String defValue) {
		return pref.getString(key, defValue);
	}

	public long get(String key, long defValue) {
		return pref.getLong(key, defValue);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public SharedPreferences getPref() {
		return pref;
	}

	public void setPref(SharedPreferences pref) {
		this.pref = pref;
	}

	public boolean logined() {
		boolean isLogined = pref.getBoolean("isLogined", false);
		return isLogined;
	}
	public double getRadiusForEventAround(){
		return get("app.setting.event.radius", (float)1.0);
	}
	
	public void setRadiusForEventAround(double r){
		put("app.setting.event.radius",(float)r);
	}
	
	public Boolean getSettingFacebook(){
		return get("app.setting.facebook.enable", false);
	}
	
	public void setSettingFacebook(Boolean r){
		put("app.setting.facebook.enable",r);
	}
	
	public Boolean getSettingLocation(){
		return get("app.setting.location.location", false);
	}
	
	public void setSettingLocation(Boolean r){
		put("app.setting.location.location",r);
	}
	
	public String getAccessToken(){
		return get("app.AccessToken", null);
	}
	
	public void setAccessToken(String r){
		put("app.AccessToken",r);
	}
	
	public Boolean getAccessHeadPhone(){
		return get("app.AccessHeadPhone", false);
	}
	
	public void setAccessHeadPhone(Boolean r){
		put("app.AccessHeadPhone",r);
	}
	public boolean getNotifyEvents(){
		return get("app.setting.event.enable", false);
	}
	public void setNotifyEvents(boolean r){
		put("app.setting.event.enable",r);
	}
	public boolean restoreFaceBook(Facebook fb){
		fb.setAccessToken(get("fb_access_token",""));
		fb.setAccessExpires(get("fb_access_token_expires",Long.MIN_VALUE));
		return fb.isSessionValid();
	}

}
