package esn.classes;

public class EsnListItem {
	private CharSequence title;
	private String subtitle;
	private int icon;
	private int id;
	private boolean checked;
	private String tagName;
	private int type;
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
	public CharSequence getTitle() {
		return title;
	}
	public void setTitle(CharSequence t) {
		this.title = t;
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
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	
	
}
