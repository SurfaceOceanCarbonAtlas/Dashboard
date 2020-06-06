package gov.noaa.pmel.tmap.addxml;

import gov.noaa.pmel.tmap.las.proxy.LASProxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import opendap.dap.Attribute;
import opendap.dap.AttributeTable;
import opendap.dap.BaseType;
import opendap.dap.DAS;
import opendap.dap.DDS;
import opendap.dap.NoSuchAttributeException;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import ucar.unidata.util.Format;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;


public class ErddapProcessor {

	private static int timeout = 400;  // units of seconds

	public DAS das = new DAS();
	public InputStream input;
	public List<String> subsetNames = new ArrayList<String>();
	public Map<String, AttributeTable> idVar = new HashMap<String, AttributeTable>();
	public Map<String, AttributeTable> timeVar = new HashMap<String, AttributeTable>();
	public Map<String, AttributeTable> latVar = new HashMap<String, AttributeTable>();
	public Map<String, AttributeTable> lonVar = new HashMap<String, AttributeTable>();
	public Map<String, AttributeTable> zVar = new HashMap<String, AttributeTable>();
	public Map<String, AttributeTable> data = new HashMap<String, AttributeTable>();
	public Map<String, AttributeTable> subsets = new HashMap<String, AttributeTable>();
	public Map<String, AttributeTable> monthOfYear = new HashMap<String, AttributeTable>();

	protected ArrayList<VariableBean> variables = new ArrayList<VariableBean>();
	protected GridBean gb = new GridBean();

	protected String url = "http://osmc.noaa.gov/erddap/tabledap/";
	protected String id = "OSMCV4_DUO_SURFACE_TRAJECTORY";
	protected List<String> axesToSkip = new ArrayList<String>();

	protected static LASProxy lasProxy = new LASProxy();

	private static String TRAJECTORY = "cdm_trajectory_variables";
	private static String PROFILE = "cdm_profile_variables";
	private static String TIMESERIES = "cdm_timeseries_variables";
	private static String POINT = "cdm_point_variables";
	

	static boolean isTrajectory = false;
	static boolean isProfile = false;
	static boolean isTimeseries = false;
	static boolean isPoint = false;
	static boolean isTrajectoryProfile = false;
	
	static boolean default_supplied = false;

	DecimalFormat df = new DecimalFormat("#.##");
	private static final DecimalFormat decimalFormat = new DecimalFormat("###############.###############");
	
	DateTimeFormatter hoursfmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm"); 
	DateTimeFormatter shortFerretForm = DateTimeFormat.forPattern("dd-MMM-yyyy").withChronology(GregorianChronology.getInstance(DateTimeZone.UTC)).withZone(DateTimeZone.UTC);
	DateTimeFormatter mediumFerretForm = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm").withChronology(GregorianChronology.getInstance(DateTimeZone.UTC)).withZone(DateTimeZone.UTC);

	public ErddapProcessor(List<String> axesToSkip) {

		this.axesToSkip = axesToSkip;
	}
	/**
	 * @param args
	 */
	public ErddapReturn process(String url, String id, boolean append, boolean auto_display, boolean do_not_write_UNKNOWN, boolean hours, double hours_step, boolean minutes, boolean verbose, String[] properties, String[] varproperties, String[] display, String[] skip, boolean separate) {
		DAS das = new DAS();
		DDS dds = new DDS();
		this.url = url;
		this.id = id;
		subsetNames = new ArrayList<String>();
		idVar = new HashMap<String, AttributeTable>();
		timeVar = new HashMap<String, AttributeTable>();
		latVar = new HashMap<String, AttributeTable>();
		lonVar = new HashMap<String, AttributeTable>();
		zVar = new HashMap<String, AttributeTable>();
		data = new HashMap<String, AttributeTable>();
		subsets = new HashMap<String, AttributeTable>();
		monthOfYear = new HashMap<String, AttributeTable>();

		variables = new ArrayList<VariableBean>();
		gb = new GridBean();
		ErddapReturn r = new ErddapReturn();

		InputStream stream = null;
		JsonStreamParser jp = null;

		try {
			
			r.write = true;
			if ( !url.endsWith("/") ) {
				url = url + "/";
			}
			DateTime date = new DateTime();
			if ( verbose ) {
				System.out.println("Processing: " + url + id + " at "+date.toString() );
			}
			if ( url.contains("OSMC_PROFILERS") ) {
				r.write = false;
				return r;
			}

			input = lasProxy.executeGetMethodAndReturnStream(url+id+".das", null, timeout);
			das.parse(input);
			input.close();
			input = lasProxy.executeGetMethodAndReturnStream(url+id+".dds", null, timeout);
			dds.parse(input);
			input.close();
			AttributeTable global = das.getAttributeTable("NC_GLOBAL");
			Attribute cdm_trajectory_variables_attribute = global.getAttribute(TRAJECTORY);
			Attribute cdm_profile_variables_attribute = global.getAttribute(PROFILE);
			Attribute cdm_timeseries_variables_attribute = global.getAttribute(TIMESERIES);
			Attribute cdm_data_type = global.getAttribute("cdm_data_type");
			Attribute altitude_proxy = global.getAttribute("altitude_proxy");
			String grid_type = cdm_data_type.getValueAt(0).toLowerCase(Locale.ENGLISH);
			Attribute subset_names = null;
			Attribute title_attribute = global.getAttribute("title");
			String title = "No title global attribute";
			if ( title_attribute != null ) {
				Iterator<String> titleIt = title_attribute.getValuesIterator();
				title = titleIt.next();
			}
			AttributeTable variableAttributes = das.getAttributeTable("s");
			if ( ( (cdm_data_type != null && grid_type.equalsIgnoreCase(CdmDatatype.POINT) ) || cdm_profile_variables_attribute !=null || cdm_trajectory_variables_attribute != null || cdm_timeseries_variables_attribute != null ) && variableAttributes != null ) {
				if ( grid_type.equals("trajectoryprofile") ) {
					isTrajectoryProfile = true;
					r.type=CdmDatatype.TRAJECTORYPROFILE;
				} else {
					if ( cdm_trajectory_variables_attribute != null && cdm_profile_variables_attribute != null ) {
						isTrajectoryProfile = true;
						r.type = CdmDatatype.TRAJECTORYPROFILE;
					} else if ( cdm_trajectory_variables_attribute != null ) {
						subset_names = cdm_trajectory_variables_attribute;
						isTrajectory = true;
						r.type = CdmDatatype.TRAJECTORY;
					} else if ( cdm_profile_variables_attribute != null ) {
						subset_names = cdm_profile_variables_attribute;
						isProfile = true;
						r.type = CdmDatatype.PROFILE;
					} else if ( cdm_timeseries_variables_attribute != null ) {
						subset_names = cdm_timeseries_variables_attribute;
						isTimeseries = true;
						r.type = CdmDatatype.TIMESERIES;
					} else if ( grid_type.equalsIgnoreCase(CdmDatatype.POINT) ) {
						subset_names = null;
						isPoint = true;
						r.type = CdmDatatype.POINT;
					}
				}
				if ( subset_names != null ) {
					Iterator<String> subset_variables_attribute_values = subset_names.getValuesIterator();
					if ( subset_variables_attribute_values.hasNext() ) {
						// Work with the first value...  Attributes like ranges can have multiple values...
						String subset_variable_value = subset_variables_attribute_values.next();
						String[] subset_variables = subset_variable_value.split(",");
						for (int i = 0; i < subset_variables.length; i++) {
							String tv = subset_variables[i].trim();
							if ( !tv.equals("") ) {
								subsetNames.add(tv);
							}
						}
					} else {
						System.err.println("No CDM trajectory, profile or timeseries variables found in the cdm_trajectory_variables, cdm_profile_variables or cdm_timeseries_variables global attribute.");
					}
				}
				// Collect the subset names...

				// Classify all of the variables...

				Enumeration names = variableAttributes.getNames();
				if ( !names.hasMoreElements() ) {
					System.out.println("No variables found in this data collection.");
				}
				while (names.hasMoreElements()) {
					String name = (String) names.nextElement();
					AttributeTable var = variableAttributes.getAttribute(name).getContainer();
					if ( subsetNames.contains(name) ) {
						if ( var.hasAttribute("cf_role") && (
								var.getAttribute("cf_role").getValueAt(0).equals("trajectory_id") || 
								var.getAttribute("cf_role").getValueAt(0).equals("profile_id") ||
								var.getAttribute("cf_role").getValueAt(0).equals("timeseries_id")
								) ) {
							idVar.put(name, var);
						} else {
							if ( !subsets.containsKey(name) ) {
								subsets.put(name, var);
							}
						}
					} else if ( var.hasAttribute("cf_role") && (
							var.getAttribute("cf_role").getValueAt(0).equals("trajectory_id") || 
							var.getAttribute("cf_role").getValueAt(0).equals("profile_id") || 
							var.getAttribute("cf_role").getValueAt(0).equals("timeseries_id")
							) ) {
						idVar.put(name, var);
						if ( !subsets.containsKey(name) ) {
							subsets.put(name, var);
						}
					}
					// Look at the attributes and classify any variable as either time, lat, lon, z or a data variable.
					if ( var.hasAttribute("_CoordinateAxisType") ) {
						String type = var.getAttribute("_CoordinateAxisType").getValueAt(0);
						if ( type.toLowerCase(Locale.ENGLISH).equals("time") ) {
							timeVar.put(name, var);
						} else if ( type.toLowerCase(Locale.ENGLISH).equals("lon") ) {
							lonVar.put(name, var);
						} else if ( type.toLowerCase(Locale.ENGLISH).equals("lat") ) {
							latVar.put(name, var);
						} else if ( type.toLowerCase(Locale.ENGLISH).equals("height") ) {
							zVar.put(name, var);
						}
					} else {
						if ( name.toLowerCase(Locale.ENGLISH).contains("tmonth") ) {
							monthOfYear.put(name, var);
						}
						boolean skipCheck = false;
						if ( skip != null ) {
							skipCheck = Arrays.asList(skip).contains(name);
						}
						if ( !data.containsKey(name) && !subsets.containsKey(name) && !idVar.containsKey(name) && !skipCheck ) {
							data.put(name, var);
						}
					}


				}
				// DEBUG what we've got so far:
				if ( !idVar.keySet().isEmpty() ) {
					String name = idVar.keySet().iterator().next();
					System.out.println(grid_type+" ID variable:");
					System.out.println("\t "+name);
				}
				System.out.println("Subset variables:");

				for (Iterator subIt = subsets.keySet().iterator(); subIt.hasNext();) {
					String key = (String) subIt.next();
					System.out.println("\t "+key);
				}
				if ( !timeVar.keySet().isEmpty() ) {
					String name = timeVar.keySet().iterator().next();
					System.out.println("Time variable:");
					System.out.println("\t "+name);
				}
				if ( !lonVar.keySet().isEmpty() ) {
					String name = lonVar.keySet().iterator().next();
					System.out.println("Lon variable:");
					System.out.println("\t "+name);
				}
				if ( !latVar.keySet().isEmpty() ) {
					String name = latVar.keySet().iterator().next();
					System.out.println("Lat variable:");
					System.out.println("\t "+name);
				}
				if ( !zVar.keySet().isEmpty() ) {
					String name = zVar.keySet().iterator().next();
					System.out.println("Z variable:");
					System.out.println("\t "+name);
				}
				if ( !monthOfYear.keySet().isEmpty() ) {
					String name = monthOfYear.keySet().iterator().next();
					System.out.println("Month of year variable:");
					System.out.println("\t "+name);
				}

				System.out.println("Data variables:");

				for (Iterator subIt = data.keySet().iterator(); subIt.hasNext();) {
					String key = (String) subIt.next();
					System.out.println("\t "+key);
				}

				Element datasetsE = new Element("datasets");
				Element gridsE = new Element("grids");
				Element axesE = new Element("axes");
				// Build the LAS configuration.
				DatasetBean db = new DatasetBean();
				
				if ( properties != null && properties.length > 0 ) {
					for (int i = 0; i < properties.length; i++) {
						// Split n-1 times so ui:default:file:blah#blah gets all of the stuff after the second ":"
						String[] parts = properties[i].split(":", 3);
						if ( parts[0].equals("ui") && parts[1].equals("default") ) default_supplied = true;
						db.setProperty(parts[0], parts[1], parts[2]);
					}
				}

				String las_id = "id-"+id;
				db.setElement(las_id);
				db.setName(title);            

				db.setUrl(url);
				// Build the grid...
				db.setProperty("ferret", "data_format", "csv");
				String gridid = "grid-"+id;
				gb.setElement(gridid);

				String dsgIDVariablename = null;
				if ( !idVar.keySet().isEmpty() ) {
					dsgIDVariablename = idVar.keySet().iterator().next();
				}

				// Get the ISO Metadata
				String isourl = url+id+".iso19115";
				stream = null;

				IsoMetadata meta = new IsoMetadata();
				stream = lasProxy.executeGetMethodAndReturnStream(isourl, null, timeout);
				if ( stream != null ) {
					JDOMUtils.XML2JDOM(new InputStreamReader(stream), meta);
					meta.init();
				} else { 
					r.write = false;
					return r;
				}

				List<AxisBean> axes = new ArrayList<AxisBean>();
				if ( !timeVar.keySet().isEmpty() ) {
					String name = timeVar.keySet().iterator().next();
					AttributeTable var = timeVar.get(name);
					db.setProperty("tabledap_access", "time", name);
					AxisBean ab = new AxisBean();
					if ( display != null && !display[0].equals("minimal") ) {
						if ( display.length == 1 ) {
							ab.setDisplay_lo(display[0]);
						} else if (display.length == 2 ){
							String t0text = display[0];
							String t1text = display[1];
							DateTime dt0;
							DateTime dt1;
							try {
								dt0 = shortFerretForm.parseDateTime(t0text);
							} catch (Exception e) {
								try {
									dt0 = mediumFerretForm.parseDateTime(t0text);
								} catch (Exception e1) {
									dt0 = null;
								} 
							}
							try {
								dt1 = shortFerretForm.parseDateTime(t1text);
							} catch (Exception e) {
								try {
									dt1 = mediumFerretForm.parseDateTime(t1text);
								} catch (Exception e1) {
									dt1 = null;
								}
							}
							if ( dt1 != null && dt0 != null ) {
								if ( dt0.isBefore(dt1)) {
									ab.setDisplay_lo(t0text);
									ab.setDisplay_hi(t1text);
								} else {
									ab.setDisplay_lo(t1text);
									ab.setDisplay_hi(t0text);
								}
							}
						}
					}
					ab.setElement(name+"-"+id);
					ab.setType("t");
					if ( minutes ) {
						ab.setUnits("minutes");
					} else if ( hours ) {
						ab.setUnits("hours");					
					} else {
						ab.setUnits("days");
					}
					Attribute ua = var.getAttribute("units");
					if ( ua != null ) {
						String units = ua.getValueAt(0);
						db.setProperty("tabledap_access", "time_units", units);
					}
					ArangeBean arb = new ArangeBean();
					
					if ( !axesToSkip.contains("t") ) {

						String start = meta.getTlo();
						String end = meta.getThi();
						
						if ( start == null || end == null ) {
							throw new Exception("Time metadata not found.");
						}

						// This should be time strings in ISO Format

						Chronology chrono = GregorianChronology.getInstance(DateTimeZone.UTC);
						DateTimeFormatter iso = ISODateTimeFormat.dateTimeParser().withChronology(chrono).withZone(DateTimeZone.UTC);

						DateTime dtstart = iso.parseDateTime(start);
						DateTime dtend = iso.parseDateTime(end);

						int days = Days.daysBetween(dtstart.withTimeAtStartOfDay() , dtend.withTimeAtStartOfDay() ).getDays();

						if ( hours || minutes ) {
							arb.setStart(hoursfmt.print(dtstart));
						} else {
							arb.setStart(hoursfmt.print(dtstart.withTimeAtStartOfDay()));
						}
						arb.setStep("1");

						// Fudge
						days = days + 1;

						if ( minutes ) {
							// Days are now minutes :-)
							days = days*24*60;
						} else if ( hours ) {
							// Days are now hours :-)
							days = (int) (days*24*Math.rint(1.d/hours_step));
							arb.setStep(decimalFormat.format(hours_step));

						} 

						arb.setSize(String.valueOf(Long.valueOf(days)));
						
						// If we're scanning a catalog set the display dates so the entire data set is not requested with the first plot.
						if ( auto_display ) {
							ab.setDisplay_lo(mediumFerretForm.print(dtstart));
							ab.setDisplay_hi(mediumFerretForm.print(dtstart.plusDays(1)));
						}
						ab.setArange(arb);

					} else {
						if ( do_not_write_UNKNOWN ) {
							r.write = false;
							r.unknown_axis = "t";
							return r;
						}
						arb.setStart("UNKNOWN_START");
						arb.setStep("UNKNOWN_STEP");
						arb.setSize("UNKNOWN_SIZE");
					}

					ab.setArange(arb);
					axes.add(ab);
				}
				if ( !lonVar.keySet().isEmpty() ) {
					String name = lonVar.keySet().iterator().next();
					db.setProperty("tabledap_access", "longitude", name);
					AxisBean ab = new AxisBean();
					ab.setElement(name+"-"+id);
					ab.setType("x");

					AttributeTable var = lonVar.get(name);
					Attribute ua = var.getAttribute("units");
					if ( ua != null ) {
						String units = ua.getValueAt(0);
						ab.setUnits(units);
					}
					ArangeBean arb = new ArangeBean();     
					if ( !axesToSkip.contains("x") ) {

						String start = meta.getXlo();
						String end = meta.getXhi();
						double dmin = -180.0d;
						double dmax = 180.0d;
						if ( Math.abs(Double.valueOf(start)) > 180.d || Math.abs(Double.valueOf(end)) > 180.d ) {
							db.setProperty("tabledap_access", "lon_domain", "0:360");
							dmin = 0.0d;
							dmax = 360.0d;
						} else {
							db.setProperty("tabledap_access", "lon_domain", "-180:180");
						}
						double dstart = Double.valueOf(start);
						double dend = Double.valueOf(end);
						double size = dend - dstart;  
						String ssize = String.valueOf(Math.round(size));
						// Fudge it up if the interval is really small...
						if ( size < 355.0 ) {
							double fudge = size*.15;
							if ( size < 1.0d ) {
								fudge = .25;
							}
							dstart = dstart - fudge;
							
							if ( dstart < dmin ) {
								dstart = dmin;
							}
							dend = dend + fudge;
							if ( dend > dmax ) {
								dend = dmax;
							}

							double c = Math.ceil(dend - dstart);
							ssize = String.valueOf((long) c+1);
							if ( ssize.equals("1") ) {
								double s = Double.valueOf(start) - 1.0d;
								start = df.format(s);
								ssize = "3";
							}
						}
						double step = (dend - dstart)/(Double.valueOf(ssize) - 1.0d);
						arb.setSize(ssize);
						arb.setStart(decimalFormat.format(dstart));
						arb.setStep(decimalFormat.format(step));
						ab.setArange(arb);
						

					} else {
						if ( do_not_write_UNKNOWN ) {
							r.write = false;
							r.unknown_axis = "x";
							return r;
						}
						arb.setStart("UNKNOWN_START");
						arb.setStep("UNKNOWN_STEP");
						arb.setSize("UNKNOWN_SIZE");
						ab.setArange(arb);
						db.setProperty("tabledap_access", "lon_domain", "UNKNOWN:UNKNOWN");
					}

					axes.add(ab);
				}
				if ( !latVar.keySet().isEmpty() ) {           

					String name = latVar.keySet().iterator().next();  
					db.setProperty("tabledap_access", "latitude", name);
					AttributeTable var = latVar.get(name);
					AxisBean ab = new AxisBean();
					ab.setElement(name+"-"+id);
					ab.setType("y");
					Attribute ua = var.getAttribute("units");
					if ( ua != null ) {
						String units = ua.getValueAt(0);
						ab.setUnits(units);
					}
					ArangeBean arb = new ArangeBean();      
					if ( !axesToSkip.contains("y") ) {

						String start = meta.getYlo();
						String end = meta.getYhi();
						double dstart = Double.valueOf(start);
						double dend = Double.valueOf(end);
						double size = dend - dstart; 
						String ssize = String.valueOf(Math.round(size));
						if ( size < 85.0 ) {
							double fudge = size * .15;
							if ( size < 1.0d ) {
								fudge = .25;
							}
							dstart = dstart - fudge;
							if ( dstart < -90.0d ) {
								dstart = -90.0d;
							}
							dend = dend + fudge;
							if ( dend > 90.0d ) {
								dend = 90.;
							}
							double c = Math.ceil(dend - dstart);
							ssize = String.valueOf((long) c + 1);
							if (ssize.equals("1")) {
								double s = Double.valueOf(start) - 1.0d;
								start = df.format(s);
								ssize = "3";
							}
						}
						double step = (dend - dstart)/(Double.valueOf(ssize) - 1.0d);
						arb.setStart(decimalFormat.format(dstart));
						arb.setStep(decimalFormat.format(step));                        
						arb.setSize(ssize);
						ab.setArange(arb);                    

					} else {
						if ( do_not_write_UNKNOWN ) {
							r.write = false;
							r.unknown_axis = "y";
							return r;
						}
						arb.setStart("UNKNOW_START");
						arb.setStep("UNKNOW_STEP");
						arb.setSize("UNKNOW_SIZE");
						ab.setArange(arb);
					}
					axes.add(ab);
				}
				/*
				 * For profiles, grab the depth and make 10 equal levels.
				 * 
				 * 
				 */
				// TODO look for the cdm_alititude_proxy attribute do a query since there won't be metadata.
				if ( !zVar.keySet().isEmpty() ) {
					String name = zVar.keySet().iterator().next();
					db.setProperty("tabledap_access", "altitude", name);
					AttributeTable var = zVar.get(name);
					AxisBean ab = new AxisBean();
					ab.setElement(name+"-"+id);
					ab.setType("z");
					Attribute ua = var.getAttribute("units");
					if ( ua != null ) {
						String units = ua.getValueAt(0);
						ab.setUnits(units);
					}
					ArangeBean arb = new ArangeBean();         
					if ( !axesToSkip.contains("z") ) {

						String start = meta.getZlo();
						String end = meta.getZhi();
						if ( start == null || end == null || altitude_proxy != null ) {
							// If it was a proxy, there's no metadata.
							// Pull the range from the data.
							stream = null;
							jp = null;
							String zquery = "";
							
							String nanDistinct = "&"+name+"!=NaN&distinct()";
							if ( zquery.length() > 0 ) {
								zquery = zquery + ",";
							}
							zquery = zquery+name+"&orderByMinMax(\""+name+"\")";
							String zurl = url+id + ".json?"+URLEncoder.encode(zquery, "UTF-8");
							stream = null;

							stream = lasProxy.executeGetMethodAndReturnStream(zurl, null, timeout);


							if ( stream != null ) {
								jp = new JsonStreamParser(new InputStreamReader(stream));
								JsonObject bounds = (JsonObject) jp.next();
								String[] zminmax = getMinMax(bounds, name);
								stream.close();

								start = zminmax[0];
								end = zminmax[1];
							}
						}
						if ( start != null && end != null ) {
							double size = Double.valueOf(end) - Double.valueOf(start);
							double step = size/10.;
							arb.setStart(start);
							arb.setStep(df.format(step));
							arb.setSize("10");
							ab.setArange(arb);   
						} else {
							if ( do_not_write_UNKNOWN ) {
								r.write = false;
								r.unknown_axis = "z";
								return r;
							}
							arb.setStart("UNKNOW_START");
							arb.setStep("UNKNOW_STEP");
							arb.setSize("UNKNOW_SIZE");
							ab.setArange(arb);
						}
					} else {
						if ( do_not_write_UNKNOWN ) {
							r.write = false;
							r.unknown_axis = "z";
							return r;
						}
						arb.setStart("UNKNOW_START");
						arb.setStep("UNKNOW_STEP");
						arb.setSize("UNKNOW_SIZE");
						ab.setArange(arb);
					}

					axes.add(ab);            
				}

				gb.setAxes(axes);

				Element cons = new Element("constraints");
				// Set up the variables...

				Element idcg = new Element("constraint_group");

				if ( dsgIDVariablename != null ) {

					AttributeTable idvar = idVar.get(dsgIDVariablename);
					VariableBean idvb = addSubset(dsgIDVariablename, idvar);
					idvb.addAttribute("color_by", "true");
					idvb.addAttribute(grid_type.toLowerCase(Locale.ENGLISH)+"_id", "true");
					idvb.addAttribute("grid_type", grid_type.toLowerCase(Locale.ENGLISH));
					variables.add(idvb);
				}

				if ( isTrajectory ) {
					idcg.setAttribute("name", "Individual Trajectory(ies)");
				}
				if ( isTrajectoryProfile ) {
					idcg.setAttribute("name", "Trajectory Profiles(s)");
				}
				if ( isProfile ) {
					idcg.setAttribute("name", "Individual Profile(s)");
				}
				if ( isTimeseries ) {
					idcg.setAttribute("name", "Individual Station(s)");
				}
				if ( isPoint ) {
					idcg.setAttribute("name", "Points");
				}
				idcg.setAttribute("type", "selection");

				Element idc = new Element("constraint");
				idc.setAttribute("name","Select By");
				if ( dsgIDVariablename != null ) {
					Element idv = new Element("variable");
					idv.setAttribute("IDREF", dsgIDVariablename+"-"+id);
					Element idkey = new Element("key");
					idkey.setText(dsgIDVariablename);
					idc.addContent(idv);
					idc.addContent(idkey);
					idcg.addContent(idc);
					cons.addContent(idcg);
				}

				Element subsetcg = new Element("constraint_group");
				subsetcg.setAttribute("type", "subset");
				subsetcg.setAttribute("name", "by Metadata");


				String lonn = lonVar.keySet().iterator().next();    
				String latn = latVar.keySet().iterator().next();

				// Before using them, remove latn and lonn
				subsets.remove(latn);
				subsets.remove(lonn);

				if ( subsets.keySet().size() > 0 ) {
					for (Iterator subsetIt = subsets.keySet().iterator(); subsetIt.hasNext();) {

						String name = (String) subsetIt.next();
						AttributeTable var = subsets.get(name);
						VariableBean vb = addSubset(name, var);
						vb.addAttribute("grid_type", grid_type.toLowerCase(Locale.ENGLISH));
						variables.add(vb);
						Element c = new Element("constraint");
						c.setAttribute("type", "subset");
						c.setAttribute("widget", "list");
						Element v = new Element("variable");
						v.setAttribute("IDREF", name+"-"+id);
						Element key = new Element("key");
						key.setText(name);
						c.addContent(v);
						c.addContent(key);
						subsetcg.addContent(c);
					}
					cons.addContent(subsetcg);
				}

				int i = 0;
				// Make the prop-prop list before adding in lat,lon and time.
				StringBuilder allv = new StringBuilder();
				for (Iterator subIt = data.keySet().iterator(); subIt.hasNext();) {
					String key = (String) subIt.next();
					allv.append(key);
					if ( subIt.hasNext() ) allv.append(",");
				}
				
				// Z name zn is used below as well...
				
				String zn = null;
				if ( !zVar.keySet().isEmpty() ) {
					zn = zVar.keySet().iterator().next();
				}

				HashMap<String, String> tabledap_access = db.getPropertyGroup("tabledap_access");
				// Don't add it unless it hasn't been previously added. It might have come off the command line.
				if ( tabledap_access == null || ( tabledap_access != null && !tabledap_access.containsKey("all_variables")) ) {
                    db.setProperty("tabledap_access", "all_variables", allv.toString());
                }
				/*
				 * There is a page that will show thumbnails of property-property plots.
				 * 
				 * It takes 3 pieces of metadata. First is the list of variables that will show up in the banner for a particular ID.
				 * 
                     <thumbnails> 
				 *
				 * THESE are ERDDAP variable names.
                          <metadata>expocode,vessel_name,investigators,qc_flag</metadata>
				 * 
				 * Next is the list of plot paris:
				 * 
				 *          <variable_pairs>
                               <!-- NO WHITESPACE AROUND THE COMMA -->
                               <!-- x-axis followed by y-axis variable. -->
                               <!-- LAS IDs -->

                               longitude-socatV3_c6c1_d431_8194,latitude-socatV3_c6c1_d431_8194
                               time-socatV3_c6c1_d431_8194,day_of_year-socatV3_c6c1_d431_8194
                               time-socatV3_c6c1_d431_8194,temp-socatV3_c6c1_d431_8194
                               time-socatV3_c6c1_d431_8194,Temperature_equi-socatV3_c6c1_d431_8194

                           </variable_paris>

				 * Finally, is just a flat list of every variable needed to make all the plots so there can be one data pull from ERDDAP.           

                           <!-- The names of the variables needed to make all of the thumbnail plots so the netcdf file can be as minimal as possible. 
                                Do not list latitude,longitude,depth,time,expocode
                                as these are handled by LAS internally
                           -->
                           <variable_names>day_of_year,temp,Temperature_equi</variable_names>

				 *
				 * The default set that we will construct will be lat vs lon and time vs all other varaibles.
				 */

				if ( dsgIDVariablename != null )
					db.setProperty("thumbnails", "metadata", dsgIDVariablename);

				StringBuilder pairs = new StringBuilder();
				StringBuilder vnames = new StringBuilder();
				pairs.append("\n");
				pairs.append(lonn+"-"+id+","+latn+"-"+id+"\n");
				String timen = timeVar.keySet().iterator().next();


				
				List<String> data_variable_ids = new ArrayList();
				List<String> data_variable_types = new ArrayList();
				for (Iterator subIt = data.keySet().iterator(); subIt.hasNext();) {
					String key = (String) subIt.next();
					vnames.append(key);
					if ( subIt.hasNext() ) {
						vnames.append(",");
					}
					if ( TRAJECTORY.contains(grid_type) ) {
						pairs.append(timen+"-"+id+","+key+"-"+id+"\n");
						pairs.append(key+"-"+id+","+latn+"-"+id+"\n");
						pairs.append(lonn+"-"+id+","+key+"-"+id+"\n");
					} else if ( PROFILE.contains(grid_type) && zn != null ) {
						pairs.append(key+"-"+id+","+zn+"-"+id+"\n");
					} else if ( TIMESERIES.contains(grid_type) ) {
						pairs.append(timen+"-"+id+","+key+"-"+id+"\n");
					} else if ( POINT.contains(grid_type) ) {
						if ( zn != null && !zn.equals("") ) {
							pairs.append(key+"-"+id+","+zn+"-"+id+"\n");
						}
						pairs.append(key+"-"+id+","+latn+"-"+id+"\n");
						pairs.append(lonn+"-"+id+","+key+"-"+id+"\n");
					}
					data_variable_ids.add(key+"-"+id);
					BaseType bt = dds.getVariable(key);
					String type = bt.getTypeName();
					data_variable_types.add(type);
				}

				// Pair up every data variable with every other.
				// Filter these in the UI to only use variables paired with current selection.
				StringBuilder data_pairs = new StringBuilder();

				for (int index = 0; index < data_variable_ids.size(); index++ ) {
					for (int jindex = index; jindex < data_variable_ids.size(); jindex++ ) {
						if ( index != jindex ) {
							if ( !data_variable_types.get(jindex).equals("String") && !data_variable_types.get(index).equals("String")) {
								data_pairs.append(data_variable_ids.get(index)+","+data_variable_ids.get(jindex)+"\n");
							} else {
								System.out.println("Rejected pair "+ data_variable_ids.get(index)+","+data_variable_ids.get(jindex)+" with types "+data_variable_types.get(index)+","+data_variable_types.get(jindex));
							}
						}
					}
				}


				pairs.append("\n");
				db.setProperty("thumbnails", "coordinate_pairs", pairs.toString());
				if ( data_pairs.length() > 0 ) {
					db.setProperty("thumbnails", "variable_pairs", data_pairs.toString());
				}
				db.setProperty("thumbnails", "variable_names", vnames.toString());

				// Add lat, lon and time to the data variable for output to the dataset

				String vn = lonVar.keySet().iterator().next();
				if ( !data.containsKey(vn) ) {
					data.put(vn, lonVar.get(vn));
				}
				vn = latVar.keySet().iterator().next();
				if ( !data.containsKey(vn ) ) {
					data.put(vn, latVar.get(vn));
				}
				vn = timeVar.keySet().iterator().next();
				if ( !data.containsKey(vn) ) {
					data.put(vn, timeVar.get(vn));
				}
				if ( zn != null && !data.containsKey(zn) ) {
					data.put(zn, zVar.get(zn));
				}
				for (Iterator dataIt = data.keySet().iterator(); dataIt.hasNext();) {
					String name = (String) dataIt.next();
					// May already be done because it's a sub set variable??
							boolean dummy = false;
					if ( !subsets.containsKey(name) ) {
						if (!dummy && !name.toLowerCase(Locale.ENGLISH).contains("time") && !name.toLowerCase(Locale.ENGLISH).contains("lat") && !name.toLowerCase(Locale.ENGLISH).contains("lon") && !name.toLowerCase(Locale.ENGLISH).contains("depth") ) {
							db.setProperty("tabledap_access", "dummy", name);
							dummy = true;
						}
						i++;
						AttributeTable var = data.get(name);
						VariableBean vb = new VariableBean();
						vb.setElement(name+"-"+id);

						vb.setUrl("#"+name);
						Attribute ua = var.getAttribute("units");
						if ( ua != null ) {
							String units = ua.getValueAt(0);
							vb.setUnits(units);
						} else {
							vb.setUnits("none");
						}
						Attribute ln = var.getAttribute("long_name");
						if ( ln != null ) {
							String longname = ln.getValueAt(0);
							vb.setName(longname);
						} else {
							vb.setName(name);
						}
						vb.setUrl("#"+name);
						vb.setGrid(gb);
						vb.addAttribute("grid_type", grid_type.toLowerCase(Locale.ENGLISH));
						variables.add(vb);   
					}

				}

				db.setProperty("tabledap_access", "table_variables", dsgIDVariablename);

				// add any variable properties.
				for (Iterator varid = variables.iterator(); varid.hasNext();) {
					VariableBean variableb = (VariableBean) varid.next();
					if ( varproperties != null && varproperties.length > 0 ) {
						for (int p = 0; p < varproperties.length; p++) {
							// Split n-1 times so any ":" after the third remain
							String[] parts = varproperties[p].split(":", 4);
							if ( variableb.getUrl().endsWith(parts[0]) ) {
								variableb.setProperty(parts[1], parts[2], parts[3]);
							}
						}
					}
				}
				
				
				db.addAllVariables(variables);

				File outputFile;
				if ( separate ) {
					outputFile = new File(id+".xml");
				} else {
					outputFile = new File("las_from_erddap.xml");
				}

				// Add all the tabledap_access properties

				//TODO "Profile"
				if (dsgIDVariablename != null){
					db.setProperty("tabledap_access", grid_type.toLowerCase(Locale.ENGLISH)+"_id", dsgIDVariablename);
				}
				db.setProperty("tabledap_access", "server", "TableDAP "+grid_type.toLowerCase(Locale.ENGLISH));
				db.setProperty("tabledap_access", "title", title);

				db.setProperty("tabledap_access", "id", id);

				if ( !default_supplied ) {
				   db.setProperty("ui", "default", "file:ui.xml#"+grid_type.toLowerCase(Locale.ENGLISH));
				}

				if ( !monthOfYear.keySet().isEmpty() ) {
					String name = monthOfYear.keySet().iterator().next();
					String mid = name+"-"+id;
					Element season = new Element("constraint_group");
					season.setAttribute("type", "season");            

					season.setAttribute("name", "by Season");
					Element con = new Element("constraint");
					con.setAttribute("widget", "month");
					Element variable = new Element("variable");
					variable.setAttribute("IDREF", mid);
					Element key = new Element("key");
					key.setText(name);
					con.addContent(key);
					con.addContent(variable);
					season.addContent(con);
					cons.addContent(season);
				}


				Element vrcg = new Element("constraint_group");
				vrcg.setAttribute("type", "variable");
				vrcg.setAttribute("name", "by Variable");
				cons.addContent(vrcg);

				Element valcg = new Element("constraint_group");
				valcg.setAttribute("type", "valid");
				valcg.setAttribute("name", "by Valid Data");
				cons.addContent(valcg);


				Element d = db.toXml();
				d.addContent(cons);
				datasetsE.addContent(d);            



				gridsE.addContent(gb.toXml());
				for (Iterator axesIt = axes.iterator(); axesIt.hasNext();) {
					AxisBean ab = (AxisBean) axesIt.next();
					axesE.addContent(ab.toXml());
				}
				if ( r.write ) {
					outputXML(outputFile, datasetsE, !separate);
					outputXML(outputFile, gridsE, true);
					outputXML(outputFile, axesE, true);
				}
				return r;
			}
			if ( subset_names == null ) {
				System.err.println("No cdm_trajectory_variables, timeseries_variables or profile_variables global attribute found in this data set.");
				r.write = false;
				r.type = "unknown";
			}
			if ( variableAttributes == null ) {
				System.err.println("No variables found.");
				r.type = "unknown";
				r.write = false;
			}

		} catch (Exception e) {
			r.write = false;
			r.type = "failed";
			String message = "";
			if ( e != null ) {
				String m = e.getMessage();
				if ( m != null )
					message = e.getMessage();
			}
			System.err.println("Error opening: "+url+id+" "+message);
			e.printStackTrace(System.err);
		} finally {
			if ( stream != null ) {
				try {
					stream.close();
				} catch (IOException e) {
					System.err.println("Error closing stream.  "+e.getMessage());
				}
			}
		}
		return r; 
	}
	public String[] getMinMax(JsonObject bounds, String name) {
		JsonArray rows = (JsonArray) ((JsonObject) (bounds.get("table"))).get("rows");
		JsonArray names = (JsonArray) ((JsonObject) (bounds.get("table"))).get("columnNames");
		int index = -1;
		for (int i = 0; i < names.size(); i++) {
			if ( names.get(i).getAsString().equals(name) ) {
				index = i;
			}
		}
		JsonArray row1 = (JsonArray) rows.get(0);
		JsonArray row2 = (JsonArray) rows.get(1);

		String min = ((JsonElement) row1.get(index)).getAsString();
		String max = ((JsonElement) row2.get(index)).getAsString();
		String[] minmax = new String[2];
		minmax[0] = min;
		minmax[1] = max;
		return minmax;
	}
	public VariableBean addSubset(String name, AttributeTable var) throws NoSuchAttributeException {
		VariableBean vb = new VariableBean();
		vb.setElement(name+"-"+id);
		Attribute ln = var.getAttribute("long_name");
		if ( ln != null ) {
			String longname = ln.getValueAt(0);
			vb.setName(longname);
		} else {
			vb.setName(name);
		}
		vb.setUnits("text");
		vb.setUrl("#"+name);
		vb.addAttribute("subset_variable", "true");
		vb.setGrid(gb);
		return vb;
	}
	public void outputXML(File outputFile, Element element, boolean append) {
		try {
			FileWriter xmlout = new FileWriter(outputFile, append);
			org.jdom.output.Format format = org.jdom.output.Format.getPrettyFormat();
			format.setLineSeparator(System.getProperty("line.separator"));
			XMLOutputter outputter =
					new XMLOutputter(format);
			outputter.output(element, xmlout);
			xmlout.write("\n");
			// Close the FileWriter
			xmlout.close();
		}
		catch (java.io.IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
