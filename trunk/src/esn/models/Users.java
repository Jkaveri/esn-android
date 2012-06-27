package esn.models;

import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import org.kobjects.isodate.IsoDate;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import android.R.bool;
import android.R.integer;

public class Users {
	
	public int AccID;
	public int RoleID;
	public String Email;	
	public String Password;
	public String AccessToken;
	public String VerificationCode;
	public Date DayCreate;	
	public Boolean IsOnline;
	public int Status;	
	public String Name;
	public String Birthday;
	public boolean Gender;
	public String Phone;
	public String Address;
	public String Street;
	public String District;
	public String City;
	public String Country;
	public String Favorite;
	public String Avatar;
	
	
	public Users(){}
	
	public Users(int accId, int roleId,String email, String password,String accessToken, String verificationCode, 
			Date dayCreate,Boolean isOnline, int status,String name, String birthday, boolean gender,String phone,
			String address,String street,String district, String city, String country, String favorite, String avatar )
	{
		AccID = accId;
		RoleID = roleId;		
		Email = email;
		Password = password;
		AccessToken=accessToken;
		VerificationCode = verificationCode;
		DayCreate=dayCreate;
		IsOnline=isOnline;		
		Status = status;
		Name=name;
		Birthday = birthday;
		Gender = gender;
		Phone = phone;
		Address = address;
		Street = street;
		District = district;
		City = city;
		Country = country;
		Favorite = favorite;
		Avatar = avatar;
	}
	
}
