package gov.noaa.pmel.tmap.las.service.database;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.exception.LASRowLimitException;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASIconWebRowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.units.DateUnit;
public class IntermediateNetcdfFile {
    private final static Logger log = LoggerFactory.getLogger(IntermediateNetcdfFile.class.getName());
    protected String[] varNames;
    protected String zname="";
    protected String tname="";
    protected String xname="";
    protected String yname="";
    protected String time_units="";
    protected String time_format="";
    protected boolean convert_time = true;
    protected double missing=-999999999999.;
    protected boolean needsProfID = true;
    protected NetcdfFileWriteable netcdfFile = null;
    protected boolean has_cruise_id = false;
    protected boolean trajectory = true;
    String icon_webrowset_filename;
    public IntermediateNetcdfFile (String filename, boolean fill) throws LASException {
        log.debug("Create new empty netCDF file.");
        try {
			netcdfFile = NetcdfFileWriteable.createNew(filename, fill);
		} catch (IOException e) {
			throw new LASException(e.toString());
		}
    }
    public void create(ResultSet resultSet, LASBackendRequest lasBackendRequest) throws SQLException, IOException, InvalidRangeException, LASException {

        ResultSetMetaData resultSetMetadata = resultSet.getMetaData();
        
        icon_webrowset_filename = lasBackendRequest.getResultAsFile("icon_webrowset");
        
    	List variables = lasBackendRequest.getVariables();
    	for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
			String name = (String) varIt.next();
			String grid = lasBackendRequest.getDataAttribute(name, "grid_type");
			if ( grid != null ) {
				trajectory = trajectory && grid.toLowerCase(Locale.ENGLISH).equals("trajectory");
			}
		}
    
        boolean hasResults = resultSet.last();
        int indexSize = resultSet.getRow();
        resultSet.beforeFirst();
        ArrayList<Dimension> dimList = new ArrayList<Dimension>();
        String row_limit_property = lasBackendRequest.getDatabaseProperty("row_limit");
        if ( row_limit_property != null && !row_limit_property.equals("") ) {
        	int row_limit = Integer.valueOf(row_limit_property).intValue();
        	if ( indexSize > row_limit ) {
        		Dimension index = netcdfFile.addDimension("index", 1);
                dimList.add(index);
                String time_name = lasBackendRequest.getDatabaseProperty("time");
                netcdfFile.addVariable(time_name, DataType.DOUBLE, dimList);
                ArrayDouble.D1 data = new ArrayDouble.D1(1);
                Double d = new Double("-9999.");
                data.set(0, d);
                netcdfFile.addGlobalAttribute("query_result", "Request resulted in "+indexSize+" rows which exceeds the allowed limit of "+row_limit+" rows.");
                netcdfFile.create();
                netcdfFile.write(time_name, data);
        		throw new LASRowLimitException("Request resulted in "+indexSize+" rows which exceeds the allowed limit of "+row_limit+" rows.");
        	}
        }
        
        
        
        if ( !hasResults ) {
            // No results found.  Fix up a minimal file and return.
            Dimension index = netcdfFile.addDimension("index", 1);
            dimList.add(index);
            String time_name = lasBackendRequest.getDatabaseProperty("time");
            netcdfFile.addVariable(time_name, DataType.DOUBLE, dimList);
            ArrayDouble.D1 data = new ArrayDouble.D1(1);
            Double d = new Double("-9999.");
            data.set(0, d);
            netcdfFile.addGlobalAttribute("query_result", "No data found to match this request.");
            netcdfFile.create();
            netcdfFile.write(time_name, data);
            if ( trajectory ) {
            	LASIconWebRowSet icon_webrowset = new LASIconWebRowSet();
            	icon_webrowset.write(icon_webrowset_filename);
            }
            return;
        }
        time_units = lasBackendRequest.getDatabaseProperty("time_units");
        if ( time_units == null || time_units.equals("") ) {
            throw new LASException("time_units database property not found.");
        }
        time_format = lasBackendRequest.getDatabaseProperty("time_format");
        if ( time_format == null || time_format.equals("") ) {
            throw new LASException("time_format database property not found.");
        }
        // The Ferret formatted time_origin.
        DateUnit dateUnit;
		try {
			dateUnit = new DateUnit(time_units);
		} catch (Exception e) {
			throw new LASException(e.toString());
		}
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss");
        DateTime dt = new DateTime(dateUnit.getDateOrigin().getTime());
        String time_origin = fmt.withZone(DateTimeZone.UTC).print(dt);

        Dimension index = netcdfFile.addDimension("index", indexSize);
        
        // These are part of the PMEL "intermediate netcdf file" and are not needed for CF featureType files...
        // The only feature type we can deal with currently is a trajectory.
        if ( !trajectory ) {
        	Dimension dim_one = netcdfFile.addDimension("dim_one", 1);
        	Dimension trdim = netcdfFile.addDimension("trdim", 2);

        	dimList.clear();
        	dimList.add(trdim);

        	netcdfFile.addVariable("trdim", DataType.DOUBLE, dimList);
        	netcdfFile.addVariableAttribute("trdim", "units", time_units);
        	netcdfFile.addVariableAttribute("trdim", "point_spacing", "even");
        	netcdfFile.addVariableAttribute("trdim", "time_origin", time_origin);

        	netcdfFile.addVariable("trange", DataType.DOUBLE, dimList);
        	netcdfFile.addVariableAttribute("trange", "units", "hours");

        	dimList.clear();
        	dimList.add(dim_one);


        	netcdfFile.addVariable("NUMPROFS", DataType.FLOAT, dimList);
        	netcdfFile.addVariableAttribute("NUMPROFS", "long_name", "Number of Profiles");
        	netcdfFile.addVariableAttribute("NUMPROFS", "units", "unitless");

        	netcdfFile.addVariable("NUMOBS", DataType.FLOAT, dimList);
        	netcdfFile.addVariableAttribute("NUMOBS", "long_name", "Number of Observations");
        	netcdfFile.addVariableAttribute("NUMOBS", "units", "unitless");
        }
        
        dimList.clear();
        dimList.add(index);
        
        String miss_string = lasBackendRequest.getDatabaseProperty("missing");
        if ( miss_string == null || miss_string.equals("")) {
            throw new LASException ("Cannot find the missing value database_access property.");
        }
        
        missing=Double.valueOf(miss_string).doubleValue();
        varNames = new String[resultSetMetadata.getColumnCount()];
        // Find the coordinate variable names.
        for (int col = 1; col <= resultSetMetadata.getColumnCount(); col++) {
        	String colName = resultSetMetadata.getColumnName(col);
        	String variable = getVariableName(colName, lasBackendRequest, col);
        	if ( variable.equals("xax")) {
        		xname = colName;
        	} else if ( variable.equals("yax")) {
        		yname = colName;
        	} else if ( variable.equals("tax")) {
        		tname = colName;
        	} else if ( variable.equals("zax")) {
        		zname = colName;
        	}
        }
        StringBuffer coordinates = new StringBuffer();
        if ( !tname.equals("") ) {
        	coordinates.append(tname);
        }
        if ( !xname.equals("") ) {
        	if (coordinates.length() > 0 ) {
        		coordinates.append(",");
        	}
        	coordinates.append(xname);
        }
        if ( !yname.equals("") ) {
        	if ( coordinates.length() > 0 ) {
        		coordinates.append(",");
        	}
        	coordinates.append(yname);
        }
        if ( !zname.equals("") ) {
        	if ( coordinates.length()> 0 ) {
        		coordinates.append(",");
        	}
        	coordinates.append(zname);
        }
        // Create the variables.
        for (int col = 1; col <= resultSetMetadata.getColumnCount(); col++) {
            String colName = resultSetMetadata.getColumnName(col);
            String variable = getVariableName(colName, lasBackendRequest, col);
            if ( variable.equals("xax")) {
                dimList.clear();
                dimList.add(index);
                DataType type = netcdfTypeFromJDBCType(col, resultSetMetadata);
                // Leave double alone, promote everything else to float.
                if ( type == DataType.DOUBLE && 
                     resultSetMetadata.getColumnType(col) != java.sql.Types.NUMERIC) {
                   netcdfFile.addVariable(colName, DataType.DOUBLE, dimList);
                   netcdfFile.addVariableAttribute(colName, "missing", new Double(missing));
                } else {
                   netcdfFile.addVariable(colName, DataType.FLOAT, dimList);
                   netcdfFile.addVariableAttribute(colName, "missing", new Float(missing));
                }
                netcdfFile.addVariableAttribute(colName, "units", "degrees_east");
                netcdfFile.addVariableAttribute(colName, "long_name", "Longitude");
                netcdfFile.addVariableAttribute(colName, "database_table", resultSetMetadata.getTableName(col));
                netcdfFile.addVariableAttribute(colName, "_CoordinateAxisType", "Lon");
               
                
            } else if ( variable.equals("yax")) {
                dimList.clear();
                dimList.add(index);
                DataType type = netcdfTypeFromJDBCType(col, resultSetMetadata);
                if ( type == DataType.DOUBLE &&
                     resultSetMetadata.getColumnType(col) != java.sql.Types.NUMERIC) {
                   netcdfFile.addVariable(colName, DataType.DOUBLE, dimList);
                   netcdfFile.addVariableAttribute(colName, "missing", new Double(missing));
                } else {
                   netcdfFile.addVariable(colName, DataType.FLOAT, dimList);
                   netcdfFile.addVariableAttribute(colName, "missing", new Float(missing));
                }
                netcdfFile.addVariableAttribute(colName, "units", "degrees_north");
                netcdfFile.addVariableAttribute(colName, "long_name", "Latitude");
                netcdfFile.addVariableAttribute(colName, "missing", new Double(missing));
                netcdfFile.addVariableAttribute(colName, "database_table", resultSetMetadata.getTableName(col));
                netcdfFile.addVariableAttribute(colName, "_CoordinateAxisType", "Lat");
               
                
            } else if ( variable.equals("tax")) {
                // Always a "double"; hours since...
                dimList.clear();
                dimList.add(index);
                DataType type = netcdfTypeFromJDBCType(col, resultSetMetadata);
                if ( type == DataType.DOUBLE || type == DataType.FLOAT ) {
                   convert_time = false;
                }
                netcdfFile.addVariable(colName, 
                        DataType.DOUBLE, 
                        dimList);

                netcdfFile.addVariableAttribute(colName, "units", time_units);
                netcdfFile.addVariableAttribute(colName, "time_origin", time_origin);
                netcdfFile.addVariableAttribute(colName, "long_name", "Time");
                netcdfFile.addVariableAttribute(colName, "missing", new Double(missing));
                netcdfFile.addVariableAttribute(colName, "database_table", resultSetMetadata.getTableName(col));
                netcdfFile.addVariableAttribute(colName, "_CoordinateAxisType", "Time");
               
            } else if ( variable.equals("zax")) {
                dimList.clear();
                dimList.add(index);
                netcdfFile.addVariable(colName, 
                        netcdfTypeFromJDBCType(col, resultSetMetadata), 
                        dimList);
                netcdfFile.addVariableAttribute(colName, "units", lasBackendRequest.getDatabaseProperty("depth_units"));
                netcdfFile.addVariableAttribute(colName, "long_name", colName);
                netcdfFile.addVariableAttribute(colName, "missing", new Double(missing));
                netcdfFile.addVariableAttribute(colName, "database_table", resultSetMetadata.getTableName(col));
                String down = lasBackendRequest.getDatabaseProperty("positive");
                if ( down != null && down.equalsIgnoreCase("down") )  {
                    netcdfFile.addVariableAttribute(colName, "positive", down);
                    netcdfFile.addVariableAttribute(colName, "_CoordinateAxisType", "Depth");
                }
               
            } else {
            	String long_name="";
            	String units="";

            	if (variable.equals("CRUISE_ID")) {
            		long_name = "CRUISE ID";
            		units = "unitless";
            		has_cruise_id = true;

            	}
            	if (variable.equals("PROF_ID")) {
            		long_name = "Profile ID";
            		units = "unitless";
            		needsProfID = false;
            	}

            	if (lasBackendRequest.hasVariable(variable) ) {
            		long_name = lasBackendRequest.getDataAttribute(variable, "title");
            		String dsUnits = lasBackendRequest.getDataAttribute(variable, "units");
            		if (dsUnits != null) {
            			units = dsUnits;
            		}
            	}
            	// If it's a trajectory file don't write the cruise id as a separate variable.
            	if ( !variable.equals("CRUISE_ID") || (variable.equals("CRUISE_ID") && !trajectory ) ) {
            		if ( netcdfTypeFromJDBCType(col, resultSetMetadata) == DataType.CHAR ) {
            			dimList.clear();
            			dimList.add(index);
            			int width = resultSetMetadata.getPrecision(col);
            			if ( width <= 0 ) {
            				width = resultSetMetadata.getColumnDisplaySize(col);
            			}
            			Dimension char_var_width = netcdfFile.addDimension(resultSetMetadata.getColumnName(col)+"_width", width);
            			dimList.add(char_var_width);                                       
            			netcdfFile.addVariable(variable, netcdfTypeFromJDBCType(col, resultSetMetadata), dimList);
            			netcdfFile.addVariableAttribute(variable, "long_name", long_name);
            			netcdfFile.addVariableAttribute(variable, "units", units);
            			netcdfFile.addVariableAttribute(variable, "missing_value", "");
            			netcdfFile.addVariableAttribute(variable, "database_table", resultSetMetadata.getTableName(col));
            			netcdfFile.addVariableAttribute(variable, "coordinates", coordinates.toString());
            		} else {
            			dimList.clear();
            			dimList.add(index);
            			netcdfFile.addVariable(variable, netcdfTypeFromJDBCType(col, resultSetMetadata), dimList);
            			netcdfFile.addVariableAttribute(variable, "long_name", long_name);
            			netcdfFile.addVariableAttribute(variable, "units", units);
            			netcdfFile.addVariableAttribute(variable, "missing_value", new Double(missing));
            			netcdfFile.addVariableAttribute(variable, "database_table", resultSetMetadata.getTableName(col));
            			netcdfFile.addVariableAttribute(variable, "coordinates", coordinates.toString());
            		}
            	}
            }

        }
        if ( needsProfID && !trajectory) {
            dimList.clear();
            dimList.add(index);
            netcdfFile.addVariable("PROF_ID", DataType.DOUBLE, dimList);
            netcdfFile.addVariableAttribute("PROF_ID", "long_name", "Profile ID");
            netcdfFile.addVariableAttribute("PROF_ID", "units", "unitless");
            netcdfFile.addVariableAttribute("PROF_ID", "missing_value", new Double(-999.));
        }
        

        if ( has_cruise_id ) {
            /*
             * Seems to me that we should conform to this convention by listing
             * the data sequentially with a unique trajectory ID as an
             * observational variable, but maybe not.
            netcdfFile.addGlobalAttribute("cdm_datatype", "Trajectory");
            netcdfFile.addGlobalAttribute("trajectory_id", "CRUISE_ID");
            netcdfFile.addGlobalAttribute("trajectory_description", "CRUISE_ID");
            * 
            * for now go with Point data.
            */
        	
            if ( trajectory ) {
            	netcdfFile.addGlobalAttribute("featureType", "trajectory");
            	netcdfFile.addGlobalAttribute("Conventions", "CF-1.6");
            } else {
            	netcdfFile.addGlobalAttribute("cdm_datatype", "Point");
                netcdfFile.addAttribute(null, new Attribute("Conventions", "LAS Intermediate netCDF File, Unidata Observation Dataset v1.0"));
                netcdfFile.addAttribute(null, new Attribute("observationDimension", "index"));
            }
        } else {
            netcdfFile.addGlobalAttribute("cdm_datatype", "Point");
        }
        
        
        log.debug("filling netcdf file"); //debug
        fill(resultSet);
    }
    
    
    public String getVariableName(String colName, LASBackendRequest lasBackendRequest, int col) throws LASException {
        
        /* 
         * A database parameter might be a description of table and a column e.g. table_name.column_name,
         * but the column name will be just the column_name portion so we need to strip off the table_name
         * for purposes of this comparison.
         */
        
        String longitude = lasBackendRequest.getDatabaseProperty("longitude");
        if ( longitude != null && longitude.contains(".") ) {
            longitude = longitude.substring(longitude.indexOf(".")+1, longitude.length());
        }
        String latitude = lasBackendRequest.getDatabaseProperty("latitude");
        if (latitude != null && latitude.contains(".") ) {
            latitude = latitude.substring(latitude.indexOf(".")+1, latitude.length());
        }
        String time = lasBackendRequest.getDatabaseProperty("time");
        if ( time != null && time.contains(".") ) {
            time = time.substring(time.indexOf(".")+1, time.length());
        }
        String depth = lasBackendRequest.getDatabaseProperty("depth");
        if ( depth != null && depth.contains(".") ) {
            depth = depth.substring(depth.indexOf(".")+1, depth.length());
        }
        String cruise_id_name = (String)lasBackendRequest.getDatabaseProperty("cruiseID");
        if ( cruise_id_name != null && cruise_id_name.contains(".") ) {
            cruise_id_name = cruise_id_name.substring(cruise_id_name.indexOf(".")+1, cruise_id_name.length());
        }
        String profile_id_name = (String)lasBackendRequest.getDatabaseProperty("profID");
        if ( profile_id_name != null && profile_id_name.contains(".") ) {
            profile_id_name = profile_id_name.substring(profile_id_name.indexOf(".")+1, profile_id_name.length());
        }
        
        if ( longitude.equals(colName)) {
            varNames[col-1] = colName;
            return "xax";
        } else if ( latitude.equals(colName)) {
            varNames[col-1] = colName;
            return "yax";
        } else if ( time.equals(colName)) {
            varNames[col-1] = colName;
            return "tax";
        } else if ( depth.equals(colName)) {
            varNames[col-1] = colName;
            return "zax";
        } else {
            
            if (cruise_id_name != null && 
                    cruise_id_name != "" &&
                    colName.equals(cruise_id_name)) {
                varNames[col-1] = "CRUISE_ID";
                return "CRUISE_ID";
            }            
            if (profile_id_name != null && 
                    profile_id_name != "" &&
                    colName.equals(profile_id_name)) {
                 varNames[col-1]= "PROF_ID";
                 return "PROF_ID";
            }
            varNames[col-1] = colName;
            return colName;
        }
    }
    
    public DataType netcdfTypeFromJDBCType(int col, ResultSetMetaData resultSetMetadata) throws SQLException {
        int columnType = resultSetMetadata.getColumnType(col);
        if (columnType == Types.DOUBLE) {
            return DataType.DOUBLE;
        } else if ( columnType == Types.FLOAT) {
            return DataType.FLOAT;
        } else if ( columnType == Types.REAL) {
            return DataType.FLOAT;
        } else if ( columnType == Types.INTEGER) {
            return DataType.INT;
        } else if ( columnType == Types.DECIMAL) {
            return DataType.FLOAT;
        } else if ( columnType == Types.SMALLINT) {
            return DataType.SHORT;
        } else if ( columnType == Types.TINYINT) {
            return DataType.BYTE;
        } else if ( columnType == Types.NUMERIC) {
            if ( resultSetMetadata.getScale(col) < 0) {
               return DataType.DOUBLE;
            }
            else if ( resultSetMetadata.getScale(col) > 0 &&
                      resultSetMetadata.getPrecision(col) > 0 ) {
               return DataType.DOUBLE;
            } else if ( resultSetMetadata.getScale(col) == 0 && 
                        resultSetMetadata.getPrecision(col) > 0 ) {
               return DataType.INT;
            } else {
               return DataType.DOUBLE;
            }
        } else if ( columnType == Types.CHAR || columnType == Types.VARCHAR) {
            return DataType.CHAR;
        } else {
            return DataType.CHAR;
        }
    }
    
    public void fill(ResultSet resultSet) throws LASException, SQLException, IOException, InvalidRangeException {

    	// Count the number of obs that belong to each trajectory...
    	int current_count = 0;
    	// Keep the list of ids in order
    	List<Object> trajectory_ids = new ArrayList<Object>();
    	// Store the number of obs by id...
    	Map<Object, Integer> trajectory_counts = new HashMap<Object, Integer>();
    	DataType trajectory_data_type = null;
        int cruise_id_width = 0;

    	ArrayDouble.D1 PROF_ID = null;
    	float prof_id_num = 1.0f;

    	ResultSetMetaData resultSetMetadata = resultSet.getMetaData();

    	resultSet.last();
    	int indexSize = resultSet.getRow();
    	resultSet.beforeFirst();
    	ArrayList<Array> columns = new ArrayList<Array>();
    	for (int col = 1; col <= resultSetMetadata.getColumnCount(); col++) {
    		DataType type = netcdfTypeFromJDBCType(col, resultSetMetadata);
    		if ( varNames[col-1].equals(tname) ) {
    			ArrayDouble.D1 data = new ArrayDouble.D1(indexSize);
    			columns.add(data);
    		} 
    		// Force lat lon to float unless they are doubles,
    		// then use double
    		else if ( varNames[col-1].equals(xname) ||
    				varNames[col-1].equals(yname) ) {
    			if ( type == DataType.DOUBLE ) {
    				ArrayDouble.D1 data = new ArrayDouble.D1(indexSize);
    				columns.add(data);
    			} else {
    				ArrayFloat.D1 data = new ArrayFloat.D1(indexSize);
    				columns.add(data);
    			}

    		}
    		else {
    			if ( type == DataType.DOUBLE) {
    				ArrayDouble.D1 data = new ArrayDouble.D1(indexSize);
    				columns.add(data);
    			} else if (type == DataType.FLOAT) {
    				ArrayFloat.D1 data = new ArrayFloat.D1(indexSize);
    				columns.add(data);
    			} else if (type == DataType.INT) {
    				ArrayInt.D1 data = new ArrayInt.D1(indexSize);
    				columns.add(data);
    			} else if (type == DataType.CHAR) {
    				// Save the cruise_id width...
    				if ( varNames[col-1].equals("CRUISE_ID") ) {
    					cruise_id_width = resultSetMetadata.getPrecision(col);
    				}
    				ArrayChar.D2 data = new ArrayChar.D2(indexSize, resultSetMetadata.getPrecision(col));
    				columns.add(data);
    			}
    		}
    	}

    	if ( needsProfID ) {
    		PROF_ID = new ArrayDouble.D1(indexSize);
    	} else {
    		// combat null deference :-)
    		PROF_ID = new ArrayDouble.D1(1);
    	}

    	ArrayDouble.D1 trdim = new ArrayDouble.D1(2);
    	trdim.set(0, 999999999.);
    	trdim.set(1, -999999999);

    	ArrayDouble.D1 trange = new ArrayDouble.D1(2);
    	trange.set(0, 999999999.);
    	trange.set(1, -999999999.);

    	int index = 0;
    	DateTimeFormatter fmt = DateTimeFormat.forPattern(time_format).withZone(DateTimeZone.UTC);
    	DateUnit dateUnit;
    	try {
    		dateUnit = new DateUnit(time_units);
    	} catch (Exception e) {
    		throw new LASException(e.toString());
    	}
    	double geospatial_lat_min =  9999.0;
    	double geospatial_lat_max = -9999.0;
    	double geospatial_lon_min =  9999.0;
    	double geospatial_lon_max = -9999.0;        
    	double time_coverage_start = 999999999.0;
    	double time_coverage_end = -999999999.0;
    	while (resultSet.next()) {
    		for (int col = 1; col <= resultSetMetadata.getColumnCount(); col++) {                
    			DataType type = netcdfTypeFromJDBCType(col, resultSetMetadata);
    			if ( varNames[col-1].equals(tname) ) {
    				ArrayDouble.D1 data = (ArrayDouble.D1)columns.get(col-1);
    				Double time;
    				if ( convert_time ) {
    					String time_string = resultSet.getString(col);
    					if (time_string == null ) {
    						time = new Double(missing);
    					} else {
    						DateTime datetime = fmt.parseDateTime(time_string).withZone(DateTimeZone.UTC);
    						double t = dateUnit.makeValue(datetime.toDate());
    						time = new Double(t);
    					}
    				}
    				else {
    					time = resultSet.getDouble(col);
    					data.set(index, time);
    				}
    				data.set(index, time);
    				columns.set(col-1, data);
    				if ( time.doubleValue() < trdim.get(0)) {
    					trdim.set(0, time);
    					trange.set(0, time);
    				}
    				if ( time.doubleValue() > trdim.get(1)) {
    					trdim.set(1, time);
    					trange.set(1,time);
    				}
    				if ( time.doubleValue() < time_coverage_start ) {
    					time_coverage_start = time.doubleValue();
    				}
    				if ( time.doubleValue() > time_coverage_end ) {
    					time_coverage_end = time.doubleValue();
    				}
    			} else if (varNames[col-1].equals(xname) || 
    					varNames[col-1].equals(yname) ) {
    				if (type == DataType.DOUBLE ) {
    					ArrayDouble.D1 data = (ArrayDouble.D1)columns.get(col-1); 
    					Double d = resultSet.getDouble(col);
    					if ( d==null ) {
    						d = new Double(missing);
    					}
    					data.set(index,d);
    					columns.set(col-1, data);
    					if (varNames[col-1].equals(xname) ) {
    						if ( d.doubleValue() > geospatial_lon_max ) {
    							geospatial_lon_max = d.doubleValue();
    						}
    						if ( d.doubleValue() < geospatial_lon_min ) {
    							geospatial_lon_min = d.doubleValue();
    						}
    					} else if ( varNames[col-1].equals(yname) ) {
    						if ( d.doubleValue() > geospatial_lat_max ) {
    							geospatial_lat_max = d.doubleValue();
    						}
    						if ( d.doubleValue() < geospatial_lat_min ) {
    							geospatial_lat_min = d.doubleValue();
    						}
    					}
    				} else {
    					ArrayFloat.D1 data = (ArrayFloat.D1)columns.get(col-1); 
    					Float d = resultSet.getFloat(col);
    					if ( d==null ) {
    						d = new Float(missing);
    					}
    					data.set(index,d);
    					columns.set(col-1, data);
    					if (varNames[col-1].equals(xname) ) {
    						if ( d.doubleValue() > geospatial_lon_max ) {
    							geospatial_lon_max = d.doubleValue();
    						}
    						if ( d.doubleValue() < geospatial_lon_min ) {
    							geospatial_lon_min = d.doubleValue();
    						}
    					} else if ( varNames[col-1].equals(yname) ) {
    						if ( d.doubleValue() > geospatial_lat_max ) {
    							geospatial_lat_max = d.doubleValue();
    						}
    						if ( d.doubleValue() < geospatial_lat_min ) {
    							geospatial_lat_min = d.doubleValue();
    						}
    					}
    				}

    			} else {
    				boolean count_trajectory = false;

    				if ( varNames[col-1].equals("CRUISE_ID") && trajectory ) {
    					count_trajectory = true;
    				}
    				if ( type == DataType.DOUBLE) {
    					ArrayDouble.D1 data = (ArrayDouble.D1)columns.get(col-1);
    					Double d = resultSet.getDouble(col);
    					if (d==null) {
    						d = new Double(missing);
    					}
    					if ( count_trajectory && !trajectory_ids.contains(d) ) {
    						trajectory_data_type = DataType.DOUBLE;
    						trajectory_ids.add(d);
    						trajectory_counts.put(d, 1);
    					} else if ( count_trajectory && trajectory_ids.contains(d) ) {
    						int c = trajectory_counts.get(d).intValue();
    						c++;
    						trajectory_counts.put(d, c);
    					}	
    					data.set(index, d);
    					columns.set(col-1, data);
    					if (zname.equals(varNames[col-1]) && needsProfID ) {
    						if ( index == 0 && PROF_ID != null) {
    							PROF_ID.set(index, prof_id_num);
    						} else {
    							if ( data.get(index) <= data.get(index-1)) {
    								prof_id_num = prof_id_num + 1.f;
    							}
    							if ( PROF_ID != null)
    								PROF_ID.set(index, prof_id_num);
    						}
    					}    
    				} else if (type == DataType.FLOAT) {
    					ArrayFloat.D1 data = (ArrayFloat.D1)columns.get(col-1);
    					Float f = resultSet.getFloat(col);
    					if ( f==null) {
    						f = new Float(missing);
    					}
    					data.set(index, f);
    					columns.set(col-1, data);
    					if (zname.equals(varNames[col-1]) && needsProfID ) {
    						if ( index == 0 ) {
    							PROF_ID.set(index, prof_id_num);
    						} else {
    							if ( data.get(index) <= data.get(index-1)) {
    								prof_id_num = prof_id_num + 1.f;
    							}
    							PROF_ID.set(index, prof_id_num);
    						}
    					}
    				} else if (type == DataType.INT) {
    					ArrayInt.D1 data = (ArrayInt.D1)columns.get(col-1);
    					Integer i = resultSet.getInt(col);
    					if ( i==null) {
    						i = new Integer(Integer.MIN_VALUE);
    					}
    					data.set(index, i);
    					columns.set(col-1, data);
    					if (zname.equals(varNames[col-1]) && needsProfID ) {
    						if ( index == 0 ) {
    							PROF_ID.set(index, prof_id_num);
    						} else {
    							if ( data.get(index) <= data.get(index-1)) {
    								prof_id_num = prof_id_num + 1.f;
    							}
    							PROF_ID.set(index, prof_id_num);
    						}
    					}
    				} else if (type == DataType.CHAR) {
    					ArrayChar.D2 data = (ArrayChar.D2)columns.get(col-1);
    					String value = resultSet.getString(col);
    					if (value==null) {
    						value="";
    					}
    					if ( count_trajectory && !trajectory_ids.contains(value) ) {
    						trajectory_data_type = DataType.CHAR;
    						trajectory_ids.add(value);
    						trajectory_counts.put(value, 1);
    					} else if ( count_trajectory && trajectory_ids.contains(value) ) {
    						int c = trajectory_counts.get(value).intValue();
    						c++;
    						trajectory_counts.put(value, c);
    					}
    					data.setString(index, value);
    					columns.set(col-1,data);
    				}
    			}
    		}
    		index++;
    	}
    	// Create the trajectory related dimensions and variables
    	ArrayChar.D2 trajectory_id_char_data = null;
		ArrayInt.D1 trajectory_count_data = null;
    	if ( trajectory ) {  
    		LASIconWebRowSet icon_webrowset = new LASIconWebRowSet();
    		Dimension trajectory_dim = netcdfFile.addDimension("trajectory", trajectory_ids.size());
    		if ( trajectory_data_type != null && trajectory_data_type.equals(DataType.DOUBLE) ) {
    			
    		} else if ( trajectory_data_type != null &&  trajectory_data_type.equals(DataType.CHAR) ) {
    			trajectory_id_char_data = new ArrayChar.D2(trajectory_ids.size(), cruise_id_width);
    			trajectory_count_data = new ArrayInt.D1(trajectory_ids.size());
    			int inx = 0;
    			for (Iterator idIt = trajectory_ids.iterator(); idIt.hasNext();) {
					String key = (String) idIt.next();
					icon_webrowset.addId(key);
					int c = trajectory_counts.get(key);	
    				trajectory_id_char_data.setString(inx, key);
    				trajectory_count_data.set(inx, c);
    				inx++;
    			}
    			List<Dimension> dimList = new ArrayList<Dimension>();
    			dimList.add(trajectory_dim);
    			Dimension width = netcdfFile.addDimension("trajectory_width", cruise_id_width);
    			dimList.add(width);
    			netcdfFile.addVariable("trajectories", DataType.CHAR, dimList);
    			netcdfFile.addVariableAttribute("trajectories", "cf_role", "trajectory_id");	
    			dimList.clear();
    			dimList.add(trajectory_dim);
    			netcdfFile.addVariable("trajectory_counts", DataType.INT, dimList);
    			netcdfFile.addVariableAttribute("trajectory_counts", "trajectory_dimension", "index");
    			netcdfFile.addVariableAttribute("trajectory_counts", "long_name", "Number of observations in this trajectory.");
    		}
    		icon_webrowset.write(icon_webrowset_filename);
    	}
    	// Write the minimal data discovery attributes for "Unidata Observation Dataset v1.0" conventions then create the file.

    	// These don't appear in the CF standard so leave them out.
    	
    	if ( !trajectory ) {
    		netcdfFile.addGlobalAttribute("geospatial_lat_min", geospatial_lat_min);
    		netcdfFile.addGlobalAttribute("geospatial_lat_max", geospatial_lat_max);
    		netcdfFile.addGlobalAttribute("geospatial_lon_min", geospatial_lon_min);
    		netcdfFile.addGlobalAttribute("geospatial_lon_max", geospatial_lon_max);
    		netcdfFile.addGlobalAttribute("time_coverage_start", time_coverage_start + " " + time_units);
    		netcdfFile.addGlobalAttribute("time_coverage_end", time_coverage_end + " " + time_units);
    	}
    	
    	netcdfFile.create();
    	// Fill the trajectory related variables.
    	if ( trajectory ) {
    		if ( trajectory_data_type.equals(DataType.DOUBLE) ) {
    			
    		} else if ( trajectory_data_type.equals(DataType.CHAR) ) {
    			netcdfFile.write("trajectories", trajectory_id_char_data);  
    		}
    		netcdfFile.write("trajectory_counts", trajectory_count_data);
    	}
    	
    	// Write out the data to the newly created netCDF file.

    	for (int col = 1; col <= resultSetMetadata.getColumnCount(); col++) {
    		DataType type = netcdfTypeFromJDBCType(col, resultSetMetadata);
    		String var = varNames[col-1];           
    		if ( var.equals(tname) ) {
    			ArrayDouble.D1 array = (ArrayDouble.D1) columns.get(col-1);
    			netcdfFile.write(var, array);
    		}
    		else if ( var.equals(xname) ||
    				var.equals(yname) ) {
    			if (type == DataType.DOUBLE ) {
    				ArrayDouble.D1 array = (ArrayDouble.D1)columns.get(col-1); 
    				netcdfFile.write(var, array);
    			} else {
    				ArrayFloat.D1 array = (ArrayFloat.D1)columns.get(col-1); 
    				netcdfFile.write(var, array);
    			}
    		} 
    		else {
    			// For trajectory data, don't write the cruise id as a separate variable.  It's already taken care...
    			if (!var.equals("CRUISE_ID") || (var.equals("CRUISE_ID") && !trajectory ) ) {
    				if ( type == DataType.DOUBLE) {
    					ArrayDouble.D1 array = (ArrayDouble.D1) columns.get(col-1);
    					netcdfFile.write(var, array);
    				} else if (type == DataType.FLOAT) {
    					ArrayFloat.D1 array = (ArrayFloat.D1)columns.get(col-1);
    					netcdfFile.write(var, array);
    				} else if (type == DataType.INT) {
    					ArrayInt.D1 array = (ArrayInt.D1)columns.get(col-1);
    					netcdfFile.write(var, array);
    				} else if (type == DataType.CHAR) {
    					ArrayChar.D2 array = (ArrayChar.D2)columns.get(col-1);
    					netcdfFile.write(var, array);
    				}   
    			}
    		}
    	}
    	// TODO Need to count profiles if profile column exists.
    	// TODO Neet to set PROF_ID to 1 for all profiles if zax does not exist.
    	
    	// We don't create a PROF_ID in a trajectory file...
    	if (needsProfID && !trajectory) {
    		netcdfFile.write("PROF_ID", PROF_ID);
    	}

    	ArrayFloat.D1 value = new ArrayFloat.D1(1);
    	if ( !trajectory ) {
    		value.set(0, indexSize);
    		netcdfFile.write("NUMOBS", value);

    		value.set(0, prof_id_num);
    		netcdfFile.write("NUMPROFS", value);

    		netcdfFile.write("trdim", trdim);

    		netcdfFile.write("trange", trange);
    	}
    }
   
   public void create() throws IOException {
       netcdfFile.create();
   }
   public void close() throws IOException {
       netcdfFile.close();
   }
public NetcdfFileWriteable getNetcdfFile() {
    return netcdfFile;
}
}
