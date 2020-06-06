package gov.noaa.pmel.tmap.las.client.laswidget;

public class DatasetFilter {
	boolean include;
	String attribute;
	String value;
	
	public DatasetFilter(boolean include, String attribute, String value) {
		super();
		this.include = include;
		this.attribute = attribute;
		this.value = value;
	}
	public boolean isInclude() {
		return include;
	}
	public void setInclude(boolean include) {
		this.include = include;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
