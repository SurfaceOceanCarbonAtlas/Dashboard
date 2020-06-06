package gov.noaa.pmel.tmap.las.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/** The class represents a set of modifiable environment variables.
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
        if ( parameters == null ) {
        	return null;
        }
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
