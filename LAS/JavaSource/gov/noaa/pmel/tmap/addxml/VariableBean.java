package gov.noaa.pmel.tmap.addxml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Element;

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
public class VariableBean extends LasBean {
	private String name;
	private String url;
	private String units;
	private GridBean grid;
	private Map<String, String> attributes = new HashMap<String, String>();
	private HashMap<String, HashMap<String, String>> properties = new HashMap<String, HashMap<String, String>>();
	public VariableBean() {
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public void setGrid(GridBean grid) {

		this.grid = grid;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getUnits() {
		return units;
	}

	public GridBean getGrid() {

		return grid;
	}
    public HashMap<String, HashMap<String, String>> getProperties() {
    	return properties;
    }
    
	public void setProperty(String group, String name, String value) {
		HashMap<String, String> groupMap = properties.get(group);
		if ( groupMap == null ) {
			groupMap = new HashMap<String, String>();
		}
		groupMap.put(name, value);
		properties.put(group, groupMap);
	}
	public void addAttribute(String name, String value) {
	    attributes.put(name, value);
	}
    public Element toXml(boolean seven) {
        Element variable;

        if ( seven ) {
            variable = new Element("variable");
            variable.setAttribute("ID", this.getElement());
        } else {
            variable = new Element(this.getElement());
        }
        variable.setAttribute("name", name);
        variable.setAttribute("units", units);
        if ( url != null && url != "" ) {
            variable.setAttribute("url", url);
        }
        if ( seven ) {
            Element g = new Element("grid");
            g.setAttribute("IDREF", grid.getElement());
            variable.addContent(0, g);
        } else {
            Element link = new Element("link");
            link.setAttribute("match", "/lasdata/grids/"+grid.getElement());
            variable.addContent(link);
        }
        for (Iterator attIt = attributes.keySet().iterator(); attIt.hasNext();) {
            String name = (String) attIt.next();
            String value = attributes.get(name);
            variable.setAttribute(name, value);
        }
        if ( properties.size() > 0 ) {
                Element propertiesE = new Element("properties");
                if ( seven ) {
                    for (Iterator groupsIt = properties.keySet().iterator(); groupsIt.hasNext();) {
                        String group = (String) groupsIt.next();
                        HashMap<String, String> groupMap = properties.get(group);
                        Element groupE = new Element("property_group");
                        groupE.setAttribute("type", group);
                        for (Iterator nameIt = groupMap.keySet().iterator(); nameIt.hasNext();) {
                            Element prop = new Element("property");
                            String name = (String) nameIt.next();
                            String value = groupMap.get(name);
                            Element n = new Element("name");
                            n.setText(name);
                            Element v = new Element("value");
                            v.setText(value);
                            prop.addContent(n);
                            prop.addContent(v);
                            groupE.addContent(prop);
                        }
                        propertiesE.addContent(groupE);
                    }
                } else {
                    for (Iterator groupsIt = properties.keySet().iterator(); groupsIt.hasNext();) {
                        String group = (String) groupsIt.next();
                        HashMap<String, String> groupMap = properties.get(group);
                        Element groupE = new Element(group);
                        for (Iterator nameIt = groupMap.keySet().iterator(); nameIt.hasNext();) {
                            String name = (String) nameIt.next();
                            String value = groupMap.get(name);
                            Element n = new Element(name);
                            n.setText(value);
                            groupE.addContent(n);
                        }
                        propertiesE.addContent(groupE);
                    }                
                }
            variable.addContent(propertiesE);
        }
        return variable;
    }
	public Element toXml() {
		return toXml(false);
	}

	@Override
	public boolean equals(LasBean bean) {
		VariableBean b;
		if ( !(bean instanceof VariableBean) ) {
			return false;
		} else {
			b = (VariableBean) bean;
		}
		if ( !url.equals(b.getUrl() )) return false;
		if ( !units.equals(b.getUnits() )) return false;
		if ( !grid.equals(b.getGrid() )) return false;
		if ( !properties.equals(b.getProperties() )) return false;
		/*
		private String url;
		private String units;
		private GridBean grid;
		private HashMap<String, HashMap<String, String>> properties = new HashMap<String, HashMap<String, String>>(); */
		return true;
	}

	public String getShortName() {
		if ( getUrl().contains("#") ) {
			return getUrl().substring(getUrl().indexOf("#")+1);
		} else {
			return getElement();
		}
	}



}
