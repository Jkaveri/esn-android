package esn.models;

public class ListNavigationItem {
	private CharSequence text;
	private int icon;
	public CharSequence getText() {
		return text;
	}
	public void setText(CharSequence text) {
		this.text = text;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		ListNavigationItem other = (ListNavigationItem)o;
		return (other.getText().equals(this.text) && other.getIcon() == this.icon);
	}
}
