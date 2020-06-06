package gov.noaa.pmel.tmap.las.service.database;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.exception.LASRowLimitException;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.jdom.LASDatabaseBackendConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.noaa.pmel.tmap.las.service.TemplateTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.rowset.WebRowSet;

import oracle.jdbc.rowset.OracleWebRowSet;

import org.apache.velocity.VelocityContext;
import org.jdom.JDOMException;

import ucar.nc2.NetcdfFile;

import com.sun.rowset.WebRowSetImpl;

public class DatabaseTool extends TemplateTool {
    
    LASDatabaseBackendConfig databaseBackendConfig;
    
    private final static Logger log = LoggerFactory.getLogger(DatabaseTool.class.getName());
    int ti = 0;
    public DatabaseTool() throws LASException, IOException {
       
        super("database", "DatabaseBackendConfig.xml");
        
        databaseBackendConfig = new LASDatabaseBackendConfig();

        try {
            JDOMUtils.XML2JDOM(getConfigFile(), databaseBackendConfig);
        } catch (Exception e) {
            throw new LASException("Could not parse Database config file: " + e.toString());
        }
    }
    
    public LASBackendResponse run(LASBackendRequest lasBackendRequest) {
        LASBackendResponse lasBackendResponse = new LASBackendResponse();
        ti = 0;   
        if (lasBackendRequest.isCanceled()) {
            lasBackendResponse.setError("Request canceled", "Request canceled.");
            log.debug("Request cancelled:"+lasBackendRequest.toCompactString());
            return lasBackendResponse;
        }
        log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Reading database properties.");
        String db_name;
        try {
            db_name = lasBackendRequest.getDatabaseProperty("db_name");
        } catch (Exception e) {
            lasBackendResponse.setError("Could not get database name from backend request name: ", e);
            return lasBackendResponse;
        }
        log.debug("got db name: "+db_name); //debug
        if ( db_name == null || db_name.equals("")) {
            lasBackendResponse.setError("Database backend configuration error.", "Request does not contain a valid db_name property.");
            return lasBackendResponse;
        }
        log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Finished reading database properties.");
        ti++;
        log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Loading database driver.");
        String driver;
        try {
            driver = databaseBackendConfig.getDriver(db_name);
        } catch (Exception e) {
            lasBackendResponse.setError("Could not get database driver: ", e);
            return lasBackendResponse;
        }
        log.debug("Got driver class: "+driver); //debug
        if ( driver.equals("")) {
            lasBackendResponse.setError("Database backend configuration error.", "Database configuration does not have a database driver attribute.");
            return lasBackendResponse;
        }
        
        // The newInstance() call is a work around for some
        // broken Java implementations
        try {
            Class.forName(driver).newInstance();
        } catch (Exception e) {
            lasBackendResponse.setError("Could load database driver class: ", e);
            return lasBackendResponse;
        }
        log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Finished loading database driver.");
        ti++;
        log.debug("Got instance of driver."); //debug
        File sqlFile;
        log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Merging database template.");
        // If defined use the debug file name as the place to build the sql statements.
        String sqlFileName = lasBackendRequest.getResultAsFile("db_debug");
        if ( sqlFileName != "" ) {
            sqlFile = new File(sqlFileName);
        // If not, make one from scratch.
        } else {
            String template_dir = getResourcePath("resources/database/temp");
            
            if ( template_dir != null ) {
                sqlFile = new File(template_dir+"sql.sql");
            } else {
                lasBackendResponse.setError("Database backend service errror.", "Could not find SQL template directory.");
                return lasBackendResponse;
            }
        }
        log.debug("About to create sqlFile");
        if (lasBackendRequest.isCanceled()) {
            lasBackendResponse.setError("Request canceled.");
            log.debug("Request cancelled:"+lasBackendRequest.toCompactString());
            return lasBackendResponse;
        }
        try {
        	String sql = lasBackendRequest.getProperty("operation","service_action") + ".vm";
            mergeCommandTemplate(lasBackendRequest, sqlFile, sql);
        } catch (Exception e) {
            lasBackendResponse.setError("Could not create SQL command file: ", e);
            return lasBackendResponse;
        }
        log.debug("Created sqlFile"); //debug
        if (lasBackendRequest.isCanceled()) {
            lasBackendResponse.setError("Request canceled", "Request canceled.");
            log.debug("Request cancelled:"+lasBackendRequest.toCompactString());
            return lasBackendResponse;
        }
        log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Finished merging database template.");
        ti++;
        log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Reading database template.");
        log.debug("preparing to read sql file"); //debug
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        String statement = "";
        try {
        	statement = readMergedDatabaseTemplate(sqlFile);
        } catch (IOException e ) {
        	lasBackendResponse.setError("Unable to read line from SQL file ", e);
        	return lasBackendResponse;
        }
        log.debug("Statement ready: "+statement);
        if (lasBackendRequest.isCanceled()) {
            lasBackendResponse.setError("Request canceled", "Request canceled.");
            log.debug("Request cancelled:"+lasBackendRequest.toCompactString());
            return lasBackendResponse;
        }
        log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Finished reading database template.");
        ti++;
        
        String user;
        try {
            user = databaseBackendConfig.getUser(db_name);
        } catch (Exception e) {
            lasBackendResponse.setError("Could not get database user from database config: ", e);
            return lasBackendResponse;
        }
        String password;
        try {
            password = databaseBackendConfig.getPassword(db_name);
        } catch (Exception e) {
            lasBackendResponse.setError("Could not get database password from database config: ", e);
            return lasBackendResponse;
        }
        String connectionURL="";
        try {
            connectionURL = databaseBackendConfig.getConnectionURL(db_name);
        } catch (Exception e) {
            lasBackendResponse.setError("Could not get database connection URL from database config: ", e);
            return lasBackendResponse;
        }
        if ( !connectionURL.startsWith("jdbc:") ) {
            connectionURL = "jdbc:"+connectionURL;
        }
        log.debug("Connecting to datbase: "+connectionURL);
        
        try {
        	log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Getting database connection.");
            con =  DriverManager.getConnection(connectionURL, user, password);
            log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Finished getting database connection.");
            ti++;
            log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Creating database statement.");
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Finished creating database statement.");
            ti++;
            String fetch_size = lasBackendRequest.getDatabaseProperty("fetch_size");
            int f = 0;
            if ( fetch_size != null && !fetch_size.equals("") ) {
            	try {
					f = Integer.valueOf(fetch_size);
				} catch (RuntimeException e) {
					f = 0;
				}
            }
 	
            stmt.setFetchSize(f);
            
            log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Executing database query.");
       	    if (stmt.execute(statement)) {
	    	rset = stmt.getResultSet();
	    } else {
		int rows = stmt.getUpdateCount();
		log.debug("Executed "+statement+" which affected "+rows+" rows.");
	    }    
	
            log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Finished executing database query.");
            ti++;
        } catch (Exception e) {
            lasBackendResponse.setError("Database access failed: "+statement+" ",e);
            return lasBackendResponse;
        }
        if (lasBackendRequest.isCanceled()) {
            lasBackendResponse.setError("Request canceled", "Request canceled.");
            log.debug("Request cancelled:"+lasBackendRequest.toCompactString());
            return lasBackendResponse;
        }
        
        String netcdfFilename = "";
        netcdfFilename = lasBackendRequest.getResultAsFile("netcdf");
        log.debug("Got netcdf filename: "+ netcdfFilename); // debug
        if (lasBackendRequest.isCanceled()) {
            lasBackendResponse.setError("Request canceled","Request canceled.");
            log.debug("Request cancelled:"+lasBackendRequest.toCompactString());
            return lasBackendResponse;
        }
        
        if ( netcdfFilename != null && !netcdfFilename.equals("")) {
            try {
               makeNetCDF(netcdfFilename, rset, lasBackendRequest);
            } catch (Exception e) {
            	if ( e instanceof LASRowLimitException ) {
            		lasBackendResponse.setError("The data you asked for exceeds the allowed limits.   Please try again with a smaller request.", e);
            	} else {
                    lasBackendResponse.setError("Unable to make intermediate netCDF file.", e);
            	}
                return lasBackendResponse;
            }
            
            if (lasBackendRequest.isCanceled()) {
                lasBackendResponse.setError("Request canceled.");
                log.debug("Request cancelled:"+lasBackendRequest.toCompactString());
                return lasBackendResponse;
            }

        }
        
        String ncmlFilename = lasBackendRequest.getResultAsFile("ncml");
        log.debug("got ncml filename: "+ncmlFilename); // debug
        FileOutputStream os = null;
        if (ncmlFilename != null && !ncmlFilename.equals("") ) {
        	log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Making ncml file.");
            try {
                NetcdfFile ncfile = NetcdfFile.open(netcdfFilename);
                log.debug("opened ncml file"); // debug
                os = new FileOutputStream(new File(ncmlFilename));
                log.debug("made stream"); // deubg
                ncfile.writeNcML(os, ncmlFilename);
                log.debug("wrote ncml file");
            } catch (IOException e) {
                try {
                    makeNetCDF(netcdfFilename, rset, lasBackendRequest);
                } catch (Exception eml) {
                    lasBackendResponse.setError("Unable to create netCDF file to dump to NcML: ", eml);
                    return lasBackendResponse;
                }
            } finally {
            	if ( os != null ) {
            		try {
						os.close();
						
					} catch (IOException e) {
						log.error("Failed to close output stream.  "+e.getMessage());
					}
            	}
            }
            log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Finished making ncml file.");
            ti++;
        }
        
        
        
        String xmlFilename = lasBackendRequest.getResultAsFile("webrowset");
        log.debug("got xml filename: "+xmlFilename); // debug
        
        if ( xmlFilename != null && !xmlFilename.equals("")) {
        	log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Makeing webrowset file.");
            try {
                WebRowSet webrowset;
                if ( driver.contains("oracle") ) {
                   webrowset = new OracleWebRowSet();
                } else {
                   webrowset = new WebRowSetImpl();
                }
                File webrowsetFile = new File(xmlFilename);
                FileWriter fw = new FileWriter(webrowsetFile);
                if ( rset != null ) {
                	rset.beforeFirst();
                	webrowset.writeXml(rset, fw);             
                }
            } catch (Exception e) {
                lasBackendResponse.setError("Unable to create  XML file: ", e);
                return lasBackendResponse;
            } finally {
                try { if ( rset != null) rset.close(); } catch(Exception e) { }
                try { if ( stmt != null) stmt.close(); } catch(Exception e) { }
                try { if ( con != null) con.close(); } catch(Exception e) { }
            }
            log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Finished makeing webrowset file.");
            ti++;
        }
        log.debug("finished with xml file");
        try { if ( rset != null) rset.close(); } catch(Exception e) { }
        try { if ( stmt != null) stmt.close(); } catch(Exception e) { }
        try { if ( con != null) con.close(); } catch(Exception e) { }
        
        
        if (lasBackendRequest.isCanceled()) {
            lasBackendResponse.setError("Request canceled.");
            log.debug("Request cancelled:"+lasBackendRequest.toCompactString());
            return lasBackendResponse;
        }
        // The service just wrote the file to the requested location so
        // copy the response element from the request to the response.
        // 
        try {
            lasBackendResponse.addResponseFromRequest(lasBackendRequest);
        } catch (JDOMException e) {
            lasBackendResponse.setError("Failed to set response element: ", e);
            return lasBackendResponse;
        }
        return lasBackendResponse;
    }
    private void makeNetCDF(String netcdfFilename, ResultSet rset, LASBackendRequest lasBackendRequest) throws Exception {
        IntermediateNetcdfFile netcdf = null;
        
        try {
        	log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Constructing empty netCDF file.");
            netcdf = new IntermediateNetcdfFile(netcdfFilename, false);        
            log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Finished constructing empty netCDF file.");
            ti++;
        } catch (Exception e) {
            throw new Exception("Error creating empty netCDF file and checking for results: "+e.toString());
        }
        
        try {
        	log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Filling netCDF file.");
            netcdf.create(rset, lasBackendRequest);
            log.debug("Timing "+ti+" : "+lasBackendRequest.getResultAsFile("db_debug")+": Finished filling netCDF file.");
            ti++;
        } catch (Exception e) {
        	if ( e instanceof LASRowLimitException ) {
        		log.debug("Got a row limit exception: "+e.getMessage());
                throw e;
        	} else {
        		log.debug("Caught an exception while creating the netCDF file: "+e.getMessage());
                throw new Exception("Could not create intermediate netCDF file stub with result set: "+e.toString());
        	}
        } finally {
            if ( netcdf != null ) {
                netcdf.close();
            }
        }
        
        log.debug("created netcdf file"); //debug
        
        // Fill up the variables with the data.
        
    }
    protected void mergeCommandTemplate (LASBackendRequest lasBackendRequest, File jnlFile, String template) throws LASException, Exception {
        PrintWriter templateWriter = null;
        try {
            templateWriter = new PrintWriter(new FileOutputStream(jnlFile));
        }
        catch(Exception e) {
            throw new LASException(e.toString());
        }
        
        // Set up the Velocity Context
        VelocityContext context = new VelocityContext(getToolboxContext());
        
        // Take all the information passed to the backend and
        // make the giant symbol collection to be handed to Ferret.
        
        
//        if ( lasBackendRequest.getProperty("database", "name").toLowerCase(Locale.ENGLISH).contains("socat") ) {
//        	Socat socat = new Socat();
//        	context.put("socat", socat);
//        }
         
        context.put("las_backendrequest", lasBackendRequest);
        // Guaranteed to be set by the Product Server
        log.debug("Velocity resource path: "+ve.getProperty("file.resource.loader.path"));
        ve.mergeTemplate(template,"ISO-8859-1", context, templateWriter);
        templateWriter.flush();
        templateWriter.close();
        
    }
}
