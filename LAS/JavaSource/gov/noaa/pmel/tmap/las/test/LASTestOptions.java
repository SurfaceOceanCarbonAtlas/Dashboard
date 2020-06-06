package gov.noaa.pmel.tmap.las.test;


public class LASTestOptions{
    private boolean dds;
    private String view;
    private String dataset;
    private boolean allVar;
    private boolean exitFirst;
    private boolean help;
    //private boolean connectionOnly;
    //private boolean responseOnly;
    private boolean testConnection;
    private boolean testResponse;
    private boolean verbose;  
    private boolean testFTDS;
    private String productTime;
    private String url;
    
    private String dregex;
    private String vregex;
    
	private long delay = 0;
    private long period = 86400000;   // 24 hours

	private String las;
  
    public LASTestOptions(){
        dregex    = null;
        vregex    = null;
        dds       = false;
        view      = "";
        dataset   = null;
        allVar    = false;
        exitFirst = false;
        help      = false;
        testConnection = false;
        testResponse = false;
        verbose  = false;
        testFTDS = false;
        las = null;
    } 
    
    public void setTestAll(){
        testFTDS = true;
        testConnection = true;
        testResponse = true;
    }

    public void setTestFTDS(){
        testFTDS = true;
    }

    public boolean testFTDS(){
        return testFTDS;
    }

    public void setDDS(){
    	dds = true;
    }
    
    public boolean showDDS(){
    	return dds;
    }
    
    public void setConnectionOnly(){
        testConnection = true;
    }
    
    public void setResponseOnly(){
        testResponse = true;
    }
    
    public boolean testConn(){
    	return testConnection;
    }
    
    public boolean testResp(){
    	return testResponse;
    }
    
    public void setHelp(){
    	help = true;
    }
    
    public void setLAS(String las) {
    	this.las = las;
    }
    
    public String getLAS() {
    	return this.las;
    }
    public void setUrl(String url) {
    	this.url = url;
    }
    public String getUrl() {
    	return this.url;
    }
 
// Keep this junk around temporarily to make sure we have all the options covered.  Yikes!
//    public void showUsage(){
//    	System.out.println("Usage: ant lastest [-Dh=1][-Da=1][-Ddds=1][-Dv=view][-De=1]");
//    	System.out.println("                   [-Dd=URL][-Dc=1][-Dr=1][-Dvb=1][-Df=1]");
//    	System.out.println("       Default   1. Test dataset connections and does not show DDS ");
//    	System.out.println("                 2. Test product responses for all possible plots of the first variable in each dataset");
//    	System.out.println("                 3. Does not exit on first error");
//    	System.out.println("                 4. Assume x and y dimensions always exist");
//    	System.out.println("       -Da=1     Test product responses for all possible plots of all variables in each dataset");  	
//    	System.out.println("       -Dc=1     Only test dataset connections");
//    	System.out.println("       -Dd=URL   Only test dataset with the given URL");
//    	System.out.println("       -Ddds=1   Show DDS on console");
//    	System.out.println("       -De=1     Exit on first error");
//        System.out.println("       -Df=1     Only test F-TDS URLs of this LAS server");
//    	System.out.println("       -Dh=1     Show usage");
//    	System.out.println("       -Dv=view  Only test a certain view; view could be x,y,z,t,xy,xz,xt,yz,yt,zt");
//    	System.out.println("                 Being ignored if such view does not exist for a variable");
//    	System.out.println("       -Dr=1     Only test product responses");
//        System.out.println("       -Dvb=1    Verbose output of error message");
//    }
//
//     public void showUsage2(){
//        System.out.println("Usage 1: ant lastest [-Dh=1] ");
//        System.out.println("         -Dh=1     Show usage");
//        System.out.println();
//        System.out.println("Usage 2: ant lastest -Dc=1 [-Ddds=1]");
//        System.out.println("         -Dc=1     Only test OPeNDAP URLs used by datasets in this LAS");
//        System.out.println("         -Ddds=1   Show DDS on console");
//        System.out.println();
//        System.out.println("Usage 3: ant lastest -Df=1");
//        System.out.println("         -Df=1     Only test F-TDS URLs provided by this LAS server");
//        System.out.println();
//        System.out.println("Usage 4: ant lastest -Dr=1 [-Da=1][-Dd=1][-De=1][-Dv=view][-Dvb=1]");
//        System.out.println("         -Dr=1     Only test product responses.");
//        System.out.println("                   Without other options:");
//        System.out.println("                   1. it tests product responses for all possible plots of the first"); 
//        System.out.println("                      variable in each dataset");
//        System.out.println("                   2. it does not exit on first error");
//        System.out.println("                   3. it assume that x and y dimensions always exist");
//        System.out.println("         -Da=1     Test product responses for all possible plots of all variables in each");
//        System.out.println("                   dataset");
//        System.out.println("         -Dd=str   Only test dataset whose URL contains this string");
//        System.out.println("         -De=1     Exit on first error");
//        System.out.println("         -Dv=view  Only test a certain view; view could be x,y,z,t,xy,xz,xt,yz,yt,zt");
//        System.out.println("                   Being ignored if such view does not exist for a variable");
//        System.out.println("         -Dvb=1    Verbose output of error message");
//    }

    
    public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public String getProductTime() {
		return productTime;
	}

	public void setProductTime(String productTime) {
		this.productTime = productTime;
	}

	public boolean showHelp(){
    	return help;
    }
    
    public void setView(String v){
    	view = v;
    }
    
    public String getView(){
    	return view;
    }
    
    public void setVerbose(){
        verbose = true;
    }

    public boolean isVerbose(){
        return verbose;
    }

    public void setDataset(String ds){
    	dataset = ds;
    }
    
    public String getDataset(){
    	return dataset;
    }
    
    public void setAllVariable(){
    	allVar = true;
    }
    
    public boolean allVariable(){
    	return allVar;
    }
    
    public void setExitFirst(){
    	exitFirst = true;
    }
    
    public boolean exitFirst(){
    	return exitFirst;
    }

    public void showOptions(){
        System.out.println("-Da  = "+ allVar);
        System.out.println("-Dc  = "+ testConnection);
        System.out.println("-Dd  = " + dataset);
        System.out.println("-Ddds= " + dds);
        System.out.println("-De  = "+ exitFirst);
        System.out.println("-Df  = "+ testFTDS);
        System.out.println("-Dh  = "+ help);
        System.out.println("-Dc  = "+ testConnection);
        System.out.println("-Dr  = "+ testResponse);
        System.out.println("-Dv  = " + view);
        System.out.println("-Dvb = "+ verbose);

    }

    public String getDregex() {
        return dregex;
    }

    public void setDregex(String dregex) {
        this.dregex = dregex;
    }

    public String getVregex() {
        return vregex;
    }

    public void setVregex(String vregex) {
        this.vregex = vregex;
    }
}
