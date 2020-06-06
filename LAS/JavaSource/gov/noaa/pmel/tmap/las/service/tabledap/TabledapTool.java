/**
 * TabledapTool
 */
package gov.noaa.pmel.tmap.las.service.tabledap;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.jdom.LASTabledapBackendConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.noaa.pmel.tmap.las.proxy.LASProxy;
import gov.noaa.pmel.tmap.las.service.TemplateTool;
import gov.noaa.pmel.tmap.las.service.database.IntermediateNetcdfFile;
import gov.noaa.pmel.tmap.las.util.Constraint;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import ucar.ma2.Array;
import ucar.ma2.ArrayBoolean;
import ucar.ma2.ArrayByte;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayLong;
import ucar.ma2.ArrayShort;
import ucar.ma2.ArrayString;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.StructureData;
import ucar.ma2.StructureMembers.Member;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;
import ucar.nc2.constants.FeatureType;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.ft.FeatureCollection;
import ucar.nc2.ft.FeatureDatasetFactoryManager;
import ucar.nc2.ft.FeatureDatasetPoint;
import ucar.nc2.ft.PointFeature;
import ucar.nc2.ft.PointFeatureIterator;
import ucar.nc2.ft.TrajectoryFeature;
import ucar.nc2.ft.TrajectoryFeatureCollection;
import ucar.nc2.time.CalendarDate;
import ucar.nc2.time.CalendarDateUnit;
import ucar.unidata.geoloc.LatLonPoint;
import ucar.unidata.geoloc.LatLonPointImpl;

import com.cohort.util.MustBe;
import com.cohort.util.String2;

/**
 * Creates an intermediate netCDF file from a Tabledap dataset
 * (http://coastwatch.pfel.noaa.gov/erddap/tabledap/index.html).
 *     
 * <p>The associated template will create two constraint expressions if needed to 
 * collect data from the two "edges" of the domain.
 * 
 *<pre>  
 *
 *             ------------------------------
 *             |                            |
 * xhi < xlo   |----x                 x-----|
 *             |                            |
 * xlo < xhi   |    x-----------------x     |
 *             |                            |
 *             ------------------------------
 *             
 *</pre>
 *  
 * @author Roland Schweitzer
 * @author Bob Simons (bob.simons@noaa.gov)
 *
 */
public class TabledapTool extends TemplateTool {
	
	Chronology chrono = GregorianChronology.getInstance(DateTimeZone.UTC);
	DateTimeFormatter ferretFormatHHmm = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm").withChronology(chrono).withZone(DateTimeZone.UTC);
	DateTimeFormatter ferretFormatHHmmss = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss").withChronology(chrono).withZone(DateTimeZone.UTC);
	DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
	 
    LASProxy lasProxy = new LASProxy();

    LASTabledapBackendConfig tabledapBackendConfig;  //force its compilation
    
    String time;
    String cruiseid;
    String latname;
    String lonname;
    String zname;
    List<String> all = new ArrayList<String>();
    List<DataRow> datarows = new ArrayList<DataRow>();
    String downloadvars = "minimal";
    String decid;
    String id;
    boolean full = false;
    private final static Logger log = LoggerFactory.getLogger(TabledapTool.class.getName());
    
    /**
     * This default constructor uses the TemplateTool base class to initialize all
     * of its temporary and template directories.  
     * It also reads the config file for this service.
     *
     * @throws IOException
     * @throws LASException
     */
    public TabledapTool() throws IOException, LASException {
        
        super("tabledap", "TabledapBackendConfig.xml");
    }

    /**
     * This connects to the data source and creates the netCDF file for this request.
     *
     * @param lasBackendRequest  
     * @return lasBackendResponse. 
     *   If an error occurs, this calls lasBackendResponse.setError,
     *   then returns the LASBackendResponse object.
     *   This won't throw an Exception.
     */
    public LASBackendResponse run(LASBackendRequest lasBackendRequest) {

        log.debug("Entered TabledapTool.run method."); // debug

       
        //If exception occurs in try/catch block, 'causeOfError' was the cause.
        String causeOfError = "Unexpected error: "; 
        LASBackendResponse lasBackendResponse = new LASBackendResponse();
           
        //then standard check can be done any time
        if ( lasBackendRequest.isCanceled() ) {
        	lasBackendResponse.setError("ERDDAP data request canceled.");
            return lasBackendResponse;
        }
        
        try {      

        //get the name for the .nc results file
        causeOfError = "Unable to get backend request's resultsAsFile(netcdf): ";
        String netcdfFilename = lasBackendRequest.getResultAsFile("netcdf");
        log.debug("Got netcdf filename: " + netcdfFilename);
        required(netcdfFilename, causeOfError);

        //get url (almost always ending in "/tabledap/")
        causeOfError = "Could not get url from backend request: "; 
        String url = lasBackendRequest.getRootElement().getChild(
                "dataObjects").getChild("data").getAttributeValue("url");
        required(url, causeOfError);
        
        causeOfError = "Could not get id."; 
        id = getTabledapProperty(lasBackendRequest, "id");
        log.debug("Got id: " + id); 
        required(id, causeOfError);
        
       decid = getTabledapProperty(lasBackendRequest, "decimated_id");
        
        Map<String, String> ferret_prop = lasBackendRequest.getPropertyGroup("ferret");
        Map<String, String> download = lasBackendRequest.getPropertyGroup("download");
        
        if ( download != null ) {
            String dall = download.get("all_data");
            if ( dall != null ) {
                downloadvars = "standard";
            }
            String data_vars = download.get("data_variables");
            if ( "minimal".equals(data_vars) ||
                 "standard".equals(data_vars) ||
                 "everything".equals(data_vars) ) {
                downloadvars = data_vars;
            }
        }
        String full_option = ferret_prop.get("full_data");
        
        if ( full_option != null && full_option.equalsIgnoreCase("yes") ) {
            full = true;
        }
       
        

            
            String query = null;
            String query2 = null;
            // If there is no need for the second query, just do the thing and carry on...
            List<String> qs = makeERDDAPQuery(lasBackendRequest, ".ncCF");
            if ( qs.size() > 0 ) {
                query = qs.get(0);
                if ( qs.size() == 2 ) {
                    query2 = qs.get(1);
                }
            } else {
                throw new Exception("Could not build queries");
            }
            DateTime dt = new DateTime();
           
            if ( query2 == null ) {
                File temp_file = new File(netcdfFilename+".temp");


                try {
                    String q = URLEncoder.encode(query.toString(), "UTF-8").replaceAll("\\+", "%20").replaceAll("%3F", "?");;
                    String dsUrl = url + q;  //don't include ".dods"; readOpendapSequence does that
                   
                    dt = new DateTime();
                    log.debug("TableDapTool query="+dsUrl);
                    log.info("{TableDapTool starting file pull for the only file at "+fmt.print(dt));
                    lasProxy.executeERDDAPMethodAndSaveResult(dsUrl, temp_file, null);
                    dt = new DateTime();
                    if (lasBackendRequest.isCanceled()) {
                    	lasBackendResponse.setError("ERDDAP data request canceled.");
                        return lasBackendResponse;
                    }
                    log.info("{TableDapTool finished file pull for the only file at "+fmt.print(dt));
                    //was the request canceled?
                    if (lasBackendRequest.isCanceled()) {
                    	lasBackendResponse.setError("ERDDAP data request canceled.");
                        return lasBackendResponse;
                    }

                    temp_file.renameTo(new File(netcdfFilename));
                    dt = new DateTime();
                    log.info("Tabledap tool renamed the netcdf file to "+netcdfFilename+" at "+fmt.print(dt));
                } catch (Exception e) {
                    String message = e.getMessage();
                    if ( e.getMessage().contains("com.cohort") ) {
                        message = message.substring(message.indexOf("com.cohort.util.SimpleException: "), message.length());
                        message = message.substring(0, message.indexOf(")"));
                    }
                    if ( message.toLowerCase(Locale.ENGLISH).contains("query produced no matching") ) {
                        writeEmpty(netcdfFilename);
                    } else {
                        causeOfError = "Data source error: " + message;
                        throw new Exception(message);
                    }
                }
            } else {
                // We have to build our own netCDF file from the two queries.  In this case we will pull two DSG files make our own DSG ragged array file.
                boolean empty1 = false;
                boolean empty2 = false;
                File temp_file1 = new File(netcdfFilename+".1.temp");
                File temp_file2 = new File(netcdfFilename+".2.temp");
                String q1 = URLEncoder.encode(query.toString(), "UTF-8").replaceAll("\\+", "%20").replaceAll("%3F", "?");
                String dsUrl1 = url + q1;  //don't include ".dods"; readOpendapSequence does that
                String q2 = URLEncoder.encode(query2.toString(), "UTF-8").replaceAll("\\+", "%20").replaceAll("%3F", "?");
                String dsUrl2 = url + q2;  //don't include ".dods"; readOpendapSequence does that
                dt = new DateTime();
                log.debug("TableDapTool query="+dsUrl1);
                log.info("{TableDapTool starting file pull for file 1 at "+fmt.print(dt));

                try {
                	if (lasBackendRequest.isCanceled()) {
                    	lasBackendResponse.setError("ERDDAP data request canceled.");
                        return lasBackendResponse;
                	}
                    lasProxy.executeERDDAPMethodAndSaveResult(dsUrl1, temp_file1, null);
                    if (lasBackendRequest.isCanceled()) {
                    	lasBackendResponse.setError("ERDDAP data request canceled.");
                        return lasBackendResponse;
                    }
                } catch (Exception e) {
                    String message = e.getMessage();
                    if ( e.getMessage().contains("com.cohort") ) {
                        message = message.substring(message.indexOf("com.cohort.util.SimpleException: "), message.length());
                        message = message.substring(0, message.indexOf(")"));
                    }
                    if ( message.toLowerCase(Locale.ENGLISH).contains("query produced no matching") ) {
                        // one empty search
                        empty1 = true;
                    } else {
                        causeOfError = "Data source error: " + message;
                        throw new Exception(message);
                    }
                }
                dt = new DateTime();
                log.info("{TableDapTool finished file pull for the only file at "+fmt.print(dt));
                //was the request canceled?
                if (lasBackendRequest.isCanceled()) {
                	lasBackendResponse.setError("ERDDAP data request canceled.");
                    return lasBackendResponse;
                }
                log.debug("TableDapTool query="+dsUrl2);
                log.info("{TableDapTool starting file pull for file 2 at "+fmt.print(dt));
                try {
                    lasProxy.executeERDDAPMethodAndSaveResult(dsUrl2, temp_file2, null);
                } catch (Exception e) {
                    String message = e.getMessage();
                    if ( e.getMessage().contains("com.cohort") ) {
                        message = message.substring(message.indexOf("com.cohort.util.SimpleException: "), message.length());
                        message = message.substring(0, message.indexOf(")"));
                    }
                    if ( message.toLowerCase(Locale.ENGLISH).contains("query produced no matching" ) ) {
                        // two empty searches
                        empty2 = true;
                    } else {
                        causeOfError = "Data source error: " + message;
                        throw new Exception(message);
                    }
                }
                if ( empty1 && empty2 ) {
                    // two empty searches, write the empty file
                    writeEmpty(netcdfFilename);
                } else if ( empty1 && !empty2 ) {
                    temp_file2.renameTo(new File(netcdfFilename));
                } else if ( !empty1 && empty2 ) {
                    temp_file1.renameTo(new File(netcdfFilename));
                } else {
                    dt = new DateTime();
                    log.info("{TableDapTool finished file pull for the only file at "+fmt.print(dt));
                    //was the request canceled?
                    if (lasBackendRequest.isCanceled()) {
                    	lasBackendResponse.setError("ERDDAP data request canceled.");
                        return lasBackendResponse;
                    }
                    merge(netcdfFilename, temp_file1, temp_file2);
                    temp_file1.delete();
                    temp_file2.delete();
                }

            }


           


            // The service just wrote the file to the requested location so
            // copy the response element from the request to the response.
            causeOfError = "Failed to set response element: ";
            lasBackendResponse.addResponseFromRequest(lasBackendRequest);

        } catch (Exception e) {
            //System.out.println("TabledapTool is processing the exception.");
            log.warn(MustBe.throwableToString(e));
            lasBackendResponse.setError(causeOfError, e);
        }

        return lasBackendResponse;
    }
    public List<String> makeERDDAPQuery(LASBackendRequest lasBackendRequest, String type) throws Exception {
        
        List<String> qs = new ArrayList<String>();

        String causeOfError;
            

           

            causeOfError = "Could not get trajectory id from backend request: "; 
            
            String tid = getTabledapProperty(lasBackendRequest, "trajectory_id");
            String pid = getTabledapProperty(lasBackendRequest, "profile_id");
            String sid = getTabledapProperty(lasBackendRequest, "timeseries_id");
            
            cruiseid = null;
            
            if ( tid != null && !tid.equals("") ) {
            	cruiseid = tid;
            } else if ( pid != null && !pid.equals("") ) {
            	cruiseid = pid;
            } else if ( sid != null && !sid.equals("") ) {
            	cruiseid = sid;
            }
                       
            // We don't really need to require this, point data do not have one. 
            
            // We just need to deal with the fact it might be null.
            
            // required(cruiseid, causeOfError);

            String lon_domain = getTabledapProperty(lasBackendRequest, "lon_domain");
            
            causeOfError = "Could not get time column name from backend request: "; 
            time = getTabledapProperty(lasBackendRequest, "time");
            required(time, causeOfError);

            latname = getTabledapProperty(lasBackendRequest, "latitude");
            lonname = getTabledapProperty(lasBackendRequest, "longitude");
            zname = getTabledapProperty(lasBackendRequest, "altitude");
            String orderby = getTabledapProperty(lasBackendRequest, "orderby");
            String dummy = getTabledapProperty(lasBackendRequest, "dummy");
            List<String> modulo_vars = new ArrayList();
            
            String modulo_vars_comma_list = getTabledapProperty(lasBackendRequest, "modulo");
            if ( modulo_vars_comma_list != null ) {
                String[] mods = modulo_vars_comma_list.split(",");

                for (int i = 0; i < mods.length; i++) {
                    modulo_vars.add(mods[i].trim());
                }
            }
           
            //get "debug" file name, may be null or ""
            //if defined, use the "debug" resultsAsFile as the place to save the constraint statement.
            causeOfError = "Unable to getResultAsFile(debug): ";
            String constraintFileName = lasBackendRequest.getResultAsFile("debug"); 

            // What operation follows this data extract.
            String operationID = lasBackendRequest.getChainedOperation();
            
            //create the query.   First: variables
            StringBuilder query = new StringBuilder();
            
         
            
            ArrayList<String> vars = lasBackendRequest.getVariables();
            // If the operation is the prop-prop plot, we need all the variables.
            if ( operationID != null && operationID.equals("Trajectgory_thumbnails") ) {
                Map<String, String> thumbnail_properties = lasBackendRequest.getPropertyGroup("thumbnails");
                String all = thumbnail_properties.get("variable_names").trim();
                query.append(all);
            } else if (operationID != null && operationID.equals("Trajectgory_correlation")) {
                String all = getTabledapProperty(lasBackendRequest, "all_variables").trim();
                query.append(all);
            }  else if ( "standard".equals(downloadvars) ) {
                String all = getTabledapProperty(lasBackendRequest, "downloadall_variables").trim();
                // If the custom download all is not set, use the default.
                if ( all == null || all.equals("") ) {
                	all = getTabledapProperty(lasBackendRequest, "all_variables").trim();
                }
                
                query.append(all);
                
                for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
					String v = (String) varIt.next();
					if ( !all.contains(v) && !v.equals(latname) && !v.equals(lonname) && !v.equals(zname) && !v.equals(time) ) {
						query.append(","+v);
					}
				}
            }  else if ( "everything".equals(downloadvars) ) {
                String all = getTabledapProperty(lasBackendRequest, "all_variables").trim();
                
                query.append(all);
                
                for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
					String v = (String) varIt.next();
					if ( !all.contains(v) && !v.equals(latname) && !v.equals(lonname) && !v.equals(zname) && !v.equals(time) ) {
						query.append(","+v);
					}
				}
            }  else {
            	
                // Apparently ERDDAP gets mad of you include lat, lon, z or time in the list of variables so just list the "data" variables.
               
                
                // Only add the extras if the variable list does not come from configuration.
                // Some things might need something besides x,y,z and t in the file so...
                String extra_metadata = getTabledapProperty(lasBackendRequest, "extra_metadata").trim();
                if ( extra_metadata != null && !extra_metadata.equals("") ) {
                    if ( extra_metadata.contains(",") ) {
                        String[] extras = extra_metadata.split(",");
                        for (int i = 0; i < extras.length; i++) {
                            String e = extras[i].trim();
                            if ( query.indexOf(e) < 0 ) {
                                if ( query.length() > 0 && !query.toString().endsWith(",") ) {
                                    query.append(",");
                                }
                                vars.remove(e);
                                query.append(e);
                            }
                        }

                    } else {
                        if ( query.indexOf(extra_metadata) < 0 ) {
                            if ( query.length() > 0 && !query.toString().endsWith(",") ) {
                                query.append(",");
                            }
                            vars.remove(extra_metadata);
                            query.append(extra_metadata);
                        }
                    }
                }

                
                // If lat, lon and z are included as data variables, knock them out of this list.
                vars.remove(latname);
                vars.remove(lonname);
                vars.remove(zname);
                vars.remove(time);
                
                if ( operationID != null && (operationID.equals("Profile_2D_poly") || operationID.equals("Time_Series_Location_Plot") || operationID.equals("Trajectory_2D_poly")) ) {
                	String mapvars = getTabledapProperty(lasBackendRequest, "map_variables").trim();
                	 if ( mapvars != null && !mapvars.equals("") ) {
                         if ( mapvars.contains(",") ) {
                             String[] extras = mapvars.split(",");
                             for (int i = 0; i < extras.length; i++) {
                                 String e = extras[i].trim();
                                 if ( query.indexOf(e) < 0 ) {
                                     if ( query.length() > 0 && !query.toString().endsWith(",") ) {
                                         query.append(",");
                                     }
                                     vars.remove(e);
                                     query.append(e);
                                 }
                             }

                         } else {
                             if ( query.indexOf(mapvars) < 0 ) {
                                 if ( query.length() > 0 && !query.toString().endsWith(",") ) {
                                     query.append(",");
                                 }
                                 vars.remove(mapvars);
                                 query.append(mapvars);
                             }
                         }
                     }
                }
                String variables = "";
                for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
                	String variable = (String) varIt.next();
                	// Apparently ERDDAP gets mad if you list the trajectory_id in the request...
                	if ( cruiseid == null || (cruiseid != null && !variable.equals(cruiseid)) ) {
                		variables = variables+variable;
                		if (varIt.hasNext()) {
                			variables = variables + ",";
                		}
                	}
                }

                if ( variables.endsWith(",") ) {
                    variables = variables.substring(0, variables.length()-1);
                }

                if ( !variables.equals("") ) {
                    if ( query.length() > 0 && !query.toString().endsWith(",") ) {
                        query.append(",");
                    }
                    query.append(String2.replaceAll(variables, " ", ""));
                } else {
                    if ( query.length() > 0 && !query.toString().endsWith(",") ) {
                        query.append(",");
                    }
                    query.append(dummy);
                }
            }

            Map<String, Constraint> constrained_modulo_vars_lt = new HashMap<String, Constraint>();
            Map<String, Constraint> constrained_modulo_vars_gt = new HashMap<String, Constraint>();


            //then variable constraints  
            List constraintElements = lasBackendRequest.getRootElement().getChildren("constraint");
            
            // For now we will not use the decimated data set when there is any constraint applied to the request.
            // In the future we may need to distinguish between a sub-set variable constraint and a variable constraint.
            // The two types below should be enough to tell the difference.
            
            boolean hasConstraints = constraintElements.size() > 0;
            
            Iterator cIt = constraintElements.iterator(); 
            while (cIt.hasNext()) {
                Element constraint = (Element) cIt.next();
                String tType = constraint.getAttributeValue("type");
                if ( tType.equals("variable") ) {
                    
                    String rhsString = constraint.getChildText("rhs");
                    String lhsString = constraint.getChildText("lhs");
                    String opString = constraint.getChildText("op");  //some sort of text format
                    Constraint c = new Constraint(lhsString, opString, rhsString);    
                    query.append("&"+c.getAsString());  //op is now <, <=, ...
                    // Gather lt and gt constraint so see if modulo variable treatment is required.
                    if ( modulo_vars.contains(lhsString) && (opString.equals("lt") || opString.equals("le")) ) {
                        constrained_modulo_vars_lt.put(lhsString, c);
                    }
                    if ( modulo_vars.contains(lhsString) && (opString.equals("gt") || opString.equals("ge")) ) {
                        constrained_modulo_vars_gt.put(lhsString, c);
                    }
                } else if ( tType.equals("text") ) {
                    String rhsString = constraint.getChildText("rhs");
                    if ( rhsString != null && rhsString.equals("") ) {
                        rhsString = " ";
                    }
                    String lhsString = constraint.getChildText("lhs");
                    String opString = constraint.getChildText("op");  //some sort of text format
                    Constraint c = new Constraint(lhsString, opString, rhsString);          
                    query.append("&"+c.getAsERDDAPString());  //op is now <, <=, ...
                }
            }
            List<String> modulo_required = new ArrayList<String>();
            for (Iterator cvarIt = constrained_modulo_vars_lt.keySet().iterator(); cvarIt.hasNext();) {
                String cvar = (String) cvarIt.next();
                if ( constrained_modulo_vars_gt.keySet().contains(cvar)) {
                    // Potential for min to be > that max requiring a modulo treatment of the query.
                    String max = constrained_modulo_vars_lt.get(cvar).getRhs();
                    String min = constrained_modulo_vars_gt.get(cvar).getRhs();
                    try {
                        double mind = Double.valueOf(min);
                        double maxd = Double.valueOf(max);
                        if( mind > maxd ) {
                            modulo_required.add(cvar);
                        }
                    } catch (Exception e) {
                        // 
                    }
                }
            }


            //get region constraints 
            causeOfError = "Unable to get required database properties.";
            String xlo = lasBackendRequest.getXlo();  //don't constrain to +-180?  getDatabaseXlo?
            String xhi = lasBackendRequest.getXhi();  
            String ylo = lasBackendRequest.getYlo();
            String yhi = lasBackendRequest.getYhi();
            String zlo = lasBackendRequest.getZlo();
            String zhi = lasBackendRequest.getZhi();
            
            String s = lasBackendRequest.getTlo();  //in Ferret format
            String tlo = "";
            if ( s.length() > 0 ) {
            	s = JDOMUtils.decode(s, "UTF-8");
            	s = s.replace("\"","");
            	DateTime loDT = null;
            	try {
					loDT = ferretFormatHHmmss.parseDateTime(s);
				} catch (Exception e1) {
					try {
						loDT = ferretFormatHHmm.parseDateTime(s);
					} catch (Exception e2) {
						// Carry on to the null check below.
					}
				}
            	if ( loDT != null ) {
            		tlo = fmt.print(loDT);
            	} else {
            		throw new LASException("Could not parse date: "+s);
            	}
            }
            
            s = lasBackendRequest.getThi();  //in Ferret format
            String thi = "";
            if ( s.length() > 0 ) {
            	s = JDOMUtils.decode(s, "UTF-8");
            	s = s.replace("\"","");
            	DateTime hiDT = null;
            	try {
            		hiDT = ferretFormatHHmmss.parseDateTime(s);
            	} catch (Exception e1 ) {
            		try {
            			hiDT = ferretFormatHHmm.parseDateTime(s);
            		} catch (Exception e2) {
            			// Carry on to the null check.
            		}
            	}
            	if ( hiDT != null ) {
            		thi = fmt.print(hiDT);
            	} else {
            		throw new LASException("Could not parse date: "+s);
            	}
            }
           
            //add region constraints other than lon
            if (ylo.length() > 0) query.append("&"+latname+">=" + ylo);
            if (yhi.length() > 0) query.append("&"+latname+"<=" + yhi);
            if (zlo.length() > 0) query.append("&"+zname+">=" + zlo);
            if (zhi.length() > 0) query.append("&"+zname+"<=" + zhi);
            if (tlo.length() > 0) query.append("&"+time+">=" + tlo);
            if (thi.length() > 0) query.append("&"+time+"<=" + thi);
            
            if ( orderby != null ) {
                if ( !orderby.equals("") && !orderby.equals("none") ) {
                    query.append("&orderBy(\""+orderby+"\")");
                } else {
                    if ( !orderby.equals("none") && cruiseid != null ) {
                        query.append("&orderBy(\""+cruiseid+","+time+"\")");
                    } else {
                        query.append("&orderBy(\""+time+"\")");
                    }
                }
            } else {
            	if ( cruiseid != null ) {
            		query.append("&orderBy(\""+cruiseid+","+time+"\")");
            	} else {
                    query.append("&orderBy(\""+time+"\")");
            	}
            }
            

            //get the data   
            causeOfError = "Could not convert the data source to a netCDF file: ";   
            

            //            Table data = new Table();
         
            StringBuilder query2 = null;
            boolean smallarea = false;
            
            if ( lon_domain.contains("180") ) {
                if (xlo.length() > 0 && xhi.length() > 0 ) {

                    double xhiDbl = String2.parseDouble(xhi);
                    double xloDbl = String2.parseDouble(xlo);
                    // Check the span before normalizing and if it's big, just forget about the lon constraint all together.
                    if ( Math.abs(xhiDbl - xloDbl ) < 358. ) {
                        if ( modulo_required.size() > 0 ) {
                            causeOfError = "Cannot handle two modulo variables in the same request (longitude and "+modulo_required.get(0)+")";
                        }
                        
                        // This little exercise will normalize the x values to -180, 180.
                        LatLonPoint p = new LatLonPointImpl(0, xhiDbl);
                        xhiDbl = p.getLongitude();
                        p = new LatLonPointImpl(0, xloDbl);
                        xloDbl = p.getLongitude();

                        double xspan = Math.abs(xhiDbl - xloDbl);
                        double yloDbl = -90.d;
                        double yhiDbl = 90.d;
                        if (ylo.length() > 0) {
                            yloDbl = Double.valueOf(ylo);
                        }
                        if (yhi.length() > 0) {
                            yhiDbl = Double.valueOf(yhi);
                        }
                        double yspan = Math.abs(yhiDbl - yloDbl);

                        double fraction = ((xspan+yspan)/(360.d + 180.d));

                        if ( fraction < .1d ) {
                            smallarea = true;
                        }


                        // Now a wrap around from west to east should be have xhi < xlo;
                        if ( xhiDbl < xloDbl ) {
                            if ( xhiDbl < 0 && xloDbl >=0 ) {


                                // This should be true, otherwise how would to get into this situation unless you wrapped around the entire world and overlapped...


                                query2 = new StringBuilder(query.toString());
                                // Get the "left" half.  The section between -180 and xhi
                                xhiDbl = xhiDbl + 360.0d;
                                query.append("&lon360>=" + xloDbl);
                                query.append("&lon360<=" + xhiDbl);
                                query2.append("&"+lonname+">="+xloDbl+"&"+lonname+"<180");

                            } else if ( xhiDbl < 0 && xloDbl < 0 ) {
                            	double t = xhiDbl;
                            	xhiDbl = xloDbl;
                            	xloDbl = t;
                            	query.append("&"+URLEncoder.encode("&"+lonname+">="+xloDbl, StandardCharsets.UTF_8.name()));
                                query.append("&"+URLEncoder.encode("&"+lonname+"<="+xhiDbl, StandardCharsets.UTF_8.name()));
                            } 
                            
                            // the else block is that you overlapped so leave off the longitude constraint all together

                        } else {
                            // This else block is the case where it a query that does not cross the date line.
                            // Still have to use the normalized values.
                            query.append("&"+lonname+">=" + xloDbl);
                            query.append("&"+lonname+"<=" + xhiDbl);
                        }
                    }// Span the whole globe so leave off the lon query all together.
                } else {
                    //  If they are not both defined, add the one that is...  There will be no difficulties with dateline crossings...
                    if (xlo.length() > 0) query.append("&"+lonname+">=" + xlo);
                    if (xhi.length() > 0) query.append("&"+lonname+"<=" + xhi);
                }
            } else {

                if (xlo.length() > 0 && xhi.length() > 0 ) {

                    double xhiDbl = String2.parseDouble(xhi);
                    double xloDbl = String2.parseDouble(xlo);
                    
                    if ( xloDbl < 0 ) xloDbl = xloDbl + 360;
                    if ( xhiDbl < 0 ) xhiDbl = xhiDbl + 360;
                    // Check the span before normalizing and if it's big, just forget about the lon constraint all together.
                    if ( Math.abs(xhiDbl - xloDbl ) < 358. ) {
                        // Now a wrap around from west to east should be have xhi < xlo;
                        if ( xhiDbl < xloDbl ) {
                            query2 = new StringBuilder(query.toString());
                            query2.append("&"+lonname+">"+0);
                            query2.append("&"+lonname+">="+xhiDbl);
                            query.append("&"+lonname+">"+xloDbl);
                        } else {
                            if (xlo.length() > 0) query.append("&"+lonname+">=" + xloDbl);
                            if (xhi.length() > 0) query.append("&"+lonname+"<=" + xhiDbl);
                        }
                    }
                    // else it's a global request. Don't constraint on lon at all.
                } else {
                    //  If they are not both defined, add the one that is...  There will be no difficulties with dateline crossings...
                    if (xlo.length() > 0) query.append("&"+lonname+">=" + xlo);
                    if (xhi.length() > 0) query.append("&"+lonname+"<=" + xhi);
                }
            }
            
            // This changes the data set to the decimated data set if it exists.
            // We have decided to try all ribbon plots with the decimated data set...
            // so we will remove the !hasConstraints 
            if ( !smallarea && operationID.equals("Trajectory_2D_poly") && !decid.equals("") && !full ) {
                id = decid;
            }
            
            query.insert(0, type+"?");
            query.insert(0, id);
            
            if ( query != null && query.length() > 0 ) {
                qs.add(query.toString());
            }
            if ( query2 != null && query.length() > 0 ) {
                query2.insert(0, type+"?");
                query2.insert(0, id);            
                qs.add(query2.toString());
            }
            return qs;
    }

    /**
     * This doesn't really do anything, but it shows how to access trajectories via the Java netCDF library.  Looping through the whole thing is pretty slow.
     * @param netcdfFilename
     * @param temp_file1
     * @param temp_file2
     * @throws IOException
     */
    public void loopthroughtraj(String netcdfFilename, File temp_file1, File temp_file2) throws IOException {
        Formatter errlog = new Formatter();
        FeatureDatasetPoint trajset1 = (FeatureDatasetPoint) FeatureDatasetFactoryManager.open(FeatureType.TRAJECTORY, temp_file1.getAbsolutePath(), null, errlog);
        List<DataRow> rows1 = getFeatures(trajset1);
        FeatureDatasetPoint trajset2 = (FeatureDatasetPoint) FeatureDatasetFactoryManager.open(FeatureType.TRAJECTORY, temp_file2.getAbsolutePath(), null, errlog);       
        List<DataRow> rows2 = getFeatures(trajset2);
        trajset1.close();
        trajset2.close();
        List<DataRow> datarows = new ArrayList<DataRow>();
        datarows.addAll(rows1);
        datarows.addAll(rows2);
        Collections.sort(datarows, new DataRowComparator());
        for (Iterator dataIT = datarows.iterator(); dataIT.hasNext();) {
            DataRow dataRow = (DataRow) dataIT.next();
            System.out.println(dataRow.getId() + "   " + dataRow.getData().get(time));
        }
        
    }
    public void writeEmpty (String netcdfFilename) throws Exception {
        ArrayList<Dimension> dimList = new ArrayList<Dimension>();
        IntermediateNetcdfFile nfile;
        try {
            nfile = new IntermediateNetcdfFile(netcdfFilename, false);
        } catch (LASException e) {
            throw new Exception("Cannot create empty file.");
        }
        NetcdfFileWriteable netcdfFile = nfile.getNetcdfFile();
        Dimension index = netcdfFile.addDimension("index", 1);
        dimList.add(index);
        netcdfFile.addVariable(time, DataType.DOUBLE, dimList);
        ArrayDouble.D1 data = new ArrayDouble.D1(1);
        Double d = new Double("-9999.");
        data.set(0, d);
        netcdfFile.addGlobalAttribute("query_result", "No data found to match this request.");
        netcdfFile.create();
        netcdfFile.write(time, data);
    }
    public void merge(String netcdfFilename, File temp_file1, File temp_file2) throws IOException, InvalidRangeException  {

        NetcdfFile trajset1 = (NetcdfFile) NetcdfDataset.open(temp_file1.getAbsolutePath());
        NetcdfFile trajset2 = (NetcdfFile) NetcdfDataset.open(temp_file2.getAbsolutePath());
        NetcdfFileWriter ncfile = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf3, netcdfFilename);
        List<Variable> vars = trajset1.getVariables();
        Dimension obsdim1 = null;
        String obsdimname = null;
        Variable obscount1 = null;
        Variable obscount2 = null;
        String trajidname = null;
        Array trajids1 = null;
        int trajidwidth = 64;
        Dimension trajdim_org = null;
        for (Iterator iterator = vars.iterator(); iterator.hasNext();) {
            Variable variable = (Variable) iterator.next();
            Attribute td = variable.findAttribute("sample_dimension");
            if ( td != null ) {
                obsdimname = td.getStringValue();
                obscount1 = variable;
            }
            Attribute tid = variable.findAttribute("cf_role");
            if ( tid != null && tid.getStringValue().equals("trajectory_id") ) {
                trajdim_org = variable.getDimension(0);
                trajidname = variable.getShortName();
                trajids1 = variable.read();
            }
        }
        Map<String, Array> subsetvars1 = new HashMap<String, Array>();
        Map<String, Array> subsetvars2 = new HashMap<String, Array>();
        for (Iterator iterator = vars.iterator(); iterator.hasNext();) {
            Variable variable = (Variable) iterator.next();
            if ( trajdim_org != null && variable.getDimension(0).getShortName().equals(trajdim_org.getShortName())) {
                Array a1 = variable.read();
                subsetvars1.put(variable.getShortName(), a1);
                Variable v2 = trajset2.findVariable(variable.getShortName());
                Array a2 = v2.read();
                subsetvars2.put(v2.getShortName(), a2);
            } else if ( variable.getDimension(0).getShortName().equals(obsdimname)) {
                all.add(variable.getShortName());
            }
        }

        obsdim1 = trajset1.findDimension(obsdimname);
        Dimension obsdim2 = trajset2.findDimension(obsdimname);
        if ( obscount1 != null ) {
        	obscount2 = trajset2.findVariable(obscount1.getShortName());
        }
        Variable tv = trajset2.findVariable(trajidname);
        Array trajids2 = tv.read();

        Set<String> trajIDs = new HashSet<String>();
        ArrayChar.D2 tid1 = null;
        ArrayChar.D2 tid2 = null;
        // Merge the values to find the number of unique IDs
        if ( trajids1 != null && trajids1 instanceof ArrayChar.D2 && trajids2 instanceof ArrayChar.D2 ) {
            // This is what I expect for now, but it could be something different.
            tid1 = (ArrayChar.D2)trajids1;
            tid2 = (ArrayChar.D2)trajids2;
            for(int index = 0; index < tid1.getShape()[0]; index++) {               
                String id = tid1.getString(index);
                trajIDs.add(id);
            }
            for(int index = 0; index < tid2.getShape()[0]; index++) {
                String id = tid2.getString(index);
                trajIDs.add(id);
            }    
        }

        if ( obscount1 != null && obscount2 != null && tid1 != null && tid2 != null ) {
            Array oc1 = obscount1.read();
            Array oc2 = obscount2.read();
            for (int index = 0; index < obscount1.getShape(0); index++) {
                String id = tid1.getString(index);
                int count = oc1.getInt(index);
                Map<String, Object> subset = new HashMap<String, Object>();
                for (Iterator subsetIt = subsetvars1.keySet().iterator(); subsetIt.hasNext();) {
                    String key = (String) subsetIt.next();
                    Array a = (Array) subsetvars1.get(key);
                    subset.put(key, getObject(a, index));
                }
                for (int j = 0; j < count; j++) {
                    DataRow datarow = new DataRow();
                    datarow.setId(id);
                    datarow.setSubsets(subset);
                    datarows.add(datarow);
                }
            }
            for (int index = 0; index < obscount2.getShape(0); index++) {
                String id = tid2.getString(index);
                int count = oc2.getInt(index);
                Map<String, Object> subset = new HashMap<String, Object>();
                for (Iterator subsetIt = subsetvars2.keySet().iterator(); subsetIt.hasNext();) {
                    String key = (String) subsetIt.next();
                    Array a = (Array) subsetvars2.get(key);
                    subset.put(key, getObject(a, index));
                }
                for (int j = 0; j < count; j++) {
                    DataRow datarow = new DataRow();
                    datarow.setId(id);
                    datarow.setSubsets(subset);
                    datarows.add(datarow);
                }
            }
        }
        if ( obsdim1 != null && obsdim2 != null ) {
            int both = obsdim1.getLength()+obsdim2.getLength();    
            Dimension trajdim = ncfile.addDimension(null, "trajectory", trajIDs.size());
            Dimension dim = ncfile.addDimension(null, "obs", both);  
            List<Variable> allvars = trajset1.getVariables();
            for (Iterator varsIt = allvars.iterator(); varsIt.hasNext(); ) {
                Variable var1 = (Variable) varsIt.next();
                String varname = var1.getShortName();
                Variable var2 = trajset2.findVariable(varname);
                List<Dimension> dimlist = new ArrayList<Dimension>();
                
                
                if ( all.contains(varname) ) {

                    // It's a data variable, so it has obs dimension
                    dimlist.add(dim);
                    if ( var1.getDataType() == DataType.CHAR ) {
                        Dimension chardim1 = var1.getDimension(1);
                        Dimension chardim2 = var2.getDimension(1);
                        Dimension nchardim = ncfile.addDimension(null, chardim1.getShortName(), Math.max(chardim1.getLength(), chardim2.getLength()));
                        dimlist.add(nchardim);
                    }
                    Variable nv = ncfile.addVariable(null, var1.getShortName(), var1.getDataType(), dimlist);
                    List<Attribute> attributes = var1.getAttributes();
                    for (Iterator attIt = attributes.iterator(); attIt.hasNext();) {
                        Attribute attribute = (Attribute) attIt.next();
                        ncfile.addVariableAttribute(nv, attribute);
                    }

                    Array d1 = var1.read();
                    Array d2 = var2.read();
                    String name = var1.getShortName();

                    fill(d1, name, 0);
                    fill(d2, name, d1.getShape()[0]);


                } else {
                    // If it has the trajectory dimension then it's a sub-set variable.
                    dimlist.add(trajdim);
                    if ( var1.getDataType() == DataType.CHAR ) {
                        Dimension chardim1 = var1.getDimension(1);
                        Dimension chardim2 = var2.getDimension(1);
                        if ( var1.getShortName().equals(trajidname)) {
                            trajidwidth = Math.max(chardim1.getLength(), chardim2.getLength());
                        }
                        Dimension nchardim = ncfile.addDimension(null, chardim1.getShortName(), Math.max(chardim1.getLength(), chardim2.getLength()));
                        dimlist.add(nchardim);
                    }
                    Variable nv = ncfile.addVariable(null, var1.getShortName(), var1.getDataType(), dimlist);
                    List<Attribute> attributes = var1.getAttributes();
                    for (Iterator attIt = attributes.iterator(); attIt.hasNext();) {
                        Attribute attribute = (Attribute) attIt.next();
                        ncfile.addVariableAttribute(nv, attribute);
                    }
                }

            }
            List<Attribute> globals = trajset1.getGlobalAttributes();
            for (Iterator gatIt = globals.iterator(); gatIt.hasNext();) {
                Attribute gatt = (Attribute) gatIt.next();
                ncfile.addGroupAttribute(null, gatt);
            }
            ncfile.create();
        } else {
            System.out.println("obsdim not found");
        }

        Collections.sort(datarows, new DataRowComparator());
        List<String> sortedTraj = new ArrayList<String>();
        for (Iterator idIt = trajIDs.iterator(); idIt.hasNext();) {
            String id = (String) idIt.next();
            sortedTraj.add(id);
        }
        Collections.sort(sortedTraj);
        int total = 0;
        
        // Create the new data for the sample dimension
        ArrayInt.D1 counts = new ArrayInt.D1(sortedTraj.size());
        int index = 0;
        for (Iterator idIt = sortedTraj.iterator(); idIt.hasNext();){
            String id = (String) idIt.next();
            int idcount = 0;
            for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                DataRow dr = (DataRow) drIt.next();
                if ( dr.getId().equals(id) ) {
                    idcount++;
                }
                
            }
            counts.setInt(index, idcount);
            index++;
            total = total + idcount;
        }
        // Write the lengths of the trajectories.
        Variable v = ncfile.findVariable(obscount1.getShortName());
        ncfile.write(v, counts);
        
        Variable ids = ncfile.findVariable(trajidname);
        ArrayChar.D2 idsData = new ArrayChar.D2(sortedTraj.size(), trajidwidth);
        int idindex = 0;
        for (Iterator sortedIt = sortedTraj.iterator(); sortedIt.hasNext();) {
            String id = (String) sortedIt.next();
            idsData.setString(idindex, id);
            idindex++;
        }
        ncfile.write(ids, idsData);
        
        
        DataRow sampleRow = datarows.get(0);
        Map<String, Object> sampleSubsets = sampleRow.getSubsets();
        Map<String, Object> sampleData = sampleRow.getData();
        
        for (Iterator subsIt = sampleSubsets.keySet().iterator(); subsIt.hasNext();) {
            String subsetvar = (String) subsIt.next();
            Variable var = ncfile.findVariable(subsetvar);
            // Write all the subset variables except the count which has already been done above.
            if ( !var.getShortName().equals(obscount1.getShortName()) ) {
                if ( var.getDataType() == DataType.BOOLEAN ) {
                    ArrayBoolean.D1 a = new ArrayBoolean.D1(var.getShape(0));
                    int trajindex = 0;
                    for (Iterator idIt = sortedTraj.iterator(); idIt.hasNext();){
                        String id = (String) idIt.next();
                        // This is a named looped and named break to stop looking one the first matching row is found.
                        // A hack, but...
                        D: for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                            DataRow dr = (DataRow) drIt.next();
                            if ( dr.getId().equals(id) ) {
                                Boolean b = (Boolean) dr.getSubsets().get(subsetvar);
                                a.set(trajindex, b);
                                break D;
                            }
                        }
                        trajindex++;
                    }
                    ncfile.write(var, a);
                } else if ( var.getDataType() == DataType.BYTE ) {
                    ArrayByte.D1 a = new ArrayByte.D1(var.getShape(0));
                    int trajindex = 0;
                    for (Iterator idIt = sortedTraj.iterator(); idIt.hasNext();){
                        String id = (String) idIt.next();
                        D: for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                            DataRow dr = (DataRow) drIt.next();
                            if ( dr.getId().equals(id) ) {
                                Byte b = (Byte) dr.getSubsets().get(subsetvar);
                                a.set(trajindex, b);
                                break D;
                            } 
                        }
                        trajindex++;
                    }
                    ncfile.write(var, a);
                } else if ( var.getDataType() == DataType.CHAR ) {
                    int size = var.getShape(0);
                    int width = var.getShape(1);
                    ArrayChar.D2 a = new ArrayChar.D2(size, width);
                    int trajindex = 0;
                    for (Iterator idIt = sortedTraj.iterator(); idIt.hasNext();){
                        String id = (String) idIt.next();
                        D: for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                            DataRow dr = (DataRow) drIt.next();
                            if ( dr.getId().equals(id) ) {
                                String b = (String) dr.getSubsets().get(subsetvar);
                                a.setString(trajindex, b);
                                break D;
                            } 
                        }
                        trajindex++;
                    }
                    ncfile.write(var, a);
                } else if ( var.getDataType() == DataType.DOUBLE ) {
                    ArrayDouble.D1 a = new ArrayDouble.D1(var.getShape(0));
                    int trajindex = 0;
                    for (Iterator idIt = sortedTraj.iterator(); idIt.hasNext();){
                        String id = (String) idIt.next();
                        D: for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                            DataRow dr = (DataRow) drIt.next();
                            if ( dr.getId().equals(id) ) {
                                Double b = (Double) dr.getSubsets().get(subsetvar);
                                a.set(trajindex, b);
                                break D;
                            } 
                        }
                        trajindex++;
                    }
                    ncfile.write(var, a);
                } else if ( var.getDataType() == DataType.FLOAT ) {
                    ArrayFloat.D1 a = new ArrayFloat.D1(var.getShape(0));
                    int trajindex = 0;
                    for (Iterator idIt = sortedTraj.iterator(); idIt.hasNext();){
                        String id = (String) idIt.next();
                        D: for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                            DataRow dr = (DataRow) drIt.next();
                            if ( dr.getId().equals(id) ) {
                                Float b = (Float) dr.getSubsets().get(subsetvar);
                                a.set(trajindex, b);
                                break D;
                            } 
                        }
                        trajindex++;
                    }
                    ncfile.write(var, a);
                } else if ( var.getDataType() == DataType.INT ) {
                    ArrayInt.D1 a = new ArrayInt.D1(var.getShape(0));
                    int trajindex = 0;
                    for (Iterator idIt = sortedTraj.iterator(); idIt.hasNext();){
                        String id = (String) idIt.next();
                        D: for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                            DataRow dr = (DataRow) drIt.next();
                            if ( dr.getId().equals(id) ) {
                                Integer b = (Integer) dr.getSubsets().get(subsetvar);
                                a.set(trajindex, b);
                                break D;
                            } 
                        }
                        trajindex++;
                    }
                    ncfile.write(var, a);
                } else if ( var.getDataType() == DataType.LONG ) {
                    ArrayLong.D1 a = new ArrayLong.D1(var.getShape(0));
                    int trajindex = 0;
                    for (Iterator idIt = sortedTraj.iterator(); idIt.hasNext();){
                        String id = (String) idIt.next();
                        D: for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                            DataRow dr = (DataRow) drIt.next();
                            if ( dr.getId().equals(id) ) {
                                Long b = (Long) dr.getSubsets().get(subsetvar);
                                a.set(trajindex, b);
                                break D;
                            } 
                        }
                        trajindex++;
                    }
                    ncfile.write(var, a);
                } else if ( var.getDataType() == DataType.SHORT ) {
                    ArrayShort.D1 a = new ArrayShort.D1(var.getShape(0));
                    int trajindex = 0;
                    for (Iterator idIt = sortedTraj.iterator(); idIt.hasNext();){
                        String id = (String) idIt.next();
                        D: for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                            DataRow dr = (DataRow) drIt.next();
                            if ( dr.getId().equals(id) ) {
                                Short b = (Short) dr.getSubsets().get(subsetvar);
                                a.set(trajindex, b);
                                break D;
                            } 
                        }
                        trajindex++;
                    }
                    ncfile.write(var, a);
                } else if ( var.getDataType() == DataType.STRING ) {
                    ArrayString.D1 a = new ArrayString.D1(var.getShape(0));
                    int trajindex = 0;
                    for (Iterator idIt = sortedTraj.iterator(); idIt.hasNext();){
                        String id = (String) idIt.next();
                        D: for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                            DataRow dr = (DataRow) drIt.next();
                            if ( dr.getId().equals(id) ) {
                                String b = (String) dr.getSubsets().get(subsetvar);
                                a.set(trajindex, b);
                                break D;
                            } 
                        }
                        trajindex++;
                    }
                    ncfile.write(var, a);

                }
            }
        }
        for (Iterator dataIt = sampleData.keySet().iterator(); dataIt.hasNext();) {
            String varname = (String) dataIt.next();
            Variable var = ncfile.findVariable(varname);
            if ( var.getDataType() == DataType.BOOLEAN ) {
                ArrayBoolean.D1 a = new ArrayBoolean.D1(var.getShape(0));
                int drindex = 0;
                for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                    DataRow dr = (DataRow) drIt.next();
                    Boolean b = (Boolean) dr.getData().get(varname);
                    a.set(drindex, b);
                    drindex++;
                }
                ncfile.write(var, a);
            } else if ( var.getDataType() == DataType.BYTE ) {
                ArrayByte.D1 a = new ArrayByte.D1(var.getShape(0));
                int drindex = 0;
                for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                    DataRow dr = (DataRow) drIt.next();
                    Byte b = (Byte) dr.getData().get(varname);
                    a.set(drindex, b);
                    drindex++;
                }
                ncfile.write(var, a);
            } else if ( var.getDataType() == DataType.CHAR ) {
                ArrayChar.D2 a = new ArrayChar.D2(var.getShape(0), var.getShape(1));
                int drindex = 0;
                for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                    DataRow dr = (DataRow) drIt.next();
                    String b = (String) dr.getData().get(varname);
                    a.setString(drindex, b);
                    drindex++;
                }
                ncfile.write(var, a);
            } else if ( var.getDataType() == DataType.DOUBLE ) {
                ArrayDouble.D1 a = new ArrayDouble.D1(var.getShape(0));
                int drindex = 0;
                
                // Don't use the missing or fill value for the actual range.
                Attribute missing = var.findAttribute("missing_value");
                Double mv = new Double(-1);
                if ( missing != null ) {
                    mv = (Double) missing.getNumericValue();
                }
                Double fill = new Double(-1);
                Attribute fillValue = var.findAttribute("_FillValue");
                if ( fillValue != null ) {
                    fill = (Double) fillValue.getNumericValue();
                }
                
                
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;
                for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                    DataRow dr = (DataRow) drIt.next();
                    Double b = (Double) dr.getData().get(varname);
                    boolean isMissing = false;
                    boolean isFill = false;
                    
                    if( missing != null && !mv.equals(Double.NaN) ) {
                        isMissing = !(Math.abs(mv - b) > 0.0001);
                    } else if ( b.equals(Double.NaN) ) {
                        isMissing = true;
                    }
                    if ( fillValue != null && !fill.equals(Double.NaN) ) {
                        isFill = !(Math.abs(fill - b) > 0.0001);
                    } else if ( b.equals(Double.NaN) ) {
                        isFill = true;
                    }
                    if ( b < min && !isMissing && !isFill ) {
                        min = b;
                    }
                    if ( b > max && !isMissing && !isFill ) {
                        max = b;
                    }
                    a.set(drindex, b);
                    drindex++;
                }
                ncfile.write(var, a);
                if ( !(min - Double.MAX_VALUE < .001 && max - Double.MIN_VALUE < .001) ) {
                    ArrayDouble.D1 minmax = new ArrayDouble.D1(2);
                    minmax.set(0, min);
                    minmax.set(1, max);
                    Attribute actual_range = new Attribute("actual_range", minmax);
                    if ( var.findAttributeIgnoreCase("actual_range") != null ) {
                        ncfile.updateAttribute(var, actual_range);
                    }
                }
            } else if ( var.getDataType() == DataType.FLOAT ) {
                ArrayFloat.D1 a = new ArrayFloat.D1(var.getShape(0));
                int drindex = 0;
                
                // Don't using the missing of fill value in the actual range.
               
                Attribute missing = var.findAttribute("missing_value");
                Float mv = new Float(-1);
                if ( missing != null ) {
                    mv = (Float) missing.getNumericValue();
                }
                Float fill = new Float(-1);
                Attribute fillValue = var.findAttribute("_FillValue");
                if ( fillValue != null ) {
                    fill = (Float) fillValue.getNumericValue();
                }
                
                float min = Float.MAX_VALUE;
                float max = Float.MIN_VALUE;
                
               
                for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                    boolean isMissing = false;
                    boolean isFill = false;
                    DataRow dr = (DataRow) drIt.next();
                    Float b = (Float) dr.getData().get(varname);
                    if( missing != null && !mv.equals(Float.NaN) ) {
                        isMissing = !(Math.abs(mv - b) > 0.0001);
                    } else if ( b.equals(Float.NaN) ) {
                        isMissing = true;
                    }
                    if ( fillValue != null && !fill.equals(Float.NaN) ) {
                        isFill = !(Math.abs(fill -b) > 0.0001);
                    } else if ( b.equals(Float.NaN) ) {
                        isFill = true;
                    }
                    if ( b < min && !isMissing && !isFill ) {
                        min = b;
                    }
                    if ( b > max ) {
                        max = b;
                    }
                    a.set(drindex, b);
                    drindex++;
                }
                ncfile.write(var, a);
                ArrayFloat.D1 minmax = new ArrayFloat.D1(2);
                minmax.set(0, min);
                minmax.set(1, max);
                Attribute actual_range = new Attribute("actual_range", minmax);
                ncfile.updateAttribute(var, actual_range);
            } else if ( var.getDataType() == DataType.INT ) {
                ArrayInt.D1 a = new ArrayInt.D1(var.getShape(0));
                int drindex = 0;
                for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                    DataRow dr = (DataRow) drIt.next();
                    Integer b = (Integer) dr.getData().get(varname);
                    a.set(drindex, b);
                    drindex++;
                }
                ncfile.write(var, a);
            } else if ( var.getDataType() == DataType.LONG ) {
                ArrayLong.D1 a = new ArrayLong.D1(var.getShape(0));
                int drindex = 0;
                for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                    DataRow dr = (DataRow) drIt.next();
                    Long b = (Long) dr.getData().get(varname);
                    a.set(drindex, b);
                    drindex++;
                }
                ncfile.write(var, a);
            } else if ( var.getDataType() == DataType.SHORT ) {
                ArrayShort.D1 a = new ArrayShort.D1(var.getShape(0));
                int drindex = 0;
                for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                    DataRow dr = (DataRow) drIt.next();
                    Short b = (Short) dr.getData().get(varname);
                    a.set(drindex, b);
                    drindex++;
                }
                ncfile.write(var, a);
            } else if ( var.getDataType() == DataType.STRING ) {
                ArrayString.D1 a = new ArrayString.D1(var.getShape(0));
                int drindex = 0;
                for (Iterator drIt = datarows.iterator(); drIt.hasNext();) {
                    DataRow dr = (DataRow) drIt.next();
                    String b = (String) dr.getData().get(varname);
                    a.set(drindex, b);
                    drindex++;
                }
                ncfile.write(var, a);
            }
        }
        Variable var = ncfile.findVariable(lonname);
        Attribute ar = var.findAttribute("actual_range");
        
        if ( ar != null ) {
            try {
                ncfile.updateAttribute(null, new Attribute("geospatial_lon_min", ar.getNumericValue(0)));
            } catch (Exception e) {
                // Bummer, but we'll deal.
            }
            try {
                ncfile.updateAttribute(null, new Attribute("Westernmost_Easting", ar.getNumericValue(0)));
            } catch (Exception e) {
             // Bummer, but we'll deal.
            }
            try {
                ncfile.updateAttribute(null, new Attribute("geospatial_lon_max", ar.getNumericValue(1)));
            } catch (Exception e) {
             // Bummer, but we'll deal.
            }
            try {
                ncfile.updateAttribute(null, new Attribute("Easternmost_Easting", ar.getNumericValue(1)));
            } catch (Exception e) {
             // Bummer, but we'll deal.
            }
        }
        
        
        var = ncfile.findVariable(latname);
        ar = var.findAttribute("actual_range");
        
        if ( ar != null ) {
            try {
                ncfile.updateAttribute(null, new Attribute("geospatial_lat_min", ar.getNumericValue(0)));
            } catch (Exception e) {
                // Bummer, but we'll deal.
            }
            try {
                ncfile.updateAttribute(null, new Attribute("Southernmost_Northing", ar.getNumericValue(0)));
            } catch (Exception e) {
                // Bummer, but we'll deal.
            }
            try {
                ncfile.updateAttribute(null, new Attribute("geospatial_lat_max", ar.getNumericValue(1)));
            } catch (Exception e) {
                // Bummer, but we'll deal.
            }
            try {
                ncfile.updateAttribute(null, new Attribute("Northernmost_Northing", ar.getNumericValue(0)));
            } catch (Exception e) {
                // Bummer, but we'll deal.
            }
        }

        if ( zname != null ) {
            var = ncfile.findVariable(zname);
            if ( var != null ) {   
                ar = var.findAttribute("actual_range");

                if ( ar != null ) {
                    try {
                        ncfile.updateAttribute(null, new Attribute("geospatial_vertical_min", ar.getNumericValue(0)));
                    } catch (Exception e) {
                        // Bummer, but we'll deal.
                    }
                    try {
                        ncfile.updateAttribute(null, new Attribute("geospatial_vertical_max", ar.getNumericValue(1)));
                    } catch (Exception e) {
                        // Bummer, but we'll deal.
                    }
                }
            }
        }

        var = ncfile.findVariable(time);
        ar = var.findAttribute("actual_range");
        if ( ar != null ) {
            Attribute unitsAttr = var.findAttribute("units");
            Attribute calAttr = var.findAttribute("calendar");
            CalendarDateUnit cdu = null;
            if ( unitsAttr != null ) {
                if ( calAttr !=  null ) {
                    cdu = CalendarDateUnit.of(calAttr.getStringValue(), unitsAttr.getStringValue());
                } else {
                    cdu = CalendarDateUnit.of("gregorian", unitsAttr.getStringValue());
                }
            }
            if ( cdu != null ) {
                CalendarDate start = cdu.makeCalendarDate((Double)ar.getNumericValue(0));
                CalendarDate end = cdu.makeCalendarDate((Double) ar.getNumericValue(1));
                try {
                    ncfile.updateAttribute(null, new Attribute("time_coverage_start", start.toString()));
                } catch (Exception e) {
                    // Bummer
                }
                try {
                    ncfile.updateAttribute(null, new Attribute("time_coverage_end", end.toString()));
                } catch (Exception e) {
                    // Bummer
                }
            }
        }
        
        
        
        ncfile.close();
        trajset1.close();
        trajset2.close();

    }
    // Make sure to return the correct Java object based on the Array data type.
    // Assumes the array is D1 (or D2 for CHAR) since these are points.
    private Object getObject(Array a, int index) {
        if ( a instanceof ArrayBoolean.D1 ) {
            return a.getBoolean(index);
        } else if (a instanceof ArrayByte.D1 ) {
            return a.getByte(index);
        } else if ( a instanceof ArrayChar.D2 ){
            ArrayChar.D2 s = (ArrayChar.D2)a;
            return s.getString(index);
        } else if ( a instanceof ArrayDouble.D1 ) {
            return a.getDouble(index);
        } else if ( a instanceof ArrayFloat.D1 ) {
            return a.getDouble(index);
        } else if ( a instanceof ArrayInt.D1 ) {
            return a.getInt(index);
        } else if ( a instanceof ArrayLong.D1 ) {
            return a.getLong(index);
        } else if ( a instanceof ArrayShort.D1 ) {
            return a.getShort(index);
        } else if ( a instanceof ArrayString.D1 ){
            ArrayString.D1 s = (ArrayString.D1)a;
            return s.get(index);
        } else {
            // Hopefully it never comes to this.
            return a.getObject(index);
        }
    }
    private void fill(Array d, String name, int offset) {
        
        
        if ( d instanceof ArrayBoolean.D1 ) {
            for ( int index = 0; index < d.getShape()[0]; index++ ) {
                DataRow rowdata = datarows.get(offset+index);
                rowdata.getData().put(name, d.getBoolean(index));
            }
        } else if (d instanceof ArrayByte.D1 ) {
            for ( int index = 0; index < d.getShape()[0]; index++ ) {
                DataRow rowdata = datarows.get(offset+index);
                rowdata.getData().put(name, d.getByte(index));
            }
        } else if ( d instanceof ArrayChar.D2 ){
            ArrayChar.D2 s = (ArrayChar.D2)d;
            for ( int index = 0; index < s.getShape()[0]; index++ ) {
                DataRow rowdata = datarows.get(offset+index);
                rowdata.getData().put(name, s.getString(index));
                if ( cruiseid != null && name.equals(cruiseid) ) {
                    rowdata.setId(s.getString(index));
                }
            }
        } else if ( d instanceof ArrayDouble.D1 ) {
            for ( int index = 0; index < d.getShape()[0]; index++ ) {
                DataRow rowdata = datarows.get(offset+index);             
                rowdata.getData().put(name, d.getDouble(index));
                if ( name.equals(time) ) {
                    rowdata.setTime(d.getDouble(index));
                }
            }
        } else if ( d instanceof ArrayFloat.D1 ) {
            for ( int index = 0; index < d.getShape()[0]; index++ ) {
                DataRow rowdata = datarows.get(offset+index);
                rowdata.getData().put(name, d.getFloat(index));
            }
        } else if ( d instanceof ArrayInt.D1 ) {
            for ( int index = 0; index < d.getShape()[0]; index++ ) {
                DataRow rowdata = datarows.get(offset+index);
                rowdata.getData().put(name, d.getInt(index));
            }
        } else if ( d instanceof ArrayLong.D1 ) {
            for ( int index = 0; index < d.getShape()[0]; index++ ) {
                DataRow rowdata = datarows.get(index);
                rowdata.getData().put(name, d.getLong(index));
            }
        } else if ( d instanceof ArrayShort.D1 ) {
            for ( int index = 0; index < d.getShape()[0]; index++ ) {
                DataRow rowdata = datarows.get(offset+index);
                rowdata.getData().put(name, d.getShort(index));
            }
        } else if ( d instanceof ArrayString.D1 ){
            ArrayString.D1 s = (ArrayString.D1)d;
            for ( int index = 0; index < s.getShape()[0]; index++ ) {
                DataRow rowdata = datarows.get(offset+index);
                rowdata.getData().put(name, s.get(index));
                if ( cruiseid != null && name.equals(cruiseid) ) {
                    rowdata.setId(s.get(index));
                }
            }
        }
    }
    private List<DataRow> getFeatures(FeatureDatasetPoint p) throws IOException {
        List<DataRow> rows = new ArrayList<DataRow>();
        List<FeatureCollection> tfc = p.getPointFeatureCollectionList();
        int size = 0;
        for (Iterator tfcIt = tfc.iterator(); tfcIt.hasNext();) {
            TrajectoryFeatureCollection featureCollection = (TrajectoryFeatureCollection) tfcIt.next();
            System.out.println(featureCollection.getName());
            while ( featureCollection.hasNext() ) {
                TrajectoryFeature f = featureCollection.next();
                int rowcount = 0;
                while ( f.hasNext() ) {
                    Map<String, Object> rowdata = new HashMap<String, Object>();
                    PointFeatureIterator pIt =  f.getPointFeatureIterator(5000);
                    rowcount++;                    
                    while ( pIt.hasNext() ) {
                        PointFeature pf = pIt.next();
                        StructureData sd = pf.getData();
                        Iterator memberIt = sd.getMembers().iterator();

                        while ( memberIt.hasNext() ) {
                            Member m = (Member) memberIt.next();
                            String name = m.getName();
                            Array a= m.getDataArray();
                            if ( a instanceof ArrayBoolean.D1 ) {
                                rowdata.put(name, a.getBoolean(0));
                            } else if (a instanceof ArrayByte.D1 ) {
                                rowdata.put(name, a.getByte(0));
                            } else if ( a instanceof ArrayChar.D2 ){
                                ArrayChar.D2 s = (ArrayChar.D2)a;
                                rowdata.put(name, s.getString(0));
                            } else if ( a instanceof ArrayDouble.D1 ) {
                                rowdata.put(name, a.getDouble(0));
                            } else if ( a instanceof ArrayFloat.D1 ) {
                                rowdata.put(name, a.getDouble(0));
                            } else if ( a instanceof ArrayInt.D1 ) {
                                rowdata.put(name, a.getInt(0));
                            } else if ( a instanceof ArrayLong.D1 ) {
                                rowdata.put(name, a.getLong(0));
                            } else if ( a instanceof ArrayShort.D1 ) {
                                rowdata.put(name, a.getShort(0));
                            } else if ( a instanceof ArrayString.D1 ){
                                ArrayString.D1 s = (ArrayString.D1)a;
                                rowdata.put(name, s.get(0));
                            }
                        }
                    }
                    System.out.println("Row: "+rowcount+" has "+rowdata.keySet().size()+" observations ");
                    //DataRow row = new DataRow((String)rowdata.get(cruiseid), (Double)rowdata.get(time), rowdata);
                    // rows.add(row);
                    System.out.println("Total rows: "+rows.size());
                }
            }
        }
        System.out.println("size="+size);
        return rows;
    }

    /**
     * This returns a required resourcePath.
     * [This method could be part of TemplateTool.]
     *
     * @param propertyName
     * @return the reqested resourcePath.
     * @throws LASException if the property isn't found or is "".
     */
    public String getRequiredResourcePath(String propertyName) throws LASException {
    	return required(getResourcePath(propertyName), 
            "resource path \"" + propertyName + "\"");
    }

    /**
     * This throws an LASException("Required value wasn't specified: " + id)
     * if s is null or "".
     * [This method could be in LASBackendRequest.]
     *
     * @param s a string which may be null or ""
     * @param id e.g., "database_access property 'url'"
     * @return s (for convenience)
     * @throws LASException("Required value wasn't specified: " + id)
     * if s is null or "".
     */
    public static String required(String s, String id) throws LASException {
        if (s == null || s.equals("")) 
            throw new LASException ("Required value wasn't specified: " + id + ".");
        return s;
    }

    /**
     * Get the value of a particular database property.
     * @param property
     * @return the value of the property
     * @throws LASException
     */
    public String getTabledapProperty(LASBackendRequest backendRequest, String property) throws LASException {
        Element data = backendRequest.getRootElement().getChild("dataObjects").getChild("data");
        Element td_access = backendRequest.findPropertyGroup(data, "tabledap_access");
        return backendRequest.findPropertyValue(td_access, property);
    }


}
