package esn.classes;

public class EsnListItem {
	private String title;
	private String subtitle;
	private int icon;
	private int id;
	public EsnListItem(int id){
		this.id = id;
	}
	public EsnListItem(){
		
	}
	public EsnListItem(String title, String subtitle, int icon) {
		this.title = title;
		this.subtitle = subtitle;
		this.icon = icon;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	
	public int getId() {
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}
	
	
	
}
