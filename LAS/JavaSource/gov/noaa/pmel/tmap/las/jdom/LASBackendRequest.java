/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development.
 */
package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.util.Constraint;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ucar.nc2.units.DateUnit;
/**
 * An instantiation of the LAS Backend Request XML.
 * @author Roland Schweitzer
 *
 */
public class LASBackendRequest extends LASDocument {

    /*
	 * Any number that uniquely identifies the version of this class' code.
	 * The Eclipse IDE will generate it automatically for you.  We do not depend on this
	 * since we do not serialize our code across the wire.
	 */
    private static final long serialVersionUID = 8177345236093847495L;

    private static Logger log = LoggerFactory.getLogger(LASBackendRequest.class.getName());
    /**
     * A convenience method that will report if the &lt;cancel&gt; element is present
     * in the request.  If so the service is supposed to stop processing any request it
     * has currently that has the same cache key.
     * @return true if this is a cancel request.
     * @throws IOException
     */
    public boolean isCancelRequest () throws IOException {
        Element cancel = this.getRootElement().getChild("cancel");
        String cancelFileName = getResultAsFile("cancel");
        if ( cancelFileName == null || cancelFileName.equals("") ) {
        	throw new IOException("Operation was not configured to allow it to be canceled.  No cancel result defined.  Operation not canceled.");
        }
        File cancelFile = new File(cancelFileName);
        // Should have been created by the runner.
        if ( cancel != null || cancelFile.exists() ) {
        	cancelFile.delete();
            return true;
        } else {
            return false;
        }
    }
    /**
     * Ask whether or not a request has been canceled.
     * @return true if canceled, false if not.
     */
    public boolean isCanceled() {
        boolean canceled = false;
        String cancelFile = getResultAsFile("cancel");
        File cancel = null;
        if ( cancelFile != null && !cancelFile.equals("") ) {
            cancel = new File(cancelFile);
            if ( cancel.exists() ) {
                cancel.delete();
                return true;
            }
        }

        return canceled;
    }
    /**
     * This is a convenience method to get the cancel result as a java.io.File
     * @return File object constructed from the path name in the cancel result; null if result does not exist.
     */
    public File getCancelFile() {
        String cancelFile = getResultAsFile("cancel");
        File cancel = null;
        if ( cancelFile != null && !cancelFile.equals("") ) {
            cancel = new File(cancelFile);
        }
        return cancel;
    }
    /**
     * A convenience method to add the &lt;cancel&gt; element to the request.
     *
     */
    public void setCancel() {
        Element cancel = new Element("cancel");
        this.getRootElement().addContent(cancel);
    }
    /**
     * Get operation that follows this operation in the chain (if any).
     */
    public String getChainedOperation() {
        Element chainedop = getRootElement().getChild("chained_operation");
        if ( chainedop != null ) {
            return chainedop.getTextNormalize();
        }
        return "";
    }
    public void setChainedOperation(String id) {
        Element chainedop = getRootElement().getChild("chained_operation");
        if ( chainedop == null ) {
            chainedop = new Element("chained_operation");
            getRootElement().addContent(chainedop);
        }
        chainedop.setText(id);
    }
    /**
     * Create a hash map of all of the information in the dataObjects so they can be written out as Ferret symbols.
     * @return HashMap of symbol names and values.
     * @see gov.noaa.pmel.tmap.las.jdom.LASBackendRequest#getDataSymbols()
     */
    public HashMap<String, String> getDataAsSymbols() {
        HashMap<String, String> symbols = new HashMap<String, String>();
        List dataObjects = this.getRootElement().getChildren("dataObjects");
        int index = 0;
        for (Iterator doIt = dataObjects.iterator(); doIt.hasNext();) {
            Element dataObject = (Element) doIt.next();
            List dataList = dataObject.getChildren("data");
            for (Iterator dataIt = dataList.iterator(); dataIt.hasNext();) {
                Element data = (Element) dataIt.next();
                List attributes = data.getAttributes();
                for (Iterator attIt = attributes.iterator(); attIt.hasNext();) {
                    Attribute attribute = (Attribute) attIt.next();
                    String name = attribute.getName();
                    String value = attribute.getValue();
                    // If the data attribute is also defined as a property, use that instead
                    String pre_defined = null;
                    try {
						pre_defined = getProperty("data_"+index, name);
					} catch (LASException e) {
						// That's ok.  We'll live with what we've got...	
					}
					if ( pre_defined != null && !pre_defined.equals("") ) {
						symbols.put("data_"+index+"_"+name, pre_defined);
					} else {
						symbols.put("data_"+index+"_"+name, value);
					}
                    // See if this variable has an associated transformation...
                    Element analysis = data.getChild("analysis");
                    if ( analysis != null ) {
                        String analysis_name = analysis.getAttributeValue("label");
                        if ( analysis_name != null ) {
                            analysis_name = analysis_name.replaceAll(" ", "_");
                        } else {
                            analysis_name = "analysis_"+index+"_variable";
                        }
                        symbols.put("analysis_var_name", analysis_name);
                        String prefix = "data_"+index+"_analysis_";
                        List attrs = analysis.getAttributes();
                        for (Iterator attrIt = attrs.iterator(); attrIt.hasNext();) {
                            Attribute attr = (Attribute) attrIt.next();
                            String atname = attr.getName();
                            String atvalue = attr.getValue();
                            symbols.put(prefix+atname, atvalue);
                        }
                        List axes = analysis.getChildren("axis");
                        int axis_index = 0;
                        symbols.put(prefix+"axis_count", Integer.toString(axes.size()));
                        for (Iterator axisIter = axes.iterator(); axisIter.hasNext();) {
                            Element analysis_axis = (Element) axisIter.next();
                            if (analysis_axis != null ) {
                                attrs = analysis_axis.getAttributes();
                                for (Iterator attrIt = attrs.iterator(); attrIt.hasNext();) {
                                    Attribute attr = (Attribute) attrIt.next();
                                    String atname = attr.getName();
                                    String atvalue = attr.getValue();
                                    symbols.put(prefix+"axis_"+axis_index+"_"+atname, atvalue);
                                }
                            }
                            axis_index++;
                        }
                    }
                }
                symbols.put("data_"+index+"_region", data.getChild("region").getAttributeValue("IDREF"));
                Element attribs = data.getChild("attributes");
                if ( attribs != null ) {
                    attributes = attribs.getChildren("attribute");
                    for (Iterator attIt = attributes.iterator(); attIt.hasNext();) {
                        Element attribute = (Element) attIt.next();
                        String name = attribute.getChildText("name");
                        String value = attribute.getChildText("value");
                        symbols.put("data_"+index+"_"+name, value);
                    }
                }
                index++;
            }
        }
        return symbols;
    }
    /**
     * Using the ID passed in find the data element that contains this result.
     * @param result_ID the ID of the chained result to find
     * @return the element of the chained data object
     * @see gov.noaa.pmel.tmap.las.jdom.LASBackendRequest#getChainedDataFile(String)
     * @see gov.noaa.pmel.tmap.las.jdom.LASBackendRequest#getChainedDataURL(String)
     */
    public Element getChainedData(String result_ID) {
    	List dataObjects = this.getRootElement().getChild("dataObjects").getChildren();
    	for (Iterator dataE = dataObjects.iterator(); dataE.hasNext();) {
    		Element data = (Element) dataE.next();
    		String chained = data.getAttributeValue("chained");
    		if (chained != null && chained.equals("true")) {
    			String id = data.getAttributeValue("result");
    			if (id.equals(result_ID)) {
    				return data;
    			}
    		}
    	}
    	return null;
    }
    /**
     * Get the data access URL of the chained result.
     * @param result_ID the chained result id
     * @return the data URL of the data object that matches
     */
    public String getChainedDataURL(String result_ID) {
    	Element data = getChainedData(result_ID);
    	String url = data.getAttributeValue("url");
    	if ( url != null ) {
    		return url;
    	} else {
    		return "";
    	}
    }
    /**
     * Get the chained result as a file
     * @param result_ID the id of the result that is chained
     * @return the path to the file
     */
    public String getChainedDataFile(String result_ID) {
    	Element data = getChainedData(result_ID);
    	String file = data.getAttributeValue("file");
    	if (file != null ) {
    		return file;
    	} else {
    		return "";


    	}
    }
    /**
     * Reduce all the region information into a HashMap of symbol names and values so the information can be handed to Ferret.
     * @return the region information as a HashMap
     */
    public HashMap<String, String> getRegionsAsSymbols() {
        HashMap<String, String> regions = new HashMap<String, String>();
        List regionElements = this.getRootElement().getChildren("region");
        for (Iterator regIt = regionElements.iterator(); regIt.hasNext();) {
            Element region = (Element) regIt.next();
            String regionID = region.getAttributeValue("ID");
            String value = region.getChildText("x_lo");
            if ( value != null ) {
                regions.put(regionID+"_"+"x_lo", value);
            }
            value = region.getChildText("x_hi");
            if (value != null) {
                regions.put(regionID+"_"+"x_hi", value);
            }
            value = region.getChildText("y_lo");
            if (value != null) {
                regions.put(regionID+"_"+"y_lo", value);
            }
            value = region.getChildText("y_hi");
            if (value != null) {
                regions.put(regionID+"_"+"y_hi", value);
            }
            value = region.getChildText("z_lo");
            if (value != null) {
                regions.put(regionID+"_"+"z_lo", value);
            }
            value = region.getChildText("z_hi");
            if (value != null) {
                regions.put(regionID+"_"+"z_hi", value);
            }
            value = region.getChildText("t_lo");
            if (value != null) {
                regions.put(regionID+"_"+"t_lo", value);
            }
            value = region.getChildText("t_hi");
            if (value != null) {
                regions.put(regionID+"_"+"t_hi", value);
            }
            value = region.getChildText("e_lo");
            if (value != null) {
                regions.put(regionID+"_"+"e_lo", value);
            }
            value = region.getChildText("e_hi");
            if (value != null) {
                regions.put(regionID+"_"+"e_hi", value);
            }
            value = region.getChildText("f_lo");
            if ( value != null ) {
            	regions.put(regionID+"_"+"f_lo", value);
            }
            value = region.getChildText("f_hi");
            if ( value != null ) {
            	regions.put(regionID+"_"+"f_hi", value);
            }
        }
        return regions;
    }

    /**
     * Get any properties that are not assigned to a particular data set as a HashMap
     * @return the properties as a HashMap of names and values.
     */
    public HashMap<String, String> getSymbols() {
        HashMap<String, String> symbols = new HashMap<String, String>();
        List propertiesList = this.getRootElement().getChildren("properties");
        for (Iterator props = propertiesList.iterator(); props.hasNext();) {
            Element properties = (Element) props.next();
            List propertyGroups = properties.getChildren("property_group");
            for (Iterator pgIt = propertyGroups.iterator(); pgIt.hasNext();) {
                Element group = (Element) pgIt.next();
                String type = group.getAttributeValue("type");
                List properityList = group.getChildren("property");
                for (Iterator propIt = properityList.iterator(); propIt.hasNext();) {
                    Element property = (Element) propIt.next();
                    // If it's defined add it.  Otherwise skip it.
                    String name = property.getChildText("name");
                    String value = property.getChildText("value");
                    if ( type.equals("ferret") && name.equals("expression") ) {
                        if ( value != null && !value.isEmpty() ) {
                            value = value.replaceAll("_ps_", "+");
                        }
                    }
                    if ( value != null && value != "" ) {
                        symbols.put(type+"_"+name, value);
                    }
                    else {
                        symbols.put(type+"_"+name, " ");
                    }
                }
            }
        }
        return symbols;
    }
    public HashMap<String, String>  getPropertyGroup(String group_name) {

        HashMap<String, String> propertyGroup = new HashMap<String, String>();
       	ArrayList groups = findPropertyGroupList(group_name);

	if(groups != null) {
		for(Iterator grpIt = groups.iterator(); grpIt.hasNext();) {
			Element grp = (Element) grpIt.next();
			for(Iterator propIt = grp.getChildren().iterator(); propIt.hasNext();) {
				Element property = (Element) propIt.next();
				propertyGroup.put(property.getChild("name").getValue(), property.getChild("value").getValue());

			}
		}
		return propertyGroup;
	}
	else return null;

    }
    public String getPropertyValuesCommaSeparated(String group_name) {
    	Map<String, String> group = getPropertyGroup(group_name);
    	StringBuilder values = new StringBuilder();
    	for (Iterator groupIt = group.keySet().iterator(); groupIt.hasNext();) {
			String key = (String) groupIt.next();
			String value = group.get(key);
			values.append(value);
			if ( groupIt.hasNext()) values.append(",");
		}
    	return values.toString();
    }

    /**
     * Translate the dataObjects in this request into pairs of names and values and return in a HashMap
     * @return the HashMap of dataObject information to be dumped as symbols for Ferret
     */
    public HashMap<String, String> getDataSymbols() {
        HashMap<String, String> symbols = new HashMap<String, String>();
        List data_objects = this.getRootElement().getChildren("dataObjects");
        for (Iterator doIt = data_objects.iterator(); doIt.hasNext();) {
            Element dataObject = (Element) doIt.next();
            List dataL = dataObject.getChildren("data");
            int index = 0;
            for (Iterator dataIt = dataL.iterator(); dataIt.hasNext();) {
                Element data = (Element) dataIt.next();
                symbols.putAll(getDataSymbols(data, index));
                index++;
            }
        }
        return symbols;
    }
    /**
     * A helper routine that pulls the properties out of a property group element.
     * @param group the group element
     * @param index the index of the data object that gets added to the symbol name.
     * @return the symbols in a HashMap of name and value
     */
    public HashMap<String, String> getSymbols (Element group, int index) {
        HashMap<String, String> symbols = new HashMap<String, String>();
        List properties = group.getChildren("property");
        String type = group.getAttributeValue("type");
        for (Iterator propIt = properties.iterator(); propIt.hasNext();) {
            Element property = (Element) propIt.next();
            String name = property.getChildText("name");
            String value = property.getChildText("value");
            symbols.put(type+"_"+index+"_"+name, value);
        }
        return symbols;
    }

     /**
      * For a given dataObject data element pull out the property groups, then get each property and flatten it all into a symbol for Ferret.
      * @param data the data element to process
      * @param index the index of the data element to add to the symbol name
      * @return the symbols in a HashMap of name and value
      */
    public HashMap<String, String> getDataSymbols(Element data, int index) {
        HashMap<String, String> symbols = new HashMap<String, String>();
        List properties = data.getChildren("properties");
        for (Iterator pIt = properties.iterator(); pIt.hasNext();) {
            Element props = (Element) pIt.next();
            List propertyGroups = props.getChildren("property_group");
            for (Iterator pgIt = propertyGroups.iterator(); pgIt.hasNext();) {
                Element group = (Element) pgIt.next();
                symbols.putAll(getSymbols(group, index));
            }
        }
        return symbols;
    }
    /**
     * Get all the information in this request as Ferret symbols
     * @return a hashmap of symbol names and values
     * @throws LASException 
     */
    public HashMap<String, String> getFerretSymbols() throws LASException {

        HashMap<String, String> symbols = new HashMap<String, String>();
        // Translate the results symbols for use by Ferret.

        int count = getResultCount();
        symbols.put("result_count", Integer.valueOf(count).toString());
        for (int index=0; index < count; index++ ) {
            String ID = getResultID(index);
            String filename = getResultFileName(index);
            String type = getResultType(index);
            symbols.put("result_"+ID+"_filename", filename);
            symbols.put("result_"+ID+"_type", type);
            symbols.put("result_"+ID+"_ID", ID);
        }

        // Global properties
        symbols.putAll(getSymbols());

        // Data symbols
        symbols.putAll(getDataSymbols());

        for ( int i = 0; i < getDataCount(); i++ ) {
            // Post-process the database_access properties for cruiseID, profID, latitude, longitude, time and depth
            // to get rid of any table names that appear since the netCDF file will have variable
            // names without the table names.
            String key = "database_access_"+i+"_longitude";
            String longitude = symbols.get(key);
            if ( longitude != null && longitude.contains(".") ) {
                longitude = longitude.substring(longitude.indexOf(".")+1, longitude.length());
                symbols.put(key, longitude);
            }
            key = "database_access_"+i+"_latitude";
            String latitude = symbols.get(key);
            if (latitude != null && latitude.contains(".") ) {
                latitude = latitude.substring(latitude.indexOf(".")+1, latitude.length());
                symbols.put(key, latitude);
            }
            key = "database_access_"+i+"_time";
            String time = symbols.get(key);
            if ( time != null && time.contains(".") ) {
                time = time.substring(time.indexOf(".")+1, time.length());
                symbols.put(key, time);
            }
            key = "database_access_"+i+"_depth";
            String depth = symbols.get(key);
            if ( depth != null && depth.contains(".") ) {
                depth = depth.substring(depth.indexOf(".")+1, depth.length());
                symbols.put(key, depth);
            }
            key = "database_access_"+i+"_cruiseID";
            String cruise_id_name = symbols.get(key);
            if ( cruise_id_name != null && cruise_id_name.contains(".") ) {
                cruise_id_name = cruise_id_name.substring(cruise_id_name.indexOf(".")+1, cruise_id_name.length());
                symbols.put(key, cruise_id_name);
            }
            key = "database_access_"+i+"_profID";
            String profile_id_name = symbols.get(key);
            if ( profile_id_name != null && profile_id_name.contains(".") ) {
                profile_id_name = profile_id_name.substring(profile_id_name.indexOf(".")+1, profile_id_name.length());
                symbols.put(key, profile_id_name);
            }
        }

        // Add a count symbol
        String definedCount = getProperty("data", "count");
        if ( definedCount == null || definedCount.equals("") ) {
            symbols.put("data_count", String.valueOf(getDataCount()));
        }

        // Data regions
        symbols.putAll(getRegionsAsSymbols());

        // Data objects
        symbols.putAll(getDataAsSymbols());

        // Constraints
        symbols.putAll(getConstraintsAsSymbols());

        return symbols;
    }
    /**
     * Get constraints in the request as Ferret symbols
     * @return symbol names and values
     */
    public HashMap<String, String> getConstraintsAsSymbols() {
        HashMap<String, String> symbols = new HashMap<String,String>();
        List constraints = this.getRootElement().getChildren("constraint");
        int index=0;
        List<MVSymbol> textSymbols = new ArrayList<MVSymbol>();
        for (Iterator conIt = constraints.iterator(); conIt.hasNext();) {
            Element con = (Element) conIt.next();
            List parts = con.getChildren();
            String type = con.getAttributeValue("type");
            if ( type.equals("text") ) {
                MVSymbol mvs= new MVSymbol();
                for (Iterator partIt = parts.iterator(); partIt.hasNext();) {
                    Element part = (Element) partIt.next();
                    String name = part.getName();
                    String value = part.getTextNormalize();
                    if ( value.contains("_ns_") ) {
                        String[] values = value.split("_ns_");
                        mvs.setValuesName(name);
                        mvs.setValues(values);
                    } else {
                        mvs.put(name, value);
                    }
                }
                textSymbols.add(mvs);
            } else {
                symbols.put("constraint_"+index+"_type", type);
                for (Iterator partIt = parts.iterator(); partIt.hasNext();) {
                    Element part = (Element) partIt.next();
                    String name = "constraint_"+index+"_"+part.getName();
                    String value = part.getTextNormalize();
                    if ( value.contains("_ns_") ) {
                        value = value.replace("_ns_", ",");
                    }
                    if ( value.contains("'") ) {
                        value = "\""+value+"\"";
                        symbols.put(name+"_quoted", "1");
                    }
                    symbols.put(name, value);
                }
                index++;
            }
        }
       
        for (Iterator mvsIt = textSymbols.iterator(); mvsIt.hasNext();) {
            
            MVSymbol mvs = (MVSymbol) mvsIt.next();
            if ( mvs.values != null ) {
                // repeat
                for (int i = 0; i < mvs.values.length; i++) {
                    for (Iterator partsIt = mvs.parts.keySet().iterator(); partsIt.hasNext();) {
                        String pname = (String) partsIt.next();
                        String name = "constraint_"+index+"_"+pname;
                        String value = mvs.parts.get(pname);
                        if ( value.contains("'") ) {
                            value = "\""+value+"\"";
                            symbols.put(name+"_quoted", "1");
                        }
                        symbols.put(name, value);
                    }
                    if ( mvs.values[i].contains("'") ) {
                        mvs.values[i] = "\""+mvs.values[i]+"\"";
                        symbols.put("constraint_"+index+"_"+mvs.valuesName+"_quoted", "1");
                    }
                    symbols.put("constraint_"+index+"_"+mvs.valuesName, mvs.values[i]);
                    symbols.put("constraint_"+index+"_type", "text");
                    index++;
                }
                
            } else {
                // print parts only.
                for (Iterator partsIt = mvs.parts.keySet().iterator(); partsIt.hasNext();) {
                    String pname = (String) partsIt.next();
                    String name = "constraint_"+index+"_"+pname;
                    String value = mvs.parts.get(pname);
                    if ( value.contains("'") ) {
                        value = "\""+value+"\"";
                        symbols.put(name+"_quoted", "1");
                    }
                    symbols.put(name, value);
                }
                symbols.put("constraint_"+index+"_type", "text");
                index++;
            }
        }
        return symbols;
    }
    private class MVSymbol {
        Map<String, String> parts = new HashMap<String, String>();
        String[] values;
        String valuesName;
        private void setValuesName(String valuesName) {
            this.valuesName = valuesName;
        }
        private void put(String name, String value) {
            parts.put(name, value);
        }
        private void setValues(String[] values) {
            this.values = values;
        }
    }
    /**
     * Count the number of results in the expected response
     * @return the count
     */
    public int getResultCount() {
        return this.getRootElement().getChild("response").getChildren("result").size();
    }
    /**
     * The number of data elements.
     * @return the count
     */
    public int getDataCount() {
        return this.getRootElement().getChild("dataObjects").getChildren("data").size();
    }
    /**
     * Get the result type at a particular index
     * @param i the index
     * @return the type of the result
     */
    public String getResultType(int i) {
        List results = this.getRootElement().getChild("response").getChildren("result");
        return ((Element)results.get(i)).getAttributeValue("type");
    }
    /**
     * Get the URL of the result at a particular index
     * @param i the index
     * @return the result url
     */
    public String getResultURL(int i) {
        List results = this.getRootElement().getChild("response").getChildren("result");
        return ((Element)results.get(i)).getAttributeValue("url");
    }
    /**
     * Get the file name of the result at a particular index
     * @param i the index
     * @return the file name
     */
    public String getResultFileName(int i) {
        List results = this.getRootElement().getChild("response").getChildren("result");
        return ((Element)results.get(i)).getAttributeValue("file");
    }
    /**
     * Get the MIME type of the result at a particular index
     * @param i the index
     * @return the MIME type
     */
    public String getResultMimeType(int i) {
        List results = this.getRootElement().getChild("response").getChildren("result");
        return ((Element)results.get(i)).getAttributeValue("mime_type");
    }
    /**
     * Get the ID of the result at a particular index.
     * @param i the index
     * @return the ID
     */
    public String getResultID(int i) {
        List results = this.getRootElement().getChild("response").getChildren("result");
        return ((Element)results.get(i)).getAttributeValue("ID");
    }
    /**
     * Get the URL of a result by ID
     * @param ID the id of the desired result
     * @return the URL of the result
     */
    public String getResult(String ID) {
        List responses = this.getRootElement().getChildren("response");
        if (responses.size() == 0) {
            //TODO Throw an exception!?
            return "No responses found.";
        }
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                String rID = result.getAttributeValue("ID");
                if (ID.equals(rID)) {
                    if (type.equals("error")) {
                        // Nothing to do...
                        return result.getText();
                    } else {
                       return result.getAttributeValue("url");
                    }
                }
            }
        }

        return "";
    }
    /**
     * Returns the file name of the result with the given ID.
     * @param ID the desired id
     * @return the file name of the result
     */
    public String getResultAsFile(String ID) {
        List responses = this.getRootElement().getChildren("response");
        if (responses.size() == 0) {
            //TODO Throw and exception!?
            return "No responses found.";
        }
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                String rID = result.getAttributeValue("ID");
                if (ID.equals(rID)) {
                    if (type.equals("error")) {
                        // Nothing to do...
                        return result.getText();
                    } else {
                       return result.getAttributeValue("file");
                    }
                }
            }
        }

        return "";
    }
    /**
     * Pull out the file name of a result according to type.
     * @param in_type the type of the desired result
     * @return the file name of the first result with a matching type
     */
    public String getResultAsFileByType(String in_type) {
        List responses = this.getRootElement().getChildren("response");
        if (responses.size() == 0) {
            //TODO Throw and exception!?
            return "No responses found.";
        }
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                if (type.equals(in_type)) {
                    if (type.equals("error")) {
                        // Nothing to do...
                        return result.getText();
                    } else {
                       return result.getAttributeValue("file");
                    }
                }
            }
        }

        return "";
    }
    /**
     * Get the service used by the backend service request
     * @return the name of the service
     * @throws JDOMException
     * @throws LASException
     */
    public String getService() throws JDOMException, LASException {
        Element operationProperties = getElementByXPath("/backend_request/properties/property_group[@type='operation']");
        return findPropertyValue(operationProperties, "service");
    }
    /**
     * Get the service action for this backend service request.
     * @return the service action
     * @throws JDOMException
     * @throws LASException
     */
    public String getServiceAction() throws JDOMException, LASException {
        Element operationProperties = getElementByXPath("/backend_request/properties/property_group[@type='operation']");
        return findPropertyValue(operationProperties, "service_action");
    }

    /**
     * Returns a list of variable names in this request.
     * @return variables an ArrayList of String objects
     */
    public ArrayList getVariables() {
        ArrayList<String> variables = new ArrayList<String>();
        String var_name = "";
        List data = this.getRootElement().getChild("dataObjects").getChildren("data");
        for (Iterator varIt = data.iterator(); varIt.hasNext();) {
            Element variable = (Element) varIt.next();
            var_name = variable.getAttributeValue("var");
            // Avoid duplicates
            if ( !variables.contains(var_name) ) {
                variables.add(var_name);
            }
        }
        return variables;
    }

    /**
     * Returns an SQL formatted string containing the list of variable names with no duplicates separated by commas.
     * @return variablesString SQL formatted string
     */
    public String getVariablesAsString() {
        String variables = "";
        List data = this.getRootElement().getChild("dataObjects").getChildren("data");
        Set<String> uniques = new HashSet<String>();
        for (Iterator varIt = data.iterator(); varIt.hasNext();) {
            Element variable = (Element) varIt.next();
            uniques.add(variable.getAttributeValue("var"));
        }
        for (Iterator varIt = uniques.iterator(); varIt.hasNext();) {
        	String variable = (String) varIt.next();
        	variables = variables+variable;
        	if (varIt.hasNext()) {
        		variables = variables + ",";
        	}
        }
        return variables;
    }
    /**
     * Returns an SQL formatted string containing the list of variable names separated by commas with each variable surrounded by an IFNULL construct.
     * @return variablesString SQL formatted string
     */
    public String getVariablesAsStringWithIFNULL() throws LASException {
        String variables = "";
        List data = this.getRootElement().getChild("dataObjects").getChildren("data");
        for (Iterator varIt = data.iterator(); varIt.hasNext();) {
            Element variable = (Element) varIt.next();
            Element db_access = findPropertyGroup(variable,"database_access");
            String missingValue = findPropertyValue(db_access, "missing");
            variables = variables+"IFNULL("+variable.getAttributeValue("var")+","+missingValue+")";
            if (varIt.hasNext()) {
                variables = variables + ", ";
            }
        }
        return variables;
    }
    /**
     * See if the request contains a variable of a particular name.
     * @param var the variable name to check
     * @return true if the request contains the variable
     */
    public boolean hasVariable(String var) {
        boolean hasVariable = false;
        List data = this.getRootElement().getChild("dataObjects").getChildren("data");
        for (Iterator varIt = data.iterator(); varIt.hasNext();) {
            Element variable = (Element) varIt.next();
            if (variable.getAttributeValue("var").equals(var) ) {
                hasVariable = true;
            }
        }
        return hasVariable;
    }
    /**
     * Get the value of a particular attribute for a particular variable.
     * @param variable look at this variable
     * @param attribute get the value of this attribute
     * @return the value of the attribute
     */
    public String getDataAttribute(String variable, String attribute) {
        String value="";
        List dataL = this.getRootElement().getChild("dataObjects").getChildren("data");
        for (Iterator varIt = dataL.iterator(); varIt.hasNext();) {
            Element data = (Element) varIt.next();
            if (data.getAttributeValue("var").equals(variable) ) {
                value = data.getAttributeValue(attribute);
                if ( value != null ) {
                    return value;
                } else {
                    return "";
                }
            }
        }
        return value;
    }
    /**
     * Build an SQL constraint that will mask out missing values.
     * @return the SQL needed to mask out missing values.
     * @throws LASException
     */
    public String getMissingConstraint() throws LASException  {
        String missing = "";
        List data = this.getRootElement().getChild("dataObjects").getChildren("data");
        for (Iterator varIt = data.iterator(); varIt.hasNext();) {
            Element variable = (Element) varIt.next();
            Element db_access = findPropertyGroup(variable,"database_access");
            String missingValue = findPropertyValue(db_access, "missing");
            if (missingValue.length() > 0 ) {
               // We have no way of knowing at this point how each variable is stored
               // in the database.  For float values we must use 'fuzzy matching'
               // when testing for the missing value.  For string values an exact
               // match is appropriate.
               // We add a special case for the standard Ferret missing value of -1.0E34
               // where the above logic fails to create a useful SQL constraint.
              try {
                float dummy = Float.valueOf(missingValue).floatValue();
                if (dummy < -1.0E33f) {
                  missing = missing + "(" + variable.getAttributeValue("var") + ">-1.0E+33" + ")";
                } else {
                  float dummy_minus = dummy - 0.0001f;
                  float dummy_plus = dummy + 0.0001f;
                  missing = missing + "(" + variable.getAttributeValue("var") + "<" + dummy_minus + " OR " +
                                            variable.getAttributeValue("var") + ">" + dummy_plus + ")";
                }
              } catch (NumberFormatException e) {
                missing = missing + "(" + variable.getAttributeValue("var") + "!=" + missingValue + ")";
              }
            }
            if (varIt.hasNext()) {
                missing = missing + " AND ";
            }
        }
        return missing;
    }
    /**
     * Get the value of a particular database property.
     * @param property
     * @return the value of the property
     * @throws LASException
     */
    public String getDatabaseProperty(String property) throws LASException {
        String value = "";
        Element data = this.getRootElement().getChild("dataObjects").getChild("data");
        Element db_access = findPropertyGroup(data,"database_access");
        if ( db_access == null ) {
            db_access = findPropertyGroup(data,"tabledap_access");
        }
        value = findPropertyValue(db_access, property);
        return value;
    }
    /**
     * Get a formatted string value for the high time in the region
     * @param format the format
     * @return the time string
     * @throws LASException
     */
    public String getThi(String format) throws LASException {
        Element region = this.getRootElement().getChild("region");
        String thi = region.getChildText("t_hi");
        if (thi != null) {
            return getDatabaseTime(thi, format);
        } else {
            return "";
        }
    }
    /**
     * Get a formatted string value for the low time in the region
     * @param format the format
     * @return the time string
     * @throws LASException
     */
    public String getTlo(String format) throws LASException  {
        Element region = this.getRootElement().getChild("region");
        String tlo = region.getChildText("t_lo");
        if (tlo != null) {
            return getDatabaseTime(tlo, format);
        } else {
            return "";
        }
    }
    /**
     * Get the high time value with no formatting applied
     * @return the high value of the time range
     */
    public String getThi() {
        Element region = this.getRootElement().getChild("region");
        String thi = region.getChildText("t_hi");
        if (thi != null) {
            return thi;
        } else {
           return "";
        }
    }
    public String getTlo(int i) {
    	String tlo = "";
    	List<Element> r = this.getRootElement().getChildren("region");
    	for (Iterator rIt = r.iterator(); rIt.hasNext();) {
			Element region = (Element) rIt.next();
			if ( region.getAttributeValue("ID").equals("region_"+i)) {
				tlo = region.getChildText("t_lo");
			}
		}
    	if ( tlo != null ) {
    		return tlo;
    	} else {
    		return "";
    	}
    }
    public String getTlo(int i, String format) throws LASException {
    	String t = getTlo(i);
    	return getDatabaseTime(t, format);
    }
    public String getThi(int i, String format) throws LASException {
    	String t = getThi(i);
    	return getDatabaseTime(t, format);
    }
    public String getThi(int i) {
    	String tlo = "";
    	List<Element> r = this.getRootElement().getChildren("region");
    	for (Iterator rIt = r.iterator(); rIt.hasNext();) {
			Element region = (Element) rIt.next();
			if ( region.getAttributeValue("ID").equals("region_"+i)) {
				tlo = region.getChildText("t_hi");
			}
		}
    	if ( tlo != null ) {
    		return tlo;
    	} else {
    		return "";
    	}
    }
    /**
     * Get the low time value with no formatting applied
     * @return the high value of the time range
     */
    public String getTlo() {
        Element region = this.getRootElement().getChild("region");
        String tlo = region.getChildText("t_lo");
        if (tlo != null) {
            return tlo;
        } else {
           return "";
        }
    }
    /**
     * Get the high Z value.
     * @return the high z
     */
    public String getZhi() {
        Element region = this.getRootElement().getChild("region");
        String zhi = region.getChildText("z_hi");
        if (zhi != null) {
            return zhi;
        } else {
           return "";
        }
    }
    /**
     * Get the low Z value.
     * @return the low z
     */
    public String getZlo() {
        Element region = this.getRootElement().getChild("region");
        String zlo = region.getChildText("z_lo");
        if (zlo != null) {
            return zlo;
        } else {
           return "";
        }
    }
    /**
     * Get the high Y value.
     * @return the high y
     */
    public String getYhi() {
        Element region = this.getRootElement().getChild("region");
        String yhi = region.getChildText("y_hi");
        if (yhi != null) {
            return yhi;
        } else {
           return "";
        }
    }
    /**
     * Get the low Y value
     * @return the low y
     */
    public String getYlo() {
        Element region = this.getRootElement().getChild("region");
        String ylo = region.getChildText("y_lo");
        if (ylo != null) {
            return ylo;
        } else {
           return "";
        }
    }
    /**
     * Get the high X value.
     * @return the high x
     */
    public String getXhi() {
        Element region = this.getRootElement().getChild("region");
        String xhi = region.getChildText("x_hi");
        if (xhi != null) {
            return xhi;
        } else {
           return "";
        }
    }
    /**
     * Get the low X value.
     * @return the low x
     */
    public String getXlo() {
        Element region = this.getRootElement().getChild("region");
        String xlo = region.getChildText("x_lo");
        if (xlo != null) {
            return xlo;
        } else {
           return "";
        }
    }
    /**
     * Get a normalized x high value.
     * @return x high between -180 and 180
     * @throws LASException
     */
    public String getDatabaseXhi() throws LASException {
        Element region = this.getRootElement().getChild("region");
        String lon_domain = getDatabaseProperty("lon_domain");
        String xhi = region.getChildText("x_hi");
        if (xhi != null) {
            if ( lon_domain == null || lon_domain.equals("")) {
                return xhi;
            } else {
                String parts[] = lon_domain.split(":");
                float lon_lo = Float.valueOf(parts[0]).floatValue();
                float lon_hi = Float.valueOf(parts[1]).floatValue();
                float hi = Float.valueOf(xhi).floatValue();
                if ( hi < lon_lo ) { hi = hi + 360.f; }
                if ( hi > lon_hi ) { hi = hi - 360.f; }
                return String.valueOf(hi);
            }
        } else {
           return "";
        }
    }
    /**
     * Get a normalized x low value.
     * @return x low between -180 and 180
     * @throws LASException
     */
    public String getDatabaseXlo() throws LASException {
        Element region = this.getRootElement().getChild("region");
        String lon_domain = getDatabaseProperty("lon_domain");
        String xlo = region.getChildText("x_lo");
        if (xlo != null) {
            if ( lon_domain == null || lon_domain.equals("")) {
                return xlo;
            } else {
                String parts[] = lon_domain.split(":");
                float lon_lo = Float.valueOf(parts[0]).floatValue();
                float lon_hi = Float.valueOf(parts[1]).floatValue();
                float lo = Float.valueOf(xlo).floatValue();
                if ( lo < lon_lo ) { lo = lo + 360.f; }
                if ( lo > lon_hi ) { lo = lo - 360.f; }
                return String.valueOf(lo);
            }
        } else {
           return "";
        }

    }
    /**
     * Get an SQL statement to constraint a query according the range of the axis
     * @param type the axis (x, y, z, t)
     * @return the SQL statement
     * @throws LASException
     */
    public String getAxisAsConstraint(String type) throws LASException {
        String lo = "";
        String hi = "";
        String name = "";
        boolean quotes = false;
        String table="";

        if (type.equals("x")) {
            lo = getDatabaseXlo();
            hi = getDatabaseXhi();
            name = getDatabaseProperty("longitude");
        } else if (type.equals("y")) {
            lo = getYlo();
            hi = getYhi();
            name = getDatabaseProperty("latitude");
        } else if (type.equals("z")) {
            lo = getZlo();
            hi = getZhi();
            name = getDatabaseProperty("depth");
        } else if (type.equals("t")) {
            String tlo = getTlo();
            String thi = getThi();
            lo = getDatabaseTime(tlo);
            hi = getDatabaseTime(thi);
            name = getDatabaseProperty("time");
            String time_type = getDatabaseProperty("time_type");

            if ( time_type == null || time_type.equals("") ) {
                throw new LASException("Cannot find time_type database property");
            }
            if ( time_type.equalsIgnoreCase("string") ) {
                quotes = true;
            }
        }
        return axisConstraint(name, type, lo, hi, quotes);

    }
    /**
     * Get a list of strings that will constrain a DRDS request according to the range of the axis
     * @param type the axis type to be constrained (x, y, z or t)
     * @return the list of string (you might need two if it's a wrap-around constraint in X)
     * @throws LASException
     */
    public ArrayList<String> getAxisAsDRDSConstraint(String type) throws LASException {
        String lo = "";
        String hi = "";
        String name = "";
        boolean quotes = false;
        String table="";

        table = getDatabaseProperty("db_table");

        if (type.equals("x")) {
            lo = getDatabaseXlo();
            hi = getDatabaseXhi();
            name = getDatabaseProperty("longitude");
        } else if (type.equals("y")) {
            lo = getYlo();
            hi = getYhi();
            name = getDatabaseProperty("latitude");
        } else if (type.equals("z")) {
            lo = getZlo();
            hi = getZhi();
            name = getDatabaseProperty("depth");
        } else if (type.equals("t")) {
            String tlo = getTlo();
            String thi = getThi();
            lo = getDatabaseTime(tlo);
            hi = getDatabaseTime(thi);
            name = getDatabaseProperty("time");
            String time_type = getDatabaseProperty("time_type");

            if ( time_type == null || time_type.equals("") ) {
                throw new LASException("Cannot find time_type database property");
            }
            if ( time_type.equalsIgnoreCase("string") ) {
                quotes = true;
            }
        }

        return axisDRDSConstraint(table, name, type, lo, hi);

    }
    /**
     * Get the entire region as an SQL statement that will select only values within the region
     * @return the SQL statement
     * @throws LASException
     */
    public String getRegionAsConstraint() throws LASException {

        String xlo = getDatabaseXlo();
        String xhi = getDatabaseXhi();
        String ylo = getYlo();
        String yhi = getYhi();
        String zlo = getZlo();
        String zhi = getZhi();
        String tlo = getTlo();
        String thi = getThi();

        String lonAxis = axisConstraint(getDatabaseProperty("longitude"), "x", xlo, xhi, false);
        String latAxis = axisConstraint(getDatabaseProperty("latitude"), "y", ylo, yhi, false);
        String zAxis = axisConstraint(getDatabaseProperty("depth"), "z", zlo, zhi, false);
        String time_type = getDatabaseProperty("time_type");
        boolean quotes = false;

        if ( time_type == null || time_type.equals("") ) {
            throw new LASException("Cannot find time_type database property");
        }

        if ( time_type.equalsIgnoreCase("string") ) {
            quotes = true;
        }
        String timeAxis = axisConstraint(getDatabaseProperty("time"), "t",
                getDatabaseTime(tlo),
                getDatabaseTime(thi), quotes);

        String region_constraint = "";

        if (lonAxis != "" ) {
            region_constraint = lonAxis;
        }
        if ( latAxis != "" ) {
            if (region_constraint != "" ) {
                region_constraint = region_constraint+" AND "+latAxis;
            } else {
                region_constraint = latAxis;
            }
        }
        if ( zAxis != "" ) {
            if (region_constraint != "" ) {
                region_constraint = region_constraint+" AND "+zAxis;
            } else {
                region_constraint = zAxis;
            }
        }
        if ( timeAxis != "" ) {
            if (region_constraint != "" ) {
                region_constraint = region_constraint+" AND "+timeAxis;
            } else {
                region_constraint = timeAxis;
            }
        }


        return region_constraint;
    }
    /**
     * Convert a time value according to the time_type database property.
     * @param time the time to convert
     * @return the converted time
     * @throws LASException
     */
    public String getDatabaseTime(String time) throws LASException {


        String time_type = getDatabaseProperty("time_type");
        if ( time_type == null || time_type.equals("") ) {
            throw new LASException("Cannot find time_type database property");
        }

        String time_units = getDatabaseProperty("time_units");
        if ( time_units == null || time_units.equals("") ) {
            throw new LASException("Cannot find time_units database property");
        }

        if (time_type.equalsIgnoreCase("double") ) {
            return String.valueOf(getDatabaseTimeAsDouble(time, time_units));
        } else {
            String target_format = getDatabaseProperty("time_format");
            if ( target_format == null || target_format.equals("") ) {
                throw new LASException("Cannot find time_format database property");
            }
            return getDatabaseTime(time, target_format);
        }

    }
    /**
     * Given a UDUNITS-style time unit (hours since 1990-01-01 00:00:00) and a formatted time string get the double value
     * @param time the formatted time string
     * @param time_units the UDUNITS-style units string
     * @return the double value representing the time
     * @throws Exception
     */
    public double getDatabaseTimeAsDouble(String time, String time_units) throws LASException {

        DateTimeFormatter short_fmt = DateTimeFormat.forPattern("dd-MMM-yyyy").withZone(DateTimeZone.UTC);
        DateTimeFormatter medium_fmt = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm").withZone(DateTimeZone.UTC);
        DateTimeFormatter long_fmt = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss").withZone(DateTimeZone.UTC);
        DateUnit dateUnit;
        try {
            dateUnit = new DateUnit(time_units);
        } catch (Exception e) {
            throw new LASException(e.toString());
        }


        DateTime dt = null;
        if (time.length() > 17) {          
            dt = long_fmt.withZone(DateTimeZone.UTC).parseDateTime(time);
        } else if ( time.length() > 11) { 
            dt = medium_fmt.withZone(DateTimeZone.UTC).parseDateTime(time);
        } else {
            dt = short_fmt.withZone(DateTimeZone.UTC).parseDateTime(time);
        }
        return dateUnit.makeValue(new Date(dt.getMillis()));
    }
    /**
     * Convert a time string to the desired format
     * @param time the original time string
     * @param target_format the new format
     * @return the newly formatted time string
     * @throws LASException
     */
    public String getDatabaseTime(String time, String target_format) throws LASException {

        DateTimeFormatter short_fmt = DateTimeFormat.forPattern("dd-MMM-yyyy").withZone(DateTimeZone.UTC).withLocale(Locale.US);
        DateTimeFormatter long_fmt = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss").withZone(DateTimeZone.UTC).withLocale(Locale.US);
        DateTimeFormatter target_fmt = DateTimeFormat.forPattern(target_format).withZone(DateTimeZone.UTC).withLocale(Locale.US);

        DateTime dt;
        if (time.length() > 11) {
            dt = long_fmt.parseDateTime(time);
        } else {
            dt = short_fmt.parseDateTime(time);
        }
        return target_fmt.print(dt);
    }
    /**
     * Create a constraint expression on the DRDS URL to select inside the range of the given axis
     * @param table the table to constrain
     * @param column the column that contains this axis
     * @param type the axis type (x, y, z, t)
     * @param lo the low value
     * @param hi the high value
     * @return the DRDS constraint expression
     * @throws LASException
     */
    private ArrayList<String> axisDRDSConstraint(String table, String column, String type, String lo, String hi) throws LASException {

        ArrayList<String> axisConstraints = new ArrayList<String>();

        String glue = "&";

        if ( type.equals("x")) {

            if ( Float.valueOf(lo).floatValue() + .01 > Float.valueOf(hi).floatValue() &&
                    Float.valueOf(lo).floatValue() - .01 < Float.valueOf(hi).floatValue() ) {
                // The whole globe was selected (and the values wrap around) so we don't need this constraint.
                return axisConstraints;
            } else if ( Float.valueOf(hi).floatValue() < Float.valueOf(lo).floatValue() ) {
               /*
                *             ------------------------------
                *             |                            |
                * xhi < xlo   |----x                 x-----|
                *             |                            |
                * xlo < xhi   |    x-----------------x     |
                *             |                            |
                *             ------------------------------
                *
                */
                String constraint = "";
                if (lo != null && !lo.equals("")) {
                    constraint = table+"."+column+">="+lo;
                    axisConstraints.add(constraint);
                }
                if (hi != null && !hi.equals("")) {
                    constraint = table+"."+column+"<="+hi;
                    axisConstraints.add(constraint);
                }
                return axisConstraints;
            }
        }

        String constraint = "";
        if (lo != null && !lo.equals("")) {
            constraint = table+"."+column+">="+lo;
        }
        if (hi != null && !hi.equals("")) {
            if (constraint != "") {
                constraint = constraint+glue+table+"."+column+"<="+hi;
            } else {
                constraint = table+"."+column+"<="+hi;
            }
        }
        if ( constraint != "" ) {
           axisConstraints.add(constraint);
        }

        return axisConstraints;

    }
    /**
     * Create an SQL statement to select inside the range of the given axis
     * @param column the column where this axis is stored
     * @param type the axis type (x, y, z or t)
     * @param lo the low value
     * @param hi the high value
     * @param quotes control whether quotes are used
     * @return the SQL statement
     */
    private String axisConstraint(String column, String type, String lo, String hi, boolean quotes) {

    	String axisConstraint = "";

    	String glue = "AND";

    	if ( type.equals("x")) {

    		if ( Float.valueOf(lo).floatValue() + .01 > Float.valueOf(hi).floatValue() &&
                 Float.valueOf(lo).floatValue() - .01 < Float.valueOf(hi).floatValue() ) {
          // The whole globe was selected (and the values wrap around) so we don't need this constraint.
    			return axisConstraint;
    		} else if ( Float.valueOf(hi).floatValue() < Float.valueOf(lo).floatValue() ) {
          // The region selected cross the edge of the map so we need the two outside sections
    			// (not the middle between the values)
    			glue = "OR";
    		}
    	}
    	if ( quotes ) {
    		if (lo != null && !lo.equals("")) {
    			axisConstraint = "("+column+">=\""+lo+"\")";
    		}
    		if (hi != null && !hi.equals("")) {
    			if (axisConstraint != "") {
    				axisConstraint = "("+axisConstraint+" "+glue+" ("+column+"<=\""+hi+"\"))";
    			} else {
    				axisConstraint = "("+column+"<=\""+hi+"\")";
    			}
    		}
    	} else {
    		if (lo != null && !lo.equals("")) {
    			axisConstraint = "("+column+">="+lo+")";
    		}
    		if (hi != null && !hi.equals("")) {
    			if (axisConstraint != "") {
    				axisConstraint = "("+axisConstraint+" "+glue+" ("+column+"<="+hi+"))";
    			} else {
    				axisConstraint = "("+column+"<="+hi+")";
    			}
    		}
    	}
    	return axisConstraint;

    }

    /**
     * Returns an array list of gov.noaa.pmel.tmap.las.util.Constraint objects
     * @return constraints an ArrayList of gov.noaa.pmel.tmap.las.util.Constraint objects
     */
    public ArrayList getConstraints() {
        ArrayList<Constraint> constraints = new ArrayList<Constraint>();
        List constraintElements = this.getRootElement().getChildren("constraint");
        for (Iterator cIt = constraintElements.iterator(); cIt.hasNext();) {
            Element constraint = (Element) cIt.next();
            String rhsString = constraint.getChildText("rhs");
            String lhsString = constraint.getChildText("lhs");
            String opString = constraint.getChildText("op");
            Constraint c = new Constraint(lhsString, opString, rhsString);
            constraints.add(c);
        }
        return constraints;
    }
    /**
     * Get the value (right-hand side) of the constraint based on the value of the left-hand side (the name of the constratain).
     * @param lhs     the "name" (left-hand side) of the constraint
     * @return rhs    the "value" (right-hand side) of the constraint
     */
    public String getConstraintRHS(String lhs) {
        List constraints = this.getRootElement().getChildren("constraint");

        for (Iterator cIt = constraints.iterator(); cIt.hasNext();) {
            Element constraint = (Element) cIt.next();
            String rhsString = constraint.getChildText("rhs");
            String lhsString = constraint.getChildText("lhs");
            if (lhsString.equals(lhs)) {
                return rhsString;
            }
        }
        return "";
    }

    /**
     * Get the a constraint based on the value of the left-hand side (the name of the constratain).
     * @param lhs            the "name" (left-hand side) of the constraint
     * @return constraint    the constraint (won't be null, but can contain empty strings)
     */
    public Constraint getConstraint(String lhs) {
        List constraints = this.getRootElement().getChildren("constraint");
        for (Iterator cIt = constraints.iterator(); cIt.hasNext();) {
            Element constraintElement = (Element) cIt.next();
            String rhsString = constraintElement.getChildText("rhs");
            String opString = constraintElement.getChildText("op");
            String lhsString = constraintElement.getChildText("lhs");
            if (lhsString.equals(lhs)) {
                return new Constraint(lhsString, opString, rhsString);
            }
        }
        return new Constraint();
    }
    /**
     * Construct a variable constraint from the variable that is suitable as a constraint
     * expression on a DRDS server.
     * @param variable the variable to be use to build the constraint
     * @return the constraint as a string suitble for use with a DRDS URL.
     * @throws LASException
     */
    public String getDRDSVariableConstraintString(String variable) throws LASException {
        String constring = "";
        Constraint con = getConstraint(variable);
        String table = getDatabaseProperty("db_table");
        if ( con != null && con.getLhs() != "" ) {
            constring = table+"."+con.getLhs()+con.getOpAsSymbol()+con.getRhs();
        }
        return constring;
    }
    /**
     * Get all the constraints in a request as a string that can be used to limit the select joined by the operator.
     * @param operator join each constraint using this operator (usually AND or OR)
     * @return a giant string that can be used in the select
     */
    public String getConstraintString(String operator) {
        List contraints = this.getRootElement().getChildren("constraint");
        String con="";
        for (Iterator cIt = contraints.iterator(); cIt.hasNext();) {
            Element constraint = (Element) cIt.next();
            String opString = "=";
            String op = constraint.getChildText("op");
            String rhsString = constraint.getChildText("rhs");
            String lhsString = constraint.getChildText("lhs");
                if ( op.equals("lt")) {
                    opString = "<";
                } else if ( op.equals("le")) {
                    opString = "<=";
                } else if (op.equals("eq")) {
                    opString = "=";
                } else if (op.equals("ne") ) {
                    opString = "!=";
                } else if (op.equals("gt")) {
                    opString = ">";
                } else if (op.equals("ge")) {
                    opString = ">=";
                }
            try {
                float dummy = Float.valueOf(rhsString).floatValue();
                con = con + lhsString+opString+rhsString;
            } catch (NumberFormatException e) {
                con = con + lhsString+opString+"\""+rhsString+"\"";
            }
            if (cIt.hasNext()) {
                con = con + " "+ operator +" ";
            }
        }
        return con;
    }
    /**
     * Get the "ui_timeout" property value.  If this time out is reach, LAS should send a message to the client indicating that
     * it is still working on the request.
     * @return the value of the time out (specified in seconds in the config)
     */
    public long getProgressTimeout() {
        // The semantics of the this is known.  If there is more
        // than one, get all of them and return the biggest.
        long timeout = 0;

        ArrayList groups = this.findPropertyGroupList("product_server");
        for (Iterator pgIt = groups.iterator(); pgIt.hasNext();) {
            Element group = (Element) pgIt.next();
            List properties = group.getChildren("property");
            for (Iterator pIt = properties.iterator(); pIt.hasNext();) {
                Element property = (Element) pIt.next();
                if (property.getChildTextTrim("name").equals("ui_timeout")) {
                    String timeoutString = property.getChildTextTrim("value");
                    long time = Long.valueOf(timeoutString).longValue();
                    if ( time > timeout ) {
                        timeout = time;
                    }
                }
            }
        }
        return timeout;
    }
    /**
     * Get the "product_timeout" value (0 if it does not exist).  All processing for this request should stop if this timeout is reached.
     * @return the time out value (specified in seconds in the config)
     */
    public long getProductTimeout() {

        long timeout = 0;
        ArrayList groups = this.findPropertyGroupList("product_server");
        for (Iterator pgIt = groups.iterator(); pgIt.hasNext();) {
            Element group = (Element) pgIt.next();
            List properties = group.getChildren("property");
            for (Iterator pIt = properties.iterator(); pIt.hasNext();) {
                Element property = (Element) pIt.next();
                if (property.getChildTextTrim("name").equals("ps_timeout")) {
                    String timeoutString = property.getChildTextTrim("value");
                    long time = Long.valueOf(timeoutString).longValue();
                    if ( time > timeout ) {
                        timeout = time;
                    }
                }
            }
        }
        return timeout;
    }
    /**
     * Remove an entire property group from the request.  This is used to keep properties that don't affect the processing
     * for a particular service from polluting the cache key for that service when it is running as part of a compound product.
     * @param group
     * @return true if remove was successful; false if it was not
     */
    public boolean removePropertyGroup(String group) {
        boolean removed = true;
        ArrayList groups = findPropertyGroupList(group);
        for (Iterator pgIt = groups.iterator(); pgIt.hasNext();) {
            Element prop_group = (Element) pgIt.next();
            boolean test = prop_group.getParent().removeContent(prop_group);
            removed = test && removed;
        }
        return removed;
    }
    /**
     * Set an empty element <remote> below the root to tell the service to
     * return URLs instead of file names.
     */
    public void runRemote() {
        Element root = getRootElement();
        Element remote = new Element("remote");
        root.addContent(remote);
    }
    /**
     * Check for the existence of the <remote> element and return true if found.
     * @return true if the service is running remotely
     */
    public boolean isRemote() {
        Element remote = getRootElement().getChild("remote");
        if ( remote != null ) {
            return true;
        }
        return false;
    }
    /**
     * If this service is running remotely, this is the URL of the remote server.
     * @return the URL
     */
    public String getRemoteURL() {
        Element remote = getRootElement().getChild("remote");
        if ( remote != null ) {
            return remote.getAttributeValue("url");
        }
        return "";
    }
    /**
     * Translates the suggested file names into local file names for use by a remote service.
     * @param output_dir the output directory for this server
     * @param http_base_url the base url of the tomcat server
     * @param opendap_base_url the base url of the remote F-TDS server
     * @throws JDOMException
     */
    public void setLocalFileNames(String output_dir, String http_base_url, String opendap_base_url) throws JDOMException {
        log.debug("setting local file names...");
        Element response = getElementByXPath("/backend_request/response");
        List results = response.getChildren("result");
        for (Iterator resIt = results.iterator(); resIt.hasNext();) {
            Element result = (Element) resIt.next();
                String file = result.getAttributeValue("file");
                // Java on windows can deal with "/" in the path.
                // Java on Unix cannot deal with a "\".  So always use "/" and everybody's happy.
                if ( file.contains("\\") ) {
                   file = file.replaceAll("\\\\", "/");
                }
                File fileobj = new File(file);
                file = fileobj.getName();
                String remote_url;
                if (result.getAttributeValue("type").equalsIgnoreCase("netcdf") ) {
                    remote_url = opendap_base_url + "/" + file;
                } else {
                    remote_url = http_base_url + "/" + file;
                }
                String output_file = output_dir + File.separator + file;
                result.setAttribute("file", output_file);
                result.setAttribute("url", remote_url);
                result.setAttribute("remote", "true");
        }

    }
    /**
     * Set the URL for a particular result type. This is used for the RSS feed URL since that result "belongs" to the product server.
     * @param serverURL the URL of the LAS server
     * @param resultType the result type to be mapped
     */
    public void mapResultToURL(String serverURL, String resultType) {
        List responses = this.getRootElement().getChildren("response");
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                if (type.equals("error")) {
                    // Nothing to do...
                    return;
                }
                String url = result.getAttributeValue("url");
                // Replace the URL value if it's not already set by the backend service.
                if ( url == null || url.equals("") ) {
                    if (type.equals(resultType)) {
                        String newurl = result.getAttributeValue("file");
                        newurl = newurl.substring(newurl.lastIndexOf(File.separator) + 1, newurl.length());
                        newurl = serverURL + "/output/" + newurl;
                        result.setAttribute("url", newurl);
                    }

                }
            }
        }
    }
    /** For each variable in this request get the list of axes that are to be transformed by an analysis request.  The HashMap is keyed
     * by the XPath of the variable and the value is an array list of axis types (x, y, z, t).
     * @return the collection of variables and their corresponding transformed axes.
     */
    public HashMap<String, ArrayList<String>> getAnalysisAxes() {
        HashMap<String, ArrayList<String>> analysisAxes = new HashMap<String, ArrayList<String>>();
        List data = getRootElement().getChild("dataObjects").getChildren("data");
        for (Iterator dataIt = data.iterator(); dataIt.hasNext();) {
            Element dataE = (Element) dataIt.next();
            Element analysis = dataE.getChild("analysis");
            String xpath = dataE.getAttributeValue("xpath");
            if ( analysis != null ) {
                List axes = analysis.getChildren("axis");
                ArrayList<String> analysis_axes = new ArrayList<String>();
                for (Iterator axesIt = axes.iterator(); axesIt.hasNext();) {
                    Element axis = (Element) axesIt.next();
                    String type = axis.getAttributeValue("type");
                    analysis_axes.add(type);
                }
                analysisAxes.put(xpath, analysis_axes);
            }
        }
        return analysisAxes;
    }
    /**
     * Get the JSESSIONID if it exists (returns an empty string if it does not exist).
     * @return the session ID.
     */
    public String getJSESSIONID() {
        String JSESSIONID;
        JSESSIONID = getRootElement().getAttributeValue("JSESSIONID");
        if ( JSESSIONID != null && !JSESSIONID.equals("") ) {
            return JSESSIONID;
        } else {
            return "";
        }
    }
    /**
     * Remove a particular property from the property group.  This is used to "clean" requests
     * to services in compound requests so that the cache key is not polluted by properties
     * that have no effect on the processing for that particular service.
     * @param group the property group name
     * @param property the name of the property
     * @return whether or not the remove was successful
     * @throws LASException
     */
	public boolean removeProperty(String group, String property) throws LASException {
		Element groupE = findPropertyGroup(group);
                if(groupE != null){
		    Element propE = findProperty(groupE, property);
                    if(propE != null){
		        boolean remove = groupE.removeContent(propE);
		        return remove;
                    }
                }
                return false;
	}
	public String getKey(Element operation) {
		List<String> excludeGroups = new ArrayList<String>();
		LASBackendRequest doc = (LASBackendRequest) this.clone();
		try {
			Element properties = operation.getChild("properties");
	        if ( properties != null ) {
	            List groups = properties.getChildren("property_group");
	            for (Iterator propIt = groups.iterator(); propIt.hasNext();) {
	                Element group = (Element) propIt.next();
	                String type = group.getAttributeValue("type");
	                if ( type.equals("backend_request")) {
	                    List bk_req_props = group.getChildren("property");
	                    for (Iterator bk_reqIt = bk_req_props.iterator(); bk_reqIt.hasNext();) {
	                        Element bk_req_prop = (Element) bk_reqIt.next();
	                        String name = bk_req_prop.getChildTextNormalize("name");
	                        String value = bk_req_prop.getChildTextNormalize("value");
	                        if ( name.equals("exclude")) {
	                            String[] excludes = value.split(",");
	                            for (int i = 0; i < excludes.length; i++) {
	                                excludeGroups.add(excludes[i]);
	                            }
	                        }
	                    }
	                }
	            }
	        }
	        doc.removePropertyGroup("product_server");
	        if (excludeGroups.contains("variables") ) {
	            boolean removed = doc.getRootElement().removeChildren("dataObjects");
	        }
			return JDOMUtils.MD5Encode(doc.toString());
		} catch (UnsupportedEncodingException e) {
			return String.valueOf(Math.random());
		}
	}
	public void removePropertyExcludedGroups(Element operation) {
        Element properties = operation.getChild("properties");
        if ( properties != null ) {
            List groups = properties.getChildren("property_group");
            for (Iterator propIt = groups.iterator(); propIt.hasNext();) {
                Element group = (Element) propIt.next();
                String type = group.getAttributeValue("type");
                if ( type.equals("backend_request")) {
                    List bk_req_props = group.getChildren("property");
                    for (Iterator bk_reqIt = bk_req_props.iterator(); bk_reqIt.hasNext();) {
                        Element bk_req_prop = (Element) bk_reqIt.next();
                        String name = bk_req_prop.getChildTextNormalize("name");
                        String value = bk_req_prop.getChildTextNormalize("value");
                        if ( name.equals("exclude")) {
                            String[] excludes = value.split(",");
                            for (int i = 0; i < excludes.length; i++) {
                                boolean removed = this.removePropertyGroup(excludes[i].trim());
                                if ( !removed ) {
                                    log.warn("Attempt to remove property group "+excludes[i]+" from backend request failed.");
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    public boolean isTrajectory() {
        boolean trajectory = true;
        List variables = getVariables();
        for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
            String name = (String) varIt.next();
            String grid = getDataAttribute(name, "grid_type");
            if ( grid != null ) {
                trajectory = trajectory && grid.toLowerCase(Locale.ENGLISH).equals("trajectory");
            }
        }
        return trajectory;
    }
}
