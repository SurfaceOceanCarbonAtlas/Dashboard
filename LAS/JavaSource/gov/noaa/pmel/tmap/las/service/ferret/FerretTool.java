package gov.noaa.pmel.tmap.las.service.ferret;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.jdom.LASFerretBackendConfig;
import gov.noaa.pmel.tmap.las.jdom.LASMapScale;
import gov.noaa.pmel.tmap.las.jdom.LASRegionIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.noaa.pmel.tmap.las.service.RuntimeEnvironment;
import gov.noaa.pmel.tmap.las.service.Task;
import gov.noaa.pmel.tmap.las.service.TemplateTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.jdom.JDOMException;

public class FerretTool extends TemplateTool{
    private final static Logger log = LoggerFactory.getLogger(FerretTool.class.getName());
    LASFerretBackendConfig lasFerretBackendConfig;
    
    public FerretTool(String service, String configFile) throws LASException, IOException {
        super(service, configFile);
    }
    
    public FerretTool() throws LASException, IOException {
        
        super("ferret", "FerretBackendConfig.xml");
        
        
        lasFerretBackendConfig = new LASFerretBackendConfig();

        try {
            JDOMUtils.XML2JDOM(getConfigFile(), lasFerretBackendConfig);
        } catch (Exception e) {
            throw new LASException("Could not parse Ferret config file: " + e.toString());
        }
    }
    public LASBackendResponse run(LASBackendRequest lasBackendRequest) throws Exception, LASException, IOException, JDOMException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        
        log.debug("Running the FerretTool.");
        
        LASBackendResponse lasBackendResponse = new LASBackendResponse();
        // TODO check to see if the request contains a custom ferret environment
         
        // Set up the runtime environment.
        lasFerretBackendConfig.setBaseDir(getResourcePath(lasFerretBackendConfig.getBaseDir()));
        HashMap<String, String> envMap = lasFerretBackendConfig.getEnvironment();
        RuntimeEnvironment runTimeEnv = new RuntimeEnvironment();
        runTimeEnv.setParameters(envMap);
        
        if (lasBackendRequest.isRemote() ) {
            
            String output_dir = lasFerretBackendConfig.getOutputDir();
            String http_base_url = lasFerretBackendConfig.getHttpBaseURL();
            String opendap_base_url = lasFerretBackendConfig.getOpendapBaseURL();
            log.debug("Setting local files names for remote server using output dir"+output_dir+" and url base "+http_base_url+" opendap url base"+opendap_base_url);
            if ( output_dir == null || output_dir.equals("") ||
                 http_base_url == null || http_base_url.equals("") ||
                 opendap_base_url == null || opendap_base_url.equals("") ) {
                
                 lasBackendResponse.setError("This backend service is not configured to accept remote requests.");
                 return lasBackendResponse;
                 
            }
            lasBackendRequest.setLocalFileNames(output_dir, http_base_url, opendap_base_url);
        }
        
        if ( lasBackendRequest.isCanceled() ) {
            lasBackendResponse.setError("Job canceled");
            return lasBackendResponse;
        }
        
        log.debug("Setting up the Ferret journal file.");
        
        String journalName = null;
        synchronized(this) {
            journalName = "ferret_operation_" + (int)(Math.random()*1000000)
                + "_" + System.currentTimeMillis();
        }

        if ( lasBackendRequest.isCanceled() ) {
            lasBackendResponse.setError("Job canceled");
            return lasBackendResponse;
        }
        
        String tempDir   = lasFerretBackendConfig.getTempDir();
        if ( !tempDir.endsWith("/") ) tempDir = tempDir + "/";
        if ( tempDir.equals("") || tempDir == null) {
            tempDir = getResourcePath("resources/ferret/temp");
        }

        File td = new File(tempDir);
        if ( !td.exists() ) {
            td.mkdirs();
        }
        
        if ( lasBackendRequest.isCanceled() ) {
            lasBackendResponse.setError("Job canceled");
            return lasBackendResponse;
        }
        
        // TODO what if resources/ferret/temp does not exist!?
        File jnlFile = new File(tempDir + journalName + ".jnl");
        
        if ( lasBackendRequest.isCanceled() ) {
            lasBackendResponse.setError("Job canceled");
            return lasBackendResponse;
        }
        
        log.debug("Creating Ferret journal file.");
        mergeCommandTemplate(lasBackendRequest, jnlFile, "launch.vm" );
        
        log.debug("Finished creating Ferret journal file.");
        
        String args[] = new String[]{jnlFile.getAbsolutePath()};

        log.debug("Creating Ferret task.");
        
        Task ferretTask;
        long timeLimit = lasBackendRequest.getProductTimeout();

        if ( lasBackendRequest.isCanceled() ) {
            lasBackendResponse.setError("Job canceled");
            return lasBackendResponse;
        }
        
        try {
            ferretTask = task(runTimeEnv, args, lasBackendRequest.getCancelFile(), timeLimit);
        } catch (Exception e) {
            lasBackendResponse.setError("Could not create Ferret task. ", e);
            return lasBackendResponse;
        }
        
        log.debug("Ferret task created.");
        
        if ( lasBackendRequest.isCanceled() ) {
            lasBackendResponse.setError("Job canceled");
            return lasBackendResponse;
        }
        
        log.debug("Running Ferret task.");
        
        try {
            ferretTask.run();
        } catch (Exception e) {
            lasBackendResponse.setError("Ferret did not run correctly. ", e);
            return lasBackendResponse;
        }
        
        log.debug("Ferret Task finished.");
        
        if ( lasBackendRequest.isCanceled() ) {
            lasBackendResponse.setError("Job canceled");
            return lasBackendResponse;
        }
             
        log.debug("Checking for errors.");
        String output = "";
    	String stderr = "";
        if ( ferretTask != null ) {
        	output = ferretTask.getOutput();
        	stderr = ferretTask.getStderr();
        }
        // Make a debug file so we can see what happened.
        String debug_filename = lasBackendRequest.getResultAsFileByType("debug");
        if ( debug_filename != null && !debug_filename.equals("") ) {
            PrintWriter debugWriter=null;
            File debug = new File(debug_filename);
            debugWriter = new PrintWriter(new FileOutputStream(debug));
            debugWriter.println(stderr);
            debugWriter.println(output);
            debugWriter.flush();
            debugWriter.close();
            
        }
        
        // Build the response as if it worked.  If it did not, the response will get modified.
        lasBackendResponse.addResponseFromRequest(lasBackendRequest);
        
        if ( !ferretTask.getHasError() ) {
            // Everything worked.  Create output.
                        
            log.debug("Creating map scale file.");
            
            // Create the map scale XML file if requested.
            String map_scale_filename = lasBackendRequest.getResultAsFileByType("map_scale");
            if ( map_scale_filename != null && !map_scale_filename.equals("") ) {
                File map_scale = new File(map_scale_filename);
                
                try {
                	LASMapScale lasMapScale = new LASMapScale(map_scale);
                    lasMapScale.write(map_scale);
                } catch (FileNotFoundException e) {
                	lasBackendResponse.setError("The map scale file was not found.", e);
                }
            }    
            
            log.debug("Creating the index file.");
            
            // Create the map scale XML file if requested.
            String index_filename = lasBackendRequest.getResultAsFileByType("index");
            if ( index_filename != null && !index_filename.equals("") ) {
                File index = new File(index_filename);
                try {
                    LASRegionIndex lasRegionIndex = new LASRegionIndex(index);                
                    lasRegionIndex.write(index);
                } catch (FileNotFoundException e){
                	lasBackendResponse.setError("The region index file was not found.", e);                	
                }
            }
        }
        else {
            // Error was generated.  Make error page instead.
            
            String errorMessage = ferretTask.getErrorMessage();
            log.debug("Error message: "+errorMessage);
            log.debug("stderr: "+stderr);
            log.debug("stdout: "+output);
            String error_message = "An error occurred creating your product.";
            try {
                error_message = findMessage(stderr, lasFerretBackendConfig);
            } catch (Exception e) {
            	
            		// Go on with a generic error message.
            	
            }
            lasBackendResponse.setError("las_message", error_message);
            try {
            	lasBackendResponse.addError("exception_message", stderr+"\n"+output);
            } catch (Exception e) {
            		lasBackendResponse.addError("exception_message", "Check debug output file for details.");
            }
            return lasBackendResponse;
        }

        
        
        if ( lasBackendRequest.isCanceled() ) {
            lasBackendResponse.setError("Job canceled");
            return lasBackendResponse;
        }
        
        log.debug("Finished running the FerretTool.");
        
        return lasBackendResponse;
    }

  
    public Task task(RuntimeEnvironment runTimeEnv, String[] args, File cancel, long timeLimit) throws Exception {
        //String[] errors = { "**ERROR", " **ERROR"," **TMAP ERR", "STOP -script mode", "Segmentation fault", "No such", " **netCDF error", "**netCDF error"};
        
        String[] errors = lasFerretBackendConfig.getErrorKeys();
        
        String scriptDir = lasFerretBackendConfig.getScriptDir();
        if ( scriptDir == "" ) {
            scriptDir = getResourcePath("resources/ferret/scripts");
        }
        String tempDir   = lasFerretBackendConfig.getTempDir();
        if ( tempDir == "" ) {
            tempDir = getResourcePath("resources/ferret/temp");
        }
        
        File scriptFile = new File(scriptDir, "FBS.jnl");
        if (!scriptFile.exists()) {
            throw new LASException("Missing controller script FBS.jnl");
        }

        StringBuffer argBuffer = new StringBuffer("FBS.jnl");

        
        for (int i = 0; i < args.length; i++) {
            argBuffer.append(" ");
            argBuffer.append(args[i]);
        }
        

        boolean useNice = lasFerretBackendConfig.getUseNice();
        String interpreter = lasFerretBackendConfig.getInterpreter();
        String ferretBinary = lasFerretBackendConfig.getExecutable();
        List<String> configured_args = lasFerretBackendConfig.getArgs();
        
        int offset = (useNice) ? 1 : 0;
        if ( (interpreter != null && !interpreter.equals("")) ) {
            offset = offset + 1;
        }
        String[] cmd;
        
        // The number of arguments:
        // The offset for the nice command and the interpreter if these are being used.
        // The arguments passed in via the configuration.
        // The executable or interpreted script and the arguments to the -script at the from the argsBuffer (+2)
        cmd = new String[offset + configured_args.size() + 2];
        
        if (useNice) {
            cmd[0] = "nice";
        }
        
        if ( (interpreter != null && !interpreter.equals("")) && useNice ) {
            cmd[1] = interpreter;
        } else if ( (interpreter != null && !interpreter.equals("")) && !useNice) {
            cmd[0] = interpreter;
        }

        if ( (interpreter != null && !interpreter.equals("")) ) {
           String arg = argBuffer.toString();
           if (arg.contains("FDS_test")) {
               cmd[offset] = "\""+ferretBinary+"\"";
           }
           else {
               String perlCmd = arg.substring(arg.indexOf(" ")+1,arg.length());
               cmd[offset] = "\""+perlCmd+"\"";
           }
        } else {
            cmd[offset] = ferretBinary;
            
            
            
            int index = 1;
            for (Iterator argsIt = configured_args.iterator(); argsIt.hasNext();) {
				String arg = (String) argsIt.next();
				cmd[offset + index] = arg;
				index++;
			}

            cmd[offset + index ] = argBuffer.toString();
        }

        String env[] = runTimeEnv.getEnv();

        File workDirFile = null;
        if (tempDir != null) {
            workDirFile = new File(tempDir);
        }

        
        Task task = new Task(cmd, env, workDirFile, cancel, timeLimit, errors);

        log.debug("command line for task is:\n" + task.getCmd());
        return task;

    }
}
