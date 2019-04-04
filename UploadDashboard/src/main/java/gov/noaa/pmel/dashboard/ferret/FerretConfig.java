package gov.noaa.pmel.dashboard.ferret;

import org.jdom2.Document;
import org.jdom2.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class manages the Ferret Config XML file that is used to run the Ferret IO Service Provider
 * for the Java netCDF library.
 *
 * @author Roland Schweitzer
 */

public class FerretConfig extends Document {

    private static final long serialVersionUID = -9187555458046328313L;

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
     * <pre>
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
     *
     * @return HashMap containing the Ferret environment variables as keys and their values as values
     *
     * @throws Exception
     *         if the base_dir is not a full path (i.e. does not start with a "/").
     */
    public RuntimeEnvironment getRuntimeEnvironment() throws Exception {
        HashMap<String,String> env = new HashMap<String,String>();
        Element environment = this.getRootElement().getChild("environment");
        String base_dir = getBaseDir();

        if ( !base_dir.startsWith("/") )
            throw new Exception("base_dir " + base_dir + " is not a full path.");
        if ( environment == null )
            return null;

        List<Element> variables = environment.getChildren("variable");
        for (Element variable : variables) {
            String name = variable.getChildTextTrim("name");
            List<Element> values = variable.getChildren("value");
            String value = "";
            for (Element valueE : values) {
                String val = valueE.getTextTrim();
                if ( val.startsWith("/") ) {
                    value += val;
                }
                else {
                    value += base_dir + val;
                }
                value += " ";
            }
            env.put(name, value.trim());
        }
        RuntimeEnvironment runenv = new RuntimeEnvironment();
        runenv.setBaseDir(base_dir);
        runenv.setParameters(env);
        return runenv;
    }

    /**
     * Get the base resource directory for LAS which the contains the Ferret config and other resources.
     *
     * @return String containing the base_dir attribute for the Ferret config.
     */
    private String getBaseDir() {
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

    public String getTempDir() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String temp_dir = invoker.getAttributeValue("temp_dir");
            if ( temp_dir != null ) {
                if ( !temp_dir.endsWith("/") ) {
                    temp_dir = temp_dir + "/";
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
            for (Element arg : configured_args) {
                args.add(arg.getTextTrim());
            }
        }
        return args;
    }

    /**
     * Returns the path to the Ferret executable.
     *
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
     * Return the time limit in milliseconds for how long Ferret should be allowed to run on one invocation.
     *
     * @return long with the time in milliseconds.  Defaults to 10000 if not defined in the config.
     */
    public long getTimeLimit() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String time_limit = invoker.getAttributeValue("time_limit");
            if ( time_limit != null )
                return Long.valueOf(time_limit);
        }
        return 10000;
    }

    public String[] getErrorKeys() {
        List<Element> messages = getRootElement().getChild("messages").getChildren("message");
        String[] errors = new String[messages.size()];
        int i = 0;
        for (Element message : messages) {
            errors[i] = message.getChild("key").getTextTrim();
            i++;
        }
        return errors;
    }

    /**
     * Gets the path to the command interpreter if the thing being run is something like a shell script.
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
     * This is the first (and only) script that will get run by the tool.
     *
     * @param actionEnum
     *         the desired action of this configuration
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

    /**
     * @return the filename extension (including the initial '.') for images created by the version
     *         of Ferret/PyFerret in this configuration.  If no executable, an empty string is returned.
     */
    public String getImageFilenameExtension() {
        File exeFile = new File(getExecutable());
        String name = exeFile.getName();
        if ( name.isEmpty() )
            return "";
        // Assume that if the executable filename starts with "python",
        // it is PyFerret and thus ".png"; otherwise it is Ferret and thus ".gif"
        if ( name.startsWith("python") )
            return ".png";
        else
            return ".gif";
    }

}
