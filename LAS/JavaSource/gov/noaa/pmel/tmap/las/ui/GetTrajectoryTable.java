package gov.noaa.pmel.tmap.las.ui;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.erddap.util.ERDDAPUtil;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;
import gov.noaa.pmel.tmap.las.product.request.ProductRequest;
import gov.noaa.pmel.tmap.las.product.server.LASAction;
import gov.noaa.pmel.tmap.las.product.server.LASConfigPlugIn;
import gov.noaa.pmel.tmap.las.proxy.LASProxy;
import gov.noaa.pmel.tmap.las.util.Category;
import gov.noaa.pmel.tmap.las.util.Dataset;
import gov.noaa.pmel.tmap.las.util.Grid;
import gov.noaa.pmel.tmap.las.util.Variable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.GJChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.owasp.encoder.Encode;

import ucar.unidata.geoloc.LatLonPoint;
import ucar.unidata.geoloc.LatLonPointImpl;

import com.cohort.util.String2;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

public class GetTrajectoryTable extends LASAction {

	private static String ERROR = "error";

	public String execute() throws Exception {	
	   
        Chronology chrono = GJChronology.getInstance(DateTimeZone.UTC);
	    
	    DateTimeFormatter longForm = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withChronology(chrono).withZone(DateTimeZone.UTC);
	    DateTimeFormatter mediumForm = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withChronology(chrono).withZone(DateTimeZone.UTC);
	    DateTimeFormatter shortForm = DateTimeFormat.forPattern("yyyy-MM-dd").withChronology(chrono).withZone(DateTimeZone.UTC);
	    DateTimeFormatter shortFerretForm = DateTimeFormat.forPattern("dd-MMM-yyyy").withChronology(chrono).withZone(DateTimeZone.UTC);
	    DateTimeFormatter mediumFerretForm = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm").withChronology(chrono).withZone(DateTimeZone.UTC);
	    DateTimeFormatter longFerretForm = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss").withChronology(chrono).withZone(DateTimeZone.UTC);
	    
        DateTimeFormatter iso_fmt = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();
        
	    LASProxy lasProxy = new LASProxy();
	    LASConfig lasConfig = (LASConfig) contextAttributes.get(LASConfigPlugIn.LAS_CONFIG_KEY);
	    String xml = request.getParameter("xml");
	    String dsid;
	    String xlo = null;
	    String xhi = null;
	    String ylo = null;
	    String yhi = null;
	    String zlo = null;
	    String zhi = null;
	    String tlo = null;
	    String thi = null;
	    
	    InputStream input = null;
	    
	    String catid = request.getParameter("catid");
	    		
	    LASUIRequest lasUIRequest = (LASUIRequest) request.getAttribute("las_request");
	    if ( lasUIRequest != null ) {

	        dsid = lasUIRequest.getDatasetIDs().get(0);
	        xlo = lasUIRequest.getXlo();
	        xhi = lasUIRequest.getXhi();
	        ylo = lasUIRequest.getYlo();
	        yhi = lasUIRequest.getYhi();
	        zlo = lasUIRequest.getZlo();
	        zhi = lasUIRequest.getZhi();
	        tlo = lasUIRequest.getTlo();
	        thi = lasUIRequest.getThi();

	    } else {
	        dsid = request.getParameter("dsid");
	    }
	    if ( dsid != null ) {
	        ProductRequest productRequest;
	        try {
	            productRequest = new ProductRequest(lasConfig, lasUIRequest, "false", "ANY");
	            Element operationElement = lasConfig.getElementByXPath(lasUIRequest.getOperationXPath());
	            productRequest.makeRequest(operationElement, lasConfig, lasUIRequest, "false", "ANY");
	        } catch ( LASException e ) {
	            logerror(request, e.getMessage(), e);
	            return ERROR;
	        } catch ( UnsupportedEncodingException e) {
	            logerror(request, "Error creating the product request.", e);
	            return ERROR;
	        } catch ( JDOMException e) {
	            logerror(request, "Error creating the product request.", e);
	            return ERROR;
	        }
	        List<LASBackendRequest> reqs = productRequest.getRequestXML();
	        LASBackendRequest backRequest = reqs.get(0);
	        
	        // lasRequest below try to be backRequest
	        
	        Dataset dataset = lasConfig.getFullDataset(dsid);
	        Map<String, Map<String, String>> properties = dataset.getPropertiesAsMap();
	        String baseurl = lasConfig.getBaseServerURL();
                if ( !baseurl.endsWith("/") ) {
                    baseurl = baseurl  + "/";
                }
                // Put back the thing the LASConfig method just stripped off.
                baseurl = baseurl + "ProductServer.do";
	        //String context = baseurl.substring(baseurl.lastIndexOf("/"), baseurl.length());
	        //baseurl = context + "/" + "ProductServer.do";
	        if ( properties != null ) {


                Map<String, String> fp = properties.get("ferret");
	            String is = null;
                    if ( fp != null ) {
                       is = fp.get("is_socat");
                    }
                    boolean socat = false;
                    if ( is != null && !is.equals("") ) {
                        socat = true;
                    }
	            Map<String, String> tabledap = properties.get("tabledap_access");
	            String varid = null;
	            if ( tabledap != null ) {
	                String table = tabledap.get("table_variables");
                    String document_base = tabledap.get("document_base");
                    String id = tabledap.get("id");
                    // String did = tabledap.get("decimated_id");
                    // Ignore the decimated data set for this operation.
                    String did = null;
                    String cruise_id = tabledap.get("trajectory_id");
                    String profile_id = tabledap.get("profile_id");
                    String timeseries_id = tabledap.get("timeseries_id");
                    String all_variables = tabledap.get("all_variables");
                    String depth_name = tabledap.get("altitude");
                    List<Variable> vars = dataset.getVariables();
                    Grid grid = null;
                    if ( all_variables != null && all_variables.length() > 0 ) {
                       
                        for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
                            Variable variable = (Variable) varIt.next();
                            String shortname = lasConfig.getVariableName(variable.getDSID(), variable.getID());
                            if ( all_variables.contains(shortname) ) {
                                varid = variable.getID();
                                grid = lasConfig.getGrid(dsid, varid);
                                break;
                            }
                        }
                        
                    }
                    if ( vars.size() > 0 && grid != null ) {

                        String gridxlo = grid.getAxis("x").getLo();
                        String gridxhi = grid.getAxis("x").getHi();
                        String gridylo = grid.getAxis("y").getLo();
                        String gridyhi = grid.getAxis("y").getHi();
                        String gridtlo = grid.getTime().getLo();
                        String gridthi = grid.getTime().getHi();
                        DateTime dtl = null;
                        try {
                            dtl = longForm.parseDateTime(gridtlo).withZone(DateTimeZone.UTC);
                        } catch (Exception e) {
                            try {
                                dtl = mediumForm.parseDateTime(gridtlo).withZone(DateTimeZone.UTC);
                            } catch (Exception e2) {
                                dtl = shortForm.parseDateTime(gridtlo).withZone(DateTimeZone.UTC);
                            }
                        }
                        DateTime dth = null;
                        try {
                            dth = longForm.parseDateTime(gridthi).withZone(DateTimeZone.UTC);
                        } catch (Exception e3) {
                            try {
                                dth = mediumForm.parseDateTime(gridthi).withZone(DateTimeZone.UTC);
                            } catch (Exception e) {
                                dth= shortForm.parseDateTime(gridthi).withZone(DateTimeZone.UTC);
                            }
                        }
                        
                        String gridtlo_ferret = mediumFerretForm.print(dtl.getMillis());
                        String gridthi_ferret = mediumFerretForm.print(dth.getMillis());
                        

                        String dsg_id = null;
                        String type = null;


                        if ( cruise_id != null && cruise_id.trim().length() > 0 ) {
                            dsg_id = cruise_id;
                            type = "trajectory";
                        } else if ( profile_id != null && profile_id.trim().length() > 0 ) {
                            dsg_id = profile_id;
                            type = "profile";
                        } else if ( timeseries_id != null && timeseries_id.trim().length() > 0 ) {
                            dsg_id = timeseries_id;
                            type = "timeseries";
                        }
                        String lon_domain = tabledap.get("lon_domain");
                        StringBuilder xquery2 = new StringBuilder();
                        StringBuilder xquery1 = new StringBuilder();
                        StringBuilder dsgQuery = new StringBuilder();
                        StringBuilder csvQuery = new StringBuilder();


                        String dataurl = lasConfig.getDataAccessURL(dsid, varid, false);

                        if ( dataurl != null && !dataurl.equals("") ) {
                            if ( dataurl.contains("#") ) dataurl = dataurl.substring(0, dataurl.indexOf("#"));
                            if (!dataurl.endsWith("/") ) dataurl = dataurl + "/";

                            // Use the full data for the downloads.
                            String fulldataurl = dataurl + id;

                            boolean smallarea = false;

                            if ( lon_domain.contains("180") ) {
                                if (xlo != null && xhi != null && xlo.length() > 0 && xhi.length() > 0 ) {

                                    double xhiDbl = String2.parseDouble(xhi);
                                    double xloDbl = String2.parseDouble(xlo);
                                    // Check the span before normalizing and if it's big, just forget about the lon constraint all together.
                                    if ( Math.abs(xhiDbl - xloDbl ) < 358. ) {

                                        // This little exercise will normalize the x values to -180, 180.
                                        LatLonPoint p = new LatLonPointImpl(0, xhiDbl);
                                        xhiDbl = p.getLongitude();
                                        p = new LatLonPointImpl(0, xloDbl);
                                        xloDbl = p.getLongitude();

                                        double xspan = Math.abs(xhiDbl - xloDbl);
                                        double yloDbl = -90.d;
                                        double yhiDbl = 90.d;
                                        if (ylo != null && ylo.length() > 0) {
                                            yloDbl = Double.valueOf(ylo);
                                        }
                                        if (yhi != null && yhi.length() > 0) {
                                            yhiDbl = Double.valueOf(yhi);
                                        }
                                        double yspan = Math.abs(yhiDbl - yloDbl);

                                        double fraction = ((xspan+yspan)/(360.d + 180.d));

                                        if ( fraction < .1d ) {
                                            smallarea = true;
                                        }
                                    }
                                }
                            }

                            // If available use the decimated URL for the other requests if the area is NOT small.
                            if ( did != null && !did.equals("") && !smallarea ) {
                                id = did;
                            }
                            dataurl = dataurl + id;
                            String lonQ2 = null;
                            if ( id != null && !id.equals("") ) {

                                String url = dataurl + ".csv";
                                String dsgurl = fulldataurl+".ncCF";
                                String csvurl = fulldataurl + ".csv";

                                if ( document_base != null && !document_base.endsWith("/") ) document_base = document_base + "/";
                                if ( table != null ) {
                                    url = url + "?" + table;
                                    response.setContentType("application/xhtml+xml");
                                    Document doc = new Document();

                                    String query = "";

                                    Map<String, String> dt = dataset.getPropertiesAsMap().get("tabledap_access");  
                                    boolean is360 = false;
                                    if ( dt != null ) {
                                        String range = dt.get("lon_domain");
                                        is360 = !range.contains("180");
                                    }

                                    try {

                                        List<String> lonQueries = ERDDAPUtil.getLongitudeQuery(is360, xlo, xhi);
                                        String lonQ1 = lonQueries.get(0);
                                        
                                        if ( lonQueries.size() > 1 ) {
                                            lonQ2 = lonQueries.get(1);
                                        }
                                        xquery1.append(lonQ1);
                                        if ( lonQ2 != null ) {
                                            xquery2.append(lonQ2);
                                        }


                                    } catch (Exception e2) {
                                        // live without x constraints.
                                    }


                                    if ( ylo != null && !ylo.equals("") ) {
                                        query = query + "&"+URLEncoder.encode("latitude>="+ylo, StandardCharsets.UTF_8.name());
                                    }
                                    if ( yhi != null && !yhi.equals("") ) {
                                        query = query + "&"+URLEncoder.encode("latitude<="+yhi, StandardCharsets.UTF_8.name());
                                    }
                                    if ( zlo != null && !zlo.equals("") ) {
                                        query = query + "&"+URLEncoder.encode(depth_name+">="+zlo, StandardCharsets.UTF_8.name());
                                    }
                                    if ( zhi != null && !zhi.equals("") ) {
                                        query = query + "&"+URLEncoder.encode(depth_name+"<="+zhi, StandardCharsets.UTF_8.name());
                                    }
                                    if ( tlo != null && !tlo.equals("") ) {
                                        DateTime dlo;
                                        try {
                                            dlo = mediumFerretForm.parseDateTime(tlo);
                                        } catch (Exception e) {
                                            try {
                                                dlo = shortFerretForm.parseDateTime(tlo);
                                            } catch (Exception e1) {
                                                logerror(request, "Error parsing dates...", e);
                                                return ERROR;
                                            }
                                        }
                                        String dtlo = iso_fmt.print(dlo.getMillis());
                                        query = query + "&"+URLEncoder.encode("time>=\""+dtlo+"\"", StandardCharsets.UTF_8.name());
                                    }
                                    if ( thi != null && !thi.equals("") ) {
                                        DateTime dhi;
                                        try {
                                            dhi = mediumFerretForm.parseDateTime(thi);
                                        } catch (Exception e) {
                                            try {
                                                dhi = shortFerretForm.parseDateTime(thi);
                                            } catch (Exception e1) {
                                                logerror(request, "Error parsing dates...", e);
                                                return ERROR;
                                            }
                                        }
                                        String dthi = iso_fmt.print(dhi.getMillis());
                                        query = query + "&"+URLEncoder.encode("time<=\""+dthi+"\"", StandardCharsets.UTF_8.name());
                                    }


                                    List constraints = backRequest.getConstraints();
                                    // The queries made here are not encode, but eh query encodes itself.
                                    String encodedConstraints = ERDDAPUtil.getConstraintQuery(constraints);



                                    // If there is only 1 x query, we're done...
                                    int xlimit = 1;
                                    List<String> previouslySent = new ArrayList<String>();
                                    if ( xquery2.length() > 0 && lonQ2 != null ) {
                                        // If there are two, we have to do two queries, save the results from the first to eliminate dups

                                        xlimit = 2;
                                    }



                                    for (int l = 0; l < xlimit; l++ ) {

                                        String fullquery = "";
                                        if ( l == 0 ) {
                                            fullquery = query + xquery1.toString();
                                        } else {
                                            fullquery = query + xquery2.toString();
                                        }


                                        fullquery = fullquery + "&distinct()";
                                        
                                        // Each of these pieces has been encoded as it was constructed
                                        fullquery = fullquery+encodedConstraints;
                                        url = url + fullquery;
                                        input = lasProxy.executeGetMethodAndReturnStream(url, response);
                                        OutputStream output = response.getOutputStream();

                                        BufferedWriter bsw = new BufferedWriter(new OutputStreamWriter(output));

                                        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
                                                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"+
                                                "<html xmlns=\"http://www.w3.org/1999/xhtml\">"+
                                                "<head>"+
                                                "  <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />"+
                                                "  <title>Table of Datasets</title>"+
                                                "  <script src=\"JavaScript/frameworks/jquery-1.11.1.min.js\" type=\"text/javascript\"></script>"+
                                                "</head>"+
                                                "<body style=\"color:black; background:white; font-family:Arial,Helvetica,sans-serif; font-size:85%; line-height:130%;\">"+
                                                "<table border=\"2\" cellpadding=\"4\" cellspacing=\"0\">";

                                        bsw.write(header);

                                        if ( input != null ) {
                                            BufferedReader bsr = new BufferedReader(new InputStreamReader(input));
                                            // The regex comes from the geniuses at: 
                                            // http://stackoverflow.com/questions/1757065/splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
                                            // Holy mother...


                                            // Process the column names.
                                            String line = bsr.readLine();
                                            String[] titles = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                                            StringBuilder columnHeaders = new StringBuilder();
                                            columnHeaders.append("<tr>\n");

                                            for (int i = 0; i < titles.length; i++) {
                                                columnHeaders.append("\n<th>"+titles[i]+"</th>\n");
                                            }
                                            if ( socat ) {
                                                columnHeaders.append("<th>documentation</th>\n");
                                            }
                                            columnHeaders.append("<th>download</th>\n");
                                            if ( ! socat ) {
                                                // Do not bother adding start and end dates in SOCAT - makes the table too wide
                                                columnHeaders.append("<th>start</th>\n");
                                                columnHeaders.append("<th>end</th>\n");
                                            }
                                            else {
                                                columnHeaders.append("<th>crossovers</th>\n");
                                                columnHeaders.append("<th>qc flags</th>\n");
                                                columnHeaders.append("<th>thumbnails</th>\n");
                                            }


                                            columnHeaders.append("</tr>\n");
                                            bsw.write(columnHeaders.toString());

                                            // Process the units.
                                            line = bsr.readLine();
                                            String[] units = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                                            StringBuilder unitStrings = new StringBuilder();
                                            unitStrings.append("<tr>\n");
                                           

                                            for (int i = 0; i < units.length; i++) {
                                                unitStrings.append("<th>"+units[i]+"</th>\n");
                                            }
                                            if ( socat ) {
                                                unitStrings.append("<th></th>\n");
                                            }
                                            unitStrings.append("<th></th>\n");
                                            if ( ! socat ) {
                                                // Do not bother adding start and end dates in SOCAT - makes the table too wide
                                                unitStrings.append("<th></th>\n");
                                                unitStrings.append("<th></th>\n");
                                            }
                                            else {
                                                unitStrings.append("<th></th>\n");
                                                unitStrings.append("<th></th>\n");
                                                unitStrings.append("<th></th>\n");
                                            }
                                            unitStrings.append("</tr>\n");
                                            bsw.write(unitStrings.toString());

                                            int index = 1;
                                            line = bsr.readLine();

                                            while ( line != null ) {

                                                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                                                if ( !previouslySent.contains(parts[0])) {
                                                    previouslySent.add(parts[0]);
                                                    StringBuilder row = new StringBuilder();
                                                    if ( index%2 == 0 ) {
                                                        row.append("<tr bgcolor=\"#FFFFFF\">");
                                                    } else {
                                                        row.append("<tr bgcolor=\"#F3E7C9\">");
                                                    }

                                                    for (int i = 0; i < parts.length; i++) {
                                                        row.append("<td nowrap=\"nowrap\" colspan=\"1\">"+parts[i]+"</td>\n");
                                                    }
                                                    dsgQuery.append("?&amp;"+cruise_id+"=\""+parts[0]+"\"");
                                                    csvQuery.append("?&amp;"+cruise_id+"=\""+parts[0]+"\"");
                                                    if ( socat ) {
                                                        row.append("<td nowrap=\"nowrap\" colspan=\"1\"><a target=\"_blank\" href=\""+document_base+parts[0].substring(0,4)+"/"+parts[0]+"/\">Documentation</a>"+"</td>\n");
                                                    }

                                                    LASUIRequest download = (LASUIRequest) lasUIRequest.clone();
                                                    download.addVariableOldXPath(dsid, varid);
                                                    download.changeOperation("Trajectory_Interactive_Download");
                                                    download.setRange("x", gridxlo, gridxhi);
                                                    download.setRange("y", gridylo, gridyhi);
                                                    download.setRange("t", gridtlo_ferret, gridthi_ferret);
                                                    download.addTextConstraint(dsg_id, "is", parts[0], null);

                                                    String ps = baseurl + "?catid="+catid+"&amp;dsid="+dsid+"&amp;xml="+download.toEncodedURLString();

                                                    row.append("<td nowrap=\"nowrap\" colspan=\"1\"><a target=\"_blank\" href='"+ps+"'>Save As...</a></td>\n");


                                                    //row.append("<td nowrap=\"nowrap\" colspan=\"1\"><a href='"+dsgurl+dsgQuery.toString()+"'>netcdf</a>"+" || "+"<a href='"+csvurl+csvQuery.toString()+"'>csv</a>"+"</td>\n");

                                                    if ( ! socat ) {
                                                        // Do not bother adding start and end dates in SOCAT - makes the table too wide
                                                        dsgQuery.setLength(0);
                                                        csvQuery.setLength(0);
                                                        InputStream stream = null;
                                                        InputStreamReader reader = null;
                                                        // Call out to ERDDAP for the lat/lon/time box.
                                                        try {

                                                            JsonStreamParser jp = null;

                                                            String timeurl = dataurl + ".json?"+URLEncoder.encode(titles[0]+",time,latitude,longitude&"+titles[0]+"=\""+parts[0]+"\"&distinct()&orderByMinMax(\"time\")", "UTF-8");
                                                            stream = null;

                                                            stream = lasProxy.executeGetMethodAndReturnStream(timeurl, response);

                                                            reader = new InputStreamReader(stream);
                                                            jp = new JsonStreamParser(reader);
                                                            JsonObject timebounds = (JsonObject) jp.next();
                                                            JsonArray timeminmax = getMinMax(timebounds);
                                                            reader.close();
                                                            row.append("<td nowrap=\"nowrap\" colspan=\"1\">"+timeminmax.get(0).getAsString()+"</td>");
                                                            row.append("<td nowrap=\"nowrap\" colspan=\"1\">"+timeminmax.get(1).getAsString()+"</td>");
                                                            // Call out to ERDDAP for all the CRUISES in the same lat/lon/time box.
                                                        } catch ( Exception e ) {
                                                            if (reader != null ) 
                                                                reader.close();
                                                            row.append("<td nowrap=\"nowrap\" colspan=\"1\">Unable to load time min.</td>");
                                                            row.append("<td nowrap=\"nowrap\" colspan=\"1\">Unable to load time max.</td>");
                                                        } finally {
                                                    	    if ( stream != null )
                                                    		    stream.close();
                                                        }
                                                    }
                                                    else {
                                                    	row.append("\n<td id=\""+parts[0]+"\" nowrap=\"nowrap\" colspan=\"1\">");
                                                    	// Add the link to load a list of potential crosses to the table.
                                                    	row.append("<a href=\"javascript:$(\'#"+parts[0]+"\').html('&lt;div&gt;checking...&lt;/div&gt;');$(\'#"+parts[0]+"\').load(\'getCrossovers.do?catid="+catid+"&amp;dsid="+dsid+"&amp;tid="+parts[0]+"\');void(0);\">Check for crossovers</a>");
                                                    	row.append("\n</td>");

                                                    	// Add the QC link
                                                    	LASUIRequest qcRequest = new LASUIRequest();
                                                    	qcRequest.addVariable(dsid, varid);
                                                    	qcRequest.setOperation("SOCAT_QC_table");
                                                    	qcRequest.setProperty("qc", cruise_id, parts[0]);
                                                    	qcRequest.setProperty("las", "output_type", "xml");
                                                    	row.append("\n<td id=\""+parts[0]+"\" nowrap=\"nowrap\" colspan=\"1\">");
                                                    	// Add the link to load a list of potential crosses to the table.
                                                    	String qc_url = "ProductServer.do?xml="+qcRequest.toEncodedURLString();

                                                    	row.append("<a target=\"_blank\" href=\""+qc_url+"\">Examine QC Flags</a>");
                                                    	row.append("\n</td>");

                                                    	// ADD a THUMBNAIL table link...
                                                    	LASUIRequest thumb = (LASUIRequest) lasUIRequest.clone();
                                                    	thumb.removeLinks();
                                                    	thumb.setOperation("PropPropThumbTable");
                                                    	
                                                    	// Remove other cruise ID constraints.
                                                    	thumb.removeTextConstraintByLHS(cruise_id);
                                                    	
                                                    	thumb.addTextConstraint(cruise_id, "eq", parts[0], null);
                                                    	String thumburl = "ProductServer.do?catid="+catid+"&amp;xml="+thumb.toEncodedURLString();
                                                    	row.append("\n<td id=\""+parts[0]+"\" nowrap=\"nowrap\" colspan=\"1\">");
                                                    	row.append("<a target=\"_blank\" href=\""+thumburl+"\">Thumbnails</a>");
                                                    	row.append("\n</td>");


                                                    }	             



                                                    row.append("</tr>");
                                                    bsw.write(row.toString());
                                                }
                                                line = bsr.readLine();
                                                index++;
                                            }

                                            bsw.write("</table>\n</body>\n</html>");

                                            input.close();

                                            bsw.flush();
                                            bsw.close();
                                            bsr.close();

                                        }
                                    }
                                } else {
                                    logerror(request, "Unable to fetch information from server...","query = "+JDOMUtils.decode(url, "UTF-8"));
                                    return ERROR;
                                }
                            }
                        }
                    }
	            }
	        }
	    }
	    return null;
	}
	private JsonArray getMinMax(JsonObject bounds) {
	    JsonArray rows = (JsonArray) ((JsonObject) (bounds.get("table"))).get("rows");
        JsonArray row1 = (JsonArray) rows.get(0);
        JsonArray row2 = (JsonArray) rows.get(1);
        
        String min = ((JsonElement) row1.get(1)).getAsString();
        String max = ((JsonElement) row2.get(1)).getAsString();
        JsonArray minmax = new JsonArray();
        minmax.add(new JsonPrimitive(min));
        minmax.add(new JsonPrimitive(max));
        return minmax;
	}
	public static String prepareURL(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		String dsid = request.getParameter("dsid");
		String catitem = request.getParameter("catitem");
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

}
