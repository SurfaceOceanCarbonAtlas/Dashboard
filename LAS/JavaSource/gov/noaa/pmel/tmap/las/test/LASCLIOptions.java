package gov.noaa.pmel.tmap.las.test;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class LASCLIOptions extends Options {
	
	public LASCLIOptions() {
		super();
		
		addOption("h", "help", false, "Show help.");
		
		addOption("f", "ftds", false, "Test F-TDS URLs.");
		
		addOption("c", "connection", false, "Test the data set connections via the file or OPeNDAP URL.");
		addOption("D", "DDS", false, "used with -c to print each DDS.");
		
		addOption("r", "responses", false, "Test product responses.");
		addOption("a", "all", false, "Test all products");
		addOption("e", "exit", false, "Exit on first error.");
		addOption("V", "verbose", false, "Verbose output.");
		addOption("h", "help", false, "Print help message.");
		addOption("u", "url", true, "Test only data URL's that contain thi string.");
		
		addOption("dregex", "dregex", true, "Test only data sets whose id matches this regular expression.");
		addOption("vregex", "vregex", true, "Test only variables whose id matches this regular expression.");
		
		Option view = new Option("v", "view", true, "Test only the specified view, one of x,y,z,t,xy,xz,xt,yz,yt,zt and xyt for in-situ.");
		view.setArgName("VIEW");
		addOption(view);
		
		Option dataset = new Option("d", "dataset", true, "Test only data sets that contain this string");
		dataset.setArgName("DATASET STRING");
		addOption(dataset);
		
		Option las = new Option("l", "LAS", true, "The base URL of the LAS to test (http://server.org/las)");
		las.setArgName("THE LAS URL");
		las.setRequired(true);
		addOption(las);
		
		
	}

}
