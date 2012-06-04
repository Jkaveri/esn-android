package esn.models;

import java.util.Hashtable;

import org.kobjects.isodate.IsoDate;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import esn.classes.EsnWebServices;

public class UsersManager {

	
	String NAMESPACE = "http://esnservice.somee.com/";	 
    String URL = "http://esnservice.somee.com/accountservice.asmx";
	
	private EsnWebServices service = new EsnWebServices(NAMESPACE, URL);
	
	public UsersManager() {
		
	}
	
	@SuppressWarnings("null")
	public Users[] getAll()
	{		
		SoapObject response = service.InvokeMethod("GetAllUser");
		
		SoapObject pi = (SoapObject) response.getProperty(0);
		
		int count = pi.getPropertyCount();
		
		Users[] ListUser = null;
		
		for (int i = 0; i < count; i++) {
			
			Users user = new Users();
			
			user.Email = pi.getProperty(0).toString();
			user.Password = pi.getProperty(1).toString();
			user.RoleID = Integer.parseInt(pi.getProperty(2).toString());
			user.Name = pi.getProperty(3).toString();
			user.Address = pi.getProperty(4).toString();
			user.District = pi.getProperty(5).toString();
			user.City = pi.getProperty(6).toString();
			user.Country = pi.getProperty(7).toString();
			user.Avatar = pi.getProperty(8).toString();
			user.Phone = pi.getProperty(9).toString();
			//user.DateOfBirth = IsoDate.stringToDate(pi.getProperty(10).toString(), 0);
			user.Gender = Boolean.parseBoolean(pi.getProperty(11).toString());
			user.ShareID = Integer.parseInt(pi.getProperty(12).toString());
			user.Favorite = pi.getProperty(13).toString();
			user.AccessToken = pi.getProperty(14).toString();
			user.VerificationCode = pi.getProperty(15).toString();
			user.DayCreate = IsoDate.stringToDate(pi.getProperty(16).toString(), 0);
			user.IsOnline = Boolean.parseBoolean(pi.getProperty(17).toString());
			user.Status = Integer.parseInt(pi.getProperty(18).toString());
			
			ListUser[i] = user;
		}
		
		return ListUser;
	}
	
	public Boolean Register(Users user)
	{
		String METHOD_NAME = "Register";
		
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		
		params.put("user", user);
		
		service.addMaping("Users", Users.class);
		
		SoapObject responseObject = service.InvokeMethod(METHOD_NAME,params);
					
		return Boolean.parseBoolean(responseObject.toString());
	}
	
	public Users Login(Users user)
	{
		// dat bien user = null
		Users  usr = null;
		//ten ham
		String METHOD_NAME = "Login";
		//parameter (tham so truyen vao ham)
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		//them tham so user
		params.put("user", user);
		//add maping
		service.addMaping("Users", Users.class);
		//goi ham nhan ve ket qua
		SoapObject result = service.InvokeMethod(METHOD_NAME,params);
		//neu ket qua !=null
		if(result.getPropertyCount()>0)
		{
			//lay ket qua (user)
			SoapObject userResult = (SoapObject) result.getProperty(0);	
			//khoi tao user
			usr = new Users();
			//set email
			usr.Email = userResult.getProperty(0).toString();
			//set password
			usr.Name = userResult.getProperty(3).toString();
			
		}
		return usr;
		
	}
	
	public Boolean CheckEmailExists(Users user)
	{
		String METHOD_NAME ="CheckEmail";
		
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		
		params.put("user", user);
		
		service.addMaping("Users", Users.class);
		
		SoapObject responseObject = service.InvokeMethod(METHOD_NAME,params);
		
		return Boolean.parseBoolean(responseObject.toString());
	}
	
	public Users GetById(Users user)
	{
		String METHOD_NAME ="GetById";
		
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		
		params.put("user", user);
		
		service.addMaping("Users", Users.class);
		
		SoapObject  result = service.InvokeMethod(METHOD_NAME, params);
		
		Users usr = null;
		
		if(result !=null)
		{
			SoapObject userResult = (SoapObject) result.getProperty(0);
			
			usr =new Users();
			
			usr.Email = userResult.getProperty(0).toString();
			usr.Password = userResult.getProperty(1).toString();
			usr.RoleID = Integer.parseInt(userResult.getProperty(2).toString());
			usr.Name = userResult.getProperty(3).toString();
			usr.Address = userResult.getProperty(4).toString();
			usr.District = userResult.getProperty(5).toString();
			usr.City = userResult.getProperty(6).toString();
			usr.Country = userResult.getProperty(7).toString();
			usr.Avatar = userResult.getProperty(8).toString();
			usr.Phone = userResult.getProperty(9).toString();
			//usr.DateOfBirth = IsoDate.stringToDate(userResult.getProperty(10).toString(), 0);
			usr.Gender = Boolean.parseBoolean(userResult.getProperty(11).toString());
			usr.ShareID = Integer.parseInt(userResult.getProperty(12).toString());
			usr.Favorite = userResult.getProperty(13).toString();
			usr.AccessToken = userResult.getProperty(14).toString();
			usr.VerificationCode = userResult.getProperty(15).toString();
			usr.DayCreate = IsoDate.stringToDate(userResult.getProperty(16).toString(),0);
			usr.IsOnline = Boolean.parseBoolean(userResult.getProperty(18).toString());
			usr.Status = Integer.parseInt(userResult.getProperty(18).toString());			
			
		}
		return usr;
	}	
}
