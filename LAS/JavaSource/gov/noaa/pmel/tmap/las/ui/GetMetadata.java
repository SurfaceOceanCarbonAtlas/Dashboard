package gov.noaa.pmel.tmap.las.ui;

import java.io.UnsupportedEncodingException;

import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;
import gov.noaa.pmel.tmap.las.product.server.LASAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class GetMetadata extends LASAction {
     public static String TEMPLATE = "template";
     public String template;
     public  String catitem;
     public String file;
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}

	public String execute() throws Exception {
		response.sendRedirect("ProductServer.do?xml="+prepareURL(request, response));
		return null;
	}
	public String prepareURL(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		String dsid = request.getParameter("dsid");
		catitem = request.getParameter("catitem");
		
		String opendap = request.getParameter("opendap");
		if ( catitem != null ) dsid = catitem;
		LASUIRequest ui_request = new LASUIRequest();
		ui_request.addVariable(dsid, "dummy");
		if ( opendap != null ) {
		   ui_request.setOperation("MetadataURLs");
		} else {
			ui_request.setOperation("Metadata");
		}
		return ui_request.toEncodedURLString();
	}
	public String getCatitem() {
		return catitem;
	}
	public void setCatitem(String catitem) {
		this.catitem = catitem;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}

}
