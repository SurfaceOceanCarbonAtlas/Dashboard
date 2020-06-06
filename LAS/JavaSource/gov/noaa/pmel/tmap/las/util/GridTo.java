package gov.noaa.pmel.tmap.las.util;

import org.jdom.Element;

public class GridTo {
	private String URL;
    private String var;
    private String varXPath;
    private String dsID;
    private StringBuffer jnl;
    private Element data;
    private boolean isAnalysis;
    private String gridID;
   
	public GridTo() {
		this.URL = "";
        this.var = "";
        this.varXPath = "";
        this.dsID = "";
        this.jnl = null;
        this.data = null;
        this.isAnalysis = false;
        this.gridID = "";
        this.jnl = new StringBuffer("");
       
	}
	/**
	 * @return the uRL
	 */
	public String getURL() {
		return URL;
	}
	/**
	 * @param url the uRL to set
	 */
	public void setURL(String url) {
		URL = url;
	}
	/**
	 * @return the var
	 */
	public String getVar() {
		return var;
	}
	/**
	 * @param var the var to set
	 */
	public void setVar(String var) {
		this.var = var;
	}
	/**
	 * @return the varXPath
	 */
	public String getVarXPath() {
		return varXPath;
	}
	/**
	 * @param varXPath the varXPath to set
	 */
	public void setVarXPath(String varXPath) {
		this.varXPath = varXPath;
	}
	/**
	 * @return the dsID
	 */
	public String getDsID() {
		return dsID;
	}
	/**
	 * @param dsID the dsID to set
	 */
	public void setDsID(String dsID) {
		this.dsID = dsID;
	}
	/**
	 * @return the jnl
	 */
	public StringBuffer getJnl() {
		return jnl;
	}
	/**
	 * @param jnl the jnl to set
	 */
	public void setJnl(StringBuffer jnl) {
		this.jnl = jnl;
	}
	/**
	 * @return the data
	 */
	public Element getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(Element data) {
		this.data = data;
	}
	/**
	 * @return the isAnalysis
	 */
	public boolean isAnalysis() {
		return isAnalysis;
	}
	/**
	 * @param isAnalysis the isAnalysis to set
	 */
	public void setAnalysis(boolean isAnalysis) {
		this.isAnalysis = isAnalysis;
	}
	/**
	 * @return the gridID
	 */
	public String getGridID() {
		return gridID;
	}
	/**
	 * @param gridID the gridID to set
	 */
	public void setGridID(String gridID) {
		this.gridID = gridID;
	}
	
}
