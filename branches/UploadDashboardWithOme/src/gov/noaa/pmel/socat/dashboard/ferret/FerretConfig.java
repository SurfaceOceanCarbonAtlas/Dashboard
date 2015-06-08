/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.socat.dashboard.ferret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

/**
 * This class manages the Ferret Config XML file that is used to run the Ferret IO Service Provider 
 * for the Java netCDF library.
 * @author Roland Schweitzer
 *
 */

public class FerretConfig extends Document {

	private static final long serialVersionUID = -1491908105223114494L;

	public enum Action {
		/**
		 * Complete a DSG file by adding data computed from user-provided data.
		 */
		COMPUTE,
		/**
		 * Decimate data in a completed DSG file.
		 */
		DECIMATE,
		/**
		 * Create the preview plots
		 */
		PLOTS,
	}

	/**
     * Returns the Ferret environment.
     * The environment section of the config file looks like this.
     * A relative path name (like scripts or jnls) will get resolved
     * by pre-pending the full path to the "base_dir" attribute
     * from the &lt;invoker&gt; element.
     *  <pre>
     *  &lt;variable&gt;
     *       &lt;name&gt;FER_GO&lt;/name&gt;
     *        &lt;value&gt;.&lt;/value&gt;
     *        &lt;value&gt;/home/porter/tmap/ferret/tmap_go&lt;/value&gt;
     *        &lt;value&gt;/home/porter/tmap/ferret/x86_64-linux/contrib&lt;/value&gt;
     *        &lt;value&gt;/home/porter/tmap/ferret/x86_64-linux/examples&lt;/value&gt;
     *        &lt;value&gt;/home/porter/tmap/ferret/x86_64-linux/go&lt;/value&gt;
     *        &lt;value&gt;jnls&lt;/value&gt;
     *        &lt;value&gt;jnls/insitu&lt;/value&gt;
     *        &lt;value&gt;jnls/section&lt;/value&gt;
     *        &lt;value&gt;scripts&lt;/value&gt;
     *   &lt;/variable&gt;
     *   </pre>
     * @return HashMap containing the Ferret environment variables as keys and their values as values
     * @throws Exception if the base_dir is not a full path (i.e. does not start with a "/").
     * @throws Exception
     */
    public RuntimeEnvironment getRuntimeEnvironment() throws Exception   {
        HashMap<String, String> env = new HashMap<String, String>();
        Element environment = this.getRootElement().getChild("environment");
        String base_dir = getBaseDir();
        
        if ( !base_dir.startsWith("/")) {
            throw new Exception("base_dir "+base_dir+" is not a full path.");
        }
        if ( environment != null ) {
            List<Element> variables = environment.getChildren("variable");
            for (Iterator<Element> varIt = variables.iterator(); varIt.hasNext();) {
                Element variable = varIt.next();
                String name = variable.getChildTextTrim("name");
                List<Element> values = variable.getChildren("value");
                String value = "";
                for (Iterator<Element> valueIt = values.iterator(); valueIt.hasNext();) {
                    Element valueE = valueIt.next();
                    String val = valueE.getTextTrim();
                    if ( val.startsWith("/")) {
                       value = value + val;
                    } else {
                        value = value + base_dir + val;
                    }
                    if (valueIt.hasNext()) {
                        value = value + " ";
                    }
                }
                env.put(name,value);
            }
            if (env != null) {
                RuntimeEnvironment runenv = new RuntimeEnvironment();
                runenv.setBaseDir(base_dir);
                runenv.setParameters(env);
                return runenv;
                
            }
        }
        return null;
    }
    /**
     * Get the base resource directory for LAS which the contains the Ferret config and other resources.
     * @return String containing the base_dir attribute for the Ferret config.
     */
    public String getBaseDir() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String base_dir = invoker.getAttributeValue("base_dir");
            if ( base_dir != null ) {
                if ( !base_dir.endsWith("/") ) {
                    base_dir = base_dir + "/";
                }
               return base_dir;
            }
        }
        return "";
    }
    public void setBaseDir(String dir) {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            invoker.setAttribute("base_dir", dir);
        }
    }
    public String getTempDir() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String temp_dir = invoker.getAttributeValue("temp_dir");
            if ( temp_dir != null ) {
               if ( !temp_dir.endsWith("/") ) {
                   temp_dir = temp_dir+"/";
               }
               return temp_dir;
            }
        }
        return "";
    }
    public List<String> getArgs() {
        List<String> args = new ArrayList<String>();
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            List<Element> configured_args = invoker.getChildren("arg");
            for (Iterator<Element> argIt = configured_args.iterator(); argIt.hasNext();) {
                Element arg = argIt.next();
                String a = arg.getTextTrim();
                args.add(a);
            }
            
        }
        return args;
    }
    /**
     * Returns the path to the Ferret executable.
     * @return String containing the path to the Ferret executable
     */
    public String getFerret() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String ferret_bin = invoker.getAttributeValue("executable");
            if ( ferret_bin != null ) {
               return ferret_bin;
            }
        }
        return "";
    }
    /**
     * Boolean to determine if Ferret should be "niced down" when invoked.
     * @return boolean true if use nice; false do not nice
     */
    public boolean getUseNice() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String use_nice = invoker.getAttributeValue("use_nice");
            if ( use_nice != null ) {
               return Boolean.valueOf(use_nice).booleanValue();
            }
        }
        return false;
    }
    /**
     * Return the time limit in milliseconds for how long Ferret should be allowed to run on one invocation.
     * @return long with the time in milliseconds.  Defaults to 10000 if not defined in the config.
     */
    public long getTimeLimit() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String time_limit = invoker.getAttributeValue("time_limit");
            if ( time_limit != null ) {
               return Long.valueOf(time_limit).longValue();
            }
        }
        return 10000;
    }
    public String[] getErrorKeys() {
        List<Element> messages = getRootElement().getChild("messages").getChildren("message");
        String[] errors = new String[messages.size()];
        int i = 0;
        for (Iterator<Element> messIt = messages.iterator(); messIt.hasNext();) {
            Element message = messIt.next();
            errors[i] = message.getChild("key").getTextTrim();
            i++;
        }
        return errors;
    }
    /**
     * Gets the path to the command interpreter if the thing being run is something like a shell script.
     * @return
     */
    public String getInterpreter() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String interpreter = invoker.getAttributeValue("interpreter");
            if ( interpreter != null ) {
               return interpreter;
            }
        }
        return "";
    }
    /**
     * Gets the path to the executable
     * @return
     */
    public String getExecutable() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String binary = invoker.getAttributeValue("executable");
            if ( binary != null ) {
               return binary;
            }
        }
        return "";
    }
    /**
     * This is the first (and only script) that will get run by the tool.
     * 
     * @param actionEnum
     * 		the desired action of this configuration
     */
    public String getDriverScript(Action actionEnum) {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
        	String driver;
        	if ( actionEnum.equals(Action.COMPUTE) )
        		driver = invoker.getAttributeValue("compute_driver");
        	else if ( actionEnum.equals(Action.DECIMATE) )
        		driver = invoker.getAttributeValue("decimate_driver");
        	else if ( actionEnum.equals(Action.PLOTS) )
        		driver = invoker.getAttributeValue("plots_driver");
        	else
        		driver = null;
            if ( driver != null ) {
               return driver;
            }
        }
        return "";
    }
}
