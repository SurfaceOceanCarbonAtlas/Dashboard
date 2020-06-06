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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/** 
 * The class represents a set of modifiable environment variables.
 * @author Roland Schweitzer
 * @author Joe W. from Cola for the Anagram Framework
 */
public class RuntimeEnvironment implements Cloneable{
    
    protected String baseDir;
    protected Map<String, String> parameters;
    
    public RuntimeEnvironment() {
        
    }
    /**Construct a RuntimeEnvironment object using
     * a parameter map 
     * @param parameters parameter map whose keys are variable 
     * names and whose values are variable string values
     */
    public RuntimeEnvironment(Map<String, String> parameters) {
        this.parameters = parameters;
    }
    /**
     * Dump out a nice string representation of this for debugging.
     * @return the string representation of the environment
     */
    public String toString() {
        StringBuffer env = new StringBuffer();
        env.append(baseDir+"\n");
        for (Iterator keyIt = parameters.keySet().iterator(); keyIt.hasNext();) {
            String key = (String) keyIt.next();
            String value = parameters.get(key);
            env.append(key+"="+value+"\n");
        }
        return env.toString();      
    }
    
    /**Returns the value of a specified variable
     * @param variable the name of the queried variable
     */
    public String getVariable(String variable){
        return (String)parameters.get(variable);
    }
    
    /** Sets the value of a specified variable
     * @param variable the name of the variable to be set
     * @param value the new value of the variable
     */
    public void setVariable(String variable, String value){
        parameters.put(variable, value);
    }
    
    /** Returns an array of the current environment variables
     */
    public String[]  getEnv(){
        
        int size = parameters.size();
        int i=0;
        String [] envp = new String[size];
        
        Iterator it = parameters.keySet().iterator();
        while(it.hasNext()){
            String var = (String)it.next();
            envp[i]="" + var + "=" + parameters.get(var);
            i++;
        }
        
        return envp;
    }
    
    
    /** Modified the current runtime environment 
     * according to the input string.
     * @param envString a list of semi-comma separated equations
     *        that modified the runtime environment
     */
    public void setEnv(String envString) {
        if(envString==null)
            return;
        StringTokenizer st = new StringTokenizer(envString, ";");
        String var, value;
        
        while(st.hasMoreTokens()){
            String current = st.nextToken();
            int equalPos = current.indexOf("=");
            if(equalPos<0){
                var=current;
                value = "";
            }
            else {
                var=current.substring(0, equalPos);
                value = current.substring(equalPos+1);
            }
            
            value = resolvePaths(value, baseDir);
            
            int dollarPos = value.indexOf("$");
            while(dollarPos>=0) {
                int i;
                for(i=dollarPos+1;i<value.length();i++){
                    char c = value.charAt(i);
                    if(!((c>='A'&&c<='Z')||(c>='a'&&c<='z')
                            ||(c>='0'&&c<='0')||c=='_'))
                        break;
                }
                String replaceVar = value.substring(dollarPos+1, i);
                String replaceValue = getVariable(replaceVar);
                if(replaceValue==null)
                    replaceValue = "";
                
                value= value.substring(0,dollarPos) + replaceValue + value.substring(i);
                
                dollarPos = value.indexOf("$");
            }
            
            var = var.trim();
            setVariable(var, value);
        }
    }
    /**
     * Builds fully qualified path names based on a base and a string of blank separated paths.  Relative paths get the basePath prepended.
     * @param paths to be resolved
     * @param basePath the "root" path
     * @return the fully qualified path
     */
    public String resolvePaths(String paths, String basePath){
        String returnVal="";
        if(paths==null)
            return returnVal;
        
        StringTokenizer vSt = new StringTokenizer(paths, " ");
        while(vSt.hasMoreTokens()){
            String oneValue = vSt.nextToken();
            if(!oneValue.startsWith("/")
                    &&!oneValue.equals(".")
                    &&!oneValue.startsWith("$")){
                oneValue = basePath+"/"+oneValue;
            }
            returnVal =returnVal + oneValue + " ";
        }
        return returnVal;
    }
    
    /** Clones a copy of this environment
     */
    protected Object clone() 
    throws CloneNotSupportedException {
        RuntimeEnvironment returnVal = new RuntimeEnvironment(new HashMap<String, String>());
        Iterator it = this.parameters.keySet().iterator();
        while(it.hasNext()) {
            String key = (String)it.next();
            String value = this.parameters.get(key);
            returnVal.parameters.put(key, value);
        }
        return returnVal;
    }
    

    /**
     * @return Returns the basePath.
     */
    public String getBaseDir() {
        return baseDir;
    }

    /**
     * @param baseDir the base directory for this runtime environment
     */
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
    /**
     * @return Returns the parameters.
     */
    public Map getParameters() {
        return parameters;
    }
    /**
     * @param parameters The parameters to set.
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
