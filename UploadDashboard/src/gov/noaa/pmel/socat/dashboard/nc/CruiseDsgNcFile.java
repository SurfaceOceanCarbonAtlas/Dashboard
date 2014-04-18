package gov.noaa.pmel.socat.dashboard.nc;

import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.NetcdfFileWriter.Version;
import ucar.nc2.Variable;
import ucar.nc2.time.Calendar;
import ucar.nc2.time.CalendarDate;


public class CruiseDsgNcFile {

	private static final String VERSION = "CruiseDsgNcFile 1.0";
	private static final Calendar BASE_CALENDAR = Calendar.proleptic_gregorian;
	private static final CalendarDate BASE_DATE = CalendarDate.of(BASE_CALENDAR, 1970, 1, 1, 0, 0, 0);

	SocatMetadata metadata;
	List<SocatCruiseData> data;

	/**
	 * Create using the given SocatMetadata and SocatCruiseData objects.
	 * 
	 * @param metadata
	 * 		metadata for the cruise
	 * @param data
	 * 		list of data for the cruise
	 * @throws IllegalArgumentException
	 * 		if either argument is null, or 
	 * 		if the list of SocatCruiseData objects is empty
	 */
	public CruiseDsgNcFile(SocatMetadata metadata, List<SocatCruiseData> data) throws IllegalArgumentException {
		if ( metadata == null )
			throw new IllegalArgumentException("Invalid SocatMetadata given to the CruiseDsgNcFile constructor");
		if ( (data == null) || (data.size() < 1) )
			throw new IllegalArgumentException("Invalid SocatCruiseData list given to the CruiseDsgNcFile constructor");
		this.metadata = metadata;
		this.data = data;
	}

	/**
	 * Creates a NetCDF DSG file containing the contents of the SocatMetadata object 
	 * and list of SocatCruiseData objects contained in this instance.
	 * 
	 * @param dsgFilename
	 * 		NetCDF DSG file to create
	 * @throws Exception
	 */
	public void create(String dsgFilename) throws Exception {
		NetcdfFileWriter ncfile = NetcdfFileWriter.createNew(Version.netcdf3, dsgFilename);

		// According to the CF standard if a file only has one trajectory, the the trajectory dimension is not necessary.
		// However, who knows what would break downstream from this process without it...

		Dimension traj = ncfile.addDimension(null, "trajectory", 1);

		// There will be a number of trajectory variables of type character from the metadata.
		// Which is the longest?
		int maxchar = metadata.getMaxStringLength();
		Dimension stringlen = ncfile.addDimension(null, "string_length", maxchar);
		List<Dimension> trajdimsChar = new ArrayList<Dimension>();
		trajdimsChar.add(traj);
		trajdimsChar.add(stringlen);

		List<Dimension> trajdims = new ArrayList<Dimension>();
		trajdims.add(traj);

		Dimension d = ncfile.addDimension(null, "obs", data.size());
		List<Dimension> dims = new ArrayList<Dimension>();
		dims.add(d);

		Dimension charDim = ncfile.addDimension(null, "char_length", 1);
		List<Dimension> charVarDims = new ArrayList<Dimension>();
		charVarDims.add(d);
		charVarDims.add(charDim);

		Field[] metaFields = metadata.getClass().getDeclaredFields();
		for ( Field f : metaFields )
			f.setAccessible(true);
		Field[] dataFields = data.get(0).getClass().getDeclaredFields();
		for ( Field f : dataFields )
			f.setAccessible(true);

		// Make character netCDF variables of all the string metadata.
		for ( Field f : metaFields ) {
			if ( f.getType().equals(String.class) && ! Modifier.isStatic(f.getModifiers()) ) {
				String name = f.getName();
				String varName = Constants.SHORT_NAME.get(name);
				Variable var = ncfile.addVariable(null, varName, DataType.CHAR, trajdimsChar);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to add the variable " + 
							varName + " for metadata field " + name);
				String longName = Constants.LONG_NAME.get(name);
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

		Variable var = ncfile.addVariable(null, "rowSize", DataType.DOUBLE, trajdims);
		ncfile.addVariableAttribute(var, new Attribute("sample_dimension", "obs"));
		ncfile.addVariableAttribute(var, new Attribute("long_name", "Number of Observations"));

		for ( Field f : dataFields ) {
			if ( ! Modifier.isStatic(f.getModifiers()) ) {
				String name = f.getName();
				String varName = Constants.SHORT_NAME.get(name);
				var = null;
				Number missVal = null;
				Class<?> type = f.getType();
				if ( type.equals(Double.class) || type.equals(Double.TYPE) ) {
					var = ncfile.addVariable(null, varName, DataType.DOUBLE, dims);
					missVal = SocatCruiseData.FP_MISSING_VALUE;
				} 
				else if ( type.equals(Integer.class) || type.equals(Integer.TYPE) ) {
					var = ncfile.addVariable(null, varName, DataType.INT, dims);
					missVal = SocatCruiseData.INT_MISSING_VALUE;
				} 
				else if ( type.equals(Character.class) || type.equals(Character.TYPE) ) {
					var = ncfile.addVariable(null, varName, DataType.CHAR, charVarDims);
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

		var = ncfile.addVariable(null, "time", DataType.DOUBLE, dims);
		ncfile.addVariableAttribute(var, new Attribute("missing_value", SocatCruiseData.FP_MISSING_VALUE));
		ncfile.addVariableAttribute(var, new Attribute("_FillValue", SocatCruiseData.FP_MISSING_VALUE));
		ncfile.addVariableAttribute(var, new Attribute("units", "seconds since 1970-01-01 00:00:00"));
		ncfile.addVariableAttribute(var, new Attribute("long_name", "time"));
		ncfile.addVariableAttribute(var, new Attribute("standard_name", "time"));
		ncfile.addVariableAttribute(var, new Attribute("ioos_category", "Time"));

		ncfile.addGroupAttribute(null, new Attribute("History", VERSION));
		ncfile.addGroupAttribute(null, new Attribute("featureType", "Trajectory"));
		ncfile.addGroupAttribute(null, new Attribute("Conventions", "CF-1.6"));

		// Add remaining (non-String) metadata as attributes
		if ( ! metadata.getWestmostLongitude().isNaN() )
			ncfile.addGroupAttribute(null, new Attribute("geospatial_lon_min", metadata.getWestmostLongitude()));
		if ( ! metadata.getEastmostLongitude().isNaN() )
			ncfile.addGroupAttribute(null, new Attribute("geospatial_lon_max", metadata.getEastmostLongitude()));
		if ( ! metadata.getSouthmostLatitude().isNaN() )
			ncfile.addGroupAttribute(null, new Attribute("geospatial_lat_min", metadata.getSouthmostLatitude()));
		if ( ! metadata.getNorthmostLatitude().isNaN() )
			ncfile.addGroupAttribute(null, new Attribute("geospatial_lat_max", metadata.getNorthmostLatitude()));
		if ( ! metadata.getBeginTime().equals(SocatMetadata.DATE_MISSING_VALUE) ) {
			CalendarDate start = CalendarDate.of(metadata.getBeginTime());
			ncfile.addGroupAttribute(null, new Attribute("time_coverage_start", start.toString()));
		}
		if ( ! metadata.getEndTime().equals(SocatMetadata.DATE_MISSING_VALUE) ) {
			CalendarDate end = CalendarDate.of(metadata.getEndTime());
			ncfile.addGroupAttribute(null, new Attribute("time_converage_end", end.toString()));
		}

		ncfile.create();
		// The header has been created.  Now let's fill it up.

		for ( Field f : metaFields ) {
			if ( f.getType().equals(String.class) && ! Modifier.isStatic(f.getModifiers()) ) {
				String varName = Constants.SHORT_NAME.get(f.getName());
				var = ncfile.findVariable(varName);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);
				ArrayChar.D2 values = new ArrayChar.D2(1, maxchar);
				values.setString(0, (String) f.get(metadata));
				ncfile.write(var, values);
			}
		}

		var = ncfile.findVariable("rowSize");
		if ( var == null )
			throw new RuntimeException("Unexpected failure to find ncfile variable rowSize");
		ArrayDouble.D1 obscount = new ArrayDouble.D1(1);
		obscount.set(0, (double) data.size());
		ncfile.write(var, obscount);

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
					long lvalue = date.getDifferenceInMsecs(BASE_DATE);
					double value = (double) lvalue / 1000.0;
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

}
