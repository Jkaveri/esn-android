package esn.models;

import org.ksoap2.serialization.SoapObject;

public class Users {
	String METHOD_NAME="InsertUser";
	String NAME_SPACE="";
	String SOAP_ACTION=NAME_SPACE+METHOD_NAME;
	String URL="";
    private int id;
    private String username;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
    
	public void Insert(){
		SoapObject soap = new SoapObject(NAME_SPACE, METHOD_NAME);
		
	}
}
