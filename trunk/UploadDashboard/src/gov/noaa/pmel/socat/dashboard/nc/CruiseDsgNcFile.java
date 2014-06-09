package gov.noaa.pmel.socat.dashboard.nc;

import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.NetcdfFileWriter.Version;
import ucar.nc2.Variable;
import ucar.nc2.time.Calendar;
import ucar.nc2.time.CalendarDate;


public class CruiseDsgNcFile extends File {

	private static final long serialVersionUID = -3372957474829495590L;

	private static final String VERSION = "CruiseDsgNcFile 1.0";
	private static final Calendar BASE_CALENDAR = Calendar.proleptic_gregorian;
	private static final CalendarDate BASE_DATE = CalendarDate.of(BASE_CALENDAR, 1970, 1, 1, 0, 0, 0);

	/**
	 * See {@link java.io.File#File(java.lang.String)}
	 */
	public CruiseDsgNcFile(String filename) {
		super(filename);
	}

	/**
	 * Creates this NetCDF DSG file with the contents of the given 
	 * SocatMetadata object and list of SocatCruiseData objects.

	 * @param metadata
	 * 		metadata for the cruise
	 * @param data
	 * 		list of data for the cruise
	 * @throws IllegalArgumentException
	 * 		if either argument is null, or 
	 * 		if the list of SocatCruiseData objects is empty
	 * 
	 * @param dsgFilename
	 * 		NetCDF DSG file to create
	 * @throws IllegalArgumentException
	 * 		if metadata or data is invalid
	 * @throws IOException
	 * 		if creating the NetCDF file throws one
	 * @throws InvalidRangeException
	 * 		if creating the NetCDF file throws one
	 * @throws IllegalAccessException
	 * 		if creating the NetCDF file throws one
	 */
	public void create(SocatMetadata metadata, List<SocatCruiseData> data) 
			throws IllegalArgumentException, IOException, InvalidRangeException, IllegalAccessException {

		if ( metadata == null )
			throw new IllegalArgumentException("Invalid SocatMetadata given to the CruiseDsgNcFile constructor");
		if ( (data == null) || (data.size() < 1) )
			throw new IllegalArgumentException("Invalid SocatCruiseData list given to the CruiseDsgNcFile constructor");

		NetcdfFileWriter ncfile = NetcdfFileWriter.createNew(Version.netcdf3, getPath());

		// According to the CF standard if a file only has one trajectory, the the trajectory dimension is not necessary.
		// However, who knows what would break downstream from this process without it...

		Dimension traj = ncfile.addDimension(null, "trajectory", 1);

		// There will be a number of trajectory variables of type character from the metadata.
		// Which is the longest?
		int maxchar = metadata.getMaxStringLength();
		Dimension stringlen = ncfile.addDimension(null, "string_length", maxchar);
		List<Dimension> trajStringDims = new ArrayList<Dimension>();
		trajStringDims.add(traj);
		trajStringDims.add(stringlen);

		List<Dimension> trajDims = new ArrayList<Dimension>();
		trajDims.add(traj);

		Dimension obslen = ncfile.addDimension(null, "obs", data.size());
		List<Dimension> dataDims = new ArrayList<Dimension>();
		dataDims.add(obslen);

		Dimension charlen = ncfile.addDimension(null, "char_length", 1);
		List<Dimension> charDataDims = new ArrayList<Dimension>();
		charDataDims.add(obslen);
		charDataDims.add(charlen);

		Field[] metaFields = metadata.getClass().getDeclaredFields();
		for ( Field f : metaFields )
			f.setAccessible(true);
		Field[] dataFields = data.get(0).getClass().getDeclaredFields();
		for ( Field f : dataFields )
			f.setAccessible(true);

		ncfile.addGroupAttribute(null, new Attribute("featureType", "Trajectory"));
		ncfile.addGroupAttribute(null, new Attribute("Conventions", "CF-1.6"));
		ncfile.addGroupAttribute(null, new Attribute("history", VERSION));

		Variable var = ncfile.addVariable(null, "num_obs", DataType.DOUBLE, trajDims);
		ncfile.addVariableAttribute(var, new Attribute("sample_dimension", "obs"));
		ncfile.addVariableAttribute(var, new Attribute("long_name", "Number of Observations"));

		// Make netCDF variables of all the metadata.
		for ( Field f : metaFields ) {
			if ( ! Modifier.isStatic(f.getModifiers()) ) {
				String name = f.getName();
				String varName = Constants.SHORT_NAME.get(name);
				if ( varName == null )
					throw new RuntimeException("Unexpected missing short name for " + name);
				var = null;
				Number missVal = null;
				Class<?> type = f.getType();
				if ( type.equals(String.class) ) {
					var = ncfile.addVariable(null, varName, DataType.CHAR, trajStringDims);
					missVal = null;
				} 
				else if ( type.equals(Double.class) || type.equals(Double.TYPE) ) {
					var = ncfile.addVariable(null, varName, DataType.DOUBLE, trajDims);
					missVal = SocatCruiseData.FP_MISSING_VALUE;
				} 
				else if ( type.equals(Date.class) ) {
					var = ncfile.addVariable(null, varName, DataType.DOUBLE, trajDims);
					missVal = Double.valueOf(SocatMetadata.DATE_MISSING_VALUE.getTime() / 1000.0);
				}
				else
					throw new RuntimeException("Unexpected metadata field type " + 
							type.getSimpleName() + " for variable " + name);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to add the variable " + 
							varName + " for metadata field " + name);

				if ( missVal != null ) {
					ncfile.addVariableAttribute(var, new Attribute("missing_value", missVal));
					ncfile.addVariableAttribute(var, new Attribute("_FillValue", missVal));
				}
				String units = Constants.UNITS.get(name);
				if ( units != null ) {
					ncfile.addVariableAttribute(var, new Attribute("units", units));
				}
				String longName = Constants.LONG_NAME.get(name);
				if ( longName == null )
					throw new RuntimeException("Unexpected missing long name for " + name);
				ncfile.addVariableAttribute(var, new Attribute("long_name", longName));
				if ( name.equals("expocode")) {
					ncfile.addVariableAttribute(var, new Attribute("cf_role", "trajectory_id"));
				}
				String stdName = Constants.STANDARD_NAMES.get(name);
				if ( stdName != null ) {
					ncfile.addVariableAttribute(var, new Attribute("standard_name", stdName));
				}
				String category = Constants.IOOS_CATEGORIES.get(name);
				if ( category != null ) {
					ncfile.addVariableAttribute(var, new Attribute("ioos_category", category));
				}
			}
		}

		// Make netCDF variables of all the data.
		for ( Field f : dataFields ) {
			if ( ! Modifier.isStatic(f.getModifiers()) ) {
				String name = f.getName();
				String varName = Constants.SHORT_NAME.get(name);
				if ( varName == null )
					throw new RuntimeException("Unexpected missing short name for " + name);
				var = null;
				Number missVal = null;
				Class<?> type = f.getType();
				if ( type.equals(Double.class) || type.equals(Double.TYPE) ) {
					var = ncfile.addVariable(null, varName, DataType.DOUBLE, dataDims);
					missVal = SocatCruiseData.FP_MISSING_VALUE;
				} 
				else if ( type.equals(Integer.class) || type.equals(Integer.TYPE) ) {
					var = ncfile.addVariable(null, varName, DataType.INT, dataDims);
					missVal = SocatCruiseData.INT_MISSING_VALUE;
				} 
				else if ( type.equals(Character.class) || type.equals(Character.TYPE) ) {
					var = ncfile.addVariable(null, varName, DataType.CHAR, charDataDims);
					missVal = null;
				} 
				else
					throw new RuntimeException("Unexpected data field type " + 
							type.getSimpleName() + " for variable " + name);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to add the variable " + 
							varName + " for data field " + name);

				if ( missVal != null ) {
					ncfile.addVariableAttribute(var, new Attribute("missing_value", missVal));
					ncfile.addVariableAttribute(var, new Attribute("_FillValue", missVal));
				}
				String units = Constants.UNITS.get(name);
				if ( units != null ) {
					ncfile.addVariableAttribute(var, new Attribute("units", units));
				}
				String longName = Constants.LONG_NAME.get(name);
				if ( longName == null )
					throw new RuntimeException("Unexpected missing long name for " + name);
				ncfile.addVariableAttribute(var, new Attribute("long_name", longName));
				if ( name.endsWith("Depth") ) {
					ncfile.addVariableAttribute(var, new Attribute("positive", "down"));
				}
				String stdName = Constants.STANDARD_NAMES.get(name);
				if ( stdName != null ) {
					ncfile.addVariableAttribute(var, new Attribute("standard_name", stdName));
				}
				String category = Constants.IOOS_CATEGORIES.get(name);
				if ( category != null ) {
					ncfile.addVariableAttribute(var, new Attribute("ioos_category", category));
				}
			}
		}

		var = ncfile.addVariable(null, "time", DataType.DOUBLE, dataDims);
		ncfile.addVariableAttribute(var, new Attribute("missing_value", SocatCruiseData.FP_MISSING_VALUE));
		ncfile.addVariableAttribute(var, new Attribute("_FillValue", SocatCruiseData.FP_MISSING_VALUE));
		ncfile.addVariableAttribute(var, new Attribute("units", "seconds since 1970-01-01T00:00:00Z"));
		ncfile.addVariableAttribute(var, new Attribute("long_name", "time"));
		ncfile.addVariableAttribute(var, new Attribute("standard_name", "time"));
		ncfile.addVariableAttribute(var, new Attribute("ioos_category", "Time"));

		ncfile.create();

		// The header has been created.  Now let's fill it up.

		var = ncfile.findVariable("num_obs");
		if ( var == null )
			throw new RuntimeException("Unexpected failure to find ncfile variable num_obs");
		ArrayDouble.D1 obscount = new ArrayDouble.D1(1);
		obscount.set(0, (double) data.size());
		ncfile.write(var, obscount);

		for ( Field f : metaFields ) {
			if ( ! Modifier.isStatic(f.getModifiers()) ) {
				String varName = Constants.SHORT_NAME.get(f.getName());
				var = ncfile.findVariable(varName);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);
				Class<?> type = f.getType();
				if ( type.equals(String.class) ) {
					ArrayChar.D2 mvar = new ArrayChar.D2(1, maxchar);
					mvar.setString(0, (String) f.get(metadata));
					ncfile.write(var, mvar);
				}
				else if ( type.equals(Double.class) || type.equals(Double.TYPE) ) {
					ArrayDouble.D1 mvar = new ArrayDouble.D1(1);
					Double dvalue = (Double) f.get(metadata);
					if ( dvalue.isNaN() )
						dvalue = SocatCruiseData.FP_MISSING_VALUE;
					mvar.set(0, dvalue);
					ncfile.write(var, mvar);
				}
				else if ( type.equals(Date.class) ) {
					ArrayDouble.D1 mvar = new ArrayDouble.D1(1);
					Date dateVal = (Date) f.get(metadata);
					mvar.set(0, Double.valueOf(dateVal.getTime() / 1000.0));
					ncfile.write(var, mvar);
				}
				else
					throw new RuntimeException("Unexpected metadata field type " + 
							type.getSimpleName() + " for variable " + varName);
			}
		}

		for ( Field f : dataFields ) {
			if ( ! Modifier.isStatic(f.getModifiers()) ) {
				String varName = Constants.SHORT_NAME.get(f.getName());
				var = ncfile.findVariable(varName);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);
				Class<?> type = f.getType();
				if ( type.equals(Double.class) || type.equals(Double.TYPE) ) {
					ArrayDouble.D1 dvar = new ArrayDouble.D1(data.size());
					for (int index = 0; index < data.size(); index++) {
						SocatCruiseData datarow = (SocatCruiseData) data.get(index);
						Double dvalue = (Double) f.get(datarow);
						if ( dvalue.isNaN() )
							dvalue = SocatCruiseData.FP_MISSING_VALUE;
						dvar.set(index, dvalue);
					}
					ncfile.write(var, dvar);
				}
				else if ( type.equals(Integer.class) || type.equals(Integer.TYPE) ) {
					ArrayInt.D1 dvar = new ArrayInt.D1(data.size());
					for (int index = 0; index < data.size(); index++) {
						SocatCruiseData datarow = (SocatCruiseData) data.get(index);
						Integer dvalue = (Integer) f.get(datarow);
						dvar.set(index, dvalue);
					}
					ncfile.write(var, dvar);
				}
				else if ( type.equals(Character.class) || type.equals(Character.TYPE) ) {
					ArrayChar.D2 dvar = new ArrayChar.D2(data.size(), 1);
					for (int index = 0; index < data.size(); index++) {
						SocatCruiseData datarow = (SocatCruiseData) data.get(index);
						Character dvalue = (Character) f.get(datarow);
						dvar.set(index, 0, dvalue);
					}
					ncfile.write(var, dvar);
				}
				else
					throw new RuntimeException("Unexpected data field type " + 
							type.getSimpleName() + " for variable " + varName);
			}
		}

		var = ncfile.findVariable("time");
		if ( var == null )
			throw new RuntimeException("Unexpected failure to find ncfile variable time");
		ArrayDouble.D1 values = new ArrayDouble.D1(data.size());
		for (int index = 0; index < data.size(); index++) {
			SocatCruiseData datarow = (SocatCruiseData) data.get(index);
			Integer year = datarow.getYear();
			Integer month = datarow.getMonth();
			Integer day = datarow.getDay();
			Integer hour = datarow.getHour();
			Integer minute = datarow.getMinute();
			Double second = datarow.getSecond();
			Integer sec;
			if ( second.isNaN() || (second == SocatCruiseData.FP_MISSING_VALUE) ) {
				sec = 0;
			}
			else {
				sec = (int) Math.round(second);
			}
			if ( (year != SocatCruiseData.INT_MISSING_VALUE) && 
				 (month != SocatCruiseData.INT_MISSING_VALUE) && 
				 (day != SocatCruiseData.INT_MISSING_VALUE) && 
				 (hour != SocatCruiseData.INT_MISSING_VALUE) && 
				 (minute != SocatCruiseData.INT_MISSING_VALUE) ) {
				try {
					CalendarDate date = CalendarDate.of(BASE_CALENDAR, year, month, day, hour, minute, sec);
					double value = date.getDifferenceInMsecs(BASE_DATE) / 1000.0;
					values.set(index, value);
				} catch (Exception ex) {
					values.set(index, SocatCruiseData.FP_MISSING_VALUE);
				}
			}
			else {
				values.set(index, SocatCruiseData.FP_MISSING_VALUE);
			}
			ncfile.write(var, values);
		}

		ncfile.close();
	}

	/**
	 * Updates this DSG file with the given WOCE flags.
	 * Optionally will also update the data type, region ID, and 
	 * row number in the WOCE flags from the data in this DSG file. 
	 * 
	 * @param woceEvent
	 * 		WOCE flags to set
	 * @param updateWoceEvent
	 * 		if true, update the WOCE flags from data in this DSG file
	 * @throws IllegalArgumentException
	 * 		if the DSG file or the WOCE flags are not valid
	 * @throws IOException
	 * 		if opening, reading from, or writing to to the DSG file throws one
	 * @throws InvalidRangeException 
	 * 		if writing the update WOCE flags to the DSG file throws one 
	 */
	public void updateWoceFlags(SocatWoceEvent woceEvent, boolean updateWoceEvent) 
			throws IllegalArgumentException, IOException, InvalidRangeException {

		NetcdfFileWriter ncfile = NetcdfFileWriter.openExisting(getPath());

		Variable var = ncfile.findVariable("longitude");
		if ( var == null ) 
			throw new IllegalArgumentException("Unable to find longitude variable in " + getName());
		ArrayDouble.D1 longitudes = (ArrayDouble.D1) var.read();

		var = ncfile.findVariable("latitude");
		if ( var == null )
			throw new IllegalArgumentException("Unable to find latitude variable in " + getName());
		ArrayDouble.D1 latitudes = (ArrayDouble.D1) var.read();

		var = ncfile.findVariable("time");
		if ( var == null ) 
			throw new IllegalArgumentException("Unable to find time variable in " + getName());
		ArrayDouble.D1 times = (ArrayDouble.D1) var.read();

		ArrayChar.D2 regionIDs;
		if ( updateWoceEvent ) {
			var = ncfile.findVariable("region_id");
			if ( var == null )
				throw new IllegalArgumentException("Unable to find region_id variable in " + getName());
			regionIDs = (ArrayChar.D2) var.read(); 
		}
		else {
			regionIDs = null;
		}

		String dataname = woceEvent.getColumnName();
		if ( updateWoceEvent ) {
			dataname = Constants.VARIABLE_NAMES.get(dataname);
			if ( dataname == null )
				throw new IllegalArgumentException("Unknown variable name " + woceEvent.getColumnName());
			woceEvent.setColumnName(dataname);
		}
		ArrayDouble.D1 datavalues;
		if ( "geoposition".equals(dataname) ) {
			// WOCE on longitude/latitude/time
			datavalues = null;
		}
		else {
			var = ncfile.findVariable(dataname);
			if ( var == null )
				throw new IllegalArgumentException("Unable to find " + dataname + " variable in " + getName());
			datavalues = (ArrayDouble.D1) var.read(); 
		}

		// 
		Variable wocevar = ncfile.findVariable("WOCE_" + dataname);
		if ( wocevar == null )
			throw new IllegalArgumentException("Unable to find WOCE_" + dataname + " variable in " + getName());
		ArrayChar.D2 wocevalues = (ArrayChar.D2) wocevar.read();

		char newFlag = woceEvent.getFlag();

		// Identify the data points using a round-robin search 
		// just in case there is more than one matching point
		int startIdx = 0;
		int arraySize = (int) times.getSize();
		HashSet<Integer> assignedRowIndices = new HashSet<Integer>(); 
		for ( DataLocation dataloc : woceEvent.getLocations() ) {
			boolean valueFound = false;
			int idx;
			for (idx = startIdx; idx < arraySize; idx++) {
				if ( dataMatches(dataloc, longitudes, latitudes, times, datavalues, idx) ) {
					if ( assignedRowIndices.add(idx) ) {
						valueFound = true;
						break;
					}
				}
			}
			if ( idx >= arraySize ) {
				for (idx = 0; idx < startIdx; idx++) {
					if ( dataMatches(dataloc, longitudes, latitudes, times, datavalues, idx) ) {
						if ( assignedRowIndices.add(idx) ) {
							valueFound = true;
							break;
						}
					}
				}
			}
			if ( ! valueFound ) 
				throw new IllegalArgumentException("Unable to find data location \n" +
						dataloc.toString() + " \n in " + getName());
			wocevalues.set(idx, 0, newFlag);
			if ( updateWoceEvent ) {
				dataloc.setRowNumber(idx + 1);
				dataloc.setRegionID(regionIDs.get(idx, 0));
			}
			// Start the next search from the next data point
			startIdx = idx + 1;
		}

		if ( updateWoceEvent ) {
			// Assign the data type of the column from the variable name
			woceEvent.setDataType(Constants.VARIABLE_TYPES.get(woceEvent.getColumnName()));
		}

		// Save the updated WOCE flags to the DSG file
		ncfile.write(wocevar, wocevalues);
		ncfile.close();
	}

	/**
	 * Compares the data location information given in a DataLocation with the
	 * longitude, latitude, time, and (if applicable) data value at a given 
	 * index into arrays of these values.
	 * 
	 * @param dataloc
	 * 		data location to compare
	 * @param longitudes
	 * 		array of longitudes to use
	 * @param latitudes
	 * 		array of latitudes to use
	 * @param times
	 * 		array of times (seconds since 1970-01-01 00:00:00) to use
	 * @param datavalues
	 * 		if not null, array of data values to use
	 * @param idx
	 * 		index into the arrays of the values to compare
	 * @return
	 * 		true if the data locations match
	 */
	private boolean dataMatches(DataLocation dataloc, ArrayDouble.D1 longitudes,
			ArrayDouble.D1 latitudes, ArrayDouble.D1 times, ArrayDouble.D1 datavalues, int idx) {
		final double rtol = 1.0E-6;
		final double atol = 1.0E-4;

		if ( ! DashboardUtils.closeTo(dataloc.getLongitude(), longitudes.get(idx), rtol, atol) )
			return false;

		if ( ! DashboardUtils.closeTo(dataloc.getLatitude(), latitudes.get(idx), rtol, atol) )
			return false;

		if ( ! DashboardUtils.closeTo(dataloc.getLatitude(), latitudes.get(idx), rtol, atol) )
			return false;

		if ( ! DashboardUtils.closeTo(dataloc.getDataDate().getTime() / 1000.0, times.get(idx), rtol, atol) )
			return false;

		if ( datavalues != null ) {
			if ( ! DashboardUtils.closeTo(dataloc.getDataValue(), datavalues.get(idx), rtol, atol) )
				return false;
		}

		return true;
	}

}
