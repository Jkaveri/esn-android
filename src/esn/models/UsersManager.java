package esn.models;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.isodate.IsoDate;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import android.R.bool;
import android.os.Bundle;
import android.text.style.BulletSpan;

import esn.classes.EsnWebServices;
import esn.classes.HttpHelper;

public class UsersManager {

		 
    String URL = "http://10.0.2.2/esn/AccountsWS.asmx/";
	
    HttpHelper helper = new HttpHelper(URL);
    
	public UsersManager() {
		
	}
	
	public boolean Login(String email, String password)
	{			
		try {
			
			JSONObject params = new JSONObject();
			
			params.put("email", email);
			params.put("password", password);
			
			JSONObject jsonObject = helper.invokeWebMethod("Login",params);
			
			boolean rs = jsonObject.getBoolean("d"); 
			
			return rs;
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return false;
	}	
	
	public int Register(Users user)
	{	
		int rs = 0;
					
		try
		{
			JSONObject params = new JSONObject();
			
			params.put("name", user.Name);
			params.put("email", user.Email);
			params.put("password", user.Password );
			params.put("birthday", user.Birthday);
			params.put("phone", user.Phone);
			params.put("gender", user.Gender);
			params.put("accessToken", user.AccessToken);
			
			JSONObject jsonObject = helper.invokeWebMethod("Register", params);
				
			rs = jsonObject.getInt("d");
		}
		catch (Exception e) {
			rs=-2;
		}
		
		return rs;
	}

	public boolean CheckEmailExists(String email)
	{	
		try {
		
			JSONObject params = new JSONObject();
			
			params.put("email", email);
			
			JSONObject jsonObject = helper.invokeWebMethod("CheckEmailExisted",params);
			
			boolean rs = jsonObject.getBoolean("d"); 
			
			return rs;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public Boolean ChangePassword(String email, String currentPass, String newPass)
	{
		try {
			
			JSONObject params = new JSONObject();
			
			params.put("email", email);
			params.put("oldPassword",currentPass);
			params.put("newPassword", newPass);
			
			JSONObject jsonObject = helper.invokeWebMethod("ChangePassword",params);
			
			boolean rs = jsonObject.getBoolean("d"); 
			
			return rs;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Users RetrieveByEmail(String email)
	{
		Users user = new Users();
		
		try
		{
			JSONObject params = new JSONObject();
			
			params.put("email", email);
			
			JSONObject jsonObject = helper.invokeWebMethod("RetrieveByEmail", params);
			
			JSONObject rs = jsonObject.getJSONObject("d");
			
			JSONObject p = rs.getJSONObject("Profile");
			
			user.Password = rs.getString("Password");
			
			user.Name = p.getString("Name");
			
			user.Birthday = p.getString("Birthday");
			
			user.Gender = p.getBoolean("Gender");
			
			user.Phone = p.getString("Phone");
			
			user.Address = p.getString("Address");
			
			user.Street = p.getString("Street");
			
			user.District = p.getString("District");
			
			user.City = p.getString("City");
			
			user.Country = p.getString("Country");
			
			user.Favorite = p.getString("Favorite");
			
			user.Avatar = p.getString("Avatar");
		}
		catch (Exception e) {
		
		}
		
		return user;
	}
	
	public Boolean UpdateProfile(Users user)
	{
		Boolean rs = false;
		
		JSONObject params = new JSONObject();
		
		try
		{
			params.put("name", user.Name);
			params.put("gender",user.Gender);
			params.put("birthday", user.Birthday);
			params.put("phone", user.Phone);
			params.put("address", user.Address);
			params.put("street", user.Street);
			params.put("district", user.District);
			params.put("city", user.City);
			params.put("country", user.Country);
			params.put("favorite", user.Favorite);
			params.put("avatar", user.Avatar);
			
			JSONObject jsonObject = helper.invokeWebMethod("ChangePassword",params);
			
			rs = jsonObject.getBoolean("d");
		}
		catch (Exception e) {
			// TODO: handle exception
		}		 
		return rs;
	}
}