package gov.noaa.pmel.tmap.las.client.util;

import com.google.gwt.core.client.GWT;

public class URLUtil {
	public static String getImageURL() {
		return getBaseURL() + "images/";
	}
	public static String getBaseURL() {
		// TODO ~~ Warning ~~ very specific to our directory structure when deployed...
		String moduleRelativeURL = GWT.getModuleBaseURL();
		// Lop off the last "/"
		String base = moduleRelativeURL.substring(0, moduleRelativeURL.length()-1);
		// The component name...
		base = base.substring(0, base.lastIndexOf("/"));
		// The "components" sub-directory
		base = base.substring(0, base.lastIndexOf("/"));
		// The "JavaScript" sub-directory (keep the "/")
		base = base.substring(0, base.lastIndexOf("/")+1);
		return base;
	}
}
