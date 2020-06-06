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

import gov.noaa.pmel.tmap.addxml.ADDXMLProcessor;
import gov.noaa.pmel.tmap.addxml.AxisBean;
import gov.noaa.pmel.tmap.addxml.CatalogRefHandler;
import gov.noaa.pmel.tmap.addxml.CategoryBean;
import gov.noaa.pmel.tmap.addxml.DatasetBean;
import gov.noaa.pmel.tmap.addxml.DatasetsGridsAxesBean;
import gov.noaa.pmel.tmap.addxml.FilterBean;
import gov.noaa.pmel.tmap.addxml.GridBean;
import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.jdom.filter.AttributeFilter;
import gov.noaa.pmel.tmap.jdom.filter.CategoryFilter;
import gov.noaa.pmel.tmap.jdom.filter.EmptySrcDatasetFilter;
import gov.noaa.pmel.tmap.las.client.lastest.TestConstants;
import gov.noaa.pmel.tmap.las.client.rpc.RPCException;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraint;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraintGroup;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.noaa.pmel.tmap.las.product.server.Cache;
import gov.noaa.pmel.tmap.las.proxy.LASProxy;
import gov.noaa.pmel.tmap.las.test.LASTestOptions;
import gov.noaa.pmel.tmap.las.ui.state.StateNameValueList;
import gov.noaa.pmel.tmap.las.ui.state.TimeSelector;
import gov.noaa.pmel.tmap.las.util.Axis;
import gov.noaa.pmel.tmap.las.util.Category;
import gov.noaa.pmel.tmap.las.util.Constants;
import gov.noaa.pmel.tmap.las.util.ContainerComparator;
import gov.noaa.pmel.tmap.las.util.DataConstraint;
import gov.noaa.pmel.tmap.las.util.Dataset;
import gov.noaa.pmel.tmap.las.util.Grid;
import gov.noaa.pmel.tmap.las.util.Institution;
import gov.noaa.pmel.tmap.las.util.NameValuePair;
import gov.noaa.pmel.tmap.las.util.Operation;
import gov.noaa.pmel.tmap.las.util.Option;
import gov.noaa.pmel.tmap.las.util.Region;
import gov.noaa.pmel.tmap.las.util.TimeAxis;
import gov.noaa.pmel.tmap.las.util.Tributary;
import gov.noaa.pmel.tmap.las.util.Variable;
import gov.noaa.pmel.tmap.las.util.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.http.HttpException;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Parent;
import org.jdom.xpath.XPath;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.SAXException;

import thredds.catalog.InvCatalog;
import thredds.catalog.InvCatalogFactory;
import thredds.catalog.InvDataset;

/**
 * This class is the JDOM instantiation of the "las.xml" file and any entities it references (data stubs and operationsV7.xml).
 * @author Roland Schweitzer
 *
 */
public class LASConfig extends LASDocument {
    private static Logger log = LoggerFactory.getLogger(LASConfig.class.getName());
    private static HashMap<String, HashSet<String>> remoteData = new HashMap<String, HashSet<String>>();
    private static LASProxy lasProxy = new LASProxy();
    private static String time_formats[] = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH",
            "yyyy-MM-dd",
            "yyy-MM-dd HH:mm:ss",
            "yyy-MM-dd HH:mm",
            "yyy-MM-dd HH",
            "yyy-MM-dd",
            "yy-MM-dd HH:mm:ss",
            "yy-MM-dd HH:mm",
            "yy-MM-dd HH",
            "yy-MM-dd",
            "y-MM-dd HH:mm:ss",
            "y-MM-dd HH:mm",
            "y-MM-dd HH",
            "y-MM-dd",
            "yyyy-M-dd HH:mm:ss",
            "yyyy-M-dd HH:mm",
            "yyyy-M-dd HH",
            "yyyy-M-dd",
            "yyy-M-dd HH:mm:ss",
            "yyy-M-dd HH:mm",
            "yyy-M-dd HH",
            "yyy-M-dd",
            "yy-M-dd HH:mm:ss",
            "yy-M-dd HH:mm",
            "yy-M-dd HH",
            "yy-M-dd",
            "y-M-dd HH:mm:ss",
            "y-M-dd HH:mm",
            "y-M-dd HH",
            "y-M-dd",
            "yyyy-MM-d HH:mm:ss",
            "yyyy-MM-d HH:mm",
            "yyyy-MM-d HH",
            "yyyy-MM-d",
            "yyy-MM-d HH:mm:ss",
            "yyy-MM-d HH:mm",
            "yyy-MM-d HH",
            "yyy-MM-d",
            "yy-MM-d HH:mm:ss",
            "yy-MM-d HH:mm",
            "yy-MM-d HH",
            "yy-MM-d",
            "y-MM-d HH:mm:ss",
            "y-MM-d HH:mm",
            "y-MM-d HH",
            "y-MM-d",
            "yyyy-M-d HH:mm:ss",
            "yyyy-M-d HH:mm",
            "yyyy-M-d HH",
            "yyyy-M-d",
            "yyy-M-d HH:mm:ss",
            "yyy-M-d HH:mm",
            "yyy-M-d HH",
            "yyy-M-d",
            "yy-M-d HH:mm:ss",
            "yy-M-d HH:mm",
            "yy-M-d HH",
            "yy-M-d",
            "y-M-d HH:mm:ss",
            "y-M-d HH:mm",
            "y-M-d HH",
            "y-M-d",
            "dd-MMM-yyyy HH:mm:ss",
            "dd-MMM-yyyy HH:mm",
            "dd-MMM-yyyy HH",
            "dd-MMM-yyyy",
            "d-MMM-yyyy HH:mm:ss",
            "d-MMM-yyyy HH:mm",
            "d-MMM-yyyy HH",
            "d-MMM-yyyy",
            "dd-MMM-yyy HH:mm:ss",
            "dd-MMM-yyy HH:mm",
            "dd-MMM-yyy HH",
            "dd-MMM-yyy",
            "d-MMM-yyy HH:mm:ss",
            "d-MMM-yyy HH:mm",
            "d-MMM-yyy HH",
            "d-MMM-yyy",
            "dd-MMM-yy HH:mm:ss",
            "dd-MMM-yy HH:mm",
            "dd-MMM-yy HH",
            "dd-MMM-yy",
            "d-MMM-yy HH:mm:ss",
            "d-MMM-yy HH:mm",
            "d-MMM-yy HH",
            "d-MMM-yy",
            "dd-MMM-y HH:mm:ss",
            "dd-MMM-y HH:mm",
            "dd-MMM-y HH",
            "dd-MMM-y",
            "d-MMM-y HH:mm:ss",
            "d-MMM-y HH:mm",
            "d-MMM-y HH",
            "d-MMM-y"
    };
    /**.
     * Get all the possible combinations of the characters in a string.  Combo routines based on code by Robert Sedgewick and Kevin Wayne.
     * from their book Introduction to Programming in Java published by Adison Wesley.
     * @param s
     * @return
     */
    private static ArrayList<String> combo(String s) {
        return combo("", s);
    }
    /**
     * Get combinations of the characters in a string.
     * @param prefix A prefix for the combinations
     * @param s the string to scramble
     * @return the combinations
     */
    public static ArrayList<String> combo(String prefix, String s) {
        ArrayList<String> comboList = new ArrayList<String>();

        if (!prefix.equals("")) {
            comboList.add(prefix);
        }

        if ( s.equals("") ) {
            return comboList;
        }
        for ( int i = 0; i < s.length(); i++ ) {
            comboList.addAll(combo(prefix + s.charAt(i), s.substring(i+1)));
        }
        return comboList;
    }
    public Map<String, String> getIDMap(String data_url) throws JDOMException, LASException {
    	List<Category> categories = getDatasetsAsCategories(false);

    	for (Iterator catIt = categories.iterator(); catIt.hasNext();) {
    		Category category = (Category) catIt.next();
    		Map<String, String> ids = findDataURL(data_url, category);
    		if ( ids.size() > 0 ) {
    			return ids;
    		}
    	}

   	    return new HashMap<String, String>();
    }
    public String getIDs(String data_url) throws JDOMException, LASException, HttpException, IOException {
        Map<String, String> ids = getIDMap(data_url);

        if ( ids.size() > 0 ) {
            return "dsid="+ids.get("dsid")+"&varid="+ids.get("varid");
        } else {

            // The code below which is turned off by default allows data sets to be added dynamically to an LAS.
            // Uncommmet the code below to re-enable this capability

            return "";

            // Add the data sets via addXML, then get the ID map again

        	// Force t to be regular.

//        	HashMap<String, String> options = new HashMap<String, String>();
//        	options.put("force", "t");
//
//        	Vector dagbs = updateSrc(data_url, "netcdf", null, null, options);
//        	Element datasets = new Element("datasets");
//        	Element grids = new Element("grids");
//        	Element axes = new Element("axes");
//        	for (Iterator dagIt = dagbs.iterator(); dagIt.hasNext();) {
//				DatasetsGridsAxesBean bean = (DatasetsGridsAxesBean) dagIt.next();
//				List<DatasetBean> d = bean.getDatasets();
//				List<GridBean> g = bean.getGrids();
//				List<AxisBean> a = bean.getAxes();
//				for (Iterator dIt = d.iterator(); dIt.hasNext();) {
//					DatasetBean dBean = (DatasetBean) dIt.next();
//                    Element ds = dBean.toXml(true);
//                    if ( !hasCategories() ) {
//                    	ds.setAttribute("catid", dBean.getElement());
//                    }
//					datasets.addContent(ds);
//				}
//				for (Iterator gIt = g.iterator(); gIt.hasNext(); ) {
//					GridBean gBean = (GridBean) gIt.next();
//					grids.addContent(gBean.toXml(true));
//				}
//				for (Iterator aIt = a.iterator(); aIt.hasNext(); ) {
//					AxisBean aBean = (AxisBean) aIt.next();
//					axes.addContent(aBean.toXml(true));
//				}
//
//			}
//        	if ( dagbs.size() > 0 ) {
//        		getRootElement().addContent(datasets);
//        		getRootElement().addContent(grids);
//        		getRootElement().addContent(axes);
//        		convertToSeven(true);
//                mergeProperites();
//                addIntervalsAndPoints();
//                addGridType();
//        	}
//        	Date now = new Date();
//			File debug = new File(getOutputDir()+"/las_debug_add_"+now.getTime()+".xml");
//			write(debug);
//        	ids = getIDMap(data_url);
//        	return "dsid="+ids.get("dsid")+"&varid="+ids.get("varid");
        }
    }
    private Map<String, String> findDataURL(String data_url, Category category) throws JDOMException, LASException {

    	if ( category.getAttributeValue("children") != null && category.getAttributeValue("children").equals("variables") ) {
    		// Look for the data url.

    		List<Variable> vars = getVariables(category.getAttributeValue("children_dsid"));
    		for (Iterator varsIt = vars.iterator(); varsIt.hasNext();) {
    			Variable variable = (Variable) varsIt.next();
    			String url = getFullDataObjectURL(variable.getDSID(), variable.getID());
                if ( url.contains("#")) {
                    url = url.substring(0,url.indexOf("#"));
                }
    			if ( !url.equals("") ) {
    				if ( data_url != null && url.equals(data_url) ) {
    				    Map<String, String> ids = new HashMap<String, String>();
    				    ids.put("catid", category.getID());
    				    ids.put("dsid", variable.getDSID());
    				    ids.put("varid", variable.getID());
    					return ids;
    				} 
    			}
    		}
    		return new HashMap<String, String>();

    	} else {
    		List<Category> categories = getCategories(category.getID());
    		for (Iterator catIt = categories.iterator(); catIt.hasNext();) {
    			Category cat = (Category) catIt.next();
    			Map<String, String> ids = findDataURL(data_url, cat);
    			if ( ids.size() > 0 ) {
    				return ids;
    			}
    		}
    	}
    	return new HashMap<String, String>();
    }
    /**
     * Return up to max direct and F-TDS URLs that can be tested.  This just grabs them in order.  Something more sophisticated could be done.
     * @param max
     * @return
     */
    public List<String> getFTDSTestURLs(int max) {
    	List<String> testURLs = new ArrayList<String>();
        List datasetsElements = getRootElement().getChildren("datasets");
        // Guess at how many we need...
        int perDataset = max/datasetsElements.size() + 1;
        for (Iterator datasetsElementIt = datasetsElements.iterator(); datasetsElementIt.hasNext();) {
            Element datasetsE = (Element) datasetsElementIt.next();
            List datasets = datasetsE.getChildren("dataset");

            for (Iterator datasetIt = datasets.iterator(); datasetIt.hasNext();) {
                HashMap<String, String> jnls = new HashMap<String, String>();
                Element dataset = (Element) datasetIt.next();
                String dsID = dataset.getAttributeValue("ID");
                List variablesElements = dataset.getChildren("variables");
                for (Iterator variablesEelementsIt = variablesElements.iterator(); variablesEelementsIt.hasNext();) {
                    Element variablesE = (Element) variablesEelementsIt.next();
                    List variables = variablesE.getChildren("variable");
                    int count = 0;
                    for (Iterator varsIt = variables.iterator(); varsIt.hasNext();) {
                    	Element variable = (Element) varsIt.next();
                    	if ( count < perDataset && variable.getAttributeValue("grid_type").equals("regular") ) {
                    		testURLs.add(variable.getAttributeValue("ftds_url"));
                    		count++;
                    	}
                    	if ( testURLs.size() > max ) {
                    		return testURLs;
                    	}
                    }
                }
            }
        }
        return testURLs;
    }
    /**
     * Take F-TDS server URL and data directory and build the F-TDS URLs for each variable.
     * @param fds_base the base URL of the F-TDS server http://server:port/thredds/dodsC
     * @param fds_dir the directory into which the F-TDS journal files will be written
     * @throws LASException
     * @throws JDOMException
     * @throws IOException
     */
    public void addFDS(String fds_base, String fds_dir) throws LASException, JDOMException, IOException {
        File datadir = new File(fds_dir);
        if ( !datadir.exists() ) {
            boolean success = datadir.mkdirs();
            if ( !success ) {
                log.warn("No T-FDS directory.");
            }
        }
        datadir = new File(fds_dir+"dynamic");
        if ( !datadir.exists() ) {
            boolean success = datadir.mkdirs();
            if ( !success ) {
                log.warn("No T-FDS directory for user defined data.");
            }
        }
        List datasetsElements = getRootElement().getChildren("datasets");
        for (Iterator datasetsElementIt = datasetsElements.iterator(); datasetsElementIt.hasNext();) {
            Element datasetsE = (Element) datasetsElementIt.next();
            List datasets = datasetsE.getChildren("dataset");

            for (Iterator datasetIt = datasets.iterator(); datasetIt.hasNext();) {
                HashMap<String, String> jnls = new HashMap<String, String>();
                Element dataset = (Element) datasetIt.next();
                String dsID = dataset.getAttributeValue("ID");
                List variablesElements = dataset.getChildren("variables");
                for (Iterator variablesEelementsIt = variablesElements.iterator(); variablesEelementsIt.hasNext();) {
                    Element variablesE = (Element) variablesEelementsIt.next();
                    List variables = variablesE.getChildren("variable");
                    for (Iterator varsIt = variables.iterator(); varsIt.hasNext();) {
                        Element variable = (Element) varsIt.next();
                        if ( variable.getAttributeValue("ftds_url") == null ) {
                            String varID = variable.getAttributeValue("ID");
                            String var = getVariableName(dsID,varID);
                            String grid_type = variable.getAttributeValue("grid_type");
                            if ( grid_type.equals("regular") ) {
                                datadir = new File(fds_dir+dsID);
                                if ( !datadir.exists() ) {
                                    boolean success = datadir.mkdirs();
                                    if ( !success ) {
                                        log.warn("No T-FDS directory for "+dsID);
                                    }
                                }
                                String url = getDataObjectURL(variable);
                                String init = getVariablePropertyValue(variable,"ferret","init_script");
                                String key;
                                if ( init != null && !init.equals("") ) {
                                    key = url+"_"+init+"_"+var;
                                } else {
                                    key = url;
                                }
                                if ( key.startsWith("http://") ) {
                                    key = key.substring(6,key.length());
                                }
                                key = key.replaceAll("/","_");
                                key = key.replaceAll(":","_");

                                if ( key.startsWith("_") ) {
                                    key = key.substring(1,key.length());
                                }

                                if ( !jnls.containsKey(key) ) {
                                    StringBuffer jnl = new StringBuffer();
                                    if ( init != null && !init.equals("") ) {
                                        jnl.append("DEFINE SYMBOL data_url \\\""+url+"\\\"\n");
                                        jnl.append("DEFINE SYMBOL data_var "+var+"\n");
                                        jnl.append("GO "+init+"\n");
                                        jnls.put(key, jnl.toString());
                                    } else {
                                        jnl.append("USE \""+url+"\"\n");
                                        jnls.put(key, jnl.toString());
                                    }
                                }

                                variable.setAttribute("ftds_url", fds_base+dsID+"/data_"+key+".jnl");


                                // HACK -- also adds the grid to a composite variable...
                            } else if ( grid_type.equals("vector") ) {
                                List<Element> compositeVars = variable.getChildren("variable");
                                if ( compositeVars.size() > 0 ) {
                                    String VARID = compositeVars.get(0).getAttributeValue("IDREF");
                                    Grid grid = getGrid(dataset.getAttributeValue("ID"), VARID);
                                    Element gridE = new Element("grid");
                                    gridE.setAttribute("IDREF", grid.getID());
                                    variable.addContent(gridE);
                                }
                            }
                        }
                    }
                }

                if ( jnls.size() > 0 ) {
                    int index=0;
                    String fds_temp = null;
                    if ( fds_dir.endsWith("/") ) {
                        fds_temp = fds_dir.substring(0, fds_dir.lastIndexOf("/"));
                        fds_temp = fds_temp.substring(0, fds_temp.lastIndexOf("/")) + File.separator+ "temp";
                    } else {
                        fds_temp = fds_dir.substring(0, fds_dir.lastIndexOf("/")) + File.separator + "temp";
                    }
                    List<File> headers = (List<File>) FileUtils.listFiles(new File(fds_temp), new String[]{"xml"}, true);
                    Set<File> remove = new HashSet<File>();
                    for (Iterator jnlsIt = jnls.keySet().iterator(); jnlsIt.hasNext();) {
                        String key = (String) jnlsIt.next();
                        String file = "data_"+key+".jnl";
                        File varjnl = new File(fds_dir+dsID+File.separator+file);
                        PrintWriter data_script = new PrintWriter(new FileWriter(varjnl));
                        data_script.println(jnls.get(key));
                        data_script.close();
                        index++;
                        for (Iterator headerIt = headers.iterator(); headerIt.hasNext();) {
                            File header = (File) headerIt.next();
                            LineIterator it = FileUtils.lineIterator(header);
                            try{
                                while (it.hasNext()){
                                    String line = it.nextLine();
                                    if(line.contains(file)){
                                        remove.add(header);
                                    }
                                }
                            }
                            finally {LineIterator.closeQuietly(it);}
                        }
                    }
                    for (Iterator removeIt = remove.iterator(); removeIt.hasNext();) {
                        File file = (File) removeIt.next();
                        FileUtils.deleteQuietly(new File(file.getParent()));
                    }
                }
            }
        }
    }
    /**
     * Descends the dataset and variable tree and set the grid_type attribute
     * if it is not already set.
     * @throws LASException
     * @throws JDOMException
     */
    public void addGridType() throws LASException, JDOMException {
        Element root = getRootElement();
        String version = root.getAttributeValue("version");
        if ( version != null && !version.contains("7.")) {
            throw new LASException("XML is not version 7.0 or above.  Try convertToSeven() first.");
        }

        List datasetsElements = root.getChildren("datasets");
        for (Iterator dseIt = datasetsElements.iterator(); dseIt.hasNext();) {
            Element datasetsE = (Element) dseIt.next();
            List datasets = datasetsE.getChildren("dataset");
            for (Iterator dsIt = datasets.iterator(); dsIt.hasNext();) {
                Element dataset = (Element) dsIt.next();
                // In theory there's only 1, but loop to be sure.
                List variablesElements = dataset.getChildren("variables");
                for (Iterator varseIt = variablesElements.iterator(); varseIt
                        .hasNext();) {
                    Element variablesE = (Element) varseIt.next();
                    List variables = variablesE.getChildren("variable");
                    for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
                        Element variable = (Element) varIt.next();
                        setGridType(variable);
                    }
                }
            }
        }
    }
    /**
     * Adds attributes to all variables that indicate whether or not the variable
     * has a range or a point in the definition of each axis.  Must be a version
     * 7.0 style document before this method is invoked.
     * @throws LASException
     * @throws JDOMException
     */
    public void addIntervalsAndPoints() throws LASException, JDOMException {
    	 Element root = getRootElement();
         String version = root.getAttributeValue("version");
         if ( version != null && !version.contains("7.")) {
             throw new LASException("XML is not version 7.0 or above.  Try convertToSeven() first.");
         }
         List gridsElements = root.getChildren("grids");
         for (Iterator gridsIt = gridsElements.iterator(); gridsIt.hasNext();) {
 			Element grids = (Element) gridsIt.next();
 			List gridElements = grids.getChildren("grid");
 			for (Iterator gridIt = gridElements.iterator(); gridIt.hasNext();) {
 				Element gridElement = (Element) gridIt.next();
 				List<Element> gridAxes = new ArrayList<Element>();
 				List<Element> axes = gridElement.getChildren("axis");
 				List<String> ids = new ArrayList<String>();
 				for (Iterator iterator = axes.iterator(); iterator.hasNext();) {
                    Element element = (Element) iterator.next();
                    ids.add(element.getAttributeValue("IDREF"));
                }
 				List<Element> allAxes = getRootElement().getChildren("axes");
 				for (Iterator allAxIt = allAxes.iterator(); allAxIt.hasNext();) {
                    Element ax = (Element) allAxIt.next();
                    List<Element> allAxis = ax.getChildren("axis");
                    for (Iterator allAxisIt = allAxis.iterator(); allAxisIt.hasNext();) {
                        Element axis = (Element) allAxisIt.next();
                        if ( ids.contains(axis.getAttributeValue("ID"))) {
                            gridAxes.add(axis);
                        }
                    }
                }
 				String[] intervals = {"","","","",""};
 		        String[] points = {"","","","",""};
 		        for (Iterator gridAxesIt = gridAxes.iterator(); gridAxesIt.hasNext();) {
 		            Element axis = (Element) gridAxesIt.next();
 		            int size;



 		            String type = axis.getAttributeValue("type");
 		            Element arange = axis.getChild("arange");

 		            if ( arange != null ) {
 		                size = Integer.valueOf(arange.getAttributeValue("size")).intValue();
 		            } else {
 		                List v = axis.getChildren("v");
 		                size = v.size();
 		            }
 		            // The axis defintions can come in any order but
 		            // we want this string orders XYZT
 		            if ( size == 1 ) {
 		                if ( type.equals("x") ) {
 		                    points[0] = type;
 		                } else if ( type.equals("y") ) {
 		                    points[1] = type;
 		                } else if ( type.equals("z") ) {
 		                    points[2] = type;
 		                } else if ( type.equals("t") ) {
 		                    points[3] = type;
 		                } else if ( type.equals("e") ) {
 		                    points[4] = type;
 		                }
 		            } else if ( size > 1 ) {
 		                if ( type.equals("x") ) {
 		                    points[0] = type;
 		                    intervals[0] = type;
 		                } else if ( type.equals("y") ) {
 		                    points[1] = type;
 		                    intervals[1] = type;
 		                } else if ( type.equals("z") ) {
 		                    points[2] = type;
 		                    intervals[2] = type;
 		                } else if ( type.equals("t") ) {
 		                    points[3] = type;
 		                    intervals[3] = type;
 		                } else if ( type.equals("e") ) {
 		                    points[4] = type;
 		                    intervals[4] = type;
 		                }
 		            }

 		            
 		        }
 		        
 		        // Set these on the original element, not the cloned grid with filled in axes.
                String existingPoints = gridElement.getAttributeValue("points");
                String existingIntervals = gridElement.getAttributeValue("intervals");
                // Set them only if they don't already exist in the variable definition.
                if ( existingPoints == null ) {
                    gridElement.setAttribute("points", points[0]+points[1]+points[2]+points[3]+points[4]);
                }
                if ( existingIntervals == null ) {
                    gridElement.setAttribute("intervals", intervals[0]+intervals[1]+intervals[2]+intervals[3]+intervals[4]);
                }
// 				Grid g = getGridById(gridElement.getAttributeValue("ID"));
// 				setPointsAndIntervals(g, gridElement);
 			}
 		}
    }
//    private void setPointsAndIntervals(Grid grid, Element gridE) throws JDOMException {
//        
//    	List<Axis> axes = grid.getAxes();
//    	String[] intervals = {"","","",""};
//    	String[] points = {"","","",""};
//    	int size;
//    	if ( grid.hasT() ) {
//    		TimeAxis taxis = grid.getTime();
//    		Arange arange = taxis.getArange();
//
//    		if ( arange != null ) {
//    			size = Integer.valueOf(arange.getSize()).intValue();
//    		} else {
//    			List v = taxis.getVerticies();
//    			size = v.size();
//    		}
//    		if ( size == 1 ) {
//    			points[3] = "t";
//    		} else if ( size > 1 ) {
//    			points[3] = "t";
//    			intervals[3] = "t";
//    		}
//    	}
//
//    	for (Iterator axesIt = axes.iterator(); axesIt.hasNext();) {
//    		Axis axis = (Axis) axesIt.next();
//    		String type = axis.getType();
//    		Arange arange = axis.getArange();
//
//    		if ( arange != null ) {
//    			size = Integer.valueOf(arange.getSize()).intValue();
//    		} else {
//    			List v = axis.getVerticies();
//    			size = v.size();
//    		}
//    		// The axis defintions can come in any order but
//    		// we want this string orders XYZT
//    		if ( size == 1 ) {
//    			if ( type.equals("x") ) {
//    				points[0] = type;
//    			} else if ( type.equals("y") ) {
//    				points[1] = type;
//    			} else if ( type.equals("z") ) {
//    				points[2] = type;
//    			} else if ( type.equals("t") ) {
//    				points[3] = type;
//    			}
//    		} else if ( size > 1 ) {
//    			if ( type.equals("x") ) {
//    				points[0] = type;
//    				intervals[0] = type;
//    			} else if ( type.equals("y") ) {
//    				points[1] = type;
//    				intervals[1] = type;
//    			} else if ( type.equals("z") ) {
//    				points[2] = type;
//    				intervals[2] = type;
//    			} else if ( type.equals("t") ) {
//    				points[3] = type;
//    				intervals[3] = type;
//    			}
//    		}
//    	}
//    	// Set these on the original element, not the cloned grid with filled in axes.
//    	String existingPoints = gridE.getAttributeValue("points");
//    	String existingIntervals = gridE.getAttributeValue("intervals");
//    	// Set them only if they don't already exist in the variable definition.
//    	if ( existingPoints == null ) {
//    		gridE.setAttribute("points", points[0]+points[1]+points[2]+points[3]);
//    	}
//    	if ( existingIntervals == null ) {
//    		gridE.setAttribute("intervals", intervals[0]+intervals[1]+intervals[2]+intervals[3]);
//    	}
//    }

    /**
     * Create all the extra fancy attributes for a time axis so the DateWidgets can be initialized.
     * @param axis the axis description from this config
     * @throws LASException
     */
    private void addTimeAxisAttributes(Element axis) throws LASException {
        Element arange = axis.getChild("arange");
        if (arange == null) {

            List v = axis.getChildren("v");
            for (Iterator vIt = v.iterator(); vIt.hasNext();) {
                Element vE = (Element) vIt.next();
                String label = vE.getAttributeValue("label");
                if ( label == null ) {
                    String content = vE.getTextTrim();
                    vE.setAttribute("label", content);
                }

            }
            Element v0 = (Element) v.get(0);
            String tlo = v0.getTextTrim();
            Element vN = (Element) v.get(v.size()-1);
            String thi = vN.getTextTrim();

            axis.setAttribute("lo", tlo);
            axis.setAttribute("hi", thi);
            axis.setAttribute("display_type", "menu");

        } else {

            axis.setAttribute("display_type", "widget");
            String tlo = arange.getAttributeValue("start");
            String units = axis.getAttributeValue("units");
            double size = Double.valueOf(arange.getAttributeValue("size")).doubleValue();
            double step = Double.valueOf(arange.getAttributeValue("step")).doubleValue();
            DateTimeFormatter fmt = null;
            DateTime lodt = new DateTime("9000-01-01");
            boolean found = false;
            for (int i = 0; i < time_formats.length; i++) {
                fmt = DateTimeFormat.forPattern(time_formats[i]).withZone(DateTimeZone.UTC);
                try {
                   lodt = fmt.parseDateTime(tlo);
                   found = true;
                } catch ( IllegalArgumentException e ) {
                    found = false;
                }
                if (found) break;
            }

            if ( !found ) {
                // Try ISO formatting as a special case.
                try {
                    fmt = ISODateTimeFormat.dateTimeParser();
                    lodt = fmt.parseDateTime(tlo);
                    found = true;
                } catch (IllegalArgumentException e ) {
                    found = false;
                }
            }

            if ( !found ) {
                throw new LASException("Time format for could not be parsed.");
            }

            DateTimeFormatter longfmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);
            DateTimeFormatter isoFmt = ISODateTimeFormat.dateTimeParser();

            axis.setAttribute("lo", lodt.toString(longfmt));
            DateTime hidt = new DateTime();
            if ( units.contains("minute") ) {
                axis.setAttribute("hourNeeded", "true");
                axis.setAttribute("dayNeeded", "true");
                axis.setAttribute("monthNeeded", "true");
                axis.setAttribute("yearNeeded", "true");;
            	int minutes = (int) Math.round((size-1)*step);
            	int minuteInterval = (int) Math.round(step*60.);
                axis.setAttribute("minuteInterval", String.valueOf(minuteInterval) );
                hidt = lodt.plus(Period.minutes(minutes));
            	axis.setAttribute("hi", hidt.toString(longfmt));
            } else if ( units.contains("hour") ) {
                axis.setAttribute("hourNeeded", "true");
                axis.setAttribute("dayNeeded", "true");
                axis.setAttribute("monthNeeded", "true");
                axis.setAttribute("yearNeeded", "true");
                int hours = (int) Math.round((size-1)*step);
                int minuteInterval = (int) Math.round(step*60.);
                axis.setAttribute("minuteInterval", String.valueOf(minuteInterval) );
                hidt = lodt.plus(Period.hours(hours));
                axis.setAttribute("hi", hidt.toString(longfmt));
            } else if ( units.contains("day") ) {
                axis.setAttribute("hourNeeded", "false");
                axis.setAttribute("dayNeeded", "true");
                axis.setAttribute("monthNeeded", "true");
                axis.setAttribute("yearNeeded", "true");
                int days = (int) Math.round((size-1)*step);
                hidt = lodt.plus(Period.days(days));
                axis.setAttribute("hi", hidt.toString(longfmt));
            } else if ( units.contains("month") ) {
                axis.setAttribute("hourNeeded", "false");
                axis.setAttribute("dayNeeded", "false");
                axis.setAttribute("monthNeeded", "true");
                axis.setAttribute("yearNeeded", "true");
                int months = (int) Math.round((size-1)*step);
                hidt = lodt.plus(Period.months(months));
                axis.setAttribute("hi", hidt.toString(longfmt));
            } else if ( units.contains("year") ) {
                axis.setAttribute("hourNeeded", "false");
                axis.setAttribute("dayNeeded", "false");
                axis.setAttribute("monthNeeded", "false");
                axis.setAttribute("yearNeeded", "true");
                // This is the number of years we need to add
                int years = (int) Math.round((size-1)*step);
                hidt = lodt.plus(Period.years(years));
                axis.setAttribute("hi", hidt.toString(longfmt));
            }

            String modulo = axis.getAttributeValue("modulo");
            String climatology = axis.getAttributeValue("climatology");
            if ( modulo != null && modulo.equals("true") ) {
            	axis.setAttribute("yearNeeded", "false");
            }
            if ( climatology != null && climatology.equals("true") ) {
            	axis.setAttribute("yearNeeded", "false");
            }

        }

    }
    /**
     * @param fdsURL
     * @param analysis_axes
     * @param jsessionid
     * @throws JDOMException
     * We aren't going to do this here.  We're going to keep a session object with the user defined variables.
    public void addAnalysisVariables(Variable var, ArrayList<String> analysis_axes, String jsessionid) throws JDOMException {

        Grid grid = getGrid(var.getDSID(), var.getAttributeValue("orig_var_id"));
        Grid analysis_grid = (Grid) grid.clone();

        for (Iterator aaIt = analysis_axes.iterator(); aaIt.hasNext();) {
            String analysis_axis_type = (String) aaIt.next();
            analysis_grid.removeAxis(analysis_axis_type);
        }

        analysis_grid.setID( analysis_grid.getID()+"_" +var.getName() + "_" + jsessionid );

        var.setGridID(analysis_grid.getID());

        if ( !containsVariable( var.getDSID(), var.getID() ) ) {
           getDatasetElement(var.getDSID()).getChild("variables").addContent(var.toElement());
        }

        if ( !containsGrid ( analysis_grid.getID() ) ) {
           getRootElement().getChild("grids").addContent(analysis_grid.toElement());
        }

        System.out.println(this.toString());


    } */
    /**
     * See if this config contains a grid with this ID
     * @param the id to check
     * @return true if found; false if not
     */
    private boolean containsGrid(String grid_id) {

        List gridsElements = getRootElement().getChildren("grids");
        for (Iterator gridsIt = gridsElements.iterator(); gridsIt.hasNext();) {
            Element gridsE = (Element) gridsIt.next();
            List grids = gridsE.getChildren("grid");
            for (Iterator gridIt = grids.iterator(); gridIt.hasNext();) {
                Element gridE = (Element) gridIt.next();
                String ID = gridE.getAttributeValue("ID");
                if ( ID.equals(grid_id) ) {
                    return true;
                }
            }
        }

        return false;
    }
    /**
     * Does this config contain this data set and variable?
     * @param dsid the data set
     * @param var_id the variable
     * @return
     * @throws JDOMException
     */
    private boolean containsVariable(String dsid, String var_id) throws JDOMException {
    	// Could be done with getElementByXPath, no?
        Element dataset = getDatasetElement(dsid);
        List variables = dataset.getChild("variables").getChildren("variable");
        for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
            Element varE = (Element) varIt.next();
            String ID = varE.getAttributeValue("ID");
            if ( ID.equals(var_id) ) {
                return true;
            }
        }
        return false;
    }
    public void convertToSeven(boolean force) throws JDOMException, UnsupportedEncodingException, LASException {
        /*
         * 
         * 
         * if ( variable.getAttributeValue("grid_type") != null && variable.getAttributeValue("grid_type").equals("vector")) {
510                                 List<Element> compositeVars = variable.getChildren("variable");
511                                 if ( compositeVars.size() > 0 ) {
512                                         String VARID = compositeVars.get(0).getAttributeValue("IDREF");
513                                         Grid grid = getGrid(dataset.getAttributeValue("ID"), VARID);
514                                         Element gridE = new Element("grid");
515                                         gridE.setAttribute("IDREF", grid.getID());
516                                         variable.addContent(gridE);
517                                         setPointsAndIntervals(variable, grid);
         */
        Element root = getRootElement();
        String version = root.getAttributeValue("version");

        Set<String> datasetsProcessed = new HashSet<String>();
        Set<String> gridsProcessed = new HashSet<String>();
        Set<String> axesProcessed = new HashSet<String>();
        // Use a list in case there are duplicates of the duplicates.
        List<String> duplicateDatasets = new ArrayList<String>();
        List<String> duplicateGrids = new ArrayList<String>();
        List<String> duplicateAxes = new ArrayList<String>();

        if ( !force && version != null && version.contains("7.") && !allowsSisters()) {
            return;
        }
        root.setAttribute("version", "7.0");
        List children = root.getChildren();
        for (Iterator childIt = children.iterator(); childIt.hasNext();) {
            Element child = (Element) childIt.next();
            if (child.getName().equalsIgnoreCase("datasets"))  {
                List datasets = child.getChildren();
                for (Iterator dsIt = datasets.iterator(); dsIt.hasNext();) {
                    Element dataset = (Element) dsIt.next();
                    /*
                     * Rather than look for the element we want we have to ignore the elements
                     * we don't want (since the "dataset" element names are random.
                     * If there are other names that must be ignored, the must be
                     * added here and below...
                     */

                    if ( !dataset.getName().equals("properties") &&
                         !dataset.getName().equals("documentation") &&
                         !dataset.getName().equals("contributor")) {
                        // If the ID is null convert it, otherwise leave it along...
                        if ( dataset.getAttributeValue("ID") == null ) {
                            String ID = dataset.getName();
                            // Mark the duplicates (process the any way so they can be removed by ID reference.
                            if ( !datasetsProcessed.contains(ID) ) {
                                datasetsProcessed.add(ID);
                            } else {
                                duplicateDatasets.add(ID);
                            }
                            dataset.setName("dataset");
                            if ( allowsSisters() && !ID.contains(Constants.NAME_SPACE_SPARATOR)) ID = JDOMUtils.MD5Encode(getBaseServerURL()) + Constants.NAME_SPACE_SPARATOR + ID;
                            dataset.setAttribute("ID", ID);
                            // Technically, I think there's only one of these per dataset,
                            // but you can't be sure so loop over all you can find.
                            List variablesParents = dataset.getChildren();
                            List<Element> compositeElements = new ArrayList<Element>();
                            for (Iterator varsIt = variablesParents.iterator(); varsIt.hasNext();) {
                                Element variablesElement = (Element) varsIt.next();

                                if ( !variablesElement.getName().equals("properties") &&
                                        !variablesElement.getName().equals("documentation") &&
                                        !variablesElement.getName().equals("contributor") && 
                                        !variablesElement.getName().equals("constraints")) {
                                    boolean composite = false;
                                    List variables = variablesElement.getChildren();
                                    Element cprops = null;
                                    if ( variablesElement.getName().equals("composite") ) {
                                        composite = true;
                                        cprops = variablesElement.getChild("properties");
                                        if (cprops != null) {
                                            cprops.setContent(LASDocument.convertProperties(cprops));
                                        }
                                    }
                                    for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
                                        Element var = (Element) varIt.next();
                                        // If it has an id assume it's already converted to 7.
                                        if ( var.getAttributeValue("ID") == null ) {
                                            if ( !var.getName().equals("properties") &&
                                                    !var.getName().equals("documentation") &&
                                                    !var.getName().equals("contributor") &&
                                                    !var.getName().equals("member")) {
                                                String VID = var.getName();
                                                var.setName("variable");
                                                var.setAttribute("ID", VID);
                                                if ( composite ) {
                                                    var.setAttribute("grid_type", "vector");
                                                    List<Element> varRef = var.getChildren("link");
                                                    for (Iterator varRefIt = varRef.iterator(); varRefIt.hasNext();) {
                                                        Element element = (Element) varRefIt.next();
                                                        String VARID = element.getAttributeValue("match");
                                                        VARID = VARID.substring(VARID.lastIndexOf("/")+1);
                                                        element.setName("variable");
                                                        element.removeAttribute("match");
                                                        element.setAttribute("IDREF", VARID);
                                                    }
                                                    compositeElements.add(var);
                                                } else {
                                                    Element grid = var.getChild("link");
                                                    String GID = grid.getAttributeValue("match");
                                                    String[] parts = GID.split("/");
                                                    GID = parts[3];
                                                    grid.setName("grid");
                                                    grid.removeAttribute("match");
                                                    grid.setAttribute("IDREF", GID);
                                                }
                                                // Convert any properties.
                                                Element vprops = var.getChild("properties");
                                                if (vprops != null) {
                                                    vprops.setContent(LASDocument.convertProperties(vprops));
                                                }
                                            }
                                        } else if ( var.getName().equals("properties") ){
                                            var.setContent(LASDocument.convertProperties(var));
                                        }
                                    }
                                    if ( composite ) {
                                        Element firstVariablesElement = dataset.getChild("variables");
                                        for (Iterator compIt = compositeElements.iterator(); compIt.hasNext();) {
                                            Element compVar = (Element) compIt.next();
                                            firstVariablesElement.addContent(compVar.detach());
                                        }
                                    }
                                } else if ( variablesElement.getName().equals("properties") ) {
                                    variablesElement.setContent(LASDocument.convertProperties(variablesElement));
                                }
                            }
                        } else {
                            if ( !datasetsProcessed.contains(dataset.getAttributeValue("ID"))) {
                                datasetsProcessed.add(dataset.getAttributeValue("ID"));
                            } else {
                                duplicateDatasets.add(dataset.getAttributeValue("ID"));
                            }

                        }
                    } else if ( dataset.getName().equals("properties") ) {
                        List<Element> propertyList = dataset.getChildren("property_group");
                        // If this has already been done there will be "property" element children.  Don't do it again.
                        if ( propertyList == null || propertyList.size() <= 0 ) {
                            dataset.setContent(LASDocument.convertProperties(dataset));
                        }
                    }
                }
            } else if ( child.getName().equalsIgnoreCase("grids")) {
                List grids = child.getChildren();
                for (Iterator gridsIt = grids.iterator(); gridsIt.hasNext();) {
                    Element grid = (Element) gridsIt.next();
                    // If it has no ID assume it needs to be converted.  Otherwise ignore it.
                    if ( grid.getAttributeValue("ID") == null ) {
                        String GID = grid.getName();
                        if ( !gridsProcessed.contains(GID) ) {
                            gridsProcessed.add(GID);
                        } else {
                            duplicateGrids.add(GID);
                        }
                        grid.setName("grid");
                        grid.setAttribute("ID", GID);
                        List axesrefs = grid.getChildren();
                        for (Iterator axisIt = axesrefs.iterator(); axisIt.hasNext();) {
                            Element axis = (Element) axisIt.next();
                            String AID = axis.getAttributeValue("match");
                            axis.setName("axis");
                            String[] parts = AID.split("/");
                            AID = parts[3];
                            axis.setAttribute("IDREF", AID);
                            axis.removeAttribute("match");
                        }
                    } else {
                        if ( !gridsProcessed.contains(grid.getAttributeValue("ID") ) ) {
                            gridsProcessed.add(grid.getAttributeValue("ID"));
                        } else {
                            duplicateGrids.add(grid.getAttributeValue("ID"));
                        }
                    }
                }
            } else if (child.getName().equalsIgnoreCase("axes")) {
                List axes = child.getChildren();
                for (Iterator axesIt = axes.iterator(); axesIt.hasNext();) {
                    Element axis = (Element) axesIt.next();
                    // If it does not have an ID assume it needs to be converted. Otherwise, ignore it.
                    if ( axis.getAttributeValue("ID") == null ) {
                        if ( !axis.getName().equals("properties")) {
                            String AID = axis.getName();
                            if ( !axesProcessed.contains(AID) ) {
                                axesProcessed.add(AID);
                            } else {
                                duplicateAxes.add(AID);
                            }
                            axis.setName("axis");
                            axis.setAttribute("ID", AID);
                            if ( axis.getAttributeValue("type").equals("t")  || axis.getAttributeValue("type").equals("f") ) {
                                addTimeAxisAttributes(axis);
                            }
                            List v = axis.getChildren("v");
                            boolean warn = false;
                            if ( v != null && axis.getAttributeValue("type").equals("t") ) {
                                for (Iterator vIt = v.iterator(); vIt.hasNext();) {
                                    Element ve = (Element) vIt.next();
                                    String value = ve.getTextTrim();
                                    if ( value.equalsIgnoreCase("jan") || value.equalsIgnoreCase("feb") ||
                                            value.equalsIgnoreCase("mar") || value.equalsIgnoreCase("apr") ||
                                            value.equalsIgnoreCase("may") || value.equalsIgnoreCase("jun") ||
                                            value.equalsIgnoreCase("jul") || value.equalsIgnoreCase("aug") ||
                                            value.equalsIgnoreCase("sep") || value.equalsIgnoreCase("oct") ||
                                            value.equalsIgnoreCase("nov") || value.equalsIgnoreCase("dec") ) {

                                        ve.setAttribute("label", value);
                                        ve.setText("15-"+value);
                                        warn = true;
                                    }
                                }
                                if ( warn ) {
                                    log.warn("Converted <v>Jan</v> syntax to <v label=\"Jan\">15-Jan</v> syntax for axis "+AID);
                                }
                            }
                        }
                    } else {
                        if ( axis.getAttributeValue("type").equals("t") || axis.getAttributeValue("type").equals("f") ) {
                            addTimeAxisAttributes(axis);
                        }
                        if ( !axesProcessed.contains(axis.getAttributeValue("ID")) ) {
                            axesProcessed.add(axis.getAttributeValue("ID"));
                        } else {
                            duplicateAxes.add(axis.getAttributeValue("ID"));
                        }
                    }
                }
            } else if ( child.getName().equalsIgnoreCase("properties")) {
                List<Element> propertyList = child.getChildren("property_group");
                // If this has already been done there will be "property" element children.  Don't do it again.
                if ( propertyList == null || propertyList.size() <= 0 ) {
                    child.setContent(LASDocument.convertProperties(child));
                }
            } else if ( child.getName().equalsIgnoreCase("las_categories") ) {
                List categories = child.getChildren("category");
                setIDs(categories);
            }

        }
        Element serversE = root.getChild("las_servers");
        if ( serversE != null ) {
            List las_serverElements = serversE.getChildren("las_server");
            for (Iterator lsIt = las_serverElements.iterator(); lsIt.hasNext();) {
                Element las_server = (Element) lsIt.next();
                String url = las_server.getAttributeValue("url");
                if ( url == null ) {
                    log.warn("<las_server> configured without a url attribute.");
                } else {
                    String ID = las_server.getAttributeValue("ID");
                    if ( ID == null ) {
                        ID = JDOMUtils.MD5Encode(url);
                        las_server.setAttribute("ID", ID);
                    }
                }
            }
        }
        // Remove duplicates.
        for (Iterator dupsIt = duplicateDatasets.iterator(); dupsIt.hasNext();) {
            String ID = (String) dupsIt.next();
            Element dup = getDatasetElement(ID);
            Element parent = dup.getParentElement();
            Element grandparent = parent.getParentElement();
            parent.removeContent(dup);
            if ( parent.getChildren().size() == 0 ) {
                grandparent.removeContent(parent);
            }
            log.warn("Removed duplicate data set with ID="+ID);
        }
        for (Iterator dupsGit = duplicateGrids.iterator(); dupsGit.hasNext(); ) {
            String ID = (String) dupsGit.next();
            Element dup = getGridElement(ID);
            Element parent = dup.getParentElement();
            Element grandparent = parent.getParentElement();
            parent.removeContent(dup);
            log.warn("Removed duplicate grid with ID="+ID);
            if ( parent.getChildren().size() == 0 ) {
                grandparent.removeContent(parent);
            }
        }
        for (Iterator dupsAit = duplicateAxes.iterator(); dupsAit
                .hasNext();) {
            String ID = (String) dupsAit.next();
            Element dup = getAxisElement(ID);
            Element parent = dup.getParentElement();
            Element grandparent = parent.getParentElement();
            parent.removeContent(dup);
            log.warn("Removed duplicate axis with ID="+ID);
            if ( parent.getChildren().size() == 0 ) {
                grandparent.removeContent(parent);
            }
        }
    }
    /**
     * Converts to XML that can be validated against a schema, or returns if it detects that XML is already "Version 7".
     * @throws JDOMException
     * @throws UnsupportedEncodingException
     * @throws LASException 
     *
     */
    public void convertToSeven() throws JDOMException, UnsupportedEncodingException, LASException {
        convertToSeven(false);
    }
    /**
     * Helper method to recursively extract the options.
     * @throws JDOMException
     */
    public ArrayList<Option> extractOptions (String optionID) throws JDOMException {
        ArrayList<Option> options = new ArrayList<Option>();
        Element optiondef = getElementByXPath("/lasdata/lasui/options/optiondef[@name='"+optionID+"']");
        List definedOptions = optiondef.getChildren("option");
        // Collect the options that are defined inside this optiondef, if any.
        Option opB = null;
        if ( definedOptions != null ) {
            for (Iterator doIt = definedOptions.iterator(); doIt.hasNext();) {
                Element opt = (Element) doIt.next();
                opB = new Option((Element)opt.clone());
                options.add(opB);
            }
        }
        // These options are defined by inheritence from other optiondef elements.
        String inherit = optiondef.getAttributeValue("inherit");
        if ( inherit != null ) {
            if ( inherit.contains(",")) {
                String[] inheritedOptionsIDs = optiondef.getAttributeValue("inherit").split(",");
                for (int i = 0; i < inheritedOptionsIDs.length; i++) {
                	String option_name = inheritedOptionsIDs[i].substring(1);
                    options.addAll(extractOptions(option_name));
                }
            } else {
                options.addAll(extractOptions(inherit.substring(1)));
            }
        }
        return options;
    }
    private static ArrayList<Option> extractInheritances(String optionsID) {
    	 ArrayList<Option> options = new ArrayList<Option>();
    	 return options;
    }
    /**
     * A filter to find a category by its ID
     * @param catid the ID to find
     * @return the category element
     * @throws JDOMException
     */
    private Element findCategory(String catid) throws JDOMException {
    	CategoryFilter filter = new CategoryFilter(catid);
    	Iterator catIt= getRootElement().getDescendants(filter);
    	if ( catIt != null && catIt.hasNext() ) {
    		return (Element) catIt.next();
    	} else {
    		return null;
    	}
    }
    /**
     * Get the base url of this LAS server (the server host, port and context path).
     * @return base url of the LAS server family.
     * @throws JDOMException
     */

    public String getBaseServerURL() throws JDOMException {
        Element ops = getElementByXPath("/lasdata/operations");
        String server = ops.getAttributeValue("url");
        if ( server != null && server.contains("ProductServer.do") ) {
            return server.substring(0, server.lastIndexOf("/"));
        } else {
            throw new JDOMException("No server URL found in the las.xml operations element.");
        }
    }
    /**
     * Get any applicable data constraints for a particular data set and variable.
     * @param dsID
     * @param varID
     * @return the list of constraints
     * @throws JDOMException
     */
    public ArrayList<DataConstraint> getConstraints(String dsID, String varID) throws JDOMException {
    	String ui_default = getUIDefaultName(dsID, varID);
    	ui_default = ui_default.substring(ui_default.indexOf("#")+1);
    	if ( ui_default != null && !ui_default.equals("") ) {
    		return getConstraints(ui_default, dsID, varID);
    	} else {
    		return new ArrayList<DataConstraint>();
    	}
    }
    /**
     * Get any applicable data constraints for a particular data set and variable 
     * as a HashMap keyed on the constraint ID.
     * @param dsID
     * @param varID
     * @return the HashMap of constraints
     * @throws JDOMException
     */
    public HashMap<String, DataConstraint> getConstraintsHash(String dsID, String varID) throws JDOMException {
    	ArrayList<DataConstraint> constraintsList = getConstraints(dsID, varID);
    	HashMap<String, DataConstraint> constraints = new HashMap<String, DataConstraint>(constraintsList.size());
    	for (DataConstraint val : constraintsList) {
    		constraints.put(val.getId(), val);
    	}
    	return constraints;
    }
    /**
     * Get any constraints from the named UI default.
     *
     * @param ui_default
     * @return the constraints
     * @throws JDOMException
     */
    public ArrayList<DataConstraint> getConstraints(String ui_default, String dsID, String varID) throws JDOMException {
    	ArrayList<DataConstraint> constraints = new ArrayList<DataConstraint>();
    	Element def = getUIDefault(ui_default);
    	Element op = getUIMap(def, "ops");
    	List cons = op.getChildren("constraint");
    	for (Iterator consIt = cons.iterator(); consIt.hasNext();) {
    		// Get the reference to the constraint...
			Element constraint = (Element) consIt.next();

		    String type = constraint.getAttributeValue("type");
		    String name = constraint.getAttributeValue("name");
		    String ID = constraint.getAttributeValue("ID");
		    if ( type.equals("variable") ) {
		    	DataConstraint vc = getVariableConstraint(dsID, varID);

		    	if ( name != null ) {
		    	    vc.setName(name);
		    	} else {
		    		vc.setName("variable");
		    	}

		    	if ( ID != null ) {
		    		vc.setID(ID);
		    	}
		    	constraints.add(vc);
		    } else {
		    	// Build the constraint...
		    	Element full_constraint = new Element("constraint");
		    	if ( name != null ) {
		    	    full_constraint.setAttribute("name", name);
		    	}
		    	if ( ID != null ) {
		    		full_constraint.setAttribute("ID", ID);
		    	}
		    	full_constraint.setAttribute("type", "menu");
		    	// First copy the attributes...
		    	List attrs = constraint.getAttributes();
		    	for (Iterator attIt = attrs.iterator(); attIt.hasNext();) {
					Attribute attr = (Attribute) attIt.next();
					full_constraint.setAttribute(attr.getName(), attr.getValue());
				}
		    	// Then follow the references to get the three parts, left-hand side menu, operations, right-hand side menu
		    	List menus = constraint.getChildren("menu");
		    	int it = 0;
		    	for (Iterator menuIt = menus.iterator(); menuIt.hasNext();) {
					Element menu_ref = (Element) menuIt.next();
					String href = menu_ref.getAttributeValue("href");
					href = href.substring(1, href.length());
					Element menu = getUIMenu(href);
					Element menu_clone = (Element) menu.clone();
					if ( it == 0 ) {
						menu_clone.setAttribute("position", "lhs");
					} else if ( it == 1 ) {
						menu_clone.setAttribute("position", "ops");
					} else if ( it == 2 ) {
						menu_clone.setAttribute("position", "rhs");
					}
					full_constraint.addContent(menu_clone);
					it++;
				}
		    	constraints.add(new DataConstraint(full_constraint));
		    }
		}
    	return constraints;
    }
    /**
     * Get any constraints from the named UI default 
     * as a HashMap keyed on the constraint ID.
     *
     * @param ui_default
     * @return the HashMap of constraints
     * @throws JDOMException
     */
    public HashMap<String, DataConstraint> getConstraintsHash(String ui_default, String dsID, String varID) throws JDOMException {
    	ArrayList<DataConstraint> constraintsList = getConstraints(ui_default, dsID, varID);
    	HashMap<String, DataConstraint> constraints = new HashMap<String, DataConstraint>(constraintsList.size());
    	for (DataConstraint val : constraintsList) {
    		constraints.put(val.getId(), val);
    	}
    	return constraints;
    }
    /**
     * Build a variable constraint from a particular variable
     * @param dsID the data set ID of the variable
     * @param varID the variable id
     * @return the data constraint for this variable
     * @throws JDOMException
     */
    public DataConstraint getVariableConstraint(String dsID, String varID) throws JDOMException {
    	Element constraint = new Element("constraint");
    	constraint.setAttribute("type", "variable");
    	ArrayList<Variable> vars = getVariables(dsID);
    	Element menu = new Element("menu");
    	menu.setAttribute("position", "lhs");
    	menu.setAttribute("type", "constraint");
		menu.setAttribute("name","variable_"+dsID);
    	for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
			Variable var = (Variable) varIt.next();
			Element item = new Element("item");
			item.setAttribute("values", var.getID());
			item.setText(var.getName());
			menu.addContent(item);
		}
    	constraint.addContent(menu);
    	menu = new Element("menu");
    	menu.setAttribute("position", "ops");
    	menu.setAttribute("type", "constraint");
		menu.setAttribute("name","variable_"+dsID);

		Element item = new Element("item");
    	item.setAttribute("values",">=");
    	item.setText(">=");
    	menu.addContent(item);

    	item = new Element("item");
    	item.setAttribute("values",">");
    	item.setText(">");
    	menu.addContent(item);

		item = new Element("item");
    	item.setAttribute("values","=");
    	item.setText("=");
    	menu.addContent(item);

    	item = new Element("item");
    	item.setAttribute("values","!=");
    	item.setText("!=");
    	menu.addContent(item);

    	item = new Element("item");
    	item.setAttribute("values","<");
    	item.setText("<");
    	menu.addContent(item);

    	item = new Element("item");
    	item.setAttribute("values","<=");
    	item.setText("<=");
    	menu.addContent(item);

    	constraint.addContent(menu);

    	return new DataConstraint(constraint);
    }
    public CategorySerializable[] getCategorySerializable(ArrayList<Category> categories) throws LASException, JDOMException {
    	CategorySerializable[] cats = new CategorySerializable[categories.size()];
    	int i=0;
    	for (Iterator catIt = categories.iterator(); catIt.hasNext();) {
    		Category cat = (Category) catIt.next();
    		CategorySerializable wireCat = cat.getCategorySerializable();
    		if ( wireCat.hasMultipleDatasets() ) {
    			DatasetSerializable[] ds = wireCat.getDatasetSerializableArray();
    			for (int j = 0; j < ds.length; j++) {
					VariableSerializable[] wireVars = ds[j].getVariablesSerializable();
					for (int k = 0; k < wireVars.length; k++) {
    					VariableSerializable var = wireVars[k];
    					Grid grid = getGrid(var.getDSID(), var.getID());
    					var.setGrid(grid.getGridSerializable());
    				}
				}

    		} else {
    			if ( wireCat.isVariableChildren() ) {
    				VariableSerializable[] wireVars = wireCat.getDatasetSerializable().getVariablesSerializable();
    				//TODO Sort here on an attribute??
    				for (int j = 0; j < wireVars.length; j++) {
    					VariableSerializable var = wireVars[j];
    					Grid grid = getGrid(var.getDSID(), var.getID());
    					var.setGrid(grid.getGridSerializable());
    				}
    			}
    		}
    		cats[i] = wireCat;

    		i++;
    	}
    	return cats;
    }
    /**
     * Get the categories directly below this id.  If the id is null get the top.
     * @param catid
     * @return
     * @throws JDOMException
     * @throws LASException
     */
    public ArrayList<Category> getCategories(String catid) throws JDOMException, LASException {
        ArrayList<Category> categories = new ArrayList<Category>();
        List tops = getRootElement().getChildren("las_categories");
        if ( catid == null ) {
            // Get the top level categories...

            if ( tops != null && tops.size() > 0 ) {
                for (Iterator topIt = tops.iterator(); topIt.hasNext();) {
                    Element las_categoryE = (Element)((Element) topIt.next()).clone();
                    List cats = (List) las_categoryE.getChildren("category");
                    for (Iterator catIt = cats.iterator(); catIt.hasNext();) {
                        Element category = (Element) ((Element) catIt.next()).clone();
                        Element category_container = new Element("category");
                        List attributes = category.getAttributes();
                        for (Iterator attrIt = attributes.iterator(); attrIt.hasNext();) {
                            Attribute attr = (Attribute) attrIt.next();
                            category_container.setAttribute(attr.getName(), attr.getValue());
                        }
                        // Set the "chidren" attribute to identify what kind of kids it has.
                        if ( category.getChild("filter") != null ) {
                        	Element filter = category.getChild("filter");
                        	List<Dataset> datasets = getDataset(filter);
                        	if ( datasets != null && datasets.size() > 0 ) {
                        		Dataset dataset = datasets.get(0);
                        		dataset.setAttribute("catid", category_container.getAttributeValue("ID"));
                        	    category_container.setAttribute("children_dsid", dataset.getAttributeValue("ID"));
                        	    if ( dataset.getAttributeValue("doc") != null &&  !dataset.getAttributeValue("doc").equals("")) {
                        	    	category_container.setAttribute("doc", dataset.getAttributeValue("doc"));
                        	    }
                        	}
                            category_container.setAttribute("children", "variables");
                        } else {
                            if ( category.getChild("category") != null ) {
                                category_container.setAttribute("children", "categories");
                            } else {
                                category_container.setAttribute("children", "none");
                            }
                        }
                        if ( category_container.getAttributeValue("children") != null && !category_container.getAttributeValue("children").equals("none") ) {
                            categories.add(new Category(category_container));
                        }
                    }
                }
            } else {
            	categories = getDatasetsAsCategories(false);
            }
        } else {
            // There are categories in the config, use them...
            if ( tops != null && tops.size() > 0 ) {
                // Either the category has other categories as children...

                // When a category has categories as children, we need to only include
                // the next level down.  Therefore we copy the attributes and add
                // the child categories without the grandchildren.
                Element category = findCategory(catid);
                if ( category == null ) {
                	// return the empty list
                	return categories;
                }
                Element category_container = new Element("category");
                List attributes = category.getAttributes();
                for (Iterator attrIt = attributes.iterator(); attrIt.hasNext();) {
                    Attribute attr = (Attribute) attrIt.next();
                    category_container.setAttribute(attr.getName(), attr.getValue());
                }
                List cats = (List) category.getChildren("category");
                if ( cats.size() > 0 ) {
                    for (Iterator catsIt = cats.iterator(); catsIt.hasNext();) {
                        Element cat = (Element) catsIt.next();
                        Element cat_nokids = new Element("category");
                        List cat_attr = cat.getAttributes();
                        for (Iterator attrIt = cat_attr.iterator(); attrIt.hasNext();) {
                            Attribute attr = (Attribute) attrIt.next();
                            cat_nokids.setAttribute(attr.getName(), attr.getValue());
                        }
                        List filters = cat.getChildren("filter");
                        if ( filters.size() > 0 ) {
                        	Element filter = (Element)filters.get(0);
                        	List<Dataset> datasets = getDataset(filter);
                        	if ( datasets != null && datasets.size() > 0 ) {
                        		// TODO What if there is more than one data set here???
                        		Dataset dataset = datasets.get(0);
                        		// Add only if the filter returns a dataset.  It's possible to create
                        		// a filter that returns an empty list.
                        	    cat_nokids.setAttribute("children_dsid", dataset.getAttributeValue("ID"));
                        	    dataset.setAttribute("catid", cat_nokids.getAttributeValue("ID"));
                        	    if ( dataset.getAttributeValue("doc") != null && !dataset.getAttributeValue("doc").equals("") ) {
                        	    	cat_nokids.setAttribute("doc", dataset.getAttributeValue("doc"));
                        	    }
                        	}
                            cat_nokids.setAttribute("children", "variables");
                        } else {
                            if ( cat.getChild("category") != null ) {
                                cat_nokids.setAttribute("children", "categories");
                            } else {
                                cat_nokids.setAttribute("children", "none");
                            }
                        }
                        if ( cat_nokids.getAttributeValue("children") != null && !cat_nokids.getAttributeValue("children").equals("none") ) {
                            categories.add(new Category(cat_nokids));
                        }
                    }
                }

                // or it has variables...
                if ( category.getChild("filter") != null ) {
                    List filters = category.getChildren("filter");
                    for (Iterator filterIt = filters.iterator(); filterIt.hasNext();) {
                        Element filter = (Element) filterIt.next();
                        /*
                         * The rules say that all of the variables caught by a filter
                         * must come from the same dataset.  I think the best way to
                         * represent this collection of information is via a dataset
                         * element the filtered list of variables.  Therefore this
                         * is a getDataset call...
                         */
                        List<Dataset> datasets = getDataset(filter);
                        // If no data sets match, don't add the category... prevent the endless category.
                        if ( datasets.size() > 0 ) {
                            for ( int i = 0; i < datasets.size(); i++ ) {
                                Dataset dataset = datasets.get(i);
                                dataset.setAttribute("catid", category_container.getAttributeValue("ID"));
                                if ( dataset != null && dataset.getVariables().size() > 0) {
                                    category_container.addContent(dataset.getElement());
                                    category_container.setAttribute("children", "variables");
                                } else if ( dataset != null && dataset.getElement().getChild("variables") != null ) {
                                    // Somebody put in a data set with no variables.  Why?  I don't know, but they did.
                                    category_container.addContent(dataset.getElement());
                                    category_container.setAttribute("children", "variables");
                                }
                            }
                            categories.add(new Category(category_container));
                        }
                    }
                }
            } else {
                // This config has no "categories", just datasets and variables.  Use them.
                Element dataset = getDatasetElement(catid);
                Element container_dataset;
                Element category = new Element("category");
                if ( dataset != null ) {
                    container_dataset = (Element) dataset.clone();
                    category.setAttribute("name", container_dataset.getAttributeValue("name"));
                    category.setAttribute("children", "variables");
                    category.setAttribute("children_dsid", container_dataset.getAttributeValue("ID"));
                    category.setAttribute("ID", dataset.getAttributeValue("ID"));
                    container_dataset.setAttribute("catid", category.getAttributeValue("ID"));
                    if ( dataset.getAttributeValue("doc") != null && !dataset.getAttributeValue("doc").equals("") ) {
                    	category.setAttribute("doc", dataset.getAttributeValue("doc"));
                    }
                } else {
                    // send an empty one
                    container_dataset = new Element("dataset");
                }
                category.addContent(container_dataset);
                if ( dataset != null ) {
                    if ( category.getAttribute("name") == null && dataset.getAttributeValue("name") != null ) {
                        category.setAttribute("name", dataset.getAttributeValue("name"));
                    }
                    if ( category.getAttribute("ID") == null && dataset.getAttributeValue("ID") != null ) {
                        category.setAttribute("ID", dataset.getAttributeValue("ID"));
                    }
                    categories.add(new Category(category));
                }
            }
            
        }
        return categories;
    }
    /**
     * Get data access URL.  If fds is set to true, then the FDS URL will be
     * returned in every case.  If the fds boolean is set to false, then the
     * actual OPeNDAP URL of the remote data set will be returned where available.
     * If no remote URL is available, the the FDS URL will be returned.
     * @param xpath the XPath of the variable
     * @param fds true if FDS URL is required
     * @throws JDOMException
     * @throws LASException
     */
    public String getDataAccessURL(String xpath, boolean fds) throws LASException, JDOMException {
        String url = "";
        String dataObjectURL = getDataObjectURL(xpath);
        if (dataObjectURL == null || dataObjectURL.equals("")) {
            return url;
        }
        // If this is a local data set then fds must be set to true.
        if ( !dataObjectURL.startsWith("http")) {
            fds = true;
        }
        if (fds) {
            url = getFTDSURL(xpath);
        } else {
            url = dataObjectURL;
        }
        return url;
    }
    /**
     * Returns a URL for an LAS data set that will show something intelligible in the browser.
     * @param xpath
     * @param fds
     * @return
     * @throws LASException
     * @throws JDOMException
     */
    public String getDataAccessBrowserURL(String xpath, boolean fds) throws LASException, JDOMException {
    	String link = getDataAccessURL(xpath, fds);
    	if ( link.contains("iridl.ldeo") ) {
    		return link;
    	} else {
    		return link+".html";
    	}
    }
    /**
     * Get data access URL.  If fds is set to true, then the FDS URL will be
     * returned in every case.  If the fds boolean is set to false, then the
     * actual OPeNDAP URL of the remote data set will be returned where available.
     * If no remote URL is available, the the FDS URL will be returned.
     * @param dsID the Dataset ID
     * @param varID the Variable ID
     * @param fds true if FDS URL is required
     * @throws JDOMException
     * @throws LASException
     */
    public String getDataAccessURL(String dsID, String varID, boolean fds) throws LASException, JDOMException {
        return getDataAccessURL("/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']", fds);
    }
    /**
     * Return a link for an LAS data set that will return something itelligble to the browser.
     * @param dsID
     * @param varID
     * @param fds
     * @return
     * @throws LASException
     * @throws JDOMException
     */
    public String getDataAccessBrowserURL(String dsID, String varID, boolean fds) throws LASException, JDOMException {
        String link = getDataAccessURL("/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']", fds);
        if ( link.contains("iridl.ldeo") ) {
        	return link;
        } else {
        	return link+".html";
        }
    }
    /**
     * Returns the data access URL for a particular variable via the XPath of the variable (strips off the #var)
     * @param xpathValue The XPath of the variable
     * @return url The access ready URL OPeNDAP URL or filename.
     * @throws JDOMException
     * @throws LASException
     */
    public String getDataObjectURL(Element variable) throws LASException, JDOMException {
        String url = getFullDataObjectURL(variable);
        if ( url.contains("#")) {
            url = url.substring(0,url.indexOf("#"));
        }
        return url;
    }
    /**
     * Returns the OPeNDAP ready data access URL for a particular variable via the XPath of the variable (strips off the #var)
     * @param xpathValue The XPath of the variable
     * @return url The access ready URL OPeNDAP URL or filename.
     * @throws JDOMException
     * @throws LASException
     */
    public String getDataObjectURL(String xpathValue) throws LASException, JDOMException {
        String url = getFullDataObjectURL(xpathValue);
        if ( url.contains("#")) {
            url = url.substring(0,url.indexOf("#"));
        }
        return url;
    }
    /**
	 * Return a list of attributes on the given element
	 * @param xpathValue The XPath to the element
	 * @return attribute The list of JDOM attributes on this element.
	 * @throws JDOMException
	 */
	public List getDataOjectAttributes(String xpathValue) throws JDOMException {
	    /*
	     * We know this is a variable XPath.  If it's "old style" fix it.
	     */
	    if (!xpathValue.contains("@ID")) {
	        String[] parts = xpathValue.split("/");
	        // Throw away index 0 since the string has a leading "/".
	        xpathValue = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
	    }
	    Element variable = getElementByXPath(xpathValue);
	    return variable.getAttributes();
	}
	public void addEnsembleIDs() throws JDOMException, LASException, HttpException, IOException {
		Element root = getRootElement();
		String version = root.getAttributeValue("version");
		if ( version != null && !version.contains("7.")) {
			throw new LASException("XML is not version 7.0 or above.  Try convertToSeven() first.");
		}
		List datasets = getRootElement().getChildren("datasets");
		for (Iterator datasetsIt = datasets.iterator(); datasetsIt.hasNext();) {
			Element datasetsE = (Element) datasetsIt.next();
			List datasetElements = datasetsE.getChildren("dataset");
			for (Iterator datasetIt = datasetElements.iterator(); datasetIt.hasNext();) {
				Element datasetE = (Element) datasetIt.next();
				Element variablesE = datasetE.getChild("variables");
				if ( variablesE != null ) {
					List variableElements = variablesE.getChildren("variable");
					for (Iterator variableIt = variableElements.iterator(); variableIt.hasNext();) {
						Element variable = (Element) variableIt.next();
						String grid_id = variable.getChild("grid").getAttributeValue("IDREF");
						Element grid = getElementByXPath("/lasdata/grids/grid[@ID='"+grid_id+"']");
						List axes = grid.getChildren("axis");
						for (Iterator axesIt = axes.iterator(); axesIt.hasNext();) {
							Element axis_ref = (Element) axesIt.next();
							String axis_id = axis_ref.getAttributeValue("IDREF");
							Element axis = getElementByXPath("/lasdata/axes/axis[@ID='"+axis_id+"']");
							if ( axis.getAttributeValue("type").equals("e") ) {
								String short_name = getVariableName(datasetE.getAttributeValue("ID"), variable.getAttributeValue("ID"));
								List members = axis.getChildren("member");
								for (Iterator membersId = members.iterator(); membersId.hasNext();) {
									Element member = (Element) membersId.next();
									String id = member.getAttributeValue("IDREF");
									if ( id == null ) {
										String url = member.getAttributeValue("url");
										id = getIDs(url);
									}
									Element ensembleDataset = getElementByXPath("/lasdata/datasets/dataset[@ID='"+id+"']");
									Element ensemble_variables = ensembleDataset.getChild("variables");
									if ( ensemble_variables != null ) {
										List ensemble_variablesE = ensemble_variables.getChildren("variable");
										for (Iterator ensembleVariablesIt = ensemble_variablesE.iterator(); ensembleVariablesIt.hasNext();) {
											Element ensemble_variable = (Element) ensembleVariablesIt.next();
											String ensemble_short_name = getVariableName(id, ensemble_variable.getAttributeValue("ID"));
										    if ( short_name.equals(ensemble_short_name) ) {
										    	Element var = new Element("variable");
												var.setAttribute("IDREF", ensemble_variable.getAttributeValue("ID"));
												var.setAttribute("short_name", short_name);
												var.setAttribute("name", variable.getAttributeValue("name"));
												member.addContent(var);
										    }
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
    /**
     * Extract data sets based on a &lt;filter&gt; element from the config.   A filter might return data from more that one dataset.
     * @param filter A category filter element to be used to select variables from the configuration.
     * @return the data set that matches the filter
     */
    private List<Dataset> getDataset(Element filter) {
    	List<Dataset> dataset_list = new ArrayList<Dataset>();
        Dataset container_dataset = null;
        String action = filter.getAttributeValue("action");

        String name_contains = filter.getAttributeValue("contains");
        String name_equals = filter.getAttributeValue("equals");
        if ( name_equals == null ) {
            name_equals = filter.getAttributeValue("name-equals");
        }
        String tag_contains = filter.getAttributeValue("contains-tag");
        String tag_equals = filter.getAttributeValue("equals-tag");

        if ( action.equals("apply-dataset") ) {

            List datasets = getRootElement().getChildren("datasets");
            for (Iterator datasetsIt = datasets.iterator(); datasetsIt.hasNext();) {
                Element datasetsE = (Element) datasetsIt.next();
                List memberDatasets = datasetsE.getChildren("dataset");
                for (Iterator memberDSIt = memberDatasets.iterator(); memberDSIt.hasNext();) {
                    Element dataset = (Element) memberDSIt.next();
                    Element container_dataset_element = (Element) dataset.clone();
                    String name = dataset.getAttributeValue("name");
                    String ID = dataset.getAttributeValue("ID");
                    if ( (name_contains != null && name.contains(name_contains)) ||
                         (name_equals != null && name.equals(name_equals)) ||
                         (tag_contains != null && ID.contains(tag_contains)) ||
                         (tag_equals != null && ID.equals(tag_equals)) ) {
                        container_dataset = new Dataset(container_dataset_element);
                        dataset_list.add(container_dataset);
                    }
                }
            }
        } else if ( action.equals("apply-variable") ) {
            List datasets = getRootElement().getChildren("datasets");
            Element container_dataset_element = null;
            for (Iterator datasetsIt = datasets.iterator(); datasetsIt.hasNext();) {

                Element datasetsE = (Element) datasetsIt.next();
                List memberDatasets = datasetsE.getChildren("dataset");

                for (Iterator memberDSIt = memberDatasets.iterator(); memberDSIt.hasNext();) {
                    Element dataset = (Element) memberDSIt.next();

                    	container_dataset_element = (Element) dataset.clone();
                    	container_dataset_element.removeChildren("variables");

                        Element varsE = dataset.getChild("variables");
                        
                        List memberVariables = new ArrayList<Element>();
                        if ( varsE != null ) {
                            memberVariables = varsE.getChildren("variable");
                        }
                        Element varsElementContainer = new Element("variables");
                        boolean hasVariable = false;
                        for (Iterator varIt = memberVariables.iterator(); varIt.hasNext();) {
                            Element variable = (Element) varIt.next();
                            Element container_variable = (Element) variable.clone();
                            String name = variable.getAttributeValue("name");
                            String ID = variable.getAttributeValue("ID");
                            if ( (name_contains != null && name.contains(name_contains)) ||
                                 (name_equals != null && name.equals(name_equals)) ||
                                 (tag_contains != null && ID.contains(tag_contains)) ||
                                 (tag_equals != null && ID.equals(tag_equals)) ) {
                            	 varsElementContainer.addContent(container_variable);
                                 hasVariable = true;
                        }
                    }
                    if ( hasVariable ) {
                        container_dataset_element.addContent(varsElementContainer);
                        container_dataset = new Dataset(container_dataset_element);
                        dataset_list.add(container_dataset);
                    }
                }

            }
        }
        return dataset_list;
    }

    /**
	 * Get all of the attributes from the parent data set element.
	 * @param varXPath the variable whose parent data set will be used
	 * @return the attributes
	 * @throws JDOMException
	 */
	public HashMap <String, String> getDatasetAttributes(String varXPath) throws JDOMException {
	    HashMap<String, String> attrs = new HashMap<String, String>();
	    if (!varXPath.contains("@ID")) {
	        String[] parts = varXPath.split("/");
	        // Throw away index 0 since the string has a leading "/".
	        varXPath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
	    }

	    Element variable = getElementByXPath(varXPath);
	    Element dataset = variable.getParentElement().getParentElement();
	    List attributes = dataset.getAttributes();
	    for (Iterator iter = attributes.iterator(); iter.hasNext();) {
	        Attribute attr = (Attribute) iter.next();
	        String name = attr.getName();
	        String value = attr.getValue();
	        if ( name != null && value != null && !name.equals("") && !value.equals("") ) {
	           attrs.put(name, value );
	        }
	    }
	    return attrs;
	}
    /**
     * Get the &lt;dataset&gt; element from this config that matches this ID
     * @param dsid
     * @return
     * @throws JDOMException
     */
    private Element getDatasetElement(String dsid) throws JDOMException {
        String xPath = "/lasdata/datasets/dataset[@ID='"+dsid+"']";
        return getElementByXPath(xPath);
    }
    private Element getGridElement(String gid) throws JDOMException {
    	String xPath = "/lasdata/grids/grid[@ID='"+gid+"']";
    	return getElementByXPath(xPath);
    }
    private Element getAxisElement(String aid) throws JDOMException {
    	String xPath = "/lasdata/axes/axis[@ID='"+aid+"']";
    	return getElementByXPath(xPath);
    }
    /**
     * Returns all the datasets as gov.noaa.pmel.tmap.las.util.Dataset objects.
     * @return ArrayList of dataset objects
     * @throws LASException
     * @throws JDOMException
     */
    public ArrayList<Category> getDatasetsAsCategories(boolean full) throws JDOMException, LASException {
        ArrayList<Category> datasets = new ArrayList<Category>();
        Element datasetsE = getDatasetsAsElement();
        List datasetElements = datasetsE.getChildren("dataset");
        for (Iterator dsIt = datasetElements.iterator(); dsIt.hasNext();) {
            Element dataset = (Element) dsIt.next();
            Element ds_novars = (Element)dataset.clone();
            ds_novars.setName("category");

            if ( full ) {
            	ArrayList<Variable> variables = getFullVariables(dataset.getAttributeValue("ID"));
            	ds_novars.getChild("variables").removeChildren("variable");
            	for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
					Variable variable = (Variable) varIt.next();
					ds_novars.getChild("variables").addContent((Element)variable.getElement().clone());
				}
            } else {
            	/*
                 * For, the JavaScript UI, we want to strip out the children.
                 * we'll remove every things that's not properties.  However, we will
                 * keep the contributor and documentation elements.
                 */
            	List children = ds_novars.getChildren();
            	ArrayList<String> remove = new ArrayList<String>();
            	for (Iterator childIt = children.iterator(); childIt.hasNext();) {
            		Element child = (Element) childIt.next();
            		if (!child.getName().equals("properties") &&
            				!child.getName().equals("documentation") &&
            				!child.getName().equals("contributor") ) {
            			remove.add(child.getName());
            		}
            	}
            	for (int i=0; i < remove.size(); i++) {
            		ds_novars.removeChild(remove.get(i));
            	}
            }
            ds_novars.setAttribute("children", "variables");
        	ds_novars.setAttribute("children_dsid", ds_novars.getAttributeValue("ID"));
        	if ( ds_novars.getAttributeValue("doc") != null && !ds_novars.getAttributeValue("doc").equals("") ) {
        		ds_novars.setAttribute("doc", ds_novars.getAttributeValue("doc"));
        	}
        	ds_novars.setAttribute("catid", ds_novars.getAttributeValue("ID"));
        	Category ds = new Category(ds_novars);
        	
        	datasets.add(ds);
        }
        return datasets;
    }
    public ArrayList<Dataset> getFullDatasets() throws JDOMException, LASException {
    	return getDatasets(true);
    }
    public ArrayList<Dataset> getDatasets() throws JDOMException, LASException {
    	return getDatasets(false);
    }
    public ArrayList<Dataset> getDatasets(boolean full, int start, int end, String url) throws JDOMException, LASException {
        ArrayList<Dataset> datasets = new ArrayList<Dataset>();
        Element datasetsE = getDatasetsAsElement();
        
        List datasetElements = datasetsE.getChildren("dataset");
        if ( start > datasetElements.size() && end > datasetElements.size() ) {
            // We've run off the end...
            return datasets;
        }
        if ( start <= 0 && end <= 0 ) {
            start = 0;
            end = datasetElements.size();
        } else {
            end = Math.min(end, datasetElements.size());
        }
        
        for (int index = start; index < end; index++) {

        	Element dataset = (Element) datasetElements.get(index);
        	Element ds_novars = (Element)dataset.clone();

        	if ( full ) {
        		ArrayList<Variable> variables = getFullVariables(dataset.getAttributeValue("ID"));
        		ds_novars.getChild("variables").removeChildren("variable");
        		for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
        			Variable variable = (Variable) varIt.next();
        			ds_novars.getChild("variables").addContent((Element)variable.getElement().clone());
        		}
        	} else {
        		/*
        		 * For, the JavaScript UI, we want to strip out the children.
        		 * we'll remove every things that's not properties.  However, we will
        		 * keep the contributor and documentation elements.
        		 */
        		List children = ds_novars.getChildren();
        		ArrayList<String> remove = new ArrayList<String>();
        		for (Iterator childIt = children.iterator(); childIt.hasNext();) {
        			Element child = (Element) childIt.next();
        			if (!child.getName().equals("properties") &&
        					!child.getName().equals("documentation") &&
        					!child.getName().equals("contributor") ) {
        				remove.add(child.getName());
        			}
        		}
        		for (int i=0; i < remove.size(); i++) {
        			ds_novars.removeChild(remove.get(i));
        		}
        	}

        	Dataset ds = new Dataset(ds_novars);
        	ds.setAttribute("catid", ds.getID());
        	if ( url == null || full && url != null && getDataAccessURL(ds.getID(), ds.getVariables().get(0).getID(), false).contains(url) ) {
        		datasets.add(ds);
        	} 
        }
        return datasets;
    }
    public ArrayList<Dataset> getDatasets(boolean full, int start, int end) throws JDOMException, LASException {
    	return getDatasets(full, start, end, null);
    }
    /**
     * Returns all the datasets as gov.noaa.pmel.tmap.las.util.Dataset objects.
     * @return ArrayList of dataset objects
     * @throws LASException
     * @throws JDOMException
     */
    public ArrayList<Dataset> getDatasets(boolean full) throws JDOMException, LASException {
        
        return getDatasets(full, 0, 0);
        
        
    }
    /**
     * Return all datasets as a single "datasets" element.
     *
     */
    public Element getDatasetsAsElement() {
        Element datasets = new Element("datasets");
        List datasetsElements = getRootElement().getChildren("datasets");
        for (Iterator dseIt = datasetsElements.iterator(); dseIt.hasNext();) {
            Element dsets = (Element) dseIt.next();
            List datasetElements = dsets.getChildren("dataset");
            for (Iterator dsIt = datasetElements.iterator(); dsIt.hasNext();) {
                Element dataset = (Element) dsIt.next();
                datasets.addContent((Element)dataset.clone());
            }
        }
        return datasets;
    }
    /**
     * Get a &lt;dataset&gt; element base on the data set id
     * @param dsID the id of the dataset
     * @return the data set element
     * @throws JDOMException
     * @throws LASException
     */
    public Dataset getDataset(String dsID) throws JDOMException, LASException {
    	String xpathValue = "/lasdata/datasets/dataset[@ID='"+dsID+"']";
    	Element ads = (Element) getElementByXPath(xpathValue);
    	Dataset dataset;
    	if ( ads != null ) {
    	    Element ds = (Element) ads.clone();
    		dataset = new Dataset(ds);
    	} else {
    		return null;
    	}
    	ArrayList<Variable> variables = getVariables(dsID);
		ArrayList<Variable> clones = new ArrayList<Variable>();
		for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
			Variable var = (Variable) varIt.next();
			Variable clone_var = new Variable((Element) var.getElement().clone(), var.getDSID(), var.getDSID(), dataset.getName());
			clones.add(clone_var);
		}
		for (Iterator cloneIt = clones.iterator(); cloneIt.hasNext();) {
			Variable var = (Variable) cloneIt.next();
			Element varE = (Element) var.getElement();
			varE.removeChild("grid");  // Get rid of the old grid with just the IDREF and replace it with the grid and axes.
			Grid grid = getGrid(var.getDSID(), var.getID());
			varE.addContent((Element)grid.getElement().clone());
		}
		dataset.setVariables(clones);
		return dataset;
    }
    /**
     * !!!! returns datasets list This ignores categories for now.  Have to fix this.
     * Don't need this anymore
     * @deprecated
     */
    //TODO decide what to do about categories...
    public ArrayList<NameValuePair> getDatasetsAsNameValueBeans() {
        ArrayList <NameValuePair> datasets = new ArrayList<NameValuePair>();
        List datasetsElements = getRootElement().getChildren("datasets");
        for (Iterator dseIt = datasetsElements.iterator(); dseIt.hasNext();) {
            Element dsets = (Element) dseIt.next();
            List datasetElements = dsets.getChildren("dataset");
            for (Iterator dsIt = datasetElements.iterator(); dsIt.hasNext();) {
                Element dataset = (Element) dsIt.next();
                String name = dataset.getAttributeValue("name");
                String value = dataset.getAttributeValue("ID");
                datasets.add(new NameValuePair(name, value));
            }
        }
        return datasets;
    }
    /**
     * Get first variable from LASConfig.
     * @return The XPath of the first variable in the LASConfig (the default)
     */
    public String getFirstVariable() {
        Element dataset = getRootElement().getChild("datasets").getChild("dataset");
        String datasetID = dataset.getAttributeValue("ID");
        Element variable = dataset.getChild("variables").getChild("variable");
        String variableID = variable.getAttributeValue("ID");
        return "/lasdata/datasets/dataset[@ID='"+datasetID+"']/variables/variable[@ID='"+variableID+"']";
    }
    /**
     * Returns the full data URL for a particular variable (as identified by the variable element) including the #var (netCDF variable name convention used by LAS)
     * @param variable The variable element of the variable whose data access URL is desired
     * @return url The access URL (either a /path/path/filename#var or http://opendap_url#var).
     * @throws JDOMException
     * @throws LASException
     */
    public String getFullDataObjectURL(Element variable) throws LASException, JDOMException {
        if ( variable == null ) {
            throw new LASException("Variable not found.");
        }
        Element dataset = variable.getParentElement().getParentElement();
        String varURL = variable.getAttributeValue("url");
        String dsURL = dataset.getAttributeValue("url");
        String url = combinedURL(dsURL, varURL);
         return url;
    }
    public String getFullDataObjectURL(String dsid, String varid) throws LASException, JDOMException {
    	return getFullDataObjectURL("/lasdata/datasets/dataset[@ID='"+dsid+"']/variables/variable[@ID='"+varid+"']");
    }
    /**
     * Returns the full data URL for a particular variable (as identified by its XPath) including the #var (netCDF variable name convention used by LAS)
     * @param xpathValue The XPath of the variable whose data access URL is desired
     * @return url The access URL (either a /path/path/filename#var or http://opendap_url#var).
     * @throws JDOMException
     * @throws LASException
     */
    public String getFullDataObjectURL(String xpathValue) throws LASException, JDOMException {
        /*
         * We know this is a variable XPath.  If it's "old style" fix it.
         */
        if (!xpathValue.contains("@ID")) {
            String[] parts = xpathValue.split("/");
            // Throw away index 0 since the string has a leading "/".
            xpathValue = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
        }
        Element variable = getElementByXPath(xpathValue);
        if ( variable == null ) {
            // It might be a "remote" variable...
            return "";
        }
        Element dataset = variable.getParentElement().getParentElement();

        String varURL = variable.getAttributeValue("url");
        String dsURL = dataset.getAttributeValue("url");
        String url = combinedURL(dsURL, varURL);
        if ( url.equals("none") ) log.warn("URL for "+xpathValue+" is empty.");
        return url;
    }
    public static String combinedURL(String dsURL, String varURL) {
        String url = "";
        if ( varURL != null && dsURL != null) {
            if ( varURL.startsWith("http://") || varURL.startsWith("file:///") ) {
                url = varURL;
            } else {
                url = dsURL + varURL;
            }
        } else if ( varURL != null && dsURL == null ) {
            url = varURL;
        } else if ( varURL == null && dsURL != null ) {
            url = dsURL;
        } else if ( varURL == null && dsURL == null ) {
            url = null;
        }

        if ( url == null ) {
            
            url = "none";
        }

        if (url.startsWith("file://")) {
            url = url.substring(6, url.length());
        }
        if (url.startsWith("file:/")) {
            url = url.substring(5, url.length());
        }
        if (url.startsWith("file:")) {
            url = url.substring(5, url.length());
        }
        return url;
    }
    /**
     * Get the value of a "global" property (a property in the config that is not in any particular data set or variable).
     * @param group the name of the property group
     * @param name the name of the property
     * @return the value of specified property
     */
    public String getGlobalPropertyValue(String group, String name) {
        String value = "";
        List<Element> propList = getRootElement().getChildren("properties");
        for (Iterator propIt = propList.iterator(); propIt.hasNext();) {
            Element properties = (Element) propIt.next();
            if (properties != null) {
                List groups = properties.getChildren("property_group");
                for (Iterator groupsIt = groups.iterator(); groupsIt.hasNext();) {
                    Element property_group = (Element) groupsIt.next();
                    if ( property_group.getAttributeValue("type").equals(group)) {
                        List props = property_group.getChildren("property");
                        for (Iterator propsIt = props.iterator(); propsIt.hasNext();) {
                            Element property = (Element) propsIt.next();
                            String pname = property.getChildTextNormalize("name");
                            String pvalue = property.getChildTextNormalize("value");
                            if ( pname.equals(name) ) {
                                return pvalue;
                            }
                        }
                    }
                }
            }
        }
        return value;
    }

    public HashMap<String, String> getGlobalPropertyGroupAsHashMap(String name) throws LASException {
        HashMap<String, String> property_group_hash = new HashMap<String, String>();
        List<Element> propList = getRootElement().getChildren("properties");
        for (Iterator propIt = propList.iterator(); propIt.hasNext();) {
            Element properties = (Element) propIt.next();
            if (properties != null) {
                List groups = properties.getChildren("property_group");
                for (Iterator groupsIt = groups.iterator(); groupsIt.hasNext();) {
                    Element property_group = (Element) groupsIt.next();
                    if ( property_group.getAttributeValue("type").equals(name)) {
                        List props = property_group.getChildren("property");
                        for (Iterator propsIt = props.iterator(); propsIt.hasNext();) {
                            Element property = (Element) propsIt.next();
                            String pname = property.getChildTextNormalize("name");
                            String pvalue = property.getChildTextNormalize("value");
                            property_group_hash.put(pname, pvalue);
                        }
                    }
                }
            }
        }
        return property_group_hash;
    }


    /**
     * Get the grid of a variable from its XPath
     * @param varXPath
     * @return
     * @throws JDOMException
     * @throws LASException
     */
    public Grid getGrid(String varXPath) throws JDOMException, LASException {
    	if (!varXPath.contains("@ID")) {
            String[] parts = varXPath.split("/");
            // Throw away index 0 since the string has a leading "/".
            varXPath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
        } 
    	Element variable = getElementByXPath(varXPath);
        if (variable != null) {
            String ID = variable.getChild("grid").getAttributeValue("IDREF");
            return fillGrid(ID);
        } else {
        	return null;
        }
    }
    /**
     * Get the grid object with its axes filled by the grids ID.
     * @param ID the ID
     * @return the grid
     * @throws LASException 
     * @throws JDOMException 
     */
    public Grid getGridById(String ID) throws JDOMException, LASException {
    	return fillGrid(ID);
    }
    private Grid fillGrid(String ID) throws JDOMException, LASException {
    	Element gridE = null;
    	ArrayList<Element> axes_list = new ArrayList<Element>();
    	Element gt = getElementByXPath("/lasdata/grids/grid[@ID='"+ID+"']");
    	if ( gt == null ) return null;
    	gridE = (Element) gt.clone();
    	List axes = gridE.getChildren("axis");
    	for (Iterator axisIt = axes.iterator(); axisIt.hasNext();) {
    		Element axis_ref = (Element) axisIt.next();
    		String axisID = axis_ref.getAttributeValue("IDREF");
    		Element axisE = (Element) getElementByXPath("/lasdata/axes/axis[@ID='"+axisID+"']").clone();
    		String type = axisE.getAttributeValue("type");
    		axes_list.add(axisE);
    	}
    	// Replace the references with the actual axis definition.
    	if ( gridE != null ) {
    		gridE.setContent(axes_list);
    		return new Grid(gridE);
    	} else {
    		return null;
    	}
    }
    /**
     * Get grid for a particular dataset and variable.
     *
     * @param dsID the id of the desired data set
     * @param varID the id if the desired variable
     * @return grid the Grid object with up to for Axes
     * @throws JDOMException
     */
    public Grid getGrid(String dsID, String varID) throws JDOMException, LASException {
        return getGrid("/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']");
    }
    /**
	 * Get the grid_type for the variable (regular, scattered, ...)
	 * @param dsID
	 * @param varID
	 * @throws JDOMException
	 */
	public String getGridType(String dsID, String varID) throws JDOMException {
	    String grid_type="";

	    // Try to get a variable with this ID:

	    String varXPath = "/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']";
	    Element var = getElementByXPath(varXPath);

	    // If it's null try to get a composite with this ID.
	    if ( var == null ) {
	    	varXPath = "/lasdata/datasets/dataset[@ID='"+dsID+"']/composite/variable[@ID='"+varID+"']";
		    var = getElementByXPath(varXPath);
	    }
	    if ( var != null ) {
	        grid_type = var.getAttributeValue("grid_type");
	        if ( grid_type == null ) {
	            grid_type = "";
	        }
	    }
	    return grid_type;
	}
    /**
     * Get hi value for a particular axis type for the specified variable
     * @param varpath XPath to the variable
     * @param type which axis x,y,z or t
     *
     */
    public String getHi(String type, String varpath) throws JDOMException {
        String hi = null;
        /*
         * We know this is a variable XPath.  If it's "old style" fix it.
         */
        if (!varpath.contains("@ID")) {
            String[] parts = varpath.split("/");
            // Throw away index 0 since the string has a leading "/".
            varpath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
        }
        Element variable = getElementByXPath(varpath);
        if (variable == null) {
            return hi;
        }
        String gridID = variable.getChild("grid").getAttributeValue("IDREF");
        Element grid = getElementByXPath("/lasdata/grids/grid[@ID='"+gridID+"']");
        if (grid == null) {
            return hi;
        }
        List axes = grid.getChildren("axis");
        for (Iterator axIt = axes.iterator(); axIt.hasNext();) {
            Element axis = (Element) axIt.next();
            String axisID = axis.getAttributeValue("IDREF");
            axis = getElementByXPath("/lasdata/axes/axis[@ID='"+axisID+"']");
            String t = axis.getAttributeValue("type");
            if ( type.equals(t) ) {
                Element arange = axis.getChild("arange");
                if (arange == null) {
                    List v = axis.getChildren("v");
                    Element vN = (Element) v.get(v.size()-1);
                    hi = vN.getTextTrim();
                } else {
                    String st = arange.getAttributeValue("start");
                    double start = Double.valueOf(st).doubleValue();
                    double step = Double.valueOf(arange.getAttributeValue("step")).doubleValue();
                    double size = Double.valueOf(arange.getAttributeValue("size")).doubleValue();
                    double end = start+(size-1)*step;
                    hi = String.valueOf(end);
                }
            }
        }
        return hi;
    }
    /**
     * Get the container with all the information about the institution that installed this LAS.
     * @return Institution the information about the place that installed this LAS.
     */
    public Institution getInstitution() throws JDOMException {
        Element institution = getElementByXPath("/lasdata/institution");
        return new Institution(institution);
    }
	/**
     * Get lo value for a particular axis type for the specified variable.  If it's
     * the T axis you get a string with the low value, but you might want a TimeSelector instead.
     * @param varpath XPath to the variable
     * @param type which axis x,y,z or t
     *
     */
    public String getLo(String type, String varpath) throws JDOMException {
        String lo = null;
        /*
         * We know this is a variable XPath.  If it's "old style" fix it.
         */
        if (!varpath.contains("@ID")) {
            String[] parts = varpath.split("/");
            // Throw away index 0 since the string has a leading "/".
            varpath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
        }
        Element variable = getElementByXPath(varpath);
        if (variable == null) {
            return lo;
        }
        String gridID = variable.getChild("grid").getAttributeValue("IDREF");
        Element grid = getElementByXPath("/lasdata/grids/grid[@ID='"+gridID+"']");
        if (grid == null) {
            return lo;
        }
        List axes = grid.getChildren("axis");
        for (Iterator axIt = axes.iterator(); axIt.hasNext();) {
            Element axis = (Element) axIt.next();
            String axisID = axis.getAttributeValue("IDREF");
            axis = getElementByXPath("/lasdata/axes/axis[@ID='"+axisID+"']");
            String t = axis.getAttributeValue("type");
            // do xyz
            if ( type.equals(t) ) {
                Element arange = axis.getChild("arange");
                if (arange == null) {
                    List v = axis.getChildren("v");
                    Element v0 = (Element) v.get(0);
                    lo = v0.getTextTrim();
                } else {
                    lo = arange.getAttributeValue("start");
                }
            }
        }
        return lo;
    }
    /**
	 * Given a dataset element merge the properties (probably should be private)
	 * All properties should have been converted to "V7.0" style properties
	 * before this code is called.
	 * @param dsE The dataset element to merge
	 * @return datasetElement The same element with all of the new properites folded in to each variable.
	 */
	public Element getMergedProperties(Element dsE) {

	    // The colleciton is a HashMap of HashMaps.
	    // TODO these should be lists and loop through the list and combine any
	    // stray property groups...
	    Element variablePropsE = dsE.getChild("properties");
	    // The parent of the variable is <variables> the parent of <variables>
	    // is the particular <[datasetname]> tag.
	    Element datasetPropsE = dsE.getParentElement().getParentElement().getChild("properties");
	    // Same as above, plus the parent of the particular dataset tag is <datasets> and
	    // the parent of that is <lasdata>
	    Element globalPropsE = dsE.getParentElement().getParentElement().getParentElement().getParentElement().getChild("properties");


	    HashMap<String, HashMap<String, String>> propertyGroups = propertiesToHashMap(globalPropsE);

	    // If the group already exists any properties from this element
	    // replace the properites from the "global" properties.
	    if (datasetPropsE != null) {
	        List propGroups = datasetPropsE.getChildren();
	        for (Iterator pgIt = propGroups.iterator(); pgIt.hasNext();) {
	            Element propGroupE = (Element) pgIt.next();
	            List props = propGroupE.getChildren();
	            HashMap<String, String> group = propertyGroups.get(propGroupE.getAttributeValue("type"));
	            if (group == null) {
	                group = new HashMap<String, String>();
	            }
	            for (Iterator propIt = props.iterator(); propIt.hasNext();) {
	                Element prop = (Element) propIt.next();
	                String propName = prop.getChildTextNormalize("name");
	                String propValue = prop.getChildTextNormalize("value");
	                String currentValue = group.get(propName);
	                if (propValue != null && propName != null ) {
	                    group.put(propName, propValue);
	                }
	            }

	            propertyGroups.put(propGroupE.getAttributeValue("type"), group);
	        }
	    }
	    // The loop that follows is a merging action which is different from propertiesToHashMap (which is used above and when processing
	    // netCDF and THREDDS data sources directly).
	    if (variablePropsE != null) {
	        List propGroups = variablePropsE.getChildren();
	        for (Iterator pgIt = propGroups.iterator(); pgIt.hasNext();) {
	            Element propGroupE = (Element) pgIt.next();
	            List props = propGroupE.getChildren();
	            HashMap<String, String> group = propertyGroups.get(propGroupE.getAttributeValue("type"));
	            if (group == null) {
	                group = new HashMap<String, String>();
	            }
	            for (Iterator propIt = props.iterator(); propIt.hasNext();) {
	                Element prop = (Element) propIt.next();
	                String propName = prop.getChildTextNormalize("name");
	                String propValue = prop.getChildTextNormalize("value");
	                String currentValue = group.get(propName);
	                if (propValue != null && propName != null ) {
	                    group.put(propName, propValue);
	                } 
	            }

	            propertyGroups.put(propGroupE.getAttributeValue("type"), group);
	        }
	    }

	    Element properties = new Element("properties");
	    Element dsGroupE = null;
	    for (Iterator propertyGroupKeyIterator = propertyGroups.keySet()
	            .iterator(); propertyGroupKeyIterator.hasNext();) {
	        String propertyGroupKey = (String) propertyGroupKeyIterator
	        .next();
	        dsGroupE = new Element("property_group");
	        dsGroupE.setAttribute("type", propertyGroupKey);
	        HashMap propertyGroup = (HashMap) propertyGroups.get(propertyGroupKey);
	        for (Iterator keyIt = propertyGroup.keySet().iterator(); keyIt
	        .hasNext();) {
	            String key = (String) keyIt.next();
	            Element property = new Element("property");
	            Element property_name = new Element("name");
	            Element property_value = new Element("value");
	            property_name.addContent(key);
	            property_value.addContent((String) propertyGroup.get(key));
	            property.addContent(property_name);
	            property.addContent(property_value);
	            dsGroupE.addContent(property);
	        }
	        properties.addContent(dsGroupE);
	    }

	    return properties;
	}

	/**
	 * Helper method to convert a "new" style properties element into a HashMap of property group hashes.
	 * @param properties
	 * @return
	 */
	private HashMap<String, HashMap<String, String>> propertiesToHashMap(Element properties) {
		HashMap<String, HashMap<String, String>> propertyGroups = new HashMap<String, HashMap<String, String>>();
	    if (properties != null) {
	        // All children should be elements of the form <property_group type="name">
	        List propGroups = properties.getChildren();
	        for (Iterator pgIt = propGroups.iterator(); pgIt.hasNext();) {
	            Element propGroupE = (Element) pgIt.next();
	            // All children should be elements of the form <property><name>thename</name><value>thevalue</value></property>
	            List props = propGroupE.getChildren();
	            HashMap<String, String> property = new HashMap<String, String>();
	            for (Iterator propIt = props.iterator(); propIt.hasNext();) {
	                Element prop = (Element) propIt.next();
	                String propName = prop.getChildTextNormalize("name");
	                String propValue = prop.getChildTextNormalize("value");
	                property.put(propName, propValue);
	            }
	            propertyGroups.put(propGroupE.getAttributeValue("type"), property);
	        }
	    }
	    return propertyGroups;
	}
    /**
     * !! temporarily busted for V7 XML -- Returns the merged properties for a particular (maybe this should be private)
     * This /lasdata/datasets/DatasetTagName/variables/VariableTagName path and it
     * should be /lasdata/datasets/dataset[@ID='DatasetID']/variables/variable[@ID='VariableID'].
     * @deprecated
     * @param xpathValue
     * @return Element The varaible element with the properties merged into it.
     * @throws JDOMException
     */

        public Element getMergedProperties(String xpathValue) throws JDOMException {
            /*
             * We know this is a variable XPath.  If it's "old style" fix it.
             */
            if (!xpathValue.contains("@ID")) {
                String[] parts = xpathValue.split("/");
                // Throw away index 0 since the string has a leading "/".
                xpathValue = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
            }
            return getMergedProperties(getElementByXPath(xpathValue));
        }
    /**
	 * Get operations for a data set and variable, either by the associated default or by the interval.
	 * @throws JDOMException
	 */
	public ArrayList<Operation> getOperations(String view, String dsID, String varID) throws JDOMException {
		ArrayList<Operation> operations = new ArrayList<Operation>();

		String grid_type = getGridType(dsID, varID);

		String ui_default = getUIDefaultName(dsID, varID);

		if ( ui_default != null && !ui_default.equals("") ) {
			ui_default = ui_default.substring(ui_default.indexOf("#")+1, ui_default.length());
			operations = getOperationsByDefault(view, ui_default);
		} else {
			operations = getOperationsByIntervalAndGridType(view, grid_type);
		}
		return operations;
	}
	/**
	 * Return operations based on the variable xpath.
	 * @param view
	 * @param xpath
	 * @return
	 * @throws JDOMException
	 */
	public ArrayList<Operation> getOperations(String view, String xpath) throws JDOMException {
		String dsid = getDSIDfromXPath(xpath);
		String varid = getVarIDfromXPath(xpath);
		return getOperations(view, dsid, varid);
	}
	public static String getDSIDfromXPath(String xpath) {
		String dsid;
		if (!xpath.contains("@ID")) {
			String[] parts = xpath.split("/");
			// Throw away index 0 since the string has a leading "/".
			dsid = parts[3];
		} else {
			dsid = xpath.substring(xpath.indexOf("@ID"), xpath.indexOf("]")-1);
		}
		return dsid;
	}
	public static String getVarIDfromXPath(String xpath) {
		String varid;
		if (!xpath.contains("@ID")) {
			String[] parts = xpath.split("/");
			// Throw away index 0 since the string has a leading "/".
			varid=parts[5];
		} else {
			varid = xpath.substring(xpath.lastIndexOf("@ID"), xpath.lastIndexOf("]")-1);
		}
		return varid;
	}
	/**
	 * Get all the operations that require more that one variable.
	 * @param view the current UI view
	 * @param xpath the dataset/variable xpath of the selected variables
	 * @return
	 */
	public ArrayList<Operation> getOperations(String view, String[] xpath) throws LASException, JDOMException {
	    Map<String, Operation> operations = new HashMap<String, Operation>();
	    //  Should return a unique set based on the ID (maybe not based on object equality).
		if ( xpath.length == 1 ) {
	        List<Operation> a = getOperations(view, xpath[0]);
	        for ( Iterator iterator = a.iterator(); iterator.hasNext(); ) {
                Operation operation = (Operation) iterator.next();
                if ( !operations.containsKey(operation.getID())) {
                    operations.put(operation.getID(), operation);
                }
            }
		} else {

			String ui_default = "";

			for (int i = 0; i < xpath.length; i++) {
				String dsid = LASConfig.getDSIDfromXPath(xpath[i]);
				String varid = LASConfig.getVarIDfromXPath(xpath[i]);
				String current_ui_default = getUIDefaultName(dsid, varid);
				if ( !current_ui_default.equals("") || !ui_default.equals("")) {
					if ( i > 0 ) {
						if (!current_ui_default.equals(ui_default)) {
							throw new LASException("The selected variables have different UI default parameters");
						} else {
							ui_default = current_ui_default;
						}
					} else {
						ui_default = current_ui_default;
					}
				}
			}
			for (int i = 0; i < xpath.length; i++) {
	            List<Operation> a = getOperations(view, xpath[i]);
	            for ( Iterator iterator = a.iterator(); iterator.hasNext(); ) {
	                Operation operation = (Operation) iterator.next();
	                if ( !operations.containsKey(operation.getID())) {
	                    operations.put(operation.getID(), operation);
			}
		}
	        }
	    }
		ArrayList<Operation> multi_variable_operations = new ArrayList<Operation>();
		int var_count = xpath.length;
		int minvars = -1;
		int maxvars = -1;
	    int o = 0;
	    for ( Iterator iterator = operations.keySet().iterator(); iterator.hasNext(); ) {
	        String key = (String) iterator.next();
	        Operation op = (Operation) operations.get(key);
			String min = op.getAttributeValue("minvars");
			String max = op.getAttributeValue("maxvars");
			if ( min != null && min.equals("") && max != null && max.equals("") && xpath.length == 1 ) {
				// Default is 1 for min and max so include it.
				multi_variable_operations.add(op);
			} else {
				if ( min != null && !min.equals("") ) {
					try {
						minvars = Integer.valueOf(min).intValue();
					} catch (Exception e) {
						throw new LASException("Cannot parse the minvars attribute value.");
					}
				}
				if ( max != null && !max.equals("") ) {
					try {
						maxvars = Integer.valueOf(max).intValue();
					} catch (Exception e) {
						throw new LASException("Cannot parse the maxvars attribute value.");
					}
				}
				if ( minvars > 0 ) {
					if ( maxvars > 0 ) {
						if ( var_count >= minvars && var_count <= maxvars ) {
							multi_variable_operations.add(op);
							minvars = -1;
							maxvars = -1;
						}
					} else {
						if ( var_count >= minvars ) {
							multi_variable_operations.add(op);
							minvars = -1;
							maxvars = -1;
						}
					}
				}
			}
		}

		return multi_variable_operations;
	}
	/**
	 * Get the operations that are include with a particular UI default
	 * @param ui_default
	 * @return operations JSONObject with the operations that are defined for this "default".
	 * @throws JDOMException
	 */
	public ArrayList<Operation> getOperationsByDefault(String view, String ui_default) throws JDOMException {
		ArrayList<Operation> operations = new ArrayList<Operation>();
		Element def = getUIDefault(ui_default);
		if ( def != null ) {
			Element map = getUIMap(def, "ops");
			if (map != null) {
				List ifmenusElements = map.getChildren("ifmenu");
				for (Iterator ifmenusIt = ifmenusElements.iterator(); ifmenusIt
				.hasNext();) {
					Element ifmenu = (Element) ifmenusIt.next();
					// An ifmenu is not always controlled by a view.
					// Could be the mode as well !!
					// TODO handle distinctions for comparison mode

					String ifmenu_view = ifmenu.getAttributeValue("view");

					if ( ifmenu_view != null ) {
						boolean match = false;
						// This attribute can be a comma separated list...
						if (ifmenu_view.contains(",") ) {
							String[] theviews = ifmenu_view.split(",");
							for (int i = 0; i < theviews.length; i++) {
								if ( theviews[i].equals(view) ) {
									match = true;
								}
							}
						} else {
							match = ifmenu_view.equals(view);
						}
						// If the incoming view is null, we want them all.
						if (match || view == null ) {
							String ops_menu_ref = ifmenu
							.getAttributeValue("href");

							Element menu = getElementByXPath("/lasdata/lasui/menus/menu[@name='"
									+ ops_menu_ref.substring(1) + "']");
							List ops = menu.getChildren("item");
							for (Iterator opsIt = ops.iterator(); opsIt.hasNext();) {
								Element item = (Element) opsIt.next();
								String value = item.getAttributeValue("values");
								/* This is pulling out information that was designed to be the values
								 * of an HTML menu.  It's not such a great way to store informaiton that
								 * is intended to be used to extract further information from the XML.
								 * Therefore there's a lot of splitting and spitting to get the job done.
								 * TODO we should re-think the XML at some point...  Soon?
								 */
								String opID = value.substring(0, value.indexOf(","));
								Element opE = getElementByXPath("/lasdata/operations/operation[@ID='"
										+ opID + "']");
								if ( opE != null ) {
									Operation op = new Operation(opE);
									if ( !operations.contains(op) ) {
										// Keep the collection unique
										operations.add(op);
									}
								} else {
									log.warn("Operation "+opID+" from default "+ui_default+" not found in configuration.");
								}
							}
						}
					}
				}
			}
		}
		return operations;
	}
    /**
     * Get the operations based on the axes defined on an interval and the grid type.
	 * @param view  The view to be matched to the interval specifications in the operation config
	 * @param grid_type the desired grid type
	 * @return the list of operations
	 */
	public ArrayList<Operation> getOperationsByIntervalAndGridType(String view, String grid_type) throws JDOMException {

	    ArrayList<Operation> matchingOperations = new ArrayList<Operation>();
	    List operationsElements = getRootElement().getChildren("operations");
	    for (Iterator opsElementsIt = operationsElements.iterator(); opsElementsIt.hasNext();) {
	        Element opsElement = (Element) opsElementsIt.next();
	        List opElements = opsElement.getChildren("operation");
	        for (Iterator opIt = opElements.iterator(); opIt.hasNext(); ) {
	            boolean grid_type_match = false;
	            boolean intervals_match = false;
	            boolean degenerate_match = false;
	            Element operation = (Element) opIt.next();
	            Element region = operation.getChild("region");
	            Element grid_types = operation.getChild("grid_types");
	            if (region != null && grid_types != null) {
	                List types = grid_types.getChildren("grid_type");
	                for (Iterator typeIt = types.iterator(); typeIt.hasNext();) {
	                    Element type = (Element) typeIt.next();
	                    if (type.getAttributeValue("name").equals(
	                            grid_type)) {
	                        grid_type_match = true;
	                    }
	                }
	                if ( view == null ) {
	                    intervals_match = true;
	                } else {
	                List intervals = region.getChildren("intervals");
	                for (Iterator intvIt = intervals.iterator(); intvIt.hasNext();) {
	                    Element intv = (Element) intvIt.next();
	                    if (intv.getAttributeValue("name").equals(view)) {
	                        intervals_match = true;
	                    }
	                }
	                }
	                if ( view != null ) {
	                Element degenerate = region.getChild("degenerate");
	                if ( degenerate != null && view.equals("d") ) {
	                	degenerate_match = true;
	                }
	                }
	                boolean private_op = false;
	                String private_attr = operation.getAttributeValue("private");
	                if ( private_attr != null && private_attr.equals("true") ) {
	                    private_op = true;
	                }
	                if (grid_type_match && (intervals_match||degenerate_match) && !private_op) {
	                    Operation op = new Operation(operation);
	                    matchingOperations.add(op);
	                }
	            }
	        }
	    }
	    return matchingOperations;
	}
    /**
     * Selects operations that use interval inputs on the axes listed in the view
     *
     * @param view a string containing an ordered subset of xyzt.
     * @return operations a list of operation elements that use this view
     * @throws JDOMException
     * @deprecated
     */
    public ArrayList<NameValuePair> getOperationsByView(String view) throws JDOMException {
        ArrayList<NameValuePair> ops = new ArrayList<NameValuePair>();
        String path = "/lasdata/operations/operation[@intervals='"+view+"']";
        XPath xpath = XPath.newInstance(path);
        for (Iterator nodes = xpath.selectNodes(this).iterator(); nodes.hasNext(); ) {
            Element op = (Element)nodes.next();
            ops.add(new NameValuePair(op.getAttributeValue("name"), op.getAttributeValue("ID")));
        }
        return ops;
    }


    /**
     * Returns the options associated with this option ID
     * @param optionID option ID
     * @return options the options for this id.
     * @throws JDOMException
     */
    public ArrayList<Option> getOptions(String optionID) throws JDOMException {
        ArrayList<Option> options = new ArrayList<Option>();
        // Collect the options from this ID (which is really the name, but nevermind).
        options.addAll(extractOptions(optionID));
        return options;
    }
    /**
     * Returns the options associated with this operation ID
     * @param operationID operation ID
     * @return options the options for this operation.
     * @throws JDOMException
     */
    public ArrayList<Option> getOptionsByOperationID(String operationID) throws JDOMException {
    	ArrayList<Option> options = new ArrayList<Option>();

    	// Be sure to make items null for textarea.

    	String optionID = getOptionID(operationID);
    	if ( optionID != null && !optionID.equals("") ) {
    		// Collect the options from this ID (which is really the name, but nevermind).
    		options.addAll(extractOptions(optionID));
    	}

    	return options;
    }
    public String getOptionID(String operationID) throws JDOMException {
    	 Element op = getElementByXPath("/lasdata/operations/operation[@ID='"+operationID+"']");
    	 String optionID = "";
         if ( op != null ) {
             Element option = op.getChild("optiondef");
             if ( option != null ) {
                 optionID = option.getAttributeValue("IDREF");
                 if ( optionID == null ) {
                	 optionID = "";
                 }
             }
         }
         return optionID;
    }
    /**
	 * Get the name of the output directory for this LAS
	 * @return outputdir The path to the output directory.
	 */
	public String getOutputDir() {
	    return this.getRootElement().getChildText("output_dir");
	}
	/**
	 * Get the readonly attribute. This is for SOCAT and is assumed false if attribute is not present.
	 * @return readonly attribute.
	 */
	public Boolean getReadonly() {
		return Boolean.parseBoolean(this.getRootElement().getChildText("readonly"));
    }
    /**
     * Get xy region for a particular variable
     * @param varpath XPath to variable
     * @return region ArrayList of NameValueBeans with x_lo, x_hi, y_lo and y_hi
     * @throws JDOMException
     */
    public ArrayList<NameValuePair> getRangeForXY(String varpath) throws JDOMException {
        ArrayList<NameValuePair> region = new ArrayList<NameValuePair>();
        /*
         * We know this is a variable XPath.  If it's "old style" fix it.
         */
        if (!varpath.contains("@ID")) {
            String[] parts = varpath.split("/");
            // Throw away index 0 since the string has a leading "/".
            varpath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
        }
        Element variable = getElementByXPath(varpath);
        if (variable == null) {
            return region;
        }
        String gridID = variable.getChild("grid").getAttributeValue("IDREF");
        Element grid = getElementByXPath("/lasdata/grids/grid[@ID='"+gridID+"']");
        if (grid == null) {
            return region;
        }
        List axes = grid.getChildren("axis");
        for (Iterator axIt = axes.iterator(); axIt.hasNext();) {
            Element axis = (Element) axIt.next();
            String axisID = axis.getAttributeValue("IDREF");
            axis = getElementByXPath("/lasdata/axes/axis[@ID='"+axisID+"']");
            String type = axis.getAttributeValue("type");
            if ( type.equals("x") || type.equals("y") ) {
                NameValuePair lo=null;
                NameValuePair hi=null;
                if ( type.equals("x") ) {
                    lo = new NameValuePair("x_lo", "");
                    hi = new NameValuePair("x_hi", "");
                } else if ( type.equals("y") ) {
                    lo = new NameValuePair("y_lo", "");
                    hi = new NameValuePair("y_hi", "");
                }
                Element arange = axis.getChild("arange");
                if (lo != null && hi != null && arange == null) {
                    List v = axis.getChildren("v");
                    Element v0 = (Element) v.get(0);
                    Element vN = (Element) v.get(v.size()-1);
                    lo.setValue(v0.getTextTrim());
                    hi.setValue(vN.getTextTrim());
                } else {
                	if ( lo != null && hi != null ) {
                		String st = arange.getAttributeValue("start");
                		lo.setValue(st);
                		double start = Double.valueOf(st).doubleValue();
                		long step = Long.valueOf(arange.getAttributeValue("step")).longValue();
                		double size = Double.valueOf(arange.getAttributeValue("size")).doubleValue();
                		double end = start+(size-1)*step;
                		hi.setValue(String.valueOf(end));
                	}
                }
                region.add(lo);
                region.add(hi);
            }
        }
        return region;
    }

    /**
     * Get the pre-defined regions, by UI default based on the data set and variable.
     * @param dsID
     * @param varID
     * @return
     * @throws JDOMException
     */
    public ArrayList<Region> getRegions(String dsID, String varID) throws JDOMException {
    	ArrayList<Region> regions = new ArrayList<Region>();
    	Element ui_default = getUIDefault(dsID, varID);
    	Element livemap = getUIMap(ui_default, "livemap");
    	if ( livemap != null ) {
    		Element menu = livemap.getChild("menu");
    		String href = menu.getAttributeValue("href");
    		menu = getElementByXPath("/lasdata/lasui/menus/menu[@name='"+href.substring(1)+"']");
    		List items = menu.getChildren("item");
    		for (Iterator itemsIt = items.iterator(); itemsIt.hasNext();) {
    			Element item = (Element) itemsIt.next();
    			Element region = new Element("region");
    			String values = item.getAttributeValue("values");
    			String name = item.getTextTrim();
    			region.setAttribute("name", name);
    			region.setAttribute("ID", name);
    			region.setAttribute("values", values);
    			String[] corners = values.split(",");
    			region.setAttribute("xlo", corners[0]);
    			region.setAttribute("xhi", corners[1]);
    			region.setAttribute("ylo", corners[2]);
    			region.setAttribute("yhi", corners[3]);
    			Region reg = new Region(region);
    			regions.add(reg);
    		}
    	}
    	return regions;
    }
    /**
     * Get all variables in a data set that are defined on a regular grid
     * @param dsID the data set
     * @return the list of variables
     * @throws JDOMException
     */
    public ArrayList<NameValuePair> getRegularVariables(String dsID) throws JDOMException {
        ArrayList<NameValuePair> variables = new ArrayList<NameValuePair>();
        Element dataset = getElementByXPath("/lasdata/datasets/dataset[@ID='"+dsID+"']");
        List variablesElements = dataset.getChildren("variables");
        for (Iterator vseIt = variablesElements.iterator(); vseIt.hasNext();) {
            Element variablesElement = (Element) vseIt.next();
            List vars = variablesElement.getChildren("variable");
            for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
                Element variable = (Element) varIt.next();
                String grid_type = variable.getAttributeValue("grid_type");
                if (grid_type.equals("regular")) {
                    String name = variable.getAttributeValue("name");
                    String value = variable.getAttributeValue("ID");
                    variables.add(new NameValuePair(name, value));
                }
            }
        }
        return variables;
    }
    /**
     * Get the URL of the product server.  This URL should be correct even if the
     * server is being proxied through Apache.
     * @throws JDOMException
     */

    public String getServerURL() throws JDOMException {
        String server = "";
        Element ops = getElementByXPath("/lasdata/operations");
        server = ops.getAttributeValue("url");
        return server;
    }

    /**
     * Get the name of the service based on the operation ID.
     * @param opID the ID of the operation
     * @return service the name of the service associated with this operation
     * @throws JDOMException
     */
    public String getService(String opID) throws JDOMException {
        String service = null;
        Element op = getElementByXPath("/lasdata/operations/operation[@ID='"+opID+"']");
        if ( op != null ) {
            Element serviceE = op.getChild("service");
            if ( serviceE != null ) {
                return serviceE.getTextNormalize();
            }
        }
        return service;
    }

    /**
     * Get time selector object the specified variable
     * @param varpath XPath to the variable
     * @throws JDOMException
     *
     */
    public TimeSelector getT(String varpath) throws JDOMException {
        TimeSelector t = new TimeSelector();
        /*
         * We know this is a variable XPath.  If it's "old style" fix it.
         */
        if (!varpath.contains("@ID")) {
            String[] parts = varpath.split("/");
            // Throw away index 0 since the string has a leading "/".
            varpath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
        }
        Element variable = getElementByXPath(varpath);
        if (variable == null) {
            return t;
        }
        String gridID = variable.getChild("grid").getAttributeValue("IDREF");
        Element grid = getElementByXPath("/lasdata/grids/grid[@ID='"+gridID+"']");
        if (grid == null) {
            return t;
        }
        List axes = grid.getChildren("axis");
        for (Iterator axIt = axes.iterator(); axIt.hasNext();) {
            Element axis = (Element) axIt.next();
            String axisID = axis.getAttributeValue("IDREF");
            axis = getElementByXPath("/lasdata/axes/axis[@ID='"+axisID+"']");
            String type = axis.getAttributeValue("type");
            if ( type.equals("t") ) {
                String default_display = axis.getAttributeValue("default");
                Element arange = axis.getChild("arange");
                if (arange == null) {
                    t.setType("menu");
                    List v = axis.getChildren("v");
                    Element v0 = (Element) v.get(0);
                    String tlo = v0.getTextTrim();
                    Element vN = (Element) v.get(v.size()-1);
                    String thi = vN.getTextTrim();
                    t.setLo(tlo);
                    t.setCurrent_lo(tlo);
                    if ( default_display != null && default_display.equals("last") ) {
                        t.setCurrent_hi(thi);
                    } else {
                        t.setCurrent_hi(tlo);
                    }
                    StateNameValueList vs = new StateNameValueList();
                    for (Iterator vIt = v.iterator(); vIt.hasNext();) {
                        Element vElement = (Element) vIt.next();
                        vs.add(new NameValuePair(vElement.getTextNormalize(), vElement.getTextNormalize()));
                    }
                    t.setLo_items(vs);
                    t.setHi_items(vs);
                    if (default_display != null && default_display.equals("last") ) {
                        t.getHi_items().setCurrent(thi);
                    }
                } else {
                    t.setType("widget");
                    String tlo = arange.getAttributeValue("start");
                    String units = axis.getAttributeValue("units");
                    double size = Double.valueOf(arange.getAttributeValue("size")).doubleValue();
                    double step = Double.valueOf(arange.getAttributeValue("step")).doubleValue();
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH").withZone(DateTimeZone.UTC);
                    DateTimeFormatter zfmt = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC);
                    DateTimeFormatter longfmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);
                    DateTime lodt;
                    if ( tlo.length() == 10 ) {
                        lodt = zfmt.parseDateTime(tlo);
                    } else {
                        lodt = fmt.parseDateTime(tlo);
                    }
                    t.setLo(lodt.toString(longfmt));
                    t.setCurrent_lo(t.getLo());
                    DateTime hidt = new DateTime();
                    if ( units.contains("hour") ) {
                        t.setHourNeeded(true);
                        t.setDayNeeded(true);
                        t.setMonthNeeded(true);
                        t.setYearNeeded(true);
                        int hours = (int) Math.round((size-1)*step);
                        int minuteInterval = (int) Math.round(step*60.);
                        t.setMinuteInterval(minuteInterval);
                        hidt = lodt.plus(Period.hours(hours));
                        t.setHi(hidt.toString(longfmt));
                    } else if ( units.contains("day") ) {
                        t.setHourNeeded(false);
                        t.setDayNeeded(true);
                        t.setMonthNeeded(true);
                        t.setYearNeeded(true);
                        int days = (int) Math.round((size-1)*step);
                        hidt = lodt.plus(Period.days(days));
                        t.setHi(hidt.toString(longfmt));
                    } else if ( units.contains("month") ) {
                        t.setHourNeeded(false);
                        t.setDayNeeded(false);
                        t.setMonthNeeded(true);
                        t.setYearNeeded(true);
                        int months = (int) Math.round((size-1)*step);
                        hidt = lodt.plus(Period.months(months));
                        t.setHi(hidt.toString(longfmt));
                    } else if ( units.contains("year") ) {
                        t.setHourNeeded(false);
                        t.setDayNeeded(false);
                        t.setMonthNeeded(false);
                        t.setYearNeeded(true);
                        double start = Double.valueOf(tlo).doubleValue();
                        int years = (int) Math.round(start + (size-1)*step);
                        hidt = lodt.plus(Period.years(years));
                        t.setHi(hidt.toString(longfmt));
                    }
                    if ( default_display != null && default_display.equals("last") ) {
                        t.setCurrent_hi(hidt.toString(longfmt));
                    } else {
                        t.setCurrent_hi(lodt.toString(longfmt));
                    }
                }
            }
        }
        return t;
    }
    public String getTemplate(String id) throws JDOMException {
    	/*
    	 *   Look like this:   /lasdata/operations/operation[@ID='Plot_2D_XY_zoom']
    	 */
    	return getTemplateByXPath("/lasdata/operations/operation[@ID='"+id+"']");
    }
        /**
         * Given the XPath to an operation return the output template that should be processed for this product
         * @param XPath The path to the operation element (an XPath looks like this: /lasdata/operations/operation[@ID='Plot_2D_XY_zoom']
         * @return output_template The name of the template (which will be resolve using the class loader and the backend config information)
         * @throws JDOMException
         */
        public String getTemplateByXPath(String XPath) throws JDOMException {
            Element opE = getElementByXPath(XPath);
            if (opE != null) {
                return opE.getAttributeValue("output_template");
            } else {
                //TODO configurable default output_template.
                return "output";
            }
        }

        /**
         * Get the F-TDS URL for this variable
		 * @param variable the variable element
		 * @return the F-TDS URL
		 * @throws JDOMException
		 */
        public String getFTDSURL(Element variable) {
        	String ftds_url = "";
        	if ( variable != null ) {
        		ftds_url = variable.getAttributeValue("ftds_url");
        		if ( ftds_url != null ) {
        			return ftds_url;
        		} else {
        			return "";
        		}
        	} else {
        		return "";
        	}
        }

        /**
         * Get the F-TDS URL for the variable specified by the XPath
		 * @param varXPath the XPath of the variable
		 * @return the F-TDS URL
		 * @throws JDOMException
		 */
		public String getFTDSURL(String varXPath) throws JDOMException {
		    if (!varXPath.contains("@ID")) {
		        String[] parts = varXPath.split("/");
		        // Throw away index 0 since the string has a leading "/".
		        varXPath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
		    }
		    Element variable = getElementByXPath(varXPath);
		    return getFTDSURL(variable);
		}
		/**
         * Get the F-TDS URL for the variable specified by the dsID and varID
		 * @param dsID of the variable
		 * @param varID of the variable
		 * @return the F-TDS URL
		 * @throws JDOMException
		 */
		public String getFTDSURL(String dsID, String varID) throws JDOMException {
			String varXPath = "/lasdata/datasets/dataset[@ID='"+dsID+"']/"+"variables/variable[@ID='"+varID+"']";
		    Element variable = getElementByXPath(varXPath);
		    return getFTDSURL(variable);
		}
        /**
         * Create a time axis with all the info needed for the DateWidgets
         * @param variable the variable whose time axis will be built
         * @return the time axis specification
         * @throws JDOMException
         * @throws LASException
         */
        public TimeAxis getTime(Element variable) throws JDOMException, LASException {
		    TimeAxis t = null;
		    if (variable == null) {
		        return t;
		    }
		    String gridID = variable.getChild("grid").getAttributeValue("IDREF");
		    Element grid = getElementByXPath("/lasdata/grids/grid[@ID='"+gridID+"']");
		    if (grid == null) {
		        return t;
		    }
		    List axes = grid.getChildren("axis");

		    for (Iterator axIt = axes.iterator(); axIt.hasNext();) {
		        Element axis = (Element) axIt.next();
		        String axisID = axis.getAttributeValue("IDREF");
		        axis = getElementByXPath("/lasdata/axes/axis[@ID='"+axisID+"']");
		        String type = axis.getAttributeValue("type");
		        if ( type.equals("t") ) {

		            Element arange = axis.getChild("arange");
		            if (arange == null) {

		                List v = axis.getChildren("v");
		                Element v0 = (Element) v.get(0);
		                String tlo = v0.getTextTrim();
		                Element vN = (Element) v.get(v.size()-1);
		                String thi = vN.getTextTrim();

		                ArrayList<NameValuePair> vs = new ArrayList<NameValuePair>();
		                for (Iterator vIt = v.iterator(); vIt.hasNext();) {
		                    Element vElement = (Element) vIt.next();
		                    String label = vElement.getAttributeValue("label");
		                    if ( label == null ) {
		                        label = vElement.getTextNormalize();
		                    }
		                    vs.add(new NameValuePair(label, vElement.getTextNormalize()));
		                }
		                t = new TimeAxis(axis);
		                t.setLo(tlo);
		                t.setHi(thi);
		                t.setDisplay_type("menu");

		            } else {
		                t = new TimeAxis(axis);
		                t.setDisplay_type("widget");
		                String tlo = arange.getAttributeValue("start");
		                String units = axis.getAttributeValue("units");
		                double size = Double.valueOf(arange.getAttributeValue("size")).doubleValue();
		                double step = Double.valueOf(arange.getAttributeValue("step")).doubleValue();
		                DateTimeFormatter fmt = null;
		                DateTime lodt = new DateTime("9000-01-01");
		                boolean found = false;
		                for (int i = 0; i < time_formats.length; i++) {
		                    fmt = DateTimeFormat.forPattern(time_formats[i]).withZone(DateTimeZone.UTC);
		                    try {
		                       lodt = fmt.parseDateTime(tlo);
		                       found = true;
		                    } catch ( IllegalArgumentException e ) {
		                        found = false;
		                    }
		                    if (found) break;
		                }

		                if ( !found ) {
		                    throw new LASException("Time format for "+tlo+" could not be parsed.");
		                }

		                t.setLo(lodt.toString(fmt));
		                DateTime hidt = new DateTime();
		                if ( units.contains("minute") ) {
		                	t.setHourNeeded(true);
		                	t.setDayNeeded(true);
		                	t.setMonthNeeded(true);
		                	t.setYearNeeded(true);
		                	int minutes = (int) Math.round((size-1)*step);
		                	int minuteInterval = (int) Math.round(step*60.);
			                t.setMinuteInterval(minuteInterval);
		                	hidt = lodt.plus(Period.minutes(minutes));
		                	t.setHi(hidt.toString(fmt));
		                } else if ( units.contains("hour") ) {
		                    t.setHourNeeded(true);
		                    t.setDayNeeded(true);
		                    t.setMonthNeeded(true);
		                    t.setYearNeeded(true);
		                    int hours = (int) Math.round((size-1)*step);
		                    int minuteInterval = (int) Math.round(step*60.);
		                    t.setMinuteInterval(minuteInterval);
		                    hidt = lodt.plus(Period.hours(hours));
		                    t.setHi(hidt.toString(fmt));
		                } else if ( units.contains("day") ) {
		                    t.setHourNeeded(false);
		                    t.setDayNeeded(true);
		                    t.setMonthNeeded(true);
		                    t.setYearNeeded(true);
		                    int days = (int) Math.round((size-1)*step);
		                    hidt = lodt.plus(Period.days(days));
		                    t.setHi(hidt.toString(fmt));
		                } else if ( units.contains("month") ) {
		                    t.setHourNeeded(false);
		                    t.setDayNeeded(false);
		                    t.setMonthNeeded(true);
		                    t.setYearNeeded(true);
		                    int months = (int) Math.round((size-1)*step);
		                    hidt = lodt.plus(Period.months(months));
		                    t.setHi(hidt.toString(fmt));
		                } else if ( units.contains("year") ) {
		                    t.setHourNeeded(false);
		                    t.setDayNeeded(false);
		                    t.setMonthNeeded(false);
		                    t.setYearNeeded(true);
		                    double start = Double.valueOf(tlo).doubleValue();
		                    int years = (int) Math.round(start + (size-1)*step);
		                    hidt = lodt.plus(Period.years(years));
		                    t.setHi(hidt.toString(fmt));
		                }
		            }
		        }
		    }
		    return t;
		}
        /**
		 * Get time selector object the specified variable
		 * @param varpath XPath to the variable
		 * @throws LASException
		 *
		 */
		public TimeAxis getTime(String varpath) throws JDOMException, LASException {

		    /*
		     * We know this is a variable XPath.  If it's "old style" fix it.
		     */
		    if (!varpath.contains("@ID")) {
		        String[] parts = varpath.split("/");
		        // Throw away index 0 since the string has a leading "/".
		        varpath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
		    }

		    Element variable = getElementByXPath(varpath);
		    return getTime(variable);

		}
        /**
		 * Extracts the LAS title from the configuration
		 * @return title The title of this LAS
		 * @throws JDOMException
		 */
		public String getTitle() throws JDOMException {
		    String title = "";
		    Element ui = getElementByXPath("/lasdata/lasui");
		    if ( ui != null ) {
		        title = ui.getAttributeValue("title");
		        if ( title == null ) {
		            title="";
		        }
		    }
		    return title;
		}
		/**
		 * Get the name of the UI Default associated with this variable.
		 * @param dsID the data set
		 * @param varID the variable
		 * @return the UI Default name; can be null
		 * @throws JDOMException
		 */
	    public String getUIDefaultName(String dsID, String varID) throws JDOMException {
	    	String xpath = "/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']";
	    	Element variable = getElementByXPath(xpath);
	    	if ( variable == null ) {
	    		xpath = "/lasdata/datasets/dataset[@ID='"+dsID+"']/composite/variable[@ID='"+varID+"']";
	    	    variable = getElementByXPath(xpath);
	    	}
	    	if ( variable == null ) {
	    		return "";
	    	} else {
			    return getVariablePropertyValue(variable,"ui", "default");
	    	}
		}
	    /**
	     * Get the actual UI Default element
	     * @param dsID the data set
	     * @param varID the variable
	     * @return the default element (can be null)
	     * @throws JDOMException
	     */
	    public Element getUIDefault(String dsID, String varID) throws JDOMException {
	    	String ui_default = getUIDefaultName(dsID, varID);
	    	ui_default = ui_default.substring(ui_default.indexOf("#")+1, ui_default.length());
	    	return getUIDefault(ui_default);
	    }
	    /**
	     * Get the default element based on its name
	     * @param ui_default the name of the desired default
	     * @return the default element (can be null)
	     */
        public Element getUIDefault(String ui_default) {
            List defaultsElements = getRootElement().getChild("lasui").getChildren("defaults");
            for (Iterator defsIt = defaultsElements.iterator(); defsIt.hasNext();) {
                Element defaultsE = (Element) defsIt.next();
                List defaults = defaultsE.getChildren("default");
                for (Iterator defIt = defaults.iterator(); defIt.hasNext();) {
                    Element def = (Element) defIt.next();
                    String name = def.getAttributeValue("name");
                    // The default default has no name...
                    if ( name != null ) {
                        if ( name.equals(ui_default)  ) {
                            return def;
                        }
                    } else {
                        // The default "default" has no name so the input is null and the match is null.
                        if ( ui_default == null || ui_default.equals("") ) {
                            return def;
                        }
                    }
                }
            }
            return null;
        }
        public Element getUIMap(Element def, String intype) throws JDOMException {
            List maps = def.getChildren("map");
            for (Iterator mapIt = maps.iterator(); mapIt.hasNext();) {
                Element map_ref = (Element) mapIt.next();
                String ref = map_ref.getAttributeValue("href");
                Element map = getElementByXPath("/lasdata/lasui/maps/map[@name='"+ref.substring(1)+"']");
                String type = map.getAttributeValue("type");
                if ( type.equals(intype)) {
                   return map;
                }
            }
            return null;
        }
        public Element getUIMenu (String href) throws JDOMException {
        	return getElementByXPath("/lasdata/lasui/menus/menu[@name='"+href+"']");
        }

        /**
		 * Extracts of list of known LAS UI Clients for this product server.
		 * @return ui_url_list a list of URLs of known LAS UI Clients for this server
		 * @throws JDOMException
		 */
		public ArrayList<String> getUIs() {
		    ArrayList<String> uis = new ArrayList<String>();
		    List uiElements = getRootElement().getChild("lasui").getChildren("ui");
		    for (Iterator uiIt = uiElements.iterator(); uiIt.hasNext();) {
		        Element ui = (Element) uiIt.next();
		        uis.add(ui.getAttributeValue("url"));
		    }
		    return uis;
		}

        public Variable getVariableByXPath(String xpath) throws JDOMException {
		    String dsid;
		    if (!xpath.contains("@ID")) {
		        String[] parts = xpath.split("/");
		        // Throw away index 0 since the string has a leading "/".
		        xpath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
		        dsid = parts[3];
		    } else {
		        dsid = xpath.substring(xpath.indexOf("dataset[@ID='")+13,xpath.indexOf("']"));
		    }
		    Element variable = getElementByXPath(xpath);
		    if ( variable != null ) {
		        return new Variable(variable, dsid);
		    } else {
		        return null;
		    }
		}
        public String getVariableIntervals(String xpath) throws JDOMException, LASException {
        	Grid grid = getGrid(xpath);
        	return grid.getAttributeValue("intervals");
		}
        /**
		 * !!! does not work with V7 XML...Returns the netCDF variable name from the variable's XPath (the #var or the variable ID)
		 * @param xpathValue The XPath of the variable
		 * @return var The netCDF variable name
		 * @throws JDOMException
		 */
		public String getVariableName(String xpathValue) throws LASException, JDOMException {
		    String url = getFullDataObjectURL(xpathValue);
		    if ( url.contains("#")) {
		        return url.substring(url.indexOf("#")+1, url.length());
		    } else {
		        /*
		         * We know this is a variable XPath.  If it's "old style" fix it.
		         */
		        if (!xpathValue.contains("@ID")) {
		            String[] parts = xpathValue.split("/");
		            // Throw away index 0 since the string has a leading "/".
		            xpathValue = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
		        }
		        Element var = getElementByXPath(xpathValue);
		        // Might be a remote variable...
		        if ( var == null ) return "";
		        if ( var.getAttributeValue("ID") != null ) {
		            return var.getAttributeValue("ID");
		        } else {
		            return var.getName();
		        }
		    }
		}
        public String getVariableName(String dsID, String varID) throws JDOMException, LASException {
			return getVariableName("/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']");
		}
        public String getVariablePoints(String xpath) throws JDOMException, LASException {
        	Grid grid = getGrid(xpath);
        	return grid.getAttributeValue("points");
		}
        /**
         * Extract the properties group from a variable given its JDOM Element
         * container as an Array List of NameValueBeans
         * @param variable Element an XML variable element
         * @param group the name of the property group to extract
         * @return properties The an array list of properties.
         *
         */
        public HashMap getVariableProperties(Element variable, String group) throws JDOMException {
            HashMap<String, String> propMap = new HashMap<String, String>();
            List propGroups = variable.getChild("properties").getChildren("property_group");
            for (Iterator pgIt = propGroups.iterator(); pgIt.hasNext();) {
                Element propGroupE = (Element) pgIt.next();
                if ( propGroupE.getAttributeValue("type").equals(group)) {
                    List props = propGroupE.getChildren("property");
                    for (Iterator pIt = props.iterator(); pIt.hasNext();) {
                        Element prop  = (Element) pIt.next();
                        String name = prop.getChildText("name");
                        String value = prop.getChildText("value");
                        propMap.put(name, value);
                    }
                }
            }
            return propMap;
        }
        /**
         * Extract the properties element from a variable given its XPath
         * @param xpathValue The XPath of the variable to find.
         * @return properties The properties element.
         *
         */
        public Element getVariableProperties(String xpathValue) throws JDOMException {
            /*
             * We know this is a variable XPath.  If it's "old style" fix it.
             */
            if (!xpathValue.contains("@ID")) {
                String[] parts = xpathValue.split("/");
                // Throw away index 0 since the string has a leading "/".
                xpathValue = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
            }
            Element variable = getElementByXPath(xpathValue);
            return variable.getChild("properties");
        }
        /**
         * Extract the properties group from a variable given its XPath as an
         * Array List of NameValueBeans
         * @param xpathValue The XPath of the variable to find.
         * @return properties The an array list of properties.
         *
         */
        public HashMap getVariableProperties(String xpathValue, String group) throws JDOMException {
            /*
             * We know this is a variable XPath.  If it's "old style" fix it.
             */
            HashMap<String, String> theProps = new HashMap<String, String>();
            if (!xpathValue.contains("@ID")) {
                String[] parts = xpathValue.split("/");
                // Throw away index 0 since the string has a leading "/".
                xpathValue = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
            }
            Element variable = getElementByXPath(xpathValue);
            List propGroups = variable.getChild("properties").getChildren("property_group");
            for (Iterator pgIt = propGroups.iterator(); pgIt.hasNext();) {
                Element propGroupE = (Element) pgIt.next();
                List props = propGroupE.getChildren("property");
                for (Iterator pIt = props.iterator(); pIt.hasNext();) {
                    Element prop  = (Element) pIt.next();
                    String name = prop.getChildText("name");
                    String value = prop.getChildText("value");
                    theProps.put(name, value);
                }
            }
            return theProps;
        }

    /**
	 * Extract a property value from a variable element
	 * Array List of NameValueBeans
	 * @param xpathValue The XPath of the variable to find.
	 * @return value the property value
	 *
	 */
	public String getVariablePropertyValue(Element variable, String group, String property) throws JDOMException {
	    if ( variable != null ) {
	        List propGroups = variable.getChild("properties").getChildren("property_group");
	        for (Iterator pgIt = propGroups.iterator(); pgIt.hasNext();) {
	            Element propGroupE = (Element) pgIt.next();
	            if ( propGroupE.getAttributeValue("type").equals(group )) {
	                List props = propGroupE.getChildren("property");
	                for (Iterator pIt = props.iterator(); pIt.hasNext();) {
	                    Element prop  = (Element) pIt.next();
	                    String name = prop.getChildText("name");
	                    String value = prop.getChildText("value");
	                    if (name.equals(property)) {
	                        return value;
	                    }
	                }
	            }
	        }
	    }
	    return "";
	}
    /**
     * Extract a property value from a variable given its dsid and varid as an
     * Array List of NameValueBeans
     * @param dsid data set id
     * @param varid the variable id
     * @return value the property
     *
     */
	public String getVariablePropertyValue(String dsid, String varid, String group, String property) throws JDOMException {
	    String xpath = "/lasdata/datasets/dataset[@ID='"+dsid+"']/variables/variable[@ID='"+varid+"']";
	    return getVariablePropertyValue(xpath, group, property);
    }
    /**
	 * Extract a property value from a variable given its XPath String
	 * @param xpathValue The XPath of the variable to find.
	 * @return value the property
	 *
	 */
	public String getVariablePropertyValue(String xpathValue, String group, String property) throws JDOMException {
	    /*
	     * We know this is a variable XPath.  If it's "old style" fix it.
	     */

	    if (!xpathValue.contains("@ID")) {
	        String[] parts = xpathValue.split("/");
	        // Throw away index 0 since the string has a leading "/".
	        xpathValue = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
	    }
	    Element variable = getElementByXPath(xpathValue);
	    if ( variable != null ) {
	        List propGroups = variable.getChild("properties").getChildren("property_group");
	        for (Iterator pgIt = propGroups.iterator(); pgIt.hasNext();) {
	            Element propGroupE = (Element) pgIt.next();
	            if ( propGroupE.getAttributeValue("type").equals(group )) {
	                List props = propGroupE.getChildren("property");
	                for (Iterator pIt = props.iterator(); pIt.hasNext();) {
	                    Element prop  = (Element) pIt.next();
	                    String name = prop.getChildText("name");
	                    String value = prop.getChildText("value");
	                    if (name.equals(property)) {
	                        return value;
	                    }
	                }
	            }
	        }
	    }
	    return "";
	}
    /**
     * Returns a Variable object from a LAS XML variable Element.
     * @param variable - the varible element
     * @return var - the variable object
     *
     * Probably won't need this method!!
     *
     *
     *
    public Variable getVariable(Element variable) {
        List attributes = variable.getAttributes();
        Variable var = new Variable();
        String dsid = variable.getParentElement().getParentElement().getAttributeValue("ID");
        var.setDSID(dsid);
        ArrayList<NameValuePair> exattrs = new ArrayList<NameValuePair>();
        for (Iterator atIt = attributes.iterator(); atIt.hasNext();) {
            Attribute attr = (Attribute) atIt.next();
            if ( attr.getName().equals("name") ) {
                var.setName(attr.getValue());
            } else if ( attr.getName().equals("ID")) {
                var.setID(attr.getValue());
            } else {
                exattrs.add(new NameValuePair(attr.getName(), attr.getValue()));
            }
        }
        var.setAttributes(exattrs);
        Element propertiesE = variable.getChild("properties");
        if ( propertiesE != null ) {
            List groups = propertiesE.getChildren("property_group");
            HashMap<String, ArrayList<NameValuePair>> props = new HashMap<String, ArrayList<NameValuePair>>();
            for (Iterator grpsIt = groups.iterator(); grpsIt.hasNext();) {
                Element group = (Element) grpsIt.next();
                String group_name = group.getAttributeValue("type");
                List properties = group.getChildren("property");
                ArrayList<NameValuePair> group_props = new ArrayList<NameValuePair>();
                for (Iterator propIt = properties.iterator(); propIt
                        .hasNext();) {
                    Element property = (Element) propIt.next();
                    NameValuePair prop = new NameValuePair(property
                            .getChildText("name"), property
                            .getChildText("value"));
                    group_props.add(prop);
                }
                props.put(group_name, group_props);
            }
            var.setProperties(props);
        }
        Element grid = variable.getChild("grid");
        var.setGridID(grid.getAttributeValue("IDREF"));
        return var;
    }
    */
    /**
     * Returns list of variables in given a dataset as pmel.tmap.las.util.Dataset objects.
     * @param dsID ID of the dataset for which variables should be listed.
     * @return variables Array list of variables
     */
    public ArrayList<Variable> getVariables(String dsID) throws JDOMException {
        ArrayList<Variable> variables = new ArrayList<Variable>();
        Element dataset = getElementByXPath("/lasdata/datasets/dataset[@ID='"+dsID+"']");
        if ( dataset != null ) {
            List variablesElements = dataset.getChildren("variables");
            for (Iterator vseIt = variablesElements.iterator(); vseIt.hasNext();) {
                Element variablesElement = (Element) vseIt.next();
                List vars = variablesElement.getChildren("variable");
                for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
                    Element variable = (Element) varIt.next();
                    Variable var = new Variable(variable, dsID, dsID, dataset.getAttributeValue("name"));
                    variables.add(var);
                }
            }
        }
        return variables;
    }
    /**
     * Returns a single variable from a given a dataset as a pmel.tmap.las.util.Variable object.
     * @param dsID ID of the dataset that contains the variable
     * @param varID ID of the variable
     * @return variable Variable object
     */
    public Variable getVariable(String dsID, String varID) throws JDOMException {
        String variableXPath = "/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']";
        return getVariableByXPath(variableXPath);
    }
    /**
     * Returns list of variables give a dataset
     * @param dsID ID of the dataset for which variables should be listed.
     * @return variables Array list of variables
     */
    public ArrayList<NameValuePair> getVariablesAsNameValueBeans(String dsID) throws JDOMException {
        ArrayList<NameValuePair> variables = new ArrayList<NameValuePair>();
        Element dataset = getElementByXPath("/lasdata/datasets/dataset[@ID='"+dsID+"']");
        List variablesElements = dataset.getChildren("variables");
        for (Iterator vseIt = variablesElements.iterator(); vseIt.hasNext();) {
            Element variablesElement = (Element) vseIt.next();
            List vars = variablesElement.getChildren("variable");
            for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
                Element variable = (Element) varIt.next();
                String name = variable.getAttributeValue("name");
                String value = variable.getAttributeValue("ID");
                variables.add(new NameValuePair(name, value));
            }
        }
        return variables;
    }
    /**
     * Get a vector variable from a data set that is made up of the components passed in as a list of variable ids.
     * @param dsid
     * @param varids
     * @return
     * @throws JDOMException
     */
    public Variable getVectorVariable(String dsid, ArrayList<String> varids ) throws JDOMException {
    	ArrayList<Variable> variables = getVariables(dsid);
    	for (Iterator varid = variables.iterator(); varid.hasNext();) {
			Variable variable = (Variable) varid.next();
			if ( variable.isVector() ) {
				List<String> compids = variable.getComponents();
				if ( compids.containsAll(varids)) {
					return variable;
				}
			}
		}
    	return null;
    }
    /**
     * Extract the supposedly human interesting title of a variable
     * @param xpathValue The XPath of the variable
     * @return title The title of the variable (Not to quibble but, it's actually the name attibute)
     * @throws JDOMException
     */
    public String getVariableTitle(String xpathValue) throws JDOMException {
        /*
         * We know this is a variable XPath.  If it's "old style" fix it.
         */
        if (!xpathValue.contains("@ID")) {
            String[] parts = xpathValue.split("/");
            // Throw away index 0 since the string has a leading "/".
            xpathValue = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
        }
        Element variableE = getElementByXPath(xpathValue);
        if ( variableE != null ) {
        	return variableE.getAttributeValue("name");
        } else {
        	return "";
        }
    }
    public ArrayList<View> getViewsByXpath(String[] xpaths) throws JDOMException, LASException {
    	Set<View> viewSet = new HashSet<View>();
    	ArrayList<View> views = new ArrayList<View>();
    	for (int i = 0; i < xpaths.length; i++) {
    		String dsid = getDSIDfromXPath(xpaths[i]);
    		String varid = getVarIDfromXPath(xpaths[i]);
    		viewSet.addAll(getViewsByDatasetAndVariable(dsid, varid));
    	}
        for (Iterator iterator = viewSet.iterator(); iterator.hasNext();) {
			View view = (View) iterator.next();
			views.add(view);
		}
    	return views;
    }
    public ArrayList<View> getViewsByDatasetAndVariable(String dsID, String varID) throws JDOMException, LASException {
        String variableXPath = "/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']";
        String ui_default = "";

        ui_default = getVariablePropertyValue(variableXPath,"ui", "default");

        ArrayList<View> views = new ArrayList<View>();

        if ( ui_default != null && !ui_default.equals("") ) {
            // This will be a list of views determined by the installer.  This list
            // will not be modified.  The installer is responsible for picking sensible
            // views for the data.
            ui_default = ui_default.substring(ui_default.indexOf("#")+1, ui_default.length());
            views = getViewsByDefault(ui_default);
        } else {
            // This returns all possible views.
            ui_default = null;
            ArrayList<View> allViews = getViewsByDefault(ui_default);
            // Now filter them according to the places where the data variable
            // is defined on an interval.
            String intervals = getVariableIntervals(variableXPath);

            // Create an ArrayList with all combinations
            ArrayList<String> combos = combo(intervals);

            for (Iterator viewIt = allViews.iterator(); viewIt.hasNext();) {
                View view = (View) viewIt.next();
                String view_value = view.getValue();

                // If the combination of interval axes matches one of the
                // views add it to the collection of acceptable views.

                for (Iterator comboIt = combos.iterator(); comboIt.hasNext();) {
                    String combo = (String) comboIt.next();
                    if ( combo.equals(view_value) ) {
                        views.add(view);
                    }
                }

            }
        }
        if ( views.size() <= 0 ) {
        	String message = "No views found for dsID="+dsID+" and varID="+varID+".";
        	if ( ui_default != null && !ui_default.equals("") ) {
        		message = message + "  UI Default is "+ui_default+". Confirm UI Default definition XML is included in ui.xml.";
        	}
        	throw new LASException(message);
        }
        return views;
    }
    /**
     * @param ui_default
     * @return
     */
    public ArrayList<View> getViewsByDefault(String ui_default) throws JDOMException {
        ArrayList <View> views = new ArrayList<View>();
        Element def = getUIDefault(ui_default);
        if ( def != null ) {
            Element map = getUIMap(def, "views");
            if ( map != null ) {
                Element menu = map.getChild("menu");
                String href = menu.getAttributeValue("href");
                menu = getElementByXPath("/lasdata/lasui/menus/menu[@name='"+href.substring(1)+"']");
                List ifmenusElements = menu.getChildren("ifitem");
                for (Iterator ifItemIt = ifmenusElements.iterator(); ifItemIt.hasNext();) {
                    Element ifitem = (Element) ifItemIt.next();
                    Element view = new Element("view");
                    view.setAttribute("name", ifitem.getTextNormalize());
                    view.setAttribute("value", ifitem.getAttributeValue("view"));
                    List attributes = ifitem.getAttributes();
                    for (Iterator attrIt = attributes.iterator(); attrIt.hasNext();) {
                        Attribute attr = (Attribute) attrIt.next();
                        // TODO this really implies we need to rework the XML so that these are expressed with XML that matches
                        // what we want it to look like in the end.
                        if (!attr.getName().equals("value") && !attr.getName().equals("values") && !attr.getName().equals("view") ) {
                            view.setAttribute((Attribute) attr.clone());
                        }
                    }
                    views.add(new View(view));
                }
            }
        }
        return views;
    }
    /**
     * Returns true if a T axis is defined for this variable.
     * @param varpath XPath of the variable to check
     * @return hasZ boolean
     */
    public boolean hasT(String varpath) throws JDOMException {
        /*
         * We know this is a variable XPath.  If it's "old style" fix it.
         */
        if (!varpath.contains("@ID")) {
            String[] parts = varpath.split("/");
            // Throw away index 0 since the string has a leading "/".
            varpath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
        }
        Element variable = getElementByXPath(varpath);
        if (variable == null) {
            return false;
        }
        String gridID = variable.getChild("grid").getAttributeValue("IDREF");
        Element grid = getElementByXPath("/lasdata/grids/grid[@ID='"+gridID+"']");
        if (grid == null) {
            return false;
        }
        List axes = grid.getChildren("axis");
        for (Iterator axIt = axes.iterator(); axIt.hasNext();) {
            Element axis = (Element) axIt.next();
            String axisID = axis.getAttributeValue("IDREF");
            axis = getElementByXPath("/lasdata/axes/axis[@ID='"+axisID+"']");
            String type = axis.getAttributeValue("type");
            if (type.equals("t")) {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns true if a T axis is defined for this variable.
     * @param dsID ID of the dataset to check
     * @param varID ID of the variable to check
     * @return hasZ boolean
     */
    public boolean hasT(String dsID, String varID) throws JDOMException {
        String varpath = "/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']";
        return hasT(varpath);
    }
    /**
     * Returns true if a Z axis is defined for this variable.
     * @param varpath XPath of the variable to check
     * @return hasZ boolean
     * @throws JDOMException
     */
    public boolean hasZ(String varpath) throws JDOMException {
        /*
         * We know this is a variable XPath.  If it's "old style" fix it.
         */
        if (!varpath.contains("@ID")) {
            String[] parts = varpath.split("/");
            // Throw away index 0 since the string has a leading "/".
            varpath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
        }
        Element variable = getElementByXPath(varpath);
        if (variable == null) {
            return false;
        }
        String gridID = variable.getChild("grid").getAttributeValue("IDREF");
        Element grid = getElementByXPath("/lasdata/grids/grid[@ID='"+gridID+"']");
        if (grid == null) {
            return false;
        }
        List axes = grid.getChildren("axis");
        for (Iterator axIt = axes.iterator(); axIt.hasNext();) {
            Element axis = (Element) axIt.next();
            String axisID = axis.getAttributeValue("IDREF");
            axis = getElementByXPath("/lasdata/axes/axis[@ID='"+axisID+"']");
            String type = axis.getAttributeValue("type");
            if (type.equals("z")) {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns true if a Z axis is defined for this variable.
     * @param dsID ID of the dataset to check
     * @param varID ID of the variable to check
     * @return hasZ boolean
     */
    public boolean hasZ(String dsID, String varID) throws JDOMException {
        String varpath = "/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']";
        return hasZ(varpath);
    }

    public boolean isRegular(String dsID, String varID) throws JDOMException {
        boolean reg = false;
        Element variable = getElementByXPath("/lasdata/datasets/dataset[@ID='"+dsID+"']"+"/variables/variable"+"[@ID='"+varID+"']");
        String grid_type = variable.getAttributeValue("grid_type");
        if ( grid_type.equals("regular")) {
            reg = true;
        }
        return reg;
    }
    /**
     * Merge all of the variable, dataset and global properties for each variable.  Variable properties override dataset
     * properties which in turn override "global" properties (those defined under the /lasdata element).
     *
     */
    public void mergeProperites() {
        List dsTags = this.getRootElement().getChildren("datasets");
        for (Iterator dsTagIt = dsTags.iterator(); dsTagIt.hasNext();) {
            Element dsTag = (Element) dsTagIt.next();
            List datasets = dsTag.getChildren();
            for (Iterator dsIt = datasets.iterator(); dsIt.hasNext();) {
                Element dataset = (Element) dsIt.next();
                List varTags = dataset.getChildren("variables");
                for (Iterator varTagsIt = varTags.iterator(); varTagsIt.hasNext();) {
                    Element varTag = (Element)varTagsIt.next();
                    List variables = varTag.getChildren();
                    for (Iterator varsIt = variables.iterator(); varsIt.hasNext();) {
                        Element var = (Element) varsIt.next();
                        Element properties = var.getChild("properties");
                        if ( properties != null ) {
                            var.setContent(var.indexOf(properties), getMergedProperties(var));
                        } else {
                            var.addContent(getMergedProperties(var));
                        }
                    }
                }
            }
        }
    }
    /**
     * Descends the dataset and variable tree and set the grid_type attribute
     * if it is not already set.
     * @throws JDOMException
     */
    public void setGridType(Element variable) throws JDOMException {
        String grid_type = variable.getAttributeValue("grid_type");
        if ( grid_type == null || grid_type.equals("") ) {
            HashMap<String, String> propMap = getVariableProperties(variable, "database_access");
            if ( propMap.size() > 0 ) {
                variable.setAttribute("grid_type","scattered");
            } else {
                variable.setAttribute("grid_type", "regular");
            }
        }
    }
    private void setIDs(List categories) throws UnsupportedEncodingException, JDOMException {
        for (Iterator catIt = categories.iterator(); catIt.hasNext();) {
            Element category = (Element) catIt.next();
            List<Element> catids = category.getChildren("catid");
            for (Iterator catidIt = catids.iterator(); catidIt.hasNext();) {
				Element catid = (Element) catidIt.next();
				String id = catid.getAttributeValue("ID");
				id = JDOMUtils.MD5Encode(getBaseServerURL()) + Constants.NAME_SPACE_SPARATOR + id;
				catid.setAttribute("ID", id);
			}
            String ID = category.getAttributeValue("ID");
            if ( ID == null ) {
                String name = category.getAttributeValue("name");
                try {
                	String parents = getParentNames(category);
                    ID = JDOMUtils.MD5Encode(name + parents);
                } catch (UnsupportedEncodingException e) {
                    ID = String.valueOf(Math.random());
                }
                if ( allowsSisters() ) {
                	ID = JDOMUtils.MD5Encode(getBaseServerURL()) + Constants.NAME_SPACE_SPARATOR + ID;
                }
                category.setAttribute("ID", ID);
            } else {
            	// Pre-pend the server key to any existing ID
            	if ( allowsSisters() ) {
                	ID = JDOMUtils.MD5Encode(getBaseServerURL()) + Constants.NAME_SPACE_SPARATOR + ID;
                	category.setAttribute("ID", ID);
                }
            }
            List subcategories = category.getChildren("category");
            if ( subcategories.size() > 0 ) {
                setIDs(subcategories);
            }
        }
    }
    private String getParentNames(Element category) {
    	Object parent = category.getParent();
    	if ( parent instanceof Element ) {
    		Element rent = (Element) parent;
    		if ( rent.getName().equals("category") ) {
    		    return rent.getAttributeValue("name") + getParentNames(rent);
    		} else {
    			return "";
    		}
    	} else {
    		return "";
    	}
    }
    /**
	 * Helper method to set the output directory if need be
	 * @param dir The path to the directory
	 */
	public void setOutputDir(String dir) {
	   Element output_dir = this.getRootElement().getChild("output_dir");
	   if (output_dir == null) {
	       output_dir = new Element("output_dir");
	       this.getRootElement().addContent(output_dir);
	   }
	   output_dir.setText(dir);
	}

	public ArrayList<Category> getTimeSeriesDatasets() throws LASException {
		ArrayList<Category> time_series = new ArrayList<Category>();
		if (hasCategories()) {
			AttributeFilter attribute_filter = new AttributeFilter("category", "group_type", "time_series");
			Iterator catIt = getRootElement().getDescendants(attribute_filter);
			while( catIt.hasNext() ) {
				Element category = (Element) ((Element)catIt.next()).clone();
				Element category_container = new Element("category");
                List attributes = category.getAttributes();
                for (Iterator attrIt = attributes.iterator(); attrIt.hasNext();) {
                    Attribute attr = (Attribute) attrIt.next();
                    category_container.setAttribute(attr.getName(), attr.getValue());
                }
                List filters = category.getChildren("filter");
                for (Iterator filterIt = filters.iterator(); filterIt.hasNext();) {
					Element filter = (Element) filterIt.next();
					List<Dataset> group_list = getDatasets(filter);
					for (Iterator groupIt = group_list.iterator(); groupIt
							.hasNext();) {
						Dataset dataset = (Dataset) groupIt.next();
						category_container.addContent(dataset.getElement());
					}
				}
                time_series.add(new Category(category_container));
			}
		} else {
			AttributeFilter filter = new AttributeFilter("dataset", "group_type", "time_series");
			Iterator dsIt= getRootElement().getDescendants(filter);
			HashMap<String, ArrayList<Dataset>> time_series_groups = new HashMap<String, ArrayList<Dataset>>();
			while (dsIt.hasNext()) {
				Element dataset = (Element)((Element) dsIt.next()).clone();
				String ts_id = dataset.getAttributeValue("group_id");
				ArrayList<Dataset> members = time_series_groups.get(ts_id);
				if ( members == null) {
					members = new ArrayList<Dataset>();
					time_series_groups.put(ts_id, members);
				}
				members.add(new Dataset(dataset));
			}
			for (Iterator gIt = time_series_groups.keySet().iterator(); gIt.hasNext();) {
				String group = (String) gIt.next();
				ArrayList<Dataset> members = time_series_groups.get(group);
				Element category = new Element("category");
				category.setAttribute("id", group);
				for (Iterator memIt = members.iterator(); memIt.hasNext();) {
					Dataset member = (Dataset) memIt.next();
					category.addContent(member.getElement());
					category.setAttribute("name", member.getAttributeValue("group_name"));
					category.setAttribute("ID", member.getAttributeValue("group_id"));
				}
				time_series.add(new Category(category));
			}
		}
		return time_series;
	}
	/**
     * Extract data sets based on a &lt;filter&gt; element from the config, used for group_type="time_series" and group_type="ensemble" categories.
     * @param filter A category filter element to be used to select variables from the configuration.
     * @return the data set that matches the filter
	 * @throws LASException
     */
    private List<Dataset> getDatasets(Element filter) throws LASException {
        List<Dataset> container_datasets = new ArrayList<Dataset>();
        String action = filter.getAttributeValue("action");

        String name_contains = filter.getAttributeValue("contains");
        String name_equals = filter.getAttributeValue("equals");
        String tag_contains = filter.getAttributeValue("contains-tag");
        String tag_equals = filter.getAttributeValue("equals-tag");

        if ( action.equals("apply-dataset") ) {

            List datasets = getRootElement().getChildren("datasets");

            for (Iterator datasetsIt = datasets.iterator(); datasetsIt.hasNext();) {
                Element datasetsE = (Element) datasetsIt.next();
                List memberDatasets = datasetsE.getChildren("dataset");
                for (Iterator memberDSIt = memberDatasets.iterator(); memberDSIt.hasNext();) {
                    Element dataset = (Element) memberDSIt.next();
                    Element container_dataset_element = (Element) dataset.clone();
                    String name = dataset.getAttributeValue("name");
                    String ID = dataset.getAttributeValue("ID");
                    if ( (name_contains != null && name.contains(name_contains)) ||
                         (name_equals != null && name.equals(name_equals)) ||
                         (tag_contains != null && ID.contains(tag_contains)) ||
                         (tag_equals != null && ID.equals(tag_equals)) ) {
                        container_datasets.add(new Dataset(container_dataset_element));
                    }
                }
            }
        } else if ( action.equals("apply-variable") ) {
        	throw new LASException("Should not try to filter groups using an 'apply-variable' filter.");
        }
        return container_datasets;
    }
	public boolean hasCategories() {
		List tops = getRootElement().getChildren("las_categories");
		if ( tops != null && tops.size() > 0 ) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Help application to extract the addXML properties as a map
	 * The proprerites group element.
	 * The resulting HashMap
	 */
	private HashMap<String, String> addXMLProperties(Element properties) {

		HashMap<String, String> addXMLproperties = new HashMap<String, String>();

		List groups = convertProperties(properties);


		for (Iterator grpIt = groups.iterator(); grpIt.hasNext();) {
			Element group = (Element) grpIt.next();
			if (group.getAttributeValue("type").equals("addXML")) {
				List props = group.getChildren("property");
				for (Iterator propIt = props.iterator(); propIt
						.hasNext();) {
					Element prop = (Element) propIt.next();
					String name = prop.getChildText("name");
					String value = prop.getChildText("value");
					addXMLproperties.put(name, value);
				}
			}
		}
		return addXMLproperties;
	}
	/**
	 * Create or update LAS metadata directly from a netCDF or THREDDS data source.
	 * @param update
	 * @param cache
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	public long addXML(boolean update, Cache cache) throws IOException, JDOMException {

		DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter ymd = DateTimeFormat.forPattern("yyyy-MM-dd");
		ArrayList<Element> src_datasets = new ArrayList<Element>();
		ArrayList<Element> src_grids = new ArrayList<Element>();
		ArrayList<Element> src_axes = new ArrayList<Element>();
		long nextUpdate = 999999999999999999l;
		HashMap<String, ArrayList<Element>> ds_children = new HashMap<String, ArrayList<Element>>();
		ArrayList<CategoryBean> categoryList = new ArrayList<CategoryBean>();
		if ( update ) {
			// Recreate only those XML stubs that are out of date.
			List datasets = getRootElement().getChildren("datasets");
			for (Iterator datasetsIt = datasets.iterator(); datasetsIt.hasNext();) {
				Element datasetsE = (Element) datasetsIt.next();
				Element dsetsE = new Element("datasets");
				List ds_list = datasetsE.getChildren("dataset");
				for (Iterator dsIt = ds_list.iterator(); dsIt.hasNext();) {
					Element dataset = (Element) dsIt.next();
					String src = dataset.getAttributeValue("src");
					String src_type = dataset.getAttributeValue("src_type");
					String update_time = dataset.getAttributeValue("update_time");
					String update_interval = dataset.getAttributeValue("update_interval");

					/* property edits for addXML configuration... */
					HashMap<String, String> addXMLprops = new HashMap<String, String>();
					Element properties = dataset.getChild("properties");
					if ( properties != null ) {
						addXMLprops = addXMLProperties(properties);
					}

					if (src != null && src_type != null ) {
						String src_key = JDOMUtils.MD5Encode(src);
						String ds_filename = getOutputDir()+File.separator+"las_datasets_"+src_key+".xml";
						String grids_filename = getOutputDir()+File.separator+"las_grids_"+src_key+".xml";
						String axes_filename = getOutputDir()+File.separator+"las_axes_"+src_key+".xml";

						boolean reconstruct = false;
						if ( update_time != null && update_interval != null ) {

							File ds_file = cache.getFile(ds_filename, Cache.GET_CACHE);
							if ( ds_file != null ) {
								LASDocument ds_doc = new LASDocument();
								JDOMUtils.XML2JDOM(ds_file, ds_doc);
								String expires_att = dataset.getAttributeValue("expires");
								DateTime expires = df.parseDateTime(expires_att);
								if ( expires.isBeforeNow() ) {
									Vector<DatasetsGridsAxesBean> dgabs = updateSrc(src, src_type, update_time, update_interval, addXMLprops);
									int src_index = 1;
									for (Iterator dgabsIt = dgabs.iterator(); dgabsIt.hasNext();) {
										DatasetsGridsAxesBean dgab = (DatasetsGridsAxesBean) dgabsIt.next();
										long n = addSrc(dgab, src_index, dsetsE, dataset, src_datasets, src_grids, src_axes, ds_children, cache, src_key);
										src_index++;
										nextUpdate = Math.min(n, nextUpdate);
										if ( src_type.equalsIgnoreCase("netCDF") ) {
											if ( addXMLprops.get("category") != null && addXMLprops.get("category").equalsIgnoreCase("true") ) {
												categoryList.addAll(makeCategories(dgab));
											}
										}
									}
									if ( src_type.equalsIgnoreCase("THREDDS") && addXMLprops.get("category") != null && addXMLprops.get("category").equalsIgnoreCase("true") ) {
										String esgS = addXMLprops.get("esg");
										boolean esg = false;
										if ( esgS != null ) {
											esg = Boolean.valueOf(esgS).booleanValue();
										}
										categoryList.addAll(makeCategoriesFromTHREDDS(src, esg));
									}
								} else {
									// reconstruct src from ds, grid and axes file
									reconstruct = true;
								}
							}
						} else {
							// Build from the parts, if available or build from the src.
							reconstruct = true;
						}
						if ( reconstruct ) {
							File ds_file = cache.getFile(ds_filename, Cache.GET_CACHE);
							File grids_file = cache.getFile(grids_filename, Cache.GET_CACHE);
							File axes_file = cache.getFile(axes_filename, Cache.GET_CACHE);
							if ( ds_file != null && grids_file != null && axes_file != null &&
									ds_file.exists() && grids_file.exists() && axes_file.exists() ) {
								LASDocument ds_doc = new LASDocument();
								LASDocument grids_doc = new LASDocument();
								LASDocument axes_doc = new LASDocument();
								JDOMUtils.XML2JDOM(ds_file, ds_doc);
								JDOMUtils.XML2JDOM(grids_file, grids_doc);
								JDOMUtils.XML2JDOM(axes_file, axes_doc);

								getRootElement().addContent((Element) ds_doc.getRootElement().clone());
								getRootElement().addContent((Element) grids_doc.getRootElement().clone());
								getRootElement().addContent((Element) axes_doc.getRootElement().clone());


							} else {
								Vector<DatasetsGridsAxesBean> dgabs = updateSrc(src, src_type, update_time, update_interval, addXMLprops);
								int src_index = 1;
								for (Iterator dgabsIt = dgabs.iterator(); dgabsIt.hasNext();) {
									DatasetsGridsAxesBean dgab = (DatasetsGridsAxesBean) dgabsIt.next();
									long n = addSrc(dgab, src_index, dsetsE, dataset, src_datasets, src_grids, src_axes, ds_children, cache, src_key);
									src_index++;
									nextUpdate = Math.min(n, nextUpdate);
									if ( src_type.equalsIgnoreCase("netCDF") ) {
										if ( addXMLprops.get("categories") != null && addXMLprops.get("categories").equalsIgnoreCase("true") ) {
											categoryList.addAll(makeCategories(dgab));
										}
									}
								}
								if ( src_type.equalsIgnoreCase("THREDDS") && addXMLprops.get("category") != null && addXMLprops.get("category").equalsIgnoreCase("true") ) {
									String esgS = addXMLprops.get("esg");
									boolean esg = false;
									if ( esgS != null ) {
										esg = Boolean.valueOf(esgS).booleanValue();
									}
									categoryList.addAll(makeCategoriesFromTHREDDS(src, esg));
								}
							}
						}
					}
				}
			}
		} else {
			// Since we're looping the XML we have accumulate new content (like these categories) and add it
			// outside the loop.

			// Create all the XML stubs if they are not in the cache or if they are out of date.
			List datasets = getRootElement().getChildren("datasets");
			for (Iterator datasetsIt = datasets.iterator(); datasetsIt.hasNext();) {
				Element datasetsE = (Element) datasetsIt.next();
				Element dsetsE = new Element("datasets");
				List ds_list = datasetsE.getChildren("dataset");
				for (Iterator dsIt = ds_list.iterator(); dsIt.hasNext();) {
					Element dataset = (Element) dsIt.next();
					String src = dataset.getAttributeValue("src");
					String src_type = dataset.getAttributeValue("src_type");
					String update_time = dataset.getAttributeValue("update_time");
					String update_interval = dataset.getAttributeValue("update_interval");
					Element properties = dataset.getChild("properties");
					HashMap<String, String> addXMLprops = new HashMap<String, String>();
					if ( properties != null ) {
					    addXMLprops = addXMLProperties(properties);
					}
					if (src != null && src_type != null) {
						String src_key = JDOMUtils.MD5Encode(src);
						Vector<DatasetsGridsAxesBean> dgabs = updateSrc(src, src_type, update_time, update_interval, addXMLprops);
						int src_index = 1;
						for (Iterator dgabsIt = dgabs.iterator(); dgabsIt.hasNext();) {
							DatasetsGridsAxesBean dgab = (DatasetsGridsAxesBean) dgabsIt.next();
							long n = addSrc(dgab, src_index, dsetsE, dataset, src_datasets, src_grids, src_axes, ds_children, cache, src_key);
							src_index++;
	                        nextUpdate = Math.min(n, nextUpdate);
	                        if ( src_type.equalsIgnoreCase("netCDF") ) {
								if ( addXMLprops.get("categories") != null && addXMLprops.get("categories").equalsIgnoreCase("true") ) {
									categoryList.addAll(makeCategories(dgab));
								}
							}
						}
						if ( src_type.equalsIgnoreCase("THREDDS") && addXMLprops.get("category") != null && addXMLprops.get("category").equalsIgnoreCase("true") ) {
							String esgS = addXMLprops.get("esg");
							boolean esg = false;
							if ( esgS != null ) {
								esg = Boolean.valueOf(esgS).booleanValue();
							}
							categoryList.addAll(makeCategoriesFromTHREDDS(src, esg));
						}
					}
				}
			}
		}
		EmptySrcDatasetFilter src_dataset_filter = new EmptySrcDatasetFilter();
		List remove_datasets = getRootElement().getChildren("datasets");
		for (Iterator datasetsIt = remove_datasets.iterator(); datasetsIt.hasNext();) {
			Element datasetsE = (Element) datasetsIt.next();
			datasetsE.removeContent(src_dataset_filter);
		}

		for (Iterator src_dsIt = src_datasets.iterator(); src_dsIt.hasNext();) {
			Element ds = (Element) src_dsIt.next();
			List dsets = ds.getChildren();
			for (Iterator dsetsIt = dsets.iterator(); dsetsIt.hasNext();) {
				Element d = (Element) dsetsIt.next();
				ArrayList<Element> children = ds_children.get(d.getName());
				if ( children != null && children.size() > 0 ) {
					for (Iterator childIt = children.iterator(); childIt.hasNext();) {
						Content child = (Content) childIt.next();
						d.addContent((Content)child.clone());
					}
				}
			}
		}
		// Only add if you found something to add...
		if (src_datasets.size() > 0 ) {
			getRootElement().addContent(src_datasets);
		}
		if ( src_grids.size() > 0 ) {
			getRootElement().addContent(src_grids);
		}
		if ( src_axes.size() > 0 ) {
			getRootElement().addContent(src_axes);
		}
		if ( src_datasets.size() > 0 ) {
			getRootElement().removeContent(src_dataset_filter);
		}
		if ( categoryList.size() > 0 ) {
			addCategories(categoryList);
		}
		return nextUpdate;

	}
	/**
	 * Makes and adds categories from a DatasetsGridsAxesBean probably from a single netCDF data source.
	 * @param dgab
	 */
	public ArrayList<CategoryBean> makeCategories(DatasetsGridsAxesBean dgab) {
		ArrayList<CategoryBean> categories = new ArrayList<CategoryBean>();
		List<DatasetBean> dsbeans = dgab.getDatasets();
		for (Iterator dsbIt = dsbeans.iterator(); dsbIt.hasNext();) {
			DatasetBean dsb = (DatasetBean) dsbIt.next();
			CategoryBean cat = new CategoryBean();
			cat.setName(dsb.getName());
			FilterBean filter = new FilterBean();
			filter.setAction("apply-dataset");
			filter.setContainstag(dsb.getElement());
			cat.addFilter(filter);
			categories.add(cat);
		}
		return categories;
	}
	/**
	 * Reads a THREDDS catalog and makes the categories to match the catalog.
	 * @param src
	 */
	public ArrayList<CategoryBean> makeCategoriesFromTHREDDS(String src, boolean esg) {
		Vector categories = new Vector();
		CategoryBean top = new CategoryBean();
		if ( esg ) {
			top.setName("ESG Catalog");
			String base = src.substring(0, src.lastIndexOf("/")+1);
			SAXParserFactory factory = SAXParserFactory.newInstance();		
			CatalogRefHandler esgCatalogHandler = new CatalogRefHandler();
			SAXParser parser;
			try {
				factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
				factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
				parser = factory.newSAXParser();			
				parser.parse(src, esgCatalogHandler);
			} catch (ParserConfigurationException e) {
				log.error("Unable to make ESG Categories: "+e.getMessage());
			} catch (SAXException e) {
				log.error("Unable to make ESG Categories: "+e.getMessage());
			} catch (IOException e) {
				log.error("Unable to make ESG Categories: "+e.getMessage());
			}
			Map<String, String> catalogs = esgCatalogHandler.getCatalogs();
			Map<String, String> lasCatalogs = new HashMap<String, String>();
			for (Iterator catIt = catalogs.keySet().iterator(); catIt.hasNext();) {
				String name = (String) catIt.next();
				String url = (String) catalogs.get(name);
				InvCatalogFactory thredds_factory = new InvCatalogFactory("default", false);
				InvCatalog catalog = (InvCatalog) thredds_factory.readXML(base+url);
				if ( ADDXMLProcessor.containsLASDatasets(catalog) ) {
					lasCatalogs.put(name, url);
				}
			}
			System.out.println(lasCatalogs.size()+" out of "+catalogs.size()+" contain LAS data.");
			for (Iterator catIt = lasCatalogs.keySet().iterator(); catIt.hasNext();) {
				String name = (String) catIt.next();
				String url = (String) lasCatalogs.get(name);
				System.out.println(name+","+url);
			}
			for (Iterator catIt = lasCatalogs.keySet().iterator(); catIt.hasNext();) {
				
				String name = (String) catIt.next();
				String url = (String) lasCatalogs.get(name);
				InvCatalogFactory thredds_factory = new InvCatalogFactory("default", false);
				InvCatalog catalog = (InvCatalog) thredds_factory.readXML(base+url);
				CategoryBean cb = ADDXMLProcessor.processESGCategories(catalog);
				categories.add(cb);				
			}			
		} else {
			InvCatalogFactory factory = new InvCatalogFactory("default", false);
			InvCatalog catalog = (InvCatalog) factory.readXML(src);

			String topName = catalog.getName();
			if (topName != null) {
				top.setName(catalog.getName());
			}
			else {
				top.setName(catalog.getUriString());
			}


			List ThreddsDatasets = catalog.getDatasets();
			Iterator di = ThreddsDatasets.iterator();
		
			while (di.hasNext() ) {
				InvDataset ThreddsDataset = (InvDataset) di.next();
				if (ThreddsDataset.hasNestedDatasets()) {
					
					CategoryBean cb = ADDXMLProcessor.processCategories(ThreddsDataset);		 
					categories.add(cb);
					
				}
			}
		}
		top.setCategories(categories);
		ArrayList<CategoryBean> one = new ArrayList<CategoryBean>();
		one.add(top);
		return one;
	}
	public long addSrc(DatasetsGridsAxesBean dgab, int src_index, Element dsetsE, Element dataset, ArrayList<Element> src_datasets, ArrayList<Element> src_grids, ArrayList<Element> src_axes, HashMap<String, ArrayList<Element>> ds_children, Cache cache, String src_key) throws UnsupportedEncodingException {
		long nextUpdate = 999999999999999999l;
		List<DatasetBean> ds_beans = dgab.getDatasets();
		for (Iterator dsbIt = ds_beans.iterator(); dsbIt.hasNext();) {
			DatasetBean dsb = (DatasetBean) dsbIt.next();
			if ( dsb.getNextUpdate() > 0 && dsb.getNextUpdate() < nextUpdate ) {
				nextUpdate = dsb.getNextUpdate();
			}
			System.out.println("Converting source data set to XML: "+dsb.getElement());
			Element datasetFromSrc = dsb.toXml();
			List attributes = dataset.getAttributes();
			for (Iterator attrIt = attributes.iterator(); attrIt.hasNext();) {
				Attribute attr = (Attribute) attrIt.next();
				String name = attr.getName();
				String value = attr.getValue();
				// The name you get from the data source should be better, so keep it.
				if ( !name.equals("name") ) {
					datasetFromSrc.setAttribute(name, value);
				}
			}
			// save in a hash and add at the very end.  Grrrr.
			ArrayList<Element> kids = new ArrayList<Element>();
			for (Iterator children = dataset.getChildren("properties").iterator(); children.hasNext();) {
				Element child = (Element) children.next();
				kids.add(child);
			}
			ds_children.put(dsb.getElement(), kids);

			dsetsE.addContent(datasetFromSrc);
			if ( !src_datasets.contains(dsetsE) ) {
			    src_datasets.add(dsetsE);
			}
		}

		List<GridBean> g_beans = dgab.getGrids();
		Element grids = new Element("grids");
		for (Iterator gbIt = g_beans.iterator(); gbIt.hasNext();) {
			GridBean gb = (GridBean) gbIt.next();
			grids.addContent(gb.toXml());
		}
		src_grids.add(grids);

		List<AxisBean> a_beans = dgab.getAxes();
		Element axes = new Element("axes");
		for (Iterator abIt = a_beans.iterator(); abIt.hasNext();) {
			AxisBean ab = (AxisBean) abIt.next();
			axes.addContent(ab.toXml());
		}
		src_axes.add(axes);

		org.jdom.Document doc = ADDXMLProcessor.createXMLfromDatasetsGridsAxesBean(dgab);
		String ds_filename = getOutputDir()+File.separator+"las_datasets_"+src_key+"_"+src_index+".xml";
		String grids_filename = getOutputDir()+File.separator+"las_grids_"+src_key+"_"+src_index+".xml";
		String axes_filename = getOutputDir()+File.separator+"las_axes_"+src_key+"_"+src_index+".xml";
		Element dsE = (Element) doc.getRootElement().getChild("datasets").clone();
		cache.addDocToCache(new LASDocument(dsE), ds_filename);
		Element gE = (Element) doc.getRootElement().getChild("grids").clone();
		cache.addDocToCache(new LASDocument(gE), grids_filename);
		Element aE = (Element) doc.getRootElement().getChild("axes").clone();
		cache.addDocToCache(new LASDocument(aE), axes_filename);
		return nextUpdate;
	}
	public Vector<DatasetsGridsAxesBean> updateSrc(String src, String src_type, String update_time, String update_interval, HashMap<String, String> options) {
		DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter ymd = DateTimeFormat.forPattern("yyyy-MM-dd");
		DatasetsGridsAxesBean dgab = null;
		ADDXMLProcessor myAddXML = new ADDXMLProcessor();
		myAddXML.setOptions(options);
		Vector<DatasetsGridsAxesBean> beans = new Vector<DatasetsGridsAxesBean>();
		if ( src_type.equalsIgnoreCase("netcdf") ) {

		
			dgab = myAddXML.createBeansFromNetcdfDataset(src, false, null);
			if ( dgab == null ) {
				log.error("Unable to make configuration from "+src);
				return beans;
			}
			

			String created = null;
			String expires = null;
			long next_update = -1;
			if ( update_time != null && update_interval != null ) {
				try {
					DateTime nt = new DateTime();
					created = df.print(nt);

					String expire_time = ymd.print(nt) + " " + update_time+":00";
					if ( update_interval.toLowerCase(Locale.ENGLISH).contains("milli") ||
						 update_interval.toLowerCase(Locale.ENGLISH).contains("sec") ||
						 update_interval.toLowerCase(Locale.ENGLISH).contains("min") ||
						 update_interval.toLowerCase(Locale.ENGLISH).contains("hour") ||
						 update_interval.toLowerCase(Locale.ENGLISH).contains("day") ||
						 update_interval.toLowerCase(Locale.ENGLISH).contains("week") ) {
						String[] interval = update_interval.split(" ");
						int n = Integer.valueOf(interval[0]).intValue();
						String units = interval[1];
						int millis = n;
						if ( units.toLowerCase(Locale.ENGLISH).contains("milli") ) {
							millis = n;
						} else if ( units.toLowerCase(Locale.ENGLISH).contains("sec") ) {
							millis = n*1000;
						} else if ( units.toLowerCase(Locale.ENGLISH).contains("min") ) {
							millis = n*60*1000;
						} else if ( units.toLowerCase(Locale.ENGLISH).contains("hour") ) {
							millis = n*60*60*1000;
						} else if ( units.toLowerCase(Locale.ENGLISH).contains("day") ) {
							millis = n*24*60*60*1000;
						} else if ( units.toLowerCase(Locale.ENGLISH).contains("week") ) {
							millis = n*7*24*60*60*1000;
						}
						Period p = new Period(0, 0, 0, millis);
						DateTime start_dt = df.parseDateTime(expire_time);
						DateTime expires_dt = start_dt.plus(p);
						// Insure that the expires date is in the future.
						while (expires_dt.isBefore(nt) ) {
							expires_dt = expires_dt.plus(p);
						}
						expires = df.print(expires_dt);

						if ( start_dt.isAfter(nt) ) {
							next_update = expires_dt.getMillis()-start_dt.getMillis();
						} else {
							next_update = expires_dt.getMillis()-nt.getMillis();
						}

					}
				} catch (Exception e) {
                    log.error("Unable to set update creation update times");
				}
				List<DatasetBean> ds_beans = dgab.getDatasets();
				for (Iterator dsbIt = ds_beans.iterator(); dsbIt.hasNext();) {
					DatasetBean dsb = (DatasetBean) dsbIt.next();
					dsb.setUpdate_interval(update_interval);
					dsb.setUpdate_time(update_time);
					dsb.setCreated(created);
					dsb.setExpires(expires);
					dsb.setNextUpdate(next_update);
				}
			}
			beans.add(dgab);
		} else if ( src_type.equalsIgnoreCase("THREDDS") ) {
			InvCatalogFactory thredds_factory = new InvCatalogFactory("default", false);
			if ( myAddXML.isEsg() ) {
				String base = src.substring(0, src.lastIndexOf("/")+1);
				SAXParserFactory factory = SAXParserFactory.newInstance();
				CatalogRefHandler esgCatalogHandler = new CatalogRefHandler();
				SAXParser parser;
				try {
					factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
					factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
					parser = factory.newSAXParser();			
					parser.parse(src, esgCatalogHandler);
				} catch (ParserConfigurationException e) {
					log.error("Error parsing the ESG THREDDS catalog."+e.getMessage());
				} catch (SAXException e) {
					log.error("Error parsing the ESG THREDDS catalog."+e.getMessage());
				} catch (IOException e) {
					log.error("Error parsing the ESG THREDDS catalog."+e.getMessage());
				}
				Map<String, String> catalogs = esgCatalogHandler.getCatalogs();
				Map<String, String> lasCatalogs = new HashMap<String, String>();
				for (Iterator catIt = catalogs.keySet().iterator(); catIt.hasNext();) {
					String name = (String) catIt.next();
					String url = catalogs.get(name);
					InvCatalog catalog = (InvCatalog) thredds_factory.readXML(base+url);
					if (myAddXML.containsLASDatasets(catalog) ) {
						lasCatalogs.put(name, url);
					}
				}
				System.out.println(lasCatalogs.size()+" out of "+catalogs.size()+" contain LAS data.");
				for (Iterator catIt = lasCatalogs.keySet().iterator(); catIt.hasNext();) {
					String name = (String) catIt.next();
					String url = (String) lasCatalogs.get(name);
					System.out.println(name+","+url);
				}
				for (Iterator catIt = lasCatalogs.keySet().iterator(); catIt.hasNext();) {					
					String name = (String) catIt.next();
					String url = catalogs.get(name);
					InvCatalog catalog = (InvCatalog) thredds_factory.readXML(base+url);
					System.out.println("Processing datasets for "+name);
					beans.addAll(myAddXML.processESGDatasets(new HashSet<String>(), catalog));
				}
			} else {
				
				InvCatalog catalog = (InvCatalog) thredds_factory.readXML(src);
				List ThreddsDatasets = catalog.getDatasets();
				Iterator di = ThreddsDatasets.iterator();
				
				ThreddsDatasets = catalog.getDatasets();
				di = ThreddsDatasets.iterator();
				while (di.hasNext() ) {
					InvDataset ThreddsDataset = (InvDataset) di.next();
					if ( ThreddsDataset.hasNestedDatasets() ) {
						beans.addAll(myAddXML.processDatasets(ThreddsDataset));
					}
				}
			}
		}
		return beans;
	}
	/**
	 * Adds the contents of a category bean from addXML to the config, creating the las_categories element if necessary.
	 * @param cat the category bean to be added.
	 */
	public void addCategories(ArrayList<CategoryBean> cats) {
		Element las_categoriesE = getRootElement().getChild("las_categories");
		if ( las_categoriesE == null ) {
			las_categoriesE = new Element("las_categories");
			getRootElement().addContent(las_categoriesE);
		}
		for (Iterator catIt = cats.iterator(); catIt.hasNext();) {
			CategoryBean cat = (CategoryBean) catIt.next();
			Element catE = cat.toXml();
			if ( catE != null ) {
			    las_categoriesE.addContent(catE);
			}
		}
		LASDocument categoryDoc = new LASDocument((Element) las_categoriesE.clone());
		try {
			categoryDoc.write(getOutputDir()+File.separator+"las_categories_"+getBaseServerURLKey()+".xml");
		} catch (UnsupportedEncodingException e) {
			// This is primarily debug output, so if we don't get it we don't really care.
		} catch (JDOMException e) {
			// This is primarily debug output, so if we don't get it we don't really care.
		}
	}
	public ArrayList<Tributary> getTributaries() {
		ArrayList<Tributary> tributaries = new ArrayList<Tributary>();
		List servers = getRootElement().getChildren("las_servers");
		for (Iterator servIt = servers.iterator(); servIt.hasNext();) {
			Element server = (Element) servIt.next();
			List tribElements = server.getChildren("las_server");
			for (Iterator tribIt = tribElements.iterator(); tribIt.hasNext();) {
				Element trib = (Element) tribIt.next();
				Tributary tributary = new Tributary(trib);
				tributaries.add(tributary);
			}
		}
		return tributaries;
	}
	public ArrayList<Tributary> getTributaries(String attribute, String value) {
	    ArrayList<Tributary> tributaries = getTributaries();
	    ArrayList<Tributary> filteredList = new ArrayList<Tributary>();
	    for (Iterator tribIt = tributaries.iterator(); tribIt.hasNext();) {
            Tributary tributary = (Tributary) tribIt.next();
            if ( tributary.getAttributesAsMap().containsKey(attribute) && tributary.getAttributesAsMap().get(attribute).contains(value) ) {
                filteredList.add(tributary);
            }
        }
	    return filteredList;
	}
	public Tributary getTributary(String key) {
		List servers = getRootElement().getChildren("las_servers");
		for (Iterator servIt = servers.iterator(); servIt.hasNext();) {
			Element server = (Element) servIt.next();
			List tribElements = server.getChildren("las_server");
			for (Iterator tribIt = tribElements.iterator(); tribIt.hasNext();) {
				Element trib = (Element) tribIt.next();
				if ( trib.getAttributeValue("ID").equals(key) ) {
					return new Tributary((Element) trib.clone());
				}
			}
		}
		return null;
	}
	public void addServerID() throws UnsupportedEncodingException, JDOMException {
		if ( allowsSisters() ) {
			List operationsElements = getRootElement().getChildren("operations");
		    for (Iterator opsElementsIt = operationsElements.iterator(); opsElementsIt.hasNext();) {
		    	Element opsParent = (Element) opsElementsIt.next();
		    	List ops = opsParent.getChildren("operation");
		    	for (Iterator opsIt = ops.iterator(); opsIt.hasNext();) {
					Element op = (Element) opsIt.next();
					Element optiondef = op.getChild("optiondef");
					// Maybe null for old operations or those without options
					if ( optiondef != null ) {
						String IDREF = optiondef.getAttributeValue("IDREF");
						IDREF = JDOMUtils.MD5Encode(getBaseServerURL()) + Constants.NAME_SPACE_SPARATOR + IDREF;
						optiondef.setAttribute("IDREF", IDREF);
					}
				}
		    }
		}
	}
	public boolean allowsSisters() {
		String allow_sisters = getRootElement().getAttributeValue("allow_sisters");
		boolean sister = false;
		if ( allow_sisters != null && allow_sisters.equalsIgnoreCase("true") ) {
			sister = true;
		}
		return sister;
	}
	public boolean pruneCategories() {
		String prune_categories = getRootElement().getAttributeValue("prune_categories");
		boolean prune = false;
		if ( prune_categories != null && prune_categories.equalsIgnoreCase("true") ) {
			prune = true;
		}
		return prune;
	}
	public boolean isLocal(String id) throws UnsupportedEncodingException, JDOMException {
		boolean local = false;
		if ( id.contains(Constants.NAME_SPACE_SPARATOR) ) {
			String server_key = id.split(Constants.NAME_SPACE_SPARATOR)[0];
			String local_server_key = local_server_key = getBaseServerURLKey();
			if ( server_key.equals(local_server_key) ) {
				local = true;
			}
		}
		return local;
	}
	public String getBaseServerURLKey() throws UnsupportedEncodingException, JDOMException {
		return JDOMUtils.MD5Encode(getBaseServerURL());
	}
	public String getTopLevelCategoryID() throws UnsupportedEncodingException, JDOMException {
	    return getBaseServerURLKey()+Constants.NAME_SPACE_SPARATOR+"Top_of_"+getBaseServerURLKey();
	}
	public ArrayList<Variable> getFullVariables(String dsID) throws JDOMException, LASException {
	    Dataset dataset = getDataset(dsID);
	    ArrayList<Variable> clones = new ArrayList<Variable>();

	    if ( dataset != null ) {
	        ArrayList<Variable> variables = getVariables(dsID);
	        for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
	            Variable var = (Variable) varIt.next();
	            Variable clone_var = new Variable((Element) var.getElement().clone(), var.getDSID(), var.getDSID(), dataset.getName());
	            clones.add(clone_var);
	        }
	        for (Iterator cloneIt = clones.iterator(); cloneIt.hasNext();) {
	            Variable var = (Variable) cloneIt.next();
	            Element varE = (Element) var.getElement();
	            varE.removeChild("grid");  // Get rid of the old grid with just the IDREF and replace it with the grid and axes.
	            Grid grid = getGrid(var.getDSID(), var.getID());
	            varE.addContent((Element)grid.getElement().clone());
	        }
	    }
	    return clones;
	}
	public Dataset getFullDataset(String dsID) throws JDOMException, LASException {
		Dataset dataset = getDataset(dsID);
		dataset.setCATID(dsID);
		ArrayList<Variable> variables = getFullVariables(dsID);
		dataset.setVariables(variables);
		return dataset;
	}
	public Dataset getFullDatasetNoGrids(String dsID) throws JDOMException, LASException {
		Dataset dataset = getDataset(dsID);
		dataset.setCATID(dsID);
		ArrayList<Variable> variables = getVariables(dsID);
		ArrayList<Variable> clones = new ArrayList<Variable>();
		for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
			Variable var = (Variable) varIt.next();
			Variable clone_var = new Variable((Element) var.getElement().clone(), var.getDSID(), var.getDSID(), dataset.getName());
			clones.add(clone_var);
		}
		dataset.setVariables(clones);
		return dataset;
	}
	public String resolveURLS(LASUIRequest lasRequest) throws JDOMException, LASException {
		// Start looping on the args in the request
		List vars = lasRequest.getRootElement().getChild("args").getChildren("link");
        for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
            Element var = (Element) varIt.next();
            String varXPath = var.getAttributeValue("match");
            String url = getFTDSURL(varXPath);
            if ( url != null && !url.equals("") ) {
                var.setAttribute("ftds_url", url);
                var.setAttribute("var_name", getVariableName(varXPath));
                var.setAttribute("var_title", getVariableTitle(varXPath));
                Variable varContainer = getVariableByXPath(varXPath);
                var.setAttribute("dsid", varContainer.getDSID());
                var.setAttribute("gridid", varContainer.getGridID());
            }
        }
        return lasRequest.toCompactString();
	}
	public void removeRemoteVariables(String JSESSIONID) {
		if ( JSESSIONID == null ) return;
		HashSet<String> toRemove = remoteData.get(JSESSIONID);
		if ( toRemove != null ) {
			HashSet<String> inUse = new HashSet<String>();

			// Find id's being used by other sessions...
			for (Iterator dsIt = remoteData.keySet().iterator(); dsIt.hasNext();) {
				String session = (String) dsIt.next();
				if ( !session.equals(JSESSIONID) ) {
					HashSet<String> ds_ids = remoteData.get(JSESSIONID);
					for (Iterator removeIt = toRemove.iterator(); removeIt.hasNext();) {
						String id = (String) removeIt.next();
						if ( ds_ids.contains(id) ) {
							inUse.add(id);
						}
					}
				}
			}

			// ... then remove them from the list of variables to be removed.
			for (Iterator inUseIt = inUse.iterator(); inUseIt.hasNext();) {
				String id = (String) inUseIt.next();
				toRemove.remove(id);
			}

			// Remove the variable...

			HashSet<String> datasets = new HashSet<String>();
			for (Iterator varsit = toRemove.iterator(); varsit.hasNext();) {
				String xpath = (String) varsit.next();
				Element variable = null;
				try {
					variable = getElementByXPath(xpath);
					String dsxpath = xpath.substring(0, xpath.indexOf("/variables"));
					datasets.add(dsxpath);
					Element ds = getElementByXPath(dsxpath);
					ds.getChild("variables").removeContent(variable);
				} catch (JDOMException e) {
					// This gets called by the session listener which is not allowed to throw an
					// exception.  The best we can do is log the error.
					log.error("Unable to remove "+xpath);
				}
			}

			// Remove the dataset it it's empty.
			for (Iterator dsIt = datasets.iterator(); dsIt.hasNext();) {
				String dsxpath = (String) dsIt.next();
				try {
					Element ds = getElementByXPath(dsxpath);
					List variables = ds.getChild("variables").getChildren("variable");
					if ( variables == null || variables.size() == 0 ) {
						getRootElement().getChild("datasets").removeContent(ds);
					}
				} catch (JDOMException e) {
					// This gets called by the session listener which is not allowed to throw an
					// exception.  The best we can do is log the error.
					log.error("Unable to remove "+dsxpath);
				}
			}

			remoteData.remove(JSESSIONID);
			Date now = new Date();
			File debug = new File(getOutputDir()+"/las_debug_remove"+now.getTime()+".xml");
			write(debug);
		}
	}

	public void addRemoteVariables(String JSESSIONID, LASUIRequest lasRequest) throws HttpException, IOException, JDOMException, LASException {
		ArrayList<String> dsids = lasRequest.getDatasetIDs();
		ArrayList<String> varids = lasRequest.getVariableIDs();
		HashMap<String, ArrayList<String[]>> grid_ids = new HashMap<String, ArrayList<String[]>>();
		Iterator varIt = varids.iterator();
		for (Iterator dsidIt = dsids.iterator(); dsidIt.hasNext();) {
			String dsid = (String) dsidIt.next();
			String varid = (String) varIt.next();
			if ( dsid.contains(Constants.NAME_SPACE_SPARATOR) ) {
				String server_key = dsid.split(Constants.NAME_SPACE_SPARATOR)[0];

				// Only add if it is not local data...
				if ( !server_key.equals(getBaseServerURLKey()) ) {

					// Get the data set list for this session from the and add the data set id.
					HashSet<String> remove_var_xpaths = remoteData.get(JSESSIONID);
					if ( remove_var_xpaths == null ) {
						remove_var_xpaths = new HashSet<String>();
					}
					remove_var_xpaths.add("/lasdata/datasets/dataset[@ID='"+dsid+"']/variables/variable[@ID='"+varid+"']");
					remoteData.put(JSESSIONID, remove_var_xpaths);


					ArrayList<String[]> grid_ids_for_key = new ArrayList<String[]>();
					Tributary trib = getTributary(server_key);
					String las_url = trib.getURL()+Constants.GET_CATEGORIES+"?format=xml&catid="+dsid;
					String ds_xml = lasProxy.executeGetMethodAndReturnResult(las_url);
					LASDocument ds_doc = new LASDocument();
					JDOMUtils.XML2JDOM(ds_xml, ds_doc);
					Element dataset = (Element) ds_doc.getRootElement().getChild("category").getChild("dataset").clone();
					Element variable = null;
					List variableElements = dataset.getChild("variables").getChildren("variable");
					for (Iterator varElementIt = variableElements.iterator(); varElementIt.hasNext();) {
						Element var = (Element) varElementIt.next();
						if ( var.getAttributeValue("ID").equals(varid)) {
							variable = (Element) var.clone();
						}
					}
					// Found it.  Add it to the local config.
					if ( variable != null ) {
						Element dataset_exists = getDatasetElement(dsid);
						if ( dataset_exists != null ) {
							boolean exists = false;
							List variables = dataset_exists.getChild("variables").getChildren("variable");
							for (Iterator existVarIt = variables.iterator(); existVarIt.hasNext();) {
								Element var_exists = (Element) existVarIt.next();
								if ( var_exists.getAttributeValue("ID").equals(variable.getAttributeValue("ID"))) {
									exists = true;
								}
							}
							if ( !exists ) {
								dataset_exists.getChild("variables").addContent(variable);
								// Need to add these axes to the local config.
								String[] ids = new String[] {dsid, variable.getAttributeValue("ID")};
								grid_ids_for_key.add(ids);
							}
						} else {
							dataset.removeContent();
							dataset.setAttribute("temporary", "true");
							Element variables = new Element("variables");
							variables.addContent(variable);
							dataset.addContent(variables);
							getRootElement().getChild("datasets").addContent(dataset);

							String[] idpair = new String[] {dsid, variable.getAttributeValue("ID")};
							grid_ids_for_key.add(idpair);
						}
					}
					grid_ids.put(server_key, grid_ids_for_key);

					// Add the grids and axes for the remote variable...
					for (Iterator keysIt = grid_ids.keySet().iterator(); keysIt.hasNext();) {
						String s_key = (String) keysIt.next();
						ArrayList<String[]> grid_id_list = grid_ids.get(s_key);
						for (Iterator gridIdIt = grid_id_list.iterator(); gridIdIt.hasNext();) {
							String[] id_pair = (String[]) gridIdIt.next();
							Grid grid = getGrid(id_pair[0], id_pair[1]);
							if ( grid == null ) {
								// Get the grid.
								Tributary tributary = getTributary(server_key);
								String grid_las_url = tributary.getURL()+Constants.GET_GRID+"?format=xml&dsid="+id_pair[0]+"&varid="+id_pair[1];
								String g_xml = lasProxy.executeGetMethodAndReturnResult(grid_las_url);
								LASDocument g_doc = new LASDocument();
								JDOMUtils.XML2JDOM(g_xml, g_doc);
								// Get the axes and add the using the server id.
								// and build the grid element with the IDREF to the axes.
								grid = new Grid(g_doc.getRootElement());
								Element gridE = new Element("grid");
								gridE.setAttribute("ID", grid.getID());
								if ( grid.hasT() ) {
									TimeAxis t = grid.getTime();
									String tid = t.getID();
									getRootElement().getChild("axes").addContent((Element)t.getElement().clone());
									Element axis = new Element("axis");
									axis.setAttribute("IDREF", tid);
									gridE.addContent(axis);
								}
								if ( grid.hasX() ) {
									Axis x = grid.getAxis("x");
									String xid = x.getID();
									getRootElement().getChild("axes").addContent((Element)x.getElement().clone());
									Element axis = new Element("axis");
									axis.setAttribute("IDREF", xid);
									gridE.addContent(axis);
								}
								if ( grid.hasY() ) {
									Axis y = grid.getAxis("y");
									String yid = y.getID();
									getRootElement().getChild("axes").addContent((Element)y.getElement().clone());
									Element axis = new Element("axis");
									axis.setAttribute("IDREF", yid);
									gridE.addContent(axis);
								}
								if ( grid.hasZ() ) {
									Axis z = grid.getAxis("z");
									String zid = z.getID();
									getRootElement().getChild("axes").addContent((Element)z.getElement().clone());
									Element axis = new Element("axis");
									axis.setAttribute("IDREF", zid);
									gridE.addContent(axis);
								}
								getRootElement().getChild("grids").addContent(gridE);
							}
						}
					}
				}
			}
		}
		Date now = new Date();
		File debug = new File(getOutputDir()+"/las_debug_add_"+now.getTime()+".xml");
		write(debug);
	}
	public Map<String, String> getEnsembleMembers(String[] dsID) throws JDOMException, LASException {
		Map<String, String> names = new HashMap<String, String>();
		for (int i = 0; i < dsID.length; i++) {
			Dataset d = getDataset(dsID[i]);
			names.put(dsID[i], d.getName());
		}
		return names;
	}
	/**
	 * Read the test options from the configuration, if there are none return null.
	 * Options are:
	 * <tests>
	 *     <test type="OPeNDAP"/> <!-- Test all OPeNDAP connections. -->
	 *     <test type="F-TDS"/>   <!-- Test all F-TDS connections. -->
	 *     
	 *     <!-- Test generation of products, 
	 *          default view is "xy" and 
	 *          default is to test the first variable, set to "all" to test all and
	 *          default is all dataset, use an ID to test a particular dataset.  -->
	 *     <test type="Products" view="xy" variable="all" dataset="DSID"> 
	 * </tests>
	 * @return
	 */
	public LASTestOptions getTestOptions() {
		LASTestOptions lto = null;
		Element testsE = getRootElement().getChild("tests");
		if ( testsE != null ) {
			lto = new LASTestOptions();
			List tests = testsE.getChildren("test");
			String delay = testsE.getAttributeValue("delay");
			
			if ( delay != null ) {
				try {
					long d = Long.valueOf(delay).longValue();
					lto.setDelay(d);
				} catch (Exception e) {
					// Oh, well...
				}
			}
			String period = testsE.getAttributeValue("period");
			if ( period != null ) {
				try {
					long p = Long.valueOf(period).longValue();
					lto.setPeriod(p);
				} catch (Exception e) {
					// Oh, well...
				}
			}
			for (Iterator testsIt = tests.iterator(); testsIt.hasNext();) {
				Element test = (Element) testsIt.next();
				String type = test.getAttributeValue("type");
				if ( type != null ) {
					if ( type.equals(TestConstants.KEY_TEST_DIRECT_OPENDAP)) {
						lto.setConnectionOnly();
					} else if ( type.equals(TestConstants.KEY_TEST_F_TDS_OPENDAP) ) {
						lto.setTestFTDS();
					} else if ( type.equals(TestConstants.KEY_TEST_PRODUCT) ) {
						lto.setResponseOnly();
						// If the view is set use it, default to "xy"
						String view = test.getAttributeValue("view");
						if ( view != null ) {
							lto.setView(view);
						} else {
						   lto.setView("xy");
						}
						String variable = test.getAttributeValue("varaible");
						// The choices are "all", "first" or the default which is first.
						if ( variable != null && variable.equals("all") ) {
							lto.setAllVariable();
						}
						String dataset = test.getAttributeValue("dataset");
						if ( dataset != null ) {
							lto.setDataset(dataset);
						}
					}
				}
			}
		}
		return lto;
	}
    public HashMap<String, String> getPointsAndIntervals(String varXPath) throws JDOMException, LASException {
        Grid grid = getGrid(varXPath);
        String points = grid.getAttributeValue("points");
        String intervals = grid.getAttributeValue("intervals");
        HashMap<String, String> ptinv = new HashMap<String, String>();
        ptinv.put("points", points);
        ptinv.put("intervals", intervals);
        return ptinv;
        
    }
    public String addDataset(String id) throws JDOMException, HttpException, IOException, LASException {
        String master_id;
        String key = null;
        DateTime now = new DateTime();
        DateTimeFormatter longfmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        String time_added = longfmt.print(now);
        if ( id.contains(Constants.NAME_SPACE_SPARATOR) ) {
            String[] parts = id.split(Constants.NAME_SPACE_SPARATOR);

            key = parts[0];
            master_id = parts[1];
        } else {
            master_id = id;
        }
        if ( master_id == null || master_id.length() == 0 ) {
        	return null;
        }

        if ( master_id.endsWith("aggregation") ) {
            // Hack off three
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
        } else if ( master_id.contains("aggregation") ) {
            // Hack off four
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
        }  else {
            if ( master_id != null && master_id.contains(".")) {
            	master_id = master_id.substring(0, master_id.lastIndexOf("."));
            }
        }
            


        String search = getGlobalPropertyValue("product_server", "esgf_search_url");
        List<Tributary> tribs = new ArrayList<Tributary>();
        if ( search != null && !search.equals("") ) {
            if ( search.contains(",") ) {
                String endings[] = search.split(",");
                for (int i = 0; i < endings.length; i++) {
                    tribs.addAll(getTributaries("url", endings[i].trim()));
                }
            } else {
                tribs.addAll(getTributaries("url", search));
            }
        } else {
            tribs.addAll(getTributaries("url", "gov"));
            tribs.addAll(getTributaries("url", "edu"));
        }
        boolean do_search;
        for (Iterator iterator = tribs.iterator(); iterator.hasNext();) {
            Tributary tributary = (Tributary) iterator.next();
            String search_base = tributary.getURL().replace("las", "esg-search/search");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            String searchurl = search_base+"?access=LAS&replica="+Constants.ESGF_REPLICAS+"&master_id="+master_id;
            do_search = true;
            try {
                lasProxy.executeGetMethodAndStreamResult(searchurl, stream);
            } catch (Exception e) {
                // Go on to the next search URL
                do_search = false;
            }
            if ( do_search ) {
                Document doc = new Document();

                JDOMUtils.XML2JDOM(stream.toString(), doc);
                Element root = doc.getRootElement();
                Element result = root.getChild("result");
                String catalog = null;
                String LAS = null;
                Set<String> time_freqs = new HashSet<String>();
                if ( result != null ) {
                    String number = result.getAttributeValue("numFound");
                    if ( !number.equals("0") ) {
                        List<Element> results = result.getChildren("doc");
                        Element solrDoc = results.get(0);
                        if ( solrDoc != null ) {
                            List<Element> arrays = solrDoc.getChildren("arr");
                            for ( Iterator arrE = arrays.iterator(); arrE.hasNext(); ) {
                                Element arr = (Element) arrE.next();
                                if ( arr.getAttributeValue("name").equals("url")) {
                                    List<Element> strs = arr.getChildren("str");
                                    for ( Iterator strIt = strs.iterator(); strIt.hasNext(); ) {
                                        Element str = (Element) strIt.next();
                                        String txt = str.getTextTrim();
                                        if ( txt.contains("|Catalog") || txt.contains("|THREDDS")) {
                                            catalog = txt.substring(0, txt.indexOf("#"));
                                            System.out.println(catalog);
                                        }
                                        if ( txt.contains("|LAS") ) {
                                            LAS = txt.substring(0, txt.indexOf("|"));
                                            LAS = LAS.substring(0, LAS.indexOf("/getUI.do"));
                                        }
                                    }
                                } else if ( arr.getAttributeValue("name").equals("time_frequency") ) {
                                    List<Element> strs = arr.getChildren("str");
                                    for ( Iterator strIt = strs.iterator(); strIt.hasNext(); ) {
                                        Element str = (Element) strIt.next();
                                        String txt = str.getTextTrim();
                                        time_freqs.add(txt);
                                    }
                                }
                            }
                        }
                        if ( key == null && LAS != null ) {
                            key = JDOMUtils.MD5Encode(LAS);
                        }
                        if ( LAS == null ) {
                            // No remote LAS, use the local LAS to plot this data.
                            key = getBaseServerURLKey();
                        }
                        InvCatalogFactory factory = new InvCatalogFactory("default", false);
                        InvCatalog invCatalog = (InvCatalog) factory.readXML(catalog);
                        List<DatasetsGridsAxesBean> dagbs = ADDXMLProcessor.processESGDatasets(time_freqs, invCatalog);
                        // There's only going to be one...
                        String key_id = null;
                        Dataset ds = null;
                        for ( Iterator dagbIt = dagbs.iterator(); dagbIt.hasNext(); ) {
                            DatasetsGridsAxesBean dagb = (DatasetsGridsAxesBean) dagbIt.next();
                            List<DatasetBean> datasets = dagb.getDatasets();
                            for ( Iterator dsIt = datasets.iterator(); dsIt.hasNext(); ) {
                                DatasetBean db = (DatasetBean) dsIt.next();
                                String e = db.getElement();
                                e = key+Constants.NAME_SPACE_SPARATOR+e;
                                db.setElement(e);
                                key_id = e;
                                
                                Element dataset = db.toXml(true);
                                dataset.setAttribute("date", time_added);
                                dataset.setAttribute("catid", db.getElement());
                                Element variables = dataset.getChild("variables");
                                if ( variables != null ) {
                                    List<Element> vars = variables.getChildren("variable");
                                    if ( vars != null ) {
                                        for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
                                            Element variable = (Element) varIt.next();
                                            String varid = variable.getAttributeValue("ID");
                                            String url = variable.getAttributeValue("url");
                                            if ( url != null ) {
                                                String ftds_url = url.substring(0, url.lastIndexOf("#"));
                                                variable.setAttribute("ftds_url", ftds_url);
                                            }
                                        }
                                    }
                                }
                                Element dsE = getRootElement().getChild("datasets");

                                if ( dsE == null ) {
                                    dsE = new Element("datasets");
                                    getRootElement().addContent(dsE);
                                } 

                                ds = getDataset(key_id);

                                // Add only if it doesn't exist already...

                                if ( ds == null ) {

                                    dsE.addContent(dataset);

                                }
                            }

                            // Add only if it doesn't exist already...
                            if ( ds == null ) {
                                List<GridBean> grids = dagb.getGrids();
                                for ( Iterator gIt = grids.iterator(); gIt.hasNext(); ) {
                                    GridBean gb = (GridBean) gIt.next();
                                    Element grid = gb.toXml(true);
                                    Element gsE = getRootElement().getChild("grids");
                                    if ( gsE == null ) {
                                        gsE = new Element("grids");
                                        getRootElement().addContent(gsE);
                                    }
                                    gsE.addContent(grid);
                                }

                                List<AxisBean> axes = dagb.getAxes();
                                for ( Iterator aIt = axes.iterator(); aIt.hasNext(); ) {
                                    AxisBean ab = (AxisBean) aIt.next();
                                    Element axis = ab.toXml(true);
                                    Element asE = getRootElement().getChild("axes");
                                    if ( asE == null ) {
                                        asE = new Element("axes");
                                        getRootElement().addContent(asE);
                                    }
                                    asE.addContent(axis);
                                }
                            } else {
                                ds.setAttribute("date", time_added);
                            }

                        }
                        return key_id;
                    } 
                }
            } // do_search
        }
        return null;
    }
    public CategorySerializable getCategorySerializableWithGrids(String catid, String dsid) throws LASException {
        try {
            CategorySerializable[] cats = getCategoriesSerializable(new String[]{catid});
            if ( cats.length != 1 ) {
                throw new RPCException("Cound not get a single category with this ID");
            }
            CategorySerializable cat = cats[0];
            if ( !cat.isCategoryChildren() ) {
                DatasetSerializable dataset = cat.getDatasetSerializable();
                VariableSerializable[] vars = dataset.getVariablesSerializable();
                for (int i = 0; i < vars.length; i++) {
                    GridSerializable wire_grid = getGridSerializable(dsid, vars[i].getID());
                    vars[i].setGrid(wire_grid);
                    vars[i].setShortname(getVariableName(dsid, vars[i].getID()));
                }
                
            }
            
            return cat;
        } catch (Exception e) {
            throw new LASException(e.getMessage());
        }
    }
    public CategorySerializable[] getCategoriesSerializable(String[] ids) throws LASException {
        ArrayList<Category> categories = new ArrayList<Category>();
        if ( this.allowsSisters() ) {
            try {
                if ( ids == null ) {
                    
                    // This is the inital top level request for a regular LAS.
                    Category local_cat = new Category(this.getTitle(), this.getTopLevelCategoryID()); 
                    // Do the local top level category
                    ArrayList<Category> local_cats = this.getCategories(null);
                    for (Iterator catIt = local_cats.iterator(); catIt.hasNext();) {
                        Category category = (Category) catIt.next();
                        local_cat.addCategory(category);
                    }
                    categories.add(local_cat);
                    // Do the remote categories...

                    ArrayList<Tributary> tributaries = this.getTributaries();
                    for (Iterator tribIt = tributaries.iterator(); tribIt.hasNext();) {
                        Tributary tributary = (Tributary) tribIt.next();
                        Category server_cat = new Category(tributary.getName(), tributary.getTopLevelCategoryID());
                        server_cat.setAttribute("remote_las", tributary.getURL()+Constants.GET_AUTH);
                        String url = tributary.getURL() + Constants.GET_CATEGORIES + "?format=xml";
                        String catxml = lasProxy.executeGetMethodAndReturnResult(url);
                        ArrayList<Category> trib_cats = LASJDOMUtils.getCategories(catxml);
                        for (Iterator tribCatsIt = trib_cats.iterator(); tribCatsIt.hasNext();) {
                            Category category = (Category) tribCatsIt.next();
                            server_cat.addCategory(category);
                        }
                        categories.add(server_cat);
                    }
                } else if ( ids.length > 1 ) {
                    // This is the inital category set from and ESGF request.
                    for ( int i = 0; i < ids.length; i++ ) {
                        categories.addAll(this.getCategories(ids[i]));
                    }
                } else if ( ids.length == 1) {
                    // This is a particular ID
                    if ( this.pruneCategories() ) {
                        // This is the ESGF case... 
                        
                        categories = this.getCategories(ids[0]);
                        
                    } else {
                        // This is the non-ESGF case...
                        if ( !ids[0].contains(Constants.NAME_SPACE_SPARATOR) || this.isLocal(ids[0]) ) {
                            // Handle the case where we're getting the local top level catagories
                            if ( ids[0].equals(this.getTopLevelCategoryID()) ) {
                                categories = this.getCategories(null);
                            } else {
                                categories = this.getCategories(ids[0]);
                            }
                        } else {
                            String[] parts = ids[0].split(Constants.NAME_SPACE_SPARATOR);
                            String server_key = null;
                            if ( parts != null ) {
                                server_key = parts[0];
                                if ( server_key != null ) {
                                    Tributary trib = this.getTributary(server_key);
                                    String las_url = trib.getURL() + Constants.GET_CATEGORIES + "?format=xml&catid="+ids[0];
                                    String catxml = lasProxy.executeGetMethodAndReturnResult(las_url);
                                    ArrayList<Category> trib_cats = LASJDOMUtils.getCategories(catxml);
                                    for (Iterator tribCatsIt = trib_cats.iterator(); tribCatsIt.hasNext();) {
                                        Category category = (Category) tribCatsIt.next();
                                        categories.add(category);
                                    }
                                }
                            }
                        }
                    }

                }
            } catch (JDOMException e) {
                throw new LASException(e.getMessage());
            } catch (LASException e) {
                throw new LASException(e.getMessage());
            } catch (UnsupportedEncodingException e) {
                throw new LASException(e.getMessage());
            } catch (HttpException e) {
                throw new LASException(e.getMessage());
            } catch (IOException e) {
                throw new LASException(e.getMessage());
            }
        } else {
            try {
                categories = this.getCategories(ids[0]);
            } catch (JDOMException e) {
                throw new LASException(e.getMessage());
            } catch (LASException e) {
                throw new LASException(e.getMessage());
            }
        }
        
        if ( categories.size() > 0 ) {
        	Category one = categories.get(0);
        	if ( one.getAttributesAsMap().containsKey("sort_order") ) {
        		Collections.sort(categories, new ContainerComparator("sort_order"));
        	} else {
        		Collections.sort(categories, new ContainerComparator("name"));
        	}
        }
       

        
        CategorySerializable[] cats = new CategorySerializable[categories.size()];

        for (int i = 0; i < cats.length; i++) {
            cats[i] = categories.get(i).getCategorySerializable();
        }
        for ( int i = 0; i < cats.length; i++ ) {
            if ( cats[i].isVariableChildren() ) {
                cats[i].sortVariables();
            }
        }
        return cats;    
    }
    public GridSerializable getGridSerializable(String dsID, String varID) throws LASException {
        Grid grid = null;
        
        try {
            grid = this.getGrid(dsID, varID);
        } catch (JDOMException e) {
            throw new LASException(e.getMessage());
        } catch (LASException e) {
            throw new LASException(e.getMessage());
        }
    
    if ( grid != null ) {
        return grid.getGridSerializable();
    } else {
        return null;
    }
    }
    public List<ERDDAPConstraintGroup> getERDDAPConstraintGroups(String dsid) throws JDOMException, LASException {
        List<ERDDAPConstraintGroup> groups = new ArrayList<ERDDAPConstraintGroup>();
        
        Element dataset = getDatasetElement(dsid);
        Element constraintsE = dataset.getChild("constraints");
        if ( constraintsE != null ) {
            List groupElements = constraintsE.getChildren("constraint_group");
            for (Iterator groupElement = groupElements.iterator(); groupElement.hasNext();) {
                Element cg = (Element) groupElement.next();
                ERDDAPConstraintGroup constraintGroup = new ERDDAPConstraintGroup();
                constraintGroup.setDsid(dsid);
                String groupName = cg.getAttributeValue("name");
                if ( groupName != null ) {
                    constraintGroup.setName(groupName);
                }
                String groupType = cg.getAttributeValue("type");
                if ( groupType != null ) {
                    constraintGroup.setType(groupType);
                }
                if ( groupType != null && groupType.equals("selection") ) {
                    // Should only be one constraint with multiple variables and one key.
                    ERDDAPConstraint constraint = new ERDDAPConstraint();
                    Element cE = cg.getChild("constraint");
                    String cName = cg.getAttributeValue("name");
                    if ( cName != null ) {
                        constraint.setName(cName);
                    }
                    List vars = cE.getChildren("variable");
                    List<VariableSerializable> variables = new ArrayList<VariableSerializable>();
                    for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
                        Element variable = (Element) varIt.next();
                        String IDREF = variable.getAttributeValue("IDREF");
                        VariableSerializable var = getVariable(dsid, IDREF).getVariableSerializable();
                        String shortname = getVariableName(dsid, IDREF);
                        var.setShortname(shortname);
                        variables.add(var);  
                    }
                    constraint.setVariables(variables);
                    Element keyE = cE.getChild("key");
                    String key = keyE.getTextNormalize();
                    constraint.setKey(key);
                    constraintGroup.add(constraint);
                } else if (groupType != null && groupType.equals("subset") || groupType.equals("regex")){
                    List constraints = cg.getChildren("constraint");
                    for (Iterator cIt = constraints.iterator(); cIt.hasNext();) {
                        ERDDAPConstraint constraint = new ERDDAPConstraint();
                        Element c = (Element) cIt.next();
                        String widget = c.getAttributeValue("widget");
                        constraint.setWidget(widget);
                        if ( widget.equals("list") ) {
                            List<VariableSerializable> variables = new ArrayList<VariableSerializable>();
                            Element variable = c.getChild("variable");
                            String IDREF = variable.getAttributeValue("IDREF");
                            VariableSerializable var = getVariable(dsid, IDREF).getVariableSerializable();
                            String shortname = getVariableName(dsid, IDREF);
                            var.setShortname(shortname);
                            variables.add(var);
                            constraint.setVariables(variables);
                            
                            Element labels = c.getChild("labels");
                            Map<String, String> valueLabels = new HashMap<String, String>();
                            if ( labels != null ) {
                                List<Element> list = labels.getChildren("label");
                                for (Iterator lsitIt = list.iterator(); lsitIt.hasNext();) {
                                    Element label = (Element) lsitIt.next();
                                    String value = label.getAttributeValue("value");
                                    String labelText = label.getTextTrim();
                                    valueLabels.put(value, labelText);
                                }
                                constraint.setLabels(valueLabels);
                            }
                        }
                        constraintGroup.add(constraint);
                        Element keyE = c.getChild("key");
                        if ( keyE != null ) {
                            String key = keyE.getTextNormalize();
                            constraint.setKey(key);
                        }
                    }
                } else if ( groupType != null && groupType.equals("season") ) {
                    // Generally, I would think there would only be one child with one variable...
                    List constraints = cg.getChildren("constraint");
                    for (Iterator cIt = constraints.iterator(); cIt.hasNext();) {
                        ERDDAPConstraint constraint = new ERDDAPConstraint();
                        Element c = (Element) cIt.next();
                        String widget = c.getAttributeValue("widget");
                        constraint.setWidget(widget);
                       
                            List<VariableSerializable> variables = new ArrayList<VariableSerializable>();
                            Element variable = c.getChild("variable");
                            String IDREF = variable.getAttributeValue("IDREF");
                            VariableSerializable var = getVariable(dsid, IDREF).getVariableSerializable();
                            String shortname = getVariableName(dsid, IDREF);
                            var.setShortname(shortname);
                            variables.add(var);
                            constraint.setVariables(variables);
                        
                        constraintGroup.add(constraint);
                        Element keyE = c.getChild("key");
                        if ( keyE != null ) {
                            String key = keyE.getTextNormalize();
                            constraint.setKey(key);
                        }
                    }
                }
                groups.add(constraintGroup);
            }
        }
        return groups;
    }
    public void removeByXpath(Set<String> toRemove) throws Exception {
    	for (Iterator idIt = toRemove.iterator(); idIt.hasNext();) {
            String xpath = (String) idIt.next();
            Element element = null;
            element = getElementByXPath(xpath);
            if ( element != null ) {
                Parent p = element.getParent();
                p.removeContent(element);
            } else {
                log.debug("Could not find "+xpath);
            }

        }
    }
    public void removeDataset(String dsid) throws Exception {
    	Dataset dataset = getDataset(dsid);
    	if ( dataset != null ) {
    		Set<String> toRemove = new HashSet<String>();
    		toRemove.add("/lasdata/datasets/dataset[@ID='"+dataset.getID()+"']");
    		List<Variable> variables = dataset.getVariables();
    		for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
    			Variable variable = (Variable) varIt.next();

    			Grid grid = variable.getGrid();
    			toRemove.add("/lasdata/grids/grid[@ID='"+grid.getID()+"']");
    			List<Axis> axes = grid.getAxes();
    			for (Iterator axisIt = axes.iterator(); axisIt.hasNext();) {
    				Axis axis = (Axis) axisIt.next();
    				toRemove.add("/lasdata/axes/axis[@ID='"+axis.getID()+"']");
    			}
    			TimeAxis taxis = grid.getTime();
    			if ( taxis != null ) {
    				toRemove.add("/lasdata/axes/axis[@ID='"+taxis.getID()+"']");
    			}
    		}
    		removeByXpath(toRemove);
    	}
    }
    public void removeOldDatasets(DateTime then) throws Exception {
        // "/lasdata/datasets/dataset[@ID='"+dsid+"']/variables/variable[@ID='"+varid+"']"
       
            DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
            List<Dataset> all = getDatasets(true);
            Set<String> toRemove = new HashSet<String>();
            for (Iterator allIt = all.iterator(); allIt.hasNext();) {
                Dataset dataset = (Dataset) allIt.next();
                String date_added = dataset.getAttributeValue("date");
                if ( date_added != null && !date_added.equals("") ) {
                    DateTime date = format.parseDateTime(date_added);
                    if ( date.isBefore(then) ) {
                        toRemove.add("/lasdata/datasets/dataset[@ID='"+dataset.getID()+"']");
                        List<Variable> variables = dataset.getVariables();
                        for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
                            Variable variable = (Variable) varIt.next();
                            
                            Grid grid = variable.getGrid();
                            toRemove.add("/lasdata/grids/grid[@ID='"+grid.getID()+"']");
                            List<Axis> axes = grid.getAxes();
                            for (Iterator axisIt = axes.iterator(); axisIt.hasNext();) {
                                Axis axis = (Axis) axisIt.next();
                                toRemove.add("/lasdata/axes/axis[@ID='"+axis.getID()+"']");
                            }
                            TimeAxis taxis = grid.getTime();
                            if ( taxis != null ) {
                                toRemove.add("/lasdata/axes/axis[@ID='"+taxis.getID()+"']");
                            }
                        }
                    }
                }
            }

            removeByXpath(toRemove);
            
            File v7 = new File(getOutputDir()+"/"+format.print(new DateTime())+"lasV7.xml");
            try {
                write(v7);
            } catch (Exception e) {
                log.error("Cannot write out new Version 7.0 las.xml file.", e);
            }
    }
}
