/**
 * 
 */
package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.ui.state.OptionBean;
import gov.noaa.pmel.tmap.las.util.VariableConstraint;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * @author Roland Schweitzer
 *
 */
public class LASUIRequest extends LASDocument {

	/**
	 * Construct a new LASUIRequest object and set the root element.
	 * TODO will this break other references to the empty constructor?
	 */
	public LASUIRequest() {
		super();
		setRootElement(new Element("lasRequest"));
	}
	/**
	 * Sets the variable to be used in this request.
	 * @param dsID the ID of the dataset that contains the variable to be added
	 * @param varID the ID of the variable to add
	 */
	public void addVariable(String dsID, String varID) {
		Element args = getRootElement().getChild("args");
		if ( args == null ) {
			args = new Element("args");
			getRootElement().addContent(args);
		}
		Element link = new Element("link");
		link.setAttribute("match", "/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']");
		args.addContent(link);
	}
	public void addVariableOldXPath(String dsid, String varid) {
		Element args = getRootElement().getChild("args");
		if ( args == null ) {
			args = new Element("args");
			getRootElement().addContent(args);
		}
		Element link = new Element("link");
		link.setAttribute("match", "/lasdata/datasets/"+dsid+"/variables/"+varid);
		args.addContent(link);
	}

	/**
	 * Get a list of all the Variables in the request as XPaths.
	 * @return ArrayList of XPath Strings for the variables in this request.
	 */
	public ArrayList<String> getVariables() {
		ArrayList<String> variables = new ArrayList<String>();
		List vars = getRootElement().getChild("args").getChildren("link");
		for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
			Element var = (Element) varIt.next();
			variables.add(var.getAttributeValue("match"));
		}
		return variables;
	}
	/**
	 * Get a list of all the variables IDs in this request.
	 * @return ArrayList<String> with the IDs
	 */
	public ArrayList<String> getVariableIDs() {
		ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> vars = getVariables();
		for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
			String varXPath = (String) varIt.next();
			int isrt = varXPath.indexOf("/variables/");
			ids.add(varXPath.substring(isrt+11, varXPath.length()));
		}
		return ids;
	}
	/**
	 * Sets the region element of the LAS UI Request object.
	 * @param region A HashMap of HashMaps; the points map contains point elements, the range map element contains range elements.
	 */
	public void setRegion(HashMap<String, HashMap<String,String[]>> region) {
		Element regionE = new Element("region");
		HashMap<String, String[]> points = region.get("points");
		HashMap<String, String[]> intervals = region.get("intervals");
		for (Iterator ptIt = points.keySet().iterator(); ptIt.hasNext();) {
			String type = (String) ptIt.next();
			String[] v = points.get(type);
			Element point = new Element("point");
			point.setAttribute("type", type);
			point.setAttribute("v",v[0]);
			regionE.addContent(point);
		}
		for (Iterator rgIt=intervals.keySet().iterator(); rgIt.hasNext();) {
			String type = (String) rgIt.next();
			String[] v = intervals.get(type);
			Element range = new Element("range");
			range.setAttribute("type", type);
			range.setAttribute("low", v[0]);
			range.setAttribute("high", v[1]);
			regionE.addContent(range);
		}
		Element args = getRootElement().getChild("args");
		if ( args == null ) {
			args = new Element("args");
			getRootElement().addContent(args);
		}
		args.addContent(regionE);
	}
	private String getRangeValueHelper(int index, String type, String extreme, List regions) {
		String value = "";
		if ( regions.size() <= 0 ) {
			return value;
		} else {
			if ( index > 0 && regions.size() == 1 ) {
				return value;
			}
			Element region = (Element) regions.get(index);
			List points = region.getChildren("point");
			for (Iterator ptIt = points.iterator(); ptIt.hasNext();) {
				Element point = (Element) ptIt.next();
				String ptype = point.getAttributeValue("type");
				if ( ptype.equals(type) ) {
					value = point.getAttributeValue("v");
					if ( value != null ) {
						return value;
					} else {
						return "";
					}
				}
			}
			List ranges = region.getChildren("range");
			for (Iterator iterator = ranges.iterator(); iterator.hasNext();) {
				Element range = (Element) iterator.next();
				String rtype = range.getAttributeValue("type");
				if ( rtype.equals(type) ) {
					value = range.getAttributeValue(extreme);
					if ( value != null ) {
						return value;
					} else {
						return "";
					}
				}
			}
		}

		return value;
	}
	private String getRangeValue(int index, String type, String extreme) {
		String value = "";
		Element args = getRootElement().getChild("args");
		if ( args == null ) {
			return value;
		} else {
			List regions = args.getChildren("region");
			if ( regions.size() - 1 < index ) {
				return value;
			}
			if ( index > 0 ) {
				value = getRangeValueHelper(1, type, extreme, regions);
				if ( value != null && !value.equals("") ) {
					return value;
				} else {
					value = getRangeValueHelper(0, type, extreme, regions);
					if ( value != null ) {
						return value;
					}
				}
				return value;
			} else {
				return getRangeValueHelper(index, type, extreme, regions);		
			}
		}
	}
	public String getXlo(int index) {
		index = index - 1;
		return getRangeValue(index, "x", "low");
	}
	public String getXlo() {
		return getRangeValue(0, "x", "low");
	}
	public String getXhi(int index) {
		index = index - 1;
		return getRangeValue(index, "x", "high");
	}
	public String getXhi() {
		return getRangeValue(0, "x", "high");
	}
	public String getYlo(int index) {	
		index = index - 1;
		return getRangeValue(index, "y", "low");
	}
	public String getYlo() {
		return getRangeValue(0, "y", "low");
	}
	public String getYhi(int index) {
		index = index - 1;
		return getRangeValue(index, "y", "high");
	}
	public String getYhi() {
		return getRangeValue(0, "y", "high");
	}
	public String getZlo(int index) {
		index = index - 1;
		return getRangeValue(index, "z", "low");
	}
	public String getZlo() {
		return getRangeValue(0, "z", "low");
	}
	public String getZhi(int index) {
		index = index - 1;
		return getRangeValue(index, "z", "high");
	}
	public String getZhi() {
		return getRangeValue(0, "z", "high");
	}
	public String getTlo(int index) {
		index = index - 1;
		return getRangeValue(index, "t", "low");	
	}
	public String getTlo() {
		return getRangeValue(0, "t", "low");
	}
	public String getThi(int index) {
		index = index - 1;
		return getRangeValue(index, "t", "high");
	}
	public String getThi() {
		return getRangeValue(0, "t", "high");
	}

	/**
	 * Sets the operation for this request.
	 * @param ID The operation ID to be used
	 */
	public void setOperation(String ID) {
		Element link = new Element("link");
		String operation_xpath = "/lasdata/operations/operation[@ID='"+ID+"']";
		link.setAttribute("match", operation_xpath);
		getRootElement().addContent(link);
	}

	/**
	 * Sets the options for this request.
	 * @param options An ArrayList<OptionBean> containing the options to be set.
	 */
	public void setOptions(String property_group, ArrayList<OptionBean> options) {
		Element properties = getRootElement().getChild("properties");
		Element group;
		if ( properties != null ) {
			group = properties.getChild(property_group);
			if ( group == null ) {
				group = new Element(property_group);
				properties.addContent(group);
			}
		} else {
			properties = new Element("properties");
			group = new Element(property_group);
			properties.addContent(group);
			getRootElement().addContent(properties);
		}
		for (Iterator opIt = options.iterator(); opIt.hasNext();) {
			OptionBean option = (OptionBean) opIt.next();
			String name = option.getWidget_name();
			String value = option.getValue();
			if ( value != null ) {
				Element n = new Element(name);
				n.setText(value);
				group.addContent(n);
			}
		}
	}
	public String getOperationXPath() {
		return this.getRootElement().getChild("link").getAttributeValue("match");
	}

	public String getOperation() {
		String operationXPath = getOperationXPath();
		if ( operationXPath.contains("@ID") ) {
			return operationXPath.substring(operationXPath.indexOf("'")+1, operationXPath.lastIndexOf("'"));
		} else {
			return operationXPath.substring(operationXPath.indexOf("/lasdata/operations/")+21, operationXPath.length());
		}
	}

	public String getSessionID() {
		String sessionID = this.getRootElement().getAttributeValue("SessionID");
		if (sessionID == null) {
			return "";
		} else {
			return sessionID;
		}
	}
	public boolean isOneToOne() {
		int data = 0;
		int region = 0;
		List args = this.getRootElement().getChildren("args");
		for (Iterator argIt = args.iterator(); argIt.hasNext();) {
			Element arg = (Element) argIt.next();
			if (arg.getName().equals("link")) {
				data++;
			}
			else if (arg.getName().equals("region")) {
				region++;
			}
		}
		return data == region;
	}

	public HashMap<String, String>  getPropertyGroup(String group_name) {
		HashMap<String, String> propertyGroup = new HashMap<String, String>();

		Element properties = this.getRootElement().getChild("properties");
		Element group = null;
		if ( properties != null ) {
			group = properties.getChild(group_name);
		}
		if(group != null) {
			for(Iterator propIt = group.getChild("property_group").getChildren().iterator(); propIt.hasNext();) {
				Element property = (Element) propIt.next();
				propertyGroup.put(property.getName(), property.getValue());
			}
			return propertyGroup;
		}
		else { 	for(Integer ct = 0; ct <5;) {
			propertyGroup.put("foo"+ct,"bar");
			ct++;
		}
		return propertyGroup;
		}
	}


	public Element getProperties() {
		return getRootElement().getChild("properties");
	}
	public String getProperty(String group_name, String property_name) {
		String value = "";
		Element properties = this.getRootElement().getChild("properties");
		Element group = null;
		if ( properties != null ) {
			group = properties.getChild(group_name);
		} 
		if ( group != null) {
			value = group.getChildTextTrim(property_name);
		}
		if ( value != null ) {
			return value;
		} else {
			return "";
		}
	}
	public void setProperty(String group_name, String name, String value) {
		Element properties = this.getRootElement().getChild("properties");

		if(properties == null){
			properties = new Element("properties");
			this.getRootElement().addContent(properties);
		}

		Element group = null;
		if ( properties != null ) {
			group = properties.getChild(group_name);
		}
		if ( group == null ) {
			group = new Element(group_name);
			properties.addContent(group);
		}
		Element nameE = group.getChild(name);
		if ( nameE == null ) {
			nameE = new Element(name);
			group.addContent(nameE);
		}
		nameE.setText(value);     
	}

	/**
	 * @return
	 */
	public boolean isAnalysisRequest() {
		List args = getRootElement().getChild("args").getChildren();
		for (Iterator argsIt = args.iterator(); argsIt.hasNext();) {
			Element arg = (Element) argsIt.next();
			Element analysis = arg.getChild("analysis");
			if ( analysis != null ) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Return the list of axes (x, y, z, t) that are analysis axes for a variable in a request.
	 * @param dsID the data set
	 * @param varID the variable
	 * @return the list of compressed axes
	 */
	public ArrayList<String> getAnalysisAxes(String dsID, String varID) {
		String oldstyle_varXPath = "/lasdata/datasets/"+dsID+"/variables/"+varID;
		return getAnalysisAxes(oldstyle_varXPath);
	} 
	/**
	 * Return the list of axes (x, y, z, t) that are analysis axes for a variable in a request.
	 * @param oldstyle_xpath variable XPath in the "old" style. e.g. /lasdata/datasets/dsID/variables/varID
	 * @param varID the variable
	 * @return the list of compressed axes
	 */
	public ArrayList<String> getAnalysisAxes(String oldstyle_xpath) {
		ArrayList<String> axes = new ArrayList<String>();
		List args = getRootElement().getChild("args").getChildren();
		for (Iterator argsIt = args.iterator(); argsIt.hasNext();) {
			Element arg = (Element) argsIt.next();
			String match = arg.getAttributeValue("match");
			Element analysis = arg.getChild("analysis");
			if ( analysis != null && match.equals(oldstyle_xpath)) {
				List axesElements = analysis.getChildren("axis");
				for (Iterator axisIt = axesElements.iterator(); axisIt.hasNext();) {
					Element axis = (Element) axisIt.next();
					axes.add(axis.getAttributeValue("type"));
				}
			}
		}
		return axes;
	} 

	/**
	 * Sets a range element of the LAS UI Request object.
	 * @param type the axis to set range
	 * @param lo   low end of the range
	 * @param hi   high end of the range
	 */
	public void setRange(String type, String lo, String hi){
		Element regionE =  getRootElement().getChild("args").getChild("region");
		if( regionE != null){
			List ranges = regionE.getChildren("range");

			for (Iterator rngIt=ranges.iterator(); rngIt.hasNext();){
				Element range = (Element) rngIt.next();
				if (range.getAttributeValue("type").equals(type)){
					range.setAttribute("low", lo);
					range.setAttribute("high",hi);
				}
				//regionE.addContent(range);
			}
		}
	}

	/**
	 * Change a range to a point on an axis
	 * @param type the axis to set range
	 * @param val  value of the point
	 */
	public void rangeToPoint(String type, String val){
		Element regionE =  getRootElement().getChild("args").getChild("region");

		//find the range and remove it
		if( regionE != null){
			List ranges = regionE.getChildren("range");
			Element theRange=null;

			for (Iterator rngIt=ranges.iterator(); rngIt.hasNext();){
				Element range = (Element) rngIt.next();
				if (range.getAttributeValue("type").equals(type)){
					//range.setAttribute("low", lo);
					//range.setAttribute("high",hi);
					theRange = range;
				}
				//regionE.addContent(range);
			}

			if(theRange != null){
				regionE.removeContent(theRange);

				//add point for this type
				Element point = new Element("point");
				point.setAttribute("type", type);
				point.setAttribute("v",val);
				regionE.addContent(point);
			}
		}
	}

	/**
	 * Get name of the dataset used by this request
	 */
	public String getDatasetName() {
		String dataLink = this.getRootElement().getChild("args").getChild("link").getAttributeValue("match");
		int isrt = dataLink.indexOf("/lasdata/datasets/");
		int iend = dataLink.indexOf("/variables/");
		return dataLink.substring(isrt+18, iend);
	}
	/**
	 * Get a list of all the Data Set IDs in this request.
	 * @return ids an ArrayList<String> of the data sets in this request.
	 */
	public ArrayList<String> getDatasetIDs() {
		ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> vars = getVariables();
		for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
			String varXPath = (String) varIt.next();
			if (varXPath.contains("ID") ) {
				ids.add(varXPath.substring(varXPath.indexOf("[@ID='")+6, varXPath.indexOf("']")));
			} else {
				if ( varXPath.contains("variables") ) {
					int isrt = varXPath.indexOf("/lasdata/datasets/");
					int iend = varXPath.indexOf("/variables/");			
					ids.add(varXPath.substring(isrt+18, iend));
				} else {
					// Assume it's a data set path and add it
					if ( varXPath.contains("datasets") ) {
						int isrt = varXPath.indexOf("/lasdata/datasets/");
						int iend = varXPath.length();
						ids.add(varXPath.substring(isrt+18, iend));
					}
				}
			}
		}
		return ids;
	}
	/**
	 * Get name of the variable used by this request -- This gets the ID not the variable name!
	 * @deprecated
	 */
	public String getVarName() {
		String dataLink = this.getRootElement().getChild("args").getChild("link").getAttributeValue("match");
		int isrt = dataLink.indexOf("/variables/");
		return dataLink.substring(isrt+11, dataLink.length());
	}

	/**
	 * Changes the operation for this request.
	 * @param ID the new operation (ID)
	 */
	public void changeOperation(String ID) {
		Element opLink = this.getRootElement().getChild("link");
		if(opLink != null){
			String operation_xpath = "/lasdata/operations/operation[@ID='"+ID+"']";
			opLink.setAttribute("match", operation_xpath);
		}
	}

	/**
	 * Changes the view for this request.
	 * @param v the new view
	 */
	public void changeView(String v) {
		Element view = this.getRootElement().getChild("properties").getChild("ferret").getChild("view");
		if(view != null){
			view.setText(v);
		}
	}

	//jing
	/**
	 * Add a constraint
	 */
	public void addTextConstraint(String lhs, String ope, String rhs, String label) {
		Element args = getRootElement().getChild("args");
		if ( args == null ) {
			args = new Element("args");
			getRootElement().addContent(args);
		}
		Element constraint = new Element("constraint");
		constraint.setAttribute("type", "text");
		Element lhsE = new Element("v");
		lhsE.setText(lhs);
		Element opeE = new Element("v");
		opeE.setText(ope);
		Element rhsE = new Element("v");
		rhsE.setText(rhs);
		constraint.addContent(lhsE);
		constraint.addContent(opeE);
		constraint.addContent(rhsE);
		if ( label != null ) {
			Element labelE = new Element("label");
			labelE.setText(label);
			constraint.addContent(label);
		}
		args.addContent(constraint);
	}
	public void removeTextConstraintByLHS(String lhs) {
		// E.G. Remove all the cruise id constraints
		Element args = getRootElement().getChild("args");
		List<Element> remove = new ArrayList<Element>();
		if ( args != null ) {
			List<Element> constraints = args.getChildren("constraint");
			for (Iterator cIt = constraints.iterator(); cIt.hasNext();) {
				Element constraint = (Element) cIt.next();
				String type = constraint.getAttributeValue("type");
				if ( type != null && type.equals("text") ) {
					List<Element> vs = constraint.getChildren("v");
					Element lhsE = vs.get(0);
					if (lhsE.getTextNormalize().equals(lhs) ) {
						remove.add(constraint);
					}
				}
			}
			for (Iterator removeIt = remove.iterator(); removeIt.hasNext();) {
				Element element = (Element) removeIt.next();
				args.removeContent(element);
			}
		}		
	}
	public String getKey() {
		try {
			LASUIRequest doc = (LASUIRequest) this.clone();
			Element props = doc.getProperties();
			props.removeChildren("product_server");
			return JDOMUtils.MD5Encode(doc.toString());
		} catch (UnsupportedEncodingException e) {
			return String.valueOf(Math.random());
		}
	}
	public String getFTDSURL(String varXPath) {
		String ftds_url = "";
		List vars = getRootElement().getChild("args").getChildren("link");
		for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
			Element var = (Element) varIt.next();
			String link = var.getAttributeValue("link");
			if ( link != null && link.equals(varXPath) ) {
				String f = var.getAttributeValue("ftds_url");
				if ( f != null ) {
					ftds_url = f;
				}
			}
		}
		return ftds_url;
	}
	/**
	 * Get variable name from a request that's been run through the URL resolver.
	 * @param varXPath
	 * @return
	 */
	public String getVariableName(String varXPath) {
		String var_name = "";
		List vars = getRootElement().getChild("args").getChildren("link");
		for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
			Element var = (Element) varIt.next();
			String link = var.getAttributeValue("link");
			if ( link != null && link.equals(varXPath) ) {
				String n = var.getAttributeValue("var_name");
				if ( n != null ) {
					var_name = n;
				}
			}
		}
		return var_name;
	}
	/**
	 * Get variable name from a request that's been run through the URL resolver.
	 * @param varXPath
	 * @return
	 */
	public String getVariableTitle(String varXPath) {
		String var_title = "";
		List vars = getRootElement().getChild("args").getChildren("link");
		for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
			Element var = (Element) varIt.next();
			String link = var.getAttributeValue("link");
			if ( link != null && link.equals(varXPath) ) {
				String t = var.getAttributeValue("var_title");
				if ( t != null ) {
					var_title = t;
				}
			}
		}
		return var_title;
	}

	/**
	 * Get variable name from a request that's been run through the URL resolver.
	 * @param varXPath
	 * @return
	 */
	public String getDatasetID(String varXPath) {
		String dsid = "";
		List vars = getRootElement().getChild("args").getChildren("link");
		for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
			Element var = (Element) varIt.next();
			String link = var.getAttributeValue("link");
			if ( link != null && link.equals(varXPath) ) {
				String t = var.getAttributeValue("dsid");
				if ( t != null ) {
					dsid = t;
				}
			}
		}
		return dsid;
	}

	/**
	 * Get variable name from a request that's been run through the URL resolver.
	 * @param varXPath
	 * @return
	 */
	public String getGridID(String varXPath) {
		String gridid = "";
		List vars = getRootElement().getChild("args").getChildren("link");
		for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
			Element var = (Element) varIt.next();
			String link = var.getAttributeValue("link");
			if ( link != null && link.equals(varXPath) ) {
				String t = var.getAttributeValue("gridid");
				if ( t != null ) {
					gridid = t;
				}
			}
		}
		return gridid;
	}
	/**
	 * 
	 * @return The constraints at an ArrayList<VariableConstraints>
	 */
	public List<VariableConstraint> getVariableConstraints() {
		List<VariableConstraint> cons = new ArrayList<VariableConstraint>();
		List constraints = this.getRootElement().getChild("args").getChildren("constraint");
		for (Iterator conIt = constraints.iterator(); conIt.hasNext();) {
			Element con = (Element) conIt.next();
			if ( con.getAttributeValue("type").equals("variable") ) {
				VariableConstraint d = new VariableConstraint(con);
				cons.add(d);
			}
		}
		return cons;
	}
	public void removeLinks() {
		getRootElement().removeChildren("link");
	}
	public void removeVariables() {
		getRootElement().getChild("args").removeChildren("link");
	}
}
