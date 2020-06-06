package gov.noaa.pmel.tmap.las.product.request;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;
import gov.noaa.pmel.tmap.las.product.server.ProductServerAction;
import gov.noaa.pmel.tmap.las.util.Grid;
import gov.noaa.pmel.tmap.las.util.GridTo;
import gov.noaa.pmel.tmap.las.util.Variable;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a POJO that is the instantiation of an entire
 * product request.  It can contain multiple requests to multiple
 * services if the operation is a "chained" operation.
 * @author Roland Schweitzer
 *
 */

public class ProductRequest {
    private final static Logger log = LoggerFactory.getLogger(ProductServerAction.class.getName());
    protected LASUIRequest lasRequest = null;
    protected String cacheKey = null;
    protected boolean useCache = true;
    protected ArrayList<LASBackendRequest> requestXML = new ArrayList<LASBackendRequest>();
    protected ArrayList<String> operationIDs = new ArrayList<String>();
    protected Element operationElement = null;
    protected ArrayList<Element>  operations = new ArrayList<Element>();
    protected ArrayList<String> cacheKeys = new ArrayList<String>();
    protected HashSet<String> emails = new HashSet<String>();

    /**
     * A default no arg constructor.
     */
    public ProductRequest() {
        super();
    }

    /**
     * Make a product request object from the UI request.  You should this constructor.
     * @param lasConfig - the instantiation of the entire las.xml configuration.
     * @param lasRequest - the current request from the LAS User Interface client (or external client).
     * @param debug - the debug level.
     * @param JSESSIONID - a session id string (can be null).
     * @throws LASException
     * @throws UnsupportedEncodingException 
     * @throws JDOMException 
     */
    public ProductRequest(LASConfig lasConfig, LASUIRequest lasRequest, String debug, String JSESSIONID) throws LASException, UnsupportedEncodingException, JDOMException {
        this.lasRequest = lasRequest;

        this.cacheKey = lasRequest.getKey();
        this.operationElement = lasConfig.getElementByXPath(lasRequest.getOperationXPath());
        if ( operationElement == null ) {
            throw new LASException ("No such operation found.");
        }
        // Only make the backend requet objects if it's not a template request.
        
        	List operations_list = operationElement.getChildren("operation");
        	if (operations_list.size() == 0) {
        		if ( !operationElement.getChild("service").getTextTrim().equals("template") ) {
        			//This is a simple operation.
        			makeRequest(operationElement, lasConfig, lasRequest, debug, JSESSIONID);
        			useCache = useCache && getUseCache(operationIDs.get(0));
        		} else {
        			operations.add(operationElement);
        			operationIDs.add(operationElement.getAttributeValue("ID"));
        		}
        	} else {
        		// This is a compound operation.  
        	    int idx = 0;
        		for (Iterator opIt = operations_list.iterator(); opIt.hasNext();) {
        			Element operation = (Element) opIt.next();
        			makeRequest(operation, lasConfig, lasRequest, debug, JSESSIONID);
        			if ( idx > 0 ) {
        			    LASBackendRequest bkrequest = getRequestXML().get(idx - 1);
        			    bkrequest.setChainedOperation(operation.getAttributeValue("ID"));
        			}
        			idx++;
        		}
        		
        	}
        	for (int i=0; i < operations_list.size(); i++) {
        		// If one data set in any sub-operation turns off the cache
        		// we have to turn it off...
        		useCache = useCache && getUseCache(operationIDs.get(i));
        	}
    }

    /**
     * Takes each individual sub-operation and creates the LASBackendRequest request object that will be used
     * to request the product from the backend service.  The LASBackendRequests, operation elements and cache
     * keys and operation IDs for each new request are accumlated in class instances variables.
     * @param operation
     * @param lasConfig
     * @param lasRequest
     * @throws JDOMException 
     * @throws LASException 
     * @throws UnsupportedEncodingException 
     */
    public void makeRequest(Element operation, LASConfig lasConfig, LASUIRequest lasRequest, String debug, String JSESSIONID) throws JDOMException, LASException, UnsupportedEncodingException {

        operations.add(operation);

        // Save the op id in the id list so we can refer
        // other lists via this id.
        operationIDs.add(operation.getAttributeValue("ID"));

        LASBackendRequest backendRequestDocument = new LASBackendRequest();            
        Element backendRequestE = new Element("backend_request");
        backendRequestDocument.setRootElement(backendRequestE);


        // Empty container for all the merged propreties
        Element mergedProperties = new Element("properties");

        // Add the backend server properties.
        if ( debug != null && 
                ( debug.equals("trace") || 
                        debug.equals("debug") || 
                        debug.equals("info") || 
                        debug.equals("info") || 
                        debug.equals("warn") ||
                        debug.equals("error") ||
                        debug.equals("fatal") ) ) {
            addProperty(mergedProperties,"las","debug", debug);
        } else {
            addProperty(mergedProperties,"las","debug","info");
        }

        addProperty(mergedProperties, "operation", "service", operation.getChildText("service"));
        addProperty(mergedProperties, "operation", "name", operation.getAttributeValue("name")); 
        addProperty(mergedProperties, "operation", "ID", operation.getAttributeValue("ID"));
        boolean regrid = false;
        String regrid_prop = operation.getAttributeValue("regrid_prn");
        
        if ( regrid_prop != null && regrid_prop.equalsIgnoreCase("true") ) {
            regrid = true;
        }
        boolean do_analysis = false;
        String service_action = operation.getAttributeValue("service_action");
        if ( service_action != null && !service_action.equals("") ) {
            addProperty(mergedProperties, "operation", "service_action", service_action);
        }
        String service = operation.getChildText("service");
        if ( service == null || service.equals("") ) {
        	throw new LASException("No service defined for operation "+operation.getAttributeValue("name"));
        }
        if ( service_action != null && !service_action.equals("") ) {
        	addProperty(mergedProperties, service, "service_action", operation.getAttributeValue("service_action"));
        }

        // Get the "global" properties from the lasRequest (not all requests have properties)
        Element props = lasRequest.getProperties();
        Element requestProperties = new Element("properties");
        if ( props != null ) {
            requestProperties = (Element)props.clone();
        }
        String view = lasRequest.getProperty("ferret", "view");
        // Add the response information to the request xml.
        
        Element response = (Element)operation.getChild("response");
        Element backendResponse = new Element("backend_response");

        Element argsE = lasRequest.getRootElement().getChild("args");
        Element dataObjectsE = new Element("dataObjects");
        // TODO Most of this logic belongs in LASRequest.  :-)
        ArrayList<String> datasetList = new ArrayList<String>();
        if ( argsE != null ) {
            List args = argsE.getChildren();		    
            if ( args != null ) {
                int region_index = 0;		
                // TODO eventually this might be extracted from a property some how.
                GridTo gridTo = new GridTo();
                int var_count=0;
                for (Iterator argsIt = args.iterator(); argsIt.hasNext();) {
                    Element arg = (Element) argsIt.next();
                    if (arg.getName().equals("link")) {
                        // This is a variable.
                        var_count++;

                        String varXPath = arg.getAttributeValue("match");
                        String chained = operation.getAttributeValue("chained");
                        Element analysis = arg.getChild("analysis");
                        do_analysis = analysis != null;

                        // The source for this variable is a previous operation.
                        if ( chained != null && chained.equals("true") ) {
                            // Find the chained arguments and substitute the data URL
                            // for the chained input.
                            Element chained_argsE = operation.getChild("args");
                            List opArgs = null;
                            if ( chained_argsE != null ) {
                                opArgs = chained_argsE.getChildren("arg");		                    }
                            // Not every chained operation needs "args".
                            if ( opArgs != null ) {
                                for (Iterator opArgsIt = opArgs.iterator(); opArgsIt.hasNext();) {
                                    Element data = new Element("data");
                                    // Set up F-TDS URLs below instead of passing on the analysis element.
                                    // data.addContent((Element)analysis.clone());
                                    // Don't know if it's possible to have an analysis request
                                    // apply to a chained result.  !?
                                    Element opArg = (Element) opArgsIt.next();
                                    String chainedInput = opArg.getAttributeValue("chained");
                                    if ( chainedInput != null && chainedInput.equals("true")) {
                                        String chainedOpName = opArg.getAttributeValue("operation");
                                        String chainedResultID = opArg.getAttributeValue("result");
                                        int opIndex = findOperationIndex(chainedOpName);
                                        Element sourceOp = operations.get(opIndex);
                                        List results = sourceOp.getChild("response").getChildren("result");
                                        for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                                            Element result = (Element) resIt.next();
                                            if ( result.getAttributeValue("ID").equals(chainedResultID)) {
                                                // TODO this should probably be the T-FDS URL of this output file when
                                                // the type="netcdf"...  :-)
                                                String url = requestXML.get(opIndex).getResult(chainedResultID);
                                                String file = requestXML.get(opIndex).getResultAsFile(chainedResultID);
                                                data.setAttribute("file", file);
                                                if ( url != null ) {
                                                    data.setAttribute("url", url);
                                                } else {
                                                    data.setAttribute("url", file);
                                                }	                                        
                                                data.setAttribute("chained", "true");
                                                data.setAttribute("result", chainedResultID);

                                            }
                                        }
                                        String opArg_type = opArg.getAttributeValue("type");
                                        // This result is associated with a variable so add the variable attributes.
                                        if ( opArg_type.equals("variable") ) {
                                            data.setAttribute("var",lasConfig.getVariableName(varXPath));
                                            data.setAttribute("title", lasConfig.getVariableTitle(varXPath));
                                            data.setAttribute("xpath", varXPath);
                                            
                                            HashMap<String, String> ptinv = lasConfig.getPointsAndIntervals(varXPath);
                                            for ( Iterator ptinvIt = ptinv.keySet().iterator(); ptinvIt.hasNext(); ) {
                                                String name = (String) ptinvIt.next();
                                                String value = ptinv.get(name);
                                                data.setAttribute(name, value);
                                            }

                                            // Add all the attributes from the parent data set element to this data object.
                                            HashMap <String, String> dataset_attrs = lasConfig.getDatasetAttributes(varXPath);

                                            for (Iterator dsAttrIt = dataset_attrs.keySet().iterator(); dsAttrIt.hasNext();) {
                                                String name = (String) dsAttrIt.next();
                                                String value = dataset_attrs.get(name);
                                                data.setAttribute(new Attribute("dataset_" + name, value));
                                            }
                                            
                                            String datasetURL = data.getAttributeValue("dataset_url");
                                            if ( datasetURL == null || datasetURL.equals("") ) {
                                            	data.setAttribute(new Attribute("dataset_url", lasConfig.getDataAccessURL(varXPath, false)));
                                            }

                                            List attribs = lasConfig.getDataOjectAttributes(varXPath);

                                            if ( attribs.size() > 0 ) {
                                                for (Iterator atIt = attribs.iterator(); atIt.hasNext();) {                        
                                                    Attribute attrib = (Attribute) atIt.next();
                                                    // The url attribute is special; don't mess with.
                                                    if ( !attrib.getName().equals("url") ) {
                                                        // This gets things like units and any other attributes
                                                        // with unknown sematics that get added in the future.
                                                        data.setAttribute((Attribute)attrib.clone());
                                                    }
                                                }                    
                                            }
                                            Element region = new Element("region");
                                            if (lasRequest.isOneToOne()) {
                                                region.setAttribute("IDREF", "region_"+String.valueOf(region_index));
                                            } else {
                                                region.setAttribute("IDREF", "region_0");
                                            }
                                            data.addContent(region);
                                            Element properties = (Element)lasConfig.getVariableProperties(varXPath).clone();
                                            HashMap merge = mergeProperties(mergedProperties, requestProperties, properties);
                                            mergedProperties.setContent(((Element)merge.get("merged")).cloneContent());
                                            data.addContent((Element)merge.get("dataset"));
                                        }
                                        dataObjectsE.addContent(data); 
                                    }                          							
                                }
                            }
                        } else {
                            Element data = new Element("data");                           
                            // We are here because the data is not from a chained operation.
                            // However, it may be a user defined variable, part of an operation that requires
                            // regriding or both or neither.  :-)
                            
                            HashMap<String, String> ptinv = lasConfig.getPointsAndIntervals(varXPath);
                            for ( Iterator ptinvIt = ptinv.keySet().iterator(); ptinvIt.hasNext(); ) {
                                String name = (String) ptinvIt.next();
                                String value = ptinv.get(name);
                                data.setAttribute(name, value);
                            }
                            
                            String current_url = lasConfig.getDataObjectURL(varXPath);
                            String current_var = lasConfig.getVariableName(varXPath);
                            String current_title = lasConfig.getVariableTitle(varXPath);
                            String current_ftds_url = lasConfig.getFTDSURL(varXPath);
                            String current_dsID = lasConfig.getVariableByXPath(varXPath).getDSID();
                            String current_gridID = lasConfig.getGrid(varXPath).getID();
                            
                            // If the data variable is curvilinear and the analysis is in X or Y, we want to fail now.
                            if ( do_analysis ) {
                            	boolean fail = false;
                            	List axes = analysis.getChildren("axis");
                                for (Iterator axisIt = axes.iterator(); axisIt.hasNext();) {
                                    Element axis = (Element) axisIt.next();
                                    String type = axis.getAttributeValue("type");
                                    fail = fail || type.equals("x") || type.equals("y");
                                }
                                String curvi = lasConfig.getVariablePropertyValue(varXPath, "ferret", "curvi_coord_lon");
                                fail = fail && (!curvi.equals(""));
                                if (fail) {
                                	throw new LASException(" At this time we are unable to process analysis requests in X or Y on curvilinear data sets.");
                                }
                            }
                            
                            if ( !datasetList.contains(current_ftds_url) ) {
                            	datasetList.add(current_ftds_url);
                            } 
                            int dataset_number = datasetList.indexOf(current_ftds_url)+1;

                            // Neither is the easiest
                            if ( !regrid && !do_analysis ) {                               
                                data.setAttribute("url",current_url);
                                data.setAttribute("var",current_var);
                                data.setAttribute("title", current_title);
                                data.setAttribute("xpath", varXPath);
                            } else if ( !regrid && do_analysis ) {
                            	// Send in all the gridTo junk, but it won't get used.  
                            	// This will all be redone in the next implementation when this moves to it's own service/class.
                                setAnalysisURL(analysis, data, lasConfig, lasRequest, varXPath, var_count, dataset_number, regrid);                               
                            } else if ( regrid && !do_analysis ) {
                            	
                                if ( dataObjectsE.getChildren("data").size() <= 0 ) {
                                	
                                	// TODO set up the gridTo.AxisNeeded !!!!
                                	
                                    // We're going to regrid.  Access first data variable via FDS
                                    gridTo.setURL(lasConfig.getFTDSURL(varXPath));
                                    gridTo.setGridID(lasConfig.getGrid(varXPath).getID());
                                    data.setAttribute("url", gridTo.getURL());
                                    gridTo.setVar(lasConfig.getVariableName(varXPath));
                                    gridTo.setVarXPath(varXPath);
                                    gridTo.setDsID(lasConfig.getDatasetAttributes(varXPath).get("ID"));
                                    data.setAttribute("var",gridTo.getVar());
                                    data.setAttribute("title", lasConfig.getVariableTitle(varXPath));
                                    gridTo.setData(data);
                                } else {
                                    // Use regrid all others to same grid as first, but
                                    // only if the URL is different.

                                    if ( !gridTo.getGridID().equals(current_gridID) ||
                                       	 !gridTo.getDsID().equals(current_dsID)) {

                                        String var = lasConfig.getVariableName(varXPath);
                                        String expression = "";
                                        try {
                                        	// The inner URL of the expression must be encoded separately.
                                        	String encoded = URLEncoder.encode(lasConfig.getFTDSURL(varXPath), "UTF-8");
                                        	String g = "g"+view;
                                        	if (gridTo.isAnalysis()) {
                                        		StringBuffer jnl = new StringBuffer("use _qt_"+encoded+"_qt__cr_");
                                        		jnl.append(gridTo.getJnl().toString());
                                        		jnl.append("_cr_letdeq1 "+var+"_"+var_count+"_transformed="+var+"[d="+dataset_number+","+g+"="+gridTo.getVar()+"[d=1]]");
                                        		// Get the original URL for the gridTo data set and append the new combined analysis and regrid URL.
                                        		String expr = URLEncoder.encode("_expr_{}{"+jnl.toString(), "UTF-8");
                                        		String comboURL = lasConfig.getFTDSURL(gridTo.getVarXPath())+expr;                                       			
                                        		data.setAttribute("url", comboURL);
                                        		// Retroactively set the gridTo data URL to be the same.  This means that both URL will use the same cache area in F-TDS.
                                        		gridTo.getData().setAttribute("url", comboURL);
                                        		gridTo.setURL(gridTo.getData().getAttributeValue("url"));
                                        	} else {    
                                        	    StringBuffer jnl = new StringBuffer("use _qt_"+encoded+"_qt__cr_");
                                                jnl.append(gridTo.getJnl().toString());
                                        		gridTo.setVar(gridTo.getVar());
                                                jnl.append("letdeq1 "+var+"_"+var_count+"_transformed="+var+"[d="+dataset_number+","+g+"="+gridTo.getVar()+"[d=1]]");
                                                expression = URLEncoder.encode("_expr_{}{"+jnl.toString()+"}", "UTF-8");
                                                data.setAttribute("url", gridTo.getURL()+expression);
                                        	}
                                        } catch (UnsupportedEncodingException e) {
                                            expression = ""; 
                                        }
                                        
                                        data.setAttribute("var", var+"_"+var_count+"_transformed");
                                        data.setAttribute("title", lasConfig.getVariableTitle(varXPath)+" on the grid of "+gridTo.getVar()+"[d=1]");
                                    } else {
                                        // It's the same data set so use it as normal except switch it to the F-TDS URL.
                                        data.setAttribute("url",lasConfig.getFTDSURL(varXPath));
                                        data.setAttribute("var",lasConfig.getVariableName(varXPath));
                                        data.setAttribute("title", lasConfig.getVariableTitle(varXPath));
                                        data.setAttribute("xpath", varXPath);
                                    }
                                }

                            } else if ( regrid && do_analysis ) {
                            	// Create the analyzed variable then set the gridTo information to use it.
                            	StringBuffer jnl = setAnalysisURL(analysis, data, lasConfig, lasRequest, varXPath, var_count, dataset_number, regrid);
                                if ( dataObjectsE.getChildren("data").size() <= 0 ) {
                                	gridTo.setJnl(jnl);
                                    gridTo.setURL(data.getAttributeValue("url"));
                                    // This is the variable name that gets constructed in setAnalysis
                                    String ovar = lasConfig.getVariableName(varXPath)+var_count+"_transformed";
                                    gridTo.setVar(ovar);                                   
                                    gridTo.setGridID(lasConfig.getGrid(varXPath).getID());
                                    gridTo.setVarXPath(varXPath);
                                    gridTo.setDsID(lasConfig.getDatasetAttributes(varXPath).get("ID"));
                                    gridTo.setData(data);
                                    gridTo.setAnalysis(true);
                                } else {
                                	
                                	String var = lasConfig.getVariableName(varXPath);
                                    String expression = "";
                                    String encoded = URLEncoder.encode(lasConfig.getFTDSURL(varXPath), "UTF-8");
                                    if ( !gridTo.getGridID().equals(current_gridID) ||
                                    	 !gridTo.getDsID().equals(current_dsID)) {
                                    	String g = "g"+view;
                                    	if (gridTo.isAnalysis()) {   
                                    	    StringBuffer analysis_jnl = new StringBuffer("use _qt_"+encoded+"_qt__cr_");
                                    	    jnl.append("_cr_");
                                    		jnl.append(gridTo.getJnl().toString());
                                    		analysis_jnl.append("_cr_"+jnl);
                                    		var = data.getAttributeValue("var");
                                    		String revar = var+"_"+var_count+"_transformed";
                                    		data.setAttribute("var", revar);
                                    		analysis_jnl.append("_cr_letdeq1 "+revar+"="+var+"[d=1,"+g+"="+gridTo.getVar()+"[d=1]]}");
                                    		// Get the original URL for the gridTo data set and append the new combined analysis and regrid URL.
                                    		String expr = URLEncoder.encode("_expr_{}{"+analysis_jnl.toString(), "UTF-8");
                                    		String comboURL = lasConfig.getFTDSURL(gridTo.getVarXPath())+expr;                                       			
                                    		data.setAttribute("url", comboURL);
                                    		// Retroactively set the gridTo data URL to be the same.  This means that both URL will use the same cache area in F-TDS.
                                    		gridTo.getData().setAttribute("url", comboURL);
                                    		gridTo.setURL(gridTo.getData().getAttributeValue("url"));
                                    	} else {    
                                    		var = data.getAttributeValue("var");
                                    		jnl.append("use _qt_"+encoded+"_qt__cr_");
                                    		jnl.append("_cr_letdeq1 "+var+"_"+var_count+"_transformed="+var+"[d=1,"+g+"="+gridTo.getVar()+"[d=1]]");
                                    		expression = URLEncoder.encode("_expr_{}"+"{"+jnl.toString()+"}", "UTF-8");
                                    		data.setAttribute("url", gridTo.getURL()+expression);
                                    		data.setAttribute("var", var+"_"+var_count+"_transformed");
                                    		//data.setAttribute("title", "Transformed Variable");
                                    		data.setAttribute("title", lasConfig.getVariableTitle(varXPath)+" on the grid of "+gridTo.getVar()+"[d=1]");
                                    	}
                                    } 
                                }
                            }
                            String prop_var = lasRequest.getProperty("ferret", "prop_var");
                            if ( prop_var != null && !prop_var.equals("") ) {
                                Variable var = lasConfig.getVariableByXPath(varXPath);
                                List<Variable> vars = lasConfig.getVariables(var.getDSID());
                                for (Iterator varsIt = vars.iterator(); varsIt.hasNext();) {
                                   Variable variable = (Variable) varsIt.next();
                                   if ( variable.getID() != null && variable.getID().equals(prop_var)) {
                                       addProperty(mergedProperties, "ferret", "prop_var_title", variable.getName());
                                       addProperty(mergedProperties, "ferret", "prop_var_units", variable.getUnits());
                                   }
                                }
                             }

                            // Add all the attributes from the parent data set element to this data object.
                            HashMap <String, String> dataset_attrs = lasConfig.getDatasetAttributes(varXPath);

                            for (Iterator dsAttrIt = dataset_attrs.keySet().iterator(); dsAttrIt.hasNext();) {
                                String name = (String) dsAttrIt.next();
                                String value = dataset_attrs.get(name);
                                data.setAttribute(new Attribute("dataset_"+name, value));
                            }
                            
                            String datasetURL = data.getAttributeValue("dataset_url");
                            if ( datasetURL == null || datasetURL.equals("") ) {
                            	data.setAttribute(new Attribute("dataset_url", lasConfig.getDataAccessURL(varXPath, false)));
                            }
                            
                            List attribs = lasConfig.getDataOjectAttributes(varXPath);

                            if ( attribs.size() > 0 ) {
                                for (Iterator atIt = attribs.iterator(); atIt.hasNext();) {                        
                                    Attribute attrib = (Attribute) atIt.next();
                                    // The url attribute is special; don't mess with.
                                    if ( !attrib.getName().equals("url")) {
                                        // This gets things like units and any other attributes
                                        // with unknown semantics that get added in the future.
                                        data.setAttribute((Attribute)attrib.clone());
                                    }
                                }                    
                            }

                            Element region = new Element("region");
                            if (lasRequest.isOneToOne()) {
                                region.setAttribute("IDREF", "region_"+String.valueOf(region_index));
                            } else {
                                region.setAttribute("IDREF", "region_0");
                            }
                            data.addContent(region);
                            Element properties = (Element)lasConfig.getVariableProperties(varXPath).clone();
                            HashMap merge = mergeProperties(mergedProperties, requestProperties, properties);
                            mergedProperties.setContent(((Element)merge.get("merged")).cloneContent());
                            data.addContent((Element)merge.get("dataset"));
                            dataObjectsE.addContent(data);
                        }

                    } else if (arg.getName().equals("region")) {

                        Element region = new Element("region");
                        region.setAttribute("ID", "region_"+String.valueOf(region_index));
                        List ranges = arg.getChildren("range");
                        for (Iterator rit = ranges.iterator(); rit.hasNext();) {
                            Element range = (Element) rit.next();
                            String type = range.getAttribute("type").getValue();
                            String low = range.getAttribute("low").getValue();
                            String high = range.getAttribute("high").getValue();
                            
                            /*
                             * Allow ranges of the form:
                             * 
                             * Last three days...
                             * "today" is midnight.
                             * <range type="t" high="today" low="259200000"/>
                             * Last half day...
                             * "now" is right now including hours, minutes and seconds
                             * <range type="t" high="now" low="43200000"/>
                             * 
                             * And into the future, for completeness.
                             * 
                             * Today plus 3 days
                             * <range type="t" high="259200000" low="today"/>
                             * Now, plus half a day
                             * <range type="t" high="43200000" low="now"/>
                             */
                            if ( high.equalsIgnoreCase("today") ) {
                            	DateTime today = new DateTime();
                            	DateTimeFormatter short_fmt = DateTimeFormat.forPattern("dd-MMM-yyyy").withZone(DateTimeZone.UTC);
                            	high = short_fmt.print(today);
                            	DateTime before = new DateTime(today.minusMillis(Integer.valueOf(low).intValue()));
                            	low = short_fmt.print(before);
                            } else if ( high.equalsIgnoreCase("now") ) {
                            	DateTime now = new DateTime();
                                DateTimeFormatter long_fmt = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss").withZone(DateTimeZone.UTC);
                                high = long_fmt.print(now);
                                DateTime before = new DateTime(now.minusMillis(Integer.valueOf(low).intValue()));
                                low = long_fmt.print(before);
                            }
                            if ( low.equalsIgnoreCase("today") ) {
                            	DateTime today = new DateTime();
                            	DateTimeFormatter short_fmt = DateTimeFormat.forPattern("dd-MMM-yyyy").withZone(DateTimeZone.UTC);
                            	low = short_fmt.print(today);
                            	DateTime after = new DateTime(today.plusMillis(Integer.valueOf(high).intValue()));
                            	high = short_fmt.print(after);
                            } else if ( low.equalsIgnoreCase("now") ) {
                            	DateTime today = new DateTime();
                            	DateTimeFormatter long_fmt = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss").withZone(DateTimeZone.UTC);
                            	low = long_fmt.print(today);
                            	DateTime after = new DateTime(today.plusMillis(Integer.valueOf(high).intValue()));
                            	high = long_fmt.print(after);
                            }
                            
                            Element lo = new Element(type+"_lo");
                            lo.setText(low);
                            region.addContent(lo);
                            Element hi = new Element(type+"_hi");
                            hi.setText(high);
                            region.addContent(hi);
                        }

                        List points = arg.getChildren("point");
                        for (Iterator pit = points.iterator(); pit.hasNext();) {
                            Element point = (Element) pit.next();
                            String type = point.getAttribute("type").getValue();
                            String value = point.getAttribute("v").getValue();
                            Element lo = new Element(type+"_lo");
                            lo.setText(value);
                            region.addContent(lo);
                            Element hi = new Element(type+"_hi");
                            hi.setText(value);
                            region.addContent(hi);
                        }

                        backendRequestE.addContent(region);
                        region_index++;

                    } else if ( arg.getName().equals("constraint")) {
                        Element lhs = new Element("lhs");
                        Element op = new Element("op");
                        Element rhs = new Element("rhs");
                        if ( arg.getAttributeValue("type").equals("text")) {

                            List v = arg.getChildren("v");
                            Element c = new Element("constraint");

                            c.setAttribute("type", "text"); 

                            Element vE = (Element)v.get(0);                   
                            lhs.setText(vE.getText());
                            c.addContent(lhs);

                            vE = (Element)v.get(1);
                            String opString = vE.getText();		                    
                            if ( opString.equals("<")) {
                                opString = "lt";
                            } else if ( opString.equals("<=")) {
                                opString = "le";
                            } else if (opString.equals("=")) {
                                opString = "eq";
                            } else if (opString.equals("!=") ) {
                                opString = "ne";
                            } else if (opString.equals(">")) {
                                opString = "gt";
                            } else if (opString.equals(">=")) {
                                opString = "ge";
                            }
                            op.setText(opString);
                            c.addContent(op);

                            vE = (Element)v.get(2);
                            rhs.setText(vE.getText());
                            c.addContent(rhs);
                            
                            Element label = arg.getChild("label");
                            if ( label != null ) {
                            	Element labelE = new Element("label");
                            	labelE.setText(label.getTextTrim());
                            	c.addContent(labelE);
                            }

                            backendRequestE.addContent(c);

                        } else if (arg.getAttributeValue("type").equals("variable")) {

                            Element c = new Element("constraint");
                            c.setAttribute("type", "variable");

                            // Things like "<=" get encoded as entities
                            // when they get stuffed into and XML file.

                            // We'll unwind the meaning of the op
                            // in the backend.

                            String opString = arg.getAttributeValue("op");
                            if ( opString.equals("<")) {
                                opString = "lt";
                            } else if ( opString.equals("<=")) {
                                opString = "le";
                            } else if (opString.equals("=")) {
                                opString = "eq";
                            } else if (opString.equals("!=") ) {
                                opString = "ne";
                            } else if (opString.equals(">")) {
                                opString = "gt";
                            } else if (opString.equals(">=")) {
                                opString = "ge";
                            }
                            op.setText(opString);
                            c.addContent(op);

                            Element v = arg.getChild("v");
                            rhs.setText(v.getText());
                            c.addContent(rhs);

                            lhs.setText(lasConfig.getVariableName(arg.getChild("link").getAttributeValue("match")));
                            c.addContent(lhs);

                            backendRequestE.addContent(c);

                        }
                    }         
                }
            } // if argsE != null
        } // if args != null

        backendRequestE.addContent(mergedProperties);    

        backendRequestE.addContent(dataObjectsE);

        // Check to see if there are any property groups that were listed in an
        // "backend_request", "exclude" property for this operation and therefore should be 
        // removed from this request.  
        backendRequestDocument.removePropertyExcludedGroups(operation);
        
        // If this is an analysis or comparison with re-grid operation, we need to remove the init_script property.
        if ( do_analysis || regrid ) {
            backendRequestDocument.removeProperty("ferret", "init_script");
        }

        // Compute the cache key as the document exists now without the key in the document.
        String key = backendRequestDocument.getKey(operation);
        
        cacheKeys.add(key);

        // Add the SESSIONID after making the cache key to it doesn't pollute the key.
        if ( JSESSIONID != null && !JSESSIONID.equals("") )  {
            backendRequestDocument.getRootElement().setAttribute("JSESSIONID", JSESSIONID);
        }

        // Use the key for the sub-request for the file name so caching on the sub-request
        // works right.  This means the cache key is independent of the results and their file
        // names, but this won't matter since the results are fixed by the operation definition
        // not the request.
        // You don't have to have a response element in an operation.  Do you?
        if (response != null) {
            backendResponse = (Element) response.clone();
            List resultList = backendResponse.getChildren("result");
            int index = 0;
            // TODO Figure out how to get the output directory into this object
            String outputDir = lasConfig.getOutputDir();
            for (Iterator resultIt = resultList.iterator(); resultIt.hasNext();) {
                Element result = (Element) resultIt.next();
                String type = result.getAttributeValue("type");
                String file_suffix = result.getAttributeValue("file_suffix");
                // Construct the file names for this result.
                String outputFileName = "";

                if ( file_suffix != null ) {
                    outputFileName = outputDir + File.separator + key + "_" + result.getAttributeValue("ID") + file_suffix;
                    result.setAttribute("file", outputFileName);
                } else {
                    outputFileName = outputDir + File.separator + key+ "_" + result.getAttributeValue("ID") + "." + type;
                    result.setAttribute("file", outputFileName);
                }
                result.setAttribute("index", String.valueOf(index));
                result.setAttribute("key", key);
                index++;
            }
            String ap = lasRequest.getProperty("ferret", "annotations");
            if ( ap != null && !ap.equals("") ) {
            	// Add the annotations file if the property is set to file.
            	if ( lasRequest.getProperty("ferret", "annotations").equalsIgnoreCase("file") || 
            			backendRequestDocument.getProperty("ferret", "annotations").equalsIgnoreCase("file") ) {
            		Element annotations = new Element("result");
            		annotations.setAttribute("type", "annotations");
            		annotations.setAttribute("ID", "annotations");
            		String annotationsFile = outputDir + File.separator + key+ "_annotations.xml";
            		annotations.setAttribute("file", annotationsFile);
            		annotations.setAttribute("key", key);
            		backendResponse.addContent(annotations);
            	}
            }
            // Add the cancel file

            Element cancel = new Element("result");
            cancel.setAttribute("type", "cancel");
            cancel.setAttribute("ID", "cancel");
            String cancelFile = outputDir + File.separator + key+ "_cancel.txt";
            cancel.setAttribute("file", cancelFile);
            cancel.setAttribute("key", key);
            backendResponse.addContent(cancel);

            // Automatically add an RSS Feed result.  This is keyed to the entire request, not the individual requests.
            Element feed = new Element("result");
            feed.setAttribute("type", "rss");
            feed.setAttribute("ID", "rss");
            String feedFile = outputDir + File.separator + cacheKey+ "_rss.rss";
            feed.setAttribute("file", feedFile);
            feed.setAttribute("key", cacheKey);
            backendResponse.addContent(feed);
        }

        backendRequestE.addContent(backendResponse);
        // Add the key to the operations element.
        addProperty(mergedProperties, "operation", "key", key);

        // Add the new document with the key to the list.
        requestXML.add(backendRequestDocument);

    }

	private void setAxes(Grid grid, Element analysis, GridTo gridTo) {
		// TODO Auto-generated method stub
		
	}

	/**
     * Builds the _expr_ analysis URL.
     * @param analysis
     * @param data
     * @param lasConfig
     * @param varXPath
     * @param var_count
     * @throws JDOMException
     * @throws LASException
     * @throws UnsupportedEncodingException
     */
    private StringBuffer setAnalysisURL(Element analysis, Element data, LASConfig lasConfig, LASUIRequest lasRequest, String varXPath, int var_count, int dataset_number, boolean regrid) throws JDOMException, LASException, UnsupportedEncodingException {
        int dset = dataset_number;
    	if ( !regrid ) {
    		dset = 1;
    	}
    	String var;
    	StringBuffer jnl = new StringBuffer();
    	
    	var = lasConfig.getVariableName(varXPath);
    	
        String key = JDOMUtils.MD5Encode(varXPath);
        
        
        List axes = analysis.getChildren("axis");
        String grid = "";
        
        /* To calculate an effective mask, we need to know the size
         * of the area being masked.  The size of the area under consideration
         * is either in the analysis axis or the range of the product request.
         */
        double lon_range;
        double xhi = -9999.;
        double xlo = -9999.;
        double lat_range;
        double yhi = -9999;
        double ylo = -9999;
        String elo = null;
        String ehi = null;
        String eop = null;
        for (Iterator axisIt = axes.iterator(); axisIt.hasNext();) {
            Element axis = (Element) axisIt.next();
            String type = axis.getAttributeValue("type");
            String op   = axis.getAttributeValue("op");
            String lo = axis.getAttributeValue("lo");
            String hi = axis.getAttributeValue("hi");
            if ( type.equals("t") ) {
            	grid = grid+","+type+"=_q-t_"+lo+"_q-t_:_q-t_"+hi+"_q-t_@"+op;
            } else {
                if ( !type.equals("e") ) { // e is done separately below
                    grid = grid+","+type+"="+lo+":"+hi+"@"+op;
                }
            }
            
            if ( type.equals("x") ) {
            	xhi = Double.valueOf(hi).doubleValue();
            	xlo = Double.valueOf(lo).doubleValue();
            }
            if ( type.equals("y") ) {
            	yhi = Double.valueOf(hi).doubleValue();
            	ylo = Double.valueOf(lo).doubleValue();
            }
            if ( type.equals("e") ) {
                elo = axis.getAttributeValue("lo");
                ehi = axis.getAttributeValue("hi");
                eop = axis.getAttributeValue("op");
            }
        }

        String ocean_mask = analysis.getAttributeValue("oceanmask");
        String land_mask = analysis.getAttributeValue("landmask");

        if ( ocean_mask != null || land_mask != null ) {
            // It seems crazy to have to spin through these again just to
            // figure out the resolution of the mask, but I can't think of
            // a better solution.
            HashMap<String, String> rangeValues = new HashMap<String, String>();
            Element argsE = lasRequest.getRootElement().getChild("args");
            if ( argsE != null ) {
                List args = argsE.getChildren();   
                for (Iterator argsIt = args.iterator(); argsIt.hasNext();) {
                    Element arg = (Element) argsIt.next();
                    List ranges = arg.getChildren("range");

                    for (Iterator rit = ranges.iterator(); rit.hasNext();) {
                        Element range = (Element) rit.next();
                        String type = range.getAttribute("type").getValue();
                        String low = range.getAttribute("low").getValue();
                        String high = range.getAttribute("high").getValue();
                        rangeValues.put(type+"_lo", low);
                        rangeValues.put(type+"_hi", high); 
                    }    
                }
            }
            
            if ( rangeValues.containsKey("x_lo") && rangeValues.containsKey("x_hi") ) {
                xhi = Double.valueOf(rangeValues.get("x_hi")).doubleValue(); 
                xlo = Double.valueOf(rangeValues.get("x_lo")).doubleValue();
                
            }
            
            if ( rangeValues.containsKey("y_lo") && rangeValues.containsKey("y_hi") ) {
                yhi = Double.valueOf(rangeValues.get("y_hi")).doubleValue();
                ylo = Double.valueOf(rangeValues.get("y_lo")).doubleValue();
               
            } 
            
            if ( xlo  < -9990. || xhi < -9990 || yhi < -9990. || ylo < -9990. ) {
                throw new LASException("Unable to create the mask variable for this user defined variable.");
            } else {
               lon_range = xhi - xlo;
               lat_range = ylo - yhi;
            }
            
            double area = (lon_range*lat_range)/(360.*180);
            String resolution;
            
            if ( area < 0.4 ) {
                resolution = "05";
            } else if ( area < 0.1 ) {
                resolution = "20";
            } else if ( area < 0.2 ) {
                resolution = "40";
            } else {
                resolution = "60";
            }
            jnl.append("set data etopo"+resolution+"_cr_");
            // Maybe we can skip this since we know how many datasets are open?
            jnl.append("let land_dsetnum = `rose,return=dsetnum`_cr_");
            jnl.append("let rose_on_grid = rose[d=`land_dsetnum`,gxy="+var+"[d="+var_count+"]]_cr_");
            if (land_mask != null) {
                jnl.append("let analysis_mask = if rose_on_grid lt 0 then 1_cr_");
            } else if ( ocean_mask != null) {
                jnl.append("let analysis_mask = if rose_on_grid gt 0 then 1_cr_");           
            }
            jnl.append("letdeq1 masked_"+var+"="+var+"[d="+var_count+"]*analysis_mask_cr_");
            jnl.append("letdeq1 "+var+"_"+var_count+"_transformed=masked_"+var+"[d="+dset+grid+"]_cr_");
        } else {
            if ( eop !=  null && grid.length() > 0) {
                
                jnl.append("letdeq1 "+var+"_"+var_count+"_etrans="+var+"[d="+dset+","+"e="+elo+":"+ehi+"@"+eop+"]_cr_ATTRCMD "+var+" "+var+"_"+var_count+"_etrans");
                jnl.append("_cr_letdeq1 "+var+"_"+var_count+"_transformed="+var+"_"+var_count+"_etrans"+"[d="+dset+grid+"]_cr_ATTRCMD "+var+"_"+var_count+"_etrans"+" "+var+"_"+var_count+"_transformed");

            } else {
                if ( eop != null ) {
                    grid = ",e="+elo+":"+ehi+"@"+eop;
                }
                jnl.append("letdeq1 "+var+"_"+var_count+"_transformed="+var+"[d="+dset+grid+"]_cr_ATTRCMD "+var+" "+var+"_"+var_count+"_transformed");
            }
        }

        String fdsURL = lasConfig.getFTDSURL(varXPath);
        String expression = "";
        try {
            expression = URLEncoder.encode("_expr_{}{"+jnl.toString()+"}", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            expression = "";
        }
        fdsURL = fdsURL+expression;
        data.setAttribute("url", fdsURL);
        data.setAttribute("var",var+"_"+var_count+"_transformed");
        String title = analysis.getAttributeValue("label")+" ["+grid.replaceAll("_q-t_", "\"")+"]";
        // Clean out junk that might make Ferret mad...
        title = title.replaceAll(",", " ");
        title = title.replaceAll("\""," ");
        title = title.replaceAll(";", " ");
        
        data.setAttribute("title",title);
        data.setAttribute("xpath", varXPath);
        //data.setAttribute("title", "Transformed Variable");
        // For cases that combine analysis and regridding we have to
        // accumulate the contents of the script.
        return jnl;

    }


    


    /**
     * Merges the properties associated with the current variable (which have already been merged with the properties
     * from the parent dataset and with global configuration properties at start up) with properties that arrived
     * from the user interface.
     * @param mergedProperties - initially contains properites about the operation should also be part of the request.
     * @param requestProperties - properites from the ui request.
     * @param dsProperties - properties for this dataset.
     * @return merged - the new collection of properties that will go into the backend request.
     * 
     */
    private HashMap mergeProperties(Element mergedProperties, Element requestProperties, Element dsProperties) {
        /* The requestProperties are "old" style properties
         * <properites>
         *    <ferret>
         *      <doo>dad</doo>
         *    </ferret>
         * </properties>
         * the others are "new" style properties
         * <properties>
         *    <property_group type="ferret">
         *       <property>
         *          <name>doo</name>
         *          <value>dad</value>
         *       </property>
         *    </property_group>
         * </properties>
         */

        // We're going to use HashMap for this job becuase they do the work of replacing the values
        // of duplicate keys.



        // Extract all three sets into HashMaps
        HashMap<String, HashMap<String, String>> merged = propertiesToHashMap(mergedProperties);
        HashMap<String, HashMap<String, String>> request = propertiesToHashMap(requestProperties);
        HashMap<String, HashMap<String, String>> dataset = propertiesToHashMap(dsProperties);
        
        /* If a property is in the request, then data set properties for that group must be merged
         * then any properties in the request can override them below.  So we need a key set for the
         * request properties to know if we should start a merge group.
         */

        Set<String> request_groups = request.keySet();
        
        // Only move data set properties into global properties for groups
        // that already exist as global properties.
        ArrayList<String> toRemove = new ArrayList<String>();
        for (Iterator groupIt = dataset.keySet().iterator(); groupIt.hasNext();) {
            String groupName = (String) groupIt.next();
            HashMap<String,String> group = dataset.get(groupName);
            HashMap<String, String> mergedGroup = merged.get(groupName);
            if ( request_groups.contains(groupName) && mergedGroup == null ) {
            	mergedGroup = new HashMap<String, String>();
            }
            if (mergedGroup != null) {
                // Move it to the merged "global" properties
                mergedGroup.putAll(group);
                merged.put(groupName, mergedGroup);
                // Remove it from the dataset properties.
                toRemove.add(groupName);
                // dataset.remove(groupName);
            }        
        }
        // Remove properites that have been moved.
        for (Iterator rmIt = toRemove.iterator(); rmIt.hasNext();) {
            String key = (String) rmIt.next();
            dataset.remove(key);
        }

        /*
         * The properties that are set to something besides "default" in the UI request should "win".  
         * If the user selects something on the UI, what they select should "win".  If the installer wants 
         * to prevent the user from having that choice they'll have to take that option off the UI for whatever 
         * circumstance they wish to prevent the choice from being made.  Make sense?
         */
        for (Iterator groupIt = request.keySet().iterator(); groupIt.hasNext();) {
            String groupName = (String) groupIt.next();
            HashMap<String, String> group = request.get(groupName);
            HashMap<String, String> mergedGroup = merged.get(groupName);
            if (mergedGroup == null) {
                mergedGroup = new HashMap<String, String>();
            }
            HashMap<String, String> nonDefaultRequestProperties = new HashMap<String, String>();
            for (Iterator propIt = group.keySet().iterator(); propIt.hasNext();) {
                String key = (String) propIt.next();
                String value = group.get(key);
                if ( !value.equals("default") ) {
                    nonDefaultRequestProperties.put(key, value);
                }
            }
            // Put all of the non-default properties from the request
            // into the merged properties, replacing any existing
            // properties.
            mergedGroup.putAll(nonDefaultRequestProperties);
            merged.put(groupName, mergedGroup);
        }

        HashMap<String, Element> returns = new HashMap<String, Element>();

        mergedProperties = hashMapToProperties(merged);
        dsProperties = hashMapToProperties(dataset);

        returns.put("merged", (Element)mergedProperties.clone());
        returns.put("dataset", (Element)dsProperties.clone());
        return returns;

    }

    /**
     * A helper method that takes a bunch of name value pairs in a hashmap and makes
     * <properties>
     *    <name>NAME1</name>
     *    <value>VALUE1</value>
     *    <name>NAME2</name>
     *    <value>VALUE2</value>
     * </properties>
     * JDOM elements.
     * @param propsMap the HashMap to convert
     * @return properties the JDOM element with the above structure.
     */
    private Element hashMapToProperties(HashMap propsMap) {
        Element properties = new Element("properties");
        for (Iterator groups = propsMap.keySet().iterator(); groups.hasNext();) {
            String groupName = (String) groups.next();
            HashMap group = (HashMap)propsMap.get(groupName);
            Element groupElement = new Element("property_group");
            groupElement.setAttribute("type", groupName);
            for (Iterator propIt = group.keySet().iterator(); propIt.hasNext();) {
                String name = (String) propIt.next();
                Element propertyElement = new Element("property");
                Element nameElement = new Element("name");
                Element valueElement = new Element("value");
                String value = (String)group.get(name);
                nameElement.setText(name);
                valueElement.setText(value);
                propertyElement.addContent(nameElement);
                propertyElement.addContent(valueElement);
                groupElement.addContent(propertyElement);
            }
            properties.addContent(groupElement);        
        }
        return properties;
    }

    /**
     * Takes a JDOM element of the form:
     * <properties type="GROUP">
     *    <name>NAME</name>
     *    <value>VALUE</value>
     * </properties>
     *    
     *    or
     * <properties>
     *    <GROUP>
     *       <NAME>VALUE</NAME>
     *    </GROUP>
     * </properties>
     * and convertes it to a HashMap with just the name/value pairs.
     * @param properties
     * @return returns a HashMap of HashMaps; the outer HashMap is the property group - the keys are the group name; the inner is the name/value pairs.
     */
    public HashMap<String, HashMap<String, String>> propertiesToHashMap (Element properties) {

        HashMap<String, HashMap<String, String>> propHashMap = new HashMap<String, HashMap<String, String>>();
        List groups = properties.getChildren("property_group");
        boolean oldstyle=false;
        if ( groups.size() == 0 ) {
            groups = properties.getChildren();
            oldstyle=true;
        }
        for (Iterator groupIt = groups.iterator(); groupIt.hasNext();) {

            Element propGroup = (Element) groupIt.next();
            String groupName;
            List props;
            if (oldstyle) {
                groupName = propGroup.getName();
                props = propGroup.getChildren();
            }else {
                groupName = propGroup.getAttributeValue("type");
                props = propGroup.getChildren("property");
            }

            HashMap<String, String> group = new HashMap<String, String>();

            for (Iterator propIt = props.iterator(); propIt.hasNext();) {
                Element prop = (Element) propIt.next();
                String propName;
                String propValue;
                if (oldstyle) {
                    propName = prop.getName();
                    propValue = prop.getText();
                } else {
                    propName = prop.getChildText("name");
                    propValue = prop.getChildText("value");
                }
                // We only care about non-default values and non-blank.
                if (propValue.length() > 0) {
                    group.put(propName, propValue);
                }
            }
            propHashMap.put(groupName, group);
        }
        return propHashMap;
    }
    /**
     * Finds a collection of properties of the form:
     * <properties>
     *    <GROUP>
     *       <NAME>VALUE</NAME>
     *    </GROUP>
     * </properties>
     * @param doc 
     * @param group
     * @return
     * @throws Exception
     */
    /* 
    public Element findOldPropertyGroup(LASDocument doc, String group) throws Exception {
        // Finds the properties that is a child of the root.
        return findOldPropertyGroup(doc.getRootElement(), group);
    }

    public Element findOldPropertyGroup(Element properties, String group) throws Exception {
        // Finds properties below a particular element.
        Filter propertyOldGroupFilter = new FindOldPropertyGroupFilter(group);
        Iterator pgIt = properties.getDescendants(propertyOldGroupFilter);
        Element propGroup = (Element) pgIt.next();
        if ( pgIt.hasNext() ) {
            throw new Exception("More than one property group with name = "+group);
        }
        return propGroup;
    }

    public Element findOldProperty(Element group, String name) throws Exception {
        Filter oldPropertyFilter = new FindOldPropertyFilter(name);
        Iterator propsIt = group.getDescendants(oldPropertyFilter);
        Element property = (Element) propsIt.next();
        if ( propsIt.hasNext()) {
            throw new Exception("More than one property with name = "+name);
        }
        return property; 
    }
     */

    /**
     * Get a property from a particular request in this chain of product requests.
     * @param ID - the ID of the operation whose sub-request we want to examine for the property
     * @param group - the property grou
     * @param name - the name of the property
     * @return value - the value of the property.
     */
    public String getProperty(String ID, String group, String name) throws LASException {

        int index = findOperationIndex(ID);

        LASBackendRequest request = requestXML.get(index);
        Element propGroup = request.findPropertyGroup(group);
        if ( propGroup != null) {
            return request.findPropertyValue(propGroup, name);
        } else {
            return "";
        }

    }

    // TODO There is now a well defined concept of global and data set properties
    // so you should have to search them all just the global ones right!?!


    /**
     * Get a data set property from a particular backend request data Element.
     * @param data - the data element to be examined
     * @param group - the property group name
     * @param name - the name of the property
     * @return value - the value of the property
     */
    public String getDataSetProperty(Element data, String group, String name) {

        Element properties = data.getChild("properites");
        if ( properties != null ) {
            List propGroups = properties.getChildren("property_group");
            for (Iterator pgIt = propGroups.iterator(); pgIt.hasNext();) {
                Element propGroup = (Element) pgIt.next();
                if (propGroup.getAttributeValue("type").equals(group)) {
                    List props = propGroup.getChildren("property");
                    for (Iterator pIt = props.iterator(); pIt.hasNext();) {
                        Element property = (Element) pIt.next();
                        String propName = property.getChildText("name");
                        if (propName.equals(name)) {
                            return property.getChildText("value");
                        }
                    }
                }
            }
        }


        return "";
    }

    /**
     * Get the list of XML Backend Request objects for this product request chain.
     * @return requestXML - the list of request objects
     */
    public ArrayList<LASBackendRequest> getRequestXML() {
        return requestXML;
    }

    /**
     * Get the global cache key for this product request.
     * @return cacheKey - the cache key that applies to the entire request chain.
     */
    public String getCacheKey() {
        return this.cacheKey;
    }

    /**
     * Get the cache key for a particular request in the chain
     * @param i - the index of the request
     * @return cacheKey - the cache key for that sub-request.
     */
    public String getSubCacheKey(int i) {
        return cacheKeys.get(i);
    }

    /**
     * Get the name of the output template that is needed to render the output from this product request.
     * @return output_template - the output template name
     */
    public String getTemplate() {
        return operationElement.getAttributeValue("output_template");
    }

    public String getTemplateMimeType() {
        return operationElement.getAttributeValue("mime_type");
    }

    /**
     * Get the name of the service that will be contacted to perform this sub-operation
     * @param ID - the ID of the sub-operation
     * @return service - the name of the service
     * @throws LASException
     */
    public String getServiceName(String ID) throws LASException {
        int index=findOperationIndex(ID);

        Element operation = operations.get(index);
        return operation.getChildText("service");
    }

    /**
     * Find the ID of a particular sub-operation based on the index.
     * @param index - the index of the sub-operation to be looked up.
     * @return the operation ID at the index
     */
    public String getOperationID(int index) {
        return operationIDs.get(index);
    }

    /**
     * Determine whether or not to use the cache for a particular sub-operation
     * @param ID - the ID of the operation being examined
     * @return use_cache - true unless it gets turned off by a "product_server" property in this request.
     * @throws LASException 
     */
    public boolean getUseCache(String ID) throws LASException{
        // The semantics of the "product_server" "use_cache" property
        // is known, so we must look at all of them and decide what
        // to do
        boolean use_cache = true;
        int index = findOperationIndex(ID);

        LASBackendRequest request = requestXML.get(index);
        ArrayList propGroups = request.findPropertyGroupList("product_server");

        for (Iterator pgIt = propGroups.iterator(); pgIt.hasNext();) {
            Element propertyGroup = (Element) pgIt.next();
            List properites = propertyGroup.getChildren("property");
            for (Iterator pIt = properites.iterator(); pIt.hasNext();) {
                Element property = (Element) pIt.next();
                if (property.getChildTextTrim("name").equals("use_cache")) {
                    String use_cacheString = property.getChildTextTrim("value");
                    use_cache = use_cache && Boolean.valueOf(use_cacheString).booleanValue();
                }
            }
        }
        return use_cache;
    }

    /**
     * The "global" value of use_cache.
     * @return true if the cached should be used.
     */
    public boolean getUseCache() {
        return useCache;
    }

    /**
     * Get the cache key for a particular sub-operation.
     * @param ID - the ID of the operation
     * @return cacheKey - the cache key for this sub-operation
     * @throws LASException
     */
    public String getCacheKey(String ID) throws LASException {
        int index = findOperationIndex(ID);
        return cacheKeys.get(index);
    }

    /**
     * When one sub-operation is chained to another, this method makes sure any output from a previous operation
     * that is input into the next operation gets in the right place in the Request XML before the next operation is fired.
     * @param ID - The ID of the operation that is looking for chained input.
     * @param lasResponse - The response object so far from which the chained output will be extracted.
     * @throws LASException 
     */
    public void chainResults(String ID, LASBackendResponse lasResponse) throws LASException {

        int index = findOperationIndex(ID);
        int nextIndex = index+1;
        if ( nextIndex >= operationIDs.size()) {
            // No more operations.  Nothing to do.
            return;
        }

        LASBackendRequest nextRequest = requestXML.get(nextIndex);


        List dataList = nextRequest.getRootElement().getChild("dataObjects").getChildren("data");
        for (Iterator dataListIt = dataList.iterator(); dataListIt.hasNext();) {
            Element data = (Element) dataListIt.next();
            String chained = data.getAttributeValue("chained");
            if ( chained != null && chained.equals("true")) {
                String resultID = data.getAttributeValue("result");
                String returnedURL = lasResponse.getResult(resultID);    
                if ( lasResponse.isResultRemote(resultID)) {
                    data.setAttribute("file",returnedURL);
                }
            }

        }
    }

    /**
     * Given a particular operation ID, find the index of said operation.
     * @param ID - the ID of the sought after operation.
     * @return index - the index of the operation.
     * @throws LASException
     */
    public int findOperationIndex(String ID) throws LASException {
        int found = -1;
        int index = 0;
        for (Iterator opidIt = operationIDs.iterator(); opidIt.hasNext();) {
            String id = (String) opidIt.next();
            if ( id.equals(ID)) {
                found = index;
                break;
            }
            index++;
        }
        if (found == -1) {
            throw new LASException("Sub-opertion with ID="+ID+"not found for this operation.");
        }
        return found;
    }

    /**
     * Add a new property to a given group, create the group if necessary.
     * @param properties - the existing properties colleciton.
     * @param propertyGroup - the name of the group.
     * @param name - the name of the property
     * @param value - the value of the property
     */
    public void addProperty(Element properties, String propertyGroup, String name, String value) {

        List property_groups = properties.getChildren("property_group");
        Element property_groupE = null;
        for (Iterator pgIt = property_groups.iterator(); pgIt.hasNext();) {
            Element property_group = (Element) pgIt.next();
            if ( property_group.getAttributeValue("type").equals(propertyGroup)) {
                property_groupE = property_group;
            }
        }

        if (property_groupE == null) {
            property_groupE = new Element("property_group");
            property_groupE.setAttribute("type", propertyGroup);
            properties.addContent(property_groupE);
        }

        Element property = new Element("property");
        Element nameE = new Element("name");
        Element valueE = new Element("value");

        nameE.setText(name);
        valueE.setText(value);
        property.setContent(nameE);
        property.addContent(valueE);
        property_groupE.addContent(property);

    }

    /**
     * Get a list of all the operation names that can be used in the status page.
     * @return op_list - the list of operations
     */
    public ArrayList getOperationNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (int opidx = 0; opidx < operations.size(); opidx++ ) {
            names.add(opidx, (operations.get(opidx)).getAttributeValue("name"));
        }
        return names;
    }

    public LASUIRequest getLasRequest() {
        return lasRequest;
    }

    public void setLasRequest(LASUIRequest lasRequest) {
        this.lasRequest = lasRequest;
    }

	public LASBackendRequest getRequestByService(String service) throws JDOMException, LASException {
		for (Iterator reqIt = requestXML.iterator(); reqIt.hasNext();) {
			LASBackendRequest request = (LASBackendRequest) reqIt.next();
			if ( request.getService().equals(service) ) {
				return request;
			}
		}
		return null;
	}

}
