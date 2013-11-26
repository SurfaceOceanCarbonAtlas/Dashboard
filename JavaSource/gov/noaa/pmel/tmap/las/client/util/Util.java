package gov.noaa.pmel.tmap.las.client.util;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.rpc.RPCService;
import gov.noaa.pmel.tmap.las.client.rpc.RPCServiceAsync;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class Util {
    static ClientFactory clientFactory = GWT.create(ClientFactory.class);
    static EventBus eventBus = clientFactory.getEventBus();
	public static String getVariableXPATH(String dsid, String varid) {
		return "/lasdata/datasets/"+dsid+"/variables/"+varid;
	}
	public static String getDSID(String xpath) {
		return xpath.substring(xpath.indexOf("/lasdata/datasets/")+1, xpath.indexOf("/variables"));
	}
	public static String getVarID(String xpath) {
		return xpath.substring(xpath.indexOf("/variables/")+1, xpath.length());
	}
	public static List<String> setOrthoAxes(String view, GridSerializable grid) {
		List<String> ortho = new ArrayList<String>();
		if ( !view.contains("e") && grid.hasE() ) {
		    ortho.add("e");
		}
		if ( !view.contains("t") && grid.hasT() ) {
			ortho.add("t");
		}
		if ( !view.contains("z") && grid.hasZ() ) {
			ortho.add("z");
		}
		if ( !view.contains("y") && grid.hasY() ) {
			ortho.add("y");
		}
		if ( !view.contains("x") && grid.hasX() ) {
			ortho.add("x");
		}
		return ortho;
	}
	public static HashMap<String, String> getTokenMap(String token) {
	    token = URL.decode(token);
		String[] tokens = token.split(";");
		HashMap<String, String> tokenMap = new HashMap<String, String>();
		for( int i=0; i < tokens.length; i++ ) {
			if ( tokens[i].contains("=") ) {
				String[] parts = tokens[i].split("=");
				String name = parts[0];
				String value = parts[1];
				if ( !value.contains("ferret_") ) {
					tokenMap.put(name, value);
				}       ClientFactory clientFactory = GWT.create(ClientFactory.class);
		        EventBus eventBus = clientFactory.getEventBus();
			}
		}
		return tokenMap;
	}
	public static HashMap<String, String> getOptionsMap(String token) {
		if ( token.startsWith(";") ) token = token.substring(1, token.length());
		if ( token.endsWith(";") ) token = token.substring(0, token.length()-1);
		String[] tokens = token.split(";");
		HashMap<String, String> optionsMap = new HashMap<String, String>();
		for( int i=0; i < tokens.length; i++ ) {
			String[] parts = tokens[i].split("=");
			String name = parts[0];
			String value = parts[1];
			if ( name.contains("ferret_") ) {
			    optionsMap.put(name.substring(7, name.length()), value);
			}
		}
		return optionsMap;
	}
	public static RPCServiceAsync getRPCService() {
		RPCServiceAsync rpcService = (RPCServiceAsync) GWT.create(RPCService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) rpcService;        
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
        EventBus eventBus = clientFactory.getEventBus();
		String rpcURL = "";
		String base_path = URLUtil.getBaseURL();
		rpcURL = base_path + "rpc";
		endpoint.setServiceEntryPoint(rpcURL);
		return rpcService;
	}
	public static String getProductServer() {
		return URLUtil.getBaseURL() + "ProductServer.do";
	}
	public static String[] getParameterStrings(String name) {
		Map<String, List<String>> parameters = Window.Location.getParameterMap();
		List param = parameters.get(name);
		if ( param != null ) {
		    int i = 0;
		    String[] ps = new String[param.size()];
		    for ( Iterator paramIt = param.iterator(); paramIt.hasNext(); ) {
                String p = (String) paramIt.next();
                ps[i] = p;
                i++;
            }
			return ps;
		}
		return null;
	}
	public static String getParameterString(String name) {
		Map<String, List<String>> parameters = Window.Location.getParameterMap();			
		List param = parameters.get(name);
		if ( param != null ) {
			return (String) param.get(0);
		}
		return null;
	}
	public static String getAnnotationService(String annUrl) {
		String file = annUrl.substring(annUrl.lastIndexOf("/")+1, annUrl.length());
		return URLUtil.getBaseURL() + "getAnnotations.do?file="+file;
	}
	public static String getAnnotationsService(String annUrl, String imageUrl) {
		String url = getAnnotationService(annUrl);
		String image = imageUrl.substring(imageUrl.lastIndexOf("/")+1, imageUrl.length());
		return url+"&image="+image+"&template=image_w_annotations.vm";
	}
	public static String getAnnotationsFrag(String annUrl, String imageUrl) {
		String file = annUrl.substring(annUrl.lastIndexOf("/")+1, annUrl.length());
		String image = imageUrl.substring(imageUrl.lastIndexOf("/")+1, imageUrl.length());
		return "file="+file+"&image="+image;
	}
	 public static native String getTileServer()
	    /*-{
	        if ($wnd.OL_map_widget_tile_server == undefined) {
	            return "";
	        } else {
	            return $wnd.OL_map_widget_tile_server;
	        }
	    }-*/;
}
