/**
 * 
 */
package gov.noaa.pmel.tmap.jdom;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.filter.FindPropertyFilter;
import gov.noaa.pmel.tmap.jdom.filter.FindPropertyGroupFilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.Filter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 * @author Roland Schweitzer
 *
 */
public class LASDocument extends Document {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public LASDocument() {
        super();
    }
    public LASDocument(Document doc) {
         setContent(doc.cloneContent());
    }
    
    public LASDocument(Element dsE) {
		super(dsE);
	}
    public Element getElementByXPath(String xpathValue) throws JDOMException {
        // E.g. xpathValue="/lasdata/operations/operation[@ID='Plot']"

        String[] elements = xpathValue.split("/");
        if ( xpathValue.contains("datasets") || xpathValue.contains("variables") || xpathValue.contains("grids") || xpathValue.contains("axes") || xpathValue.contains("operations")) {
            if ( elements.length == 4 ) {
                String type = elements[2];
                String id = elements[3];
                id = id.substring(id.indexOf("@ID='")+5, id.length()-2);
                if ( type == null || id == null) {
                    System.out.println("typeis null");
                }
                return getByID(type, getRootElement(), id);
            } else if (elements.length == 6 ) {
                String type = elements[2];
                String id = elements[3];
                id = id.substring(id.indexOf("@ID='")+5, id.length()-2);
                Element parent = getByID(type, getRootElement(), id);
                type = elements[4];
                id = elements[5];
                id = id.substring(id.indexOf("@ID='")+5, id.length()-2);
                if ( type == null || parent == null || id == null) {
                    System.out.println("you are null");
                }
                
                return getByID(type, parent, id);
            } else {
            	if ( xpathValue.contains("../") || xpathValue.contains("ancestor") ) {
            		throw new JDOMException("Illegal XPath value.");
            	}
                // Do the old style xpath search...
                Object jdomO = this;
                XPath xpath = XPath.newInstance(xpathValue);
                return (Element) xpath.selectSingleNode(jdomO);   
            }
        }else {
        	if ( xpathValue.contains("../") || xpathValue.contains("ancestor") ) {
        		throw new JDOMException("Illegal XPath value.");
        	}
            Object jdomO = this;
            XPath xpath = XPath.newInstance(xpathValue);
            return (Element) xpath.selectSingleNode(jdomO);   
        }
    }
    private Element getByID (String type, Element element, String id) {
        List typeElement = element.getChildren(type);
        for (Iterator typeIt = typeElement.iterator(); typeIt.hasNext();) {
            Element typeE = (Element) typeIt.next();
            List children = typeE.getChildren();
            for (Iterator childIt = children.iterator(); childIt.hasNext();) {
                Element child = (Element) childIt.next();
                if ( child.getAttributeValue("ID") != null && child.getAttributeValue("ID").equals(id) ) {
                    return child;
                }
            }    
        }
        return null;
    }
    public String toCompactString() {
        Format format = Format.getCompactFormat();
        format.setOmitDeclaration(true);
        String xml = toString(format);
        xml = xml.replaceAll("\\r","");
        xml = xml.replaceAll("\\n","");
        return "<?xml version=\"1.0\"?>"+xml;
    }
    
    public String toEncodedURLString() throws UnsupportedEncodingException {
        Format format = Format.getCompactFormat();
        format.setOmitDeclaration(true);
        String xml = toString(format);
        xml = xml.replaceAll("\\r\\n","");
        xml = xml.replaceAll("\\r","");
        xml = xml.replaceAll("\\n","");
        return URLEncoder.encode("<?xml version=\"1.0\"?>"+xml, "UTF-8");
    }
    
    public String toEncodedJavaScriptSafeURLString() throws UnsupportedEncodingException  {
        Format format = Format.getCompactFormat();
        format.setOmitDeclaration(true);
        String xml = toString(format);
        
        //xml = xml.replaceAll("\"","\\\\\"");
        xml = xml.replaceAll("'","\\\\'");;
        xml = xml.replaceAll("\\r\\n","");
        xml = xml.replaceAll("\\r","");
        xml = xml.replaceAll("\\n","");
        return URLEncoder.encode("<?xml version=\"1.0\"?>"+xml, "UTF-8");
    }
    
    public String toJavaScriptSafeString() {
        Format format = Format.getCompactFormat();
        format.setOmitDeclaration(true);
        String xml = toString(format);
        xml="<?xml version=\"1.0\"?>"+xml;
        xml = xml.replaceAll("'","\\\\'");
        xml = xml.replaceAll("\"","\\\\\"");
        xml = xml.replaceAll("\\r\\n","");
        xml = xml.replaceAll("\\r","");
        xml = xml.replaceAll("\\n","");
        return xml;
    }
    
    public String toString(Format format) {
        StringWriter xmlout = new StringWriter();
        try {
            format.setLineSeparator(System.getProperty("line.separator"));
            XMLOutputter outputter = new XMLOutputter(format);
            outputter.output(this, xmlout);
            // Close the FileWriter
            xmlout.close();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return xmlout.toString();
    }
    
    public String toString() {
        Format format = Format.getPrettyFormat();
        return toString(format);
    }
    
    public void write(String fileName) {
        File file = new File(fileName);
        write(file);
    }
    
    public void write(File file) {
    	FileWriter xmlout = null;
        try {        
        	xmlout = new FileWriter(file);
            org.jdom.output.Format format = org.jdom.output.Format.getPrettyFormat();
            format.setLineSeparator(System.getProperty("line.separator"));
            XMLOutputter outputter = new XMLOutputter(format);
            outputter.output(this, xmlout);
            // Close the FileWriter
            xmlout.close();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
        	if ( xmlout != null ) {
        		try {
					xmlout.close();
				} catch (IOException e) {
					//
				}
        	}
        }
        
    }
    
    public String write() {
    	String xml = null;
    	try {
    		StringWriter xmlout = new StringWriter();
    		org.jdom.output.Format format = org.jdom.output.Format.getPrettyFormat();
    		format.setLineSeparator(System.getProperty("line.separator"));
    		XMLOutputter outputter = new XMLOutputter(format);
    		outputter.output(this, xmlout);
    		xml = xmlout.toString();
    		xmlout.close();
    		return xml;
    	} catch (Exception e) {
    		return null;
    	}
    }
    public void writeElement(String element, File file, boolean append) {
    	try {
            FileWriter xmlout = new FileWriter(file, append);
            if (append) xmlout.write("\n");
            org.jdom.output.Format format = org.jdom.output.Format.getPrettyFormat();
            format.setLineSeparator(System.getProperty("line.separator"));
            XMLOutputter outputter = new XMLOutputter(format);
            outputter.output(this.getRootElement().getChild(element), xmlout);
            // Close the FileWriter
            xmlout.close();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public Element findProperty(Element group, String name) throws LASException {
        Filter propertyFilter = new FindPropertyFilter(name);       
        Iterator propsIt = group.getDescendants(propertyFilter);
        Element property=null;
        if ( propsIt.hasNext() ) {
            property = (Element) propsIt.next();
        }
        if ( propsIt.hasNext()) {
            throw new LASException("More than one property with same name.");
        }
        return property;  
    }
    
    public String findPropertyValue(Element group, String name) throws LASException {
        Element property = findProperty(group, name);
        if (property == null ) {
            return "";
        }
        return property.getChildText("value");
    }
    
    
    public String getProperty(String group, String name) throws LASException {
        // Finds a property group and property in the "global" properties.
        Element propGroup = this.findPropertyGroup(group);
        if ( propGroup != null ) {
            return findPropertyValue(propGroup, name);
        }
        return "";
    }
    
    public Element findPropertyGroup(String group) throws LASException {
        // Finds the properties that is a child of the root.
        return findPropertyGroup(this.getRootElement(), group);
    }
    
    public Element findPropertyGroup(Element properties, String group) throws LASException {
        // Finds properties below a particular element.
        Filter propertyGroupFilter = new FindPropertyGroupFilter(group);
        Iterator pgIt = properties.getDescendants(propertyGroupFilter);
        Element propGroup = null;
        if (pgIt.hasNext()) {
            propGroup = (Element) pgIt.next();
        }
//        if ( pgIt.hasNext() ) {
//            throw new LASException("More than one property group with name = "+group);
//        }
        // Just return the first...
        return propGroup;
    }
    
    public ArrayList<Element> findPropertyGroupList(String group) {
        // Finds all property groups of a particular type anywhere below the root.
        return findPropertyGroupList(this.getRootElement(), group);
    }
    
    public ArrayList<Element> findPropertyGroupList(Element element, String group) {
        ArrayList<Element> groups = new ArrayList<Element>();
        Filter propertyGroupFilter = new FindPropertyGroupFilter(group);
        for (Iterator pgIt = element.getDescendants(propertyGroupFilter); pgIt.hasNext(); ) {
            groups.add((Element)pgIt.next());
        }
        return groups;
    }
    public static List convertProperties(Element properties) {
        List<Element> new_prop_groups = new ArrayList<Element>();
        List property_groups = properties.getChildren("property_group");
        if ( property_groups != null && property_groups.size() > 0 ) {
        	return property_groups;
        }
        property_groups = properties.getChildren();
        for (Iterator grpIt = property_groups.iterator(); grpIt.hasNext();) {
            Element group = (Element) grpIt.next();
            Element property_group = new Element("property_group");
            property_group.setAttribute("type", group.getName());
            List props = group.getChildren();
            for (Iterator propsIt = props.iterator(); propsIt.hasNext();) {
                Element prop = (Element) propsIt.next();
                String name = prop.getName();
                String value = prop.getTextNormalize();
                Element nameE = new Element("name");
                Element valueE = new Element("value");
                Element propertyE = new Element("property");
                nameE.setText(name);
                valueE.setText(value);
                propertyE.addContent(nameE);
                propertyE.addContent(valueE);
                property_group.addContent(propertyE);
            }
            new_prop_groups.add(property_group);
        }
        return new_prop_groups;
    }
    /**
     * Return a JSON representation of this XML document.
     * @return JSON object as a string
     * @throws JSONException
     */
    public String toJSON() throws JSONException {      
        return XML.toJSONObject(this.toCompactString()).toString();
    }
    /**
     * Return a JSON representation of this XML document pretty printed with "indent" spaces.
     * @param indent - the number of spaces to indent each level.
     * @return JSON object as a string
     * @throws JSONException
     */
    public String toJSON(int indent) throws JSONException {
        return XML.toJSONObject(this.toCompactString()).toString(indent);
    }
}
