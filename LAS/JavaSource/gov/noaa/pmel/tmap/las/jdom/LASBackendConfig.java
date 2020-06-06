/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.util.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * @author rhs
 *
 */
public class LASBackendConfig extends LASDocument {
	   public HashMap<String, String> getEnvironment() throws LASException {
	        HashMap<String, String> env = new HashMap<String, String>();
	        Element environment = this.getRootElement().getChild("environment");
	        String base_dir = getBaseDir();
	        if ( !base_dir.startsWith("/")) {
	            throw new LASException("Configuration erorr: base_dir is not a full path.");
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
	    public String getBaseDir() {
	        Element invoker = this.getRootElement().getChild("invoker");
	        if ( invoker != null ) {
	            String base_dir = invoker.getAttributeValue("base_dir");
	            if ( base_dir != null ) {
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
	    public String getScriptDir() {
	        Element invoker = this.getRootElement().getChild("invoker");
	        if ( invoker != null ) {
	            String script_dir = invoker.getAttributeValue("script_dir");
	            if ( script_dir != null ) {
	               return script_dir;
	            }
	        }
	        return "";
	    }
	    public String getTempDir() {
	        Element invoker = this.getRootElement().getChild("invoker");
	        if ( invoker != null ) {
	            String temp_dir = invoker.getAttributeValue("temp_dir");
	            if ( temp_dir != null ) {
	               return temp_dir;
	            }
	        }
	        return "";
	    }
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
	     * @return output_dir - the directory where the remote service will write it's files.
	     */
	    public String getOutputDir() {
	        Element invoker = this.getRootElement().getChild("invoker");
	        if ( invoker != null ) {
	            String output_dir = invoker.getAttributeValue("output_dir");
	            if ( output_dir != null ) {
	               return output_dir;
	            }
	        }
	        return "";
	    }
	    /**
	     * @return http_base_url - the base_url of the remote server for a particular service for generic HTTP traffic.
	     */
	    public String getHttpBaseURL() {
	        Element invoker = this.getRootElement().getChild("invoker");
	        if ( invoker != null ) {
	            String base_url = invoker.getAttributeValue("http_base_url");
	            if ( base_url != null ) {
	               return base_url;
	            }
	        }
	        return "";
	    }
	    /**
	     * @return opendap_base_url - the base_url of the remote opendap server for a particular service.
	     */
	    public String getOpendapBaseURL() {
	        Element invoker = this.getRootElement().getChild("invoker");
	        if ( invoker != null ) {
	            String base_url = invoker.getAttributeValue("opendap_base_url");
	            if ( base_url != null ) {
	               return base_url;
	            }
	        }
	        return "";
	    }
	    public String[] getErrorKeys() {
	        List messages = getRootElement().getChild("messages").getChildren("message");
	        String[] errors = new String[messages.size()];
	        int i = 0;
	        for (Iterator messIt = messages.iterator(); messIt.hasNext();) {
	            Element message = (Element) messIt.next();
	            errors[i] = message.getChild("key").getTextTrim();
	            i++;
	        }
	        return errors;
	    }
	    public ArrayList<Message> getMessages() {
	        ArrayList<Message> messageList= new ArrayList<Message>();
	        List messages = getRootElement().getChild("messages").getChildren("message");
	        for (Iterator messIt = messages.iterator(); messIt.hasNext();) {
	            Element message = (Element) messIt.next();
	            messageList.add(new Message(message));
	        }
	        return messageList;
	    }
}
