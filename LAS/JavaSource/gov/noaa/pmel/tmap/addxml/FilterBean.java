package gov.noaa.pmel.tmap.addxml;

import org.jdom.*;

/**
 * <p>Title: addXML</p>
 *
 * <p>Description: Reads local or OPeNDAP netCDF files and generates LAS XML
 * configuration information.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: NOAA/PMEL/TMAP</p>
 *
 * @author RHS
 * @version 1.0
 */
public class FilterBean {
	private String action;
	private String contains;
	private String equals;
	private String containstag;
	private String equalstag;
	public FilterBean() {
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setContains(String contains) {
		this.contains = contains;
	}

	public void setEquals(String equals) {
		this.equals = equals;
	}

	public void setContainstag(String containstag) {
		this.containstag = containstag;
	}

	public String getAction() {
		return action;
	}

	public String getContains() {
		return contains;
	}

	public String getEquals() {
		return equals;
	}

	public String getContainstag() {
		return containstag;
	}

	public String getEqualstag() {
		return equalstag;
	}

	public void setEqualstag(String equalstag) {
		this.equalstag = equalstag;
	}

	/**
	 * toXml
	 *
	 * @return Element
	 */
	public Element toXml() {
		Element filterE = new Element("filter");
		filterE.setAttribute("action", action);
		if (contains != null) {
			filterE.setAttribute("contains", contains);
		} else if (equals != null) {
			filterE.setAttribute("equals", equals);
		} else if ( containstag != null ) {
			filterE.setAttribute("contains-tag", containstag);
		} else if ( equalstag != null ) {
			filterE.setAttribute("equals-tag", equalstag);
		}
		return filterE;
	}
}
