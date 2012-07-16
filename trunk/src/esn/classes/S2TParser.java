package esn.classes;

public class S2TParser {
	private String strRecog;
	private String action;
	private String event;
	
	public void parse(String resultRecog){
		int fi = resultRecog.indexOf('|');
		int li = resultRecog.lastIndexOf('|');
		
		action = resultRecog.substring(0, fi).trim();
		event = resultRecog.substring(fi+1, li).trim();
		strRecog = resultRecog.substring(li + 1).trim();
		strRecog = strRecog.substring(0, 1).toUpperCase() + strRecog.substring(1);
	}
	
	public String getStrRecog() {
		return strRecog;
	}
	
	public String getAction() {
		return action;
	}
	
	public String getEvent() {
		return event;
	}
	
	public String toString(){
		return "Action: " + action + ", Event: " + event + ", Recognition: " + strRecog;
	}
}
