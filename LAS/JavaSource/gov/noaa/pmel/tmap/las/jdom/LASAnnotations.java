package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.ui.GetAnnotations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.CDATA;
import org.jdom.Element;


public class LASAnnotations extends LASDocument {
   
	private static final String ELEMENT_ANNOTATIONS = "annotations";
	private static final String ELEMENT_ANNOTATION_GROUP = "annotation_group";
	private static final String ELEMENT_ANNOTATION = "annotation";
	private static final String ELEMENT_LABEL = "label";
	private static final String ELEMENT_VALUE = "value";
	
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String TYPE_VARIABLE = "variable";
	private static final String TYPE_DATASET = "dataset";
	private static final String TYPE_CALENDAR = "calendar";
	private static final String TYPE_ORTHOGONAL_AXES = "orthogonal_axes";
	private static final String TYPE_LAS = "las";
	private static final String TYPE_NOTES = "notes";
	private static final String TYPE_NOTE = "note";
	private static final String TYPE_TITLE = "title";
	private static final String TYPE_UNITS = "units";
	private static final String TYPE_FERRET_VERSION = "ferret_version";
	private static final String TYPE_LAS_VERSION = "las_version";
	private static final String TYPE_DATA = "data";
	private static final String TYPE_DATASET_URL = "dataset_url";
	private static final String TYPE_VARIABLE_TITLE = "variable_title";
	private static final String TYPE_DATASET_TITLE = "dataset_title";
	private static final String TYPE_DATASET_ID = "dataset_ID";
	private static final String TYPE_HEADER = "header";
	
	private static final long serialVersionUID = 5934613476516539108L;
	/**
     * 
     */
    
    public LASAnnotations() {
        super();
        Element annotations = new Element(ELEMENT_ANNOTATIONS);
        addContent(annotations);
    }
    public List<String> getVariableTitles() {
    	return getAnnotationValues(getRootElement(), TYPE_DATA, TYPE_VARIABLE_TITLE);
    }
    public List<String> getDatasetTitles() {
    	return getAnnotationValues(getRootElement(), TYPE_DATA, TYPE_DATASET_TITLE);
    }
    public List<String> getDatasetIDs() {
        return getAnnotationValues(getRootElement(), TYPE_DATA, TYPE_DATASET_ID);
    }
//    public List<String> getVariableTitles() {
//    	List<String> titles = new ArrayList<String>();
//    	List<Element> datasets = getAnnotationGroups(TYPE_DATASET);
//    	if ( datasets.size() > 0 ) {
//    		for (int d = 0; d < datasets.size(); d++) {
//    			Element dataset = datasets.get(d);
//    			// Find the first variable groups inside the dataset group.
//    			Element variableGroup = null;
//    			if ( dataset != null ) {
//    				List<Element> variables = getAnnotationGroups(dataset, TYPE_VARIABLE);
//    				for (int v = 0; v < variables.size(); v++ ) {
//    					StringBuffer title = new StringBuffer();
//    					variableGroup = variables.get(v);
//    					String t = null;
//    					String u = "";
//    					if ( variableGroup != null ) {
//    						// Gets the first, there should only be one.
//    						List<Element> annotations = getAnnotations(variableGroup, TYPE_TITLE);
//    						if ( annotations.size() > 0 ) {
//    							t = getValue(annotations.get(0));
//    						}
//    						annotations = getAnnotations(variableGroup, TYPE_UNITS);
//    						if ( annotations.size() > 0 ) {
//    							u = getValue(annotations.get(0));
//    						}
//    					}
//    					if ( t != null ) {
//    						title.append(t);
//    					}
//    					if ( u != null ) {
//    						title.append(" (");
//    						title.append(u);
//    						title.append(")");
//    					}
//    					titles.add(title.toString());
//    				}
//    			}
//    		}
//    	}
//    	return titles;
//    }
    public List<String> getNotes() {
    	return getAnnotationValues(getRootElement(), TYPE_NOTES, TYPE_NOTE);
    }
    public List<String> getOrthogonalAxes() {
    	return getAnnotationLabelsAndValues(getRootElement(), TYPE_ORTHOGONAL_AXES, null);
    }
    public String getHeader() {
    	List<String> headers = getAnnotationValues(getRootElement(), TYPE_LAS, TYPE_HEADER);
    	if ( headers.size() > 0 ) {
    	    return headers.get(0);
    	} else {
    		return "";
    	}
    }
    public List<String> getDatasetURLs() {
    	return getAnnotationValues(getRootElement(), TYPE_DATA, TYPE_DATASET_URL);
    }
    public String getFooter() {
    	StringBuilder footer = new StringBuilder();
    	List<Element> groups = getAnnotationGroups(TYPE_LAS);
    	if ( groups.size() > 0 ) {
    		List<Element> annotations = getAnnotations(groups.get(0), TYPE_LAS_VERSION);
    		if ( annotations.size() > 0 ) {
    			String las = getValue(annotations.get(0));
    			if ( las != null ) {
    				footer.append("LAS ");
    				footer.append(las);
    			}
    		}
    		annotations = getAnnotations(groups.get(0), TYPE_FERRET_VERSION);
    		if ( annotations.size() > 0 ) {
    			String ferret = getValue(annotations.get(0));
    			if ( footer.length() > 0 ) {
    				footer.append("/");
    			}
    			footer.append("Ferret ");
    			footer.append(ferret);
    		}
    	}
    	
    	return footer.toString();
    }
    public List<String> getCalendar() {
    	return getAnnotationValues(getRootElement(), TYPE_CALENDAR, null);
    }
    private List<String> getAnnotationLabelsAndValues(Element group, String group_type, String annotation_type) {
    	List<String> lvs = new ArrayList<String>();
    	List groups = getAnnotationGroups(group_type);
    	for (Iterator groupsIt = groups.iterator(); groupsIt.hasNext();) {
			Element groupE = (Element) groupsIt.next();
			List annotations = getAnnotations(groupE, annotation_type);
			for (Iterator annIt = annotations.iterator(); annIt.hasNext();) {
				StringBuilder lv = new StringBuilder();
				Element annotation = (Element) annIt.next();
				String label = getLabel(annotation);
				if ( label != null ) {
					lv.append(label);
				}
				String value = getValue(annotation);
				if ( value != null ) {
					if ( lv.length() > 0 ) {
						lv.append(" ");
					}
					lv.append(value);
				}
				lvs.add(lv.toString());
			}
    	}
    	return lvs;
    }
    private List<String> getAnnotationValues(Element group, String group_type, String annotation_type) {
    	List<String> values = new ArrayList<String>();
    	List groups = getAnnotationGroups(group_type);
    	for (Iterator groupsIt = groups.iterator(); groupsIt.hasNext();) {
			Element groupE = (Element) groupsIt.next();
			List annotations = getAnnotations(groupE, annotation_type);
			for (Iterator annIt = annotations.iterator(); annIt.hasNext();) {
				Element annotation = (Element) annIt.next();
				String value = getValue(annotation);
				if ( value != null && !values.contains(value) ) {
					values.add(value);
				}
			}
    	}
    	return values;
    }
    private String getValue(Element annotation) {
    	Element valueE = annotation.getChild(ELEMENT_VALUE);
    	String v = null;
		if ( valueE != null ) {
			v = valueE.getTextTrim();
		}
		return v;
    }
    private String getLabel(Element annotation) {
    	Element labelE = annotation.getChild(ELEMENT_LABEL);
    	String l = null;
		if ( labelE != null ) {
		    l = labelE.getTextTrim();
		}
		return l;
    }
    private List<Element> getAnnotations(Element group, String type) {
    	List<Element> annotations = new ArrayList<Element>();
    	List allAnnotations = group.getChildren(ELEMENT_ANNOTATION);
    	for (Iterator annIt = allAnnotations.iterator(); annIt.hasNext();) {
    		Element a = (Element) annIt.next();
    		if ( type == null ) {
    			// All if no type is specified
    			annotations.add(a);
    		} else {
    			if ( a.getAttributeValue(ATTRIBUTE_TYPE).equals(type) ) {
    				annotations.add(a);
    			} 
    		}
    	}
    	return annotations;
    }
    private List<Element> getAnnotationGroups(Element group, String type) {
    	List<Element> type_groups = new ArrayList<Element>();
    	// Get the top level groups and find the first data set group...
    	List groups = group.getChildren(ELEMENT_ANNOTATION_GROUP);
    	for (Iterator groupsIt = groups.iterator(); groupsIt.hasNext();) {
			Element groupE = (Element) groupsIt.next();
			if ( groupE.getAttributeValue(ATTRIBUTE_TYPE) != null && groupE.getAttributeValue(ATTRIBUTE_TYPE).equals(type) ) {
				type_groups.add(groupE);
			}
		}
    	return type_groups;
    }
    private List<Element> getAnnotationGroups(String type) {
    	return getAnnotationGroups(getRootElement(), type);
    }
	public void setDatasetTitle(String string) {
		Element group = new Element(ELEMENT_ANNOTATION_GROUP);
		group.setAttribute("type", "data");
		Element ann = new Element(ELEMENT_ANNOTATION);
		ann.setAttribute("type", "dataset_title");
		Element value = new Element(ELEMENT_VALUE);
		CDATA text = new CDATA(string);
		value.addContent(text);
		ann.addContent(value);
		group.addContent(ann);
		getRootElement().addContent(group);
	}
	public void clear() {
		getRootElement().removeContent();
	}
}
