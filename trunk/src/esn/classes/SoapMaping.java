package esn.classes;


public class SoapMaping {
	private String namespace;
	private String name;
	private Class clazz;
	public SoapMaping(String NAMESPACE,String Name, Class clazz){
		this.namespace = NAMESPACE;
		this.name = Name;
		this.clazz = clazz;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class getClazz() {
		return clazz;
	}
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	
	
}
