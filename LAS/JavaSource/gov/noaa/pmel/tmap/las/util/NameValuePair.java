package gov.noaa.pmel.tmap.las.util;



public class NameValuePair  implements Comparable<NameValuePair> {
    
    private String name;
    private String value;
    
    public NameValuePair(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }
	public int compareTo(NameValuePair o) {
		return getName().compareTo(o.getName());
	}
}
