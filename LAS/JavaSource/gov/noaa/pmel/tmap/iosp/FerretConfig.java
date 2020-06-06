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

import gov.noaa.pmel.tmap.iosp.IOSPException;
import gov.noaa.pmel.tmap.iosp.IOSPDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

/**
 * This class manages the Ferret Config XML file that is used to run the Ferret IO Service Provider 
 * for the Java netCDF library.
 * @author Roland Schweitzer
 *
 */

public class FerretConfig extends IOSPDocument {
	/*
	 * Any number that uniquely identifies the version of this class' code.  
	 * The Eclipse IDE will generate it automatically for you.  We do not depend on this
	 * since we do not serialize our code across the wire.
	 */
    private static final long serialVersionUID = 4074966641591379924L;
    
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
     * @throws IOSPException if the base_dir is not a full path (i.e. does not start with a "/").
     * @throws Exception
     */
    public HashMap<String, String> getEnvironment() throws IOSPException   {
    	/*
    	 
    	 *
    	 */
        HashMap<String, String> env = new HashMap<String, String>();
        Element environment = this.getRootElement().getChild("environment");
        String base_dir = getIOSPBaseDir();
        
        if ( !base_dir.startsWith("/")) {
            throw new IOSPException("base_dir "+base_dir+" is not a full path.");
        }
        if ( environment != null ) {
            List variables = environment.getChildren("variable");
            for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
                Element variable = (Element) varIt.next();
                String name = variable.getChildTextTrim("name");
                List values = variable.getChildren("value");
                String value = "";
                for (Iterator valueIt = values.iterator(); valueIt.hasNext();) {
                    Element valueE = (Element) valueIt.next();
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
                return env;
            }
        }
        return null;
    }
    /**
     * Get the base resource directory which the contains the Ferret config and other resources for the IOSP.
     * @return String containing the base_dir attribute for the Ferret config.
     */
    public String getIOSPBaseDir() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String base_dir = invoker.getAttributeValue("iosp_base_dir");
            if ( base_dir != null ) {
                if ( !base_dir.endsWith("/") ) {
                    base_dir = base_dir + "/";
                }
               return base_dir;
            }
        }
        return "";
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
    /**
     * Set the path to the iosp_base_dir used by the IOSP.
     * @param dir the directory to set
     */
    public void setIOSPBaseDir(String dir) {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            invoker.setAttribute("iosp_base_dir", dir);
        }
    }
    /**
     * Set the path to the base_dir use by LAS.
     * @param dir the directory to set
     */
    public void setBaseDir(String dir) {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            invoker.setAttribute("base_dir", dir);
        }
    }
    /**
     * Returns the directory where the Ferret scripts specifically for the IOSP are stored.
     * @return String containing the path to the scripts directory
     */
    public String getIOSPScriptDir() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String script_dir = invoker.getAttributeValue("iosp_script_dir");
            if ( script_dir != null ) {
                if ( !script_dir.endsWith("/") ) {
                    script_dir = script_dir+"/";
                }
               return script_dir;
            }
        }
        return "";
    }
    /**
     * Returns the directory where the Ferret scripts for LAS are stored.
     * @return String containing the path to the scripts directory
     */
    public String getScriptDir() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String script_dir = invoker.getAttributeValue("script_dir");
            if ( script_dir != null ) {
                if ( !script_dir.endsWith("/") ) {
                    script_dir = script_dir+"/";
                }
               return script_dir;
            }
        }
        return "";
    }
    /**
     * Returns the directory path for the temporary directory for the ISOP.  This will 
     * potentially require a lot of storage space.
     * @return String with the path to the temporary directory.
     */
    public String getIOSPTempDir() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String temp_dir = invoker.getAttributeValue("iosp_temp_dir");
            if ( temp_dir != null ) {
               if ( !temp_dir.endsWith("/") ) {
                   temp_dir = temp_dir+"/";
               }
               return temp_dir;
            }
        }
        return "";
    }
    /**
     * Returns the directory path for the temporary directory for this Ferret invoker.
     * @return String with the path to the temporary directory.
     */
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
    /**
     * Get the data directory attribute for this IOSP.
     * @return The data directory for this IOSP (might not need this?!)
     */
    public String getIOSPDataDir() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String data_dir = invoker.getAttributeValue("iosp_data_dir");
            if ( data_dir != null ) {
               if ( !data_dir.endsWith("/") ) {
                   data_dir = data_dir+"/";
               }
               return data_dir;
            }
        }
        return "";
    }
    /**
     * Get the data directory attribute for this Ferret invoker.
     * @return The data directory for this Ferret invoker
     */
    public String getDataDir() {
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            String data_dir = invoker.getAttributeValue("data_dir");
            if ( data_dir != null ) {
               if ( !data_dir.endsWith("/") ) {
                   data_dir = data_dir+"/";
               }
               return data_dir;
            }
        }
        return "";
    }
    public List<String> getArgs() {
        List<String> args = new ArrayList<String>();
        Element invoker = this.getRootElement().getChild("invoker");
        if ( invoker != null ) {
            List configured_args = invoker.getChildren("arg");
            for (Iterator argIt = configured_args.iterator(); argIt.hasNext();) {
                Element arg = (Element) argIt.next();
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
}
