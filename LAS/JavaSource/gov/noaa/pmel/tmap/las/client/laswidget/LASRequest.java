package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.serializable.AnalysisAxisSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.AnalysisSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConstraintSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

public class LASRequest {
    Document document;

    public LASRequest() {
        super();
        document = XMLParser.parse("<?xml version=\"1.0\"?><lasRequest href=\"file:las.xml\"></lasRequest>");
    }

    public LASRequest(String xml) {
        document = XMLParser.parse(xml);
    }

    /**
     * Replaces the top level <link match=...> element in the LASRequest.
     * 
     * @param operation
     * @param id
     */
    public void setOperation(String operation, String style) {
        if ( style.contains("7") ) {
            NodeList l = document.getDocumentElement().getElementsByTagName("link");
            Element op = findOp(l);
            if ( op == null ) {
                op = document.createElement("link");
                document.getDocumentElement().appendChild(op);
            }
            op.setAttribute("match", "/lasdata/operations/operation[@ID='" + operation + "']");
        }
    }
    /**
     * Get the data file for an operation that uses a temporary file.
     * @param index
     * @return the file name
     */
    public String getData(int index) {
        NodeList l = document.getDocumentElement().getElementsByTagName("data_"+index);
        Element data;
        if ( l != null && l.getLength() > 0 ) {
            if ( index > l.getLength() - 1 ) {
                return null;
            }
            data = (Element) l.item(index);
            Element url = (Element) data.getFirstChild();
            return url.getFirstChild().getNodeValue();
        }
        return null;
    }
    /**
     * Gets the value of a Property element in the named PropertyGroup of
     * the LASRequest. Null if the property is not found.
     * @param group
     * @param property
     */
    public String getProperty(String group_name, String property_name) {
        NodeList l = document.getDocumentElement().getElementsByTagName("properties");
        Element properties;
        if ( l != null && l.getLength() > 0 ) {
            properties = (Element) l.item(0);
        } else {
            return null;
        }
        Element group = null;
        NodeList groups = properties.getChildNodes();
        for ( int i = 0; i < groups.getLength(); i++ ) {
            Node child = groups.item(i);
            if ( child instanceof Element ) {
                Element g = (Element) child;
                if ( g.getNodeName().equals(group_name) ) {
                    group = g;
                }
            }
        }
        if ( group != null ) {
            NodeList props = group.getChildNodes();
            Element prop = null;
            for ( int i = 0; i < props.getLength(); i++ ) {
                Node child = props.item(i);
                if ( child instanceof Element ) {
                    Element p = (Element) child;
                    if ( p.getNodeName().equals(property_name) ) {
                        return p.getFirstChild().getNodeValue();
                    }
                }
            }
            // Property not found. Ignore it.
            if ( prop == null ) {
                return null;
            }
        } else {
            return null;
        }
        return null;
    }
    /**
     * Private helper to find the element that contains a property.
     */
    public void removeProperty(String group_name, String property_name) {
        NodeList l = document.getDocumentElement().getElementsByTagName("properties");
        Element properties = null;
        if ( l != null && l.getLength() > 0 ) {
            properties = (Element) l.item(0);
        } 
        Element group = null;
        if ( properties != null ) {
            NodeList groups = properties.getChildNodes();
            for ( int i = 0; i < groups.getLength(); i++ ) {
                Node child = groups.item(i);
                if ( child instanceof Element ) {
                    Element g = (Element) child;
                    if ( g.getNodeName().equals(group_name) ) {
                        group = g;
                    }
                }
            }
            if ( group != null ) {
                NodeList props = group.getChildNodes();
                Element prop = null;
                for ( int i = 0; i < props.getLength(); i++ ) {
                    Node child = props.item(i);
                    if ( child instanceof Element ) {
                        Element p = (Element) child;
                        if ( p.getNodeName().equals(property_name) ) {
                            prop = p;
                        }
                    }
                }
                // Property not found. Ignore it.
                if ( prop != null ) {
                    group.removeChild(prop);
                }
                // If all the properties are gone, remove the group.
                props = group.getChildNodes();
                if ( props.getLength() <= 0 ) {
                    properties.removeChild(group);
                }
            } 
            
        }
    }
    /**
     * Replaces the value of a Property element in the named PropertyGroup of
     * the LASRequest. If the property is not found a new Property element will
     * be created.
     * 
     * @param group
     * @param property
     * @param value
     */
    public void setProperty(String group_name, String property_name, String property_value) {
    	
    	// Sanitize all input into properties.
   
        group_name = SimpleHtmlSanitizer.sanitizeHtml(group_name).asString();        
        property_name = SimpleHtmlSanitizer.sanitizeHtml(property_name).asString();
        property_value = SimpleHtmlSanitizer.sanitizeHtml(property_value).asString();
    	
        NodeList l = document.getDocumentElement().getElementsByTagName("properties");
        Element properties;
        if ( l != null && l.getLength() > 0 ) {
            properties = (Element) l.item(0);
        } else {
            properties = document.createElement("properties");
            document.getDocumentElement().appendChild(properties);
        }
        Element group = null;
        NodeList groups = properties.getChildNodes();
        for ( int i = 0; i < groups.getLength(); i++ ) {
            Node child = groups.item(i);
            if ( child instanceof Element ) {
                Element g = (Element) child;
                if ( g.getNodeName().equals(group_name) ) {
                    group = g;
                }
            }
        }
        if ( group != null ) {
            NodeList props = group.getChildNodes();
            Element prop = null;
            for ( int i = 0; i < props.getLength(); i++ ) {
                Node child = props.item(i);
                if ( child instanceof Element ) {
                    Element p = (Element) child;
                    if ( p.getNodeName().equals(property_name) ) {
                        prop = p;
                        Text text = document.createTextNode(property_value);
                        p.replaceChild(text, p.getFirstChild());
                    }
                }
            }
            // Property not found. Create it.
            if ( prop == null ) {
                prop = document.createElement(property_name);
                Text text = document.createTextNode(property_value);
                prop.appendChild(text);
                group.appendChild(prop);
            }
        } else {
            group = document.createElement(group_name);
            Element property = document.createElement(property_name);
            Text text = document.createTextNode(property_value);
            property.appendChild(text);
            group.appendChild(property);
            properties.appendChild(group);
        }
    }

    /**
     * Sets a range using a range element to <region> section of the LASRequest.
     * If no Region with this region_ID is found, one will be created. If a
     * Range along the desired axis already exists it will be replaced.
     * 
     * @param axis
     * @param lo
     * @param hi
     * @param region
     */
    public void setRange(String axis_type, String lo, String hi, int index) {
    	
    	// Sanitize input

        NodeList regions = document.getElementsByTagName("region");
        if ( index >= 0 && index < regions.getLength() ) {

            Element region = (Element) regions.item(index);

            Element axis = null;

            NodeList ranges = region.getElementsByTagName("range");
            for ( int i = 0; i < ranges.getLength(); i++ ) {
                Element range = (Element) ranges.item(i);
                String type = range.getAttribute("type");
                if ( type.equals(axis_type) ) {
                    axis = range;
                }
            }

            NodeList points = region.getElementsByTagName("point");
            for ( int i = 0; i < points.getLength(); i++ ) {
                Element point = (Element) points.item(i);
                String type = point.getAttribute("type");
                if ( type.equals(axis_type) ) {
                    axis = point;
                }
            }

            if ( axis != null ) {
                region.removeChild(axis);
            }

            if ( lo.equals(hi) ) {
                Element point = document.createElement("point");
                point.setAttribute("type", axis_type);
                point.setAttribute("v", lo);
                region.appendChild(point);
            } else {
                Element range = document.createElement("range");
                range.setAttribute("type", axis_type);
                range.setAttribute("low", lo);
                range.setAttribute("high", hi);
                region.appendChild(range);
            }

        } else {
            Element region = document.createElement("region");
            if ( lo.equals(hi) ) {
                Element point = document.createElement("point");
                point.setAttribute("type", axis_type);
                point.setAttribute("v", lo);
                region.appendChild(point);
            } else {
                Element range = document.createElement("range");
                range.setAttribute("type", axis_type);
                range.setAttribute("low", lo);
                range.setAttribute("high", hi);
                region.appendChild(range);
            }
            NodeList argsL = document.getDocumentElement().getElementsByTagName("args");
            Element args = (Element) argsL.item(0);
            args.appendChild(region);
        }
    }

    /**
     * Adds a <link match=.../> element to the <args> section of the LASRequest.
     * This will add a new dataset-variable pair to the LASRequest. Note that
     * the order in which variables appear in an LASRequest is important as
     * differencing products (as of 2007-10-24) always subtract the second
     * variable from the first.
     * 
     * @param dsID
     * @param varID
     * @param region
     *            - the region to which this variable belongs.
     */
    public void addVariable(String dsID, String varID, int region_index) {
        Element link = makeLink(dsID, varID);
        NodeList l = document.getDocumentElement().getElementsByTagName("args");
        Element args = (Element) l.item(0);
        if ( args == null ) {
            args = document.createElement("args");
            document.getDocumentElement().appendChild(args);
        }
        NodeList regions = document.getElementsByTagName("region");
        if ( region_index >= 0 && region_index < regions.getLength() ) {
            Element region = (Element) regions.item(region_index);
            args.insertBefore(link, region);
        } else {
            args.appendChild(link);
        }
    }

    /**
     * Adds a Constraint element of type 'variable' to the <args> section of the
     * LASRequest. Constraints of type 'variable' contain dataset-variable xpath
     * information as the left hand side.
     * 
     * @param dataset
     * @param variable
     * @param op
     * @param value
     */
    public void addVariableConstraint(String dataset, String variable, String op, String value, String cid) {
    	
        Element constraint = document.createElement("constraint");
        constraint.setAttribute("type", "variable");
        if ( cid != null ) {
            constraint.setAttribute("id", cid);
        }
        constraint.setAttribute("op", op);
        Element link = makeLink(dataset, variable);
        Element v = document.createElement("v");
        Text text = document.createTextNode(value);
        v.appendChild(text);
        constraint.appendChild(link);
        constraint.appendChild(v);
        Element args = getArgsElement();
        args.appendChild(constraint);
    }
    
    /**
     * Add a constraint.  
     * @param lhs
     * @param op
     * @param rhs
     */
    public void addConstraint(String lhs, String op, String rhs, String cid, String label) {
    	
        Element constraint = document.createElement("constraint");
        constraint.setAttribute("type", "text");
        if ( cid != null ) {
            constraint.setAttribute("id", cid);
        }
        Element v1 = document.createElement("v");
        Text lhsT = document.createTextNode(lhs);
        Text opT = document.createTextNode(op);
        Element v2 = document.createElement("v");
        Text rhsT = document.createTextNode(rhs);
        Element v3 = document.createElement("v");
        v1.appendChild(lhsT);
        v2.appendChild(opT);
        v3.appendChild(rhsT);
        constraint.appendChild(v1);
        constraint.appendChild(v2);
        constraint.appendChild(v3);
        if ( label != null ) {
        	Element labelE = document.createElement("label");
        	Text labelText = document.createTextNode(label);
        	labelE.appendChild(labelText);
        	constraint.appendChild(labelE);
        }
        Element args = getArgsElement();
        args.appendChild(constraint);
    }

    /**
     * Removes all Constraint elements defined in the <args> section of the
     * LASRequest. No 'data options' will be applied before creating the
     * product.
     */
    public void removeConstraints() {
        Element args = getArgsElement();
        NodeList constraints = args.getElementsByTagName("constraint");
        List<Node> remove = new ArrayList<Node>();
        for ( int i = 0; i < constraints.getLength(); i++ ) {
            Node constraint = constraints.item(i);
            remove.add(constraint);
        }
        for ( int i = 0; i < remove.size(); i++ ) {
            args.removeChild(remove.get(i));
        }
    }

    /**
     * Removes all <link match=...> elements defined in the <args> section of
     * the LASRequest. Clears out all dataset-variable pairs defined in the
     * LASRequest.
     */
    public void removeVariables() {
        Element args = getArgsElement();
        NodeList variables = args.getElementsByTagName("link");
        List<Node> remove = new ArrayList<Node>();
        for ( int i = 0; i < variables.getLength(); i++ ) {
            Node var = variables.item(i);
            if ( var.getParentNode().getNodeName().equals("args") ) {
                remove.add(var);
            }
        }
        for ( int i = 0; i < remove.size(); i++ ) {
            args.removeChild(remove.get(i));
        }
    }

    public String getVariable(int index) {
        Element args = getArgsElement();
        NodeList variables = args.getElementsByTagName("link");
        int counter = 0;
        for ( int i = 0; i < variables.getLength(); i++ ) {
            Element var = (Element) variables.item(i);
            // ByTagName gets children, grandchildren and below. Check that it
            // is a child of "args".
            if ( var.getParentNode().getNodeName().equals("args") ) {
                if ( counter == index ) {
                    return getVariableId(var.getAttribute("match"));
                }
                counter++;
            }
        }
        return null;
    }

    public String getDataset(int index) {
        Element args = getArgsElement();
        NodeList variables = args.getElementsByTagName("link");
        Element var = (Element) variables.item(index);
        if ( var != null ) {
            return getDatasetId(var.getAttribute("match"));
        } else {
            return null;
        }
    }

    public List<Map<String, String>> getVariableConstraints() {
        List<Map<String, String>> vcs = new ArrayList<Map<String, String>>();
        NodeList l = document.getDocumentElement().getElementsByTagName("args");
        Element args = (Element) l.item(0);
        NodeList constraints = args.getElementsByTagName("constraint");
        for ( int i = 0; i < constraints.getLength(); i++ ) {
            Map<String, String> c = new HashMap<String, String>();
            Element constraint = (Element) constraints.item(i);
            String type = constraint.getAttribute("type");
            c.put("op", constraint.getAttribute("op"));
            c.put("type", type);
            String id = constraint.getAttribute("id");
            if ( id != null ) {
                c.put("id", id);
            }
            if ( type.equals("variable") ) {
                NodeList vl = constraint.getElementsByTagName("v");
                Element v = (Element) vl.item(0);
                Text text = (Text) v.getFirstChild();
                String value = text.getData();
                c.put("value", value);

                // If it's a variable constraint it will contain a "link" element that defines the variable.
                NodeList ll = constraint.getElementsByTagName("link");
                Element link = (Element) ll.item(0);
                String match = link.getAttribute("match");

                c.put("dsID", getDatasetId(match));
                c.put("varID", getVariableId(match));
            } else if ( type.equals("text") ) {
                NodeList vl = constraint.getElementsByTagName("v");
                Element v = (Element) vl.item(0);
                Text text = (Text) v.getFirstChild();
                String lhs = text.getData();
                c.put("lhs", lhs);
                Element v1 = (Element) vl.item(1);
                Text text1 = (Text) v1.getFirstChild();
                String op = text1.getData();
                c.put("op", op);
                Element v2 = (Element) vl.item(2);
                Text text2 = (Text) v2.getFirstChild();
                String rhs = text2.getData();
                c.put("rhs", rhs);
            }
            vcs.add(c);
        }
        return vcs;
    }

    public String getRangeHi(String axis_type, int index) {
        Element args = getArgsElement();
        NodeList regions = args.getElementsByTagName("region");
        if ( regions.getLength() > index ) {
            Element region = (Element) regions.item(index);
            NodeList ranges = region.getElementsByTagName("range");
            for ( int i = 0; i < ranges.getLength(); i++ ) {
                Element range = (Element) ranges.item(i);
                if ( range.getAttribute("type").equals(axis_type) ) {
                    return range.getAttribute("high");
                }
            }
            NodeList points = region.getElementsByTagName("point");
            for ( int i = 0; i < points.getLength(); i++ ) {
                Element point = (Element) points.item(i);
                if ( point.getAttribute("type").equals(axis_type) ) {
                    return point.getAttribute("v");
                }
            }
        }
        return null;
    }

    public String getRangeLo(String axis_type, int index) {
        Element args = getArgsElement();
        NodeList regions = args.getElementsByTagName("region");
        if ( regions.getLength() > index ) {
            Element region = (Element) regions.item(index);
            NodeList ranges = region.getElementsByTagName("range");
            for ( int i = 0; i < ranges.getLength(); i++ ) {
                Element range = (Element) ranges.item(i);
                if ( range.getAttribute("type").equals(axis_type) ) {
                    return range.getAttribute("low");
                }
            }
            NodeList points = region.getElementsByTagName("point");
            for ( int i = 0; i < points.getLength(); i++ ) {
                Element point = (Element) points.item(i);
                if ( point.getAttribute("type").equals(axis_type) ) {
                    return point.getAttribute("v");
                }
            }
        }
        return null;
    }

    public String toString() {
        String xml = document.toString();
        xml = xml.replaceAll("\n", "");
        // Get rid of the doc declaration
        xml = xml.substring(xml.indexOf("<lasRequest"), xml.length());
        return xml;
    }

    private Element getArgsElement() {
        NodeList l = document.getDocumentElement().getElementsByTagName("args");
        return (Element) l.item(0);
    }

    private Element makeLink(String dsid, String varid) {
        Element nl = document.createElement("link");
        nl.setAttribute("match", "/lasdata/datasets/" + dsid + "/variables/" + varid);
        return nl;
    }

    private String getVariableId(String match) {
        return match.substring(match.lastIndexOf("/") + 1, match.length());
    }

    private String getDatasetId(String match) {
        String ds = match.substring(match.indexOf("/lasdata/datasets/"), match.indexOf("/variables/"));
        return ds.substring(ds.lastIndexOf("/") + 1, ds.length());
    }

    public void setAnalysis(AnalysisSerializable analysis, int index) {
        Element args = getArgsElement();
        NodeList variables = args.getElementsByTagName("link");
        int counter = 0;
        for ( int i = 0; i < variables.getLength(); i++ ) {
            Element var = (Element) variables.item(i);
            // ByTagName gets children, grandchildren and below. Check that it
            // is a child of "args".
            if ( var.getParentNode().getNodeName().equals("args") ) {
                if ( counter == index ) {
                    // Found it, set the analysis.
                    NodeList existingAn = var.getChildNodes();
                    Element anE = null;
                    if ( existingAn != null && existingAn.getLength() > 0 ) {
                        for (int eain = 0; eain < existingAn.getLength(); eain++ ) {
                            anE = (Element) existingAn.item(0);
                        }
                    }
                    if ( anE == null ) {
                        anE = document.createElement("analysis");
                        var.appendChild(anE);
                    }
                    String label = analysis.getLabel();
                    if ( label != null )
                        anE.setAttribute("label", label);
                    Map<String, AnalysisAxisSerializable> map = analysis.getAxes();
                    for ( Iterator mapIt = map.keySet().iterator(); mapIt.hasNext(); ) {
                        String key = (String) mapIt.next();
                        if ( analysis.isActive(key) ) {
                            AnalysisAxisSerializable a = map.get(key);
                            Element axE = document.createElement("axis");
                            axE.setAttribute("type", a.getType());
                            axE.setAttribute("op", a.getOp());
                            axE.setAttribute("lo", a.getLo());
                            axE.setAttribute("hi", a.getHi());
                            anE.appendChild(axE);
                        }
                    }
                    
                }
                counter++;
            }
        }
    }

    public double getVariableConstraintMin(String in_varid) {
        // This is a bit of a kludge. The XML doens't know what constraint is
        // the min nor
        // whether it's a constraint on on of the axis of a prop-prop plot so we
        // use an ID. Therefore the method name indicates this only works
        // reliably
        // with prop-prop plots. Don't call it if the variable does not have a
        // contraint. Kludge
        double min = Double.MIN_VALUE;
        List<Map<String, String>> vcons = getVariableConstraints();
        if ( vcons.size() > 0 ) {
            for ( Iterator vconsIt = vcons.iterator(); vconsIt.hasNext(); ) {
                Map<String, String> con = (Map<String, String>) vconsIt.next();
                String varid = con.get("varID");
                String op = con.get("op");
                String value = con.get("value");
                String id = con.get("id");
                if ( id.contains("min") ) {
                    if ( varid.equals(in_varid) ) {
                        min = Double.valueOf(value).doubleValue();
                    }
                }
            }
        }
        return min;
    }

    public double getVariableConstraintMax(String in_varid) {
        double max = Double.MAX_VALUE;
        List<Map<String, String>> vcons = getVariableConstraints();
        if ( vcons.size() > 0 ) {
            for ( Iterator vconsIt = vcons.iterator(); vconsIt.hasNext(); ) {
                Map<String, String> con = (Map<String, String>) vconsIt.next();
                String varid = con.get("varID");
                String op = con.get("op");
                String value = con.get("value");
                String id = con.get("id");
                if ( id.contains("max") ) {
                    if ( varid.equals(in_varid) ) {
                        max = Double.valueOf(value).doubleValue();
                    }
                }
            }
        }
        return max;
    }

    public String getOperation() {

        NodeList l = document.getDocumentElement().getElementsByTagName("link");
        Element op = findOp(l);
        if ( op != null ) {
            String opid = op.getAttribute("match");
            return opid.substring(opid.indexOf("ID='") + 4, opid.indexOf("']"));
        } else {
            return "";
        }
    }
    private Element findOp(NodeList l) {
        Element op = null;
        if ( l != null && l.getLength() > 0 ) {
            // This might be a variable in the <args> element. Check it's
            // parent.
            for ( int i = 0; i < l.getLength(); i++ ) {
                Element link = (Element) l.item(i);
                if ( op == null && !link.getParentNode().getNodeName().equals("args") && !link.getParentNode().getNodeName().equals("constraint")) {
                    op = (Element) l.item(i);
                }
            }
        }
        return op;
    }
    public void addConstraint(ConstraintSerializable constraint) {
        if ( constraint.getType().equals("text") ) {
            this.addConstraint(constraint.getLhs(), constraint.getOp(), constraint.getRhs(), null, constraint.getLabel());
        } else {
            this.addVariableConstraint(constraint.getDsid(), constraint.getVarid(), constraint.getOp(), constraint.getRhs(), constraint.getId());
        }
    }
    public void addConstraints(List<ConstraintSerializable> cons) {
        for (Iterator conIt = cons.iterator(); conIt.hasNext();) {
            ConstraintSerializable constraint = (ConstraintSerializable) conIt.next();
            addConstraint(constraint);
        }
        
    }
}
