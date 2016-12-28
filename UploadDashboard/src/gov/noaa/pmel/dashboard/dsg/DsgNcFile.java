package gov.noaa.pmel.dashboard.dsg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import gov.noaa.pmel.dashboard.datatype.CharDashDataType;
import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.NetcdfFileWriter.Version;
import ucar.nc2.Variable;
import ucar.nc2.time.Calendar;
import ucar.nc2.time.CalendarDate;
import uk.ac.uea.socat.omemetadata.OmeMetadata;


public class DsgNcFile extends File {

	private static final long serialVersionUID = 8793930730574156281L;

	private static final String DSG_VERSION = "DsgNcFile 2.0";
	private static final Calendar BASE_CALENDAR = Calendar.proleptic_gregorian;
	/** 1970-01-01 00:00:00 */
	private static final CalendarDate BASE_DATE = CalendarDate.of(BASE_CALENDAR, 1970, 1, 1, 0, 0, 0);
	private static final String TIME_ORIGIN_ATTRIBUTE = "01-JAN-1970 00:00:00";

	private DsgMetadata metadata;
	private ArrayList<DsgData> dataList;

	/**
	 * See {@link java.io.File#File(java.lang.String)}
	 * The internal metadata and data list references are set null.
	 */
	public DsgNcFile(String filename) {
		super(filename);
		metadata = null;
		dataList = null;
	}

	/**
	 * See {@link java.io.File#File(java.io.File,java.lang.String)}
	 * The internal metadata and data list references are set null.
	 */
	public DsgNcFile(File parent, String child) {
		super(parent, child);
		metadata = null;
		dataList = null;
	}

	/**
	 * Adds the missing_value, _FillValue, long_name, standard_name, ioos_category, 
	 * and units attributes to the given variables in the given NetCDF file.
	 * 
	 * @param ncfile
	 * 		NetCDF file being written containing the variable
	 * @param var
	 * 		the variables to add attributes to
	 * @param missVal
	 * 		if not null, the value for the missing_value and _FillValue attributes
	 * @param longName
	 * 		if not {@link DashboardUtils#STRING_MISSING_VALUE}, the value for the 
	 * 		long_name attribute; cannot be null
	 * @param standardName
	 * 		if not {@link DashboardUtils#STRING_MISSING_VALUE}, the value for the 
	 * 		standard_name attribute; cannot be null
	 * @param ioosCategory
	 * 		if not {@link DashboardUtils#STRING_MISSING_VALUE}, the value for the 
	 * 		ioos_category attribute; cannot be null
	 * @param units
	 * 		if not {@link DashboardUtils#STRING_MISSING_VALUE}, the value for the 
	 * 		units attribute; cannot be null
	 */
	private void addAttributes(NetcdfFileWriter ncfile, Variable var, Number missVal, 
			String longName, String standardName, String ioosCategory, String units) {
		if ( missVal != null ) {
			ncfile.addVariableAttribute(var, new Attribute("missing_value", missVal));
			ncfile.addVariableAttribute(var, new Attribute("_FillValue", missVal));
		}
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(longName) ) {
			ncfile.addVariableAttribute(var, new Attribute("long_name", longName));
		}
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(standardName) ) {
			ncfile.addVariableAttribute(var, new Attribute("standard_name", standardName));
		}
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(ioosCategory) ) {
			ncfile.addVariableAttribute(var, new Attribute("ioos_category", ioosCategory));
		}
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(units) ) {
			ncfile.addVariableAttribute(var, new Attribute("units", units));
		}
	}

	/**
	 * Creates this NetCDF DSG file with the contents of the given 
	 * DsgMetadata object and list of DsgData objects.
	 * The internal metadata and data list references are set to 
	 * the given arguments. 
	 * 
	 * @param mdata
	 * 		metadata for the cruise
	 * @param data
	 * 		list of data for the cruise
	 * @throws IllegalArgumentException
	 * 		if either argument is null,
	 * 		if the list of DsgData objects is empty, or
	 * 		if a date/time in the data is missing or invalid
	 * @throws IOException
	 * 		if creating the NetCDF file throws one
	 * @throws InvalidRangeException
	 * 		if creating the NetCDF file throws one
	 * @throws IllegalAccessException
	 * 		if creating the NetCDF file throws one
	 */
	public void create(DsgMetadata mdata, ArrayList<DsgData> data) 
			throws IllegalArgumentException, IOException, InvalidRangeException, IllegalAccessException {
		metadata = mdata;
		dataList = data;

		if ( metadata == null )
			throw new IllegalArgumentException("DsgMetadata given to create cannot be null");
		if ( (dataList == null) || (dataList.size() < 1) )
			throw new IllegalArgumentException("DsgData list given to create cannot be null or empty");

		NetcdfFileWriter ncfile = NetcdfFileWriter.createNew(Version.netcdf3, getPath());
		try {
			// According to the CF standard if a file only has one trajectory, 
			// then the trajectory dimension is not necessary.
			// However, who knows what would break downstream from this process without it...
			Dimension traj = ncfile.addDimension(null, "trajectory", 1);

			// There will be a number of trajectory variables of type character from the metadata.
			// Which is the longest?
			int maxchar = metadata.getMaxStringLength();
			Dimension stringlen = ncfile.addDimension(null, "string_length", maxchar);
			List<Dimension> trajStringDims = new ArrayList<Dimension>();
			trajStringDims.add(traj);
			trajStringDims.add(stringlen);

			Dimension charlen = ncfile.addDimension(null, "char_length", 1);
			List<Dimension> trajCharDims = new ArrayList<Dimension>();
			trajCharDims.add(traj);
			trajCharDims.add(charlen);

			List<Dimension> trajDims = new ArrayList<Dimension>();
			trajDims.add(traj);

			Dimension obslen = ncfile.addDimension(null, "obs", dataList.size());
			List<Dimension> dataDims = new ArrayList<Dimension>();
			dataDims.add(obslen);

			List<Dimension> charDataDims = new ArrayList<Dimension>();
			charDataDims.add(obslen);
			charDataDims.add(charlen);

			ncfile.addGroupAttribute(null, new Attribute("featureType", "Trajectory"));
			ncfile.addGroupAttribute(null, new Attribute("Conventions", "CF-1.6"));
			ncfile.addGroupAttribute(null, new Attribute("history", DSG_VERSION));

			// Add the "num_obs" variable which will be assigned using the number of data points
			Variable var = ncfile.addVariable(null, "num_obs", DataType.INT, trajDims);
			ncfile.addVariableAttribute(var, new Attribute("sample_dimension", "obs"));
			ncfile.addVariableAttribute(var, new Attribute("long_name", "Number of Observations"));
			ncfile.addVariableAttribute(var, new Attribute("missing_value", DashboardUtils.INT_MISSING_VALUE));
			ncfile.addVariableAttribute(var, new Attribute("_FillValue", DashboardUtils.INT_MISSING_VALUE));

			// Make netCDF variables of all the metadata and data variables
			String varName;
			for (  StringDashDataType dtype : metadata.getStringVariables().keySet() ) {
				// Metadata Strings
				varName = dtype.getVarName();
				var = ncfile.addVariable(null, varName, DataType.CHAR, trajStringDims);
				// No missing_value, _FillValue, or units for strings
				addAttributes(ncfile, var, null, dtype.getDescription(), 
						dtype.getStandardName(), dtype.getCategoryName(), DashboardUtils.STRING_MISSING_VALUE);
				if ( DashboardServerUtils.DATASET_ID.typeNameEquals(dtype) ) {
					ncfile.addVariableAttribute(var, new Attribute("cf_role", "trajectory_id"));
				}
			}

			for (  CharDashDataType dtype : metadata.getCharVariables().keySet() ) {
				// Metadata characters
				varName = dtype.getVarName();
				var = ncfile.addVariable(null, varName, DataType.CHAR, trajCharDims);
				// No missing_value, _FillValue, or units for characters
				addAttributes(ncfile, var, null, dtype.getDescription(), 
						dtype.getStandardName(), dtype.getCategoryName(), DashboardUtils.STRING_MISSING_VALUE);
			}

			for (  IntDashDataType dtype : metadata.getIntVariables().keySet() ) {
				// Metadata Integers
				varName = dtype.getVarName();
				var = ncfile.addVariable(null, varName, DataType.INT, trajDims);
				addAttributes(ncfile, var, DashboardUtils.INT_MISSING_VALUE, dtype.getDescription(), 
						dtype.getStandardName(), dtype.getCategoryName(), dtype.getUnits().get(0));
			}

			for (  DoubleDashDataType dtype : metadata.getDoubleVariables().keySet() ) {
				// Metadata Doubles
				varName = dtype.getVarName();
				var = ncfile.addVariable(null, varName, DataType.DOUBLE, trajDims);
				addAttributes(ncfile, var, DashboardUtils.FP_MISSING_VALUE, dtype.getDescription(), 
						dtype.getStandardName(), dtype.getCategoryName(), dtype.getUnits().get(0));
				if ( dtype.getUnits().get(0).equals(DashboardServerUtils.TIME_UNITS.get(0)) ) {
					// Additional attribute giving the time origin (although also mentioned in the units)
					ncfile.addVariableAttribute(var, new Attribute("time_origin", TIME_ORIGIN_ATTRIBUTE));
				}
			}

			for (  IntDashDataType dtype : dataList.get(0).getIntegerVariables().keySet() ) {
				// Data Integers
				varName = dtype.getVarName();
				var = ncfile.addVariable(null, varName, DataType.INT, dataDims);
				addAttributes(ncfile, var, DashboardUtils.INT_MISSING_VALUE, dtype.getDescription(), 
						dtype.getStandardName(), dtype.getCategoryName(), dtype.getUnits().get(0));
			}

			for (  CharDashDataType dtype : dataList.get(0).getCharacterVariables().keySet() ) {
				// Data Characters
				varName = dtype.getVarName();
				var = ncfile.addVariable(null, varName, DataType.CHAR, charDataDims);
				// No missing_value, _FillValue, or units for characters
				addAttributes(ncfile, var, null, dtype.getDescription(), 
						dtype.getStandardName(), dtype.getCategoryName(), DashboardUtils.STRING_MISSING_VALUE);
			}

			for (  DoubleDashDataType dtype : dataList.get(0).getDoubleVariables().keySet() ) {
				// Data Doubles
				varName = dtype.getVarName();
				var = ncfile.addVariable(null, varName, DataType.DOUBLE, dataDims);
				addAttributes(ncfile, var, DashboardUtils.FP_MISSING_VALUE, dtype.getDescription(), 
						dtype.getStandardName(), dtype.getCategoryName(), dtype.getUnits().get(0));
				if ( DashboardServerUtils.TIME.typeNameEquals(dtype) ) {
					// Additional attribute giving the time origin (although also mentioned in the units)
					ncfile.addVariableAttribute(var, new Attribute("time_origin", TIME_ORIGIN_ATTRIBUTE));
				}
				if ( dtype.getStandardName().endsWith("depth") ) {
					ncfile.addVariableAttribute(var, new Attribute("positive", "down"));
				}
			}

			// The "time" variable should have been one of the known data file variables,
			// although it is probably all-missing.  It will be assigned below using the 
			// year, month, day, hour, minute, and (optionally) second values for each data point

			ncfile.create();

			// The header has been created.  Now let's fill it up.

			var = ncfile.findVariable("num_obs");
			if ( var == null )
				throw new RuntimeException("Unexpected failure to find ncfile variable num_obs");
			ArrayInt.D1 obscount = new ArrayInt.D1(1);
			obscount.set(0, dataList.size());
			ncfile.write(var, obscount);

			for (  Entry<StringDashDataType,String> entry : metadata.getStringVariables().entrySet() ) {
				// Metadata Strings
				varName = entry.getKey().getVarName();
				var = ncfile.findVariable(varName);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);
				String dvalue = entry.getValue();
				if ( dvalue == null )
					dvalue = "";
				ArrayChar.D2 mvar = new ArrayChar.D2(1, maxchar);
				mvar.setString(0, dvalue);
				ncfile.write(var, mvar);
			}

			for (  Entry<CharDashDataType,Character> entry : metadata.getCharVariables().entrySet() ) {
				// Metadata characters
				varName = entry.getKey().getVarName();
				var = ncfile.findVariable(varName);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);
				Character dvalue = entry.getValue();
				if ( dvalue == null )
					dvalue = ' ';
				ArrayChar.D2 mvar = new ArrayChar.D2(1, 1);
				mvar.setString(0, dvalue.toString());
				ncfile.write(var, mvar);
			}

			for ( Entry<IntDashDataType,Integer> entry : metadata.getIntVariables().entrySet() ) {
				// Metadata Integers
				varName = entry.getKey().getVarName();
				var = ncfile.findVariable(varName);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);
				Integer dvalue = entry.getValue();
				if ( dvalue == null )
					dvalue = DashboardUtils.INT_MISSING_VALUE;
				ArrayInt.D1 mvar = new ArrayInt.D1(1);
				mvar.set(0, dvalue);
				ncfile.write(var, mvar);
			}
			
			for ( Entry<DoubleDashDataType,Double> entry : metadata.getDoubleVariables().entrySet() ) {
				// Metadata Doubles
				varName = entry.getKey().getVarName();
				var = ncfile.findVariable(varName);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);
				Double dvalue = entry.getValue();
				if ( (dvalue == null) || dvalue.isNaN() || dvalue.isInfinite() )
					dvalue = DashboardUtils.FP_MISSING_VALUE;
				ArrayDouble.D1 mvar = new ArrayDouble.D1(1);
				mvar.set(0, dvalue);
				ncfile.write(var, mvar);
			}

			for (  IntDashDataType dtype : dataList.get(0).getIntegerVariables().keySet() ) {
				// Data Integers
				varName = dtype.getVarName();
				var = ncfile.findVariable(varName);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);
				ArrayInt.D1 dvar = new ArrayInt.D1(dataList.size());
				for (int index = 0; index < dataList.size(); index++) {
					Integer dvalue = dataList.get(index).getIntegerVariables().get(dtype);
					if ( dvalue == null )
						dvalue = DashboardUtils.INT_MISSING_VALUE;
					dvar.set(index, dvalue);
				}
				ncfile.write(var, dvar);
			}

			for (  CharDashDataType dtype : dataList.get(0).getCharacterVariables().keySet() ) {
				// Data Characters
				varName = dtype.getVarName();
				var = ncfile.findVariable(varName);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);
				ArrayChar.D2 dvar = new ArrayChar.D2(dataList.size(), 1);
				for (int index = 0; index < dataList.size(); index++) {
					Character dvalue = dataList.get(index).getCharacterVariables().get(dtype);
					if ( dvalue == null )
						dvalue = ' ';
					dvar.set(index, 0, dvalue);
				}
				ncfile.write(var, dvar);
			}

			for (  DoubleDashDataType dtype : dataList.get(0).getDoubleVariables().keySet() ) {
				// Data Doubles
				varName = dtype.getVarName();
				var = ncfile.findVariable(varName);
				if ( var == null )
					throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);
				ArrayDouble.D1 dvar = new ArrayDouble.D1(dataList.size());
				for (int index = 0; index < dataList.size(); index++) {
					Double dvalue = dataList.get(index).getDoubleVariables().get(dtype);
					if ( (dvalue == null) || dvalue.isNaN() || dvalue.isInfinite() )
						dvalue = DashboardUtils.FP_MISSING_VALUE;
					dvar.set(index, dvalue);
				}
				ncfile.write(var, dvar);
			}

			// Reassign the time variable using the year, month, day, hour, 
			// minute, and (optionally) second values for each data point
			varName = DashboardServerUtils.TIME.getVarName();
			var = ncfile.findVariable(varName);
			if ( var == null )
				throw new RuntimeException("Unexpected failure to find ncfile variable '" + varName + "'");
			ArrayDouble.D1 values = new ArrayDouble.D1(dataList.size());
			for (int index = 0; index < dataList.size(); index++) {
				DsgData datarow = dataList.get(index);
				Integer year = datarow.getYear();
				if ( year == DashboardUtils.INT_MISSING_VALUE )
					throw new IllegalArgumentException("No year is given");
				Integer month = datarow.getMonth();
				if ( month == DashboardUtils.INT_MISSING_VALUE )
					throw new IllegalArgumentException("No month is given");
				Integer day = datarow.getDay();
				if ( day == DashboardUtils.INT_MISSING_VALUE )
					throw new IllegalArgumentException("No day is given");
				Integer hour = datarow.getHour();
				if ( hour == DashboardUtils.INT_MISSING_VALUE )
					throw new IllegalArgumentException("No hour is given");
				Integer minute = datarow.getMinute();
				if ( minute == DashboardUtils.INT_MISSING_VALUE )
					throw new IllegalArgumentException("No month is given");
				Double second = datarow.getSecond();
				Integer sec;
				if ( second.isNaN() || (second == DashboardUtils.FP_MISSING_VALUE) ) {
					sec = 0;
				}
				else {
					// Truncate - don't deal with roll-overs such as from Feb 28 23:59:59.75;
					// furthermore, Ferret will overwrite with the fractional seconds. 
					sec = (int) Math.round(Math.floor(second));
				}
				try {
					CalendarDate date = CalendarDate.of(BASE_CALENDAR, year, month, day, hour, minute, sec);
					double value = date.getDifferenceInMsecs(BASE_DATE) / 1000.0;
					values.set(index, value);
				} catch (Exception ex) {
					throw new IllegalArgumentException("Invalid timestamp " + 
							year + "-" + month + "-" + day + " " + 
							hour + ":" + minute + ":" + sec);
				}
			}
			ncfile.write(var, values);
		} finally {
			ncfile.close();
		}
	}

	/**
	 * Creates and assigns the internal metadata 
	 * reference from the contents of this netCDF DSG file.
	 * 
	 * @param metadataTypes
	 * 		known data types 
	 * @return
	 * 		names of the metadata fields not assigned from this 
	 * 		netCDF file (will have its default/missing value)
	 * @throws IOException
	 * 		if there are problems opening or reading from the netCDF file
	 */
	public ArrayList<String> readMetadata(KnownDataTypes metadataTypes) throws IOException{
		ArrayList<String> namesNotFound = new ArrayList<String>();
		NetcdfFile ncfile = NetcdfFile.open(getPath());
		try {
			// Create the metadata with default (missing) values
			metadata = new DsgMetadata(metadataTypes);

			for ( DashDataType<?> dtype : metadataTypes.getKnownTypesSet() ) {
				String varName = dtype.getVarName();
				Variable var = ncfile.findVariable(varName);
				if ( var == null ) {
					namesNotFound.add(varName);
					continue;
				}
				if ( dtype instanceof StringDashDataType ) {
					ArrayChar.D2 mvar = (ArrayChar.D2) var.read();
					metadata.setStringVariableValue((StringDashDataType) dtype, mvar.getString(0));
				}
				else if ( dtype instanceof CharDashDataType ) {
					ArrayChar.D2 mvar = (ArrayChar.D2) var.read();
					String strval = mvar.getString(0);
					Character charval;
					if ( strval.length() > 0 )
						charval = strval.charAt(0);
					else
						charval = ' ';
					metadata.setCharVariableValue((CharDashDataType) dtype, charval);
				}
				else if ( dtype instanceof IntDashDataType ) {
					ArrayInt.D1 mvar = (ArrayInt.D1) var.read();
					metadata.setIntVariableValue((IntDashDataType) dtype, mvar.getInt(0));
				}
				else if ( dtype instanceof DoubleDashDataType ) {
					ArrayDouble.D1 mvar = (ArrayDouble.D1) var.read();
					metadata.setDoubleVariableValue((DoubleDashDataType) dtype, mvar.getDouble(0));
				}
				else {
					throw new RuntimeException("Unexpected data class name '" + 
							dtype.getDataClassName() + "' for variable '" + varName + "'");
				}
			}
		} finally {
			ncfile.close();
		}
		return namesNotFound;
	}

	/**
	 * Creates and assigns the internal data list
	 * reference from the contents of this netCDF DSG file.
	 * 
	 * @return
	 * 		names of the data fields not assigned from this 
	 * 		netCDF file (will have its default/missing value)
	 * @throws IOException
	 * 		if there are problems opening or reading from the netCDF file
	 * @throws IllegalArgumentException
	 * 		if the netCDF file is invalid.  Currently it must have a
	 * 		'time' variable and all data variables must have the same
	 * 		number of values as this variable.
	 */
	public ArrayList<String> readData(KnownDataTypes knownTypes) 
			throws IOException, IllegalArgumentException {
		ArrayList<String> namesNotFound = new ArrayList<String>();
		NetcdfFile ncfile = NetcdfFile.open(getPath());
		try {
			// Get the number of data points from the length of the time 1D array
			String varName = DashboardServerUtils.TIME.getVarName();
			Variable var = ncfile.findVariable(varName);
			if ( var == null )
				throw new IllegalArgumentException("Unable to find variable '" + 
						varName + "' in " + getName());
			int numData = var.getShape(0);

			// Create the list of data values, all with default (missing) values
			dataList = new ArrayList<DsgData>(numData);
			for (int k = 0; k < numData; k++)
				dataList.add(new DsgData(knownTypes));

			for ( DashDataType<?> dtype : knownTypes.getKnownTypesSet() ) {
				varName = dtype.getVarName();
				var = ncfile.findVariable(varName);
				if ( var == null ) {
					namesNotFound.add(varName);
					continue;
				}
				if ( var.getShape(0) != numData )
					throw new IllegalArgumentException("Number of values for '" + varName + 
							"' (" + Integer.toString(var.getShape(0)) + ") does not match " +
							"the number of values for 'time' (" + Integer.toString(numData) + ")");
				if ( dtype instanceof IntDashDataType ) {
					ArrayInt.D1 dvar = (ArrayInt.D1) var.read();
					for (int k = 0; k < numData; k++)
						dataList.get(k).setIntegerVariableValue((IntDashDataType) dtype, dvar.get(k));
				}
				else if ( dtype instanceof CharDashDataType ) {
					ArrayChar.D2 dvar = (ArrayChar.D2) var.read();
					for (int k = 0; k < numData; k++)
						dataList.get(k).setCharacterVariableValue((CharDashDataType) dtype, dvar.get(k,0));
				}
				else if ( dtype instanceof DoubleDashDataType ) {
					ArrayDouble.D1 dvar = (ArrayDouble.D1) var.read();
					for (int k = 0; k < numData; k++)
						dataList.get(k).setDoubleVariableValue((DoubleDashDataType) dtype, dvar.get(k));
				}
				else {
					throw new RuntimeException("Unexpected data class name '" + 
							dtype.getDataClassName() + "' for variable '" + varName + "'");
				}
			}
		} finally {
			ncfile.close();
		}
		return namesNotFound;
	}

	/**
	 * @return
	 * 		the internal metadata reference; may be null
	 */
	public DsgMetadata getMetadata() {
		return metadata;
	}

	/**
	 * @return
	 * 		the internal data list reference; may be null
	 */
	public ArrayList<DsgData> getDataList() {
		return dataList;
	}

	/**
	 * Reads and returns the array of data values for the specified variable
	 * contained in this DSG file.  The variable must be saved in the DSG file
	 * as characters.  Empty strings are changed to a single blank character.
	 * For some variables, this DSG file must have been processed by Ferret, 
	 * such as when saved using 
	 * {@link DsgNcFileHandler#saveCruise(OmeMetadata, DashboardDatasetData, String)}
	 * for the data values to be meaningful.
	 * 
	 * @param varName
	 * 		name of the variable to read
	 * @return
	 * 		array of values for the specified variable
	 * @throws IOException
	 * 		if there is a problem opening or reading from this DSG file
	 * @throws IllegalArgumentException
	 * 		if the variable name is invalid, or
	 * 		if the variable is not a single-character array variable
	 */
	public char[] readCharVarDataValues(String varName) 
								throws IOException, IllegalArgumentException {
		char[] dataVals;
		NetcdfFile ncfile = NetcdfFile.open(getPath());
		try {
			Variable var = ncfile.findVariable(varName);
			if ( var == null )
				throw new IllegalArgumentException("Unable to find variable '" + 
						varName + "' in " + getName());
			ArrayChar.D2 cvar = (ArrayChar.D2) var.read();
			if ( var.getShape(1) != 1 ) 
				throw new IllegalArgumentException("Variable '" + varName + 
						"' is not a single-character array variable in " + getName());
			int numVals = var.getShape(0);
			dataVals = new char[numVals];
			for (int k = 0; k < numVals; k++) {
				char value = cvar.get(k,0);
				if ( value == (char) 0 )
					value = ' ';
				dataVals[k] = value;
			}
		} finally {
			ncfile.close();
		}
		return dataVals;
	}

	/**
	 * Reads and returns the array of data values for the specified variable
	 * contained in this DSG file.  The variable must be saved in the DSG file
	 * as integers.  For some variables, this DSG file must have been processed 
	 * by Ferret, such as when saved using 
	 * {@link DsgNcFileHandler#saveCruise(OmeMetadata, DashboardDatasetData, String)}
	 * for the data values to be meaningful.
	 * 
	 * @param varName
	 * 		name of the variable to read
	 * @return
	 * 		array of values for the specified variable
	 * @throws IOException
	 * 		if there is a problem opening or reading from this DSG file
	 * @throws IllegalArgumentException
	 * 		if the variable name is invalid
	 */
	public int[] readIntVarDataValues(String varName) 
								throws IOException, IllegalArgumentException {
		int[] dataVals;
		NetcdfFile ncfile = NetcdfFile.open(getPath());
		try {
			Variable var = ncfile.findVariable(varName);
			if ( var == null )
				throw new IllegalArgumentException("Unable to find variable '" + 
						varName + "' in " + getName());
			ArrayInt.D1 dvar = (ArrayInt.D1) var.read();
			int numVals = var.getShape(0);
			dataVals = new int[numVals];
			for (int k = 0; k < numVals; k++) {
				dataVals[k] = dvar.get(k);
			}
		} finally {
			ncfile.close();
		}
		return dataVals;
	}

	/**
	 * Reads and returns the array of data values for the specified variable
	 * contained in this DSG file.  The variable must be saved in the DSG file
	 * as doubles.  NaN and infinite values are changed to 
	 * {@link DsgData#FP_MISSING_VALUE}.  For some variables, this 
	 * DSG file must have been processed by Ferret, such as when saved using 
	 * {@link DsgNcFileHandler#saveCruise(OmeMetadata, DashboardDatasetData, String)}
	 * for the data values to be meaningful.
	 * 
	 * @param varName
	 * 		name of the variable to read
	 * @return
	 * 		array of values for the specified variable
	 * @throws IOException
	 * 		if there is a problem opening or reading from this DSG file
	 * @throws IllegalArgumentException
	 * 		if the variable name is invalid
	 */
	public double[] readDoubleVarDataValues(String varName) 
								throws IOException, IllegalArgumentException {
		double[] dataVals;
		NetcdfFile ncfile = NetcdfFile.open(getPath());
		try {
			Variable var = ncfile.findVariable(varName);
			if ( var == null )
				throw new IllegalArgumentException("Unable to find variable '" + 
						varName + "' in " + getName());
			ArrayDouble.D1 dvar = (ArrayDouble.D1) var.read();
			int numVals = var.getShape(0);
			dataVals = new double[numVals];
			for (int k = 0; k < numVals; k++) {
				double value = dvar.get(k);
				if ( Double.isNaN(value) || Double.isInfinite(value) )
					value = DashboardUtils.FP_MISSING_VALUE;
				dataVals[k] = value;
			}
		} finally {
			ncfile.close();
		}
		return dataVals;
	}

	/**
	 * Updates the string recorded for the given variable in this DSG file.
	 * 
	 * @param varName
	 * 		name of the variable in this DSG file
	 * @param newValue
	 * 		new string value to record in this DSG file
	 * @throws IllegalArgumentException
	 * 		if this DSG file is not valid
	 * @throws IOException
	 * 		if opening or updating this DSG file throws one
	 * @throws InvalidRangeException 
	 * 		if writing the updated string to this DSG file throws one 
	 * 		or if the updated string is too long for this DSG file
	 */
	public void updateStringVarValue(String varName, String newValue) 
		throws IllegalArgumentException, IOException, InvalidRangeException {
		NetcdfFileWriter ncfile = NetcdfFileWriter.openExisting(getPath());
		try {
			Variable var = ncfile.findVariable(varName);
			if ( var == null ) 
				throw new IllegalArgumentException("Unable to find variable '" + 
						varName + "' in " + getName());
			int varLen = var.getShape(1);
			if ( newValue.length() > varLen )
				throw new InvalidRangeException("Length of new string (" + 
						newValue.length() + ") exceeds available space (" + 
						varLen + ")");
			ArrayChar.D2 valArray = new ArrayChar.D2(1, varLen);
			valArray.setString(0, newValue);
			ncfile.write(var, valArray);
		} finally {
			ncfile.close();
		}
	}

	/**
	 * Writes the given array of characters as the values 
	 * for the given character data variable.
	 * 
	 * @param varName
	 * 		character data variable name
	 * @param values
	 * 		character values to assign
	 * @throws IOException
	 * 		if reading from or writing to the file throws one
	 * @throws IllegalArgumentException
	 * 		if the variable name or number of provided values
	 * 		is invalid
	 */
	public void writeCharVarDataValues(String varName, char[] values) 
								throws IOException, IllegalArgumentException {
		NetcdfFileWriter ncfile = NetcdfFileWriter.openExisting(getPath());
		try {
			Variable var = ncfile.findVariable(varName);
			if ( var == null )
				throw new IllegalArgumentException("Unable to find variable '" + 
						varName + "' in " + getName());
			if ( var.getShape(1) != 1 ) 
				throw new IllegalArgumentException("Variable '" + varName + 
						"' is not a single-character array variable in " + getName());
			int numVals = var.getShape(0);
			if ( numVals != values.length )
				throw new IllegalArgumentException("Inconstistent number of variables for '" + 
						varName + "' (" + Integer.toString(numVals) + 
						") and provided data (" + Integer.toString(values.length) + ")");
			ArrayChar.D2 dvar = new ArrayChar.D2(numVals, 1);
			for (int k = 0; k < numVals; k++) {
				dvar.set(k, 0, values[k]);
			}
			try {
				ncfile.write(var, dvar);
			} catch (InvalidRangeException ex) {
				throw new IllegalArgumentException(ex);
			}
		} finally {
			ncfile.close();
		}
	}

}
