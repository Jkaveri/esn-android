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
	public Date DateCreated;	
	public Boolean IsOnline;
	public int Status;	
	public String Name;
	public Date Birthday;
	public boolean Gender;
	public String Phone;
	public String Address;
	public String Street;
	public String District;
	public String City;
	public String Country;
	public String Favorite;
	public String Avatar;
	public String fbID;
	
	public Users(){}	
}
