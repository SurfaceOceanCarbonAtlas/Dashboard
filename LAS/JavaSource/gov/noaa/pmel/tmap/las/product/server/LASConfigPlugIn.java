package gov.noaa.pmel.tmap.las.product.server;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.noaa.pmel.tmap.las.test.LASTestOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LASConfigPlugIn {

	private static boolean reinit_flag = false;

	/* These are Java objects that contain these configuration pieces. */
	/**  Servlet context key server config is stored under */
	public final static String LAS_CONFIG_KEY = "las_config";

	/**  Servlet context key server config is stored under */
	public final static String SERVER_CONFIG_KEY = "server_config";

	/**  Servlet context key server config is stored under */
	public final static String CACHE_KEY = "cache";

	/* 
	 * These are the files names from which these configuration objects are derived. 
	 * The file names are used by the reinit method.
	 */
	public final static String LAS_TIMER_KEY = "las_timer_key";
	public final static String LAS_CONFIG_FILENAME_KEY = "las_config_filename";
	public final static String LAS_SERVER_CONFIG_FILENAME_KEY = "server_config_filename";
	public final static String LAS_OPERATIONS_CONFIG_FILENAME_KEY = "operations_config_filename";
	public final static String LAS_UI_CONFIG_FILENAME_KEY = "ui_config_filename";
	public final static String LAS_LAZY_START_KEY = "lazy_start";
	public final static String LAS_LAZY_START_RUNNING_KEY = "lazy_start_running";
	public final static String LAS_LOCK_KEY = "lock";
	public final static String LAS_SISTERS_CONFIG_FILENAME_KEY = "las_servers_filename";
	public final static String LAS_SISTERS_STATIC_CONFIG_FILENAME_KEY = "las_servers_static_filename";
	public final static String LAS_VERSION_KEY = "las_version";
	public static final String LAS_CONFIG_NOTFOUND_MESSAGE = "Server failed to get the LAS configuration. "
			+ "It is likely that LAS was not configured and thus there is no tomcat6/content/LAS_WEBAPP_NAME/conf/server/las.xml file for the LAS WebApp. "
			+ "Administrators can check for this problem by reading the log file, probably in tomcat6/content/baker/logs/las.log";
	
	public static final String TEST_SCHEDULER = "test_schduler";
	public static final String REAP_SCHEDULER = "read_scheduler";
	
	/*
	 * This is the key where we will store a boolean with the results of an F-TDS test.
	 */
	public final static String LAS_FTDS_UP_KEY = "ftds_up";

	private static Logger log = LoggerFactory.getLogger(LASConfigPlugIn.class.getName());

	private ServletContext context;

	/** File name from the plug-in config for the productserver.xml file **/
	private String serverConfigFileName=null;

	public String getServerConfigFileName() {
		return serverConfigFileName;
	}

	public void setServerConfigFileName(String serverConfigFileName) {
		this.serverConfigFileName = serverConfigFileName;
	}

	/** File name from plug-in config **/
	private String configFileName=null;

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}
	/*
	 *  File name from the plug-in config
	 *  for the v7 operations definitions
	 */
	private String v7OperationsFileName;

	public String getV7OperationsFileName() {
		return v7OperationsFileName;
	}
	public void setV7OperationsFileName(String v7OperationsFileName) {
		this.v7OperationsFileName = v7OperationsFileName;
	}

	/*
	 *  File name from the plug-in config
	 *  for the v7 operations definitions
	 */
	private String lasUIFileName;

	public String getLasUIFileName() {
		return lasUIFileName;
	}
	public void setLasUIFileName(String lasUIFileName) {
		this.lasUIFileName = lasUIFileName;
	}

    /*
     * File name for the las_servers.xml file
     */
	private String lasServersFileName;
	public String getLasServersFileName() {
		return lasServersFileName;
	}
	public void setLasServersFileName(String lasServersFileName) {
		this.lasServersFileName = lasServersFileName;
	}
	private String lasServersStaticFileName;
	public String getLasServersStaticFileName() {
	    return lasServersStaticFileName;
	}
	public void setLasServersStaticFileName(String lasServersStaticFileName) {
	    this.lasServersStaticFileName = lasServersStaticFileName;
	}

	/**
	 *  Initialize the LASConfig
	 *
	 *@param  servlet               The ActionServlet for this web application
	 *@param  config                The ModuleConfig for our owning module
	 *@exception  ServletException  if we cannot configure ourselves correctly
	 * @throws IOException 
	 */
	public void init(ServletContext context)
	throws ServletException {

		this.context = context;

		if ((configFileName == null || configFileName.length() == 0)) {
			throw new ServletException("No LAS configuration file specified.");
		} else {
			// Store in the servlet context for use by reinit method
			context.setAttribute(LAS_CONFIG_FILENAME_KEY, configFileName);
		}
		
		if ( lasServersFileName != null && lasServersFileName.length() > 0 ) {
			context.setAttribute(LAS_SISTERS_CONFIG_FILENAME_KEY, lasServersFileName);
		}
		
		if ( lasServersStaticFileName != null && lasServersStaticFileName.length() > 0 ) {
            context.setAttribute(LAS_SISTERS_STATIC_CONFIG_FILENAME_KEY, lasServersStaticFileName);
        }

		/* Set up the serverConfig for this server. */

		if ((serverConfigFileName == null || serverConfigFileName.length() == 0)) {
			throw new ServletException("No server configuration file specified.");
		} else {
			// Store in the servlet context for use by the reinit method
			context.setAttribute(LAS_SERVER_CONFIG_FILENAME_KEY, serverConfigFileName);
		}

		if (v7OperationsFileName == null || v7OperationsFileName.length() == 0) {
			throw new ServletException("No v7 operations file specified.");
		} else {
			// Store in the servlet context for use by the reinit method
			context.setAttribute(LAS_OPERATIONS_CONFIG_FILENAME_KEY, v7OperationsFileName);
		}

		if (lasUIFileName == null || lasUIFileName.length() == 0) {
			throw new ServletException("No ui.xml file specified.");
		} else {
			// Store in the servlet context for use by the reinit method
			context.setAttribute(LAS_UI_CONFIG_FILENAME_KEY, lasUIFileName);
		}
		reinit_flag = false;
		try {
			go_init();
		} catch (JDOMException e) {
			log.error("Could not parse the las config file "+configFileName);
		} catch (UnsupportedEncodingException e) {
			log.error("Could not parse the las config file "+configFileName);
		} catch (LASException e) {
		    log.error("Could not parse the las config file "+configFileName + " " + e.getLocalizedMessage());
        } catch (IOException e) {
            log.error("Could not parse the las config file "+configFileName);
        }
		

	}
	
	public void reinit(ServletContext reinitContext) throws ServletException {
		context = reinitContext;
		configFileName = (String) reinitContext.getAttribute(LAS_CONFIG_FILENAME_KEY);
		serverConfigFileName = (String) reinitContext.getAttribute(LAS_SERVER_CONFIG_FILENAME_KEY);
		v7OperationsFileName = (String) reinitContext.getAttribute(LAS_OPERATIONS_CONFIG_FILENAME_KEY);
		lasUIFileName = (String) reinitContext.getAttribute(LAS_UI_CONFIG_FILENAME_KEY);
		lasServersFileName = (String) reinitContext.getAttribute(LAS_SISTERS_CONFIG_FILENAME_KEY);
	    lasServersStaticFileName = (String) reinitContext.getAttribute(LAS_SISTERS_STATIC_CONFIG_FILENAME_KEY);


		if ((configFileName == null || configFileName.length() == 0)) {
			throw new ServletException("No LAS configuration file specified.");
		}

		/* Set up the serverConfig for this server. */

		if ((serverConfigFileName == null || serverConfigFileName.length() == 0)) {
			throw new ServletException("No server configuration file specified.");
		}

		if (v7OperationsFileName == null || v7OperationsFileName.length() == 0) {
			throw new ServletException("No v7 operations file specified.");
		}

		if (lasUIFileName == null || lasUIFileName.length() == 0) {
			throw new ServletException("No ui.xml file specified.");
		}
		reinit_flag = true;
		try {
			go_init();
		} catch (JDOMException e) {
			log.error("Could not parse the las config file "+configFileName);
		} catch (UnsupportedEncodingException e) {
			log.error("Could not parse the las config file "+configFileName);
		} catch (LASException e) {
            log.error("Could not parse the las config file "+configFileName);
        } catch (IOException e) {
            log.error("Could not parse the las config file "+configFileName);
        }
	}
	public void go_init() throws JDOMException, LASException, IOException {

	    log.info("Starting initialization.");
	    // If this is not a reinit, lock the product server here!!
	    if ( !reinit_flag ) context.setAttribute(LAS_LOCK_KEY, "true");

	    File configFile = new File(configFileName);
	    LASConfig lasConfig = new LASConfig();

	    try {
	        JDOMUtils.XML2JDOM(configFile, lasConfig);
	    } catch (Exception e) {
	        log.error("Could not parse the las config file "+configFileName);
	    }
	    Map<String, Element> sisters = new HashMap<String, Element>();
	    if ( lasServersFileName != null && lasConfig.allowsSisters() ) {
	        File sistersFile = new File(lasServersFileName);
	        LASDocument sistersDoc = new LASDocument();
	        try {
	            JDOMUtils.XML2JDOM(sistersFile, sistersDoc);
	            Element las_servers = sistersDoc.getRootElement();


	            Namespace ns = Namespace.getNamespace("http://ferret.pmel.noaa.gov/las");
	            List servers = las_servers.getChildren("las_server", ns);
	            if ( servers.size() == 0 ) {
	                // If the ESG Registry built this file, it will be under this namespace.  :-)
	                ns = Namespace.getNamespace("http://www.esgf.org/registry");
	                servers = las_servers.getChildren("las_server", ns);
	            }
	            if ( servers.size() == 0 ) {
	                servers = las_servers.getChildren("las_server");
	            }
	            for (Iterator serversIt = servers.iterator(); serversIt.hasNext();) {
	                Element server = (Element) serversIt.next();
	                server.setNamespace(null);
	                String ip = server.getAttributeValue("ip");
	                if ( !sisters.containsKey(ip) ) {
	                    Element sis = (Element) server.clone();
	                    sisters.put(ip, sis);
	                }
	            }

	        } catch (Exception e) {
	            // We'll live without our sisters.
	            log.warn("Could not read the "+lasServersFileName+" file.");
	        }
	    }

	    if ( lasServersStaticFileName != null && lasConfig.allowsSisters() ) {
	        File sistersFile = new File(lasServersStaticFileName);
	        LASDocument sistersDoc = new LASDocument();
	        try {
	            JDOMUtils.XML2JDOM(sistersFile, sistersDoc);
	            Element las_servers = sistersDoc.getRootElement();

	            Namespace ns = Namespace.getNamespace("http://ferret.pmel.noaa.gov/las");
	            List servers = las_servers.getChildren("las_server", ns);
	            if ( servers.size() == 0 ) {
	                // If the ESG Registry built this file, it will be under this namespace.  :-)
	                ns = Namespace.getNamespace("http://www.esgf.org/registry");
	                servers = las_servers.getChildren("las_server", ns);
	            }
	            if ( servers.size() == 0 ) {
	                servers = las_servers.getChildren("las_server");
	            }
	            for (Iterator serversIt = servers.iterator(); serversIt.hasNext();) {
	                Element server = (Element) serversIt.next();
	                server.setNamespace(null);
	                String ip = server.getAttributeValue("ip");
	                if ( !sisters.containsKey(ip) ) {
	                    Element sis = (Element) server.clone();
	                    sisters.put(ip, sis);
	                }
	            }

	        } catch (Exception e) {
	            // We'll live without our sisters.
	            log.warn("Could not read the "+lasServersStaticFileName+" file.");
	        }
	    }
	    Element las_serversE = lasConfig.getRootElement().getChild("las_servers");
	    if ( las_serversE == null ) {
	        las_serversE = new Element("las_servers");
	        lasConfig.getRootElement().addContent(las_serversE);
	    }
	    for (Iterator sisIt = sisters.keySet().iterator(); sisIt.hasNext();) {
	        String ip = (String) sisIt.next();
	        Element server = sisters.get(ip);
	        las_serversE.addContent(server);
	    }	    



	    File serverConfigFile = new File(serverConfigFileName);
	    ServerConfig serverConfig = new ServerConfig();

	    try {
	        JDOMUtils.XML2JDOM(serverConfigFile, serverConfig);
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }

	    context.setAttribute(SERVER_CONFIG_KEY, serverConfig);
	    Cache cache = (Cache) context.getAttribute(CACHE_KEY);
	    Element root = lasConfig.getRootElement();
	    String lazy_start = null;
	    // The cache is emptied and reinitialized in a separate task so it gets created in the init and left alone in a reinit.
	    if (!reinit_flag) {
	        // Create the Cache
	        cache = new Cache(serverConfig.getCacheSize(), serverConfig.getCacheMaxBytes());

	        // Read the Cache if it exists
	        File cacheFile = serverConfig.getCacheFile();
	        if ( cacheFile != null) {
	            try {
	                cache.loadCacheFromStore(cacheFile);
	            } catch (Exception e) {
	                log.warn("Cache file not loaded: "+e.toString());
	            }
	        }

	        lazy_start = root.getAttributeValue("lazy_start");

	        // Store the cache in the context.
	        context.setAttribute(CACHE_KEY, cache);
	    }

	    String version = root.getAttributeValue("version");
	    long next = 999999999999999999l;
	    if ( lazy_start != null && lazy_start.equals("true") ) {
	        context.setAttribute(LAS_LAZY_START_KEY, "true");
	    } else {

	        boolean seven = false;
	        if ( version != null && version.contains("7.")) {
	            seven = true;
	        }

	        File v7OperationsFile = new File(v7OperationsFileName);
	        LASDocument v7operationsDoc = new LASDocument();
	        try {
	            JDOMUtils.XML2JDOM(v7OperationsFile, v7operationsDoc);
	        } catch (Exception e) {
	            log.error("Could not parse the v7 operations file "+v7OperationsFileName, e);
	        }

	        List v7operations = v7operationsDoc.getRootElement().getChildren("operation");
	        Element operations = lasConfig.getRootElement().getChild("operations");
	        for (Iterator opIt = v7operations.iterator(); opIt.hasNext();) {
	            Element op = (Element) opIt.next();
	            operations.addContent((Element)op.clone());
	        }


	        if ( lasConfig.getOutputDir() == null ) {
	            lasConfig.setOutputDir(context.getRealPath("/")+"output");
	        }

	        // Create XML stubs from THREDDS or netCDF input.


	        try {
	            next = lasConfig.addXML(reinit_flag, cache);
	        } catch (UnsupportedEncodingException e) {
	            log.error("Could not add the referencd dataset by addXML.", e);
	        } catch (IOException e) {
	            log.error("Could not add the referencd dataset by addXML.", e);
	        } catch (JDOMException e) {
	            log.error("Could not add the referencd dataset by addXML.", e);
	        }

	        log.debug("Starting convert to 7");
	        if ( !seven ) {
	            lasConfig.convertToSeven();
	        }
	        log.debug("Finished convert to 7");



	        log.debug("Starting merge properties.");

	        lasConfig.mergeProperites();

	        log.debug("Finished merge properties.");


	        log.debug("Starting add intervals");
	        try {
	            lasConfig.addIntervalsAndPoints();        
	        } catch (Exception e) {
	            log.error("Could not add the intervals and points attributes to variables in this LAS configuration.", e);
	        }
	        log.debug("Finished add intervals");

	        log.debug("Starting add grid type");
	        try {
	            lasConfig.addGridType();
	        } catch (Exception e) {
	            log.error("Could not add the grid_type to variables in this LAS configuration.", e);
	        }
	        log.debug("Finished add grid type");

	        String fds_base = serverConfig.getFTDSBase();
	        String fds_dir = serverConfig.getFTDSDir();
	        try {
	            log.debug("Adding F-TDS attributes to data set.");
	            log.debug("base url: "+fds_base+" local directory "+fds_dir);
	            lasConfig.addFDS(fds_base, fds_dir);
	        } catch (LASException e) {
	            log.error("Could not add F-TDS URLs to data configuration. "+e.toString());
	        } catch (JDOMException e) {
	            log.error("Could not add F-TDS URLs to data configuration. "+e.toString());
	        } catch (IOException e) {
	            log.error("Could not add F-TDS URLs to data configuration. "+e.toString());
	        }

	        log.debug("Staring add ui");
	        File lasUIFile = new File(lasUIFileName);
	        LASDocument lasUIDoc = new LASDocument();
	        try {
	            JDOMUtils.XML2JDOM(lasUIFile, lasUIDoc);
	        } catch (Exception e) {
	            log.error("Could not parse the ui.xml file "+lasUIFileName, e);
	        }

	        String title = lasUIDoc.getRootElement().getAttributeValue("title");
	        List uis = lasUIDoc.getRootElement().getChildren("ui");

	        Element ui = new Element("lasui");
	        if (title != null && !title.equals("")) {
	            ui.setAttribute("title", title);
	        }
	        for (Iterator uiIt = uis.iterator(); uiIt.hasNext();) {
	            Element uiE = (Element) uiIt.next();
	            ui.addContent((Element)uiE.clone());
	        }

	        List options = lasUIDoc.getRootElement().getChildren("options");

	        for (Iterator optionsIt = options.iterator(); optionsIt.hasNext();) {
	            Element optionsElement = (Element) optionsIt.next();
	            List optionsDefElements = optionsElement.getChildren("optiondef");
	            for (Iterator optionsDefElementsIt = optionsDefElements.iterator(); optionsDefElementsIt.hasNext();) {
	                Element optionsDef = (Element) optionsDefElementsIt.next();
	                String od_name = optionsDef.getAttributeValue("name");
	                List optionElements = optionsDef.getChildren("option");
	                for (Iterator optionsElementsIt = optionElements.iterator(); optionsElementsIt.hasNext();) {
	                    String id = "id_"+Double.toString(Math.random());
	                    Element option = (Element) optionsElementsIt.next();
	                    Element textfield = option.getChild("textfield");
	                    String name = "";
	                    if (textfield != null) {
	                        name = textfield.getAttributeValue("name");
	                    }
	                    Element menu = option.getChild("menu");
	                    if ( menu != null ) {
	                        name = menu.getAttributeValue("name");
	                    }
	                    if (!od_name.equals(name)) {
	                        id = od_name+"_"+name.replaceAll(" ", "_");
	                    } else {
	                        id = od_name;
	                    }
	                    option.setAttribute("ID", id);
	                }
	            }
	            ui.addContent((Element)optionsElement.clone());
	        }

	        List defaults = lasUIDoc.getRootElement().getChildren("defaults");
	        for (Iterator defit = defaults.iterator(); defit.hasNext();) {
	            Element defaultE = (Element) defit.next();
	            ui.addContent((Element)defaultE.clone());
	        }

	        List maps = lasUIDoc.getRootElement().getChildren("maps");
	        for (Iterator mapIt = maps.iterator(); mapIt.hasNext();) {
	            Element mapsE = (Element) mapIt.next();
	            ui.addContent((Element)mapsE.clone());
	        }

	        List menus = lasUIDoc.getRootElement().getChildren("menus");
	        for (Iterator menuIt = menus.iterator(); menuIt.hasNext();) {
	            Element menusE = (Element) menuIt.next();
	            ui.addContent((Element)menusE.clone());
	        }

	        lasConfig.getRootElement().addContent(ui);

	        log.debug("Finished all ui");

	        log.debug("Starting add server ID");
	        // Finally add the server id to the options if applicable.
	        lasConfig.addServerID();
	        log.debug("Finished add server ID");

	        File v7 = new File(lasConfig.getOutputDir()+"/lasV7.xml");
	        try {
	            lasConfig.write(v7);
	        } catch (Exception e) {
	            log.error("Cannot write out new Version 7.0 las.xml file.", e);
	        }

	        synchronized (context) {
	        	context.setAttribute(LAS_CONFIG_KEY, lasConfig);
			}
	          
	    }

	    // Set it to false so it gets tested at least the first time...
	    context.setAttribute(LAS_FTDS_UP_KEY, "false");

	    // Unlock the product server and start accepting new requests...
	    context.setAttribute(LAS_LOCK_KEY, "false");

	    if ( !reinit_flag ) {
	        
	        
	        
	        if ( lasConfig != null ) {
	            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	            LASTestOptions lto = lasConfig.getTestOptions();
	            if ( lto != null ) {

	                long d = lto.getDelay();
	                if ( d < 0 ) d = 0;
	                long p = lto.getPeriod();
	                // Not more than twice a day.  
	                if ( p < 43200000 ) p = 43200000;
	                scheduler.scheduleAtFixedRate(new TestTask(context), d, p, TimeUnit.MICROSECONDS);
	                context.setAttribute(TEST_SCHEDULER, scheduler);
	            }
	        }
	        if ( lasConfig != null ) {
	            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	            // Default to once a day...
	            String interval_string = "24";
	            String age_string = "168";
	            String ivunits = "hours";
	            String time = "00:01";
	            TimeUnit timeunit = TimeUnit.HOURS;

	            // How often to reap in either hours or days 
	            interval_string = lasConfig.getGlobalPropertyValue("product_server", "clean_interval");
	            // Units used for the interval and the age
	            ivunits = lasConfig.getGlobalPropertyValue("product_server", "clean_units");
	            // Age expressed in chosen units at which a file is removed (defaults to 7 days)
	            age_string = lasConfig.getGlobalPropertyValue("product_server", "clean_age");
	            // Time of day at which to start the cleaner running.  Defaults to start immediately.
	            time = lasConfig.getGlobalPropertyValue("product_server", "clean_time");

	            if ( interval_string.equals("") ) {
	                interval_string = "24";
	            }
	            if ( age_string.equals("") ) {
	                age_string = "168";
	            }
	            if ( ivunits.equals("") ) {
	                ivunits = "hours";
	            }
	            if ( time.equals("") ) {
	                time = "00:01";
	            }

	            DateTime now = new DateTime();
	            long interval = 1000*60*60*24;    
	            long age = interval*7; // Older than a week old
	            try {
	                interval = Long.valueOf(interval_string);
	                age = Long.valueOf(age_string);
	                if ( ivunits.toLowerCase(Locale.ENGLISH).contains("hour") ) {
	                    interval = interval * 1000*60*60;
	                    age = age * 1000*60*60;
	                    // timeunit defaults to hours
	                } else if (ivunits.toLowerCase(Locale.ENGLISH).contains("day") ) {
	                    interval = interval * 1000*60*60*24;         
	                    age = age * 1000*60*60*24;
	                    timeunit = TimeUnit.DAYS;
	                }
	            } catch (Exception e) {
	                interval = 1000*60*60*24;
	                age = interval*7;
	            }

	            DateTimeFormatter ymd = DateTimeFormat.forPattern("yyyy-MM-dd");
	            DateTimeFormatter ymdhm = DateTimeFormat.forPattern("yyyy-MM-dd HH:ss");
	            String today_string = ymd.print(now);
	            today_string = today_string + " " + time;
	            long delay = 0;
	            DateTime startToday = ymdhm.parseDateTime(today_string);
	            DateTime startTomorrow = startToday.plusHours(24);
	            if ( now.isAfter(startToday) ) {
	                delay = startTomorrow.getMillis() - now.getMillis();
	            } else {
	                delay = startToday.getMillis() - now.getMillis();
	            }
	            if ( delay < 0 ) {
	                delay = 0;
	            }

	            scheduler.scheduleAtFixedRate(new ReaperTask(context, age), delay, interval, timeunit);
	            context.setAttribute(REAP_SCHEDULER, scheduler);
	        }
	        

	    }
	    log.info("Initialization finished.");
	}


	public void update(ServletContext context) throws ServletException, JDOMException, UnsupportedEncodingException, LASException {
		this.context = context;
		configFileName = (String) context.getAttribute(LAS_CONFIG_FILENAME_KEY);

		if ((configFileName == null || configFileName.length() == 0)) {
			throw new ServletException("No LAS configuration file specified.");
		}

		File configFile = new File(configFileName);
		LASConfig updatedLASConfig = new LASConfig();

		try {
			JDOMUtils.XML2JDOM(configFile, updatedLASConfig);
		} catch (Exception e) {
			log.error("Could not parse the las config file "+configFileName);
		}
		Element root = updatedLASConfig.getRootElement();
		String version = root.getAttributeValue("version");
		boolean seven = false;
		if ( version != null && version.contains("7.")) {
			seven = true;
		}
		if ( !seven ) {
			updatedLASConfig.convertToSeven();
		}

		updatedLASConfig.mergeProperites();

		try {
			updatedLASConfig.addIntervalsAndPoints();        
		} catch (Exception e) {
			log.error("Could not add the intervals and points attributes to variables in this LAS configuration.", e);
		}

		try {
			updatedLASConfig.addGridType();
		} catch (Exception e) {
			log.error("Could not add the grid_type to variables in this LAS configuration.", e);
		}
		List datasets = updatedLASConfig.getRootElement().getChildren("datasets");
		List grids = updatedLASConfig.getRootElement().getChildren("grids");
		List axes = updatedLASConfig.getRootElement().getChildren("axes");

		LASConfig lasConfig = (LASConfig) context.getAttribute(LAS_CONFIG_KEY);
		lasConfig.getRootElement().removeChildren("datasets");
		lasConfig.getRootElement().removeChildren("axes");
		lasConfig.getRootElement().removeChildren("grids");

		for (Iterator dsIt = datasets.iterator(); dsIt.hasNext();) {
			Element datasetsE = (Element) dsIt.next();
			lasConfig.getRootElement().addContent((Element) datasetsE.clone());
		}

		for (Iterator gridsId = grids.iterator(); gridsId.hasNext();) {
			Element gridsE = (Element) gridsId.next();
			lasConfig.getRootElement().addContent((Element) gridsE.clone());
		}

		for (Iterator axesIt = axes.iterator(); axesIt.hasNext();) {
			Element axesE = (Element) axesIt.next();
			lasConfig.getRootElement().addContent((Element) axesE.clone());
		}
	}
}
