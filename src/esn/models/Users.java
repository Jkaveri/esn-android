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
	
	@Override
	public Object getProperty(int index) {
		switch (index) {
		
		case 0 :
			return AccID;
		case 1 :
			return RoleID;
		case 2:
			return Email;	
		case 3 :
			return Password;		
		case 4:
			return AccessToken;
		case 5 : 
			return VerificationCode;
		case 6 :
			return DayCreate;
		case 7 :
			return IsOnline;
		case 8 :
			return Status;
		case 9 :
			return Name;
		case 10 :
			return Birthday;
		case 11:
			return Gender;
		case 12:
			return Phone;
		case 13 :
			return Address;
		case 14 :
			return Street;
		case 15 :
			return District;
		case 16 :
			return City;
		case 17 :
			return Country;
		case 18 :
			return Favorite;
		case 19:
			return Avatar;
		case 20:
			return Avatar;
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
			info.name = "AccID";
			info.type = PropertyInfo.INTEGER_CLASS;
		case 1 :
			info.name = "RoleID";
			info.type = PropertyInfo.INTEGER_CLASS;				
		case 2 :
			info.name = "Email";
			info.type = PropertyInfo.STRING_CLASS;
		case 3 :
			info.name = "Password";
			info.type = PropertyInfo.STRING_CLASS;	
		case 4 :
			info.name = "AccessToken";
			info.type = PropertyInfo.STRING_CLASS;
		case 5 :
			info.name = "VerificationCode";
			info.type = PropertyInfo.STRING_CLASS;
		case 6 :
			info.name = "DayCreate";
			info.type = MarshalDate.DATE_CLASS;
		case 7 :
			info.name = "IsOnline";
			info.type = PropertyInfo.BOOLEAN_CLASS;
		case 8 :			
			info.name = "Status";
			info.type = PropertyInfo.INTEGER_CLASS;
		case 9 :
			info.name="Name";
			info.type = PropertyInfo.STRING_CLASS;
		case 10 :
			info.name = "Birthday";
			info.type = PropertyInfo.STRING_CLASS;
		case 11 :
			info.name = "Gender";
			info.type = PropertyInfo.BOOLEAN_CLASS;
		case 12 :
			info.name = "Phone";
			info.type = PropertyInfo.STRING_CLASS;
		default:
			break;
		}
	}

	@Override
	public void setProperty(int index, Object value) {
		switch (index) {
		case 0 :
			AccID = Integer.parseInt(value.toString());
			break;
		case 1 :
			RoleID = Integer.parseInt(value.toString());
			break;		
		case 2:	
			Email = value.toString();
			break;
		case 3:
			Password = value.toString();
			break;
		case 4 :
			AccessToken = value.toString();
			break;
		case 5 :
			VerificationCode = value.toString();
			break;
		case 6 :
			DayCreate = IsoDate.stringToDate(value.toString(),0);
			break;
		case 7 :
			IsOnline = Boolean.parseBoolean(value.toString());
			break;
		case 8 :
			Status = Integer.parseInt(value.toString());
			break;
		case 9 :
			Name = value.toString();
		case 10 :
			Birthday = value.toString();
			break;
		case 11 :
			Gender = Boolean.parseBoolean(value.toString());
			break;
		case 12 :
			Phone = value.toString();
			break;
		default:
			break;
		}
	}
}
