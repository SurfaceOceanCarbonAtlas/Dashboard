/**
 * 
 */
package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import org.jdom.Element;

/**
 * @author Roland Schweitzer
 *
 */
public class Variable extends Container implements VariableInterface {
    
    String dsid;
    String DSName;
    String catid;
    
    public Variable(Element variable, String dsid) {
        super(variable);
        setDSID(dsid);
    }
    public Variable(Element variable, String catid, String dsid, String DSName) {
        super(variable);
        setCATID(catid);
        setDSID(dsid);
        setDSName(DSName);
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.VariableInterface#getXPath()
     */
    public String getXPath() {
        return "/lasdata/datasets/dataset[@ID='"+dsid+"']/variables/variable[@ID='"+getID()+"']";
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.VariableInterface#getDSID()
     */
    public String getDSID() {
        return dsid;
    }
    public String getCATID() {
        return catid;
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.VariableInterface#setDSID(java.lang.String)
     */
    public void setDSID(String dsid) {
        this.dsid = dsid;
        element.setAttribute("dsid", dsid);
    }
    public void setCATID(String catid) {
        this.catid = catid;
        element.setAttribute("catid", catid);
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.VariableInterface#getGridID()
     */
    public String getGridID() {
        return element.getChild("grid").getAttributeValue("IDREF");
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.VariableInterface#setGridID(java.lang.String)
     */
    public void setGridID(String gridID) {
        element.getChild("grid").setAttribute("IDREF",gridID);
    }
    
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.VariableInterface#setURL(java.lang.String)
     */
    public void setURL(String var_url) {
        element.setAttribute("url", var_url);
    }
    
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.VariableInterface#getUnits()
     */
    public String getUnits() {
    	String uni = element.getAttributeValue("units");
    	return uni;
    	//return element.getAttributeValue("units");
    }
    public VariableSerializable getVariableSerializable() {
    	VariableSerializable variableSerializable = new VariableSerializable();
    	variableSerializable.setName(getName());
    	variableSerializable.setID(getID());
    	variableSerializable.setCATID(getCATID());
    	variableSerializable.setDSID(getDSID());
    	variableSerializable.setDSName(getDSName());
    	variableSerializable.setAttributes(getAttributesAsMap());
    	variableSerializable.setProperties(getPropertiesAsMap());
    	variableSerializable.setGrid(getGridSerializable());
    	if ( isVector() ) {
    		variableSerializable.setVector(isVector());
    		variableSerializable.setComponents(getComponents());
    	}
    	return variableSerializable;
    }
    public GridSerializable getGridSerializable() {
    	Grid grid = new Grid(getElement().getChild("grid"));
    	return grid.getGridSerializable();
    }
    public Grid getGrid() {
    	return new Grid(getElement().getChild("grid"));
    }
	/**
	 * @return the dsid
	 */
	public String getDsid() {
		return dsid;
	}
	/**
	 * @return the dSName
	 */
	public String getDSName() {
		return DSName;
	}
	/**
	 * @param dsid the dsid to set
	 */
	public void setDsid(String dsid) {
		this.dsid = dsid;
	}
	/**
	 * @param name the dSName to set
	 */
	public void setDSName(String name) {
		DSName = name;
		element.setAttribute("dsname", name);
	}
	public String getGridType() {
		for (Iterator attrIt = getAttributes().iterator(); attrIt.hasNext();) {
			NameValuePair attr = (NameValuePair) attrIt.next();
			if ( attr.getName().equals("grid_type") ) {
				return attr.getValue();
			}
		}
		return null;
	}
	public boolean isDiscrete() {
		boolean discrete = false;
		for (Iterator attrIt = getAttributes().iterator(); attrIt.hasNext();) {
			NameValuePair attr = (NameValuePair) attrIt.next();
			if ( attr.getName().equals("grid_type") && ( attr.getValue().equals("trajectory") || 
		            attr.getValue().equals("scattered") || 
		            attr.getValue().equals("point") ||
		            attr.getValue().equals("timeseries") ||
		            attr.getValue().equals("profile" ) ) ) {
				discrete = true;
			}
		}
		return discrete;
	}
	public boolean isVector() {
		boolean vector = false;
		for (Iterator attrIt = getAttributes().iterator(); attrIt.hasNext();) {
			NameValuePair attr = (NameValuePair) attrIt.next();
			if ( attr.getName().equals("grid_type") && attr.getValue().equals("vector") ) {
				vector = true;
			}
		}
		return vector;
	}
	
	public boolean hasW() {
		boolean hasw = false;
		if ( isVector() ) {
			if ( getComponents().size() == 3 ) {
			   hasw = true;
			}
		}
		return hasw;
	}
	public List<String> getComponents() {
		
		List<String> components = new ArrayList<String>();
		
		if ( isVector() ) {
			List comps = getElement().getChildren("variable");
			for (Iterator compsIt = comps.iterator(); compsIt.hasNext();) {
				Element var = (Element) compsIt.next();
				components.add(var.getAttributeValue("IDREF"));
			}
		}
		
		return components;
	}
}
