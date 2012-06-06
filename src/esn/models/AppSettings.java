package esn.models;

public class AppSettings {
	private AppSettings instance;
	public AppSettings getInstance(){
		if(instance==null) instance = new AppSettings();
		return instance;
	}
}
