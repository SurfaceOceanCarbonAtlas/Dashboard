package gov.noaa.pmel.tmap.addxml;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class DatasetBean extends LasBean {
	private String name;
	private String doc;
	private String url;
	private List<VariableBean> variables = new ArrayList<VariableBean>();
	private String comment;
	private String version;
	private String creator;
	private String group_name;
	private String group_type;
	private String group_id;
	private String update_time;
	private String update_interval;
	private String created;
	private String expires;
	private long nextUpdate;
	private HashMap<String, HashMap<String, String>> properties = new HashMap<String, HashMap<String, String>>();

	/**
	 * @return the nextUpdate
	 */
	public long getNextUpdate() {
		return nextUpdate;
	}

	/**
	 * @param nextUpdate the nextUpdate to set
	 */
	public void setNextUpdate(long nextUpdate) {
		this.nextUpdate = nextUpdate;
	}

	public DatasetBean() {
	}

	public void addAllVariables(List<VariableBean> var) {
		if (variables == null) {
			variables = new ArrayList<VariableBean>();
		}
		this.variables.addAll(var);
	}

	public void addVariable(VariableBean var) {
		if (variables == null) {
			variables = new ArrayList<VariableBean>();
		}
		this.variables.add(var);
	}

	public String getComment() {
		return comment;
	}

	/**
	 * @return the created
	 */
	public String getCreated() {
		return created;
	}

	public String getCreator() {
		return creator;
	}

	public String getDoc() {
		return doc;
	}

	/**
	 * @return the expires
	 */
	public String getExpires() {
		return expires;
	}

	/**
	 * @return the group_id
	 */
	public String getGroup_id() {
		return group_id;
	}

	/**
	 * @return the group_name
	 */
	public String getGroup_name() {
		return group_name;
	}

	/**
	 * @return the group_type
	 */
	public String getGroup_type() {
		return group_type;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the update_interval
	 */
	public String getUpdate_interval() {
		return update_interval;
	}

	/**
	 * @return the update_time
	 */
	public String getUpdate_time() {
		return update_time;
	}

	public String getUrl() {
		return url;
	}

	public List<VariableBean> getVariables() {
		return variables;
	}

	public String getVersion() {
		return version;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(String created) {
		this.created = created;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}

	/**
	 * @param expires the expires to set
	 */
	public void setExpires(String expires) {
		this.expires = expires;
	}

	/**
	 * @param group_id the group_id to set
	 */
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	/**
	 * @param group_name the group_name to set
	 */
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	/**
	 * @param group_type the group_type to set
	 */
	public void setGroup_type(String group_type) {
		this.group_type = group_type;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param update_interval the update_interval to set
	 */
	public void setUpdate_interval(String update_interval) {
		this.update_interval = update_interval;
	}

	/**
	 * @param update_time the update_time to set
	 */
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setVariables(List<VariableBean> variables) {
		this.variables = variables;
	}

	public void setVersion(String version) {
		this.version = version;
	}
 
	public void setProperty(String group, String name, String value) {
		HashMap<String, String> groupMap = properties.get(group);
		if ( groupMap == null ) {
			groupMap = new HashMap<String, String>();
			properties.put(group, groupMap);
		}
		groupMap.put(name, value);
	}
	public HashMap<String, String> getPropertyGroup(String group) {
		return properties.get(group);
	}
	public Element toXml(boolean seven) {
	    String e = this.getElement();
        boolean null_bean = false;
        if ( e == null ) {
            e = "empty_"+String.valueOf(new Date().getTime());
            null_bean = true;
        }
        
        Element dataset;
        
        if ( seven ) {
            dataset = new Element("dataset");
            dataset.setAttribute("ID", e);
        } else {
            dataset = new Element(e);
        }
        if ( null_bean ) {
            return dataset;
        }
        if ( comment != null ) {
            dataset.addContent(new Comment(comment));
        }
        dataset.setAttribute("name",this.getName());

        if (this.getGroup_name() != null) {
            dataset.setAttribute("group_name", this.getGroup_name());
        }

        if ( this.getGroup_type() != null ) {
            dataset.setAttribute("group_type", this.getGroup_type());
        }

        if ( this.getGroup_id() != null ) {
            dataset.setAttribute("group_id", this.getGroup_id());
        }

        if ( this.getUpdate_time() != null ) {
            dataset.setAttribute("update_time", this.getUpdate_time());
        }

        if ( this.getUpdate_interval() != null ) {
            dataset.setAttribute("update_interval", this.getUpdate_interval());
        }

        if ( this.getCreated() != null ) {
            dataset.setAttribute("created", this.getCreated());
        }

        if ( this.getExpires() != null ) {
            dataset.setAttribute("expires", this.getExpires());
        }

        if ( this.getUrl() != null ) {
            dataset.setAttribute("url", this.getUrl());
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
            dataset.addContent(propertiesE);
        }
        Collections.sort(variables, new NameComparator());
        
        Element variablesElement = new Element("variables");
        List<List<String>> vectors = new ArrayList<List<String>>();
        // First do names containing "zonal".
        for (int v = 0; v < variables.size(); v++ ) {
            VariableBean var = (VariableBean) variables.get(v);
            String vname = var.getShortName();
            if ( vname.toLowerCase(Locale.ENGLISH).contains(Util.zonal) ) {
                List<String> vector = new ArrayList<String>();
                vector.add(var.getElement());
                String match_name = vname.toLowerCase(Locale.ENGLISH).replace(Util.zonal, Util.meridional);
                for (int j = 0; j < variables.size(); j++ ) {
                    VariableBean vVar = variables.get(j);
                    if ( vVar.getShortName().toLowerCase(Locale.ENGLISH).equals(match_name) ) {
                        vector.add(vVar.getElement());
                    }
                    // look for vertical dimension
                    for ( int k = 0; k < Util.verticalComponentNames.length; k++) {
                        String vertical_match = vname.replace(Util.zonal, Util.verticalComponentNames[k]);
                        if ( vVar.getShortName().toLowerCase(Locale.ENGLISH).equals(vertical_match) ) {
                            vector.add(vVar.getElement());
                        }
                    }
                }
                vectors.add(vector);
            }
        }
        if ( vectors.size() > 1 ) {
            
            for ( int v = 0; v < vectors.size(); v++ ) {
                StringBuilder vector_id = new StringBuilder();
                StringBuilder vector_long_name = new StringBuilder("Vector of ");
                List<String> vector = vectors.get(v);
                for (int i = 0; i < vector.size(); i++ ) {
                    vector_id.append(vector.get(i));
                    vector_long_name.append(getVariable(vector.get(i)).getName());
                    if ( i < vectors.size() -1 ) {
                        vector_id.append("_");
                        vector_long_name.append(" and ");
                    }

                    String id = (String) vector.get(i);
                    VariableBean varB = getVariable(id);
                    varB.setProperty("ferret", "palette", "light_centered");
                    varB.setProperty("ferret", "fill_levels", "c");

                }
                addComposite(dataset, vector_long_name.toString(), vector_id.toString(), vector);
            }

        }
        for (int v = 0; v < variables.size(); v++ ) {
            VariableBean var = (VariableBean) variables.get(v);

            String vname = var.getShortName();
            String name = var.getName();
            int matching_occurance_index = -1;
            // Don't match mask variables...
            if ( !name.toLowerCase(Locale.ENGLISH).contains("mask") ) {
                for ( int p = 0; p < Util.vectorPatterns[0].length; p ++ ) {
                    List<String> uv_vectors = new ArrayList<String>();
                    // Look for x, X, u or U
                    char c = Util.vectorPatterns[0][p];
                    int count = Util.countOccurrences(vname, c);
                    if (vname.indexOf(c) >= 0) {
                        // if it has an x, X, u or U make pattern that will match any character in place of the x, X, u or U
                        // to find any potential partners (and including itself)
                        int start = vname.indexOf(c);
                        // There might be more than one x, X, u or U so we have to find them all
                        for ( int i = 0; i < count; i++ ) {
                            String match_range = Util.vectorRanges[p];

                            String pattern_string = vname.replace(String.valueOf(c), match_range);


                            if ( vname.length() > 1 ) {
                                if ( start == 0 ) {
                                    // Matches the first character  
                                    pattern_string = match_range+vname.substring(1);
                                } else if ( start > 0 && start < vname.length()-1  ) {
                                    // Matches somewhere in the middle
                                    pattern_string = vname.substring(0, start-1)+match_range+vname.substring(start+1);
                                } else {
                                    // Matches the last character
                                    pattern_string = vname.substring(0, vname.length()-1)+match_range;
                                }
                            } else {
                                pattern_string = match_range;
                            }
                            Pattern pattern = Pattern.compile(pattern_string);
                            List<String> short_names = new ArrayList<String>();
                            for (int j = 0; j < variables.size(); j++ ) {
                                VariableBean vVar = variables.get(j);
                                Matcher match = pattern.matcher(vVar.getShortName());
                                String short_name = vVar.getShortName();
                                if ( match.matches() ) {
                                    if ( !uv_vectors.contains(vVar.getElement()) && !short_names.contains(short_name) ) {
                                        short_names.add(vVar.getShortName());
                                        uv_vectors.add(vVar.getElement());
                                        matching_occurance_index = start;
                                    }
                                }
                            }
                            start = vname.substring(start).indexOf(c);
                        }
                    }
                    // matching index tells me which of the many x, X, u or U characters in the name had potential partners
                    if ( matching_occurance_index >= 0 ) {
                        // Sort everybody

                    	Collections.sort(uv_vectors);
                        int matches = 0;
                        // index p tells me which of x, X, u or U matched.
                        // index i checks the matches to make sure the partners have the corresponding y, Y, v or V and z, Z, w or W 
                        StringBuilder substituion = new StringBuilder();
                        StringBuilder vector_id = new StringBuilder();
                        StringBuilder vector_long_name = new StringBuilder("Vector of ");
                        for ( int i = 0; i < uv_vectors.size(); i++ ) {
                            if ( uv_vectors.get(i).charAt(matching_occurance_index) == Util.vectorPatterns[i][p]) {
                                matches++;
                                substituion.append(Util.vectorPatterns[i][p]);
                                vector_id.append(uv_vectors.get(i));
                                vector_long_name.append(getVariable(uv_vectors.get(i)).getName());
                                if ( i < uv_vectors.size() -1 ) {
                                    vector_id.append("_");
                                    vector_long_name.append(" and ");
                                }

                            }
                        }
                        // This constructs a name based on the netCDF variables names.

                        // I think we should try based on the long names first and see how we like that.
                        if ( matches > 0 && matches == uv_vectors.size() && uv_vectors.size() > 1 ) {

                            String vector_name = null;
                            // We have some so add it to its parent

                            if ( uv_vectors.get(0).length() > 1 ) {
                                if ( matching_occurance_index == 0 ) {
                                    // Matches the first character  
                                    vector_name = substituion.toString()+"_"+getVariable(uv_vectors.get(0)).getShortName().substring(1);
                                } else if ( matching_occurance_index > 0 && matching_occurance_index < uv_vectors.get(0).length()-1  ) {
                                    // Matches somewhere in the middle
                                    vector_name = getVariable(uv_vectors.get(0)).getShortName().substring(0, matching_occurance_index-1)+
                                    "_"+substituion.toString()+"_"+
                                    uv_vectors.get(0).substring(matching_occurance_index+1);
                                } else {
                                    // Matches the last character
                                    vector_name = getVariable(uv_vectors.get(0)).getShortName().substring(0, uv_vectors.get(0).length()-1)+"_"+substituion.toString();
                                }
                            } else {
                                vector_name = substituion.toString();
                            }
                            addComposite(dataset, vector_long_name.toString(), vector_id.toString(), uv_vectors);
                            
                            // These have been added to a vector composite so we know they are indeed vector components.
                            // So add the centered palette property.
                            for (Iterator vectIt = uv_vectors.iterator(); vectIt.hasNext();) {
                                String id = (String) vectIt.next();
                                VariableBean varB = getVariable(id);
                                varB.setProperty("ferret", "palette", "light_centered");
                                varB.setProperty("ferret", "fill_levels", "c");
                            }
                        }
                    }
                }
            }
        }
        Iterator vars = variables.iterator();
        while (vars.hasNext()) {
            VariableBean var = (VariableBean) vars.next();
            Element varElement;

            if ( seven ) {
                varElement = var.toXml(true);
            } else {
                varElement = var.toXml();
            }
            variablesElement.addContent(varElement);
        }
        dataset.addContent(variablesElement);


        return dataset;
	}
	public Element toXml() {
		return toXml(false);
	}

	private void addComposite(Element dataset, String name, String id, List<String> vectors) {
		Element compositeElement = new Element("composite");
		dataset.addContent(compositeElement);
		Element vectorElement = new Element(id.toString());;
		vectorElement.setAttribute("name", name.toString());
		vectorElement.setAttribute("units", getVariable(vectors.get(0)).getUnits());
		
		Element properties = new Element("properties");
		Element ui = new Element("ui");
		Element defaultElement = new Element("default");
		defaultElement.setText("file:ui.xml#VecVariable");
		ui.addContent(defaultElement);
		properties.addContent(ui);
		
		vectorElement.addContent(properties);
		
		for ( int i = 0; i < vectors.size(); i++ ) {
			Element link = new Element("link");
			link.setAttribute("match", "../../variables/"+vectors.get(i));
			vectorElement.addContent(link);
		}
		compositeElement.addContent(vectorElement);
	}
	public VariableBean getVariable(String element) {
		for (Iterator varIt = getVariables().iterator(); varIt.hasNext();) {
			VariableBean var = (VariableBean) varIt.next();
			if ( var.getElement().equals(element) ) {
				return var;
			}
		}
		return null;
	}

	@Override
	public boolean equals(LasBean bean) {
		DatasetBean b = null;
		if ( !(bean instanceof DatasetBean) ) {
			return false;
		} else {
			b = (DatasetBean) bean;
		}
		if ( !name.equals(b.getName() )) return false;
		if ( !doc.equals(b.getDoc() )) return false;
		if ( !url.equals(b.getUrl() )) return false;
		if ( variables != null ) {
			if ( b.getVariables() == null ) return false;
			if ( !variables.equals(b.getVariables() )) return false;
		}

		if ( !comment.equals(b.getComment() )) return false;
		if ( !version.equals(b.getVersion() )) return false;
		if ( !creator.equals(b.getCreator() )) return false;
		if ( !group_name.equals(b.getGroup_name() )) return false;
		if ( !group_type.equals(b.getGroup_type() )) return false;
		if ( !group_id.equals(b.getGroup_id() )) return false;
		if ( !update_time.equals(b.getUpdate_time() )) return false;
		if ( !update_interval.equals(b.getUpdate_interval() )) return false;
		if ( !created.equals(b.getCreated() )) return false;
		if ( !expires.equals(b.getExpires() )) return false;
		if ( nextUpdate != b.getNextUpdate() ) return false;
		return true;
	}
}
