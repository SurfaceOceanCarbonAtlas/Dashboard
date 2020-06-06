package gov.noaa.pmel.tmap.las.server;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.client.lastest.TestConstants;
import gov.noaa.pmel.tmap.las.client.rpc.RPCException;
import gov.noaa.pmel.tmap.las.client.rpc.RPCService;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConfigSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConstraintSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraint;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraintGroup;
import gov.noaa.pmel.tmap.las.client.serializable.ESGFDatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.FacetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.OperationSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.OptionSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.RegionSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.TestSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;
import gov.noaa.pmel.tmap.las.erddap.util.ERDDAPUtil;
import gov.noaa.pmel.tmap.las.jdom.ESGFSearchDocument;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASTestResults;
import gov.noaa.pmel.tmap.las.jdom.ServerConfig;
import gov.noaa.pmel.tmap.las.product.server.LASConfigPlugIn;
import gov.noaa.pmel.tmap.las.proxy.LASProxy;
import gov.noaa.pmel.tmap.las.util.Category;
import gov.noaa.pmel.tmap.las.util.Constants;
import gov.noaa.pmel.tmap.las.util.Constraint;
import gov.noaa.pmel.tmap.las.util.Dataset;
import gov.noaa.pmel.tmap.las.util.Operation;
import gov.noaa.pmel.tmap.las.util.Option;
import gov.noaa.pmel.tmap.las.util.Region;
import gov.noaa.pmel.tmap.las.util.Tributary;
import gov.noaa.pmel.tmap.las.util.Variable;
import opendap.dap.Attribute;
import opendap.dap.AttributeTable;
import opendap.dap.DAP2Exception;
import opendap.dap.DAS;

public class RPCServiceImpl extends RemoteServiceServlet implements RPCService {
	private static final LASProxy lasProxy = new LASProxy();
	private static Logger log = LoggerFactory.getLogger(RPCServiceImpl.class.getName());
/*
 * This might be necessary for our set up with the F5, but it messes up many other installations.
 *
 *
    @Override
    protected SerializationPolicy doGetSerializationPolicy(javax.servlet.http.HttpServletRequest request,
    		java.lang.String moduleBaseURL,
    		java.lang.String strongName) {

    	SerializationPolicy serializationPolicy = null;

		String policyFile = moduleBaseURL + strongName + ".gwt.rpc";
		
    	try {		
    		log.warn("Attempting to read serialization policy from: "+policyFile);
    		InputStream is = lasProxy.executeGetMethodAndReturnStream(policyFile, null);
    		serializationPolicy = SerializationPolicyLoader.loadFromStream(is, null);
    	} catch (IOException e) {
    		log.error("Unable to get serializaiton policy from: "+policyFile+" "+e.getMessage());
    	} catch (HttpException e) {
    		log.error("Unable to get serializaiton policy from: "+policyFile+" "+e.getMessage());
    	} catch (ParseException e) {
    		log.error("Unable to get serializaiton policy from: "+policyFile+" "+e.getMessage());
    	}

    	log.warn("Module base for serialization policy successfully read from: "+policyFile);
    	return serializationPolicy;

    }
*/

	/**
	 * @return
	 * @throws RPCException
	 */
	LASConfig getLASConfig() throws RPCException {
		String lock = (String) getServletContext().getAttribute(LASConfigPlugIn.LAS_LOCK_KEY);
        if ( lock != null && lock.equals("true") ) {
        	throw new RPCException("Site updating. Reload and try again in a minute.");
        }
		LASConfig lasConfig = (LASConfig) getServletContext().getAttribute(
				LASConfigPlugIn.LAS_CONFIG_KEY);
		if (lasConfig == null) {
			throw new RPCException(LASConfigPlugIn.LAS_CONFIG_NOTFOUND_MESSAGE);
		}
		return lasConfig;
	}
	ServerConfig getServerConfig() throws RPCException {
	    ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(LASConfigPlugIn.SERVER_CONFIG_KEY);
	    if ( serverConfig == null ) {
	        throw new RPCException(LASConfigPlugIn.LAS_CONFIG_NOTFOUND_MESSAGE);
	    }
	    return serverConfig;
	}

	public HashMap<String, String> getPropertyGroup(String name) throws RPCException {
        LASConfig lasConfig = getLASConfig(); 
        HashMap<String, String> property_group;
        try {
            property_group = lasConfig.getGlobalPropertyGroupAsHashMap(name);
        } catch (LASException e) {
            throw new RPCException(e.getLocalizedMessage());
        }
        return property_group;
    }

    public TestSerializable[] getTestResults(String test_key) throws RPCException {
        LASTestResults testResults = new LASTestResults();
        LASConfig lasConfig = getLASConfig();
        String test_output_file = lasConfig.getOutputDir()+File.separator+TestConstants.TEST_RESULTS_FILE;
        File c = new File(test_output_file);
        if ( c.exists() ) {
            try {
                JDOMUtils.XML2JDOM(new File(test_output_file), testResults);
            } catch (IOException e) {
                throw new RPCException(e.getMessage());
            } catch (JDOMException e) {
                throw new RPCException(e.getMessage());
            }
        }
        if ( test_key == null ) {
            return testResults.getTests();
        } else {
            TestSerializable[] tests = new TestSerializable[1];
            tests[0] = testResults.getTest(test_key);
            return tests;
        }
    }
    /**
     * Get the dsid and varid based on an opendap URL.  Will not work with sister servers!!
     * @param data_url
     * @return
     * @throws RPCException 
     */
    public Map<String, String> getIDMap(String data_url) throws RPCException {
        LASConfig lasConfig = getLASConfig(); 
        try {
            return lasConfig.getIDMap(data_url);
        } catch ( JDOMException e ) {
            throw new RPCException(e.getMessage());
        } catch ( LASException e ) {
            throw new RPCException(e.getMessage());
        }
    }
    /**
     * @throws RPCException 
     * 
     */
    public CategorySerializable[] getTimeSeries() throws RPCException {
        LASConfig lasConfig = getLASConfig(); 
        

        CategorySerializable[] cats = null;
        // TODO Do we need a confluence implementation of this?
        
            try {
                ArrayList<Category> categories = lasConfig.getTimeSeriesDatasets();
                cats = lasConfig.getCategorySerializable(categories);
            } catch (LASException e) {
                throw new RPCException(e.getMessage());
            } catch (JDOMException e) {
                throw new RPCException(e.getMessage());
            }
        
        return cats;
    }
    public ConfigSerializable getConfig(String view, String[] xpaths) throws RPCException {
        try {
            ConfigSerializable wire_config = new ConfigSerializable();
            String dsid = Util.getDSID(xpaths[0]);
            String varid = Util.getVarID(xpaths[0]);
            List<ERDDAPConstraintGroup> constraintGroups = getERRDAPConstraintGroups(dsid);
            if ( constraintGroups != null && constraintGroups.size() > 0) {
                wire_config.setConstraintGroups(constraintGroups);
            } else {
                wire_config.setConstraintGroups(null);
            }
            GridSerializable wire_grid = getGridSerializable(dsid, varid);
            wire_config.setGrid(wire_grid);
            OperationSerializable[] wire_operations = getOperations(view, xpaths);
            wire_config.setOperations(wire_operations);
            RegionSerializable[] wire_regions = getRegionsSerializable(dsid, varid);
            wire_config.setRegions(wire_regions);
            wire_config.setDsid(dsid);
            wire_config.setVarid(varid);
            return wire_config;
        } catch (RPCException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    public ConfigSerializable getConfig(String view, String catid, String dsid, String varid) throws RPCException {
        try {
            ConfigSerializable wire_config = new ConfigSerializable();
            CategorySerializable cat= getCategoryWithGrids(catid, dsid);
            wire_config.setCategorySerializable(cat);
            List<ERDDAPConstraintGroup> constraintGroups = getERRDAPConstraintGroups(dsid);
            if ( constraintGroups != null && constraintGroups.size() > 0) {
                wire_config.setConstraintGroups(constraintGroups);
            } else {
                wire_config.setConstraintGroups(null);
            }
            GridSerializable wire_grid = getGridSerializable(dsid, varid);
            wire_config.setGrid(wire_grid);
            OperationSerializable[] wire_operations = getOperationsSerialziable(view, dsid, varid);
            wire_config.setOperations(wire_operations);
            RegionSerializable[] wire_regions = getRegionsSerializable(dsid, varid);
            wire_config.setRegions(wire_regions);
            wire_config.setDsid(dsid);
            wire_config.setVarid(varid);
            return wire_config;
        } catch (RPCException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    public VariableSerializable getVariable(String dsid, String varid ) throws RPCException {
        LASConfig lasConfig = getLASConfig();
        Variable variable = null;
        try {
            Dataset dataset = null;
            if ( !dsid.contains(Constants.NAME_SPACE_SPARATOR) || lasConfig.isLocal(dsid) ) {
                dataset = lasConfig.getDataset(dsid);
                if ( dataset != null ) {
                    variable = dataset.getVariable(varid);
                } else {
                    throw new RPCException("Cannot find data set for this id: "+dsid);
                }
            } else {
                String[] parts = dsid.split(Constants.NAME_SPACE_SPARATOR);
                String server_key = null;
                if ( parts != null ) {
                    server_key = parts[0];
                    if ( server_key != null ) {
                        Tributary trib = lasConfig.getTributary(server_key);
                        String las_url = trib.getURL() + Constants.GET_VARIABLE + "?format=xml&dsid="+dsid+"&varid="+varid;
                        String varxml = lasProxy.executeGetMethodAndReturnResult(las_url);
                        LASDocument vardoc = new LASDocument();
                        JDOMUtils.XML2JDOM(varxml, vardoc);
                        variable = new Variable(vardoc.getRootElement(), dsid);
                        variable.setDSName(variable.getAttributeValue("dsname"));
                        variable.setDSID(variable.getAttributeValue("dsid"));
                    }
                }
            }       
        } catch (JDOMException e) {
            throw new RPCException(e.getMessage());
        } catch (LASException e) {
            throw new RPCException(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RPCException(e.getMessage());
        } catch (HttpException e) {
            throw new RPCException(e.getMessage());
        } catch (IOException e) {
            throw new RPCException(e.getMessage());
        }
        if ( variable != null ) {
            return variable.getVariableSerializable();
        } else {
            throw new RPCException("Variable not found.");
        }
    }
    public DatasetSerializable getFullDataset(String id) throws RPCException {
        LASConfig lasConfig = getLASConfig();
        try {
            return lasConfig.getFullDataset(id).getDatasetSerializable();
        } catch (JDOMException e) {
            throw new RPCException(e.getMessage());
        } catch (LASException e) {
            throw new RPCException(e.getMessage());
        }
    }
    public CategorySerializable getCategoryWithGrids(String catid, String dsid) throws RPCException {
        LASConfig lasConfig = getLASConfig();

        if ( catid == null && dsid != null ) {
            catid = dsid;
        } else if ( catid != null && dsid == null ) {
            dsid = catid;
        } 
        try {
            return lasConfig.getCategorySerializableWithGrids(catid, dsid);
        } catch (LASException e) {
            throw new RPCException(e.getMessage());
        }
        
    }
	public CategorySerializable[] getCategories(String catid, String dsid) throws RPCException {
	    String id = null;
	    if ( catid == null && dsid == null ) {
	        id = null;
	    } else if ( catid == null && dsid != null ) {
	        id = dsid;
	    } else if ( catid != null && dsid == null ) {
	        id = catid;
	    } else if ( catid != null && dsid != null ) {
	    	if ( catid.equals(dsid) ) {
	    		id = dsid;
	    	} else {
	    		id = catid;
	    	}
	    }
	    return getCategories(id);
	}
	private CategorySerializable[] getCategories(String id) throws RPCException {
	    try {
	        LASConfig lasConfig = getLASConfig();

	        CategorySerializable[] categories = new CategorySerializable[0];
	        if ( lasConfig.allowsSisters() ) {

	            if ( id == null ) {
	                if ( lasConfig.pruneCategories() ) {

	                    HttpServletRequest request = this.getThreadLocalRequest();
	                    String [] catids = (String[]) request.getSession().getAttribute("catid");
	                    if ( catids != null ) {
	                        categories = lasConfig.getCategoriesSerializable(catids);
	                    }
	                } else {
	                    categories =  lasConfig.getCategoriesSerializable(null);

	                }

	            } else {
	                categories = lasConfig.getCategoriesSerializable(new String[]{id});
	            }
	        } else {
	            categories = lasConfig.getCategoriesSerializable(new String[]{id});
	        }
	        for (int i = 0; i < categories.length; i++) {
                categories[i].sortVariables();
            }
	        return categories; 
	    } catch (Exception e) {
	        throw new RPCException(e.getMessage());
	    }  
	}
//  public Map<String, String> getEnembleMembers(String[] dsID) throws RPCException {
//      LASConfig lasConfig = (LASConfig) getServletContext().getAttribute(LASConfigPlugIn.LAS_CONFIG_KEY);
//      Map<String, String> members = null;
//      if ( lasConfig.allowsSisters() ) {
//          // Group the dsIDS by the node where they reside.
//          
//          // Get the local names.
//          
//          // Fire a request to each host to get the names that belong on the remote host
//          
//          // Collect the lot into a single Map and return the result
//      } else {
//          try {
//              members = lasConfig.getEnsembleMembers(dsID);
//          } catch (JDOMException e) {
//              throw new RPCException(e.getMessage());
//          } catch (LASException e) {
//              throw new RPCException(e.getMessage());
//          }
//      }
//      return members;
//  }
    public GridSerializable getGrid(String dsID, String varID) throws RPCException {
        return getGridSerializable(dsID, varID);
    }
    private GridSerializable getGridSerializable(String dsID, String varID) throws RPCException {
        LASConfig lasConfig = getLASConfig();
        try {
            return lasConfig.getGridSerializable(dsID, varID);
        } catch (LASException e) {
            throw new RPCException(e.getMessage());
        }       
    }
    public OperationSerializable[] getOperations(String view, String[] xpath ) throws RPCException {
        if ( xpath.length == 1 ) {
            String dsid = LASConfig.getDSIDfromXPath(xpath[0]);
            String varid = LASConfig.getVarIDfromXPath(xpath[0]);
            return getOperations(view, dsid, varid);
        }
        LASConfig lasConfig = getLASConfig();
        List<OperationSerializable> multi_variable_operations = new ArrayList<OperationSerializable>();
        if ( lasConfig.allowsSisters() ) {

            Map<String, OperationSerializable> operations = new HashMap<String, OperationSerializable>();
            for (int i = 0; i < xpath.length; i++) {
                String dsid = LASConfig.getDSIDfromXPath(xpath[i]);
                String varid = LASConfig.getVarIDfromXPath(xpath[i]);
                OperationSerializable[] ops = getOperations(view, dsid, varid);
                for (int j = 0; j < ops.length; j++) {
                    operations.put(ops[j].getID(), ops[j]);
                }
            }
            int var_count = xpath.length;
            int minvars = -1;
            int maxvars = -1;
            
            for (Iterator opIt = operations.keySet().iterator(); opIt.hasNext();) {
                String opKey = (String) opIt.next();
                OperationSerializable op = operations.get(opKey);
                String min = null;
                String max = null;
                if ( op.getAttributes() != null ) {
                    min = op.getAttributes().get("minvars");
                    max = op.getAttributes().get("maxvars");
                }
                if ( min != null && !min.equals("") ) {
                    try {
                        minvars = Integer.valueOf(min).intValue();
                    } catch (Exception e) {
                        throw new RPCException("Cannot parse the minvars attribute value.");
                    }
                }
                if ( max != null && !max.equals("") ) {
                    try {
                        maxvars = Integer.valueOf(max).intValue();
                    } catch (Exception e) {
                        throw new RPCException("Cannot parse the maxvars attribute value.");
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
        } else {
            try {

                List<Operation> os = lasConfig.getOperations(view, xpath);
                for ( Iterator iterator = os.iterator(); iterator.hasNext(); ) {
                    Operation operation = (Operation) iterator.next();
                    multi_variable_operations.add(operation.getOperationSerializable());
                }
            } catch ( LASException e ) {
               throw new RPCException(e.getMessage());
            } catch ( JDOMException e ) {
               throw new RPCException(e.getMessage());
            }
        }

        int o = 0;
        OperationSerializable[] returnOps = new OperationSerializable[multi_variable_operations.size()];
        for (Iterator opIt = multi_variable_operations.iterator(); opIt.hasNext();) {
            
            OperationSerializable op = (OperationSerializable) opIt.next();
            returnOps[o] = op;
            o++;
        }
        return returnOps;
    }
    public OperationSerializable[] getOperations(String view, String dsID, String varID) throws RPCException {
        return getOperationsSerialziable(view, dsID, varID);
    }
    private OperationSerializable[] getOperationsSerialziable(String view, String dsID, String varID) throws RPCException {
    	LASConfig lasConfig = getLASConfig();
    	ArrayList<Operation> operations = new ArrayList<Operation>();
    	OperationSerializable[] wireOps = null;
    	try {


    		if ( dsID != null ) {

    			operations = lasConfig.getOperations(view, dsID, varID);

    			if ( operations.size() <= 0 ) {
    				throw new RPCException("No operations found.");
    			} else {
    				// Check to see if there's something in there.
    				Operation op = operations.get(0);
    				String id = op.getID();
    				if ( id == null || id.equals("") ) {
    					throw new RPCException("No operations found.");
    				}
    			}

    		}
    	} catch (JDOMException e) {
    		throw new RPCException(e.getMessage());
    	} catch (RPCException e) {
    		throw new RPCException(e.getMessage());
    	}
    	
    	int k=0;
    	wireOps = new OperationSerializable[operations.size()];
    	for (Iterator opsIt = operations.iterator(); opsIt.hasNext();) {
    		Operation op = (Operation) opsIt.next();
    		wireOps[k] = op.getOperationSerializable();
    		k++;
    	}
    	return wireOps;
    }
    public RegionSerializable[] getRegions(String dsID, String varID) throws RPCException {
        return getRegionsSerializable(dsID, varID);
    }
    private RegionSerializable[] getRegionsSerializable(String dsID, String varID) throws RPCException {
        LASConfig lasConfig = getLASConfig();
        ArrayList<Region> regions = new ArrayList<Region>();
        RegionSerializable[] wire_regions = null;
        if ( lasConfig.allowsSisters() ) {
            try {
                if ( dsID != null ) {
                    regions = lasConfig.getRegions(dsID, varID);
                    
                }
            } catch(Exception e) {
                throw new RPCException(e.getMessage());
            }
        } else {
            try {
                // Get the regions according to data set and variable.
                regions = lasConfig.getRegions(dsID, varID);
            } catch ( JDOMException e ) {
                throw new RPCException(e.getMessage());
            }
        }
        if ( regions.size() <= 0 ) {
            // TODO throw this.  In the meantime it's ok to send back an empty list since we're not using it on the client.
            //throw new RPCException("No regions found.");
        }
        wire_regions = new RegionSerializable[regions.size()];
        for( int i = 0; i < regions.size(); i++ ) {
            wire_regions[i] = regions.get(i).getRegionSerializable();
        }
        return wire_regions;
    }
    /**
     * (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.client.rpc.RPCService#getOptions(java.lang.String)
     * 
     */
    public OptionSerializable[] getOptions(String opid) throws RPCException {
        LASConfig lasConfig = getLASConfig();
        // The id is actually the name in the options section of the XML, but it's unique and functions as an id...
        ArrayList<Option> options = new ArrayList<Option>();
        OptionSerializable[] wireOptions;
        if ( lasConfig.allowsSisters() ) {
            try {
                if ( opid != null ) {

                    if ( !opid.contains(Constants.NAME_SPACE_SPARATOR) || lasConfig.isLocal(opid) ) {
                        if ( opid.contains(Constants.NAME_SPACE_SPARATOR) ) {
                            String[] parts = opid.split(Constants.NAME_SPACE_SPARATOR);
                            opid = parts[1];
                        }
                        options = lasConfig.getOptions(opid);
                    } else {
                        String[] parts = opid.split(Constants.NAME_SPACE_SPARATOR);
                        String server_key = null;
                        if ( parts != null ) {
                            server_key = parts[0];
                            if ( server_key != null ) {
                                Tributary trib = lasConfig.getTributary(server_key);    
                                // The server key is in the operations definition, we need to get it from the options without the server key.
                                opid = parts[1];
                                String las_url = trib.getURL() + Constants.GET_OPTIONS + "?format=xml&opid=" + opid;
                                String options_xml = lasProxy.executeGetMethodAndReturnResult(las_url);
                                LASDocument optionsdoc = new LASDocument();
                                JDOMUtils.XML2JDOM(options_xml, optionsdoc);
                                List opElements = optionsdoc.getRootElement().getChildren("option");
                                for (Iterator opIt = opElements.iterator(); opIt.hasNext();) {
                                    Element opElement = (Element) opIt.next();
                                    Option option = new Option((Element)opElement.clone());
                                    options.add(option);
                                }
                            }
                        }
                    }
                }
            } catch (JDOMException e) {
                throw new RPCException(e.getMessage());
            } catch (HttpException e) {
                throw new RPCException(e.getMessage());
            } catch (IOException e) {
                throw new RPCException(e.getMessage());
            } 
        } else {
            try {
                options = lasConfig.getOptions(opid);
            } catch (JDOMException e) {
                throw new RPCException(e.getMessage());
            }
        }
        int i=0;
        if ( options != null ) {
            wireOptions = new OptionSerializable[options.size()];
            for (Iterator optionIt = options.iterator(); optionIt.hasNext();) {
                Option option = (Option) optionIt.next();
                wireOptions[i] = option.getOptionSerializable();
                i++;
            }
            return wireOptions;
        } else {
            return null;
        }
    }
    /*
     * Everything from here down is for the ESGF search interface.
     */
    /**
     * Get ESGF Facets
     * Returns a list of ESGF search facet objects
     */
    public List<FacetSerializable> getFacets() throws RPCException {
        try {
            List<FacetSerializable> facets = new ArrayList<FacetSerializable>();
            LASConfig lasConfig = getLASConfig();
            String search = lasConfig.getGlobalPropertyValue("product_server", "esgf_search_url");
            List<Tributary> tribs = new ArrayList<Tributary>();
            if ( search != null && !search.equals("") ) {
                if ( search.contains(",") ) {
                    String endings[] = search.split(",");
                    for (int i = 0; i < endings.length; i++) {
                        tribs.addAll(lasConfig.getTributaries("url", endings[i].trim()));
                    }
                } else {
                    tribs.addAll(lasConfig.getTributaries("url", search));
                }
            } else {
                tribs.addAll(lasConfig.getTributaries("url", "gov"));
                tribs.addAll(lasConfig.getTributaries("url", "edu"));
            }
            for (Iterator iterator = tribs.iterator(); iterator.hasNext();) {
                Tributary tributary = (Tributary) iterator.next();
                String search_base = tributary.getURL().replace("las", "esg-search/search");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                boolean download = true;
                try {
                    lasProxy.executeGetMethodAndStreamResult(search_base+"?limit=0&facets=project,institute,model,submodel,instrument,experiment_family,experiment,subexperiment,time_frequency,product,realm,variable,variable_long_name,cmip_table,cf_standard_name,ensemble,data_node", stream);
                } catch ( Exception e ) {
                    download = false;
                }
                if ( download ) {
                    ESGFSearchDocument doc = new ESGFSearchDocument();
                    JDOMUtils.XML2JDOM(stream.toString(), doc);
                    if ( doc.getStatus() == 0 ) {
                        facets = doc.getFacets();
                        return facets;
                    }
                }
            }
            return facets;
        } catch ( IOException e ) {
            throw new RPCException(e.getMessage());
        } catch ( JDOMException e ) {
            throw new RPCException(e.getMessage());
        }
    }
    /**
     * Return ESGF data sets matching a particular query string.
     * @see <a href="http://www.esgf.org/wiki/ESGF_Search_REST_API">http://www.esgf.org/wiki/ESGF_Search_REST_API</a>
     */
    public List<ESGFDatasetSerializable> getESGFDatasets(String query) throws RPCException {
        List<ESGFDatasetSerializable> datasets = new ArrayList<ESGFDatasetSerializable>();
        
        try {
            LASConfig lasConfig = getLASConfig();
            String search = lasConfig.getGlobalPropertyValue("product_server", "esgf_search_url");
            List<Tributary> tribs = new ArrayList<Tributary>();
            if ( search != null && !search.equals("") ) {
                if ( search.contains(",") ) {
                    String endings[] = search.split(",");
                    for (int i = 0; i < endings.length; i++) {
                        tribs.addAll(lasConfig.getTributaries("url", endings[i].trim()));
                    }
                } else {
                    tribs.addAll(lasConfig.getTributaries("url", search));
                }
            } else {
                tribs.addAll(lasConfig.getTributaries("url", "gov"));
                tribs.addAll(lasConfig.getTributaries("url", "edu"));
            }
            for (Iterator iterator = tribs.iterator(); iterator.hasNext();) {
                Tributary tributary = (Tributary) iterator.next();
                String search_base = tributary.getURL().replace("las", "esg-search/search");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                boolean success = true;
                try {
                	lasProxy.executeGetMethodAndStreamResult(search_base+"?"+query, stream);
                } catch (Exception e ) {
                	success = false;
                }
                if ( success ) {
                	ESGFSearchDocument doc = new ESGFSearchDocument();
                	JDOMUtils.XML2JDOM(stream.toString(), doc);
                	if ( doc.getStatus() == 0 ) {
                		datasets = doc.getDatasets();
                	}
                	if ( datasets.size() > 0 ) break;
                }
            }
            for (Iterator datasetsIt = datasets.iterator(); datasetsIt.hasNext();) {
                ESGFDatasetSerializable esgfDatasetSerializable = (ESGFDatasetSerializable) datasetsIt.next();
                Dataset d = lasConfig.getDataset(esgfDatasetSerializable.getLASID());
                if ( d != null ) {
                    esgfDatasetSerializable.setAlreadyAdded(true);
                } else {
                    esgfDatasetSerializable.setAlreadyAdded(false);
                }
            }
            return datasets;
        } catch ( IOException e ) {
            throw new RPCException(e.getMessage());
        } catch ( JDOMException e ) {
            throw new RPCException(e.getMessage());
        } catch (LASException e) {
            throw new RPCException(e.getMessage());
        }
    }
    public String[] addESGFDatasets(List<String> ids) throws RPCException {
        String[] rids = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            rids[i] = addESGFDataset(ids.get(i));
        }
        return rids;
    }
    public String addESGFDataset(String id) throws RPCException {
        LASConfig lasConfig = getLASConfig();
        ServerConfig serverConfig = getServerConfig();
        String keyid = null;
        try {
            keyid = lasConfig.addDataset(id);
            lasConfig.convertToSeven(true);
            lasConfig.mergeProperites();
            lasConfig.addIntervalsAndPoints();    
            lasConfig.addGridType();
            
            HttpServletRequest request = this.getThreadLocalRequest();
            String [] catids = (String[]) request.getSession().getAttribute("catid");
            boolean add = true;          
            if ( catids != null && keyid != null) {
                for ( int i = 0; i < catids.length; i++ ) {
                    if ( keyid.equals(catids[i]) ) {
                        add = false;
                    }
                }
                if ( add ) {
                    String[] c = new String[catids.length+1];
                    for ( int i = 0; i < catids.length; i++ ) {
                        c[i] = catids[i];
                    }
                    c[catids.length] = keyid;
                    request.getSession().setAttribute("catid", c);
                }
            } else {
                String[] c = new String[1];
                c[0] = keyid;
                request.getSession().setAttribute("catid", c);
            }
            return keyid;
        } catch ( HttpException e ) {
            throw new RPCException(e.getMessage());
        } catch ( JDOMException e ) {
            throw new RPCException(e.getMessage());
        } catch ( IOException e ) {
            throw new RPCException(e.getMessage());
        } catch ( LASException e ) {
            throw new RPCException(e.getMessage());
        }       
    }
    /**
     * @deprecated
     */
    public List<String> getTributaryServers() {
        List<String> wiretribs = new ArrayList<String>();
        LASConfig lasConfig;
		try {
			lasConfig = getLASConfig();
	        ArrayList<Tributary> tribs = lasConfig.getTributaries();
	        for (Iterator<Tributary> tribIt = tribs.iterator(); tribIt.hasNext();) {
	            Tributary tributary = (Tributary) tribIt.next();
	            String url = tributary.getURL()+"/auth.do";
	            wiretribs.add(url);
	        }
	        return wiretribs;
		} catch (RPCException e) {
			// Ignoring the exception since this method is deprecated anyway
			e.printStackTrace();
		}
		return null;
    }
    @Override
    protected void checkPermutationStrongName() throws SecurityException {
        // we are going to completely forego this check...
    }
    /**
     * @param dsid the data set id to use for find the values
     * @param varid the variable id to use for find the values
     * @param key_variable if the values are one variable and the key are another (like cruise_name matches one to one with cruise_id)
     * @param constraint the original ERDDAPConstraint object that trigger this request. Use to add labels. Could be empty or null
     * @param constraints the values might need to be constrained to some sub set like in the case where the menu is used to build the id list for the thumbnails. could be empty 
     * Get the values of an ERDDAP "subset" variable to use as a way to select which DSG items are selected from the ERRDDAP server.
     * A query looks something like this:
     * 
     * http://XXXXX/erddap/tabledap/OSMCV4_DUO_SURFACE_TRAJECTORY.json?platform_type&distinct()
     * 
     */
    @Override
    public Map<String, String> getERDDAPOuterSequenceValues(String dsid, String varid, String key_variable, ERDDAPConstraint constraint, List<ConstraintSerializable> constriants) throws RPCException {
        Map<String, String> outerSequenceValues = new TreeMap<String, String>();
        ByteArrayOutputStream jsonStream = new ByteArrayOutputStream();
        try {
            LASConfig lasConfig = getLASConfig();
            Dataset dataset = lasConfig.getDataset(dsid);
            String url = lasConfig.getDataAccessURL(dsid, varid, false);
            String shortname = lasConfig.getVariableName(dsid, varid);
            Map<String, Map<String, String>> props = dataset.getPropertiesAsMap();
            String id = props.get("tabledap_access").get("id");
            if ( shortname.equals(key_variable) ) {
                url = url + id+".json?"+shortname+"&distinct()";
            } else {
                url = url + id+".json?"+key_variable+","+shortname+"&distinct()";
            }
            String time_name = lasConfig.getProperty("tabledap_access", "time");
            String lon_name = lasConfig.getProperty("tabledap_access", "longitude");

            StringBuilder xquery1 = new StringBuilder();
            StringBuilder xquery2 = new StringBuilder();
            String xhi = null;
            String xlo = null;
            // If they exist, add the other constraints...
            List<Constraint> textConstraints = new ArrayList<Constraint>();
            for (Iterator iterator = constriants.iterator(); iterator.hasNext();) {
                ConstraintSerializable constraintSerializable = (ConstraintSerializable) iterator.next();
                
                String lhs = constraintSerializable.getLhs();
                String op = constraintSerializable.getOp();
                String rhs = constraintSerializable.getRhs();
                String type = constraintSerializable.getType();
                
                if ( type.equals("variable")) {
                    String sname = lasConfig.getVariableName(dsid, lhs);
                    if ( sname != null && !sname.equals("") ) {
                        lhs = sname;
                    }
                }
                if ( lhs.equals(time_name) ) {
                    rhs = reformatFerretToISO(rhs);
                }
                if ( lhs.equals(lon_name) ) {
                    if ( op.equals("lt") || op.equals("le") ) {
                        xhi = rhs;
                    } else {
                        xlo = rhs;
                    }
                } else {
                    Constraint c = new Constraint(lhs, op, rhs);
                    textConstraints.add(c);
                }
              
            }
            
            String cq = ERDDAPUtil.getConstraintQuery(textConstraints);
            xquery1.append(cq);
            xquery2.append(cq);

            Map<String, String> labels = constraint.getLabels();
            // Decide what to do about x now that we have the info.
   	 	            // If the data set is 0 to 360 and the UI sends -180 to 180, re-normalize 
            Map<String, String> dt = dataset.getPropertiesAsMap().get("tabledap_access");   
            boolean is360 = false; 
            if ( dt != null ) { 
                String range = dt.get("lon_domain"); 
                is360 = !range.contains("180"); 
            } 
            List<String> lonQueries = ERDDAPUtil.getLongitudeQuery(is360, xlo, xhi);
            String lonQ1 = lonQueries.get(0);
            String lonQ2 = null;
            if ( lonQueries.size() > 1 ) {
            	lonQ2 = lonQueries.get(1);
            }
            xquery1.append(lonQ1);
            if ( lonQ2 != null ) {
            	xquery2.append(lonQ2);
            }
            // Always to the first query.
                String q1url = url + xquery1.toString();

                lasProxy.executeGetMethodAndStreamResult(q1url, jsonStream);
                String jsonText = jsonStream.toString();
                JSONObject json = new JSONObject(jsonText);
                JSONArray v = json.getJSONObject("table").getJSONArray("rows");
                for (int i = 0; i < v.length(); i++) {
                    JSONArray s = v.getJSONArray(i);
                    String t = s.getString(0);
                    if ( shortname.equals(key_variable) && labels == null) {
                        outerSequenceValues.put(t,t);
                    } else {
                        // The config specifies the names...
                        if ( labels != null ) {
                            String u = labels.get(t);
                            if ( u != null ) { 
                                outerSequenceValues.put(t,u); 
                            } else { 
                                // No label found for this one 
                                outerSequenceValues.put(t,t); 
                            } 
                        } else {
                            // A second variable specifies the names
                            String u = s.getString(1);
                            outerSequenceValues.put(t, u);
                        }
                    }
                }
            
            if ( lonQ2 != null ) {
                String q2url = url + xquery2.toString();

                lasProxy.executeGetMethodAndStreamResult(q2url, jsonStream);
                String jsonText2 = jsonStream.toString();
                JSONObject json2 = new JSONObject(jsonText2);
                JSONArray v2 = json2.getJSONObject("table").getJSONArray("rows");
                for (int i = 0; i < v2.length(); i++) {
                    JSONArray s = v2.getJSONArray(i);
                    String t = s.getString(0);
                    if ( shortname.equals(key_variable) ) {
                        outerSequenceValues.put(t,t);
                    } else {
                        String u = s.getString(1);
                        outerSequenceValues.put(t, u);
                    }
                }
            }
        } catch (MalformedURLException e) {
            throw new RPCException(e.getMessage());
        } catch (IOException e) {
            throw new RPCException(e.getMessage());
        } catch (JSONException e) {
            throw new RPCException(e.getMessage());
        } catch (JDOMException e) {
            throw new RPCException(e.getMessage());
        } catch (LASException e) {
            throw new RPCException(e.getMessage());
        } catch (HttpException e) {
            throw new RPCException(e.getMessage());
        }
        return outerSequenceValues;
    }
    @Override
    public String getERDDAPGeometry(String catid, String dsid, String varid, String variables) throws RPCException {
        LASConfig lasConfig = getLASConfig();
        Dataset dataset;
        InputStream jsonStream;
       
        try {
            dataset = lasConfig.getDataset(dsid);
            String url = lasConfig.getDataAccessURL(dsid, varid, false);
            Map<String, Map<String, String>> props = dataset.getPropertiesAsMap();
            String id = props.get("tabledap_access").get("id");
            String id_name = props.get("tabledap_access").get("trajectory_id");
            boolean trajectory = true;
            if ( id_name == null || id_name.trim().length() == 0 ) {
                id_name = props.get("tabledap_access").get("profile_id");
                trajectory = false;
            }
            if ( id_name == null || id_name.trim().length() == 0 ) {
                id_name = props.get("tabledap_access").get("timeseries_id");
                trajectory = false;
            }
            if ( trajectory ) {
                variables = variables+","+id_name;
            } 
            url = url + id + ".json" + "?" + variables + "&distinct()";
            if ( variables.contains("time") ) {
                String tq;
                if ( trajectory ) {
                    tq = URLEncoder.encode(id_name+",time,1 hours");
                } else {
                    tq = URLEncoder.encode("time,1 hours");
                }
            	url = url + "&orderByClosest(\""+tq+"\")";
            } else if ( variables.contains("time") && id_name != null ) {
            	url = url + "&orderBy(\"time,"+id_name+"\")";
            }

            log.debug("ERDDAP GEOMETRY reqeust: " + url);

            jsonStream = new URL(url).openStream();
            String jsonText = IOUtils.toString(jsonStream);
            
            jsonText = jsonText.replace("\"rows\"", "\"catid\":\""+catid+"\",\n\"rows\"");
            
            return jsonText;
        } catch (JDOMException e) {
            throw new RPCException(e.getMessage());
        } catch (LASException e) {
            throw new RPCException(e.getMessage());
        } catch (MalformedURLException e) {
            throw new RPCException(e.getMessage());
        } catch (IOException e) {
            throw new RPCException(e.getMessage());
        }
    }
    @Override
    public String getERDDAPJSON(String dsid, String varid, String platform_id_value, String variables) throws RPCException {
        LASConfig lasConfig = getLASConfig();
        Dataset dataset;
        InputStream jsonStream;
       
        try {
            dataset = lasConfig.getDataset(dsid);
            String url = lasConfig.getDataAccessURL(dsid, varid, false);
            Map<String, Map<String, String>> props = dataset.getPropertiesAsMap();
            String id = props.get("tabledap_access").get("id");
            String id_name = props.get("tabledap_access").get("trajectory_id");
            if ( id_name == null || id_name.trim().length() == 0 ) {
                id_name = props.get("tabledap_access").get("profile_id");
            }
            if ( id_name == null || id_name.trim().length() == 0 ) {
                id_name = props.get("tabledap_access").get("timeseries_id");
            }
            String n = URLEncoder.encode("\""+platform_id_value+"\"", "UTF-8");
            String q = variables + "&" + id_name + "="+n;
            url = url + id + ".json" + "?" + q + "&distinct()";
            
            jsonStream = lasProxy.executeGetMethodAndReturnStream(url, null);
            String jsonText = IOUtils.toString(jsonStream);
            return jsonText;
        } catch (JDOMException e) {
            throw new RPCException(e.getMessage());
        } catch (LASException e) {
            throw new RPCException(e.getMessage());
        } catch (MalformedURLException e) {
            throw new RPCException(e.getMessage());
        } catch (IOException e) {
            throw new RPCException(e.getMessage());
        } catch (HttpException e) {
        	 throw new RPCException(e.getMessage());
		}
       
    }
   
    @Override
    public Map<String, String> getERDDAPOuterSequenceVariables(String dsid, String varid) throws RPCException {
        Map<String, String> osv = new TreeMap<String, String>();
        DAS das = new DAS();
        InputStream input;
        
        try {
            LASConfig lasConfig = getLASConfig();
            Dataset dataset = lasConfig.getDataset(dsid);
            String url = lasConfig.getDataAccessURL(dsid, varid, false);
            Map<String, Map<String, String>> props = dataset.getPropertiesAsMap();
            String id = props.get("tabledap_access").get("id");
            url = url + id;
            input = new URL(url+".das").openStream();
            das.parse(input);
            AttributeTable variableAttributes = das.getAttributeTable("s");
            AttributeTable global = das.getAttributeTable("NC_GLOBAL");
            Attribute cdm_trajectory_variables_attribute = global.getAttribute("cdm_trajectory_variables");
            if ( cdm_trajectory_variables_attribute != null ) {
                Iterator<String> trajectory_variables_attribute_values = cdm_trajectory_variables_attribute.getValuesIterator();
                if ( trajectory_variables_attribute_values.hasNext() ) {
                    // Work with the first value...
                    String trajectory_variables_value = trajectory_variables_attribute_values.next();
                    String[] trajector_variables = trajectory_variables_value.split(",");
                    for (int i = 0; i < trajector_variables.length; i++) {
                        String variable = trajector_variables[i].trim();
                        Attribute tva = variableAttributes.getAttribute(variable);
                        //TODO null check each of these.
                        String long_name = tva.getContainer().getAttribute("long_name").getValueAt(0);
                        osv.put(variable, long_name);
                    }
                }
            } else {
                throw new RPCException("No cdm_trajectory_variables GLOBAL attribute found in the data source.");
            }
            // There will be only one value
        } catch (MalformedURLException e) {
            throw new RPCException(e.getMessage());
        } catch (IOException e) {
            throw new RPCException(e.getMessage());
        } catch (DAP2Exception e) {
            throw new RPCException(e.getMessage());
        } catch (LASException e) {
            throw new RPCException(e.getMessage());
        } catch (JDOMException e) {
            throw new RPCException(e.getMessage());
        } 
        return osv;
    }
    private List<ERDDAPConstraintGroup> getERRDAPConstraintGroups(String dsid) throws RPCException {
        LASConfig lasConfig = getLASConfig();
        try {
            return lasConfig.getERDDAPConstraintGroups(dsid);
        } catch (JDOMException e) {
           throw new RPCException(e.getMessage());
        } catch (LASException e) {
            throw new RPCException(e.getMessage());
        }
    }
    @Override
    public List<ERDDAPConstraintGroup> getERDDAPConstraintGroups(String dsid) throws RPCException {
        return getERDDAPConstraintGroups(dsid);
    }
   
    private String reformatFerretToISO(String in) {
        Chronology chrono = GregorianChronology.getInstance(DateTimeZone.UTC);
        DateTimeFormatter iso = ISODateTimeFormat.dateTime().withChronology(chrono).withZone(DateTimeZone.UTC);;
        DateTimeFormatter sFerretForm = DateTimeFormat.forPattern("dd-MMM-yyyy").withChronology(chrono).withZone(DateTimeZone.UTC);
        DateTimeFormatter lFerretForm = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm").withChronology(chrono).withZone(DateTimeZone.UTC);

        DateTime td;
        try {
            td = lFerretForm.parseDateTime(in).withZone(DateTimeZone.UTC).withChronology(chrono);
        } catch (Exception e) {
            try {
                td = sFerretForm.parseDateTime(in).withZone(DateTimeZone.UTC).withChronology(chrono);
            } catch (Exception e2) {
                return null;
            }
        }
        if ( td != null ) {
            return iso.print(td.getMillis());
        } else {
            return null;
        }
    }
}
