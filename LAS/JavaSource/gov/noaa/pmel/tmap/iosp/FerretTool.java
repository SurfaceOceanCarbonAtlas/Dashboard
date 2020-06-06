/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.iosp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
/**
 * This is the Tool class (based on the Anagram Tool class) that runs Ferret so it can do work for the IOSP.
 * @author rhs
 *
 */
public class FerretTool extends Tool{
    private final static Logger log = LoggerFactory.getLogger(FerretTool.class.getName());
    //private final static Logger log = LoggerFactory.getLogger(FerretTool.class.getName());
    FerretConfig ferretConfig;
    String scriptDir;
    String tempDir;
    String dataDir;
    /**
     * The default constructor that reads the config file and readies the tool to be used.
     * @throws Exception
     */
    public FerretTool() throws Exception {

        // Set up the Java Properties Object

        String configFilePath = getResourcePath("resources/iosp/FerretConfig.xml");
        File configFile;
        if ( configFilePath != null ) {
            configFile = new File(configFilePath);                               
        } else {
            throw new Exception("Ferret config file resources/iosp/FerretConfig.xml not found.");
        }

        ferretConfig = new FerretConfig();

        try {
            JDOMUtils.XML2JDOM(configFile, ferretConfig);
        } catch (Exception e) {
            throw new Exception("Could not parse Ferret config file: " + e.toString());
        }
        
        log.debug("config file parsed.");

        scriptDir   = ferretConfig.getIOSPScriptDir();
        if ( scriptDir == "" ) {
            scriptDir = getResourcePath("resources/iosp/scripts");
        } else if ( !scriptDir.startsWith("/") ) {
            scriptDir = getResourcePath(scriptDir);
        }

        log.debug("Setting script dir to: "+ scriptDir);


        tempDir   = ferretConfig.getIOSPTempDir();
        if ( tempDir == "" ) {
            tempDir = getResourcePath("resources/iosp/temp");
        } else if ( !tempDir.startsWith("/") ) {
            tempDir = getResourcePath(tempDir);
        }

        log.debug("Setting temp dir to: "+ tempDir);

        dataDir   = ferretConfig.getIOSPDataDir();
        if ( dataDir == "" ) {
            dataDir = getResourcePath("resources/iosp/data");
        } else if ( !dataDir.startsWith("/") ) {
            dataDir = getResourcePath(dataDir);
        }

        log.debug("Setting data dir to: "+ dataDir);

    }
    /**
     * This runs the tool and captures the output to a debug file.
     * @param driver the command to run (ferret in this case).
     * @param jnl the command file.
     * @param cacheKey the cache key (should be removed since we don't need it)
     * @param output_filename where to write STDOUT, can be a debug file or the header.xml file.
     * @throws Exception
     */
    public void run (String driver, String jnl, String cacheKey, String temporary_filename, String output_filename) throws Exception {
        log.debug("Running the FerretTool.");

        // Set up the runtime environment.
        log.debug("iosp base dir in="+ferretConfig.getIOSPBaseDir());
        log.debug("iosp base dir="+getResourcePath(ferretConfig.getIOSPBaseDir()));
        ferretConfig.setIOSPBaseDir(getResourcePath(ferretConfig.getIOSPBaseDir()));
        log.debug("base dir set");
        HashMap<String, String> envMap = ferretConfig.getEnvironment();
        log.debug("got enviroment.");
        RuntimeEnvironment runTimeEnv = new RuntimeEnvironment();
        log.debug("Constructed new runTimeEnv.");
        runTimeEnv.setParameters(envMap);

        log.debug("Setting up the Ferret journal file.");

        String journalName = null;
        synchronized(this) {
            journalName = "ferret_operation"
                + "_" + System.currentTimeMillis();
        }
        
        if ( tempDir.contains("../") || tempDir.contains("/..") || cacheKey.contains("../") || cacheKey.contains("/..") || journalName.contains("../") || journalName.contains("/..") ) {
        	throw new Exception("Illegal file name.");
        	
        }

        File jnlFile = new File(tempDir +File.separator+ cacheKey + File.separator + journalName + ".jnl");

        log.debug("Creating Ferret journal file in " + jnlFile.getAbsolutePath() );

        createJournal(jnl, jnlFile);

        log.debug("Finished creating Ferret journal file.");

        String args[] = new String[]{driver, jnlFile.getAbsolutePath(), temporary_filename};

        log.debug("Creating Ferret task.");

        Task ferretTask=null;

        long timeLimit = ferretConfig.getTimeLimit();

        try {
            ferretTask = task(runTimeEnv, args, timeLimit, scriptDir, tempDir);
        } catch (Exception e) {
            log.error("Could not create Ferret task. "+e.toString());
        }

        log.debug("Running Ferret task.");
        
        if ( temporary_filename.contains("../") || temporary_filename.contains("/..") ) {
        	throw new Exception("Illegal temporary file name.");
        }

        if ( output_filename.contains("../") || output_filename.contains("/..") ) {
        	throw new Exception("Illegal output file name.");
        }
        
        File temp_file = new File(temporary_filename);
        File out_file = new File(output_filename);
        try {
        	if ( ferretTask != null ) {
        		temp_file.createNewFile();
        		ferretTask.run();
        	}
            
        } catch (Exception e) {
            log.error("Ferret did not run correctly. "+e.toString());
        }

        log.debug("Ferret Task finished.");

        log.debug("Checking for errors.");


        
        String output = ferretTask.getOutput();
        String stderr = ferretTask.getStderr();
        if ( output.contains("/..") || output.contains("../") || stderr.contains("/..") || stderr.contains("../") ) {
        	throw new Exception("Illegal file name.");
        }
        
        if ( !ferretTask.getHasError() || stderr.contains("**ERROR: regridding") ) {
            // Everything worked.  Create output.

            log.debug("Ferret task completed without error.");
            log.debug("Output:\n"+output);
            temp_file.renameTo(out_file);
            if ( output_filename != null && !output_filename.equals("") ) {

                String logfile = output_filename+".log";
                
                if ( logfile.contains("../") || logfile.contains("/..") ) {
                	throw new Exception("Illegal log file name.");
                }
                log.debug("Writing output to "+output_filename);
                PrintWriter logwriter = new PrintWriter(new FileOutputStream(logfile));               
                logwriter.println(output);
                logwriter.println(stderr);

                logwriter.flush();
                logwriter.close();


            }
        }
        else {
            // Error was generated.  Make error page instead.
            log.error("Ferret generated an error.");
            String errorMessage = ferretTask.getErrorMessage();
            log.error(errorMessage+"\n"+stderr+"\n");
            log.error(output);
            File bad = new File(out_file.getAbsoluteFile()+".bad");
            temp_file.renameTo(bad);
        }

        log.debug("Finished running the FerretTool.");

    }
    /**
     * Run Ferret and capture STDOUT into the header.xml file for this data set.
     * @param driver which command to run (ferret in this case).
     * @param jnl the file with the commands to run.
     * @param cacheKey not used, should be removed.
     * @return return the full path to the STDOUT file (header.xml in this case).
     * @throws Exception
     */
    public String run_header(String driver, String jnl, String cacheKey) throws Exception {
        log.debug("Entering method.");
        String tempDir   = ferretConfig.getIOSPTempDir();
        if ( tempDir == "" ) {
            tempDir = getResourcePath("resources/ferret/temp");
        } else if ( !tempDir.startsWith(File.separator) ) {
            tempDir = getResourcePath(tempDir);
        }
        tempDir = tempDir+cacheKey;
        File cacheDir = new File (tempDir);
        String header_filename = tempDir+File.separator+"header.xml";
        String header_temp_filename = header_filename+".tmp";
        if ( header_filename.contains("../") || header_filename.contains("/..") ) {
        	throw new Exception("Illegal header file name.");
        }
        if ( !cacheDir.isDirectory() ) {
            cacheDir.mkdir();
        } else {
            File header = new File(header_filename);
            if ( header.exists() ) {
                log.debug("The header file already exists.  It's a cache hit.  Return now.");
                return header_filename;
            }
        }

        log.debug("Generating the XML header for this data source using "+header_filename);
        run(driver, jnl, cacheKey, header_temp_filename, header_filename);
        
        return header_filename;
    }
    /**
     * Create a journal file that Ferret will use to perform a task.
     * @param jnl
     * @param jnlFile
     * @throws Exception
     */
    private void createJournal(String jnl, File jnlFile) throws Exception {
        PrintWriter jnlWriter = null;
        try {
            jnlWriter = new PrintWriter(new FileOutputStream(jnlFile));
        }
        catch(Exception e) {
            // We need to package these and send them back to the UI.
        }

        if ( jnlWriter != null ) {
        	jnlWriter.println(jnl);
        	jnlWriter.flush();
        	jnlWriter.close();
        }

    }
    /**
     * The construct a Task which is the definition of the command line arguments for particular invocation of the Tool.
     * @param runTimeEnv the Java instantiation of the shell run time environment for the invocation of this command.
     * @param args the command line arguments
     * @param timeLimit how long to let it run
     * @param scriptDir where to look for the "driver" script.
     * @param tempDir where to write temp files if you need it.
     * @return the task object
     * @throws Exception
     */
    public Task task(RuntimeEnvironment runTimeEnv, String[] args, long timeLimit, String scriptDir, String tempDir) throws Exception {
        String[] errors = { "**ERROR", " **ERROR"," **TMAP ERR", "STOP -script mode", "Segmentation fault", "No such"};

        File scriptFile = new File(scriptDir, "data.jnl");
        if (!scriptFile.exists()) {
            throw new Exception("Missing controller script data.jnl");
        }

        scriptFile = new File(scriptDir, "header.jnl");
        if (!scriptFile.exists()) {
            throw new Exception("Missing controller script header.jnl");
        }

        StringBuffer argBuffer = new StringBuffer();


        for (int i = 0; i < args.length; i++) {
            argBuffer.append(args[i]);
            argBuffer.append(" ");
        }


        /*
         * There are two sets of arguments that come into the task.
         * fargs aref from the configuration -gif -script stuff like that.
         * args are those arguments that need to be passed to the [py]ferret command line
         * which in the case of making the header or the name of the script to make the headers,
         * the name of the script to initialize the data set and the name of the XML file to receive the info.
         *
         */
        boolean useNice = ferretConfig.getUseNice();

        String ferretBinary = ferretConfig.getFerret();
        
        List<String> fargs = ferretConfig.getArgs();
        
        int offset = (useNice) ? 1 : 0;

        String[] cmd;

        // We've never used the "nice" feature
        cmd = new String[offset + fargs.size() + args.length + 1];


        if (useNice) {
            cmd[0] = "nice";
        }


        cmd[offset] = ferretBinary;
        
        for (int i = 0; i < fargs.size(); i++) {
            cmd[offset + i+1] = fargs.get(i);
        }
        for (int i = 0; i < args.length; i++) {
            cmd[offset + fargs.size()+1+i] = args[i];
        }


        String env[] = runTimeEnv.getEnv();

        File workDirFile = null;
        if (tempDir != null) {
            workDirFile = new File(tempDir);
        }


        Task task = new Task(cmd, env, workDirFile, timeLimit, errors);

        log.debug("command line for task is:\n"
                + task.getCmd());
        return task;

    }
    public String getDataDir() {
        return dataDir;
    }
    public String getTempDir() {
        return tempDir;
    }
    public String getScriptDir() {
        return scriptDir;
    }
}
