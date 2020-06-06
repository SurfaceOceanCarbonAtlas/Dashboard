package gov.noaa.pmel.tmap.las.client.serializable;


import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AxisSerializable extends Serializable implements IsSerializable {
	
	
	String type;
	String hi;
	String lo;
	ArangeSerializable arange;
	String label;
	String units;
	String[] names;
	String[] values;
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @return the hi
	 */
	public String getHi() {
	    if ( hi == null ) {
	        if ( values != null ) {
	            hi = values[values.length - 1];
	        }
	    }
		return hi;
	}
	/**
	 * @return the lo
	 */
	public String getLo() {
	    if ( lo == null ) {
	        if ( values != null ) {
	            lo = values[0];
	        }
	    }
		return lo;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @param hi the hi to set
	 */
	public void setHi(String hi) {
		this.hi = hi;
	}
	/**
	 * @param lo the lo to set
	 */
	public void setLo(String lo) {
		this.lo = lo;
	}
	/**
	 * @return the id
	 */
	public String getID() {
		return ID;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.ID = id;
	}
	
	public ArangeSerializable getArangeSerializable() {
		return arange;
	}
	public void setArangeSerializable(ArangeSerializable arange) {
		this.arange = arange;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String[] getNames() {
		return names;
	}
	public void setNames(String[] names) {
		this.names = names;
	}
	public String[] getValues() {
		return values;
	}
	public void setValues(String[] values) {
		this.values = values;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
    public boolean isOne() {
        if ( values != null && values.length == 1 ) {
            return true;
        } else {
            if ( getHi().equals(getLo())) {
                return true;
            }
        }
        return false;
    }
	
}
