package gov.noaa.pmel.tmap.las.service;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.jdom.LASBackendConfig;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.noaa.pmel.tmap.las.util.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.view.XMLToolboxManager;

public class TemplateTool extends Tool {
    
    public VelocityEngine ve = new VelocityEngine();
    Map toolboxContext;
    private File configFile;
    private final static Logger log = LoggerFactory.getLogger(TemplateTool.class.getName());
    
    public TemplateTool() {    
    }
    
    public TemplateTool(String serviceName, String configFileName) throws LASException, IOException {
        Properties p = new Properties();
        InputStream is;
        String resourcePath = "resources/"+serviceName;
        is = this.getClass().getClassLoader().getResourceAsStream(resourcePath+"/velocity.properties");
        String template = getResourcePath(resourcePath+"/templates");
        if (is == null) {
            if ( template != null ) {
                // Can't find properties file.  Set where we look for templates.
                log.debug("Setting template path to default: "+template);
                p.setProperty("file.resource.loader.path", template);
            } else {
                throw new LASException("Cannot find "+serviceName+" templates directory.");
            }
        } else {
           p.load(is);
        }  
        if ( p.getProperty("file.resource.loader.path") == null ) {
            if ( template != null ) {
                // Can't find properties file.  Set where we look for templates.
                log.debug("Template path not found in properties file.  Setting to default: "+template);
                p.setProperty("file.resource.loader.path", template);
            } else {
                throw new LASException("Cannot find database templates directory.");
            }
        }
        if ( p.getProperty("runtime.log") == null ) {
        	log.debug("Setting runtime velocity log to /dev/null.");
        	p.setProperty("runtime.log", "/dev/null");
        }
        try {
            log.debug("Setting log to /dev/null and loader path to: "+p.getProperty("file.resource.loader.path"));
            ve.init(p);
        } catch (Exception e) {
            throw new LASException("Cannot initialize the velocity engine.");
        }
        
        String configPath = getResourcePath(resourcePath+"/"+configFileName);
        if ( configPath != null ) {
            configFile = new File(configPath);                               
        } else {
            throw new LASException("Config file "+ configPath +" not found.");
        }
        InputStream tbis = this.getClass().getClassLoader().getResourceAsStream("resources/services/toolbox.xml");
        XMLToolboxManager toolboxManager = new XMLToolboxManager();
        try {         
            if ( tbis == null ) {
                String toolboxPath = getResourcePath("resources/services/toolbox.xml");
                tbis = new FileInputStream(toolboxPath);
                throw new LASException("The Velocity toolbox configuration toolbox.xml not found. ");
            }
            toolboxManager.load(tbis);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new LASException("The Velocity toolbox configuration toolbox.xml not found. "+e.toString());
        } finally {
        	if ( tbis != null)
        	    tbis.close();
        }
        toolboxContext = toolboxManager.getToolbox(null);
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public Map getToolboxContext() {
        return toolboxContext;
    }

    public void setToolboxContext(Map toolboxContext) {
        this.toolboxContext = toolboxContext;
    }
    public String findMessage(String stderr, LASBackendConfig config) throws IOException {
        ArrayList<Message> messages = config.getMessages();
        if ( messages == null || messages.size() == 0 ) {
            return stderr;
        } else {
            // Loop through each line of the stderr output and see if one of the keys matches.
            BufferedReader stderr_reader = new BufferedReader(new StringReader(stderr));
            String line = stderr_reader.readLine().trim();
            while ( line != null ) {    
                for (Iterator messIt = messages.iterator(); messIt.hasNext();) {
                    Message message = (Message) messIt.next();
                    if ( message.getType().equals("startsWith")) {
                        if ( line.startsWith(message.getKey())) {
                            String text = message.getText();
                            if ( text != null && text.length() > 0 ) {
                                return text;
                            }
                        }
                    } else if ( message.getType().equals("contains") ) {
                        if ( line.contains(message.getKey())) {
                            String text = message.getText();
                            if ( text != null && text.length() > 0 ) {
                                return text;
                            }
                        }
                    } else if ( message.getType().equals("bracket") ) {
                        String key = message.getKey();
                        if ( line.contains(key)) {
                            return stderr.substring(stderr.indexOf(key)+key.length(), stderr.lastIndexOf(key));
                        }
                    }
                }
                line = stderr_reader.readLine().trim();
            }
        }
        return stderr;
    }
    public String readMergedTemplate(File file) throws IOException {
    	BufferedReader reader = null;
    	FileReader f = new FileReader(file);
    	reader = new BufferedReader(f);

    	StringBuffer contents = new StringBuffer("");
    	if (reader != null) {

    		String line = reader.readLine();
    		while ( line != null ) {
    			contents = contents.append(line.trim()+" ");
    			if ( line.endsWith(";")) {
    				break;
    			}
    			line = reader.readLine();
    		}

    	}
    	return contents.toString();
    }
    public String readMergedDatabaseTemplate(File file) throws IOException {
    	BufferedReader reader = null;
    	FileReader f = new FileReader(file);
    	reader = new BufferedReader(f);

    	StringBuffer contents = new StringBuffer("");
    	if (reader != null) {

    		String line = reader.readLine();
    		while ( line != null ) {
    			contents = contents.append(line.trim()+" ");
    			line = reader.readLine();
    		}

    	}
    	return contents.toString();
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
        
        HashMap<String, String> symbols = lasBackendRequest.getFerretSymbols();
        
        context.put("symbols", symbols);   
        context.put("las_backendrequest", lasBackendRequest);
        // Guaranteed to be set by the Product Server
        log.debug("Velocity resource path: "+ve.getProperty("file.resource.loader.path"));
        ve.mergeTemplate(template,"ISO-8859-1", context, templateWriter);
        templateWriter.flush();
        templateWriter.close();
        
    }
}
