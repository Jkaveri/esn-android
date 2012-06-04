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

public class Users implements KvmSerializable {
	
	public String Email;	
	public String Password;	
	public int RoleID;	
	public int AccID;
	public String Name;	
	public String Address;	
	public String District;	
	public String City;	
	public String Country;	
	public String Avatar;	
	public String Phone;	
	public String DateOfBirth;	
	public boolean Gender;	
	public int ShareID;	
	public String Favorite;	
	public String AccessToken;	
	public String VerificationCode;	
	public Date DayCreate;	
	public Boolean IsOnline;	
	public int Status;
	
	public Users(){}
	
	public Users(String email, String password, int roleId, int accId, String name,String address, String district, String city, String country, String avatar,String phone, String dateOfBirth,boolean gender, int shareId, String favorite,String accessToken, String verificationCode, Date dayCreate,Boolean isOnline, int status)
	{
		Email = email;
		Password = password;
		RoleID = roleId;
		AccID = accId;
		Name = name;
		Address=address;
		District=district;
		City = city;
		Country = country;
		Avatar = avatar;
		Phone = phone;
		DateOfBirth=dateOfBirth;
		Gender = gender;
		ShareID=shareId;
		Favorite=favorite;
		AccessToken=accessToken;
		VerificationCode = verificationCode;
		DayCreate=dayCreate;
		IsOnline=isOnline;
		Status = status;		
	}
	
	@Override
	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return Email;	
		case 1 :
			return Password;
		case 2 :
			return RoleID;
		case 3 :
			return AccID;
		case 4:
			return Name;
		case 5 : 
			return Address;
		case 6 :
			return District;
		case 7 :
			return City;
		case 8 :
			return Country;
		case 9 : 
			return Avatar;
		case 10 :
			return Phone;
		case 11 :
			return DateOfBirth;
		case 12 :
			return Gender;
		case 13 :
			return ShareID;
		case 14 :
			return Favorite;					
		case 15 : 
			return AccessToken;
		case 16 : 
			return VerificationCode;
		case 17 :
			return DayCreate;
		case 18 :
			return IsOnline;
		case 19 :
			return Status;
		default:
			return null;
		}
		
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
		switch (index) {
		case 0 :
			info.name = "Email";
			info.type = PropertyInfo.STRING_CLASS;
		case 1 :
			info.name = "Password";
			info.type = PropertyInfo.STRING_CLASS;
		case 2 :
			info.name = "RoleID";
			info.type = PropertyInfo.INTEGER_CLASS;
		case 3 :
			info.name = "AccID";
			info.type = PropertyInfo.INTEGER_CLASS;
		case 4 :
			info.name = "Name";
			info.type = PropertyInfo.STRING_CLASS;
		case 5 :
			info.name = "Address";
			info.type = PropertyInfo.STRING_CLASS;
		case 6 :
			info.name = "District";
			info.type = PropertyInfo.STRING_CLASS;
		case 7 :
			info.name = "City";
			info.type = PropertyInfo.STRING_CLASS;
		case 8 :
			info.name = "Country";
			info.type = PropertyInfo.STRING_CLASS;
		case 9 :
			info.name = "Avatar";
			info.type = PropertyInfo.STRING_CLASS;
		case 10 :
			info.name = "Phone";
			info.type = PropertyInfo.STRING_CLASS;
		case 11 :
			info.name = "DateOfBirth";
			info.type = PropertyInfo.STRING_CLASS;
		case 12 : 
			info.name = "Gender";
			info.type = PropertyInfo.BOOLEAN_CLASS;
		case 13 :
			info.name = "ShareID";
			info.type = PropertyInfo.INTEGER_CLASS;
		case 14 :
			info.name = "Favorite";
			info.type = PropertyInfo.STRING_CLASS;
		case 15 :
			info.name = "AccessToken";
			info.type = PropertyInfo.STRING_CLASS;
		case 16 :
			info.name = "VerificationCode";
			info.type = PropertyInfo.STRING_CLASS;
		case 17 :
			info.name = "DayCreate";
			info.type = MarshalDate.DATE_CLASS;
		case 18 :
			info.name = "IsOnline";
			info.type = PropertyInfo.BOOLEAN_CLASS;
		case 19 :
			info.name = "Status";
			info.type = PropertyInfo.INTEGER_CLASS;;
		default:
			break;
		}
	}

	@Override
	public void setProperty(int index, Object value) {
		switch (index) {
		case 0:	
			Email = value.toString();
			break;
		case 1:
			Password = value.toString();
			break;
		case 2 :
			RoleID = Integer.parseInt(value.toString());
			break;
		case 3 :
			AccID = Integer.parseInt(value.toString());
			break;
		case 4 : 
			Name = value.toString();
			break;
		case 5 :
			Address = value.toString();
			break;
		case 6 :
			District = value.toString();
			break;
		case 7 :
			City = value.toString();
			break;
		case 8 :
			Country = value.toString();
			break;
		case 9 :
			Avatar = value.toString();
			break;
		case 10 :
			Phone = value.toString();
			break;
		case 11 :
			DateOfBirth = value.toString();
			break;
		case 12 :
			Gender = Boolean.parseBoolean(value.toString());
			break;
		case 13 :
			ShareID = Integer.parseInt(value.toString());
			break;
		case 14 :
			Favorite = value.toString();
			break;
		case 15 :
			AccessToken = value.toString();
			break;
		case 16 :
			VerificationCode = value.toString();
			break;
		case 17 :
			DayCreate = IsoDate.stringToDate(value.toString(),0);
			break;
		case 18 :
			IsOnline = Boolean.parseBoolean(value.toString());
			break;
		case 19 :
			Status = Integer.parseInt(value.toString());
			break;
		default:
			break;
		}
	}
}
