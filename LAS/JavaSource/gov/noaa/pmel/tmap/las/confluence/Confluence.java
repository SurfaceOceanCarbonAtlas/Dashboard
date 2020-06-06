package gov.noaa.pmel.tmap.las.confluence;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASMapScale;
import gov.noaa.pmel.tmap.las.jdom.LASRegionIndex;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;
import gov.noaa.pmel.tmap.las.product.server.InitThread;
import gov.noaa.pmel.tmap.las.product.server.LASAction;
import gov.noaa.pmel.tmap.las.product.server.LASConfigPlugIn;
import gov.noaa.pmel.tmap.las.proxy.LASProxy;
import gov.noaa.pmel.tmap.las.ui.GetMetadata;
import gov.noaa.pmel.tmap.las.ui.Util;
import gov.noaa.pmel.tmap.las.util.Category;
import gov.noaa.pmel.tmap.las.util.Constants;
import gov.noaa.pmel.tmap.las.util.Result;
import gov.noaa.pmel.tmap.las.util.Tributary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpException;
import org.apache.struts2.ServletActionContext;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;

import com.google.gwt.safehtml.shared.UriUtils;

public class Confluence extends LASAction {

	private static Logger log = LoggerFactory.getLogger(Confluence.class.getName());

	private static final LASProxy lasProxy = new LASProxy();

	private String request_format;
	
	private static String LAZY_START = "lazy_start";
	private static String BATCH = "batch";

	public static final HashMap<String, String[]> ajaxCalls =   
		new HashMap<String, String[]>()   
		{  
			//Unnamed Block.  
			{  
				put(Constants.GET_CATEGORIES, new String[]{"catid"}); 
				put(Constants.GET_DATASETS, new String[]{"dsid", "varid"});
				put(Constants.GET_DATACONSTRAINTS, new String[]{"dsid", "varid"});  
				put(Constants.GET_GRID,  new String[]{"dsid", "varid"});  
				put(Constants.GET_METADATA, new String[]{"dsid", "catitem", "opendap"});  
				put(Constants.GET_OPERATIONS, new String[]{"dsid", "varid", "view"}); 
				put(Constants.GET_OPTIONS, new String[]{"dsid", "opid"});
				put(Constants.GET_REGIONS, new String[]{"dsid", "varid"});
				put(Constants.GET_UI, new String[]{"dsid", "varid"});
				put(Constants.GET_VARIABLES, new String[]{"dsid"});
				put(Constants.GET_VARIABLE, new String[]{"dsid", "varid"});
				put(Constants.GET_VIEWS, new String[]{"dsid", "varid"});
				put(Constants.GET_ANNOTATIONS, new String[]{"dsid", "file"});
			}  
		};
		
		public static final HashMap<String, String> services =   
			new HashMap<String, String>()   
			{  
				//Unnamed Block.  
				{  
					put(Constants.GET_CATEGORIES_KEY, Constants.GET_CATEGORIES);  
					put(Constants.GET_DATASETS_KEY, Constants.GET_DATASETS);
					put(Constants.GET_DATACONSTRAINTS_KEY, Constants.GET_DATACONSTRAINTS);  
					put(Constants.GET_GRID_KEY, Constants.GET_GRID);  
					put(Constants.GET_METADATA_KEY, Constants.GET_METADATA);  
					put(Constants.GET_OPERATIONS_KEY, Constants.GET_OPERATIONS); 
					put(Constants.GET_OPTIONS_KEY, Constants.GET_OPTIONS);
					put(Constants.GET_REGIONS_KEY, Constants.GET_REGIONS);
					put(Constants.GET_UI_KEY, Constants.GET_UI);
					put(Constants.GET_VARIABLES_KEY, Constants.GET_VARIABLES);
					put(Constants.GET_VARIABLE_KEY, Constants.GET_VARIABLE);
					put(Constants.GET_VIEWS_KEY, Constants.GET_VIEWS);
					put(Constants.GET_AUTH_KEY, Constants.GET_AUTH);
					put(Constants.GET_ANNOTATIONS_KEY, Constants.GET_ANNOTATIONS);
				}  
			};
		public String execute(){

			String lazy_start = (String) contextAttributes.get(LASConfigPlugIn.LAS_LAZY_START_KEY);
	        if ( lazy_start != null && lazy_start.equals("true") ) {
	        	// Start the initialization and forward to lazy start page
	        	String init_running = (String) contextAttributes.get(LASConfigPlugIn.LAS_LAZY_START_RUNNING_KEY);
	        	if ( init_running == null ) {
	        		contextAttributes.put(LASConfigPlugIn.LAS_LAZY_START_RUNNING_KEY, "true");
	        	    InitThread thread = new InitThread(ServletActionContext.getServletContext());
	        	    thread.start();
	        	}
	        	log.debug("Init is running...");
	        	return LAZY_START;
	        }
			
			String openid = request.getParameter("openid");
			LASConfig lasConfig = (LASConfig) contextAttributes.get(LASConfigPlugIn.LAS_CONFIG_KEY);

			ArrayList<Tributary> servers = new ArrayList<Tributary>();

			servers = lasConfig.getTributaries();

			request_format = "";
			// Process the request...
			String url = request.getRequestURL().toString();
			String proxy = lasConfig.getGlobalPropertyValue("product_server", "proxy");
			if ( proxy == null ) {
				proxy = "partial";
			}
			try {
				if ( url.contains(Constants.GET_UI) ) {
					// Collect the query parameters.

					// Find the first variable in the tree

					// Forward to the local getUI
					if ( lasConfig.pruneCategories() ) {


						String[] catids = request.getParameterValues("catid");
						String[] dsids = request.getParameterValues("dsid");
						String varid = request.getParameter("varid");

						if ( catids != null && catids.length > 0 ) {
							request.getSession().setAttribute("catid", catids);
							ArrayList<Category> cats = getCategories(catids[0], lasConfig, request, true);
							String dsid = null;
							for (Iterator catsIt = cats.iterator(); catsIt.hasNext();) {
								Category category = (Category) catsIt.next();
								dsid = findFirstVariable(category, lasConfig, request);
							}
							if ( dsid != null ) {
								StringBuilder q = new StringBuilder("localGetUI.do?auto=true&dsid="+dsid);
								if ( varid != null ) {
									q.append("&varid="+varid);
								}
								response.sendRedirect(UriUtils.sanitizeUri(q.toString()));
							} else {
								response.sendRedirect("localGetUI.do");
							}
						}
						if ( dsids != null && dsids.length > 0 ) {
							request.getSession().setAttribute("dsid", dsids);
							StringBuilder q = new StringBuilder("localGetUI.do?auto=true&dsid="+dsids[0]);
							for (int i = 1; i < dsids.length; i++) {
								q.append("&dsid="+dsids[i]);
							}
							if ( varid != null ) {
								q.append("&varid="+varid);
							}
							response.sendRedirect(UriUtils.sanitizeUri(q.toString()));
						}
						if ( dsids != null && varid != null ) {
							request.getSession().setAttribute("varid", varid);
							request.getSession().setAttribute("dsid", dsids);
							StringBuilder q = new StringBuilder("localGetUI.do?auto=true&dsid="+dsids[0]);
							for (int i = 1; i < dsids.length; i++) {
								q.append("&dsid="+dsids[i]);
							}
							q.append("&varid="+varid);
							response.sendRedirect(UriUtils.sanitizeUri(q.toString()));
						}
						if (catids == null && dsids == null ) {
							return Constants.CATEGORIES_REQUIRED_KEY;
						}
					} else {
						response.sendRedirect("localGetUI.do");
					}

				} else if ( url.contains(Constants.GET_DATASETS) ) {
					String dsid = request.getParameter("dsid");
					if ( dsid.contains(Constants.NAME_SPACE_SPARATOR) ) {
						String[] parts = dsid.split(Constants.NAME_SPACE_SPARATOR);
						String server_key = parts[0];
						if ( server_key.equals(lasConfig.getBaseServerURLKey()) ) {
							return Constants.GET_DATASETS_KEY;
						}

						dsid = parts[1];

						Tributary trib = lasConfig.getTributary(server_key);
						String las_url = trib.getURL() + Constants.GET_DATASETS + "?dsid=" + dsid;
						lasProxy.executeGetMethodAndStreamResult(las_url, response);
					} else {
						// Send it to the local server which will work most of the time..
						return Constants.GET_DATASETS_KEY;
					}
					
				} else if ( url.contains(Constants.GET_CATEGORIES) ) {
				
					ArrayList<Category> categories = new  ArrayList<Category>();

					if ( request.getParameter("catid") ==  null) {
						// Check to see if this deployment wants the category tree trimmed 
						// according to the parameters on the initial getUI query string.
						if ( lasConfig.pruneCategories() ) {

							String [] catids = (String[]) request.getSession().getAttribute("catid");
							// The getUI class has to deal with these two things
							String [] dsids = (String[]) request.getSession().getAttribute("dsid");
							String varid = (String) request.getSession().getAttribute("varid");
							
							if ( catids != null ) {
								if ( catids.length > 1 ) {
									// In this case the top level categories are these and only these...
									for (int i = 0; i < catids.length; i++) {
										categories.addAll(getCategories(catids[i], lasConfig, request, false));
									}
								} else { 
									categories.addAll(getCategories(catids[0], lasConfig, request, false));
								}

								
								String out = Util.toJSON(categories, "categories").toString();
								log.error(out);
								InputStream is = new ByteArrayInputStream(out.getBytes("UTF-8"));
								lasProxy.stream(is, response.getOutputStream());
							}

						} else {
							// The only other possible parameter is the format.
							if ( request.getParameter("format") != null ) {
								// Save it.  When use it when we stream the response...
								request_format = request.getParameter("format");
							}

							// Start with the categories on this server...
							Category local_cat = new Category(lasConfig.getTitle(), lasConfig.getTopLevelCategoryID()); 
							categories.add(local_cat);

							for (Iterator servIt = servers.iterator(); servIt.hasNext();) {
								Tributary trib = (Tributary) servIt.next();						
								Category server_cat = new Category(trib.getName(), trib.getTopLevelCategoryID());
								if ( !proxy.equalsIgnoreCase("full") ) {
									if ( openid != null ) {
										server_cat.setAttribute("remote_las", trib.getURL()+Constants.GET_AUTH);
									}
								}
								categories.add(server_cat);
							}
							InputStream is = new ByteArrayInputStream(Util.toJSON(categories, "categories").toString().getBytes("UTF-8"));
							lasProxy.stream(is, response.getOutputStream());

						} 
					} else {
						String catid = request.getParameter("catid");
						String format = request.getParameter("format");
						if ( format == null ) format = "json";
						String server_key = catid.split(Constants.NAME_SPACE_SPARATOR)[0];

						if ( server_key.equals(lasConfig.getBaseServerURLKey()) ) {

							return Constants.GET_CATEGORIES_KEY;

						} else {

							Tributary trib = lasConfig.getTributary(server_key);
							String las_url = trib.getURL() + Constants.GET_CATEGORIES;					

							if ( !catid.endsWith(Constants.NAME_SPACE_SPARATOR+JDOMUtils.MD5Encode(trib.getName()))) {
								// If this is a request of the top level category for this server
								// the id must be left off the request.  In this case it's not so
								// add it on.
								las_url = las_url + "?" + request.getQueryString();
							}
							lasProxy.executeGetMethodAndStreamResult(las_url, response);
						}

					}
				} else if (url.contains(Constants.GET_OPTIONS)) {

					// I think there is a bug in the client where it requests the options without the server key initially
					// so I'm going to throw that request away.

					String opid = request.getParameter("opid");
					if ( opid.contains(Constants.NAME_SPACE_SPARATOR) ) {
						String[] parts = opid.split(Constants.NAME_SPACE_SPARATOR);
						String server_key = parts[0];
						if ( server_key.equals(lasConfig.getBaseServerURLKey()) ) {
							return Constants.GET_OPTIONS_KEY;
						}

						opid = parts[1];

						Tributary trib = lasConfig.getTributary(server_key);
						String las_url = trib.getURL() + Constants.GET_OPTIONS + "?opid=" + opid;
						lasProxy.executeGetMethodAndStreamResult(las_url, response);
					} else {
						// Send it to the local server which will work most of the time..
						return Constants.GET_OPTIONS_KEY;
					}
				} else if ( url.contains(Constants.GET_ANNOTATIONS) ) {
					String catid = request.getParameter("catid");
					if ( catid != null ) {
						String server_key = catid.split(Constants.NAME_SPACE_SPARATOR)[0];
						if ( server_key != null ) {
							if ( server_key.equals(lasConfig.getBaseServerURLKey()) ) {
								// Process here as normal...
								return Constants.GET_ANNOTATIONS_KEY;
							} else {
								Tributary trib = lasConfig.getTributary(server_key);
								String las_url = trib.getURL();
								if ( proxy.equalsIgnoreCase("full") ) {
									return processRequest(request, response, lasConfig, las_url, server_key, openid);
								} else {
									las_url = las_url + Constants.PRODUCT_SERVER + "?" + request.getQueryString();	
									lasProxy.executeGetMethodAndStreamResult(las_url, response);
								}
							}
						}
					}
				} else if (url.contains(Constants.PRODUCT_SERVER)) {
					String xml = request.getParameter("xml");
					if ( xml == null ) {
						// Local request for the info page...
						return "LocalProductServer";
					}
					LASUIRequest ui_request = new LASUIRequest();
					try {
						JDOMUtils.XML2JDOM(xml, ui_request);
					} catch (IOException e) {
						logerror(request, "Failed to parse XML request.", e);
						return ERROR;					
					} catch (JDOMException e) {
						logerror(request, "Failed to parse XML request.", e);
						return ERROR;
					}
					ArrayList<String> ids = ui_request.getDatasetIDs();

					String op = ui_request.getOperation();
					String service = "";
					try {
						service = lasConfig.getService(op);
					} catch (JDOMException e) {
						logerror(request, "Unable to find service name.", e);
						return ERROR;
					}
					if ( ids.size() == 0 || (service !=null && service.equals("template")) ) {
						// Forward to the local server...
						return "LocalProductServer";
					} else if ( ids.size() > 1 ) {
						// Check to see how many servers are needed for this product.

						HashMap<String, Tributary> tribs = new HashMap<String, Tributary>();
						for (Iterator idsIt = ids.iterator(); idsIt.hasNext();) {
							String id = (String) idsIt.next();
							String server_key = id.split(Constants.NAME_SPACE_SPARATOR)[0];
							Tributary trib = lasConfig.getTributary(server_key);
							tribs.put(server_key, trib);
						}

						if ( tribs.size() == 1 ) {
							String key = (String) tribs.keySet().toArray()[0];
							// Multiple variables, but one server so send to the appropriate server.
							if ( key.equals(lasConfig.getBaseServerURLKey()) ) {
								// Process here as normal...
								return Constants.LOCAL_PRODUCT_SERVER_KEY;
							} else {
								String las_url = tribs.get(key).getURL();
								if ( proxy.equalsIgnoreCase("full") ) {
									return processRequest(request, response, lasConfig, las_url, key, openid);
								} else {
									las_url = las_url + Constants.PRODUCT_SERVER + "?" + request.getQueryString();	
									lasProxy.executeGetMethodAndStreamResult(las_url, response);
								}
							}
						} else {
							// Add the special parameter to create product locally using remote analysis and send to local product server.
							return Constants.LOCAL_PRODUCT_SERVER+"?remote_las=true&xml="+xml;
						}
					} else {

						// Get the server key and forward to that server...
						String server_key = ids.get(0).split(Constants.NAME_SPACE_SPARATOR)[0];
						if ( server_key.equals(lasConfig.getBaseServerURLKey()) ) {
							return "LocalProductServer";
						}
						Tributary trib = lasConfig.getTributary(server_key);
						String las_url = trib.getURL();

						if ( proxy.equalsIgnoreCase("full") ) {
							return processRequest(request, response, lasConfig, las_url, server_key, openid);
						} else {
							las_url = las_url + Constants.PRODUCT_SERVER + "?" + request.getQueryString();	
							lasProxy.executeGetMethodAndStreamResult(las_url, response);
						}
					}
				} else {
					String dsid = request.getParameter("dsid");
					// All xpaths will come from the same data set for now.  :-)
					// TODO cross-server data set comparisons
					if ( dsid == null ) {
						String[] xpaths = request.getParameterValues("xpath");
						dsid = LASConfig.getDSIDfromXPath(xpaths[0]);
					}
					String server_key = dsid.split(Constants.NAME_SPACE_SPARATOR)[0];

					boolean local = server_key.equals(lasConfig.getBaseServerURLKey());
					String las_url = null;
					if ( !local ) {
						Tributary trib = lasConfig.getTributary(server_key);
						las_url = trib.getURL();
					}
					if ( url.contains(Constants.GET_DATACONSTRAINTS) ) {
						if ( local ) {
							return Constants.GET_DATACONSTRAINTS_KEY;
						} else {
							las_url = las_url + Constants.GET_DATACONSTRAINTS + "?" + request.getQueryString();
						}
					} else if ( url.contains(Constants.GET_GRID) ) {
						if ( local ) {	
							return Constants.GET_GRID_KEY;
						} else {
							las_url = las_url + Constants.GET_GRID + "?" + request.getQueryString();
						}
					} else if ( url.contains(Constants.GET_METADATA) ) {
						if ( local ) {
							GetMetadata gm = new GetMetadata();
							return "/LocalProductServer.do?xml="+gm.prepareURL(request, response);
						} else {		
							las_url = las_url + Constants.GET_METADATA + "?" + request.getQueryString();
						}
					} else if ( url.contains(Constants.GET_OPERATIONS) ) {
						if ( local ) {
							return Constants.GET_OPERATIONS_KEY;
						} else {
							las_url = las_url + Constants.GET_OPERATIONS + "?" + request.getQueryString();
						}
					} else if ( url.contains(Constants.GET_REGIONS) ) {
						if ( local ) {
							return Constants.GET_REGIONS_KEY;
						} else {
							las_url = las_url + Constants.GET_REGIONS + "?" + request.getQueryString();
						}
					} else if ( url.contains(Constants.GET_VARIABLES) ) {
						if ( local ) {
							return Constants.GET_VARIABLES_KEY;
						} else {
							las_url = las_url + Constants.GET_VARIABLES + "?" + request.getQueryString();
						}
					} else if ( url.contains(Constants.GET_VARIABLE) ) {
						if ( local ) {
							return Constants.GET_VARIABLE_KEY;
						} else {
							las_url = las_url + Constants.GET_VARIABLE + "?" + request.getQueryString();
						}
					} else if ( url.contains(Constants.GET_VIEWS) ) {
						if ( local ) {
							return Constants.GET_VIEWS_KEY;
						} else {
							las_url = las_url + Constants.GET_VIEWS + "?" + request.getQueryString();
						}
					}
					if ( !local ) {
						lasProxy.executeGetMethodAndStreamResult(UriUtils.sanitizeUri(las_url), response);
					}					
				}
			} catch (HttpException e) {
				logerror(request, "Unable to do process request.", e);
			} catch (IOException e) {
				logerror(request, "Unable to do process request.", e);
			} catch (JDOMException e) {
				logerror(request, "Unable to do process request.", e);				
			} catch (LASException e) {
				logerror(request, "Unable to do process request.", e);
			} catch (JSONException e) {
				logerror(request, "Unable to do process request.", e);
			}
			return null;
		}
		private String findFirstVariable(Category category, LASConfig lasConfig, HttpServletRequest request) throws JDOMException, LASException, UnsupportedEncodingException, HttpException, IOException {
			String dsid = null;		
			if ( category.hasVariableChildrenAttribute() ) {
				if ( category.getDataset() != null && category.getDataset().getID() != null ) {
					return category.getDataset().getID();
				}
				return category.getChildren_DatasetID();
			} else {
				ArrayList<Category> cats = getCategories(category.getID(), lasConfig, request, true);
				for (Iterator catIt = cats.iterator(); catIt.hasNext();) {
					Category cat = (Category) catIt.next();
					dsid = findFirstVariable(cat, lasConfig, request);
				}
			}
			return dsid;
		}
		public static ArrayList<Category> getCategories(String id, LASConfig lasConfig, HttpServletRequest request, boolean include_dataset) 
		    throws UnsupportedEncodingException, HttpException, JDOMException, LASException, IOException {
			ArrayList<Category> cats = new ArrayList<Category>();
			String server_key = id.split(Constants.NAME_SPACE_SPARATOR)[0];
			if ( server_key.equals(lasConfig.getBaseServerURLKey()) ) {
				cats = lasConfig.getCategories(id);	
				for (Iterator catsIt = cats.iterator(); catsIt.hasNext();) {
					Category category = (Category) catsIt.next();
					if ( category.getDataset() != null && !include_dataset) {
						category.setDataset(null);
					}
				}
			} else {
				Tributary trib = lasConfig.getTributary(server_key);
				String las_url = trib.getURL() + Constants.GET_CATEGORIES;					

				if ( !id.endsWith(Constants.NAME_SPACE_SPARATOR+JDOMUtils.MD5Encode(trib.getName())) ) {
					// If this is a request of the top level category for this server
					// the id must be left off the request.  In this case it's not so
					// add it on.
					las_url = las_url + "?catid="+id+"&format=xml";
				}
				String xml = lasProxy.executeGetMethodAndReturnResult(las_url);
				LASDocument catD = new LASDocument();
				JDOMUtils.XML2JDOM(xml, catD);
				Element catsE = catD.getRootElement();
				List<Element> catsList = catsE.getChildren("category");
				for (Iterator catsListIt = catsList.iterator(); catsListIt.hasNext();) {
					Element c = (Element) catsListIt.next();
					Category cat = new Category(c);
					// If the remote LAS has no categories defined, then the category that comes back
					// has no attributes, so we add them here to match the data set inside the category.
					if ( cat.getID() == null ) {
						cat.setID(cat.getChildren_DatasetID());
					}
					if ( cat.getName() == null ) {
						cat.setName(cat.getDataset().getName());
					}
					// The UI does not expect the data set to be contained in this response and does the wrong thing
					// when it is included.
					if ( !include_dataset && cat.getDataset() != null ) {
						cat.setAttribute("children_dsid", cat.getDataset().getID());
						cat.setDataset(null);
					}
					cats.add(cat);
				}
			}
			return cats;
		}
		private String processRequest(HttpServletRequest request, HttpServletResponse response, LASConfig lasConfig, String las_url, String server_key, String openid) throws JDOMException, HttpException, IOException, LASException {
			String requestXML = request.getParameter("xml");
			LASUIRequest lasRequest = new LASUIRequest();
			if ( requestXML != null ) {
				try {
	        		String temp = JDOMUtils.decode(requestXML, "UTF-8");
	        		requestXML = temp;
	        	} catch (UnsupportedEncodingException e) {
	        		LASAction.logerror(request, "Error decoding the XML request query string.", e);
	        		return ERROR;
	        	}

	        	try {
	        		JDOMUtils.XML2JDOM(requestXML, lasRequest);
	        		// Set the lasRequest object in the HttpServletRequest so the product server does not have to rebuild it.
	        		request.setAttribute("las_request", lasRequest);
	        	} catch (Exception e) {
	        		LASAction.logerror(request, "Error parsing the request XML. ", e);
	        		return ERROR;
	        	}
	        }
			LASUIRequest originalRequest = (LASUIRequest) lasRequest.clone();
			String op = lasRequest.getOperation();
			if ( op.contains(Constants.GE_OP_ID) || op.equals(Constants.DOWNLOAD_OP_ID) || op.contains(Constants.ANIMATION_OP_ID) ) {
		    	// These operations have their own interface and can run off of the data node at the user is authenticated...
				las_url = las_url+Constants.PRODUCT_SERVER+"?xml="+lasRequest.toEncodedURLString();
				if ( openid != null && !openid.equals("") ) {
					las_url = las_url + "&openid=" + openid;
				}
		    	response.sendRedirect(las_url);
		    	return null;
		    }
			String template = lasConfig.getTemplate(op);
		    lasRequest.setProperty("las", "output_type", "xml");
		    // This operation depends on getting a GRID into the template context. 
		    // Normally it comes from the config object, but in this case it has to come
		    // over the wire from the remote location.
		    
//            if ( op.equals(Constants.DOWNLOAD_OP_ID) ) {
//            	String dsid = lasRequest.getDatasetIDs().get(0);
//            	String varid = lasRequest.getVariableIDs().get(0);
//            	String grid_url = las_url+Constants.GET_GRID+"?dsid="+dsid+"&varid="+varid;
//            	String grid_JSON = lasProxy.executeGetMethodAndReturnResult(grid_url);
//            	request.setAttribute("grid_JSON", grid_JSON);
//            }
		    las_url = las_url+Constants.PRODUCT_SERVER+"?xml="+lasRequest.toEncodedURLString();
		    for (Enumeration params = request.getParameterNames(); params.hasMoreElements() ;) {
				String param = (String) params.nextElement();
				String value = (String) request.getParameter(param);
				if ( !param.equals("xml") ) {
					las_url = las_url + "&" + param + "=" + value;
				}
			}
		    
			String xml_response = lasProxy.executeGetMethodAndReturnResult(las_url).trim();
			LASBackendResponse resDoc = new LASBackendResponse();
			JDOMUtils.XML2JDOM(xml_response, resDoc);
			if ( resDoc.getResultByType("batch") != null ) {
			    request.setAttribute("las_response", resDoc);
			    return BATCH;
			}
			List<Result> results = resDoc.getResults();
			LASBackendResponse confluence_response = new LASBackendResponse();
			for (Iterator resultsIt = results.iterator(); resultsIt.hasNext();) {
				Result result = (Result) resultsIt.next();
				String result_file = result.getFile();
				String result_url = result.getURL();
				File file = new File(result_file);
				String filename = lasConfig.getOutputDir()+File.separator+server_key+"_"+file.getName();
				if ( filename.contains("../") || filename.contains("/..") ) {
					throw new IOException("Illegal file name.");
				}
				File outfile = new File(filename);
				Element cr = new Element("result");
				// Copy the attributes.
				Map<String, String> attrs = result.getAttributesAsMap();
				for (Iterator atIt = attrs.keySet().iterator(); atIt.hasNext();) {
					String attr = (String) atIt.next();
					String attrvalue = attrs.get(attr);
					cr.setAttribute(attr, attrvalue);
				}
				// replace file and url.
				cr.setAttribute("file", filename);
				cr.setAttribute("url", lasConfig.getBaseServerURL()+"/output/"+outfile.getName());
				confluence_response.addResult(cr);
				if ( !outfile.exists() ) {
					lasProxy.executeGetMethodAndSaveResult(result_url, outfile, response);				
				}
				if (result.getType().equals("map_scale") ) {
					LASMapScale mapscale = new LASMapScale();
					JDOMUtils.XML2JDOM(outfile, mapscale);
					request.setAttribute("las_map_scale", mapscale);						
				} else if ( result.getType().equals("index") ) {
					LASRegionIndex rindex = new LASRegionIndex();
					JDOMUtils.XML2JDOM(outfile, rindex);
					request.setAttribute("las_region_index", rindex);
				} else if ( result.getType().equals("webrowset") ) {
					throw new LASException("Confluence server cannot handle webrowset data.");
				}
			}
			request.setAttribute("las_response", confluence_response);
			request.setAttribute("las_request", originalRequest);
			request.setAttribute("las_config", lasConfig);
			return "/productserver/templates/"+template+".vm";
		}
}